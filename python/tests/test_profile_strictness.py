from pathlib import Path

import pytest
from pydantic import ValidationError

from jsonprofile.profile.model import JsonProfile
from jsonprofile.utils import validate_profile_schema


def minimal_profile() -> dict:
    return {
        "id": "https://example.org/profile",
        "version": "1.0.0",
        "name": "Strict profile",
        "requirements": {},
    }


@pytest.mark.parametrize(
    "change",
    [
        lambda profile: profile.update(unknown_profile_key=True),
        lambda profile: profile.update(configuration={"unknown_config_key": True}),
        lambda profile: profile.update(
            requirements={"$.value": {"code": "REQ-1", "unknown_requirement_key": True}}
        ),
        lambda profile: profile.update(
            requirements={
                "$.value": {
                    "code": "REQ-1",
                    "value_constraint": {"type": "regex", "pattern": "x", "typo": True},
                }
            }
        ),
    ],
)
def test_profile_models_reject_unknown_fields(change):
    profile = minimal_profile()
    change(profile)

    with pytest.raises(ValidationError):
        JsonProfile.model_validate(profile)


def test_profile_schema_rejects_unknown_fields():
    profile = minimal_profile()
    profile["typo"] = True

    with pytest.raises(Exception):
        validate_profile_schema(profile)


def test_explicit_dynamic_maps_allow_arbitrary_keys():
    profile = minimal_profile()
    profile["configuration"] = {
        "profile_validator_factory_class_arguments": {"extension_option": {"x": 1}}
    }
    profile["requirements"] = {
        "$.custom": {
            "code": "REQ-CUSTOM",
            "value_constraint": {
                "type": "custom",
                "name": "custom-check",
                "default_arguments": {"extension_option": {"x": 1}},
            },
        }
    }

    validate_profile_schema(profile)
    parsed = JsonProfile.model_validate(profile)

    assert parsed.configuration.profile_validator_factory_class_arguments[
        "extension_option"
    ] == {"x": 1}


def test_packaged_profile_schemas_match_shared_schema():
    repository_root = Path(__file__).resolve().parents[2]
    shared_schema = (
        repository_root / "shared/schema/jsonprofile.schema.json"
    ).read_bytes()
    for relative_path in (
        "python/src/jsonprofile/jsonprofile.schema.json",
        "java/src/main/resources/jsonprofile.schema.json",
        "rust/resources/jsonprofile.schema.json",
    ):
        assert (repository_root / relative_path).read_bytes() == shared_schema
