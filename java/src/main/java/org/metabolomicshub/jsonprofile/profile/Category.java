package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
  PARSE("parse"),
  FORMAT("format"),
  CROSS_CHECK("cross_check"),
  PROFILE("profile"),
  SCHEMA("schema");

  private final String value;

  Category(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
