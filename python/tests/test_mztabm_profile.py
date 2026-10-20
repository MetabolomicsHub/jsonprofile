import logging
import pathlib

import orjson
import pytest

from jsonprofile.profile.base import EnforcementLevel, ValidationRuntimeConfiguration
from jsonprofile.utils import setup_basic_logging_config
from jsonprofile.validator import JsonValidator

logger = logging.getLogger(__name__)
DEFAULT_PROFILE_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json"
MTD_SMF_PROFILE_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_smf-profile-2.1.0-M.json"
MTD_SML_PROFILE_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_sml-profile-2.1.0-M.json"
FULL_PROFILE_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-full-profile-2.1.0-M.json"
METABOLIGHTS_PROFILE_ID = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json"

referenced_profiles = {
    DEFAULT_PROFILE_ID: "tests/resources/profiles/mztabm-default-profile-2.1.0-M.json",
    MTD_SMF_PROFILE_ID: "tests/resources/profiles/mztabm-mtd_smf-profile-2.1.0-M.json",
    MTD_SML_PROFILE_ID: "tests/resources/profiles/mztabm-mtd_sml-profile-2.1.0-M.json",
    FULL_PROFILE_ID: "tests/resources/profiles/mztabm-full-profile-2.1.0-M.json",
    METABOLIGHTS_PROFILE_ID: (
        "tests/resources/profiles/mztabm-metabolights-profile-2.1.0-M.json"
    ),
}


@pytest.mark.parametrize(
    "profile",
    [
        DEFAULT_PROFILE_ID,
        MTD_SMF_PROFILE_ID,
        FULL_PROFILE_ID,
        MTD_SML_PROFILE_ID,
        METABOLIGHTS_PROFILE_ID,
    ],
)
def test_example_profile(profile) -> None:
    setup_basic_logging_config()

    profile_path = referenced_profiles.get(profile)
    input_json = "tests/data/manual_null_MTBLS263.mztab.json"
    json_schema_path = "tests/resources/mztabm-2.1.0-M.schema.json"
    validator = JsonValidator(
        json_schema=json_schema_path,
        profile=profile_path,
        referenced_profiles=referenced_profiles,
    )

    runtime_config = ValidationRuntimeConfiguration()

    input_data = orjson.loads(pathlib.Path(input_json).read_bytes())
    result = validator.validate_dict(input_data, runtime_config=runtime_config)

    if result.errors:
        logger.info("%s", 120 * "-")
        logger.info("%s", profile)
        logger.info("PROFILE VALIDATION MESSAGES")
        logger.info("%s", 120 * "-")
        for _, messages in result.errors.items():
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
