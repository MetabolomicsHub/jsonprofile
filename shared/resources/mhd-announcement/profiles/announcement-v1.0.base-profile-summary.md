# MetabolomicsHub Announcement File v1.0 Base Profile

MetabolomicsHub Announcement File v1.0 Base Profile

## General Information

| Field | Value |
| :--- | :--- |
| **ID** | `https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.base-profile.json` |
| **Name** | MetabolomicsHub Announcement File v1.0 Base Profile |
| **Version** | `v1.0` |
| **Extends** | - |
| **Description** | MetabolomicsHub Announcement File v1.0 Base Profile |
| **JSONPath Targets** | 137 |
| **Total Requirements** | 138 (`required`: 138, `recommended`: 0, `optional`: 0) |

## Configuration

| Setting | Value |
| :--- | :--- |
| **CV Term Field Mapping** | `label`: `source`, `accession`: `accession`, `name`: `name`, `value`: `value` |

## Requirements Summary

| JSONPath | Code | Enforcement | Type / Constraint | Description | Definition Summary |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `$` | `base-required-0001` | `required` | `property-check` | - | required_properties=[`id`, `$schema`, `profile_uri`, `type`, `uri`, `mhd_metadata_file_url`, `cv_definitions`, `repository_name`, `repository_short_name`, `repository_identifier`]; recommended_properties=[`license`, `license_name`, `mhd_metadata_file_hashes`, `submitter_keywords`, `reported_molecular_entities`, `repository_metadata_file_list`, `raw_data_file_list`, `result_file_list`] |
| `$.tag_list[*]` | `base-tag_list-0001` | `required` | `property-check` | - | recommended_properties=[`key`, `value`] |
| `$.tag_list[*].key` | `base-tag_list-key-0001` | `required` | `not-null` | - | type=`not-null`, null_values=[None, ''], precondition=[path=`$`, (type=`string-length`, exceptional_values=[None])] |
| `$.tag_list[*].key` | `base-tag_list-key-0002` | `required` | `not-null` | - | type=`not-null`, precondition=[path=`$`, (type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True)] |
| `$.tag_list[*].value` | `base-tag_list-0004` | `required` | `not-null` | - | type=`not-null`, null_values=[None, ''] |
| `$.additional_identifier_list[*].value` | `base-additional_identifier_list-0001` | `required` | `not-null` | - | type=`not-null`, null_values=[None, ''] |
| `$.additional_identifier_list[*]` | `base-additional_identifier_list-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.repository_revision` | `base-repository_revision-0001` | `required` | `integer` | - | type=`integer`, min=0 |
| `$.repository_revision_comment` | `base-repository_revision_comment-0001` | `required` | `string-length` | - | type=`string-length`, min_length=0 |
| `$.repository_revision_datetime` | `base-repository_revision_datetime-0001` | `required` | `time` | - | type=`time` |
| `$.change_log` | `base-change_log-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.change_log[*]` | `base-change_log-revision-0001` | `required` | `property-check` | - | required_properties=[`revision`, `revision_datetime`] |
| `$.change_log[*].revision` | `base-change_log-revision-0002` | `required` | `integer` | - | type=`integer`, min=0 |
| `$.change_log[*].revision_datetime` | `base-change_log-revision-0003` | `required` | `time` | - | type=`time` |
| `$.change_log[*].comment` | `base-change_log-revision-0004` | `required` | `string-length` | - | type=`string-length`, min_length=0 |
| `$['$schema']` | `base-schema-0001` | `required` | `string-length` | - | type=`string-length`, min_length=10 |
| `$.profile_uri` | `base-profile_uri-0001` | `required` | `string-length` | - | type=`string-length`, min_length=10 |
| `$.created_at` | `base-created_at-0001` | `required` | `time` | - | type=`time` |
| `$.id` | `base-id-0001` | `required` | `string-length` | - | type=`string-length`, min_length=10 |
| `$.type` | `base-type-0001` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.url_list` | `base-url-list-0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=0 |
| `$.url_list[*]` | `base-url-list-0002` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.uri` | `base-uri-0001` | `required` | `uri` | - | type=`uri` |
| `$.repository_identifier` | `base-repository_identifier-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.alternative_id_list` | `base-alternative_id_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.alternative_id_list[*]` | `base-alternative_id_list-0002` | `required` | `string-length` | - | type=`string-length`, min_length=10 |
| `$.repository_name` | `base-repository_name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.repository_short_name` | `base-repository_short_name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.license` | `base-license-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https'] |
| `$.license_name` | `base-license-name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=0 |
| `$.name` | `base-name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=0 |
| `$.description` | `base-description-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.cv_definitions` | `base-cv_definitions-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.cv_definitions[*]` | `base-cv_definitions-definition-0001` | `required` | `property-check` | - | required_properties=[`label`, `name`, `uri`] |
| `$.cv_definitions[*].label` | `base-cv_definitions-definition-0002` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.cv_definitions[*].name` | `base-cv_definitions-definition-0003` | `required` | `string-length` | - | type=`string-length` |
| `$.cv_definitions[*].uri` | `base-cv_definitions-definition-0004` | `required` | `uri` | - | type=`uri`, exceptional_values=[''] |
| `$.cv_definitions[*].prefix` | `base-cv_definitions-prefix-0005` | `required` | `string-length` | - | type=`string-length` |
| `$.cv_definitions[*].version` | `base-cv_definitions-version-0006` | `required` | `string-length` | - | type=`string-length` |
| `$.mhd_identifier` | `base-mhd_identifier-0001` | `required` | `string-length` | - | type=`string-length`, min_length=9, max_length=12 |
| `$.mhd_metadata_file_url` | `base-mhd_metadata_file_url-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https'] |
| `$.mhd_metadata_file_hashes` | `base-mhd_metadata_file_hashes-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.mhd_metadata_file_hashes[*]` | `base-mhd_metadata_file_hashes_hash-0001` | `required` | `cv-term` | - | required_properties=[`name`, `value`]; type=`cv-term`, allow_user_defined_terms=True, is_cv_term_value_required=True, allow_synonym=True |
| `$.mhd_metadata_file_hashes[*].value` | `base-mhd_metadata_file_hashes_hash-0002` | `required` | `string-length` | - | type=`string-length`, min_length=32 |
| `$.mhd_metadata_file_hashes[*].name` | `base-mhd_metadata_file_hashes_hash-0003` | `required` | `string-length` | - | type=`string-length`, min_length=2 |
| `$.submission_date` | `base-submission_date-0001` | `required` | `time` | - | type=`time` |
| `$.public_release_date` | `base-public_release_date-0001` | `required` | `time` | - | type=`time` |
| `$.submitters` | `base-submitters-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.submitters[*]` | `base-submitters-0002` | `required` | `property-check` | - | required_properties=[`full_name`, `email_list`] |
| `$.submitters[*].full_name` | `base-submitters-full_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=5 |
| `$.submitters[*].email_list` | `base-submitters_email_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.submitters[*].email_list[*]` | `base-submitters-email_list-0002` | `required` | `email` | - | type=`email` |
| `$.submitters[*].orcid` | `base-submitters-orcid-0002` | `required` | `regex` | - | type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$` |
| `$.submitters[*].affiliation_list` | `base-submitters-affiliation_list-0001` | `required` | `items-count` | - | type=`items-count`, min_match=1 |
| `$.principal_investigators` | `base-principal_investigators-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.principal_investigators[*]` | `base-principal_investigators-0002` | `required` | `property-check` | - | required_properties=[`full_name`] |
| `$.principal_investigators[*].full_name` | `base-principal_investigators_full_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=5 |
| `$.principal_investigators[*].email_list` | `base-principal_investigators-email_list_0001` | `required` | `items-count` | - | type=`items-count` |
| `$.principal_investigators[*].email_list[*]` | `base-principal_investigators-email_list_0002` | `required` | `email` | - | type=`email` |
| `$.omics_type` | `base-omics_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.omics_type[*]` | `base-omics_type-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.technology_type` | `base-technology_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.technology_type[*]` | `base-technology_type-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.measurement_type` | `base-measurement_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.measurement_type[*]` | `base-measurement_type-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.assay_type` | `base-assay_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.assay_type[*]` | `base-assay_type-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.submitter_keywords` | `base-submitter_keywords-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.submitter_keywords[*]` | `base-submitter_keywords-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.descriptors` | `base-descriptors-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.descriptors[*]` | `base-descriptors-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.publications` | `base-publications-0001` | `required` | `group` | Publication must be defined or annotated with a missing cv term | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`items-count`, min_occurs=1; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1002853`, name=`Dataset with no associated published manuscript`}, {cv_label=`MS`, cv_accession=`MS:1002858`, name=`Dataset with its publication pending`}]] |
| `$.publications[*].doi` | `base-publications-0010` | `required` | `regex` | Publication DOI must be valid | type=`regex`, pattern=`^10[.].+/.+$` |
| `$.publications[*].pubmed_id` | `base-publications-0011` | `required` | `regex` | Publication PubMed Id must be valid | type=`regex`, pattern=`^[0-9]{1,20}$` |
| `$.publications[*].title` | `base-publications-0012` | `required` | `string-length` | Publication title must be valid | type=`string-length`, min_length=5 |
| `$.publications[*].author_list[*]` | `base-publications-0013` | `required` | `string-length` | Publication author must be valid | type=`string-length`, min_length=2 |
| `$.study_factors` | `base-study_factors-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.study_factors[*]` | `base-study_factors-factor-0001` | `required` | `property-check` | - | required_properties=[`key`] |
| `$.study_factors[*].key` | `base-study_factors-factor-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.study_factors[*].values` | `base-study_factors-factor-0003` | `required` | `items-count` | - | type=`items-count` |
| `$.study_factors[*].values[*]` | `base-study_factors-factor-0004-01` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.study_factors[*].values[*].unit` | `base-study_factors-factor-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.characteristic_values` | `base-characteristic_values-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.characteristic_values[*]` | `base-characteristic_values-characteristic-0001` | `required` | `property-check` | - | required_properties=[`key`] |
| `$.characteristic_values[*].key` | `base-characteristic_values-characteristic-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.characteristic_values[*].values` | `base-characteristic_values-characteristic-0003` | `required` | `items-count` | - | type=`items-count` |
| `$.characteristic_values[*].values[*]` | `base-characteristic_values-characteristic-value-0001` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.characteristic_values[*].values[*].unit` | `base-characteristic_values-values-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols` | `base-protocols-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.protocols[*]` | `base-protocols-protocol-0001` | `required` | `property-check` | - | required_properties=[`name`] |
| `$.protocols[*].protocol_type` | `base-protocols-protocol_type-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols[*].description` | `base-protocols-description-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.protocols[*].protocol_parameters` | `base-protocols-protocol_parameters-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.protocols[*].protocol_parameters[*]` | `base-protocols-protocol_parameters-0002` | `required` | `property-check` | - | required_properties=[`key`] |
| `$.protocols[*].protocol_parameters[*].key` | `base-protocols-protocol_parameters-0003` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols[*].protocol_parameters[*].values` | `base-protocols-protocol_parameters-0004` | `required` | `items-count` | - | type=`items-count` |
| `$.protocols[*].protocol_parameters[*].values[*]` | `base-protocols-protocol_parameters-values-0004-01` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.protocols[*].protocol_parameters[*].values[*].unit` | `base-protocols-protocol_parameters-values-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata'] |
| `$.reported_molecular_entities` | `base-reported_molecular_entities-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.reported_molecular_entities[*]` | `base-reported_molecular_entities-0002` | `required` | `property-check` | - | required_properties=[`name`] |
| `$.reported_molecular_entities[*].name` | `base-reported_molecular_entities-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.reported_molecular_entities[*].database_identifiers` | `base-reported_molecular_entities-database_identifiers-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.reported_molecular_entities[*].database_identifiers[*]` | `base-reported_molecular_entities-database_identifiers-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.repository_metadata_file_list` | `base-repository_metadata_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.repository_metadata_file_list[*]` | `base-repository_metadata_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.repository_metadata_file_list[*].url_list[*]` | `base-repository_metadata_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.repository_metadata_file_list[*].name` | `base-repository_metadata_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.repository_metadata_file_list[*].format` | `base-repository_metadata_file_list-format-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.repository_metadata_file_list[*].compression_formats[*]` | `base-repository_metadata_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.repository_metadata_file_list[*].extension` | `base-repository_metadata_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.raw_data_file_list` | `base-raw_data_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.raw_data_file_list[*]` | `base-raw_data_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.raw_data_file_list[*].url_list[*]` | `base-raw_data_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.raw_data_file_list[*].name` | `base-raw_data_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.raw_data_file_list[*].format` | `base-raw_data_file_list-format-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.raw_data_file_list[*].compression_formats[*]` | `base-raw_data_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.raw_data_file_list[*].extension` | `base-raw_data_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.result_file_list` | `base-result_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.result_file_list[*]` | `base-result_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.result_file_list[*].url_list[*]` | `base-result_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.result_file_list[*].name` | `base-result_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.result_file_list[*].format` | `base-result_file_list-format-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.result_file_list[*].compression_formats[*]` | `base-result_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.result_file_list[*].extension` | `base-result_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.derived_data_file_list` | `base-derived_data_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.derived_data_file_list[*]` | `base-derived_data_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.derived_data_file_list[*].url_list[*]` | `base-derived_data_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.derived_data_file_list[*].name` | `base-derived_data_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.derived_data_file_list[*].format` | `base-derived_data_file_list-format-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.derived_data_file_list[*].compression_formats[*]` | `base-derived_data_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.derived_data_file_list[*].extension` | `base-derived_data_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.supplementary_file_list` | `base-supplementary_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.supplementary_file_list[*]` | `base-supplementary_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.supplementary_file_list[*].url_list[*]` | `base-supplementary_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.supplementary_file_list[*].name` | `base-supplementary_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.supplementary_file_list[*].format` | `base-supplementary_file_list-format-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.supplementary_file_list[*].compression_formats[*]` | `base-supplementary_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.supplementary_file_list[*].extension` | `base-supplementary_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |

## Requirement Definitions by JSONPath

### `$`

- **Requirement Group** (1 requirement(s))

#### `base-required-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`id`, `$schema`, `profile_uri`, `type`, `uri`, `mhd_metadata_file_url`, `cv_definitions`, `repository_name`, `repository_short_name`, `repository_identifier`]; recommended_properties=[`license`, `license_name`, `mhd_metadata_file_hashes`, `submitter_keywords`, `reported_molecular_entities`, `repository_metadata_file_list`, `raw_data_file_list`, `result_file_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-required-0001",
  "enforcement_level": "required",
  "required_properties": [
    "id",
    "$schema",
    "profile_uri",
    "type",
    "uri",
    "mhd_metadata_file_url",
    "cv_definitions",
    "repository_name",
    "repository_short_name",
    "repository_identifier"
  ],
  "recommended_properties": [
    "license",
    "license_name",
    "mhd_metadata_file_hashes",
    "submitter_keywords",
    "reported_molecular_entities",
    "repository_metadata_file_list",
    "raw_data_file_list",
    "result_file_list"
  ]
}
```

</details>

### `$.tag_list[*]`

#### `base-tag_list-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: recommended_properties=[`key`, `value`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-tag_list-0001",
  "enforcement_level": "required",
  "recommended_properties": [
    "key",
    "value"
  ]
}
```

</details>

### `$.tag_list[*].key`

- **Requirement Group** (2 requirement(s))

#### `base-tag_list-key-0001` (`required` — `not-null`)

- **Description**: -
- **Summary**: type=`not-null`, null_values=[None, ''], precondition=[path=`$`, (type=`string-length`, exceptional_values=[None])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-tag_list-key-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "precondition": {
      "evaluations": [
        {
          "constraint": {
            "type": "string-length",
            "exceptional_values": [
              null
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "null_values": [
      null,
      ""
    ]
  }
}
```

</details>

#### `base-tag_list-key-0002` (`required` — `not-null`)

- **Description**: -
- **Summary**: type=`not-null`, precondition=[path=`$`, (type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-tag_list-key-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "precondition": {
      "evaluations": [
        {
          "constraint": {
            "type": "cv-term",
            "allow_synonym": true,
            "allow_user_defined_terms": true
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    }
  }
}
```

</details>

### `$.tag_list[*].value`

#### `base-tag_list-0004` (`required` — `not-null`)

- **Description**: -
- **Summary**: type=`not-null`, null_values=[None, '']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-tag_list-0004",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "null_values": [
      null,
      ""
    ]
  }
}
```

</details>

### `$.additional_identifier_list[*].value`

#### `base-additional_identifier_list-0001` (`required` — `not-null`)

- **Description**: -
- **Summary**: type=`not-null`, null_values=[None, '']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-additional_identifier_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "null_values": [
      null,
      ""
    ]
  }
}
```

</details>

### `$.additional_identifier_list[*]`

#### `base-additional_identifier_list-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-additional_identifier_list-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.repository_revision`

#### `base-repository_revision-0001` (`required` — `integer`)

- **Description**: -
- **Summary**: type=`integer`, min=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_revision-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "integer",
    "minimum": 0
  }
}
```

</details>

### `$.repository_revision_comment`

#### `base-repository_revision_comment-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_revision_comment-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 0
  }
}
```

</details>

### `$.repository_revision_datetime`

#### `base-repository_revision_datetime-0001` (`required` — `time`)

- **Description**: -
- **Summary**: type=`time`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_revision_datetime-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "time"
  }
}
```

</details>

### `$.change_log`

#### `base-change_log-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-change_log-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.change_log[*]`

#### `base-change_log-revision-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`revision`, `revision_datetime`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-change_log-revision-0001",
  "enforcement_level": "required",
  "required_properties": [
    "revision",
    "revision_datetime"
  ]
}
```

</details>

### `$.change_log[*].revision`

#### `base-change_log-revision-0002` (`required` — `integer`)

- **Description**: -
- **Summary**: type=`integer`, min=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-change_log-revision-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "integer",
    "minimum": 0
  }
}
```

</details>

### `$.change_log[*].revision_datetime`

#### `base-change_log-revision-0003` (`required` — `time`)

- **Description**: -
- **Summary**: type=`time`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-change_log-revision-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "time"
  }
}
```

</details>

### `$.change_log[*].comment`

#### `base-change_log-revision-0004` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-change_log-revision-0004",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 0
  }
}
```

</details>

### `$['$schema']`

#### `base-schema-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-schema-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
  }
}
```

</details>

### `$.profile_uri`

#### `base-profile_uri-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-profile_uri-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
  }
}
```

</details>

### `$.created_at`

#### `base-created_at-0001` (`required` — `time`)

- **Description**: -
- **Summary**: type=`time`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-created_at-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "time"
  }
}
```

</details>

### `$.id`

#### `base-id-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-id-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
  }
}
```

</details>

### `$.type`

#### `base-type-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
  }
}
```

</details>

### `$.url_list`

#### `base-url-list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-url-list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 0
  }
}
```

</details>

### `$.url_list[*]`

#### `base-url-list-0002` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-url-list-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.uri`

#### `base-uri-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-uri-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri"
  }
}
```

</details>

### `$.repository_identifier`

#### `base-repository_identifier-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_identifier-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.alternative_id_list`

#### `base-alternative_id_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-alternative_id_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.alternative_id_list[*]`

#### `base-alternative_id_list-0002` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-alternative_id_list-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
  }
}
```

</details>

### `$.repository_name`

#### `base-repository_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.repository_short_name`

#### `base-repository_short_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_short_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.license`

#### `base-license-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-license-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https"
    ]
  }
}
```

</details>

### `$.license_name`

#### `base-license-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-license-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 0
  }
}
```

</details>

### `$.name`

#### `base-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 0
  }
}
```

</details>

### `$.description`

#### `base-description-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-description-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.cv_definitions`

#### `base-cv_definitions-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.cv_definitions[*]`

#### `base-cv_definitions-definition-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`label`, `name`, `uri`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-definition-0001",
  "enforcement_level": "required",
  "required_properties": [
    "label",
    "name",
    "uri"
  ]
}
```

</details>

### `$.cv_definitions[*].label`

#### `base-cv_definitions-definition-0002` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-definition-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
  }
}
```

</details>

### `$.cv_definitions[*].name`

#### `base-cv_definitions-definition-0003` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-definition-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.cv_definitions[*].uri`

#### `base-cv_definitions-definition-0004` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, exceptional_values=['']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-definition-0004",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "exceptional_values": [
      ""
    ]
  }
}
```

</details>

### `$.cv_definitions[*].prefix`

#### `base-cv_definitions-prefix-0005` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-prefix-0005",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.cv_definitions[*].version`

#### `base-cv_definitions-version-0006` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-cv_definitions-version-0006",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.mhd_identifier`

#### `base-mhd_identifier-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=9, max_length=12

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_identifier-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 9,
    "maximum": 12
  }
}
```

</details>

### `$.mhd_metadata_file_url`

#### `base-mhd_metadata_file_url-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_metadata_file_url-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https"
    ]
  }
}
```

</details>

### `$.mhd_metadata_file_hashes`

#### `base-mhd_metadata_file_hashes-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_metadata_file_hashes-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.mhd_metadata_file_hashes[*]`

#### `base-mhd_metadata_file_hashes_hash-0001` (`required` — `cv-term`)

- **Description**: -
- **Summary**: required_properties=[`name`, `value`]; type=`cv-term`, allow_user_defined_terms=True, is_cv_term_value_required=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_metadata_file_hashes_hash-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true,
    "is_cv_term_value_required": true
  },
  "required_properties": [
    "name",
    "value"
  ]
}
```

</details>

### `$.mhd_metadata_file_hashes[*].value`

#### `base-mhd_metadata_file_hashes_hash-0002` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=32

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_metadata_file_hashes_hash-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 32
  }
}
```

</details>

### `$.mhd_metadata_file_hashes[*].name`

#### `base-mhd_metadata_file_hashes_hash-0003` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-mhd_metadata_file_hashes_hash-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 2
  }
}
```

</details>

### `$.submission_date`

#### `base-submission_date-0001` (`required` — `time`)

- **Description**: -
- **Summary**: type=`time`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submission_date-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "time"
  }
}
```

</details>

### `$.public_release_date`

#### `base-public_release_date-0001` (`required` — `time`)

- **Description**: -
- **Summary**: type=`time`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-public_release_date-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "time"
  }
}
```

</details>

### `$.submitters`

#### `base-submitters-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.submitters[*]`

#### `base-submitters-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`full_name`, `email_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-0002",
  "enforcement_level": "required",
  "required_properties": [
    "full_name",
    "email_list"
  ]
}
```

</details>

### `$.submitters[*].full_name`

#### `base-submitters-full_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=5

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-full_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 5
  }
}
```

</details>

### `$.submitters[*].email_list`

#### `base-submitters_email_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters_email_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.submitters[*].email_list[*]`

#### `base-submitters-email_list-0002` (`required` — `email`)

- **Description**: -
- **Summary**: type=`email`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-email_list-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "email",
    "check_deliverability": false
  }
}
```

</details>

### `$.submitters[*].orcid`

#### `base-submitters-orcid-0002` (`required` — `regex`)

- **Description**: -
- **Summary**: type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-orcid-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "pattern": "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$",
    "case_sensitive": true
  }
}
```

</details>

### `$.submitters[*].affiliation_list`

#### `base-submitters-affiliation_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_match=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitters-affiliation_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_match": 1
  }
}
```

</details>

### `$.principal_investigators`

#### `base-principal_investigators-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-principal_investigators-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.principal_investigators[*]`

#### `base-principal_investigators-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`full_name`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-principal_investigators-0002",
  "enforcement_level": "required",
  "required_properties": [
    "full_name"
  ]
}
```

</details>

### `$.principal_investigators[*].full_name`

#### `base-principal_investigators_full_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=5

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-principal_investigators_full_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 5
  }
}
```

</details>

### `$.principal_investigators[*].email_list`

#### `base-principal_investigators-email_list_0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-principal_investigators-email_list_0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.principal_investigators[*].email_list[*]`

#### `base-principal_investigators-email_list_0002` (`required` — `email`)

- **Description**: -
- **Summary**: type=`email`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-principal_investigators-email_list_0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "email",
    "check_deliverability": false
  }
}
```

</details>

### `$.omics_type`

#### `base-omics_type-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-omics_type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.omics_type[*]`

#### `base-omics_type-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-omics_type-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.technology_type`

#### `base-technology_type-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-technology_type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.technology_type[*]`

#### `base-technology_type-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-technology_type-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.measurement_type`

#### `base-measurement_type-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-measurement_type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.measurement_type[*]`

#### `base-measurement_type-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-measurement_type-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.assay_type`

#### `base-assay_type-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-assay_type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.assay_type[*]`

#### `base-assay_type-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-assay_type-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.submitter_keywords`

#### `base-submitter_keywords-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitter_keywords-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.submitter_keywords[*]`

#### `base-submitter_keywords-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-submitter_keywords-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.descriptors`

#### `base-descriptors-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-descriptors-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.descriptors[*]`

#### `base-descriptors-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-descriptors-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.publications`

#### `base-publications-0001` (`required` — `group`)

- **Description**: Publication must be defined or annotated with a missing cv term
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`items-count`, min_occurs=1; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1002853`, name=`Dataset with no associated published manuscript`}, {cv_label=`MS`, cv_accession=`MS:1002858`, name=`Dataset with its publication pending`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-publications-0001",
  "description": "Publication must be defined or annotated with a missing cv term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "items-count",
        "min_occurs": 1
      },
      {
        "type": "cv-term-enum",
        "allowed_cv_terms": [
          {
            "cv_label": "MS",
            "cv_accession": "MS:1002853",
            "name": "Dataset with no associated published manuscript"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1002858",
            "name": "Dataset with its publication pending"
          }
        ]
      }
    ],
    "join_operator": "or",
    "min_valid": 1
  }
}
```

</details>

### `$.publications[*].doi`

#### `base-publications-0010` (`required` — `regex`)

- **Description**: Publication DOI must be valid
- **Summary**: type=`regex`, pattern=`^10[.].+/.+$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-publications-0010",
  "description": "Publication DOI must be valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "pattern": "^10[.].+/.+$",
    "case_sensitive": true
  }
}
```

</details>

### `$.publications[*].pubmed_id`

#### `base-publications-0011` (`required` — `regex`)

- **Description**: Publication PubMed Id must be valid
- **Summary**: type=`regex`, pattern=`^[0-9]{1,20}$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-publications-0011",
  "description": "Publication PubMed Id must be valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "pattern": "^[0-9]{1,20}$",
    "case_sensitive": true
  }
}
```

</details>

### `$.publications[*].title`

#### `base-publications-0012` (`required` — `string-length`)

- **Description**: Publication title must be valid
- **Summary**: type=`string-length`, min_length=5

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-publications-0012",
  "description": "Publication title must be valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 5
  }
}
```

</details>

### `$.publications[*].author_list[*]`

#### `base-publications-0013` (`required` — `string-length`)

- **Description**: Publication author must be valid
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-publications-0013",
  "description": "Publication author must be valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 2
  }
}
```

</details>

### `$.study_factors`

#### `base-study_factors-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.study_factors[*]`

#### `base-study_factors-factor-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`key`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-factor-0001",
  "enforcement_level": "required",
  "required_properties": [
    "key"
  ]
}
```

</details>

### `$.study_factors[*].key`

#### `base-study_factors-factor-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-factor-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.study_factors[*].values`

#### `base-study_factors-factor-0003` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-factor-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.study_factors[*].values[*]`

#### `base-study_factors-factor-0004-01` (`required` — `group`)

- **Description**: -
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True; type=`string-length`, json_path=`.value`, min_length=1]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-factor-0004-01",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "cv-term",
        "allow_synonym": true,
        "allow_user_defined_terms": true
      },
      {
        "type": "string-length",
        "json_path": ".value",
        "minimum": 1
      }
    ],
    "join_operator": "or",
    "min_valid": 1
  }
}
```

</details>

### `$.study_factors[*].values[*].unit`

#### `base-study_factors-factor-unit-0001` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-study_factors-factor-unit-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.characteristic_values`

#### `base-characteristic_values-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.characteristic_values[*]`

#### `base-characteristic_values-characteristic-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`key`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-characteristic-0001",
  "enforcement_level": "required",
  "required_properties": [
    "key"
  ]
}
```

</details>

### `$.characteristic_values[*].key`

#### `base-characteristic_values-characteristic-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-characteristic-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.characteristic_values[*].values`

#### `base-characteristic_values-characteristic-0003` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-characteristic-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.characteristic_values[*].values[*]`

#### `base-characteristic_values-characteristic-value-0001` (`required` — `group`)

- **Description**: -
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-characteristic-value-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "cv-term",
        "exceptional_cv_list": [
          "ILX",
          "wikidata"
        ],
        "allow_synonym": true,
        "allow_user_defined_terms": true
      },
      {
        "type": "string-length",
        "json_path": ".value",
        "minimum": 1
      }
    ],
    "join_operator": "or",
    "min_valid": 1
  }
}
```

</details>

### `$.characteristic_values[*].values[*].unit`

#### `base-characteristic_values-values-unit-0001` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-characteristic_values-values-unit-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.protocols`

#### `base-protocols-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.protocols[*]`

#### `base-protocols-protocol-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol-0001",
  "enforcement_level": "required",
  "required_properties": [
    "name"
  ]
}
```

</details>

### `$.protocols[*].protocol_type`

#### `base-protocols-protocol_type-0001` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_type-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.protocols[*].description`

#### `base-protocols-description-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-description-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.protocols[*].protocol_parameters`

#### `base-protocols-protocol_parameters-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.protocols[*].protocol_parameters[*]`

#### `base-protocols-protocol_parameters-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`key`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-0002",
  "enforcement_level": "required",
  "required_properties": [
    "key"
  ]
}
```

</details>

### `$.protocols[*].protocol_parameters[*].key`

#### `base-protocols-protocol_parameters-0003` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.protocols[*].protocol_parameters[*].values`

#### `base-protocols-protocol_parameters-0004` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-0004",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.protocols[*].protocol_parameters[*].values[*]`

#### `base-protocols-protocol_parameters-values-0004-01` (`required` — `group`)

- **Description**: -
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-values-0004-01",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "cv-term",
        "exceptional_cv_list": [
          "ILX",
          "wikidata"
        ],
        "allow_synonym": true,
        "allow_user_defined_terms": true
      },
      {
        "type": "string-length",
        "json_path": ".value",
        "minimum": 1
      }
    ],
    "join_operator": "or",
    "min_valid": 1
  }
}
```

</details>

### `$.protocols[*].protocol_parameters[*].values[*].unit`

#### `base-protocols-protocol_parameters-values-unit-0001` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-protocols-protocol_parameters-values-unit-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "exceptional_cv_list": [
      "ILX",
      "wikidata"
    ],
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.reported_molecular_entities`

#### `base-reported_molecular_entities-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-reported_molecular_entities-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.reported_molecular_entities[*]`

#### `base-reported_molecular_entities-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-reported_molecular_entities-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name"
  ]
}
```

</details>

### `$.reported_molecular_entities[*].name`

#### `base-reported_molecular_entities-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-reported_molecular_entities-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.reported_molecular_entities[*].database_identifiers`

#### `base-reported_molecular_entities-database_identifiers-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-reported_molecular_entities-database_identifiers-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.reported_molecular_entities[*].database_identifiers[*]`

#### `base-reported_molecular_entities-database_identifiers-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-reported_molecular_entities-database_identifiers-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true,
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.repository_metadata_file_list`

#### `base-repository_metadata_file_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.repository_metadata_file_list[*]`

#### `base-repository_metadata_file_list-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`, `url_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "url_list"
  ]
}
```

</details>

### `$.repository_metadata_file_list[*].url_list[*]`

#### `base-repository_metadata_file_list-url_list-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-url_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.repository_metadata_file_list[*].name`

#### `base-repository_metadata_file_list-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.repository_metadata_file_list[*].format`

#### `base-repository_metadata_file_list-format-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-format-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.repository_metadata_file_list[*].compression_formats[*]`

#### `base-repository_metadata_file_list-compression_formats-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-compression_formats-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.repository_metadata_file_list[*].extension`

#### `base-repository_metadata_file_list-extension-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-repository_metadata_file_list-extension-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.raw_data_file_list`

#### `base-raw_data_file_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.raw_data_file_list[*]`

#### `base-raw_data_file_list-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`, `url_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "url_list"
  ]
}
```

</details>

### `$.raw_data_file_list[*].url_list[*]`

#### `base-raw_data_file_list-url_list-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-url_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.raw_data_file_list[*].name`

#### `base-raw_data_file_list-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.raw_data_file_list[*].format`

#### `base-raw_data_file_list-format-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-format-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.raw_data_file_list[*].compression_formats[*]`

#### `base-raw_data_file_list-compression_formats-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-compression_formats-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.raw_data_file_list[*].extension`

#### `base-raw_data_file_list-extension-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-raw_data_file_list-extension-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.result_file_list`

#### `base-result_file_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.result_file_list[*]`

#### `base-result_file_list-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`, `url_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "url_list"
  ]
}
```

</details>

### `$.result_file_list[*].url_list[*]`

#### `base-result_file_list-url_list-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-url_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.result_file_list[*].name`

#### `base-result_file_list-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.result_file_list[*].format`

#### `base-result_file_list-format-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-format-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.result_file_list[*].compression_formats[*]`

#### `base-result_file_list-compression_formats-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-compression_formats-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.result_file_list[*].extension`

#### `base-result_file_list-extension-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-result_file_list-extension-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.derived_data_file_list`

#### `base-derived_data_file_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.derived_data_file_list[*]`

#### `base-derived_data_file_list-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`, `url_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "url_list"
  ]
}
```

</details>

### `$.derived_data_file_list[*].url_list[*]`

#### `base-derived_data_file_list-url_list-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-url_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.derived_data_file_list[*].name`

#### `base-derived_data_file_list-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.derived_data_file_list[*].format`

#### `base-derived_data_file_list-format-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-format-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.derived_data_file_list[*].compression_formats[*]`

#### `base-derived_data_file_list-compression_formats-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-compression_formats-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.derived_data_file_list[*].extension`

#### `base-derived_data_file_list-extension-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-derived_data_file_list-extension-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.supplementary_file_list`

#### `base-supplementary_file_list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count"
  }
}
```

</details>

### `$.supplementary_file_list[*]`

#### `base-supplementary_file_list-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`name`, `url_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-0002",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "url_list"
  ]
}
```

</details>

### `$.supplementary_file_list[*].url_list[*]`

#### `base-supplementary_file_list-url_list-0001` (`required` — `uri`)

- **Description**: -
- **Summary**: type=`uri`, schemes=['http', 'https', 'ftp']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-url_list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "allowed_schemes": [
      "http",
      "https",
      "ftp"
    ]
  }
}
```

</details>

### `$.supplementary_file_list[*].name`

#### `base-supplementary_file_list-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>

### `$.supplementary_file_list[*].format`

#### `base-supplementary_file_list-format-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-format-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.supplementary_file_list[*].compression_formats[*]`

#### `base-supplementary_file_list-compression_formats-0002` (`required` — `cv-term`)

- **Description**: -
- **Summary**: type=`cv-term`, allow_synonym=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-compression_formats-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "allow_synonym": true
  }
}
```

</details>

### `$.supplementary_file_list[*].extension`

#### `base-supplementary_file_list-extension-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "base-supplementary_file_list-extension-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length"
  }
}
```

</details>
