package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonProfileConfiguration {
  @JsonProperty("cv_term_field_mapping")
  private CvTermFieldMapping cvTermFieldMapping = new CvTermFieldMapping();

  @JsonProperty("supported_cv_lists")
  private List<String> supportedCvLists;

  @JsonProperty("supported_cv_list_enforcement_level")
  private EnforcementLevel supportedCvListEnforcementLevel;

  @JsonProperty("custom_validator_definitions")
  private List<ProfileValidatorDefinition> customValidatorDefinitions;

  @JsonProperty("default_validator_key")
  private String defaultValidatorKey;

  @JsonProperty("profile_validator_factory_class")
  private Map<String, String> profileValidatorFactoryClass;

  @JsonProperty("profile_validator_factory_class_arguments")
  private Map<String, Object> profileValidatorFactoryClassArguments;

  @JsonProperty("default_wasm_file_key")
  private String defaultWasmFileKey;

  @JsonProperty("wasm_file_definitions")
  private Map<String, WasmFileDefinition> wasmFileDefinitions;

  @JsonProperty("object_unique_id_fields")
  private List<String> objectUniqueIdFields;

  public JsonProfileConfiguration() {}

  public void mergeFromParent(JsonProfileConfiguration parent) {
    if (parent == null) {
      return;
    }
    if (this.cvTermFieldMapping == null) {
      this.cvTermFieldMapping = parent.cvTermFieldMapping;
    }
    if (this.supportedCvLists == null) {
      this.supportedCvLists = parent.supportedCvLists;
    }
    if (this.supportedCvListEnforcementLevel == null) {
      this.supportedCvListEnforcementLevel = parent.supportedCvListEnforcementLevel;
    }
    if (this.customValidatorDefinitions == null) {
      this.customValidatorDefinitions = parent.customValidatorDefinitions;
    }
    if (this.defaultValidatorKey == null) {
      this.defaultValidatorKey = parent.defaultValidatorKey;
    }
    if (this.profileValidatorFactoryClass == null) {
      this.profileValidatorFactoryClass = parent.profileValidatorFactoryClass;
    }
    if (this.profileValidatorFactoryClassArguments == null) {
      this.profileValidatorFactoryClassArguments = parent.profileValidatorFactoryClassArguments;
    }
    if (this.defaultWasmFileKey == null) {
      this.defaultWasmFileKey = parent.defaultWasmFileKey;
    }
    if (this.wasmFileDefinitions == null) {
      this.wasmFileDefinitions = parent.wasmFileDefinitions;
    }
    if (this.objectUniqueIdFields == null) {
      this.objectUniqueIdFields = parent.objectUniqueIdFields;
    }
  }

  public CvTermFieldMapping getCvTermFieldMapping() {
    return cvTermFieldMapping;
  }

  public void setCvTermFieldMapping(CvTermFieldMapping cvTermFieldMapping) {
    this.cvTermFieldMapping = cvTermFieldMapping;
  }

  public List<String> getSupportedCvLists() {
    return supportedCvLists;
  }

  public void setSupportedCvLists(List<String> supportedCvLists) {
    this.supportedCvLists = supportedCvLists;
  }

  public EnforcementLevel getSupportedCvListEnforcementLevel() {
    return supportedCvListEnforcementLevel;
  }

  public void setSupportedCvListEnforcementLevel(EnforcementLevel supportedCvListEnforcementLevel) {
    this.supportedCvListEnforcementLevel = supportedCvListEnforcementLevel;
  }

  public List<ProfileValidatorDefinition> getCustomValidatorDefinitions() {
    return customValidatorDefinitions;
  }

  public void setCustomValidatorDefinitions(
      List<ProfileValidatorDefinition> customValidatorDefinitions) {
    this.customValidatorDefinitions = customValidatorDefinitions;
  }

  public String getDefaultValidatorKey() {
    return defaultValidatorKey;
  }

  public void setDefaultValidatorKey(String defaultValidatorKey) {
    this.defaultValidatorKey = defaultValidatorKey;
  }

  public Map<String, String> getProfileValidatorFactoryClass() {
    return profileValidatorFactoryClass;
  }

  public void setProfileValidatorFactoryClass(Map<String, String> profileValidatorFactoryClass) {
    this.profileValidatorFactoryClass = profileValidatorFactoryClass;
  }

  public Map<String, Object> getProfileValidatorFactoryClassArguments() {
    return profileValidatorFactoryClassArguments;
  }

  public void setProfileValidatorFactoryClassArguments(
      Map<String, Object> profileValidatorFactoryClassArguments) {
    this.profileValidatorFactoryClassArguments = profileValidatorFactoryClassArguments;
  }

  public String getDefaultWasmFileKey() {
    return defaultWasmFileKey;
  }

  public void setDefaultWasmFileKey(String defaultWasmFileKey) {
    this.defaultWasmFileKey = defaultWasmFileKey;
  }

  public Map<String, WasmFileDefinition> getWasmFileDefinitions() {
    return wasmFileDefinitions;
  }

  public void setWasmFileDefinitions(Map<String, WasmFileDefinition> wasmFileDefinitions) {
    this.wasmFileDefinitions = wasmFileDefinitions;
  }

  public List<String> getObjectUniqueIdFields() {
    return objectUniqueIdFields;
  }

  public void setObjectUniqueIdFields(List<String> objectUniqueIdFields) {
    this.objectUniqueIdFields = objectUniqueIdFields;
  }
}
