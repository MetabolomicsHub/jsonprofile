package org.metabolomicshub.jsonprofile.validator;

import java.util.Map;

public class NotImplementedOpaEngine implements OpaEngine {
  @Override
  public Object evaluate(
      Map<String, Object> inputData, String entrypoint, Map<String, Object> data) {
    throw new UnsupportedOperationException(
        "Configure an OpaEngineFactory (or enable optional OPA WASM runtime) "
            + "to evaluate OPA requirements");
  }
}
