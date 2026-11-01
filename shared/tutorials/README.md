# `jsonprofile` Cross-Language Tutorials

This folder contains step-by-step, hands-on tutorials demonstrating how to validate scientific JSON datasets against multiple hierarchical profiles using **Python**, **Java 21**, and **Rust**.

All three implementations guarantee **strict cross-language parity**: for any given JSON document, JSON Schema, and `JsonProfile`, **Python**, **Java 21**, and **Rust** return the **exact same `errors`, `recommendations`, and `optionals`** for every JSONPath (`json_path`) and requirement `code` (human-readable message text may vary slightly by language).

---

## Tutorials

1. **[Tutorial 1: Validating an ISA-JSON Dataset Using Different Profiles](./01-isa-json-validation.md)**
   - **Dataset**: [`../resources/isa-json/metabolights-input-isa.json`](../resources/isa-json/metabolights-input-isa.json)
   - **Profiles**:
     - [`metabolights-profile-0.1.0.json`](../resources/isa-json/profiles/metabolights-profile-0.1.0.json) — Base MetaboLights ISA-JSON profile (`13` error paths, `5` recommendation paths, `0` optionals in offline mode)
     - [`metabolights-bh-extended-profile-0.1.0.json`](../resources/isa-json/profiles/metabolights-bh-extended-profile-0.1.0.json) — Extended child profile (`extends`) customizing study identifier/filename patterns and removing protocol description length requirements (`5` error paths, `5` recommendation paths, `0` optionals in offline mode)
   - **Key Concepts Covered**:
     - Custom ontology field mappings (`cv_term_field_mapping`: `termSource`, `termAccession`, `annotationValue`)
     - Cross-object `@id` graph resolution (`object_unique_id_fields: ["@id"]` + `is_reference_field: true`)
     - Profile inheritance (`extends`), rule overrides, and rule deletion (`null`)
     - Online vs. offline ontology term validation (`ValidationRuntimeConfiguration`)

2. **[Tutorial 2: Validating an mzTab-M JSON Dataset Using Different Profiles & OPA WASM](./02-mztab-m-validation.md)**
   - **Dataset**: [`../resources/mztab-m/manual_null_MTBLS263.mztab.json`](../resources/mztab-m/manual_null_MTBLS263.mztab.json)
   - **Schema**: [`../resources/mztab-m/mztabm-2.1.0-M.schema.json`](../resources/mztab-m/mztabm-2.1.0-M.schema.json)
   - **OPA WASM Bundle**: [`../resources/mztab-m/mztabm-default-2.1.0-M.wasm`](../resources/mztab-m/mztabm-default-2.1.0-M.wasm)
   - **Profiles**:
     - [`mztabm-default-profile-2.1.0-M.json`](../resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json) — Base profile with OPA WASM cross-section rules (`D-0010`, `D-0020`, `D-0021`, `D-0022`) and field constraints (`20` error paths, `12` recommendation paths, `0` optionals)
     - [`mztabm-mtd_smf-profile-2.1.0-M.json`](../resources/mztab-m/profiles/mztabm-mtd_smf-profile-2.1.0-M.json) — Extends `default`; requires `metadata` + `smallMoleculeFeature` (`20` error paths, `12` recommendation paths, `0` optionals)
     - [`mztabm-mtd_sml-profile-2.1.0-M.json`](../resources/mztab-m/profiles/mztabm-mtd_sml-profile-2.1.0-M.json) — Extends `default`; requires `metadata` + `smallMoleculeSummary` and disables feature/evidence rules (`null`), passing with `0` error paths (`12` recommendation paths, `0` optionals)
     - [`mztabm-full-profile-2.1.0-M.json`](../resources/mztab-m/profiles/mztabm-full-profile-2.1.0-M.json) — Extends `default`; requires all 4 mzTab-M sections (`20` error paths, `12` recommendation paths, `0` optionals)
     - [`mztabm-metabolights-profile-2.1.0-M.json`](../resources/mztab-m/profiles/mztabm-metabolights-profile-2.1.0-M.json) — Extends `default`; adds MetaboLights-specific metadata and ontology rules (`2` error paths, `0` recommendation paths, `0` optionals)
   - **Key Concepts Covered**:
     - Combining JSON Schema validation with multi-profile inheritance
     - Native Open Policy Agent (OPA) WebAssembly (`.wasm` / `.tar.gz`) execution in Python (`wasmtime`), Java 21 (`Chicory`), and Rust (`wasmi`)
     - Per-requirement circuit-breaking (`max_messages_for_each_requirement`)

---

## Installation & Setup Across All 3 Languages

### 1. Python (`>= 3.10`)

Requires [`uv`](https://docs.astral.sh/uv/) (recommended) or `pip`:

```sh
# Install jsonprofile with OPA WebAssembly support and dev dependencies
cd python
uv sync --extra opa --group dev

# Or via pip in a virtual environment:
pip install -e ".[opa]"
```

### 2. Java (`JDK 21+`)

Requires **Java 21+** and **Gradle 8.7+** (all dependencies—including Jackson, Jayway JsonPath, NetworkNT JSON Schema Validator, and the pure-Java Chicory WebAssembly runtime for OPA—are managed automatically by [`java/build.gradle`](../../java/build.gradle)):

```sh
# Compile and verify Java 21 implementation from the repository root
gradle --project-cache-dir .cache/gradle/project-cache -p java compileJava compileTestJava
```

Or add `org.metabolomicshub:jsonprofile:0.1.0` to your Gradle or Maven project:

```groovy
dependencies {
    implementation 'org.metabolomicshub:jsonprofile:0.1.0'
}
```

### 3. Rust (`1.75+` / Cargo)

Requires the standard Rust toolchain (`rustup` / `cargo`; all dependencies—including `serde_json`, `serde_json_path`, `jsonschema`, and the `wasmi` WebAssembly runtime for OPA—are managed by [`rust/Cargo.toml`](../../rust/Cargo.toml)):

```sh
# Build the Rust crate (with default local-opa feature enabled)
cargo build --manifest-path rust/Cargo.toml
```

---

## Running the Tutorial Validations

From the repository root, you can run all tutorial validations across Python, Java 21, and Rust using [`just`](../../justfile) or the native language commands:

```sh
# Run all languages (Python + Java 21 + Rust)
just test

# Or run individual language suites:
just test-python
just test-java
just test-rust
```

To run only the ISA-JSON or mzTab-M tutorial tests with verbose output:

```sh
# Python
cd python && PYTHONPATH=. uv run pytest tests/test_isa_json_profile.py tests/test_mztabm_profile.py -v -s

# Java 21
gradle --project-cache-dir .cache/gradle/project-cache -p java test --tests org.metabolomicshub.jsonprofile.ConformanceSmokeTest --info

# Rust
cargo test --manifest-path rust/Cargo.toml --test conformance_test -- --nocapture
```
