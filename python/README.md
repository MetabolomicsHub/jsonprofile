# jsonprofile Python

Python implementation of jsonprofile.

Requires Python 3.10 or later. CI tests Python 3.10 through 3.14.

From this directory, run `uv sync --locked` to install dependencies and
`uv run pytest` to run the tests. The default development interpreter is
Python 3.10; use `uv run --python 3.14 pytest` to test another version.

Local OPA policy evaluation is optional. Install it with `pip install "jsonprofile[opa]"`
or use `uv sync --extra opa` when working from this directory. The validator
automatically uses the local OPA engine when the optional dependency is installed.
