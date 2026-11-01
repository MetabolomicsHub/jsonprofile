use crate::profile::base::{Category, EnforcementLevel, JsonProfileMessage};
use crate::profile::model::{JsonProfile, RequirementDefinition};
use crate::validator::context::normalize_jsonpath_syntax;
use serde_json::Value;
use serde_json_path::JsonPath;
use std::collections::HashSet;
use std::path::Path;
use std::sync::OnceLock;

const PROFILE_SCHEMA: &str = include_str!("../../resources/jsonprofile.schema.json");

fn profile_schema_validator() -> &'static jsonschema::Validator {
    static VALIDATOR: OnceLock<jsonschema::Validator> = OnceLock::new();
    VALIDATOR.get_or_init(|| {
        let schema: Value =
            serde_json::from_str(PROFILE_SCHEMA).expect("embedded JsonProfile schema is valid");
        jsonschema::validator_for(&schema).expect("embedded JsonProfile schema compiles")
    })
}

pub fn validate_profile_schema(value: &Value) -> Result<(), String> {
    let errors: Vec<String> = profile_schema_validator()
        .iter_errors(value)
        .map(|error| error.to_string())
        .collect();
    if errors.is_empty() {
        Ok(())
    } else {
        Err(errors.join("; "))
    }
}

pub fn validate_profile_file(file_path: impl AsRef<Path>) -> (bool, Vec<JsonProfileMessage>) {
    let path = file_path.as_ref();
    if !path.exists() {
        return (
            false,
            vec![JsonProfileMessage::new(
                "",
                None,
                Category::CrossCheck,
                None,
                "Profile file path is not defined.",
                EnforcementLevel::Required,
            )],
        );
    }
    let bytes = match std::fs::read(path) {
        Ok(b) => b,
        Err(e) => {
            return (
                false,
                vec![JsonProfileMessage::new(
                    "",
                    None,
                    Category::CrossCheck,
                    None,
                    format!("Profile file is not valid json file: {e}"),
                    EnforcementLevel::Required,
                )],
            )
        }
    };
    let val: Value = match serde_json::from_slice(&bytes) {
        Ok(v) => v,
        Err(e) => {
            return (
                false,
                vec![JsonProfileMessage::new(
                    "",
                    None,
                    Category::CrossCheck,
                    None,
                    format!("Profile file is not valid json file: {e}"),
                    EnforcementLevel::Required,
                )],
            )
        }
    };
    validate_profile_value(&val)
}

pub fn validate_profile_value(value: &Value) -> (bool, Vec<JsonProfileMessage>) {
    if let Err(error) = validate_profile_schema(value) {
        return (
            false,
            vec![JsonProfileMessage::new(
                "",
                None,
                Category::CrossCheck,
                None,
                format!("Profile json file format is not valid: {error}"),
                EnforcementLevel::Required,
            )],
        );
    }
    let profile: JsonProfile = match serde_json::from_value(value.clone()) {
        Ok(p) => p,
        Err(e) => {
            return (
                false,
                vec![JsonProfileMessage::new(
                    "",
                    None,
                    Category::CrossCheck,
                    None,
                    format!("Profile json file format is not valid: {e}"),
                    EnforcementLevel::Required,
                )],
            )
        }
    };
    if profile.id.trim().is_empty()
        || profile.name.trim().is_empty()
        || profile.version.trim().is_empty()
    {
        return (
            false,
            vec![JsonProfileMessage::new(
                "",
                None,
                Category::CrossCheck,
                None,
                "Profile json file format is not valid: missing required id, name, or version",
                EnforcementLevel::Required,
            )],
        );
    }
    validate_profile(&profile)
}

pub fn validate_profile(profile: &JsonProfile) -> (bool, Vec<JsonProfileMessage>) {
    let mut messages = Vec::new();
    let mut validated_json_paths = HashSet::new();
    let mut unique_requirement_codes = HashSet::new();

    if let Some(reqs) = &profile.requirements {
        for (key, def_opt) in reqs {
            let Some(def) = def_opt else {
                continue;
            };
            let list: Vec<&RequirementDefinition> = match def {
                RequirementDefinition::Group(g) => g.requirements.iter().collect(),
                other => vec![other],
            };
            for (idx, req) in list.into_iter().enumerate() {
                validate_requirement(
                    key,
                    "",
                    req,
                    idx,
                    &mut messages,
                    &mut validated_json_paths,
                    &mut unique_requirement_codes,
                );
            }
        }
    }

    let has_errors = messages
        .iter()
        .any(|m| m.enforcement_level == EnforcementLevel::Required);
    (!has_errors, messages)
}

fn validate_requirement(
    key: &str,
    sub_path: &str,
    requirement: &RequirementDefinition,
    _requirement_idx: usize,
    messages: &mut Vec<JsonProfileMessage>,
    validated_json_paths: &mut HashSet<String>,
    unique_requirement_codes: &mut HashSet<String>,
) {
    if let RequirementDefinition::Group(group) = requirement {
        for (idx, item) in group.requirements.iter().enumerate() {
            let next_path = if sub_path.is_empty() {
                format!("requirements[{idx}]")
            } else {
                format!("{sub_path}.requirements[{idx}]")
            };
            validate_requirement(
                key,
                &next_path,
                item,
                idx,
                messages,
                validated_json_paths,
                unique_requirement_codes,
            );
        }
        return;
    }

    let code = requirement.code().unwrap_or("");
    if code.is_empty() {
        messages.push(JsonProfileMessage::new(
            "",
            Some(sub_path.to_string()),
            Category::CrossCheck,
            None,
            format!("Requirement '{key}' code or description is not defined."),
            EnforcementLevel::Required,
        ));
    } else {
        if unique_requirement_codes.contains(code) {
            messages.push(JsonProfileMessage::new(
                code,
                Some(sub_path.to_string()),
                Category::CrossCheck,
                None,
                format!("Requirement {code} of '{key}' is not unique."),
                EnforcementLevel::Required,
            ));
        }
        unique_requirement_codes.insert(code.to_string());
    }

    if let RequirementDefinition::Field(field_req) = requirement {
        if let Some(vc) = &field_req.value_constraint {
            if let Some(pre) = &vc.common.precondition {
                for (i, eval) in pre.evaluations.iter().enumerate() {
                    if let Some(jp) = &eval.json_path {
                        if !jp.is_empty() && !validated_json_paths.contains(jp) {
                            let norm = normalize_jsonpath_syntax(jp);
                            if JsonPath::parse(&norm).is_ok() {
                                validated_json_paths.insert(jp.clone());
                            } else {
                                messages.push(JsonProfileMessage::new(
                                    code,
                                    Some(sub_path.to_string()),
                                    Category::CrossCheck,
                                    None,
                                    format!(
                                        "'{key}' requirement {code} precondition evaluation (at index {i}) json path '{jp}' of is not valid."
                                    ),
                                    EnforcementLevel::Required,
                                ));
                            }
                        }
                    }
                }
            }
            if let Some(jp) = &vc.common.json_path {
                if !jp.is_empty() && !validated_json_paths.contains(jp) {
                    let norm = normalize_jsonpath_syntax(jp);
                    if JsonPath::parse(&norm).is_ok() {
                        validated_json_paths.insert(jp.clone());
                    } else {
                        messages.push(JsonProfileMessage::new(
                            code,
                            Some(sub_path.to_string()),
                            Category::CrossCheck,
                            None,
                            format!(
                                "Requirement {code} json path '{jp}' of '{key}' is not valid. "
                            ),
                            EnforcementLevel::Required,
                        ));
                    }
                }
            }
        }
    }
}
