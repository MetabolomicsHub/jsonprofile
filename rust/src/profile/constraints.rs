use crate::profile::base::BaseCvTerm;
use rust_decimal::Decimal;
use serde::{Deserialize, Deserializer, Serialize};
use serde_json::Value;
use std::collections::{BTreeMap, BTreeSet};

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Precondition {
    pub evaluations: Vec<Evaluation>,
    #[serde(default = "default_and")]
    pub join_operator: String,
    #[serde(default)]
    pub min_valid: Option<usize>,
    #[serde(default)]
    pub max_valid: Option<usize>,
}

fn default_and() -> String {
    "and".to_string()
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Evaluation {
    #[serde(default)]
    pub root_value_evaluation: Option<bool>,
    #[serde(default)]
    pub json_path: Option<String>,
    #[serde(default)]
    pub is_reference_field: Option<bool>,
    pub constraint: Box<Constraint>,
    #[serde(default)]
    pub default_evaluation: Option<bool>,
    #[serde(default = "default_and")]
    pub join_operator: String,
    #[serde(default)]
    pub min_valid: Option<usize>,
    #[serde(default)]
    pub max_valid: Option<usize>,
    #[serde(default)]
    pub negated: Option<bool>,
}

impl Evaluation {
    pub fn is_root_value_evaluation(&self) -> bool {
        self.root_value_evaluation.unwrap_or(false)
    }

    pub fn is_reference_field(&self) -> bool {
        self.is_reference_field.unwrap_or(false)
    }

    pub fn is_negated(&self) -> bool {
        self.negated.unwrap_or(false)
    }
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct ConstraintCommon {
    #[serde(default)]
    pub validator: Option<String>,
    #[serde(default)]
    pub name: Option<String>,
    #[serde(default)]
    pub description: Option<String>,
    #[serde(default)]
    pub precondition: Option<Precondition>,
    #[serde(default)]
    pub json_path: Option<String>,
    #[serde(default)]
    pub is_reference_field: Option<bool>,
    #[serde(default)]
    pub default_precondition_evaluation: Option<bool>,
    #[serde(default)]
    pub negated: Option<bool>,
    #[serde(default)]
    pub null_values: Option<Vec<Option<String>>>,
    #[serde(default)]
    pub exceptional_values: Option<Vec<Value>>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Constraint {
    #[serde(flatten)]
    pub common: ConstraintCommon,
    #[serde(flatten)]
    pub kind: ConstraintKind,
}

impl Constraint {
    pub fn constraint_type(&self) -> &'static str {
        self.kind.constraint_type()
    }

    pub fn is_negated(&self) -> bool {
        self.common.negated.unwrap_or(false)
    }

    pub fn is_reference_field(&self) -> bool {
        self.common.is_reference_field.unwrap_or(false)
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(tag = "type")]
pub enum ConstraintKind {
    #[serde(rename = "not-null")]
    NotNull {
        #[serde(default)]
        case_sensitive: Option<bool>,
    },
    #[serde(rename = "regex")]
    Regex {
        pattern: String,
        #[serde(default = "default_true_opt")]
        case_sensitive: Option<bool>,
    },
    #[serde(rename = "string-length")]
    StringLength {
        #[serde(default)]
        minimum: Option<usize>,
        #[serde(default)]
        maximum: Option<usize>,
    },
    #[serde(rename = "string-enum")]
    StringEnum {
        #[serde(deserialize_with = "deserialize_string_enum_options")]
        options: BTreeSet<String>,
    },
    #[serde(rename = "integer-enum")]
    IntegerEnum {
        #[serde(deserialize_with = "deserialize_integer_enum_options")]
        options: BTreeMap<i64, Value>,
    },
    #[serde(rename = "integer")]
    Integer {
        #[serde(default)]
        minimum: Option<i64>,
        #[serde(default)]
        maximum: Option<i64>,
    },
    #[serde(rename = "positive-integer")]
    PositiveInteger {
        #[serde(default = "default_one_i64")]
        minimum: Option<i64>,
        #[serde(default)]
        maximum: Option<i64>,
    },
    #[serde(rename = "non-negative-integer")]
    NonNegativeInteger {
        #[serde(default = "default_zero_i64")]
        minimum: Option<i64>,
        #[serde(default)]
        maximum: Option<i64>,
    },
    #[serde(rename = "boolean")]
    Boolean {
        #[serde(default)]
        exceptional_false_values: Option<Vec<Option<String>>>,
        #[serde(default)]
        exceptional_true_values: Option<Vec<Option<String>>>,
    },
    #[serde(rename = "decimal")]
    Decimal {
        #[serde(default)]
        minimum: Option<Decimal>,
        #[serde(default)]
        maximum: Option<Decimal>,
        #[serde(default)]
        allow_scientific_notation: Option<bool>,
        #[serde(default)]
        min_scale: Option<u32>,
        #[serde(default)]
        max_scale: Option<u32>,
        #[serde(default)]
        allow_non_finite_values: Option<bool>,
    },
    #[serde(rename = "time")]
    DateTime {
        #[serde(default)]
        format: Option<String>,
    },
    #[serde(rename = "email")]
    Email {
        #[serde(default)]
        check_deliverability: Option<bool>,
    },
    #[serde(rename = "uri")]
    Uri {
        #[serde(default)]
        allowed_schemes: Option<Vec<String>>,
    },
    #[serde(rename = "items-count")]
    Collection {
        #[serde(default)]
        min_occurs: Option<usize>,
        #[serde(default)]
        max_occurs: Option<usize>,
        #[serde(default)]
        item_value_jsonpath_list: Option<Vec<String>>,
        #[serde(default)]
        item_value_match_constraint: Option<Box<Constraint>>,
        #[serde(default)]
        match_reference_values: Option<Vec<Value>>,
        #[serde(default)]
        min_match: Option<usize>,
        #[serde(default)]
        max_match: Option<usize>,
        #[serde(default)]
        min_referenced_value_match: Option<usize>,
        #[serde(default)]
        max_referenced_value_match: Option<usize>,
    },
    #[serde(rename = "cv-term")]
    CvTerm {
        #[serde(default)]
        exceptional_cv_list: Option<Vec<String>>,
        #[serde(default)]
        allow_synonym: Option<bool>,
        #[serde(default)]
        allow_user_defined_terms: Option<bool>,
        #[serde(default)]
        is_cv_term_value_required: Option<bool>,
    },
    #[serde(rename = "allowed-cv-list")]
    AllowedCvList {
        #[serde(default)]
        allowed_cv_list: Option<Vec<String>>,
        #[serde(default)]
        exceptional_cv_list: Option<Vec<String>>,
        #[serde(default)]
        allow_synonym: Option<bool>,
        #[serde(default)]
        allow_user_defined_terms: Option<bool>,
        #[serde(default)]
        is_cv_term_value_required: Option<bool>,
    },
    #[serde(rename = "cv-term-enum")]
    CvTermEnum {
        allowed_cv_terms: Vec<BaseCvTerm>,
        #[serde(default)]
        exceptional_cv_list: Option<Vec<String>>,
        #[serde(default)]
        allow_synonym: Option<bool>,
        #[serde(default)]
        allow_user_defined_terms: Option<bool>,
        #[serde(default)]
        is_cv_term_value_required: Option<bool>,
    },
    #[serde(rename = "parent-cv-term")]
    ParentCvTerm {
        #[serde(default)]
        parent_cv_terms: Option<Vec<BaseCvTerm>>,
        #[serde(default)]
        excluded_cv_terms: Option<Vec<BaseCvTerm>>,
        #[serde(default)]
        exceptional_cv_list: Option<Vec<String>>,
        #[serde(default)]
        allow_synonym: Option<bool>,
        #[serde(default)]
        allow_user_defined_terms: Option<bool>,
        #[serde(default)]
        is_cv_term_value_required: Option<bool>,
    },
    #[serde(rename = "cv-term-value")]
    CvTermValue {
        #[serde(default)]
        key_cv_term: Option<BaseCvTerm>,
        #[serde(default)]
        value_constraint: Option<Box<Constraint>>,
    },
    #[serde(rename = "opa-policy")]
    OpaPolicy {
        #[serde(default)]
        wasm_file_key: Option<String>,
        policy_id: String,
    },
    #[serde(rename = "custom")]
    Custom {
        #[serde(default)]
        default_arguments: Option<BTreeMap<String, Value>>,
        #[serde(default)]
        value_jsonpath_arguments: Option<BTreeMap<String, String>>,
        #[serde(default)]
        root_jsonpath_arguments: Option<BTreeMap<String, String>>,
    },
    #[serde(rename = "group")]
    Group {
        constraints: Vec<Constraint>,
        #[serde(default = "default_and")]
        join_operator: String,
        #[serde(default)]
        min_valid: Option<usize>,
        #[serde(default)]
        max_valid: Option<usize>,
    },
}

impl ConstraintKind {
    pub fn constraint_type(&self) -> &'static str {
        match self {
            ConstraintKind::NotNull { .. } => "not-null",
            ConstraintKind::Regex { .. } => "regex",
            ConstraintKind::StringLength { .. } => "string-length",
            ConstraintKind::StringEnum { .. } => "string-enum",
            ConstraintKind::IntegerEnum { .. } => "integer-enum",
            ConstraintKind::Integer { .. } => "integer",
            ConstraintKind::PositiveInteger { .. } => "positive-integer",
            ConstraintKind::NonNegativeInteger { .. } => "non-negative-integer",
            ConstraintKind::Boolean { .. } => "boolean",
            ConstraintKind::Decimal { .. } => "decimal",
            ConstraintKind::DateTime { .. } => "time",
            ConstraintKind::Email { .. } => "email",
            ConstraintKind::Uri { .. } => "uri",
            ConstraintKind::Collection { .. } => "items-count",
            ConstraintKind::CvTerm { .. } => "cv-term",
            ConstraintKind::AllowedCvList { .. } => "allowed-cv-list",
            ConstraintKind::CvTermEnum { .. } => "cv-term-enum",
            ConstraintKind::ParentCvTerm { .. } => "parent-cv-term",
            ConstraintKind::CvTermValue { .. } => "cv-term-value",
            ConstraintKind::OpaPolicy { .. } => "opa-policy",
            ConstraintKind::Custom { .. } => "custom",
            ConstraintKind::Group { .. } => "group",
        }
    }
}

fn default_true_opt() -> Option<bool> {
    Some(true)
}
fn default_one_i64() -> Option<i64> {
    Some(1)
}
fn default_zero_i64() -> Option<i64> {
    Some(0)
}

fn deserialize_integer_enum_options<'de, D>(
    deserializer: D,
) -> Result<BTreeMap<i64, Value>, D::Error>
where
    D: Deserializer<'de>,
{
    let v = Value::deserialize(deserializer)?;
    let mut map = BTreeMap::new();
    match v {
        Value::Array(arr) => {
            for item in arr {
                if let Some(i) = item.as_i64() {
                    map.insert(i, Value::String(i.to_string()));
                } else if let Some(s) = item.as_str() {
                    if let Ok(i) = s.parse::<i64>() {
                        map.insert(i, Value::String(s.to_string()));
                    }
                }
            }
        }
        Value::Object(obj) => {
            for (k, val) in obj {
                let i = k.parse::<i64>().map_err(serde::de::Error::custom)?;
                map.insert(i, val);
            }
        }
        _ => {}
    }
    Ok(map)
}

fn deserialize_string_enum_options<'de, D>(deserializer: D) -> Result<BTreeSet<String>, D::Error>
where
    D: Deserializer<'de>,
{
    let v = Value::deserialize(deserializer)?;
    let mut set = BTreeSet::new();
    match v {
        Value::Array(arr) => {
            for item in arr {
                if let Some(s) = item.as_str() {
                    set.insert(s.to_string());
                } else {
                    set.insert(item.to_string());
                }
            }
        }
        Value::Object(map) => {
            for k in map.keys() {
                set.insert(k.clone());
            }
        }
        _ => {}
    }
    Ok(set)
}
