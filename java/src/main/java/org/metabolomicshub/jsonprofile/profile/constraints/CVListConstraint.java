package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CVListConstraint extends CVTermConstraint {
  @JsonProperty("allowed_cv_list")
  private List<String> allowedCvList;

  public CVListConstraint() {
    super("allowed-cv-list");
    setAllowUserDefinedTerms(Boolean.FALSE);
  }

  public List<String> getAllowedCvList() {
    return allowedCvList;
  }

  public void setAllowedCvList(List<String> allowedCvList) {
    if (allowedCvList != null) {
      this.allowedCvList =
          allowedCvList.stream().filter(x -> x != null).map(String::toUpperCase).toList();
    } else {
      this.allowedCvList = null;
    }
  }
}
