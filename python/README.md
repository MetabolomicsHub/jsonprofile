# `jsonprofile` for Python

Python implementation of `jsonprofile` for validating JSON documents against JSON Schemas and domain-specific JSON Profiles.

Requires **Python 3.10 or later** (tested on Python 3.10 through 3.14).

---

## Installation

### Standard Installation

```sh
pip install jsonprofile
# or with uv:
uv add jsonprofile
```

### With Optional OPA WASM Extension

Local Open Policy Agent (OPA) WASM policy evaluation is provided as an optional extra (`jsonprofile[opa]`):

```sh
pip install "jsonprofile[opa]"
# or in a development workspace:
uv sync --extra opa --group dev
```

---

## Core API Overview

| Class / Function | Module | Description |
| :--- | :--- | :--- |
| `JsonValidator` | `jsonprofile.validator` | Main validator engine. Accepts a JSON Schema (`str`, `Path`, `dict`, or `None`), a target profile (`str`, `Path`, `dict`, or `JsonProfile`), and optional `referenced_profiles`, `default_cv_term_search`, and `opa_engine_factory`. |
| `JsonProfile` | `jsonprofile.profile.model` | Pydantic model representing a JSON Profile (`id`, `version`, `name`, `extends`, `configuration`, `requirements`). |
| `ValidationRuntimeConfiguration` | `jsonprofile.profile.base` | Per-run settings (`offline_mode`, `skip_jsonschema_validation`, `skip_profile_validation`, `skip_decimal_validations`, `skipped_requirements`, `max_messages_for_each_requirement`). |
| `JsonValidationResult` | `jsonprofile.validator.context` | Validation output containing `errors`, `recommendations`, and `optionals` (each `dict[str, list[JsonProfileMessage]]` keyed by JSONPath). |
| `validate_profile_file` | `jsonprofile.profile.profile_validator` | Validates a profile JSON file against `jsonprofile.schema.json` and checks structural rules (unique requirement codes, valid JSONPath expressions). |

---

## Usage Examples

### 1. Basic Usage (Validating a JSON File or Dictionary)

```python
from pathlib import Path
from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.validator import JsonValidator

validator = JsonValidator(
    json_schema=Path("../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json"),
    profile=Path(
        "../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json"
    ),
)

runtime_config = ValidationRuntimeConfiguration(
    offline_mode=True,
    skip_jsonschema_validation=False,
    max_messages_for_each_requirement=10,
)

result = validator.validate_json_file(
    json_file_path="../shared/resources/mztab-m/manual_null_MTBLS263.mztab.json",
    runtime_config=runtime_config,
)

for json_path, messages in result.errors.items():
    for msg in messages:
        print(f"[REQUIRED] {msg.code} ({json_path}): {msg.message}")

for json_path, messages in result.recommendations.items():
    for msg in messages:
        print(f"[RECOMMENDED] {msg.code} ({json_path}): {msg.message}")
```

### 2. Profile Inheritance (`extends` & `referenced_profiles`)

When a profile extends one or more parent profiles via `"extends"`, pass the parent profiles (by ID -> file path, `dict`, or `JsonProfile`) in `referenced_profiles`:

```python
from jsonprofile.validator import JsonValidator

DEFAULT_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json"
MTBLS_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json"

referenced_profiles = {
    DEFAULT_ID: "../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json",
    MTBLS_ID: "../shared/resources/mztab-m/profiles/mztabm-metabolights-profile-2.1.0-M.json",
}

validator = JsonValidator(
    json_schema="../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json",
    profile=referenced_profiles[MTBLS_ID],
    referenced_profiles=referenced_profiles,
)
```

---

## OPA Extension Usage

`jsonprofile` supports evaluating compiled OPA WASM bundles (`.wasm` or `.tar.gz` bundles containing `policy.wasm` and `data.json`) in two ways:
1. **`OpaFieldRequirement`**: Defined directly under `requirements` with a `policy_id` and optional `wasm_file_key`.
2. **`OpaPolicyConstraint` (`type: "opa-policy"`)**: Defined inside a `FieldRequirement`'s `value_constraint`.

### Configuring OPA in a Profile

```json
{
  "id": "https://example.org/profiles/opa-profile.json",
  "version": "1.0.0",
  "name": "OPA-Enabled Profile",
  "configuration": {
    "default_wasm_file_key": "default",
    "wasm_file_definitions": {
      "default": {
        "wasm_file_path": "../shared/resources/mztab-m/mztabm-default-2.1.0-M.wasm",
        "wasm_file_download_url": "https://github.com/HUPO-PSI/mzTab-M/raw/refs/heads/development/mztab_m_io/resources/mztabm-default-2.1.0-M.wasm",
        "entrypoint": "mztabm/policies"
      }
    }
  },
  "requirements": {
    "$": {
      "code": "D-0010",
      "enforcement_level": "required",
      "policy_id": "policy_d_0010"
    }
  }
}
```

### Running with `LocalOpaEngineFactory` or Custom `OpaEngineFactory`

When `jsonprofile[opa]` (`opa-wasmtime`) is installed, `JsonValidator` automatically uses `LocalOpaEngineFactory`. You can also instantiate `LocalOpaEngine` directly or inject a custom `OpaEngineFactory`:

```python
from typing import Optional
from jsonprofile.validator import JsonValidator
from jsonprofile.validator.opa_engine import OpaEngine, OpaEngineFactory


class CustomOpaEngine(OpaEngine):
    def evaluate(
        self,
        input_data: dict,
        entrypoint: Optional[str] = None,
        data: Optional[dict] = None,
    ) -> dict:
        # Custom or remote OPA evaluation returning {"messages": [...]}
        return {"messages": []}


class CustomOpaEngineFactory(OpaEngineFactory):
    def get_opa_engine(
        self,
        wasm_file_path: str,
        wasm_file_download_url: Optional[str] = None,
    ) -> OpaEngine:
        return CustomOpaEngine()


validator = JsonValidator(
    json_schema=None,
    profile="opa-profile.json",
    opa_engine_factory=CustomOpaEngineFactory(),
)
```

---

## Custom Validators & Constraint Checkers

You can register custom constraint checkers using `@profile_validator` and `@constraint_checker`:

```python
from typing import Any, Optional, Tuple
from jsonprofile.profile.constraints import CustomConstraint
from jsonprofile.validator.abstract_checker import ConstraintChecker
from jsonprofile.validator.context import JsonProfileRunContext
from jsonprofile.validator.default_validator import (
    DefaultProfileValidator,
    constraint_checker,
    profile_validator,
)


@profile_validator(label="my_domain", validator_id="my-domain-validator")
class MyDomainValidator(DefaultProfileValidator):
    pass


@constraint_checker(
    CustomConstraint,
    constraint_name="accession_prefix",
    validator_id="my-domain-validator",
)
class AccessionPrefixChecker(ConstraintChecker):
    def validate(
        self,
        constraint: CustomConstraint,
        value: Any,
        root: dict[str, Any],
        context: JsonProfileRunContext,
    ) -> Tuple[bool, Optional[str]]:
        ok = isinstance(value, str) and value.startswith("ACC-")
        return ok, self.format_message(constraint, "Must start with ACC-")
```

---

## Development & Testing

```sh
# From repository root:
just format-python
just lint-python
just test-python
just build-python

# Or from python/ directory:
uv sync --extra opa --group dev
PYTHONPATH=. uv run pytest
```
