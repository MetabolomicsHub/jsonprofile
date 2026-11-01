from __future__ import annotations

from typing import Any

import pytest

from jsonprofile.validator.default import ols_cv_term_search


class FakeResponse:
    def __init__(self, status_code: int, body: dict[str, Any] | None = None) -> None:
        self.status_code = status_code
        self.body = body or {}

    def json(self) -> dict[str, Any]:
        return self.body


@pytest.fixture(autouse=True)
def clear_search_cache():
    ols_cv_term_search._search_ols.cache.clear()  # noqa: SLF001
    yield
    ols_cv_term_search._search_ols.cache.clear()  # noqa: SLF001


def _search() -> tuple[int, dict[str, Any]]:
    return ols_cv_term_search._search_ols(  # noqa: SLF001
        "https://www.ebi.ac.uk/ols4/api/search",
        {"q": "MS:1001"},
        {"Accept": "application/json"},
        timeout=10,
    )


def test_transient_http_status_is_not_cached(monkeypatch: pytest.MonkeyPatch) -> None:
    responses = [
        FakeResponse(503),
        FakeResponse(200, {"response": {"docs": []}}),
    ]
    calls = 0

    def fake_get(*args: Any, **kwargs: Any) -> FakeResponse:
        nonlocal calls
        response = responses[calls]
        calls += 1
        return response

    monkeypatch.setattr(ols_cv_term_search.httpx2, "get", fake_get)

    with pytest.raises(ols_cv_term_search._OlsSearchFailure):  # noqa: SLF001
        _search()
    assert _search() == (200, {"response": {"docs": []}})
    assert _search() == (200, {"response": {"docs": []}})
    assert calls == 2


def test_transport_error_is_not_cached(monkeypatch: pytest.MonkeyPatch) -> None:
    calls = 0

    def fake_get(*args: Any, **kwargs: Any) -> FakeResponse:
        nonlocal calls
        calls += 1
        if calls == 1:
            raise ConnectionError("temporary network failure")
        return FakeResponse(200, {"response": {"docs": []}})

    monkeypatch.setattr(ols_cv_term_search.httpx2, "get", fake_get)

    with pytest.raises(ConnectionError, match="temporary network failure"):
        _search()
    assert _search() == (200, {"response": {"docs": []}})
    assert calls == 2


def test_not_found_response_remains_cached(monkeypatch: pytest.MonkeyPatch) -> None:
    calls = 0

    def fake_get(*args: Any, **kwargs: Any) -> FakeResponse:
        nonlocal calls
        calls += 1
        return FakeResponse(404)

    monkeypatch.setattr(ols_cv_term_search.httpx2, "get", fake_get)

    assert _search() == (404, {})
    assert _search() == (404, {})
    assert calls == 1
