package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseCvTerm {
  @JsonProperty("cv_label")
  private String cvLabel = "";

  @JsonProperty("cv_accession")
  private String cvAccession = "";

  @JsonProperty("name")
  private String name = "";

  public BaseCvTerm() {}

  public BaseCvTerm(String cvLabel, String cvAccession, String name) {
    this.cvLabel = cvLabel != null ? cvLabel : "";
    this.cvAccession = cvAccession != null ? cvAccession : "";
    this.name = name != null ? name : "";
  }

  public String getCvLabel() {
    return cvLabel;
  }

  public void setCvLabel(String cvLabel) {
    this.cvLabel = cvLabel != null ? cvLabel : "";
  }

  public String getCvAccession() {
    return cvAccession;
  }

  public void setCvAccession(String cvAccession) {
    this.cvAccession = cvAccession != null ? cvAccession : "";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name != null ? name : "";
  }

  @Override
  public String toString() {
    return "["
        + JsonProfileUtils.sanitizeStr(cvLabel)
        + ", "
        + JsonProfileUtils.sanitizeStr(cvAccession)
        + ", "
        + JsonProfileUtils.sanitizeStr(name)
        + ", ]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseCvTerm that)) {
      return false;
    }
    return Objects.equals(this.cvLabel, that.cvLabel)
        && Objects.equals(this.cvAccession, that.cvAccession)
        && Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.cvLabel, this.cvAccession, this.name);
  }
}
