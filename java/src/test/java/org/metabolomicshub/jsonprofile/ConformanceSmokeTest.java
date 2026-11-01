package org.metabolomicshub.jsonprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.JsonProfileUtils;
import org.metabolomicshub.jsonprofile.profile.ProfileValidatorUtil;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.validator.JsonValidationResult;
import org.metabolomicshub.jsonprofile.validator.JsonValidator;

class ConformanceSmokeTest {
  @Test
  void sharedConformanceManifestLoadsAndValidates() throws Exception {
    Path manifestPath = Path.of("..", "shared", "conformance", "smoke.json");
    ObjectMapper mapper = JsonProfileUtils.mapper();
    JsonNode manifest = mapper.readTree(manifestPath.toFile());

    assertEquals("0.1.0", manifest.get("version").asText());
    assertEquals(2, manifest.get("cases").size());

    for (JsonNode testCase : manifest.get("cases")) {
      String id = testCase.get("id").asText();
      boolean expectedValid = testCase.get("valid").asBoolean();
      Path fixturePath =
          manifestPath.getParent().resolve(testCase.get("fixture").asText()).normalize();
      ProfileValidatorUtil.ProfileValidationResult result =
          ProfileValidatorUtil.validateProfileFile(fixturePath);
      assertEquals(
          expectedValid, result.valid(), "Smoke case failed: " + id + " -> " + result.messages());
    }
  }

  @Test
  void sharedValidationConformanceSuitePasses() throws Exception {
    Path suitePath = Path.of("..", "shared", "conformance", "validation_suite.json");
    ObjectMapper mapper = JsonProfileUtils.mapper();
    Map<String, Object> suite =
        mapper.readValue(suitePath.toFile(), new TypeReference<Map<String, Object>>() {});

    assertEquals("0.1.0", suite.get("version"));
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> cases = (List<Map<String, Object>>) suite.get("cases");

    for (Map<String, Object> testCase : cases) {
      Map<String, Object> referenced = new HashMap<>();
      @SuppressWarnings("unchecked")
      Map<String, Object> parentProfile = (Map<String, Object>) testCase.get("parentProfile");
      if (parentProfile != null) {
        referenced.put((String) parentProfile.get("id"), parentProfile);
      }

      JsonValidator validator =
          new JsonValidator(testCase.get("schema"), testCase.get("profile"), referenced);

      ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
      rt.setOfflineMode(Boolean.TRUE.equals(testCase.get("offlineMode")));
      rt.setSkipJsonschemaValidation(testCase.get("schema") == null);

      @SuppressWarnings("unchecked")
      Map<String, Object> document = (Map<String, Object>) testCase.get("document");
      JsonValidationResult result = validator.validateDict(document, rt);

      Set<String> actualErrors = new HashSet<>();
      for (List<JsonProfileMessage> msgs : result.getErrors().values()) {
        for (JsonProfileMessage m : msgs) {
          actualErrors.add(m.getCode());
        }
      }

      Set<String> actualRecs = new HashSet<>();
      for (List<JsonProfileMessage> msgs : result.getRecommendations().values()) {
        for (JsonProfileMessage m : msgs) {
          actualRecs.add(m.getCode());
        }
      }

      @SuppressWarnings("unchecked")
      Set<String> expectedErrors = new HashSet<>((List<String>) testCase.get("expectedErrorCodes"));
      @SuppressWarnings("unchecked")
      Set<String> expectedRecs =
          new HashSet<>((List<String>) testCase.get("expectedRecommendationCodes"));

      String caseId = (String) testCase.get("id");
      assertEquals(expectedErrors, actualErrors, "Error code mismatch in case: " + caseId);
      assertEquals(expectedRecs, actualRecs, "Recommendation code mismatch in case: " + caseId);
    }
  }

  @Test
  void validatesAnnouncementExampleProfilesOffline() {
    Path baseDir = Path.of("..", "python", "src", "scripts", "example_01");
    Path schemaPath = baseDir.resolve("announcement-v1.0.schema.json");
    Path baseProfilePath = baseDir.resolve("announcement-v1.0.base-profile.json");
    Path legacyProfilePath = baseDir.resolve("announcement-v1.0.legacy-profile.json");
    Path msProfilePath = baseDir.resolve("announcement-v1.0.ms-profile.json");
    Path inputJsonPath = baseDir.resolve("example_input_01.json");

    Map<String, Object> referenced =
        Map.of(
            "https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.base-profile.json",
            baseProfilePath,
            "https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.legacy-profile.json",
            legacyProfilePath);

    JsonValidator validator = new JsonValidator(schemaPath, msProfilePath, referenced);
    assertNotNull(validator.getJsonProfile());

    ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
    rt.setOfflineMode(true);
    JsonValidationResult res = validator.validateJsonFile(inputJsonPath, rt);
    assertNotNull(res);
    assertFalse(res.getErrors().isEmpty(), "Expected validation errors on example_input_01.json");
  }

  @Test
  void validatesIsaJsonMetabolightsProfileOffline() {
    Path baseProfilePath =
        Path.of(
            "..", "shared", "resources", "isa-json", "profiles", "metabolights-profile-0.1.0.json");
    Path extendedProfilePath =
        Path.of(
            "..",
            "shared",
            "resources",
            "isa-json",
            "profiles",
            "metabolights-bh-extended-profile-0.1.0.json");
    Path inputJsonPath =
        Path.of("..", "shared", "resources", "isa-json", "metabolights-input-isa.json");

    String baseId = "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json";
    String extendedId =
        "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json";
    Map<String, Object> referenced =
        Map.of(
            baseId, baseProfilePath,
            extendedId, extendedProfilePath);

    ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
    rt.setOfflineMode(true);
    rt.setSkipJsonschemaValidation(true);

    JsonValidator baseValidator = new JsonValidator(null, baseProfilePath, referenced);
    JsonValidationResult baseRes = baseValidator.validateJsonFile(inputJsonPath, rt);
    assertNotNull(baseRes);
    Set<String> expectedBaseErrors =
        Set.of(
            "$.studies[0].assays[0].measurementType",
            "$.studies[0].characteristicCategories[2].characteristicType",
            "$.studies[0].description",
            "$.studies[0].filename",
            "$.studies[0].identifier",
            "$.studies[0].materials.sources[0]",
            "$.studies[0].materials.sources[1]",
            "$.studies[0].protocols[12].description",
            "$.studies[0].protocols[13].description",
            "$.studies[0].protocols[14].description",
            "$.studies[0].protocols[2].description",
            "$.studies[0].protocols[3].description",
            "$.studies[0].studyDesignDescriptors");
    Set<String> expectedIsaRecs =
        Set.of(
            "$.studies[0].protocols[10].protocolType",
            "$.studies[0].protocols[11].protocolType",
            "$.studies[0].protocols[12].protocolType",
            "$.studies[0].protocols[8].protocolType",
            "$.studies[0].protocols[9].protocolType");
    assertEquals(expectedBaseErrors, baseRes.getErrors().keySet());
    assertEquals(expectedIsaRecs, baseRes.getRecommendations().keySet());
    assertTrue(baseRes.getOptionals().isEmpty());

    JsonValidator extValidator = new JsonValidator(null, extendedProfilePath, referenced);
    JsonValidationResult extRes = extValidator.validateJsonFile(inputJsonPath, rt);
    assertNotNull(extRes);
    Set<String> expectedExtErrors =
        Set.of(
            "$.studies[0].assays[0].measurementType",
            "$.studies[0].characteristicCategories[2].characteristicType",
            "$.studies[0].description",
            "$.studies[0].materials.sources[0]",
            "$.studies[0].materials.sources[1]");
    assertEquals(expectedExtErrors, extRes.getErrors().keySet());
    assertEquals(expectedIsaRecs, extRes.getRecommendations().keySet());
    assertTrue(extRes.getOptionals().isEmpty());
  }

  @Test
  void validatesMassSpectrometryProfilesOffline() {
    Path mztabDir = Path.of("..", "shared", "resources", "mztab-m");
    Path profilesDir = mztabDir.resolve("profiles");
    Path schemaPath = mztabDir.resolve("mztabm-2.1.0-M.schema.json");
    Path inputJsonPath = mztabDir.resolve("manual_null_MTBLS263.mztab.json");

    String defaultId =
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";
    String mtdSmfId =
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_smf-profile-2.1.0-M.json";
    String mtdSmlId =
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_sml-profile-2.1.0-M.json";
    String fullId =
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-full-profile-2.1.0-M.json";
    String mtblsId =
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json";

    Map<String, Object> referenced =
        Map.of(
            defaultId, profilesDir.resolve("mztabm-default-profile-2.1.0-M.json"),
            mtdSmfId, profilesDir.resolve("mztabm-mtd_smf-profile-2.1.0-M.json"),
            mtdSmlId, profilesDir.resolve("mztabm-mtd_sml-profile-2.1.0-M.json"),
            fullId, profilesDir.resolve("mztabm-full-profile-2.1.0-M.json"),
            mtblsId, profilesDir.resolve("mztabm-metabolights-profile-2.1.0-M.json"));

    ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
    rt.setOfflineMode(true);

    for (String profileId : List.of(defaultId, mtdSmfId, mtdSmlId, fullId, mtblsId)) {
      Path profilePath = (Path) referenced.get(profileId);
      ProfileValidatorUtil.ProfileValidationResult profileCheck =
          ProfileValidatorUtil.validateProfileFile(profilePath);
      assertTrue(
          profileCheck.valid(),
          "Profile should be valid: " + profileId + " -> " + profileCheck.messages());

      JsonValidator validator = new JsonValidator(schemaPath, profilePath, referenced);
      JsonValidationResult res = validator.validateJsonFile(inputJsonPath, rt);
      assertNotNull(res);
      assertTrue(res.getOptionals().isEmpty(), "Expected 0 optionals for " + profileId);
      if (mtdSmlId.equals(profileId)) {
        assertTrue(
            res.getErrors().isEmpty(),
            "Expected no errors for mtd_sml profile, got: " + res.getErrors());
        assertEquals(
            12,
            res.getRecommendations().size(),
            "Expected 12 recommendations for mtd_sml, got: " + res.getRecommendations().keySet());
      } else if (mtblsId.equals(profileId)) {
        assertEquals(
            Set.of("$.metadata.contact", "$.metadata.sample[0].species[0]"),
            res.getErrors().keySet());
        assertTrue(
            res.getRecommendations().isEmpty(),
            "Expected 0 recommendations for metabolights profile, got: "
                + res.getRecommendations().keySet());
      } else {
        assertEquals(
            20,
            res.getErrors().size(),
            "Expected 20 error paths for " + profileId + ", got: " + res.getErrors().keySet());
        assertEquals(
            12,
            res.getRecommendations().size(),
            "Expected 12 recommendation paths for "
                + profileId
                + ", got: "
                + res.getRecommendations().keySet());
        assertTrue(res.getErrors().containsKey("$.smallMoleculeFeature[81].charge"));
        assertTrue(res.getErrors().containsKey("$.smallMoleculeEvidence[81].charge"));
        assertTrue(res.getRecommendations().containsKey("$.metadata.contact"));
        assertTrue(res.getRecommendations().containsKey("$.metadata.sample[0].species[0]"));
        assertTrue(res.getRecommendations().containsKey("$.metadata.ms_run[1].instrument_ref"));
      }
    }
  }

  @Test
  void supportsOptionalOpaEngineRequirements() {
    Map<String, Object> profile =
        Map.of(
            "id", "https://example.org/profiles/opa-test.json",
            "version", "1.0.0",
            "name", "OPA Test Profile",
            "configuration",
                Map.of(
                    "wasm_file_definitions",
                    Map.of(
                        "default",
                        Map.of(
                            "wasm_file_path", "dummy.wasm",
                            "wasm_file_download_url", "https://example.org/dummy.wasm",
                            "entrypoint", "test/policies"))),
            "requirements",
                Map.of(
                    "$.item",
                    Map.of(
                        "code", "OPA-0001",
                        "policy_id", "policy_0001",
                        "enforcement_level", "required")));

    org.metabolomicshub.jsonprofile.validator.OpaEngineFactory mockOpaFactory =
        (wasmPath, url) ->
            (inputData, ep, data) ->
                List.of(
                    Map.of(
                        "messages",
                        List.of(
                            Map.of(
                                "policy_id", "policy_0001",
                                "source", "$.item",
                                "message", "OPA policy failed"))));

    JsonValidator validator = new JsonValidator(null, profile, null, null, mockOpaFactory);
    ValidationRuntimeConfiguration rt = new ValidationRuntimeConfiguration();
    rt.setSkipJsonschemaValidation(true);

    JsonValidationResult res = validator.validateDict(Map.of("item", "bad"), rt);
    assertTrue(res.getErrors().containsKey("$.item"));
    assertEquals("OPA-0001", res.getErrors().get("$.item").get(0).getCode());
  }
}
