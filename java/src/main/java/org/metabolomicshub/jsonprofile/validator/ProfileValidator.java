package org.metabolomicshub.jsonprofile.validator;

import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;

public abstract class ProfileValidator {
  private final ProfileValidatorFactory profileValidatorFactory;
  private final String id;

  protected ProfileValidator(ProfileValidatorFactory profileValidatorFactory, String id) {
    this.profileValidatorFactory = profileValidatorFactory;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public ProfileValidatorFactory getProfileValidatorFactory() {
    return profileValidatorFactory;
  }

  public abstract ConstraintChecker getChecker(Constraint constraint);

  public abstract ConstraintChecker getCheckerByName(String constraintType, String constraintName);

  public abstract void registerChecker(
      String constraintType, String constraintName, ConstraintChecker checker);
}
