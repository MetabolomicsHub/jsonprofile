use crate::profile::base::{
    convert_full_path, instance_pointer_to_jsonpath, value_as_cow_str, Category, EnforcementLevel,
    JsonProfileMessage, ValidationRuntimeConfiguration,
};
use crate::profile::constraints::ConstraintKind;
use crate::profile::model::{
    FieldRequirement, FieldRequirementGroup, JsonProfile, JsonProfileConfiguration,
    OpaFieldRequirement, RequirementDefinition,
};
use crate::profile::profile_validator::{validate_profile, validate_profile_schema};
use crate::validator::checker::validate_constraint;
use crate::validator::context::{JsonProfileRunContext, JsonValidationResult, MessageCollector};
use crate::validator::cv_term_search::{CvTermSearch, OlsCvTermSearch};
use crate::validator::factory::{DefaultProfileValidatorFactory, ProfileValidatorFactory};
use crate::validator::opa_engine::OpaEngineFactory;
use serde_json::Value;
use std::collections::{BTreeMap, HashMap};
use std::fs;
use std::path::{Path, PathBuf};
use std::sync::{Arc, RwLock};

#[derive(Debug, Clone)]
#[allow(clippy::large_enum_variant)]
pub enum ProfileSource {
    Path(PathBuf),
    Json(Value),
    Model(JsonProfile),
}

impl From<&Path> for ProfileSource {
    fn from(p: &Path) -> Self {
        ProfileSource::Path(p.to_path_buf())
    }
}

impl From<PathBuf> for ProfileSource {
    fn from(p: PathBuf) -> Self {
        ProfileSource::Path(p)
    }
}

impl From<&str> for ProfileSource {
    fn from(s: &str) -> Self {
        ProfileSource::Path(PathBuf::from(s))
    }
}

impl From<String> for ProfileSource {
    fn from(s: String) -> Self {
        ProfileSource::Path(PathBuf::from(s))
    }
}

impl From<Value> for ProfileSource {
    fn from(v: Value) -> Self {
        ProfileSource::Json(v)
    }
}

impl From<JsonProfile> for ProfileSource {
    fn from(p: JsonProfile) -> Self {
        ProfileSource::Model(p)
    }
}

pub struct JsonValidator {
    pub json_schema: Option<Value>,
    schema_validator: Option<Arc<jsonschema::Validator>>,
    pub referenced_profiles: HashMap<String, ProfileSource>,
    pub json_profile: JsonProfile,
    pub default_cv_term_search: Arc<dyn CvTermSearch>,
    pub opa_engine_factory: Arc<dyn OpaEngineFactory>,
    pub profile_validator_factory: Arc<dyn ProfileValidatorFactory>,
}

impl JsonValidator {
    pub fn new(
        json_schema: Option<ProfileSource>,
        profile: impl Into<ProfileSource>,
    ) -> Result<Self, String> {
        Self::with_options(json_schema, profile, HashMap::new(), None, None)
    }

    pub fn with_referenced_profiles(
        json_schema: Option<ProfileSource>,
        profile: impl Into<ProfileSource>,
        referenced_profiles: HashMap<String, ProfileSource>,
    ) -> Result<Self, String> {
        Self::with_options(json_schema, profile, referenced_profiles, None, None)
    }

    pub fn with_options(
        json_schema: Option<ProfileSource>,
        profile: impl Into<ProfileSource>,
        mut referenced_profiles: HashMap<String, ProfileSource>,
        default_cv_term_search: Option<Arc<dyn CvTermSearch>>,
        opa_engine_factory: Option<Arc<dyn OpaEngineFactory>>,
    ) -> Result<Self, String> {
        let (schema_val, schema_validator) = if let Some(src) = json_schema {
            let val = Self::load_json_schema(src)?;
            let compiled = jsonschema::validator_for(&val)
                .map_err(|e| format!("Failed to compile JSON schema: {e}"))?;
            (Some(val), Some(Arc::new(compiled)))
        } else {
            (None, None)
        };

        let mut target_profile = Self::create_profile(profile.into(), &mut referenced_profiles)?;
        if target_profile.configuration.is_none() {
            target_profile.configuration = Some(JsonProfileConfiguration::default());
        }
        let config = target_profile.configuration.as_ref().unwrap();
        let cv_search: Arc<dyn CvTermSearch> =
            default_cv_term_search.unwrap_or_else(|| Arc::new(OlsCvTermSearch::new()));
        let opa_factory: Arc<dyn OpaEngineFactory> = opa_engine_factory
            .unwrap_or_else(crate::validator::opa_engine::default_opa_engine_factory);

        let custom_defs = config
            .custom_validator_definitions
            .clone()
            .unwrap_or_default();
        let mut default_validator_id: Option<String> = None;
        if let Some(key) = &config.default_validator_key {
            for def in &custom_defs {
                if &def.label == key {
                    default_validator_id = Some(def.validator_id.clone());
                    break;
                }
            }
        }

        let factory = Arc::new(DefaultProfileValidatorFactory::new(
            &custom_defs,
            default_validator_id,
            Arc::clone(&opa_factory),
        )?);

        Ok(Self {
            json_schema: schema_val,
            schema_validator,
            referenced_profiles,
            json_profile: target_profile,
            default_cv_term_search: cv_search,
            opa_engine_factory: opa_factory,
            profile_validator_factory: factory,
        })
    }

    pub fn load_json_schema(src: ProfileSource) -> Result<Value, String> {
        match src {
            ProfileSource::Path(p) => {
                if !p.exists() {
                    return Err(format!(
                        "Json schema ({}) file does not exist.",
                        p.display()
                    ));
                }
                let content = fs::read_to_string(&p)
                    .map_err(|e| format!("Failed to read schema file: {e}"))?;
                serde_json::from_str(&content)
                    .map_err(|e| format!("Failed to parse schema JSON: {e}"))
            }
            ProfileSource::Json(v) => Ok(v),
            ProfileSource::Model(m) => {
                serde_json::to_value(m).map_err(|e| format!("Failed to serialize schema: {e}"))
            }
        }
    }

    pub fn create_profile(
        profile: ProfileSource,
        referenced_profiles: &mut HashMap<String, ProfileSource>,
    ) -> Result<JsonProfile, String> {
        let target_profile: JsonProfile = match profile {
            ProfileSource::Path(p) => {
                if !p.exists() {
                    return Err(format!(
                        "Json profile ({}) file does not exist.",
                        p.display()
                    ));
                }
                let content = fs::read_to_string(&p)
                    .map_err(|e| format!("Failed to read profile file: {e}"))?;
                let value: Value = serde_json::from_str(&content)
                    .map_err(|e| format!("Failed to parse profile JSON: {e}"))?;
                validate_profile_schema(&value)
                    .map_err(|e| format!("Profile JSON does not match JsonProfile schema: {e}"))?;
                serde_json::from_value(value)
                    .map_err(|e| format!("Failed to parse profile JSON: {e}"))?
            }
            ProfileSource::Json(v) => {
                validate_profile_schema(&v)
                    .map_err(|e| format!("Profile JSON does not match JsonProfile schema: {e}"))?;
                serde_json::from_value(v)
                    .map_err(|e| format!("Failed to deserialize JsonProfile: {e}"))?
            }
            ProfileSource::Model(m) => m,
        };

        if let Some(parent_id) = target_profile
            .extends
            .clone()
            .filter(|s| !s.trim().is_empty())
        {
            let Some(parent_src) = referenced_profiles.get(&parent_id).cloned() else {
                return Err(format!("Referenced Json profile {parent_id} not found."));
            };
            let referenced_profile = Self::create_profile(parent_src, referenced_profiles)?;
            referenced_profiles.insert(
                referenced_profile.id.clone(),
                ProfileSource::Model(referenced_profile.clone()),
            );
            return Ok(Self::extend_profile(
                Some(&referenced_profile),
                target_profile,
            ));
        }

        referenced_profiles.insert(
            target_profile.id.clone(),
            ProfileSource::Model(target_profile.clone()),
        );
        let _ = validate_profile(&target_profile);
        Ok(target_profile)
    }

    pub fn extend_profile(
        parent_profile: Option<&JsonProfile>,
        mut input_profile: JsonProfile,
    ) -> JsonProfile {
        let Some(parent) = parent_profile else {
            if input_profile.configuration.is_none() {
                input_profile.configuration = Some(JsonProfileConfiguration::default());
            }
            return input_profile;
        };

        let new_reqs = input_profile.requirements.clone().unwrap_or_default();
        let mut merged_reqs = parent.requirements.clone().unwrap_or_default();

        match (&mut input_profile.configuration, &parent.configuration) {
            (None, Some(p_cfg)) => {
                input_profile.configuration = Some(p_cfg.clone());
            }
            (None, None) => {
                input_profile.configuration = Some(JsonProfileConfiguration::default());
            }
            (Some(c_cfg), Some(p_cfg)) => {
                c_cfg.merge_from_parent(p_cfg);
            }
            (Some(_), None) => {}
        }

        for (k, v) in new_reqs {
            if v.is_none() {
                merged_reqs.remove(&k);
            } else {
                merged_reqs.insert(k, v);
            }
        }
        input_profile.requirements = Some(merged_reqs);
        input_profile
    }

    pub fn validate_json_file(
        &self,
        json_file_path: impl AsRef<Path>,
        runtime_config: Option<ValidationRuntimeConfiguration>,
    ) -> Result<JsonValidationResult, String> {
        let path = json_file_path.as_ref();
        if !path.exists() {
            return Err("Json file does not exist.".to_string());
        }
        let content =
            fs::read_to_string(path).map_err(|e| format!("Failed to read JSON file: {e}"))?;
        let val: Value = serde_json::from_str(&content)
            .map_err(|e| format!("Failed to parse JSON file: {e}"))?;
        Ok(self.validate_dict(&val, runtime_config))
    }

    pub fn validate_dict(
        &self,
        input_json: &Value,
        runtime_config: Option<ValidationRuntimeConfiguration>,
    ) -> JsonValidationResult {
        let mut rt = runtime_config.unwrap_or_default();
        if rt.skipped_requirements.is_none() {
            rt.skipped_requirements = Some(Vec::new());
        }
        let id_fields = self
            .json_profile
            .configuration
            .as_ref()
            .and_then(|c| c.object_unique_id_fields.clone());
        let mut unique_id_objects: HashMap<String, Value> = HashMap::new();
        if let Some(fields) = id_fields.as_ref().filter(|f| !f.is_empty()) {
            Self::update_unique_object_dict("$", input_json, fields, &mut unique_id_objects);
        }

        let max_msgs = rt.max_messages_for_each_requirement;
        let context = JsonProfileRunContext {
            runtime_config: rt.clone(),
            profile_config: self.json_profile.configuration.clone().unwrap_or_default(),
            cv_term_search: Arc::clone(&self.default_cv_term_search),
            profile_validator_factory: Arc::clone(&self.profile_validator_factory),
            opa_engine_factory: Arc::clone(&self.opa_engine_factory),
            message_collector: RwLock::new(MessageCollector::new(max_msgs)),
            json_path_expressions: RwLock::new(HashMap::new()),
            unique_id_objects,
        };

        if !rt.is_skip_jsonschema_validation() && self.schema_validator.is_some() {
            self.validate_json_with_schema(input_json, &context);
        }
        if rt.is_skip_profile_validation() {
            return context
                .message_collector
                .read()
                .map(|c| c.process_messages())
                .unwrap_or_default();
        }

        if let Some(requirements) = &self.json_profile.requirements {
            for (raw_path, req_opt) in requirements {
                let json_path = if raw_path.is_empty() {
                    "$"
                } else {
                    raw_path.as_str()
                };
                let Some(req_def) = req_opt else {
                    continue;
                };
                let group = match req_def {
                    RequirementDefinition::Group(g) => g.clone(),
                    other => FieldRequirementGroup {
                        code: other.code().map(str::to_string),
                        description: other.description().map(str::to_string),
                        enforcement_level: EnforcementLevel::Required,
                        requirements: vec![other.clone()],
                        min_valid: None,
                        max_valid: None,
                    },
                };
                let mut group_collector = MessageCollector::new(None);
                self.validate_requirement(
                    &RequirementDefinition::Group(group),
                    json_path,
                    input_json,
                    &context,
                    &mut group_collector,
                );
                if let Ok(mut main_guard) = context.message_collector.write() {
                    for path_key in &group_collector.ordered_paths {
                        if let Some(by_code) = group_collector.messages.get(path_key) {
                            for msg_list in by_code.values() {
                                for msg in msg_list {
                                    main_guard.add_message(path_key, msg.clone());
                                }
                            }
                        }
                    }
                }
            }
        }

        context
            .message_collector
            .read()
            .map(|c| c.process_messages())
            .unwrap_or_default()
    }

    pub fn validate_json_with_schema(&self, json_data: &Value, context: &JsonProfileRunContext) {
        let Some(validator) = &self.schema_validator else {
            return;
        };
        let mut errors: Vec<(String, String)> = validator
            .iter_errors(json_data)
            .map(|err| {
                let jp = instance_pointer_to_jsonpath(&err.instance_path.to_string());
                (jp, err.to_string())
            })
            .collect();
        errors.sort();
        if let Ok(mut guard) = context.message_collector.write() {
            for (json_path, err_msg) in errors {
                guard.add_message(
                    &json_path,
                    JsonProfileMessage::new(
                        "SCHEMA-0001",
                        Some(json_path.clone()),
                        Category::Schema,
                        Some("jsonschema".to_string()),
                        format!("Json schema validation error: {err_msg}"),
                        EnforcementLevel::Required,
                    ),
                );
            }
        }
    }

    pub fn update_unique_object_dict(
        object_path: &str,
        object_item: &Value,
        id_fields: &[String],
        objects: &mut HashMap<String, Value>,
    ) {
        match object_item {
            Value::Array(list) => {
                for (i, item) in list.iter().enumerate() {
                    Self::update_unique_object_dict(
                        &format!("{object_path}[{i}]"),
                        item,
                        id_fields,
                        objects,
                    );
                }
            }
            Value::Object(map) => {
                for (field_name, val) in map {
                    let item_path = format!("{object_path}.{field_name}");
                    if id_fields.contains(field_name) && map.len() > 1 {
                        let key = value_as_cow_str(val);
                        if !key.is_empty() && !objects.contains_key(&key) {
                            objects.insert(key, object_item.clone());
                        }
                    }
                    if val.is_object() || val.is_array() {
                        Self::update_unique_object_dict(&item_path, val, id_fields, objects);
                    }
                }
            }
            _ => {}
        }
    }

    fn validate_requirement(
        &self,
        field_requirement: &RequirementDefinition,
        json_path: &str,
        input_json: &Value,
        context: &JsonProfileRunContext,
        group_collector: &mut MessageCollector,
    ) {
        match field_requirement {
            RequirementDefinition::Opa(opa_req) => {
                let _ = self.validate_opa_field_requirement(
                    std::slice::from_ref(opa_req),
                    json_path,
                    input_json,
                    context,
                    group_collector,
                );
            }
            RequirementDefinition::Group(group) => {
                let mut sub_collector = MessageCollector::new(None);
                self.validate_opa_field_requirements(
                    group,
                    json_path,
                    input_json,
                    context,
                    &mut sub_collector,
                );
                for item in &group.requirements {
                    if matches!(item, RequirementDefinition::Opa(_)) {
                        continue;
                    }
                    self.validate_requirement(
                        item,
                        json_path,
                        input_json,
                        context,
                        &mut sub_collector,
                    );
                }

                let mut instances: BTreeMap<String, BTreeMap<String, Vec<JsonProfileMessage>>> =
                    BTreeMap::new();
                let mut instance_order: Vec<String> = Vec::new();
                for source_json_path in &sub_collector.ordered_paths {
                    let Some(by_code) = sub_collector.messages.get(source_json_path) else {
                        continue;
                    };
                    if !instances.contains_key(source_json_path) {
                        instance_order.push(source_json_path.clone());
                    }
                    let inst_entry = instances.entry(source_json_path.clone()).or_default();
                    for (code, msgs) in by_code {
                        let req_items: Vec<JsonProfileMessage> = msgs
                            .iter()
                            .filter(|x| x.enforcement_level == EnforcementLevel::Required)
                            .cloned()
                            .collect();
                        if !req_items.is_empty() {
                            inst_entry.insert(code.clone(), req_items);
                        }
                        for non_req in msgs {
                            if non_req.enforcement_level != EnforcementLevel::Required {
                                let src = non_req.source.as_deref().unwrap_or(source_json_path);
                                group_collector.add_message(src, non_req.clone());
                            }
                        }
                    }
                }

                let total_reqs = group.requirements.len();
                for inst_key in &instance_order {
                    let Some(message_list) = instances.get(inst_key) else {
                        continue;
                    };
                    let valid_reqs = total_reqs.saturating_sub(message_list.len());
                    let mut valid_group = total_reqs == valid_reqs;
                    if !valid_group && (group.min_valid.is_some() || group.max_valid.is_some()) {
                        let min_bound = group.min_valid.unwrap_or(1);
                        let max_bound = group.max_valid.unwrap_or(total_reqs);
                        if valid_reqs >= min_bound && valid_reqs <= max_bound {
                            valid_group = true;
                        }
                    }
                    if !valid_group {
                        for items in message_list.values() {
                            for msg in items {
                                let src = msg.source.as_deref().unwrap_or(json_path);
                                group_collector.add_message(src, msg.clone());
                            }
                        }
                    }
                }
            }
            RequirementDefinition::Field(req) => {
                self.validate_field_requirement(
                    req,
                    json_path,
                    input_json,
                    context,
                    group_collector,
                );
            }
        }
    }

    fn validate_field_requirement(
        &self,
        req: &FieldRequirement,
        json_path: &str,
        input_json: &Value,
        context: &JsonProfileRunContext,
        group_collector: &mut MessageCollector,
    ) {
        let code = req.code.as_deref().unwrap_or("");
        let constraint_name = req
            .value_constraint
            .as_ref()
            .map(|c| c.constraint_type())
            .unwrap_or("field-requirement");

        if let Some(vc) = &req.value_constraint {
            if matches!(vc.kind, ConstraintKind::Decimal { .. })
                && context.runtime_config.is_skip_decimal_validations()
            {
                group_collector.add_message(
                    json_path,
                    JsonProfileMessage::new(
                        code,
                        Some(json_path.to_string()),
                        Category::Profile,
                        Some(vc.constraint_type().to_string()),
                        format!("{code} decimal validations are skipped for '{json_path}'"),
                        EnforcementLevel::Recommended,
                    ),
                );
                return;
            }
        }

        let matched: Vec<(String, Value)> = if json_path == "$" {
            vec![("$".to_string(), input_json.clone())]
        } else if let Ok(compiled) = context.compile_or_get_json_path(json_path) {
            compiled
                .query_located(input_json)
                .into_iter()
                .map(|loc| {
                    (
                        convert_full_path(&loc.location().to_string()),
                        loc.node().clone(),
                    )
                })
                .collect()
        } else {
            Vec::new()
        };

        if matched.is_empty() {
            if req.match_is_required.unwrap_or(false) {
                group_collector.add_message(
                    json_path,
                    JsonProfileMessage::new(
                        code,
                        Some(json_path.to_string()),
                        Category::Profile,
                        Some(constraint_name.to_string()),
                        format!("There is no value with json path '{json_path}'"),
                        req.enforcement_level,
                    ),
                );
            }
            return;
        }

        for (source, val) in matched {
            if group_collector.is_open(code) {
                break;
            }
            let has_prop_checks = req
                .required_properties
                .as_ref()
                .is_some_and(|p| !p.is_empty())
                || req
                    .recommended_properties
                    .as_ref()
                    .is_some_and(|p| !p.is_empty());

            if has_prop_checks && !val.is_object() {
                group_collector.add_message(
                    &source,
                    JsonProfileMessage::new(
                        code,
                        Some(source.clone()),
                        Category::Profile,
                        Some("property-check".to_string()),
                        format!("Property check failed. '{source}' is not defined."),
                        EnforcementLevel::Required,
                    ),
                );
            } else if let Value::Object(map_val) = &val {
                if let Some(req_props) = &req.required_properties {
                    let missing_req: Vec<&str> = req_props
                        .iter()
                        .filter(|p| !map_val.contains_key(*p))
                        .map(String::as_str)
                        .collect();
                    if !missing_req.is_empty() {
                        group_collector.add_message(
                            &source,
                            JsonProfileMessage::new(
                                code,
                                Some(source.clone()),
                                Category::Profile,
                                Some("property-check".to_string()),
                                format!(
                                    "Required property does not exist {} is not in object '{}'",
                                    missing_req.join(", "),
                                    source
                                ),
                                EnforcementLevel::Required,
                            ),
                        );
                    }
                }
                if let Some(rec_props) = &req.recommended_properties {
                    let missing_rec: Vec<&str> = rec_props
                        .iter()
                        .filter(|p| !map_val.contains_key(*p))
                        .map(String::as_str)
                        .collect();
                    if !missing_rec.is_empty() {
                        if let Ok(mut main_guard) = context.message_collector.write() {
                            main_guard.add_message(
                                &source,
                                JsonProfileMessage::new(
                                    code,
                                    Some(source.clone()),
                                    Category::Profile,
                                    Some("property-check".to_string()),
                                    format!(
                                        "Recommended property does not exist. {} field(s) not in object '{}'",
                                        missing_rec.join(", "),
                                        source
                                    ),
                                    EnforcementLevel::Recommended,
                                ),
                            );
                        }
                    }
                }
            }

            if let Some(mut constraint) = req.value_constraint.clone() {
                let skip = context.runtime_config.is_skip_decimal_validations()
                    && matches!(constraint.kind, ConstraintKind::Decimal { .. });
                if !skip {
                    if constraint.common.description.is_none() {
                        constraint.common.description = req.description.clone();
                    }
                    let res = validate_constraint(&constraint, &val, input_json, context);
                    if !res.is_valid {
                        group_collector.add_message(
                            &source,
                            JsonProfileMessage::new(
                                code,
                                Some(source.clone()),
                                Category::Profile,
                                Some(constraint.constraint_type().to_string()),
                                res.message.unwrap_or_default(),
                                req.enforcement_level,
                            ),
                        );
                    }
                }
            }
        }
    }

    fn validate_opa_field_requirements(
        &self,
        group: &FieldRequirementGroup,
        json_path: &str,
        input_json: &Value,
        context: &JsonProfileRunContext,
        group_collector: &mut MessageCollector,
    ) {
        let mut by_wasm: BTreeMap<String, Vec<OpaFieldRequirement>> = BTreeMap::new();
        let skipped = context
            .runtime_config
            .skipped_requirements
            .as_deref()
            .unwrap_or(&[]);

        for item in &group.requirements {
            let RequirementDefinition::Opa(opa_field) = item else {
                continue;
            };
            let code = opa_field.code.as_deref().unwrap_or("");
            if skipped.iter().any(|s| s == code) {
                group_collector.add_message(
                    json_path,
                    JsonProfileMessage::new(
                        code,
                        Some(json_path.to_string()),
                        Category::Profile,
                        Some(opa_field.policy_id.clone()),
                        format!("{code} of '{json_path}' is in skipped list."),
                        EnforcementLevel::Recommended,
                    ),
                );
                continue;
            }
            let key = opa_field
                .wasm_file_key
                .clone()
                .unwrap_or_else(|| "default".to_string());
            by_wasm.entry(key).or_default().push(opa_field.clone());
        }

        for batch in by_wasm.values() {
            if !batch.is_empty() {
                let _ = self.validate_opa_field_requirement(
                    batch,
                    json_path,
                    input_json,
                    context,
                    group_collector,
                );
            }
        }
    }

    fn validate_opa_field_requirement(
        &self,
        opa_reqs: &[OpaFieldRequirement],
        json_path: &str,
        input_json: &Value,
        context: &JsonProfileRunContext,
        group_collector: &mut MessageCollector,
    ) -> Result<(), String> {
        let policy_ids: Vec<String> = opa_reqs.iter().map(|r| r.policy_id.clone()).collect();
        let mut by_id: HashMap<String, &OpaFieldRequirement> = HashMap::new();
        for r in opa_reqs {
            by_id.insert(r.policy_id.clone(), r);
        }
        let label = opa_reqs
            .first()
            .and_then(|r| r.wasm_file_key.as_deref())
            .unwrap_or("default");
        let mut wasm_file_path = "";
        let mut wasm_download_url = None;
        let mut entrypoint = None;
        if let Some(defs) = &context.profile_config.wasm_file_definitions {
            let opa_cfg = defs
                .get(label)
                .ok_or_else(|| "OPA policy file not found".to_string())?;
            wasm_file_path = &opa_cfg.wasm_file_path;
            wasm_download_url = Some(opa_cfg.wasm_file_download_url.as_str());
            entrypoint = Some(opa_cfg.entrypoint.as_str());
        }

        let engine = context
            .profile_validator_factory
            .opa_engine_factory()
            .get_opa_engine(wasm_file_path, wasm_download_url)?;

        let sub_val = if json_path == "$" {
            input_json.clone()
        } else if let Ok(expr) = context.compile_or_get_json_path(json_path) {
            let found: Vec<Value> = expr.query(input_json).into_iter().cloned().collect();
            match found.len() {
                0 => Value::Null,
                1 => found.into_iter().next().unwrap_or(Value::Null),
                _ => Value::Array(found),
            }
        } else {
            Value::Null
        };

        let opa_input = serde_json::json!({
            "policy_ids": policy_ids,
            "value": sub_val,
            "root": input_json,
            "config": context.profile_config,
            "constraint": Value::Null,
        });

        let result = engine.evaluate(&opa_input, entrypoint, None)?;
        let first_decision = if let Some(arr) = result.as_array() {
            arr.first()
        } else {
            Some(&result)
        };
        let Some(dec) = first_decision else {
            return Err("OPA policy decision is empty.".to_string());
        };
        if let Some(msgs_list) = dec.get("messages").and_then(|v| v.as_array()) {
            for item_obj in msgs_list {
                let pid = item_obj
                    .get("policy_id")
                    .and_then(|v| v.as_str())
                    .unwrap_or("");
                let source = item_obj
                    .get("source")
                    .and_then(|v| v.as_str())
                    .unwrap_or(json_path);
                let msg_text = item_obj
                    .get("message")
                    .and_then(|v| v.as_str())
                    .unwrap_or("");
                let req = by_id.get(pid);
                let added = group_collector.add_message(
                    json_path,
                    JsonProfileMessage::new(
                        req.and_then(|r| r.code.as_deref()).unwrap_or(""),
                        Some(source.to_string()),
                        Category::Profile,
                        Some("opa-field-requirement".to_string()),
                        msg_text,
                        req.map(|r| r.enforcement_level)
                            .unwrap_or(EnforcementLevel::Required),
                    ),
                );
                if !added {
                    break;
                }
            }
        }
        Ok(())
    }
}
