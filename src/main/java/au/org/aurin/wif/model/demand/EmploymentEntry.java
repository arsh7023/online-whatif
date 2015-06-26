/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>EmploymentEntry.java</b> : Holds employment information.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class EmploymentEntry {

  /** The employment past trend info. */
  @JsonIgnore
  private EmploymentPastTrendInfo employmentPastTrendInfo;
  
  /** The sector. */
  @JsonIgnore
 private EmploymentSector sector;

  /** The sector label. */
  private String sectorLabel;

  /** The employees. */
  private Long employees;

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
   * Gets the employment past trend info.
   *
   * @return the employmentPastTrendInfo
   */
  public EmploymentPastTrendInfo getEmploymentPastTrendInfo() {
    return employmentPastTrendInfo;
  }

  /**
   * Sets the employment past trend info.
   *
   * @param employmentPastTrend the new employment past trend info
   */
  public void setEmploymentPastTrendInfo(EmploymentPastTrendInfo employmentPastTrend) {
    this.employmentPastTrendInfo = employmentPastTrend;
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
