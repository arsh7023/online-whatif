/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.CurrentDemographic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class ResidentialCurrentData.
 */
@JsonInclude(Include.NON_NULL)
public class ResidentialCurrentData {
	
  /** The residential lu. */
  @JsonIgnore
	private AllocationLU residentialLU;
  
  /** The residential lu id. */
  private String residentialLUId;

	/** The number of housing units. */
	private Integer numberOfHousingUnits;
			
	/** The density. */
	private Double density;
  
  /** The breakdown density. */
  private Double breakdownDensity;
	
	/** The vacancy rate. */
	private Double vacancyRate;
	
	/** The infill rate. */
	private Double infillRate;

  /** The current demographic. */
	@JsonIgnore
  private CurrentDemographic currentDemographic;

  /**
   * Gets the number of housing units.
   *
   * @return the number of housing units
   */
  public Integer getNumberOfHousingUnits() {
    return numberOfHousingUnits;
  }

  /**
   * Sets the number of housing units.
   *
   * @param numberOfHousingUnits the new number of housing units
   */
  public void setNumberOfHousingUnits(Integer numberOfHousingUnits) {
    this.numberOfHousingUnits = numberOfHousingUnits;
  }

  /**
   * Gets the residential lu.
   *
   * @return the residential lu
   */
  public AllocationLU getResidentialLU() {
    return residentialLU;
  }

  /**
   * Sets the residential lu.
   *
   * @param residentialLU the new residential lu
   */
  public void setResidentialLU(AllocationLU residentialLU) {
    this.residentialLU = residentialLU;
  }

  /**
   * Gets the density.
   *
   * @return the density
   */
  public Double getDensity() {
    return density;
  }

  /**
   * Sets the density.
   *
   * @param density the new density
   */
  public void setDensity(Double density) {
    this.density = density;
  }

  /**
   * Gets the vacancy rate.
   *
   * @return the vacancy rate
   */
  public Double getVacancyRate() {
    return vacancyRate;
  }

  /**
   * Sets the vacancy rate.
   *
   * @param vacancyRate the new vacancy rate
   */
  public void setVacancyRate(Double vacancyRate) {
    this.vacancyRate = vacancyRate;
  }

  /**
   * Gets the infill rate.
   *
   * @return the infill rate
   */
  public Double getInfillRate() {
    return infillRate;
  }

  /**
   * Sets the infill rate.
   *
   * @param infillRate the new infill rate
   */
  public void setInfillRate(Double infillRate) {
    this.infillRate = infillRate;
  }

  /**
   * Gets the current demographic.
   *
   * @return the current demographic
   */
  public CurrentDemographic getCurrentDemographic() {
    return currentDemographic;
  }

  /**
   * Sets the current demographic.
   *
   * @param currentDemographic the new current demographic
   */
  public void setCurrentDemographic(CurrentDemographic currentDemographic) {
    this.currentDemographic = currentDemographic;
  }

  /**
   * Gets the breakdown density.
   *
   * @return the breakdown density
   */
  public Double getBreakdownDensity() {
    return breakdownDensity;
  }

  /**
   * Sets the breakdown density.
   *
   * @param breakdownDensity the new breakdown density
   */
  public void setBreakdownDensity(Double breakdownDensity) {
    this.breakdownDensity = breakdownDensity;
  }

  /**
   * Gets the residential lu id.
   *
   * @return the residentialLUId
   */
  public String getResidentialLUId() {
    return residentialLUId;
  }

  /**
   * Sets the residential lu id.
   *
   * @param residentialLUId the residentialLUId to set
   */
  public void setResidentialLUId(String residentialLUId) {
    this.residentialLUId = residentialLUId;
  }

}
