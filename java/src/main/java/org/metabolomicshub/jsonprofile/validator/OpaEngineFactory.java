package org.metabolomicshub.jsonprofile.validator;

public interface OpaEngineFactory {
  OpaEngine getOpaEngine(String wasmFilePath, String wasmFileDownloadUrl);
}
