package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class FieldRequirement extends EnforcedRequirement {
  @JsonProperty("value_constraint")
  private Constraint valueConstraint;

  @JsonProperty("match_is_required")
  private Boolean matchIsRequired;

  @JsonProperty("required_properties")
  private List<String> requiredProperties;

  @JsonProperty("recommended_properties")
  private List<String> recommendedProperties;

  public FieldRequirement() {}

  public Constraint getValueConstraint() {
    return valueConstraint;
  }

  public void setValueConstraint(Constraint valueConstraint) {
    this.valueConstraint = valueConstraint;
  }

  public Boolean getMatchIsRequired() {
    return matchIsRequired;
  }

  public boolean isMatchIsRequired() {
    return Boolean.TRUE.equals(matchIsRequired);
  }

  public void setMatchIsRequired(Boolean matchIsRequired) {
    this.matchIsRequired = matchIsRequired;
  }

  public List<String> getRequiredProperties() {
    return requiredProperties;
  }

  public void setRequiredProperties(List<String> requiredProperties) {
    this.requiredProperties = requiredProperties;
  }

  public List<String> getRecommendedProperties() {
    return recommendedProperties;
  }

  public void setRecommendedProperties(List<String> recommendedProperties) {
    this.recommendedProperties = recommendedProperties;
  }
}
