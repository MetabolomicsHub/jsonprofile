from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.profile.model import JsonProfile
from jsonprofile.validator.json_validator import JsonValidator


def _profile(**updates) -> JsonProfile:
    profile = {
        "id": "https://example.org/profile",
        "version": "1.0.0",
        "name": "Test profile",
        "requirements": {
            "$.value": {
                "code": "REQ-1",
                "description": "Value description",
                "value_constraint": {"type": "string-length", "minimum": 1},
            }
        },
    }
    profile.update(updates)
    return JsonProfile.model_validate(profile)


def test_profile_extension_does_not_mutate_or_share_inputs():
    parent = _profile(
        id="https://example.org/parent",
        requirements={
            "$.parent": {
                "code": "PARENT-1",
                "value_constraint": {"type": "string-length", "minimum": 2},
            }
        },
        configuration={"object_unique_id_fields": ["id"]},
    )
    child = _profile(
        id="https://example.org/child",
        extends=parent.id,
        requirements={
            "$.child": {
                "code": "CHILD-1",
                "value_constraint": {"type": "string-length", "minimum": 3},
            }
        },
    )
    parent_before = parent.model_dump()
    child_before = child.model_dump()

    merged = JsonValidator.extend_profile(parent, child)
    merged.requirements["$.parent"].value_constraint.minimum = 9
    merged.configuration.object_unique_id_fields.append("other")

    assert parent.model_dump() == parent_before
    assert child.model_dump() == child_before


def test_validation_does_not_mutate_profile_or_runtime_configuration():
    profile = _profile()
    profile_before = profile.model_dump()
    runtime_config = ValidationRuntimeConfiguration()

    validator = JsonValidator(json_schema=None, profile=profile)
    validator.validate_dict({"value": "ok"}, runtime_config=runtime_config)

    assert profile.model_dump() == profile_before
    assert runtime_config.skipped_requirements is None
    assert (
        validator.json_profile.requirements["$.value"].value_constraint.description
        is None
    )
