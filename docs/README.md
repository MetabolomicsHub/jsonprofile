# `jsonprofile` Documentation

Welcome to the documentation for the multi-language `jsonprofile` validation framework.

## Documentation Index

- **[Tutorials (`../shared/tutorials/README.md`)](../shared/tutorials/README.md)**:
  - **[Tutorial 1: Validating an ISA-JSON Dataset Using Different Profiles (`../shared/tutorials/01-isa-json-validation.md`)](../shared/tutorials/01-isa-json-validation.md)**
  - **[Tutorial 2: Validating an mzTab-M JSON Dataset Using Different Profiles & OPA WASM (`../shared/tutorials/02-mztab-m-validation.md`)](../shared/tutorials/02-mztab-m-validation.md)**
- **[Shared Specification (`../shared/spec/README.md`)](../shared/spec/README.md)**: Language-neutral specification of `JsonProfile`, `JsonProfileConfiguration`, profile inheritance (`extends`), `FieldRequirement`, `FieldRequirementGroup`, `OpaFieldRequirement`, `Precondition`, all 21 built-in `Constraint` types, and side-by-side Python, Java 21, and Rust snippets.
- **[Python Guide (`../python/README.md`)](../python/README.md)**: Python API reference, installation, basic usage, profile inheritance, optional `jsonprofile[opa]` extension usage, and `@profile_validator` / `@constraint_checker` custom validator registration.
- **[Java 21 Guide (`../java/README.md`)](../java/README.md)**: Java 21 (`org.metabolomicshub.jsonprofile`) API reference, Gradle setup, basic usage, profile inheritance, OPA extension usage (`OpaEngineFactory`), and custom validator registration.
- **[Rust Guide (`../rust/README.md`)](../rust/README.md)**: Rust (`jsonprofile`) API reference, Cargo features, basic usage, profile inheritance, OPA extension usage (`OpaEngineFactory`), compile-time `inventory` plugin macros, and runtime `libloading` shared library plugins.

## Shared Resources & Conformance Fixtures

- **[`../shared/schema/jsonprofile.schema.json`](../shared/schema/jsonprofile.schema.json)**: Canonical JSON Schema for `JsonProfile` documents.
- **[`../shared/conformance/`](../shared/conformance/)**: Cross-language smoke manifest (`smoke.json`) and validation conformance suite (`validation_suite.json`).
- **[`../shared/resources/isa-json/`](../shared/resources/isa-json/)**: Shared ISA-JSON MetaboLights profiles (`profiles/metabolights-profile-0.1.0.json`, `profiles/metabolights-bh-extended-profile-0.1.0.json`) and test input dataset (`metabolights-input-isa.json`).
- **[`../shared/resources/mztab-m/`](../shared/resources/mztab-m/)**: Shared HUPO-PSI mzTab-M 2.1.0-M schema, OPA WASM bundle, 5 hierarchical profiles (`default`, `mtd_smf`, `mtd_sml`, `full`, `metabolights`), and test dataset.
