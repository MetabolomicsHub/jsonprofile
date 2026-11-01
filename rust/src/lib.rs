pub mod profile;
pub mod validator;

pub use profile::base::{
    BaseCvTerm, Category, CvTerm, CvTermFieldMapping, EnforcementLevel, JsonProfileMessage,
    ValidationRuntimeConfiguration,
};
pub use profile::constraints::{
    Constraint, ConstraintCommon, ConstraintKind, Evaluation, Precondition,
};
pub use profile::model::{
    FieldRequirement, FieldRequirementGroup, JsonProfile, JsonProfileConfiguration,
    OpaFieldRequirement, ProfileValidatorDefinition, RequirementDefinition, WasmFileDefinition,
};
pub use profile::profile_validator::{validate_profile, validate_profile_file};
pub use validator::checker::{ConstraintChecker, ConstraintValidationResult};
pub use validator::context::{JsonProfileRunContext, JsonValidationResult, MessageCollector};
pub use validator::cv_term_search::{CvTermSearch, OlsCvTermSearch};
pub use validator::factory::{
    DefaultProfileValidator, DefaultProfileValidatorFactory, ProfileValidator,
    ProfileValidatorFactory, RegisteredCheckerPlugin, RegisteredValidatorPlugin,
};
pub use validator::json_validator::{JsonValidator, ProfileSource};
pub use validator::opa_engine::{
    default_opa_engine_factory, NotImplementedOpaEngine, NotImplementedOpaEngineFactory, OpaEngine,
    OpaEngineFactory,
};
#[cfg(feature = "opa")]
pub use validator::opa_engine::{LocalOpaEngine, LocalOpaEngineFactory};

#[macro_export]
macro_rules! register_constraint_checker {
    ($validator_id:expr, $constraint_type:expr, $constraint_name:expr, $constructor:expr) => {
        inventory::submit! {
            $crate::RegisteredCheckerPlugin {
                validator_id: $validator_id,
                constraint_type: $constraint_type,
                constraint_name: $constraint_name,
                constructor: $constructor,
            }
        }
    };
}

#[macro_export]
macro_rules! register_profile_validator {
    ($rust_type_path:expr, $constructor:expr) => {
        inventory::submit! {
            $crate::RegisteredValidatorPlugin {
                rust_type_path: $rust_type_path,
                constructor: $constructor,
            }
        }
    };
}
