package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpaPolicyConstraint extends Constraint {
  @JsonProperty("wasm_file_key")
  private String wasmFileKey;

  @JsonProperty("policy_id")
  private String policyId;

  public OpaPolicyConstraint() {
    super("opa-policy");
  }

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
