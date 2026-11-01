package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = BooleanConstraint.class, name = "boolean"),
  @JsonSubTypes.Type(value = CollectionConstraint.class, name = "items-count"),
  @JsonSubTypes.Type(value = ConstraintGroup.class, name = "group"),
  @JsonSubTypes.Type(value = CVListConstraint.class, name = "allowed-cv-list"),
  @JsonSubTypes.Type(value = CVTermConstraint.class, name = "cv-term"),
  @JsonSubTypes.Type(value = CVTermEnumConstraint.class, name = "cv-term-enum"),
  @JsonSubTypes.Type(value = CVTermValueConstraint.class, name = "cv-term-value"),
  @JsonSubTypes.Type(value = CustomConstraint.class, name = "custom"),
  @JsonSubTypes.Type(value = DateTimeConstraint.class, name = "time"),
  @JsonSubTypes.Type(value = DecimalConstraint.class, name = "decimal"),
  @JsonSubTypes.Type(value = EmailConstraint.class, name = "email"),
  @JsonSubTypes.Type(value = IntegerConstraint.class, name = "integer"),
  @JsonSubTypes.Type(value = IntegerEnumConstraint.class, name = "integer-enum"),
  @JsonSubTypes.Type(value = NonNegativeIntegerConstraint.class, name = "non-negative-integer"),
  @JsonSubTypes.Type(value = NotNullConstraint.class, name = "not-null"),
  @JsonSubTypes.Type(value = OpaPolicyConstraint.class, name = "opa-policy"),
  @JsonSubTypes.Type(value = ParentCVTermConstraint.class, name = "parent-cv-term"),
  @JsonSubTypes.Type(value = PositiveIntegerConstraint.class, name = "positive-integer"),
  @JsonSubTypes.Type(value = RegexConstraint.class, name = "regex"),
  @JsonSubTypes.Type(value = StringEnumConstraint.class, name = "string-enum"),
  @JsonSubTypes.Type(value = StringConstraint.class, name = "string-length"),
  @JsonSubTypes.Type(value = UriConstraint.class, name = "uri")
})
public abstract class Constraint {
  @JsonProperty("validator")
  private String validator;

  @JsonProperty("type")
  private String type;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("precondition")
  private Precondition precondition;

  @JsonProperty("json_path")
  private String jsonPath;

  @JsonProperty("is_reference_field")
  private Boolean isReferenceField;

  @JsonProperty("default_precondition_evaluation")
  private Boolean defaultPreconditionEvaluation;

  @JsonProperty("negated")
  private Boolean negated;

  @JsonProperty("null_values")
  private List<String> nullValues;

  @JsonProperty("exceptional_values")
  private List<Object> exceptionalValues;

  protected Constraint() {}

  protected Constraint(String type) {
    this.type = type;
  }

  public String getValidator() {
    return validator;
  }

  public void setValidator(String validator) {
    this.validator = validator;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Precondition getPrecondition() {
    return precondition;
  }

  public void setPrecondition(Precondition precondition) {
    this.precondition = precondition;
  }

  public String getJsonPath() {
    return jsonPath;
  }

  public void setJsonPath(String jsonPath) {
    this.jsonPath = jsonPath;
  }

  public Boolean getIsReferenceField() {
    return isReferenceField;
  }

  public boolean isReferenceField() {
    return Boolean.TRUE.equals(isReferenceField);
  }

  public void setIsReferenceField(Boolean isReferenceField) {
    this.isReferenceField = isReferenceField;
  }

  public Boolean getDefaultPreconditionEvaluation() {
    return defaultPreconditionEvaluation;
  }

  public void setDefaultPreconditionEvaluation(Boolean defaultPreconditionEvaluation) {
    this.defaultPreconditionEvaluation = defaultPreconditionEvaluation;
  }

  public Boolean getNegated() {
    return negated;
  }

  public boolean isNegated() {
    return Boolean.TRUE.equals(negated);
  }

  public void setNegated(Boolean negated) {
    this.negated = negated;
  }

  public List<String> getNullValues() {
    return nullValues;
  }

  public void setNullValues(List<String> nullValues) {
    this.nullValues = nullValues;
  }

  public List<Object> getExceptionalValues() {
    return exceptionalValues;
  }

  public void setExceptionalValues(List<Object> exceptionalValues) {
    this.exceptionalValues = exceptionalValues;
  }
}
