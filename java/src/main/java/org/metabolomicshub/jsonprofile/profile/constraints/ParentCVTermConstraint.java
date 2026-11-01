package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.metabolomicshub.jsonprofile.profile.BaseCvTerm;

public class ParentCVTermConstraint extends CVTermConstraint {
  @JsonProperty("parent_cv_terms")
  private List<BaseCvTerm> parentCvTerms;

  @JsonProperty("excluded_cv_terms")
  private List<BaseCvTerm> excludedCvTerms;

  public ParentCVTermConstraint() {
    super("parent-cv-term");
  }

  public List<BaseCvTerm> getParentCvTerms() {
    return parentCvTerms;
  }

  public void setParentCvTerms(List<BaseCvTerm> parentCvTerms) {
    this.parentCvTerms = parentCvTerms;
  }

  public List<BaseCvTerm> getExcludedCvTerms() {
    return excludedCvTerms;
  }

  public void setExcludedCvTerms(List<BaseCvTerm> excludedCvTerms) {
    this.excludedCvTerms = excludedCvTerms;
  }
}
