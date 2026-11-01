use crate::profile::base::{CvTermFieldMapping, EnforcementLevel};
use crate::profile::constraints::Constraint;
use serde::{Deserialize, Deserializer, Serialize};
use serde_json::Value;
use std::collections::BTreeMap;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct FieldRequirement {
    #[serde(default)]
    pub code: Option<String>,
    #[serde(default)]
    pub description: Option<String>,
    #[serde(default)]
    pub enforcement_level: EnforcementLevel,
    #[serde(default)]
    pub value_constraint: Option<Constraint>,
    #[serde(default)]
    pub match_is_required: Option<bool>,
    #[serde(default)]
    pub required_properties: Option<Vec<String>>,
    #[serde(default)]
    pub recommended_properties: Option<Vec<String>>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct OpaFieldRequirement {
    #[serde(default)]
    pub code: Option<String>,
    #[serde(default)]
    pub description: Option<String>,
    #[serde(default)]
    pub enforcement_level: EnforcementLevel,
    #[serde(default)]
    pub wasm_file_key: Option<String>,
    pub policy_id: String,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct FieldRequirementGroup {
    #[serde(default)]
    pub code: Option<String>,
    #[serde(default)]
    pub description: Option<String>,
    #[serde(default)]
    pub enforcement_level: EnforcementLevel,
    pub requirements: Vec<RequirementDefinition>,
    #[serde(default)]
    pub min_valid: Option<usize>,
    #[serde(default)]
    pub max_valid: Option<usize>,
}

#[derive(Debug, Clone, Serialize)]
#[serde(untagged)]
#[allow(clippy::large_enum_variant)]
pub enum RequirementDefinition {
    Group(FieldRequirementGroup),
    Opa(OpaFieldRequirement),
    Field(FieldRequirement),
}

impl RequirementDefinition {
    pub fn code(&self) -> Option<&str> {
        match self {
            RequirementDefinition::Group(g) => g.code.as_deref(),
            RequirementDefinition::Opa(o) => o.code.as_deref(),
            RequirementDefinition::Field(f) => f.code.as_deref(),
        }
    }

    pub fn description(&self) -> Option<&str> {
        match self {
            RequirementDefinition::Group(g) => g.description.as_deref(),
            RequirementDefinition::Opa(o) => o.description.as_deref(),
            RequirementDefinition::Field(f) => f.description.as_deref(),
        }
    }
}

impl<'de> Deserialize<'de> for RequirementDefinition {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: Deserializer<'de>,
    {
        let v = Value::deserialize(deserializer)?;
        let obj = v
            .as_object()
            .ok_or_else(|| serde::de::Error::custom("Requirement must be a JSON object"))?;
        if obj.contains_key("requirements") {
            let group: FieldRequirementGroup =
                serde_json::from_value(v).map_err(serde::de::Error::custom)?;
            Ok(RequirementDefinition::Group(group))
        } else if obj.contains_key("policy_id") {
            let opa: OpaFieldRequirement =
                serde_json::from_value(v).map_err(serde::de::Error::custom)?;
            Ok(RequirementDefinition::Opa(opa))
        } else {
            let field: FieldRequirement =
                serde_json::from_value(v).map_err(serde::de::Error::custom)?;
            Ok(RequirementDefinition::Field(field))
        }
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct ProfileValidatorDefinition {
    pub label: String,
    pub validator_id: String,
    pub profile_validator_class: BTreeMap<String, String>,
    #[serde(default)]
    pub description: Option<String>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct WasmFileDefinition {
    pub wasm_file_download_url: String,
    pub wasm_file_path: String,
    pub entrypoint: String,
}

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
pub struct JsonProfileConfiguration {
    #[serde(default)]
    pub cv_term_field_mapping: CvTermFieldMapping,
    #[serde(default)]
    pub supported_cv_lists: Option<Vec<String>>,
    #[serde(default)]
    pub supported_cv_list_enforcement_level: Option<EnforcementLevel>,
    #[serde(default)]
    pub custom_validator_definitions: Option<Vec<ProfileValidatorDefinition>>,
    #[serde(default)]
    pub default_validator_key: Option<String>,
    #[serde(default)]
    pub profile_validator_factory_class: Option<BTreeMap<String, String>>,
    #[serde(default)]
    pub profile_validator_factory_class_arguments: Option<BTreeMap<String, Value>>,
    #[serde(default)]
    pub default_wasm_file_key: Option<String>,
    #[serde(default)]
    pub wasm_file_definitions: Option<BTreeMap<String, WasmFileDefinition>>,
    #[serde(default)]
    pub object_unique_id_fields: Option<Vec<String>>,
}

impl JsonProfileConfiguration {
    pub fn merge_from_parent(&mut self, parent: &JsonProfileConfiguration) {
        if self.supported_cv_lists.is_none() {
            self.supported_cv_lists = parent.supported_cv_lists.clone();
        }
        if self.supported_cv_list_enforcement_level.is_none() {
            self.supported_cv_list_enforcement_level = parent.supported_cv_list_enforcement_level;
        }
        if self.custom_validator_definitions.is_none() {
            self.custom_validator_definitions = parent.custom_validator_definitions.clone();
        }
        if self.default_validator_key.is_none() {
            self.default_validator_key = parent.default_validator_key.clone();
        }
        if self.profile_validator_factory_class.is_none() {
            self.profile_validator_factory_class = parent.profile_validator_factory_class.clone();
        }
        if self.profile_validator_factory_class_arguments.is_none() {
            self.profile_validator_factory_class_arguments =
                parent.profile_validator_factory_class_arguments.clone();
        }
        if self.default_wasm_file_key.is_none() {
            self.default_wasm_file_key = parent.default_wasm_file_key.clone();
        }
        if self.wasm_file_definitions.is_none() {
            self.wasm_file_definitions = parent.wasm_file_definitions.clone();
        }
        if self.object_unique_id_fields.is_none() {
            self.object_unique_id_fields = parent.object_unique_id_fields.clone();
        }
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct JsonProfile {
    pub id: String,
    #[serde(default)]
    pub extends: Option<String>,
    pub version: String,
    pub name: String,
    #[serde(default)]
    pub description: Option<String>,
    #[serde(default)]
    pub configuration: Option<JsonProfileConfiguration>,
    #[serde(default)]
    pub requirements: Option<BTreeMap<String, Option<RequirementDefinition>>>,
}
