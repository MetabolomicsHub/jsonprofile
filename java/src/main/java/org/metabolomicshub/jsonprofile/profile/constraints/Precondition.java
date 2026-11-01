package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Precondition {
  @JsonProperty("evaluations")
  private List<Evaluation> evaluations;

  @JsonProperty("join_operator")
  private String joinOperator = "and";

  @JsonProperty("min_valid")
  private Integer minValid;

  @JsonProperty("max_valid")
  private Integer maxValid;

  public Precondition() {}

  public List<Evaluation> getEvaluations() {
    return evaluations;
  }

  public void setEvaluations(List<Evaluation> evaluations) {
    this.evaluations = evaluations;
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
