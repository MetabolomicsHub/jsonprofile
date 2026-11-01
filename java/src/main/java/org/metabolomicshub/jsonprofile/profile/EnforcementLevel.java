package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnforcementLevel {
  NOT_DEFINED("not-defined"),
  OPTIONAL("optional"),
  RECOMMENDED("recommended"),
  REQUIRED("required");

  private final String value;

  EnforcementLevel(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static EnforcementLevel fromValue(String value) {
    if (value == null) {
      return REQUIRED;
    }
    for (EnforcementLevel level : values()) {
      if (level.value.equalsIgnoreCase(value) || level.name().equalsIgnoreCase(value)) {
        return level;
      }
    }
    throw new IllegalArgumentException("Unknown enforcement level: " + value);
  }
}
