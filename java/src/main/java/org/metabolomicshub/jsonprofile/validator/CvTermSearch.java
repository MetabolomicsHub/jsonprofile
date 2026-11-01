package org.metabolomicshub.jsonprofile.validator;

import java.util.List;
import org.metabolomicshub.jsonprofile.profile.CvTerm;

public interface CvTermSearch {
  record CheckResult(boolean valid, String message) {}

  record TermWithSynonyms(CvTerm term, List<String> synonyms) {}

  String getIri(CvTerm cvTerm);

  String getCurie(CvTerm cvTerm);

  CvTerm findCvTerm(
      String source, String accessionOrLabel, String matchedAccession, boolean allowSynonymSearch);

  CheckResult checkCvTerm(CvTerm cvTerm, CvTerm parentCvTerm, boolean allowSynonymSearch);

  TermWithSynonyms findCvTermWithAccession(String source, String accession);
}
