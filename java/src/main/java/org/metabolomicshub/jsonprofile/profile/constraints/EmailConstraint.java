package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailConstraint extends Constraint {
  @JsonProperty("check_deliverability")
  private Boolean checkDeliverability;

  public EmailConstraint() {
    super("email");
  }

  public Boolean getCheckDeliverability() {
    return checkDeliverability;
  }

  public boolean isCheckDeliverability() {
    return Boolean.TRUE.equals(checkDeliverability);
  }

  public void setCheckDeliverability(Boolean checkDeliverability) {
    this.checkDeliverability = checkDeliverability;
  }
}
