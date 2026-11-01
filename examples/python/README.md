# Python example

This example validates the shared mzTab-M sample document against the
MetaboLights profile using the Python implementation. It uses the same schema,
profile, input document, and referenced default profile as the Rust example.

From the repository root, run the example:

```sh
cd examples/python
uv sync
uv run python main.py
```

The script resolves shared resources relative to its own location, so it can
also be run from another working directory with the same command.
