# `jsonprofile` for Java (Java 21)

Java 21 implementation of `jsonprofile` (`org.metabolomicshub.jsonprofile`) for validating JSON documents against JSON Schemas and domain-specific JSON Profiles.

---

## Requirements & Build Setup

- **Java**: JDK 21 (Temurin or compatible)
- **Build Tool**: Gradle (Kotlin DSL)

### Gradle Dependency

```kotlin
dependencies {
    implementation("org.metabolomicshub.jsonprofile:jsonprofile:0.1.0")

    // Optional dependencies for OPA WASM bundle extraction / evaluation:
    implementation("com.dylibso.chicory:runtime:1.1.0")
    implementation("org.apache.commons:commons-compress:1.27.1")
}
```

---

## Core API Overview

| Class / Interface | Package | Description |
| :--- | :--- | :--- |
| `JsonValidator` | `org.metabolomicshub.jsonprofile.validator` | Main validator engine. Accepts a JSON Schema (`Path`, `String`, `JsonNode`, `Map<String, Object>`, or `null`), a profile (`Path`, `String`, `JsonNode`, `Map<String, Object>`, or `JsonProfile`), and optional `referencedProfiles`, `CvTermSearch`, and `OpaEngineFactory`. |
| `JsonProfile` | `org.metabolomicshub.jsonprofile.profile` | Model representing a JSON Profile (`id`, `version`, `name`, `extends`, `configuration`, `requirements`). |
| `ValidationRuntimeConfiguration` | `org.metabolomicshub.jsonprofile.profile` | Per-run settings (`offlineMode`, `skipJsonschemaValidation`, `skipProfileValidation`, `skipDecimalValidations`, `skippedRequirements`, `maxMessagesForEachRequirement`). |
| `JsonValidationResult` | `org.metabolomicshub.jsonprofile.validator` | Validation output containing `getErrors()`, `getRecommendations()`, and `getOptionals()` (`Map<String, List<JsonProfileMessage>>` keyed by JSONPath). |
| `ProfileValidatorUtil` | `org.metabolomicshub.jsonprofile.profile` | Validates a profile file or `JsonProfile` object via `ProfileValidatorUtil.validateProfileFile(Path)` and `ProfileValidatorUtil.validateProfile(JsonProfile)`. |

---

## Usage Examples

### 1. Basic Usage (Validating a JSON File or `Map<String, Object>`)

```java
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class BasicValidationExample {
    public static void main(String[] args) {
        Path schemaPath = Path.of("../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json");
        Path profilePath = Path.of("../shared/resources/mztab-m/profiles/mztabm-default-profile-2.1.0-M.json");
        Path inputJsonPath = Path.of("../shared/resources/mztab-m/manual_null_MTBLS263.mztab.json");

        JsonValidator validator = new JsonValidator(schemaPath, profilePath);

        ValidationRuntimeConfiguration runtimeConfig = new ValidationRuntimeConfiguration();
        runtimeConfig.setOfflineMode(true);
        runtimeConfig.setMaxMessagesForEachRequirement(10);

        JsonValidationResult result = validator.validateJsonFile(inputJsonPath, runtimeConfig);

        for (Map.Entry<String, List<JsonProfileMessage>> entry : result.getErrors().entrySet()) {
            for (JsonProfileMessage msg : entry.getValue()) {
                System.out.printf("[REQUIRED] %s (%s): %s%n",
                        msg.getCode(), entry.getKey(), msg.getMessage());
            }
        }
    }
}
```

### 2. Profile Inheritance (`extends` & `referencedProfiles`)

When validating a profile that extends one or more parent profiles via `"extends"`, provide a `Map<String, Object>` mapping each parent profile's `id` to its `Path`, `Map`, or `JsonProfile`:

```java
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;
import java.nio.file.Path;
import java.util.Map;

public class ProfileInheritanceExample {
    public static void main(String[] args) {
        Path mztabDir = Path.of("../shared/resources/mztab-m");
        Path profilesDir = mztabDir.resolve("profiles");

        String defaultId = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";
        String mtblsId = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json";

        Map<String, Object> referencedProfiles = Map.of(
                defaultId, profilesDir.resolve("mztabm-default-profile-2.1.0-M.json"),
                mtblsId, profilesDir.resolve("mztabm-metabolights-profile-2.1.0-M.json"));

        JsonValidator validator = new JsonValidator(
                mztabDir.resolve("mztabm-2.1.0-M.schema.json"),
                profilesDir.resolve("mztabm-metabolights-profile-2.1.0-M.json"),
                referencedProfiles);

        ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
        rt.setOfflineMode(true);

        JsonValidationResult res = validator.validateJsonFile(
                mztabDir.resolve("manual_null_MTBLS263.mztab.json"), rt);
        System.out.println("Error paths: " + res.getErrors().keySet());
    }
}
```

---

## OPA Extension Usage

OPA evaluation in Java is pluggable via the [`OpaEngine`](src/main/java/org/metabolomicshub/jsonprofile/validator/OpaEngine.java) and [`OpaEngineFactory`](src/main/java/org/metabolomicshub/jsonprofile/validator/OpaEngineFactory.java) interfaces:

- **Automatic `LocalOpaEngineFactory` & `LocalOpaEngine`**: When `com.dylibso.chicory:runtime` and `org.apache.commons:commons-compress` are on the classpath, `JsonValidator` automatically uses `LocalOpaEngineFactory`, which extracts `policy.wasm` and `/data.json` from `.wasm` / `.tar.gz` bundles and executes OPA policies natively via the OPA WASM ABI v1.2 (including built-ins `sprintf`, `time.now_ns`, `time.format`, `time.parse_ns`, `regex.find_n`, `regex.replace`, `json.match_schema`). When Chicory is absent, `JsonValidator` falls back to `NotImplementedOpaEngineFactory`.
- **Custom `OpaEngineFactory`**: Pass a custom `OpaEngineFactory` to the 5-argument `JsonValidator` constructor to evaluate OPA policies via an embedded WASM runtime or remote OPA server:

```java
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;
import org.metabolomicshub.jsonprofile.validator.OpaEngineFactory;
import java.util.List;
import java.util.Map;

public class OpaExtensionExample {
    public static void main(String[] args) {
        Map<String, Object> profile = Map.of(
                "id", "https://example.org/profiles/opa-test.json",
                "version", "1.0.0",
                "name", "OPA Test Profile",
                "configuration", Map.of(
                        "wasm_file_definitions", Map.of(
                                "default", Map.of(
                                        "wasm_file_path", "../shared/resources/mztab-m/mztabm-default-2.1.0-M.wasm",
                                        "wasm_file_download_url", "https://example.org/dummy.wasm",
                                        "entrypoint", "mztabm/policies"))),
                "requirements", Map.of(
                        "$.item", Map.of(
                                "code", "OPA-0001",
                                "policy_id", "policy_0001",
                                "enforcement_level", "required")));

        OpaEngineFactory customOpaFactory = (wasmPath, downloadUrl) -> (inputData, entrypoint, data) ->
                List.of(Map.of(
                        "messages", List.of(Map.of(
                                "policy_id", "policy_0001",
                                "source", "$.item",
                                "message", "OPA policy violation detected"))));

        JsonValidator validator = new JsonValidator(
                null,
                profile,
                null,
                null,
                customOpaFactory);

        ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
        rt.setSkipJsonschemaValidation(true);

        JsonValidationResult res = validator.validateDict(Map.of("item", "invalid"), rt);
        System.out.println("OPA Errors: " + res.getErrors());
    }
}
```

---

## Custom Validators & Constraint Checkers

To register a custom `ProfileValidator` and `ConstraintChecker` in Java:
1. Subclass `DefaultProfileValidator` and register your `ConstraintChecker` in its constructor.
2. Reference its fully-qualified class name under `"java"` in the profile's `configuration.custom_validator_definitions`:

```java
package com.example.validators;

import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.validator.ConstraintChecker;
import org.metabolomicshub.jsonprofile.validator.DefaultProfileValidator;
import org.metabolomicshub.jsonprofile.validator.JsonProfileRunContext;
import java.util.Map;

public class MyCustomValidator extends DefaultProfileValidator {
    public MyCustomValidator(String id) {
        super(id);
        registerChecker("custom", "even_length", new ConstraintChecker() {
            @Override
            public CheckResult validate(
                    Constraint constraint,
                    Object value,
                    Map<String, Object> root,
                    JsonProfileRunContext context) {
                boolean ok = value instanceof String s && (s.length() % 2 == 0);
                return new CheckResult(ok, formatMessage(constraint, "String length must be even"));
            }
        });
    }
}
```

---

## Development & Testing

```sh
# From repository root:
just format-java
just lint-java
just test-java
just build-java
```
