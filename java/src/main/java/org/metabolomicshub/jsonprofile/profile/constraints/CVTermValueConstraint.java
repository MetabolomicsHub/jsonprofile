package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.metabolomicshub.jsonprofile.profile.BaseCvTerm;

public class CVTermValueConstraint extends Constraint {
  @JsonProperty("key_cv_term")
  private BaseCvTerm keyCvTerm;

  @JsonProperty("value_constraint")
  private Constraint valueConstraint;

  public CVTermValueConstraint() {
    super("cv-term-value");
  }

  public BaseCvTerm getKeyCvTerm() {
    return keyCvTerm;
  }

  public void setKeyCvTerm(BaseCvTerm keyCvTerm) {
    this.keyCvTerm = keyCvTerm;
  }

  public Constraint getValueConstraint() {
    return valueConstraint;
  }

  public void setValueConstraint(Constraint valueConstraint) {
    this.valueConstraint = valueConstraint;
  }
}
