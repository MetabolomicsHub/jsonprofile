"""Validate the shared mzTab-M example with the Python implementation."""

from pathlib import Path

from jsonprofile.validator.json_validator import JsonValidator


def main() -> None:
    repository_root = Path(__file__).resolve().parents[4]
    resources = repository_root / "shared" / "resources" / "isa-json"
    profiles = resources / "profiles"
    default_profile_id = (
        "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json"
    )

    validator = JsonValidator(
        json_schema=None,
        profile=profiles / "metabolights-profile-0.1.0.json",
        referenced_profiles={
            default_profile_id: profiles / "mztabm-default-profile-2.1.0-M.json"
        },
    )
    result = validator.validate_json_file(resources / "metabolights-input-isa.json")

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
