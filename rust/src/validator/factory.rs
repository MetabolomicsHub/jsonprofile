use crate::profile::constraints::Constraint;
use crate::profile::model::ProfileValidatorDefinition;
use crate::validator::checker::ConstraintChecker;
use crate::validator::checkers::register_default_and_custom_checkers;
use crate::validator::opa_engine::OpaEngineFactory;
use std::collections::HashMap;
use std::sync::{Arc, RwLock};

pub trait ProfileValidator: Send + Sync {
    fn get_id(&self) -> &str;
    fn get_checker(&self, constraint: &Constraint) -> Result<Arc<dyn ConstraintChecker>, String>;
    fn get_checker_by_name(
        &self,
        constraint_type: &str,
        constraint_name: Option<&str>,
    ) -> Result<Arc<dyn ConstraintChecker>, String>;
    fn register_checker(
        &self,
        constraint_type: &str,
        constraint_name: Option<&str>,
        checker: Arc<dyn ConstraintChecker>,
    );
}

pub trait ProfileValidatorFactory: Send + Sync {
    fn get_validator_by_label(&self, label: Option<&str>) -> Option<Arc<dyn ProfileValidator>>;
    fn get_validator_by_id(&self, validator_id: Option<&str>) -> Option<Arc<dyn ProfileValidator>>;
    fn get_checker(&self, constraint: &Constraint) -> Result<Arc<dyn ConstraintChecker>, String>;
    fn opa_engine_factory(&self) -> Arc<dyn OpaEngineFactory>;
}

/// Compile-time auto-registration entry for custom `ConstraintChecker` implementations via `inventory`.
pub struct RegisteredCheckerPlugin {
    pub validator_id: &'static str,
    pub constraint_type: &'static str,
    pub constraint_name: Option<&'static str>,
    pub constructor: fn() -> Arc<dyn ConstraintChecker>,
}

inventory::collect!(RegisteredCheckerPlugin);

/// Compile-time auto-registration entry for custom `ProfileValidator` implementations via `inventory`.
pub struct RegisteredValidatorPlugin {
    pub rust_type_path: &'static str,
    pub constructor: fn(&str) -> Arc<dyn ProfileValidator>,
}

inventory::collect!(RegisteredValidatorPlugin);

type ConstraintCheckerKey = (String, Option<String>);
type SharedConstraintChecker = Arc<dyn ConstraintChecker>;
type ConstraintCheckerRegistry = RwLock<HashMap<ConstraintCheckerKey, SharedConstraintChecker>>;

pub struct DefaultProfileValidator {
    id: String,
    checkers: ConstraintCheckerRegistry,
}

impl DefaultProfileValidator {
    pub fn new(id: impl Into<String>) -> Self {
        let id_str = id.into();
        let validator = Self {
            id: id_str.clone(),
            checkers: RwLock::new(HashMap::new()),
        };
        register_default_and_custom_checkers(&validator);
        // Auto-discover any checkers registered via `inventory::submit!` for this validator_id
        for plugin in inventory::iter::<RegisteredCheckerPlugin> {
            if plugin.validator_id == id_str {
                validator.register_checker(
                    plugin.constraint_type,
                    plugin.constraint_name,
                    (plugin.constructor)(),
                );
            }
        }
        validator
    }
}

impl ProfileValidator for DefaultProfileValidator {
    fn get_id(&self) -> &str {
        &self.id
    }

    fn get_checker(&self, constraint: &Constraint) -> Result<Arc<dyn ConstraintChecker>, String> {
        self.get_checker_by_name(
            constraint.constraint_type(),
            constraint.common.name.as_deref(),
        )
    }

    fn get_checker_by_name(
        &self,
        constraint_type: &str,
        constraint_name: Option<&str>,
    ) -> Result<Arc<dyn ConstraintChecker>, String> {
        let norm_name = constraint_name
            .filter(|s| !s.is_empty())
            .map(str::to_string);
        let guard = self.checkers.read().map_err(|e| e.to_string())?;
        if let Some(c) = guard.get(&(constraint_type.to_string(), norm_name.clone())) {
            return Ok(Arc::clone(c));
        }
        if norm_name.is_some() && constraint_type != "custom" {
            if let Some(c) = guard.get(&(constraint_type.to_string(), None)) {
                return Ok(Arc::clone(c));
            }
        }
        Err(format!(
            "There is no checker class for '{constraint_type}, {:?}'",
            constraint_name
        ))
    }

    fn register_checker(
        &self,
        constraint_type: &str,
        constraint_name: Option<&str>,
        checker: Arc<dyn ConstraintChecker>,
    ) {
        let norm_name = constraint_name
            .filter(|s| !s.is_empty())
            .map(str::to_string);
        if let Ok(mut guard) = self.checkers.write() {
            guard.insert((constraint_type.to_string(), norm_name), checker);
        }
    }
}

pub struct DefaultProfileValidatorFactory {
    validators: RwLock<HashMap<String, Arc<dyn ProfileValidator>>>,
    ids_by_label: RwLock<HashMap<String, String>>,
    default_validator_id: String,
    opa_engine_factory: Arc<dyn OpaEngineFactory>,
}

impl DefaultProfileValidatorFactory {
    pub fn new(
        custom_definitions: &[ProfileValidatorDefinition],
        default_validator_id: Option<String>,
        opa_engine_factory: Arc<dyn OpaEngineFactory>,
    ) -> Result<Self, String> {
        let base = Arc::new(DefaultProfileValidator::new("default"));
        let mut validators: HashMap<String, Arc<dyn ProfileValidator>> = HashMap::new();
        let mut ids_by_label: HashMap<String, String> = HashMap::new();
        validators.insert("default".to_string(), base);
        ids_by_label.insert(String::new(), "default".to_string());

        let def_id = default_validator_id.unwrap_or_else(|| "default".to_string());
        for def in custom_definitions {
            let loaded = Self::load_custom_validator(def)?;
            validators.insert(def.validator_id.clone(), loaded);
            ids_by_label.insert(def.label.clone(), def.validator_id.clone());
        }

        Ok(Self {
            validators: RwLock::new(validators),
            ids_by_label: RwLock::new(ids_by_label),
            default_validator_id: def_id,
            opa_engine_factory,
        })
    }

    fn load_custom_validator(
        def: &ProfileValidatorDefinition,
    ) -> Result<Arc<dyn ProfileValidator>, String> {
        if let Some(rust_target) = def.profile_validator_class.get("rust") {
            // 1. Check compile-time `inventory` registry first
            for plugin in inventory::iter::<RegisteredValidatorPlugin> {
                if plugin.rust_type_path == rust_target {
                    return Ok((plugin.constructor)(&def.validator_id));
                }
            }
            // 2. Check if target specifies a dynamic library path (`libloading`)
            #[cfg(feature = "dynamic-plugins")]
            if rust_target.contains('#')
                || rust_target.ends_with(".dylib")
                || rust_target.ends_with(".so")
                || rust_target.ends_with(".dll")
            {
                return Self::load_from_cdylib(rust_target, &def.validator_id);
            }
            return Err(format!(
                "Custom Rust ProfileValidator '{rust_target}' not found in inventory or dynamic libraries"
            ));
        }
        Ok(Arc::new(DefaultProfileValidator::new(&def.validator_id)))
    }

    #[cfg(feature = "dynamic-plugins")]
    fn load_from_cdylib(
        spec: &str,
        validator_id: &str,
    ) -> Result<Arc<dyn ProfileValidator>, String> {
        let (lib_path, sym_name) = spec
            .split_once('#')
            .unwrap_or((spec, "jsonprofile_create_validator"));
        let clean_path = lib_path.strip_prefix("file://").unwrap_or(lib_path);
        unsafe {
            let lib = libloading::Library::new(clean_path)
                .map_err(|e| format!("Failed to load dynamic library '{clean_path}': {e}"))?;
            type CreateValidatorFn = unsafe fn(&str) -> *mut dyn ProfileValidator;
            let func: libloading::Symbol<CreateValidatorFn> = lib
                .get(sym_name.as_bytes())
                .map_err(|e| format!("Symbol '{sym_name}' not found in '{clean_path}': {e}"))?;
            let raw = func(validator_id);
            if raw.is_null() {
                return Err(format!("Factory symbol '{sym_name}' returned null"));
            }
            std::mem::forget(lib);
            Ok(Arc::from(Box::from_raw(raw)))
        }
    }
}

impl ProfileValidatorFactory for DefaultProfileValidatorFactory {
    fn get_validator_by_label(&self, label: Option<&str>) -> Option<Arc<dyn ProfileValidator>> {
        let lbl = label.unwrap_or("");
        let id = if lbl.is_empty() {
            self.default_validator_id.clone()
        } else {
            self.ids_by_label
                .read()
                .ok()
                .and_then(|m| m.get(lbl).cloned())
                .unwrap_or_else(|| lbl.to_string())
        };
        self.get_validator_by_id(Some(&id))
    }

    fn get_validator_by_id(&self, validator_id: Option<&str>) -> Option<Arc<dyn ProfileValidator>> {
        let id = validator_id
            .filter(|s| !s.is_empty())
            .unwrap_or(&self.default_validator_id);
        self.validators.read().ok().and_then(|m| m.get(id).cloned())
    }

    fn get_checker(&self, constraint: &Constraint) -> Result<Arc<dyn ConstraintChecker>, String> {
        let validator = self
            .get_validator_by_id(constraint.common.validator.as_deref())
            .ok_or_else(|| format!("Validator not found for {:?}", constraint.common.validator))?;
        validator.get_checker(constraint)
    }

    fn opa_engine_factory(&self) -> Arc<dyn OpaEngineFactory> {
        Arc::clone(&self.opa_engine_factory)
    }
}
