package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class FieldRequirementGroup extends EnforcedRequirement {
  @JsonProperty("requirements")
  private List<EnforcedRequirement> requirements = new ArrayList<>();

  @JsonProperty("min_valid")
  private Integer minValid;

  @JsonProperty("max_valid")
  private Integer maxValid;

  public FieldRequirementGroup() {}

  public FieldRequirementGroup(
      String code, String description, List<EnforcedRequirement> requirements) {
    setCode(code);
    setDescription(description);
    this.requirements = requirements != null ? requirements : new ArrayList<>();
  }

  public List<EnforcedRequirement> getRequirements() {
    return requirements;
  }

  public void setRequirements(List<EnforcedRequirement> requirements) {
    this.requirements = requirements != null ? requirements : new ArrayList<>();
  }

  public Integer getMinValid() {
    return minValid;
  }

  public void setMinValid(Integer minValid) {
    this.minValid = minValid;
  }

  public Integer getMaxValid() {
    return maxValid;
  }

  public void setMaxValid(Integer maxValid) {
    this.maxValid = maxValid;
  }
}
