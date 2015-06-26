/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.EmploymentSector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <b>EmploymentCurrentData.java</b> : Configuration parameters required to set
 * up the residential demand.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonInclude(Include.NON_NULL)
public class EmploymentCurrentData {

  /** The sector. */
  @JsonIgnore
  private EmploymentSector sector;
  
  /** The sector label. */
  private String sectorLabel;

  /** The employees. */
  private Long employees;

  /** The density. */
  private Double density;

  /** The infill rate. */
  private Double infillRate;

  /** The current demographic. */
  @JsonIgnore
  private CurrentDemographic currentDemographic;

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
   * @param density the density to set
   */
  public void setDensity(Double density) {
    this.density = density;
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
   * @param infillRate the infillRate to set
   */
  public void setInfillRate(Double infillRate) {
    this.infillRate = infillRate;
  }

  /**
   * Gets the current demographic.
   *
   * @return the currentDemographic
   */
  public CurrentDemographic getCurrentDemographic() {
    return currentDemographic;
  }

  /**
   * Sets the current demographic.
   *
   * @param currentDemographic the currentDemographic to set
   */
  public void setCurrentDemographic(CurrentDemographic currentDemographic) {
    this.currentDemographic = currentDemographic;
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
   * Gets the employees.
   *
   * @return the employees
   */
  public Long getEmployees() {
    return employees;
  }

  /**
   * Sets the employees.
   *
   * @param employees the employees to set
   */
  public void setEmployees(Long employees) {
    this.employees = employees;
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
