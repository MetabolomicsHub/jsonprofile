package org.metabolomicshub.jsonprofile.validator;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CustomConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.DecimalConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.Evaluation;

public abstract class ConstraintChecker {
  public static final Configuration VALUE_CONFIG =
      Configuration.defaultConfiguration()
          .addOptions(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);

  public static final Configuration PATH_CONFIG =
      Configuration.defaultConfiguration()
          .addOptions(Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS);

  public record CheckResult(boolean isValid, String message) {}

  public abstract CheckResult validate(
      Constraint constraint, Object value, Map<String, Object> root, JsonProfileRunContext context);

  public String formatMessage(Constraint constraint, String message) {
    String msg = message != null ? message.trim() : "";
    if (constraint != null
        && constraint.getDescription() != null
        && !constraint.getDescription().isEmpty()) {
      return constraint.getDescription() + ". " + msg;
    }
    return msg;
  }

  public String joinJsonPath(String path1, String path2, Object value) {
    String p1 = path1 != null ? path1.trim() : "";
    String p2 = path2 != null ? path2.trim() : "";
    p1 = stripDollarAndDots(p1);
    p2 = stripDollarAndDots(p2);
    boolean isList = value instanceof List<?>;
    if (p1.isEmpty() && p2.isEmpty()) {
      return isList ? "$[*]" : "$";
    }
    if (p2.isEmpty()) {
      return isList ? "$[*]." + p1 : "$." + p1;
    }
    if (p1.isEmpty()) {
      return isList ? "$[*]." + p2 : "$." + p2;
    }
    if (isList) {
      return "$." + p1 + "[*]." + p2;
    }
    return "$." + p1 + "." + p2;
  }

  private static String stripDollarAndDots(String s) {
    String res = s;
    while (res.startsWith("$")) {
      res = res.substring(1);
    }
    while (res.startsWith(".")) {
      res = res.substring(1);
    }
    while (res.endsWith(".")) {
      res = res.substring(0, res.length() - 1);
    }
    return res;
  }

  public CheckResult evaluatePrecondition(
      Constraint constraint,
      Object value,
      Map<String, Object> root,
      JsonProfileRunContext context) {
    if (constraint.getPrecondition() == null
        || constraint.getPrecondition().getEvaluations() == null
        || constraint.getPrecondition().getEvaluations().isEmpty()) {
      return new CheckResult(true, "There in no precondition");
    }

    List<Evaluation> validConditions = new ArrayList<>();
    for (Evaluation evaluation : constraint.getPrecondition().getEvaluations()) {
      String jp =
          evaluation.getJsonPath() != null && !evaluation.getJsonPath().isEmpty()
              ? evaluation.getJsonPath()
              : "$";
      jp = joinJsonPath(jp, null, value);
      Object jsonInput = evaluation.isRootValueEvaluation() ? root : value;
      ConstraintChecker checker =
          context.getProfileValidatorFactory().getChecker(evaluation.getConstraint());

      Object matches = queryMatches(jp, jsonInput, context);
      boolean skip =
          context.getRuntimeConfig().isSkipDecimalValidations()
              && constraint instanceof DecimalConstraint;
      if (evaluation.isReferenceField()
          && context.getUniqueIdObjects() != null
          && matches != null) {
        if (matches instanceof List<?> list) {
          List<Object> resolved = new ArrayList<>();
          for (Object id : list) {
            resolved.add(id != null ? context.getUniqueIdObjects().get(id.toString()) : null);
          }
          matches = resolved;
        } else {
          matches = context.getUniqueIdObjects().get(matches.toString());
        }
      }
      if (!skip) {
        boolean evalValid;
        if (matches == null) {
          evalValid =
              evaluation.getDefaultEvaluation() != null ? evaluation.getDefaultEvaluation() : true;
          if (evaluation.isNegated()) {
            evalValid = !evalValid;
          }
        } else {
          ConstraintValidationResult res =
              checker.validateConstraint(evaluation.getConstraint(), matches, root, context);
          evalValid = res.isValid();
          if (evaluation.isNegated()) {
            evalValid = !evalValid;
          }
        }
        if (evalValid) {
          validConditions.add(evaluation);
        }
      }
    }

    return evaluateResults(
        validConditions.size(),
        constraint.getPrecondition().getEvaluations().size(),
        constraint.getPrecondition().getJoinOperator(),
        constraint.getPrecondition().getMinValid(),
        constraint.getPrecondition().getMaxValid(),
        constraint.getDefaultPreconditionEvaluation(),
        false);
  }

  public CheckResult evaluateResults(
      int validItemsCount,
      int allItemsCount,
      String joinOperator,
      Integer minValid,
      Integer maxValid,
      Boolean defaultEvaluation,
      Boolean negated) {
    boolean isNegated = Boolean.TRUE.equals(negated);
    if (allItemsCount == 0) {
      boolean success = defaultEvaluation != null ? defaultEvaluation : true;
      if (isNegated) {
        success = !success;
      }
      return new CheckResult(success, "No item found for evaluation");
    }

    String op = joinOperator != null ? joinOperator : "and";
    if ("and".equalsIgnoreCase(op)) {
      if (validItemsCount == allItemsCount) {
        return new CheckResult(!isNegated, "all conditions are satisfied");
      }
      return new CheckResult(isNegated, "some conditions are not satisfied");
    } else {
      int min = minValid != null ? minValid : 1;
      int max = maxValid != null ? maxValid : allItemsCount;
      List<String> msgs = new ArrayList<>();
      boolean minReq = validItemsCount >= min;
      if (!minReq) {
        msgs.add("min valid conditions is not satisfied");
      }
      boolean maxReq = validItemsCount <= max;
      if (!maxReq) {
        msgs.add("min valid conditions is not satisfied");
      }
      if (minReq && maxReq) {
        return new CheckResult(!isNegated, "all conditions are satisfied");
      }
      return new CheckResult(isNegated, String.join(". ", msgs));
    }
  }

  public ConstraintValidationResult validateConstraint(
      Constraint constraint,
      Object value,
      Map<String, Object> root,
      JsonProfileRunContext context) {
    ConstraintChecker checker = context.getProfileValidatorFactory().getChecker(constraint);
    if (checker != null) {
      CheckResult pre = checker.evaluatePrecondition(constraint, value, root, context);
      Object subValue = getJsonPathValue(constraint, value, context);
      boolean isValid;
      String message;
      if (pre.isValid()) {
        if (constraint instanceof CustomConstraint) {
          boolean nullValue = subValue == null;
          if (!nullValue && constraint.getNullValues() != null) {
            for (String configuredNull : constraint.getNullValues()) {
              if (configuredNull != null && configuredNull.equals(subValue.toString())) {
                nullValue = true;
                break;
              }
            }
          }
          boolean exceptional = false;
          if (constraint.getExceptionalValues() != null) {
            for (Object configured : constraint.getExceptionalValues()) {
              if ((configured == null && nullValue)
                  || (configured != null && configured.equals(subValue))) {
                exceptional = true;
                break;
              }
            }
          }
          CheckResult res =
              exceptional
                  ? new CheckResult(true, "value is in exceptional list")
                  : checker.validate(constraint, nullValue ? null : subValue, root, context);
          isValid = constraint.isNegated() ? !res.isValid() : res.isValid();
          message = res.message() != null ? res.message() : "";
        } else {
          CheckResult res = checker.validate(constraint, subValue, root, context);
          isValid = res.isValid();
          message = res.message();
        }
      } else {
        isValid = true;
        message = "Precondition does not meet: " + pre.message();
      }
      return new ConstraintValidationResult(isValid, message);
    }
    return new ConstraintValidationResult(false, "Unknown constraint type: " + constraint);
  }

  public Object getJsonPathValue(
      Constraint constraint, Object value, JsonProfileRunContext context) {
    Object subInputValue = value;
    if (constraint.getJsonPath() != null && !constraint.getJsonPath().isEmpty()) {
      String subJsonPath = constraint.getJsonPath();
      if (!subJsonPath.startsWith("$")) {
        if (subJsonPath.startsWith("[") || subJsonPath.startsWith(".")) {
          subJsonPath = "$" + subJsonPath;
        } else {
          subJsonPath = "$." + subJsonPath;
        }
      }
      String[] parts = subJsonPath.split("\\.");
      if (value instanceof Map<?, ?> map
          && parts.length == 2
          && "$".equals(parts[0])
          && !parts[1].contains("]")) {
        subInputValue = map.get(parts[1]);
      } else {
        subInputValue = queryMatches(subJsonPath, value, context);
      }
    }
    if (constraint.isReferenceField()
        && context != null
        && context.getUniqueIdObjects() != null
        && subInputValue != null) {
      if (subInputValue instanceof List<?> list) {
        List<Object> resolved = new ArrayList<>();
        for (Object x : list) {
          resolved.add(x != null ? context.getUniqueIdObjects().get(x.toString()) : null);
        }
        return resolved;
      }
      return context.getUniqueIdObjects().get(subInputValue.toString());
    }
    return subInputValue;
  }

  protected static Object queryMatches(
      String jsonPath, Object jsonInput, JsonProfileRunContext context) {
    if (jsonInput == null) {
      return null;
    }
    try {
      JsonPath compiled = context.compileOrGetJsonPath(jsonPath);
      List<Object> found = compiled.read(jsonInput, VALUE_CONFIG);
      if (found == null || found.isEmpty()) {
        return null;
      }
      if (found.size() == 1) {
        return found.get(0);
      }
      return found;
    } catch (Exception ex) {
      return null;
    }
  }
}
