set shell := ["bash", "-eu", "-o", "pipefail", "-c"]

export PRE_COMMIT_HOME := ".cache/pre-commit"
export UV_CACHE_DIR := ".cache/uv"
export GRADLE_USER_HOME := ".cache/gradle"
export CARGO_TARGET_DIR := ".cache/rust/target"

test: test-python test-java test-rust

lint:
    uv --project python run pre-commit run --all-files

lint-check: lint-python lint-java lint-rust
    uv lock --check --project python
    SKIP=end-of-file-fixer,trailing-whitespace,detect-missing-init,uv-lock,ruff-check,ruff-format,spotless-format,java-lint,cargo-fmt,rust-lint uv --project python run pre-commit run --all-files

format: format-python format-java format-rust

format-python:
    uv --project python run ruff check --fix --config python/pyproject.toml python
    uv --project python run ruff format --config python/pyproject.toml python

lint-python:
    uv --project python run ruff check --no-fix --config python/pyproject.toml python
    uv --project python run ruff format --check --config python/pyproject.toml python

format-java:
    gradle --project-cache-dir .cache/gradle/project-cache -p java spotlessApply

lint-java:
    gradle --project-cache-dir .cache/gradle/project-cache -p java spotlessCheck checkstyleMain checkstyleTest

format-rust:
    cargo fmt --manifest-path rust/Cargo.toml
    cargo fmt --manifest-path examples/rust/Cargo.toml

lint-rust:
    cargo fmt --manifest-path rust/Cargo.toml -- --check
    cargo fmt --manifest-path examples/rust/Cargo.toml -- --check
    cargo clippy --manifest-path rust/Cargo.toml --all-targets --all-features -- -D warnings
    cargo clippy --manifest-path examples/rust/Cargo.toml --all-targets --all-features -- -D warnings

build: build-python build-java build-rust

clean:
    rm -rf .cache/python .cache/java .cache/rust .cache/gradle .cache/uv .cache/pre-commit

test-python:
    cd python && uv sync --extra opa --group dev && PYTHONPATH=. uv run pytest

test-java:
    gradle --project-cache-dir .cache/gradle/project-cache -p java test

test-rust:
    cargo test --manifest-path rust/Cargo.toml

build-python:
    cd python && uv build --out-dir ../.cache/python/dist --clear

build-java:
    gradle --project-cache-dir .cache/gradle/project-cache -p java build

build-rust:
    cargo build --release --manifest-path rust/Cargo.toml
