import logging
import pathlib
from importlib import resources

import orjson

from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.utils import setup_basic_logging_config
from jsonprofile.validator.json_validator import JsonValidator

logger = logging.getLogger(__name__)

if __name__ == "__main__":
    setup_basic_logging_config(level=logging.WARNING)
    json_path = pathlib.Path(resources.files("scripts").joinpath("example_01"))
    ms_profile = orjson.loads(
        pathlib.Path(json_path / "announcement-v1.0.ms-profile.json").read_bytes()
    )
    legacy_profile = orjson.loads(
        pathlib.Path(json_path / "announcement-v1.0.legacy-profile.json").read_bytes()
    )
    base_profile = orjson.loads(
        pathlib.Path(json_path / "announcement-v1.0.base-profile.json").read_bytes()
    )
    json_schema = orjson.loads(
        pathlib.Path(json_path / "announcement-v1.0.schema.json").read_bytes()
    )
    pathlib.Path(json_path / "announcement-v1.0.schema.json")
    validator = JsonValidator(
        json_schema=json_schema,
        profile=ms_profile,
        referenced_profiles={
            base_profile.get("id"): base_profile,
            legacy_profile.get("id"): legacy_profile,
            ms_profile.get("id"): ms_profile,
        },
        # default_cv_term_search=mhd_cv_term_search,
    )
    runtime_config: ValidationRuntimeConfiguration = ValidationRuntimeConfiguration(
        skip_jsonschema_validation=False
    )

    input_json = orjson.loads(
        pathlib.Path(json_path / "example_input_01.json").read_bytes()
    )
    result = validator.validate_dict(
        input_json=input_json, runtime_config=runtime_config
    )

    for category in (result.errors, result.recommendations, result.optionals):
        for code, errors in category.items():
            for error in errors:
                logger.error(
                    "%s: %s: %s: %s",
                    error.enforcement_level.name,
                    error.source,
                    code,
                    error.message,
                )
