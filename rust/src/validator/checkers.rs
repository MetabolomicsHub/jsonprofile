use crate::profile::base::{convert_full_path, BaseCvTerm, CvTerm};
use crate::profile::constraints::{Constraint, ConstraintKind};
use crate::validator::checker::{
    format_message, join_json_path, validate_constraint, ConstraintChecker,
};
use crate::validator::context::JsonProfileRunContext;
use crate::validator::factory::ProfileValidator;
use chrono::{NaiveDate, NaiveDateTime, NaiveTime};
use regex::RegexBuilder;
use rust_decimal::Decimal;
use serde_json::Value;
use std::collections::{BTreeMap, BTreeSet};
use std::str::FromStr;
use std::sync::Arc;
use url::Url;

pub fn register_default_and_custom_checkers(validator: &dyn ProfileValidator) {
    validator.register_checker("not-null", None, Arc::new(NotNullChecker));
    validator.register_checker("regex", None, Arc::new(RegexChecker));
    validator.register_checker("string-length", None, Arc::new(StringLengthChecker));
    validator.register_checker("string-enum", None, Arc::new(StringEnumChecker));
    validator.register_checker("integer-enum", None, Arc::new(IntegerEnumChecker));
    validator.register_checker("integer", None, Arc::new(IntegerChecker));
    validator.register_checker("positive-integer", None, Arc::new(IntegerChecker));
    validator.register_checker("non-negative-integer", None, Arc::new(IntegerChecker));
    validator.register_checker("boolean", None, Arc::new(BooleanChecker));
    validator.register_checker("decimal", None, Arc::new(DecimalChecker));
    validator.register_checker("time", None, Arc::new(DateTimeChecker));
    validator.register_checker("email", None, Arc::new(EmailChecker));
    validator.register_checker("uri", None, Arc::new(UriChecker));
    validator.register_checker("items-count", None, Arc::new(CollectionChecker));
    validator.register_checker("cv-term", None, Arc::new(CvTermChecker));
    validator.register_checker("allowed-cv-list", None, Arc::new(AllowedCvListChecker));
    validator.register_checker("cv-term-enum", None, Arc::new(CvTermEnumChecker));
    validator.register_checker("parent-cv-term", None, Arc::new(ParentCvTermChecker));
    validator.register_checker("cv-term-value", None, Arc::new(CvTermValueChecker));
    validator.register_checker("group", None, Arc::new(ConstraintGroupChecker));
    validator.register_checker("opa-policy", None, Arc::new(OpaPolicyChecker));

    // Built-in custom checkers matching Python custom_checkers.py
    validator.register_checker("custom", Some("orcid"), Arc::new(OrcidChecker));
    validator.register_checker(
        "custom",
        Some("accessible_url"),
        Arc::new(AccessibleUrlChecker),
    );
}

fn in_exceptional_values(c: &Constraint, val: Option<&Value>, str_val: Option<&str>) -> bool {
    let Some(exc_list) = &c.common.exceptional_values else {
        return false;
    };
    for exc in exc_list {
        if exc.is_null() && val.is_none_or(|v| v.is_null()) {
            return true;
        }
        if let Some(v) = val {
            if values_match(exc, v) {
                return true;
            }
        }
        if let Some(sv) = str_val {
            let exc_str = display_val(exc);
            if exc_str == sv {
                return true;
            }
        }
    }
    false
}

fn is_null_by_config(c: &Constraint, val: &Value, case_sensitive: bool) -> bool {
    if val.is_null() {
        return true;
    }
    let Some(null_list) = &c.common.null_values else {
        return false;
    };
    let check = if case_sensitive {
        display_val(val)
    } else {
        display_val(val).to_lowercase()
    };
    for nv in null_list {
        let Some(s) = nv else {
            continue;
        };
        let cmp = if case_sensitive {
            s.clone()
        } else {
            s.to_lowercase()
        };
        if check == cmp {
            return true;
        }
    }
    false
}

fn values_match(a: &Value, b: &Value) -> bool {
    if a == b {
        return true;
    }
    match (a.as_i64(), b.as_i64()) {
        (Some(ia), Some(ib)) => ia == ib,
        _ => false,
    }
}

pub struct NotNullChecker;

impl ConstraintChecker for NotNullChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let case_sensitive = match &constraint.kind {
            ConstraintKind::NotNull { case_sensitive } => case_sensitive.unwrap_or(false),
            _ => false,
        };
        let mut messages = Vec::new();
        let mut is_null = value.is_null();
        if !is_null && is_null_by_config(constraint, value, case_sensitive) {
            messages.push("value is in null value list");
            is_null = true;
        }
        let mut evaluation = !is_null || in_exceptional_values(constraint, Some(value), None);
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (
            evaluation,
            Some(format_message(constraint, &messages.join(". "))),
        )
    }
}

pub struct RegexChecker;

impl ConstraintChecker for RegexChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Regex {
            pattern,
            case_sensitive,
        } = &constraint.kind
        else {
            return (false, None);
        };
        let cs = case_sensitive.unwrap_or(true);
        let is_null = is_null_by_config(constraint, value, cs);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let raw_str = display_val(value);
            let str_val = if cs { raw_str } else { raw_str.to_lowercase() };
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else {
                let anchored = if pattern.starts_with('^') && pattern.ends_with('$') {
                    pattern.clone()
                } else {
                    format!("^(?:{pattern})$")
                };
                match RegexBuilder::new(&anchored).case_insensitive(!cs).build() {
                    Ok(re) if re.is_match(&str_val) => {
                        evaluation = true;
                        format!("value matches the pattern. input: '{str_val}' pattern: '{pattern}'")
                    }
                    _ => format!(
                        "value does not match the pattern. input: '{str_val}' expected pattern: '{pattern}'"
                    ),
                }
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct StringLengthChecker;

impl ConstraintChecker for StringLengthChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::StringLength { minimum, maximum } = &constraint.kind else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut min_req = false;
        let mut max_req = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                min_req = true;
                max_req = true;
            } else if minimum.unwrap_or(0) == 0 {
                min_req = true;
            }
            "value is null".to_string()
        } else if let Some(s) = value.as_str() {
            if in_exceptional_values(constraint, Some(value), Some(s)) {
                min_req = true;
                max_req = true;
                String::new()
            } else {
                let len = s.chars().count();
                min_req = minimum.is_none_or(|m| len >= m);
                max_req = maximum.is_none_or(|m| len <= m);
                let mut msgs = Vec::new();
                if !min_req {
                    msgs.push("minimum length error");
                }
                if !max_req {
                    msgs.push("maximum length error");
                }
                msgs.join(". ")
            }
        } else {
            "Value is not a string".to_string()
        };
        let mut evaluation = min_req && max_req;
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct StringEnumChecker;

impl ConstraintChecker for StringEnumChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::StringEnum { options } = &constraint.kind else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else if options.contains(&str_val) {
                evaluation = true;
                "value is in the enum list".to_string()
            } else {
                format!("{str_val} value is not in the enum list")
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct IntegerEnumChecker;

impl ConstraintChecker for IntegerEnumChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::IntegerEnum { options } = &constraint.kind else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else if let Ok(int_val) = str_val.parse::<i64>() {
                if options.contains_key(&int_val) {
                    evaluation = true;
                    format!("value {str_val} is in the int enum list")
                } else {
                    format!("value {str_val} is not in the int enum list")
                }
            } else {
                format!("value '{str_val}' is not numeric")
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct IntegerChecker;

impl ConstraintChecker for IntegerChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let (minimum, maximum) = match &constraint.kind {
            ConstraintKind::Integer { minimum, maximum }
            | ConstraintKind::PositiveInteger { minimum, maximum }
            | ConstraintKind::NonNegativeInteger { minimum, maximum } => (*minimum, *maximum),
            _ => return (false, None),
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut min_req = false;
        let mut max_req = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                min_req = true;
                max_req = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                min_req = true;
                max_req = true;
                String::new()
            } else if value.is_boolean() {
                format!("value '{str_val}' is not numeric")
            } else if let Ok(int_val) = str_val.parse::<i64>() {
                min_req = minimum.is_none_or(|m| int_val >= m);
                max_req = maximum.is_none_or(|m| int_val <= m);
                let mut msgs = Vec::new();
                if let Some(m) = minimum {
                    if !min_req {
                        msgs.push(format!("minimum value error. current: {int_val}, min: {m}"));
                    }
                }
                if let Some(m) = maximum {
                    if !max_req {
                        msgs.push(format!("maximum value error. current: {int_val}, min: {m}"));
                    }
                }
                msgs.join(". ")
            } else {
                format!("value '{str_val}' is not numeric")
            }
        };
        let mut evaluation = min_req && max_req;
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct BooleanChecker;

impl ConstraintChecker for BooleanChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Boolean {
            exceptional_false_values,
            exceptional_true_values,
        } = &constraint.kind
        else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else {
                let mut bool_val: Option<bool> = value.as_bool();
                if bool_val.is_none() {
                    if let Some(s) = value.as_str() {
                        let lower = s.to_lowercase();
                        if exceptional_false_values.as_ref().is_some_and(|list| {
                            list.iter()
                                .any(|x| x.as_ref().is_some_and(|v| v.to_lowercase() == lower))
                        }) {
                            bool_val = Some(false);
                        } else if exceptional_true_values.as_ref().is_some_and(|list| {
                            list.iter()
                                .any(|x| x.as_ref().is_some_and(|v| v.to_lowercase() == lower))
                        }) {
                            bool_val = Some(true);
                        }
                    }
                }
                if let Some(b) = bool_val {
                    evaluation = true;
                    format!("value '{str_val}' evaluated as {b}")
                } else {
                    format!("value '{str_val}' is not boolean")
                }
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct DecimalChecker;

impl ConstraintChecker for DecimalChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Decimal {
            minimum,
            maximum,
            allow_scientific_notation,
            min_scale,
            max_scale,
            allow_non_finite_values,
        } = &constraint.kind
        else {
            return (false, None);
        };
        if context.runtime_config.is_skip_decimal_validations() {
            return (true, Some("Decimal validation is skipped.".to_string()));
        }
        let is_null = is_null_by_config(constraint, value, false);
        let mut min_req = false;
        let mut max_req = false;
        let mut min_scale_req = false;
        let mut max_scale_req = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                min_req = true;
                max_req = true;
                min_scale_req = true;
                max_scale_req = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                min_req = true;
                max_req = true;
                min_scale_req = true;
                max_scale_req = true;
                String::new()
            } else if str_val.eq_ignore_ascii_case("nan")
                || str_val.eq_ignore_ascii_case("infinity")
                || str_val.eq_ignore_ascii_case("+infinity")
                || str_val.eq_ignore_ascii_case("-infinity")
            {
                if allow_non_finite_values.unwrap_or(false) {
                    min_req = true;
                    max_req = true;
                    min_scale_req = true;
                    max_scale_req = true;
                }
                format!("Value is {str_val}")
            } else if value.is_boolean() {
                format!("value '{str_val}' is not numeric")
            } else {
                let allow_sci = allow_scientific_notation.unwrap_or(true);
                let parsed = if str_val.contains(['e', 'E']) {
                    Decimal::from_scientific(&str_val).or_else(|_| Decimal::from_str(&str_val))
                } else {
                    Decimal::from_str(&str_val)
                };
                match parsed {
                    Ok(dec) => {
                        if !allow_sci && str_val.contains(['e', 'E']) {
                            "scientific notation is not allowed".to_string()
                        } else {
                            let scale = dec.scale();
                            min_scale_req = min_scale.is_none_or(|ms| scale >= ms);
                            max_scale_req = max_scale.is_none_or(|ms| scale <= ms);
                            min_req = minimum.is_none_or(|m| dec >= m);
                            max_req = maximum.is_none_or(|m| dec <= m);
                            let mut msgs = Vec::new();
                            if minimum.is_some() && !min_req {
                                msgs.push("minimum value error");
                            }
                            if maximum.is_some() && !max_req {
                                msgs.push("maximum value error");
                            }
                            if min_scale.is_some() && !min_scale_req {
                                msgs.push("minimum scale error");
                            }
                            if max_scale.is_some() && !max_scale_req {
                                msgs.push("max scale error");
                            }
                            msgs.join(". ")
                        }
                    }
                    Err(_) => format!("value '{str_val}' is not numeric"),
                }
            }
        };
        let mut evaluation = min_req && max_req && min_scale_req && max_scale_req;
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct DateTimeChecker;

impl ConstraintChecker for DateTimeChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::DateTime { format } = &constraint.kind else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, false);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else if is_valid_datetime(&str_val, format.as_deref()) {
                evaluation = true;
                "value is valid datetime".to_string()
            } else {
                "value is is not valid datetime".to_string()
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

fn is_valid_datetime(s: &str, format: Option<&str>) -> bool {
    if let Some(fmt) = format.filter(|f| !f.is_empty()) {
        NaiveDateTime::parse_from_str(s, fmt).is_ok()
            || NaiveDate::parse_from_str(s, fmt).is_ok()
            || NaiveTime::parse_from_str(s, fmt).is_ok()
            || chrono::DateTime::parse_from_str(s, fmt).is_ok()
    } else {
        chrono::DateTime::parse_from_rfc3339(s).is_ok()
            || NaiveDateTime::parse_from_str(s, "%Y-%m-%dT%H:%M:%S").is_ok()
            || NaiveDateTime::parse_from_str(s, "%Y-%m-%d %H:%M:%S").is_ok()
            || NaiveDate::parse_from_str(s, "%Y-%m-%d").is_ok()
            || NaiveTime::parse_from_str(s, "%H:%M:%S").is_ok()
    }
}

pub struct EmailChecker;

impl ConstraintChecker for EmailChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let is_null = is_null_by_config(constraint, value, false);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else {
                let re = regex::Regex::new(r"^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$")
                    .unwrap();
                if re.is_match(&str_val) {
                    evaluation = true;
                    "value is valid email".to_string()
                } else {
                    "value is not valid email".to_string()
                }
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct UriChecker;

impl ConstraintChecker for UriChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Uri { allowed_schemes } = &constraint.kind else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, false);
        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
                String::new()
            } else if let Ok(parsed) = Url::parse(&str_val) {
                let scheme = parsed.scheme();
                let has_target =
                    parsed.host_str().is_some_and(|h| !h.is_empty()) || !parsed.path().is_empty();
                if scheme.is_empty() || !has_target {
                    format!("value '{str_val}' is not a valid URI")
                } else if let Some(schemes) = allowed_schemes.as_ref().filter(|s| !s.is_empty()) {
                    if schemes.iter().any(|s| s == scheme) {
                        evaluation = true;
                        "value is valid url with allowed scheme".to_string()
                    } else {
                        format!(
                            "value is valid url but scheme '{scheme}' is not in allowed schemes: {}",
                            schemes.join(", ")
                        )
                    }
                } else {
                    evaluation = true;
                    "value is valid url".to_string()
                }
            } else {
                format!("value {str_val} is not valid url")
            }
        };
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct CollectionChecker;

impl ConstraintChecker for CollectionChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Collection {
            min_occurs,
            max_occurs,
            item_value_jsonpath_list,
            item_value_match_constraint,
            min_match,
            max_match,
            min_referenced_value_match,
            max_referenced_value_match,
            ..
        } = &constraint.kind
        else {
            return (false, None);
        };

        let is_null = is_null_by_config(constraint, value, true);
        let mut min_req = false;
        let mut max_req = false;
        let mut min_match_req = false;
        let mut max_match_req = false;
        let mut min_ref_req = false;
        let mut max_ref_req = false;
        let mut matched_set = BTreeSet::new();
        let mut unmatched_set = BTreeSet::new();
        let mut messages = Vec::new();

        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                min_req = true;
                max_req = true;
                min_match_req = true;
                max_match_req = true;
                min_ref_req = true;
                max_ref_req = true;
            } else if min_occurs.unwrap_or(0) == 0 {
                min_req = true;
            }
            "value is not defined".to_string()
        } else if value.is_array() || (value.is_object() && item_value_match_constraint.is_some()) {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                min_req = true;
                max_req = true;
                min_match_req = true;
                max_match_req = true;
                min_ref_req = true;
                max_ref_req = true;
                "value is in exceptional list".to_string()
            } else {
                let mut item_values: BTreeMap<String, Value> = BTreeMap::new();
                if item_value_match_constraint.is_some() {
                    if let Some(sub_paths) =
                        item_value_jsonpath_list.as_ref().filter(|l| !l.is_empty())
                    {
                        for item_jp in sub_paths {
                            let joined = join_json_path(Some(item_jp), None, value);
                            if let Ok(compiled) = context.compile_or_get_json_path(&joined) {
                                for loc in compiled.query_located(value) {
                                    let src = convert_full_path(&loc.location().to_string());
                                    item_values.insert(src, loc.node().clone());
                                }
                            }
                        }
                    } else if let Some(raw_list) = value.as_array() {
                        for (i, item) in raw_list.iter().enumerate() {
                            item_values.insert(format!("$[{i}]"), item.clone());
                        }
                    }
                }

                let raw_len = value.as_array().map_or(item_values.len(), |a| a.len());
                let current_size =
                    if item_value_match_constraint.is_some() && !item_values.is_empty() {
                        item_values.len()
                    } else {
                        raw_len
                    };
                min_req = min_occurs.is_none_or(|m| current_size >= m);
                max_req = max_occurs.is_none_or(|m| current_size <= m);
                if !min_req {
                    messages.push(format!(
                        "minimum count error: current: {current_size}, min: {:?}",
                        min_occurs
                    ));
                }
                if !max_req {
                    messages.push(format!(
                        "maximum count error: current: {current_size}, max: {:?}",
                        max_occurs
                    ));
                }

                if let Some(item_constraint) = item_value_match_constraint {
                    let mut matched_count = 0usize;
                    for item in item_values.values() {
                        let mut m = constraint.common.null_values.as_ref().is_some_and(|nvs| {
                            let s = display_val(item);
                            nvs.iter().any(|nv| nv.as_deref() == Some(s.as_str()))
                        });
                        if !m {
                            let res = validate_constraint(item_constraint, item, root, context);
                            m = res.is_valid;
                        }
                        let item_key = if matches!(
                            item_constraint.kind,
                            ConstraintKind::CvTerm { .. }
                                | ConstraintKind::AllowedCvList { .. }
                                | ConstraintKind::CvTermEnum { .. }
                                | ConstraintKind::ParentCvTerm { .. }
                        ) {
                            CvTerm::from_json_value(
                                item,
                                &context.profile_config.cv_term_field_mapping,
                            )
                            .map(|cv| cv.to_base_cv_term().to_string())
                            .unwrap_or_else(|| display_val(item))
                        } else {
                            display_val(item)
                        };
                        if m {
                            matched_count += 1;
                            matched_set.insert(item_key);
                        } else {
                            unmatched_set.insert(item_key);
                        }
                    }
                    let ref_matched_count = matched_set.len();
                    min_match_req = min_match.map_or(matched_count > 0, |m| matched_count >= m);
                    max_match_req = max_match.is_none_or(|m| matched_count <= m);
                    min_ref_req = min_referenced_value_match
                        .map_or(item_values.is_empty() || ref_matched_count > 0, |m| {
                            ref_matched_count >= m
                        });
                    max_ref_req = max_referenced_value_match
                        .map_or(min_ref_req || unmatched_set.is_empty(), |m| {
                            ref_matched_count <= m
                        });
                } else {
                    min_match_req = true;
                    max_match_req = true;
                    min_ref_req = true;
                    max_ref_req = true;
                }
                messages.join(". ")
            }
        } else {
            return (
                constraint.is_negated(),
                Some(format!(
                    "{:?} is not a collection.",
                    constraint.common.json_path
                )),
            );
        };

        let mut evaluation =
            min_req && max_req && min_match_req && max_match_req && min_ref_req && max_ref_req;
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct CvTermChecker;

impl ConstraintChecker for CvTermChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::CvTerm {
            exceptional_cv_list,
            allow_synonym,
            allow_user_defined_terms,
            is_cv_term_value_required,
        } = &constraint.kind
        else {
            return (false, None);
        };
        let mapping = &context.profile_config.cv_term_field_mapping;
        let param = if !value.is_null() {
            CvTerm::from_json_value(value, mapping)
        } else {
            None
        };
        let mut is_null = value.is_null();
        if !is_null {
            if let Some(nvs) = &constraint.common.null_values {
                if param
                    .as_ref()
                    .is_none_or(|p| nvs.iter().any(|nv| nv.as_deref() == Some(&p.to_string())))
                {
                    is_null = true;
                }
            }
        }

        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is not defined".to_string()
        } else if let Some(param) = param {
            if param.cv_label.is_empty() && param.cv_accession.is_empty() && param.name.is_empty() {
                format!(
                    "Value '{}' could not be parsed as a CV term.",
                    display_val(value)
                )
            } else {
                let mut name_req = false;
                let msg = if exceptional_cv_list
                    .as_ref()
                    .is_some_and(|list| list.contains(&param.cv_label))
                {
                    name_req = true;
                    "CV source is in exception list".to_string()
                } else if param.cv_label.is_empty()
                    && param.cv_accession.is_empty()
                    && !param.name.is_empty()
                {
                    if allow_user_defined_terms.unwrap_or(false) {
                        name_req = true;
                        "User-defined terms is allowed.".to_string()
                    } else {
                        "User-defined terms is not allowed.".to_string()
                    }
                } else if !param.name.is_empty() {
                    if param.cv_label.is_empty() || param.cv_accession.is_empty() {
                        format!("invalid cv term label or accession. {param}")
                    } else if context.runtime_config.is_offline_mode() {
                        name_req = true;
                        "term is valid.".to_string()
                    } else {
                        let (ok, message) = context.cv_term_search.check_cv_term(
                            &param,
                            None,
                            allow_synonym.unwrap_or(false),
                        );
                        name_req = ok;
                        message
                    }
                } else {
                    format!("invalid cv term name: {param}")
                };

                let mut msgs = Vec::new();
                if !msg.is_empty() {
                    msgs.push(msg);
                }
                let val_req = if is_cv_term_value_required.unwrap_or(false) {
                    if !param.value.is_empty() {
                        msgs.push("Value of CV term is defined.".to_string());
                        true
                    } else {
                        msgs.push("Value slot for CV term is required.".to_string());
                        false
                    }
                } else {
                    true
                };
                evaluation = name_req && val_req;
                msgs.join(". ")
            }
        } else {
            format!("Value '{}' is not a CV term.", display_val(value))
        };

        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct AllowedCvListChecker;

impl ConstraintChecker for AllowedCvListChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::AllowedCvList {
            allowed_cv_list,
            exceptional_cv_list,
            allow_synonym,
            is_cv_term_value_required,
            ..
        } = &constraint.kind
        else {
            return (false, None);
        };
        let mapping = &context.profile_config.cv_term_field_mapping;
        let param = if !value.is_null() {
            CvTerm::from_json_value(value, mapping)
        } else {
            None
        };
        let mut is_null = value.is_null();
        if !is_null {
            if let Some(nvs) = &constraint.common.null_values {
                if param
                    .as_ref()
                    .is_none_or(|p| nvs.iter().any(|nv| nv.as_deref() == Some(&p.to_string())))
                {
                    is_null = true;
                }
            }
        }

        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else if let Some(param) = param {
            if param.cv_label.is_empty() && param.cv_accession.is_empty() && param.name.is_empty() {
                format!(
                    "Value '{}' could not be parsed as a CV term.",
                    display_val(value)
                )
            } else {
                let mut name_req = false;
                let mut msg = String::new();
                if !param.name.is_empty() {
                    if param.cv_label.is_empty() || param.cv_accession.is_empty() {
                        msg = format!(" '{param}' invalid cv term label or accession.");
                    } else {
                        let base_param = param.to_base_cv_term();
                        if in_exceptional_values(constraint, None, Some(&base_param.to_string())) {
                            name_req = true;
                            msg = format!("'{param}' term is in exception cv term list.");
                        }
                        if !name_req
                            && exceptional_cv_list
                                .as_ref()
                                .is_some_and(|list| list.iter().any(|x| x == &param.cv_label))
                        {
                            name_req = true;
                            msg = "CV source is in exception list".to_string();
                        }
                        if !name_req {
                            if let Some(list) = allowed_cv_list.as_ref().filter(|l| !l.is_empty()) {
                                let in_list =
                                    list.iter().any(|x| x.eq_ignore_ascii_case(&param.cv_label));
                                if in_list {
                                    if context.runtime_config.is_offline_mode() {
                                        name_req = true;
                                        msg = "offline mode.".to_string();
                                    } else {
                                        let (ok, m) = context.cv_term_search.check_cv_term(
                                            &param,
                                            None,
                                            allow_synonym.unwrap_or(false),
                                        );
                                        name_req = ok;
                                        msg = m;
                                    }
                                } else {
                                    msg = format!(
                                        "'{param}' not in the selected cv list: {}",
                                        list.join(", ")
                                    );
                                }
                            } else if context.runtime_config.is_offline_mode() {
                                name_req = true;
                                msg = "offline mode.".to_string();
                            } else {
                                let (ok, m) = context.cv_term_search.check_cv_term(
                                    &param,
                                    None,
                                    allow_synonym.unwrap_or(false),
                                );
                                name_req = ok;
                                msg = m;
                            }
                        }
                    }
                } else {
                    msg = "invalid cv term name.".to_string();
                }

                let mut msgs = Vec::new();
                if !msg.is_empty() {
                    msgs.push(msg);
                }
                let val_req = if is_cv_term_value_required.unwrap_or(false) {
                    if !param.value.is_empty() {
                        msgs.push("Value of CV term is defined.".to_string());
                        true
                    } else {
                        msgs.push("Value slot for CV term is required.".to_string());
                        false
                    }
                } else {
                    true
                };
                evaluation = name_req && val_req;
                msgs.join(". ")
            }
        } else {
            format!("Value '{}' is not a CV term.", display_val(value))
        };

        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct CvTermEnumChecker;

impl ConstraintChecker for CvTermEnumChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::CvTermEnum {
            allowed_cv_terms,
            exceptional_cv_list,
            is_cv_term_value_required,
            ..
        } = &constraint.kind
        else {
            return (false, None);
        };
        let mapping = &context.profile_config.cv_term_field_mapping;
        let param = if !value.is_null() {
            CvTerm::from_json_value(value, mapping)
        } else {
            None
        };
        let mut is_null = value.is_null();
        if !is_null {
            if let Some(nvs) = &constraint.common.null_values {
                if param
                    .as_ref()
                    .is_none_or(|p| nvs.iter().any(|nv| nv.as_deref() == Some(&p.to_string())))
                {
                    is_null = true;
                }
            }
        }

        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else if let Some(param) = param {
            if param.cv_label.is_empty() && param.cv_accession.is_empty() && param.name.is_empty() {
                format!(
                    "Value '{}' could not be parsed as a CV term.",
                    display_val(value)
                )
            } else {
                let mut name_req = false;
                let mut msg = String::new();
                let has_name_only_enums = allowed_cv_terms.iter().any(|x| x.accession().is_empty());
                if !param.name.is_empty() {
                    if !has_name_only_enums
                        && (param.cv_label.is_empty() || param.cv_accession.is_empty())
                    {
                        msg = format!("invalid cv term label or accession '{param}'.");
                    } else {
                        if exceptional_cv_list
                            .as_ref()
                            .is_some_and(|list| list.contains(&param.cv_label))
                        {
                            name_req = true;
                            msg = format!("term '{}' is in exception cv list.", param.cv_label);
                        }
                        if !name_req {
                            if !allowed_cv_terms.is_empty() {
                                let input_val = param.to_base_cv_term();
                                if allowed_cv_terms.contains(&input_val) {
                                    name_req = true;
                                    msg = format!("'{input_val}' is in cv term list");
                                } else {
                                    msg = format!("'{input_val}' is not in cv term list");
                                }
                            } else {
                                name_req = true;
                                msg = "term in cv term list".to_string();
                            }
                        }
                    }
                } else {
                    msg = "invalid cv term name".to_string();
                }

                let mut msgs = Vec::new();
                if !msg.is_empty() {
                    msgs.push(msg);
                }
                let val_req = if is_cv_term_value_required.unwrap_or(false) {
                    if !param.value.is_empty() {
                        msgs.push("Value of CV term is defined.".to_string());
                        true
                    } else {
                        msgs.push("Value slot for CV term is required.".to_string());
                        false
                    }
                } else {
                    true
                };
                evaluation = name_req && val_req;
                msgs.join(". ")
            }
        } else {
            format!("Value '{}' is not a CV term.", display_val(value))
        };

        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct ParentCvTermChecker;

impl ConstraintChecker for ParentCvTermChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::ParentCvTerm {
            parent_cv_terms,
            exceptional_cv_list,
            allow_synonym,
            is_cv_term_value_required,
            ..
        } = &constraint.kind
        else {
            return (false, None);
        };
        let mapping = &context.profile_config.cv_term_field_mapping;
        let param = if !value.is_null() {
            CvTerm::from_json_value(value, mapping)
        } else {
            None
        };
        let mut is_null = value.is_null();
        if !is_null {
            if let Some(nvs) = &constraint.common.null_values {
                if param
                    .as_ref()
                    .is_none_or(|p| nvs.iter().any(|nv| nv.as_deref() == Some(&p.to_string())))
                {
                    is_null = true;
                }
            }
        }

        let mut evaluation = false;
        let message = if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            "value is null".to_string()
        } else if let Some(param) = param {
            if param.cv_label.is_empty() && param.cv_accession.is_empty() && param.name.is_empty() {
                format!(
                    "Value '{}' could not be parsed as a CV term.",
                    display_val(value)
                )
            } else {
                let mut name_req = false;
                let mut msg = String::new();
                if !param.name.is_empty() {
                    if param.cv_label.is_empty() || param.cv_accession.is_empty() {
                        msg = format!("invalid cv term label or accession '{param}'.");
                    } else {
                        let base_param = param.to_base_cv_term();
                        if in_exceptional_values(constraint, None, Some(&base_param.to_string())) {
                            name_req = true;
                            msg = "cv term is in exception list.".to_string();
                        }
                        if !name_req
                            && exceptional_cv_list
                                .as_ref()
                                .is_some_and(|list| list.iter().any(|x| x == &param.cv_label))
                        {
                            name_req = true;
                            msg = "CV source is in exception list".to_string();
                        }
                        if !name_req {
                            if context.runtime_config.is_offline_mode() {
                                name_req = true;
                                msg = "offline mode.".to_string();
                            } else if let Some(parents) = parent_cv_terms {
                                for parent in parents {
                                    let parent_cv = CvTerm::new(
                                        parent.label(),
                                        parent.accession(),
                                        parent.term_name(),
                                        "",
                                    );
                                    let (ok, _) = context.cv_term_search.check_cv_term(
                                        &param,
                                        Some(&parent_cv),
                                        allow_synonym.unwrap_or(false),
                                    );
                                    if ok {
                                        name_req = true;
                                        msg = format!("CV term {param} is parent of {parent}.");
                                        break;
                                    }
                                }
                                if !name_req {
                                    msg = "not child of the parent.".to_string();
                                }
                            } else {
                                name_req = true;
                            }
                        }
                    }
                } else {
                    msg = "invalid cv term name.".to_string();
                }

                let mut msgs = Vec::new();
                if !msg.is_empty() {
                    msgs.push(msg);
                }
                let val_req =
                    !is_cv_term_value_required.unwrap_or(false) || !param.value.is_empty();
                if is_cv_term_value_required.unwrap_or(false) && !val_req {
                    msgs.push("Value slot for CV term is required.".to_string());
                }
                evaluation = name_req && val_req;
                msgs.join(". ")
            }
        } else {
            format!("Value '{}' is not a CV term.", display_val(value))
        };

        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct CvTermValueChecker;

impl ConstraintChecker for CvTermValueChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::CvTermValue {
            key_cv_term,
            value_constraint,
        } = &constraint.kind
        else {
            return (false, None);
        };
        let mapping = &context.profile_config.cv_term_field_mapping;
        let param = if !value.is_null() {
            CvTerm::from_json_value(value, mapping)
        } else {
            None
        };
        let base: Option<BaseCvTerm> = param.as_ref().map(|p| p.to_base_cv_term());

        let (mut evaluation, message) = match (&base, key_cv_term) {
            (Some(b), Some(k)) if b.to_string().eq_ignore_ascii_case(&k.to_string()) => {
                if let Some(vc) = value_constraint {
                    let val_node = param
                        .as_ref()
                        .map(|p| Value::String(p.value.clone()))
                        .unwrap_or(Value::Null);
                    let res = validate_constraint(vc, &val_node, root, context);
                    (res.is_valid, res.message.unwrap_or_default())
                } else {
                    (true, "No value constraint defined.".to_string())
                }
            }
            _ => (
                true,
                format!(
                    "CV term '{:?}' does not match the key CV term '{:?}'",
                    param, key_cv_term
                ),
            ),
        };

        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct ConstraintGroupChecker;

impl ConstraintChecker for ConstraintGroupChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::Group {
            constraints,
            join_operator,
            min_valid,
            max_valid,
        } = &constraint.kind
        else {
            return (false, None);
        };
        if constraints.is_empty() {
            return (true, None);
        }
        let mut val = value.clone();
        if !val.is_null() {
            if let Some(nvs) = &constraint.common.null_values {
                let param =
                    CvTerm::from_json_value(&val, &context.profile_config.cv_term_field_mapping);
                if param
                    .as_ref()
                    .is_none_or(|p| nvs.iter().any(|nv| nv.as_deref() == Some(&p.to_string())))
                {
                    val = Value::Null;
                }
            }
        }

        let is_and = join_operator.eq_ignore_ascii_case("and");
        let mut messages = Vec::new();
        let mut valid_count = 0usize;
        for sub in constraints {
            let res = validate_constraint(sub, &val, root, context);
            if res.is_valid {
                valid_count += 1;
            } else if let Some(m) = res.message {
                if !m.is_empty() {
                    messages.push(m);
                }
            }
        }

        let mut evaluation = false;
        if is_and {
            if valid_count == constraints.len() {
                messages.push("All conditions are valid".to_string());
                evaluation = true;
            }
        } else {
            let min_v = min_valid.unwrap_or(1);
            let max_v = max_valid.unwrap_or(constraints.len());
            let min_eval = valid_count >= min_v;
            let max_eval = valid_count <= max_v;
            messages.push(if min_eval {
                format!("Min {min_v} conditions are valid")
            } else {
                format!("Min {min_v} conditions are not valid")
            });
            messages.push(if max_eval {
                format!("Max {max_v} conditions are valid")
            } else {
                format!("Max {max_v} conditions are not valid")
            });
            evaluation = min_eval && max_eval;
        }
        let message = messages.join(". ");
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct OpaPolicyChecker;

impl ConstraintChecker for OpaPolicyChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let ConstraintKind::OpaPolicy {
            wasm_file_key,
            policy_id,
        } = &constraint.kind
        else {
            return (false, None);
        };
        let is_null = is_null_by_config(constraint, value, true);
        let mut evaluation = false;
        let mut message = String::new();
        if is_null {
            if in_exceptional_values(constraint, None, None) {
                evaluation = true;
            }
            message = "value is null".to_string();
        } else {
            let str_val = display_val(value);
            if in_exceptional_values(constraint, Some(value), Some(&str_val)) {
                evaluation = true;
            } else {
                let label = wasm_file_key
                    .as_deref()
                    .filter(|s| !s.is_empty())
                    .unwrap_or("default");
                let mut wasm_file_path = "";
                let mut wasm_download_url = None;
                let mut entrypoint = None;
                if let Some(defs) = &context.profile_config.wasm_file_definitions {
                    let Some(opa_cfg) = defs.get(label) else {
                        return (
                            false,
                            Some(format_message(constraint, "OPA policy file not found")),
                        );
                    };
                    wasm_file_path = &opa_cfg.wasm_file_path;
                    wasm_download_url = Some(opa_cfg.wasm_file_download_url.as_str());
                    entrypoint = Some(opa_cfg.entrypoint.as_str());
                }
                let Ok(engine) = context
                    .profile_validator_factory
                    .opa_engine_factory()
                    .get_opa_engine(wasm_file_path, wasm_download_url)
                else {
                    return (
                        false,
                        Some(format_message(constraint, "Failed to get OPA engine")),
                    );
                };
                let opa_input = serde_json::json!({
                    "policy_ids": [policy_id],
                    "value": value,
                    "root": root,
                    "config": context.profile_config,
                    "constraint": constraint
                });
                let Ok(result) = engine.evaluate(&opa_input, entrypoint, None) else {
                    return (
                        false,
                        Some(format_message(
                            constraint,
                            "OPA policy decision is not valid",
                        )),
                    );
                };
                let first_decision = if let Some(arr) = result.as_array() {
                    arr.first()
                } else {
                    Some(&result)
                };
                if let Some(dec) = first_decision {
                    let msgs = dec
                        .get("messages")
                        .and_then(|v| v.as_array())
                        .cloned()
                        .unwrap_or_default();
                    if msgs.is_empty() {
                        evaluation = true;
                    } else {
                        let texts: Vec<String> = msgs
                            .iter()
                            .filter_map(|m| m.get("message").and_then(|v| v.as_str()))
                            .map(str::to_string)
                            .collect();
                        message = texts.join(". ");
                    }
                }
            }
        }
        if constraint.is_negated() {
            evaluation = !evaluation;
        }
        (evaluation, Some(format_message(constraint, &message)))
    }
}

pub struct OrcidChecker;

impl ConstraintChecker for OrcidChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let mut evaluation = false;
        let str_val = display_val(value);
        let message = if value.is_null() {
            "value is null".to_string()
        } else {
            let re = regex::Regex::new(r"^\s*\d{4}-\d{4}-\d{4}-\d{3}[0-9X]\s*$").unwrap();
            if re.is_match(&str_val) && verify_orcid_checksum(str_val.trim()) {
                evaluation = true;
                "value is a valid ORCID".to_string()
            } else {
                "value is not a valid ORCID".to_string()
            }
        };
        (evaluation, Some(format_message(constraint, &message)))
    }
}

fn verify_orcid_checksum(orcid: &str) -> bool {
    let digits: String = orcid.chars().filter(|c| *c != '-').collect();
    if digits.len() != 16 {
        return false;
    }
    let mut total = 0u32;
    for ch in digits[..15].chars() {
        let Some(d) = ch.to_digit(10) else {
            return false;
        };
        total = (total + d) * 2;
    }
    let remainder = total % 11;
    let result = (12 - remainder) % 11;
    let expected = if result == 10 {
        'X'
    } else {
        char::from_digit(result, 10).unwrap_or('0')
    };
    digits.ends_with(expected)
}

pub struct AccessibleUrlChecker;

impl ConstraintChecker for AccessibleUrlChecker {
    fn validate(
        &self,
        constraint: &Constraint,
        value: &Value,
        root: &Value,
        context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let mut inner_constraint = constraint.clone();
        inner_constraint.common.negated = None;
        inner_constraint.common.null_values = None;
        inner_constraint.common.exceptional_values = None;
        UriChecker.validate(&inner_constraint, value, root, context)
    }
}

fn display_val(val: &Value) -> String {
    match val {
        Value::Null => "None".to_string(),
        Value::String(s) => s.clone(),
        other => other.to_string(),
    }
}
