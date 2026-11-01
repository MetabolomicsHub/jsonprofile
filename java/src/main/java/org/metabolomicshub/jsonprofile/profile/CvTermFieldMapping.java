package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CvTermFieldMapping {
  public static final CvTermFieldMapping DEFAULT = new CvTermFieldMapping();

  @JsonProperty("label_field")
  private String labelField = "cv_label";

  @JsonProperty("accession_field")
  private String accessionField = "cv_accession";

  @JsonProperty("name_field")
  private String nameField = "name";

  @JsonProperty("value_field")
  private String valueField = "value";

  public CvTermFieldMapping() {}

  public CvTermFieldMapping(
      String labelField, String accessionField, String nameField, String valueField) {
    this.labelField = labelField;
    this.accessionField = accessionField;
    this.nameField = nameField;
    this.valueField = valueField;
  }

  public String getLabelField() {
    return labelField;
  }

  public void setLabelField(String labelField) {
    this.labelField = labelField;
  }

  public String getAccessionField() {
    return accessionField;
  }

  public void setAccessionField(String accessionField) {
    this.accessionField = accessionField;
  }

  public String getNameField() {
    return nameField;
  }

  public void setNameField(String nameField) {
    this.nameField = nameField;
  }

  public String getValueField() {
    return valueField;
  }

  public void setValueField(String valueField) {
    this.valueField = valueField;
  }
}
