package au.org.aurin.wif.model.allocation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.AbstractLandUse;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>AllocationLU.java</b> See Section 4.6: These land uses are the ones that
 * currently exist and are used to map and describe the study area.
 * 
 * A major issue facing the use of different data sources for a particular study
 * area is the fact that different names may be used to classify the current,
 * projected, and planned land uses. In addition, the number of land use
 * categories used may differ between the sources. For example, an area’s GIS
 * data base may divide the current land uses into 15 different categories
 * including three residential categories: “low density residential,” “medium
 * density residential,” and “high density residential.” The current land use
 * plan may include 20 land uses with three different residential categories,
 * “rural residential,” “single family residential,” and “multi-family
 * residential.” And the suitability analysis may consider eight different land
 * uses, including only a single “residential” category. As a result, the first
 * task is defining the different categories of land uses and how they relate to
 * each other.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class AllocationLU extends AbstractLandUse {

  /**
   * 
   */
  private static final long serialVersionUID = 1891779438491310702L;

  /** Determines the allocation order. */
  @JsonIgnore
  private Integer priority;

  /** The allocation feature field name. */
  private String allocationFeatureFieldName;

  /** The total area. */

  private Double totalArea;

  /** The land use function by default is undefined unless explicitly assigned. */
  private LandUseFunction landUseFunction = LandUseFunction.NOT_DEVELOPABLE_OR_UNDEFINED;

  /** The area requirements. */
  @JsonIgnore
  private Set<AreaRequirement> areaRequirements;

  /** The associated lu. @uml.property name="associatedLU" */
  @JsonIgnore
  private SuitabilityLU associatedLU;

  @JsonIgnore
  private Map<String, String> associatedALUMap;

  /** The planned lu. */
  @JsonIgnore
  private PlannedLU plannedLU;

  /** The conversions. */
  @JsonIgnore
  private Set<SuitabilityRule> conversions;

  /** The demand infos. @uml.property name="demandInfos" */
  @JsonIgnore
  private Set<DemandInfo> demandInfos;

  /** The forecasts. */
  @JsonIgnore
  private Set<Forecast> forecasts;

  /** The employment sectors. */
  @JsonIgnore
  private Set<EmploymentSector> employmentSectors;

  @JsonIgnore
  private Set<String> sectorsLabel;

  /**
   * indicate whether the land use can be grouped in quarters The group quarters
   * population includes all people not living in households. Two general
   * categories of people in group quarters are generally recognized: 1 the
   * institutionalized population which includes people under formally
   * authorized supervised care or custody in institutions such as correctional
   * institutions, nursing homes, and juvenile institutions; and 2. the
   * non-institutionalized population which includes all people who live in
   * group quarters other than institutions such as college dormitories,
   * military quarters, and group homes.
   */
  private Boolean groupQuarters = false;
  /**
   * It may be necessary to define a new land use that does is not currently
   * used in a study area. For example, an area that currently does not have any
   * agricultural or open space conservation areas may want to consider
   * establishing them in the future. In this case, a “Conservation” use may not
   * currently exist, requiring that a new one be defined.
   */
  private Boolean newLU = false;
  /**
   * Local land uses refer to land uses that primarily serve a local market,
   * i.e., whose size is dependent primarily on population residing in the city,
   * neighborhood, or rural area in which the activity is located.
   */
  private Boolean local = false;
  /**
   * The second column of check boxes, labeled “Buildings,” is used to
   * differentiate between arts, entertainment, and recreational land uses that
   * have buildings located on them (e.g., libraries and museums) and those that
   * don’t (e.g., parks and recreational areas). All other land uses are assumed
   * to have buildings on them
   */
  private Boolean builtUp = false;
  /**
   * not subject to development, e.g. Water. If is set to to true, will be only
   * an "Existing LU", not taken into consideration for analysis
   */
  private Boolean notDevelopable = false;

  /** a feature whose land use is not defined/ unknown. */
  private Boolean notDefined = false;
  /**
   * will be used for agricultural, open space, environmental preservation and
   * similar uses.
   */
  private Boolean newPreservation = false;

  /**
   * Checks if is group quarters.
   * 
   * @return the groupQuarters
   */
  public Boolean isGroupQuarters() {
    return this.groupQuarters;
  }

  /**
   * Sets the group quarters.
   * 
   * @param groupQuarters
   *          the groupQuarters to set
   */
  public void setGroupQuarters(Boolean groupQuarters) {
    this.groupQuarters = groupQuarters;
  }

  /**
   * Gets the new lu.
   * 
   * @return the newLU
   */
  public Boolean getNewLU() {
    return this.newLU;
  }

  /**
   * Sets the new lu.
   * 
   * @param newLU
   *          the newLU to set
   */
  public void setNewLU(Boolean newLU) {
    this.newLU = newLU;
  }

  /**
   * Checks if is local.
   * 
   * @return the local
   */
  public Boolean isLocal() {
    return this.local;
  }

  /**
   * Sets the local.
   * 
   * @param local
   *          the local to set
   */
  public void setLocal(Boolean local) {
    this.local = local;
  }

  /**
   * Checks if is built up.
   * 
   * @return the builtUp
   */
  public Boolean isBuiltUp() {
    return this.builtUp;
  }

  /**
   * Sets the built up.
   * 
   * @param builtUp
   *          the builtUp to set
   */
  public void setBuiltUp(Boolean builtUp) {
    this.builtUp = builtUp;
  }

  /**
   * Checks if is not developable.
   * 
   * @return the notDevelopable
   */
  public Boolean isNotDevelopable() {
    return this.notDevelopable;
  }

  /**
   * Sets the not developable.
   * 
   * @param notDevelopable
   *          the notDevelopable to set
   */
  public void setNotDevelopable(Boolean notDevelopable) {
    this.notDevelopable = notDevelopable;
  }

  /**
   * Gets the not defined.
   * 
   * @return the notDefined
   */
  public Boolean getNotDefined() {
    return this.notDefined;
  }

  /**
   * Sets the not defined.
   * 
   * @param notDefined
   *          the notDefined to set
   */
  public void setNotDefined(Boolean notDefined) {
    this.notDefined = notDefined;
  }

  /**
   * Checks if is new preservation.
   * 
   * @return the newPreservation
   */
  public Boolean isNewPreservation() {
    return this.newPreservation;
  }

  /**
   * Sets the new preservation.
   * 
   * @param newPreservation
   *          the newPreservation to set
   */
  public void setNewPreservation(Boolean newPreservation) {
    this.newPreservation = newPreservation;
  }

  /**
   * Sets the demand infos.
   * 
   * @param demandInfos
   *          the new demand infos
   */
  public void setDemandInfos(Set<DemandInfo> demandInfos) {
    this.demandInfos = demandInfos;
  }

  /**
   * Adds the demand info.
   * 
   * @param demandInfos
   *          the demand infos
   */
  public void addDemandInfo(DemandInfo demandInfos) {
    this.demandInfos.add(demandInfos);
  }

  /**
   * Gets the demand infos.
   * 
   * @return the demand infos
   */
  public Set<DemandInfo> getDemandInfos() {
    return demandInfos;
  }

  /**
   * Gets the demand info by scenario name.
   * 
   * @param demandScn
   *          the new scenario name
   * @return the demand info by scenario name
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public DemandInfo getDemandInfoByScenario(DemandScenario demandScn)
      throws WifInvalidInputException {
    for (DemandInfo demandInfo : this.getDemandInfos()) {
      if (demandInfo.getDemandScenario().equals(demandScn)) {
        return demandInfo;

      }
    }
    throw new WifInvalidInputException("there's no demand information from  : "
        + this.getLabel() + " in scenario: " + demandScn);
  }

  /**
   * Checks for demand info in scenario.
   * 
   * @param newScenario
   *          the new scenario name
   * @return true, if successful
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public boolean hasDemandInfoInScenario(DemandScenario newScenario)
      throws WifInvalidInputException {
    for (DemandInfo demandInfo : this.getDemandInfos()) {
      if (demandInfo.getDemandScenario().getLabel()
          .equals(newScenario.getLabel())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the associated lu.
   * 
   * @param associatedLU
   *          the new associated lu
   */
  public void setAssociatedLU(SuitabilityLU associatedLU) {
    this.associatedLU = associatedLU;
  }

  /**
   * Gets the associated lu.
   * 
   * @return the associated lu
   */
  public SuitabilityLU getAssociatedLU() {
    return associatedLU;
  }

  /**
   * Sets the conversions.
   * 
   * @param conversions
   *          the new conversions
   */
  public void setConversions(Set<SuitabilityRule> conversions) {
    this.conversions = conversions;
  }

  /**
   * Gets the conversions.
   * 
   * @return the conversions
   */
  public Set<SuitabilityRule> getConversions() {
    return conversions;
  }

  /**
   * Sets the employment sectors.
   * 
   * @param employmentSectors
   *          the employmentSectors to set
   */
  public void setEmploymentSectors(Set<EmploymentSector> employmentSectors) {
    this.employmentSectors = employmentSectors;
  }

  /**
   * Gets the employment sectors.
   * 
   * @return the employmentSectors
   */
  public Set<EmploymentSector> getEmploymentSectors() {
    return employmentSectors;
  }

  /**
   * Adds the employment sector.
   * 
   * @param sector
   *          the sector
   */
  public void addEmploymentSector(EmploymentSector sector) {
    this.employmentSectors.add(sector);
  }

  /**
   * Sets the area requirements.
   * 
   * @param areaRequirements
   *          the areaRequirements to set
   */
  public void setAreaRequirements(Set<AreaRequirement> areaRequirements) {
    this.areaRequirements = areaRequirements;
  }

  /**
   * Gets the area requirements.
   * 
   * @return the areaRequirements
   */
  public Set<AreaRequirement> getAreaRequirements() {
    return areaRequirements;
  }

  /**
   * Adds the area requirement.
   * 
   * @param areq
   *          the areq
   */
  public void addAreaRequirement(AreaRequirement areq) {
    this.areaRequirements.add(areq);
  }

  /**
   * Adds the forecast.
   * 
   * @param f
   *          the f
   */
  public void addForecast(Forecast f) {
    this.forecasts.add(f);
  }

  /**
   * Gets the forecast.
   * 
   * @param projection
   *          the projection
   * @param demandScn
   *          the demand scn
   * @return the forecast
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public Forecast getForecast(Projection projection, DemandScenario demandScn)
      throws WifInvalidConfigException {
    for (Forecast f : this.forecasts) {
      if ((f.getDemandScenario() == demandScn)
          && (f.getProjection() == projection)) {
        return f;
      }
    }
    throw new WifInvalidConfigException(
        "there's no forecast information from  : " + this.getLabel()
            + " in scenario: " + demandScn.getLabel() + " Four projection:"
            + projection.getLabel());
  }

  /**
   * Checks if is associated.
   * 
   * @param edinfo
   *          the edinfo
   * @return true, if is associated
   */
  public boolean isAssociated(EmploymentDemandInfo edinfo) {
    if (this.employmentSectors.size() == 0) {
      if (this.sectorsLabel.contains(edinfo.getSector().getLabel())) {
        return true;
      }
      return false;
    } else {
      if (this.employmentSectors.contains(edinfo.getSector())) {
        return true;
      }
      return false;
    }
  }

  /**
   * Gets the area requirement.
   * 
   * @param projection
   *          the projection
   * @param demandScn
   *          the demand scn
   * @return the area requirement
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public AreaRequirement getAreaRequirement(Projection projection,
      DemandScenario demandScn) throws WifInvalidConfigException {
    for (AreaRequirement ar : this.areaRequirements) {
      if ((ar.getDemandScenario().getId().equals(demandScn.getId()))
          && (ar.getProjection().getLabel().equals(projection.getLabel()))) {
        if (ar instanceof LocalAreaRequirement) {
          if (this.isLocal()) {
            return ar;
          }
        } else {
          return ar;
        }
      }
    }
    throw new WifInvalidConfigException(
        "there's no area requirement information from  : " + this.getLabel()
            + " id " + this.getId() + " in scenario: " + demandScn.getLabel()
            + " Four projection:" + projection.getLabel());
  }

  public AreaRequirement getAreaRequirementIfExists(Projection projection,
      DemandScenario demandScn) {
    for (AreaRequirement ar : this.areaRequirements) {
      if ((ar.getDemandScenario() == demandScn)
          && (ar.getProjection() == projection)) {
        return ar;
      }
    }
    return null;
  }

  /**
   * Sets the forecasts.
   * 
   * @param forecasts
   *          the forecasts to set
   */
  public void setForecasts(Set<Forecast> forecasts) {
    this.forecasts = forecasts;
  }

  /**
   * Gets the forecasts.
   * 
   * @return the forecasts
   */
  public Set<Forecast> getForecasts() {
    return forecasts;
  }

  /**
   * Sets the order.
   * 
   * @param order
   *          the order to set
   */
  public void setPriority(Integer order) {
    this.priority = order;
  }

  /**
   * Gets the order.
   * 
   * @return the order
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the allocation label.
   * 
   * @param allocationLabel
   *          the allocationLabel to set
   */
  public void setAllocationFeatureFieldName(String allocationLabel) {
    this.allocationFeatureFieldName = allocationLabel;
  }

  /**
   * Gets the allocation label.
   * 
   * @return the allocationLabel
   */
  public String getAllocationFeatureFieldName() {
    return allocationFeatureFieldName;
  }

  /**
   * Sets the total area.
   * 
   * @param totalArea
   *          the totalArea to set
   */
  public void setTotalArea(Double totalArea) {
    this.totalArea = totalArea;
  }

  /**
   * Gets the total area.
   * 
   * @return the totalArea
   */
  public Double getTotalArea() {
    return totalArea;
  }

  /**
   * Sets the land use function.
   * 
   * @param landUseFunction
   *          the landUseFunction to set
   */
  public void setLandUseFunction(LandUseFunction landUseFunction) {
    this.landUseFunction = landUseFunction;
  }

  /**
   * Gets the land use function.
   * 
   * @return the landUseFunction
   */
  public LandUseFunction getLandUseFunction() {
    return landUseFunction;
  }

  /**
   * @return the plannedLU
   */
  public PlannedLU getPlannedLU() {
    return plannedLU;
  }

  /**
   * @param plannedLU
   *          the plannedLU to set
   */
  public void setPlannedLU(PlannedLU plannedLU) {
    this.plannedLU = plannedLU;
  }

  /**
   * 
   */
  public AllocationLU() {
    super();
    this.setAreaRequirements(new HashSet<AreaRequirement>());
    this.demandInfos = new HashSet<DemandInfo>();
    this.employmentSectors = new HashSet<EmploymentSector>();
    this.sectorsLabel = new HashSet<String>();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AllocationLU ["
        + (priority != null ? "priority=" + priority + ", " : "")
        + (allocationFeatureFieldName != null ? "allocationLabel="
            + allocationFeatureFieldName + ", " : "")
        + (totalArea != null ? "totalArea=" + totalArea + ", " : "")
        + (landUseFunction != null ? "landUseFunction=" + landUseFunction
            + ", " : "")
        + (areaRequirements != null ? "areaRequirements=" + areaRequirements
            + ", " : "")
        + (associatedLU != null ? "associatedLU=" + associatedLU + ", " : "")
        + (plannedLU != null ? "plannedLU=" + plannedLU + ", " : "")
        + (conversions != null ? "conversions=" + conversions + ", " : "")
        + (demandInfos != null ? "demandInfos=" + demandInfos + ", " : "")
        + (forecasts != null ? "forecasts=" + forecasts + ", " : "")
        + (employmentSectors != null ? "employmentSectors=" + employmentSectors
            + ", " : "")
        + (groupQuarters != null ? "groupQuarters=" + groupQuarters + ", " : "")
        + (newLU != null ? "newLU=" + newLU + ", " : "")
        + (local != null ? "local=" + local + ", " : "")
        + (builtUp != null ? "builtUp=" + builtUp + ", " : "")
        + (notDevelopable != null ? "notDevelopable=" + notDevelopable + ", "
            : "")
        + (notDefined != null ? "notDefined=" + notDefined + ", " : "")
        + (newPreservation != null ? "newPreservation=" + newPreservation
            + ", " : "")
        + (docType != null ? "docType=" + docType + ", " : "")
        + (getFeatureFieldName() != null ? "getFeatureFieldName()="
            + getFeatureFieldName() + ", " : "")
        + (getLabel() != null ? "getLabel()=" + getLabel() + ", " : "")
        + (getWifProject() != null ? "getWifProject()=" + getWifProject()
            + ", " : "")
        + (getProjectId() != null ? "getProjectId()=" + getProjectId() + ", "
            : "")
        + (getDocType() != null ? "getDocType()=" + getDocType() + ", " : "")
        + (getId() != null ? "getId()=" + getId() + ", " : "")
        + (getRevision() != null ? "getRevision()=" + getRevision() + ", " : "")
        + "isNew()=" + isNew() + "]";
  }

  /**
   * @return the associatedALUMap
   */
  @JsonProperty(value = "associatedALU")
  public Map<String, String> getAssociatedALUMap() {
    return associatedALUMap;
  }

  /**
   * @param associatedALUMap
   *          the associatedALUMap to set
   */
  @JsonProperty(value = "associatedALU")
  public void setAssociatedALUMap(Map<String, String> associatedALUMap) {
    this.associatedALUMap = associatedALUMap;
  }

  /**
   * @return the sectorsLabel
   */
  @JsonProperty(value = "sectors")
  public Set<String> getSectorsLabel() {
    return sectorsLabel;
  }

  /**
   * @param sectorsLabel
   *          the sectorsLabel to set
   */
  @JsonProperty(value = "sectors")
  public void setSectorsLabel(Set<String> sectorsSet) {
    this.sectorsLabel = sectorsSet;
  }

  public void addSectorLabel(String label) {
    this.sectorsLabel.add(label);
  }

  /**
   * Checks if is residential lu.
   * 
   * @return true, if is residential lu
   */
  @JsonIgnore
  public boolean isResidentialLU() {

    if (this.getLandUseFunction().equals(LandUseFunction.LBCS_1XXX)) {
      return true;
    }
    return false;
  }

  @JsonIgnore
  public boolean isEmploymentLU() {

    if (!this.getLandUseFunction().equals(LandUseFunction.LBCS_1XXX)
        && (!this.isLocal()) && !this.isNewPreservation()) {
      return true;
    }
    return false;
  }

}
