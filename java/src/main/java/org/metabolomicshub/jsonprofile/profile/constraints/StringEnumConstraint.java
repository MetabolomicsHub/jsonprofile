package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class StringEnumConstraint extends Constraint {
  @JsonProperty("options")
  @JsonDeserialize(using = OptionsDeserializer.class)
  private Set<String> options = new LinkedHashSet<>();

  public StringEnumConstraint() {
    super("string-enum");
  }

  public Set<String> getOptions() {
    return options;
  }

  public void setOptions(Set<String> options) {
    this.options = options != null ? options : new LinkedHashSet<>();
  }

  public static class OptionsDeserializer extends JsonDeserializer<Set<String>> {
    @Override
    public Set<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      Set<String> result = new LinkedHashSet<>();
      if (node == null || node.isNull()) {
        return result;
      }
      if (node.isArray()) {
        for (JsonNode item : node) {
          result.add(item.asText());
        }
      } else if (node.isObject()) {
        node.fieldNames().forEachRemaining(result::add);
      }
      return result;
    }
  }
}
