package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileValidatorDefinition {
  @JsonProperty("label")
  private String label;

  @JsonProperty("validator_id")
  private String validatorId;

  @JsonProperty("profile_validator_class")
  private Map<String, String> profileValidatorClass;

  @JsonProperty("description")
  private String description;

  public ProfileValidatorDefinition() {}

  public ProfileValidatorDefinition(
      String label,
      String validatorId,
      Map<String, String> profileValidatorClass,
      String description) {
    this.label = label;
    this.validatorId = validatorId;
    this.profileValidatorClass = profileValidatorClass;
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValidatorId() {
    return validatorId;
  }

  public void setValidatorId(String validatorId) {
    this.validatorId = validatorId;
  }

  public Map<String, String> getProfileValidatorClass() {
    return profileValidatorClass;
  }

  public void setProfileValidatorClass(Map<String, String> profileValidatorClass) {
    this.profileValidatorClass = profileValidatorClass;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
