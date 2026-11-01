from pathlib import Path
from typing import Any, Dict, Set

import orjson

from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.profile.profile_validator import validate_profile_file
from jsonprofile.validator.json_validator import JsonValidator


def test_shared_conformance_manifest_loads() -> None:
    manifest_path = Path(__file__).parents[2] / "shared/conformance/smoke.json"
    manifest: Dict[str, Any] = orjson.loads(manifest_path.read_bytes())

    assert manifest["version"] == "0.1.0"
    assert {case["id"] for case in manifest["cases"]} == {
        "minimal-valid-profile",
        "missing-required-profile-name",
    }

    for case in manifest["cases"]:
        fixture_path = (manifest_path.parent / case["fixture"]).resolve()
        valid, _ = validate_profile_file(fixture_path)
        assert valid is case["valid"], f"Failed smoke case {case['id']}"


def test_shared_validation_conformance_suite() -> None:
    suite_path = Path(__file__).parents[2] / "shared/conformance/validation_suite.json"
    suite: Dict[str, Any] = orjson.loads(suite_path.read_bytes())

    assert suite["version"] == "0.1.0"
    for case in suite["cases"]:
        referenced: Dict[str, Any] = {}
        parent_profile = case.get("parentProfile")
        if parent_profile:
            referenced[parent_profile["id"]] = parent_profile

        validator = JsonValidator(
            json_schema=case.get("schema"),
            profile=case["profile"],
            referenced_profiles=referenced,
        )
        runtime_config = ValidationRuntimeConfiguration(
            offline_mode=bool(case.get("offlineMode", False)),
            skip_jsonschema_validation=case.get("schema") is None,
        )
        result = validator.validate_dict(
            case["document"], runtime_config=runtime_config
        )

        actual_error_codes: Set[str] = {
            msg.code for messages in result.errors.values() for msg in messages
        }
        actual_rec_codes: Set[str] = {
            msg.code for messages in result.recommendations.values() for msg in messages
        }

        assert actual_error_codes == set(case["expectedErrorCodes"]), (
            f"Case {case['id']} error mismatch: {actual_error_codes}"
        )
        assert actual_rec_codes == set(case["expectedRecommendationCodes"]), (
            f"Case {case['id']} recommendation mismatch: {actual_rec_codes}"
        )
