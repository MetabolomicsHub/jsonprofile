package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.metabolomicshub.jsonprofile.profile.BaseCvTerm;

public class CVTermEnumConstraint extends CVTermConstraint {
  @JsonProperty("allowed_cv_terms")
  private List<BaseCvTerm> allowedCvTerms;

  public CVTermEnumConstraint() {
    super("cv-term-enum");
  }

  public List<BaseCvTerm> getAllowedCvTerms() {
    return allowedCvTerms;
  }

  public void setAllowedCvTerms(List<BaseCvTerm> allowedCvTerms) {
    this.allowedCvTerms = allowedCvTerms;
  }
}
