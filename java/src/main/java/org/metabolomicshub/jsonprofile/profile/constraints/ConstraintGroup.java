package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ConstraintGroup extends Constraint {
  @JsonProperty("constraints")
  private List<Constraint> constraints;

  @JsonProperty("join_operator")
  private String joinOperator = "and";

  @JsonProperty("min_valid")
  private Integer minValid;

  @JsonProperty("max_valid")
  private Integer maxValid;

  public ConstraintGroup() {
    super("group");
  }

  public List<Constraint> getConstraints() {
    return constraints;
  }

  public void setConstraints(List<Constraint> constraints) {
    this.constraints = constraints;
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
}
