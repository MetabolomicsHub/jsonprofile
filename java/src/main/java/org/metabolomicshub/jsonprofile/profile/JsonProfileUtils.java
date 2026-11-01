package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class JsonProfileUtils {
  private static final ObjectMapper MAPPER =
      new ObjectMapper().registerModule(new JavaTimeModule());
  private static final Pattern WHITESPACE_CONTROL = Pattern.compile("[\\t\\n\\r]");
  private static final Pattern OTHER_CONTROL =
      Pattern.compile("[\\x00-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]");
  private static final Pattern MULTI_SPACE = Pattern.compile(" +");
  private static final Set<Character> SEPARATORS = Set.of(',', '|', '"');

  private JsonProfileUtils() {}

  public static ObjectMapper mapper() {
    return MAPPER;
  }

  public static String sanitizeStr(Object val) {
    if (val == null) {
      return "";
    }
    String sanitized = val.toString().trim();
    while (sanitized.startsWith("\"") && sanitized.length() > 1) {
      sanitized = sanitized.substring(1);
    }
    while (sanitized.endsWith("\"") && !sanitized.isEmpty()) {
      sanitized = sanitized.substring(0, sanitized.length() - 1);
    }
    sanitized = sanitized.trim();
    sanitized = WHITESPACE_CONTROL.matcher(sanitized).replaceAll(" ");
    sanitized = OTHER_CONTROL.matcher(sanitized).replaceAll("");
    sanitized = MULTI_SPACE.matcher(sanitized).replaceAll(" ");
    for (int i = 0; i < sanitized.length(); i++) {
      if (SEPARATORS.contains(sanitized.charAt(i))) {
        return "\"" + sanitized + "\"";
      }
    }
    return sanitized;
  }

  public static String toJsonPath(List<?> reference) {
    if (reference == null || reference.isEmpty()) {
      return "$";
    }
    StringBuilder sb = new StringBuilder("$");
    for (Object item : reference) {
      if (item instanceof Number) {
        sb.append("[").append(item).append("]");
      } else {
        sb.append(".").append(item);
      }
    }
    return sb.toString();
  }

  /**
   * Converts a Jayway JsonPath or JSON Pointer path to standard jsonprofile format (e.g. $.a[0].b).
   */
  public static String convertFullPath(String fullPath) {
    if (fullPath == null || fullPath.isEmpty() || "$".equals(fullPath)) {
      return "$";
    }
    String path = fullPath;
    // Convert Jayway bracket notation $['a'][0]['b'] -> $.a[0].b
    path = path.replaceAll("\\['([^']+)'\\]", ".$1");
    path = path.replace(".[", "[");
    path = path.replace("(", "").replace(")", "");
    if (path.startsWith("$.")) {
      return path;
    }
    if (path.startsWith("$[")) {
      return "$.[" + path.substring(2);
    }
    if (path.startsWith("$")) {
      path = path.substring(1);
    }
    if (path.startsWith(".")) {
      path = path.substring(1);
    }
    return path.isEmpty() ? "$" : "$." + path;
  }

  public static String instanceLocationToJsonPath(String instanceLocation) {
    if (instanceLocation == null || instanceLocation.isEmpty() || "$".equals(instanceLocation)) {
      return "$";
    }
    // NetworkNT JsonNodePath.toString() uses $.foo[0].bar format directly
    if (instanceLocation.startsWith("$")) {
      return instanceLocation;
    }
    if (instanceLocation.startsWith("/")) {
      String[] parts = instanceLocation.substring(1).split("/");
      StringBuilder sb = new StringBuilder("$");
      for (String p : parts) {
        if (p.matches("\\d+")) {
          sb.append("[").append(p).append("]");
        } else {
          sb.append(".").append(p);
        }
      }
      return sb.toString();
    }
    return "$." + instanceLocation;
  }

  public static String nodeToString(JsonNode node) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (node.isTextual()) {
      return node.asText();
    }
    return node.toString();
  }
}
