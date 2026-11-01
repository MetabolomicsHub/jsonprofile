import logging
import time
from collections.abc import Mapping
from pathlib import Path
from typing import Annotated, Any, Dict, List, Optional, Tuple, Union

import jsonpath_ng
import jsonschema_rs
import orjson
from jsonschema_rs import ValidationError
from pydantic import Field

from jsonprofile.profile.base import (
    BaseCvTerm,
    Category,
    EnforcementLevel,
    JsonPath,
    JsonProfileMessage,
    ValidationRuntimeConfiguration,
)
from jsonprofile.profile.constraints.constraints import (
    BaseCvTermConstraint,
    BooleanConstraint,
    CollectionConstraint,
    Constraint,
    ConstraintGroup,
    CustomConstraint,
    CVListConstraint,
    CVTermConstraint,
    CVTermEnumConstraint,
    CVTermValueConstraint,
    DateTimeConstraint,
    DecimalConstraint,
    IntegerConstraint,
    IntegerEnumConstraint,
    OpaPolicyConstraint,
    ParentCVTermConstraint,
    RegexConstraint,
    StringConstraint,
    StringEnumConstraint,
    UriConstraint,
)
from jsonprofile.profile.model import (
    FieldRequirement,
    FieldRequirementGroup,
    JsonProfile,
    JsonProfileConfiguration,
    OpaFieldRequirement,
)
from jsonprofile.profile.profile_validator import validate_profile
from jsonprofile.utils import convert_full_path, to_jsonpath
from jsonprofile.validator.base import CvTermSearch, ProfileValidatorFactory
from jsonprofile.validator.checkers.default_checker import (
    OpaPolicyInput,
    OpaPolicyMessagesOutput,
)
from jsonprofile.validator.context import (
    JsonProfileRunContext,
    JsonValidationResult,
    MessageCollector,
)
from jsonprofile.validator.default.ols_cv_term_search import OlsCvTermSearch
from jsonprofile.validator.default.profile_validator_factory import (
    DefaultProfileValidatorFactory,
)
from jsonprofile.validator.opa_engine import (
    NotImplementedOpaEngineFactory,
    OpaEngineFactory,
)

logger = logging.getLogger(__name__)

_default_opa_engine_factory: None | OpaEngineFactory = None


def get_default_opa_engine_factory() -> OpaEngineFactory:
    global _default_opa_engine_factory
    if _default_opa_engine_factory is None:
        try:
            from jsonprofile.validator.local_opa_engine import LocalOpaEngineFactory
        except ModuleNotFoundError as ex:
            if ex.name != "opa_wasmtime":
                raise
            _default_opa_engine_factory = NotImplementedOpaEngineFactory()
        else:
            _default_opa_engine_factory = LocalOpaEngineFactory()
    return _default_opa_engine_factory


class JsonValidator:
    def __init__(
        self,
        json_schema: Annotated[
            None | str | Path | dict,
            Field(description="Json schema file path for the profile."),
        ],
        profile: Annotated[
            None | str | Path | dict | JsonProfile,
            Field(
                description="Json profile file path or dictionary."
                " If it is not defined, default profile will be used."
            ),
        ],
        referenced_profiles: Annotated[
            None
            | dict[
                str,
                Union[str | Path, dict | JsonProfile],
            ],
            Field(
                description="Profile ids referenced (`extends`, etc.) in the profile "
                "and their sources."
            ),
        ] = None,
        default_cv_term_search: Annotated[
            None | CvTermSearch,
            Field(description="Default cv term search implementation "),
        ] = None,
        opa_engine_factory: Annotated[
            None | OpaEngineFactory,
            Field(description="Default cv term search implementation "),
        ] = None,
    ):

        logger.debug("Json schema will be loaded.")
        self.json_schema = None
        self.schema_validator = None
        if json_schema is not None:
            self.json_schema = self.load_jsonschema(json_schema)
            self.validate_jsonschema(self.json_schema)
            logger.debug("Json schema validated.")
            self.schema_validator = jsonschema_rs.validator_for(self.json_schema)
        else:
            logger.warning(
                "Json schema is not provided. "
                "Validator will skip json schema validation: %s",
                profile,
            )

        self.referenced_profiles = (
            referenced_profiles.copy() if referenced_profiles is not None else {}
        )
        self.json_profile = self.create_profile(profile)
        self.default_cv_term_search = default_cv_term_search
        if not default_cv_term_search:
            self.default_cv_term_search = OlsCvTermSearch()

        if not isinstance(self.json_profile, JsonProfile):
            raise ValueError("Profile is not valid")

        if not self.json_profile.configuration:
            self.json_profile.configuration = JsonProfileConfiguration()
        config = self.json_profile.configuration

        custom_validator_definitions = None
        self.default_profile_validator_id = None

        custom_validator_definitions = config.custom_validator_definitions or []
        labels = {x.label: x for x in custom_validator_definitions}
        default_label = config.default_validator_key
        default_validator_definition = labels.get(default_label)
        self.default_profile_validator_id = (
            default_validator_definition.validator_id
            if default_validator_definition
            else None
        )
        self.opa_engine_factory = opa_engine_factory
        if not opa_engine_factory:
            self.opa_engine_factory = get_default_opa_engine_factory()

        profile_validator_factory_class = ".".join(
            [
                DefaultProfileValidatorFactory.__module__,
                DefaultProfileValidatorFactory.__name__,
            ]
        )
        kwargs = {}
        if config.profile_validator_factory_class:
            kwargs = config.profile_validator_factory_class_arguments or {}
            config.profile_validator_factory_class = (
                config.profile_validator_factory_class or {}
            )
            if config.profile_validator_factory_class.get("python"):
                profile_validator_factory_class = (
                    config.profile_validator_factory_class.get("python")
                )

        self.profile_validator_factory = (
            ProfileValidatorFactory.get_profile_validator_factory(
                profile_validator_factory_class,
                custom_validator_definitions=custom_validator_definitions,
                default_profile_validator_id=self.default_profile_validator_id,
                opa_engine_factory=self.opa_engine_factory,
                **kwargs,
            )
        )
        self.json_path_expressions: dict[JsonPath, Any] = {}

    def load_jsonschema(self, json_schema: str | Path | dict) -> dict:
        if isinstance(json_schema, str):
            json_schema = Path(json_schema)
        target_schema = None
        if isinstance(json_schema, Path):
            if not json_schema.exists():
                raise ValueError(f"Json schema ({json_schema}) file does not exist.")
            target_schema = orjson.loads(json_schema.read_bytes())
        elif isinstance(json_schema, dict):
            target_schema = json_schema
        if not target_schema or not isinstance(target_schema, dict):
            raise ValueError(
                "Json schema load failure. Please provide schema file path or dict"
            )
        return target_schema

    def create_profile(
        self,
        profile: Annotated[
            str | Path | dict | JsonProfile,
            Field(
                description="Json profile file path or dictionary."
                " If it is not defined, default profile will be used."
            ),
        ],
    ) -> JsonProfile:
        if isinstance(profile, str):
            profile = Path(profile)
        target_profile = None
        if isinstance(profile, Path):
            if not profile.exists():
                raise ValueError(f"Json profile ({profile}) file does not exist.")
            profile_json = orjson.loads(profile.read_bytes())
            target_profile = JsonProfile.model_validate(profile_json, by_alias=True)
        elif isinstance(profile, dict):
            target_profile = JsonProfile.model_validate(profile, by_alias=True)
        if isinstance(profile, JsonProfile):
            target_profile = profile
        if not target_profile or not isinstance(target_profile, JsonProfile):
            raise ValueError(
                "Json profile load failure. "
                "Please provide profile file path, dict or JsonProfile object"
            )
        if target_profile.extends:
            if target_profile.extends not in self.referenced_profiles:
                raise ValueError(
                    f"Referenced Json profile {target_profile.extends} not found. "
                )
            referenced_profile_source = self.referenced_profiles[target_profile.extends]
            referenced_profile = self.create_profile(referenced_profile_source)
            self.referenced_profiles[referenced_profile.id] = referenced_profile

            merged_profile = self.extend_profile(referenced_profile, target_profile)
            return merged_profile

        self.referenced_profiles[target_profile.id] = target_profile
        success, messages = validate_profile(target_profile)
        if not success and messages:
            for item in messages:
                logger.warning(
                    "%s: %s-%s: %s",
                    item.category.name,
                    item.source,
                    item.code,
                    item.message,
                )
        return target_profile

    def validate_json_file(
        self,
        json_file_path: str | Path,
        runtime_config: None | ValidationRuntimeConfiguration = None,
    ) -> JsonValidationResult:
        if isinstance(json_file_path, str):
            json_file_path = Path(json_file_path)

        if not json_file_path.exists():
            raise ValueError("Json file does not exist.")

        if not runtime_config:
            runtime_config = ValidationRuntimeConfiguration()

        input_json = orjson.loads(json_file_path.read_bytes())

        return self.validate_dict(input_json, runtime_config=runtime_config)

    @staticmethod
    def extend_profile(parent_profile: None | JsonProfile, input_profile: JsonProfile):
        if not parent_profile:
            if not input_profile.configuration:
                input_profile.configuration = JsonProfileConfiguration(
                    wasm_file_definitions={}
                )
            logger.info(
                "Profile %s does not extend any parent profile.",
                input_profile.name,
            )
            return input_profile
        logger.info(
            "Profile '%s' extends '%s' profile. "
            "Updating configuration and requirements.",
            input_profile.name,
            parent_profile.name,
        )
        new_requirements = input_profile.requirements or {}
        input_profile.requirements = dict(parent_profile.requirements or {})

        if not input_profile.configuration:
            input_profile.configuration = (
                parent_profile.configuration or JsonProfileConfiguration()
            )

        # set configuration fields from parent if they are not defined.
        config = input_profile.configuration
        parent_config = parent_profile.configuration or JsonProfileConfiguration()
        for field, _ in config.__class__.model_fields.items():
            val = getattr(config, field)
            if val is None:
                parent_val = getattr(parent_config, field)
                setattr(config, field, parent_val)
        merge_actions: list[str] = []

        for key, value in new_requirements.items():
            new_codes = JsonValidator.get_requirement_codes(value)
            if key in input_profile.requirements:
                old_codes = JsonValidator.get_requirement_codes(
                    input_profile.requirements[key]
                )
                if value is None:
                    merge_actions.append(f"Deleted: {key}[{old_codes}] -> {key}[]")
                    del input_profile.requirements[key]
                    logger.debug(
                        "Deleted: Dropped profile requirement(s) for '%s': %s",
                        key,
                        old_codes,
                    )
                    continue
                else:
                    merge_actions.append(
                        f"Overridden: {key}[{old_codes}] -> {key}[{new_codes}]"
                    )
                    logger.debug(
                        "Overridden: "
                        "Requirement updates for '%s'. old codes: %s, new: %s",
                        key,
                        old_codes,
                        new_codes,
                    )

            else:
                if value is None:
                    continue
                merge_actions.append(f"Added: {key}[] -> {key}[{new_codes}]")
                logger.debug(
                    "Added: New profile requirement(s) for '%s': %s ",
                    key,
                    new_codes,
                )
            input_profile.requirements[key] = value
        if merge_actions:
            logger.debug("Profile merge results:")
            for action in merge_actions:
                logger.debug(action)
        return input_profile

    @staticmethod
    def get_requirement_codes(value: FieldRequirement | FieldRequirementGroup):
        requirement_codes = []
        if value:
            if isinstance(value, FieldRequirement):
                requirement_codes = [value.code] if value.code else ""
            else:
                requirement_codes = [x.code for x in value.requirements if x.code]
        requirement_codes_str = ", ".join(requirement_codes)
        return requirement_codes_str

    @staticmethod
    def _iter_error_context(error):
        yield error
        context = getattr(getattr(error, "kind", None), "context", None)
        if context is None:
            return
        if isinstance(context, list):
            for item in context:
                if isinstance(item, list):
                    for nested_item in item:
                        yield from JsonValidator._iter_error_context(nested_item)
                else:
                    yield from JsonValidator._iter_error_context(item)

    @staticmethod
    def best_match(errors):
        def has_context(error) -> bool:
            return getattr(getattr(error, "kind", None), "context", None) is not None

        errors = [
            nested_error
            for error in errors
            for nested_error in JsonValidator._iter_error_context(error)
        ]
        if not errors:
            return None

        return max(
            errors,
            key=lambda e: (
                len(getattr(e, "instance_path", []) or []),
                not has_context(e),
                str(e.schema_path),
            ),
        )

    def validate_json_with_schema(
        self, json_data: dict, context: JsonProfileRunContext
    ) -> None:
        logging.debug("Json schema validation started.")
        start = time.perf_counter()

        for error in sorted(self.schema_validator.iter_errors(json_data), key=str):
            error = self.best_match([error]) or error
            json_path = to_jsonpath(error.instance_path)
            context.message_collector.add_message(
                json_path=json_path,
                message=JsonProfileMessage(
                    code="SCHEMA-0001",
                    source=json_path,
                    category=Category.SCHEMA,
                    name="jsonschema",
                    enforcement_level=EnforcementLevel.REQUIRED,
                    message=f"Json schema validation error: {error.message}",
                ),
            )
        end = time.perf_counter()
        logging.info("Json schema validation ended.")
        if end > start + 1:
            logger.warning(
                "Json data is validated with its source jsonschema in %.6f seconds",
                end - start,
            )

    def create_context(
        self,
        runtime_config: None | ValidationRuntimeConfiguration = None,
        unique_id_objects: None | dict[str, dict] = None,
    ) -> JsonProfileRunContext:
        runtime_config = runtime_config or ValidationRuntimeConfiguration()
        profile_config = self.json_profile.configuration or JsonProfileConfiguration()
        return JsonProfileRunContext(
            runtime_config=runtime_config or ValidationRuntimeConfiguration(),
            profile_config=profile_config,
            opa_engine_factory=self.opa_engine_factory,
            profile_validator_factory=self.profile_validator_factory,
            cv_term_search=self.default_cv_term_search,
            message_collector=MessageCollector(
                runtime_config.max_messages_for_each_requirement
            ),
            json_path_expressions=self.json_path_expressions,
            unique_id_objects=unique_id_objects,
        )

    def validate_jsonschema(
        self, json_schema: dict
    ) -> tuple[bool, None | JsonProfileMessage]:
        try:
            jsonschema_rs.meta.validate(json_schema)
        except ValidationError as ex:
            raise ValueError(f"Json schema is not valid. {ex.message}") from ex

    def find_all_constraints(
        self,
        requirement: FieldRequirement | FieldRequirementGroup,
        constraints: set[tuple[str, None | str]],
    ):
        if requirement is None:
            return
        if isinstance(requirement, FieldRequirement):
            if requirement.value_constraint:
                constraints.add(
                    (
                        requirement.value_constraint.type,
                        requirement.value_constraint.name or None,
                    )
                )
        elif isinstance(requirement, FieldRequirementGroup):
            for req in requirement.requirements:
                self.find_all_constraints(req, constraints)

    def update_unique_object_dict(
        self,
        object_path: str,
        object_item: Any,
        id_fields: list[str],
        objects: None | dict[str, dict] = None,
    ):
        if objects is None:
            objects = {}
        if isinstance(object_item, list):
            for idx, x in enumerate(object_item):
                subitem_path = object_path + f"[{idx}]"
                self.update_unique_object_dict(subitem_path, x, id_fields, objects)
        elif isinstance(object_item, dict):
            for field_name, val in object_item.items():
                item_path = f"{object_path}.{field_name}"
                if field_name in id_fields and len(object_item) > 1:
                    if not val:
                        logger.warning("%s value is not defined", item_path)
                    else:
                        if val in objects:
                            logger.warning(
                                "%s '%s' id is already defined.", item_path, val
                            )
                        else:
                            objects[val] = object_item
                if isinstance(val, dict):
                    self.update_unique_object_dict(item_path, val, id_fields, objects)
                elif isinstance(val, list):
                    for idx, x in enumerate(val):
                        subitem_path = item_path + f"[{idx}]"
                        self.update_unique_object_dict(
                            subitem_path, x, id_fields, objects
                        )
        return objects

    def validate_dict(
        self,
        input_json: dict,
        runtime_config: None | ValidationRuntimeConfiguration = None,
        process_reporting_threshold_in_secs: float = 5.0,
    ) -> JsonValidationResult:
        id_fields = self.json_profile.configuration.object_unique_id_fields
        if not runtime_config:
            runtime_config = ValidationRuntimeConfiguration()
        unique_id_objects = {}
        if id_fields:
            unique_id_objects = self.update_unique_object_dict(
                "$", input_json, id_fields=id_fields
            )
        runtime_config.skipped_requirements = runtime_config.skipped_requirements or []
        context = self.create_context(
            runtime_config=runtime_config, unique_id_objects=unique_id_objects
        )
        if not runtime_config.skip_jsonschema_validation and self.schema_validator:
            self.validate_json_with_schema(json_data=input_json, context=context)
        else:
            logger.info("Jsonschema validation is skipped")
        if runtime_config.skip_profile_validation:
            logger.info("Profile validation is skipped")
            return context.message_collector.process_messages()
        requirements_start = time.perf_counter()
        requirement_evaluation_times: dict[str, str] = {}
        for (
            json_path,
            requirement_definition,
        ) in self.json_profile.requirements.items():
            source_json_path = json_path
            requirement_evaluation_times[source_json_path] = 0
            json_path_eval_start = time.perf_counter()
            json_path = json_path or "$"
            if not requirement_definition:
                logger.debug(
                    "Skipping key. Field requirement is not defined for '%s'", json_path
                )
                continue

            if runtime_config.skip_decimal_validations:
                all_field_constraints: set[tuple[str, None | str]] = set()
                self.find_all_constraints(
                    requirement=requirement_definition,
                    constraints=all_field_constraints,
                )
                is_decimal = [x for x, _ in all_field_constraints if x == "decimal"]
                if len(all_field_constraints) == 1 and is_decimal:
                    logger.warning(
                        "Decimal validations are skipped for '%s'", json_path
                    )
            requirement_group = None
            if isinstance(
                requirement_definition, (FieldRequirement, OpaFieldRequirement)
            ):
                requirement_group = FieldRequirementGroup(
                    code=requirement_definition.code,
                    description=requirement_definition.description,
                    requirements=[requirement_definition],
                )
            elif isinstance(requirement_definition, FieldRequirementGroup):
                requirement_group = requirement_definition
            else:
                raise ValueError(
                    f"Invalid requirement type: {type(requirement_definition)}"
                )
            if not json_path:
                logger.info("Field key is not defined. $ will be used.")
                json_path = "$"

            start = time.perf_counter()

            group_message_collector: MessageCollector = MessageCollector()
            self.validate_requirement(
                field_requirement=requirement_group,
                json_path=json_path,
                input_json=input_json,
                context=context,
                group_message_collector=group_message_collector,
            )
            for k, v in group_message_collector.messages.items():
                for items in v.values():
                    for item in items:
                        context.message_collector.add_message(k, item)
            end = time.perf_counter()
            duration = end - start
            if duration >= process_reporting_threshold_in_secs:
                logger.warning(
                    "%s execution time: %.6f seconds",
                    requirement_definition.code,
                    (end - start),
                )
            json_path_eval_end = time.perf_counter()
            requirement_evaluation_times[source_json_path] = (
                json_path_eval_end - json_path_eval_start
            )

        requirements_end = time.perf_counter()
        logger.info(
            "Profile requirements are evaluated in %.6f seconds",
            requirements_end - requirements_start,
        )
        if logger.getEffectiveLevel() == logging.DEBUG:
            for json_path, elapsed_time in requirement_evaluation_times.items():
                logger.debug("Elapsed time: %s\t%0.6f sec.", json_path, elapsed_time)

        self.json_path_expressions.update(context.json_path_expressions)
        return context.message_collector.process_messages()

    def validate_opa_field_requirement(
        self,
        opa_field_requirements: list[OpaFieldRequirement],
        json_path: JsonPath,
        input_json: dict,
        context: JsonProfileRunContext,
        group_message_collector: MessageCollector,
    ):
        policy_ids = [x.policy_id for x in opa_field_requirements]
        policy_id_reqs = {x.policy_id: x for x in opa_field_requirements}

        wasm_file_key = opa_field_requirements[0].wasm_file_key

        config = context.profile_config
        label = wasm_file_key or "default"
        wasm_file_path = None
        wasm_file_download_url = None
        if config and config.wasm_file_definitions:
            opa_config = config.wasm_file_definitions.get(label)
            if not opa_config:
                raise ValueError("OPA policy file not found")
            wasm_file_path = opa_config.wasm_file_path
            wasm_file_download_url = opa_config.wasm_file_download_url
            entrypoint = opa_config.entrypoint
        engine = context.profile_validator_factory.opa_engine_factory.get_opa_engine(
            wasm_file_path=wasm_file_path,
            wasm_file_download_url=wasm_file_download_url,
        )
        sub_jsonpath_expr = context.json_path_expressions.get(json_path)
        if not sub_jsonpath_expr:
            sub_jsonpath_expr = jsonpath_ng.parse(json_path)
            context.json_path_expressions[json_path] = sub_jsonpath_expr

        sub_input_value = [a.value for a in sub_jsonpath_expr.find(input_json)]
        if len(sub_input_value) == 1:
            sub_input_value = sub_input_value[0]
        elif len(sub_input_value) == 0:
            sub_input_value = None
        opa_input = OpaPolicyInput(
            policy_ids=policy_ids,
            value=sub_input_value,
            root=input_json,
            config=config,
            constraint=None,
        )
        input_data = opa_input.model_dump(by_alias=True)

        start = time.perf_counter()
        result = engine.evaluate(input_data=input_data, entrypoint=entrypoint)
        end = time.perf_counter()
        logger.debug("Policy Engine Execution time: %.6f seconds", (end - start))
        if result is None:
            raise ValueError("OPA policy decision is not valid")
        if isinstance(result, dict):
            result = [result]
        if not isinstance(result, list):
            raise ValueError("OPA policy decision is not valid")
        if not result:
            raise ValueError(
                "OPA policy decision is empty. "
                f"Check input policy ids {', '.join(policy_ids)} "
                f"and entrypoint: {entrypoint or '<default>'}"
            )
        messages_output = OpaPolicyMessagesOutput.model_validate(result[0])

        for item in messages_output.messages or []:
            added = group_message_collector.add_message(
                json_path=json_path,
                message=JsonProfileMessage(
                    code=policy_id_reqs.get(item.policy_id).code,
                    category=Category.PROFILE,
                    name="opa-field-requirement",
                    source=item.source,
                    message=item.message,
                    enforcement_level=policy_id_reqs.get(
                        item.policy_id
                    ).enforcement_level,
                ),
            )
            if not added:
                break

    def validate_opa_field_requirements(
        self,
        field_requirement: OpaFieldRequirement | FieldRequirementGroup,
        json_path: JsonPath,
        input_json: dict,
        context: JsonProfileRunContext,
        group_message_collector: MessageCollector,
    ):
        if isinstance(field_requirement, FieldRequirementGroup):
            opa_field_requirements = [
                x
                for x in field_requirement.requirements
                if isinstance(x, OpaFieldRequirement)
            ]
        elif isinstance(field_requirement, OpaFieldRequirement):
            opa_field_requirements = [field_requirement]

        wasm_file_reqs: dict[str, list[OpaFieldRequirement]] = {}
        for opa_field in opa_field_requirements:
            if opa_field.code in context.runtime_config.skipped_requirements:
                logger.warning(
                    "%s for %s is in skipped list.", opa_field.code, json_path
                )
                constraint_name = (
                    opa_field.policy_id if opa_field.policy_id else "field-requirement"
                )
                group_message_collector.add_message(
                    json_path=json_path,
                    message=JsonProfileMessage(
                        code=opa_field.code,
                        source=json_path,
                        category=Category.PROFILE,
                        name=constraint_name,
                        enforcement_level=EnforcementLevel.RECOMMENDED,
                        message=f"{opa_field.code} of '{json_path}' "
                        "is in skipped list.",
                    ),
                )
                continue
            if opa_field.wasm_file_key not in wasm_file_reqs:
                wasm_file_reqs[opa_field.wasm_file_key] = []
            wasm_file_reqs[opa_field.wasm_file_key].append(opa_field)
        for opa_fields in wasm_file_reqs.values():
            if opa_fields:
                start = time.perf_counter()

                self.validate_opa_field_requirement(
                    opa_field_requirements=opa_fields,
                    json_path=json_path,
                    input_json=input_json,
                    context=context,
                    group_message_collector=group_message_collector,
                )

                end = time.perf_counter()
                duration = end - start
                if duration > 0.5:
                    logger.warning(
                        "OPA policy requirement(s) execution time for %s: %.6f seconds",
                        ", ".join([x.code for x in opa_fields]),
                        (end - start),
                    )

    def validate_requirement(
        self,
        field_requirement: FieldRequirement
        | OpaFieldRequirement
        | FieldRequirementGroup,
        json_path: JsonPath,
        input_json: dict,
        context: JsonProfileRunContext,
        group_message_collector: MessageCollector,
    ):
        if isinstance(field_requirement, OpaFieldRequirement):
            valid = self.validate_opa_field_requirement(
                opa_field_requirements=[field_requirement],
                json_path=json_path,
                input_json=input_json,
                context=context,
                group_message_collector=group_message_collector,
            )
            return valid
        if isinstance(field_requirement, FieldRequirementGroup):
            sub_message_collector: MessageCollector = MessageCollector()

            self.validate_opa_field_requirements(
                field_requirement=field_requirement,
                json_path=json_path,
                input_json=input_json,
                context=context,
                group_message_collector=sub_message_collector,
            )
            for item in field_requirement.requirements:
                if isinstance(item, OpaFieldRequirement):
                    continue
                valid = self.validate_requirement(
                    field_requirement=item,
                    json_path=json_path,
                    input_json=input_json,
                    context=context,
                    group_message_collector=sub_message_collector,
                )
            instances = {}
            for (
                source_json_path,
                messages_dict,
            ) in sub_message_collector.messages.items():
                if source_json_path not in instances:
                    instances[source_json_path] = {}
                for code, messages in messages_dict.items():
                    items = [
                        x
                        for x in messages
                        if x.enforcement_level == EnforcementLevel.REQUIRED
                    ]
                    if items:
                        instances[source_json_path][code] = items
                    items = [
                        x
                        for x in messages
                        if x.enforcement_level != EnforcementLevel.REQUIRED
                    ]

                    for item in items or []:
                        group_message_collector.add_message(item.source, item)

            valid_requirements = {}
            total_reqs = len(field_requirement.requirements)
            for source, message_list in instances.items():
                valid_reqs = total_reqs - len(message_list)
                valid_requirements[source] = valid_reqs

                valid_requirement_group = total_reqs == valid_reqs
                if not valid_requirement_group and (
                    field_requirement.min_valid is not None
                    or field_requirement.max_valid is not None
                ):
                    min_bound = (
                        field_requirement.min_valid
                        if field_requirement.min_valid is not None
                        else 1
                    )
                    max_bound = (
                        field_requirement.max_valid
                        if field_requirement.max_valid is not None
                        else total_reqs
                    )
                    if min_bound <= valid_reqs <= max_bound:
                        valid_requirement_group = True
                if not valid_requirement_group:
                    for items in message_list.values():
                        for message in items:
                            group_message_collector.add_message(message.source, message)

            return
        runtime_config = context.runtime_config

        constraint_name = (
            field_requirement.value_constraint.type
            if field_requirement.value_constraint
            else "field-requirement"
        )
        if field_requirement.value_constraint:
            if runtime_config and runtime_config.skip_decimal_validations:
                if isinstance(field_requirement.value_constraint, DecimalConstraint):
                    logger.warning("Decimal validations are skipped for %s", json_path)
                    group_message_collector.add_message(
                        json_path=json_path,
                        message=JsonProfileMessage(
                            code=field_requirement.code,
                            source=json_path,
                            category=Category.PROFILE,
                            name=field_requirement.value_constraint.type,
                            enforcement_level=EnforcementLevel.RECOMMENDED,
                            message=f"{field_requirement.code} decimal validations "
                            f"are skipped for '{json_path}'",
                        ),
                    )
                    return False
        jsonpath_expr = context.json_path_expressions.get(json_path)
        if not jsonpath_expr:
            jsonpath_expr = jsonpath_ng.parse(json_path)
            context.json_path_expressions[json_path] = jsonpath_expr

        matches = jsonpath_expr.find(input_json)
        if not matches:
            if field_requirement.match_is_required:
                group_message_collector.add_message(
                    json_path=json_path,
                    message=JsonProfileMessage(
                        code=field_requirement.code,
                        source=json_path,
                        category=Category.PROFILE,
                        name=constraint_name,
                        enforcement_level=field_requirement.enforcement_level,
                        message=f"There is no value with json path '{json_path}'",
                    ),
                )
        else:
            for x in matches or []:
                if group_message_collector.is_open(field_requirement.code):
                    break
                source = convert_full_path(x.full_path)
                if (
                    field_requirement.required_properties
                    or field_requirement.recommended_properties
                ) and not isinstance(x.value, Mapping):
                    group_message_collector.add_message(
                        json_path=source,
                        message=JsonProfileMessage(
                            code=field_requirement.code,
                            source=source,
                            category=Category.PROFILE,
                            name="property-check",
                            enforcement_level=EnforcementLevel.REQUIRED,
                            message="Property check failed. "
                            f"'{source}' is not defined.",
                        ),
                    )

                else:
                    not_defined_fields = [
                        a
                        for a in field_requirement.required_properties or []
                        if a not in x.value
                    ]
                    if not_defined_fields:
                        fields = ", ".join(not_defined_fields)
                        group_message_collector.add_message(
                            json_path=source,
                            message=JsonProfileMessage(
                                code=field_requirement.code,
                                source=source,
                                category=Category.PROFILE,
                                name="property-check",
                                enforcement_level=EnforcementLevel.REQUIRED,
                                message="Required property does not exist "
                                f"{fields} is not in object '{source}'",
                            ),
                        )
                    not_defined_fields = [
                        a
                        for a in field_requirement.recommended_properties or []
                        if a not in x.value
                    ]

                    if not_defined_fields:
                        fields = ", ".join(not_defined_fields)
                        context.message_collector.add_message(
                            json_path=source,
                            message=JsonProfileMessage(
                                code=field_requirement.code,
                                source=source,
                                category=Category.PROFILE,
                                name="property-check",
                                enforcement_level=EnforcementLevel.RECOMMENDED,
                                message="Recommended property does not exist. "
                                f"{fields} field(s) not in object '{source}'",
                            ),
                        )

                if field_requirement.value_constraint:
                    constraint = field_requirement.value_constraint
                    checker = self.profile_validator_factory.get_checker(constraint)
                    skip = False
                    if runtime_config and runtime_config.skip_decimal_validations:
                        if isinstance(constraint, DecimalConstraint):
                            skip = True

                    if not skip:
                        if not field_requirement.value_constraint.description:
                            field_requirement.value_constraint.description = (
                                field_requirement.description
                            )
                        res = checker.validate_constraint(
                            constraint=field_requirement.value_constraint,
                            value=x.value,
                            root=input_json,
                            context=context,
                        )
                        if not res.is_valid:
                            group_message_collector.add_message(
                                json_path=source,
                                message=JsonProfileMessage(
                                    code=field_requirement.code,
                                    source=source,
                                    category=Category.PROFILE,
                                    name=field_requirement.value_constraint.type
                                    or "field-requirement",
                                    enforcement_level=field_requirement.enforcement_level,
                                    message=res.message,
                                ),
                            )

    def create_markdown_file(
        self,
        output_file_path: Union[str, Path],
        profile: Optional[JsonProfile] = None,
    ) -> str:
        """Generate a Markdown summary for the profile and write it to disk."""
        target_path = Path(output_file_path)
        target_path.parent.mkdir(parents=True, exist_ok=True)
        markdown_content = self.to_markdown(profile=profile)
        target_path.write_text(markdown_content, encoding="utf-8")
        return markdown_content

    def to_markdown(self, profile: Optional[JsonProfile] = None) -> str:
        """Render a Markdown document summarizing general fields and requirements."""
        target_profile = profile or self.json_profile
        if not target_profile:
            raise ValueError("No JsonProfile available to summarize.")

        requirements_map = target_profile.requirements or {}
        leaf_entries: List[
            Tuple[
                str,
                Union[FieldRequirement, OpaFieldRequirement],
                Optional[FieldRequirementGroup],
            ]
        ] = []
        for raw_path, req_def in requirements_map.items():
            if req_def is None:
                continue
            display_path = raw_path if raw_path else "$"
            leaf_entries.extend(
                self._collect_leaf_requirements(display_path, req_def, None)
            )

        level_counts: Dict[str, int] = {
            EnforcementLevel.REQUIRED.value: 0,
            EnforcementLevel.RECOMMENDED.value: 0,
            EnforcementLevel.OPTIONAL.value: 0,
        }
        for _, leaf_req, _ in leaf_entries:
            lvl = (
                leaf_req.enforcement_level.value
                if leaf_req.enforcement_level
                else EnforcementLevel.REQUIRED.value
            )
            level_counts[lvl] = level_counts.get(lvl, 0) + 1

        lines: List[str] = [
            f"# {target_profile.name}",
            "",
        ]
        if target_profile.description:
            lines.extend([target_profile.description, ""])

        extends_str = f"`{target_profile.extends}`" if target_profile.extends else "-"
        desc_cell = self._escape_md_cell(target_profile.description or "-")
        lines.extend(
            [
                "## General Information",
                "",
                "| Field | Value |",
                "| :--- | :--- |",
                f"| **ID** | `{target_profile.id}` |",
                f"| **Name** | {self._escape_md_cell(target_profile.name)} |",
                f"| **Version** | `{target_profile.version}` |",
                f"| **Extends** | {extends_str} |",
                f"| **Description** | {desc_cell} |",
                f"| **JSONPath Targets** | {len(requirements_map)} |",
                f"| **Total Requirements** | {len(leaf_entries)} "
                f"(`required`: {level_counts.get('required', 0)}, "
                f"`recommended`: {level_counts.get('recommended', 0)}, "
                f"`optional`: {level_counts.get('optional', 0)}) |",
                "",
            ]
        )

        config = target_profile.configuration
        if config:
            lines.extend(self._format_configuration_markdown(config))

        lines.extend(
            [
                "## Requirements Summary",
                "",
                "| JSONPath | Code | Enforcement | Type / Constraint "
                "| Description | Definition Summary |",
                "| :--- | :--- | :--- | :--- | :--- | :--- |",
            ]
        )
        for display_path, leaf_req, parent_group in leaf_entries:
            code_str = f"`{leaf_req.code}`" if leaf_req.code else "-"
            level_str = (
                f"`{leaf_req.enforcement_level.value}`"
                if leaf_req.enforcement_level
                else "`required`"
            )
            req_type_str = self._get_requirement_type_label(leaf_req)
            desc_str = self._escape_md_cell(leaf_req.description or "-")
            summary_str = self._escape_md_cell(
                self._summarize_requirement_definition(leaf_req, parent_group)
            )
            lines.append(
                f"| `{self._escape_md_cell(display_path)}` | {code_str} | {level_str} "
                f"| `{req_type_str}` | {desc_str} | {summary_str} |"
            )
        lines.append("")

        lines.extend(["## Requirement Definitions by JSONPath", ""])
        for raw_path, req_def in requirements_map.items():
            if req_def is None:
                continue
            display_path = raw_path if raw_path else "$"
            lines.extend(
                self._format_requirement_section_markdown(display_path, req_def)
            )

        return "\n".join(lines).rstrip() + "\n"

    @classmethod
    def _collect_leaf_requirements(
        cls,
        json_path: str,
        req_def: Union[FieldRequirement, OpaFieldRequirement, FieldRequirementGroup],
        parent_group: Optional[FieldRequirementGroup] = None,
    ) -> List[
        Tuple[
            str,
            Union[FieldRequirement, OpaFieldRequirement],
            Optional[FieldRequirementGroup],
        ]
    ]:
        if isinstance(req_def, FieldRequirementGroup):
            results: List[
                Tuple[
                    str,
                    Union[FieldRequirement, OpaFieldRequirement],
                    Optional[FieldRequirementGroup],
                ]
            ] = []
            for sub_req in req_def.requirements:
                results.extend(
                    cls._collect_leaf_requirements(json_path, sub_req, req_def)
                )
            return results
        return [(json_path, req_def, parent_group)]

    @classmethod
    def _format_configuration_markdown(
        cls, config: JsonProfileConfiguration
    ) -> List[str]:
        rows: List[Tuple[str, str]] = []
        if config.cv_term_field_mapping:
            m = config.cv_term_field_mapping
            rows.append(
                (
                    "CV Term Field Mapping",
                    f"`label`: `{m.label_field}`, `accession`: `{m.accession_field}`, "
                    f"`name`: `{m.name_field}`, `value`: `{m.value_field}`",
                )
            )
        if config.object_unique_id_fields:
            fields_str = ", ".join(f"`{f}`" for f in config.object_unique_id_fields)
            rows.append(("Object Unique ID Fields", fields_str))
        if config.supported_cv_lists:
            cvs_str = ", ".join(f"`{c}`" for c in config.supported_cv_lists)
            rows.append(("Supported CV Lists", cvs_str))
        if config.default_validator_key:
            rows.append(("Default Validator Key", f"`{config.default_validator_key}`"))
        if config.custom_validator_definitions:
            defs_str = ", ".join(
                f"`{d.label}` (`{d.validator_id}`)"
                for d in config.custom_validator_definitions
            )
            rows.append(("Custom Validators", defs_str))
        if config.default_wasm_file_key:
            rows.append(("Default WASM Key", f"`{config.default_wasm_file_key}`"))
        if config.wasm_file_definitions:
            wasm_parts = [
                f"`{k}`: entrypoint `{v.entrypoint}`, path `{v.wasm_file_path}`"
                for k, v in config.wasm_file_definitions.items()
            ]
            rows.append(("OPA WASM Definitions", "; ".join(wasm_parts)))

        if not rows:
            return []

        lines = [
            "## Configuration",
            "",
            "| Setting | Value |",
            "| :--- | :--- |",
        ]
        for key, val in rows:
            lines.append(f"| **{key}** | {cls._escape_md_cell(val)} |")
        lines.append("")
        return lines

    @staticmethod
    def _get_requirement_type_label(
        req: Union[FieldRequirement, OpaFieldRequirement],
    ) -> str:
        if isinstance(req, OpaFieldRequirement):
            return "opa-field-requirement"
        if req.value_constraint:
            return req.value_constraint.type
        if req.required_properties or req.recommended_properties:
            return "property-check"
        if req.match_is_required:
            return "match-required"
        return "field-requirement"

    @classmethod
    def _summarize_requirement_definition(
        cls,
        req: Union[FieldRequirement, OpaFieldRequirement],
        parent_group: Optional[FieldRequirementGroup] = None,
    ) -> str:
        parts: List[str] = []
        if parent_group and (
            parent_group.min_valid is not None
            or parent_group.max_valid is not None
            or parent_group.code
        ):
            grp_meta: List[str] = []
            if parent_group.code:
                grp_meta.append(f"code={parent_group.code}")
            if parent_group.min_valid is not None:
                grp_meta.append(f"min_valid={parent_group.min_valid}")
            if parent_group.max_valid is not None:
                grp_meta.append(f"max_valid={parent_group.max_valid}")
            parts.append(f"group({', '.join(grp_meta)})")

        if isinstance(req, OpaFieldRequirement):
            parts.append(f"policy_id=`{req.policy_id}`")
            if req.wasm_file_key:
                parts.append(f"wasm_file_key=`{req.wasm_file_key}`")
            return "; ".join(parts)

        if req.match_is_required:
            parts.append("match_is_required=True")
        if req.required_properties:
            req_props = ", ".join(f"`{p}`" for p in req.required_properties)
            parts.append(f"required_properties=[{req_props}]")
        if req.recommended_properties:
            rec_props = ", ".join(f"`{p}`" for p in req.recommended_properties)
            parts.append(f"recommended_properties=[{rec_props}]")
        if req.value_constraint:
            parts.append(cls._summarize_constraint(req.value_constraint))

        return "; ".join(parts) if parts else "-"

    @classmethod
    def _summarize_constraint(cls, constraint: Constraint) -> str:
        details: List[str] = [f"type=`{constraint.type}`"]
        if constraint.name:
            details.append(f"name=`{constraint.name}`")
        if constraint.json_path:
            details.append(f"json_path=`{constraint.json_path}`")
        if constraint.is_reference_field:
            details.append("is_reference_field=True")
        if constraint.negated:
            details.append("negated=True")

        if isinstance(constraint, RegexConstraint):
            details.append(f"pattern=`{constraint.pattern}`")
            if constraint.case_sensitive is False:
                details.append("case_sensitive=False")
        elif isinstance(constraint, StringConstraint):
            if constraint.minimum is not None:
                details.append(f"min_length={constraint.minimum}")
            if constraint.maximum is not None:
                details.append(f"max_length={constraint.maximum}")
        elif isinstance(constraint, StringEnumConstraint):
            opts = (
                list(constraint.options.keys())
                if isinstance(constraint.options, dict)
                else list(constraint.options)
            )
            preview = ", ".join(f"`{x}`" for x in opts[:5])
            if len(opts) > 5:
                preview += f", ... (+{len(opts) - 5} more)"
            details.append(f"options=[{preview}]")
        elif isinstance(constraint, IntegerEnumConstraint):
            opts = [str(k) for k in constraint.options.keys()]
            details.append(f"options=[{', '.join(opts)}]")
        elif isinstance(constraint, IntegerConstraint):
            if constraint.minimum is not None:
                details.append(f"min={constraint.minimum}")
            if constraint.maximum is not None:
                details.append(f"max={constraint.maximum}")
        elif isinstance(constraint, DecimalConstraint):
            if constraint.minimum is not None:
                details.append(f"min={constraint.minimum}")
            if constraint.maximum is not None:
                details.append(f"max={constraint.maximum}")
            if constraint.min_scale is not None:
                details.append(f"min_scale={constraint.min_scale}")
            if constraint.max_scale is not None:
                details.append(f"max_scale={constraint.max_scale}")
        elif isinstance(constraint, BooleanConstraint):
            if constraint.exceptional_true_values:
                details.append(f"true_values={constraint.exceptional_true_values}")
            if constraint.exceptional_false_values:
                details.append(f"false_values={constraint.exceptional_false_values}")
        elif isinstance(constraint, DateTimeConstraint):
            if constraint.format:
                details.append(f"format=`{constraint.format}`")
        elif isinstance(constraint, UriConstraint):
            if constraint.allowed_schemes:
                details.append(f"schemes={constraint.allowed_schemes}")
        elif isinstance(constraint, CollectionConstraint):
            if constraint.min_occurs is not None:
                details.append(f"min_occurs={constraint.min_occurs}")
            if constraint.max_occurs is not None:
                details.append(f"max_occurs={constraint.max_occurs}")
            if constraint.min_match is not None:
                details.append(f"min_match={constraint.min_match}")
            if constraint.max_match is not None:
                details.append(f"max_match={constraint.max_match}")
            if constraint.item_value_jsonpath_list:
                details.append(f"item_paths={constraint.item_value_jsonpath_list}")
            if constraint.item_value_match_constraint:
                sub = cls._summarize_constraint(constraint.item_value_match_constraint)
                details.append(f"item_constraint=({sub})")
        elif isinstance(constraint, CVTermEnumConstraint):
            terms = [cls._format_cv_term(t) for t in constraint.allowed_cv_terms]
            details.append(f"allowed_cv_terms=[{', '.join(terms)}]")
            cls._append_cv_term_common_fields(constraint, details)
        elif isinstance(constraint, CVListConstraint):
            if constraint.allowed_cv_list:
                details.append(f"allowed_cv_list={constraint.allowed_cv_list}")
            cls._append_cv_term_common_fields(constraint, details)
        elif isinstance(constraint, ParentCVTermConstraint):
            if constraint.parent_cv_terms:
                parents = [cls._format_cv_term(t) for t in constraint.parent_cv_terms]
                details.append(f"parent_cv_terms=[{', '.join(parents)}]")
            if constraint.excluded_cv_terms:
                excluded = [
                    cls._format_cv_term(t) for t in constraint.excluded_cv_terms
                ]
                details.append(f"excluded_cv_terms=[{', '.join(excluded)}]")
            cls._append_cv_term_common_fields(constraint, details)
        elif isinstance(constraint, CVTermConstraint):
            cls._append_cv_term_common_fields(constraint, details)
        elif isinstance(constraint, CVTermValueConstraint):
            if constraint.key_cv_term:
                details.append(
                    f"key_cv_term={cls._format_cv_term(constraint.key_cv_term)}"
                )
            if constraint.value_constraint:
                sub = cls._summarize_constraint(constraint.value_constraint)
                details.append(f"value_constraint=({sub})")
        elif isinstance(constraint, OpaPolicyConstraint):
            details.append(f"policy_id=`{constraint.policy_id}`")
            if constraint.wasm_file_key:
                details.append(f"wasm_file_key=`{constraint.wasm_file_key}`")
        elif isinstance(constraint, CustomConstraint):
            details.append(f"custom_name=`{constraint.name}`")
        elif isinstance(constraint, ConstraintGroup):
            details.append(f"join_operator=`{constraint.join_operator}`")
            if constraint.min_valid is not None:
                details.append(f"min_valid={constraint.min_valid}")
            if constraint.max_valid is not None:
                details.append(f"max_valid={constraint.max_valid}")
            sub_types = [cls._summarize_constraint(c) for c in constraint.constraints]
            details.append(f"constraints=[{'; '.join(sub_types)}]")

        if not isinstance(constraint, BaseCvTermConstraint):
            if constraint.null_values is not None:
                details.append(f"null_values={constraint.null_values}")
            if constraint.exceptional_values is not None:
                details.append(f"exceptional_values={constraint.exceptional_values}")

        if constraint.precondition and constraint.precondition.evaluations:
            evals: List[str] = []
            for e in constraint.precondition.evaluations:
                eval_parts: List[str] = [f"path=`{e.json_path or '$'}`"]
                if e.is_reference_field:
                    eval_parts.append("is_reference_field=True")
                if e.root_value_evaluation:
                    eval_parts.append("root_value_evaluation=True")
                if e.negated:
                    eval_parts.append("negated=True")
                eval_parts.append(f"({cls._summarize_constraint(e.constraint)})")
                evals.append(", ".join(eval_parts))
            details.append(f"precondition=[{'; '.join(evals)}]")

        return ", ".join(details)

    @classmethod
    def _format_cv_term(cls, term: BaseCvTerm) -> str:
        parts: List[str] = []
        for field_name in ("cv_label", "cv_accession", "name", "value"):
            val = getattr(term, field_name, None)
            if val is not None and str(val).strip() != "":
                parts.append(f"{field_name}=`{str(val).strip()}`")
        return f"{{{', '.join(parts)}}}" if parts else "{}"

    @classmethod
    def _append_cv_term_common_fields(
        cls, constraint: CVTermConstraint, details: List[str]
    ) -> None:
        if constraint.allow_user_defined_terms is not None:
            details.append(
                f"allow_user_defined_terms={constraint.allow_user_defined_terms}"
            )
        if constraint.is_cv_term_value_required is not None:
            details.append(
                f"is_cv_term_value_required={constraint.is_cv_term_value_required}"
            )
        if constraint.allow_synonym is not None:
            details.append(f"allow_synonym={constraint.allow_synonym}")
        if constraint.exceptional_cv_list:
            details.append(f"exceptional_cv_list={constraint.exceptional_cv_list}")
        if constraint.exceptional_values:
            exc_terms = [
                cls._format_cv_term(t) if isinstance(t, BaseCvTerm) else str(t)
                for t in constraint.exceptional_values
                if t is not None
            ]
            details.append(f"exceptional_values=[{', '.join(exc_terms)}]")
        if constraint.null_values:
            details.append(f"null_values={constraint.null_values}")

    @classmethod
    def _format_requirement_section_markdown(
        cls,
        json_path: str,
        req_def: Union[FieldRequirement, OpaFieldRequirement, FieldRequirementGroup],
    ) -> List[str]:
        lines: List[str] = [f"### `{json_path}`", ""]
        if isinstance(req_def, FieldRequirementGroup):
            group_meta: List[str] = [
                f"**Requirement Group** ({len(req_def.requirements)} requirement(s))"
            ]
            if req_def.code:
                group_meta.append(f"code: `{req_def.code}`")
            if req_def.min_valid is not None:
                group_meta.append(f"min_valid: `{req_def.min_valid}`")
            if req_def.max_valid is not None:
                group_meta.append(f"max_valid: `{req_def.max_valid}`")
            lines.append("- " + ", ".join(group_meta))
            if req_def.description:
                lines.append(f"- **Group Description**: {req_def.description}")
            lines.append("")

        leaf_items = cls._collect_leaf_requirements(json_path, req_def, None)
        for idx, (_, item, _) in enumerate(leaf_items, start=1):
            code_label = item.code or f"Requirement #{idx}"
            lvl_label = (
                item.enforcement_level.value
                if item.enforcement_level
                else EnforcementLevel.REQUIRED.value
            )
            type_label = cls._get_requirement_type_label(item)
            lines.append(f"#### `{code_label}` (`{lvl_label}` — `{type_label}`)")
            lines.append("")
            lines.append(f"- **Description**: {item.description or '-'}")
            lines.append(
                f"- **Summary**: {cls._summarize_requirement_definition(item)}"
            )
            serialized = orjson.dumps(
                item.model_dump(by_alias=True, exclude_none=True),
                option=orjson.OPT_INDENT_2 | orjson.OPT_NON_STR_KEYS,
            ).decode("utf-8")
            lines.extend(
                [
                    "",
                    "<details>",
                    "<summary>Requirement Definition (JSON)</summary>",
                    "",
                    "```json",
                    serialized,
                    "```",
                    "",
                    "</details>",
                    "",
                ]
            )
        return lines

    @staticmethod
    def _escape_md_cell(text: str) -> str:
        return text.replace("|", "\\|").replace("\n", " ").strip()
