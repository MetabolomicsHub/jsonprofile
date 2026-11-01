# mzTab-M 2.1.0-M Default Profile

Baseline HUPO-PSI mzTab-M 2.1.0-M Default Profile defining minimum structural, metadata, controlled vocabulary, and cross-section OPA integrity requirements for valid mzTab-M documents

## General Information

| Field | Value |
| :--- | :--- |
| **ID** | `https://github.com/HUPO-PSI/mzTab-M/tree/main/schema/profiles/mztabm-default-profile-2.1.0-M.json` |
| **Name** | mzTab-M 2.1.0-M Default Profile |
| **Version** | `2.1.0-M` |
| **Extends** | - |
| **Description** | Baseline HUPO-PSI mzTab-M 2.1.0-M Default Profile defining minimum structural, metadata, controlled vocabulary, and cross-section OPA integrity requirements for valid mzTab-M documents |
| **JSONPath Targets** | 134 |
| **Total Requirements** | 157 (`required`: 140, `recommended`: 17, `optional`: 0) |

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
| `$` | `D-0001` | `required` | `property-check` | Root mzTab-M document MUST contain the metadata section and is recommended to include smallMoleculeSummary | required_properties=[`metadata`]; recommended_properties=[`smallMoleculeSummary`] |
| `$.comment[*]` | `D-COMMENT-0001` | `required` | `regex` | Each comment entry prefix MUST equal 'COM' | type=`regex`, json_path=`$.prefix`, pattern=`^COM$` |
| `$.comment[*].message` | `D-COMMENT-MESSAGE-0001` | `required` | `string-length` | Each comment entry MUST contain a non-empty message string (minimum 1 character) | type=`string-length`, min_length=1 |
| `$.comment[*].line_number` | `D-COMMENT-LINE_NUMBER-0001` | `required` | `positive-integer` | Comment line_number, when provided, MUST be a positive integer (>= 1) | type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata` | `D-METADATA-0001` | `required` | `property-check` | Metadata section MUST be present and define all mandatory mzTab-M 2.1.0-M properties (prefix, mzTab-version, mzTab-ID, quantification_method, ms_run, assay, study_variable, cv, database, small_molecule-quantification_unit, id_confidence_measure), and is recommended to define contact, publication, software, and sample | match_is_required=True; required_properties=[`prefix`, `mzTab-version`, `mzTab-ID`, `quantification_method`, `ms_run`, `assay`, `study_variable`, `cv`, `database`, `small_molecule-quantification_unit`, `id_confidence_measure`]; recommended_properties=[`contact`, `publication`, `software`, `sample`] |
| `$.metadata.prefix` | `D-METADATA-PREFIX-0001` | `required` | `regex` | Metadata prefix MUST be present and equal 'MTD' | match_is_required=True; type=`regex`, pattern=`^MTD$` |
| `$.metadata.mzTab-version` | `D-METADATA-MZTAB_VERSION-0001` | `required` | `regex` | Metadata mzTab-version MUST be present and follow the version format 'X.Y.Z-M' (e.g. '2.1.0-M') | match_is_required=True; type=`regex`, pattern=`^\d{1}\.\d{1}\.\d{1}-[A-Z]{1}$` |
| `$.metadata.mzTab-ID` | `D-METADATA-MZTAB_ID-0001` | `required` | `string-length` | Metadata mzTab-ID MUST be present and be a non-empty string identifier (minimum 1 character) | match_is_required=True; type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.instrument` | `D-METADATA-INSTRUMENT-0001` | `recommended` | `items-count` | At least one MS instrument definition is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.instrument[*]` | `D-METADATA-INSTRUMENT-0010` | `recommended` | `property-check` | Each instrument entry is recommended to define the instrument name CV parameter | recommended_properties=[`name`] |
| `$.metadata.software` | `D-METADATA-SOFTWARE-0001` | `recommended` | `items-count` | At least one data processing or analysis software entry is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.software[*]` | `D-METADATA-SOFTWARE-0010` | `recommended` | `property-check` | Each software entry MUST define its software CV parameter | required_properties=[`parameter`] |
| `$.metadata.software[*].parameter` | `D-METADATA-SOFTWARE-0011` | `required` | `cv-term` | Software parameter MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.publication` | `D-MTD-PUBLICATION-0001` | `recommended` | `items-count` | At least one publication entry is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.publication[*]` | `D-MTD-PUBLICATION-0010` | `required` | `items-count` | Each publication entry MUST define publication_items with at least one citation item | required_properties=[`publication_items`]; type=`items-count`, json_path=`$.publication_items`, min_occurs=1 |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0020` | `required` | `group` | Each publication item MUST define both type ('doi', 'pubmed', or 'uri') and a non-null accession | required_properties=[`type`, `accession`]; type=`group`, join_operator=`and`, constraints=[type=`regex`, json_path=`$.type`, pattern=`^doi\|pubmed\|uri$`, null_values=[None, 'null', '', '""', "''"]; type=`not-null`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"]] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0021` | `required` | `regex` | When publication item type is 'doi', accession MUST follow valid DOI syntax ('10.<prefix>/<suffix>') | type=`regex`, json_path=`$.accession`, pattern=`^10\..+/.+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^doi$`)] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0022` | `required` | `regex` | When publication item type is 'pubmed', accession MUST be a numeric PubMed identifier | type=`regex`, json_path=`$.accession`, pattern=`^\d+$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^pubmed$`)] |
| `$.metadata.publication[*].publication_items[*]` | `D-MTD-PUBLICATION-0023` | `required` | `uri` | When publication item type is 'uri', accession MUST be a valid URI | type=`uri`, json_path=`$.accession`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None], precondition=[path=`$.type`, (type=`regex`, pattern=`^uri$`)] |
| `$.metadata.contact` | `D-MTD-CONTACT-0001` | `recommended` | `items-count` | At least one contact person is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.contact[*]` | `D-MTD-CONTACT-0010` | `recommended` | `property-check` | Each contact entry is recommended to include name, email, affiliation, and orcid | recommended_properties=[`name`, `email`, `affiliation`, `orcid`] |
| `$.metadata.contact[*].name` | `D-MTD-CONTACT-NAME-0001` | `recommended` | `string-length` | Contact name is recommended to be at least 5 characters long | type=`string-length`, min_length=5 |
| `$.metadata.contact[*].email` | `D-MTD-CONTACT-EMAIL-0001` | `required` | `email` | Contact email, when present, MUST be a valid email address | type=`email` |
| `$.metadata.contact[*].affiliation` | `D-MTD-CONTACT-AFFILIATION-0001` | `recommended` | `string-length` | Contact affiliation is recommended to be at least 2 characters long | type=`string-length`, min_length=2 |
| `$.metadata.contact[*].orcid` | `D-MTD-CONTACT-ORCID-0001` | `required` | `regex` | Contact ORCID, when provided, MUST match the 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX) | type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.uri[*].value` | `D-MTD-CONTACT-URI-0010` | `required` | `uri` | Each metadata URI entry value MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.external_study_uri[*].value` | `D-MTD-CONTACT-EXTERNAL_STUDY_URI-0010` | `required` | `uri` | Each external study URI entry value MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.quantification_method` | `D-MTD-CONTACT-QUANTIFICATION_METHOD-0001` | `required` | `cv-term` | Metadata quantification_method MUST be present and defined as a valid ontology CV term or user-defined parameter | match_is_required=True; type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.sample` | `D-MTD-SAMPLE-0001` | `recommended` | `items-count` | At least one sample entry is recommended in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.sample[*]` | `D-MTD-SAMPLE-0010` | `recommended` | `property-check` | Each sample entry MUST define name and is recommended to include species | required_properties=[`name`]; recommended_properties=[`species`] |
| `$.metadata.sample[*].name` | `D-MTD-SAMPLE-0011` | `required` | `string-length` | Sample name MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1 |
| `$.metadata.sample[*].species[*]` | `D-MTD-SAMPLE-0020` | `recommended` | `allowed-cv-list` | Sample species CV terms are recommended to be sourced from NCBITAXON, ENVO, or CHEBI ontologies | type=`allowed-cv-list`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False |
| `$.metadata.ms_run` | `D-MTD-MS_RUN-0001` | `required` | `items-count` | At least one MS run MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.ms_run[*]` | `D-MTD-MS_RUN-0010` | `required` | `property-check` | Each MS run entry MUST define location and is recommended to define instrument_ref | required_properties=[`location`]; recommended_properties=[`instrument_ref`] |
| `$.metadata.ms_run[*].location` | `D-MTD-MS_RUN-0020` | `required` | `uri` | Each MS run location MUST be a valid non-null URI pointing to the raw or processed mass spectrometry data file | type=`uri`, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.ms_run[*].scan_polarity` | `D-MTD-MS_RUN-0030` | `required` | `items-count` | Each MS run scan_polarity list may contain at most 2 polarity entries (positive and/or negative scan) | type=`items-count`, max_occurs=2 |
| `$.metadata.ms_run[*].scan_polarity[*]` | `D-MTD-MS_RUN-0031` | `required` | `cv-term-enum` | Each MS run scan_polarity entry MUST be either 'negative scan' (MS:1000129) or 'positive scan' (MS:1000130) | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000129`, name=`negative scan`}, {cv_label=`MS`, cv_accession=`MS:1000130`, name=`positive scan`}] |
| `$.metadata.ms_run[*].instrument_ref` | `D-MTD-MS_RUN-0040` | `recommended` | `positive-integer` | Each MS run instrument_ref is recommended to be a non-null positive integer (>= 1) referencing a declared instrument | type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"] |
| `$.metadata.assay` | `D-MTD-ASSAY-0001` | `required` | `items-count` | At least one assay MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.assay[*]` | `D-MTD-ASSAY-0020` | `required` | `property-check` | Each assay entry MUST define name and ms_run_ref, and is recommended to define sample_ref | required_properties=[`name`, `ms_run_ref`]; recommended_properties=[`sample_ref`] |
| `$.metadata.assay[*].name` | `D-MTD-ASSAY-0030` | `required` | `string-length` | Assay name MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1 |
| `$.metadata.assay[*].ms_run_ref` | `D-MTD-ASSAY-0040` | `required` | `items-count` | Each assay ms_run_ref list MUST contain at least one MS run reference | type=`items-count`, min_occurs=1 |
| `$.metadata.assay[*].ms_run_ref[*]` | `D-MTD-ASSAY-0041` | `required` | `positive-integer` | Each MS run reference in assay.ms_run_ref MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.assay[*].protocol_refs[*]` | `D-MTD-ASSAY-0050` | `required` | `positive-integer` | Each protocol reference in assay.protocol_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.metadata.assay[*].sample_ref` | `D-MTD-ASSAY-0060` | `required` | `positive-integer` | Assay sample_ref, when provided, MUST be a positive integer (>= 1) referencing a declared sample | type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.assay[*].external_uri` | `D-MTD-ASSAY-0070` | `required` | `uri` | Assay external_uri, when provided, MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.metadata.protocol[*]` | `D-MTD-PROTOCOL-0010` | `required` | `property-check` | Each protocol entry MUST define both name and type | required_properties=[`name`, `type`] |
| `$.metadata.protocol[*].name` | `D-MTD-PROTOCOL-0020` | `required` | `string-length` | Protocol name MUST be a valid string | type=`string-length` |
| `$.metadata.protocol[*].type` | `D-MTD-PROTOCOL-0030` | `required` | `cv-term` | Protocol type MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.study_variable_group[*]` | `D-MTD-STUDY_VARIABLE_GROUP-0010` | `required` | `property-check` | Each study variable group MUST define name and is recommended to define type and datatype | required_properties=[`name`]; recommended_properties=[`type`, `datatype`] |
| `$.metadata.study_variable_group[*].name` | `D-MTD-STUDY_VARIABLE_GROUP-0020` | `required` | `cv-term` | Study variable group name MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.study_variable_group[*].type` | `D-MTD-STUDY_VARIABLE_GROUP-0030` | `required` | `cv-term` | Study variable group type MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.metadata.study_variable_group[*].datatype` | `D-MTD-STUDY_VARIABLE_GROUP-0040` | `required` | `string-enum` | Study variable group datatype MUST be one of the allowed XML Schema datatypes (xsd:string, xsd:integer, xsd:decimal, xsd:boolean, xsd:date, xsd:time, xsd:dateTime, xsd:anyURI) or 'Parameter' | type=`string-enum`, options=[`xsd:string`, `xsd:integer`, `xsd:decimal`, `xsd:boolean`, `xsd:date`, ... (+4 more)] |
| `$.metadata.study_variable` | `D-MTD-STUDY_VARIABLE-0001` | `required` | `items-count` | At least one study variable MUST be defined in metadata | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.metadata.study_variable[*]` | `D-MTD-STUDY_VARIABLE-0020` | `required` | `property-check` | Each study variable MUST define name and is recommended to include group_refs | required_properties=[`name`]; recommended_properties=[`group_refs`] |
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
| `$.smallMoleculeSummary` | `D-SML-0001` | `recommended` | `items-count` | The smallMoleculeSummary (SML) table is recommended to be present and contain at least one summary row | match_is_required=True; type=`items-count`, min_occurs=1 |
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
| `$.smallMoleculeFeature` | `D-SMF-0001` | `recommended` | `items-count` | The smallMoleculeFeature (SMF) table is recommended to be present and contain at least one feature row | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.smallMoleculeFeature[*]` | `D-SMF-0010` | `required` | `property-check` | Each smallMoleculeFeature (SMF) row MUST define all mandatory SMF properties (prefix, header_prefix, smf_id, sme_id_refs, sme_id_ref_ambiguity_code, adduct_ion, isotopomer, exp_mass_to_charge, charge, retention_time_in_seconds, retention_time_in_seconds_start, retention_time_in_seconds_end) | required_properties=[`prefix`, `header_prefix`, `smf_id`, `sme_id_refs`, `sme_id_ref_ambiguity_code`, `adduct_ion`, `isotopomer`, `exp_mass_to_charge`, `charge`, `retention_time_in_seconds`, `retention_time_in_seconds_start`, `retention_time_in_seconds_end`] |
| `$.smallMoleculeFeature[*].prefix` | `D-SMF-PREFIX-0001` | `required` | `regex` | Each smallMoleculeFeature row prefix MUST equal 'SMF' | type=`regex`, pattern=`^SMF$` |
| `$.smallMoleculeFeature[*].header_prefix` | `D-SMF-HEADER_PREFIX-0001` | `required` | `regex` | Each smallMoleculeFeature row header_prefix MUST equal 'SFH' | type=`regex`, pattern=`^SFH$` |
| `$.smallMoleculeFeature[*].smf_id` | `D-SMF-SMF_ID-0001` | `required` | `positive-integer` | Each smallMoleculeFeature smf_id MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeFeature[*].sme_id_refs[*]` | `D-SMF-SME_ID_REFS-0010` | `required` | `positive-integer` | Each evidence reference in smallMoleculeFeature.sme_id_refs MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeFeature[*].sme_id_ref_ambiguity_code` | `D-SMF-SME_ID_REF_AMBIGUITY_CODE-0001` | `required` | `integer-enum` | SmallMoleculeFeature sme_id_ref_ambiguity_code MUST be 1, 2, 3, or null | type=`integer-enum`, options=[1, 2, 3], null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].adduct_ion` | `D-SMF-ADDUCT_ION-0001` | `required` | `regex` | SmallMoleculeFeature adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-' | type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].isotopomer` | `D-SMF-ISOTOPOMER-0001` | `required` | `cv-term` | SmallMoleculeFeature isotopomer, when provided, MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeFeature[*].exp_mass_to_charge` | `D-SMF-EXP_MASS_TO_CHARGE-0001` | `required` | `decimal` | SmallMoleculeFeature exp_mass_to_charge MUST be a non-null decimal value | type=`decimal`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeFeature[*].charge` | `D-SMF-CHARGE-0001` | `required` | `positive-integer` | SmallMoleculeFeature charge MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeFeature[*].retention_time_in_seconds` | `D-SMF-RETENTION_TIME_IN_SECONDS-0001` | `required` | `decimal` | SmallMoleculeFeature retention_time_in_seconds MUST be a decimal value or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].retention_time_in_seconds_start` | `D-SMF-RETENTION_TIME_IN_SECONDS_START-0001` | `required` | `decimal` | SmallMoleculeFeature retention_time_in_seconds_start MUST be a decimal value or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].retention_time_in_seconds_end` | `D-SMF-RETENTION_TIME_IN_SECONDS_END-0001` | `required` | `decimal` | SmallMoleculeFeature retention_time_in_seconds_end MUST be a decimal value or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].abundance_assay[*]` | `D-SMF-ABUNDANCE_ASSAY-0010` | `required` | `decimal` | Each assay abundance value in smallMoleculeFeature.abundance_assay MUST be a decimal number or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeFeature[*].opt[*]` | `D-SMF-OPT-0010` | `required` | `property-check` | Each optional column entry in smallMoleculeFeature.opt is recommended to define identifier and param | recommended_properties=[`identifier`, `param`] |
| `$.smallMoleculeFeature[*].opt[*]` | `D-SMF-OPT-0020` | `required` | `regex` | Optional column identifier in smallMoleculeFeature.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]' | type=`regex`, json_path=`$.identifier`, pattern=`^global\|ms_run\[\d+\]\|assay\[\d+\]\|study_variable\[\d+\]$` |
| `$.smallMoleculeFeature[*].opt[*]` | `D-SMF-OPT-0030` | `required` | `cv-term` | Optional column param in smallMoleculeFeature.opt MUST be a valid non-null ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence` | `D-SME-0001` | `recommended` | `items-count` | The smallMoleculeEvidence (SME) table is recommended to be present and contain at least one evidence row | match_is_required=True; type=`items-count`, min_occurs=1 |
| `$.smallMoleculeEvidence[*]` | `D-SME-0010` | `required` | `property-check` | Each smallMoleculeEvidence (SME) row MUST define all mandatory SME properties (prefix, header_prefix, sme_id, evidence_input_id, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, derivatized_form, adduct_ion, exp_mass_to_charge, charge, theoretical_mass_to_charge, spectra_ref, identification_method, ms_level, id_confidence_measure, rank) | required_properties=[`prefix`, `header_prefix`, `sme_id`, `evidence_input_id`, `database_identifier`, `chemical_formula`, `smiles`, `inchi`, `chemical_name`, `uri`, `derivatized_form`, `adduct_ion`, `exp_mass_to_charge`, `charge`, `theoretical_mass_to_charge`, `spectra_ref`, `identification_method`, `ms_level`, `id_confidence_measure`, `rank`] |
| `$.smallMoleculeEvidence[*].prefix` | `D-SME-PREFIX-0001` | `required` | `regex` | Each smallMoleculeEvidence row prefix MUST equal 'SME' | type=`regex`, pattern=`^SME$` |
| `$.smallMoleculeEvidence[*].header_prefix` | `D-SME-HEADER_PREFIX-0001` | `required` | `regex` | Each smallMoleculeEvidence row header_prefix MUST equal 'SEH' | type=`regex`, pattern=`^SEH$` |
| `$.smallMoleculeEvidence[*].sme_id` | `D-SME-SME_ID-0001` | `required` | `positive-integer` | Each smallMoleculeEvidence sme_id MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeEvidence[*].evidence_input_id` | `D-SME-EVIDENCE_INPUT_ID-0001` | `required` | `string-length` | Each smallMoleculeEvidence evidence_input_id MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].database_identifier` | `D-SME-DATABASE_IDENTIFIER-0001` | `required` | `not-null` | Each smallMoleculeEvidence database_identifier MUST be non-null | type=`not-null`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].chemical_formula` | `D-SME-CHEMICAL_FORMULA-0001` | `required` | `string-length` | SmallMoleculeEvidence chemical_formula, when provided, MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].smiles` | `D-SME-SMILES-0001` | `required` | `string-length` | SmallMoleculeEvidence smiles, when provided, MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].inchi` | `D-SME-INCHI-0001` | `required` | `string-length` | SmallMoleculeEvidence inchi, when provided, MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].chemical_name` | `D-SME-CHEMICAL_NAME-0001` | `required` | `string-length` | SmallMoleculeEvidence chemical_name, when provided, MUST be a non-empty string (minimum 1 character) | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].uri` | `D-SME-URI-0001` | `required` | `uri` | SmallMoleculeEvidence uri, when provided, MUST be a valid URI | type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].derivatized_form` | `D-SME-DERIVATIZED_FORM-0001` | `required` | `cv-term` | SmallMoleculeEvidence derivatized_form, when provided, MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].adduct_ion` | `D-SME-ADDUCT_ION-0001` | `required` | `regex` | SmallMoleculeEvidence adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-' | type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].exp_mass_to_charge` | `D-SME-EXP_MASS_TO_CHARGE-0001` | `required` | `decimal` | SmallMoleculeEvidence exp_mass_to_charge MUST be a non-null decimal value | type=`decimal`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].charge` | `D-SME-CHARGE-0001` | `required` | `positive-integer` | SmallMoleculeEvidence charge MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeEvidence[*].theoretical_mass_to_charge` | `D-SME-THEORETICAL_MASS_TO_CHARGE-0001` | `required` | `decimal` | SmallMoleculeEvidence theoretical_mass_to_charge MUST be a non-null decimal value | type=`decimal`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].spectra_ref` | `D-SME-SPECTRA_REF-0001` | `required` | `items-count` | Each smallMoleculeEvidence row MUST define at least one spectrum reference in spectra_ref | type=`items-count`, min_occurs=1, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].spectra_ref[*]` | `D-SME-SPECTRA_REF-0010` | `required` | `property-check` | Each spectrum reference entry in smallMoleculeEvidence.spectra_ref MUST define ms_run and reference | required_properties=[`ms_run`, `reference`] |
| `$.smallMoleculeEvidence[*].spectra_ref[*]` | `D-SME-SPECTRA_REF-0011` | `required` | `positive-integer` | Spectrum reference ms_run index MUST be a positive integer (>= 1) | type=`positive-integer`, json_path=`$.ms_run`, min=1 |
| `$.smallMoleculeEvidence[*].spectra_ref[*]` | `D-SME-SPECTRA_REF-0012` | `required` | `not-null` | Spectrum reference string in smallMoleculeEvidence.spectra_ref MUST be non-null | type=`not-null`, json_path=`$.reference`, null_values=[None, 'null', '', '""', "''"] |
| `$.smallMoleculeEvidence[*].identification_method` | `D-SME-IDENTIFICATION_METHOD-0001` | `required` | `cv-term` | SmallMoleculeEvidence identification_method MUST be a valid ontology CV term or user-defined parameter | type=`cv-term`, allow_user_defined_terms=True |
| `$.smallMoleculeEvidence[*].ms_level` | `D-SME-MS_LEVEL-0001` | `required` | `cv-term-enum` | SmallMoleculeEvidence ms_level MUST use the PSI-MS 'ms level' CV term (MS:1000511) with a non-empty value | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000511`, name=`ms level`}], is_cv_term_value_required=True |
| `$.smallMoleculeEvidence[*].id_confidence_measure[*]` | `D-SME-ID_CONFIDENCE_MEASURE-0010` | `required` | `decimal` | Each confidence score in smallMoleculeEvidence.id_confidence_measure MUST be a decimal value or null | type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None] |
| `$.smallMoleculeEvidence[*].rank` | `D-SME-RANK-0001` | `required` | `positive-integer` | SmallMoleculeEvidence identification rank MUST be a positive integer (>= 1) | type=`positive-integer`, min=1 |
| `$.smallMoleculeEvidence[*].opt[*]` | `D-SME-OPT-0010` | `required` | `property-check` | Each optional column entry in smallMoleculeEvidence.opt is recommended to define identifier and param | recommended_properties=[`identifier`, `param`] |
| `$.smallMoleculeEvidence[*].opt[*]` | `D-SME-OPT-0020` | `required` | `regex` | Optional column identifier in smallMoleculeEvidence.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]' | type=`regex`, json_path=`$.identifier`, pattern=`^global\|ms_run\[\d+\]\|assay\[\d+\]\|study_variable\[\d+\]$` |
| `$.smallMoleculeEvidence[*].opt[*]` | `D-SME-OPT-0030` | `required` | `cv-term` | Optional column param in smallMoleculeEvidence.opt MUST be a valid non-null ontology CV term or user-defined parameter | type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"] |

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

- **Requirement Group** (1 requirement(s))
- **Group Description**: Root-level structural requirements for a valid HUPO-PSI mzTab-M 2.1.0-M document

#### `D-0001` (`required` — `property-check`)

- **Description**: Root mzTab-M document MUST contain the metadata section and is recommended to include smallMoleculeSummary
- **Summary**: required_properties=[`metadata`]; recommended_properties=[`smallMoleculeSummary`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-0001",
  "description": "Root mzTab-M document MUST contain the metadata section and is recommended to include smallMoleculeSummary",
  "enforcement_level": "required",
  "required_properties": [
    "metadata"
  ],
  "recommended_properties": [
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

#### `D-METADATA-0001` (`required` — `property-check`)

- **Description**: Metadata section MUST be present and define all mandatory mzTab-M 2.1.0-M properties (prefix, mzTab-version, mzTab-ID, quantification_method, ms_run, assay, study_variable, cv, database, small_molecule-quantification_unit, id_confidence_measure), and is recommended to define contact, publication, software, and sample
- **Summary**: match_is_required=True; required_properties=[`prefix`, `mzTab-version`, `mzTab-ID`, `quantification_method`, `ms_run`, `assay`, `study_variable`, `cv`, `database`, `small_molecule-quantification_unit`, `id_confidence_measure`]; recommended_properties=[`contact`, `publication`, `software`, `sample`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-0001",
  "description": "Metadata section MUST be present and define all mandatory mzTab-M 2.1.0-M properties (prefix, mzTab-version, mzTab-ID, quantification_method, ms_run, assay, study_variable, cv, database, small_molecule-quantification_unit, id_confidence_measure), and is recommended to define contact, publication, software, and sample",
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
    "id_confidence_measure"
  ],
  "recommended_properties": [
    "contact",
    "publication",
    "software",
    "sample"
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

### `$.metadata.instrument`

#### `D-METADATA-INSTRUMENT-0001` (`recommended` — `items-count`)

- **Description**: At least one MS instrument definition is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-INSTRUMENT-0001",
  "description": "At least one MS instrument definition is recommended in metadata",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 instrument entry in metadata.instrument",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.instrument[*]`

#### `D-METADATA-INSTRUMENT-0010` (`recommended` — `property-check`)

- **Description**: Each instrument entry is recommended to define the instrument name CV parameter
- **Summary**: recommended_properties=[`name`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-METADATA-INSTRUMENT-0010",
  "description": "Each instrument entry is recommended to define the instrument name CV parameter",
  "enforcement_level": "recommended",
  "recommended_properties": [
    "name"
  ]
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

#### `D-MTD-PUBLICATION-0001` (`recommended` — `items-count`)

- **Description**: At least one publication entry is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PUBLICATION-0001",
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

#### `D-MTD-CONTACT-0001` (`recommended` — `items-count`)

- **Description**: At least one contact person is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-0001",
  "description": "At least one contact person is recommended in metadata",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 contact entry in metadata.contact",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.contact[*]`

#### `D-MTD-CONTACT-0010` (`recommended` — `property-check`)

- **Description**: Each contact entry is recommended to include name, email, affiliation, and orcid
- **Summary**: recommended_properties=[`name`, `email`, `affiliation`, `orcid`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-0010",
  "description": "Each contact entry is recommended to include name, email, affiliation, and orcid",
  "enforcement_level": "recommended",
  "recommended_properties": [
    "name",
    "email",
    "affiliation",
    "orcid"
  ]
}
```

</details>

### `$.metadata.contact[*].name`

#### `D-MTD-CONTACT-NAME-0001` (`recommended` — `string-length`)

- **Description**: Contact name is recommended to be at least 5 characters long
- **Summary**: type=`string-length`, min_length=5

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-NAME-0001",
  "description": "Contact name is recommended to be at least 5 characters long",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "string-length",
    "description": "Contact name should have a minimum length of 5 characters",
    "minimum": 5
  }
}
```

</details>

### `$.metadata.contact[*].email`

#### `D-MTD-CONTACT-EMAIL-0001` (`required` — `email`)

- **Description**: Contact email, when present, MUST be a valid email address
- **Summary**: type=`email`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-EMAIL-0001",
  "description": "Contact email, when present, MUST be a valid email address",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "email",
    "description": "Contact email must conform to standard email address syntax"
  }
}
```

</details>

### `$.metadata.contact[*].affiliation`

#### `D-MTD-CONTACT-AFFILIATION-0001` (`recommended` — `string-length`)

- **Description**: Contact affiliation is recommended to be at least 2 characters long
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-AFFILIATION-0001",
  "description": "Contact affiliation is recommended to be at least 2 characters long",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "string-length",
    "description": "Contact affiliation should have a minimum length of 2 characters",
    "minimum": 2
  }
}
```

</details>

### `$.metadata.contact[*].orcid`

#### `D-MTD-CONTACT-ORCID-0001` (`required` — `regex`)

- **Description**: Contact ORCID, when provided, MUST match the 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX)
- **Summary**: type=`regex`, pattern=`^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-CONTACT-ORCID-0001",
  "description": "Contact ORCID, when provided, MUST match the 16-digit ORCID format (XXXX-XXXX-XXXX-XXXX)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "ORCID identifier must match pattern ^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}[X0-9]$ or be null",
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

#### `D-MTD-SAMPLE-0001` (`recommended` — `items-count`)

- **Description**: At least one sample entry is recommended in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SAMPLE-0001",
  "description": "At least one sample entry is recommended in metadata",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 sample entry in metadata.sample",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.metadata.sample[*]`

#### `D-MTD-SAMPLE-0010` (`recommended` — `property-check`)

- **Description**: Each sample entry MUST define name and is recommended to include species
- **Summary**: required_properties=[`name`]; recommended_properties=[`species`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SAMPLE-0010",
  "description": "Each sample entry MUST define name and is recommended to include species",
  "enforcement_level": "recommended",
  "required_properties": [
    "name"
  ],
  "recommended_properties": [
    "species"
  ]
}
```

</details>

### `$.metadata.sample[*].name`

#### `D-MTD-SAMPLE-0011` (`required` — `string-length`)

- **Description**: Sample name MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SAMPLE-0011",
  "description": "Sample name MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "Sample name must have a minimum length of 1 character",
    "minimum": 1
  }
}
```

</details>

### `$.metadata.sample[*].species[*]`

#### `D-MTD-SAMPLE-0020` (`recommended` — `allowed-cv-list`)

- **Description**: Sample species CV terms are recommended to be sourced from NCBITAXON, ENVO, or CHEBI ontologies
- **Summary**: type=`allowed-cv-list`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-SAMPLE-0020",
  "description": "Sample species CV terms are recommended to be sourced from NCBITAXON, ENVO, or CHEBI ontologies",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "allowed-cv-list",
    "description": "Species ontology term should belong to NCBITAXON, ENVO, or CHEBI",
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

#### `D-MTD-MS_RUN-0001` (`required` — `items-count`)

- **Description**: At least one MS run MUST be defined in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0001",
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

#### `D-MTD-MS_RUN-0010` (`required` — `property-check`)

- **Description**: Each MS run entry MUST define location and is recommended to define instrument_ref
- **Summary**: required_properties=[`location`]; recommended_properties=[`instrument_ref`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0010",
  "description": "Each MS run entry MUST define location and is recommended to define instrument_ref",
  "enforcement_level": "required",
  "required_properties": [
    "location"
  ],
  "recommended_properties": [
    "instrument_ref"
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

### `$.metadata.ms_run[*].instrument_ref`

#### `D-MTD-MS_RUN-0040` (`recommended` — `positive-integer`)

- **Description**: Each MS run instrument_ref is recommended to be a non-null positive integer (>= 1) referencing a declared instrument
- **Summary**: type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-MS_RUN-0040",
  "description": "Each MS run instrument_ref is recommended to be a non-null positive integer (>= 1) referencing a declared instrument",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Instrument reference should be a non-null positive integer (>= 1)",
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

### `$.metadata.assay`

#### `D-MTD-ASSAY-0001` (`required` — `items-count`)

- **Description**: At least one assay MUST be defined in metadata
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0001",
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

#### `D-MTD-ASSAY-0020` (`required` — `property-check`)

- **Description**: Each assay entry MUST define name and ms_run_ref, and is recommended to define sample_ref
- **Summary**: required_properties=[`name`, `ms_run_ref`]; recommended_properties=[`sample_ref`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0020",
  "description": "Each assay entry MUST define name and ms_run_ref, and is recommended to define sample_ref",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "ms_run_ref"
  ],
  "recommended_properties": [
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

#### `D-MTD-ASSAY-0060` (`required` — `positive-integer`)

- **Description**: Assay sample_ref, when provided, MUST be a positive integer (>= 1) referencing a declared sample
- **Summary**: type=`positive-integer`, min=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-ASSAY-0060",
  "description": "Assay sample_ref, when provided, MUST be a positive integer (>= 1) referencing a declared sample",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "Assay sample_ref must be a positive integer (>= 1) or null",
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

#### `D-MTD-PROTOCOL-0010` (`required` — `property-check`)

- **Description**: Each protocol entry MUST define both name and type
- **Summary**: required_properties=[`name`, `type`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PROTOCOL-0010",
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

#### `D-MTD-PROTOCOL-0020` (`required` — `string-length`)

- **Description**: Protocol name MUST be a valid string
- **Summary**: type=`string-length`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PROTOCOL-0020",
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

#### `D-MTD-PROTOCOL-0030` (`required` — `cv-term`)

- **Description**: Protocol type MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-PROTOCOL-0030",
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

#### `D-MTD-STUDY_VARIABLE_GROUP-0010` (`required` — `property-check`)

- **Description**: Each study variable group MUST define name and is recommended to define type and datatype
- **Summary**: required_properties=[`name`]; recommended_properties=[`type`, `datatype`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE_GROUP-0010",
  "description": "Each study variable group MUST define name and is recommended to define type and datatype",
  "enforcement_level": "required",
  "required_properties": [
    "name"
  ],
  "recommended_properties": [
    "type",
    "datatype"
  ]
}
```

</details>

### `$.metadata.study_variable_group[*].name`

#### `D-MTD-STUDY_VARIABLE_GROUP-0020` (`required` — `cv-term`)

- **Description**: Study variable group name MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE_GROUP-0020",
  "description": "Study variable group name MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "Study variable group name must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
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

#### `D-MTD-STUDY_VARIABLE-0020` (`required` — `property-check`)

- **Description**: Each study variable MUST define name and is recommended to include group_refs
- **Summary**: required_properties=[`name`]; recommended_properties=[`group_refs`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-MTD-STUDY_VARIABLE-0020",
  "description": "Each study variable MUST define name and is recommended to include group_refs",
  "enforcement_level": "required",
  "required_properties": [
    "name"
  ],
  "recommended_properties": [
    "group_refs"
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

#### `D-SML-0001` (`recommended` — `items-count`)

- **Description**: The smallMoleculeSummary (SML) table is recommended to be present and contain at least one summary row
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SML-0001",
  "description": "The smallMoleculeSummary (SML) table is recommended to be present and contain at least one summary row",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 entry in smallMoleculeSummary",
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

### `$.smallMoleculeFeature`

#### `D-SMF-0001` (`recommended` — `items-count`)

- **Description**: The smallMoleculeFeature (SMF) table is recommended to be present and contain at least one feature row
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-0001",
  "description": "The smallMoleculeFeature (SMF) table is recommended to be present and contain at least one feature row",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 entry in smallMoleculeFeature",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.smallMoleculeFeature[*]`

#### `D-SMF-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeFeature (SMF) row MUST define all mandatory SMF properties (prefix, header_prefix, smf_id, sme_id_refs, sme_id_ref_ambiguity_code, adduct_ion, isotopomer, exp_mass_to_charge, charge, retention_time_in_seconds, retention_time_in_seconds_start, retention_time_in_seconds_end)
- **Summary**: required_properties=[`prefix`, `header_prefix`, `smf_id`, `sme_id_refs`, `sme_id_ref_ambiguity_code`, `adduct_ion`, `isotopomer`, `exp_mass_to_charge`, `charge`, `retention_time_in_seconds`, `retention_time_in_seconds_start`, `retention_time_in_seconds_end`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-0010",
  "description": "Each smallMoleculeFeature (SMF) row MUST define all mandatory SMF properties (prefix, header_prefix, smf_id, sme_id_refs, sme_id_ref_ambiguity_code, adduct_ion, isotopomer, exp_mass_to_charge, charge, retention_time_in_seconds, retention_time_in_seconds_start, retention_time_in_seconds_end)",
  "enforcement_level": "required",
  "required_properties": [
    "prefix",
    "header_prefix",
    "smf_id",
    "sme_id_refs",
    "sme_id_ref_ambiguity_code",
    "adduct_ion",
    "isotopomer",
    "exp_mass_to_charge",
    "charge",
    "retention_time_in_seconds",
    "retention_time_in_seconds_start",
    "retention_time_in_seconds_end"
  ]
}
```

</details>

### `$.smallMoleculeFeature[*].prefix`

#### `D-SMF-PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeFeature row prefix MUST equal 'SMF'
- **Summary**: type=`regex`, pattern=`^SMF$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-PREFIX-0001",
  "description": "Each smallMoleculeFeature row prefix MUST equal 'SMF'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SMF row prefix must match the literal string 'SMF'",
    "pattern": "^SMF$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeFeature[*].header_prefix`

#### `D-SMF-HEADER_PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeFeature row header_prefix MUST equal 'SFH'
- **Summary**: type=`regex`, pattern=`^SFH$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-HEADER_PREFIX-0001",
  "description": "Each smallMoleculeFeature row header_prefix MUST equal 'SFH'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SMF header_prefix must match the literal string 'SFH'",
    "pattern": "^SFH$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeFeature[*].smf_id`

#### `D-SMF-SMF_ID-0001` (`required` — `positive-integer`)

- **Description**: Each smallMoleculeFeature smf_id MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-SMF_ID-0001",
  "description": "Each smallMoleculeFeature smf_id MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SMF smf_id must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeFeature[*].sme_id_refs[*]`

#### `D-SMF-SME_ID_REFS-0010` (`required` — `positive-integer`)

- **Description**: Each evidence reference in smallMoleculeFeature.sme_id_refs MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-SME_ID_REFS-0010",
  "description": "Each evidence reference in smallMoleculeFeature.sme_id_refs MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SMF sme_id_refs item must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeFeature[*].sme_id_ref_ambiguity_code`

#### `D-SMF-SME_ID_REF_AMBIGUITY_CODE-0001` (`required` — `integer-enum`)

- **Description**: SmallMoleculeFeature sme_id_ref_ambiguity_code MUST be 1, 2, 3, or null
- **Summary**: type=`integer-enum`, options=[1, 2, 3], null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-SME_ID_REF_AMBIGUITY_CODE-0001",
  "description": "SmallMoleculeFeature sme_id_ref_ambiguity_code MUST be 1, 2, 3, or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "integer-enum",
    "description": "SMF sme_id_ref_ambiguity_code must be in {1, 2, 3} or null",
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
    "options": {
      "1": "Ambiguous identification",
      "2": "Only different evidence streams for the same molecule with no ambiguity",
      "3": "Both ambiguous identification and multiple evidence streams"
    }
  }
}
```

</details>

### `$.smallMoleculeFeature[*].adduct_ion`

#### `D-SMF-ADDUCT_ION-0001` (`required` — `regex`)

- **Description**: SmallMoleculeFeature adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'
- **Summary**: type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-ADDUCT_ION-0001",
  "description": "SmallMoleculeFeature adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SMF adduct_ion must match pattern ^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$ or be null",
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
    "pattern": "^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeFeature[*].isotopomer`

#### `D-SMF-ISOTOPOMER-0001` (`required` — `cv-term`)

- **Description**: SmallMoleculeFeature isotopomer, when provided, MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-ISOTOPOMER-0001",
  "description": "SmallMoleculeFeature isotopomer, when provided, MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SMF isotopomer must be a valid CV term or null",
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

### `$.smallMoleculeFeature[*].exp_mass_to_charge`

#### `D-SMF-EXP_MASS_TO_CHARGE-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeFeature exp_mass_to_charge MUST be a non-null decimal value
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-EXP_MASS_TO_CHARGE-0001",
  "description": "SmallMoleculeFeature exp_mass_to_charge MUST be a non-null decimal value",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SMF exp_mass_to_charge must be a non-null decimal number",
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

### `$.smallMoleculeFeature[*].charge`

#### `D-SMF-CHARGE-0001` (`required` — `positive-integer`)

- **Description**: SmallMoleculeFeature charge MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-CHARGE-0001",
  "description": "SmallMoleculeFeature charge MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SMF charge must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeFeature[*].retention_time_in_seconds`

#### `D-SMF-RETENTION_TIME_IN_SECONDS-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeFeature retention_time_in_seconds MUST be a decimal value or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-RETENTION_TIME_IN_SECONDS-0001",
  "description": "SmallMoleculeFeature retention_time_in_seconds MUST be a decimal value or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SMF retention_time_in_seconds must be a decimal number or null",
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

### `$.smallMoleculeFeature[*].retention_time_in_seconds_start`

#### `D-SMF-RETENTION_TIME_IN_SECONDS_START-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeFeature retention_time_in_seconds_start MUST be a decimal value or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-RETENTION_TIME_IN_SECONDS_START-0001",
  "description": "SmallMoleculeFeature retention_time_in_seconds_start MUST be a decimal value or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SMF retention_time_in_seconds_start must be a decimal number or null",
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

### `$.smallMoleculeFeature[*].retention_time_in_seconds_end`

#### `D-SMF-RETENTION_TIME_IN_SECONDS_END-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeFeature retention_time_in_seconds_end MUST be a decimal value or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-RETENTION_TIME_IN_SECONDS_END-0001",
  "description": "SmallMoleculeFeature retention_time_in_seconds_end MUST be a decimal value or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SMF retention_time_in_seconds_end must be a decimal number or null",
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

### `$.smallMoleculeFeature[*].abundance_assay[*]`

#### `D-SMF-ABUNDANCE_ASSAY-0010` (`required` — `decimal`)

- **Description**: Each assay abundance value in smallMoleculeFeature.abundance_assay MUST be a decimal number or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-ABUNDANCE_ASSAY-0010",
  "description": "Each assay abundance value in smallMoleculeFeature.abundance_assay MUST be a decimal number or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SMF abundance_assay item must be a decimal number or null",
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

### `$.smallMoleculeFeature[*].opt[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for optional custom columns (opt_) in smallMoleculeFeature (SMF) rows

#### `D-SMF-OPT-0010` (`required` — `property-check`)

- **Description**: Each optional column entry in smallMoleculeFeature.opt is recommended to define identifier and param
- **Summary**: recommended_properties=[`identifier`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-OPT-0010",
  "description": "Each optional column entry in smallMoleculeFeature.opt is recommended to define identifier and param",
  "enforcement_level": "required",
  "recommended_properties": [
    "identifier",
    "param"
  ]
}
```

</details>

#### `D-SMF-OPT-0020` (`required` — `regex`)

- **Description**: Optional column identifier in smallMoleculeFeature.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'
- **Summary**: type=`regex`, json_path=`$.identifier`, pattern=`^global|ms_run\[\d+\]|assay\[\d+\]|study_variable\[\d+\]$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-OPT-0020",
  "description": "Optional column identifier in smallMoleculeFeature.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SMF opt identifier must match ^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "json_path": "$.identifier",
    "pattern": "^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "case_sensitive": true
  }
}
```

</details>

#### `D-SMF-OPT-0030` (`required` — `cv-term`)

- **Description**: Optional column param in smallMoleculeFeature.opt MUST be a valid non-null ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SMF-OPT-0030",
  "description": "Optional column param in smallMoleculeFeature.opt MUST be a valid non-null ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SMF opt param must be a valid non-null CV term (user-defined terms allowed)",
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

### `$.smallMoleculeEvidence`

#### `D-SME-0001` (`recommended` — `items-count`)

- **Description**: The smallMoleculeEvidence (SME) table is recommended to be present and contain at least one evidence row
- **Summary**: match_is_required=True; type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-0001",
  "description": "The smallMoleculeEvidence (SME) table is recommended to be present and contain at least one evidence row",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "items-count",
    "description": "Recommends at least 1 entry in smallMoleculeEvidence",
    "min_occurs": 1
  },
  "match_is_required": true
}
```

</details>

### `$.smallMoleculeEvidence[*]`

#### `D-SME-0010` (`required` — `property-check`)

- **Description**: Each smallMoleculeEvidence (SME) row MUST define all mandatory SME properties (prefix, header_prefix, sme_id, evidence_input_id, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, derivatized_form, adduct_ion, exp_mass_to_charge, charge, theoretical_mass_to_charge, spectra_ref, identification_method, ms_level, id_confidence_measure, rank)
- **Summary**: required_properties=[`prefix`, `header_prefix`, `sme_id`, `evidence_input_id`, `database_identifier`, `chemical_formula`, `smiles`, `inchi`, `chemical_name`, `uri`, `derivatized_form`, `adduct_ion`, `exp_mass_to_charge`, `charge`, `theoretical_mass_to_charge`, `spectra_ref`, `identification_method`, `ms_level`, `id_confidence_measure`, `rank`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-0010",
  "description": "Each smallMoleculeEvidence (SME) row MUST define all mandatory SME properties (prefix, header_prefix, sme_id, evidence_input_id, database_identifier, chemical_formula, smiles, inchi, chemical_name, uri, derivatized_form, adduct_ion, exp_mass_to_charge, charge, theoretical_mass_to_charge, spectra_ref, identification_method, ms_level, id_confidence_measure, rank)",
  "enforcement_level": "required",
  "required_properties": [
    "prefix",
    "header_prefix",
    "sme_id",
    "evidence_input_id",
    "database_identifier",
    "chemical_formula",
    "smiles",
    "inchi",
    "chemical_name",
    "uri",
    "derivatized_form",
    "adduct_ion",
    "exp_mass_to_charge",
    "charge",
    "theoretical_mass_to_charge",
    "spectra_ref",
    "identification_method",
    "ms_level",
    "id_confidence_measure",
    "rank"
  ]
}
```

</details>

### `$.smallMoleculeEvidence[*].prefix`

#### `D-SME-PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeEvidence row prefix MUST equal 'SME'
- **Summary**: type=`regex`, pattern=`^SME$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-PREFIX-0001",
  "description": "Each smallMoleculeEvidence row prefix MUST equal 'SME'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SME row prefix must match the literal string 'SME'",
    "pattern": "^SME$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].header_prefix`

#### `D-SME-HEADER_PREFIX-0001` (`required` — `regex`)

- **Description**: Each smallMoleculeEvidence row header_prefix MUST equal 'SEH'
- **Summary**: type=`regex`, pattern=`^SEH$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-HEADER_PREFIX-0001",
  "description": "Each smallMoleculeEvidence row header_prefix MUST equal 'SEH'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SME header_prefix must match the literal string 'SEH'",
    "pattern": "^SEH$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].sme_id`

#### `D-SME-SME_ID-0001` (`required` — `positive-integer`)

- **Description**: Each smallMoleculeEvidence sme_id MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SME_ID-0001",
  "description": "Each smallMoleculeEvidence sme_id MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SME sme_id must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].evidence_input_id`

#### `D-SME-EVIDENCE_INPUT_ID-0001` (`required` — `string-length`)

- **Description**: Each smallMoleculeEvidence evidence_input_id MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-EVIDENCE_INPUT_ID-0001",
  "description": "Each smallMoleculeEvidence evidence_input_id MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME evidence_input_id must be a non-null string of at least 1 character",
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

### `$.smallMoleculeEvidence[*].database_identifier`

#### `D-SME-DATABASE_IDENTIFIER-0001` (`required` — `not-null`)

- **Description**: Each smallMoleculeEvidence database_identifier MUST be non-null
- **Summary**: type=`not-null`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-DATABASE_IDENTIFIER-0001",
  "description": "Each smallMoleculeEvidence database_identifier MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SME database_identifier must not be null or empty",
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

### `$.smallMoleculeEvidence[*].chemical_formula`

#### `D-SME-CHEMICAL_FORMULA-0001` (`required` — `string-length`)

- **Description**: SmallMoleculeEvidence chemical_formula, when provided, MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-CHEMICAL_FORMULA-0001",
  "description": "SmallMoleculeEvidence chemical_formula, when provided, MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME chemical_formula must be a non-empty string or null",
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

### `$.smallMoleculeEvidence[*].smiles`

#### `D-SME-SMILES-0001` (`required` — `string-length`)

- **Description**: SmallMoleculeEvidence smiles, when provided, MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SMILES-0001",
  "description": "SmallMoleculeEvidence smiles, when provided, MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME smiles must be a non-empty string or null",
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

### `$.smallMoleculeEvidence[*].inchi`

#### `D-SME-INCHI-0001` (`required` — `string-length`)

- **Description**: SmallMoleculeEvidence inchi, when provided, MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-INCHI-0001",
  "description": "SmallMoleculeEvidence inchi, when provided, MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME inchi must be a non-empty string or null",
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

### `$.smallMoleculeEvidence[*].chemical_name`

#### `D-SME-CHEMICAL_NAME-0001` (`required` — `string-length`)

- **Description**: SmallMoleculeEvidence chemical_name, when provided, MUST be a non-empty string (minimum 1 character)
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-CHEMICAL_NAME-0001",
  "description": "SmallMoleculeEvidence chemical_name, when provided, MUST be a non-empty string (minimum 1 character)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "description": "SME chemical_name must be a non-empty string or null",
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

### `$.smallMoleculeEvidence[*].uri`

#### `D-SME-URI-0001` (`required` — `uri`)

- **Description**: SmallMoleculeEvidence uri, when provided, MUST be a valid URI
- **Summary**: type=`uri`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-URI-0001",
  "description": "SmallMoleculeEvidence uri, when provided, MUST be a valid URI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "uri",
    "description": "SME uri must be a valid URI or null",
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

### `$.smallMoleculeEvidence[*].derivatized_form`

#### `D-SME-DERIVATIZED_FORM-0001` (`required` — `cv-term`)

- **Description**: SmallMoleculeEvidence derivatized_form, when provided, MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True, exceptional_values=[], null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-DERIVATIZED_FORM-0001",
  "description": "SmallMoleculeEvidence derivatized_form, when provided, MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SME derivatized_form must be a valid CV term or null",
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

### `$.smallMoleculeEvidence[*].adduct_ion`

#### `D-SME-ADDUCT_ION-0001` (`required` — `regex`)

- **Description**: SmallMoleculeEvidence adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'
- **Summary**: type=`regex`, pattern=`^\[\d*M([+-][\w\d]+)*\]\d*[+-]$`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-ADDUCT_ION-0001",
  "description": "SmallMoleculeEvidence adduct_ion, when provided, MUST match the standard mzTab-M adduct notation '[nM+X]z+' or '[nM-X]z-'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SME adduct_ion must match pattern ^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$ or be null",
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
    "pattern": "^\\[\\d*M([+-][\\w\\d]+)*\\]\\d*[+-]$",
    "case_sensitive": true
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].exp_mass_to_charge`

#### `D-SME-EXP_MASS_TO_CHARGE-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeEvidence exp_mass_to_charge MUST be a non-null decimal value
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-EXP_MASS_TO_CHARGE-0001",
  "description": "SmallMoleculeEvidence exp_mass_to_charge MUST be a non-null decimal value",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SME exp_mass_to_charge must be a non-null decimal number",
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

### `$.smallMoleculeEvidence[*].charge`

#### `D-SME-CHARGE-0001` (`required` — `positive-integer`)

- **Description**: SmallMoleculeEvidence charge MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-CHARGE-0001",
  "description": "SmallMoleculeEvidence charge MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SME charge must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].theoretical_mass_to_charge`

#### `D-SME-THEORETICAL_MASS_TO_CHARGE-0001` (`required` — `decimal`)

- **Description**: SmallMoleculeEvidence theoretical_mass_to_charge MUST be a non-null decimal value
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-THEORETICAL_MASS_TO_CHARGE-0001",
  "description": "SmallMoleculeEvidence theoretical_mass_to_charge MUST be a non-null decimal value",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SME theoretical_mass_to_charge must be a non-null decimal number",
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

### `$.smallMoleculeEvidence[*].spectra_ref`

#### `D-SME-SPECTRA_REF-0001` (`required` — `items-count`)

- **Description**: Each smallMoleculeEvidence row MUST define at least one spectrum reference in spectra_ref
- **Summary**: type=`items-count`, min_occurs=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SPECTRA_REF-0001",
  "description": "Each smallMoleculeEvidence row MUST define at least one spectrum reference in spectra_ref",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "description": "Requires at least 1 spectrum reference entry in SME spectra_ref",
    "null_values": [
      null,
      "null",
      "",
      "\"\"",
      "''"
    ],
    "min_occurs": 1
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].spectra_ref[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for MS run and spectrum reference pairs in smallMoleculeEvidence (SME) rows

#### `D-SME-SPECTRA_REF-0010` (`required` — `property-check`)

- **Description**: Each spectrum reference entry in smallMoleculeEvidence.spectra_ref MUST define ms_run and reference
- **Summary**: required_properties=[`ms_run`, `reference`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SPECTRA_REF-0010",
  "description": "Each spectrum reference entry in smallMoleculeEvidence.spectra_ref MUST define ms_run and reference",
  "enforcement_level": "required",
  "required_properties": [
    "ms_run",
    "reference"
  ]
}
```

</details>

#### `D-SME-SPECTRA_REF-0011` (`required` — `positive-integer`)

- **Description**: Spectrum reference ms_run index MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, json_path=`$.ms_run`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SPECTRA_REF-0011",
  "description": "Spectrum reference ms_run index MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SME spectra_ref.ms_run must be a positive integer (>= 1)",
    "json_path": "$.ms_run",
    "minimum": 1
  }
}
```

</details>

#### `D-SME-SPECTRA_REF-0012` (`required` — `not-null`)

- **Description**: Spectrum reference string in smallMoleculeEvidence.spectra_ref MUST be non-null
- **Summary**: type=`not-null`, json_path=`$.reference`, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-SPECTRA_REF-0012",
  "description": "Spectrum reference string in smallMoleculeEvidence.spectra_ref MUST be non-null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "not-null",
    "description": "SME spectra_ref.reference must not be null or empty",
    "json_path": "$.reference",
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

### `$.smallMoleculeEvidence[*].identification_method`

#### `D-SME-IDENTIFICATION_METHOD-0001` (`required` — `cv-term`)

- **Description**: SmallMoleculeEvidence identification_method MUST be a valid ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-IDENTIFICATION_METHOD-0001",
  "description": "SmallMoleculeEvidence identification_method MUST be a valid ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SME identification_method must be a valid CV term (user-defined terms allowed)",
    "allow_user_defined_terms": true
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].ms_level`

#### `D-SME-MS_LEVEL-0001` (`required` — `cv-term-enum`)

- **Description**: SmallMoleculeEvidence ms_level MUST use the PSI-MS 'ms level' CV term (MS:1000511) with a non-empty value
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`MS:1000511`, name=`ms level`}], is_cv_term_value_required=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-MS_LEVEL-0001",
  "description": "SmallMoleculeEvidence ms_level MUST use the PSI-MS 'ms level' CV term (MS:1000511) with a non-empty value",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "description": "SME ms_level must be CV term MS:1000511 ('ms level') with a required value",
    "is_cv_term_value_required": true,
    "allowed_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "MS:1000511",
        "name": "ms level"
      }
    ]
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].id_confidence_measure[*]`

#### `D-SME-ID_CONFIDENCE_MEASURE-0010` (`required` — `decimal`)

- **Description**: Each confidence score in smallMoleculeEvidence.id_confidence_measure MUST be a decimal value or null
- **Summary**: type=`decimal`, null_values=[None, 'null', '', '""', "''"], exceptional_values=[None]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-ID_CONFIDENCE_MEASURE-0010",
  "description": "Each confidence score in smallMoleculeEvidence.id_confidence_measure MUST be a decimal value or null",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "decimal",
    "description": "SME id_confidence_measure item must be a decimal number or null",
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

### `$.smallMoleculeEvidence[*].rank`

#### `D-SME-RANK-0001` (`required` — `positive-integer`)

- **Description**: SmallMoleculeEvidence identification rank MUST be a positive integer (>= 1)
- **Summary**: type=`positive-integer`, min=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-RANK-0001",
  "description": "SmallMoleculeEvidence identification rank MUST be a positive integer (>= 1)",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "positive-integer",
    "description": "SME rank must be a positive integer (>= 1)",
    "minimum": 1
  }
}
```

</details>

### `$.smallMoleculeEvidence[*].opt[*]`

- **Requirement Group** (3 requirement(s))
- **Group Description**: Validation rules for optional custom columns (opt_) in smallMoleculeEvidence (SME) rows

#### `D-SME-OPT-0010` (`required` — `property-check`)

- **Description**: Each optional column entry in smallMoleculeEvidence.opt is recommended to define identifier and param
- **Summary**: recommended_properties=[`identifier`, `param`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-OPT-0010",
  "description": "Each optional column entry in smallMoleculeEvidence.opt is recommended to define identifier and param",
  "enforcement_level": "required",
  "recommended_properties": [
    "identifier",
    "param"
  ]
}
```

</details>

#### `D-SME-OPT-0020` (`required` — `regex`)

- **Description**: Optional column identifier in smallMoleculeEvidence.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'
- **Summary**: type=`regex`, json_path=`$.identifier`, pattern=`^global|ms_run\[\d+\]|assay\[\d+\]|study_variable\[\d+\]$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-OPT-0020",
  "description": "Optional column identifier in smallMoleculeEvidence.opt MUST be 'global' or reference an indexed 'ms_run[n]', 'assay[n]', or 'study_variable[n]'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "description": "SME opt identifier must match ^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "json_path": "$.identifier",
    "pattern": "^global|ms_run\\[\\d+\\]|assay\\[\\d+\\]|study_variable\\[\\d+\\]$",
    "case_sensitive": true
  }
}
```

</details>

#### `D-SME-OPT-0030` (`required` — `cv-term`)

- **Description**: Optional column param in smallMoleculeEvidence.opt MUST be a valid non-null ontology CV term or user-defined parameter
- **Summary**: type=`cv-term`, json_path=`$.param`, allow_user_defined_terms=True, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "D-SME-OPT-0030",
  "description": "Optional column param in smallMoleculeEvidence.opt MUST be a valid non-null ontology CV term or user-defined parameter",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "description": "SME opt param must be a valid non-null CV term (user-defined terms allowed)",
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
