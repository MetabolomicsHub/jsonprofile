package org.metabolomicshub.jsonprofile.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.metabolomicshub.jsonprofile.profile.CvTerm;
import org.metabolomicshub.jsonprofile.profile.JsonProfileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlsCvTermSearch implements CvTermSearch {
  private static final Logger logger = LoggerFactory.getLogger(OlsCvTermSearch.class);
  private static final String OLS4_SEARCH_URL = "https://www.ebi.ac.uk/ols4/api/search";

  private final HttpClient httpClient =
      HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(10))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .build();

  private final Cache<String, CheckResult> searchCache =
      Caffeine.newBuilder().maximumSize(2048).expireAfterWrite(Duration.ofMinutes(10)).build();

  private final Cache<String, CvTerm> cvTermCache =
      Caffeine.newBuilder().maximumSize(2048).expireAfterWrite(Duration.ofMinutes(10)).build();

  @Override
  public String getCurie(CvTerm cvTerm) {
    if (cvTerm == null || cvTerm.getCvAccession() == null || cvTerm.getCvAccession().isEmpty()) {
      return null;
    }
    String acc = cvTerm.getCvAccession();
    if (acc.startsWith("http://purl.obolibrary.org/obo/")) {
      String tail = acc.substring("http://purl.obolibrary.org/obo/".length());
      return tail.replace('_', ':');
    }
    return acc;
  }

  @Override
  public String getIri(CvTerm cvTerm) {
    if (cvTerm == null || cvTerm.getCvAccession() == null || cvTerm.getCvAccession().isEmpty()) {
      return null;
    }
    String acc = cvTerm.getCvAccession();
    if (acc.startsWith("http://") || acc.startsWith("https://")) {
      return acc;
    }
    if (acc.contains(":")) {
      String[] parts = acc.split(":", 2);
      return "http://purl.obolibrary.org/obo/" + parts[0] + "_" + parts[1];
    }
    return null;
  }

  @Override
  public TermWithSynonyms findCvTermWithAccession(String source, String accession) {
    String curie = accession;
    if (curie == null || curie.isEmpty()) {
      return new TermWithSynonyms(null, List.of());
    }
    try {
      String query =
          "q="
              + encode(curie.toLowerCase(Locale.ROOT))
              + "&ontology="
              + encode(source.toLowerCase(Locale.ROOT))
              + "&type=class,property,individual&exact=true&format=json&start=0&rows=1"
              + "&local=false&obsoletes=false&lang=en"
              + "&queryFields=obo_id,iri,short_form"
              + "&fieldList=iri,obo_id,label,short_form,ontology_prefix,synonym";
      JsonNode root = queryOls(OLS4_SEARCH_URL + "?" + query);
      if (root == null) {
        return new TermWithSynonyms(null, List.of());
      }
      JsonNode docs = root.path("response").path("docs");
      if (!docs.isArray() || docs.isEmpty()) {
        return new TermWithSynonyms(null, List.of());
      }
      JsonNode first = docs.get(0);
      String label = first.path("label").asText("");
      List<String> synonyms = new ArrayList<>();
      JsonNode synNode = first.path("synonym");
      if (synNode.isArray()) {
        for (JsonNode s : synNode) {
          synonyms.add(s.asText());
        }
      }
      return new TermWithSynonyms(new CvTerm(source, accession, label, ""), synonyms);
    } catch (Exception ex) {
      logger.debug("OLS lookup failed for {} in {}: {}", accession, source, ex.getMessage());
      return new TermWithSynonyms(null, List.of());
    }
  }

  @Override
  public CvTerm findCvTerm(
      String source, String accessionOrLabel, String matchedAccession, boolean allowSynonymSearch) {
    String cacheKey =
        source + "|" + accessionOrLabel + "|" + matchedAccession + "|" + allowSynonymSearch;
    CvTerm cached = cvTermCache.getIfPresent(cacheKey);
    if (cached != null) {
      return cached;
    }
    if (source == null
        || source.isEmpty()
        || accessionOrLabel == null
        || accessionOrLabel.isEmpty()) {
      throw new IllegalArgumentException("Source and accessionOrLabel must be provided");
    }
    boolean isAccessionInput = accessionOrLabel.split(":").length == 2;
    String searchByAccession =
        matchedAccession != null ? matchedAccession : (isAccessionInput ? accessionOrLabel : null);
    if (searchByAccession != null) {
      TermWithSynonyms res = findCvTermWithAccession(source, searchByAccession);
      if (res.term() != null) {
        if (isAccessionInput) {
          cvTermCache.put(cacheKey, res.term());
          return res.term();
        } else {
          boolean synMatch =
              res.synonyms().stream().anyMatch(s -> s.equalsIgnoreCase(accessionOrLabel));
          if (res.term().getName().equalsIgnoreCase(accessionOrLabel) || synMatch) {
            cvTermCache.put(cacheKey, res.term());
            return res.term();
          }
        }
      }
    }
    return null;
  }

  @Override
  public CheckResult checkCvTerm(CvTerm cvTerm, CvTerm parentCvTerm, boolean allowSynonymSearch) {
    if (cvTerm == null
        || cvTerm.getCvAccession() == null
        || cvTerm.getCvAccession().isEmpty()
        || cvTerm.getName() == null
        || cvTerm.getName().isEmpty()
        || cvTerm.getCvLabel() == null
        || cvTerm.getCvLabel().isEmpty()) {
      return new CheckResult(false, "Invalid cv term " + cvTerm);
    }
    if (parentCvTerm == null) {
      CvTerm term =
          findCvTerm(cvTerm.getCvLabel(), cvTerm.getCvAccession(), null, allowSynonymSearch);
      if (term == null) {
        return new CheckResult(false, "CV term '" + cvTerm + "' is not found");
      }
      return new CheckResult(true, "");
    }
    String key = cvTerm + "|" + parentCvTerm + "|" + allowSynonymSearch;
    CheckResult cached = searchCache.getIfPresent(key);
    if (cached != null) {
      return cached;
    }
    try {
      String parentUri = getIri(parentCvTerm);
      String queryFields =
          allowSynonymSearch ? "obo_id,short_form,iri,synonym" : "obo_id,short_form,iri";
      String fieldList =
          allowSynonymSearch
              ? "iri,obo_id,label,short_form,ontology_prefix,synonym"
              : "iri,obo_id,label,short_form,ontology_prefix";
      String query =
          "q="
              + encode(cvTerm.getCvAccession().toLowerCase(Locale.ROOT))
              + "&ontology="
              + encode(cvTerm.getCvLabel().toLowerCase(Locale.ROOT))
              + "&type=class,property,individual&exact=true&start=0&obsoletes=false"
              + "&rows=1&format=json&lang=en"
              + "&queryFields="
              + encode(queryFields)
              + "&fieldList="
              + encode(fieldList)
              + (parentUri != null ? "&allChildrenOf=" + encode(parentUri) : "");
      JsonNode root = queryOls(OLS4_SEARCH_URL + "?" + query);
      if (root == null) {
        CheckResult r = new CheckResult(false, cvTerm.getCvAccession() + " does not match");
        searchCache.put(key, r);
        return r;
      }
      JsonNode docs = root.path("response").path("docs");
      if (docs.isArray() && !docs.isEmpty()) {
        JsonNode first = docs.get(0);
        String oboId = first.path("obo_id").asText("");
        String iri = first.path("iri").asText("");
        String label = first.path("label").asText("");
        boolean accMatch =
            cvTerm.getCvAccession().equalsIgnoreCase(oboId)
                || cvTerm.getCvAccession().equalsIgnoreCase(iri);
        boolean labelMatch = cvTerm.getName().equalsIgnoreCase(label);
        if (!labelMatch && allowSynonymSearch && first.path("synonym").isArray()) {
          for (JsonNode s : first.path("synonym")) {
            if (cvTerm.getName().equalsIgnoreCase(s.asText(""))) {
              labelMatch = true;
              break;
            }
          }
        }
        if (accMatch && labelMatch) {
          CheckResult ok = new CheckResult(true, null);
          searchCache.put(key, ok);
          return ok;
        }
      }
      CheckResult fail =
          new CheckResult(
              false,
              cvTerm.getName()
                  + " "
                  + cvTerm.getCvAccession()
                  + " is not child of "
                  + parentCvTerm.getCvAccession()
                  + " on source "
                  + parentCvTerm.getCvLabel()
                  + ".");
      searchCache.put(key, fail);
      return fail;
    } catch (Exception ex) {
      return new CheckResult(false, cvTerm.getCvAccession() + " search failed: " + ex.getMessage());
    }
  }

  private JsonNode queryOls(String fullUrl) throws Exception {
    HttpRequest req =
        HttpRequest.newBuilder(URI.create(fullUrl))
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();
    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (resp.statusCode() == 200 || resp.statusCode() == 201) {
      return JsonProfileUtils.mapper().readTree(resp.body());
    }
    return null;
  }

  private static String encode(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8);
  }
}
