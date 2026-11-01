import logging
import pathlib
from importlib import resources

import orjson

from jsonprofile.profile.model import JsonProfile
from jsonprofile.utils import setup_basic_logging_config

logger = logging.getLogger(__name__)


if __name__ == "__main__":
    setup_basic_logging_config(level=logging.WARNING)
    json_path = pathlib.Path(
        resources.files("jsonprofile").joinpath("jsonprofile.schema.json")
    )
    schema = JsonProfile.model_json_schema(by_alias=True)
    schema_bytes = orjson.dumps(schema, option=orjson.OPT_INDENT_2)
    json_path.write_bytes(schema_bytes)
    shared_schema_path = (
        pathlib.Path(__file__).resolve().parents[3]
        / "shared"
        / "schema"
        / "jsonprofile.schema.json"
    )
    if shared_schema_path.parent.exists():
        shared_schema_path.write_bytes(schema_bytes)

    for language_schema_path in (
        shared_schema_path.parents[2]
        / "java"
        / "src"
        / "main"
        / "resources"
        / "jsonprofile.schema.json",
        shared_schema_path.parents[2]
        / "rust"
        / "resources"
        / "jsonprofile.schema.json",
    ):
        language_schema_path.parent.mkdir(parents=True, exist_ok=True)
        language_schema_path.write_bytes(schema_bytes)
