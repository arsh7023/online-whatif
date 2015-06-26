/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.model.demand.EmploymentSector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class EmploymentDemandInfo.
 *
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 * marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonInclude(Include.NON_NULL)
public class EmploymentDemandInfo extends ProjectedDemandInfo {

  /** The current density. */
  private Double currentDensity;

  /** The future density. */
  private Double futureDensity;

  /** The infill rate. */
  private Double infillRate;

  /** The sector. */
  @JsonIgnore
  private EmploymentSector sector;

  /** The sector label. */
  private String sectorLabel;

  /**
   * Instantiates a new employment demand info.
   */
  public EmploymentDemandInfo() {
    super();
  }

  /**
   * Instantiates a new employment demand info.
   *
   * @param copy the copy
   */
  public EmploymentDemandInfo(EmploymentDemandInfo copy) {
    super(copy);
    this.currentDensity = copy.getCurrentDensity();
    this.futureDensity = copy.getFutureDensity();
    this.infillRate = copy.getInfillRate();
    this.sector = copy.getSector();
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
   * Sets the sector.
   * 
   * @param sector
   *          the sector to set
   */
  public void setSector(EmploymentSector sector) {
    this.sector = sector;
  }

  /**
   * Gets the sector.
   * 
   * @return the sector
   */
  public EmploymentSector getSector() {
    return sector;
  }

  /**
   * Gets the sector label.
   *
   * @return the sectorLabel
   */
  public String getSectorLabel() {
    return sectorLabel;
  }

  /**
   * Sets the sector label.
   *
   * @param sectorLabel the sectorLabel to set
   */
  public void setSectorLabel(String sectorLabel) {
    this.sectorLabel = sectorLabel;
  }

}
