use jsonprofile::{JsonValidator, ProfileSource, ValidationRuntimeConfiguration};
use std::collections::HashMap;
use std::path::Path;

fn main() -> Result<(), String> {
    let schema_path = Path::new("../../shared/resources/mztab-m/mztabm-2.1.0-M.schema.json");
    let profile_path = Path::new(
        "../../shared/resources/mztab-m/profiles/mztabm-metabolights-profile-2.1.0-M.json",
    );
    let input_path = Path::new("../../shared/resources/mztab-m/manual_null_MTBLS263.mztab.json");

    let profiles_dir = Path::new("../../shared/resources/mztab-m/profiles");
    let default_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";

    let mut refs: HashMap<String, ProfileSource> = HashMap::new();
    refs.insert(
        default_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-default-profile-2.1.0-M.json")),
    );

    let validator = JsonValidator::with_referenced_profiles(
        Some(ProfileSource::from(schema_path)),
        profile_path,
        refs.clone(),
    )?;

    let runtime_config = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        max_messages_for_each_requirement: Some(10),
        ..Default::default()
    };

    let result = validator.validate_json_file(input_path, Some(runtime_config))?;

    for (json_path, messages) in &result.errors {
        for msg in messages {
            println!("[REQUIRED] {} ({}): {}", msg.code, json_path, msg.message);
        }
    }
    Ok(())
}
