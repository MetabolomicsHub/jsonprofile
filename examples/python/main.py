"""Validate the shared mzTab-M example with the Python implementation."""

from pathlib import Path

from jsonprofile.profile.base import ValidationRuntimeConfiguration
from jsonprofile.validator.json_validator import JsonValidator


def main() -> None:
    repository_root = Path(__file__).resolve().parents[2]
    resources = repository_root / "shared" / "resources" / "mztab-m"
    profiles = resources / "profiles"
    default_profile_id = (
        "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/"
        "mztabm-default-profile-2.1.0-M.json"
    )

    validator = JsonValidator(
        json_schema=resources / "mztabm-2.1.0-M.schema.json",
        profile=profiles / "mztabm-metabolights-profile-2.1.0-M.json",
        referenced_profiles={
            default_profile_id: profiles / "mztabm-default-profile-2.1.0-M.json"
        },
    )
    result = validator.validate_json_file(
        resources / "manual_null_MTBLS263.mztab.json",
        runtime_config=ValidationRuntimeConfiguration(
            offline_mode=True,
            max_messages_for_each_requirement=10,
        ),
    )

    for label, messages_by_path in (
        ("REQUIRED", result.errors),
        ("RECOMMENDED", result.recommendations),
        ("OPTIONAL", result.optionals),
    ):
        print(f"{label} ({len(messages_by_path)} paths):")  # noqa: T201
        for json_path, messages in sorted(messages_by_path.items()):
            for message in messages:
                print(  # noqa: T201
                    f"  [{label}] {message.code}\t{json_path}\t{message.message}"
                )


if __name__ == "__main__":
    main()
