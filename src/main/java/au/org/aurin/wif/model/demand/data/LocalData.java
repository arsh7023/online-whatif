package au.org.aurin.wif.model.demand.data;

import au.org.aurin.wif.model.demand.LocalJurisdiction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class LocalData.
 */
public class LocalData extends ProjectedData {

  /** The local jurisdiction. @uml.property name="factor" */
  @JsonIgnore
  private LocalJurisdiction localJurisdiction;
  
  /** The local jurisdiction label. */
  private String localJurisdictionLabel;
  /** The required density. */
  private Double requiredDensity;

  /**
   * Instantiates a new local data.
   *
   * @param copy the copy
   */
  public LocalData(LocalData copy) {
    super(copy);
    this.localJurisdiction = copy.getLocalJurisdiction();
    this.requiredDensity = copy.getRequiredDensity();
  }

  /**
   * Gets the local jurisdiction.
   *
   * @return the local jurisdiction
   */
  public LocalJurisdiction getLocalJurisdiction() {
    return localJurisdiction;
  }

  /**
   * Sets the local jurisdiction.
   *
   * @param localJurisdiction the new local jurisdiction
   */
  public void setLocalJurisdiction(LocalJurisdiction localJurisdiction) {
    this.localJurisdiction = localJurisdiction;
  }

  /**
   * Instantiates a new local data.
   */
  public LocalData() {
  }

  /**
   * Sets the required density.
   * 
   * @param reservedArea
   *          the new required density
   */
  public void setRequiredDensity(Double reservedArea) {
    this.requiredDensity = reservedArea;
  }

  /**
   * Gets the required density.
   * 
   * @return the employees
   */
  public Double getRequiredDensity() {
    return requiredDensity;
  }

  /**
   * Gets the local jurisdiction label.
   *
   * @return the localJurisdictionLabel
   */
  public String getLocalJurisdictionLabel() {
    return localJurisdictionLabel;
  }

  /**
   * Sets the local jurisdiction label.
   *
   * @param localJurisdictionLabel the localJurisdictionLabel to set
   */
  public void setLocalJurisdictionLabel(String localJurisdictionLabel) {
    this.localJurisdictionLabel = localJurisdictionLabel;
  }

}
