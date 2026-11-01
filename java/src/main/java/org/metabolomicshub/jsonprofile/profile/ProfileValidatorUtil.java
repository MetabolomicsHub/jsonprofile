package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.profile.constraints.Evaluation;

public final class ProfileValidatorUtil {
  private static final ObjectMapper STRICT_MAPPER =
      JsonProfileUtils.mapper().copy().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

  private ProfileValidatorUtil() {}

  public record ProfileValidationResult(boolean valid, List<JsonProfileMessage> messages) {}

  public static ProfileValidationResult validateProfileFile(Path filePath) {
    if (filePath == null || !Files.exists(filePath)) {
      return new ProfileValidationResult(
          false,
          List.of(
              new JsonProfileMessage(
                  "",
                  null,
                  Category.CROSS_CHECK,
                  null,
                  "Profile file path is not defined.",
                  EnforcementLevel.REQUIRED)));
    }
    try {
      JsonNode node = STRICT_MAPPER.readTree(filePath.toFile());
      return validateProfileNode(node);
    } catch (Exception ex) {
      return new ProfileValidationResult(
          false,
          List.of(
              new JsonProfileMessage(
                  "",
                  null,
                  Category.CROSS_CHECK,
                  null,
                  "Profile file is not valid json file: " + ex.getMessage(),
                  EnforcementLevel.REQUIRED)));
    }
  }

  public static ProfileValidationResult validateProfileNode(JsonNode node) {
    try {
      if (node == null || !node.isObject()) {
        return new ProfileValidationResult(
            false,
            List.of(
                new JsonProfileMessage(
                    "",
                    null,
                    Category.CROSS_CHECK,
                    null,
                    "Profile json file format is not valid: root must be object",
                    EnforcementLevel.REQUIRED)));
      }
      JsonProfile profile = STRICT_MAPPER.treeToValue(node, JsonProfile.class);
      if (profile.getId() == null
          || profile.getId().isBlank()
          || profile.getName() == null
          || profile.getName().isBlank()
          || profile.getVersion() == null
          || profile.getVersion().isBlank()) {
        return new ProfileValidationResult(
            false,
            List.of(
                new JsonProfileMessage(
                    "",
                    null,
                    Category.CROSS_CHECK,
                    null,
                    "Profile json file format is not valid: missing required id, name, or version",
                    EnforcementLevel.REQUIRED)));
      }
      return validateProfile(profile);
    } catch (Exception ex) {
      return new ProfileValidationResult(
          false,
          List.of(
              new JsonProfileMessage(
                  "",
                  null,
                  Category.CROSS_CHECK,
                  null,
                  "Profile json file format is not valid: " + ex.getMessage(),
                  EnforcementLevel.REQUIRED)));
    }
  }

  public static ProfileValidationResult validateProfile(JsonProfile profile) {
    List<JsonProfileMessage> messages = new ArrayList<>();
    Set<String> validatedJsonPaths = new HashSet<>();
    Set<String> uniqueRequirementCodes = new HashSet<>();

    if (profile.getRequirements() != null) {
      for (Map.Entry<String, EnforcedRequirement> entry : profile.getRequirements().entrySet()) {
        String key = entry.getKey();
        EnforcedRequirement definition = entry.getValue();
        if (definition == null) {
          continue;
        }
        List<EnforcedRequirement> reqList;
        if (definition instanceof FieldRequirementGroup group) {
          reqList = group.getRequirements();
        } else {
          reqList = List.of(definition);
        }
        for (int idx = 0; idx < reqList.size(); idx++) {
          validateRequirement(
              key, "", reqList.get(idx), idx, messages, validatedJsonPaths, uniqueRequirementCodes);
        }
      }
    }
    boolean hasErrors =
        messages.stream().anyMatch(m -> m.getEnforcementLevel() == EnforcementLevel.REQUIRED);
    return new ProfileValidationResult(!hasErrors, messages);
  }

  private static void validateRequirement(
      String key,
      String subPath,
      EnforcedRequirement requirement,
      int requirementIdx,
      List<JsonProfileMessage> messages,
      Set<String> validatedJsonPaths,
      Set<String> uniqueRequirementCodes) {
    if (requirement instanceof FieldRequirementGroup group) {
      List<EnforcedRequirement> items = group.getRequirements();
      for (int idx = 0; idx < items.size(); idx++) {
        String nextPath =
            subPath == null || subPath.isEmpty()
                ? "requirements[" + idx + "]"
                : subPath + ".requirements[" + idx + "]";
        validateRequirement(
            key,
            nextPath,
            items.get(idx),
            idx,
            messages,
            validatedJsonPaths,
            uniqueRequirementCodes);
      }
      return;
    }

    String currentJsonPath = subPath != null ? subPath : "";
    if (requirement.getCode() == null || requirement.getCode().isEmpty()) {
      String msg = "Requirement '" + key + "' code or description is not defined.";
      messages.add(
          new JsonProfileMessage(
              "", currentJsonPath, Category.CROSS_CHECK, null, msg, EnforcementLevel.REQUIRED));
    } else {
      if (uniqueRequirementCodes.contains(requirement.getCode())) {
        String msg = "Requirement " + requirement.getCode() + " of '" + key + "' is not unique.";
        messages.add(
            new JsonProfileMessage(
                requirement.getCode(),
                currentJsonPath,
                Category.CROSS_CHECK,
                null,
                msg,
                EnforcementLevel.REQUIRED));
      }
      uniqueRequirementCodes.add(requirement.getCode());
    }

    if (requirement instanceof FieldRequirement fieldReq && fieldReq.getValueConstraint() != null) {
      Constraint constraint = fieldReq.getValueConstraint();
      if (constraint.getPrecondition() != null
          && constraint.getPrecondition().getEvaluations() != null) {
        List<Evaluation> evals = constraint.getPrecondition().getEvaluations();
        for (int i = 0; i < evals.size(); i++) {
          String jp = evals.get(i).getJsonPath();
          if (jp != null && !jp.isEmpty() && !validatedJsonPaths.contains(jp)) {
            try {
              JsonPath.compile(jp);
              validatedJsonPaths.add(jp);
            } catch (Exception ex) {
              messages.add(
                  new JsonProfileMessage(
                      requirement.getCode() != null ? requirement.getCode() : "",
                      currentJsonPath,
                      Category.CROSS_CHECK,
                      null,
                      "'"
                          + key
                          + "' requirement "
                          + requirement.getCode()
                          + " precondition evaluation (at index "
                          + i
                          + ") json path '"
                          + jp
                          + "' of is not valid.",
                      EnforcementLevel.REQUIRED));
            }
          }
        }
      }
      if (constraint.getJsonPath() != null && !constraint.getJsonPath().isEmpty()) {
        String jp = constraint.getJsonPath();
        if (!validatedJsonPaths.contains(jp)) {
          try {
            JsonPath.compile(jp);
            validatedJsonPaths.add(jp);
          } catch (Exception ex) {
            messages.add(
                new JsonProfileMessage(
                    requirement.getCode() != null ? requirement.getCode() : "",
                    currentJsonPath,
                    Category.CROSS_CHECK,
                    null,
                    "Requirement "
                        + requirement.getCode()
                        + " json path '"
                        + jp
                        + "' of '"
                        + key
                        + "' is not valid. ",
                    EnforcementLevel.REQUIRED));
          }
        }
      }
    }
  }
}
