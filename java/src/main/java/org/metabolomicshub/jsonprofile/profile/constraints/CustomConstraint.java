package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class CustomConstraint extends Constraint {
  @JsonProperty("default_arguments")
  private Map<String, Object> defaultArguments;

  @JsonProperty("value_jsonpath_arguments")
  private Map<String, String> valueJsonpathArguments;

  @JsonProperty("root_jsonpath_arguments")
  private Map<String, String> rootJsonpathArguments;

  public CustomConstraint() {
    super("custom");
  }

  public Map<String, Object> getDefaultArguments() {
    return defaultArguments;
  }

  public void setDefaultArguments(Map<String, Object> defaultArguments) {
    this.defaultArguments = defaultArguments;
  }

  public Map<String, String> getValueJsonpathArguments() {
    return valueJsonpathArguments;
  }

  public void setValueJsonpathArguments(Map<String, String> valueJsonpathArguments) {
    this.valueJsonpathArguments = valueJsonpathArguments;
  }

  public Map<String, String> getRootJsonpathArguments() {
    return rootJsonpathArguments;
  }

  public void setRootJsonpathArguments(Map<String, String> rootJsonpathArguments) {
    this.rootJsonpathArguments = rootJsonpathArguments;
  }
}
