# jsonprofile Python

Python implementation of jsonprofile.

Local OPA policy evaluation is optional. Install it with `pip install "jsonprofile[opa]"`
or use `uv sync --extra opa` when working from this directory. The validator
automatically uses the local OPA engine when the optional dependency is installed.
