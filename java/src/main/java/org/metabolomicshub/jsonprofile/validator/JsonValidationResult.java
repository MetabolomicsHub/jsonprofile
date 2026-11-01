package org.metabolomicshub.jsonprofile.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;

public class JsonValidationResult {
  @JsonProperty("errors")
  private Map<String, List<JsonProfileMessage>> errors = new LinkedHashMap<>();

  @JsonProperty("recommendations")
  private Map<String, List<JsonProfileMessage>> recommendations = new LinkedHashMap<>();

  @JsonProperty("optionals")
  private Map<String, List<JsonProfileMessage>> optionals = new LinkedHashMap<>();

  public JsonValidationResult() {}

  public JsonValidationResult(
      Map<String, List<JsonProfileMessage>> errors,
      Map<String, List<JsonProfileMessage>> recommendations,
      Map<String, List<JsonProfileMessage>> optionals) {
    this.errors = errors != null ? errors : new LinkedHashMap<>();
    this.recommendations = recommendations != null ? recommendations : new LinkedHashMap<>();
    this.optionals = optionals != null ? optionals : new LinkedHashMap<>();
  }

  public Map<String, List<JsonProfileMessage>> getErrors() {
    return errors;
  }

  public void setErrors(Map<String, List<JsonProfileMessage>> errors) {
    this.errors = errors;
  }

  public Map<String, List<JsonProfileMessage>> getRecommendations() {
    return recommendations;
  }

  public void setRecommendations(Map<String, List<JsonProfileMessage>> recommendations) {
    this.recommendations = recommendations;
  }

  public Map<String, List<JsonProfileMessage>> getOptionals() {
    return optionals;
  }

  public void setOptionals(Map<String, List<JsonProfileMessage>> optionals) {
    this.optionals = optionals;
  }
}
