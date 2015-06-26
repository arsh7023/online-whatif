/**
 * 
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * It holds the employment demographic information of the demand scenario that
 * doesn't change for each land use, it is global for a given projection year.
 * 
 * @author marcosnr
 */
public class EmploymentDemographicData extends DemographicData {

  /** The sector. */
  @JsonIgnore
  private EmploymentSector sector;

  /** The sector label. */
  private String sectorLabel;

  /**
   * The employees. Employment demographics data information GOOD place for
   * employment data in demographicTrend Employee information will be duplicated
   * in employmentDemographicData and employmentProjectedData to allow
   * employment configuration per scenario The first belongs to a trend (one per
   * project), the second belongs to a demandScenario (one per scenario), the
   * duplicated information is the number of employees
   */
  private Integer employees;

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
  public Integer getEmployees() {
    return employees;
  }

  /**
   * Sets the employees.
   *
   * @param employees the employees to set
   */
  public void setEmployees(Integer employees) {
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
