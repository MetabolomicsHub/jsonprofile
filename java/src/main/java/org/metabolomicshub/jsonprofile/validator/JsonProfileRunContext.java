package org.metabolomicshub.jsonprofile.validator;

import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
import org.metabolomicshub.jsonprofile.profile.JsonProfileConfiguration;
import org.metabolomicshub.jsonprofile.profile.ValidationRuntimeConfiguration;

public class JsonProfileRunContext {
  private final ValidationRuntimeConfiguration runtimeConfig;
  private final JsonProfileConfiguration profileConfig;
  private final CvTermSearch cvTermSearch;
  private final ProfileValidatorFactory profileValidatorFactory;
  private final OpaEngineFactory opaEngineFactory;
  private final MessageCollector messageCollector;
  private final Map<String, JsonPath> jsonPathExpressions;
  private final Map<String, Object> customContextData = new HashMap<>();
  private final Map<String, Map<String, Object>> uniqueIdObjects;

  public JsonProfileRunContext(
      ValidationRuntimeConfiguration runtimeConfig,
      JsonProfileConfiguration profileConfig,
      CvTermSearch cvTermSearch,
      ProfileValidatorFactory profileValidatorFactory,
      OpaEngineFactory opaEngineFactory,
      MessageCollector messageCollector,
      Map<String, JsonPath> jsonPathExpressions,
      Map<String, Map<String, Object>> uniqueIdObjects) {
    this.runtimeConfig =
        runtimeConfig != null ? runtimeConfig : new ValidationRuntimeConfiguration();
    this.profileConfig = profileConfig != null ? profileConfig : new JsonProfileConfiguration();
    this.cvTermSearch = cvTermSearch;
    this.profileValidatorFactory = profileValidatorFactory;
    this.opaEngineFactory = opaEngineFactory;
    this.messageCollector = messageCollector != null ? messageCollector : new MessageCollector();
    this.jsonPathExpressions = jsonPathExpressions != null ? jsonPathExpressions : new HashMap<>();
    this.uniqueIdObjects = uniqueIdObjects != null ? uniqueIdObjects : new HashMap<>();
  }

  public ValidationRuntimeConfiguration getRuntimeConfig() {
    return runtimeConfig;
  }

  public JsonProfileConfiguration getProfileConfig() {
    return profileConfig;
  }

  public CvTermSearch getCvTermSearch() {
    return cvTermSearch;
  }

  public ProfileValidatorFactory getProfileValidatorFactory() {
    return profileValidatorFactory;
  }

  public OpaEngineFactory getOpaEngineFactory() {
    return opaEngineFactory;
  }

  public MessageCollector getMessageCollector() {
    return messageCollector;
  }

  public Map<String, JsonPath> getJsonPathExpressions() {
    return jsonPathExpressions;
  }

  public JsonPath compileOrGetJsonPath(String expr) {
    return jsonPathExpressions.computeIfAbsent(expr, JsonPath::compile);
  }

  public Map<String, Object> getCustomContextData() {
    return customContextData;
  }

  public Map<String, Map<String, Object>> getUniqueIdObjects() {
    return uniqueIdObjects;
  }
}
