package org.metabolomicshub.jsonprofile.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.validator.checkers.CustomConstraintCheckers;
import org.metabolomicshub.jsonprofile.validator.checkers.DefaultConstraintCheckers;

public class DefaultProfileValidator extends ProfileValidator {
  private record CheckerKey(String type, String name) {
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof CheckerKey that)) {
        return false;
      }
      return Objects.equals(type, that.type) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, name);
    }
  }

  private final Map<CheckerKey, ConstraintChecker> checkers = new HashMap<>();

  public DefaultProfileValidator(ProfileValidatorFactory factory, String id) {
    super(factory, id != null ? id : "default");
    DefaultConstraintCheckers.registerAll(this);
    CustomConstraintCheckers.registerAll(this);
  }

  @Override
  public ConstraintChecker getChecker(Constraint constraint) {
    return getCheckerByName(constraint.getType(), constraint.getName());
  }

  @Override
  public ConstraintChecker getCheckerByName(String constraintType, String constraintName) {
    String normName = (constraintName != null && !constraintName.isEmpty()) ? constraintName : null;
    ConstraintChecker checker = checkers.get(new CheckerKey(constraintType, normName));
    if (checker == null && normName != null && !"custom".equals(constraintType)) {
      checker = checkers.get(new CheckerKey(constraintType, null));
    }
    if (checker == null) {
      throw new IllegalArgumentException(
          "There is no checker class for '" + constraintType + ", " + constraintName + "'");
    }
    return checker;
  }

  @Override
  public void registerChecker(
      String constraintType, String constraintName, ConstraintChecker checker) {
    String normName = (constraintName != null && !constraintName.isEmpty()) ? constraintName : null;
    checkers.put(new CheckerKey(constraintType, normName), checker);
  }
}
