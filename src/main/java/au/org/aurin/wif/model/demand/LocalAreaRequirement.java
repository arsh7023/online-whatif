package au.org.aurin.wif.model.demand;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class LocalAreaRequirement.
 */
public class LocalAreaRequirement extends AreaRequirement {

  /**
   * 
   */
  private static final long serialVersionUID = -6227642540622296746L;

  /** The jurisdiction. */
  @JsonIgnore
  private LocalJurisdiction localJurisdiction;

  /** The local jurisdiction label. */
  private String localJurisdictionLabel;

  /**
   * Sets the jurisdiction.
   * 
   * @param jurisdiction
   *          the new jurisdiction
   */
  public void setLocalJurisdiction(LocalJurisdiction jurisdiction) {
    this.localJurisdiction = jurisdiction;
    setLocalJurisdictionLabel(jurisdiction.getLabel());
  }

  /**
   * Gets the jurisdiction.
   * 
   * @return the jurisdiction
   */
  public LocalJurisdiction getLocalJurisdiction() {
    return localJurisdiction;
  }

  /**
   * Gets the local jurisdiction label.
   * 
   * @return the local jurisdiction label
   */
  public String getLocalJurisdictionLabel() {
    return localJurisdictionLabel;
  }

  /**
   * Sets the local jurisdiction label.
   * 
   * @param localJurisdictionLabel
   *          the new local jurisdiction label
   */
  public void setLocalJurisdictionLabel(String localJurisdictionLabel) {
    this.localJurisdictionLabel = localJurisdictionLabel;
  }
}
