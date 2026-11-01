import json
from pathlib import Path
from typing import Dict

from jsonprofile.validator import JsonValidator

if __name__ == "__main__":
    profile_paths = {
        "../shared/resources/isa-json/profiles": (
            "../shared/resources/isa-json/profiles-summary"
        ),
        "../shared/resources/mhd-announcement/profiles": (
            "../shared/resources/mhd-announcement/profiles-summary"
        ),
        "../shared/resources/mztab-m/profiles": (
            "../shared/resources/mztab-m/profiles-summary"
        ),
    }
    referenced_profiles: Dict[str, str] = {}
    profile_summary_path: Dict[str, str] = {}
    for profiles_path_str, summary_path_str in profile_paths.items():
        profiles_path = Path(profiles_path_str)
        for file in profiles_path.glob("*.json"):
            data = json.loads(file.read_text())
            id_ = data.get("id")
            if not id_:
                continue
            referenced_profiles[id_] = str(file.resolve())
            file_base = file.stem
            report_name = f"{file_base}-summary.md"
            profile_summary_path[id_] = Path(summary_path_str) / report_name
    for profile_id, profile_path in referenced_profiles.items():
        summary_path = profile_summary_path[profile_id]
        validator = JsonValidator(
            json_schema=None,
            profile=profile_path,
            referenced_profiles=referenced_profiles,
        )
        summary_path.parent.mkdir(parents=True, exist_ok=True)
        validator.create_markdown_file(summary_path)
