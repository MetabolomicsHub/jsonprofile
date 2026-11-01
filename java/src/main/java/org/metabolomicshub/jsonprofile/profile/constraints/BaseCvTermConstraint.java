package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public abstract class BaseCvTermConstraint extends Constraint {
  @JsonProperty("exceptional_cv_list")
  private List<String> exceptionalCvList;

  @JsonProperty("allow_synonym")
  private Boolean allowSynonym;

  protected BaseCvTermConstraint(String type) {
    super(type);
  }

  public List<String> getExceptionalCvList() {
    return exceptionalCvList;
  }

  public void setExceptionalCvList(List<String> exceptionalCvList) {
    this.exceptionalCvList = exceptionalCvList;
  }

  public Boolean getAllowSynonym() {
    return allowSynonym;
  }

  public boolean isAllowSynonym() {
    return Boolean.TRUE.equals(allowSynonym);
  }

  public void setAllowSynonym(Boolean allowSynonym) {
    this.allowSynonym = allowSynonym;
  }
}
