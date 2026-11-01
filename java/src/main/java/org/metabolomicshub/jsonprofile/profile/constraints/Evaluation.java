package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Evaluation {
  @JsonProperty("root_value_evaluation")
  private Boolean rootValueEvaluation;

  @JsonProperty("json_path")
  private String jsonPath;

  @JsonProperty("is_reference_field")
  private Boolean isReferenceField;

  @JsonProperty("constraint")
  private Constraint constraint;

  @JsonProperty("default_evaluation")
  private Boolean defaultEvaluation;

  @JsonProperty("join_operator")
  private String joinOperator = "and";

  @JsonProperty("min_valid")
  private Integer minValid;

  @JsonProperty("max_valid")
  private Integer maxValid;

  @JsonProperty("negated")
  private Boolean negated;

  public Evaluation() {}

  public Boolean getRootValueEvaluation() {
    return rootValueEvaluation;
  }

  public boolean isRootValueEvaluation() {
    return Boolean.TRUE.equals(rootValueEvaluation);
  }

  public void setRootValueEvaluation(Boolean rootValueEvaluation) {
    this.rootValueEvaluation = rootValueEvaluation;
  }

  public String getJsonPath() {
    return jsonPath;
  }

  public void setJsonPath(String jsonPath) {
    this.jsonPath = jsonPath;
  }

  public Boolean getIsReferenceField() {
    return isReferenceField;
  }

  public boolean isReferenceField() {
    return Boolean.TRUE.equals(isReferenceField);
  }

  public void setIsReferenceField(Boolean isReferenceField) {
    this.isReferenceField = isReferenceField;
  }

  public Constraint getConstraint() {
    return constraint;
  }

  public void setConstraint(Constraint constraint) {
    this.constraint = constraint;
  }

  public Boolean getDefaultEvaluation() {
    return defaultEvaluation;
  }

  public void setDefaultEvaluation(Boolean defaultEvaluation) {
    this.defaultEvaluation = defaultEvaluation;
  }

  public String getJoinOperator() {
    return joinOperator != null ? joinOperator : "and";
  }

  public void setJoinOperator(String joinOperator) {
    this.joinOperator = joinOperator != null ? joinOperator : "and";
  }

  public Integer getMinValid() {
    return minValid;
  }

  public void setMinValid(Integer minValid) {
    this.minValid = minValid;
  }

  public Integer getMaxValid() {
    return maxValid;
  }

  public void setMaxValid(Integer maxValid) {
    this.maxValid = maxValid;
  }

  public Boolean getNegated() {
    return negated;
  }

  public boolean isNegated() {
    return Boolean.TRUE.equals(negated);
  }

  public void setNegated(Boolean negated) {
    this.negated = negated;
  }
}
