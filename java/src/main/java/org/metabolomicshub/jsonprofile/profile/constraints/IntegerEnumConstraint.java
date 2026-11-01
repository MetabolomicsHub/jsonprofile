package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegerEnumConstraint extends Constraint {
  @JsonProperty("options")
  private Map<Integer, Object> options = new LinkedHashMap<>();

  public IntegerEnumConstraint() {
    super("integer-enum");
  }

  public Map<Integer, Object> getOptions() {
    return options;
  }

  public void setOptions(Map<Integer, Object> options) {
    this.options = options != null ? options : new LinkedHashMap<>();
  }
}
