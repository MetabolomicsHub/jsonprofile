package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateTimeConstraint extends Constraint {
  @JsonProperty("format")
  private String format;

  public DateTimeConstraint() {
    super("time");
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }
}
