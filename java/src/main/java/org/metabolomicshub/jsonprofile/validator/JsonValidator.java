package org.metabolomicshub.jsonprofile.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.metabolomicshub.jsonprofile.profile.Category;
import org.metabolomicshub.jsonprofile.profile.EnforcedRequirement;
import org.metabolomicshub.jsonprofile.profile.EnforcementLevel;
import org.metabolomicshub.jsonprofile.profile.FieldRequirement;
import org.metabolomicshub.jsonprofile.profile.FieldRequirementGroup;
import org.metabolomicshub.jsonprofile.profile.JsonProfile;
import org.metabolomicshub.jsonprofile.profile.JsonProfileConfiguration;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.metabolomicshub.jsonprofile.profile.JsonProfileUtils;
import org.metabolomicshub.jsonprofile.profile.OpaFieldRequirement;
import org.metabolomicshub.jsonprofile.profile.ProfileValidatorDefinition;
import org.metabolomicshub.jsonprofile.profile.ProfileValidatorUtil;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;
import org.metabolomicshub.jsonprofile.profile.WasmFileDefinition;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.profile.constraints.DecimalConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonValidator {
  private static final Logger logger = LoggerFactory.getLogger(JsonValidator.class);

  private final JsonNode jsonSchemaNode;
  private final JsonSchema schemaValidator;
  private final Map<String, Object> referencedProfiles;
  private final JsonProfile jsonProfile;
  private final CvTermSearch defaultCvTermSearch;
  private final OpaEngineFactory opaEngineFactory;
  private final ProfileValidatorFactory profileValidatorFactory;
  private final Map<String, JsonPath> jsonPathExpressions = new HashMap<>();

  public JsonValidator(Object jsonSchema, Object profile) {
    this(jsonSchema, profile, null, null, null);
  }

  public JsonValidator(Object jsonSchema, Object profile, Map<String, Object> referencedProfiles) {
    this(jsonSchema, profile, referencedProfiles, null, null);
  }

  public JsonValidator(
      Object jsonSchema,
      Object profile,
      Map<String, Object> referencedProfiles,
      CvTermSearch defaultCvTermSearch,
      OpaEngineFactory opaEngineFactory) {
    if (jsonSchema != null) {
      this.jsonSchemaNode = loadJsonSchema(jsonSchema);
      this.schemaValidator = buildSchemaValidator(this.jsonSchemaNode);
    } else {
      this.jsonSchemaNode = null;
      this.schemaValidator = null;
      logger.warn("Json schema is not provided. Validator will skip json schema validation.");
    }

    this.referencedProfiles =
        referencedProfiles != null
            ? new LinkedHashMap<>(referencedProfiles)
            : new LinkedHashMap<>();
    this.jsonProfile = createProfile(profile);
    if (this.jsonProfile == null) {
      throw new IllegalArgumentException("Profile is not valid");
    }
    if (this.jsonProfile.getConfiguration() == null) {
      this.jsonProfile.setConfiguration(new JsonProfileConfiguration());
    }
    JsonProfileConfiguration config = this.jsonProfile.getConfiguration();
    this.defaultCvTermSearch =
        defaultCvTermSearch != null ? defaultCvTermSearch : new OlsCvTermSearch();
    this.opaEngineFactory =
        opaEngineFactory != null ? opaEngineFactory : createDefaultOpaEngineFactory();

    List<ProfileValidatorDefinition> customDefs =
        config.getCustomValidatorDefinitions() != null
            ? config.getCustomValidatorDefinitions()
            : List.of();
    String defaultValidatorId = null;
    if (config.getDefaultValidatorKey() != null) {
      for (ProfileValidatorDefinition def : customDefs) {
        if (config.getDefaultValidatorKey().equals(def.getLabel())) {
          defaultValidatorId = def.getValidatorId();
          break;
        }
      }
    }
    this.profileValidatorFactory =
        new DefaultProfileValidatorFactory(customDefs, defaultValidatorId, this.opaEngineFactory);
  }

  private static OpaEngineFactory createDefaultOpaEngineFactory() {
    try {
      Class.forName("com.dylibso.chicory.runtime.Instance");
      Class.forName("org.apache.commons.compress.archivers.tar.TarArchiveInputStream");
      return new LocalOpaEngineFactory();
    } catch (ClassNotFoundException ex) {
      return new NotImplementedOpaEngineFactory();
    }
  }

  public JsonProfile getJsonProfile() {
    return jsonProfile;
  }

  public JsonNode loadJsonSchema(Object jsonSchema) {
    try {
      if (jsonSchema instanceof String str) {
        return loadJsonSchema(Path.of(str));
      }
      if (jsonSchema instanceof Path path) {
        if (!Files.exists(path)) {
          throw new IllegalArgumentException("Json schema (" + path + ") file does not exist.");
        }
        return JsonProfileUtils.mapper().readTree(path.toFile());
      }
      if (jsonSchema instanceof JsonNode node) {
        return node;
      }
      if (jsonSchema instanceof Map<?, ?> map) {
        return JsonProfileUtils.mapper().valueToTree(map);
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException("Json schema load failure: " + ex.getMessage(), ex);
    }
    throw new IllegalArgumentException(
        "Json schema load failure. Please provide schema file path or Map");
  }

  private static JsonSchema buildSchemaValidator(JsonNode schemaNode) {
    SpecVersion.VersionFlag flag = SpecVersion.VersionFlag.V202012;
    if (schemaNode.has("$schema")) {
      String meta = schemaNode.get("$schema").asText("");
      if (meta.contains("draft-07")) {
        flag = SpecVersion.VersionFlag.V7;
      }
    }
    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(flag);
    return factory.getSchema(schemaNode);
  }

  public final JsonProfile createProfile(Object profile) {
    JsonProfile targetProfile = null;
    try {
      if (profile instanceof String str) {
        targetProfile = createProfile(Path.of(str));
      } else if (profile instanceof Path path) {
        if (!Files.exists(path)) {
          throw new IllegalArgumentException("Json profile (" + path + ") file does not exist.");
        }
        targetProfile =
            JsonProfileUtils.profileMapper().readValue(path.toFile(), JsonProfile.class);
      } else if (profile instanceof JsonProfile jp) {
        targetProfile = jp;
      } else if (profile instanceof JsonNode node) {
        targetProfile = JsonProfileUtils.profileMapper().treeToValue(node, JsonProfile.class);
      } else if (profile instanceof Map<?, ?> map) {
        targetProfile = JsonProfileUtils.profileMapper().convertValue(map, JsonProfile.class);
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException("Json profile load failure: " + ex.getMessage(), ex);
    }
    if (targetProfile == null) {
      throw new IllegalArgumentException(
          "Json profile load failure. Please provide profile file path, Map or JsonProfile object");
    }

    if (targetProfile.getExtends() != null && !targetProfile.getExtends().isEmpty()) {
      String parentId = targetProfile.getExtends();
      if (!referencedProfiles.containsKey(parentId)) {
        throw new IllegalArgumentException("Referenced Json profile " + parentId + " not found.");
      }
      Object parentSource = referencedProfiles.get(parentId);
      JsonProfile referencedProfile = createProfile(parentSource);
      referencedProfiles.put(referencedProfile.getId(), referencedProfile);
      return extendProfile(referencedProfile, targetProfile);
    }

    referencedProfiles.put(targetProfile.getId(), targetProfile);
    ProfileValidatorUtil.ProfileValidationResult check =
        ProfileValidatorUtil.validateProfile(targetProfile);
    if (!check.valid() && check.messages() != null) {
      for (JsonProfileMessage item : check.messages()) {
        logger.warn(
            "{}: {}-{}: {}",
            item.getCategory(),
            item.getSource(),
            item.getCode(),
            item.getMessage());
      }
    }
    return targetProfile;
  }

  public static JsonProfile extendProfile(JsonProfile parentProfile, JsonProfile inputProfile) {
    if (parentProfile == null) {
      if (inputProfile.getConfiguration() == null) {
        inputProfile.setConfiguration(new JsonProfileConfiguration());
      }
      return inputProfile;
    }
    Map<String, EnforcedRequirement> newRequirements =
        inputProfile.getRequirements() != null
            ? new LinkedHashMap<>(inputProfile.getRequirements())
            : new LinkedHashMap<>();
    Map<String, EnforcedRequirement> mergedRequirements =
        parentProfile.getRequirements() != null
            ? new LinkedHashMap<>(parentProfile.getRequirements())
            : new LinkedHashMap<>();

    if (inputProfile.getConfiguration() == null) {
      inputProfile.setConfiguration(
          parentProfile.getConfiguration() != null
              ? parentProfile.getConfiguration()
              : new JsonProfileConfiguration());
    } else {
      inputProfile.getConfiguration().mergeFromParent(parentProfile.getConfiguration());
    }

    for (Map.Entry<String, EnforcedRequirement> entry : newRequirements.entrySet()) {
      String key = entry.getKey();
      EnforcedRequirement val = entry.getValue();
      if (val == null) {
        mergedRequirements.remove(key);
      } else {
        mergedRequirements.put(key, val);
      }
    }
    inputProfile.setRequirements(mergedRequirements);
    return inputProfile;
  }

  public JsonValidationResult validateJsonFile(Path jsonFilePath) {
    return validateJsonFile(jsonFilePath, null);
  }

  public JsonValidationResult validateJsonFile(
      Path jsonFilePath, ValidationRuntimeConfiguration runtimeConfig) {
    if (jsonFilePath == null || !Files.exists(jsonFilePath)) {
      throw new IllegalArgumentException("Json file does not exist.");
    }
    try {
      Map<String, Object> inputJson =
          JsonProfileUtils.mapper()
              .readValue(jsonFilePath.toFile(), new TypeReference<Map<String, Object>>() {});
      return validateDict(inputJson, runtimeConfig);
    } catch (IOException ex) {
      throw new IllegalArgumentException("Failed to read JSON file: " + ex.getMessage(), ex);
    }
  }

  public JsonValidationResult validateDict(Map<String, Object> inputJson) {
    return validateDict(inputJson, null);
  }

  public JsonValidationResult validateDict(
      Map<String, Object> inputJson, ValidationRuntimeConfiguration runtimeConfig) {
    ValidationRuntimeConfiguration rt =
        runtimeConfig != null ? runtimeConfig : new ValidationRuntimeConfiguration();
    if (rt.getSkippedRequirements() == null) {
      rt.setSkippedRequirements(new ArrayList<>());
    }
    List<String> idFields =
        jsonProfile.getConfiguration() != null
            ? jsonProfile.getConfiguration().getObjectUniqueIdFields()
            : null;
    Map<String, Map<String, Object>> uniqueIdObjects = new LinkedHashMap<>();
    if (idFields != null && !idFields.isEmpty()) {
      updateUniqueObjectDict("$", inputJson, idFields, uniqueIdObjects);
    }

    JsonProfileRunContext context =
        new JsonProfileRunContext(
            rt,
            jsonProfile.getConfiguration(),
            defaultCvTermSearch,
            profileValidatorFactory,
            opaEngineFactory,
            new MessageCollector(rt.getMaxMessagesForEachRequirement()),
            jsonPathExpressions,
            uniqueIdObjects);

    if (!rt.isSkipJsonschemaValidation() && schemaValidator != null) {
      validateJsonWithSchema(inputJson, context);
    }
    if (rt.isSkipProfileValidation()) {
      return context.getMessageCollector().processMessages();
    }

    if (jsonProfile.getRequirements() != null) {
      for (Map.Entry<String, EnforcedRequirement> entry :
          jsonProfile.getRequirements().entrySet()) {
        String jsonPath =
            (entry.getKey() == null || entry.getKey().isEmpty()) ? "$" : entry.getKey();
        EnforcedRequirement reqDef = entry.getValue();
        if (reqDef == null) {
          continue;
        }
        FieldRequirementGroup group;
        if (reqDef instanceof FieldRequirementGroup g) {
          group = g;
        } else {
          group =
              new FieldRequirementGroup(reqDef.getCode(), reqDef.getDescription(), List.of(reqDef));
        }
        MessageCollector groupCollector = new MessageCollector();
        validateRequirement(group, jsonPath, inputJson, context, groupCollector);
        for (Map.Entry<String, Map<String, List<JsonProfileMessage>>> pathEntry :
            groupCollector.getMessages().entrySet()) {
          for (List<JsonProfileMessage> msgList : pathEntry.getValue().values()) {
            for (JsonProfileMessage msg : msgList) {
              context.getMessageCollector().addMessage(pathEntry.getKey(), msg);
            }
          }
        }
      }
    }

    return context.getMessageCollector().processMessages();
  }

  public void validateJsonWithSchema(Map<String, Object> jsonData, JsonProfileRunContext context) {
    JsonNode dataNode = JsonProfileUtils.mapper().valueToTree(jsonData);
    Set<ValidationMessage> errors = schemaValidator.validate(dataNode);
    List<ValidationMessage> sorted = new ArrayList<>(errors);
    sorted.sort(Comparator.comparing(ValidationMessage::toString));
    for (ValidationMessage err : sorted) {
      String jsonPath =
          JsonProfileUtils.instanceLocationToJsonPath(
              err.getInstanceLocation() != null ? err.getInstanceLocation().toString() : "$");
      context
          .getMessageCollector()
          .addMessage(
              jsonPath,
              new JsonProfileMessage(
                  "SCHEMA-0001",
                  jsonPath,
                  Category.SCHEMA,
                  "jsonschema",
                  "Json schema validation error: " + err.getMessage(),
                  EnforcementLevel.REQUIRED));
    }
  }

  @SuppressWarnings("unchecked")
  public void updateUniqueObjectDict(
      String objectPath,
      Object objectItem,
      List<String> idFields,
      Map<String, Map<String, Object>> objects) {
    if (objectItem instanceof List<?> list) {
      for (int i = 0; i < list.size(); i++) {
        updateUniqueObjectDict(objectPath + "[" + i + "]", list.get(i), idFields, objects);
      }
    } else if (objectItem instanceof Map<?, ?> rawMap) {
      Map<String, Object> map = (Map<String, Object>) rawMap;
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String fieldName = entry.getKey();
        Object val = entry.getValue();
        String itemPath = objectPath + "." + fieldName;
        if (idFields.contains(fieldName) && map.size() > 1) {
          if (val != null && !val.toString().isEmpty()) {
            objects.putIfAbsent(val.toString(), map);
          }
        }
        if (val instanceof Map<?, ?> || val instanceof List<?>) {
          updateUniqueObjectDict(itemPath, val, idFields, objects);
        }
      }
    }
  }

  private void validateRequirement(
      EnforcedRequirement fieldRequirement,
      String jsonPath,
      Map<String, Object> inputJson,
      JsonProfileRunContext context,
      MessageCollector groupCollector) {
    if (fieldRequirement instanceof OpaFieldRequirement opaReq) {
      validateOpaFieldRequirement(List.of(opaReq), jsonPath, inputJson, context, groupCollector);
      return;
    }
    if (fieldRequirement instanceof FieldRequirementGroup group) {
      MessageCollector subCollector = new MessageCollector();
      validateOpaFieldRequirements(group, jsonPath, inputJson, context, subCollector);
      for (EnforcedRequirement item : group.getRequirements()) {
        if (item instanceof OpaFieldRequirement) {
          continue;
        }
        validateRequirement(item, jsonPath, inputJson, context, subCollector);
      }

      Map<String, Map<String, List<JsonProfileMessage>>> instances = new LinkedHashMap<>();
      for (Map.Entry<String, Map<String, List<JsonProfileMessage>>> entry :
          subCollector.getMessages().entrySet()) {
        String sourceJsonPath = entry.getKey();
        instances.computeIfAbsent(sourceJsonPath, k -> new LinkedHashMap<>());
        for (Map.Entry<String, List<JsonProfileMessage>> codeEntry : entry.getValue().entrySet()) {
          String code = codeEntry.getKey();
          List<JsonProfileMessage> msgs = codeEntry.getValue();
          List<JsonProfileMessage> reqItems =
              msgs.stream()
                  .filter(x -> x.getEnforcementLevel() == EnforcementLevel.REQUIRED)
                  .toList();
          if (!reqItems.isEmpty()) {
            instances.get(sourceJsonPath).put(code, reqItems);
          }
          for (JsonProfileMessage nonReq : msgs) {
            if (nonReq.getEnforcementLevel() != EnforcementLevel.REQUIRED) {
              groupCollector.addMessage(nonReq.getSource(), nonReq);
            }
          }
        }
      }

      int totalReqs = group.getRequirements().size();
      for (Map.Entry<String, Map<String, List<JsonProfileMessage>>> instEntry :
          instances.entrySet()) {
        Map<String, List<JsonProfileMessage>> messageList = instEntry.getValue();
        int validReqs = totalReqs - messageList.size();
        boolean validGroup = (totalReqs == validReqs);
        if (!validGroup && (group.getMinValid() != null || group.getMaxValid() != null)) {
          int minBound = group.getMinValid() != null ? group.getMinValid() : 1;
          int maxBound = group.getMaxValid() != null ? group.getMaxValid() : totalReqs;
          if (validReqs >= minBound && validReqs <= maxBound) {
            validGroup = true;
          }
        }
        if (!validGroup) {
          for (List<JsonProfileMessage> items : messageList.values()) {
            for (JsonProfileMessage msg : items) {
              groupCollector.addMessage(msg.getSource(), msg);
            }
          }
        }
      }
      return;
    }

    if (!(fieldRequirement instanceof FieldRequirement req)) {
      return;
    }
    ValidationRuntimeConfiguration runtimeConfig = context.getRuntimeConfig();
    String constraintName =
        req.getValueConstraint() != null && req.getValueConstraint().getType() != null
            ? req.getValueConstraint().getType()
            : "field-requirement";

    if (req.getValueConstraint() instanceof DecimalConstraint
        && runtimeConfig.isSkipDecimalValidations()) {
      groupCollector.addMessage(
          jsonPath,
          new JsonProfileMessage(
              req.getCode(),
              jsonPath,
              Category.PROFILE,
              req.getValueConstraint().getType(),
              req.getCode() + " decimal validations are skipped for '" + jsonPath + "'",
              EnforcementLevel.RECOMMENDED));
      return;
    }

    JsonPath compiled = context.compileOrGetJsonPath(jsonPath);
    List<String> matchedPaths =
        "$".equals(jsonPath)
            ? List.of("$")
            : compiled.read(inputJson, ConstraintChecker.PATH_CONFIG);
    List<Object> matchedValues =
        "$".equals(jsonPath)
            ? List.of(inputJson)
            : compiled.read(inputJson, ConstraintChecker.VALUE_CONFIG);

    if (matchedPaths == null || matchedPaths.isEmpty()) {
      if (req.isMatchIsRequired()) {
        groupCollector.addMessage(
            jsonPath,
            new JsonProfileMessage(
                req.getCode(),
                jsonPath,
                Category.PROFILE,
                constraintName,
                "There is no value with json path '" + jsonPath + "'",
                req.getEnforcementLevel()));
      }
      return;
    }

    for (int i = 0; i < matchedPaths.size() && i < matchedValues.size(); i++) {
      if (groupCollector.isOpen(req.getCode())) {
        break;
      }
      String source = JsonProfileUtils.convertFullPath(matchedPaths.get(i));
      Object val = matchedValues.get(i);

      boolean hasPropChecks =
          (req.getRequiredProperties() != null && !req.getRequiredProperties().isEmpty())
              || (req.getRecommendedProperties() != null
                  && !req.getRecommendedProperties().isEmpty());
      if (hasPropChecks && !(val instanceof Map<?, ?>)) {
        groupCollector.addMessage(
            source,
            new JsonProfileMessage(
                req.getCode(),
                source,
                Category.PROFILE,
                "property-check",
                "Property check failed. '" + source + "' is not defined.",
                EnforcementLevel.REQUIRED));
      } else if (val instanceof Map<?, ?> mapVal) {
        if (req.getRequiredProperties() != null) {
          List<String> missingReq =
              req.getRequiredProperties().stream().filter(p -> !mapVal.containsKey(p)).toList();
          if (!missingReq.isEmpty()) {
            groupCollector.addMessage(
                source,
                new JsonProfileMessage(
                    req.getCode(),
                    source,
                    Category.PROFILE,
                    "property-check",
                    "Required property does not exist "
                        + String.join(", ", missingReq)
                        + " is not in object '"
                        + source
                        + "'",
                    EnforcementLevel.REQUIRED));
          }
        }
        if (req.getRecommendedProperties() != null) {
          List<String> missingRec =
              req.getRecommendedProperties().stream().filter(p -> !mapVal.containsKey(p)).toList();
          if (!missingRec.isEmpty()) {
            context
                .getMessageCollector()
                .addMessage(
                    source,
                    new JsonProfileMessage(
                        req.getCode(),
                        source,
                        Category.PROFILE,
                        "property-check",
                        "Recommended property does not exist. "
                            + String.join(", ", missingRec)
                            + " field(s) not in object '"
                            + source
                            + "'",
                        EnforcementLevel.RECOMMENDED));
          }
        }
      }

      if (req.getValueConstraint() != null) {
        Constraint constraint = req.getValueConstraint();
        ConstraintChecker checker = profileValidatorFactory.getChecker(constraint);
        boolean skip =
            runtimeConfig.isSkipDecimalValidations() && constraint instanceof DecimalConstraint;
        if (!skip) {
          if (constraint.getDescription() == null) {
            constraint.setDescription(req.getDescription());
          }
          ConstraintValidationResult res =
              checker.validateConstraint(constraint, val, inputJson, context);
          if (!res.isValid()) {
            groupCollector.addMessage(
                source,
                new JsonProfileMessage(
                    req.getCode(),
                    source,
                    Category.PROFILE,
                    constraint.getType() != null ? constraint.getType() : "field-requirement",
                    res.message(),
                    req.getEnforcementLevel()));
          }
        }
      }
    }
  }

  private void validateOpaFieldRequirements(
      FieldRequirementGroup group,
      String jsonPath,
      Map<String, Object> inputJson,
      JsonProfileRunContext context,
      MessageCollector groupCollector) {
    Map<String, List<OpaFieldRequirement>> byWasm = new LinkedHashMap<>();
    for (EnforcedRequirement item : group.getRequirements()) {
      if (!(item instanceof OpaFieldRequirement opaField)) {
        continue;
      }
      if (context.getRuntimeConfig().getSkippedRequirements().contains(opaField.getCode())) {
        String constraintName =
            opaField.getPolicyId() != null ? opaField.getPolicyId() : "field-requirement";
        groupCollector.addMessage(
            jsonPath,
            new JsonProfileMessage(
                opaField.getCode(),
                jsonPath,
                Category.PROFILE,
                constraintName,
                opaField.getCode() + " of '" + jsonPath + "' is in skipped list.",
                EnforcementLevel.RECOMMENDED));
        continue;
      }
      String key = opaField.getWasmFileKey() != null ? opaField.getWasmFileKey() : "default";
      byWasm.computeIfAbsent(key, k -> new ArrayList<>()).add(opaField);
    }
    for (List<OpaFieldRequirement> batch : byWasm.values()) {
      if (!batch.isEmpty()) {
        validateOpaFieldRequirement(batch, jsonPath, inputJson, context, groupCollector);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void validateOpaFieldRequirement(
      List<OpaFieldRequirement> opaReqs,
      String jsonPath,
      Map<String, Object> inputJson,
      JsonProfileRunContext context,
      MessageCollector groupCollector) {
    Map<String, OpaFieldRequirement> byId = new LinkedHashMap<>();
    for (OpaFieldRequirement r : opaReqs) {
      byId.put(r.getPolicyId(), r);
    }
    String label =
        opaReqs.get(0).getWasmFileKey() != null ? opaReqs.get(0).getWasmFileKey() : "default";
    JsonProfileConfiguration config = context.getProfileConfig();
    String wasmFilePath = null;
    String wasmDownloadUrl = null;
    String entrypoint = null;
    if (config != null && config.getWasmFileDefinitions() != null) {
      WasmFileDefinition opaCfg = config.getWasmFileDefinitions().get(label);
      if (opaCfg == null) {
        throw new IllegalArgumentException("OPA policy file not found");
      }
      wasmFilePath = opaCfg.getWasmFilePath();
      wasmDownloadUrl = opaCfg.getWasmFileDownloadUrl();
      entrypoint = opaCfg.getEntrypoint();
    }
    OpaEngine engine;
    try {
      engine =
          context
              .getProfileValidatorFactory()
              .getOpaEngineFactory()
              .getOpaEngine(wasmFilePath, wasmDownloadUrl);
    } catch (UnsupportedOperationException ex) {
      logger.warn("OPA engine not enabled; skipping OPA requirements for '{}'", jsonPath);
      return;
    }

    JsonPath expr = context.compileOrGetJsonPath(jsonPath);
    List<Object> found =
        "$".equals(jsonPath)
            ? List.of(inputJson)
            : expr.read(inputJson, ConstraintChecker.VALUE_CONFIG);
    Object subVal = found.isEmpty() ? null : (found.size() == 1 ? found.get(0) : found);

    List<String> policyIds = opaReqs.stream().map(OpaFieldRequirement::getPolicyId).toList();
    Map<String, Object> opaInput = new LinkedHashMap<>();
    opaInput.put("policy_ids", policyIds);
    opaInput.put("value", subVal);
    opaInput.put("root", inputJson);
    opaInput.put("config", config);
    opaInput.put("constraint", null);

    Object result = engine.evaluate(opaInput, entrypoint, null);
    if (result == null) {
      throw new IllegalArgumentException("OPA policy decision is not valid");
    }
    Map<String, Object> firstDecision = null;
    if (result instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Map<?, ?> m) {
      firstDecision = (Map<String, Object>) m;
    } else if (result instanceof Map<?, ?> m) {
      firstDecision = (Map<String, Object>) m;
    }
    if (firstDecision == null) {
      throw new IllegalArgumentException("OPA policy decision is empty.");
    }
    Object msgsObj = firstDecision.get("messages");
    if (msgsObj instanceof List<?> msgsList) {
      for (Object itemObj : msgsList) {
        if (itemObj instanceof Map<?, ?> itemMap) {
          String pid = itemMap.get("policy_id") != null ? itemMap.get("policy_id").toString() : "";
          String source =
              itemMap.get("source") != null ? itemMap.get("source").toString() : jsonPath;
          String msgText = itemMap.get("message") != null ? itemMap.get("message").toString() : "";
          OpaFieldRequirement req = byId.get(pid);
          boolean added =
              groupCollector.addMessage(
                  jsonPath,
                  new JsonProfileMessage(
                      req != null ? req.getCode() : "",
                      source,
                      Category.PROFILE,
                      "opa-field-requirement",
                      msgText,
                      req != null ? req.getEnforcementLevel() : EnforcementLevel.REQUIRED));
          if (!added) {
            break;
          }
        }
      }
    }
  }
}
