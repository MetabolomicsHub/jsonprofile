package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegerEnumConstraint extends Constraint {
  @JsonProperty("options")
  private Map<Long, Object> options = new LinkedHashMap<>();

  public IntegerEnumConstraint() {
    super("integer-enum");
  }

  public Map<Long, Object> getOptions() {
    return options;
  }

  public void setOptions(Map<Long, Object> options) {
    this.options = options != null ? options : new LinkedHashMap<>();
  }
}
