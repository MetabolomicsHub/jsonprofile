# Java example

This example validates the same mzTab-M sample document and MetaboLights
profile used by the Python implementation, using the Java implementation.

Java 21 is required. If you use SDKMAN, activate the repository's pinned Java
version from the repository root, then run the Gradle application:

```sh
sdk env
cd examples/java
./gradlew run
```

The example uses `manual_null_MTBLS263.mztab.json`, the mzTab-M schema, and
profiles under `../../shared/resources/mztab-m`. The MetaboLights profile
extends the default profile, which is supplied as a referenced profile.
