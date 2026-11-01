# ISA-JSON file profile for MetaboLights

ISA-JSON file profile for MetaboLights

## General Information

| Field | Value |
| :--- | :--- |
| **ID** | `https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json` |
| **Name** | ISA-JSON file profile for MetaboLights |
| **Version** | `0.1.0` |
| **Extends** | - |
| **Description** | ISA-JSON file profile for MetaboLights |
| **JSONPath Targets** | 29 |
| **Total Requirements** | 42 (`required`: 40, `recommended`: 2, `optional`: 0) |

## Configuration

| Setting | Value |
| :--- | :--- |
| **CV Term Field Mapping** | `label`: `termSource`, `accession`: `termAccession`, `name`: `annotationValue`, `value`: `value` |
| **Object Unique ID Fields** | `@id` |

## Requirements Summary

| JSONPath | Code | Enforcement | Type / Constraint | Description | Definition Summary |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `$` | `M-00100` | `required` | `property-check` | - | required_properties=[`ontologySourceReferences`, `studies`] |
| `$.ontologySourceReferences[*]` | `M-00200` | `required` | `property-check` | Ontology source reference name must be defined. | required_properties=[`name`] |
| `$.ontologySourceReferences[*].name` | `M-00210` | `required` | `string-length` | Ontology source reference name must be defined. | type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"] |
| `$.studies` | `M-00300` | `required` | `items-count` | Exactly 1 study must be defined in ISA-JSON file | type=`items-count`, min_occurs=1, max_occurs=1 |
| `$.studies[0]` | `M-003101` | `required` | `property-check` | Exactly 1 study must be defined in ISA-JSON file | required_properties=[`filename`, `identifier`, `title`, `description`, `submissionDate`, `publicReleaseDate`, `factors`, `assays`, `protocols`, `studyDesignDescriptors`, `people`, `materials`, `characteristicCategories`] |
| `$.studies[0].identifier` | `M-00304` | `required` | `regex` | study identifier MUST have MTBLS, REQ or MHD prefix, and end with digits. e.g., MTBLS1, REQ202610220111. | type=`regex`, pattern=`^(MTBLS\|REQ\|MHD)\d$` |
| `$.studies[0].title` | `M-00305` | `required` | `string-length` | Study title MUST be equal or grater than 10 characters | type=`string-length`, min_length=10 |
| `$.studies[0].description` | `M-00306` | `required` | `string-length` | Study description MUST be equal or grater than 150 characters | type=`string-length`, min_length=150 |
| `$.studies[0].filename` | `M-00307` | `required` | `regex` | Study file name must start 's_' and follow study identifier and '.txt' extension. e.g. s_REQ11111.txt, s_MTBLS1.txt. | type=`regex`, pattern=`^s_(MTBLS\|REQ\|MHD)\d\.txt$` |
| `$.studies[0].factors` | `M-000308` | `required` | `items-count` | At least one factor MUST be defined in ISA-JSON file | type=`items-count`, min_occurs=1 |
| `$.studies[0].assays` | `M-000708` | `required` | `items-count` | At least one assay MUST be defined in ISA-JSON file | type=`items-count`, min_occurs=1 |
| `$.studies[0].characteristicCategories` | `M-000309` | `required` | `items-count` | At least one characteristic category MUST be defined in ISA-JSON file | type=`items-count`, min_occurs=1 |
| `$.studies[0].people` | `M-000310` | `required` | `items-count` | At least one contact MUST be defined in ISA-JSON file | type=`items-count`, min_occurs=1 |
| `$.studies[0].studyDesignDescriptors` | `M-000311` | `required` | `items-count` | At least 3 design descriptors MUST be defined. | type=`items-count`, min_occurs=3 |
| `$.studies[0].characteristicCategories[*].characteristicType` | `M-000316` | `required` | `cv-term-enum` | If there is a characteristic type 'Organism', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]. Term is not valid | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^organism$`, case_sensitive=False)] |
| `$.studies[0].characteristicCategories[*].characteristicType` | `M-000317` | `required` | `cv-term-enum` | If there is a characteristic type 'Organism Part', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]Term is not valid | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^organism part$`, case_sensitive=False)] |
| `$.studies[0].characteristicCategories[*].characteristicType` | `M-000318` | `required` | `cv-term-enum` | If there is a characteristic type 'cell type', It MUST be defined as [EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. Term is not valid | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0000324`, name=`cell type`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^cell type$`, case_sensitive=False)] |
| `$.studies[0].characteristicCategories[*].characteristicType` | `M-000319` | `required` | `cv-term-enum` | If there is a characteristic type 'disease', It MUST be defined as [MONDO, http://purl.obolibrary.org/obo/MONDO_00000019, disease]. Term is not valid | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`http://purl.obolibrary.org/obo/MONDO_00000019`, name=`disease`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^disease$`, case_sensitive=False)] |
| `$.studies[0].materials` | `M-000320` | `required` | `property-check` | sources and samples are required in study | required_properties=[`sources`, `samples`] |
| `$.studies[0].materials.sources` | `M-000321` | `required` | `items-count` | There must be at least one source material | type=`items-count`, min_occurs=1 |
| `$.studies[0].materials.sources[*]` | `M-000322` | `required` | `items-count` | Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism] | type=`items-count`, json_path=`characteristics[*].category["@id"]`, is_reference_field=True, min_match=1, item_paths=['characteristicType'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}]) |
| `$.studies[0].materials.sources[*]` | `M-000323` | `required` | `items-count` | Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part] | type=`items-count`, json_path=`characteristics[*].category["@id"]`, is_reference_field=True, min_match=1, item_paths=['.characteristicType'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}]) |
| `$.studies[0].materials.sources[*].characteristics[*]` | `M-000350` | `required` | `cv-term` | Term must be a valid ontology term | type=`cv-term`, json_path=`value`, allow_user_defined_terms=True |
| `$.studies[0].materials.sources[*].characteristics[*]` | `M-000360` | `required` | `allowed-cv-list` | 'organism' characteristic value must be from NCBITaxon, ENVO or CHEBI | type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}])] |
| `$.studies[0].materials.sources[*].characteristics[*]` | `M-000361` | `required` | `allowed-cv-list` | 'organism part' characteristic value must be from BTO, UBERON, CHEBI | type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['BTO', 'UBERON', 'NCIT', 'CHEBI'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}])] |
| `$.studies[0].materials.sources[*].characteristics[*]` | `M-000362` | `required` | `allowed-cv-list` | 'disease' characteristic value must be from MONDO, MP, SNOMED | type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['MONDO', 'MP', 'SNOMED'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{name=`disease`}])] |
| `$.studies[0].materials.sources[*].characteristics[*]` | `M-000363` | `required` | `allowed-cv-list` | 'cell type' characteristic value must be from CL or CLO | type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['CL', 'CLO'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0000324`, name=`cell type`}])] |
| `$.studies[0].assays[*]` | `M-000720` | `required` | `property-check` | sources and samples are required in study | required_properties=[`measurementType`, `technologyType`, `filename`, `materials`, `dataFiles`] |
| `$.studies[0].protocols` | `M-000400` | `required` | `items-count` | At least one protocol MUST be defined in ISA-JSON file | type=`items-count`, min_occurs=1 |
| `$.studies[0].protocols[*]` | `M-000401` | `required` | `property-check` | Name, description and protocol type are required | required_properties=[`name`, `description`, `protocolType`] |
| `$.studies[0].protocols[*]` | `M-000402` | `required` | `items-count` | Mass spectrometry protocol MUST have a parameter type 'Instrument' | type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Instrument`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])] |
| `$.studies[0].protocols[*]` | `M-000403` | `required` | `items-count` | Mass spectrometry protocol MUST have a parameter type 'Scan polarity' | type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan polarity`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])] |
| `$.studies[0].protocols[*]` | `M-000404` | `required` | `items-count` | Mass spectrometry protocol MUST have a parameter type 'Scan m/z range' | type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan m/z range`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])] |
| `$.studies[0].protocols[*].name` | `M-000452` | `required` | `string-length` | Protocol name MUST be equal or greater than 2 characters | type=`string-length`, min_length=2 |
| `$.studies[0].protocols[*].description` | `M-007453` | `required` | `string-length` | Protocol description MUST be equal or greater than 40 characters | type=`string-length`, min_length=40 |
| `$.studies[0].protocols[*].protocolType` | `M-000454` | `required` | `property-check` | Protocol type value MUST be defined. | required_properties=[`annotationValue`] |
| `$.studies[0].protocols[*].protocolType` | `M-000453` | `recommended` | `cv-term-enum` | Protocol type is not in the defined list: '[EFO, http://www.ebi.ac.uk/efo/EFO_0005518, sample collection protocol, ], [MS, http://purl.obolibrary.org/obo/MS_1000831, sample preparation, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000470, mass spectrometry, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0001000, chromatography, ], [OBI, http://purl.obolibrary.org/obo/OBI_0200000, data transformation, ], [EDAM, http://edamontology.org/operation_4074, Compound identification, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000591, nuclear magnetic resonance spectroscopy, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0002927, NMR sample preparation, ]' | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0005518`, name=`sample collection protocol`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1000831`, name=`sample preparation`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0001000`, name=`chromatography`}, {cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0200000`, name=`data transformation`}, {cv_label=`EDAM`, cv_accession=`http://edamontology.org/operation_4074`, name=`Compound identification`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000591`, name=`nuclear magnetic resonance spectroscopy`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0002927`, name=`NMR sample preparation`}] |
| `$.studies[0].protocols[*].protocolType.annotationValue` | `M-000455` | `required` | `string-length` | Protocol type value MUST be equal or greater than 2 characters | type=`string-length`, min_length=2 |
| `$.studies[0].assays[*].technologyType` | `M-000500` | `recommended` | `cv-term-enum` | Assay technology type is not in the defined list: '[OBI, http://purl.obolibrary.org/obo/OBI_0000470, mass spectrometry assay, ], [OBI, http://purl.obolibrary.org/obo/OBI_0000623, NMR spectroscopy assay, ]' | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0000623`, name=`NMR spectroscopy assay`}] |
| `$.studies[0].assays[*].measurementType` | `M-000501` | `required` | `cv-term-enum` | Assay technology type is not in the defined list: '[MS, http://purl.obolibrary.org/obo/MS_1003904, untargeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003905, targeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003906, semi-targeted analysis, ]' | type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003904`, name=`untargeted analysis`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003905`, name=`targeted analysis`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003906`, name=`semi-targeted analysis`}] |
| `$.studies[0].assays[*].processSequence[*].parameterValues[*]` | `M-000502` | `required` | `parent-cv-term` | Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1000031, instrument model] | type=`parent-cv-term`, json_path=`value`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1000031`, name=`instrument model`}], precondition=[path=`category["@id"]`, is_reference_field=True, (type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Instrument`])] |
| `$.studies[0].assays[*].processSequence[*].parameterValues[*]` | `M-000503` | `required` | `parent-cv-term` | Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1003776, acquisition polarity] | type=`parent-cv-term`, json_path=`value`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003776`, name=`acquisition polarity`}], precondition=[path=`category["@id"]`, is_reference_field=True, (type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan polarity`])] |

## Requirement Definitions by JSONPath

### `$`

- **Requirement Group** (1 requirement(s))

#### `M-00100` (`required` — `property-check`)

- **Description**: -
- **Summary**: required_properties=[`ontologySourceReferences`, `studies`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00100",
  "enforcement_level": "required",
  "required_properties": [
    "ontologySourceReferences",
    "studies"
  ]
}
```

</details>

### `$.ontologySourceReferences[*]`

#### `M-00200` (`required` — `property-check`)

- **Description**: Ontology source reference name must be defined.
- **Summary**: required_properties=[`name`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00200",
  "description": "Ontology source reference name must be defined.",
  "enforcement_level": "required",
  "required_properties": [
    "name"
  ]
}
```

</details>

### `$.ontologySourceReferences[*].name`

#### `M-00210` (`required` — `string-length`)

- **Description**: Ontology source reference name must be defined.
- **Summary**: type=`string-length`, min_length=1, null_values=[None, 'null', '', '""', "''"]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00210",
  "description": "Ontology source reference name must be defined.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
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

### `$.studies`

#### `M-00300` (`required` — `items-count`)

- **Description**: Exactly 1 study must be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1, max_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00300",
  "description": "Exactly 1 study must be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1,
    "max_occurs": 1
  }
}
```

</details>

### `$.studies[0]`

#### `M-003101` (`required` — `property-check`)

- **Description**: Exactly 1 study must be defined in ISA-JSON file
- **Summary**: required_properties=[`filename`, `identifier`, `title`, `description`, `submissionDate`, `publicReleaseDate`, `factors`, `assays`, `protocols`, `studyDesignDescriptors`, `people`, `materials`, `characteristicCategories`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-003101",
  "description": "Exactly 1 study must be defined in ISA-JSON file",
  "enforcement_level": "required",
  "required_properties": [
    "filename",
    "identifier",
    "title",
    "description",
    "submissionDate",
    "publicReleaseDate",
    "factors",
    "assays",
    "protocols",
    "studyDesignDescriptors",
    "people",
    "materials",
    "characteristicCategories"
  ]
}
```

</details>

### `$.studies[0].identifier`

#### `M-00304` (`required` — `regex`)

- **Description**: study identifier MUST have MTBLS, REQ or MHD prefix, and end with digits. e.g., MTBLS1, REQ202610220111.
- **Summary**: type=`regex`, pattern=`^(MTBLS|REQ|MHD)\d$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00304",
  "description": "study identifier MUST have MTBLS, REQ or MHD prefix, and end with digits. e.g., MTBLS1, REQ202610220111.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "pattern": "^(MTBLS|REQ|MHD)\\d$",
    "case_sensitive": true
  }
}
```

</details>

### `$.studies[0].title`

#### `M-00305` (`required` — `string-length`)

- **Description**: Study title MUST be equal or grater than 10 characters
- **Summary**: type=`string-length`, min_length=10

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00305",
  "description": "Study title MUST be equal or grater than 10 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 10
  }
}
```

</details>

### `$.studies[0].description`

#### `M-00306` (`required` — `string-length`)

- **Description**: Study description MUST be equal or grater than 150 characters
- **Summary**: type=`string-length`, min_length=150

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00306",
  "description": "Study description MUST be equal or grater than 150 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 150
  }
}
```

</details>

### `$.studies[0].filename`

#### `M-00307` (`required` — `regex`)

- **Description**: Study file name must start 's_' and follow study identifier and '.txt' extension. e.g. s_REQ11111.txt, s_MTBLS1.txt.
- **Summary**: type=`regex`, pattern=`^s_(MTBLS|REQ|MHD)\d\.txt$`

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-00307",
  "description": "Study file name must start 's_' and follow study identifier and '.txt' extension. e.g. s_REQ11111.txt, s_MTBLS1.txt.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "regex",
    "pattern": "^s_(MTBLS|REQ|MHD)\\d\\.txt$",
    "case_sensitive": true
  }
}
```

</details>

### `$.studies[0].factors`

#### `M-000308` (`required` — `items-count`)

- **Description**: At least one factor MUST be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000308",
  "description": "At least one factor MUST be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].assays`

#### `M-000708` (`required` — `items-count`)

- **Description**: At least one assay MUST be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000708",
  "description": "At least one assay MUST be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].characteristicCategories`

#### `M-000309` (`required` — `items-count`)

- **Description**: At least one characteristic category MUST be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000309",
  "description": "At least one characteristic category MUST be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].people`

#### `M-000310` (`required` — `items-count`)

- **Description**: At least one contact MUST be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000310",
  "description": "At least one contact MUST be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].studyDesignDescriptors`

#### `M-000311` (`required` — `items-count`)

- **Description**: At least 3 design descriptors MUST be defined.
- **Summary**: type=`items-count`, min_occurs=3

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000311",
  "description": "At least 3 design descriptors MUST be defined.",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 3
  }
}
```

</details>

### `$.studies[0].characteristicCategories[*].characteristicType`

- **Requirement Group** (4 requirement(s))

#### `M-000316` (`required` — `cv-term-enum`)

- **Description**: If there is a characteristic type 'Organism', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]. Term is not valid
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^organism$`, case_sensitive=False)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000316",
  "description": "If there is a characteristic type 'Organism', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]. Term is not valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "precondition": {
      "evaluations": [
        {
          "json_path": "annotationValue",
          "constraint": {
            "type": "regex",
            "pattern": "^organism$",
            "case_sensitive": false
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "allowed_cv_terms": [
      {
        "cv_label": "NCIT",
        "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C14250",
        "name": "Organism"
      }
    ]
  }
}
```

</details>

#### `M-000317` (`required` — `cv-term-enum`)

- **Description**: If there is a characteristic type 'Organism Part', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]Term is not valid
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^organism part$`, case_sensitive=False)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000317",
  "description": "If there is a characteristic type 'Organism Part', It MUST be defined as [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]Term is not valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "precondition": {
      "evaluations": [
        {
          "json_path": "annotationValue",
          "constraint": {
            "type": "regex",
            "pattern": "^organism part$",
            "case_sensitive": false
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "allowed_cv_terms": [
      {
        "cv_label": "NCIT",
        "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C103199",
        "name": "Organism Part"
      }
    ]
  }
}
```

</details>

#### `M-000318` (`required` — `cv-term-enum`)

- **Description**: If there is a characteristic type 'cell type', It MUST be defined as [EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. Term is not valid
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0000324`, name=`cell type`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^cell type$`, case_sensitive=False)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000318",
  "description": "If there is a characteristic type 'cell type', It MUST be defined as [EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. Term is not valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "precondition": {
      "evaluations": [
        {
          "json_path": "annotationValue",
          "constraint": {
            "type": "regex",
            "pattern": "^cell type$",
            "case_sensitive": false
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "allowed_cv_terms": [
      {
        "cv_label": "EFO",
        "cv_accession": "http://www.ebi.ac.uk/efo/EFO_0000324",
        "name": "cell type"
      }
    ]
  }
}
```

</details>

#### `M-000319` (`required` — `cv-term-enum`)

- **Description**: If there is a characteristic type 'disease', It MUST be defined as [MONDO, http://purl.obolibrary.org/obo/MONDO_00000019, disease]. Term is not valid
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MONDO`, cv_accession=`http://purl.obolibrary.org/obo/MONDO_00000019`, name=`disease`}], precondition=[path=`annotationValue`, (type=`regex`, pattern=`^disease$`, case_sensitive=False)]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000319",
  "description": "If there is a characteristic type 'disease', It MUST be defined as [MONDO, http://purl.obolibrary.org/obo/MONDO_00000019, disease]. Term is not valid",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "precondition": {
      "evaluations": [
        {
          "json_path": "annotationValue",
          "constraint": {
            "type": "regex",
            "pattern": "^disease$",
            "case_sensitive": false
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "allowed_cv_terms": [
      {
        "cv_label": "MONDO",
        "cv_accession": "http://purl.obolibrary.org/obo/MONDO_00000019",
        "name": "disease"
      }
    ]
  }
}
```

</details>

### `$.studies[0].materials`

#### `M-000320` (`required` — `property-check`)

- **Description**: sources and samples are required in study
- **Summary**: required_properties=[`sources`, `samples`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000320",
  "description": "sources and samples are required in study",
  "enforcement_level": "required",
  "required_properties": [
    "sources",
    "samples"
  ]
}
```

</details>

### `$.studies[0].materials.sources`

#### `M-000321` (`required` — `items-count`)

- **Description**: There must be at least one source material
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000321",
  "description": "There must be at least one source material",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].materials.sources[*]`

- **Requirement Group** (2 requirement(s))

#### `M-000322` (`required` — `items-count`)

- **Description**: Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]
- **Summary**: type=`items-count`, json_path=`characteristics[*].category["@id"]`, is_reference_field=True, min_match=1, item_paths=['characteristicType'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000322",
  "description": "Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "json_path": "characteristics[*].category[\"@id\"]",
    "is_reference_field": true,
    "item_value_jsonpath_list": [
      "characteristicType"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "NCIT",
          "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C14250",
          "name": "Organism"
        }
      ]
    },
    "min_match": 1
  }
}
```

</details>

#### `M-000323` (`required` — `items-count`)

- **Description**: Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]
- **Summary**: type=`items-count`, json_path=`characteristics[*].category["@id"]`, is_reference_field=True, min_match=1, item_paths=['.characteristicType'], item_constraint=(type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}])

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000323",
  "description": "Source material must have a characteristic value with type [NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, Organism Part]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "json_path": "characteristics[*].category[\"@id\"]",
    "is_reference_field": true,
    "item_value_jsonpath_list": [
      ".characteristicType"
    ],
    "item_value_match_constraint": {
      "type": "cv-term-enum",
      "allowed_cv_terms": [
        {
          "cv_label": "NCIT",
          "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C103199",
          "name": "Organism Part"
        }
      ]
    },
    "min_match": 1
  }
}
```

</details>

### `$.studies[0].materials.sources[*].characteristics[*]`

- **Requirement Group** (5 requirement(s))

#### `M-000350` (`required` — `cv-term`)

- **Description**: Term must be a valid ontology term
- **Summary**: type=`cv-term`, json_path=`value`, allow_user_defined_terms=True

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000350",
  "description": "Term must be a valid ontology term",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term",
    "json_path": "value",
    "allow_user_defined_terms": true
  }
}
```

</details>

#### `M-000360` (`required` — `allowed-cv-list`)

- **Description**: 'organism' characteristic value must be from NCBITaxon, ENVO or CHEBI
- **Summary**: type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['NCBITAXON', 'ENVO', 'CHEBI'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C14250`, name=`Organism`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000360",
  "description": "'organism' characteristic value must be from NCBITaxon, ENVO or CHEBI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "allowed-cv-list",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "cv-term-enum",
            "json_path": "characteristicType",
            "allowed_cv_terms": [
              {
                "cv_label": "NCIT",
                "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C14250",
                "name": "Organism"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
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

#### `M-000361` (`required` — `allowed-cv-list`)

- **Description**: 'organism part' characteristic value must be from BTO, UBERON, CHEBI
- **Summary**: type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['BTO', 'UBERON', 'NCIT', 'CHEBI'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`NCIT`, cv_accession=`http://purl.obolibrary.org/obo/NCIT_C103199`, name=`Organism Part`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000361",
  "description": "'organism part' characteristic value must be from BTO, UBERON, CHEBI",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "allowed-cv-list",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "cv-term-enum",
            "json_path": "characteristicType",
            "allowed_cv_terms": [
              {
                "cv_label": "NCIT",
                "cv_accession": "http://purl.obolibrary.org/obo/NCIT_C103199",
                "name": "Organism Part"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
    "allow_user_defined_terms": false,
    "allowed_cv_list": [
      "BTO",
      "UBERON",
      "NCIT",
      "CHEBI"
    ]
  }
}
```

</details>

#### `M-000362` (`required` — `allowed-cv-list`)

- **Description**: 'disease' characteristic value must be from MONDO, MP, SNOMED
- **Summary**: type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['MONDO', 'MP', 'SNOMED'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{name=`disease`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000362",
  "description": "'disease' characteristic value must be from MONDO, MP, SNOMED",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "allowed-cv-list",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "cv-term-enum",
            "json_path": "characteristicType",
            "allowed_cv_terms": [
              {
                "cv_label": "",
                "cv_accession": "",
                "name": "disease"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
    "allow_user_defined_terms": false,
    "allowed_cv_list": [
      "MONDO",
      "MP",
      "SNOMED"
    ]
  }
}
```

</details>

#### `M-000363` (`required` — `allowed-cv-list`)

- **Description**: 'cell type' characteristic value must be from CL or CLO
- **Summary**: type=`allowed-cv-list`, json_path=`value`, allowed_cv_list=['CL', 'CLO'], allow_user_defined_terms=False, precondition=[path=`category["@id"]`, is_reference_field=True, (type=`cv-term-enum`, json_path=`characteristicType`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0000324`, name=`cell type`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000363",
  "description": "'cell type' characteristic value must be from CL or CLO",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "allowed-cv-list",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "cv-term-enum",
            "json_path": "characteristicType",
            "allowed_cv_terms": [
              {
                "cv_label": "EFO",
                "cv_accession": "http://www.ebi.ac.uk/efo/EFO_0000324",
                "name": "cell type"
              }
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
    "allow_user_defined_terms": false,
    "allowed_cv_list": [
      "CL",
      "CLO"
    ]
  }
}
```

</details>

### `$.studies[0].assays[*]`

#### `M-000720` (`required` — `property-check`)

- **Description**: sources and samples are required in study
- **Summary**: required_properties=[`measurementType`, `technologyType`, `filename`, `materials`, `dataFiles`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000720",
  "description": "sources and samples are required in study",
  "enforcement_level": "required",
  "required_properties": [
    "measurementType",
    "technologyType",
    "filename",
    "materials",
    "dataFiles"
  ]
}
```

</details>

### `$.studies[0].protocols`

#### `M-000400` (`required` — `items-count`)

- **Description**: At least one protocol MUST be defined in ISA-JSON file
- **Summary**: type=`items-count`, min_occurs=1

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000400",
  "description": "At least one protocol MUST be defined in ISA-JSON file",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "min_occurs": 1
  }
}
```

</details>

### `$.studies[0].protocols[*]`

- **Requirement Group** (4 requirement(s))

#### `M-000401` (`required` — `property-check`)

- **Description**: Name, description and protocol type are required
- **Summary**: required_properties=[`name`, `description`, `protocolType`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000401",
  "description": "Name, description and protocol type are required",
  "enforcement_level": "required",
  "required_properties": [
    "name",
    "description",
    "protocolType"
  ]
}
```

</details>

#### `M-000402` (`required` — `items-count`)

- **Description**: Mass spectrometry protocol MUST have a parameter type 'Instrument'
- **Summary**: type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Instrument`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000402",
  "description": "Mass spectrometry protocol MUST have a parameter type 'Instrument'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "protocolType",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "CHMO",
                "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0000470",
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
      ".parameters[*]"
    ],
    "item_value_match_constraint": {
      "type": "string-enum",
      "json_path": "parameterName.annotationValue",
      "options": [
        "Instrument"
      ]
    },
    "min_match": 1,
    "max_match": 1
  }
}
```

</details>

#### `M-000403` (`required` — `items-count`)

- **Description**: Mass spectrometry protocol MUST have a parameter type 'Scan polarity'
- **Summary**: type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan polarity`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000403",
  "description": "Mass spectrometry protocol MUST have a parameter type 'Scan polarity'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "protocolType",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "CHMO",
                "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0000470",
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
      ".parameters[*]"
    ],
    "item_value_match_constraint": {
      "type": "string-enum",
      "json_path": "parameterName.annotationValue",
      "options": [
        "Scan polarity"
      ]
    },
    "min_match": 1,
    "max_match": 1
  }
}
```

</details>

#### `M-000404` (`required` — `items-count`)

- **Description**: Mass spectrometry protocol MUST have a parameter type 'Scan m/z range'
- **Summary**: type=`items-count`, min_match=1, max_match=1, item_paths=['.parameters[*]'], item_constraint=(type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan m/z range`]), precondition=[path=`protocolType`, (type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000404",
  "description": "Mass spectrometry protocol MUST have a parameter type 'Scan m/z range'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "items-count",
    "precondition": {
      "evaluations": [
        {
          "json_path": "protocolType",
          "constraint": {
            "type": "cv-term-enum",
            "allowed_cv_terms": [
              {
                "cv_label": "CHMO",
                "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0000470",
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
      ".parameters[*]"
    ],
    "item_value_match_constraint": {
      "type": "string-enum",
      "json_path": "parameterName.annotationValue",
      "options": [
        "Scan m/z range"
      ]
    },
    "min_match": 1,
    "max_match": 1
  }
}
```

</details>

### `$.studies[0].protocols[*].name`

#### `M-000452` (`required` — `string-length`)

- **Description**: Protocol name MUST be equal or greater than 2 characters
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000452",
  "description": "Protocol name MUST be equal or greater than 2 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 2
  }
}
```

</details>

### `$.studies[0].protocols[*].description`

#### `M-007453` (`required` — `string-length`)

- **Description**: Protocol description MUST be equal or greater than 40 characters
- **Summary**: type=`string-length`, min_length=40

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-007453",
  "description": "Protocol description MUST be equal or greater than 40 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 40
  }
}
```

</details>

### `$.studies[0].protocols[*].protocolType`

- **Requirement Group** (2 requirement(s))

#### `M-000454` (`required` — `property-check`)

- **Description**: Protocol type value MUST be defined.
- **Summary**: required_properties=[`annotationValue`]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000454",
  "description": "Protocol type value MUST be defined.",
  "enforcement_level": "required",
  "required_properties": [
    "annotationValue"
  ]
}
```

</details>

#### `M-000453` (`recommended` — `cv-term-enum`)

- **Description**: Protocol type is not in the defined list: '[EFO, http://www.ebi.ac.uk/efo/EFO_0005518, sample collection protocol, ], [MS, http://purl.obolibrary.org/obo/MS_1000831, sample preparation, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000470, mass spectrometry, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0001000, chromatography, ], [OBI, http://purl.obolibrary.org/obo/OBI_0200000, data transformation, ], [EDAM, http://edamontology.org/operation_4074, Compound identification, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000591, nuclear magnetic resonance spectroscopy, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0002927, NMR sample preparation, ]'
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`EFO`, cv_accession=`http://www.ebi.ac.uk/efo/EFO_0005518`, name=`sample collection protocol`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1000831`, name=`sample preparation`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000470`, name=`mass spectrometry`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0001000`, name=`chromatography`}, {cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0200000`, name=`data transformation`}, {cv_label=`EDAM`, cv_accession=`http://edamontology.org/operation_4074`, name=`Compound identification`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0000591`, name=`nuclear magnetic resonance spectroscopy`}, {cv_label=`CHMO`, cv_accession=`http://purl.obolibrary.org/obo/CHMO_0002927`, name=`NMR sample preparation`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000453",
  "description": "Protocol type is not in the defined list: '[EFO, http://www.ebi.ac.uk/efo/EFO_0005518, sample collection protocol, ], [MS, http://purl.obolibrary.org/obo/MS_1000831, sample preparation, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000470, mass spectrometry, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0001000, chromatography, ], [OBI, http://purl.obolibrary.org/obo/OBI_0200000, data transformation, ], [EDAM, http://edamontology.org/operation_4074, Compound identification, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0000591, nuclear magnetic resonance spectroscopy, ], [CHMO, http://purl.obolibrary.org/obo/CHMO_0002927, NMR sample preparation, ]'",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "EFO",
        "cv_accession": "http://www.ebi.ac.uk/efo/EFO_0005518",
        "name": "sample collection protocol"
      },
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1000831",
        "name": "sample preparation"
      },
      {
        "cv_label": "CHMO",
        "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0000470",
        "name": "mass spectrometry"
      },
      {
        "cv_label": "CHMO",
        "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0001000",
        "name": "chromatography"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "http://purl.obolibrary.org/obo/OBI_0200000",
        "name": "data transformation"
      },
      {
        "cv_label": "EDAM",
        "cv_accession": "http://edamontology.org/operation_4074",
        "name": "Compound identification"
      },
      {
        "cv_label": "CHMO",
        "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0000591",
        "name": "nuclear magnetic resonance spectroscopy"
      },
      {
        "cv_label": "CHMO",
        "cv_accession": "http://purl.obolibrary.org/obo/CHMO_0002927",
        "name": "NMR sample preparation"
      }
    ]
  }
}
```

</details>

### `$.studies[0].protocols[*].protocolType.annotationValue`

#### `M-000455` (`required` — `string-length`)

- **Description**: Protocol type value MUST be equal or greater than 2 characters
- **Summary**: type=`string-length`, min_length=2

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000455",
  "description": "Protocol type value MUST be equal or greater than 2 characters",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "string-length",
    "minimum": 2
  }
}
```

</details>

### `$.studies[0].assays[*].technologyType`

#### `M-000500` (`recommended` — `cv-term-enum`)

- **Description**: Assay technology type is not in the defined list: '[OBI, http://purl.obolibrary.org/obo/OBI_0000470, mass spectrometry assay, ], [OBI, http://purl.obolibrary.org/obo/OBI_0000623, NMR spectroscopy assay, ]'
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0000470`, name=`mass spectrometry assay`}, {cv_label=`OBI`, cv_accession=`http://purl.obolibrary.org/obo/OBI_0000623`, name=`NMR spectroscopy assay`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000500",
  "description": "Assay technology type is not in the defined list: '[OBI, http://purl.obolibrary.org/obo/OBI_0000470, mass spectrometry assay, ], [OBI, http://purl.obolibrary.org/obo/OBI_0000623, NMR spectroscopy assay, ]'",
  "enforcement_level": "recommended",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "OBI",
        "cv_accession": "http://purl.obolibrary.org/obo/OBI_0000470",
        "name": "mass spectrometry assay"
      },
      {
        "cv_label": "OBI",
        "cv_accession": "http://purl.obolibrary.org/obo/OBI_0000623",
        "name": "NMR spectroscopy assay"
      }
    ]
  }
}
```

</details>

### `$.studies[0].assays[*].measurementType`

#### `M-000501` (`required` — `cv-term-enum`)

- **Description**: Assay technology type is not in the defined list: '[MS, http://purl.obolibrary.org/obo/MS_1003904, untargeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003905, targeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003906, semi-targeted analysis, ]'
- **Summary**: type=`cv-term-enum`, allowed_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003904`, name=`untargeted analysis`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003905`, name=`targeted analysis`}, {cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003906`, name=`semi-targeted analysis`}]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000501",
  "description": "Assay technology type is not in the defined list: '[MS, http://purl.obolibrary.org/obo/MS_1003904, untargeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003905, targeted analysis, ], [MS, http://purl.obolibrary.org/obo/MS_1003906, semi-targeted analysis, ]'",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "cv-term-enum",
    "allowed_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1003904",
        "name": "untargeted analysis"
      },
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1003905",
        "name": "targeted analysis"
      },
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1003906",
        "name": "semi-targeted analysis"
      }
    ]
  }
}
```

</details>

### `$.studies[0].assays[*].processSequence[*].parameterValues[*]`

- **Requirement Group** (2 requirement(s))

#### `M-000502` (`required` — `parent-cv-term`)

- **Description**: Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1000031, instrument model]
- **Summary**: type=`parent-cv-term`, json_path=`value`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1000031`, name=`instrument model`}], precondition=[path=`category["@id"]`, is_reference_field=True, (type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Instrument`])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000502",
  "description": "Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1000031, instrument model]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "parent-cv-term",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "string-enum",
            "json_path": "parameterName.annotationValue",
            "options": [
              "Instrument"
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
    "parent_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1000031",
        "name": "instrument model"
      }
    ]
  }
}
```

</details>

#### `M-000503` (`required` — `parent-cv-term`)

- **Description**: Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1003776, acquisition polarity]
- **Summary**: type=`parent-cv-term`, json_path=`value`, parent_cv_terms=[{cv_label=`MS`, cv_accession=`http://purl.obolibrary.org/obo/MS_1003776`, name=`acquisition polarity`}], precondition=[path=`category["@id"]`, is_reference_field=True, (type=`string-enum`, json_path=`parameterName.annotationValue`, options=[`Scan polarity`])]

<details>
<summary>Requirement Definition (JSON)</summary>

```json
{
  "code": "M-000503",
  "description": "Instrument parameter value MUST be child of [MS, http://purl.obolibrary.org/obo/MS_1003776, acquisition polarity]",
  "enforcement_level": "required",
  "value_constraint": {
    "type": "parent-cv-term",
    "precondition": {
      "evaluations": [
        {
          "json_path": "category[\"@id\"]",
          "is_reference_field": true,
          "constraint": {
            "type": "string-enum",
            "json_path": "parameterName.annotationValue",
            "options": [
              "Scan polarity"
            ]
          },
          "join_operator": "and"
        }
      ],
      "join_operator": "and"
    },
    "json_path": "value",
    "parent_cv_terms": [
      {
        "cv_label": "MS",
        "cv_accession": "http://purl.obolibrary.org/obo/MS_1003776",
        "name": "acquisition polarity"
      }
    ]
  }
}
```

</details>
