use crate::profile::constraints::{Constraint, ConstraintKind};
use crate::validator::context::JsonProfileRunContext;
use serde_json::Value;

#[derive(Debug, Clone)]
pub struct ConstraintValidationResult {
    pub is_valid: bool,
    pub message: Option<String>,
}

pub trait ConstraintChecker: Send + Sync {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>);
}

pub fn format_message(constraint: &Constraint, message: &str) -> String {
    let trimmed = message.trim();
    if let Some(desc) = &constraint.common.description {
        if !desc.is_empty() {
            return format!("{desc}. {trimmed}");
        }
    }
    trimmed.to_string()
}

pub fn join_json_path(path1: Option<&str>, path2: Option<&str>, value: &Value) -> String {
    let p1 = strip_dollar_and_dots(path1.unwrap_or(""));
    let p2 = strip_dollar_and_dots(path2.unwrap_or(""));
    let is_list = value.is_array();
    if p1.is_empty() && p2.is_empty() {
        return if is_list {
            "$[*]".to_string()
        } else {
            "$".to_string()
        };
    }
    if p2.is_empty() {
        return if is_list {
            format!("$[*].{p1}")
        } else {
            format!("$.{p1}")
        };
    }
    if p1.is_empty() {
        return if is_list {
            format!("$[*].{p2}")
        } else {
            format!("$.{p2}")
        };
    }
    if is_list {
        format!("$.{p1}[*].{p2}")
    } else {
        format!("$.{p1}.{p2}")
    }
}

fn strip_dollar_and_dots(s: &str) -> &str {
    s.trim().trim_start_matches('$').trim_matches('.')
}

pub fn evaluate_results(
    valid_items_count: usize,
    all_items_count: usize,
    join_operator: &str,
    min_valid: Option<usize>,
    max_valid: Option<usize>,
    default_evaluation: Option<bool>,
    negated: bool,
) -> (bool, String) {
    if all_items_count == 0 {
        let mut success = default_evaluation.unwrap_or(true);
        if negated {
            success = !success;
        }
        return (success, "No item found for evaluation".to_string());
    }

    if join_operator.eq_ignore_ascii_case("and") {
        if valid_items_count == all_items_count {
            return (!negated, "all conditions are satisfied".to_string());
        }
        return (negated, "some conditions are not satisfied".to_string());
    }

    let min_v = min_valid.unwrap_or(1);
    let max_v = max_valid.unwrap_or(all_items_count);
    let mut msgs = Vec::new();
    let min_ok = valid_items_count >= min_v;
    if !min_ok {
        msgs.push("min valid conditions is not satisfied");
    }
    let max_ok = valid_items_count <= max_v;
    if !max_ok {
        msgs.push("min valid conditions is not satisfied");
    }
    if min_ok && max_ok {
        return (!negated, "all conditions are satisfied".to_string());
    }
    (negated, msgs.join(". "))
}

pub fn evaluate_precondition(
    constraint: &Constraint,
    value: &Value,
    root: &Value,
    context: &JsonProfileRunContext,
) -> (bool, String) {
    let Some(pre) = &constraint.common.precondition else {
        return (true, "There in no precondition".to_string());
    };
    if pre.evaluations.is_empty() {
        return (true, "There in no precondition".to_string());
    }

    let mut valid_count = 0usize;
    for evaluation in &pre.evaluations {
        let jp_raw = evaluation.json_path.as_deref().unwrap_or("$");
        let jp = join_json_path(Some(jp_raw), None, value);
        let json_input = if evaluation.is_root_value_evaluation() {
            root
        } else {
            value
        };
        let mut matches = query_matches(&jp, json_input, context);
        let skip = context.runtime_config.is_skip_decimal_validations()
            && matches!(constraint.kind, ConstraintKind::Decimal { .. });

        if evaluation.is_reference_field() && !matches.is_null() {
            matches = resolve_reference_value(&matches, context);
        }

        if !skip {
            let eval_valid = if matches.is_null() {
                let mut v = evaluation.default_evaluation.unwrap_or(true);
                if evaluation.is_negated() {
                    v = !v;
                }
                v
            } else {
                let res = validate_constraint(&evaluation.constraint, &matches, root, context);
                let mut v = res.is_valid;
                if evaluation.is_negated() {
                    v = !v;
                }
                v
            };
            if eval_valid {
                valid_count += 1;
            }
        }
    }

    evaluate_results(
        valid_count,
        pre.evaluations.len(),
        &pre.join_operator,
        pre.min_valid,
        pre.max_valid,
        constraint.common.default_precondition_evaluation,
        false,
    )
}

pub fn get_json_path_value(
    constraint: &Constraint,
    value: &Value,
    context: &JsonProfileRunContext,
) -> Value {
    let mut sub_val = value.clone();
    if let Some(jp) = &constraint.common.json_path {
        if !jp.is_empty() {
            let normalized = if jp.starts_with('$') {
                jp.clone()
            } else if jp.starts_with('[') || jp.starts_with('.') {
                format!("${jp}")
            } else {
                format!("$.{jp}")
            };
            let parts: Vec<&str> = normalized.split('.').collect();
            if let (Value::Object(map), ["$", field]) = (value, parts.as_slice()) {
                if !field.contains(']') {
                    sub_val = map.get(*field).cloned().unwrap_or(Value::Null);
                } else {
                    sub_val = query_matches(&normalized, value, context);
                }
            } else {
                sub_val = query_matches(&normalized, value, context);
            }
        }
    }
    if constraint.is_reference_field() && !sub_val.is_null() {
        sub_val = resolve_reference_value(&sub_val, context);
    }
    sub_val
}

fn resolve_reference_value(val: &Value, context: &JsonProfileRunContext) -> Value {
    match val {
        Value::Array(items) => Value::Array(
            items
                .iter()
                .map(|x| {
                    let key = x.as_str().unwrap_or("");
                    context
                        .unique_id_objects
                        .get(key)
                        .cloned()
                        .unwrap_or(Value::Null)
                })
                .collect(),
        ),
        Value::String(s) => context
            .unique_id_objects
            .get(s)
            .cloned()
            .unwrap_or(Value::Null),
        _ => Value::Null,
    }
}

pub fn query_matches(json_path: &str, input: &Value, context: &JsonProfileRunContext) -> Value {
    if input.is_null() {
        return Value::Null;
    }
    let Ok(compiled) = context.compile_or_get_json_path(json_path) else {
        return Value::Null;
    };
    let nodes: Vec<Value> = compiled.query(input).into_iter().cloned().collect();
    match nodes.len() {
        0 => Value::Null,
        1 => nodes.into_iter().next().unwrap_or(Value::Null),
        _ => Value::Array(nodes),
    }
}

pub fn validate_constraint(
    constraint: &Constraint,
    value: &Value,
    root: &Value,
    context: &JsonProfileRunContext,
) -> ConstraintValidationResult {
    let Ok(checker) = context.profile_validator_factory.get_checker(constraint) else {
        return ConstraintValidationResult {
            is_valid: false,
            message: Some(format!(
                "Unknown constraint type: {}",
                constraint.constraint_type()
            )),
        };
    };

    let (pre_ok, pre_msg) = evaluate_precondition(constraint, value, root, context);
    let sub_value = get_json_path_value(constraint, value, context);
    if pre_ok {
        let (is_valid, msg) = checker.validate(constraint, &sub_value, root, context);
        ConstraintValidationResult {
            is_valid,
            message: msg,
        }
    } else {
        ConstraintValidationResult {
            is_valid: true,
            message: Some(format!("Precondition does not meet: {pre_msg}")),
        }
    }
}
