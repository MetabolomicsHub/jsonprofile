# `jsonprofile` Shared Specification

`jsonprofile` is a declarative, language-neutral validation layer on top of **JSON Schema**. It enables domain communities (such as metabolomics, proteomics, and multi-omics repositories like MetaboLights, ISA-JSON, and HUPO-PSI mzTab-M) to enforce context-specific profiles, controlled vocabulary (CV) rules, cross-field preconditions, object reference resolution, and Open Policy Agent (OPA) WASM rules on JSON documents that share a common base JSON Schema.

---

## 1. Core Concepts & Data Model

A `JsonProfile` document (validated against [`shared/schema/jsonprofile.schema.json`](../schema/jsonprofile.schema.json)) consists of:

| Field | Type | Required | Description |
| :--- | :--- | :---: | :--- |
| `id` | `string` | Yes | Unique URI or identifier of the profile. |
| `version` | `string` | Yes | Semantic version of the profile (e.g., `"1.0.0"`, `"2.1.0-M"`). |
| `name` | `string` | Yes | Human-readable profile name. |
| `description` | `string` | No | Detailed description of the profile's purpose. |
| `extends` | `string` | No | `id` of a parent `JsonProfile` from which this profile inherits configuration and requirements. |
| `configuration` | `JsonProfileConfiguration` | No | Profile-level settings for CV term mapping, unique object ID indexing, custom validators, and OPA WASM bundles. |
| `requirements` | `Map<JsonPath, Requirement>` | No | Mapping from a target JSONPath expression (e.g. `"$"`, `"$.metadata.contact[*]"`) to a `FieldRequirement`, `FieldRequirementGroup`, `OpaFieldRequirement`, or `null` (to delete an inherited requirement). |

### 1.1 Profile Inheritance (`extends`)

A profile may declare `"extends": "<parent-profile-id>"`. Inheritance is resolved recursively (supporting multi-level chains such as `base-profile` -> `legacy-profile` -> `ms-profile`):
1. **Configuration merging**: Unset (`null`) fields in the child's `configuration` inherit their values from the parent profile's `configuration`.
2. **Requirement overriding & deletion**:
   - Any JSONPath key present in the parent's `requirements` is inherited by default.
   - If the child defines a non-null requirement at the same JSONPath key, it overrides the parent's requirement at that path.
   - If the child maps a JSONPath key to `null` (e.g. `"$.metadata.instrument": null`), that requirement is removed from the effective profile.

### 1.2 Profile Configuration (`JsonProfileConfiguration`)

- `cv_term_field_mapping`: Maps CV term object properties to canonical slots (`label_field` default `"cv_label"`, `accession_field` default `"cv_accession"`, `name_field` default `"name"`, `value_field` default `"value"`). For example, ISA-JSON profiles map `termSource`, `termAccession`, and `annotationValue`.
- `object_unique_id_fields`: List of property names (e.g. `["@id"]`) used to pre-index objects across the JSON document so that constraints with `"is_reference_field": true` can dereference ID strings into target objects before validation.
- `custom_validator_definitions`: List of custom validator registrations with per-language class/type mappings (`{"python": "...", "java": "...", "rust": "..."}`).
- `wasm_file_definitions` & `default_wasm_file_key`: Named OPA bundle/WASM definitions (`wasm_file_path`, `wasm_file_download_url`, `entrypoint`).

---

## 2. Requirements & Enforcement Levels

Every requirement has an `enforcement_level`:
- `"required"` (default): Violations are reported in `result.errors`.
- `"recommended"`: Violations are reported in `result.recommendations`.
- `"optional"`: Violations are reported in `result.optionals`.

### 2.1 Requirement Types

1. **`FieldRequirement`**:
   - `code`: Unique requirement identifier (e.g. `"M-00305"`).
   - `match_is_required`: If `true`, emits a message when the target JSONPath matches zero nodes in the input document.
   - `required_properties`: List of keys that must exist on every matched JSON object (emits `REQUIRED` violations).
   - `recommended_properties`: List of keys recommended on every matched JSON object (emits `RECOMMENDED` warnings directly to the main collector).
   - `value_constraint`: A polymorphic `Constraint` evaluated against each matched node.
2. **`FieldRequirementGroup`**:
   - Groups a `requirements` list at a single JSONPath.
   - Evaluates all child requirements and groups `REQUIRED` failures per matched instance path.
   - Supports `min_valid` and `max_valid` bounds: if the number of passing child requirements for an instance falls within `[min_valid, max_valid]`, the group is satisfied for that instance.
3. **`OpaFieldRequirement`**:
   - Evaluates a compiled OPA Rego policy (`policy_id`, optional `wasm_file_key`) against `{policy_ids, value, root, config, constraint}`.

---

## 3. Preconditions & Built-in Constraints

### 3.1 Preconditions (`Precondition` & `Evaluation`)

Any constraint may specify a `precondition` with one or more `evaluations`, combined using `join_operator` (`"and"` or `"or"` with `min_valid`/`max_valid`):
- Each `Evaluation` selects a target node relative to the current value (or relative to the document root when `"root_value_evaluation": true`), optionally dereferences it (`"is_reference_field": true`), and checks an inner `constraint`.
- If the precondition evaluates to `false`, the outer constraint is skipped and treated as satisfied (`is_valid = true`).

### 3.2 Built-in Constraint Types (21 Types)

| `type` Discriminator | Model Name | Key Properties |
| :--- | :--- | :--- |
| `"not-null"` | `NotNullConstraint` | `case_sensitive`, `null_values` |
| `"regex"` | `RegexConstraint` | `pattern`, `case_sensitive` (full-string match) |
| `"string-length"` | `StringConstraint` | `minimum`, `maximum` |
| `"string-enum"` | `StringEnumConstraint` | `options` (list or object keys) |
| `"integer-enum"` | `IntegerEnumConstraint` | `options` (list or map of integer keys) |
| `"integer"` | `IntegerConstraint` | `minimum`, `maximum` |
| `"positive-integer"` | `PositiveIntegerConstraint` | `minimum` (defaults to `1`), `maximum` |
| `"non-negative-integer"` | `NonNegativeIntegerConstraint` | `minimum` (defaults to `0`), `maximum` |
| `"boolean"` | `BooleanConstraint` | `exceptional_true_values`, `exceptional_false_values` |
| `"decimal"` | `DecimalConstraint` | `minimum`, `maximum`, `min_scale`, `max_scale`, `allow_scientific_notation`, `allow_non_finite_values` |
| `"time"` | `DateTimeConstraint` | `format` (`strftime` pattern or ISO-8601 when omitted) |
| `"email"` | `EmailConstraint` | `check_deliverability` |
| `"uri"` | `UriConstraint` | `allowed_schemes` |
| `"items-count"` | `CollectionConstraint` | `min_occurs`, `max_occurs`, `item_value_jsonpath_list`, `item_value_match_constraint`, `min_match`, `max_match`, `min_referenced_value_match`, `max_referenced_value_match` |
| `"cv-term"` | `CVTermConstraint` | `allow_user_defined_terms`, `is_cv_term_value_required`, `allow_synonym`, `exceptional_cv_list` |
| `"allowed-cv-list"` | `CVListConstraint` | `allowed_cv_list`, `exceptional_cv_list`, `is_cv_term_value_required` |
| `"cv-term-enum"` | `CVTermEnumConstraint` | `allowed_cv_terms`, `exceptional_cv_list`, `is_cv_term_value_required` |
| `"parent-cv-term"` | `ParentCVTermConstraint` | `parent_cv_terms`, `excluded_cv_terms`, `allow_synonym` |
| `"cv-term-value"` | `CVTermValueConstraint` | `key_cv_term`, `value_constraint` |
| `"group"` | `ConstraintGroup` | `constraints`, `join_operator` (`"and"` / `"or"`), `min_valid`, `max_valid` |
| `"opa-policy"` | `OpaPolicyConstraint` | `policy_id`, `wasm_file_key` |
| `"custom"` | `CustomConstraint` | `name`, `validator`, `default_arguments`, `value_jsonpath_arguments`, `root_jsonpath_arguments` |

All constraints also share common modifiers: `json_path`, `is_reference_field`, `precondition`, `default_precondition_evaluation`, `negated`, `null_values`, and `exceptional_values`.

---

## 4. Example Profile & Multi-Language Usage Snippets

### Example Profile (`profile.json`)

```json
{
  "id": "https://example.org/profiles/study-profile-1.0.json",
  "version": "1.0.0",
  "name": "Study Submission Profile",
  "configuration": {
    "object_unique_id_fields": ["@id"]
  },
  "requirements": {
    "$.study": {
      "code": "STUDY-001",
      "enforcement_level": "required",
      "match_is_required": true,
      "required_properties": ["identifier", "title"],
      "recommended_properties": ["description"]
    },
    "$.study.identifier": {
      "code": "STUDY-002",
      "enforcement_level": "required",
      "value_constraint": {
        "type": "regex",
        "pattern": "^MTBLS[0-9]+$"
      }
    },
    "$.study.title": {
      "code": "STUDY-003",
      "enforcement_level": "required",
      "value_constraint": {
        "type": "string-length",
        "minimum": 10
      }
    }
  }
}
```

### Python Example

```python
from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.validator import JsonValidator

validator = JsonValidator(
    json_schema="schema.json",
    profile="profile.json",
)

runtime_config = ValidationRuntimeConfiguration(offline_mode=True)
result = validator.validate_json_file("input.json", runtime_config=runtime_config)

for path, messages in result.errors.items():
    for msg in messages:
        print(f"[ERROR] {msg.code} at {path}: {msg.message}")
```

### Java 21 Example

```java
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) {
        JsonValidator validator = new JsonValidator(
                Path.of("schema.json"),
                Path.of("profile.json"));

        ValidationRuntimeConfiguration runtimeConfig = new ValidationRuntimeConfiguration();
        runtimeConfig.setOfflineMode(true);

        JsonValidationResult result = validator.validateJsonFile(Path.of("input.json"), runtimeConfig);
        for (Map.Entry<String, List<JsonProfileMessage>> entry : result.getErrors().entrySet()) {
            for (JsonProfileMessage msg : entry.getValue()) {
                System.out.printf("[ERROR] %s at %s: %s%n", msg.getCode(), entry.getKey(), msg.getMessage());
            }
        }
    }
}
```

### Rust Example

```rust
use jsonprofile::{JsonValidator, ProfileSource, ValidationRuntimeConfiguration};
use std::path::Path;

fn main() -> Result<(), String> {
    let validator = JsonValidator::new(
        Some(ProfileSource::from(Path::new("schema.json"))),
        Path::new("profile.json"),
    )?;

    let runtime_config = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        ..Default::default()
    };

    let result = validator.validate_json_file("input.json", Some(runtime_config))?;
    for (path, messages) in &result.errors {
        for msg in messages {
            println!("[ERROR] {} at {}: {}", msg.code, path, msg.message);
        }
    }
    Ok(())
}
```

---

## 5. Shared Resources & Conformance Suites

- **Schema**: [`shared/schema/jsonprofile.schema.json`](../schema/jsonprofile.schema.json)
- **Conformance Suites**:
  - [`shared/conformance/smoke.json`](../conformance/smoke.json): Profile validity smoke cases.
  - [`shared/conformance/validation_suite.json`](../conformance/validation_suite.json): Cross-language validation conformance test cases verified identically across Python, Java 21, and Rust.
- **Domain Profiles & Test Datasets**:
  - [`shared/resources/isa-json/`](../resources/isa-json/): MetaboLights ISA-JSON profiles (`profiles/metabolights-profile-0.1.0.json`, `profiles/metabolights-bh-extended-profile-0.1.0.json`) and test investigation input (`metabolights-input-isa.json`).
  - [`shared/resources/mztab-m/`](../resources/mztab-m/): HUPO-PSI mzTab-M 2.1.0-M JSON Schema, OPA WASM bundle, 5 hierarchical profiles (`default`, `mtd_smf`, `mtd_sml`, `full`, `metabolights`), and test dataset (`manual_null_MTBLS263.mztab.json`).
