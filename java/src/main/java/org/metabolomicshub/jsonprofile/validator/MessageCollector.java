package org.metabolomicshub.jsonprofile.validator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.metabolomicshub.jsonprofile.profile.EnforcementLevel;
import org.metabolomicshub.jsonprofile.profile.JsonProfileMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCollector {
  private static final Logger logger = LoggerFactory.getLogger(MessageCollector.class);

  private final Integer maxMessagesForEachRequirement;
  private final Map<String, Map<String, List<JsonProfileMessage>>> messages = new LinkedHashMap<>();
  private final Map<String, List<JsonProfileMessage>> codeMessages = new LinkedHashMap<>();

  public MessageCollector() {
    this(null);
  }

  public MessageCollector(Integer maxMessagesForEachRequirement) {
    this.maxMessagesForEachRequirement = maxMessagesForEachRequirement;
  }

  public boolean addMessage(String jsonPath, JsonProfileMessage message) {
    String code = message.getCode() != null ? message.getCode() : "";
    if (!isOpen(code)) {
      messages
          .computeIfAbsent(jsonPath, k -> new LinkedHashMap<>())
          .computeIfAbsent(code, k -> new ArrayList<>())
          .add(message);

      List<JsonProfileMessage> byCode = codeMessages.computeIfAbsent(code, k -> new ArrayList<>());
      byCode.add(message);
      if (maxMessagesForEachRequirement != null && byCode.size() >= maxMessagesForEachRequirement) {
        logger.warn(
            "{} messages reached to {}. Circuit breaker activated for {}",
            code,
            maxMessagesForEachRequirement,
            code);
      }
      return true;
    }
    return false;
  }

  public boolean isOpen(String code) {
    if (maxMessagesForEachRequirement == null) {
      return false;
    }
    String key = code != null ? code : "";
    List<JsonProfileMessage> current = codeMessages.get(key);
    return current != null && current.size() >= maxMessagesForEachRequirement;
  }

  public Map<String, Map<String, List<JsonProfileMessage>>> getMessages() {
    return messages;
  }

  public JsonValidationResult processMessages() {
    Map<String, List<JsonProfileMessage>> errors = new LinkedHashMap<>();
    Map<String, List<JsonProfileMessage>> recommendations = new LinkedHashMap<>();
    Map<String, List<JsonProfileMessage>> optionals = new LinkedHashMap<>();

    for (Map.Entry<String, Map<String, List<JsonProfileMessage>>> entry : messages.entrySet()) {
      String k = entry.getKey();
      for (List<JsonProfileMessage> data : entry.getValue().values()) {
        for (JsonProfileMessage x : data) {
          EnforcementLevel level = x.getEnforcementLevel();
          if (level == EnforcementLevel.REQUIRED) {
            errors.computeIfAbsent(k, p -> new ArrayList<>()).add(x);
          } else if (level == EnforcementLevel.RECOMMENDED) {
            recommendations.computeIfAbsent(k, p -> new ArrayList<>()).add(x);
          } else if (level == EnforcementLevel.OPTIONAL) {
            optionals.computeIfAbsent(k, p -> new ArrayList<>()).add(x);
          }
        }
      }
    }
    return new JsonValidationResult(errors, recommendations, optionals);
  }
}
