package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationRuntimeConfiguration {
  @JsonProperty("offline_mode")
  private Boolean offlineMode;

  @JsonProperty("skipped_requirements")
  private List<String> skippedRequirements = new ArrayList<>();

  @JsonProperty("skip_jsonschema_validation")
  private Boolean skipJsonschemaValidation;

  @JsonProperty("skip_profile_validation")
  private Boolean skipProfileValidation;

  @JsonProperty("max_messages_for_each_requirement")
  private Integer maxMessagesForEachRequirement = 10;

  @JsonProperty("skip_decimal_validations")
  private Boolean skipDecimalValidations;

  @JsonProperty("cv_term_search_class")
  private String cvTermSearchClass;

  public ValidationRuntimeConfiguration() {}

  public Boolean getOfflineMode() {
    return offlineMode;
  }

  public boolean isOfflineMode() {
    return Boolean.TRUE.equals(offlineMode);
  }

  public void setOfflineMode(Boolean offlineMode) {
    this.offlineMode = offlineMode;
  }

  public List<String> getSkippedRequirements() {
    return skippedRequirements;
  }

  public void setSkippedRequirements(List<String> skippedRequirements) {
    this.skippedRequirements =
        skippedRequirements != null ? skippedRequirements : new ArrayList<>();
  }

  public Boolean getSkipJsonschemaValidation() {
    return skipJsonschemaValidation;
  }

  public boolean isSkipJsonschemaValidation() {
    return Boolean.TRUE.equals(skipJsonschemaValidation);
  }

  public void setSkipJsonschemaValidation(Boolean skipJsonschemaValidation) {
    this.skipJsonschemaValidation = skipJsonschemaValidation;
  }

  public Boolean getSkipProfileValidation() {
    return skipProfileValidation;
  }

  public boolean isSkipProfileValidation() {
    return Boolean.TRUE.equals(skipProfileValidation);
  }

  public void setSkipProfileValidation(Boolean skipProfileValidation) {
    this.skipProfileValidation = skipProfileValidation;
  }

  public Integer getMaxMessagesForEachRequirement() {
    return maxMessagesForEachRequirement;
  }

  public void setMaxMessagesForEachRequirement(Integer maxMessagesForEachRequirement) {
    this.maxMessagesForEachRequirement = maxMessagesForEachRequirement;
  }

  public Boolean getSkipDecimalValidations() {
    return skipDecimalValidations;
  }

  public boolean isSkipDecimalValidations() {
    return Boolean.TRUE.equals(skipDecimalValidations);
  }

  public void setSkipDecimalValidations(Boolean skipDecimalValidations) {
    this.skipDecimalValidations = skipDecimalValidations;
  }

  public String getCvTermSearchClass() {
    return cvTermSearchClass;
  }

  public void setCvTermSearchClass(String cvTermSearchClass) {
    this.cvTermSearchClass = cvTermSearchClass;
  }
}
