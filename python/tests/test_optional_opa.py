import subprocess
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor

import pytest

from jsonprofile.validator import json_validator


def test_validator_imports_and_initializes_without_opa():
    result = subprocess.run(
        [
            sys.executable,
            "-c",
            """
import sys
import importlib.abc

class BlockOpa(importlib.abc.MetaPathFinder):
    def find_spec(self, fullname, path=None, target=None):
        if fullname == "opa_wasmtime":
            raise ModuleNotFoundError("OPA is not installed", name=fullname)

sys.meta_path.insert(0, BlockOpa())
from jsonprofile.validator import JsonValidator
from jsonprofile.profile.model import JsonProfile
from jsonprofile.validator.opa_engine import NotImplementedOpaEngineFactory

validator = JsonValidator(
    json_schema={"type": "object"},
    profile=JsonProfile(id="test", version="1.0", name="Test", requirements={}),
)
assert isinstance(validator.opa_engine_factory, NotImplementedOpaEngineFactory)
try:
    validator.opa_engine_factory.get_opa_engine("policy.wasm")
except NotImplementedError as exc:
    assert "jsonprofile[opa]" in str(exc)
else:
    raise AssertionError("OPA use must fail with installation instructions")
""",
        ],
        capture_output=True,
        text=True,
        check=False,
    )
    assert result.returncode == 0, result.stderr


def test_default_factory_uses_local_opa_when_installed(monkeypatch):
    pytest.importorskip("opa_wasmtime")
    from jsonprofile.validator.local_opa_engine import LocalOpaEngineFactory

    monkeypatch.setattr(json_validator, "_default_opa_engine_factory", None)
    factory = json_validator.get_default_opa_engine_factory()
    assert isinstance(factory, LocalOpaEngineFactory)
    assert json_validator.get_default_opa_engine_factory() is factory


def test_local_factory_creates_and_caches_local_engine(monkeypatch, tmp_path):
    pytest.importorskip("opa_wasmtime")
    from jsonprofile.validator import local_opa_engine

    wasm_path = tmp_path / "policy.wasm"
    wasm_path.touch()
    calls = []
    engine = object()

    def create_engine(wasm_path):
        calls.append(wasm_path)
        return engine

    monkeypatch.setattr(local_opa_engine, "LocalOpaEngine", create_engine)
    factory = local_opa_engine.LocalOpaEngineFactory()
    assert factory.get_opa_engine(str(wasm_path)) is engine
    assert factory.get_opa_engine(str(wasm_path)) is engine
    assert calls == [str(wasm_path)]


def test_local_engine_serializes_policy_data_and_evaluation(monkeypatch, tmp_path):
    pytest.importorskip("opa_wasmtime")
    from jsonprofile.validator import local_opa_engine

    first_evaluation_entered = threading.Event()
    allow_first_evaluation_to_finish = threading.Event()
    second_data_set = threading.Event()

    class FakePolicy:
        entrypoints = {}

        def __init__(self, *_args, **_kwargs):
            self.data = None

        def set_data(self, data):
            self.data = data
            if data == {"owner": "second"}:
                second_data_set.set()

        def evaluate(self, input_data, **_kwargs):
            if input_data["id"] == "first":
                first_evaluation_entered.set()
                assert allow_first_evaluation_to_finish.wait(timeout=2)
            return [{"result": self.data}]

    monkeypatch.setattr(local_opa_engine, "OPAPolicy", FakePolicy)
    wasm_path = tmp_path / "policy.wasm"
    wasm_path.write_bytes(b"test wasm")
    engine = local_opa_engine.LocalOpaEngine(str(wasm_path))

    with ThreadPoolExecutor(max_workers=2) as executor:
        first = executor.submit(
            engine.evaluate, {"id": "first"}, data={"owner": "first"}
        )
        assert first_evaluation_entered.wait(timeout=2)
        second = executor.submit(
            engine.evaluate, {"id": "second"}, data={"owner": "second"}
        )
        second_was_serialized = not second_data_set.wait(timeout=0.1)
        allow_first_evaluation_to_finish.set()
        first_result = first.result(timeout=2)
        second_result = second.result(timeout=2)

    assert second_was_serialized
    assert first_result == {"owner": "first"}
    assert second_result == {"owner": "second"}


def test_local_factory_creates_one_engine_for_concurrent_requests(
    monkeypatch, tmp_path
):
    pytest.importorskip("opa_wasmtime")
    from jsonprofile.validator import local_opa_engine

    wasm_path = tmp_path / "policy.wasm"
    wasm_path.touch()
    calls = []
    engine = object()

    def create_engine(wasm_path):
        calls.append(wasm_path)
        time.sleep(0.02)
        return engine

    monkeypatch.setattr(local_opa_engine, "LocalOpaEngine", create_engine)
    factory = local_opa_engine.LocalOpaEngineFactory()
    with ThreadPoolExecutor(max_workers=4) as executor:
        engines = list(
            executor.map(lambda _: factory.get_opa_engine(str(wasm_path)), range(4))
        )

    assert engines == [engine] * 4
    assert len(calls) == 1
