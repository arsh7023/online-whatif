/*
 *
 */
package au.org.aurin.wif.model.allocation;

import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b>PlanneAdLU.java</b> :These land uses are used in allocation each existing
 * land use can be in one or more Planned land use.
 * 
 */
public class PlannedALU {

  /** The label of the field */
  private String label;

  /** The existing land use. */
  @JsonIgnore
  private Set<AllocationLU> associatedALUs;

  @JsonIgnore
  private Map<String, String> associatedALUsMap;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

  /**
   * Gets the wif project.
   * 
   * @return the wif project
   */
  public WifProject getWifProject() {
    return this.wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the AssociatedALUs land uses.
   * 
   * @return the plannedLUs
   */
  public Set<AllocationLU> getAssociatedALUs() {
    return associatedALUs;
  }

  /**
   * Sets the existing land uses.
   * 
   * @param existingLUs
   *          the new existing land uses
   */
  public void setAssociatedALUs(Set<AllocationLU> existingLUs) {
    this.associatedALUs = existingLUs;
  }

  /**
   * Adds the existing lu.
   * 
   * @param elu
   *          the elu
   */
  public void addAssociatedALU(AllocationLU elu) {
    this.associatedALUs.add(elu);
  }

  /**
   * Gets the label
   * 
   * @return the label
   */

  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * 
   * @param label
   *          the label
   */

  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the associated al us map.
   * 
   * @return the associated al us map
   */
  @JsonProperty(value = "associatedALUs")
  public Map<String, String> getAssociatedALUsMap() {
    return associatedALUsMap;
  }

  /**
   * Sets the associated al us map.
   * 
   * @param associatedALUsMap
   *          the associated al us map
   */
  @JsonProperty(value = "associatedALUs")
  public void setAssociatedALUsMap(Map<String, String> associatedALUsMap) {
    this.associatedALUsMap = associatedALUsMap;
  }

  /**
   * Instantiates a new suitability lu.
   */
  // public PlannedALU() {
  // super();
  // this.associatedALUs = new HashSet<AllocationLU>();
  // associatedALUsMap = new HashMap<String, String>();
  //
  // }

}
