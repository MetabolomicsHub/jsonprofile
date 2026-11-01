package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonProfileMessage {
  @JsonProperty("code")
  private String code = "";

  @JsonProperty("source")
  private String source;

  @JsonProperty("category")
  private Category category;

  @JsonProperty("name")
  private String name;

  @JsonProperty("message")
  private String message;

  @JsonProperty("enforcement_level")
  private EnforcementLevel enforcementLevel = EnforcementLevel.REQUIRED;

  public JsonProfileMessage() {}

  public JsonProfileMessage(
      String code,
      String source,
      Category category,
      String name,
      String message,
      EnforcementLevel enforcementLevel) {
    this.code = code != null ? code : "";
    this.source = source;
    this.category = category;
    this.name = name;
    this.message = message;
    this.enforcementLevel = enforcementLevel != null ? enforcementLevel : EnforcementLevel.REQUIRED;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code != null ? code : "";
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public EnforcementLevel getEnforcementLevel() {
    return enforcementLevel;
  }

  public void setEnforcementLevel(EnforcementLevel enforcementLevel) {
    this.enforcementLevel = enforcementLevel != null ? enforcementLevel : EnforcementLevel.REQUIRED;
  }

  @Override
  public String toString() {
    return "JsonProfileMessage{"
        + "code='"
        + code
        + '\''
        + ", source='"
        + source
        + '\''
        + ", category="
        + category
        + ", name='"
        + name
        + '\''
        + ", enforcementLevel="
        + enforcementLevel
        + ", message='"
        + message
        + '\''
        + '}';
  }
}
