package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalConstraint extends Constraint {
  private static final BigInteger MAX_COEFFICIENT =
      BigInteger.ONE.shiftLeft(96).subtract(BigInteger.ONE);
  private static final int MAX_SCALE = 28;

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
    ensureSharedRange(minimum);
    this.minimum = minimum;
  }

  public BigDecimal getMaximum() {
    return maximum;
  }

  public void setMaximum(BigDecimal maximum) {
    ensureSharedRange(maximum);
    this.maximum = maximum;
  }

  public static boolean fitsSharedRange(BigDecimal value) {
    if (value == null) {
      return true;
    }
    int scale = value.scale();
    if (scale > MAX_SCALE) {
      return false;
    }
    BigInteger coefficient = value.unscaledValue().abs();
    if (scale < 0 && coefficient.signum() != 0) {
      long shift = -(long) scale;
      if (coefficient.toString().length() + shift > MAX_COEFFICIENT.toString().length()) {
        return false;
      }
      coefficient = coefficient.multiply(BigInteger.TEN.pow((int) shift));
    }
    return coefficient.compareTo(MAX_COEFFICIENT) <= 0;
  }

  private static void ensureSharedRange(BigDecimal value) {
    if (!fitsSharedRange(value)) {
      throw new IllegalArgumentException(
          "decimal bounds must fit a 96-bit coefficient and scale <= 28");
    }
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
