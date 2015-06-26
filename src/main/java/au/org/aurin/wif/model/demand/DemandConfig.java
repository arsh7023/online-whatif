package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.ProjectCouchDoc;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class DemandConfig.
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "docType" })
public class DemandConfig extends ProjectCouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4573398998727018611L;

  /** The parsed. */
  @JsonIgnore
  private boolean parsed = false;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

  /** The population growth rate. */
  private Double populationGrowthRate;

  /** The gq growth rate. */
  private Double gqGrowthRate;

  /** The households growth rate. */
  private Double householdsGrowthRate;

  /** The base year. */
  private Integer baseYear;

  /** The total population feature field name. */
  private String totalPopulationFeatureFieldName;

  /** The local demand areas feature field name. */
  @JsonIgnore
  private String localDemandAreasFeatureFieldName;

  /** The number of households feature field name. */
  private String numberOfHouseholdsFeatureFieldName;

  /** The number of housing units feature field name. */
  private String numberOfHousingUnitsFeatureFieldName;

  /** The group quarters population feature field name. */
  @JsonIgnore
  private String groupQuartersPopulationFeatureFieldName;

  /** The enumeration district area feature field name. */
  @JsonIgnore
  private String enumerationDistrictAreaFeatureFieldName;

  /** The enumeration district feature field name. */
  @JsonIgnore
  private String enumerationDistrictFeatureFieldName;

  /** The clipped enumeration district area feature field name. */
  @JsonIgnore
  private String clippedEnumerationDistrictAreaFeatureFieldName;

  /** The vacant land feature field name. */
  @JsonIgnore
  private String vacantLandFeatureFieldName;

  /** The projections. */
  private Set<Projection> projections;

  /** The sectors. */
  private Set<EmploymentSector> sectors;

  /** The current demographic. */
  private CurrentDemographic currentDemographic;

  /** The residential past trend infos. */
  private Set<ResidentialPastTrendInfo> residentialPastTrendInfos;

  /** The employment past trend infos. */
  private Set<EmploymentPastTrendInfo> employmentPastTrendInfos;

  /** The employment growth rates. */
  private Set<EmploymentGrowthRate> employmentGrowthRates;

  /** The demographic trends. */
  private Set<DemographicTrend> demographicTrends;

  /** The local jurisdictions. */
  private Set<LocalJurisdiction> localJurisdictions;

  /** The include trends. */
  private boolean includeTrends;

  // @JsonIgnore
  private Set<DensityDemandInfo> densityDemandInfo;

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
  public void setWifProject(final WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the current demographic.
   * 
   * @return the current demographic
   */
  public CurrentDemographic getCurrentDemographic() {
    return currentDemographic;
  }

  /**
   * Sets the current demographic.
   * 
   * @param currentDemographic
   *          the new current demographic
   */
  public void setCurrentDemographic(final CurrentDemographic currentDemographic) {
    this.currentDemographic = currentDemographic;
  }

  /**
   * Gets the residential past trend infos.
   * 
   * @return the residential past trend infos
   */
  public Set<ResidentialPastTrendInfo> getResidentialPastTrendInfos() {
    return residentialPastTrendInfos;
  }

  /**
   * Sets the residential past trend infos.
   * 
   * @param pastTrends
   *          the new residential past trend infos
   */
  public void setResidentialPastTrendInfos(
      final Set<ResidentialPastTrendInfo> pastTrends) {
    this.residentialPastTrendInfos = pastTrends;
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
  public void setBaseYear(final Integer baseYear) {
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
      final String totalPopulationFeatureFieldName) {
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
      final String numberOfHouseholdsFeatureFieldName) {
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
      final String numberOfHousingUnitsFeatureFieldName) {
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
      final String groupQuartersPopulationFeatureFieldName) {
    this.groupQuartersPopulationFeatureFieldName = groupQuartersPopulationFeatureFieldName;
  }

  /**
   * Gets the enumeration district area feature field .
   * 
   * @return the enumeration district area feature field name
   */
  public String getEnumerationDistrictAreaFeatureFieldName() {
    return enumerationDistrictAreaFeatureFieldName;
  }

  /**
   * Sets the enumeration district area feature field name.
   * 
   * @param enumerationDistrictAreaFeatureFieldName
   *          the new enumeration district area feature field name
   */
  public void setEnumerationDistrictAreaFeatureFieldName(
      final String enumerationDistrictAreaFeatureFieldName) {
    this.enumerationDistrictAreaFeatureFieldName = enumerationDistrictAreaFeatureFieldName;
  }

  /**
   * Gets the clipped enumeration district area feature field name.
   * 
   * @return the clipped enumeration district area feature field name
   */
  public String getClippedEnumerationDistrictAreaFeatureFieldName() {
    return clippedEnumerationDistrictAreaFeatureFieldName;
  }

  /**
   * Sets the clipped enumeration district area feature field name.
   * 
   * @param clippedEnumerationDistrictAreaFeatureFieldName
   *          the new clipped enumeration district area feature field name
   */
  public void setClippedEnumerationDistrictAreaFeatureFieldName(
      final String clippedEnumerationDistrictAreaFeatureFieldName) {
    this.clippedEnumerationDistrictAreaFeatureFieldName = clippedEnumerationDistrictAreaFeatureFieldName;
  }

  /**
   * Gets the vacant land feature field name.
   * 
   * @return the vacant land feature field name
   */
  public String getVacantLandFeatureFieldName() {
    return vacantLandFeatureFieldName;
  }

  /**
   * Sets the vacant land feature field name.
   * 
   * @param vacantLandFeatureFieldName
   *          the new vacant land feature field name
   */
  public void setVacantLandFeatureFieldName(
      final String vacantLandFeatureFieldName) {
    this.vacantLandFeatureFieldName = vacantLandFeatureFieldName;
  }

  /**
   * Gets the population growth rate.
   * 
   * @return the population growth rate
   */
  public Double getPopulationGrowthRate() {
    return populationGrowthRate;
  }

  /**
   * Sets the population growth rate.
   * 
   * @param trendGrowthRate
   *          the new population growth rate
   */
  public void setPopulationGrowthRate(final Double trendGrowthRate) {
    this.populationGrowthRate = trendGrowthRate;
  }

  /**
   * Gets the gq growth rate.
   * 
   * @return the gq growth rate
   */
  public Double getGqGrowthRate() {
    return gqGrowthRate;
  }

  /**
   * Sets the gq growth rate.
   * 
   * @param gqGrowthRate
   *          the new gq growth rate
   */
  public void setGqGrowthRate(final Double gqGrowthRate) {
    this.gqGrowthRate = gqGrowthRate;
  }

  /**
   * Gets the households growth rate.
   * 
   * @return the households growth rate
   */
  public Double getHouseholdsGrowthRate() {
    return householdsGrowthRate;
  }

  /**
   * Sets the households growth rate.
   * 
   * @param householdsGrowthRate
   *          the new households growth rate
   */
  public void setHouseholdsGrowthRate(final Double householdsGrowthRate) {
    this.householdsGrowthRate = householdsGrowthRate;
  }

  /**
   * Gets the employment past trend infos.
   * 
   * @return the employment past trend infos
   */
  public Set<EmploymentPastTrendInfo> getEmploymentPastTrendInfos() {
    return employmentPastTrendInfos;
  }

  /**
   * Sets the employment past trend infos.
   * 
   * @param employmentPastTrends
   *          the new employment past trend infos
   */
  public void setEmploymentPastTrendInfos(
      final Set<EmploymentPastTrendInfo> employmentPastTrends) {
    this.employmentPastTrendInfos = employmentPastTrends;
  }

  /**
   * Gets the employment growth rates.
   * 
   * @return the employment growth rates
   */
  public Set<EmploymentGrowthRate> getEmploymentGrowthRates() {
    return employmentGrowthRates;
  }

  /**
   * Sets the employment growth rates.
   * 
   * @param employmentGrowthRates
   *          the new employment growth rates
   */
  public void setEmploymentGrowthRates(
      final Set<EmploymentGrowthRate> employmentGrowthRates) {
    this.employmentGrowthRates = employmentGrowthRates;
  }

  /**
   * Gets the employment growth rate.
   * 
   * @param employmentSector
   *          the employment sector
   * @return the employment growth rate
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public double getEmploymentGrowthRate(final EmploymentSector employmentSector)
      throws WifInvalidInputException {
    final Set<EmploymentGrowthRate> growthRates = getEmploymentGrowthRates();
    for (final EmploymentGrowthRate employmentGrowthRate : growthRates) {
      if (employmentGrowthRate.getSector().equals(employmentSector)) {
        return employmentGrowthRate.getGrowthRate();
      }
    }
    throw new WifInvalidInputException(employmentSector.getLabel()
        + " sector not assigned");
  }

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
  public void setProjections(final Set<Projection> projections) {
    this.projections = projections;
  }

  /**
   * Instantiates a new demand config.
   */
  public DemandConfig() {
    super();
    projections = new HashSet<Projection>();
    sectors = new HashSet<EmploymentSector>();
    localJurisdictions = new HashSet<LocalJurisdiction>();
    demographicTrends = new HashSet<DemographicTrend>();
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
  public void setSectors(final Set<EmploymentSector> sectors) {
    this.sectors = sectors;
  }

  /**
   * Gets the demographic trends.
   * 
   * @return the demographic trends
   */
  public Set<DemographicTrend> getDemographicTrends() {
    return demographicTrends;
  }

  /**
   * Sets the demographic trends.
   * 
   * @param demographicTrends
   *          the new demographic trends
   */
  public void setDemographicTrends(final Set<DemographicTrend> demographicTrends) {
    this.demographicTrends = demographicTrends;
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
  public EmploymentSector getSectorByLabel(final String label)
      throws WifInvalidInputException {
    for (final EmploymentSector sector : this.getSectors()) {
      if (sector.getLabel().equals(label)) {
        return sector;
      }
    }
    throw new WifInvalidInputException(label + " the sector label not found");
  }

  /**
   * Gets the trend by label.
   * 
   * @param label
   *          the label
   * @return the trend by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemographicTrend getTrendByLabel(final String label)
      throws WifInvalidInputException {
    for (final DemographicTrend trend : this.getDemographicTrends()) {
      if (trend.getLabel().equals(label)) {
        return trend;
      }
    }
    throw new WifInvalidInputException(label + " trend label not found");
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
  public Projection getProjectionByLabel(final String label)
      throws WifInvalidInputException {
    for (final Projection proj : this.getProjections()) {
      if (proj.getLabel().equals(label)) {
        return proj;
      }
    }
    throw new WifInvalidInputException(label + " name not found");
  }

  /**
   * Gets the local jurisdictions.
   * 
   * @return the local jurisdictions
   */
  public Set<LocalJurisdiction> getLocalJurisdictions() {
    return localJurisdictions;
  }

  /**
   * Sets the local jurisdictions.
   * 
   * @param localJurisdictions
   *          the new local jurisdictions
   */
  public void setLocalJurisdictions(
      final Set<LocalJurisdiction> localJurisdictions) {
    this.localJurisdictions = localJurisdictions;
  }

  /**
   * Sets the include trends.
   * 
   * @param b
   *          the new include trends
   */
  public void setIncludeTrends(final boolean b) {
    this.includeTrends = b;
  }

  /**
   * Gets the include trends.
   * 
   * @return the include trends
   */
  public boolean getIncludeTrends() {
    return this.includeTrends;
  }

  /**
   * Gets the local jurisdiction by label.
   * 
   * @param label
   *          the label
   * @return the local jurisdiction by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public LocalJurisdiction getLocalJurisdictionByLabel(final String label)
      throws WifInvalidInputException {
    for (final LocalJurisdiction local : this.getLocalJurisdictions()) {
      if (local.getLabel().equals(label)) {
        return local;
      }
    }
    throw new WifInvalidInputException(label + " label not found");
  }

  /**
   * Checks if is parsed.
   * 
   * @return true, if is parsed
   */
  public boolean isParsed() {
    return parsed;
  }

  /**
   * Sets the parsed.
   * 
   * @param parsed
   *          the new parsed
   */
  public void setParsed(final boolean parsed) {
    this.parsed = parsed;
  }

  /**
   * Gets the enumeration district feature field name.
   * 
   * @return the enumeration district feature field name
   */
  public String getEnumerationDistrictFeatureFieldName() {
    return enumerationDistrictFeatureFieldName;
  }

  /**
   * Sets the enumeration district feature field name.
   * 
   * @param enumerationDistrictFeatureFieldName
   *          the new enumeration district feature field name
   */
  public void setEnumerationDistrictFeatureFieldName(
      final String enumerationDistrictFeatureFieldName) {
    this.enumerationDistrictFeatureFieldName = enumerationDistrictFeatureFieldName;
  }

  /**
   * Gets the local demand areas feature field name.
   * 
   * @return the local demand areas feature field name
   */
  public String getLocalDemandAreasFeatureFieldName() {
    return localDemandAreasFeatureFieldName;
  }

  /**
   * Sets the local demand areas feature field name.
   * 
   * @param localDemandAreasFeatureFieldName
   *          the new local demand areas feature field name
   */
  public void setLocalDemandAreasFeatureFieldName(
      final String localDemandAreasFeatureFieldName) {
    this.localDemandAreasFeatureFieldName = localDemandAreasFeatureFieldName;
  }

  public Set<DensityDemandInfo> getDensityDemandInfo() {
    return densityDemandInfo;
  }

  public void setDensityDemandInfo(
      final Set<DensityDemandInfo> densityDemandInfo) {
    this.densityDemandInfo = densityDemandInfo;
  }
}
