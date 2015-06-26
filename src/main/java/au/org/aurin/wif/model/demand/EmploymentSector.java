/**
 * 
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class EmploymentSector.
 * 
 * @author marcosnr
 */
public class EmploymentSector {

  /** The wif project. @uml.property name="the wif project" */
  @JsonIgnore
  private WifProject wifProject;

  /** The code. */
  private String code;

  /** The label. */
  private String label;

  /** The feature field name. */
  private String featureFieldName;

  /** The associated l us. */
  @JsonIgnore
  private Set<AllocationLU> associatedLUs;

  /** The associated al us map. */
  @JsonIgnore
  private Map<String, String> associatedALUsMap;

  @JsonIgnore
  private Map<String, Double> associatedALUsPercentage;

  /**
   * Sets the label.
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the code.
   * 
   * @param code
   *          the code to set
   */
  public void setCode(final String code) {
    this.code = code;
  }

  /**
   * Gets the code.
   * 
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * Sets the associated l us.
   * 
   * @param associatedLUs
   *          the associatedLUs to set
   */
  public void setAssociatedLUs(final Set<AllocationLU> associatedLUs) {
    this.associatedLUs = associatedLUs;
  }

  /**
   * Gets the associated l us.
   * 
   * @return the associatedLUs
   */
  public Set<AllocationLU> getAssociatedLUs() {
    return associatedLUs;
  }

  /**
   * Adds the associated l us.
   * 
   * @param associatedLU
   *          the associated lu
   */
  public void addAssociatedLUs(final AllocationLU associatedLU) {
    this.associatedLUs.add(associatedLU);
  }

  /**
   * Gets the wif project.
   * 
   * @return the wifProject
   */
  public WifProject getWifProject() {
    return wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the wifProject to set
   */
  public void setWifProject(final WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the feature field name.
   * 
   * @return the featureFieldName
   */
  public String getFeatureFieldName() {
    return featureFieldName;
  }

  /**
   * Sets the feature field name.
   * 
   * @param featureFieldName
   *          the featureFieldName to set
   */
  public void setFeatureFieldName(final String featureFieldName) {
    this.featureFieldName = featureFieldName;
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
  public void setAssociatedALUsMap(final Map<String, String> associatedALUsMap) {
    this.associatedALUsMap = associatedALUsMap;
  }

  @JsonProperty(value = "associatedALUsPercentage")
  public Map<String, Double> getAssociatedALUsPercentage() {
    return associatedALUsPercentage;
  }

  @JsonProperty(value = "associatedALUsPercentage")
  public void setAssociatedALUsPercentage(
      final Map<String, Double> associatedALUsPercentage) {
    this.associatedALUsPercentage = associatedALUsPercentage;
  }
}
