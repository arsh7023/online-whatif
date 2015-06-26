/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <b>EmploymentCurrentData.java</b> : Configuration parameters required to set
 * up the residential demand.
 * 
 * @author ashamakhy 18/12/2013
 * 
 */
@JsonInclude(Include.NON_NULL)
public class EmploymentCurrentDataNew {

  /** The sector label. */
  private String sectorLabel;

  /** The employees. */
  private Long employees;

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
   * @param employees
   *          the employees to set
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
   * @param sectorLabel
   *          the sectorLabel to set
   */
  public void setSectorLabel(String sectorLabel) {
    this.sectorLabel = sectorLabel;
  }

}
