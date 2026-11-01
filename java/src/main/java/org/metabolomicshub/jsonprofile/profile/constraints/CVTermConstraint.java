package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CVTermConstraint extends BaseCvTermConstraint {
  @JsonProperty("allow_user_defined_terms")
  private Boolean allowUserDefinedTerms;

  @JsonProperty("is_cv_term_value_required")
  private Boolean isCvTermValueRequired;

  public CVTermConstraint() {
    super("cv-term");
  }

  protected CVTermConstraint(String type) {
    super(type);
  }

  public Boolean getAllowUserDefinedTerms() {
    return allowUserDefinedTerms;
  }

  public boolean isAllowUserDefinedTerms() {
    return Boolean.TRUE.equals(allowUserDefinedTerms);
  }

  public void setAllowUserDefinedTerms(Boolean allowUserDefinedTerms) {
    this.allowUserDefinedTerms = allowUserDefinedTerms;
  }

  public Boolean getIsCvTermValueRequired() {
    return isCvTermValueRequired;
  }

  public boolean isCvTermValueRequired() {
    return Boolean.TRUE.equals(isCvTermValueRequired);
  }

  public void setIsCvTermValueRequired(Boolean isCvTermValueRequired) {
    this.isCvTermValueRequired = isCvTermValueRequired;
  }
}
