package org.metabolomicshub.jsonprofile.profile.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CollectionConstraint extends Constraint {
  @JsonProperty("min_occurs")
  private Integer minOccurs;

  @JsonProperty("max_occurs")
  private Integer maxOccurs;

  @JsonProperty("item_value_jsonpath_list")
  private List<String> itemValueJsonpathList;

  @JsonProperty("item_value_match_constraint")
  private Constraint itemValueMatchConstraint;

  @JsonProperty("match_reference_values")
  private List<Object> matchReferenceValues;

  @JsonProperty("min_match")
  private Integer minMatch;

  @JsonProperty("max_match")
  private Integer maxMatch;

  @JsonProperty("min_referenced_value_match")
  private Integer minReferencedValueMatch;

  @JsonProperty("max_referenced_value_match")
  private Integer maxReferencedValueMatch;

  public CollectionConstraint() {
    super("items-count");
  }

  public Integer getMinOccurs() {
    return minOccurs;
  }

  public void setMinOccurs(Integer minOccurs) {
    this.minOccurs = minOccurs;
  }

  public Integer getMaxOccurs() {
    return maxOccurs;
  }

  public void setMaxOccurs(Integer maxOccurs) {
    this.maxOccurs = maxOccurs;
  }

  public List<String> getItemValueJsonpathList() {
    return itemValueJsonpathList;
  }

  public void setItemValueJsonpathList(List<String> itemValueJsonpathList) {
    this.itemValueJsonpathList = itemValueJsonpathList;
  }

  public Constraint getItemValueMatchConstraint() {
    return itemValueMatchConstraint;
  }

  public void setItemValueMatchConstraint(Constraint itemValueMatchConstraint) {
    this.itemValueMatchConstraint = itemValueMatchConstraint;
  }

  public List<Object> getMatchReferenceValues() {
    return matchReferenceValues;
  }

  public void setMatchReferenceValues(List<Object> matchReferenceValues) {
    this.matchReferenceValues = matchReferenceValues;
  }

  public Integer getMinMatch() {
    return minMatch;
  }

  public void setMinMatch(Integer minMatch) {
    this.minMatch = minMatch;
  }

  public Integer getMaxMatch() {
    return maxMatch;
  }

  public void setMaxMatch(Integer maxMatch) {
    this.maxMatch = maxMatch;
  }

  public Integer getMinReferencedValueMatch() {
    return minReferencedValueMatch;
  }

  public void setMinReferencedValueMatch(Integer minReferencedValueMatch) {
    this.minReferencedValueMatch = minReferencedValueMatch;
  }

  public Integer getMaxReferencedValueMatch() {
    return maxReferencedValueMatch;
  }

  public void setMaxReferencedValueMatch(Integer maxReferencedValueMatch) {
    this.maxReferencedValueMatch = maxReferencedValueMatch;
  }
}
