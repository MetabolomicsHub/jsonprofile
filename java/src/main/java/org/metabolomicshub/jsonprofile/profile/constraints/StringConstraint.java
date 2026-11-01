package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StringConstraint extends Constraint {
  @JsonProperty("minimum")
  private Integer minimum;

  @JsonProperty("maximum")
  private Integer maximum;

  public StringConstraint() {
    super("string-length");
  }

  public Integer getMinimum() {
    return minimum;
  }

  public void setMinimum(Integer minimum) {
    this.minimum = minimum;
  }

  public Integer getMaximum() {
    return maximum;
  }

  public void setMaximum(Integer maximum) {
    this.maximum = maximum;
  }
}
