package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.ProjectCouchDoc;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentDataNew;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class DemandConfigNew.
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "docType" })
public class DemandConfigNew extends ProjectCouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4573398998727018611L;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

  /** The base year. */
  private Integer baseYear;

  /** The total population feature field name. */
  private String totalPopulationFeatureFieldName;

  /** The number of households feature field name. */
  private String numberOfHouseholdsFeatureFieldName;

  /** The number of housing units feature field name. */
  private String numberOfHousingUnitsFeatureFieldName;

  /** The group quarters population feature field name. */
  private String groupQuartersPopulationFeatureFieldName;

  // /** The enumeration district feature field name. */
  // private String enumerationDistrictFeatureFieldName;
  //
  // /** The enumeration district area feature field name. */
  // private String enumerationDistrictAreaFeatureFieldName;
  //
  // /** The clipped enumeration district area feature field name. */
  // private String clippedEnumerationDistrictAreaFeatureFieldName;
  //
  // /** The landuse field name. */
  // private String landUseFieldName;
  //
  // /** The low density residential value. */
  // private String lowDensityresidentialValue;
  //
  // /** The medium density residential value. */
  // private String mediumDensityresidentialValue;
  //
  // /** The high density residential value. */
  // private String highDensityresidentialValue;

  /** The projections. */
  private Set<Projection> projections;

  /** The sectors. */
  private Set<EmploymentSector> sectors;

  /** The Projection Names. */
  // private Set<String> projectionNames;

  /** The current Employment. */
  private Set<EmploymentCurrentDataNew> employmentCurrentDataNew;

  /**
   * Gets the wif project.
   * 
   * @return the wif project
   */
  public WifProject getWifProject() {
    return wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the base year.
   * 
   * @return the base year
   */
  public Integer getBaseYear() {
    return baseYear;
  }

  /**
   * Sets the base year.
   * 
   * @param baseYear
   *          the new base year
   */
  public void setBaseYear(Integer baseYear) {
    this.baseYear = baseYear;
  }

  /**
   * Gets the total population feature field name.
   * 
   * @return the total population feature field name
   */
  public String getTotalPopulationFeatureFieldName() {
    return totalPopulationFeatureFieldName;
  }

  /**
   * Sets the total population feature field name.
   * 
   * @param totalPopulationFeatureFieldName
   *          the new total population feature field name
   */
  public void setTotalPopulationFeatureFieldName(
      String totalPopulationFeatureFieldName) {
    this.totalPopulationFeatureFieldName = totalPopulationFeatureFieldName;
  }

  /**
   * Gets the number of households feature field name.
   * 
   * @return the number of households feature field name
   */
  public String getNumberOfHouseholdsFeatureFieldName() {
    return numberOfHouseholdsFeatureFieldName;
  }

  /**
   * Sets the number of households feature field name.
   * 
   * @param numberOfHouseholdsFeatureFieldName
   *          the new number of households feature field name
   */
  public void setNumberOfHouseholdsFeatureFieldName(
      String numberOfHouseholdsFeatureFieldName) {
    this.numberOfHouseholdsFeatureFieldName = numberOfHouseholdsFeatureFieldName;
  }

  /**
   * Gets the number of housing units feature field name.
   * 
   * @return the number of housing units feature field name
   */
  public String getNumberOfHousingUnitsFeatureFieldName() {
    return numberOfHousingUnitsFeatureFieldName;
  }

  /**
   * Sets the number of housing units feature field name.
   * 
   * @param numberOfHousingUnitsFeatureFieldName
   *          the new number of housing units feature field name
   */
  public void setNumberOfHousingUnitsFeatureFieldName(
      String numberOfHousingUnitsFeatureFieldName) {
    this.numberOfHousingUnitsFeatureFieldName = numberOfHousingUnitsFeatureFieldName;
  }

  /**
   * Gets the group quarters population feature field name.
   * 
   * @return the group quarters population feature field name
   */
  public String getGroupQuartersPopulationFeatureFieldName() {
    return groupQuartersPopulationFeatureFieldName;
  }

  /**
   * Sets the group quarters population feature field name.
   * 
   * @param groupQuartersPopulationFeatureFieldName
   *          the new group quarters population feature field name
   */
  public void setGroupQuartersPopulationFeatureFieldName(
      String groupQuartersPopulationFeatureFieldName) {
    this.groupQuartersPopulationFeatureFieldName = groupQuartersPopulationFeatureFieldName;
  }

  // /**
  // * Gets the enumeration district area feature field name.
  // *
  // * @return the enumeration district area feature field name
  // */
  // public String getEnumerationDistrictAreaFeatureFieldName() {
  // return enumerationDistrictAreaFeatureFieldName;
  // }
  //
  // /**
  // * Sets the enumeration district area feature field name.
  // *
  // * @param enumerationDistrictAreaFeatureFieldName
  // * the new enumeration district area feature field name
  // */
  // public void setEnumerationDistrictAreaFeatureFieldName(
  // String enumerationDistrictAreaFeatureFieldName) {
  // this.enumerationDistrictAreaFeatureFieldName =
  // enumerationDistrictAreaFeatureFieldName;
  // }
  //
  // /**
  // * Gets the clipped enumeration district area feature field name.
  // *
  // * @return the clipped enumeration district area feature field name
  // */
  // public String getClippedEnumerationDistrictAreaFeatureFieldName() {
  // return clippedEnumerationDistrictAreaFeatureFieldName;
  // }
  //
  // /**
  // * Sets the clipped enumeration district area feature field name.
  // *
  // * @param clippedEnumerationDistrictAreaFeatureFieldName
  // * the new clipped enumeration district area feature field name
  // */
  // public void setClippedEnumerationDistrictAreaFeatureFieldName(
  // String clippedEnumerationDistrictAreaFeatureFieldName) {
  // this.clippedEnumerationDistrictAreaFeatureFieldName =
  // clippedEnumerationDistrictAreaFeatureFieldName;
  // }

  /**
   * Gets the projections.
   * 
   * @return the projections
   */
  public Set<Projection> getProjections() {
    return projections;
  }

  /**
   * Sets the projections.
   * 
   * @param projections
   *          the new projections
   */
  public void setProjections(Set<Projection> projections) {
    this.projections = projections;
  }

  /**
   * Instantiates a new demand config.
   */
  public DemandConfigNew() {
    super();
    projections = new HashSet<Projection>();
    sectors = new HashSet<EmploymentSector>();
    employmentCurrentDataNew = new HashSet<EmploymentCurrentDataNew>();

  }

  /**
   * Gets the sectors.
   * 
   * @return the sectors
   */
  public Set<EmploymentSector> getSectors() {
    return sectors;
  }

  /**
   * Sets the sectors.
   * 
   * @param sectors
   *          the new sectors
   */
  public void setSectors(Set<EmploymentSector> sectors) {
    this.sectors = sectors;
  }

  /**
   * Gets the sector by label.
   * 
   * @param label
   *          the label
   * @return the sector by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public EmploymentSector getSectorByLabel(String label)
      throws WifInvalidInputException {
    for (EmploymentSector sector : this.getSectors()) {
      if (sector.getLabel().equals(label)) {
        return sector;
      }
    }
    throw new WifInvalidInputException(label + " the sector label not found");
  }

  /**
   * Gets the projection by label.
   * 
   * @param label
   *          the label
   * @return the projection by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Projection getProjectionByLabel(String label)
      throws WifInvalidInputException {
    for (Projection proj : this.getProjections()) {
      if (proj.getLabel().equals(label)) {
        return proj;
      }
    }
    throw new WifInvalidInputException(label + " name not found");
  }

  // /**
  // * Gets the enumeration district feature field name.
  // *
  // * @return the enumeration district feature field name
  // */
  // public String getEnumerationDistrictFeatureFieldName() {
  // return enumerationDistrictFeatureFieldName;
  // }
  //
  // /**
  // * Sets the enumeration district feature field name.
  // *
  // * @param enumerationDistrictFeatureFieldName
  // * the new enumeration district feature field name
  // */
  // public void setEnumerationDistrictFeatureFieldName(
  // String enumerationDistrictFeatureFieldName) {
  // this.enumerationDistrictFeatureFieldName =
  // enumerationDistrictFeatureFieldName;
  // }

  // public Set<String> getProjectionNames() {
  // return projectionNames;
  // }
  //
  // public void setProjectionNames(Set<String> projectionNames) {
  // this.projectionNames = projectionNames;
  // }

  public Set<EmploymentCurrentDataNew> getEmploymentCurrentDataNew() {
    return employmentCurrentDataNew;
  }

  public void setEmploymentCurrentDataNew(
      Set<EmploymentCurrentDataNew> employmentCurrentDataNew) {
    this.employmentCurrentDataNew = employmentCurrentDataNew;
  }

  // public String getLandUseFieldName() {
  // return landUseFieldName;
  // }
  //
  // public void setLandUseFieldName(String landUseFieldName) {
  // this.landUseFieldName = landUseFieldName;
  // }
  //
  // public String getLowDensityresidentialValue() {
  // return lowDensityresidentialValue;
  // }
  //
  // public void setLowDensityresidentialValue(String
  // lowDensityresidentialValue) {
  // this.lowDensityresidentialValue = lowDensityresidentialValue;
  // }
  //
  // public String getMediumDensityresidentialValue() {
  // return mediumDensityresidentialValue;
  // }
  //
  // public void setMediumDensityresidentialValue(
  // String mediumDensityresidentialValue) {
  // this.mediumDensityresidentialValue = mediumDensityresidentialValue;
  // }
  //
  // public String getHighDensityresidentialValue() {
  // return highDensityresidentialValue;
  // }
  //
  // public void setHighDensityresidentialValue(String
  // highDensityresidentialValue) {
  // this.highDensityresidentialValue = highDensityresidentialValue;
  // }

}
