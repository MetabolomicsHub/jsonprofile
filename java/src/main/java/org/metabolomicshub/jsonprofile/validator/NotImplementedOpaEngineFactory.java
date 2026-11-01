package org.metabolomicshub.jsonprofile.validator;

public class NotImplementedOpaEngineFactory implements OpaEngineFactory {
  @Override
  public OpaEngine getOpaEngine(String wasmFilePath, String wasmFileDownloadUrl) {
    throw new UnsupportedOperationException(
        "Configure an OpaEngineFactory (or enable optional OPA WASM runtime) "
            + "to evaluate OPA requirements");
  }
}
