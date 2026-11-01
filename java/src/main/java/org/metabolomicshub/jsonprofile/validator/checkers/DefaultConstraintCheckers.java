package org.metabolomicshub.jsonprofile.validator.checkers;

import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.metabolomicshub.jsonprofile.profile.BaseCvTerm;
import org.metabolomicshub.jsonprofile.profile.CvTerm;
import org.metabolomicshub.jsonprofile.profile.CvTermFieldMapping;
import org.metabolomicshub.jsonprofile.profile.JsonProfileConfiguration;
import org.metabolomicshub.jsonprofile.profile.JsonProfileUtils;
import org.metabolomicshub.jsonprofile.profile.WasmFileDefinition;
import org.metabolomicshub.jsonprofile.profile.constraints.BooleanConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CVListConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CVTermConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CVTermEnumConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CVTermValueConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.CollectionConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.profile.constraints.ConstraintGroup;
import org.metabolomicshub.jsonprofile.profile.constraints.DateTimeConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.DecimalConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.EmailConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.IntegerConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.IntegerEnumConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.NotNullConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.OpaPolicyConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.ParentCVTermConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.RegexConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.StringConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.StringEnumConstraint;
import org.metabolomicshub.jsonprofile.profile.constraints.UriConstraint;
import org.metabolomicshub.jsonprofile.validator.ConstraintChecker;
import org.metabolomicshub.jsonprofile.validator.ConstraintValidationResult;
import org.metabolomicshub.jsonprofile.validator.CvTermSearch;
import org.metabolomicshub.jsonprofile.validator.JsonProfileRunContext;
import org.metabolomicshub.jsonprofile.validator.OpaEngine;
import org.metabolomicshub.jsonprofile.validator.ProfileValidator;

public final class DefaultConstraintCheckers {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

  private DefaultConstraintCheckers() {}

  public static void registerAll(ProfileValidator validator) {
    validator.registerChecker("not-null", null, new NotNullConstraintChecker());
    validator.registerChecker("regex", null, new RegexConstraintChecker());
    validator.registerChecker("string-length", null, new StringConstraintChecker());
    validator.registerChecker("items-count", null, new CollectionConstraintChecker());
    validator.registerChecker("string-enum", null, new StringEnumConstraintChecker());
    validator.registerChecker("integer-enum", null, new IntegerEnumConstraintChecker());
    IntegerConstraintChecker intChecker = new IntegerConstraintChecker();
    validator.registerChecker("integer", null, intChecker);
    validator.registerChecker("positive-integer", null, intChecker);
    validator.registerChecker("non-negative-integer", null, intChecker);
    validator.registerChecker("boolean", null, new BooleanConstraintChecker());
    validator.registerChecker("decimal", null, new DecimalConstraintChecker());
    validator.registerChecker("time", null, new DateTimeConstraintChecker());
    validator.registerChecker("email", null, new EmailConstraintChecker());
    validator.registerChecker("uri", null, new UriConstraintChecker());
    validator.registerChecker("cv-term", null, new CVTermConstraintChecker());
    validator.registerChecker("allowed-cv-list", null, new CVListConstraintChecker());
    validator.registerChecker("cv-term-enum", null, new CVTermEnumConstraintChecker());
    validator.registerChecker("parent-cv-term", null, new ParentCVTermConstraintChecker());
    validator.registerChecker("cv-term-value", null, new CVTermValueConstraintChecker());
    validator.registerChecker("group", null, new ConstraintGroupChecker());
    validator.registerChecker("opa-policy", null, new OpaPolicyConstraintChecker());
  }

  private static boolean inExceptionalValues(Constraint c, Object value, String strVal) {
    if (c.getExceptionalValues() == null || c.getExceptionalValues().isEmpty()) {
      return false;
    }
    for (Object exc : c.getExceptionalValues()) {
      if (exc == null && value == null) {
        return true;
      }
      if (exc != null && (exc.equals(value) || exc.toString().equals(strVal))) {
        return true;
      }
    }
    return false;
  }

  private static boolean isNullByConfig(Constraint c, Object value, boolean caseSensitive) {
    if (value == null) {
      return true;
    }
    if (c.getNullValues() == null || c.getNullValues().isEmpty()) {
      return false;
    }
    String check = caseSensitive ? value.toString() : value.toString().toLowerCase(Locale.ROOT);
    for (String nv : c.getNullValues()) {
      if (nv == null) {
        continue;
      }
      String cmp = caseSensitive ? nv : nv.toLowerCase(Locale.ROOT);
      if (check.equals(cmp)) {
        return true;
      }
    }
    return false;
  }

  public static class NotNullConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      NotNullConstraint constraint = (NotNullConstraint) raw;
      List<String> messages = new ArrayList<>();
      Object val = value;
      if (val != null && isNullByConfig(constraint, val, constraint.isCaseSensitive())) {
        messages.add("value is in null value list");
        val = null;
      }
      boolean evaluation = val != null;
      String message = String.join(". ", messages);
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class RegexConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      RegexConstraint constraint = (RegexConstraint) raw;
      Object val = isNullByConfig(constraint, value, constraint.isCaseSensitive()) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal =
            constraint.isCaseSensitive() ? val.toString() : val.toString().toLowerCase(Locale.ROOT);
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else {
          int flags = constraint.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE;
          boolean matches =
              Pattern.compile(constraint.getPattern(), flags).matcher(strVal).matches();
          if (matches) {
            evaluation = true;
            message =
                "value matches the pattern. input: '"
                    + strVal
                    + "' pattern: '"
                    + constraint.getPattern()
                    + "'";
          } else {
            message =
                "value does not match the pattern. input: '"
                    + strVal
                    + "' expected pattern: '"
                    + constraint.getPattern()
                    + "'";
          }
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class StringConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      StringConstraint constraint = (StringConstraint) raw;
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean minReq = false;
      boolean maxReq = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          minReq = true;
          maxReq = true;
        } else if (constraint.getMinimum() == null || constraint.getMinimum() == 0) {
          minReq = true;
        }
        message = "value is null";
      } else if (!(val instanceof String str)) {
        message = "Value type is " + val.getClass().getSimpleName();
      } else if (inExceptionalValues(constraint, val, str)) {
        minReq = true;
        maxReq = true;
        message = "";
      } else {
        List<String> msgs = new ArrayList<>();
        minReq = constraint.getMinimum() == null || str.length() >= constraint.getMinimum();
        maxReq = constraint.getMaximum() == null || str.length() <= constraint.getMaximum();
        if (!minReq) {
          msgs.add("minimum length error");
        }
        if (!maxReq) {
          msgs.add("maximum length error");
        }
        message = String.join(". ", msgs);
      }
      boolean evaluation = minReq && maxReq;
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class CollectionConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      CollectionConstraint constraint = (CollectionConstraint) raw;
      CvTermFieldMapping mapping =
          context.getProfileConfig().getCvTermFieldMapping() != null
              ? context.getProfileConfig().getCvTermFieldMapping()
              : CvTermFieldMapping.DEFAULT;

      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean minReq = false;
      boolean maxReq = false;
      boolean minMatchReq = false;
      boolean maxMatchReq = false;
      boolean minRefReq = false;
      boolean maxRefReq = false;
      Set<String> matchedSet = new LinkedHashSet<>();
      Set<String> unmatchedSet = new LinkedHashSet<>();
      List<String> messages = new ArrayList<>();
      String message;

      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          minReq = true;
          maxReq = true;
          minMatchReq = true;
          maxMatchReq = true;
          minRefReq = true;
          maxRefReq = true;
        } else if (constraint.getMinOccurs() == null || constraint.getMinOccurs() == 0) {
          minReq = true;
        }
        message = "value is not defined";
      } else if (!(val instanceof List<?>)
          && !(val instanceof Map<?, ?> && constraint.getItemValueMatchConstraint() != null)) {
        return new CheckResult(false, constraint.getJsonPath() + " is not a collection.");
      } else if (inExceptionalValues(constraint, val, val.toString())) {
        minReq = true;
        maxReq = true;
        minMatchReq = true;
        maxMatchReq = true;
        minRefReq = true;
        maxRefReq = true;
        message = "value is in exceptional list";
      } else {
        Map<String, Object> itemValues = new LinkedHashMap<>();
        if (constraint.getItemValueMatchConstraint() != null) {
          List<String> subPaths = constraint.getItemValueJsonpathList();
          if (subPaths != null && !subPaths.isEmpty()) {
            for (String itemJp : subPaths) {
              String joined = joinJsonPath(itemJp, null, val);
              JsonPath compiled = context.compileOrGetJsonPath(joined);
              List<String> paths = compiled.read(val, PATH_CONFIG);
              List<Object> vals = compiled.read(val, VALUE_CONFIG);
              for (int i = 0; i < paths.size() && i < vals.size(); i++) {
                String src = JsonProfileUtils.convertFullPath(paths.get(i));
                Object v = vals.get(i);
                if (constraint.getItemValueMatchConstraint() instanceof CVTermConstraint
                    && v instanceof Map<?, ?> m) {
                  CvTerm cv = CvTerm.fromObject(m, mapping);
                  v = cv != null ? cv.toBaseCvTerm() : v;
                }
                itemValues.put(src, v);
              }
            }
          } else if (val instanceof List<?> rawList) {
            for (int i = 0; i < rawList.size(); i++) {
              itemValues.put("$[" + i + "]", rawList.get(i));
            }
          }
        }

        int rawSize = val instanceof List<?> rawList ? rawList.size() : itemValues.size();
        int currentSize =
            constraint.getItemValueMatchConstraint() != null && !itemValues.isEmpty()
                ? itemValues.size()
                : rawSize;
        minReq = constraint.getMinOccurs() == null || currentSize >= constraint.getMinOccurs();
        maxReq = constraint.getMaxOccurs() == null || currentSize <= constraint.getMaxOccurs();
        if (!minReq) {
          messages.add(
              "minimum count error: current: "
                  + currentSize
                  + ", min: "
                  + constraint.getMinOccurs());
        }
        if (!maxReq) {
          messages.add(
              "maximum count error: current: "
                  + currentSize
                  + ", max: "
                  + constraint.getMaxOccurs());
        }

        if (constraint.getItemValueMatchConstraint() != null) {
          Constraint itemConstraint = constraint.getItemValueMatchConstraint();
          ConstraintChecker checker =
              context.getProfileValidatorFactory().getChecker(itemConstraint);
          if (itemConstraint.getDescription() == null) {
            itemConstraint.setDescription(constraint.getDescription());
          }
          int matchedCount = 0;
          for (Map.Entry<String, Object> entry : itemValues.entrySet()) {
            Object item = entry.getValue();
            boolean match =
                constraint.getNullValues() != null
                    && item != null
                    && constraint.getNullValues().contains(item.toString());
            if (!match) {
              ConstraintValidationResult res =
                  checker.validateConstraint(itemConstraint, item, root, context);
              match = res.isValid();
            }
            if (match) {
              matchedCount++;
              matchedSet.add(String.valueOf(item));
            } else {
              unmatchedSet.add(String.valueOf(item));
            }
          }
          int refMatchedCount = matchedSet.size();
          minMatchReq =
              constraint.getMinMatch() != null
                  ? matchedCount >= constraint.getMinMatch()
                  : matchedCount > 0;
          maxMatchReq =
              constraint.getMaxMatch() == null || matchedCount <= constraint.getMaxMatch();
          minRefReq =
              constraint.getMinReferencedValueMatch() != null
                  ? refMatchedCount >= constraint.getMinReferencedValueMatch()
                  : (itemValues.isEmpty() || refMatchedCount > 0);
          maxRefReq =
              constraint.getMaxReferencedValueMatch() != null
                  ? refMatchedCount <= constraint.getMaxReferencedValueMatch()
                  : (minRefReq || unmatchedSet.isEmpty());
        } else {
          minMatchReq = true;
          maxMatchReq = true;
          minRefReq = true;
          maxRefReq = true;
        }
        message = String.join(". ", messages);
      }

      boolean evaluation = minReq && maxReq && minMatchReq && maxMatchReq && minRefReq && maxRefReq;
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class StringEnumConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      StringEnumConstraint constraint = (StringEnumConstraint) raw;
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else if (constraint.getOptions() != null && constraint.getOptions().contains(strVal)) {
          evaluation = true;
          message = "value is in the enum list";
        } else {
          message = strVal + " value is not in the enum list";
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class IntegerEnumConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      IntegerEnumConstraint constraint = (IntegerEnumConstraint) raw;
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else {
          try {
            int intVal = Integer.parseInt(strVal);
            if (constraint.getOptions() != null && constraint.getOptions().containsKey(intVal)) {
              evaluation = true;
              message = "value " + strVal + " is in the int enum list";
            } else {
              message = "value " + strVal + " is not in the int enum list";
            }
          } catch (NumberFormatException ex) {
            message = "value '" + strVal + "' is not numeric";
          }
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class IntegerConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      IntegerConstraint constraint = (IntegerConstraint) raw;
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean minReq = false;
      boolean maxReq = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          minReq = true;
          maxReq = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          minReq = true;
          maxReq = true;
          message = "";
        } else if (val instanceof Boolean) {
          message = "value '" + strVal + "' is not numeric";
        } else {
          try {
            long intVal = Long.parseLong(strVal);
            minReq = constraint.getMinimum() == null || intVal >= constraint.getMinimum();
            maxReq = constraint.getMaximum() == null || intVal <= constraint.getMaximum();
            List<String> msgs = new ArrayList<>();
            if (constraint.getMinimum() != null && !minReq) {
              msgs.add(
                  "minimum value error. current: " + intVal + ", min: " + constraint.getMinimum());
            }
            if (constraint.getMaximum() != null && !maxReq) {
              msgs.add(
                  "maximum value error. current: " + intVal + ", min: " + constraint.getMaximum());
            }
            message = String.join(". ", msgs);
          } catch (NumberFormatException ex) {
            message = "value '" + strVal + "' is not numeric";
          }
        }
      }
      boolean evaluation = minReq && maxReq;
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class BooleanConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      BooleanConstraint constraint = (BooleanConstraint) raw;
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else {
          Boolean boolVal = null;
          if (val instanceof Boolean b) {
            boolVal = b;
          } else if (val instanceof String s) {
            String lower = s.toLowerCase(Locale.ROOT);
            if (constraint.getExceptionalFalseValues() != null
                && constraint.getExceptionalFalseValues().stream()
                    .anyMatch(x -> x != null && x.toLowerCase(Locale.ROOT).equals(lower))) {
              boolVal = Boolean.FALSE;
            } else if (constraint.getExceptionalTrueValues() != null
                && constraint.getExceptionalTrueValues().stream()
                    .anyMatch(x -> x != null && x.toLowerCase(Locale.ROOT).equals(lower))) {
              boolVal = Boolean.TRUE;
            }
          }
          if (boolVal != null) {
            evaluation = true;
            message = "value '" + strVal + "' evaluated as " + boolVal;
          } else {
            message = "value '" + strVal + "' is not boolean";
          }
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class DecimalConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      DecimalConstraint constraint = (DecimalConstraint) raw;
      if (context.getRuntimeConfig() != null
          && context.getRuntimeConfig().isSkipDecimalValidations()) {
        return new CheckResult(true, "Decimal validation is skipped.");
      }
      Object val = isNullByConfig(constraint, value, false) ? null : value;
      boolean minReq = false;
      boolean maxReq = false;
      boolean minScaleReq = false;
      boolean maxScaleReq = false;
      String message;

      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          minReq = true;
          maxReq = true;
          minScaleReq = true;
          maxScaleReq = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          minReq = true;
          maxReq = true;
          minScaleReq = true;
          maxScaleReq = true;
          message = "";
        } else if ("NaN".equalsIgnoreCase(strVal)
            || "Infinity".equalsIgnoreCase(strVal)
            || "+Infinity".equalsIgnoreCase(strVal)
            || "-Infinity".equalsIgnoreCase(strVal)) {
          message = "Value is " + strVal;
          if (constraint.isAllowNonFiniteValues()) {
            minReq = true;
            maxReq = true;
            minScaleReq = true;
            maxScaleReq = true;
          }
        } else {
          try {
            BigDecimal dec = new BigDecimal(strVal);
            if (!constraint.isAllowScientificNotation()
                && strVal.toLowerCase(Locale.ROOT).contains("e")) {
              message = "scientific notation is not allowed";
            } else {
              int scale = Math.max(0, dec.scale());
              minScaleReq = constraint.getMinScale() == null || scale >= constraint.getMinScale();
              maxScaleReq = constraint.getMaxScale() == null || scale <= constraint.getMaxScale();
              minReq =
                  constraint.getMinimum() == null || dec.compareTo(constraint.getMinimum()) >= 0;
              maxReq =
                  constraint.getMaximum() == null || dec.compareTo(constraint.getMaximum()) <= 0;
              List<String> msgs = new ArrayList<>();
              if (constraint.getMinimum() != null && !minReq) {
                msgs.add("minimum value error");
              }
              if (constraint.getMaximum() != null && !maxReq) {
                msgs.add("maximum value error");
              }
              if (constraint.getMinScale() != null && !minScaleReq) {
                msgs.add("minimum scale error");
              }
              if (constraint.getMaxScale() != null && !maxScaleReq) {
                msgs.add("max scale error");
              }
              message = String.join(". ", msgs);
            }
          } catch (NumberFormatException ex) {
            message = "value '" + strVal + "' is not numeric";
          }
        }
      }
      boolean evaluation = minReq && maxReq && minScaleReq && maxScaleReq;
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class DateTimeConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      DateTimeConstraint constraint = (DateTimeConstraint) raw;
      Object val = isNullByConfig(constraint, value, false) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else {
          if (isValidDateTime(strVal, constraint.getFormat())) {
            evaluation = true;
            message = "value is valid datetime";
          } else {
            message = "value is is not valid datetime";
          }
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }

    private static boolean isValidDateTime(String strVal, String format) {
      try {
        if (format != null && !format.isEmpty()) {
          String javaPattern = convertStrftimeToJava(format);
          DateTimeFormatter fmt = DateTimeFormatter.ofPattern(javaPattern);
          fmt.parse(strVal);
          return true;
        } else {
          return tryParseIso(strVal);
        }
      } catch (Exception ex) {
        return false;
      }
    }

    private static boolean tryParseIso(String strVal) {
      try {
        OffsetDateTime.parse(strVal);
        return true;
      } catch (Exception ignored) {
        // Try the next supported date-time representation.
      }
      try {
        ZonedDateTime.parse(strVal);
        return true;
      } catch (Exception ignored) {
        // Try the next supported date-time representation.
      }
      try {
        LocalDateTime.parse(strVal);
        return true;
      } catch (Exception ignored) {
        // Try the next supported date-time representation.
      }
      try {
        LocalDate.parse(strVal);
        return true;
      } catch (Exception ignored) {
        // Try the next supported date-time representation.
      }
      try {
        LocalTime.parse(strVal);
        return true;
      } catch (Exception ignored) {
        // No supported representation matched.
      }
      return false;
    }

    private static String convertStrftimeToJava(String fmt) {
      return fmt.replace("%Y", "yyyy")
          .replace("%m", "MM")
          .replace("%d", "dd")
          .replace("%H", "HH")
          .replace("%M", "mm")
          .replace("%S", "ss")
          .replace("%f", "SSSSSS")
          .replace("%z", "Z");
    }
  }

  public static class EmailConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      EmailConstraint constraint = (EmailConstraint) raw;
      Object val = isNullByConfig(constraint, value, false) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else if (EMAIL_PATTERN.matcher(strVal).matches()) {
          evaluation = true;
          message = "value is valid email";
        } else {
          message = "value is not valid email";
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class UriConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      UriConstraint constraint = (UriConstraint) raw;
      Object val = isNullByConfig(constraint, value, false) ? null : value;
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else {
        String strVal = val.toString();
        if (inExceptionalValues(constraint, val, strVal)) {
          evaluation = true;
          message = "";
        } else {
          try {
            URI uri = URI.create(strVal);
            String scheme = uri.getScheme();
            boolean hasTarget =
                (uri.getHost() != null && !uri.getHost().isEmpty())
                    || (uri.getPath() != null && !uri.getPath().isEmpty())
                    || (uri.getSchemeSpecificPart() != null
                        && !uri.getSchemeSpecificPart().isEmpty());
            if (scheme == null || scheme.isEmpty() || !hasTarget) {
              message = "value '" + strVal + "' is not a valid URI";
            } else if (constraint.getAllowedSchemes() != null
                && !constraint.getAllowedSchemes().isEmpty()) {
              if (constraint.getAllowedSchemes().contains(scheme)) {
                evaluation = true;
                message = "value is valid url with allowed scheme";
              } else {
                message =
                    "value is valid url but scheme '"
                        + scheme
                        + "' is not in allowed schemes: "
                        + String.join(", ", constraint.getAllowedSchemes());
              }
            } else {
              evaluation = true;
              message = "value is valid url";
            }
          } catch (Exception ex) {
            message = "value " + strVal + " is not valid url";
          }
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class CVTermConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      CVTermConstraint constraint = (CVTermConstraint) raw;
      CvTermFieldMapping mapping = context.getProfileConfig().getCvTermFieldMapping();
      CvTerm param = value != null ? CvTerm.fromObject(value, mapping) : null;
      Object val = value;
      if (val != null && constraint.getNullValues() != null) {
        if (param == null || constraint.getNullValues().contains(param.toString())) {
          val = null;
        }
      }
      boolean evaluation = false;
      String message;
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is not defined";
      } else if (param == null) {
        message = "Value '" + val + "' is not a CV term.";
      } else if (param.getCvLabel().isEmpty()
          && param.getCvAccession().isEmpty()
          && param.getName().isEmpty()) {
        message = "Value '" + val + "' could not be parsed as a CV term.";
      } else if (constraint.getExceptionalCvList() != null
          && constraint.getExceptionalCvList().contains(param.getCvLabel())) {
        return new CheckResult(true, formatMessage(constraint, "CV source is in exception list"));
      } else {
        boolean nameReq = false;
        boolean isUserDefined =
            param.getCvLabel().isEmpty()
                && param.getCvAccession().isEmpty()
                && !param.getName().isEmpty();
        if (isUserDefined) {
          if (constraint.isAllowUserDefinedTerms()) {
            nameReq = true;
            message = "User-defined terms is allowed.";
          } else {
            message = "User-defined terms is not allowed.";
          }
        } else if (!param.getName().isEmpty()) {
          if (param.getCvLabel().isEmpty() || param.getCvAccession().isEmpty()) {
            message = "invalid cv term label or accession. " + param;
          } else if (context.getRuntimeConfig().isOfflineMode()) {
            nameReq = true;
            message = "term is valid.";
          } else {
            CvTermSearch.CheckResult res =
                context.getCvTermSearch().checkCvTerm(param, null, constraint.isAllowSynonym());
            return new CheckResult(res.valid(), formatMessage(constraint, res.message()));
          }
        } else {
          message = "invalid cv term name: " + param;
        }

        List<String> msgs = new ArrayList<>();
        if (message != null && !message.isEmpty()) {
          msgs.add(message);
        }
        boolean valReq;
        if (constraint.isCvTermValueRequired()) {
          if (param.getValue() != null && !param.getValue().isEmpty()) {
            valReq = true;
            msgs.add("Value of CV term is defined.");
          } else {
            valReq = false;
            msgs.add("Value slot for CV term is required.");
          }
        } else {
          valReq = true;
        }
        message = String.join(". ", msgs);
        evaluation = nameReq && valReq;
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class CVListConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      CVListConstraint constraint = (CVListConstraint) raw;
      CvTermFieldMapping mapping = context.getProfileConfig().getCvTermFieldMapping();
      CvTerm param = value != null ? CvTerm.fromObject(value, mapping) : null;
      Object val = value;
      if (val != null && constraint.getNullValues() != null) {
        if (param == null || constraint.getNullValues().contains(param.toString())) {
          val = null;
        }
      }
      boolean evaluation = false;
      String message = "";
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else if (param == null) {
        message = "Value '" + val + "' is not a CV term.";
      } else if (param.getCvLabel().isEmpty()
          && param.getCvAccession().isEmpty()
          && param.getName().isEmpty()) {
        message = "Value '" + val + "' could not be parsed as a CV term.";
      } else {
        boolean nameReq = false;
        if (!param.getName().isEmpty()) {
          if (param.getCvLabel().isEmpty() || param.getCvAccession().isEmpty()) {
            message = " '" + param + "' invalid cv term label or accession.";
          } else {
            BaseCvTerm baseParam = param.toBaseCvTerm();
            if (inExceptionalValues(constraint, baseParam, baseParam.toString())) {
              nameReq = true;
              message = "'" + param + "' term is in exception cv term list.";
            }
            if (!nameReq) {
              if (constraint.getAllowedCvList() != null
                  && !constraint.getAllowedCvList().isEmpty()) {
                boolean inList =
                    constraint.getAllowedCvList().stream()
                        .anyMatch(x -> x.equalsIgnoreCase(param.getCvLabel()));
                if (inList) {
                  if (context.getRuntimeConfig().isOfflineMode()) {
                    nameReq = true;
                    message = "offline mode.";
                  } else {
                    CvTermSearch.CheckResult res =
                        context
                            .getCvTermSearch()
                            .checkCvTerm(param, null, constraint.isAllowSynonym());
                    return new CheckResult(res.valid(), formatMessage(constraint, res.message()));
                  }
                } else {
                  message =
                      "'"
                          + param
                          + "' not in the selected cv list: "
                          + String.join(", ", constraint.getAllowedCvList());
                }
              } else if (context.getRuntimeConfig().isOfflineMode()) {
                nameReq = true;
                message = "offline mode.";
              } else {
                CvTermSearch.CheckResult res =
                    context.getCvTermSearch().checkCvTerm(param, null, constraint.isAllowSynonym());
                return new CheckResult(res.valid(), formatMessage(constraint, res.message()));
              }
            }
          }
        } else {
          message = "invalid cv term name.";
        }

        List<String> msgs = new ArrayList<>();
        if (!message.isEmpty()) {
          msgs.add(message);
        }
        boolean valReq;
        if (constraint.isCvTermValueRequired()) {
          if (param.getValue() != null && !param.getValue().isEmpty()) {
            valReq = true;
            msgs.add("Value of CV term is defined.");
          } else {
            valReq = false;
            msgs.add("Value slot for CV term is required.");
          }
        } else {
          valReq = true;
        }
        message = String.join(". ", msgs);
        evaluation = nameReq && valReq;
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class CVTermEnumConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      CVTermEnumConstraint constraint = (CVTermEnumConstraint) raw;
      CvTermFieldMapping mapping = context.getProfileConfig().getCvTermFieldMapping();
      CvTerm param = value != null ? CvTerm.fromObject(value, mapping) : null;
      Object val = value;
      if (val != null && constraint.getNullValues() != null) {
        if (param == null || constraint.getNullValues().contains(param.toString())) {
          val = null;
        }
      }
      boolean evaluation = false;
      String message = "";
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else if (param == null) {
        message = "Value '" + val + "' is not a CV term.";
      } else if (param.getCvLabel().isEmpty()
          && param.getCvAccession().isEmpty()
          && param.getName().isEmpty()) {
        message = "Value '" + val + "' could not be parsed as a CV term.";
      } else {
        boolean nameReq = false;
        boolean hasNameOnlyEnums =
            constraint.getAllowedCvTerms() != null
                && constraint.getAllowedCvTerms().stream()
                    .anyMatch(x -> x.getCvAccession() == null || x.getCvAccession().isEmpty());
        if (!param.getName().isEmpty()) {
          if (!hasNameOnlyEnums
              && (param.getCvLabel().isEmpty() || param.getCvAccession().isEmpty())) {
            message = "invalid cv term label or accession '" + param + "'.";
          } else {
            if (constraint.getExceptionalCvList() != null
                && constraint.getExceptionalCvList().contains(param.getCvLabel())) {
              nameReq = true;
              message = "term '" + param.getCvLabel() + "' is in exception cv list.";
            }
            if (!nameReq) {
              if (constraint.getAllowedCvTerms() != null
                  && !constraint.getAllowedCvTerms().isEmpty()) {
                BaseCvTerm inputVal = param.toBaseCvTerm();
                if (constraint.getAllowedCvTerms().contains(inputVal)) {
                  nameReq = true;
                  message = "'" + inputVal + "' is in cv term list";
                } else {
                  message = "'" + inputVal + "' is not in cv term list";
                }
              } else {
                nameReq = true;
                message = "term in cv term list";
              }
            }
          }
        } else {
          message = "invalid cv term name";
        }
        List<String> msgs = new ArrayList<>();
        if (!message.isEmpty()) {
          msgs.add(message);
        }
        boolean valReq;
        if (constraint.isCvTermValueRequired()) {
          if (param.getValue() != null && !param.getValue().isEmpty()) {
            valReq = true;
            msgs.add("Value of CV term is defined.");
          } else {
            valReq = false;
            msgs.add("Value slot for CV term is required.");
          }
        } else {
          valReq = true;
        }
        message = String.join(". ", msgs);
        evaluation = nameReq && valReq;
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class ParentCVTermConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      ParentCVTermConstraint constraint = (ParentCVTermConstraint) raw;
      CvTermFieldMapping mapping = context.getProfileConfig().getCvTermFieldMapping();
      CvTerm param = value != null ? CvTerm.fromObject(value, mapping) : null;
      Object val = value;
      if (val != null && constraint.getNullValues() != null) {
        if (param == null || constraint.getNullValues().contains(param.toString())) {
          val = null;
        }
      }
      boolean evaluation = false;
      String message = "";
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else if (param == null) {
        message = "Value '" + val + "' is not a CV term.";
      } else if (param.getCvLabel().isEmpty()
          && param.getCvAccession().isEmpty()
          && param.getName().isEmpty()) {
        message = "Value '" + val + "' could not be parsed as a CV term.";
      } else {
        boolean nameReq = false;
        if (!param.getName().isEmpty()) {
          if (param.getCvLabel().isEmpty() || param.getCvAccession().isEmpty()) {
            message = "invalid cv term label or accession '" + param + "'.";
          } else {
            BaseCvTerm baseParam = param.toBaseCvTerm();
            if (inExceptionalValues(constraint, baseParam, baseParam.toString())) {
              nameReq = true;
              message = "cv term is in exception list.";
            }
            if (!nameReq) {
              if (context.getRuntimeConfig().isOfflineMode()) {
                nameReq = true;
                message = "offline mode.";
              } else if (constraint.getParentCvTerms() != null) {
                for (BaseCvTerm parent : constraint.getParentCvTerms()) {
                  CvTerm parentCv =
                      new CvTerm(
                          parent.getCvLabel(), parent.getCvAccession(), parent.getName(), "");
                  CvTermSearch.CheckResult res =
                      context
                          .getCvTermSearch()
                          .checkCvTerm(param, parentCv, constraint.isAllowSynonym());
                  if (res.valid()) {
                    nameReq = true;
                    message = "CV term " + param + " is parent of " + parent + ".";
                    break;
                  }
                }
                if (!nameReq) {
                  message = "not child of the parent.";
                }
              } else {
                nameReq = true;
              }
            }
          }
        } else {
          message = "invalid cv term name.";
        }
        List<String> msgs = new ArrayList<>();
        if (!message.isEmpty()) {
          msgs.add(message);
        }
        boolean valReq =
            !constraint.isCvTermValueRequired()
                || (param.getValue() != null && !param.getValue().isEmpty());
        if (constraint.isCvTermValueRequired() && !valReq) {
          msgs.add("Value slot for CV term is required.");
        }
        message = String.join(". ", msgs);
        evaluation = nameReq && valReq;
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class CVTermValueConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      CVTermValueConstraint constraint = (CVTermValueConstraint) raw;
      CvTermFieldMapping mapping = context.getProfileConfig().getCvTermFieldMapping();
      CvTerm param = value != null ? CvTerm.fromObject(value, mapping) : null;
      BaseCvTerm base = param != null ? param.toBaseCvTerm() : null;
      boolean evaluation;
      String message;
      if (base != null
          && constraint.getKeyCvTerm() != null
          && base.toString().equalsIgnoreCase(constraint.getKeyCvTerm().toString())) {
        if (constraint.getValueConstraint() != null) {
          ConstraintChecker checker =
              context.getProfileValidatorFactory().getChecker(constraint.getValueConstraint());
          ConstraintValidationResult res =
              checker.validateConstraint(
                  constraint.getValueConstraint(), param.getValue(), root, context);
          evaluation = res.isValid();
          message = res.message();
        } else {
          evaluation = true;
          message = "No value constraint defined.";
        }
      } else {
        evaluation = true;
        message =
            "CV term '"
                + param
                + "' does not match the key CV term '"
                + constraint.getKeyCvTerm()
                + "'";
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class ConstraintGroupChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      ConstraintGroup constraint = (ConstraintGroup) raw;
      if (constraint.getConstraints() == null || constraint.getConstraints().isEmpty()) {
        return new CheckResult(true, null);
      }
      Object val = value;
      if (val != null && constraint.getNullValues() != null) {
        CvTerm param = CvTerm.fromObject(val, context.getProfileConfig().getCvTermFieldMapping());
        if (param == null || constraint.getNullValues().contains(param.toString())) {
          val = null;
        }
      }
      boolean isAnd = "and".equalsIgnoreCase(constraint.getJoinOperator());
      List<String> messages = new ArrayList<>();
      int validCount = 0;
      for (Constraint sub : constraint.getConstraints()) {
        ConstraintValidationResult res = validateConstraint(sub, val, root, context);
        if (res.isValid()) {
          validCount++;
        } else if (res.message() != null && !res.message().isEmpty()) {
          messages.add(res.message());
        }
      }
      boolean evaluation = false;
      if (isAnd) {
        if (validCount == constraint.getConstraints().size()) {
          messages.add("All conditions are valid");
          evaluation = true;
        }
      } else {
        int minValid = constraint.getMinValid() != null ? constraint.getMinValid() : 1;
        int maxValid =
            constraint.getMaxValid() != null
                ? constraint.getMaxValid()
                : constraint.getConstraints().size();
        boolean minEval = validCount >= minValid;
        boolean maxEval = validCount <= maxValid;
        messages.add(
            minEval
                ? "Min " + minValid + " conditions are valid"
                : "Min " + minValid + " conditions are not valid");
        messages.add(
            maxEval
                ? "Max " + maxValid + " conditions are valid"
                : "Max " + maxValid + " conditions are not valid");
        evaluation = minEval && maxEval;
      }
      String message = String.join(". ", messages);
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }

  public static class OpaPolicyConstraintChecker extends ConstraintChecker {
    @Override
    public CheckResult validate(
        Constraint raw, Object value, Map<String, Object> root, JsonProfileRunContext context) {
      OpaPolicyConstraint constraint = (OpaPolicyConstraint) raw;
      JsonProfileConfiguration config = context.getProfileConfig();
      Object val = isNullByConfig(constraint, value, true) ? null : value;
      boolean evaluation = false;
      String message = "";
      if (val == null) {
        if (inExceptionalValues(constraint, null, null)) {
          evaluation = true;
        }
        message = "value is null";
      } else if (inExceptionalValues(constraint, val, val.toString())) {
        evaluation = true;
      } else {
        String label =
            constraint.getWasmFileKey() != null && !constraint.getWasmFileKey().isEmpty()
                ? constraint.getWasmFileKey()
                : "default";
        String wasmFilePath = null;
        String wasmDownloadUrl = null;
        String entrypoint = null;
        if (config != null && config.getWasmFileDefinitions() != null) {
          WasmFileDefinition opaCfg = config.getWasmFileDefinitions().get(label);
          if (opaCfg == null) {
            throw new IllegalArgumentException("OPA policy file not found");
          }
          wasmFilePath = opaCfg.getWasmFilePath();
          wasmDownloadUrl = opaCfg.getWasmFileDownloadUrl();
          entrypoint = opaCfg.getEntrypoint();
        }
        OpaEngine engine =
            context
                .getProfileValidatorFactory()
                .getOpaEngineFactory()
                .getOpaEngine(wasmFilePath, wasmDownloadUrl);
        Map<String, Object> inputData = new LinkedHashMap<>();
        inputData.put("policy_ids", List.of(constraint.getPolicyId()));
        inputData.put("value", val);
        inputData.put("root", root);
        inputData.put("config", config);
        inputData.put("constraint", constraint);

        Object result = engine.evaluate(inputData, entrypoint, null);
        if (result instanceof List<?> list
            && !list.isEmpty()
            && list.get(0) instanceof Map<?, ?> first) {
          Object evalObj = first.get("evaluation");
          evaluation = Boolean.TRUE.equals(evalObj);
          Object msgObj = first.get("message");
          message = msgObj != null ? msgObj.toString() : "";
        } else if (result instanceof Map<?, ?> map) {
          Object evalObj = map.get("evaluation");
          evaluation = Boolean.TRUE.equals(evalObj);
          Object msgObj = map.get("message");
          message = msgObj != null ? msgObj.toString() : "";
        } else {
          throw new IllegalArgumentException("OPA policy decision is not valid");
        }
      }
      if (constraint.isNegated()) {
        evaluation = !evaluation;
      }
      return new CheckResult(evaluation, formatMessage(constraint, message));
    }
  }
}
