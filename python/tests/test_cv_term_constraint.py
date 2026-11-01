from jsonprofile.profile.base import CvTerm, ValidationRuntimeConfiguration
from jsonprofile.validator.base import CvTermSearch
from jsonprofile.validator.json_validator import JsonValidator


class FixedCvTermSearch(CvTermSearch):
    def __init__(self, valid: bool):
        self.valid = valid
        self.calls = 0

    def get_iri(self, cv_term: CvTerm) -> str | None:
        return None

    def get_curie(self, cv_term: CvTerm) -> str | None:
        return None

    def find_cv_term(
        self,
        source: str,
        accession_or_label: str,
        matched_accession: None | str = None,
        allow_synonym_search: bool = False,
    ) -> None | CvTerm:
        return None

    def check_cv_term(
        self,
        cv_term: CvTerm,
        parent_cv_term: None | CvTerm = None,
        allow_synonym_search: bool = False,
    ) -> tuple[bool, str]:
        self.calls += 1
        return self.valid, "fixed test result"

    def find_cv_term_with_accession(
        self, source: str, accession: str
    ) -> tuple[CvTerm, list[str]]:
        return None, []


def validate_cv_term(
    term: dict,
    search: FixedCvTermSearch,
    *,
    negated: bool = False,
) -> tuple[bool, int]:
    validator = JsonValidator(
        json_schema=None,
        profile={
            "id": "https://example.org/cv-test",
            "version": "1.0.0",
            "name": "CV test profile",
            "requirements": {
                "$.term": {
                    "code": "CV-TEST",
                    "value_constraint": {
                        "type": "cv-term",
                        "is_cv_term_value_required": True,
                        "exceptional_cv_list": ["EX"],
                        "negated": negated,
                    },
                }
            },
        },
        default_cv_term_search=search,
    )
    result = validator.validate_dict(
        {"term": term},
        ValidationRuntimeConfiguration(offline_mode=False),
    )
    return "$.term" in result.errors, search.calls


def test_online_cv_lookup_still_enforces_required_value():
    search = FixedCvTermSearch(valid=True)
    term = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term", "value": ""}

    has_error, calls = validate_cv_term(term, search)

    assert has_error
    assert calls == 1


def test_online_cv_lookup_respects_negation():
    search = FixedCvTermSearch(valid=True)
    term = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term", "value": "x"}

    has_error, calls = validate_cv_term(term, search, negated=True)

    assert has_error
    assert calls == 1


def test_invalid_online_term_passes_negated_constraint():
    search = FixedCvTermSearch(valid=False)
    term = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term", "value": "x"}

    has_error, calls = validate_cv_term(term, search, negated=True)

    assert not has_error
    assert calls == 1


def test_exception_cv_source_enforces_value_and_negation():
    term = {"cv_label": "EX", "cv_accession": "EX:1", "name": "term", "value": ""}
    search = FixedCvTermSearch(valid=True)

    has_error, calls = validate_cv_term(term, search)

    assert has_error
    assert calls == 0

    has_error, calls = validate_cv_term(term, search, negated=True)

    assert not has_error
    assert calls == 0
