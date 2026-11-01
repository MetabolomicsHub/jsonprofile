package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegexConstraint extends Constraint {
  @JsonProperty("pattern")
  private String pattern;

  @JsonProperty("case_sensitive")
  private Boolean caseSensitive = Boolean.TRUE;

  public RegexConstraint() {
    super("regex");
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public Boolean getCaseSensitive() {
    return caseSensitive;
  }

  public boolean isCaseSensitive() {
    return caseSensitive == null || caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }
}
