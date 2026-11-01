use serde::{Deserialize, Deserializer, Serialize};
use serde_json::Value;

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
#[serde(rename_all = "snake_case")]
pub enum Category {
    Parse,
    Format,
    CrossCheck,
    Profile,
    Schema,
}

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize, Default)]
pub enum EnforcementLevel {
    #[serde(rename = "not-defined")]
    NotDefined,
    #[serde(rename = "optional")]
    Optional,
    #[serde(rename = "recommended")]
    Recommended,
    #[serde(rename = "required")]
    #[default]
    Required,
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct JsonProfileMessage {
    #[serde(default)]
    pub code: String,
    #[serde(default)]
    pub source: Option<String>,
    pub category: Category,
    #[serde(default)]
    pub name: Option<String>,
    pub message: String,
    #[serde(default)]
    pub enforcement_level: EnforcementLevel,
}

impl JsonProfileMessage {
    pub fn new(
        code: impl Into<String>,
        source: Option<String>,
        category: Category,
        name: Option<String>,
        message: impl Into<String>,
        enforcement_level: EnforcementLevel,
    ) -> Self {
        Self {
            code: code.into(),
            source,
            category,
            name,
            message: message.into(),
            enforcement_level,
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct CvTermFieldMapping {
    #[serde(default = "default_label_field")]
    pub label_field: Option<String>,
    #[serde(default = "default_accession_field")]
    pub accession_field: Option<String>,
    #[serde(default = "default_name_field")]
    pub name_field: Option<String>,
    #[serde(default = "default_value_field")]
    pub value_field: Option<String>,
}

fn default_label_field() -> Option<String> {
    Some("cv_label".to_string())
}
fn default_accession_field() -> Option<String> {
    Some("cv_accession".to_string())
}
fn default_name_field() -> Option<String> {
    Some("name".to_string())
}
fn default_value_field() -> Option<String> {
    Some("value".to_string())
}

impl Default for CvTermFieldMapping {
    fn default() -> Self {
        Self {
            label_field: default_label_field(),
            accession_field: default_accession_field(),
            name_field: default_name_field(),
            value_field: default_value_field(),
        }
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Serialize, Deserialize)]
pub struct ValidationRuntimeConfiguration {
    #[serde(default)]
    pub offline_mode: Option<bool>,
    #[serde(default)]
    pub skipped_requirements: Option<Vec<String>>,
    #[serde(default)]
    pub skip_jsonschema_validation: Option<bool>,
    #[serde(default)]
    pub skip_profile_validation: Option<bool>,
    #[serde(default = "default_max_messages")]
    pub max_messages_for_each_requirement: Option<usize>,
    #[serde(default)]
    pub skip_decimal_validations: Option<bool>,
    #[serde(default)]
    pub cv_term_search_class: Option<String>,
}

fn default_max_messages() -> Option<usize> {
    Some(10)
}

impl Default for ValidationRuntimeConfiguration {
    fn default() -> Self {
        Self {
            offline_mode: None,
            skipped_requirements: None,
            skip_jsonschema_validation: None,
            skip_profile_validation: None,
            max_messages_for_each_requirement: Some(10),
            skip_decimal_validations: None,
            cv_term_search_class: None,
        }
    }
}

impl ValidationRuntimeConfiguration {
    pub fn is_offline_mode(&self) -> bool {
        self.offline_mode.unwrap_or(false)
    }

    pub fn is_skip_jsonschema_validation(&self) -> bool {
        self.skip_jsonschema_validation.unwrap_or(false)
    }

    pub fn is_skip_profile_validation(&self) -> bool {
        self.skip_profile_validation.unwrap_or(false)
    }

    pub fn is_skip_decimal_validations(&self) -> bool {
        self.skip_decimal_validations.unwrap_or(false)
    }
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct BaseCvTerm {
    #[serde(default)]
    pub cv_label: Option<String>,
    #[serde(default)]
    pub cv_accession: Option<String>,
    #[serde(default)]
    pub name: Option<String>,
}

impl BaseCvTerm {
    pub fn new(
        cv_label: impl Into<String>,
        cv_accession: impl Into<String>,
        name: impl Into<String>,
    ) -> Self {
        Self {
            cv_label: Some(cv_label.into()),
            cv_accession: Some(cv_accession.into()),
            name: Some(name.into()),
        }
    }

    pub fn label(&self) -> &str {
        self.cv_label.as_deref().unwrap_or("")
    }

    pub fn accession(&self) -> &str {
        self.cv_accession.as_deref().unwrap_or("")
    }

    pub fn term_name(&self) -> &str {
        self.name.as_deref().unwrap_or("")
    }
}

impl std::fmt::Display for BaseCvTerm {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "[{}, {}, {}, ]",
            sanitize_str(self.label()),
            sanitize_str(self.accession()),
            sanitize_str(self.term_name())
        )
    }
}

impl PartialEq for BaseCvTerm {
    fn eq(&self, other: &Self) -> bool {
        self.to_string() == other.to_string()
    }
}

impl Eq for BaseCvTerm {}

impl std::hash::Hash for BaseCvTerm {
    fn hash<H: std::hash::Hasher>(&self, state: &mut H) {
        self.to_string().hash(state);
    }
}

#[derive(Debug, Clone, Serialize, Default)]
pub struct CvTerm {
    #[serde(default)]
    pub cv_label: String,
    #[serde(default)]
    pub cv_accession: String,
    #[serde(default)]
    pub name: String,
    #[serde(default)]
    pub value: String,
}

impl CvTerm {
    pub fn new(
        cv_label: impl Into<String>,
        cv_accession: impl Into<String>,
        name: impl Into<String>,
        value: impl Into<String>,
    ) -> Self {
        Self {
            cv_label: cv_label.into(),
            cv_accession: cv_accession.into(),
            name: name.into(),
            value: value.into(),
        }
    }

    pub fn parse_bracketed(s: &str) -> Self {
        let cleaned = s.trim().trim_start_matches('[').trim_end_matches(']');
        let parts: Vec<&str> = cleaned.splitn(4, ',').collect();
        Self {
            cv_label: parts.first().map(|x| x.trim()).unwrap_or("").to_string(),
            cv_accession: parts.get(1).map(|x| x.trim()).unwrap_or("").to_string(),
            name: parts.get(2).map(|x| x.trim()).unwrap_or("").to_string(),
            value: parts.get(3).map(|x| x.trim()).unwrap_or("").to_string(),
        }
    }

    pub fn from_json_value(val: &Value, mapping: &CvTermFieldMapping) -> Option<Self> {
        match val {
            Value::String(s) => Some(Self::parse_bracketed(s)),
            Value::Object(map) => {
                let get_field = |primary: &str, mapped: Option<&str>| -> String {
                    if let Some(v) = map.get(primary).and_then(value_to_opt_string) {
                        return v;
                    }
                    if let Some(m) = mapped {
                        if let Some(v) = map.get(m).and_then(value_to_opt_string) {
                            return v;
                        }
                    }
                    String::new()
                };
                Some(Self {
                    cv_label: get_field("cv_label", mapping.label_field.as_deref()),
                    cv_accession: get_field("cv_accession", mapping.accession_field.as_deref()),
                    name: get_field("name", mapping.name_field.as_deref()),
                    value: get_field("value", mapping.value_field.as_deref()),
                })
            }
            _ => None,
        }
    }

    pub fn to_base_cv_term(&self) -> BaseCvTerm {
        BaseCvTerm {
            cv_label: Some(self.cv_label.clone()),
            cv_accession: Some(self.cv_accession.clone()),
            name: Some(self.name.clone()),
        }
    }
}

fn value_to_opt_string(v: &Value) -> Option<String> {
    match v {
        Value::Null => None,
        Value::String(s) => Some(s.trim().to_string()),
        other => Some(other.to_string()),
    }
}

impl std::fmt::Display for CvTerm {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "[{}, {}, {}, {}]",
            sanitize_str(&self.cv_label),
            sanitize_str(&self.cv_accession),
            sanitize_str(&self.name),
            sanitize_str(&self.value)
        )
    }
}

impl PartialEq for CvTerm {
    fn eq(&self, other: &Self) -> bool {
        self.to_string() == other.to_string()
    }
}

impl Eq for CvTerm {}

impl<'de> Deserialize<'de> for CvTerm {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: Deserializer<'de>,
    {
        let val = Value::deserialize(deserializer)?;
        CvTerm::from_json_value(&val, &CvTermFieldMapping::default())
            .ok_or_else(|| serde::de::Error::custom("Invalid CvTerm value"))
    }
}

pub fn sanitize_str(val: &str) -> String {
    if val.is_empty() {
        return String::new();
    }
    let mut sanitized = val.trim().trim_matches('"').trim().to_string();
    sanitized = sanitized
        .chars()
        .map(|c| {
            if matches!(c, '\t' | '\n' | '\r') {
                ' '
            } else {
                c
            }
        })
        .filter(|&c| {
            let u = c as u32;
            !((u <= 0x1f && u != 0x20) || u == 0x7f)
        })
        .collect();
    while sanitized.contains("  ") {
        sanitized = sanitized.replace("  ", " ");
    }
    if sanitized.contains([',', '|', '"']) {
        format!("\"{sanitized}\"")
    } else {
        sanitized
    }
}

pub fn convert_full_path(full_path: &str) -> String {
    if full_path.is_empty() || full_path == "$" {
        return "$".to_string();
    }
    let mut path = full_path.to_string();
    // Normalize serde_json_path normalized paths like $['a'][0]['b'] -> $.a[0].b
    while let Some(start) = path.find("['") {
        if let Some(end) = path[start + 2..].find("']") {
            let key = &path[start + 2..start + 2 + end];
            let replacement = format!(".{key}");
            path.replace_range(start..start + 4 + end, &replacement);
        } else {
            break;
        }
    }
    path = path.replace(".[", "[");
    path = path.replace(['(', ')'], "");
    if path.starts_with("$.") {
        return path;
    }
    if let Some(path) = path.strip_prefix("$[") {
        return format!("$.[{path}");
    }
    let trimmed = path.trim_start_matches('$').trim_start_matches('.');
    if trimmed.is_empty() {
        "$".to_string()
    } else {
        format!("$.{trimmed}")
    }
}

pub fn instance_pointer_to_jsonpath(pointer: &str) -> String {
    if pointer.is_empty() || pointer == "/" || pointer == "$" {
        return "$".to_string();
    }
    let mut out = String::from("$");
    for seg in pointer.trim_start_matches('/').split('/') {
        if seg.chars().all(|c| c.is_ascii_digit()) {
            out.push('[');
            out.push_str(seg);
            out.push(']');
        } else {
            out.push('.');
            out.push_str(seg);
        }
    }
    out
}

pub fn value_as_cow_str(v: &Value) -> String {
    match v {
        Value::String(s) => s.clone(),
        Value::Null => String::new(),
        other => other.to_string(),
    }
}
