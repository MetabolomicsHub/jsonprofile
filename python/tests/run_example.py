import logging
import pathlib

import orjson
import pytest
from scripts.example_base_profile import (
    BASE_ANNOUNCEMENT_FILE_PROFILE_v1_0,
)
from scripts.example_legacy_profile import (
    LEGACY_ANNOUNCEMENT_FILE_PROFILE_v1_0,
)
from scripts.example_ms_profile import MS_ANNOUNCEMENT_FILE_PROFILE_v1_0

from jsonprofile.profile.base import (
    EnforcementLevel,
    ValidationRuntimeConfiguration,
)
from jsonprofile.validator.json_validator import JsonValidator

logger = logging.getLogger(__name__)


@pytest.fixture(scope="session")
def test_data_path():
    return pathlib.Path(__file__).parent / "example_01"


@pytest.fixture(scope="session")
def json_schema(test_data_path: str):
    return orjson.loads(
        pathlib.Path(f"{test_data_path}/example_schema.json").read_bytes()
    )


@pytest.fixture(scope="session")
def base_profile():
    return BASE_ANNOUNCEMENT_FILE_PROFILE_v1_0


@pytest.fixture(scope="session")
def legacy_profile():
    return LEGACY_ANNOUNCEMENT_FILE_PROFILE_v1_0


@pytest.fixture(scope="session")
def ms_profile():
    return MS_ANNOUNCEMENT_FILE_PROFILE_v1_0


@pytest.fixture(scope="session")
def ms_profile_validator(
    json_schema: dict, ms_profile, base_profile, legacy_profile
) -> JsonValidator:
    return JsonValidator(
        json_schema=json_schema,
        profile=ms_profile,
        referenced_profiles={
            BASE_ANNOUNCEMENT_FILE_PROFILE_v1_0.id: base_profile,
            LEGACY_ANNOUNCEMENT_FILE_PROFILE_v1_0.id: legacy_profile,
        },
    )


@pytest.fixture(scope="session")
def legacy_profile_validator(json_schema, legacy_profile: dict) -> JsonValidator:
    return JsonValidator(json_schema=json_schema, profile=legacy_profile)


@pytest.fixture(scope="session")
def default_validation_runtime_config() -> ValidationRuntimeConfiguration:
    return ValidationRuntimeConfiguration(skip_jsonschema_validation=False)


def test_validate_input_01(
    ms_profile_validator: JsonValidator,
    default_validation_runtime_config: ValidationRuntimeConfiguration,
    test_data_path: pathlib.Path,
):

    result = ms_profile_validator.validate_json_file(
        json_file_path=f"{test_data_path}/example_input_01.json",
        runtime_config=default_validation_runtime_config,
    )

    for category in (result.errors, result.recommendations):
        for code, messages in category.items():
            for message in messages:
                if message.enforcement_level == EnforcementLevel.REQUIRED:
                    logger.error(
                        "%s: %s: %s",
                        message.enforcement_level.name,
                        code,
                        message.message,
                    )
                else:
                    logger.warning(
                        "%s: %s: %s",
                        message.enforcement_level.name,
                        code,
                        message.message,
                    )
