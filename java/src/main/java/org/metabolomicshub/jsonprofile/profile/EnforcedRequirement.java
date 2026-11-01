package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = EnforcedRequirement.Deserializer.class)
public abstract class EnforcedRequirement {
  @JsonProperty("code")
  private String code;

  @JsonProperty("description")
  private String description;

  @JsonProperty("enforcement_level")
  private EnforcementLevel enforcementLevel = EnforcementLevel.REQUIRED;

  protected EnforcedRequirement() {}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public EnforcementLevel getEnforcementLevel() {
    return enforcementLevel != null ? enforcementLevel : EnforcementLevel.REQUIRED;
  }

  public void setEnforcementLevel(EnforcementLevel enforcementLevel) {
    this.enforcementLevel = enforcementLevel != null ? enforcementLevel : EnforcementLevel.REQUIRED;
  }

  public static EnforcedRequirement fromJsonNode(JsonNode node, ObjectMapper mapper)
      throws IOException {
    if (node == null || node.isNull()) {
      return null;
    }
    if (!node.isObject()) {
      throw new IOException("Expected object for EnforcedRequirement, got: " + node.getNodeType());
    }
    if (node.has("requirements")) {
      return mapper.treeToValue(node, FieldRequirementGroup.class);
    }
    if (node.has("policy_id")) {
      return mapper.treeToValue(node, OpaFieldRequirement.class);
    }
    return mapper.treeToValue(node, FieldRequirement.class);
  }

  public static class Deserializer extends JsonDeserializer<EnforcedRequirement> {
    @Override
    public EnforcedRequirement deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
      ObjectMapper mapper = (ObjectMapper) p.getCodec();
      JsonNode node = mapper.readTree(p);
      return fromJsonNode(node, mapper);
    }
  }
}
