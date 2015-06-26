/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.Set;

import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;

/**
 * <b>EmploymentPastTrendInfo.java</b> : Past trends that hold information
 * necessary to produce demographic trends automatically.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class EmploymentPastTrendInfo extends PastTrendInfo {

  /** The employment entries. */
  private Set<EmploymentEntry> employmentEntries;

  /**
   * Gets the employment entries.
   * 
   * @return the employmentEntries
   */
  public Set<EmploymentEntry> getEmploymentEntries() {
    return employmentEntries;
  }

  /**
   * Sets the employment entries.
   * 
   * @param employmentEntries
   *          the employmentEntries to set
   */
  public void setEmploymentEntries(Set<EmploymentEntry> employmentEntries) {
    this.employmentEntries = employmentEntries;
  }

  /**
   * Gets the employment entry by sector.
   * 
   * @param sector
   *          the sector
   * @return the employment entry by sector
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public EmploymentEntry getEmploymentEntryBySector(EmploymentSector sector)
      throws IncompleteDemandConfigException {
    for (EmploymentEntry entry : this.getEmploymentEntries()) {
      if (sector.getLabel().equals(entry.getSectorLabel())) {
        return entry;
      }
    }
    throw new IncompleteDemandConfigException(sector.getLabel()
        + " EmploymentEntry not assigned in this trend");
  }
}
