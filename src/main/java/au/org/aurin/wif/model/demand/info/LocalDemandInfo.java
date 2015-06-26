/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import java.util.Set;

import au.org.aurin.wif.model.demand.LocalJurisdiction;

/**
 * TODO persistence commented out,wwe get to the logic of right
 * <b>ResidentialDemandInfo.java</b> : Configuration parameters required to set
 * up the residential demand.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class LocalDemandInfo extends DemandInfo {

  /** The local jurisdictions. */
  private Set<LocalJurisdiction> localJurisdictions;

  /**
   * Instantiates a new local demand info.
   */
  public LocalDemandInfo() {
    super();
  }

  /**
   * Instantiates a new local demand info.
   *
   * @param copy the copy
   */
  public LocalDemandInfo(LocalDemandInfo copy) {
    super(copy);
    this.localJurisdictions = copy.getLocalJurisdictions();
  }

  /**
   * Sets the local jurisdictions.
   * 
   * @param localJurisdictions
   *          the localJurisdictions to set
   */
  public void setLocalJurisdictions(Set<LocalJurisdiction> localJurisdictions) {
    this.localJurisdictions = localJurisdictions;
  }

  /**
   * Gets the local jurisdictions.
   * 
   * @return the localJurisdictions
   */
  public Set<LocalJurisdiction> getLocalJurisdictions() {
    return localJurisdictions;
  }

}
