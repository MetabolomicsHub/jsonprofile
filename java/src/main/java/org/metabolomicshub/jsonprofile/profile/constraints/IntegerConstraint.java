package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntegerConstraint extends Constraint {
  @JsonProperty("minimum")
  private Long minimum;

  @JsonProperty("maximum")
  private Long maximum;

  public IntegerConstraint() {
    super("integer");
  }

  protected IntegerConstraint(String type, Long defaultMinimum) {
    super(type);
    this.minimum = defaultMinimum;
  }

  public Long getMinimum() {
    return minimum;
  }

  public void setMinimum(Long minimum) {
    this.minimum = minimum;
  }

  public Long getMaximum() {
    return maximum;
  }

  public void setMaximum(Long maximum) {
    this.maximum = maximum;
  }
}
