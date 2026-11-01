# mzTab-M 2.1.0-M Profile for MetaboLights Submissions

MetaboLights submission profile extending the HUPO-PSI mzTab-M 2.1.0-M Default Profile with stricter curation rules for contacts, samples, species ontologies, assays, protocols, study variable groups, and summary quantification

## General Information

| Field | Value |
| :--- | :--- |
| **ID** | `https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-metabolights-profile-2.1.0-M.json` |
| **Name** | mzTab-M 2.1.0-M Profile for MetaboLights Submissions |
| **Version** | `2.1.0-M` |
| **Extends** | `https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json` |
| **Description** | MetaboLights submission profile extending the HUPO-PSI mzTab-M 2.1.0-M Default Profile with stricter curation rules for contacts, samples, species ontologies, assays, protocols, study variable groups, and summary quantification |
| **JSONPath Targets** | 96 |
| **Total Requirements** | 113 (`required`: 107, `recommended`: 6, `optional`: 0) |

## Configuration

| Setting | Value |
| :--- | :--- |
| **CV Term Field Mapping** | `label`: `cv_label`, `accession`: `cv_accession`, `name`: `name`, `value`: `value` |
| **Default WASM Key** | `default` |
| **OPA WASM Definitions** | `default`: entrypoint `mztabm/policies`, path `../shared/resources/mztab-m/mztabm-default-2.1.0-M.wasm` |

## Requirements Summary

| JSONPath | Code | Enforcement | Type / Constraint | Description | Definition Summary |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `$` | `D-0010` | `required` | `opa-field-requirement` | Verifies that abundance column counts in smallMoleculeSummary and smallMoleculeFeature match the number of assays and study variables declared in metadata (abundance_assay vs metadata.assay; abundance_study_variable and abundance_variation_study_variable vs metadata.study_variable) | policy_id=`policy_d_0010` |
| `$` | `D-0020` | `required` | `opa-field-requirement` | Checks that required identifier and reference fields across metadata and table sections (id, sml_id, smf_id, sme_id, instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, spectra_ref.ms_run) are defined and non-null | policy_id=`policy_d_0020` |
| `$` | `D-0021` | `required` | `opa-field-requirement` | Verifies referential integrity by checking that all cross-referenced IDs (instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, smf_id_refs, sme_id_refs, spectra_ref.ms_run) resolve to existing target items | policy_id=`policy_d_0021` |
| `$` | `D-0022` | `required` | `opa-field-requirement` | Checks that all declared target entities (instrument, sample, ms_run, study_variable_group, assay, smallMoleculeFeature, smallMoleculeEvidence) are referenced by at least one corresponding reference field in the mzTab-M document | policy_id=`policy_d_0022` |
| `$` | `MTBLS-0001` | `required` | `property-check` | MetaboLights mzTab-M submissions MUST define the metadata and smallMoleculeSummary sections | required_properties=[`metadata`, `smallMoleculeSummary`] |
| `$.comment[*]` | `D-COMMENT-0001` | `required` | `regex` | Each comment entry prefix MUST equal 'COM' | type=`regex`, json_path=`$.prefix`, pattern=`^COM$` |
| `$.comment[*].message` | `D-COMMENT-MESSAGE-0001` | `required` | `string-length` | Each comment entry MUST contain a non-empty message string (minimum 1 character) | type=`string-length`, min_length=1 |
| `$.comment[*].line_number` | `D-COMMENT-LINE_NUMBER-0001` | `required` | `positive-integer` | Comment line_number, when provided, MUST be a positive integer (>= 1) | type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata` | `MTBLS-METADATA-0001` | `required` | `property-check` | Metadata MUST include all mandatory MetaboLights study metadata properties (including protocol, sample, contact, and study_variable_group) and is recommended to include software and publication | match_is_required=True; required_properties=[`prefix`, `mzTab-version`, `mzTab-ID`, `quantification_method`, `ms_run`, `assay`, `study_variable`, `cv`, `database`, `small_molecule-quantification_unit`, `id_confidence_measure`, `study_variable_group`, `protocol`, `sample`, `contact`]; recommended_properties=[`software`, `publication`] |
| `$.metadata.prefix` | `D-METADATA-PREFIX-0001` | `required` | `regex` | Metadata prefix MUST be present and equal 'MTD' | match_is_required=True; type=`regex`, pattern=`^MTD$` |
| `$.metadata.mzTab-version` | `D-METADATA-MZTAB_VERSION-0001` | `required` | `regex` | Metadata mzTab-version MUST be present and follow the version format 'X.Y.Z-M' (e.g. '2.1.0-M') | match_is_required=True; type=`regex`, pattern=`^\d{1}\.\d{1}\.\d{1}-[A-Z]{1}$` |
| `$.metadata.mzTab-ID` | `D-METADATA-MZTAB_ID-0001` | `required` | `string-length` | Metadata mzTab-ID MUST be present and be a non-empty string identifier (minimum 1 character) | match_is_required=True; type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.software` | `D-METADATA-SOFTWARE-0001` | `recommended` | `items-count` | At least one data processing or analysis software entry is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.software[*]` | `D-METADATA-SOFTWARE-0010` | `recommended` | `property-check` | Each software entry MUST define its software CV parameter | required_properties=[`parameter`] |
| `$.metadata.software[*].parameter` | `D-METADATA-SOFTWARE-0011` | `required` | `cv-term` | Software parameter MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.publication` | `MTBLS-METADATA-PUBLICATION-0001` | `recommended` | `items-count` | At least one publication entry is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.publication[*]` | `D-MTD-PUBLICATION-0010` | `required` | `items-count` | Each publication entry MUST define publication_items with at least one citation item | required_properties=[`publication_items`]; type=`items-count`, json_path=`$.publication_items`, min_occurs=1 |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0020` | `required` | `group` | Each publication item MUST define both type ('doi', 'pubmed', or 'uri') and a non-null accession | required_properties=[`type`, `accession`]; type=`group`, join_operator=`and`, constraints=[type=`regex`, json_path=`$.type`, pattern=`^doi\|pubmed\|uri$`, null_values=[None, 'null', '', '""', "''"]; type=`not-null`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"]] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0021` | `required` | `regex` | When publication item type is 'doi', accession MUST follow valid DOI syntax ('10.<prefix>/<suffix>') | type=`regex`, json_path=`$.accession`, pattern=`^10\..+/.+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^doi$`)] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0022` | `required` | `regex` | When publication item type is 'pubmed', accession MUST be a numeric PubMed identifier | type=`regex`, json_path=`$.accession`, pattern=`^\d+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^pubmed$`)] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0023` | `required` | `uri` | When publication item type is 'uri', accession MUST be a valid URI | type=`uri`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^uri$`)] |
| `$.metadata.contact` | `MTBLS-METADATA-CONTACT-0001` | `required` | `items-count` | At least one study contact MUST be defined in metadata for MetaboLights submissions | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.contact[*]` | `MTBLS-METADATA-CONTACT-0010` | `required` | `property-check` | Each contact entry MUST define name, email, and affiliation, and is recommended to include an ORCID identifier | required_properties=[`name`, `email`, `affiliation`]; recommended_properties=[`orcid`] |
| `$.metadata.contact[*].name` | `MTBLS-METADATA-CONTACT-0020` | `required` | `string-length` | Contact full name MUST be at least 5 characters long | type=`string-length`, min_length=5 |
| `$.metadata.contact[*].email` | `MTBLS-METADATA-CONTACT-0030` | `required` | `email` | Contact email MUST be a valid email address | type=`email` |
| `$.metadata.contact[*].affiliation` | `MTBLS-METADATA-CONTACT-0040` | `required` | `string-length` | Contact affiliation MUST be at least 2 characters long | type=`string-length`, min_length=2 |
| `$.metadata.contact[*].orcid` | `MTBLS-METADATA-CONTACT-0050` | `recommended` | `regex` | Contact ORCID is recommended and, when provided, MUST match the standard 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX) | type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$`, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.uri[*].value` | `D-MTD-CONTACT-URI-0010` | `required` | `uri` | Each metadata URI entry value MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.external_study_uri[*].value` | `D-MTD-CONTACT-EXTERNAL_STUDY_URI-0010` | `required` | `uri` | Each external study URI entry value MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.quantification_method` | `D-MTD-CONTACT-QUANTIFICATION_METHOD-0001` | `required` | `cv-term` | Metadata quantification_method MUST be present and defined as a valid ontology CV term or user-defined parameter | match_is_required=True; type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.sample` | `MTBLS-METADATA-SAMPLE-0001` | `required` | `items-count` | At least one biological or experimental sample MUST be defined in metadata for MetaboLights submissions | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.sample[*]` | `MTBLS-METADATA-SAMPLE-0010` | `recommended` | `property-check` | Each sample entry MUST define name and species, and is recommended to include tissue and cell_type | required_properties=[`name`, `species`]; recommended_properties=[`tissue`, `cell_type`] |
| `$.metadata.sample[*].name` | `MTBLS-METADATA-SAMPLE-0020` | `required` | `string-length` | Sample name MUST be at least 2 characters long | type=`string-length`, min_length=2 |
| `$.metadata.sample[*].species[*]` | `MTBLS-METADATA-SAMPLE-0031` | `required` | `allowed-cv-list` | Sample species CV terms MUST be sourced from NCBITAXON, ENVO, or CHEBI ontologies (user-defined terms are not permitted) | type=`allowed-cv-list`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False |
| `$.metadata.ms_run` | `MTBLS-METADATA-MS_RUN-0001` | `required` | `items-count` | At least one MS run MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.ms_run[*]` | `MTBLS-METADATA-MS_RUN-0010` | `required` | `property-check` | Each MS run entry MUST define its data file location | required_properties=[`location`] |
| `$.metadata.ms_run[*].location` | `D-MTD-MS_RUN-0020` | `required` | `uri` | Each MS run location MUST be a valid non-null URI pointing to the raw or processed mass spectrometry data file | type=`uri`, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.ms_run[*].scan_polarity` | `D-MTD-MS_RUN-0030` | `required` | `items-count` | Each MS run scan_polarity list may contain at most 2 polarity entries (positive and/or negative scan) | type=`items-count`, max_occurs=2 |
| `$.metadata.ms_run[*].scan_polarity[*]` | `D-MTD-MS_RUN-0031` | `required` | `cv-term-enum` | Each MS run scan_polarity entry MUST be either 'negative scan' (MS:1000129) or 'positive scan' (MS:1000130) | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000129`, name=`negative scan`}, {cv_label=`MS`, cv_accession=`MS:1000130`, name=`positive scan`}] |
| `$.metadata.assay` | `MTBLS-METADATA-ASSAY-0001` | `required` | `items-count` | At least one assay MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.assay[*]` | `MTBLS-METADATA-ASSAY-0010` | `required` | `property-check` | Each assay entry MUST define name, ms_run_ref, and sample_ref for MetaboLights submissions | required_properties=[`name`, `ms_run_ref`, `sample_ref`] |
| `$.metadata.assay[*].name` | `D-MTD-ASSAY-0030` | `required` | `string-length` | Assay name MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1 |
| `$.metadata.assay[*].ms_run_ref` | `D-MTD-ASSAY-0040` | `required` | `items-count` | Each assay ms_run_ref list MUST contain at least one MS run reference | type=`items-count`, min_occurs=1 |
| `$.metadata.assay[*].ms_run_ref[*]` | `D-MTD-ASSAY-0041` | `required` | `positive-integer` | Each MS run reference in assay.ms_run_ref MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.assay[*].protocol_refs[*]` | `D-MTD-ASSAY-0050` | `required` | `positive-integer` | Each protocol reference in assay.protocol_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.assay[*].sample_ref` | `MTBLS-METADATA-ASSAY-0020` | `required` | `positive-integer` | Each assay sample_ref MUST be a non-null positive integer (>= 1) referencing a declared sample | type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.assay[*].external_uri` | `D-MTD-ASSAY-0070` | `required` | `uri` | Assay external_uri, when provided, MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.protocol[*]` | `MTBLS-METADATA-PROTOCOL-0010` | `required` | `property-check` | Each protocol entry MUST define both name and type | required_properties=[`name`, `type`] |
| `$.metadata.protocol[*].name` | `MTBLS-METADATA-PROTOCOL-0020` | `required` | `string-length` | Protocol name MUST be a valid string | type=`string-length` |
| `$.metadata.protocol[*].type` | `MTBLS-METADATA-PROTOCOL-0030` | `required` | `cv-term` | Protocol type MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.study_variable_group[*]` | `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0010` | `required` | `property-check` | Each study variable group MUST define name and type, and is recommended to define datatype | required_properties=[`name`, `type`]; recommended_properties=[`datatype`] |
| `$.metadata.study_variable_group[*].name` | `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0020` | `required` | `group` | Study variable group name MUST be either a valid CV term or a string of at least 2 characters | type=`group`, join_operator=`or`, constraints=[type=`cv-term`, allow_user_defined_terms=True; type=`string-length`, min_length=2] |
| `$.metadata.study_variable_group[*].type` | `D-MTD-STUDY_VARIABLE_GROUP-0030` | `required` | `cv-term` | Study variable group type MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.study_variable_group[*].datatype` | `D-MTD-STUDY_VARIABLE_GROUP-0040` | `required` | `string-enum` | Study variable group datatype MUST be one of the allowed XML Schema datatypes (xsd:string, xsd:integer, xsd:decimal, xsd:boolean, xsd:date, xsd:time, xsd:dateTime, xsd:anyURI) or 'Parameter' | type=`string-enum`, options=[`xsd:string`, `xsd:integer`, `xsd:decimal`, `xsd:boolean`, `xsd:date`, ... (+4 more)] |
| `$.metadata.study_variable` | `D-MTD-STUDY_VARIABLE-0001` | `required` | `items-count` | At least one study variable MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.study_variable[*]` | `MTBLS-METADATA-STUDY_VARIABLE-0010` | `required` | `property-check` | Each study variable MUST define name, group_refs, and assay_refs | required_properties=[`name`, `group_refs`, `assay_refs`] |
| `$.metadata.study_variable[*].name` | `D-MTD-STUDY_VARIABLE-0030` | `required` | `group` | Study variable name MUST be a valid CV term, non-empty string, integer, decimal, or boolean value | type=`group`, join_operator=`or`, constraints=[type=`cv-term`, allow_user_defined_terms=True; type=`string-length`, min_length=1; type=`integer`; type=`decimal`; type=`boolean`] |
| `$.metadata.study_variable[*].group_refs[*]` | `D-MTD-STUDY_VARIABLE-0041` | `required` | `positive-integer` | Each group reference in study_variable.group_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.study_variable[*].assay_refs[*]` | `D-MTD-STUDY_VARIABLE-0051` | `required` | `positive-integer` | Each assay reference in study_variable.assay_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.study_variable[*].ms_run_refs[*]` | `D-MTD-STUDY_VARIABLE-0061` | `required` | `positive-integer` | Each MS run reference in study_variable.ms_run_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.custom[*]` | `D-MTD-CUSTOM-0010` | `required` | `cv-term` | Each custom metadata parameter MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.cv` | `D-MTD-CV-0001` | `required` | `items-count` | At least one controlled vocabulary (CV) source MUST be declared in metadata.cv | type=`items-count`, min_occurs=1 |
| `$.metadata.cv[*]` | `D-MTD-CV-0010` | `required` | `property-check` | Each controlled vocabulary entry MUST define label, full_name, version, and uri | required_properties=[`label`, `full_name`, `version`, `uri`] |
| `$.metadata.cv[*].label` | `D-MTD-CV-0020` | `required` | `string-length` | Controlled vocabulary label MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.cv[*].full_name` | `D-MTD-CV-0030` | `required` | `string-length` | Controlled vocabulary full_name MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.cv[*].version` | `D-MTD-CV-0040` | `required` | `string-length` | Controlled vocabulary version MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.cv[*].uri` | `D-MTD-CV-0050` | `required` | `uri` | Controlled vocabulary uri MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.small_molecule-quantification_unit` | `D-MTD-SML_QUANTIFICATION_UNIT-0001` | `required` | `cv-term` | Metadata small_molecule-quantification_unit MUST be present and defined as a valid ontology CV term or user-defined parameter | match_is_required=True; type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.small_molecule_feature-quantification_unit` | `D-MTD-SMF_QUANTIFICATION_UNIT-0001` | `required` | `cv-term` | Metadata small_molecule_feature-quantification_unit, when present, MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.small_molecule-identification_reliability` | `D-MTD-SML_IDENTIFICATION_RELIABILITY-0001` | `required` | `cv-term` | Metadata small_molecule-identification_reliability, when present, MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.database` | `D-MTD-DATABASE-0001` | `required` | `items-count` | At least one chemical or metabolite database MUST be declared in metadata.database | type=`items-count`, min_occurs=1 |
| `$.metadata.database[*]` | `D-MTD-DATABASE-0010` | `required` | `property-check` | Each database entry MUST define param, prefix, version, and uri | required_properties=[`param`, `prefix`, `version`, `uri`] |
| `$.metadata.database[*]` | `D-MTD-DATABASE-0020` | `required` | `cv-term` | Database param MUST be a valid ontology CV term or user-defined parameter identifying the database | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True |
| `$.metadata.database[*]` | `D-MTD-DATABASE-0030` | `required` | `string-length` | Database version MUST be a non-empty string (minimum 1 character) or 'Unknown' | type=`string-length`, json_path=`$.version`, min_length=1, exceptional_values=['Unknown'] |
| `$.metadata.database[*]` | `D-MTD-DATABASE-0040` | `required` | `uri` | Database uri, when provided, MUST be a valid URI | type=`uri`, json_path=`$.uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.id_confidence_measure` | `D-MTD-ID_CONFIDENCE_MEASURE-0001` | `required` | `items-count` | At least one identification confidence measure MUST be declared in metadata.id_confidence_measure | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.id_confidence_measure[*]` | `D-MTD-ID_CONFIDENCE_MEASURE-0010` | `required` | `cv-term` | Each identification confidence measure MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.colunit-small_molecule[*]` | `D-MTD-COLUNIT_SML-0010` | `required` | `property-check` | Each smallMoleculeSummary column unit entry MUST define column_name and param | required_properties=[`column_name`, `param`] |
| `$.metadata.colunit-small_molecule[*]` | `D-MTD-COLUNIT_SML-0020` | `required` | `string-length` | SmallMoleculeSummary column unit column_name MUST be a non-empty string (minimum 1 character) | type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.colunit-small_molecule[*]` | `D-MTD-COLUNIT_SML-0030` | `required` | `cv-term` | SmallMoleculeSummary column unit param MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True |
| `$.metadata.colunit-small_molecule_feature[*]` | `D-MTD-COLUNIT_SMF-0010` | `required` | `property-check` | Each smallMoleculeFeature column unit entry MUST define column_name and param | required_properties=[`column_name`, `param`] |
| `$.metadata.colunit-small_molecule_feature[*]` | `D-MTD-COLUNIT_SMF-0020` | `required` | `string-length` | SmallMoleculeFeature column unit column_name MUST be a non-empty string (minimum 1 character) | type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.colunit-small_molecule_feature[*]` | `D-MTD-COLUNIT_SMF-0030` | `required` | `cv-term` | SmallMoleculeFeature column unit param MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True |
| `$.metadata.colunit-small_molecule_evidence[*]` | `D-MTD-COLUNIT_SME-0010` | `required` | `property-check` | Each smallMoleculeEvidence column unit entry MUST define column_name and param | required_properties=[`column_name`, `param`] |
| `$.metadata.colunit-small_molecule_evidence[*]` | `D-MTD-COLUNIT_SME-0020` | `required` | `string-length` | SmallMoleculeEvidence column unit column_name MUST be a non-empty string (minimum 1 character) | type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.colunit-small_molecule_evidence[*]` | `D-MTD-COLUNIT_SME-0030` | `required` | `cv-term` | SmallMoleculeEvidence column unit param MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True |
| `$.smallMoleculeSummary` | `MTBLS-SML-0001` | `required` | `items-count` | The smallMoleculeSummary (SML) table MUST be present and contain at least one summary row | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.smallMoleculeSummary[*]` | `D-SML-0010` | `required` | `property-check` | Each smallMoleculeSummary (SML) row MUST define all mandatory SML properties (prefix, header_prefix, sml_id, smf_id_refs, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, theoretical_neutral_mass, adduct_ions, reliability, best_id_confidence_measure, best_id_confidence_value) | required_properties=[`prefix`, `header_prefix`, `sml_id`, `smf_id_refs`, `database_identifier`, `chemical_formula`, `smiles`, `inchi`, `chemical_name`, `uri`, `theoretical_neutral_mass`, `adduct_ions`, `reliability`, `best_id_confidence_measure`, `best_id_confidence_value`] |
| `$.smallMoleculeSummary[*].prefix` | `D-SML-PREFIX-0001` | `required` | `regex` | Each smallMoleculeSummary row prefix MUST equal 'SML' | type=`regex`, pattern=`^SML$` |
| `$.smallMoleculeSummary[*].header_prefix` | `D-SML-HEADER_PREFIX-0001` | `required` | `regex` | Each smallMoleculeSummary row header_prefix MUST equal 'SMH' | type=`regex`, pattern=`^SMH$` |
| `$.smallMoleculeSummary[*].sml_id` | `D-SML-SML_ID-0001` | `required` | `positive-integer` | Each smallMoleculeSummary sml_id MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeSummary[*].smf_id_refs[*]` | `D-SML-SMF_ID_REFS-0010` | `required` | `positive-integer` | Each feature reference in smallMoleculeSummary.smf_id_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeSummary[*].database_identifier[*]` | `D-SML-DATABASE_IDENTIFIER-0010` | `required` | `not-null` | Each database_identifier entry in smallMoleculeSummary MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].chemical_formula[*]` | `D-SML-CHEMICAL_FORMULA-0010` | `required` | `not-null` | Each chemical_formula entry in smallMoleculeSummary MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].smiles[*]` | `D-SML-SMILES-0010` | `required` | `not-null` | Each SMILES entry in smallMoleculeSummary MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].inchi[*]` | `D-SML-INCHI-0010` | `required` | `not-null` | Each InChI entry in smallMoleculeSummary MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].chemical_name[*]` | `D-SML-CHEMICAL_NAME-0010` | `required` | `not-null` | Each chemical_name entry in smallMoleculeSummary MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].uri[*]` | `D-SML-URI-0010` | `required` | `uri` | Each URI entry in smallMoleculeSummary.uri MUST be a valid non-null URI | type=`uri`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].theoretical_neutral_mass[*]` | `D-SML-THEORETICAL_NEUTRAL_MASS-0010` | `required` | `decimal` | Each theoretical_neutral_mass entry in smallMoleculeSummary MUST be a valid decimal number or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeSummary[*].adduct_ions[*]` | `D-SML-ADDUCT_IONS-0010` | `required` | `regex` | Each adduct ion in smallMoleculeSummary.adduct_ions MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-' | type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$` |
| `$.smallMoleculeSummary[*].best_id_confidence_measure` | `D-SML-BEST_ID_CONFIDENCE_MEASURE-0001` | `required` | `cv-term` | SmallMoleculeSummary best_id_confidence_measure MUST be a valid ontology CV term or user-defined parameter (or null) | type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].best_id_confidence_value` | `D-SML-BEST_ID_CONFIDENCE_VALUE-0001` | `recommended` | `decimal` | SmallMoleculeSummary best_id_confidence_value is recommended to be a non-null decimal value | type=`decimal`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeSummary[*].abundance_assay[*]` | `D-SML-ABUNDANCE_ASSAY-0010` | `required` | `decimal` | Each assay abundance value in smallMoleculeSummary.abundance_assay MUST be a decimal number or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeSummary[*].abundance_study_variable[*]` | `D-SML-ABUNDANCE_STUDY_VARIABLE-0010` | `required` | `decimal` | Each study variable abundance value in smallMoleculeSummary.abundance_study_variable MUST be a decimal number or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeSummary[*].abundance_variation_study_variable[*]` | `D-SML-ABUNDANCE_VARIATION_STUDY_VARIABLE-0010` | `required` | `decimal` | Each study variable abundance variation value in smallMoleculeSummary.abundance_variation_study_variable MUST be a decimal number or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeSummary[*].opt[*]` | `D-SML-OPT-0010` | `required` | `property-check` | Each optional column entry in smallMoleculeSummary.opt is recommended to define identifier and param | recommended_properties=[`identifier`, `param`] |
| `$.smallMoleculeSummary[*].opt[*]` | `D-SML-OPT-0020` | `required` | `regex` | Optional column identifier in smallMoleculeSummary.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]' | type=`regex`, json_path=`$.identifier`, pattern=`^global\|ms_run\[\d+\]\|assay\[\d+\]\|study_variable\[\d+\]$` |
| `$.smallMoleculeSummary[*].opt[*]` | `D-SML-OPT-0030` | `required` | `cv-term` | Optional column param in smallMoleculeSummary.opt MUST be a valid non-null ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.sample[*].species` | `MTBLS-METADATA-SAMPLE-0030` | `required` | `items-count` | Each sample MUST specify at least one species ontology term | type=`items-count`, min_occurs=1 |
| `$.metadata.protocol` | `MTBLS-METADATA-PROTOCOL-0001` | `required` | `items-count` | Metadata protocols MUST include all 6 required MetaboLights protocol types (EFO:0005518, EFO:0000490, CHMO:0000470, CHMO:0001000, OBI:0200000, MI:2131) | match_is_required=True; type=`items-count`, min_occurs=1, min_match=6, item_paths=['$[*].type.cv_accession'] |
| `$.metadata.study_variable_group` | `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0001` | `required` | `items-count` | At least one study variable group MUST be defined in metadata for MetaboLights submissions | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.study_variable[*].group_refs` | `MTBLS-METADATA-STUDY_VARIABLE-0020` | `required` | `items-count` | Each study variable MUST reference at least one study variable group in group_refs | type=`items-count`, min_occurs=1 |
| `$.metadata.study_variable[*].assay_refs` | `MTBLS-METADATA-STUDY_VARIABLE-0030` | `required` | `items-count` | Each study variable MUST reference at least one assay in assay_refs | type=`items-count`, min_occurs=1 |

## Requirement Definitions by JSONPath

### `$`

- **Requirement Group** (4 requirement(s))
- **Group Description**: Global cross-section OPA policy rules validating abundance column counts and cross-entity identifier referential integrity across the mzTab-M document

#### `D-0010` (`required` — `opa-field-requirement`)

- **Description**: Verifies that abundance column counts in smallMoleculeSummary and smallMoleculeFeature match the number of assays and study variables declared in metadata (abundance_assay vs metadata.assay; abundance_study_variable and abundance_variation_study_variable vs metadata.study_variable)
- **Summary**: policy_id=`policy_d_0010`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-0010",
  "description": "Verifies that abundance column counts in smallMoleculeSummary and smallMoleculeFeature match the number of assays and study variables declared in metadata (abundance_assay vs metadata.assay; abundance_study_variable and abundance_variation_study_variable vs metadata.study_variable)",
  "enforcement_level": "required",
  "policy_id": "policy_d_0010"
}
```

</details>

#### `D-0020` (`required` — `opa-field-requirement`)

- **Description**: Checks that required identifier and reference fields across metadata and table sections (id, sml_id, smf_id, sme_id, instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, spectra_ref.ms_run) are defined and non-null
- **Summary**: policy_id=`policy_d_0020`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-0020",
  "description": "Checks that required identifier and reference fields across metadata and table sections (id, sml_id, smf_id, sme_id, instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, spectra_ref.ms_run) are defined and non-null",
  "enforcement_level": "required",
  "policy_id": "policy_d_0020"
}
```

</details>

#### `D-0021` (`required` — `opa-field-requirement`)

- **Description**: Verifies referential integrity by checking that all cross-referenced IDs (instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, smf_id_refs, sme_id_refs, spectra_ref.ms_run) resolve to existing target items
- **Summary**: policy_id=`policy_d_0021`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-0021",
  "description": "Verifies referential integrity by checking that all cross-referenced IDs (instrument_ref, sample_ref, ms_run_refs, group_refs, assay_refs, smf_id_refs, sme_id_refs, spectra_ref.ms_run) resolve to existing target items",
  "enforcement_level": "required",
  "policy_id": "policy_d_0021"
}
```

</details>

#### `D-0022` (`required` — `opa-field-requirement`)

- **Description**: Checks that all declared target entities (instrument, sample, ms_run, study_variable_group, assay, smallMoleculeFeature, smallMoleculeEvidence) are referenced by at least one corresponding reference field in the mzTab-M document
- **Summary**: policy_id=`policy_d_0022`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-0022",
  "description": "Checks that all declared target entities (instrument, sample, ms_run, study_variable_group, assay, smallMoleculeFeature, smallMoleculeEvidence) are referenced by at least one corresponding reference field in the mzTab-M document",
  "enforcement_level": "required",
  "policy_id": "policy_d_0022"
}
```

</details>

### `$`

#### `MTBLS-0001` (`required` — `property-check`)

- **Description**: MetaboLights mzTab-M submissions MUST define the metadata and smallMoleculeSummary sections
- **Summary**: required_properties=[`metadata`, `smallMoleculeSummary`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-0001",
  "description": "MetaboLights mzTab-M submissions MUST define the metadata and smallMoleculeSummary sections",
  "enforcement_level": "required",
  "required_properties": [
    "metadata",
    "smallMoleculeSummary"
  ]
}
```

</details>

### `$.comment[*]`

#### `D-COMMENT-0001` (`required` — `regex`)

- **Description**: Each comment entry prefix MUST equal 'COM'
- **Summary**: type=`regex`, json_path=`$.prefix`, pattern=`^COM$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-COMMENT-0001",
  "description": "Each comment entry prefix MUST equal 'COM'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "Comment prefix must match the literal string 'COM'",
    "json_path": "$.prefix",
    "pattern": "^COM$",
    "case_sensitive": true
  }
}
```

</details>

### `$.comment[*].message`

#### `D-COMMENT-MESSAGE-0001` (`required` — `string-length`)

- **Description**: Each comment entry MUST contain a non-empty message string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-COMMENT-MESSAGE-0001",
  "description": "Each comment entry MUST contain a non-empty message string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Comment message must have a minimum length of 1 character",
    "minimum": 1
  }
}
```

</details>

### `$.comment[*].line_number`

#### `D-COMMENT-LINE_NUMBER-0001` (`required` — `positive-integer`)

- **Description**: Comment line_number, when provided, MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-COMMENT-LINE_NUMBER-0001",
  "description": "Comment line_number, when provided, MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Comment line number must be a positive integer (>= 1) or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ],
    "minimum": 1
  }
}
```

</details>

### `$.metadata`

#### `MTBLS-METADATA-0001` (`required` — `property-check`)

- **Description**: Metadata MUST include all mandatory MetaboLights study metadata properties (including protocol, sample, contact, and study_variable_group) and is recommended to include software and publication
- **Summary**: match_is_required=True; required_properties=[`prefix`, `mzTab-version`, `mzTab-ID`, `quantification_method`, `ms_run`, `assay`, `study_variable`, `cv`, `database`, `small_molecule-quantification_unit`, `id_confidence_measure`, `study_variable_group`, `protocol`, `sample`, `contact`]; recommended_properties=[`software`, `publication`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-0001",
  "description": "Metadata MUST include all mandatory MetaboLights study metadata properties (including protocol, sample, contact, and study_variable_group) and is recommended to include software and publication",
  "enforcement_level": "required",
  "match_is_required": true,
  "required_properties": [
    "prefix",
    "mzTab-version",
    "mzTab-ID",
    "quantification_method",
    "ms_run",
    "assay",
    "study_variable",
    "cv",
    "database",
    "small_molecule-quantification_unit",
    "id_confidence_measure",
    "study_variable_group",
    "protocol",
    "sample",
    "contact"
  ],
  "recommended_properties": [
    "software",
    "publication"
  ]
}
```

</details>

### `$.metadata.prefix`

#### `D-METADATA-PREFIX-0001` (`required` — `regex`)

- **Description**: Metadata prefix MUST be present and equal 'MTD'
- **Summary**: match_is_required=True; type=`regex`, pattern=`^MTD$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-PREFIX-0001",
  "description": "Metadata prefix MUST be present and equal 'MTD'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "Metadata prefix must match the literal string 'MTD'",
    "pattern": "^MTD$",
    "case_sensitive": true
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.mzTab-version`

#### `D-METADATA-MZTAB_VERSION-0001` (`required` — `regex`)

- **Description**: Metadata mzTab-version MUST be present and follow the version format 'X.Y.Z-M' (e.g. '2.1.0-M')
- **Summary**: match_is_required=True; type=`regex`, pattern=`^\d{1}\.\d{1}\.\d{1}-[A-Z]{1}$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-MZTAB_VERSION-0001",
  "description": "Metadata mzTab-version MUST be present and follow the version format 'X.Y.Z-M' (e.g. '2.1.0-M')",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "mzTab-version must match pattern ^\\d{1}\\.\\d{1}\\.\\d{1}-[A-Z]{1}$",
    "pattern": "^\\d{1}\\.\\d{1}\\.\\d{1}-[A-Z]{1}$",
    "case_sensitive": true
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.mzTab-ID`

#### `D-METADATA-MZTAB_ID-0001` (`required` — `string-length`)

- **Description**: Metadata mzTab-ID MUST be present and be a non-empty string identifier (minimum 1 character)
- **Summary**: match_is_required=True; type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-MZTAB_ID-0001",
  "description": "Metadata mzTab-ID MUST be present and be a non-empty string identifier (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "mzTab-ID must be a non-null string of at least 1 character",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.software`

#### `D-METADATA-SOFTWARE-0001` (`recommended` — `items-count`)

- **Description**: At least one data processing or analysis software entry is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-SOFTWARE-0001",
  "description": "At least one data processing or analysis software entry is recommended in metadata",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 software entry in metadata.software",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.software[*]`

#### `D-METADATA-SOFTWARE-0010` (`recommended` — `property-check`)

- **Description**: Each software entry MUST define its software CV parameter
- **Summary**: required_properties=[`parameter`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-SOFTWARE-0010",
  "description": "Each software entry MUST define its software CV parameter",
  "enforcement_level": "recommended",
  "required_properties": [
    "parameter"
  ]
}
```

</details>

### `$.metadata.software[*].parameter`

#### `D-METADATA-SOFTWARE-0011` (`required` — `cv-term`)

- **Description**: Software parameter MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-SOFTWARE-0011",
  "description": "Software parameter MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Software parameter must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.publication`

#### `MTBLS-METADATA-PUBLICATION-0001` (`recommended` — `items-count`)

- **Description**: At least one publication entry is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-PUBLICATION-0001",
  "description": "At least one publication entry is recommended in metadata",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 publication entry in metadata.publication",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.publication[*]`

#### `D-MTD-PUBLICATION-0010` (`required` — `items-count`)

- **Description**: Each publication entry MUST define publication_items with at least one citation item
- **Summary**: required_properties=[`publication_items`]; type=`items-count`, json_path=`$.publication_items`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0010",
  "description": "Each publication entry MUST define publication_items with at least one citation item",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 item in publication_items",
    "json_path": "$.publication_items",
    "min_occurs": 1
  },
  "required_properties": [
    "publication_items"
  ]
}
```

</details>

### `$.metadata.publication[*].publication_items[*]`

- **Requirement Group** (4 requirement(s))
- **Group Description**: Validation rules for individual publication citation items (DOI, PubMed ID, or URI)

#### `D-MTD-PUBLICATION-0020` (`required` — `group`)

- **Description**: Each publication item MUST define both type ('doi', 'pubmed', or 'uri') and a non-null accession
- **Summary**: required_properties=[`type`, `accession`]; type=`group`, join_operator=`and`, constraints=[type=`regex`, json_path=`$.type`, pattern=`^doi|pubmed|uri$`, null_values=[None, 'null', '', '""', "''"]; type=`not-null`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"]]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0020",
  "description": "Each publication item MUST define both type ('doi', 'pubmed', or 'uri') and a non-null accession",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "description": "Publication item must have a valid type ('doi', 'pubmed', or 'uri') and a non-null accession",
    "constraints": [
      {
        "type": "regex",
        "description": "Publication item type must be 'doi', 'pubmed', or 'uri'",
        "json_path": "$.type",
        "null_values": [
          null,
          "null",
          "",
          "\"\"",
          "''"
        ],
        "pattern": "^doi|pubmed|uri$",
        "case_sensitive": true
      },
      {
        "type": "not-null",
        "description": "Publication item accession must not be null or empty",
        "json_path": "$.accession",
        "null_values": [
          null,
          "null",
          "",
          "\"\"",
          "''"
        ]
      }
    ],
    "join_operator": "and"
  },
  "required_properties": [
    "type",
    "accession"
  ]
}
```

</details>

#### `D-MTD-PUBLICATION-0021` (`required` — `regex`)

- **Description**: When publication item type is 'doi', accession MUST follow valid DOI syntax ('10.<prefix>/<suffix>')
- **Summary**: type=`regex`, json_path=`$.accession`, pattern=`^10\..+/.+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^doi$`)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0021",
  "description": "When publication item type is 'doi', accession MUST follow valid DOI syntax ('10.<prefix>/<suffix>')",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "DOI accession must match pattern ^10\\..+/.+$ when type is 'doi'",
    "precondition": {
      "evaluations": [
        {
          "json_path": "$.type",
          "constraint": {
            "type": "regex",
            "description": "Checks whether publication item type equals 'doi'",
            "pattern": "^doi$",
            "case_sensitive": true
          },
          "default_evaluation": false,
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "$.accession",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ],
    "pattern": "^10\\..+/.+$",
    "case_sensitive": true
  }
}
```

</details>

#### `D-MTD-PUBLICATION-0022` (`required` — `regex`)

- **Description**: When publication item type is 'pubmed', accession MUST be a numeric PubMed identifier
- **Summary**: type=`regex`, json_path=`$.accession`, pattern=`^\d+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^pubmed$`)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0022",
  "description": "When publication item type is 'pubmed', accession MUST be a numeric PubMed identifier",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "PubMed accession must contain only digits (^\\d+$) when type is 'pubmed'",
    "precondition": {
      "evaluations": [
        {
          "json_path": "$.type",
          "constraint": {
            "type": "regex",
            "description": "Checks whether publication item type equals 'pubmed'",
            "pattern": "^pubmed$",
            "case_sensitive": true
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "$.accession",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ],
    "pattern": "^\\d+$",
    "case_sensitive": true
  }
}
```

</details>

#### `D-MTD-PUBLICATION-0023` (`required` — `uri`)

- **Description**: When publication item type is 'uri', accession MUST be a valid URI
- **Summary**: type=`uri`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^uri$`)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0023",
  "description": "When publication item type is 'uri', accession MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "Publication accession must be a valid URI when type is 'uri'",
    "precondition": {
      "evaluations": [
        {
          "json_path": "$.type",
          "constraint": {
            "type": "regex",
            "description": "Checks whether publication item type equals 'uri'",
            "pattern": "^uri$",
            "case_sensitive": true
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "$.accession",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.contact`

#### `MTBLS-METADATA-CONTACT-0001` (`required` — `items-count`)

- **Description**: At least one study contact MUST be defined in metadata for MetaboLights submissions
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0001",
  "description": "At least one study contact MUST be defined in metadata for MetaboLights submissions",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 contact entry in metadata.contact",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.contact[*]`

#### `MTBLS-METADATA-CONTACT-0010` (`required` — `property-check`)

- **Description**: Each contact entry MUST define name, email, and affiliation, and is recommended to include an ORCID identifier
- **Summary**: required_properties=[`name`, `email`, `affiliation`]; recommended_properties=[`orcid`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0010",
  "description": "Each contact entry MUST define name, email, and affiliation, and is recommended to include an ORCID identifier",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "email",
    "affiliation"
  ],
  "recommended_properties": [
    "orcid"
  ]
}
```

</details>

### `$.metadata.contact[*].name`

#### `MTBLS-METADATA-CONTACT-0020` (`required` — `string-length`)

- **Description**: Contact full name MUST be at least 5 characters long
- **Summary**: type=`string-length`, min_length=5

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0020",
  "description": "Contact full name MUST be at least 5 characters long",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Contact name must have a minimum length of 5 characters",
    "minimum": 5
  }
}
```

</details>

### `$.metadata.contact[*].email`

#### `MTBLS-METADATA-CONTACT-0030` (`required` — `email`)

- **Description**: Contact email MUST be a valid email address
- **Summary**: type=`email`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0030",
  "description": "Contact email MUST be a valid email address",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "email",
    "description": "Contact email must conform to standard email address syntax"
  }
}
```

</details>

### `$.metadata.contact[*].affiliation`

#### `MTBLS-METADATA-CONTACT-0040` (`required` — `string-length`)

- **Description**: Contact affiliation MUST be at least 2 characters long
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0040",
  "description": "Contact affiliation MUST be at least 2 characters long",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Contact affiliation must have a minimum length of 2 characters",
    "minimum": 2
  }
}
```

</details>

### `$.metadata.contact[*].orcid`

#### `MTBLS-METADATA-CONTACT-0050` (`recommended` — `regex`)

- **Description**: Contact ORCID is recommended and, when provided, MUST match the standard 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX)
- **Summary**: type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-CONTACT-0050",
  "description": "Contact ORCID is recommended and, when provided, MUST match the standard 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX)",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "regex",
    "description": "ORCID identifier must match pattern ^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "pattern": "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$",
    "case_sensitive": true
  }
}
```

</details>

### `$.metadata.uri[*].value`

#### `D-MTD-CONTACT-URI-0010` (`required` — `uri`)

- **Description**: Each metadata URI entry value MUST be a valid URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-URI-0010",
  "description": "Each metadata URI entry value MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "Metadata URI value must be a valid URI or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.external_study_uri[*].value`

#### `D-MTD-CONTACT-EXTERNAL_STUDY_URI-0010` (`required` — `uri`)

- **Description**: Each external study URI entry value MUST be a valid URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-EXTERNAL_STUDY_URI-0010",
  "description": "Each external study URI entry value MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "External study URI value must be a valid URI or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.quantification_method`

#### `D-MTD-CONTACT-QUANTIFICATION_METHOD-0001` (`required` — `cv-term`)

- **Description**: Metadata quantification_method MUST be present and defined as a valid ontology CV term or user-defined parameter
- **Summary**: match_is_required=True; type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-QUANTIFICATION_METHOD-0001",
  "description": "Metadata quantification_method MUST be present and defined as a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Quantification method must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.sample`

#### `MTBLS-METADATA-SAMPLE-0001` (`required` — `items-count`)

- **Description**: At least one biological or experimental sample MUST be defined in metadata for MetaboLights submissions
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-SAMPLE-0001",
  "description": "At least one biological or experimental sample MUST be defined in metadata for MetaboLights submissions",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 sample entry in metadata.sample",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.sample[*]`

#### `MTBLS-METADATA-SAMPLE-0010` (`recommended` — `property-check`)

- **Description**: Each sample entry MUST define name and species, and is recommended to include tissue and cell_type
- **Summary**: required_properties=[`name`, `species`]; recommended_properties=[`tissue`, `cell_type`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-SAMPLE-0010",
  "description": "Each sample entry MUST define name and species, and is recommended to include tissue and cell_type",
  "enforcement_level": "recommended",
  "required_properties": [
    "name",
    "species"
  ],
  "recommended_properties": [
    "tissue",
    "cell_type"
  ]
}
```

</details>

### `$.metadata.sample[*].name`

#### `MTBLS-METADATA-SAMPLE-0020` (`required` — `string-length`)

- **Description**: Sample name MUST be at least 2 characters long
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-SAMPLE-0020",
  "description": "Sample name MUST be at least 2 characters long",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Sample name must have a minimum length of 2 characters",
    "minimum": 2
  }
}
```

</details>

### `$.metadata.sample[*].species[*]`

#### `MTBLS-METADATA-SAMPLE-0031` (`required` — `allowed-cv-list`)

- **Description**: Sample species CV terms MUST be sourced from NCBITAXON, ENVO, or CHEBI ontologies (user-defined terms are not permitted)
- **Summary**: type=`allowed-cv-list`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-SAMPLE-0031",
  "description": "Sample species CV terms MUST be sourced from NCBITAXON, ENVO, or CHEBI ontologies (user-defined terms are not permitted)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "allowed-cv-list",
    "description": "Species ontology term must belong to NCBITAXON, ENVO, or CHEBI",
    "allow_user_defined_terms": false,
    "allowed_cv_list": [
      "NCBITAXON",
      "ENVO",
      "CHEBI"
    ]
  }
}
```

</details>

### `$.metadata.ms_run`

#### `MTBLS-METADATA-MS_RUN-0001` (`required` — `items-count`)

- **Description**: At least one MS run MUST be defined in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-MS_RUN-0001",
  "description": "At least one MS run MUST be defined in metadata",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 MS run entry in metadata.ms_run",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.ms_run[*]`

#### `MTBLS-METADATA-MS_RUN-0010` (`required` — `property-check`)

- **Description**: Each MS run entry MUST define its data file location
- **Summary**: required_properties=[`location`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-MS_RUN-0010",
  "description": "Each MS run entry MUST define its data file location",
  "enforcement_level": "required",
  "required_properties": [
    "location"
  ]
}
```

</details>

### `$.metadata.ms_run[*].location`

#### `D-MTD-MS_RUN-0020` (`required` — `uri`)

- **Description**: Each MS run location MUST be a valid non-null URI pointing to the raw or processed mass spectrometry data file
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0020",
  "description": "Each MS run location MUST be a valid non-null URI pointing to the raw or processed mass spectrometry data file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "MS run location must be a valid non-null URI",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.metadata.ms_run[*].scan_polarity`

#### `D-MTD-MS_RUN-0030` (`required` — `items-count`)

- **Description**: Each MS run scan_polarity list may contain at most 2 polarity entries (positive and/or negative scan)
- **Summary**: type=`items-count`, max_occurs=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0030",
  "description": "Each MS run scan_polarity list may contain at most 2 polarity entries (positive and/or negative scan)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Allows at most 2 entries in ms_run.scan_polarity",
    "max_occurs": 2
  }
}
```

</details>

### `$.metadata.ms_run[*].scan_polarity[*]`

#### `D-MTD-MS_RUN-0031` (`required` — `cv-term-enum`)

- **Description**: Each MS run scan_polarity entry MUST be either 'negative scan' (MS:1000129) or 'positive scan' (MS:1000130)
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000129`, name=`negative scan`}, {cv_label=`MS`, cv_accession=`MS:1000130`, name=`positive scan`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0031",
  "description": "Each MS run scan_polarity entry MUST be either 'negative scan' (MS:1000129) or 'positive scan' (MS:1000130)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "description": "Scan polarity CV term must be MS:1000129 (negative scan) or MS:1000130 (positive scan)",
    "allowed_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "MS:1000129",
        "name": "negative scan"
      },
      {
        "cv_label": "MS",
        "cv_accession": "MS:1000130",
        "name": "positive scan"
      }
    ]
  }
}
```

</details>

### `$.metadata.assay`

#### `MTBLS-METADATA-ASSAY-0001` (`required` — `items-count`)

- **Description**: At least one assay MUST be defined in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-ASSAY-0001",
  "description": "At least one assay MUST be defined in metadata",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 assay entry in metadata.assay",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.assay[*]`

#### `MTBLS-METADATA-ASSAY-0010` (`required` — `property-check`)

- **Description**: Each assay entry MUST define name, ms_run_ref, and sample_ref for MetaboLights submissions
- **Summary**: required_properties=[`name`, `ms_run_ref`, `sample_ref`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-ASSAY-0010",
  "description": "Each assay entry MUST define name, ms_run_ref, and sample_ref for MetaboLights submissions",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "ms_run_ref",
    "sample_ref"
  ]
}
```

</details>

### `$.metadata.assay[*].name`

#### `D-MTD-ASSAY-0030` (`required` — `string-length`)

- **Description**: Assay name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0030",
  "description": "Assay name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Assay name must have a minimum length of 1 character",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.assay[*].ms_run_ref`

#### `D-MTD-ASSAY-0040` (`required` — `items-count`)

- **Description**: Each assay ms_run_ref list MUST contain at least one MS run reference
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0040",
  "description": "Each assay ms_run_ref list MUST contain at least one MS run reference",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 MS run reference in assay.ms_run_ref",
    "min_occurs": 1
  }
}
```

</details>

### `$.metadata.assay[*].ms_run_ref[*]`

#### `D-MTD-ASSAY-0041` (`required` — `positive-integer`)

- **Description**: Each MS run reference in assay.ms_run_ref MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0041",
  "description": "Each MS run reference in assay.ms_run_ref MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Assay ms_run_ref item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.assay[*].protocol_refs[*]`

#### `D-MTD-ASSAY-0050` (`required` — `positive-integer`)

- **Description**: Each protocol reference in assay.protocol_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0050",
  "description": "Each protocol reference in assay.protocol_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Assay protocol_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.assay[*].sample_ref`

#### `MTBLS-METADATA-ASSAY-0020` (`required` — `positive-integer`)

- **Description**: Each assay sample_ref MUST be a non-null positive integer (>= 1) referencing a declared sample
- **Summary**: type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-ASSAY-0020",
  "description": "Each assay sample_ref MUST be a non-null positive integer (>= 1) referencing a declared sample",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Assay sample_ref must be a positive integer (>= 1) and cannot be null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

### `$.metadata.assay[*].external_uri`

#### `D-MTD-ASSAY-0070` (`required` — `uri`)

- **Description**: Assay external_uri, when provided, MUST be a valid URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0070",
  "description": "Assay external_uri, when provided, MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "Assay external_uri must be a valid URI or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.protocol[*]`

#### `MTBLS-METADATA-PROTOCOL-0010` (`required` — `property-check`)

- **Description**: Each protocol entry MUST define both name and type
- **Summary**: required_properties=[`name`, `type`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-PROTOCOL-0010",
  "description": "Each protocol entry MUST define both name and type",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "type"
  ]
}
```

</details>

### `$.metadata.protocol[*].name`

#### `MTBLS-METADATA-PROTOCOL-0020` (`required` — `string-length`)

- **Description**: Protocol name MUST be a valid string
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-PROTOCOL-0020",
  "description": "Protocol name MUST be a valid string",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Validates that protocol name is a string value"
  }
}
```

</details>

### `$.metadata.protocol[*].type`

- **Requirement Group** (1 requirement(s))
- **Group Description**: Validation rules for protocol type CV term definitions

#### `MTBLS-METADATA-PROTOCOL-0030` (`required` — `cv-term`)

- **Description**: Protocol type MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-PROTOCOL-0030",
  "description": "Protocol type MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Protocol type must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.study_variable_group[*]`

#### `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0010` (`required` — `property-check`)

- **Description**: Each study variable group MUST define name and type, and is recommended to define datatype
- **Summary**: required_properties=[`name`, `type`]; recommended_properties=[`datatype`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE_GROUP-0010",
  "description": "Each study variable group MUST define name and type, and is recommended to define datatype",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "type"
  ],
  "recommended_properties": [
    "datatype"
  ]
}
```

</details>

### `$.metadata.study_variable_group[*].name`

#### `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0020` (`required` — `group`)

- **Description**: Study variable group name MUST be either a valid CV term or a string of at least 2 characters
- **Summary**: type=`group`, join_operator=`or`, constraints=[type=`cv-term`, allow_user_defined_terms=True; type=`string-length`, min_length=2]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE_GROUP-0020",
  "description": "Study variable group name MUST be either a valid CV term or a string of at least 2 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "description": "Study variable group name must satisfy either the CV term constraint or minimum string length of 2",
    "constraints": [
      {
        "type": "cv-term",
        "description": "Validates study variable group name as a CV term (user-defined terms allowed)",
        "allow_user_defined_terms": true
      },
      {
        "type": "string-length",
        "description": "Validates study variable group name as a string of at least 2 characters",
        "minimum": 2
      }
    ],
    "join_operator": "or"
  }
}
```

</details>

### `$.metadata.study_variable_group[*].type`

#### `D-MTD-STUDY_VARIABLE_GROUP-0030` (`required` — `cv-term`)

- **Description**: Study variable group type MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE_GROUP-0030",
  "description": "Study variable group type MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Study variable group type must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.study_variable_group[*].datatype`

#### `D-MTD-STUDY_VARIABLE_GROUP-0040` (`required` — `string-enum`)

- **Description**: Study variable group datatype MUST be one of the allowed XML Schema datatypes (xsd:string, xsd:integer, xsd:decimal, xsd:boolean, xsd:date, xsd:time, xsd:dateTime, xsd:anyURI) or 'Parameter'
- **Summary**: type=`string-enum`, options=[`xsd:string`, `xsd:integer`, `xsd:decimal`, `xsd:boolean`, `xsd:date`, ... (+4 more)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE_GROUP-0040",
  "description": "Study variable group datatype MUST be one of the allowed XML Schema datatypes (xsd:string, xsd:integer, xsd:decimal, xsd:boolean, xsd:date, xsd:time, xsd:dateTime, xsd:anyURI) or 'Parameter'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-enum",
    "description": "Study variable group datatype must be an allowed XSD type or 'Parameter'",
    "options": [
      "xsd:string",
      "xsd:integer",
      "xsd:decimal",
      "xsd:boolean",
      "xsd:date",
      "xsd:time",
      "xsd:dateTime",
      "xsd:anyURI",
      "Parameter"
    ]
  }
}
```

</details>

### `$.metadata.study_variable`

#### `D-MTD-STUDY_VARIABLE-0001` (`required` — `items-count`)

- **Description**: At least one study variable MUST be defined in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0001",
  "description": "At least one study variable MUST be defined in metadata",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 study variable entry in metadata.study_variable",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.study_variable[*]`

#### `MTBLS-METADATA-STUDY_VARIABLE-0010` (`required` — `property-check`)

- **Description**: Each study variable MUST define name, group_refs, and assay_refs
- **Summary**: required_properties=[`name`, `group_refs`, `assay_refs`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE-0010",
  "description": "Each study variable MUST define name, group_refs, and assay_refs",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "group_refs",
    "assay_refs"
  ]
}
```

</details>

### `$.metadata.study_variable[*].name`

#### `D-MTD-STUDY_VARIABLE-0030` (`required` — `group`)

- **Description**: Study variable name MUST be a valid CV term, non-empty string, integer, decimal, or boolean value
- **Summary**: type=`group`, join_operator=`or`, constraints=[type=`cv-term`, allow_user_defined_terms=True; type=`string-length`, min_length=1; type=`integer`; type=`decimal`; type=`boolean`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0030",
  "description": "Study variable name MUST be a valid CV term, non-empty string, integer, decimal, or boolean value",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "group",
    "description": "Study variable name must satisfy at least one of: CV term, non-empty string, integer, decimal, or boolean",
    "constraints": [
      {
        "type": "cv-term",
        "description": "Validates study variable name as a CV term (user-defined terms allowed)",
        "allow_user_defined_terms": true
      },
      {
        "type": "string-length",
        "description": "Validates study variable name as a non-empty string (minimum 1 character)",
        "minimum": 1
      },
      {
        "type": "integer",
        "description": "Validates study variable name as an integer value"
      },
      {
        "type": "decimal",
        "description": "Validates study variable name as a decimal value"
      },
      {
        "type": "boolean",
        "description": "Validates study variable name as a boolean value"
      }
    ],
    "join_operator": "or"
  }
}
```

</details>

### `$.metadata.study_variable[*].group_refs[*]`

#### `D-MTD-STUDY_VARIABLE-0041` (`required` — `positive-integer`)

- **Description**: Each group reference in study_variable.group_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0041",
  "description": "Each group reference in study_variable.group_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Study variable group_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.study_variable[*].assay_refs[*]`

#### `D-MTD-STUDY_VARIABLE-0051` (`required` — `positive-integer`)

- **Description**: Each assay reference in study_variable.assay_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0051",
  "description": "Each assay reference in study_variable.assay_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Study variable assay_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.study_variable[*].ms_run_refs[*]`

#### `D-MTD-STUDY_VARIABLE-0061` (`required` — `positive-integer`)

- **Description**: Each MS run reference in study_variable.ms_run_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0061",
  "description": "Each MS run reference in study_variable.ms_run_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Study variable ms_run_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.custom[*]`

#### `D-MTD-CUSTOM-0010` (`required` — `cv-term`)

- **Description**: Each custom metadata parameter MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CUSTOM-0010",
  "description": "Each custom metadata parameter MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Custom metadata parameter must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.cv`

#### `D-MTD-CV-0001` (`required` — `items-count`)

- **Description**: At least one controlled vocabulary (CV) source MUST be declared in metadata.cv
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0001",
  "description": "At least one controlled vocabulary (CV) source MUST be declared in metadata.cv",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 controlled vocabulary entry in metadata.cv",
    "min_occurs": 1
  }
}
```

</details>

### `$.metadata.cv[*]`

#### `D-MTD-CV-0010` (`required` — `property-check`)

- **Description**: Each controlled vocabulary entry MUST define label, full_name, version, and uri
- **Summary**: required_properties=[`label`, `full_name`, `version`, `uri`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0010",
  "description": "Each controlled vocabulary entry MUST define label, full_name, version, and uri",
  "enforcement_level": "required",
  "required_properties": [
    "label",
    "full_name",
    "version",
    "uri"
  ]
}
```

</details>

### `$.metadata.cv[*].label`

#### `D-MTD-CV-0020` (`required` — `string-length`)

- **Description**: Controlled vocabulary label MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0020",
  "description": "Controlled vocabulary label MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "CV label must be a non-null string of at least 1 character",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

### `$.metadata.cv[*].full_name`

#### `D-MTD-CV-0030` (`required` — `string-length`)

- **Description**: Controlled vocabulary full_name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0030",
  "description": "Controlled vocabulary full_name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "CV full_name must be a non-null string of at least 1 character",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

### `$.metadata.cv[*].version`

#### `D-MTD-CV-0040` (`required` — `string-length`)

- **Description**: Controlled vocabulary version MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0040",
  "description": "Controlled vocabulary version MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "CV version must be a non-null string of at least 1 character",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

### `$.metadata.cv[*].uri`

#### `D-MTD-CV-0050` (`required` — `uri`)

- **Description**: Controlled vocabulary uri MUST be a valid URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CV-0050",
  "description": "Controlled vocabulary uri MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "CV uri must be a valid URI or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.small_molecule-quantification_unit`

#### `D-MTD-SML_QUANTIFICATION_UNIT-0001` (`required` — `cv-term`)

- **Description**: Metadata small_molecule-quantification_unit MUST be present and defined as a valid ontology CV term or user-defined parameter
- **Summary**: match_is_required=True; type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SML_QUANTIFICATION_UNIT-0001",
  "description": "Metadata small_molecule-quantification_unit MUST be present and defined as a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Small molecule quantification unit must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.small_molecule_feature-quantification_unit`

#### `D-MTD-SMF_QUANTIFICATION_UNIT-0001` (`required` — `cv-term`)

- **Description**: Metadata small_molecule_feature-quantification_unit, when present, MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SMF_QUANTIFICATION_UNIT-0001",
  "description": "Metadata small_molecule_feature-quantification_unit, when present, MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Small molecule feature quantification unit must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.small_molecule-identification_reliability`

#### `D-MTD-SML_IDENTIFICATION_RELIABILITY-0001` (`required` — `cv-term`)

- **Description**: Metadata small_molecule-identification_reliability, when present, MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SML_IDENTIFICATION_RELIABILITY-0001",
  "description": "Metadata small_molecule-identification_reliability, when present, MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Small molecule identification reliability must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.database`

#### `D-MTD-DATABASE-0001` (`required` — `items-count`)

- **Description**: At least one chemical or metabolite database MUST be declared in metadata.database
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-DATABASE-0001",
  "description": "At least one chemical or metabolite database MUST be declared in metadata.database",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 database entry in metadata.database",
    "min_occurs": 1
  }
}
```

</details>

### `$.metadata.database[*]`

- **Requirement Group** (4 requirement(s))
- **Group Description**: Validation rules for reference database declarations in metadata

#### `D-MTD-DATABASE-0010` (`required` — `property-check`)

- **Description**: Each database entry MUST define param, prefix, version, and uri
- **Summary**: required_properties=[`param`, `prefix`, `version`, `uri`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-DATABASE-0010",
  "description": "Each database entry MUST define param, prefix, version, and uri",
  "enforcement_level": "required",
  "required_properties": [
    "param",
    "prefix",
    "version",
    "uri"
  ]
}
```

</details>

#### `D-MTD-DATABASE-0020` (`required` — `cv-term`)

- **Description**: Database param MUST be a valid ontology CV term or user-defined parameter identifying the database
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-DATABASE-0020",
  "description": "Database param MUST be a valid ontology CV term or user-defined parameter identifying the database",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Database param must be a valid CV term (user-defined terms allowed)",
    "json_path": "$.param",
    "allow_user_defined_terms": true
  }
}
```

</details>

#### `D-MTD-DATABASE-0030` (`required` — `string-length`)

- **Description**: Database version MUST be a non-empty string (minimum 1 character) or 'Unknown'
- **Summary**: type=`string-length`, json_path=`$.version`, min_length=1, exceptional_values=['Unknown']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-DATABASE-0030",
  "description": "Database version MUST be a non-empty string (minimum 1 character) or 'Unknown'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Database version must have a minimum length of 1 character (or be 'Unknown')",
    "json_path": "$.version",
    "exceptional_values": [
      "Unknown"
    ],
    "minimum": 1
  }
}
```

</details>

#### `D-MTD-DATABASE-0040` (`required` — `uri`)

- **Description**: Database uri, when provided, MUST be a valid URI
- **Summary**: type=`uri`, json_path=`$.uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-DATABASE-0040",
  "description": "Database uri, when provided, MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "Database uri must be a valid URI or null",
    "json_path": "$.uri",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.metadata.id_confidence_measure`

#### `D-MTD-ID_CONFIDENCE_MEASURE-0001` (`required` — `items-count`)

- **Description**: At least one identification confidence measure MUST be declared in metadata.id_confidence_measure
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ID_CONFIDENCE_MEASURE-0001",
  "description": "At least one identification confidence measure MUST be declared in metadata.id_confidence_measure",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 entry in metadata.id_confidence_measure",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.id_confidence_measure[*]`

#### `D-MTD-ID_CONFIDENCE_MEASURE-0010` (`required` — `cv-term`)

- **Description**: Each identification confidence measure MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ID_CONFIDENCE_MEASURE-0010",
  "description": "Each identification confidence measure MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Identification confidence measure must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.colunit-small_molecule[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for smallMoleculeSummary (SML) column unit definitions

#### `D-MTD-COLUNIT_SML-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeSummary column unit entry MUST define column_name and param
- **Summary**: required_properties=[`column_name`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SML-0010",
  "description": "Each smallMoleculeSummary column unit entry MUST define column_name and param",
  "enforcement_level": "required",
  "required_properties": [
    "column_name",
    "param"
  ]
}
```

</details>

#### `D-MTD-COLUNIT_SML-0020` (`required` — `string-length`)

- **Description**: SmallMoleculeSummary column unit column_name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SML-0020",
  "description": "SmallMoleculeSummary column unit column_name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SML column unit column_name must be a non-null string of at least 1 character",
    "json_path": "$.column_name",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

#### `D-MTD-COLUNIT_SML-0030` (`required` — `cv-term`)

- **Description**: SmallMoleculeSummary column unit param MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SML-0030",
  "description": "SmallMoleculeSummary column unit param MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SML column unit param must be a valid CV term (user-defined terms allowed)",
    "json_path": "$.param",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.colunit-small_molecule_feature[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for smallMoleculeFeature (SMF) column unit definitions

#### `D-MTD-COLUNIT_SMF-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeFeature column unit entry MUST define column_name and param
- **Summary**: required_properties=[`column_name`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SMF-0010",
  "description": "Each smallMoleculeFeature column unit entry MUST define column_name and param",
  "enforcement_level": "required",
  "required_properties": [
    "column_name",
    "param"
  ]
}
```

</details>

#### `D-MTD-COLUNIT_SMF-0020` (`required` — `string-length`)

- **Description**: SmallMoleculeFeature column unit column_name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SMF-0020",
  "description": "SmallMoleculeFeature column unit column_name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SMF column unit column_name must be a non-null string of at least 1 character",
    "json_path": "$.column_name",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

#### `D-MTD-COLUNIT_SMF-0030` (`required` — `cv-term`)

- **Description**: SmallMoleculeFeature column unit param MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SMF-0030",
  "description": "SmallMoleculeFeature column unit param MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SMF column unit param must be a valid CV term (user-defined terms allowed)",
    "json_path": "$.param",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.colunit-small_molecule_evidence[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for smallMoleculeEvidence (SME) column unit definitions

#### `D-MTD-COLUNIT_SME-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeEvidence column unit entry MUST define column_name and param
- **Summary**: required_properties=[`column_name`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SME-0010",
  "description": "Each smallMoleculeEvidence column unit entry MUST define column_name and param",
  "enforcement_level": "required",
  "required_properties": [
    "column_name",
    "param"
  ]
}
```

</details>

#### `D-MTD-COLUNIT_SME-0020` (`required` — `string-length`)

- **Description**: SmallMoleculeEvidence column unit column_name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, json_path=`$.column_name`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SME-0020",
  "description": "SmallMoleculeEvidence column unit column_name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME column unit column_name must be a non-null string of at least 1 character",
    "json_path": "$.column_name",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "minimum": 1
  }
}
```

</details>

#### `D-MTD-COLUNIT_SME-0030` (`required` — `cv-term`)

- **Description**: SmallMoleculeEvidence column unit param MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-COLUNIT_SME-0030",
  "description": "SmallMoleculeEvidence column unit param MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SME column unit param must be a valid CV term (user-defined terms allowed)",
    "json_path": "$.param",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.smallMoleculeSummary`

#### `MTBLS-SML-0001` (`required` — `items-count`)

- **Description**: The smallMoleculeSummary (SML) table MUST be present and contain at least one summary row
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-SML-0001",
  "description": "The smallMoleculeSummary (SML) table MUST be present and contain at least one summary row",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 entry in smallMoleculeSummary",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.smallMoleculeSummary[*]`

#### `D-SML-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeSummary (SML) row MUST define all mandatory SML properties (prefix, header_prefix, sml_id, smf_id_refs, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, theoretical_neutral_mass, adduct_ions, reliability, best_id_confidence_measure, best_id_confidence_value)
- **Summary**: required_properties=[`prefix`, `header_prefix`, `sml_id`, `smf_id_refs`, `database_identifier`, `chemical_formula`, `smiles`, `inchi`, `chemical_name`, `uri`, `theoretical_neutral_mass`, `adduct_ions`, `reliability`, `best_id_confidence_measure`, `best_id_confidence_value`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-0010",
  "description": "Each smallMoleculeSummary (SML) row MUST define all mandatory SML properties (prefix, header_prefix, sml_id, smf_id_refs, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, theoretical_neutral_mass, adduct_ions, reliability, best_id_confidence_measure, best_id_confidence_value)",
  "enforcement_level": "required",
  "required_properties": [
    "prefix",
    "header_prefix",
    "sml_id",
    "smf_id_refs",
    "database_identifier",
    "chemical_formula",
    "smiles",
    "inchi",
    "chemical_name",
    "uri",
    "theoretical_neutral_mass",
    "adduct_ions",
    "reliability",
    "best_id_confidence_measure",
    "best_id_confidence_value"
  ]
}
```

</details>

### `$.smallMoleculeSummary[*].prefix`

#### `D-SML-PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeSummary row prefix MUST equal 'SML'
- **Summary**: type=`regex`, pattern=`^SML$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-PREFIX-0001",
  "description": "Each smallMoleculeSummary row prefix MUST equal 'SML'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SML row prefix must match the literal string 'SML'",
    "pattern": "^SML$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeSummary[*].header_prefix`

#### `D-SML-HEADER_PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeSummary row header_prefix MUST equal 'SMH'
- **Summary**: type=`regex`, pattern=`^SMH$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-HEADER_PREFIX-0001",
  "description": "Each smallMoleculeSummary row header_prefix MUST equal 'SMH'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SML header_prefix must match the literal string 'SMH'",
    "pattern": "^SMH$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeSummary[*].sml_id`

#### `D-SML-SML_ID-0001` (`required` — `positive-integer`)

- **Description**: Each smallMoleculeSummary sml_id MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-SML_ID-0001",
  "description": "Each smallMoleculeSummary sml_id MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SML sml_id must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeSummary[*].smf_id_refs[*]`

#### `D-SML-SMF_ID_REFS-0010` (`required` — `positive-integer`)

- **Description**: Each feature reference in smallMoleculeSummary.smf_id_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-SMF_ID_REFS-0010",
  "description": "Each feature reference in smallMoleculeSummary.smf_id_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SML smf_id_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeSummary[*].database_identifier[*]`

#### `D-SML-DATABASE_IDENTIFIER-0010` (`required` — `not-null`)

- **Description**: Each database_identifier entry in smallMoleculeSummary MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-DATABASE_IDENTIFIER-0010",
  "description": "Each database_identifier entry in smallMoleculeSummary MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SML database_identifier item must not be null or empty",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].chemical_formula[*]`

#### `D-SML-CHEMICAL_FORMULA-0010` (`required` — `not-null`)

- **Description**: Each chemical_formula entry in smallMoleculeSummary MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-CHEMICAL_FORMULA-0010",
  "description": "Each chemical_formula entry in smallMoleculeSummary MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SML chemical_formula item must not be null or empty",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].smiles[*]`

#### `D-SML-SMILES-0010` (`required` — `not-null`)

- **Description**: Each SMILES entry in smallMoleculeSummary MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-SMILES-0010",
  "description": "Each SMILES entry in smallMoleculeSummary MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SML smiles item must not be null or empty",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].inchi[*]`

#### `D-SML-INCHI-0010` (`required` — `not-null`)

- **Description**: Each InChI entry in smallMoleculeSummary MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-INCHI-0010",
  "description": "Each InChI entry in smallMoleculeSummary MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SML inchi item must not be null or empty",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].chemical_name[*]`

#### `D-SML-CHEMICAL_NAME-0010` (`required` — `not-null`)

- **Description**: Each chemical_name entry in smallMoleculeSummary MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-CHEMICAL_NAME-0010",
  "description": "Each chemical_name entry in smallMoleculeSummary MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SML chemical_name item must not be null or empty",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].uri[*]`

#### `D-SML-URI-0010` (`required` — `uri`)

- **Description**: Each URI entry in smallMoleculeSummary.uri MUST be a valid non-null URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-URI-0010",
  "description": "Each URI entry in smallMoleculeSummary.uri MUST be a valid non-null URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "SML uri item must be a valid non-null URI",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].theoretical_neutral_mass[*]`

#### `D-SML-THEORETICAL_NEUTRAL_MASS-0010` (`required` — `decimal`)

- **Description**: Each theoretical_neutral_mass entry in smallMoleculeSummary MUST be a valid decimal number or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-THEORETICAL_NEUTRAL_MASS-0010",
  "description": "Each theoretical_neutral_mass entry in smallMoleculeSummary MUST be a valid decimal number or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SML theoretical_neutral_mass item must be a decimal number or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].adduct_ions[*]`

#### `D-SML-ADDUCT_IONS-0010` (`required` — `regex`)

- **Description**: Each adduct ion in smallMoleculeSummary.adduct_ions MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'
- **Summary**: type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-ADDUCT_IONS-0010",
  "description": "Each adduct ion in smallMoleculeSummary.adduct_ions MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SML adduct ion must match pattern ^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$",
    "pattern": "^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeSummary[*].best_id_confidence_measure`

#### `D-SML-BEST_ID_CONFIDENCE_MEASURE-0001` (`required` — `cv-term`)

- **Description**: SmallMoleculeSummary best_id_confidence_measure MUST be a valid ontology CV term or user-defined parameter (or null)
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-BEST_ID_CONFIDENCE_MEASURE-0001",
  "description": "SmallMoleculeSummary best_id_confidence_measure MUST be a valid ontology CV term or user-defined parameter (or null)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SML best_id_confidence_measure must be a valid CV term or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ],
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.smallMoleculeSummary[*].best_id_confidence_value`

#### `D-SML-BEST_ID_CONFIDENCE_VALUE-0001` (`recommended` — `decimal`)

- **Description**: SmallMoleculeSummary best_id_confidence_value is recommended to be a non-null decimal value
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-BEST_ID_CONFIDENCE_VALUE-0001",
  "description": "SmallMoleculeSummary best_id_confidence_value is recommended to be a non-null decimal value",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "decimal",
    "description": "SML best_id_confidence_value should be a non-null decimal number",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].abundance_assay[*]`

#### `D-SML-ABUNDANCE_ASSAY-0010` (`required` — `decimal`)

- **Description**: Each assay abundance value in smallMoleculeSummary.abundance_assay MUST be a decimal number or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-ABUNDANCE_ASSAY-0010",
  "description": "Each assay abundance value in smallMoleculeSummary.abundance_assay MUST be a decimal number or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SML abundance_assay item must be a decimal number or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].abundance_study_variable[*]`

#### `D-SML-ABUNDANCE_STUDY_VARIABLE-0010` (`required` — `decimal`)

- **Description**: Each study variable abundance value in smallMoleculeSummary.abundance_study_variable MUST be a decimal number or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-ABUNDANCE_STUDY_VARIABLE-0010",
  "description": "Each study variable abundance value in smallMoleculeSummary.abundance_study_variable MUST be a decimal number or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SML abundance_study_variable item must be a decimal number or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].abundance_variation_study_variable[*]`

#### `D-SML-ABUNDANCE_VARIATION_STUDY_VARIABLE-0010` (`required` — `decimal`)

- **Description**: Each study variable abundance variation value in smallMoleculeSummary.abundance_variation_study_variable MUST be a decimal number or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-ABUNDANCE_VARIATION_STUDY_VARIABLE-0010",
  "description": "Each study variable abundance variation value in smallMoleculeSummary.abundance_variation_study_variable MUST be a decimal number or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SML abundance_variation_study_variable item must be a decimal number or null",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "exceptional_values": [
      null
    ]
  }
}
```

</details>

### `$.smallMoleculeSummary[*].opt[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for optional custom columns (opt_) in smallMoleculeSummary (SML) rows

#### `D-SML-OPT-0010` (`required` — `property-check`)

- **Description**: Each optional column entry in smallMoleculeSummary.opt is recommended to define identifier and param
- **Summary**: recommended_properties=[`identifier`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-OPT-0010",
  "description": "Each optional column entry in smallMoleculeSummary.opt is recommended to define identifier and param",
  "enforcement_level": "required",
  "recommended_properties": [
    "identifier",
    "param"
  ]
}
```

</details>

#### `D-SML-OPT-0020` (`required` — `regex`)

- **Description**: Optional column identifier in smallMoleculeSummary.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'
- **Summary**: type=`regex`, json_path=`$.identifier`, pattern=`^global|ms_run\[\d+\]|assay\[\d+\]|study_variable\[\d+\]$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-OPT-0020",
  "description": "Optional column identifier in smallMoleculeSummary.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SML opt identifier must match ^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "json_path": "$.identifier",
    "pattern": "^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "case_sensitive": true
  }
}
```

</details>

#### `D-SML-OPT-0030` (`required` — `cv-term`)

- **Description**: Optional column param in smallMoleculeSummary.opt MUST be a valid non-null ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-OPT-0030",
  "description": "Optional column param in smallMoleculeSummary.opt MUST be a valid non-null ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SML opt param must be a valid non-null CV term (user-defined terms allowed)",
    "json_path": "$.param",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.metadata.sample[*].species`

#### `MTBLS-METADATA-SAMPLE-0030` (`required` — `items-count`)

- **Description**: Each sample MUST specify at least one species ontology term
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-SAMPLE-0030",
  "description": "Each sample MUST specify at least one species ontology term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 species CV term in sample.species",
    "min_occurs": 1
  }
}
```

</details>

### `$.metadata.protocol`

#### `MTBLS-METADATA-PROTOCOL-0001` (`required` — `items-count`)

- **Description**: Metadata protocols MUST include all 6 required MetaboLights protocol types (EFO:0005518, EFO:0000490, CHMO:0000470, CHMO:0001000, OBI:0200000, MI:2131)
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1, min_match=6, item_paths=['$[*].type.cv_accession']

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-PROTOCOL-0001",
  "description": "Metadata protocols MUST include all 6 required MetaboLights protocol types (EFO:0005518, EFO:0000490, CHMO:0000470, CHMO:0001000, OBI:0200000, MI:2131)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 protocol and all 6 standard MetaboLights protocol CV accessions to be present",
    "min_occurs": 1,
    "item_value_jsonpath_list": [
      "$[*].type.cv_accession"
    ],
    "match_reference_values": [
      "EFO:0005518",
      "EFO:0000490",
      "CHMO:0000470",
      "CHMO:0001000",
      "OBI:0200000",
      "MI:2131"
    ],
    "min_match": 6,
    "min_referenced_value_match": 6
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.study_variable_group`

#### `MTBLS-METADATA-STUDY_VARIABLE_GROUP-0001` (`required` — `items-count`)

- **Description**: At least one study variable group MUST be defined in metadata for MetaboLights submissions
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE_GROUP-0001",
  "description": "At least one study variable group MUST be defined in metadata for MetaboLights submissions",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 study variable group entry in metadata.study_variable_group",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.study_variable[*].group_refs`

#### `MTBLS-METADATA-STUDY_VARIABLE-0020` (`required` — `items-count`)

- **Description**: Each study variable MUST reference at least one study variable group in group_refs
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE-0020",
  "description": "Each study variable MUST reference at least one study variable group in group_refs",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 group reference in study_variable.group_refs",
    "min_occurs": 1
  }
}
```

</details>

### `$.metadata.study_variable[*].assay_refs`

#### `MTBLS-METADATA-STUDY_VARIABLE-0030` (`required` — `items-count`)

- **Description**: Each study variable MUST reference at least one assay in assay_refs
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "MTBLS-METADATA-STUDY_VARIABLE-0030",
  "description": "Each study variable MUST reference at least one assay in assay_refs",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 assay reference in study_variable.assay_refs",
    "min_occurs": 1
  }
}
```

</details>
