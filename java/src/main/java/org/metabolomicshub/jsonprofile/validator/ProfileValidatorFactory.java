package org.metabolomicshub.jsonprofile.validator;

import org.metabolomicshub.jsonprofile.profile.ProfileValidatorDefinition;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;

public abstract class ProfileValidatorFactory {
  private final OpaEngineFactory opaEngineFactory;

  protected ProfileValidatorFactory(OpaEngineFactory opaEngineFactory) {
    this.opaEngineFactory = opaEngineFactory;
  }

  public OpaEngineFactory getOpaEngineFactory() {
    return opaEngineFactory;
  }

  public abstract ProfileValidator getValidatorByLabel(String label);

  public abstract ProfileValidator getValidatorById(String validatorId);

  public abstract ConstraintChecker getChecker(Constraint constraint);

  public abstract ConstraintChecker getCheckerByName(
      String constraintType, String constraintName, String validatorId);

  public abstract ProfileValidator registerProfileValidator(
      ProfileValidatorDefinition definition, boolean isDefault);
}
