# jsonprofile

Multi-language implementations of `jsonprofile` — a declarative validation layer on top of JSON Schema supporting profile inheritance (`extends`), controlled vocabulary (CV) validation, preconditions, cross-object `@id` reference resolution, custom validators, and Open Policy Agent (OPA) WebAssembly (`.wasm` / `.tar.gz`) policy rules.

---

## Repository Folder Structure

```text
jsonprofile/
├── shared/                                  # Language-neutral specification, schemas, datasets & tutorials
│   ├── spec/
│   │   └── README.md                        # Formal JsonProfile specification & multi-language snippets
│   ├── schema/
│   │   └── jsonprofile.schema.json          # Canonical JSON Schema for validating JsonProfile documents
│   ├── conformance/
│   │   ├── smoke.json                       # Cross-language smoke test fixture
│   │   └── validation_suite.json            # Cross-language conformance test suite (13 scenarios)
│   ├── resources/
│   │   ├── isa-json/                        # Shared ISA-JSON profiles & MetaboLights test dataset
│   │   │   ├── metabolights-input-isa.json
│   │   │   └── profiles/
│   │   │       ├── metabolights-profile-0.1.0.json
│   │   │       └── metabolights-bh-extended-profile-0.1.0.json
│   │   └── mztab-m/                         # Shared HUPO-PSI mzTab-M 2.1.0-M schema, OPA WASM & profiles
│   │       ├── mztabm-2.1.0-M.schema.json
│   │       ├── mztabm-default-2.1.0-M.wasm
│   │       ├── manual_null_MTBLS263.mztab.json
│   │       └── profiles/
│   │           ├── mztabm-default-profile-2.1.0-M.json
│   │           ├── mztabm-mtd_smf-profile-2.1.0-M.json
│   │           ├── mztabm-mtd_sml-profile-2.1.0-M.json
│   │           ├── mztabm-full-profile-2.1.0-M.json
│   │           └── mztabm-metabolights-profile-2.1.0-M.json
│   └── tutorials/                           # Step-by-step 3-language tutorials with execution outputs
│       ├── README.md
│       ├── 01-isa-json-validation.md
│       └── 02-mztab-m-validation.md
│
├── python/                                  # Python implementation (jsonprofile)
│   ├── pyproject.toml                       # uv / PEP 621 project & optional jsonprofile[opa] config
│   ├── README.md                            # Python API reference & usage examples
│   ├── src/
│   │   ├── jsonprofile/
│   │   │   ├── profile/                     # Pydantic models, 21 built-in constraints & profile validator
│   │   │   └── validator/                   # JsonValidator, checkers, OLS search & wasmtime OPA engine
│   │   └── scripts/                         # Profile generation utilities & announcement examples
│   └── tests/                               # pytest conformance, ISA-JSON, mzTab-M & OPA test suites
│
├── java/                                    # Java 21 implementation (org.metabolomicshub.jsonprofile)
│   ├── build.gradle.kts                     # Gradle Kotlin DSL build (Jackson, networknt, Jayway, Chicory)
│   ├── settings.gradle.kts
│   ├── README.md                            # Java 21 API reference & usage examples
│   └── src/
│       ├── main/java/org/metabolomicshub/jsonprofile/
│       │   ├── profile/                     # Profile models, polymorphic constraints & ProfileValidatorUtil
│       │   └── validator/                   # JsonValidator, checkers, OLS search & Chicory OPA engine
│       └── test/java/org/metabolomicshub/jsonprofile/
│           ├── ConformanceSmokeTest.java    # JUnit 5 conformance, ISA-JSON, mzTab-M & OPA tests
│           └── JsonProfileTest.java
│
├── rust/                                    # Rust implementation (jsonprofile crate)
│   ├── Cargo.toml                           # Cargo manifest (dynamic-plugins via libloading, opa via wasmi)
│   ├── README.md                            # Rust API reference & usage examples
│   ├── src/
│   │   ├── lib.rs                           # Crate exports & register_constraint_checker! macros
│   │   ├── profile/                         # Serde models, ConstraintKind enum & profile_validator
│   │   └── validator/                       # JsonValidator, checkers, inventory/cdylib factory & wasmi OPA
│   └── tests/
│       └── conformance_test.rs              # Cargo conformance, ISA-JSON, mzTab-M & OPA tests
│
├── examples/                                # Runnable examples for each implementation
│   ├── python/                              # Python validation example
│   ├── java/                                # Java validation application
│   └── rust/                                # Rust validation application
│
├── docs/
│   └── README.md                            # Central documentation index
├── .github/
│   └── workflows/ci.yml                     # Multi-language CI pipeline (Python, Java 21, Rust)
├── justfile                                 # Unified workspace task runner (test, lint, build, clean)
└── .cache/                                  # Centralized build & tool cache directory (git-ignored)
```

### Folder Breakdown

- **[`shared/`](shared/spec/README.md)**: Single source of truth shared across all language implementations:
  - [`shared/spec/`](shared/spec/README.md): Language-neutral specification of the `JsonProfile` structure, inheritance (`extends`), requirements, preconditions, and constraints.
  - [`shared/schema/`](shared/schema/jsonprofile.schema.json): Canonical JSON Schema (`jsonprofile.schema.json`) used to validate profile definitions in Python, Java, and Rust.
  - [`shared/conformance/`](shared/conformance/): Cross-language test manifests (`smoke.json` and `validation_suite.json`) executed by every language's test suite to guarantee behavioral parity.
  - [`shared/resources/`](shared/resources/): Real-world scientific validation datasets, schemas, profiles, and OPA WASM bundles for **ISA-JSON** (`isa-json/`) and **HUPO-PSI mzTab-M** (`mztab-m/`).
- [`shared/tutorials/`](shared/tutorials/README.md): Step-by-step guides for validating ISA-JSON and mzTab-M datasets across Python, Java 21, and Rust.
- **[`python/`](python/README.md)**: Reference Python implementation (`jsonprofile`, Python 3.10+) using `pydantic`, `jsonschema-rs`, `jsonpath-ng`, `ontology-lookup`, and optional `opa-wasmtime` (`jsonprofile[opa]`).
- **[`java/`](java/README.md)**: Java 21 implementation (`org.metabolomicshub.jsonprofile`) built with Gradle, Jackson, `networknt/json-schema-validator`, Jayway `JsonPath`, Caffeine, and `Chicory` pure-Java WebAssembly runtime for OPA WASM evaluation.
- **[`rust/`](rust/README.md)**: Rust 2021 edition crate (`jsonprofile`) using `serde`, `jsonschema`, `serde_json_path`, `inventory` (compile-time plugin registration), `libloading` (runtime shared-library plugins), and `wasmi` for OPA WASM evaluation.
- **[`docs/`](docs/README.md)**: Documentation entrypoint linking specifications, language guides, and tutorials.
- **[`justfile`](justfile)**: Unified command runner routing all build artifacts and caches (`uv`, `pytest`, `ruff`, `gradle`, `cargo`) into `.cache/` to keep source directories clean.

---

## Documentation

### Tutorials

The shared tutorials walk through profile validation with real datasets and show how profile inheritance, controlled vocabulary checks, and OPA policies behave across the three implementations.

- [Tutorial index](shared/tutorials/README.md): setup, execution instructions, and an overview of both walkthroughs.
- [ISA-JSON validation](shared/tutorials/01-isa-json-validation.md): validate a MetaboLights ISA-JSON dataset with the base and BH-extended profiles; demonstrates profile inheritance and ontology field mappings.
- [mzTab-M validation](shared/tutorials/02-mztab-m-validation.md): validate an mzTab-M dataset against multiple profiles; demonstrates inheritance, JSON Schema checks, and OPA WebAssembly rules.

### Runnable examples

Each example folder contains a small application and instructions for running it from that implementation's directory. The Python and Java examples validate the same mzTab-M input with the MetaboLights profile; the Rust example demonstrates validation with the Rust crate.

- [Python example](examples/python/README.md): run `main.py` with `uv`; it resolves the shared schema, profiles, and sample input from the repository.
- [Java example](examples/java/README.md): run the Gradle application with Java 21 using the repository's SDKMAN configuration.
- [Rust example](examples/rust/README.md): run the Cargo application against the shared mzTab-M resources.

The examples use the shared [`manual_null_MTBLS263.mztab.json`](shared/resources/mztab-m/manual_null_MTBLS263.mztab.json) sample. They are intended as concise starting points; the tutorials provide the broader multi-profile walkthroughs.

### Implementation guides

- [Shared Specification & Multi-Language Examples](shared/spec/README.md)
- [Python API & Usage Guide](python/README.md)
- [Java 21 API & Usage Guide](java/README.md)
- [Rust API & Usage Guide](rust/README.md)

---

## Development Environment

The repository uses Python 3.10 or later, Java 21, Gradle, and the stable Rust toolchain. The root [`justfile`](justfile) provides the common formatting, lint, test, and build commands.

### Install the tools

On macOS with Homebrew, install `just` and `uv`:

```sh
brew install just uv
```

For other installation methods and platforms, see the [just installation guide](https://just.systems/man/en/installation.html) and [uv installation guide](https://docs.astral.sh/uv/getting-started/installation/).

Install [SDKMAN!](https://sdkman.io/install/) if it is not already available, then open a new Bash or Zsh terminal or initialize it in the current shell:

```sh
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

This repository's [`.sdkmanrc`](.sdkmanrc) pins Java to Temurin 21. Install the pinned JDK and Gradle with:

```sh
sdk env install
sdk install gradle
```

To switch to the project JDK automatically when entering the repository, set `sdkman_auto_env=true` in `~/.sdkman/etc/config`. Otherwise, run `sdk env` in the repository shell. SDKMAN documents both [project environment files](https://sdkman.io/usage/) and [Gradle installation](https://gradle.org/install/).

Install Rust with [rustup](https://rustup.rs/) on macOS or Linux, then ensure the stable toolchain and the components used by this repository are installed:

```sh
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
rustup toolchain install stable
rustup default stable
rustup component add rustfmt clippy
```

Install Python 3.10 through `uv` (or let `uv` download a compatible Python when needed), then create the project environment with its optional OPA support and development tools:

```sh
uv python install 3.10
uv sync --project python --extra opa --group dev
```

The Python requirement is `>=3.10`; `uv` can install and manage Python versions as described in its [Python installation guide](https://docs.astral.sh/uv/guides/install-python/).

To run pre-commit automatically when committing, install the Git hook:

```sh
uv run --project python pre-commit install
```

### Common commands

Run these commands from the repository root:

```sh
just format       # format Python, Java, and Rust
just lint         # run pre-commit hooks (formatting hooks may update files)
just lint-check   # check formatting and lint without applying fixes
just test         # run Python, Java, and Rust tests
just build        # build all three implementations
```

Language-specific tasks are also available, for example `just lint-python`, `just lint-java`, `just lint-rust`, and their `format-*`, `test-*`, and `build-*` counterparts. All local caches and build outputs are routed under `.cache/`.

---

## Implementation Status

- **Python (`python/`)**: Full `JsonProfile` and `JsonValidator` implementation with `jsonprofile[opa]` (`wasmtime`) support, tested against shared conformance and `isa-json` / `mztab-m` suites.
- **Java 21 (`java/`)**: Full `JsonProfile` and `JsonValidator` implementation with native `Chicory` OPA WASM execution and pluggable `OpaEngineFactory`, tested against shared conformance and `isa-json` / `mztab-m` suites.
- **Rust (`rust/`)**: Full `JsonProfile` and `JsonValidator` implementation with native `wasmi` OPA WASM execution, `inventory` compile-time plugin registration, and optional `libloading` dynamic library loading, tested against shared conformance and `isa-json` / `mztab-m` suites.
- **Shared Contract (`shared/`)**: Synchronized `jsonprofile.schema.json`, `smoke.json`, `validation_suite.json`, and shared `isa-json` & `mztab-m` domain profiles/datasets.
