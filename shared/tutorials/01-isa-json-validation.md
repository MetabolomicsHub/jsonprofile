# Tutorial 1: Validating an ISA-JSON Dataset Using Different Profiles

In this tutorial, you will validate an **ISA-JSON** metabolomics investigation document ([`shared/resources/isa-json/metabolights-input-isa.json`](../resources/isa-json/metabolights-input-isa.json)) against two profiles in **Python**, **Java 21**, and **Rust**:

1. **Base MetaboLights Profile** ([`metabolights-profile-0.1.0.json`](../resources/isa-json/profiles/metabolights-profile-0.1.0.json)) — Enforces strict MetaboLights submission conventions (`MTBLS`/`REQ`/`MHD` study identifiers, minimum 150-character study descriptions, minimum 3 study design descriptors, 40-character protocol descriptions, `@id` cross-reference resolution, and ontology constraints).
2. **Extended BH Study Profile** ([`metabolights-bh-extended-profile-0.1.0.json`](../resources/isa-json/profiles/metabolights-bh-extended-profile-0.1.0.json)) — Uses `"extends"` to inherit from the base MetaboLights profile while relaxing study identifier/filename patterns for external `BH`-prefixed studies (`BH2024`), reducing the minimum study design descriptor count from `3` to `2`, and deleting the protocol description length rule via `null`.

All 3 language implementations (**Python**, **Java 21**, and **Rust**) are guaranteed to return the **exact same `errors`, `recommendations`, and `optionals`** for every JSONPath and requirement code.

---

## Step 1: Installation & Environment Setup

Run all commands from the repository root (`/path/to/jsonprofile`):

### 1.1 Python (`>= 3.10`)
```sh
cd python
uv sync --extra opa --group dev
cd ..
```

### 1.2 Java (`JDK 21+` & `Gradle 8.7+`)
```sh
gradle --project-cache-dir .cache/gradle/project-cache -p java testClasses
```

### 1.3 Rust (`Cargo 1.75+`)
```sh
cargo build --manifest-path rust/Cargo.toml --tests
```

---

## Step 2: Understand the ISA-JSON Profiles

### 2.1 Base Profile (`metabolights-profile-0.1.0.json`)
ISA-JSON documents represent ontology terms using `termSource`, `termAccession`, and `annotationValue`, and link entities (such as protocols, characteristics, and parameters) via `@id` references (e.g., `"#ontology_annotation/..."`).

The base profile configures this in its `"configuration"` block:

```json
{
  "id": "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json",
  "version": "0.1.0",
  "name": "MetaboLights ISA-JSON Base Profile",
  "configuration": {
    "object_unique_id_fields": ["@id"],
    "cv_term_field_mapping": {
      "label_field": "termSource",
      "accession_field": "termAccession",
      "name_field": "annotationValue",
      "value_field": "value"
    }
  }
}
```

Key requirements in `metabolights-profile-0.1.0.json`:
- **`M-00304` (`$.studies[0].identifier`, `required`)**: Regex `^(MTBLS|REQ|MHD)\d$`
- **`M-00306` (`$.studies[0].description`, `required`)**: Minimum string length `150`
- **`M-00307` (`$.studies[0].filename`, `required`)**: Regex `^s_(MTBLS|REQ|MHD)\d\.txt$`
- **`M-000311` (`$.studies[0].studyDesignDescriptors`, `required`)**: `items-count` with `min_occurs: 3`
- **`M-000318` (`$.studies[0].characteristicCategories[*].characteristicType`, `required`)**: Preconditioned `cv-term` check when `annotationValue == "cell type"`
- **`M-000323` (`$.studies[0].materials.sources[*]`, `required`)**: `items-count` resolving `$.characteristics[*].category["@id"][*].characteristicType` via `@id` reference lookup
- **`M-000401..M-000404` (`$.studies[0].protocols[*]`, `required`)**: `FieldRequirementGroup` verifying required properties (`name`, `description`, `protocolType`) and that Mass Spectrometry protocols (`CHMO_0000470`) include `"Instrument"`, `"Scan polarity"`, and `"Scan m/z range"` parameters in `.parameters[*]`
- **`M-000453` (`$.studies[0].protocols[*].description`, `required`)**: Minimum string length `40`
- **`M-000453` (`$.studies[0].protocols[*].protocolType`, `recommended`)**: `cv-term-enum` recommending standard ontology terms for protocol types
- **`M-000501` (`$.studies[0].assays[*].measurementType`, `required`)**: `cv-term-enum` check against allowed MS measurement types

### 2.2 Extended Profile (`metabolights-bh-extended-profile-0.1.0.json`)
Our input file `metabolights-input-isa.json` has:
- `"identifier": "BH2024"`
- `"filename": "s_BH2024-study.txt"`
- `2` items in `studyDesignDescriptors`
- Short protocol descriptions

Rather than duplicating the 600+ lines of `metabolights-profile-0.1.0.json`, the child profile [`metabolights-bh-extended-profile-0.1.0.json`](../resources/isa-json/profiles/metabolights-bh-extended-profile-0.1.0.json) extends the base profile:

```json
{
  "id": "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json",
  "extends": "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json",
  "version": "0.1.0",
  "name": "MetaboLights BH Extended ISA-JSON Profile",
  "requirements": {
    "$.studies[0].identifier": {
      "code": "BH-00304",
      "enforcement_level": "required",
      "value_constraint": {
        "type": "regex",
        "pattern": "^(MTBLS|REQ|MHD|BH)\\d+$"
      }
    },
    "$.studies[0].filename": {
      "code": "BH-00307",
      "enforcement_level": "required",
      "value_constraint": {
        "type": "regex",
        "pattern": "^s_(MTBLS|REQ|MHD|BH)\\d+(-study)?\\.txt$"
      }
    },
    "$.studies[0].studyDesignDescriptors": {
      "code": "BH-000311",
      "enforcement_level": "required",
      "value_constraint": {
        "type": "items-count",
        "min_occurs": 2
      }
    },
    "$.studies[0].protocols[*].description": null
  }
}
```

---

## Step 3: Implement Validation in 3 Languages

### 3.1 Python Implementation

See [`python/tests/test_isa_json_profile.py`](../../python/tests/test_isa_json_profile.py):

```python
import pathlib
import orjson
from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.validator import JsonValidator

BASE_ID = "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json"
EXTENDED_ID = "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json"

referenced_profiles = {
    BASE_ID: "../shared/resources/isa-json/profiles/metabolights-profile-0.1.0.json",
    EXTENDED_ID: "../shared/resources/isa-json/profiles/metabolights-bh-extended-profile-0.1.0.json",
}

input_data = orjson.loads(
    pathlib.Path("../shared/resources/isa-json/metabolights-input-isa.json").read_bytes()
)
runtime_config = ValidationRuntimeConfiguration(
    offline_mode=True,
    skip_jsonschema_validation=True,
)

for profile_id, profile_path in referenced_profiles.items():
    validator = JsonValidator(
        json_schema=None,
        profile=profile_path,
        referenced_profiles=referenced_profiles,
    )
    result = validator.validate_dict(input_data, runtime_config=runtime_config)
    print(f"=== Profile: {validator.json_profile.name} ({profile_id}) ===")
    print(f"Errors ({len(result.errors)} paths):")
    for path, msgs in sorted(result.errors.items()):
        for msg in msgs:
            print(f"  [REQUIRED]    {msg.code}\t{path}\t{msg.message}")
    print(f"Recommendations ({len(result.recommendations)} paths):")
    for path, msgs in sorted(result.recommendations.items()):
        for msg in msgs:
            print(f"  [RECOMMENDED] {msg.code}\t{path}\t{msg.message}")
    print(f"Optionals ({len(result.optionals)} paths):")
    for path, msgs in sorted(result.optionals.items()):
        for msg in msgs:
            print(f"  [OPTIONAL]    {msg.code}\t{path}\t{msg.message}")
```

### 3.2 Java 21 Implementation

See [`java/src/test/java/org/metabolomicshub/jsonprofile/ConformanceSmokeTest.java`](../../java/src/test/java/org/metabolomicshub/jsonprofile/ConformanceSmokeTest.java):

```java
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;

public class IsaJsonTutorial {
    public static void main(String[] args) {
        Path isaDir = Path.of("../shared/resources/isa-json");
        Path baseProfile = isaDir.resolve("profiles/metabolights-profile-0.1.0.json");
        Path extendedProfile = isaDir.resolve("profiles/metabolights-bh-extended-profile-0.1.0.json");
        Path inputJson = isaDir.resolve("metabolights-input-isa.json");

        String baseId = "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json";
        String extendedId = "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json";

        Map<String, Object> referencedProfiles = Map.of(
                baseId, baseProfile,
                extendedId, extendedProfile);

        ValidationRuntimeConfiguration runtimeConfig = new ValidationRuntimeConfiguration();
        runtimeConfig.setOfflineMode(true);
        runtimeConfig.setSkipJsonschemaValidation(true);

        for (Path profilePath : List.of(baseProfile, extendedProfile)) {
            JsonValidator validator = new JsonValidator(null, profilePath, referencedProfiles);
            JsonValidationResult result = validator.validateJsonFile(inputJson, runtimeConfig);

            System.out.printf("=== Profile: %s (%s) ===%n",
                    validator.getJsonProfile().getName(), validator.getJsonProfile().getId());
            System.out.printf("Errors (%d paths):%n", result.getErrors().size());
            for (var entry : new TreeMap<>(result.getErrors()).entrySet()) {
                for (JsonProfileMessage msg : entry.getValue()) {
                    System.out.printf("  [REQUIRED]    %s\t%s\t%s%n",
                            msg.getCode(), entry.getKey(), msg.getMessage());
                }
            }
            System.out.printf("Recommendations (%d paths):%n", result.getRecommendations().size());
            for (var entry : new TreeMap<>(result.getRecommendations()).entrySet()) {
                for (JsonProfileMessage msg : entry.getValue()) {
                    System.out.printf("  [RECOMMENDED] %s\t%s\t%s%n",
                            msg.getCode(), entry.getKey(), msg.getMessage());
                }
            }
            System.out.printf("Optionals (%d paths):%n", result.getOptionals().size());
            for (var entry : new TreeMap<>(result.getOptionals()).entrySet()) {
                for (JsonProfileMessage msg : entry.getValue()) {
                    System.out.printf("  [OPTIONAL]    %s\t%s\t%s%n",
                            msg.getCode(), entry.getKey(), msg.getMessage());
                }
            }
        }
    }
}
```

### 3.3 Rust Implementation

See [`rust/tests/conformance_test.rs`](../../rust/tests/conformance_test.rs):

```rust
use jsonprofile::{JsonValidator, ProfileSource, ValidationRuntimeConfiguration};
use std::collections::HashMap;
use std::path::PathBuf;

fn main() -> Result<(), String> {
    let isa_dir = PathBuf::from("../shared/resources/isa-json");
    let base_profile = isa_dir.join("profiles/metabolights-profile-0.1.0.json");
    let extended_profile = isa_dir.join("profiles/metabolights-bh-extended-profile-0.1.0.json");
    let input_json = isa_dir.join("metabolights-input-isa.json");

    let mut refs: HashMap<String, ProfileSource> = HashMap::new();
    refs.insert(
        "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json".to_string(),
        ProfileSource::Path(base_profile.clone()),
    );
    refs.insert(
        "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json".to_string(),
        ProfileSource::Path(extended_profile.clone()),
    );

    let runtime_config = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        skip_jsonschema_validation: Some(true),
        ..Default::default()
    };

    for profile_path in [base_profile, extended_profile] {
        let validator = JsonValidator::with_referenced_profiles(
            None,
            ProfileSource::Path(profile_path),
            refs.clone(),
        )?;
        let result = validator.validate_json_file(&input_json, Some(runtime_config.clone()))?;

        println!(
            "=== Profile: {} ({}) ===",
            validator.json_profile.name, validator.json_profile.id
        );
        println!("Errors ({} paths):", result.errors.len());
        for (path, msgs) in &result.errors {
            for msg in msgs {
                println!("  [REQUIRED]    {}\t{}\t{}", msg.code, path, msg.message);
            }
        }
        println!("Recommendations ({} paths):", result.recommendations.len());
        for (path, msgs) in &result.recommendations {
            for msg in msgs {
                println!("  [RECOMMENDED] {}\t{}\t{}", msg.code, path, msg.message);
            }
        }
        println!("Optionals ({} paths):", result.optionals.len());
        for (path, msgs) in &result.optionals {
            for msg in msgs {
                println!("  [OPTIONAL]    {}\t{}\t{}", msg.code, path, msg.message);
            }
        }
    }
    Ok(())
}
```

---

## Step 4: Run Steps & Example Validation Outputs

Run the ISA-JSON validation tests in each language from the repository root:

```sh
# 1. Python
cd python && PYTHONPATH=. uv run pytest tests/test_isa_json_profile.py -v -s && cd ..

# 2. Java 21
gradle --project-cache-dir .cache/gradle/project-cache -p java test --tests org.metabolomicshub.jsonprofile.ConformanceSmokeTest.validatesMetaboLightsIsaJsonProfileOffline --info

# 3. Rust
cargo test --manifest-path rust/Cargo.toml --test conformance_test test_metabolights_isa_json_profile -- --nocapture
```

### 4.1 Output for Profile 1: `metabolights-profile-0.1.0.json` (Base Profile)

In **Python**, **Java 21**, and **Rust** (with `offline_mode=True`), validating `metabolights-input-isa.json` against `metabolights-profile-0.1.0.json` returns the **exact same 13 `errors` paths, 5 `recommendations` paths, and 0 `optionals` paths**:

```text
=== Profile: MetaboLights ISA-JSON Base Profile (https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json) ===
Errors (13 paths):
  [REQUIRED]    M-000501	$.studies[0].assays[0].measurementType	Assay technology type is not in the defined list: '[MS, http://purl.obolibrary.org/obo/MS_1003904, untargeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003905, targeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003906, semi-targeted analysis, ]'. '[MSIO, http://purl.obolibrary.org/obo/msio.owl#metabolite_profiling, metabolite profiling, ]' is not in cv term list
  [REQUIRED]    M-000318	$.studies[0].characteristicCategories[2].characteristicType	If there is a characteristic type 'cell type', It MUST be defined as [EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. Term is not valid. invalid cv term label or accession '[, , cell type, ]'.
  [REQUIRED]    M-00306	$.studies[0].description	Study description MUST be equal or grater than 150 characters. minimum length error
  [REQUIRED]    M-00307	$.studies[0].filename	Study file name must start 's_' and follow study identifier and '.txt' extension. e.g. s_REQ11111.txt, s_MTBLS1.txt.. value does not match the pattern. input: 's_BH2024-study.txt' expected pattern: '^s_(MTBLS|REQ|MHD)\d\.txt$'
  [REQUIRED]    M-00304	$.studies[0].identifier	study identifier MUST have MTBLS, REQ or MHD prefix, and end with digits. e.g., MTBLS1, REQ202610220111.. value does not match the pattern. input: 'BH2024' expected pattern: '^(MTBLS|REQ|MHD)\d$'
  [REQUIRED]    M-000323	$.studies[0].materials.sources[0]	Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]. Minimum items error in $.characteristics[*].category["@id"][*].characteristicType. Matched count: 0, expected : 1 .
  [REQUIRED]    M-000323	$.studies[0].materials.sources[1]	Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]. Minimum items error in $.characteristics[*].category["@id"][*].characteristicType. Matched count: 0, expected : 1 .
  [REQUIRED]    M-000453	$.studies[0].protocols[12].description	Protocol description MUST be equal or greater than 40 characters. minimum length error
  [REQUIRED]    M-000453	$.studies[0].protocols[13].description	Protocol description MUST be equal or greater than 40 characters. minimum length error
  [REQUIRED]    M-000453	$.studies[0].protocols[14].description	Protocol description MUST be equal or greater than 40 characters. minimum length error
  [REQUIRED]    M-000453	$.studies[0].protocols[2].description	Protocol description MUST be equal or greater than 40 characters. minimum length error
  [REQUIRED]    M-000453	$.studies[0].protocols[3].description	Protocol description MUST be equal or greater than 40 characters. minimum length error
  [REQUIRED]    M-000311	$.studies[0].studyDesignDescriptors	At least 3 design descriptors MUST be defined.. minimum count error: current: 2, min: 3
Recommendations (5 paths):
  [RECOMMENDED] M-000453	$.studies[0].protocols[10].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , library construction, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[11].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , library construction, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[12].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , nucleic acid sequencing, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[8].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , material separation, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[9].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , material separation, ]'.
Optionals (0 paths):
```

*(Note: When `offline_mode=False`, all implementations additionally query EBI OLS4 for online term/hierarchy checks `M-000350`, `M-000502`, and `M-000503`.)*

### 4.2 Output for Profile 2: `metabolights-bh-extended-profile-0.1.0.json` (Extended Child Profile)

When validated against the extended child profile (`metabolights-bh-extended-profile-0.1.0.json`), `$.studies[0].identifier` (`BH-00304`), `$.studies[0].filename` (`BH-00307`), and `$.studies[0].studyDesignDescriptors` (`BH-000311`) accept the `BH2024` study conventions, and `$.studies[0].protocols[*].description` is deleted via `null`.

All 3 implementations (**Python**, **Java 21**, and **Rust**) return the **exact same 5 `errors` paths, 5 `recommendations` paths, and 0 `optionals` paths**:

```text
=== Profile: MetaboLights BH Extended ISA-JSON Profile (https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json) ===
Errors (5 paths):
  [REQUIRED]    M-000501	$.studies[0].assays[0].measurementType	Assay technology type is not in the defined list: '[MS, http://purl.obolibrary.org/obo/MS_1003904, untargeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003905, targeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003906, semi-targeted analysis, ]'. '[MSIO, http://purl.obolibrary.org/obo/msio.owl#metabolite_profiling, metabolite profiling, ]' is not in cv term list
  [REQUIRED]    M-000318	$.studies[0].characteristicCategories[2].characteristicType	If there is a characteristic type 'cell type', It MUST be defined as [EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. Term is not valid. invalid cv term label or accession '[, , cell type, ]'.
  [REQUIRED]    M-00306	$.studies[0].description	Study description MUST be equal or grater than 150 characters. minimum length error
  [REQUIRED]    M-000323	$.studies[0].materials.sources[0]	Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]. Minimum items error in $.characteristics[*].category["@id"][*].characteristicType. Matched count: 0, expected : 1 .
  [REQUIRED]    M-000323	$.studies[0].materials.sources[1]	Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]. Minimum items error in $.characteristics[*].category["@id"][*].characteristicType. Matched count: 0, expected : 1 .
Recommendations (5 paths):
  [RECOMMENDED] M-000453	$.studies[0].protocols[10].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , library construction, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[11].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , library construction, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[12].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , nucleic acid sequencing, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[8].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , material separation, ]'.
  [RECOMMENDED] M-000453	$.studies[0].protocols[9].protocolType	Protocol type is not in the defined list... invalid cv term label or accession '[, , material separation, ]'.
Optionals (0 paths):
```

---

## Step 5: Cross-Language Parity Summary

| Profile | Category | Count | Exact JSONPaths (`json_path`) & Requirement Codes (`code`) Across Python, Java 21, and Rust |
| :--- | :--- | :---: | :--- |
| **`metabolights-profile-0.1.0.json`** | `errors` (`REQUIRED`) | **13** | `$.studies[0].assays[0].measurementType` (`M-000501`), `$.studies[0].characteristicCategories[2].characteristicType` (`M-000318`), `$.studies[0].description` (`M-00306`), `$.studies[0].filename` (`M-00307`), `$.studies[0].identifier` (`M-00304`), `$.studies[0].materials.sources[0..1]` (`M-000323`), `$.studies[0].protocols[2,3,12,13,14].description` (`M-000453`), `$.studies[0].studyDesignDescriptors` (`M-000311`) |
| **`metabolights-profile-0.1.0.json`** | `recommendations` (`RECOMMENDED`) | **5** | `$.studies[0].protocols[8..12].protocolType` (`M-000453`) |
| **`metabolights-profile-0.1.0.json`** | `optionals` (`OPTIONAL`) | **0** | *(none)* |
| **`metabolights-bh-extended-profile-0.1.0.json`** | `errors` (`REQUIRED`) | **5** | `$.studies[0].assays[0].measurementType` (`M-000501`), `$.studies[0].characteristicCategories[2].characteristicType` (`M-000318`), `$.studies[0].description` (`M-00306`), `$.studies[0].materials.sources[0..1]` (`M-000323`) |
| **`metabolights-bh-extended-profile-0.1.0.json`** | `recommendations` (`RECOMMENDED`) | **5** | `$.studies[0].protocols[8..12].protocolType` (`M-000453`) |
| **`metabolights-bh-extended-profile-0.1.0.json`** | `optionals` (`OPTIONAL`) | **0** | *(none)* |
