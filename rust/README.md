# `jsonprofile` for Rust

Rust implementation of `jsonprofile` for validating JSON documents against JSON Schemas and domain-specific JSON Profiles.

---

## Installation & Cargo Features

Add `jsonprofile` to your `Cargo.toml`:

```toml
[dependencies]
jsonprofile = { path = "../rust" }

# Optional feature flags:
# - "dynamic-plugins" (enabled by default): runtime shared library loading via `libloading`
# - "opa": optional OPA policy extension flag
```

---

## Core API Overview

| Struct / Trait / Function | Module | Description |
| :--- | :--- | :--- |
| `JsonValidator` | `jsonprofile` | Main validator engine (`JsonValidator::new`, `JsonValidator::with_referenced_profiles`, `JsonValidator::with_options`). |
| `ProfileSource` | `jsonprofile` | Input source wrapper accepting `&Path`, `PathBuf`, `&str`, `String`, `serde_json::Value`, or `JsonProfile`. |
| `JsonProfile` | `jsonprofile` | Strongly-typed Serde model representing a JSON Profile (`id`, `version`, `name`, `extends`, `configuration`, `requirements`). |
| `ValidationRuntimeConfiguration` | `jsonprofile` | Per-run settings (`offline_mode`, `skip_jsonschema_validation`, `skip_profile_validation`, `skip_decimal_validations`, `skipped_requirements`, `max_messages_for_each_requirement`). |
| `JsonValidationResult` | `jsonprofile` | Validation result containing `errors`, `recommendations`, and `optionals` (`BTreeMap<String, Vec<JsonProfileMessage>>`). |
| `validate_profile_file` | `jsonprofile` | Validates a profile JSON file and returns `(bool, Vec<JsonProfileMessage>)`. |

---

## Usage Examples

### 1. Basic Usage (Validating a JSON File or `serde_json::Value`)

```rust
use jsonprofile::{JsonValidator, ProfileSource, ValidationRuntimeConfiguration};
use std::path::Path;

fn main() -> Result<(), String> {
    let schema_path = Path::new("../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json");
    let profile_path = Path::new("../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json");
    let input_path = Path::new("../shared/resources/mztab-m/manual_null_MTBLS263.mztab.json");

    let validator = JsonValidator::new(
        Some(ProfileSource::from(schema_path)),
        profile_path,
    )?;

    let runtime_config = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        max_messages_for_each_requirement: Some(10),
        ..Default::default()
    };

    let result = validator.validate_json_file(input_path, Some(runtime_config))?;

    for (json_path, messages) in &result.errors {
        for msg in messages {
            println!("[REQUIRED] {} ({}): {}", msg.code, json_path, msg.message);
        }
    }
    Ok(())
}
```

### 2. Profile Inheritance (`extends` & `with_referenced_profiles`)

When validating a profile that extends one or more parent profiles via `"extends"`, pass a `HashMap<String, ProfileSource>` mapping each parent profile's `id` to its `ProfileSource`:

```rust
use jsonprofile::{JsonValidator, ProfileSource, ValidationRuntimeConfiguration};
use std::collections::HashMap;
use std::path::Path;

fn main() -> Result<(), String> {
    let default_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";
    let mtbls_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json";

    let mut referenced_profiles = HashMap::new();
    referenced_profiles.insert(
        default_id.to_string(),
        ProfileSource::from(Path::new("../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json")),
    );
    referenced_profiles.insert(
        mtbls_id.to_string(),
        ProfileSource::from(Path::new("../shared/resources/mztab-m/profiles/mztabm-metabolights-profile-2.1.0-M.json")),
    );

    let validator = JsonValidator::with_referenced_profiles(
        Some(ProfileSource::from(Path::new("../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json"))),
        Path::new("../shared/resources/mztab-m/profiles/mztabm-metabolights-profile-2.1.0-M.json"),
        referenced_profiles,
    )?;

    let rt = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        ..Default::default()
    };

    let res = validator.validate_json_file(
        "../shared/resources/mztab-m/manual_null_MTBLS263.mztab.json",
        Some(rt),
    )?;
    println!("Errors count: {}", res.errors.len());
    Ok(())
}
```

---

## OPA Extension Usage

OPA policy evaluation in Rust is supported through the [`OpaEngine`](src/validator/opa_engine.rs) and [`OpaEngineFactory`](src/validator/opa_engine.rs) traits:

- **Built-in `LocalOpaEngineFactory` & `LocalOpaEngine` (`opa` feature, enabled by default)**: Automatically extracts `policy.wasm` and `/data.json` from `.wasm` / `.tar.gz` OPA bundles and executes compiled Rego policies natively via `wasmi` using the OPA WASM ABI v1.2 (including built-ins `sprintf`, `time.now_ns`, `time.format`, `time.parse_ns`, `regex.find_n`, `regex.replace`, `json.match_schema`). When the `opa` feature is disabled, `JsonValidator` falls back to `NotImplementedOpaEngineFactory`.
- **Custom `OpaEngineFactory`**: You can also implement `OpaEngine` and `OpaEngineFactory` and pass your custom factory to `JsonValidator::with_options`:

```rust
use jsonprofile::{
    JsonValidator, OpaEngine, OpaEngineFactory, ValidationRuntimeConfiguration,
};
use serde_json::{json, Value};
use std::collections::HashMap;
use std::sync::Arc;

struct MyOpaEngine;

impl OpaEngine for MyOpaEngine {
    fn evaluate(
        &self,
        _input_data: &Value,
        _entrypoint: Option<&str>,
        _data: Option<&Value>,
    ) -> Result<Value, String> {
        Ok(json!([
            {
                "messages": [
                    {
                        "policy_id": "policy_0001",
                        "source": "$.item",
                        "message": "OPA policy failed"
                    }
                ]
            }
        ]))
    }
}

struct MyOpaEngineFactory;

impl OpaEngineFactory for MyOpaEngineFactory {
    fn get_opa_engine(
        &self,
        _wasm_file_path: &str,
        _wasm_file_download_url: Option<&str>,
    ) -> Result<Arc<dyn OpaEngine>, String> {
        Ok(Arc::new(MyOpaEngine))
    }
}

fn main() -> Result<(), String> {
    let profile = json!({
        "id": "https://example.org/profiles/opa-test.json",
        "version": "1.0.0",
        "name": "OPA Test Profile",
        "configuration": {
            "wasm_file_definitions": {
                "default": {
                    "wasm_file_path": "policy.wasm",
                    "wasm_file_download_url": "https://example.org/policy.wasm",
                    "entrypoint": "test/policies"
                }
            }
        },
        "requirements": {
            "$.item": {
                "code": "OPA-0001",
                "policy_id": "policy_0001",
                "enforcement_level": "required"
            }
        }
    });

    let validator = JsonValidator::with_options(
        None,
        profile,
        HashMap::new(),
        None,
        Some(Arc::new(MyOpaEngineFactory)),
    )?;

    let rt = ValidationRuntimeConfiguration {
        skip_jsonschema_validation: Some(true),
        ..Default::default()
    };
    let result = validator.validate_dict(&json!({"item": "bad"}), Some(rt));
    assert!(result.errors.contains_key("$.item"));
    Ok(())
}
```

---

## Custom Validators & Constraint Checkers

Rust provides two zero-boilerplate mechanisms for discovering custom validators referenced in `configuration.custom_validator_definitions` under `"rust"`:

### 1. Compile-Time Auto-Discovery (`inventory` Macros)

Use `register_constraint_checker!` and `register_profile_validator!` anywhere in your crate (analogous to Python's `@constraint_checker` and `@profile_validator` decorators):

```rust
use jsonprofile::{
    register_constraint_checker, register_profile_validator, Constraint,
    ConstraintChecker, DefaultProfileValidator, JsonProfileRunContext,
};
use serde_json::Value;
use std::sync::Arc;

struct EvenLenChecker;

impl ConstraintChecker for EvenLenChecker {
    fn validate(
        &self,
        _constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let s = value.as_str().unwrap_or("");
        if s.len() % 2 == 0 {
            (true, Some("even length".to_string()))
        } else {
            (false, Some("odd length".to_string()))
        }
    }
}

register_constraint_checker!("custom-rust-val", "custom", Some("even_len"), || {
    Arc::new(EvenLenChecker)
});

register_profile_validator!("my_crate::CustomRustValidator", |id| {
    Arc::new(DefaultProfileValidator::new(id))
});
```

### 2. Runtime Dynamic Shared Library Loading (`libloading`)

With the `dynamic-plugins` feature enabled (default), if `"rust"` in `profile_validator_class` points to a shared library (`.dylib`, `.so`, `.dll`) with an optional `#symbol_name` suffix (defaulting to `jsonprofile_create_validator`), `DefaultProfileValidatorFactory` loads the validator dynamically at runtime.

---

## Development & Testing

```sh
# From repository root:
just format-rust
just lint-rust
just test-rust
just build-rust
```
