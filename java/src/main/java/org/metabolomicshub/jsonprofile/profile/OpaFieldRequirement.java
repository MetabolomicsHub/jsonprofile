package org.metabolomicshub.jsonprofile.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class OpaFieldRequirement extends EnforcedRequirement {
  @JsonProperty("wasm_file_key")
  private String wasmFileKey;

  @JsonProperty("policy_id")
  private String policyId;

  public OpaFieldRequirement() {}

  public String getWasmFileKey() {
    return wasmFileKey;
  }

  public void setWasmFileKey(String wasmFileKey) {
    this.wasmFileKey = wasmFileKey;
  }

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }
}
