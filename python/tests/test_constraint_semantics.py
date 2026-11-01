from __future__ import annotations

from typing import Any

import pytest
from pydantic import ValidationError

from jsonprofile.profile.base import CvTerm, ValidationRuntimeConfiguration
from jsonprofile.profile.constraints.constraints import (
    INT64_MAX,
    INT64_MIN,
    IntegerConstraint,
    IntegerEnumConstraint,
)
from jsonprofile.validator.base import CvTermSearch
from jsonprofile.validator.json_validator import JsonValidator
from jsonprofile.validator.opa_engine import OpaEngine, OpaEngineFactory


class FixedCvTermSearch(CvTermSearch):
    def __init__(self, valid: bool = True) -> None:
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
        matched_accession: str | None = None,
        allow_synonym_search: bool = False,
    ) -> CvTerm | None:
        return None

    def check_cv_term(
        self,
        cv_term: CvTerm,
        parent_cv_term: CvTerm | None = None,
        allow_synonym_search: bool = False,
    ) -> tuple[bool, str]:
        self.calls += 1
        return self.valid, "fixed CV result"

    def find_cv_term_with_accession(
        self, source: str, accession: str
    ) -> tuple[CvTerm | None, list[str]]:
        return None, []


def validate_constraint(
    constraint: dict[str, Any],
    value: Any,
    *,
    offline: bool = True,
    cv_search: FixedCvTermSearch | None = None,
    opa_engine_factory: OpaEngineFactory | None = None,
) -> bool:
    validator = JsonValidator(
        json_schema=None,
        profile={
            "id": "https://example.org/constraint-tests",
            "version": "1.0.0",
            "name": "Constraint tests",
            "requirements": {
                "$.value": {
                    "code": "CONSTRAINT-TEST",
                    "enforcement_level": "required",
                    "value_constraint": constraint,
                }
            },
        },
        default_cv_term_search=cv_search,
        opa_engine_factory=opa_engine_factory,
    )
    result = validator.validate_dict(
        {"value": value},
        ValidationRuntimeConfiguration(offline_mode=offline),
    )
    return not any(
        message.code == "CONSTRAINT-TEST"
        for messages in result.errors.values()
        for message in messages
    )


@pytest.mark.parametrize(
    ("constraint", "valid_value", "invalid_value"),
    [
        ({"type": "regex", "pattern": "^ok$"}, "ok", "no"),
        ({"type": "string-length", "minimum": 1, "maximum": 4}, "okay", ""),
        ({"type": "string-enum", "options": ["ok"]}, "ok", "no"),
        ({"type": "integer-enum", "options": {"2": "two"}}, 2, 3),
        ({"type": "integer", "minimum": 1, "maximum": 3}, 2, 0),
        ({"type": "positive-integer"}, 1, 0),
        ({"type": "non-negative-integer"}, 0, -1),
        ({"type": "boolean"}, True, "perhaps"),
        ({"type": "decimal", "minimum": "1", "maximum": "3"}, "2", "0"),
        ({"type": "time", "format": "%Y-%m-%d"}, "2024-02-29", "invalid"),
        (
            {"type": "email", "check_deliverability": False},
            "person@example.com",
            "invalid",
        ),
        ({"type": "uri"}, "https://example.com", "not a uri"),
        ({"type": "items-count", "min_occurs": 1, "max_occurs": 2}, ["x"], []),
    ],
)
def test_scalar_and_collection_constraints_null_exception_and_negation(
    constraint: dict[str, Any], valid_value: Any, invalid_value: Any
) -> None:
    assert not validate_constraint(constraint, None)
    assert validate_constraint({**constraint, "exceptional_values": [None]}, None)
    if constraint["type"] != "items-count":
        assert validate_constraint(
            {**constraint, "exceptional_values": ["SKIP"]}, "SKIP"
        )

    assert not validate_constraint({**constraint, "negated": True}, valid_value)
    assert validate_constraint({**constraint, "negated": True}, invalid_value)


@pytest.mark.parametrize(
    ("constraint", "at_min", "below_min", "at_max", "above_max"),
    [
        ({"type": "integer", "minimum": 2, "maximum": 4}, 2, 1, 4, 5),
        (
            {"type": "decimal", "minimum": "2.0", "maximum": "4.0"},
            "2.0",
            "1.9",
            "4.0",
            "4.1",
        ),
        (
            {"type": "string-length", "minimum": 2, "maximum": 4},
            "ab",
            "a",
            "abcd",
            "abcde",
        ),
        (
            {"type": "items-count", "min_occurs": 2, "max_occurs": 4},
            [1, 2],
            [1],
            [1, 2, 3, 4],
            [1, 2, 3, 4, 5],
        ),
    ],
)
def test_inclusive_minimum_and_maximum_boundaries(
    constraint: dict[str, Any],
    at_min: Any,
    below_min: Any,
    at_max: Any,
    above_max: Any,
) -> None:
    assert validate_constraint(constraint, at_min)
    assert not validate_constraint(constraint, below_min)
    assert validate_constraint(constraint, at_max)
    assert not validate_constraint(constraint, above_max)


def test_signed_64_bit_integer_policy() -> None:
    integer = {"type": "integer"}
    assert validate_constraint(integer, str(INT64_MIN))
    assert validate_constraint(integer, str(INT64_MAX))
    assert not validate_constraint(integer, str(INT64_MIN - 1))
    assert not validate_constraint(integer, str(INT64_MAX + 1))

    integer_enum = {
        "type": "integer-enum",
        "options": {str(INT64_MIN): "minimum", str(INT64_MAX): "maximum"},
    }
    assert validate_constraint(integer_enum, str(INT64_MIN))
    assert validate_constraint(integer_enum, str(INT64_MAX))
    assert not validate_constraint(integer_enum, str(INT64_MAX + 1))

    with pytest.raises(ValidationError):
        IntegerConstraint(minimum=INT64_MAX + 1)
    with pytest.raises(ValidationError):
        IntegerEnumConstraint(options={INT64_MAX + 1: "outside"})


def test_decimal_scientific_scale_range_and_non_finite_behavior() -> None:
    assert validate_constraint(
        {"type": "decimal", "minimum": "1000", "maximum": "1000"}, "1e3"
    )
    assert not validate_constraint(
        {
            "type": "decimal",
            "minimum": "1000",
            "maximum": "1000",
            "allow_scientific_notation": False,
        },
        "1e3",
    )
    assert validate_constraint(
        {
            "type": "decimal",
            "minimum": "1000",
            "maximum": "1000",
            "min_scale": 0,
            "max_scale": 0,
        },
        "1e3",
    )
    assert validate_constraint(
        {
            "type": "decimal",
            "minimum": "0.001",
            "maximum": "0.001",
            "min_scale": 3,
            "max_scale": 3,
        },
        "1e-3",
    )
    assert not validate_constraint({"type": "decimal"}, "1e-29")
    assert validate_constraint({"type": "decimal"}, "79228162514264337593543950335")
    assert not validate_constraint({"type": "decimal"}, "79228162514264337593543950336")
    assert not validate_constraint({"type": "decimal"}, "NaN")
    assert validate_constraint(
        {"type": "decimal", "allow_non_finite_values": True}, "NaN"
    )

    with pytest.raises(ValidationError):
        JsonValidator(
            json_schema=None,
            profile={
                "id": "https://example.org/decimal-bound",
                "version": "1.0.0",
                "name": "Decimal bound",
                "requirements": {
                    "$.value": {
                        "code": "DECIMAL-BOUND",
                        "value_constraint": {
                            "type": "decimal",
                            "minimum": "79228162514264337593543950336",
                        },
                    }
                },
            },
        )


@pytest.mark.parametrize(
    ("constraint", "value"),
    [
        ({"type": "decimal", "min_scale": 2, "max_scale": 2}, "1.20"),
        ({"type": "regex", "pattern": "^A[0-9]{2}$"}, "A01"),
        ({"type": "string-enum", "options": ["first", "last"]}, "last"),
        ({"type": "integer-enum", "options": {"1": "one", "3": "three"}}, 3),
    ],
)
def test_scale_regex_and_enum_boundaries(
    constraint: dict[str, Any], value: Any
) -> None:
    assert validate_constraint(constraint, value)


@pytest.mark.parametrize(
    ("offline", "search_valid", "expected_valid", "expected_calls"),
    [
        (True, True, True, 0),
        (False, True, True, 1),
        (False, False, False, 1),
    ],
)
def test_cv_term_online_and_offline_behavior(
    offline: bool, search_valid: bool, expected_valid: bool, expected_calls: int
) -> None:
    search = FixedCvTermSearch(search_valid)
    term = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term"}
    valid = validate_constraint(
        {"type": "cv-term"}, term, offline=offline, cv_search=search
    )
    assert valid is expected_valid
    assert search.calls == expected_calls


def test_cv_term_exception_source_and_value_requirement() -> None:
    search = FixedCvTermSearch(valid=False)
    constraint = {
        "type": "cv-term",
        "exceptional_cv_list": ["EX"],
        "is_cv_term_value_required": True,
    }
    missing_value = {
        "cv_label": "EX",
        "cv_accession": "EX:1",
        "name": "term",
        "value": "",
    }
    populated_value = {**missing_value, "value": "5"}

    assert not validate_constraint(constraint, missing_value, cv_search=search)
    assert validate_constraint(constraint, populated_value, cv_search=search)
    assert search.calls == 0
    assert not validate_constraint(
        {**constraint, "negated": True}, populated_value, cv_search=search
    )


def test_not_null_honors_exceptions_before_negation() -> None:
    constraint = {"type": "not-null", "exceptional_values": [None]}
    assert validate_constraint(constraint, None)
    assert not validate_constraint({**constraint, "negated": True}, None)
    assert not validate_constraint({"type": "not-null"}, None)
    assert validate_constraint(
        {
            "type": "not-null",
            "null_values": ["NA"],
            "exceptional_values": ["NA"],
        },
        "NA",
    )


def test_collection_type_error_is_inverted_by_negation() -> None:
    constraint = {"type": "items-count", "min_occurs": 1}
    assert not validate_constraint(constraint, "not-a-list")
    assert validate_constraint({**constraint, "negated": True}, "not-a-list")


def test_cv_list_online_results_apply_required_value_and_negation() -> None:
    constraint = {
        "type": "allowed-cv-list",
        "allowed_cv_list": ["MS"],
        "is_cv_term_value_required": True,
    }
    missing_value = {
        "cv_label": "MS",
        "cv_accession": "MS:1",
        "name": "term",
        "value": "",
    }
    populated_value = {**missing_value, "value": "5"}
    search = FixedCvTermSearch(valid=True)

    assert not validate_constraint(
        constraint, missing_value, offline=False, cv_search=search
    )
    assert not validate_constraint(
        {**constraint, "negated": True},
        populated_value,
        offline=False,
        cv_search=search,
    )
    invalid_search = FixedCvTermSearch(valid=False)
    assert validate_constraint(
        {**constraint, "negated": True},
        populated_value,
        offline=False,
        cv_search=invalid_search,
    )
    assert search.calls == 2
    assert invalid_search.calls == 1

    exceptional_constraint = {
        **constraint,
        "exceptional_cv_list": ["EX"],
    }
    exceptional_term = {
        "cv_label": "EX",
        "cv_accession": "EX:1",
        "name": "term",
        "value": "",
    }
    exceptional_search = FixedCvTermSearch(valid=False)
    assert not validate_constraint(
        exceptional_constraint,
        exceptional_term,
        offline=False,
        cv_search=exceptional_search,
    )
    assert not validate_constraint(
        {**exceptional_constraint, "negated": True},
        {**exceptional_term, "value": "5"},
        offline=False,
        cv_search=exceptional_search,
    )
    assert exceptional_search.calls == 0


def test_cv_term_enum_and_parent_cv_term() -> None:
    allowed = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term"}
    enum_constraint = {
        "type": "cv-term-enum",
        "allowed_cv_terms": [allowed],
    }
    assert validate_constraint(enum_constraint, allowed)
    assert not validate_constraint(enum_constraint, {**allowed, "name": "other"})
    assert validate_constraint({**enum_constraint, "exceptional_values": [None]}, None)
    assert not validate_constraint({**enum_constraint, "negated": True}, allowed)

    parent_constraint = {
        "type": "parent-cv-term",
        "parent_cv_terms": [
            {"cv_label": "MS", "cv_accession": "MS:ROOT", "name": "root"}
        ],
    }
    child = {**allowed, "value": ""}
    matching_search = FixedCvTermSearch(valid=True)
    assert validate_constraint(
        parent_constraint, child, offline=False, cv_search=matching_search
    )
    assert matching_search.calls == 1
    nonmatching_search = FixedCvTermSearch(valid=False)
    assert not validate_constraint(
        parent_constraint, child, offline=False, cv_search=nonmatching_search
    )
    assert validate_constraint(
        {**parent_constraint, "negated": True},
        child,
        offline=False,
        cv_search=nonmatching_search,
    )

    exceptional_parent = {
        **parent_constraint,
        "exceptional_cv_list": ["EX"],
        "is_cv_term_value_required": True,
    }
    exceptional_child = {**child, "cv_label": "EX", "cv_accession": "EX:1"}
    exceptional_search = FixedCvTermSearch(valid=False)
    assert not validate_constraint(
        exceptional_parent,
        exceptional_child,
        offline=False,
        cv_search=exceptional_search,
    )
    assert not validate_constraint(
        {**exceptional_parent, "negated": True},
        {**exceptional_child, "value": "5"},
        offline=False,
        cv_search=exceptional_search,
    )
    assert exceptional_search.calls == 0


def test_cv_term_value_and_constraint_group() -> None:
    term = {"cv_label": "MS", "cv_accession": "MS:1", "name": "term", "value": "2"}
    cv_value = {
        "type": "cv-term-value",
        "key_cv_term": {"cv_label": "MS", "cv_accession": "MS:1", "name": "term"},
        "value_constraint": {"type": "positive-integer"},
    }
    assert validate_constraint(cv_value, term)
    assert not validate_constraint({**cv_value, "negated": True}, term)
    assert validate_constraint(
        cv_value,
        {**term, "name": "different", "value": "not-matched"},
    )

    group = {
        "type": "group",
        "join_operator": "or",
        "min_valid": 1,
        "constraints": [
            {"type": "regex", "pattern": "^A$"},
            {"type": "regex", "pattern": "^B$"},
        ],
    }
    assert validate_constraint(group, "A")
    assert not validate_constraint(group, "C")
    assert validate_constraint({**group, "negated": True}, "C")


class FixedOpaEngine(OpaEngine):
    def __init__(self, valid: bool) -> None:
        self.valid = valid

    def evaluate(
        self,
        input_data: dict,
        entrypoint: str | int | None = None,
        data: dict | None = None,
    ) -> dict:
        return {"evaluation": self.valid, "message": "fixed OPA result"}


class FixedOpaEngineFactory(OpaEngineFactory):
    def __init__(self, valid: bool) -> None:
        self.engine = FixedOpaEngine(valid)

    def get_opa_engine(
        self, wasm_file_path: str, wasm_file_download_url: str | None = None
    ) -> OpaEngine:
        return self.engine


def test_opa_constraint_null_exception_negation_and_decision() -> None:
    constraint = {"type": "opa-policy", "policy_id": "policy_test"}
    assert validate_constraint({**constraint, "exceptional_values": [None]}, None)
    assert not validate_constraint(
        {**constraint, "negated": True, "exceptional_values": [None]}, None
    )
    assert validate_constraint(
        constraint, "value", opa_engine_factory=FixedOpaEngineFactory(True)
    )
    assert not validate_constraint(
        {**constraint, "negated": True},
        "value",
        opa_engine_factory=FixedOpaEngineFactory(True),
    )
    assert validate_constraint(
        {**constraint, "negated": True},
        "value",
        opa_engine_factory=FixedOpaEngineFactory(False),
    )


def test_custom_constraint_applies_common_null_exception_and_negation() -> None:
    constraint = {"type": "custom", "name": "orcid"}
    valid_orcid = "0000-0002-1825-0097"
    assert validate_constraint(constraint, valid_orcid)
    assert not validate_constraint({**constraint, "negated": True}, valid_orcid)
    assert validate_constraint({**constraint, "negated": True}, "invalid")
    assert validate_constraint({**constraint, "exceptional_values": [None]}, None)
    assert validate_constraint(
        {**constraint, "null_values": ["NA"], "negated": True}, "NA"
    )
