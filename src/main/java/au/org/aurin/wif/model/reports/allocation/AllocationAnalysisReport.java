/**
 * 
 */
package au.org.aurin.wif.model.reports.allocation;

import java.util.ArrayList;
import java.util.List;

import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.reports.ScenarioReport;

/**
 * The Class AllocationAnalysisReport.
 */
public class AllocationAnalysisReport extends ScenarioReport {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -27923554363458781L;

  /** The land use information. */
  private List<AreaRequirement> landUseInformation;

  /** The population information. */
  private List<ResidentialDemographicData> populationInformation;

  /** The employment information. */
  private List<EmploymentDemographicData> employmentInformation;

  /**
   * Gets the land use information.
   * 
   * @return the land use information
   */
  public List<AreaRequirement> getLandUseInformation() {
    return landUseInformation;
  }

  /**
   * Sets the land use information.
   * 
   * @param landUseInformation
   *          the new land use information
   */
  public void setLandUseInformation(List<AreaRequirement> landUseInformation) {
    this.landUseInformation = landUseInformation;
  }

  /**
   * Gets the population information.
   * 
   * @return the population information
   */
  public List<ResidentialDemographicData> getPopulationInformation() {
    return populationInformation;
  }

  /**
   * Sets the population information.
   * 
   * @param populationInformation
   *          the new population information
   */
  public void setPopulationInformation(
      List<ResidentialDemographicData> populationInformation) {
    this.populationInformation = populationInformation;
  }

  /**
   * Gets the employment information.
   * 
   * @return the employment information
   */
  public List<EmploymentDemographicData> getEmploymentInformation() {
    return employmentInformation;
  }

  /**
   * Sets the employment information.
   * 
   * @param employmentInformation
   *          the new employment information
   */
  public void setEmploymentInformation(
      List<EmploymentDemographicData> employmentInformation) {
    this.employmentInformation = employmentInformation;
  }

  /**
   * 
   */
  public AllocationAnalysisReport() {
    super();
    landUseInformation = new ArrayList<AreaRequirement>();
    populationInformation = new ArrayList<ResidentialDemographicData>();
    employmentInformation = new ArrayList<EmploymentDemographicData>();
  }
}
