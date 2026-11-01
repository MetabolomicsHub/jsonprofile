package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WasmFileDefinition {
  @JsonProperty("wasm_file_download_url")
  private String wasmFileDownloadUrl;

  @JsonProperty("wasm_file_path")
  private String wasmFilePath;

  @JsonProperty("entrypoint")
  private String entrypoint;

  public WasmFileDefinition() {}

  public WasmFileDefinition(String wasmFileDownloadUrl, String wasmFilePath, String entrypoint) {
    this.wasmFileDownloadUrl = wasmFileDownloadUrl;
    this.wasmFilePath = wasmFilePath;
    this.entrypoint = entrypoint;
  }

  public String getWasmFileDownloadUrl() {
    return wasmFileDownloadUrl;
  }

  public void setWasmFileDownloadUrl(String wasmFileDownloadUrl) {
    this.wasmFileDownloadUrl = wasmFileDownloadUrl;
  }

  public String getWasmFilePath() {
    return wasmFilePath;
  }

  public void setWasmFilePath(String wasmFilePath) {
    this.wasmFilePath = wasmFilePath;
  }

  public String getEntrypoint() {
    return entrypoint;
  }

  public void setEntrypoint(String entrypoint) {
    this.entrypoint = entrypoint;
  }
}
