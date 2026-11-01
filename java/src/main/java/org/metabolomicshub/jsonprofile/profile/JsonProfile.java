package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonProfile {
  @JsonProperty("id")
  private String id;

  @JsonProperty("extends")
  private String extendsProfile;

  @JsonProperty("version")
  private String version;

  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  @JsonProperty("configuration")
  private JsonProfileConfiguration configuration;

  @JsonProperty("requirements")
  private Map<String, EnforcedRequirement> requirements = new LinkedHashMap<>();

  public JsonProfile() {}

  public static String name() {
    return "jsonprofile";
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getExtends() {
    return extendsProfile;
  }

  public void setExtends(String extendsProfile) {
    this.extendsProfile = extendsProfile;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
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

  public JsonProfileConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(JsonProfileConfiguration configuration) {
    this.configuration = configuration;
  }

  public Map<String, EnforcedRequirement> getRequirements() {
    return requirements;
  }

  public void setRequirements(Map<String, EnforcedRequirement> requirements) {
    this.requirements = requirements != null ? requirements : new LinkedHashMap<>();
  }
}
