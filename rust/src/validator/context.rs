use crate::profile::base::{EnforcementLevel, JsonProfileMessage, ValidationRuntimeConfiguration};
use crate::profile::model::JsonProfileConfiguration;
use crate::validator::cv_term_search::CvTermSearch;
use crate::validator::factory::ProfileValidatorFactory;
use crate::validator::opa_engine::OpaEngineFactory;
use serde::{Deserialize, Serialize};
use serde_json::Value;
use serde_json_path::JsonPath;
use std::collections::{BTreeMap, HashMap};
use std::sync::{Arc, RwLock};

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct JsonValidationResult {
    pub errors: BTreeMap<String, Vec<JsonProfileMessage>>,
    pub recommendations: BTreeMap<String, Vec<JsonProfileMessage>>,
    pub optionals: BTreeMap<String, Vec<JsonProfileMessage>>,
}

#[derive(Debug, Clone, Default)]
pub struct MessageCollector {
    pub max_messages_for_each_requirement: Option<usize>,
    pub messages: BTreeMap<String, BTreeMap<String, Vec<JsonProfileMessage>>>,
    pub code_messages: BTreeMap<String, Vec<JsonProfileMessage>>,
    pub ordered_paths: Vec<String>,
}

impl MessageCollector {
    pub fn new(max_messages_for_each_requirement: Option<usize>) -> Self {
        Self {
            max_messages_for_each_requirement,
            messages: BTreeMap::new(),
            code_messages: BTreeMap::new(),
            ordered_paths: Vec::new(),
        }
    }

    pub fn add_message(&mut self, json_path: &str, message: JsonProfileMessage) -> bool {
        let code = message.code.clone();
        if !self.is_open(&code) {
            if !self.messages.contains_key(json_path) {
                self.ordered_paths.push(json_path.to_string());
            }
            self.messages
                .entry(json_path.to_string())
                .or_default()
                .entry(code.clone())
                .or_default()
                .push(message.clone());

            self.code_messages.entry(code).or_default().push(message);
            return true;
        }
        false
    }

    pub fn is_open(&self, code: &str) -> bool {
        let Some(limit) = self.max_messages_for_each_requirement else {
            return false;
        };
        let count = self.code_messages.get(code).map(|v| v.len()).unwrap_or(0);
        count >= limit
    }

    pub fn process_messages(&self) -> JsonValidationResult {
        let mut errors: BTreeMap<String, Vec<JsonProfileMessage>> = BTreeMap::new();
        let mut recommendations: BTreeMap<String, Vec<JsonProfileMessage>> = BTreeMap::new();
        let mut optionals: BTreeMap<String, Vec<JsonProfileMessage>> = BTreeMap::new();

        for (k, by_code) in &self.messages {
            for data in by_code.values() {
                for x in data {
                    match x.enforcement_level {
                        EnforcementLevel::Required => {
                            errors.entry(k.clone()).or_default().push(x.clone());
                        }
                        EnforcementLevel::Recommended => {
                            recommendations
                                .entry(k.clone())
                                .or_default()
                                .push(x.clone());
                        }
                        EnforcementLevel::Optional => {
                            optionals.entry(k.clone()).or_default().push(x.clone());
                        }
                        EnforcementLevel::NotDefined => {}
                    }
                }
            }
        }

        JsonValidationResult {
            errors,
            recommendations,
            optionals,
        }
    }
}

pub struct JsonProfileRunContext {
    pub runtime_config: ValidationRuntimeConfiguration,
    pub profile_config: JsonProfileConfiguration,
    pub cv_term_search: Arc<dyn CvTermSearch>,
    pub profile_validator_factory: Arc<dyn ProfileValidatorFactory>,
    pub opa_engine_factory: Arc<dyn OpaEngineFactory>,
    pub message_collector: RwLock<MessageCollector>,
    pub json_path_expressions: RwLock<HashMap<String, Arc<JsonPath>>>,
    pub unique_id_objects: HashMap<String, Value>,
}

impl JsonProfileRunContext {
    pub fn compile_or_get_json_path(&self, expr: &str) -> Result<Arc<JsonPath>, String> {
        let normalized = normalize_jsonpath_syntax(expr);
        if let Ok(read_guard) = self.json_path_expressions.read() {
            if let Some(found) = read_guard.get(&normalized) {
                return Ok(Arc::clone(found));
            }
        }
        let parsed = Arc::new(
            JsonPath::parse(&normalized).map_err(|e| format!("Invalid JSONPath '{expr}': {e}"))?,
        );
        if let Ok(mut write_guard) = self.json_path_expressions.write() {
            write_guard.insert(normalized, Arc::clone(&parsed));
        }
        Ok(parsed)
    }
}

/// Normalizes minor jsonpath_ng syntax differences (e.g. `$.[*]`, `[*]`, or hyphenated `.mzTab-ID`) for RFC 9535 `serde_json_path`.
pub fn normalize_jsonpath_syntax(expr: &str) -> String {
    let trimmed = expr.trim();
    if trimmed.is_empty() {
        return "$".to_string();
    }
    let mut s = if trimmed.starts_with('$') {
        trimmed.to_string()
    } else if trimmed.starts_with('[') || trimmed.starts_with('.') {
        format!("${trimmed}")
    } else {
        format!("$.{trimmed}")
    };
    s = s.replace("$.[", "$[");
    escape_non_rfc9535_dot_segments(&s)
}

fn escape_non_rfc9535_dot_segments(input: &str) -> String {
    let bytes = input.as_bytes();
    let mut out = String::with_capacity(input.len() + 8);
    let mut i = 0;
    let mut bracket_depth = 0usize;
    while i < bytes.len() {
        let b = bytes[i];
        if b == b'[' {
            bracket_depth += 1;
            out.push('[');
            i += 1;
        } else if b == b']' {
            bracket_depth = bracket_depth.saturating_sub(1);
            out.push(']');
            i += 1;
        } else if b == b'.' && bracket_depth == 0 {
            if i + 1 < bytes.len() && bytes[i + 1] == b'.' {
                out.push_str("..");
                i += 2;
                continue;
            }
            let start = i + 1;
            let mut end = start;
            while end < bytes.len() && bytes[end] != b'.' && bytes[end] != b'[' {
                end += 1;
            }
            let seg = &input[start..end];
            if seg.contains('-') || seg.contains('@') {
                out.push_str("['");
                out.push_str(seg);
                out.push_str("']");
            } else {
                out.push('.');
                out.push_str(seg);
            }
            i = end;
        } else {
            let ch = input[i..].chars().next().unwrap();
            out.push(ch);
            i += ch.len_utf8();
        }
    }
    out
}
