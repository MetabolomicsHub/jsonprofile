package org.metabolomicshub.jsonprofile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.CvTermSearch;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;

class ProfileStrictnessTest {
  private static final String PROFILE_ID = "https://example.org/cv-test";

  private static class FixedCvTermSearch implements CvTermSearch {
    private final boolean valid;

    FixedCvTermSearch(boolean valid) {
      this.valid = valid;
    }

    @Override
    public String getIri(org.metabolomicshub.jsonprofile.profile.CvTerm cvTerm) {
      return null;
    }

    @Override
    public String getCurie(org.metabolomicshub.jsonprofile.profile.CvTerm cvTerm) {
      return null;
    }

    @Override
    public org.metabolomicshub.jsonprofile.profile.CvTerm findCvTerm(
        String source,
        String accessionOrLabel,
        String matchedAccession,
        boolean allowSynonymSearch) {
      return null;
    }

    @Override
    public CheckResult checkCvTerm(
        org.metabolomicshub.jsonprofile.profile.CvTerm cvTerm,
        org.metabolomicshub.jsonprofile.profile.CvTerm parentCvTerm,
        boolean allowSynonymSearch) {
      return new CheckResult(valid, "fixed test result");
    }

    @Override
    public TermWithSynonyms findCvTermWithAccession(String source, String accession) {
      return null;
    }
  }

  private static Map<String, Object> profile(Object requirement) {
    Map<String, Object> profile = new HashMap<>();
    profile.put("id", PROFILE_ID);
    profile.put("version", "1.0.0");
    profile.put("name", "CV test profile");
    profile.put("requirements", Map.of("$.term", requirement));
    return profile;
  }

  private static JsonValidationResult validateCvTerm(
      Map<String, Object> term, boolean searchValid, boolean negated) {
    Map<String, Object> constraint =
        Map.of(
            "type",
            "cv-term",
            "is_cv_term_value_required",
            true,
            "exceptional_cv_list",
            List.of("EX"),
            "negated",
            negated);
    JsonValidator validator =
        new JsonValidator(
            null,
            profile(Map.of("code", "CV-TEST", "value_constraint", constraint)),
            Map.of(),
            new FixedCvTermSearch(searchValid),
            null);
    ValidationRuntimeConfiguration runtime = new ValidationRuntimeConfiguration();
    runtime.setOfflineMode(false);
    return validator.validateDict(Map.of("term", term), runtime);
  }

  private static Map<String, Object> cvTerm(String source, String value) {
    return Map.of(
        "cv_label", source, "cv_accession", source + ":1", "name", "term", "value", value);
  }

  @Test
  void onlineLookupAppliesRequiredValueAndNegation() {
    assertTrue(validateCvTerm(cvTerm("MS", ""), true, false).getErrors().containsKey("$.term"));
    assertTrue(validateCvTerm(cvTerm("MS", "x"), true, true).getErrors().containsKey("$.term"));
    assertTrue(validateCvTerm(cvTerm("MS", "x"), false, true).getErrors().isEmpty());
  }

  @Test
  void exceptionalSourceAppliesRequiredValueAndNegation() {
    assertTrue(validateCvTerm(cvTerm("EX", ""), true, false).getErrors().containsKey("$.term"));
    assertTrue(validateCvTerm(cvTerm("EX", ""), true, true).getErrors().isEmpty());
  }

  @Test
  void profileDeserializationRejectsUnknownModelFieldsButKeepsMapsOpen() {
    Map<String, Object> rootTypo = profile(Map.of("code", "R"));
    rootTypo.put("unknown_profile_key", true);
    assertThrows(RuntimeException.class, () -> new JsonValidator(null, rootTypo));

    Map<String, Object> configTypo = profile(Map.of("code", "R"));
    configTypo.put("configuration", Map.of("unknown_config_key", true));
    assertThrows(RuntimeException.class, () -> new JsonValidator(null, configTypo));

    Map<String, Object> requirementTypo = profile(Map.of("code", "R", "unknown", true));
    assertThrows(RuntimeException.class, () -> new JsonValidator(null, requirementTypo));

    Map<String, Object> constraintTypo =
        profile(
            Map.of(
                "code",
                "R",
                "value_constraint",
                Map.of("type", "regex", "pattern", "x", "unknown", true)));
    assertThrows(RuntimeException.class, () -> new JsonValidator(null, constraintTypo));

    Map<String, Object> dynamic = profile(Map.of("code", "R"));
    dynamic.put(
        "configuration",
        Map.of("profile_validator_factory_class_arguments", Map.of("extension_option", 1)));
    assertDoesNotThrow(() -> new JsonValidator(null, dynamic));
  }
}
