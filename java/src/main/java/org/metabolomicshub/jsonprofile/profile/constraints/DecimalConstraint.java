package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class DecimalConstraint extends Constraint {
  @JsonProperty("minimum")
  private BigDecimal minimum;

  @JsonProperty("maximum")
  private BigDecimal maximum;

  @JsonProperty("allow_scientific_notation")
  private Boolean allowScientificNotation;

  @JsonProperty("min_scale")
  private Integer minScale;

  @JsonProperty("max_scale")
  private Integer maxScale;

  @JsonProperty("allow_non_finite_values")
  private Boolean allowNonFiniteValues;

  public DecimalConstraint() {
    super("decimal");
  }

  public BigDecimal getMinimum() {
    return minimum;
  }

  public void setMinimum(BigDecimal minimum) {
    this.minimum = minimum;
  }

  public BigDecimal getMaximum() {
    return maximum;
  }

  public void setMaximum(BigDecimal maximum) {
    this.maximum = maximum;
  }

  public Boolean getAllowScientificNotation() {
    return allowScientificNotation;
  }

  public boolean isAllowScientificNotation() {
    return !Boolean.FALSE.equals(allowScientificNotation);
  }

  public void setAllowScientificNotation(Boolean allowScientificNotation) {
    this.allowScientificNotation = allowScientificNotation;
  }

  public Integer getMinScale() {
    return minScale;
  }

  public void setMinScale(Integer minScale) {
    this.minScale = minScale;
  }

  public Integer getMaxScale() {
    return maxScale;
  }

  public void setMaxScale(Integer maxScale) {
    this.maxScale = maxScale;
  }

  public Boolean getAllowNonFiniteValues() {
    return allowNonFiniteValues;
  }

  public boolean isAllowNonFiniteValues() {
    return Boolean.TRUE.equals(allowNonFiniteValues);
  }

  public void setAllowNonFiniteValues(Boolean allowNonFiniteValues) {
    this.allowNonFiniteValues = allowNonFiniteValues;
  }
}
