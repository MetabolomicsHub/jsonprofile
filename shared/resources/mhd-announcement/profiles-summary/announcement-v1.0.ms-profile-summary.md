# MetabolomicsHub Announcement File v1.0 MS Profile

MetabolomicsHub Announcement File v1.0 MS Profile

## General Information

| Field | Value |
| :--- | :--- |
| **ID** | `https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.schema.ms-profile.json` |
| **Name** | MetabolomicsHub Announcement File v1.0 MS Profile |
| **Version** | `v1.0` |
| **Extends** | `https://metabolomicshub.github.io/mhd-model/schemas/v1_0/announcement-v1.0.legacy-profile.json` |
| **Description** | MetabolomicsHub Announcement File v1.0 MS Profile |
| **JSONPath Targets** | 137 |
| **Total Requirements** | 148 (`required`: 148, `recommended`: 0, `optional`: 0) |

## Configuration

| Setting | Value |
| :--- | :--- |
| **CV Term Field Mapping** | `label`: `source`, `accession`: `accession`, `name`: `name`, `value`: `value` |

## Requirements Summary

| JSONPath | Code | Enforcement | Type / Constraint | Description | Definition Summary |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `$` | `ms-required-0001` | `required` | `property-check` | - | required_properties=[`id`, `$schema`, `profile_uri`, `type`, `uri`, `mhd_metadata_file_url`, `cv_definitions`, `repository_name`, `repository_short_name`, `repository_identifier`, `license`, `url_list`, `name`, `description`, `submission_date`, `public_release_date`, `submitters`, `repository_metadata_file_list`, `characteristic_values`, `protocols`]; recommended_properties=[`submitter_keywords`, `raw_data_file_list`, `result_file_list`, `reported_molecular_entities`, `principal_investigators`] |
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
| `$.url_list` | `ms-url-list-0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=1 |
| `$.url_list[*]` | `base-url-list-0002` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.uri` | `base-uri-0001` | `required` | `uri` | - | type=`uri` |
| `$.repository_identifier` | `ms-repository_identifier-0001` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.alternative_id_list` | `base-alternative_id_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.alternative_id_list[*]` | `base-alternative_id_list-0002` | `required` | `string-length` | - | type=`string-length`, min_length=10 |
| `$.repository_name` | `ms-repository_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.repository_short_name` | `ms-repository_short_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.license` | `base-license-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https'] |
| `$.license_name` | `ms-license-name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=1 |
| `$.name` | `ms-name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=0 |
| `$.description` | `ms-description-0001` | `required` | `string-length` | - | type=`string-length` |
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
| `$.submitters` | `ms-submitters-0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=1 |
| `$.submitters[*]` | `base-submitters-0002` | `required` | `property-check` | - | required_properties=[`full_name`, `email_list`] |
| `$.submitters[*].full_name` | `base-submitters-full_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=5 |
| `$.submitters[*].email_list` | `ms-submitters_emails-0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=1 |
| `$.submitters[*].email_list[*]` | `base-submitters-email_list-0002` | `required` | `email` | - | type=`email` |
| `$.submitters[*].orcid` | `base-submitters-orcid-0002` | `required` | `regex` | - | type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$` |
| `$.submitters[*].affiliation_list` | `base-submitters-affiliation_list-0001` | `required` | `items-count` | - | type=`items-count`, min_match=1 |
| `$.principal_investigators` | `ms-principal_investigators-0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=1 |
| `$.principal_investigators[*]` | `ms-principal_investigators-0002` | `required` | `property-check` | - | required_properties=[`full_name`, `email_list`] |
| `$.principal_investigators[*].full_name` | `base-principal_investigators_full_name-0001` | `required` | `string-length` | - | type=`string-length`, min_length=5 |
| `$.principal_investigators[*].email_list` | `ms-principal_investigators-email_list_0001` | `required` | `items-count` | - | type=`items-count`, min_occurs=1 |
| `$.principal_investigators[*].email_list[*]` | `base-principal_investigators-email_list_0002` | `required` | `email` | - | type=`email` |
| `$.omics_type` | `base-omics_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.omics_type[*]` | `ms-omics_type-0010` | `required` | `cv-term-enum` | Omics type value must be in this list: [EDAM, EDAM:topic_3172, Metabolomics, ],[EDAM, EDAM:topic_0153, Lipidomics, ],[EDAM, EDAM:topic_3955, Fluxomics, ],[EDAM, EDAM:topic_4065, Exposomics, ] | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:topic_3172`, name=`Metabolomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_0153`, name=`Lipidomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_3955`, name=`Fluxomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_4065`, name=`Exposomics`}] |
| `$.technology_type` | `base-technology_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.technology_type[*]` | `ms-technology_type-0010` | `required` | `cv-term-enum` | Technology type value must be in this list: [OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ] | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000623`, name=`NMR spectroscopy assay`}] |
| `$.measurement_type` | `base-measurement_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.measurement_type[*]` | `ms-measurement_type-0010` | `required` | `cv-term-enum` | Measurement type value must be in this list: [MS, MS:1003904, untargeted analysis, ],[MS, MS:1003905, targeted analysis, ],[MS, MS:1003906, semi-targeted analysis, ] | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003904`, name=`untargeted analysis`}, {cv_label=`MS`, cv_accession=`MS:1003905`, name=`targeted analysis`}, {cv_label=`MS`, cv_accession=`MS:1003906`, name=`semi-targeted analysis`}] |
| `$.assay_type` | `base-assay_type-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.assay_type[*]` | `ms-assay_type-0010` | `required` | `cv-term-enum` | Assay type value must be in this list: [OBI, OBI:0003097, liquid chromatography mass spectrometry assay, ],[OBI, OBI:0003110, gas chromatography mass spectrometry assay, ],[OBI, OBI:0003741, capillary electrophoresis mass spectrometry assay, ],[OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ] | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0003097`, name=`liquid chromatography mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0003110`, name=`gas chromatography mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0003741`, name=`capillary electrophoresis mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000623`, name=`NMR spectroscopy assay`}] |
| `$.submitter_keywords` | `base-submitter_keywords-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.submitter_keywords[*]` | `base-submitter_keywords-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.descriptors` | `base-descriptors-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.descriptors[*]` | `base-descriptors-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.publications` | `base-publications-0001` | `required` | `group` | Publication must be defined or annotated with a missing cv term | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`items-count`, min_occurs=1; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1002853`, name=`Dataset with no associated published manuscript`}, {cv_label=`MS`, cv_accession=`MS:1002858`, name=`Dataset with its publication pending`}]] |
| `$.publications[*].doi` | `base-publications-0010` | `required` | `regex` | Publication DOI must be valid | type=`regex`, pattern=`^10[.].+/.+$` |
| `$.publications[*].pubmed_id` | `base-publications-0011` | `required` | `regex` | Publication PubMed Id must be valid | type=`regex`, pattern=`^[0-9]{1,20}$` |
| `$.publications[*].title` | `ms-publications-0011` | `required` | `string-length` | Publication title must be valid | type=`string-length`, min_length=10 |
| `$.publications[*].author_list[*]` | `base-publications-0013` | `required` | `string-length` | Publication author must be valid | type=`string-length`, min_length=2 |
| `$.study_factors` | `base-study_factors-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.study_factors[*]` | `base-study_factors-factor-0001` | `required` | `property-check` | - | required_properties=[`key`] |
| `$.study_factors[*].key` | `base-study_factors-factor-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.study_factors[*].values` | `base-study_factors-factor-0003` | `required` | `items-count` | - | type=`items-count` |
| `$.study_factors[*].values[*]` | `base-study_factors-factor-0004-01` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.study_factors[*].values[*].unit` | `base-study_factors-factor-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.characteristic_values` | `ms-characteristic_values-0001` | `required` | `items-count` | - | type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C14250`, name=`Organism`}]) |
| `$.characteristic_values` | `ms-characteristic_values-0002` | `required` | `items-count` | - | type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C103199`, name=`Organism Part`}]) |
| `$.characteristic_values` | `ms-characteristic_values-0003` | `required` | `items-count` | - | type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`EFO:0000324`, name=`cell type`}]) |
| `$.characteristic_values` | `ms-characteristic_values-0004` | `required` | `items-count` | - | type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`MONDO:0000001`, name=`disease`}]) |
| `$.characteristic_values[*]` | `ms-characteristic_values-0010` | `required` | `items-count` | At least one organism characteristic from NCBITAXON, ENVO and CHEBI is required. | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`NCBITAXON`, `ENVO`, `CHEBI`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C14250`, name=`Organism`}])] |
| `$.characteristic_values[*]` | `ms-characteristic_values-0011` | `required` | `items-count` | At least one organism part characteristic from BTO, ENVO and CHEBI is required. | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`UBERON`, `BTO`, `NCIT`, `CHEBI`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C103199`, name=`Organism Part`}])] |
| `$.characteristic_values[*]` | `ms-characteristic_values-0012` | `required` | `items-count` | At least one cell type characteristic from CL, CLO ontologies is required. | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, constraints=[type=`string-enum`, json_path=`source`, options=[`CL`, `CLO`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`EFO:0000324`, name=`cell type`}])] |
| `$.characteristic_values[*]` | `ms-characteristic_values-0013` | `required` | `items-count` | At least one disease characteristic value from MONDO, SNOMED ontologies is required. | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`MONDO`, `MP`, `SNOMED`, `ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`MONDO:0000001`, name=`disease`}])] |
| `$.characteristic_values[*].key` | `base-characteristic_values-characteristic-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.characteristic_values[*].values` | `base-characteristic_values-characteristic-0003` | `required` | `items-count` | - | type=`items-count` |
| `$.characteristic_values[*].values[*]` | `base-characteristic_values-characteristic-value-0001` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.characteristic_values[*].values[*].unit` | `base-characteristic_values-values-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols` | `ms-protocols-0001` | `required` | `items-count` | - | type=`items-count`, item_paths=['protocol_type'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}]) |
| `$.protocols[*]` | `ms-protocols-0010` | `required` | `items-count` | mass spectrometer parameter must be defined in mass spectrometry protocol | type=`items-count`, item_paths=['protocol_parameters[*].key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000049`, name=`mass spectrometer`}]), precondition=[path=`protocol_type`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}])] |
| `$.protocols[*]` | `ms-protocols-0011` | `required` | `items-count` | acquisition polarity parameter must be defined in mass spectrometry protocol | type=`items-count`, item_paths=['protocol_parameters[*].key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}]), precondition=[path=`protocol_type`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}])] |
| `$.protocols[*].protocol_type` | `base-protocols-protocol_type-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols[*].description` | `base-protocols-description-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.protocols[*].protocol_parameters` | `base-protocols-protocol_parameters-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.protocols[*].protocol_parameters[*]` | `ms-protocols-parameters-0010` | `required` | `items-count` | At least one mass spectrometer parameter value must be defined and the values must be a child of [MS, MS:1000031, instrument model] CV Term | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000031`, name=`instrument model`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000049`, name=`mass spectrometer`}])] |
| `$.protocols[*].protocol_parameters[*]` | `ms-protocols-parameters-0020` | `required` | `items-count` | At least one acquisition polarity parameter value must be defined and the values must be a child of [MS, MS:1003776, acquisition polarity] CV Term | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}])] |
| `$.protocols[*].protocol_parameters[*]` | `ms-protocols-parameters-0030` | `required` | `items-count` | Ionization type parameter value must be a child of [MS, MS:1000008, ionization type] CV Term | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000008`, name=`ionization type`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000008`, name=`ionization type`}])] |
| `$.protocols[*].protocol_parameters[*]` | `ms-protocols-parameters-0040` | `required` | `items-count` | Inlet parameter value must be a child of [MS, MS:1000007, inlet type] CV Term | type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000007`, name=`inlet type`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000007`, name=`inlet type`}])] |
| `$.protocols[*].protocol_parameters[*].key` | `base-protocols-protocol_parameters-0003` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.protocols[*].protocol_parameters[*].values` | `base-protocols-protocol_parameters-0004` | `required` | `items-count` | - | type=`items-count` |
| `$.protocols[*].protocol_parameters[*].values[*]` | `base-protocols-protocol_parameters-values-0004-01` | `required` | `group` | - | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata']; type=`string-length`, json_path=`.value`, min_length=1] |
| `$.protocols[*].protocol_parameters[*].values[*].unit` | `base-protocols-protocol_parameters-values-unit-0001` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True, exceptional_cv_list=['ILX', 'wikidata'] |
| `$.reported_molecular_entities` | `base-reported_molecular_entities-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.reported_molecular_entities[*]` | `ms-reported_molecular_entities-identifiers-0010` | `required` | `items-count` | Molecular entity identifier must be a child of [EDAM, EDAM:data_2894, Compound accession] | type=`items-count`, item_paths=['database_identifiers[*]'], item_constraint=(type=`group`, join_operator=`or`, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:data_2894`, name=`Compound accession`}]]) |
| `$.reported_molecular_entities[*].name` | `base-reported_molecular_entities-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.reported_molecular_entities[*].database_identifiers` | `base-reported_molecular_entities-database_identifiers-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.reported_molecular_entities[*].database_identifiers[*]` | `base-reported_molecular_entities-database_identifiers-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_user_defined_terms=True, allow_synonym=True |
| `$.repository_metadata_file_list` | `base-repository_metadata_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.repository_metadata_file_list[*]` | `base-repository_metadata_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.repository_metadata_file_list[*].url_list[*]` | `base-repository_metadata_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.repository_metadata_file_list[*].name` | `base-repository_metadata_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.repository_metadata_file_list[*].format` | `ms-repository_metadata_file_list-format-0010` | `required` | `group` | File extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format] | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]] |
| `$.repository_metadata_file_list[*].compression_formats[*]` | `base-repository_metadata_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.repository_metadata_file_list[*].extension` | `base-repository_metadata_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.raw_data_file_list` | `base-raw_data_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.raw_data_file_list[*]` | `base-raw_data_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.raw_data_file_list[*].url_list[*]` | `base-raw_data_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.raw_data_file_list[*].name` | `base-raw_data_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.raw_data_file_list[*].format` | `ms-raw_data_file_list-format-0010` | `required` | `group` | Raw data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format] | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]] |
| `$.raw_data_file_list[*].compression_formats[*]` | `base-raw_data_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.raw_data_file_list[*].extension` | `base-raw_data_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.result_file_list` | `base-result_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.result_file_list[*]` | `base-result_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.result_file_list[*].url_list[*]` | `base-result_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.result_file_list[*].name` | `base-result_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.result_file_list[*].format` | `ms-result_file_list-format-0010` | `required` | `group` | Result file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format] | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]] |
| `$.result_file_list[*].compression_formats[*]` | `base-result_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.result_file_list[*].extension` | `base-result_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.derived_data_file_list` | `base-derived_data_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.derived_data_file_list[*]` | `base-derived_data_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.derived_data_file_list[*].url_list[*]` | `base-derived_data_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.derived_data_file_list[*].name` | `base-derived_data_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.derived_data_file_list[*].format` | `ms-derived_data_file_list-format-0010` | `required` | `group` | Derived data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format] | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]] |
| `$.derived_data_file_list[*].compression_formats[*]` | `base-derived_data_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.derived_data_file_list[*].extension` | `base-derived_data_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.supplementary_file_list` | `base-supplementary_file_list-0001` | `required` | `items-count` | - | type=`items-count` |
| `$.supplementary_file_list[*]` | `base-supplementary_file_list-0002` | `required` | `property-check` | - | required_properties=[`name`, `url_list`] |
| `$.supplementary_file_list[*].url_list[*]` | `base-supplementary_file_list-url_list-0001` | `required` | `uri` | - | type=`uri`, schemes=['http', 'https', 'ftp'] |
| `$.supplementary_file_list[*].name` | `base-supplementary_file_list-name-0001` | `required` | `string-length` | - | type=`string-length` |
| `$.supplementary_file_list[*].format` | `ms-supplementary_file_list-format-0010` | `required` | `group` | Supplementary file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format] | type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]] |
| `$.supplementary_file_list[*].compression_formats[*]` | `base-supplementary_file_list-compression_formats-0002` | `required` | `cv-term` | - | type=`cv-term`, allow_synonym=True |
| `$.supplementary_file_list[*].extension` | `base-supplementary_file_list-extension-0001` | `required` | `string-length` | - | type=`string-length` |

## Requirement Definitions by JSONPath

### `$`

- **Requirement Group** (1 requirement(s))

#### `ms-required-0001` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`id`, `$schema`, `profile_uri`, `type`, `uri`, `mhd_metadata_file_url`, `cv_definitions`, `repository_name`, `repository_short_name`, `repository_identifier`, `license`, `url_list`, `name`, `description`, `submission_date`, `public_release_date`, `submitters`, `repository_metadata_file_list`, `characteristic_values`, `protocols`]; recommended_properties=[`submitter_keywords`, `raw_data_file_list`, `result_file_list`, `reported_molecular_entities`, `principal_investigators`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-required-0001",
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
    "repository_identifier",
    "license",
    "url_list",
    "name",
    "description",
    "submission_date",
    "public_release_date",
    "submitters",
    "repository_metadata_file_list",
    "characteristic_values",
    "protocols"
  ],
  "recommended_properties": [
    "submitter_keywords",
    "raw_data_file_list",
    "result_file_list",
    "reported_molecular_entities",
    "principal_investigators"
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

#### `ms-url-list-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-url-list-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
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

#### `ms-repository_identifier-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-repository_identifier-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
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

#### `ms-repository_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-repository_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
  }
}
```

</details>

### `$.repository_short_name`

#### `ms-repository_short_name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-repository_short_name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
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

#### `ms-license-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-license-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 1
  }
}
```

</details>

### `$.name`

#### `ms-name-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`, min_length=0

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-name-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 0
  }
}
```

</details>

### `$.description`

#### `ms-description-0001` (`required` — `string-length`)

- **Description**: -
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-description-0001",
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

#### `ms-submitters-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-submitters-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
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

#### `ms-submitters_emails-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-submitters_emails-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
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

#### `ms-principal_investigators-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-principal_investigators-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.principal_investigators[*]`

#### `ms-principal_investigators-0002` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`full_name`, `email_list`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-principal_investigators-0002",
  "enforcement_level": "required",
  "required_properties": [
    "full_name",
    "email_list"
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

#### `ms-principal_investigators-email_list_0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-principal_investigators-email_list_0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
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

#### `ms-omics_type-0010` (`required` — `cv-term-enum`)

- **Description**: Omics type value must be in this list: [EDAM, EDAM:topic_3172, Metabolomics, ],[EDAM, EDAM:topic_0153, Lipidomics, ],[EDAM, EDAM:topic_3955, Fluxomics, ],[EDAM, EDAM:topic_4065, Exposomics, ]
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:topic_3172`, name=`Metabolomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_0153`, name=`Lipidomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_3955`, name=`Fluxomics`}, {cv_label=`EDAM`, cv_accession=`EDAM:topic_4065`, name=`Exposomics`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-omics_type-0010",
  "description": "Omics type value must be in this list: [EDAM, EDAM:topic_3172, Metabolomics, ],[EDAM, EDAM:topic_0153, Lipidomics, ],[EDAM, EDAM:topic_3955, Fluxomics, ],[EDAM, EDAM:topic_4065, Exposomics, ]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "EDAM",
        "cv_accession": "EDAM:topic_3172",
        "name": "Metabolomics"
      },
      {
        "cv_label": "EDAM",
        "cv_accession": "EDAM:topic_0153",
        "name": "Lipidomics"
      },
      {
        "cv_label": "EDAM",
        "cv_accession": "EDAM:topic_3955",
        "name": "Fluxomics"
      },
      {
        "cv_label": "EDAM",
        "cv_accession": "EDAM:topic_4065",
        "name": "Exposomics"
      }
    ]
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

#### `ms-technology_type-0010` (`required` — `cv-term-enum`)

- **Description**: Technology type value must be in this list: [OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ]
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000623`, name=`NMR spectroscopy assay`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-technology_type-0010",
  "description": "Technology type value must be in this list: [OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0000470",
        "name": "mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0000623",
        "name": "NMR spectroscopy assay"
      }
    ]
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

#### `ms-measurement_type-0010` (`required` — `cv-term-enum`)

- **Description**: Measurement type value must be in this list: [MS, MS:1003904, untargeted analysis, ],[MS, MS:1003905, targeted analysis, ],[MS, MS:1003906, semi-targeted analysis, ]
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003904`, name=`untargeted analysis`}, {cv_label=`MS`, cv_accession=`MS:1003905`, name=`targeted analysis`}, {cv_label=`MS`, cv_accession=`MS:1003906`, name=`semi-targeted analysis`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-measurement_type-0010",
  "description": "Measurement type value must be in this list: [MS, MS:1003904, untargeted analysis, ],[MS, MS:1003905, targeted analysis, ],[MS, MS:1003906, semi-targeted analysis, ]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "MS:1003904",
        "name": "untargeted analysis"
      },
      {
        "cv_label": "MS",
        "cv_accession": "MS:1003905",
        "name": "targeted analysis"
      },
      {
        "cv_label": "MS",
        "cv_accession": "MS:1003906",
        "name": "semi-targeted analysis"
      }
    ]
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

#### `ms-assay_type-0010` (`required` — `cv-term-enum`)

- **Description**: Assay type value must be in this list: [OBI, OBI:0003097, liquid chromatography mass spectrometry assay, ],[OBI, OBI:0003110, gas chromatography mass spectrometry assay, ],[OBI, OBI:0003741, capillary electrophoresis mass spectrometry assay, ],[OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ]
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0003097`, name=`liquid chromatography mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0003110`, name=`gas chromatography mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0003741`, name=`capillary electrophoresis mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`OBI:0000623`, name=`NMR spectroscopy assay`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-assay_type-0010",
  "description": "Assay type value must be in this list: [OBI, OBI:0003097, liquid chromatography mass spectrometry assay, ],[OBI, OBI:0003110, gas chromatography mass spectrometry assay, ],[OBI, OBI:0003741, capillary electrophoresis mass spectrometry assay, ],[OBI, OBI:0000470, mass spectrometry assay, ],[OBI, OBI:0000623, NMR spectroscopy assay, ]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0003097",
        "name": "liquid chromatography mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0003110",
        "name": "gas chromatography mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0003741",
        "name": "capillary electrophoresis mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0000470",
        "name": "mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "OBI:0000623",
        "name": "NMR spectroscopy assay"
      }
    ]
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

#### `ms-publications-0011` (`required` — `string-length`)

- **Description**: Publication title must be valid
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-publications-0011",
  "description": "Publication title must be valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
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

- **Requirement Group** (4 requirement(s))

#### `ms-characteristic_values-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C14250`, name=`Organism`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "There must be a characteristic with type [NCIT, NCIT:C14250, Organism] in characteristic_values",
    "item_value_jsonpath_list": [
      "key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "NCIT",
          "cv_accession": "NCIT:C14250",
          "name": "Organism"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

#### `ms-characteristic_values-0002` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C103199`, name=`Organism Part`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0002",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "There must be a characteristic named [NCIT, NCIT:C103199, Organism Part] in characteristic_values",
    "item_value_jsonpath_list": [
      "key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "NCIT",
          "cv_accession": "NCIT:C103199",
          "name": "Organism Part"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

#### `ms-characteristic_values-0003` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`EFO:0000324`, name=`cell type`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0003",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "There must be a characteristic with type [EFO, EFO:0000324, cell type] in characteristic_values",
    "item_value_jsonpath_list": [
      "key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "EFO",
          "cv_accession": "EFO:0000324",
          "name": "cell type"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

#### `ms-characteristic_values-0004` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, item_paths=['key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`MONDO:0000001`, name=`disease`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0004",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "There must be a characteristic with type [MONDO, MONDO:0000001, disease] in characteristic_values",
    "item_value_jsonpath_list": [
      "key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "MONDO",
          "cv_accession": "MONDO:0000001",
          "name": "disease"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

### `$.characteristic_values[*]`

- **Requirement Group** (4 requirement(s))

#### `ms-characteristic_values-0010` (`required` — `items-count`)

- **Description**: At least one organism characteristic from NCBITAXON, ENVO and CHEBI is required.
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`NCBITAXON`, `ENVO`, `CHEBI`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C14250`, name=`Organism`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0010",
  "description": "At least one organism characteristic from NCBITAXON, ENVO and CHEBI is required.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Organism value is required from NCBITAXON, ENVO, CHEBI ontologies",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "NCIT",
                "cv_accession": "NCIT:C14250",
                "name": "Organism"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "NCBITAXON",
            "ENVO",
            "CHEBI"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    }
  }
}
```

</details>

#### `ms-characteristic_values-0011` (`required` — `items-count`)

- **Description**: At least one organism part characteristic from BTO, ENVO and CHEBI is required.
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`UBERON`, `BTO`, `NCIT`, `CHEBI`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C103199`, name=`Organism Part`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0011",
  "description": "At least one organism part characteristic from BTO, ENVO and CHEBI is required.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Organism Part value is required from UBERON, BTO, NCIT, CHEBI ontologies",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "NCIT",
                "cv_accession": "NCIT:C103199",
                "name": "Organism Part"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "UBERON",
            "BTO",
            "NCIT",
            "CHEBI"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    }
  }
}
```

</details>

#### `ms-characteristic_values-0012` (`required` — `items-count`)

- **Description**: At least one cell type characteristic from CL, CLO ontologies is required.
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, constraints=[type=`string-enum`, json_path=`source`, options=[`CL`, `CLO`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`EFO:0000324`, name=`cell type`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0012",
  "description": "At least one cell type characteristic from CL, CLO ontologies is required.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "cell type value is required from CL, CLO ontologies",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "EFO",
                "cv_accession": "EFO:0000324",
                "name": "cell type"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "CL",
            "CLO"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or"
    }
  }
}
```

</details>

#### `ms-characteristic_values-0013` (`required` — `items-count`)

- **Description**: At least one disease characteristic value from MONDO, SNOMED ontologies is required.
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`MONDO`, `MP`, `SNOMED`, `ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`MONDO:0000001`, name=`disease`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-characteristic_values-0013",
  "description": "At least one disease characteristic value from MONDO, SNOMED ontologies is required.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "disease value is required from MONDO, MP, SNOMED, ILX, wikidata ontologies",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "MONDO",
                "cv_accession": "MONDO:0000001",
                "name": "disease"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "MONDO",
            "MP",
            "SNOMED",
            "ILX",
            "wikidata"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    }
  }
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

- **Requirement Group** (1 requirement(s))

#### `ms-protocols-0001` (`required` — `items-count`)

- **Description**: -
- **Summary**: type=`items-count`, item_paths=['protocol_type'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-0001",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "There must be a protocol with type [CHMO, CHMO:0000470, mass spectrometry] in protocols",
    "item_value_jsonpath_list": [
      "protocol_type"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "CHMO",
          "cv_accession": "CHMO:0000470",
          "name": "mass spectrometry"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

### `$.protocols[*]`

- **Requirement Group** (2 requirement(s))

#### `ms-protocols-0010` (`required` — `items-count`)

- **Description**: mass spectrometer parameter must be defined in mass spectrometry protocol
- **Summary**: type=`items-count`, item_paths=['protocol_parameters[*].key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000049`, name=`mass spectrometer`}]), precondition=[path=`protocol_type`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-0010",
  "description": "mass spectrometer parameter must be defined in mass spectrometry protocol",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "protocol_type",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "CHMO",
                "cv_accession": "CHMO:0000470",
                "name": "mass spectrometry"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "protocol_parameters[*].key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "OBI",
          "cv_accession": "OBI:0000049",
          "name": "mass spectrometer"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
}
```

</details>

#### `ms-protocols-0011` (`required` — `items-count`)

- **Description**: acquisition polarity parameter must be defined in mass spectrometry protocol
- **Summary**: type=`items-count`, item_paths=['protocol_parameters[*].key'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}]), precondition=[path=`protocol_type`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`CHMO:0000470`, name=`mass spectrometry`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-0011",
  "description": "acquisition polarity parameter must be defined in mass spectrometry protocol",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "protocol_type",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "CHMO",
                "cv_accession": "CHMO:0000470",
                "name": "mass spectrometry"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "protocol_parameters[*].key"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "MS",
          "cv_accession": "MS:1003776",
          "name": "acquisition polarity"
        }
      ]
    },
    "min_referenced_value_match": 1,
    "max_referenced_value_match": 1
  }
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

- **Requirement Group** (4 requirement(s))

#### `ms-protocols-parameters-0010` (`required` — `items-count`)

- **Description**: At least one mass spectrometer parameter value must be defined and the values must be a child of [MS, MS:1000031, instrument model] CV Term
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000031`, name=`instrument model`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`OBI:0000049`, name=`mass spectrometer`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-parameters-0010",
  "description": "At least one mass spectrometer parameter value must be defined and the values must be a child of [MS, MS:1000031, instrument model] CV Term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "OBI",
                "cv_accession": "OBI:0000049",
                "name": "mass spectrometer"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "parent-cv-term",
          "parent_cv_terms": [
            {
              "cv_label": "MS",
              "cv_accession": "MS:1000031",
              "name": "instrument model"
            }
          ]
        },
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "ILX",
            "wikidata"
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    },
    "min_referenced_value_match": 1
  }
}
```

</details>

#### `ms-protocols-parameters-0020` (`required` — `items-count`)

- **Description**: At least one acquisition polarity parameter value must be defined and the values must be a child of [MS, MS:1003776, acquisition polarity] CV Term
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1003776`, name=`acquisition polarity`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-parameters-0020",
  "description": "At least one acquisition polarity parameter value must be defined and the values must be a child of [MS, MS:1003776, acquisition polarity] CV Term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "MS",
                "cv_accession": "MS:1003776",
                "name": "acquisition polarity"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "parent-cv-term",
          "parent_cv_terms": [
            {
              "cv_label": "MS",
              "cv_accession": "MS:1003776",
              "name": "acquisition polarity"
            }
          ]
        },
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "ILX",
            "wikidata"
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    },
    "min_referenced_value_match": 1
  }
}
```

</details>

#### `ms-protocols-parameters-0030` (`required` — `items-count`)

- **Description**: Ionization type parameter value must be a child of [MS, MS:1000008, ionization type] CV Term
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000008`, name=`ionization type`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000008`, name=`ionization type`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-parameters-0030",
  "description": "Ionization type parameter value must be a child of [MS, MS:1000008, ionization type] CV Term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "MS",
                "cv_accession": "MS:1000008",
                "name": "ionization type"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "parent-cv-term",
          "parent_cv_terms": [
            {
              "cv_label": "MS",
              "cv_accession": "MS:1000008",
              "name": "ionization type"
            }
          ]
        },
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "ILX",
            "wikidata"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    }
  }
}
```

</details>

#### `ms-protocols-parameters-0040` (`required` — `items-count`)

- **Description**: Inlet parameter value must be a child of [MS, MS:1000007, inlet type] CV Term
- **Summary**: type=`items-count`, item_paths=['values[*]'], item_constraint=(type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`parent-cv-term`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000007`, name=`inlet type`}]; type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`NCIT:C48660`, name=`Not Applicable`}, {cv_label=`NCIT`, cv_accession=`NCIT:C126101`, name=`Not Available`}, {cv_label=`NCIT`, cv_accession=`NCIT:C150904`, name=`Masked Data`}]]), precondition=[path=`key`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000007`, name=`inlet type`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-protocols-parameters-0040",
  "description": "Inlet parameter value must be a child of [MS, MS:1000007, inlet type] CV Term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "key",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "MS",
                "cv_accession": "MS:1000007",
                "name": "inlet type"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "item_value_jsonpath_list": [
      "values[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "parent-cv-term",
          "parent_cv_terms": [
            {
              "cv_label": "MS",
              "cv_accession": "MS:1000007",
              "name": "inlet type"
            }
          ]
        },
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "ILX",
            "wikidata"
          ]
        },
        {
          "type": "cv-term-enum",
          "allowed_cv_terms": [
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C48660",
              "name": "Not Applicable"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C126101",
              "name": "Not Available"
            },
            {
              "cv_label": "NCIT",
              "cv_accession": "NCIT:C150904",
              "name": "Masked Data"
            }
          ]
        }
      ],
      "join_operator": "or",
      "min_valid": 1
    }
  }
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

#### `ms-reported_molecular_entities-identifiers-0010` (`required` — `items-count`)

- **Description**: Molecular entity identifier must be a child of [EDAM, EDAM:data_2894, Compound accession]
- **Summary**: type=`items-count`, item_paths=['database_identifiers[*]'], item_constraint=(type=`group`, join_operator=`or`, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:data_2894`, name=`Compound accession`}]])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-reported_molecular_entities-identifiers-0010",
  "description": "Molecular entity identifier must be a child of [EDAM, EDAM:data_2894, Compound accession]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "item_value_jsonpath_list": [
      "database_identifiers[*]"
    ],
    "item_value_match_constraint": {
      "type": "group",
      "constraints": [
        {
          "type": "string-enum",
          "json_path": "source",
          "options": [
            "ILX",
            "wikidata"
          ]
        },
        {
          "type": "parent-cv-term",
          "parent_cv_terms": [
            {
              "cv_label": "EDAM",
              "cv_accession": "EDAM:data_2894",
              "name": "Compound accession"
            }
          ]
        }
      ],
      "join_operator": "or"
    }
  }
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

#### `ms-repository_metadata_file_list-format-0010` (`required` — `group`)

- **Description**: File extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-repository_metadata_file_list-format-0010",
  "description": "File extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "string-enum",
        "json_path": "source",
        "options": [
          "ILX",
          "wikidata"
        ]
      },
      {
        "type": "parent-cv-term",
        "parent_cv_terms": [
          {
            "cv_label": "EDAM",
            "cv_accession": "EDAM:format_1915",
            "name": "Format"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1001459",
            "name": "file format"
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

#### `ms-raw_data_file_list-format-0010` (`required` — `group`)

- **Description**: Raw data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-raw_data_file_list-format-0010",
  "description": "Raw data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "string-enum",
        "json_path": "source",
        "options": [
          "ILX",
          "wikidata"
        ]
      },
      {
        "type": "parent-cv-term",
        "parent_cv_terms": [
          {
            "cv_label": "EDAM",
            "cv_accession": "EDAM:format_1915",
            "name": "Format"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1001459",
            "name": "file format"
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

#### `ms-result_file_list-format-0010` (`required` — `group`)

- **Description**: Result file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-result_file_list-format-0010",
  "description": "Result file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "string-enum",
        "json_path": "source",
        "options": [
          "ILX",
          "wikidata"
        ]
      },
      {
        "type": "parent-cv-term",
        "parent_cv_terms": [
          {
            "cv_label": "EDAM",
            "cv_accession": "EDAM:format_1915",
            "name": "Format"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1001459",
            "name": "file format"
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

#### `ms-derived_data_file_list-format-0010` (`required` — `group`)

- **Description**: Derived data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-derived_data_file_list-format-0010",
  "description": "Derived data file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "string-enum",
        "json_path": "source",
        "options": [
          "ILX",
          "wikidata"
        ]
      },
      {
        "type": "parent-cv-term",
        "parent_cv_terms": [
          {
            "cv_label": "EDAM",
            "cv_accession": "EDAM:format_1915",
            "name": "Format"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1001459",
            "name": "file format"
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

#### `ms-supplementary_file_list-format-0010` (`required` — `group`)

- **Description**: Supplementary file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]
- **Summary**: type=`group`, join_operator=`or`, min_valid=1, constraints=[type=`string-enum`, json_path=`source`, options=[`ILX`, `wikidata`]; type=`parent-cv-term`, parent_cv_terms=[{cv_label=`EDAM`, cv_accession=`EDAM:format_1915`, name=`Format`}, {cv_label=`MS`, cv_accession=`MS:1001459`, name=`file format`}]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "ms-supplementary_file_list-format-0010",
  "description": "Supplementary file extension must be a child of [EDAM, EDAM:format_1915, Format] or [MS, MS:1001459, file format]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "constraints": [
      {
        "type": "string-enum",
        "json_path": "source",
        "options": [
          "ILX",
          "wikidata"
        ]
      },
      {
        "type": "parent-cv-term",
        "parent_cv_terms": [
          {
            "cv_label": "EDAM",
            "cv_accession": "EDAM:format_1915",
            "name": "Format"
          },
          {
            "cv_label": "MS",
            "cv_accession": "MS:1001459",
            "name": "file format"
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
