import logging

from jsonprofile.profile.base import (
    BaseCvTerm,
)
from jsonprofile.profile.constraints import Evaluation, Precondition, constraints
from jsonprofile.profile.model import (
    FieldRequirement,
    FieldRequirementGroup,
    JsonProfile,
    JsonProfileConfiguration,
)
from scripts.example_base_profile import (
    ANNOUNCEMENT_FILE_V1_0_LEGACY_PROFILE_NAME,
    ANNOUNCEMENT_FILE_V1_0_MS_PROFILE_NAME,
    MHD_CV_TERM_MAPPING,
)

logger = logging.getLogger(__name__)

exceptional_values: list[BaseCvTerm] = [
    BaseCvTerm(
        cv_label="NCIT",
        cv_accession="NCIT:C48660",
        name="Not Applicable",
    ),
    BaseCvTerm(
        cv_label="NCIT",
        cv_accession="NCIT:C126101",
        name="Not Available",
    ),
    BaseCvTerm(
        cv_label="NCIT",
        cv_accession="NCIT:C150904",
        name="Masked Data",
    ),
]


def create_characteristic_value_constraint(
    key: BaseCvTerm,
    cv_list: list[str],
    required: bool = True,
    allow_missing_terms: bool = False,
    allow_exceptional_cv_list: bool = False,
):
    description = f"{key.name} value is required from {', '.join(cv_list)} ontologies "
    constraint = constraints.CollectionConstraint(
        description=description,
        precondition=Precondition(
            evaluations=[
                Evaluation(
                    json_path="key",
                    constraint=constraints.CVTermEnumConstraint(allowed_cv_terms=[key]),
                ),
            ]
        ),
        item_value_jsonpath_list=[".values[*]"],
        item_value_match_constraint=constraints.ConstraintGroup(
            join_operator="or",
            min_valid=1 if required else None,
            constraints=[
                constraints.StringEnumConstraint(json_path="source", options=cv_list),
            ],
        ),
    )
    group: constraints.ConstraintGroup = constraint.item_value_match_constraint
    if allow_exceptional_cv_list:
        group.constraints.append(
            constraints.StringEnumConstraint(
                json_path="source", options=["ILX", "wikidata"]
            )
        )
    if allow_missing_terms:
        group.constraints.append(
            constraints.CVTermEnumConstraint(
                allowed_cv_terms=[x.model_copy() for x in exceptional_values]
            )
        )
    return constraint


def create_parent_parameter_constraint(
    key: BaseCvTerm,
    parents: list[BaseCvTerm],
    required: bool = True,
    allow_missing_terms: bool = False,
    allow_exceptional_cv_list: bool = True,
):
    constraint = constraints.CollectionConstraint(
        precondition=Precondition(
            evaluations=[
                Evaluation(
                    json_path="key",
                    constraint=constraints.CVTermEnumConstraint(allowed_cv_terms=[key]),
                ),
            ]
        ),
        item_value_jsonpath_list=[".values[*]"],
        min_referenced_value_match=1 if required else None,
        item_value_match_constraint=constraints.ConstraintGroup(
            join_operator="or",
            min_valid=1,
            constraints=[constraints.ParentCVTermConstraint(parent_cv_terms=parents)],
        ),
    )
    group: constraints.ConstraintGroup = constraint.item_value_match_constraint
    if allow_exceptional_cv_list:
        group.constraints.append(
            constraints.StringEnumConstraint(
                json_path="source", options=["ILX", "wikidata"]
            )
        )
    if allow_missing_terms:
        group.constraints.append(
            constraints.CVTermEnumConstraint(
                allowed_cv_terms=[x.model_copy() for x in exceptional_values]
            )
        )
    return constraint


MS_ANNOUNCEMENT_FILE_PROFILE_v1_0 = JsonProfile(
    id=ANNOUNCEMENT_FILE_V1_0_MS_PROFILE_NAME,
    extends=ANNOUNCEMENT_FILE_V1_0_LEGACY_PROFILE_NAME,
    version="v1.0",
    name="MetabolomicsHub Announcement File v1.0 MS Profile",
    description="MetabolomicsHub Announcement File v1.0 MS Profile",
    configuration=JsonProfileConfiguration(cv_term_field_mapping=MHD_CV_TERM_MAPPING),
    requirements={
        "$": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-required-0001",
                    enforcement_level="required",
                    required_properties=[
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
                        "protocols",
                    ],
                    recommended_properties=[
                        "submitter_keywords",
                        "raw_data_file_list",
                        "result_file_list",
                        "reported_molecular_entities",
                        "principal_investigators",
                    ],
                ),
            ]
        ),
        "$.url_list": FieldRequirement(
            code="ms-url-list-0001",
            value_constraint=constraints.CollectionConstraint(min_occurs=1),
        ),
        "$.repository_name": FieldRequirement(
            code="ms-repository_name-0001",
            value_constraint=constraints.StringConstraint(minimum=1),
        ),
        "$.repository_short_name": FieldRequirement(
            code="ms-repository_short_name-0001",
            value_constraint=constraints.StringConstraint(minimum=1),
        ),
        "$.repository_identifier": FieldRequirement(
            code="ms-repository_identifier-0001",
            value_constraint=constraints.StringConstraint(minimum=1),
        ),
        "$.license_name": FieldRequirement(
            code="ms-license-name-0001",
            value_constraint=constraints.StringConstraint(minimum=1),
        ),
        "$.name": FieldRequirement(
            code="ms-name-0001",
            value_constraint=constraints.StringConstraint(minimum=0),
        ),
        "$.description": FieldRequirement(
            code="ms-description-0001",
            value_constraint=constraints.StringConstraint(),
        ),
        "$.submitters": FieldRequirement(
            code="ms-submitters-0001",
            value_constraint=constraints.CollectionConstraint(min_occurs=1),
        ),
        "$.submitters[*].email_list": FieldRequirement(
            code="ms-submitters_emails-0001",
            value_constraint=constraints.CollectionConstraint(min_occurs=1),
        ),
        "$.principal_investigators": FieldRequirement(
            code="ms-principal_investigators-0001",
            value_constraint=constraints.CollectionConstraint(min_occurs=1),
        ),
        "$.principal_investigators[*].email_list": FieldRequirement(
            code="ms-principal_investigators-email_list_0001",
            value_constraint=constraints.CollectionConstraint(min_occurs=1),
        ),
        "$.characteristic_values": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-characteristic_values-0001",
                    value_constraint=constraints.CollectionConstraint(
                        description="There must be a characteristic with type "
                        "[NCIT, NCIT:C14250, Organism] in characteristic_values",
                        item_value_jsonpath_list=[".[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="NCIT",
                                    cv_accession="NCIT:C14250",
                                    name="Organism",
                                )
                            ]
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0002",
                    value_constraint=constraints.CollectionConstraint(
                        description="There must be a characteristic named "
                        "[NCIT, NCIT:C103199, Organism Part] in characteristic_values",
                        item_value_jsonpath_list=[".[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="NCIT",
                                    cv_accession="NCIT:C103199",
                                    name="Organism Part",
                                )
                            ]
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0003",
                    value_constraint=constraints.CollectionConstraint(
                        description="There must be a characteristic with type "
                        "[EFO, EFO:0000324, cell type] in characteristic_values",
                        item_value_jsonpath_list=[".[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="EFO",
                                    cv_accession="EFO:0000324",
                                    name="cell type",
                                )
                            ]
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0004",
                    value_constraint=constraints.CollectionConstraint(
                        description="There must be a characteristic with type "
                        "[MONDO, MONDO:0000001, disease] in characteristic_values",
                        item_value_jsonpath_list=[".[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="MONDO",
                                    cv_accession="MONDO:0000001",
                                    name="disease",
                                )
                            ]
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
            ]
        ),
        "$.characteristic_values[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-characteristic_values-0010",
                    description="At least one organism characteristic "
                    "from NCBITAXON, ENVO and CHEBI is required.",
                    value_constraint=create_characteristic_value_constraint(
                        key=BaseCvTerm(
                            cv_label="NCIT",
                            cv_accession="NCIT:C14250",
                            name="Organism",
                        ),
                        cv_list=["NCBITAXON", "ENVO", "CHEBI"],
                        required=True,
                        allow_missing_terms=True,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0011",
                    description="At least one organism part characteristic "
                    "from BTO, ENVO and CHEBI is required.",
                    value_constraint=create_characteristic_value_constraint(
                        key=BaseCvTerm(
                            cv_label="NCIT",
                            cv_accession="NCIT:C103199",
                            name="Organism Part",
                        ),
                        cv_list=["UBERON", "BTO", "NCIT", "CHEBI"],
                        required=True,
                        allow_missing_terms=True,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0012",
                    description="At least one cell type characteristic "
                    "from CL, CLO ontologies is required.",
                    value_constraint=create_characteristic_value_constraint(
                        key=BaseCvTerm(
                            cv_label="EFO",
                            cv_accession="EFO:0000324",
                            name="cell type",
                        ),
                        cv_list=["CL", "CLO"],
                        required=False,
                        allow_missing_terms=True,
                    ),
                ),
                FieldRequirement(
                    code="ms-characteristic_values-0013",
                    description="At least one disease characteristic value "
                    "from MONDO, SNOMED ontologies is required.",
                    value_constraint=create_characteristic_value_constraint(
                        key=BaseCvTerm(
                            cv_label="MONDO",
                            cv_accession="MONDO:0000001",
                            name="disease",
                        ),
                        cv_list=["MONDO", "MP", "SNOMED", "ILX", "wikidata"],
                        required=True,
                        allow_missing_terms=True,
                    ),
                ),
            ]
        ),
        "$.protocols": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-protocols-0001",
                    value_constraint=constraints.CollectionConstraint(
                        description="There must be a protocol with type "
                        "[CHMO, CHMO:0000470, mass spectrometry] in protocols",
                        item_value_jsonpath_list=[".[*].protocol_type"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="CHMO",
                                    cv_accession="CHMO:0000470",
                                    name="mass spectrometry",
                                )
                            ],
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                )
            ]
        ),
        "$.protocols[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-protocols-0010",
                    description="mass spectrometer parameter must be defined "
                    "in mass spectrometry protocol",
                    value_constraint=constraints.CollectionConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="protocol_type",
                                    constraint=constraints.CVTermEnumConstraint(
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_label="CHMO",
                                                cv_accession="CHMO:0000470",
                                                name="mass spectrometry",
                                            )
                                        ]
                                    ),
                                ),
                            ]
                        ),
                        item_value_jsonpath_list=[".protocol_parameters[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="OBI",
                                    cv_accession="OBI:0000049",
                                    name="mass spectrometer",
                                )
                            ],
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
                FieldRequirement(
                    code="ms-protocols-0011",
                    description="acquisition polarity parameter must be "
                    "defined in mass spectrometry protocol",
                    value_constraint=constraints.CollectionConstraint(
                        precondition=Precondition(
                            evaluations=[
                                Evaluation(
                                    json_path="protocol_type",
                                    constraint=constraints.CVTermEnumConstraint(
                                        allowed_cv_terms=[
                                            BaseCvTerm(
                                                cv_label="CHMO",
                                                cv_accession="CHMO:0000470",
                                                name="mass spectrometry",
                                            )
                                        ]
                                    ),
                                ),
                            ]
                        ),
                        item_value_jsonpath_list=[".protocol_parameters[*].key"],
                        item_value_match_constraint=constraints.CVTermEnumConstraint(
                            allowed_cv_terms=[
                                BaseCvTerm(
                                    cv_label="MS",
                                    cv_accession="MS:1003776",
                                    name="acquisition polarity",
                                )
                            ],
                        ),
                        min_referenced_value_match=1,
                        max_referenced_value_match=1,
                    ),
                ),
            ],
        ),
        "$.protocols[*].protocol_parameters[*]": FieldRequirementGroup(
            requirements=[
                FieldRequirement(
                    code="ms-protocols-parameters-0010",
                    description="At least one mass spectrometer parameter value must "
                    "be defined and the values must be a child of "
                    "[MS, MS:1000031, instrument model] CV Term",
                    value_constraint=create_parent_parameter_constraint(
                        key=BaseCvTerm(
                            cv_label="OBI",
                            cv_accession="OBI:0000049",
                            name="mass spectrometer",
                        ),
                        parents=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="MS:1000031",
                                name="instrument model",
                            )
                        ],
                        required=True,
                        allow_missing_terms=False,
                    ),
                ),
                FieldRequirement(
                    code="ms-protocols-parameters-0020",
                    description="At least one acquisition polarity parameter value "
                    "must be defined and the values must be a child of "
                    "[MS, MS:1003776, acquisition polarity] CV Term",
                    value_constraint=create_parent_parameter_constraint(
                        key=BaseCvTerm(
                            cv_label="MS",
                            cv_accession="MS:1003776",
                            name="acquisition polarity",
                        ),
                        parents=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="MS:1003776",
                                name="acquisition polarity",
                            )
                        ],
                        required=True,
                        allow_missing_terms=False,
                    ),
                ),
                FieldRequirement(
                    code="ms-protocols-parameters-0030",
                    description="Ionization type parameter value "
                    "must be a child of "
                    "[MS, MS:1000008, ionization type] CV Term",
                    value_constraint=create_parent_parameter_constraint(
                        key=BaseCvTerm(
                            cv_label="MS",
                            cv_accession="MS:1000008",
                            name="ionization type",
                        ),
                        parents=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="MS:1000008",
                                name="ionization type",
                            )
                        ],
                        required=False,
                        allow_missing_terms=True,
                    ),
                ),
                FieldRequirement(
                    code="ms-protocols-parameters-0040",
                    description="Inlet parameter value "
                    "must be a child of "
                    "[MS, MS:1000007, inlet type] CV Term",
                    value_constraint=create_parent_parameter_constraint(
                        key=BaseCvTerm(
                            cv_label="MS",
                            cv_accession="MS:1000007",
                            name="inlet type",
                        ),
                        parents=[
                            BaseCvTerm(
                                cv_label="MS",
                                cv_accession="MS:1000007",
                                name="inlet type",
                            )
                        ],
                        required=False,
                        allow_missing_terms=True,
                    ),
                ),
            ],
        ),
        "$.reported_molecular_entities[*]": FieldRequirement(
            description="Molecular entity identifier must be a child of "
            "[EDAM, EDAM:data_2894, Compound accession]",
            code="ms-reported_molecular_entities-identifiers-0010",
            value_constraint=constraints.CollectionConstraint(
                item_value_jsonpath_list=[".database_identifiers[*]"],
                item_value_match_constraint=constraints.ConstraintGroup(
                    join_operator="or",
                    constraints=[
                        constraints.StringEnumConstraint(
                            json_path="source", options=["ILX", "wikidata"]
                        ),
                        constraints.ParentCVTermConstraint(
                            parent_cv_terms=[
                                BaseCvTerm(
                                    cv_label="EDAM",
                                    cv_accession="EDAM:data_2894",
                                    name="Compound accession",
                                )
                            ]
                        ),
                    ],
                ),
            ),
        ),
        "$.repository_metadata_file_list[*].format": FieldRequirement(
            description="Molecular entity identifier must be a child of "
            "[EDAM, EDAM:data_2894, Compound accession]",
            code="ms-reported_molecular_entities-identifiers-0010",
            value_constraint=constraints.CollectionConstraint(
                item_value_jsonpath_list=[".database_identifiers[*]"],
                item_value_match_constraint=constraints.ConstraintGroup(
                    join_operator="or",
                    constraints=[
                        constraints.StringEnumConstraint(
                            json_path="source", options=["ILX", "wikidata"]
                        ),
                        constraints.ParentCVTermConstraint(
                            parent_cv_terms=[
                                BaseCvTerm(
                                    cv_label="EDAM",
                                    cv_accession="EDAM:data_2894",
                                    name="Compound accession",
                                )
                            ]
                        ),
                    ],
                ),
            ),
        ),
    },
)
