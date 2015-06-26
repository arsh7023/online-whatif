/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>EmploymentGrowthRate.java</b> : Holds employment information.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class EmploymentGrowthRate {

  /** The id. @uml.property name="id" */
  private Integer id;

  /** The demand config. */
  @JsonIgnore
  private DemandConfig demandConfig;

  /** The sector. */
  @JsonIgnore
 private EmploymentSector sector;
  
  /** The sector label. */
  private String sectorLabel;

  /** The growthRate. */
  private Double growthRate;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
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
   * Sets the sector.
   *
   * @param sector the sector to set
   */
  public void setSector(EmploymentSector sector) {
    this.sector = sector;
  }

  /**
   * Gets the demand config.
   *
   * @return the demandConfig
   */
  public DemandConfig getDemandConfig() {
    return demandConfig;
  }

  /**
   * Sets the demand config.
   *
   * @param demandConfig the demandConfig to set
   */
  public void setDemandConfig(DemandConfig demandConfig) {
    this.demandConfig = demandConfig;
  }

  /**
   * Gets the growth rate.
   *
   * @return the growthRate
   */
  public Double getGrowthRate() {
    return growthRate;
  }

  /**
   * Sets the growth rate.
   *
   * @param growthRate the growthRate to set
   */
  public void setGrowthRate(Double growthRate) {
    this.growthRate = growthRate;
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
