import logging
import pathlib

import orjson
import pytest

from jsonprofile.profile.base import EnforcementLevel, ValidationRuntimeConfiguration
from jsonprofile.utils import setup_basic_logging_config
from jsonprofile.validator import JsonValidator

logger = logging.getLogger(__name__)

METABOLIGHTS_PROFILE_ID = (
    "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json"
)
METABOLIGHTS_BH_EXTENDED_PROFILE_ID = (
    "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json"
)
referenced_profiles = {
    METABOLIGHTS_PROFILE_ID: (
        "../shared/resources/isa-json/profiles/metabolights-profile-0.1.0.json"
    ),
    METABOLIGHTS_BH_EXTENDED_PROFILE_ID: (
        "../shared/resources/isa-json/profiles/"
        "metabolights-bh-extended-profile-0.1.0.json"
    ),
}


@pytest.mark.parametrize(
    "profile",
    [METABOLIGHTS_PROFILE_ID, METABOLIGHTS_BH_EXTENDED_PROFILE_ID],
)
def test_isa_json_profile_01(profile) -> None:
    setup_basic_logging_config()

    profile_path = pathlib.Path(referenced_profiles.get(profile))
    input_json = "../shared/resources/isa-json/metabolights-input-isa.json"
    validator = JsonValidator(
        json_schema=None,
        profile=profile_path,
        referenced_profiles=referenced_profiles,
    )

    runtime_config = ValidationRuntimeConfiguration(skip_jsonschema_validation=True)

    input_data = orjson.loads(pathlib.Path(input_json).read_bytes())
    result = validator.validate_dict(input_data, runtime_config=runtime_config)

    if result.errors:
        logger.info("%s", 120 * "-")
        logger.info("%s", profile)
        logger.info("PROFILE VALIDATION MESSAGES")
        logger.info("%s", 120 * "-")
        for items in (result.errors, result.recommendations, result.optionals):
            for _, messages in items.items():
                for _, message in enumerate(messages):
                    enforcement_level = message.enforcement_level
                    if message.enforcement_level == EnforcementLevel.REQUIRED:
                        logger.error(
                            "%s\t%s\t%s\t%s",
                            enforcement_level.name,
                            message.code or "-",
                            message.source,
                            message.message,
                        )
                    elif message.enforcement_level == EnforcementLevel.RECOMMENDED:
                        logger.warning(
                            "%s\t%s\t%s\t%s",
                            enforcement_level.name,
                            message.code or "-",
                            message.source,
                            message.message,
                        )
                    else:
                        logger.info(
                            "%s\t%s\t%s\t%s",
                            enforcement_level.name,
                            message.code or "-",
                            message.source,
                            message.message,
                        )
        logger.info("%s", 120 * "-")
    else:
        logger.info("%s", 120 * "-")
        logger.info("%s", profile)
        logger.info("SUCCESS - NO PROFILE VALIDATION MESSAGE")
        logger.info("%s", 120 * "-")
    if profile == METABOLIGHTS_PROFILE_ID:
        assert len(result.errors) >= 13
    else:
        assert len(result.errors) >= 5
    assert set(result.recommendations.keys()) == {
        "$.studies[0].protocols[8].protocolType",
        "$.studies[0].protocols[9].protocolType",
        "$.studies[0].protocols[10].protocolType",
        "$.studies[0].protocols[11].protocolType",
        "$.studies[0].protocols[12].protocolType",
    }
    assert not result.optionals


@pytest.mark.parametrize(
    "profile",
    [METABOLIGHTS_PROFILE_ID, METABOLIGHTS_BH_EXTENDED_PROFILE_ID],
)
def test_isa_json_profile_offline_parity(profile: str) -> None:
    input_json = "../shared/resources/isa-json/metabolights-input-isa.json"
    validator = JsonValidator(
        json_schema=None,
        profile=referenced_profiles[profile],
        referenced_profiles=dict(referenced_profiles),
    )
    runtime_config = ValidationRuntimeConfiguration(
        offline_mode=True,
        skip_jsonschema_validation=True,
    )
    input_data = orjson.loads(pathlib.Path(input_json).read_bytes())
    result = validator.validate_dict(input_data, runtime_config=runtime_config)

    if profile == METABOLIGHTS_PROFILE_ID:
        assert set(result.errors.keys()) == {
            "$.studies[0].assays[0].measurementType",
            "$.studies[0].characteristicCategories[2].characteristicType",
            "$.studies[0].description",
            "$.studies[0].filename",
            "$.studies[0].identifier",
            "$.studies[0].materials.sources[0]",
            "$.studies[0].materials.sources[1]",
            "$.studies[0].protocols[2].description",
            "$.studies[0].protocols[3].description",
            "$.studies[0].protocols[12].description",
            "$.studies[0].protocols[13].description",
            "$.studies[0].protocols[14].description",
            "$.studies[0].studyDesignDescriptors",
        }
    else:
        assert set(result.errors.keys()) == {
            "$.studies[0].assays[0].measurementType",
            "$.studies[0].characteristicCategories[2].characteristicType",
            "$.studies[0].description",
            "$.studies[0].materials.sources[0]",
            "$.studies[0].materials.sources[1]",
        }
    assert set(result.recommendations.keys()) == {
        "$.studies[0].protocols[8].protocolType",
        "$.studies[0].protocols[9].protocolType",
        "$.studies[0].protocols[10].protocolType",
        "$.studies[0].protocols[11].protocolType",
        "$.studies[0].protocols[12].protocolType",
    }
    assert not result.optionals
