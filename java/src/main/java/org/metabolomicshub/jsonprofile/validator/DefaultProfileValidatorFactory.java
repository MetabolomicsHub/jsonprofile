package org.metabolomicshub.jsonprofile.validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.metabolomicshub.jsonprofile.profile.ProfileValidatorDefinition;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;

public class DefaultProfileValidatorFactory extends ProfileValidatorFactory {
  private final Map<String, ProfileValidator> profileValidators = new HashMap<>();
  private final Map<String, String> validatorIdsByLabel = new HashMap<>();
  private String defaultProfileValidatorId;

  public DefaultProfileValidatorFactory(
      List<ProfileValidatorDefinition> customValidatorDefinitions,
      String defaultProfileValidatorId,
      OpaEngineFactory opaEngineFactory) {
    super(opaEngineFactory);
    DefaultProfileValidator base = new DefaultProfileValidator(this, "default");
    profileValidators.put(base.getId(), base);
    validatorIdsByLabel.put("", base.getId());
    this.defaultProfileValidatorId =
        defaultProfileValidatorId != null ? defaultProfileValidatorId : base.getId();

    if (customValidatorDefinitions != null) {
      for (ProfileValidatorDefinition def : customValidatorDefinitions) {
        boolean isDef = this.defaultProfileValidatorId.equals(def.getValidatorId());
        registerProfileValidator(def, isDef);
      }
    }
  }

  @Override
  public ProfileValidator getValidatorByLabel(String label) {
    String validatorId =
        (label == null || label.isEmpty())
            ? defaultProfileValidatorId
            : validatorIdsByLabel.getOrDefault(label, label);
    return getValidatorById(validatorId);
  }

  @Override
  public ProfileValidator getValidatorById(String validatorId) {
    String targetId =
        (validatorId == null || validatorId.isEmpty()) ? defaultProfileValidatorId : validatorId;
    return profileValidators.get(targetId);
  }

  @Override
  public ConstraintChecker getChecker(Constraint constraint) {
    ProfileValidator validator = getValidatorById(constraint.getValidator());
    if (validator == null) {
      throw new IllegalArgumentException("Validator is not found for " + constraint.getValidator());
    }
    return validator.getChecker(constraint);
  }

  @Override
  public ConstraintChecker getCheckerByName(
      String constraintType, String constraintName, String validatorId) {
    ProfileValidator validator = getValidatorById(validatorId);
    if (validator == null) {
      throw new IllegalArgumentException("Validator is not found for " + validatorId);
    }
    return validator.getCheckerByName(constraintType, constraintName);
  }

  @Override
  public ProfileValidator registerProfileValidator(
      ProfileValidatorDefinition definition, boolean isDefault) {
    Map<String, String> clsMap = definition.getProfileValidatorClass();
    String javaClass = clsMap != null ? clsMap.get("java") : null;
    ProfileValidator instance;
    if (javaClass != null && !javaClass.isEmpty()) {
      try {
        Class<?> clazz = Class.forName(javaClass);
        try {
          instance =
              (ProfileValidator)
                  clazz
                      .getConstructor(ProfileValidatorFactory.class, String.class)
                      .newInstance(this, definition.getValidatorId());
        } catch (NoSuchMethodException e) {
          instance = (ProfileValidator) clazz.getConstructor().newInstance();
        }
      } catch (Exception ex) {
        throw new IllegalArgumentException(
            "Failed to load Java ProfileValidator: " + javaClass, ex);
      }
    } else {
      instance = new DefaultProfileValidator(this, definition.getValidatorId());
    }
    profileValidators.put(definition.getValidatorId(), instance);
    if (definition.getLabel() != null) {
      validatorIdsByLabel.put(definition.getLabel(), definition.getValidatorId());
    }
    if (isDefault) {
      this.defaultProfileValidatorId = definition.getValidatorId();
    }
    return instance;
  }
}
