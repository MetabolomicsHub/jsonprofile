package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UriConstraint extends Constraint {
  @JsonProperty("allowed_schemes")
  private List<String> allowedSchemes;

  public UriConstraint() {
    super("uri");
  }

  public List<String> getAllowedSchemes() {
    return allowedSchemes;
  }

  public void setAllowedSchemes(List<String> allowedSchemes) {
    this.allowedSchemes = allowedSchemes;
  }
}
