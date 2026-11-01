package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = CvTerm.Deserializer.class)
public class CvTerm {
  @JsonProperty("cv_label")
  private String cvLabel = "";

  @JsonProperty("cv_accession")
  private String cvAccession = "";

  @JsonProperty("name")
  private String name = "";

  @JsonProperty("value")
  private String value = "";

  public CvTerm() {}

  public CvTerm(String cvLabel, String cvAccession, String name, String value) {
    this.cvLabel = cvLabel != null ? cvLabel : "";
    this.cvAccession = cvAccession != null ? cvAccession : "";
    this.name = name != null ? name : "";
    this.value = value != null ? value : "";
  }

  public static CvTerm fromObject(Object obj, CvTermFieldMapping mapping) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof CvTerm cv) {
      return cv;
    }
    if (obj instanceof BaseCvTerm base) {
      return new CvTerm(base.getCvLabel(), base.getCvAccession(), base.getName(), "");
    }
    CvTermFieldMapping map = mapping != null ? mapping : CvTermFieldMapping.DEFAULT;
    if (obj instanceof Map<?, ?> m) {
      String label = getString(m, "cv_label", map.getLabelField());
      String accession = getString(m, "cv_accession", map.getAccessionField());
      String name = getString(m, "name", map.getNameField());
      String val = getString(m, "value", map.getValueField());
      return new CvTerm(label, accession, name, val);
    }
    if (obj instanceof String s) {
      return parseBracketed(s);
    }
    return null;
  }

  private static String getString(Map<?, ?> m, String primary, String mapped) {
    Object v = m.get(primary);
    if (v == null && mapped != null) {
      v = m.get(mapped);
    }
    return v != null ? v.toString().trim() : "";
  }

  public static CvTerm parseBracketed(String str) {
    if (str == null) {
      return null;
    }
    String cleaned = str.trim();
    if (cleaned.startsWith("[")) {
      cleaned = cleaned.substring(1);
    }
    if (cleaned.endsWith("]")) {
      cleaned = cleaned.substring(0, cleaned.length() - 1);
    }
    String[] parts = cleaned.split(",", 4);
    String label = parts.length > 0 ? parts[0].trim() : "";
    String accession = parts.length > 1 ? parts[1].trim() : "";
    String name = parts.length > 2 ? parts[2].trim() : "";
    String val = parts.length > 3 ? parts[3].trim() : "";
    return new CvTerm(label, accession, name, val);
  }

  public BaseCvTerm toBaseCvTerm() {
    return new BaseCvTerm(cvLabel, cvAccession, name);
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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value != null ? value : "";
  }

  @Override
  public String toString() {
    return "["
        + JsonProfileUtils.sanitizeStr(cvLabel)
        + ", "
        + JsonProfileUtils.sanitizeStr(cvAccession)
        + ", "
        + JsonProfileUtils.sanitizeStr(name)
        + ", "
        + JsonProfileUtils.sanitizeStr(value)
        + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CvTerm that)) {
      return false;
    }
    return Objects.equals(this.toString(), that.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.toString());
  }

  public static class Deserializer extends JsonDeserializer<CvTerm> {
    @Override
    public CvTerm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      if (node == null || node.isNull()) {
        return null;
      }
      if (node.isTextual()) {
        return parseBracketed(node.asText());
      }
      if (node.isObject()) {
        String label = node.hasNonNull("cv_label") ? node.get("cv_label").asText("") : "";
        String accession =
            node.hasNonNull("cv_accession") ? node.get("cv_accession").asText("") : "";
        String name = node.hasNonNull("name") ? node.get("name").asText("") : "";
        String val = node.hasNonNull("value") ? node.get("value").asText("") : "";
        return new CvTerm(label, accession, name, val);
      }
      return null;
    }
  }
}
