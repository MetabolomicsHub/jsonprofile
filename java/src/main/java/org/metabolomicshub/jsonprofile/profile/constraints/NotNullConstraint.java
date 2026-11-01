package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotNullConstraint extends Constraint {
  @JsonProperty("case_sensitive")
  private Boolean caseSensitive;

  public NotNullConstraint() {
    super("not-null");
  }

  public Boolean getCaseSensitive() {
    return caseSensitive;
  }

  public boolean isCaseSensitive() {
    return Boolean.TRUE.equals(caseSensitive);
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }
}
