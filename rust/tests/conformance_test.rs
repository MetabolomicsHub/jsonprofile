use jsonprofile::{
    register_constraint_checker, register_profile_validator, validate_profile_file, Constraint,
    ConstraintChecker, DefaultProfileValidator, JsonProfileRunContext, JsonValidator, OpaEngine,
    OpaEngineFactory, ProfileSource, ValidationRuntimeConfiguration,
};
use serde_json::Value;
use std::collections::{BTreeSet, HashMap, HashSet};
use std::fs;
use std::path::{Path, PathBuf};
use std::sync::Arc;

fn repo_root() -> PathBuf {
    Path::new(env!("CARGO_MANIFEST_DIR"))
        .parent()
        .expect("Failed to locate repo root")
        .to_path_buf()
}

#[test]
fn test_smoke_fixture() {
    let root = repo_root();
    let smoke_path = root.join("shared/conformance/smoke.json");
    let content = fs::read_to_string(&smoke_path).expect("Failed to read smoke.json");
    let root_val: Value = serde_json::from_str(&content).expect("Failed to parse smoke.json");
    assert_eq!(root_val["version"], "0.1.0");
    let cases = root_val["cases"].as_array().expect("cases must be array");
    assert_eq!(cases.len(), 2);

    let smoke_dir = smoke_path.parent().unwrap();
    for tc in cases {
        let id = tc["id"].as_str().unwrap();
        let expected_valid = tc["valid"].as_bool().unwrap();
        let fixture_rel = tc["fixture"].as_str().unwrap();
        let profile_path = smoke_dir.join(fixture_rel);
        let (valid, msgs) = validate_profile_file(&profile_path);
        assert_eq!(
            valid, expected_valid,
            "Unexpected validity for {id}: {msgs:?}"
        );
    }
}

#[test]
fn test_shared_conformance_validation_suite() {
    let root = repo_root();
    let suite_path = root.join("shared/conformance/validation_suite.json");
    let content = fs::read_to_string(&suite_path).expect("Failed to read validation_suite.json");
    let suite: Value =
        serde_json::from_str(&content).expect("Failed to parse validation_suite.json");
    assert_eq!(suite["version"], "0.1.0");
    let cases = suite["cases"].as_array().expect("cases must be array");
    assert!(!cases.is_empty());

    for tc in cases {
        let case_id = tc["id"].as_str().unwrap();
        let schema_src = tc
            .get("schema")
            .filter(|v| !v.is_null())
            .cloned()
            .map(ProfileSource::Json);
        let profile = tc["profile"].clone();
        let mut ref_profiles: HashMap<String, ProfileSource> = HashMap::new();
        if let Some(parent_profile) = tc.get("parentProfile").filter(|v| !v.is_null()) {
            let parent_id = parent_profile["id"].as_str().unwrap().to_string();
            ref_profiles.insert(parent_id, ProfileSource::Json(parent_profile.clone()));
        }

        let runtime_cfg = ValidationRuntimeConfiguration {
            offline_mode: Some(
                tc.get("offlineMode")
                    .and_then(|v| v.as_bool())
                    .unwrap_or(false),
            ),
            skip_jsonschema_validation: Some(schema_src.is_none()),
            ..Default::default()
        };

        let validator = JsonValidator::with_referenced_profiles(schema_src, profile, ref_profiles)
            .unwrap_or_else(|e| panic!("Case {case_id} failed to create validator: {e}"));
        let result = validator.validate_dict(&tc["document"], Some(runtime_cfg));

        let mut actual_err_codes = BTreeSet::new();
        for msgs in result.errors.values() {
            for m in msgs {
                actual_err_codes.insert(m.code.clone());
            }
        }
        let expected_err_codes: BTreeSet<String> = tc["expectedErrorCodes"]
            .as_array()
            .unwrap()
            .iter()
            .map(|x| x.as_str().unwrap().to_string())
            .collect();
        assert_eq!(
            actual_err_codes, expected_err_codes,
            "Case {case_id} error_codes mismatch. Full errors: {:?}",
            result.errors
        );

        let mut actual_rec_codes = BTreeSet::new();
        for msgs in result.recommendations.values() {
            for m in msgs {
                actual_rec_codes.insert(m.code.clone());
            }
        }
        let expected_rec_codes: BTreeSet<String> = tc["expectedRecommendationCodes"]
            .as_array()
            .unwrap()
            .iter()
            .map(|x| x.as_str().unwrap().to_string())
            .collect();
        assert_eq!(
            actual_rec_codes, expected_rec_codes,
            "Case {case_id} recommendation_codes mismatch"
        );
    }
}

#[test]
fn test_example01_profiles() {
    let root = repo_root();
    let ex_dir = root.join("python/src/scripts/example_01");
    let schema_path = ex_dir.join("announcement-v1.0.schema.json");
    let base_profile = ex_dir.join("announcement-v1.0.base-profile.json");
    let legacy_profile = ex_dir.join("announcement-v1.0.legacy-profile.json");
    let ms_profile = ex_dir.join("announcement-v1.0.ms-profile.json");
    let input_json_path = ex_dir.join("example_input_01.json");

    let mut refs: HashMap<String, ProfileSource> = HashMap::new();
    refs.insert(
        "https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.base-profile.json".to_string(),
        ProfileSource::Path(base_profile),
    );
    refs.insert(
        "https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.legacy-profile.json".to_string(),
        ProfileSource::Path(legacy_profile),
    );

    let validator = JsonValidator::with_referenced_profiles(
        Some(ProfileSource::Path(schema_path)),
        ProfileSource::Path(ms_profile),
        refs,
    )
    .expect("Failed to create ms-profile validator");

    let rt = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        ..Default::default()
    };
    let res = validator
        .validate_json_file(input_json_path, Some(rt))
        .expect("Failed to validate example_input_01.json");
    assert!(
        !res.errors.is_empty(),
        "Expected validation errors on example_input_01.json"
    );
}

#[test]
fn test_metabolights_isa_json_profile() {
    let root = repo_root();
    let base_profile_path =
        root.join("shared/resources/isa-json/profiles/metabolights-profile-0.1.0.json");
    let ext_profile_path =
        root.join("shared/resources/isa-json/profiles/metabolights-bh-extended-profile-0.1.0.json");
    let input_json_path = root.join("shared/resources/isa-json/metabolights-input-isa.json");

    let mut refs: HashMap<String, ProfileSource> = HashMap::new();
    refs.insert(
        "https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json".to_string(),
        ProfileSource::Path(base_profile_path.clone()),
    );
    refs.insert(
        "https://github.com/ISA-tools/isa-api/metabolights-bh-extended-profile-0.1.0.json"
            .to_string(),
        ProfileSource::Path(ext_profile_path.clone()),
    );

    let rt = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        skip_jsonschema_validation: Some(true),
        ..Default::default()
    };

    let validator = JsonValidator::with_referenced_profiles(
        None,
        ProfileSource::Path(base_profile_path),
        refs.clone(),
    )
    .expect("Failed to create metabolights validator");

    let res = validator
        .validate_json_file(&input_json_path, Some(rt.clone()))
        .expect("Failed to validate metabolights-input-isa.json");
    let expected_base_errors: HashSet<&str> = [
        "$.studies[0].assays[0].measurementType",
        "$.studies[0].characteristicCategories[2].characteristicType",
        "$.studies[0].description",
        "$.studies[0].filename",
        "$.studies[0].identifier",
        "$.studies[0].materials.sources[0]",
        "$.studies[0].materials.sources[1]",
        "$.studies[0].protocols[12].description",
        "$.studies[0].protocols[13].description",
        "$.studies[0].protocols[14].description",
        "$.studies[0].protocols[2].description",
        "$.studies[0].protocols[3].description",
        "$.studies[0].studyDesignDescriptors",
    ]
    .into_iter()
    .collect();
    let expected_isa_recs: HashSet<&str> = [
        "$.studies[0].protocols[10].protocolType",
        "$.studies[0].protocols[11].protocolType",
        "$.studies[0].protocols[12].protocolType",
        "$.studies[0].protocols[8].protocolType",
        "$.studies[0].protocols[9].protocolType",
    ]
    .into_iter()
    .collect();
    let actual_base_errors: HashSet<&str> = res.errors.keys().map(String::as_str).collect();
    let actual_base_recs: HashSet<&str> = res.recommendations.keys().map(String::as_str).collect();
    assert_eq!(expected_base_errors, actual_base_errors);
    assert_eq!(expected_isa_recs, actual_base_recs);
    assert!(res.optionals.is_empty());

    let ext_validator =
        JsonValidator::with_referenced_profiles(None, ProfileSource::Path(ext_profile_path), refs)
            .expect("Failed to create extended metabolights validator");

    let ext_res = ext_validator
        .validate_json_file(&input_json_path, Some(rt))
        .expect("Failed to validate metabolights-input-isa.json with extended profile");
    let expected_ext_errors: HashSet<&str> = [
        "$.studies[0].assays[0].measurementType",
        "$.studies[0].characteristicCategories[2].characteristicType",
        "$.studies[0].description",
        "$.studies[0].materials.sources[0]",
        "$.studies[0].materials.sources[1]",
    ]
    .into_iter()
    .collect();
    let actual_ext_errors: HashSet<&str> = ext_res.errors.keys().map(String::as_str).collect();
    let actual_ext_recs: HashSet<&str> =
        ext_res.recommendations.keys().map(String::as_str).collect();
    assert_eq!(expected_ext_errors, actual_ext_errors);
    assert_eq!(expected_isa_recs, actual_ext_recs);
    assert!(ext_res.optionals.is_empty());
}

#[test]
fn test_mztab_m_profiles() {
    let root = repo_root();
    let mztab_dir = root.join("shared/resources/mztab-m");
    let profiles_dir = mztab_dir.join("profiles");
    let schema_path = mztab_dir.join("mztabm-2.1.0-M.schema.json");
    let input_json_path = mztab_dir.join("manual_null_MTBLS263.mztab.json");

    let default_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json";
    let mtd_smf_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_smf-profile-2.1.0-M.json";
    let mtd_sml_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-mtd_sml-profile-2.1.0-M.json";
    let full_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-full-profile-2.1.0-M.json";
    let mtbls_id = "https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json";

    let mut refs: HashMap<String, ProfileSource> = HashMap::new();
    refs.insert(
        default_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-default-profile-2.1.0-M.json")),
    );
    refs.insert(
        mtd_smf_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-mtd_smf-profile-2.1.0-M.json")),
    );
    refs.insert(
        mtd_sml_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-mtd_sml-profile-2.1.0-M.json")),
    );
    refs.insert(
        full_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-full-profile-2.1.0-M.json")),
    );
    refs.insert(
        mtbls_id.to_string(),
        ProfileSource::Path(profiles_dir.join("mztabm-metabolights-profile-2.1.0-M.json")),
    );

    let rt = ValidationRuntimeConfiguration {
        offline_mode: Some(true),
        ..Default::default()
    };

    for profile_id in [default_id, mtd_smf_id, mtd_sml_id, full_id, mtbls_id] {
        let ProfileSource::Path(profile_path) = refs.get(profile_id).unwrap().clone() else {
            unreachable!()
        };
        let (valid, msgs) = validate_profile_file(&profile_path);
        assert!(
            valid,
            "Expected profile {profile_id} to be valid, got: {msgs:?}"
        );

        let validator = JsonValidator::with_referenced_profiles(
            Some(ProfileSource::Path(schema_path.clone())),
            ProfileSource::Path(profile_path),
            refs.clone(),
        )
        .unwrap_or_else(|e| panic!("Failed to create validator for {profile_id}: {e}"));

        let res = validator
            .validate_json_file(&input_json_path, Some(rt.clone()))
            .unwrap_or_else(|e| panic!("Failed to validate {profile_id}: {e}"));

        assert!(
            res.optionals.is_empty(),
            "Expected 0 optionals for {profile_id}"
        );
        if profile_id == mtd_sml_id {
            assert!(
                res.errors.is_empty(),
                "Expected no errors for mtd_sml profile, got: {:?}",
                res.errors
            );
            assert_eq!(
                12,
                res.recommendations.len(),
                "Expected 12 recommendations for mtd_sml, got: {:?}",
                res.recommendations.keys()
            );
        } else if profile_id == mtbls_id {
            let expected_mtbls_errors: HashSet<&str> =
                ["$.metadata.contact", "$.metadata.sample[0].species[0]"]
                    .into_iter()
                    .collect();
            let actual_mtbls_errors: HashSet<&str> =
                res.errors.keys().map(String::as_str).collect();
            assert_eq!(expected_mtbls_errors, actual_mtbls_errors);
            assert!(
                res.recommendations.is_empty(),
                "Expected 0 recommendations for metabolights profile, got: {:?}",
                res.recommendations.keys()
            );
        } else {
            assert_eq!(
                20,
                res.errors.len(),
                "Expected 20 error paths for {profile_id}, got: {:?}",
                res.errors.keys()
            );
            assert_eq!(
                12,
                res.recommendations.len(),
                "Expected 12 recommendation paths for {profile_id}, got: {:?}",
                res.recommendations.keys()
            );
            assert!(res.errors.contains_key("$.smallMoleculeFeature[81].charge"));
            assert!(res
                .errors
                .contains_key("$.smallMoleculeEvidence[81].charge"));
            assert!(res.recommendations.contains_key("$.metadata.contact"));
            assert!(res
                .recommendations
                .contains_key("$.metadata.sample[0].species[0]"));
            assert!(res
                .recommendations
                .contains_key("$.metadata.ms_run[1].instrument_ref"));
        }
    }
}

struct MockOpaEngine;

impl OpaEngine for MockOpaEngine {
    fn evaluate(
        &self,
        _input_data: &Value,
        _entrypoint: Option<&str>,
        _data: Option<&Value>,
    ) -> Result<Value, String> {
        Ok(serde_json::json!([
            {
                "messages": [
                    {
                        "policy_id": "policy_0001",
                        "source": "$.item",
                        "message": "OPA policy failed"
                    }
                ]
            }
        ]))
    }
}

struct MockOpaEngineFactory;

impl OpaEngineFactory for MockOpaEngineFactory {
    fn get_opa_engine(
        &self,
        _wasm_file_path: &str,
        _wasm_file_download_url: Option<&str>,
    ) -> Result<Arc<dyn OpaEngine>, String> {
        Ok(Arc::new(MockOpaEngine))
    }
}

#[test]
fn test_optional_opa_engine_requirements() {
    let profile = serde_json::json!({
        "id": "https://example.org/profiles/opa-test.json",
        "version": "1.0.0",
        "name": "OPA Test Profile",
        "configuration": {
            "wasm_file_definitions": {
                "default": {
                    "wasm_file_path": "dummy.wasm",
                    "wasm_file_download_url": "https://example.org/dummy.wasm",
                    "entrypoint": "test/policies"
                }
            }
        },
        "requirements": {
            "$.item": {
                "code": "OPA-0001",
                "policy_id": "policy_0001",
                "enforcement_level": "required"
            }
        }
    });

    let validator = JsonValidator::with_options(
        None,
        profile,
        HashMap::new(),
        None,
        Some(Arc::new(MockOpaEngineFactory)),
    )
    .expect("validator should initialize");

    let rt = ValidationRuntimeConfiguration {
        skip_jsonschema_validation: Some(true),
        ..Default::default()
    };
    let res = validator.validate_dict(&serde_json::json!({"item": "bad"}), Some(rt));
    assert!(res.errors.contains_key("$.item"));
    assert_eq!(res.errors["$.item"][0].code, "OPA-0001");
}

struct EvenLenChecker;

impl ConstraintChecker for EvenLenChecker {
    fn validate(
        &self,
        _constraint: &Constraint,
        value: &Value,
        _root: &Value,
        _context: &JsonProfileRunContext,
    ) -> (bool, Option<String>) {
        let s = value.as_str().unwrap_or("");
        if s.len().is_multiple_of(2) {
            (true, Some("even length".to_string()))
        } else {
            (false, Some("odd length".to_string()))
        }
    }
}

register_constraint_checker!("custom-rust-val", "custom", Some("even_len"), || {
    Arc::new(EvenLenChecker)
});

register_profile_validator!("my_crate::CustomRustValidator", |id| {
    Arc::new(DefaultProfileValidator::new(id))
});

#[test]
fn test_inventory_custom_validator_discovery() {
    let profile_json = serde_json::json!({
        "id": "https://example.org/custom-rust.json",
        "version": "1.0.0",
        "name": "Custom Rust Validator Profile",
        "configuration": {
            "custom_validator_definitions": [
                {
                    "label": "custom_rust",
                    "validator_id": "custom-rust-val",
                    "profile_validator_class": {
                        "rust": "my_crate::CustomRustValidator"
                    }
                }
            ]
        },
        "requirements": {
            "$.code": {
                "code": "CUST-001",
                "enforcement_level": "required",
                "value_constraint": {
                    "type": "custom",
                    "validator": "custom-rust-val",
                    "name": "even_len"
                }
            }
        }
    });

    let validator = JsonValidator::new(None, profile_json).expect("validator should load");
    let ok_res = validator.validate_dict(&serde_json::json!({"code": "ABCD"}), None);
    assert!(ok_res.errors.is_empty());

    let fail_res = validator.validate_dict(&serde_json::json!({"code": "ABC"}), None);
    assert!(fail_res.errors.contains_key("$.code"));
}
