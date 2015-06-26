/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>DemographicTrend.java</b> : All the demographic data that changes per
 * projection year. This demographic data holds trends that are specified in the
 * setup of a project.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class DemographicTrend {

  /** The wif project. @uml.property name="wifProject_fk" */
  @JsonIgnore
  private WifProject wifProject;

  /** The label. @uml.property name="label" */
  private String label;

  /** The demographicData. */
  private Set<DemographicData> demographicData;

  /**
   * Gets the wif project.
   * 
   * @return the wifProject
   */
  public WifProject getWifProject() {
    return wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the wifProject to set
   */
  public void setWifProject(final WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * Gets the demographic data.
   * 
   * @return the demographicData
   */
  public Set<DemographicData> getDemographicData() {
    return demographicData;
  }

  /**
   * Sets the demographic data.
   * 
   * @param demographicData
   *          the demographicData to set
   */
  public void setDemographicData(final Set<DemographicData> demographicData) {
    this.demographicData = demographicData;
  }

  /**
   * Gets the residential demographic data.
   * 
   * @param projection
   *          the projection
   * @return the residential demographic data
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public ResidentialDemographicData getResidentialDemographicData(
      final Projection projection) throws WifInvalidInputException {

    for (final DemographicData demographicData : this.getDemographicData()) {
      if (demographicData instanceof ResidentialDemographicData
          && demographicData.getProjection().getYear()
              .equals(projection.getYear())) {
        return (ResidentialDemographicData) demographicData;
      }
    }
    throw new WifInvalidInputException(
        "there's no ResidentialDemographicData from  projection:"
            + projection.getLabel());
  }

  /**
   * Gets the employment demographic data.
   * 
   * @param projection
   *          the projection
   * @param sector
   *          the sector
   * @return the employment demographic data
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public EmploymentDemographicData getEmploymentDemographicData(
      final Projection projection, final EmploymentSector sector)
      throws WifInvalidInputException {

    for (final DemographicData demographicData : this.getDemographicData()) {
      if (demographicData instanceof EmploymentDemographicData
          && demographicData.getProjection().getYear()
              .equals(projection.getYear())
          && ((EmploymentDemographicData) demographicData).getEmployees()
              .equals(sector)) {
        return (EmploymentDemographicData) demographicData;
      }
    }
    throw new WifInvalidInputException(
        "there's no EmploymentDemographicData from  projection:"
            + projection.getLabel());
  }

  public EmploymentDemographicData getEmploymentDemographicData(
      final Projection projection, final String sectorLabel)
      throws WifInvalidInputException {

    for (final DemographicData demographicData : this.getDemographicData()) {
      if (demographicData instanceof EmploymentDemographicData
          && demographicData.getProjection().getYear()
              .equals(projection.getYear())) {
        final EmploymentDemographicData newdemographicData = (EmploymentDemographicData) demographicData;
        if (newdemographicData.getSectorLabel().equals(sectorLabel)) {
          return (EmploymentDemographicData) demographicData;
        }
      }
    }
    throw new WifInvalidInputException(
        "there's no EmploymentDemographicData from  projection:"
            + projection.getLabel());
  }
}
