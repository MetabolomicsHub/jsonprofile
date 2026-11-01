package org.metabolomicshub.jsonprofile.validator.checkers;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;
import org.metabolomicshub.jsonprofile.profile.constraints.Constraint;
import org.metabolomicshub.jsonprofile.validator.ConstraintChecker;
import org.metabolomicshub.jsonprofile.validator.JsonProfileRunContext;
import org.metabolomicshub.jsonprofile.validator.ProfileValidator;

public final class CustomConstraintCheckers {
  private CustomConstraintCheckers() {}

  public static void registerAll(ProfileValidator validator) {
    validator.registerChecker("custom", "orcid", new OrcidChecker());
    validator.registerChecker("custom", "accessible-url", new AccessibleUrlChecker());
  }

  public static class OrcidChecker extends ConstraintChecker {
    private static final Pattern ORCID_PATTERN =
        Pattern.compile("^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$");

    @Override
    public CheckResult validate(
        Constraint constraint,
        Object value,
        Map<String, Object> root,
        JsonProfileRunContext context) {
      if (!(value instanceof String str)) {
        return new CheckResult(false, "Value must be a string");
      }
      if (!ORCID_PATTERN.matcher(str).matches()) {
        return new CheckResult(
            false, "Value '" + str + "' is not in ORCID format: XXXX-XXXX-XXXX-XXXX");
      }
      return new CheckResult(true, null);
    }
  }

  public static class AccessibleUrlChecker extends ConstraintChecker {
    private final HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final Cache<String, CheckResult> urlCache =
        Caffeine.newBuilder().maximumSize(2048).expireAfterWrite(Duration.ofMinutes(10)).build();

    @Override
    public CheckResult validate(
        Constraint constraint,
        Object value,
        Map<String, Object> root,
        JsonProfileRunContext context) {
      if (!(value instanceof String url)) {
        return new CheckResult(false, "Value must be a string");
      }
      if (context.getRuntimeConfig() != null && context.getRuntimeConfig().isOfflineMode()) {
        return new CheckResult(true, "Offline mode");
      }
      return urlCache.get(url, this::checkHttpUrl);
    }

    private CheckResult checkHttpUrl(String url) {
      try {
        HttpRequest req =
            HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10)).GET().build();
        HttpResponse<Void> resp = httpClient.send(req, HttpResponse.BodyHandlers.discarding());
        int code = resp.statusCode();
        if (code != 200 && code != 201) {
          return new CheckResult(false, "URL '" + url + "' is not accessible: " + code);
        }
        return new CheckResult(true, null);
      } catch (Exception ex) {
        return new CheckResult(false, "URL '" + url + "' is not accessible: " + ex.getMessage());
      }
    }
  }
}
