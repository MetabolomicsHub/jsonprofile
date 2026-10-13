import abc
import logging

logger = logging.getLogger(__name__)


class OpaEngine(abc.ABC):
    @abc.abstractmethod
    def evaluate(
        self,
        input_data: dict,
        entrypoint: None | str | int = None,
        data: None | dict = None,
    ) -> dict:
        """
        Evaluate physical input data against the loaded OPA WASM rules.

        Args:
            input_data: OPA input document.
            entrypoint: Optional OPA WASM entrypoint for this evaluation.
            data: Optional OPA data document for this evaluation. When omitted,
                bundle data loaded from data.json is restored if available.
        """


class OpaEngineFactory(abc.ABC):
    @abc.abstractmethod
    def get_opa_engine(
        self,
        wasm_file_path: str,
        wasm_file_download_url: None | str = None,
    ) -> None | OpaEngine: ...


class NotImplementedOpaEngine(OpaEngine):
    def evaluate(
        self,
        input_data: dict,
        entrypoint: None | str | int = None,
        data: None | dict = None,
    ) -> dict:
        raise NotImplementedError(
            "Install 'jsonprofile[opa]' to evaluate OPA requirements"
        )


class NotImplementedOpaEngineFactory(OpaEngineFactory):
    def get_opa_engine(
        self,
        wasm_file_path: str,
        wasm_file_download_url: None | str = None,
    ) -> None | OpaEngine:
        raise NotImplementedError(
            "Install 'jsonprofile[opa]' to evaluate OPA requirements"
        )
