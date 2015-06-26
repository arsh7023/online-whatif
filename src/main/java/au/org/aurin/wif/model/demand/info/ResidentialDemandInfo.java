/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.model.allocation.AllocationLU;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * TODO persistence commented out,wwe get to the logic of right
 * <b>ResidentialDemandInfo.java</b> : Configuration parameters required to set
 * up the residential demand.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class ResidentialDemandInfo extends DemandInfo {

  /** The residential lu. */
  @JsonIgnore
  private AllocationLU residentialLU;
  

  /** The future breakdown by h type. */
  private Double futureBreakdownByHType;

  /**
   * The current density. This value should really not be in this entity,
   * because it doesn't change in the whole scenario, but makes the algorithm
   * easier to implement
   */
  private Double currentDensity;

  /** The future density. This value should really not be in this entity,
   * because it doesn't change in the whole scenario, but makes the algorithm
   * easier to implement */
  private Double futureDensity;

  /** The future vacancy rate. */
  private Double futureVacancyRate;

  /** The infill rate. */
  private Double infillRate;

  /**
   * Instantiates a new residential demand info.
   */
  public ResidentialDemandInfo() {
    super();
    //
  }

  /**
   * Instantiates a new residential demand info.
   *
   * @param copy the copy
   */
  public ResidentialDemandInfo(ResidentialDemandInfo copy) {
    super(copy);
    this.futureDensity = copy.getFutureDensity();
    this.futureVacancyRate = copy.getFutureVacancyRate();
    this.futureBreakdownByHType = copy.getFutureBreakdownByHType();
    this.infillRate = copy.getInfillRate();
  }

  /**
   * Gets the residential lu.
   * 
   * @return the residentialLU
   */
  public AllocationLU getResidentialLU() {
    return residentialLU;
  }

  /**
   * Sets the residential lu.
   * 
   * @param residentialLU
   *          the residentialLU to set
   */
  public void setResidentialLU(AllocationLU residentialLU) {
    this.residentialLU = residentialLU;
  }

  /**
   * Gets the future breakdown by h type.
   * 
   * @return the newBreakdownByHType
   */
  public Double getFutureBreakdownByHType() {
    return futureBreakdownByHType;
  }

  /**
   * Sets the future breakdown by h type.
   * 
   * @param newBreakdownByHType
   *          the newBreakdownByHType to set
   */
  public void setFutureBreakdownByHType(Double newBreakdownByHType) {
    this.futureBreakdownByHType = newBreakdownByHType;
  }

  /**
   * Gets the current density.
   * 
   * @return the currentDensity
   */
  public Double getCurrentDensity() {
    return currentDensity;
  }

  /**
   * Sets the current density.
   * 
   * @param currentDensity
   *          the currentDensity to set
   */
  public void setCurrentDensity(Double currentDensity) {
    this.currentDensity = currentDensity;
  }

  /**
   * Gets the future density.
   * 
   * @return the newDensity
   */
  public Double getFutureDensity() {
    return futureDensity;
  }

  /**
   * Sets the future density.
   * 
   * @param newDensity
   *          the newDensity to set
   */
  public void setFutureDensity(Double newDensity) {
    this.futureDensity = newDensity;
  }

  /**
   * Gets the future vacancy rate.
   * 
   * @return the newVacancyRate
   */
  public Double getFutureVacancyRate() {
    return futureVacancyRate;
  }

  /**
   * Sets the future vacancy rate.
   * 
   * @param newVacancyRate
   *          the newVacancyRate to set
   */
  public void setFutureVacancyRate(Double newVacancyRate) {
    this.futureVacancyRate = newVacancyRate;
  }

  /**
   * Gets the infill rate.
   * 
   * @return the infillRate
   */
  public Double getInfillRate() {
    return infillRate;
  }

  /**
   * Sets the infill rate.
   * 
   * @param infillRate
   *          the infillRate to set
   */
  public void setInfillRate(Double infillRate) {
    this.infillRate = infillRate;
  }

  /**
   * Gets the residential lu id.
   *
   * @return the residentialLUId
   */
  public String getResidentialLUId() {
    return super.getAllocationLUId();
  }

  /**
   * Sets the residential lu id.
   *
   * @param residentialLUId the residentialLUId to set
   */
  public void setResidentialLUId(String residentialLUId) {
   super.setAllocationLUId(residentialLUId);
  }

}
