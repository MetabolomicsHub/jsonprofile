import logging
import pathlib

import orjson
import pytest
from scripts.profiles.isa_json.metabolights_profile import METABOLIGHTS_BASE_PROFILE

from jsonprofile.profile.base import EnforcementLevel, ValidationRuntimeConfiguration
from jsonprofile.utils import setup_basic_logging_config
from jsonprofile.validator import JsonValidator

logger = logging.getLogger(__name__)

METABOLIGHTS_PROFILE_ID = (
    "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json"
)
referenced_profiles = {
    METABOLIGHTS_PROFILE_ID: "tests/resources/isa-json/metabolights-profile-0.1.0.json"
}


@pytest.mark.parametrize("profile", [METABOLIGHTS_PROFILE_ID])
def test_isa_json_profile_01(profile) -> None:
    setup_basic_logging_config()

    profile_path = pathlib.Path(referenced_profiles.get(profile))
    schema = METABOLIGHTS_BASE_PROFILE.model_dump(by_alias=True, exclude_none=True)
    profile_path.write_bytes(orjson.dumps(schema, option=orjson.OPT_INDENT_2))
    input_json = "tests/data/metabolights-input-isa.json"
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
    # assert not result.errors
