package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BooleanConstraint extends Constraint {
  @JsonProperty("exceptional_false_values")
  private List<String> exceptionalFalseValues;

  @JsonProperty("exceptional_true_values")
  private List<String> exceptionalTrueValues;

  public BooleanConstraint() {
    super("boolean");
  }

  public List<String> getExceptionalFalseValues() {
    return exceptionalFalseValues;
  }

  public void setExceptionalFalseValues(List<String> exceptionalFalseValues) {
    this.exceptionalFalseValues = exceptionalFalseValues;
  }

  public List<String> getExceptionalTrueValues() {
    return exceptionalTrueValues;
  }

  public void setExceptionalTrueValues(List<String> exceptionalTrueValues) {
    this.exceptionalTrueValues = exceptionalTrueValues;
  }
}
