from jsonprofile.profile import BaseCvTerm
from jsonprofile.profile.base import CvTermFieldMapping, EnforcementLevel
from jsonprofile.profile.constraints import (
    CollectionConstraint,
    CVListConstraint,
    CVTermConstraint,
    CVTermEnumConstraint,
    Evaluation,
    Precondition,
    RegexConstraint,
    StringConstraint,
)
from jsonprofile.profile.constraints.constraints import (
    ParentCVTermConstraint,
    StringEnumConstraint,
)
from jsonprofile.profile.model import (
    FieldRequirement,
    FieldRequirementGroup,
    JsonProfile,
    JsonProfileConfiguration,
)

DEFAULT_NULL_VALUES = [None, "null", "", '""', "''"]
#

ISA_JSON_CV_TERM_MAPPING = CvTermFieldMapping(
    label_field="termSource",
    accession_field="termAccession",
    name_field="annotationValue",
    value_field="value",
)

PREDEFINED_TECHNOLOGY_TYPES = [
    BaseCvTerm(
        cv_label="OBI",
        cv_accession="http://purl.obolibrary.org/obo/OBI_0000470",
        name="mass spectrometry assay",
    ),
    BaseCvTerm(
        cv_label="OBI",
        cv_accession="http://purl.obolibrary.org/obo/OBI_0000623",
        name="NMR spectroscopy assay",
    ),
]

PREDEFINED_MEASUREMENT_TYPES = [
    BaseCvTerm(
        cv_label="MS",
        cv_accession="http://purl.obolibrary.org/obo/MS_1003904",
        name="untargeted analysis",
    ),
    BaseCvTerm(
        cv_label="MS",
        cv_accession="http://purl.obolibrary.org/obo/MS_1003905",
        name="targeted analysis",
    ),
    BaseCvTerm(
        cv_label="MS",
        cv_accession="http://purl.obolibrary.org/obo/MS_1003906",
        name="semi-targeted analysis",
    ),
]

PREDEFINED_PROTOCOLS = [
    BaseCvTerm(
        cv_label="EFO",
        cv_accession="http://www.ebi.ac.uk/efo/EFO_0005518",
        name="sample collection protocol",
    ),
    BaseCvTerm(
        cv_label="MS",
        cv_accession="http://purl.obolibrary.org/obo/MS_1000831",
        name="sample preparation",
    ),
    BaseCvTerm(
        cv_label="CHMO",
        cv_accession="http://purl.obolibrary.org/obo/CHMO_0000470",
        name="mass spectrometry",
    ),
    BaseCvTerm(
        cv_label="CHMO",
        cv_accession="http://purl.obolibrary.org/obo/CHMO_0001000",
        name="chromatography",
    ),
    BaseCvTerm(
        cv_label="OBI",
        cv_accession="http://purl.obolibrary.org/obo/OBI_0200000",
        name="data transformation",
    ),
    BaseCvTerm(
        cv_label="EDAM",
        cv_accession="http://edamontology.org/operation_4074",
        name="Compound identification",
    ),
    BaseCvTerm(
        cv_label="CHMO",
        cv_accession="http://purl.obolibrary.org/obo/CHMO_0000591",
        name="nuclear magnetic resonance spectroscopy",
    ),
    BaseCvTerm(
        cv_label="CHMO",
        cv_accession="http://purl.obolibrary.org/obo/CHMO_0002927",
        name="NMR sample preparation",
    ),
]

METABOLIGHTS_BASE_PROFILE = JsonProfile(
    id="https://github.com/ISA-tools/isa-api/metabolights-profile-0.1.0.json",
    version="0.1.0",
    name="ISA-JSON file profile for MetaboLights",
    description="ISA-JSON file profile for MetaboLights",
    configuration=JsonProfileConfiguration(
        object_unique_id_fields=["@id"], cv_term_field_mapping=ISA_JSON_CV_TERM_MAPPING
    ),
    requirements={
        "$": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="M-00100",
                    required_properties=["ontologySourceReferences", "studies"],
                ),
            ]
        ),
        "$.ontologySourceReferences[*]": FieldRequirement(
            code="M-00200",
            description="Ontology source reference name must be defined.",
            required_properties=["name"],
        ),
        "$.ontologySourceReferences[*].name": FieldRequirement(
            code="M-00210",
            description="Ontology source reference name must be defined.",
            value_constraint=StringConstraint(
                minimum=1, null_values=DEFAULT_NULL_VALUES
            ),
        ),
        "$.studies": FieldRequirement(
            code="M-00300",
            description="Exactly 1 study must be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1, max_occurs=1),
        ),
        "$.studies[0]": FieldRequirement(
            code="M-003101",
            description="Exactly 1 study must be defined in ISA-JSON file",
            required_properties=[
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
                "characteristicCategories",
            ],
        ),
        "$.studies[0].identifier": FieldRequirement(
            code="M-00304",
            description="study identifier MUST have MTBLS, REQ or MHD prefix"
            ", and end with digits. e.g., MTBLS1, REQ202610220111.",
            value_constraint=RegexConstraint(pattern=r"^(MTBLS|REQ|MHD)\d$"),
        ),
        "$.studies[0].title": FieldRequirement(
            code="M-00305",
            description="Study title MUST be equal or grater than 10 characters",
            value_constraint=StringConstraint(minimum=10),
        ),
        "$.studies[0].description": FieldRequirement(
            code="M-00306",
            description="Study description MUST be equal or grater than 150 characters",
            value_constraint=StringConstraint(minimum=150),
        ),
        "$.studies[0].filename": FieldRequirement(
            code="M-00307",
            description="Study file name must start 's_' and "
            "follow study identifier and '.txt' extension. "
            "e.g. s_REQ11111.txt, s_MTBLS1.txt.",
            value_constraint=RegexConstraint(pattern=r"^s_(MTBLS|REQ|MHD)\d\.txt$"),
        ),
        "$.studies[0].factors": FieldRequirement(
            code="M-000308",
            description="At least one factor MUST be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].assays": FieldRequirement(
            code="M-000308",
            description="At least one assay MUST be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].characteristicCategories": FieldRequirement(
            code="M-000309",
            description="At least one characteristic category "
            "MUST be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].people": FieldRequirement(
            code="M-000310",
            description="At least one contact MUST be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].studyDesignDescriptors": FieldRequirement(
            code="M-000311",
            description="At least 3 design descriptors MUST be defined.",
            value_constraint=CollectionConstraint(min_occurs=3),
        ),
        "$.studies[0].characteristicCategories[*].characteristicType": FieldRequirementGroup(  # noqa: E501
            requirements=[
                FieldRequirement(
                    code="M-000316",
                    description="If there is a characteristic type 'Organism', "
                    "It MUST be defined as "
                    "[NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]. "
                    "Term is not valid",
                    value_constraint=CVTermEnumConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="annotationValue",
                                    constraint=RegexConstraint(
                                        pattern=r"^organism$", case_sensitive=False
                                    ),
                                )
                            ]
                        ),
                        allowed_cv_terms=[
                            BaseCvTerm(
                                cv_accession="http://purl.obolibrary.org/obo/NCIT_C14250",
                                cv_label="NCIT",
                                name="Organism",
                            )
                        ],
                    ),
                ),
                FieldRequirement(
                    code="M-000317",
                    description="If there is a characteristic type 'Organism Part', "
                    "It MUST be defined as "
                    "[NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, "
                    "Organism Part]"
                    "Term is not valid",
                    value_constraint=CVTermEnumConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="annotationValue",
                                    constraint=RegexConstraint(
                                        pattern=r"^organism part$", case_sensitive=False
                                    ),
                                )
                            ]
                        ),
                        allowed_cv_terms=[
                            BaseCvTerm(
                                cv_accession="http://purl.obolibrary.org/obo/NCIT_C103199",
                                cv_label="NCIT",
                                name="Organism Part",
                            )
                        ],
                    ),
                ),
                FieldRequirement(
                    code="M-000318",
                    description="If there is a characteristic type 'cell type', "
                    "It MUST be defined as "
                    "[EFO, http://www.ebi.ac.uk/efo/EFO_0000324, cell type]. "
                    "Term is not valid",
                    value_constraint=CVTermEnumConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="annotationValue",
                                    constraint=RegexConstraint(
                                        pattern=r"^cell type$", case_sensitive=False
                                    ),
                                )
                            ]
                        ),
                        allowed_cv_terms=[
                            BaseCvTerm(
                                cv_accession="http://www.ebi.ac.uk/efo/EFO_0000324",
                                cv_label="EFO",
                                name="cell type",
                            )
                        ],
                    ),
                ),
                FieldRequirement(
                    code="M-000319",
                    description="If there is a characteristic type 'disease', "
                    "It MUST be defined as "
                    "[MONDO, http://purl.obolibrary.org/obo/MONDO_00000019, disease]. "
                    "Term is not valid",
                    value_constraint=CVTermEnumConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="annotationValue",
                                    constraint=RegexConstraint(
                                        pattern=r"^disease$", case_sensitive=False
                                    ),
                                )
                            ]
                        ),
                        allowed_cv_terms=[
                            BaseCvTerm(
                                cv_accession="http://purl.obolibrary.org/obo/MONDO_00000019",
                                cv_label="MONDO",
                                name="disease",
                            )
                        ],
                    ),
                ),
            ]
        ),
        "$.studies[0].materials": FieldRequirement(
            code="M-000320",
            description="sources and samples are required in study",
            required_properties=["sources", "samples"],
        ),
        "$.studies[0].materials.sources": FieldRequirement(
            code="M-000321",
            description="There must be at least one source material",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].materials.sources[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="M-000322",
                    description="Source material must have a "
                    "characteristic value with type "
                    "[NCIT, http://purl.obolibrary.org/obo/NCIT_C14250, Organism]",
                    value_constraint=CollectionConstraint(
                        json_path='characteristics[*].category["@id"]',
                        is_reference_field=True,
                        item_value_jsonpath_list=["characteristicType"],
                        min_match=1,
                        item_value_match_constraint=CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_accession="http://purl.obolibrary.org/obo/NCIT_C14250",
                                    cv_label="NCIT",
                                    name="Organism",
                                )
                            ],
                        ),
                    ),
                ),
                FieldRequirement(
                    code="M-000323",
                    description="Source material must have a "
                    "characteristic value with type "
                    "[NCIT, http://purl.obolibrary.org/obo/NCIT_C103199, "
                    "Organism Part]",
                    value_constraint=CollectionConstraint(
                        json_path='characteristics[*].category["@id"]',
                        is_reference_field=True,
                        item_value_jsonpath_list=[".characteristicType"],
                        min_match=1,
                        item_value_match_constraint=CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_accession="http://purl.obolibrary.org/obo/NCIT_C103199",
                                    cv_label="NCIT",
                                    name="Organism Part",
                                )
                            ],
                        ),
                    ),
                ),
            ]
        ),
        "$.studies[0].materials.sources[*].characteristics[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="M-000350",
                    description="Term must be a valid ontology term",
                    value_constraint=CVTermConstraint(
                        json_path="value", allow_user_defined_terms=True
                    ),
                ),
                FieldRequirement(
                    code="M-000360",
                    description="'organism' characteristic value "
                    "must be from NCBITaxon, ENVO or CHEBI",
                    value_constraint=CVListConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=CVTermEnumConstraint(
                                        json_path="characteristicType",
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_accession="http://purl.obolibrary.org/obo/NCIT_C14250",
                                                cv_label="NCIT",
                                                name="Organism",
                                            )
                                        ],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        allowed_cv_list=["NCBITaxon", "ENVO", "CHEBI"],
                    ),
                ),
                FieldRequirement(
                    code="M-000361",
                    description="'organism part' characteristic value "
                    "must be from BTO, UBERON, CHEBI",
                    value_constraint=CVListConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=CVTermEnumConstraint(
                                        json_path="characteristicType",
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_accession="http://purl.obolibrary.org/obo/NCIT_C103199",
                                                cv_label="NCIT",
                                                name="Organism Part",
                                            )
                                        ],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        allowed_cv_list=["BTO", "UBERON", "NCIT", "CHEBI"],
                    ),
                ),
                FieldRequirement(
                    code="M-000362",
                    description="'disease' characteristic value "
                    "must be from MONDO, MP, SNOMED",
                    value_constraint=CVListConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=CVTermEnumConstraint(
                                        json_path="characteristicType",
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_accession="",
                                                cv_label="",
                                                name="disease",
                                            )
                                        ],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        allowed_cv_list=["MONDO", "MP", "SNOMED"],
                    ),
                ),
                FieldRequirement(
                    code="M-000363",
                    description="'cell type' characteristic value "
                    "must be from CL or CLO",
                    value_constraint=CVListConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=CVTermEnumConstraint(
                                        json_path="characteristicType",
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_accession="http://www.ebi.ac.uk/efo/EFO_0000324",
                                                cv_label="EFO",
                                                name="cell type",
                                            )
                                        ],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        allowed_cv_list=["CL", "CLO"],
                    ),
                ),
            ]
        ),
        "$.studies[0].assays[*]": FieldRequirement(
            code="M-000320",
            description="sources and samples are required in study",
            required_properties=[
                "measurementType",
                "technologyType",
                "filename",
                "materials",
                "dataFiles",
            ],
        ),
        "$.studies[0].protocols": FieldRequirement(
            code="M-000400",
            description="At least one protocol MUST be defined in ISA-JSON file",
            value_constraint=CollectionConstraint(min_occurs=1),
        ),
        "$.studies[0].protocols[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="M-000401",
                    description="Name, description and protocol type are required",
                    required_properties=["name", "description", "protocolType"],
                ),
                FieldRequirement(
                    code="M-000402",
                    description="Mass spectrometry protocol MUST have a parameter type "
                    "'Instrument'",
                    value_constraint=CollectionConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="protocolType",
                                    constraint=CVTermEnumConstraint(
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_label="CHMO",
                                                cv_accession=" http://purl.obolibrary.org/obo/CHMO_0000470",
                                                name="mass spectrometry",
                                            ),
                                        ]
                                    ),
                                )
                            ]
                        ),
                        min_match=1,
                        max_match=1,
                        item_value_jsonpath_list=[".parameters[*]"],
                        item_value_match_constraint=StringEnumConstraint(
                            json_path="parameterName.annotationValue",
                            options=["Instrument"],
                        ),
                    ),
                ),
                FieldRequirement(
                    code="M-000403",
                    description="Mass spectrometry protocol MUST have a parameter type "
                    "'Scan polarity'",
                    value_constraint=CollectionConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="protocolType",
                                    constraint=CVTermEnumConstraint(
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_label="CHMO",
                                                cv_accession=" http://purl.obolibrary.org/obo/CHMO_0000470",
                                                name="mass spectrometry",
                                            ),
                                        ]
                                    ),
                                )
                            ]
                        ),
                        min_match=1,
                        max_match=1,
                        item_value_jsonpath_list=[".parameters[*]"],
                        item_value_match_constraint=StringEnumConstraint(
                            json_path="parameterName.annotationValue",
                            options=["Scan polarity"],
                        ),
                    ),
                ),
                FieldRequirement(
                    code="M-000404",
                    description="Mass spectrometry protocol MUST have a parameter type "
                    "'Scan m/z range'",
                    value_constraint=CollectionConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="protocolType",
                                    constraint=CVTermEnumConstraint(
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_label="CHMO",
                                                cv_accession=" http://purl.obolibrary.org/obo/CHMO_0000470",
                                                name="mass spectrometry",
                                            ),
                                        ]
                                    ),
                                )
                            ]
                        ),
                        min_match=1,
                        max_match=1,
                        item_value_jsonpath_list=[".parameters[*]"],
                        item_value_match_constraint=StringEnumConstraint(
                            json_path="parameterName.annotationValue",
                            options=["Scan m/z range"],
                        ),
                    ),
                ),
            ]
        ),
        "$.studies[0].protocols[*].name": FieldRequirement(
            code="M-000452",
            description="Protocol name MUST be equal or greater than 2 characters",
            value_constraint=StringConstraint(minimum=2),
        ),
        "$.studies[0].protocols[*].description": FieldRequirement(
            code="M-000453",
            description="Protocol description MUST be equal or greater "
            "than 40 characters",
            value_constraint=StringConstraint(minimum=40),
        ),
        "$.studies[0].protocols[*].protocolType": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="M-000454",
                    description="Protocol type value MUST be defined.",
                    required_properties=["annotationValue"],
                ),
                FieldRequirement(
                    code="M-000453",
                    enforcement_level=EnforcementLevel.RECOMMENDED,
                    description="Protocol type is not in the defined list: "
                    f"'{', '.join(str(x) for x in PREDEFINED_PROTOCOLS)}'",
                    value_constraint=CVTermEnumConstraint(
                        allowed_cv_terms=PREDEFINED_PROTOCOLS
                    ),
                ),
            ]
        ),
        "$.studies[0].protocols[*].protocolType.annotationValue": FieldRequirement(
            code="M-000455",
            description="Protocol type value MUST be equal or greater "
            "than 2 characters",
            value_constraint=StringConstraint(minimum=2),
        ),
        "$.studies[0].assays[*].technologyType": FieldRequirement(
            code="M-000500",
            enforcement_level=EnforcementLevel.RECOMMENDED,
            description="Assay technology type is not in the defined list: "
            f"'{', '.join(str(x) for x in PREDEFINED_TECHNOLOGY_TYPES)}'",
            value_constraint=CVTermEnumConstraint(
                allowed_cv_terms=PREDEFINED_TECHNOLOGY_TYPES
            ),
        ),
        "$.studies[0].assays[*].measurementType": FieldRequirement(
            code="M-000501",
            description="Assay technology type is not in the defined list: "
            f"'{', '.join(str(x) for x in PREDEFINED_MEASUREMENT_TYPES)}'",
            value_constraint=CVTermEnumConstraint(
                allowed_cv_terms=PREDEFINED_MEASUREMENT_TYPES
            ),
        ),
        "$.studies[0].assays[*].processSequence[*].parameterValues[*]": FieldRequirementGroup(  # noqa: E501
            requirements=[
                FieldRequirement(
                    code="M-000502",
                    description="Instrument parameter value MUST be child of "
                    "[MS, http://purl.obolibrary.org/obo/MS_1000031, instrument model]",
                    value_constraint=ParentCVTermConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=StringEnumConstraint(
                                        json_path="parameterName.annotationValue",
                                        options=["Instrument"],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        parent_cv_terms=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="http://purl.obolibrary.org/obo/MS_1000031",
                                name="instrument model",
                            )
                        ],
                    ),
                ),
                FieldRequirement(
                    code="M-000503",
                    description="Instrument parameter value MUST be child of "
                    "[MS, http://purl.obolibrary.org/obo/MS_1003776, "
                    "acquisition polarity]",
                    value_constraint=ParentCVTermConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path='category["@id"]',
                                    is_reference_field=True,
                                    constraint=StringEnumConstraint(
                                        json_path="parameterName.annotationValue",
                                        options=["Scan polarity"],
                                    ),
                                )
                            ]
                        ),
                        json_path="value",
                        parent_cv_terms=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="http://purl.obolibrary.org/obo/MS_1003776",
                                name="acquisition polarity",
                            ),
                        ],
                    ),
                ),
            ]
        ),
    },
)
