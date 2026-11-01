package org.metabolomicshub.jsonprofile.profile.constraints;

public class NonNegativeIntegerConstraint extends IntegerConstraint {
  public NonNegativeIntegerConstraint() {
    super("non-negative-integer", 0L);
  }
}
