package org.metabolomicshub.jsonprofile.profile.constraints;

public class PositiveIntegerConstraint extends IntegerConstraint {
  public PositiveIntegerConstraint() {
    super("positive-integer", 1L);
  }
}
