package au.org.aurin.wif.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opengis.feature.simple.SimpleFeatureType;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.authorisation.AuthorisationRight;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandConfigNew;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemandScenarioNew;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class WifProject.
 */

@JsonPropertyOrder({ "id", "name", "srs", "analysisOption", "originalUnits",
    "uazDataStoreURI", "existingLUAttributeName" })
// @JsonInclude(Include.NON_NULL)
public class WifProject extends CouchDoc implements Model2JsonMapped {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1749708370549745923L;

  /**
   * The schema. convenient holder of spatial information, this is not
   * persistent
   */
  @JsonIgnore
  private SimpleFeatureType schema;
  /** The parsed. */
  @JsonIgnore
  private boolean parsed = false;

  /** The study area. */
  private String studyArea;

  /** The bbox. */
  private String bbox;

  /** The uri local path where the extracted shape file is stored. */
  private String localShpFile;

  /** The area label. */
  private String areaLabel;

  /** The ready. */
  private Boolean ready = false;

  /** The setup completed. */
  private Boolean setupCompleted = false;

  /** The own geo datastore name. */
  private String ownGeoDatastoreName;

  /**
   * The upload uaz datatore uri. Which is created when the user wants to upload
   * the current analysis geospatial table.
   */
  private String uploadUAZDatatoreUri;

  /** The name. */
  private String name;

  /** The srs. */
  private String srs;

  /** The geometry column name. */
  private String geometryColumnName;

  /** The creation date. */
  private Date creationDate;

  /** The modified date. */
  private Date modifiedDate;

  /** The original units. */
  private String originalUnits;

  /** The uaz data store uri. */
  private String uazDataStoreURI;

  /** The existing lu attribute name. */
  private String existingLUAttributeName;

  /** The role owner. */
  private String roleOwner;

  /** The analysis option. */
  private String analysisOption;

  /** The suitability config. */
  private SuitabilityConfig suitabilityConfig;

  /** The authorisation rights. */
  @JsonIgnore
  private Set<AuthorisationRight> authorisationRights;

  /** The allocation land uses. */
  private Set<AllocationLU> allocationLandUses;

  /** The suitability l us. */
  private Set<SuitabilityLU> suitabilityLUs;

  /** The factors. */
  private Set<Factor> factors;

  /** The suitability scenarios. */
  @JsonIgnore
  private Set<SuitabilityScenario> suitabilityScenarios;

  /** The suitability scenarios map. */
  @JsonIgnore
  private Map<String, String> suitabilityScenariosMap;
  // Demand module

  /** The demand scenarios. */
  @JsonIgnore
  private Set<DemandScenario> demandScenarios;

  /** The demand scenarios map. */
  @JsonIgnore
  private Map<String, String> demandScenariosMap;

  /** The demand scenarios New. */
  @JsonIgnore
  private Set<DemandScenarioNew> demandScenariosNew;

  /** The demand scenarios New map. */
  @JsonIgnore
  private Map<String, String> demandScenariosNewMap;

  public Set<DemandScenarioNew> getDemandScenariosNew() {
    return demandScenariosNew;
  }

  public void setDemandScenariosNew(
      final Set<DemandScenarioNew> demandScenariosNew) {
    this.demandScenariosNew = demandScenariosNew;
  }

  @JsonProperty(value = "demandScenarioNews")
  public Map<String, String> getDemandScenariosNewMap() {
    return demandScenariosNewMap;
  }

  @JsonProperty(value = "demandScenarioNews")
  public void setDemandScenariosNewMap(
      final Map<String, String> demandScenariosNewMap) {
    this.demandScenariosNewMap = demandScenariosNewMap;
  }

  /** The manual demand scenarios. */
  @JsonIgnore
  private Set<DemandOutcome> demandOutcomes;

  /** The demand scenarios map. */
  @JsonIgnore
  private Map<String, String> demandOutcomesMap;

  /** The demand config. */
  @JsonIgnore
  private DemandConfig demandConfig;

  /** The demand config new. */
  @JsonIgnore
  private DemandConfigNew demandConfigNew;

  /** The Manual demand config. */
  @JsonIgnore
  private AllocationConfigs allocationConfigs;

  /** The allocation configs id. */
  private String allocationConfigsId;

  /** The demand config id. */
  private String demandConfigId;

  /** The demand config New id. */
  private String demandConfigNewId;

  // Allocation module
  /** The allocation scenarios. */
  @JsonIgnore
  private Set<AllocationScenario> allocationScenarios;

  /** The allocation scenarios map. */
  @JsonIgnore
  private Map<String, String> allocationScenariosMap;

  /** The allocation control scenarios. */
  @JsonIgnore
  private Set<AllocationControlScenario> allocationControlScenarios;

  /** The allocation control scenarios map. */
  @JsonIgnore
  private Map<String, String> allocationControlScenariosMap;

  /** The allocation config. */
  private AllocationConfig allocationConfig;

  /**
   * Instantiates a new wif project.
   */
  public WifProject() {
    allocationLandUses = new HashSet<AllocationLU>();
    suitabilityLUs = new HashSet<SuitabilityLU>();
    factors = new HashSet<Factor>();
    suitabilityScenarios = new HashSet<SuitabilityScenario>();
    suitabilityScenariosMap = new HashMap<String, String>();
    demandScenarios = new HashSet<DemandScenario>();
    demandScenariosMap = new HashMap<String, String>();
    allocationScenarios = new HashSet<AllocationScenario>();
    allocationScenariosMap = new HashMap<String, String>();

    demandOutcomes = new HashSet<DemandOutcome>();
    demandOutcomesMap = new HashMap<String, String>();

    allocationConfig = new AllocationConfig();

    allocationControlScenarios = new HashSet<AllocationControlScenario>();
    allocationControlScenariosMap = new HashMap<String, String>();

    demandScenariosNew = new HashSet<DemandScenarioNew>();
    demandScenariosNewMap = new HashMap<String, String>();

  }

  /**
   * Gets the creation date.
   * 
   * @return the creation date
   */
  public Date getCreationDate() {
    return this.creationDate;
  }

  /**
   * Sets the creation date.
   * 
   * @param creationDate
   *          the new creation date
   */
  public void setCreationDate(final Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets the modified date.
   * 
   * @return the modified date
   */
  public Date getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * Sets the modified date.
   * 
   * @param modifiedDate
   *          the new modified date
   */
  public void setModifiedDate(final Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.model.Model2JsonMapped#getLabel()
   */
  /**
   * Gets the label.
   * 
   * @return the label
   */
  @JsonIgnore
  public String getLabel() {
    return name;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Gets the study area.
   * 
   * @return the study area
   */
  public String getStudyArea() {
    return studyArea;
  }

  /**
   * Sets the study area.
   * 
   * @param studyArea
   *          the new study area
   */
  public void setStudyArea(final String studyArea) {
    this.studyArea = studyArea;
  }

  /**
   * Gets the projections.
   * 
   * @return the projections
   */
  @JsonIgnore
  public Set<Projection> getProjections() {
    return this.demandConfig.getProjections();
  }

  /**
   * Sets the projections.
   * 
   * @param projections
   *          the new projections
   */
  @JsonIgnore
  public void setProjections(final Set<Projection> projections) {
    this.demandConfig.setProjections(projections);
  }

  /**
   * Gets the suitability scenarios.
   * 
   * @return the suitability scenarios
   */
  public Set<SuitabilityScenario> getSuitabilityScenarios() {
    return this.suitabilityScenarios;
  }

  /**
   * Gets the suitability scenarios map.
   * 
   * @return the suitability scenarios map
   */
  @JsonProperty(value = "suitabilityScenarios")
  public Map<String, String> getSuitabilityScenariosMap() {
    return this.suitabilityScenariosMap;
  }

  /**
   * Sets the suitability scenarios map.
   * 
   * @param idLabelMap
   *          the id label map
   */
  @JsonProperty(value = "suitabilityScenarios")
  public void setSuitabilityScenariosMap(final Map<String, String> idLabelMap) {
    this.suitabilityScenariosMap = idLabelMap;
  }

  /**
   * Sets the suitability scenarios.
   * 
   * @param suitabilityScenarios
   *          the new suitability scenarios
   */
  public void setSuitabilityScenarios(
      final Set<SuitabilityScenario> suitabilityScenarios) {
    this.suitabilityScenarios = suitabilityScenarios;
  }

  /**
   * Gets the demand scenarios.
   * 
   * @return the demand scenarios
   */
  public Set<DemandScenario> getDemandScenarios() {
    return this.demandScenarios;
  }

  public Set<DemandOutcome> getdemandOutcomes() {
    return this.demandOutcomes;
  }

  /**
   * Sets the demand scenarios.
   * 
   * @param demandScenarios
   *          the new demand scenarios
   */
  public void setDemandScenarios(final Set<DemandScenario> demandScenarios) {
    this.demandScenarios = demandScenarios;
  }

  public void setdemandOutcomes(final Set<DemandOutcome> demandOutcomes) {
    this.demandOutcomes = demandOutcomes;
  }

  /**
   * Gets the allocation land uses.
   * 
   * @return the allocation land uses
   */
  public Set<AllocationLU> getAllocationLandUses() {
    return this.allocationLandUses;
  }

  /**
   * Sets the allocation land uses.
   * 
   * @param existingLandUses
   *          the new allocation land uses
   */
  public void setAllocationLandUses(final Set<AllocationLU> existingLandUses) {
    this.allocationLandUses = existingLandUses;
  }

  /**
   * Gets the original units.
   * 
   * @return the original units
   */
  public String getOriginalUnits() {
    return this.originalUnits;
  }

  /**
   * Sets the original units.
   * 
   * @param originalUnits
   *          the new original units
   */
  public void setOriginalUnits(final String originalUnits) {
    this.originalUnits = originalUnits;
  }

  /**
   * Gets the suitability scenario by label.
   * 
   * @param newScenarioLabel
   *          the new scenario label
   * @return the suitability scenario by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public SuitabilityScenario getSuitabilityScenarioByLabel(
      final String newScenarioLabel) throws WifInvalidInputException {
    for (final SuitabilityScenario scn : this.getSuitabilityScenarios()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  /**
   * Gets the demand scenario by label.
   * 
   * @param newScenarioLabel
   *          the new scenario label
   * @return the demand scenario by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandScenario getDemandScenarioByLabel(final String newScenarioLabel)
      throws WifInvalidInputException {
    for (final DemandScenario scn : this.getDemandScenarios()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  /**
   * Gets the allocation scenario by label.
   * 
   * @param newScenarioLabel
   *          the new scenario label
   * @return the allocation scenario by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public AllocationScenario getAllocationScenarioByLabel(
      final String newScenarioLabel) throws WifInvalidInputException {
    for (final AllocationScenario scn : this.getAllocationScenarios()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  /**
   * Gets the existing land use by id.
   * 
   * @param id
   *          the id
   * @return the existing land use by id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public AllocationLU getExistingLandUseById(final String id)
      throws WifInvalidInputException {
    for (final AllocationLU alu : this.getAllocationLandUses()) {
      if (alu.getId().equals(id)) {
        return alu;
      }
    }
    throw new WifInvalidInputException(id + " id not found");
  }

  /**
   * Gets the suitability lu by name.
   * 
   * @param sLUName
   *          the s lu name
   * @return the suitability lu by name
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public SuitabilityLU getSuitabilityLUByName(final String sLUName)
      throws WifInvalidInputException {
    for (final SuitabilityLU slu : this.getSuitabilityLUs()) {
      if (slu.getLabel().equals(sLUName)) {
        return slu;
      }
    }
    throw new WifInvalidInputException(sLUName + " label not found");
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
    throw new WifInvalidInputException(label + " label not found");
  }

  /**
   * Gets the suitability lu by id.
   * 
   * @param sluid
   *          the sluid
   * @return the suitability lu by id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public SuitabilityLU getSuitabilityLUById(final String sluid)
      throws WifInvalidInputException {
    for (final SuitabilityLU slu : this.getSuitabilityLUs()) {
      if (slu.getId().equals(sluid)) {
        return slu;
      }
    }
    throw new WifInvalidInputException(sluid + " id not found");
  }

  /**
   * Gets the factor by id.
   * 
   * @param id
   *          the id
   * @return the factor by id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Factor getFactorById(final String id) throws WifInvalidInputException {
    for (final Factor factor : this.getFactors()) {
      if (factor.getId().equals(id)) {
        return factor;
      }
    }
    throw new WifInvalidInputException(id + " id not found");
  }

  /**
   * Gets the factor by label.
   * 
   * @param label
   *          the label
   * @return the factor by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Factor getFactorByLabel(final String label)
      throws WifInvalidInputException {
    for (final Factor factor : this.getFactors()) {
      if (factor.getLabel().equals(label)) {
        return factor;
      }
    }
    throw new WifInvalidInputException(label + " label not found");
  }

  /**
   * Gets the existing land use by label.
   * 
   * @param aLULabel
   *          the a lu label
   * @return the existing land use by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public AllocationLU getExistingLandUseByLabel(final String aLULabel)
      throws WifInvalidInputException {
    for (final AllocationLU alu : this.getAllocationLandUses()) {
      if (alu.getLabel().equals(aLULabel)) {
        return alu;
      }
    }
    throw new WifInvalidInputException(aLULabel + " Label not found");
  }

  /**
   * Gets the uaz data store uri.
   * 
   * @return the uaz data store uri
   */
  public String getUazDataStoreURI() {
    return uazDataStoreURI;
  }

  /**
   * Sets the uaz data store uri.
   * 
   * @param uazDataStoreURI
   *          the new uaz data store uri
   */
  public void setUazDataStoreURI(final String uazDataStoreURI) {
    this.uazDataStoreURI = uazDataStoreURI;
  }

  /**
   * Sets the suitability l us.
   * 
   * @param suitabilityLUs
   *          the new suitability l us
   */
  public void setSuitabilityLUs(final Set<SuitabilityLU> suitabilityLUs) {
    this.suitabilityLUs = suitabilityLUs;
  }

  /**
   * Gets the suitability l us.
   * 
   * @return the suitability l us
   */
  public Set<SuitabilityLU> getSuitabilityLUs() {
    return suitabilityLUs;
  }

  /**
   * Sets the factors.
   * 
   * @param factors
   *          the new factors
   */
  public void setFactors(final Set<Factor> factors) {
    this.factors = factors;
  }

  /**
   * Gets the factors.
   * 
   * @return the factors
   */
  public Set<Factor> getFactors() {
    return factors;
  }

  /**
   * Sets the sectors.
   * 
   * @param sectors
   *          the new sectors
   */
  @JsonIgnore
  public void setSectors(final Set<EmploymentSector> sectors) {
    this.demandConfig.setSectors(sectors);
  }

  /**
   * Gets the sectors.
   * 
   * @return the sectors
   */
  @JsonIgnore
  public Set<EmploymentSector> getSectors() {
    return this.demandConfig.getSectors();
  }

  /**
   * Sets the existing lu attribute name.
   * 
   * @param existingLULabel
   *          the new existing lu attribute name
   */
  public void setExistingLUAttributeName(final String existingLULabel) {
    this.existingLUAttributeName = existingLULabel;
  }

  /**
   * Gets the existing lu attribute name.
   * 
   * @return the existing lu attribute name
   */
  public String getExistingLUAttributeName() {
    return existingLUAttributeName;
  }

  /**
   * Gets the authorisation rights.
   * 
   * @return the authorisation rights
   */
  public Set<AuthorisationRight> getAuthorisationRights() {
    return authorisationRights;
  }

  /**
   * Sets the authorisation rights.
   * 
   * @param authorisationRights
   *          the new authorisation rights
   */
  public void setAuthorisationRights(
      final Set<AuthorisationRight> authorisationRights) {
    this.authorisationRights = authorisationRights;
  }

  /**
   * Checks if is authorised.
   * 
   * @param role
   *          the role
   * @return true, if is authorised
   */
  public boolean isAuthorised(final String role) {
    final Set<AuthorisationRight> authorisationRights2 = getAuthorisationRights();
    for (final AuthorisationRight authorisationRight : authorisationRights2) {
      if (authorisationRight.getUserRole().getRoleName().equalsIgnoreCase(role)) {
        return true;
      }
    }
    return false;
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
    for (final Projection scn : this.getProjections()) {
      if (scn.getLabel().equals(label)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(label + " name not found");
  }

  /**
   * Gets the suitability config.
   * 
   * @return the suitability config
   */
  public SuitabilityConfig getSuitabilityConfig() {
    return suitabilityConfig;
  }

  /**
   * Sets the suitability config.
   * 
   * @param config
   *          the new suitability config
   */
  public void setSuitabilityConfig(final SuitabilityConfig config) {
    this.suitabilityConfig = config;
  }

  /**
   * Gets the analysis option.
   * 
   * @return the analysis option
   */
  public String getAnalysisOption() {
    return analysisOption;
  }

  /**
   * Sets the analysis option.
   * 
   * @param analysisOption
   *          the new analysis option
   */
  public void setAnalysisOption(final String analysisOption) {
    this.analysisOption = analysisOption;
  }

  /**
   * Gets the role owner.
   * 
   * @return the role owner
   */
  public String getRoleOwner() {
    return roleOwner;
  }

  /**
   * Sets the role owner.
   * 
   * @param roleOwner
   *          the new role owner
   */
  public void setRoleOwner(final String roleOwner) {
    this.roleOwner = roleOwner;
  }

  /**
   * Gets the demographic trends.
   * 
   * @return the demographic trends
   */
  @JsonIgnore
  public Set<DemographicTrend> getDemographicTrends() {
    return getDemandConfig().getDemographicTrends();
  }

  /**
   * Sets the demographic trends.
   * 
   * @param demographicTrends
   *          the new demographic trends
   */
  @JsonIgnore
  public void setDemographicTrends(final Set<DemographicTrend> demographicTrends) {
    getDemandConfig().setDemographicTrends(demographicTrends);
  }

  /**
   * Update new values.
   * 
   * @param project
   *          the project
   */
  public void updateNewValues(final WifProject project) {
    if (project.getName() != null) {
      this.setName(project.getName());
    }
    if (project.getOriginalUnits() != null) {
      this.setOriginalUnits(project.getOriginalUnits());
    }
    if (project.getExistingLUAttributeName() != null) {
      this.setExistingLUAttributeName(project.getExistingLUAttributeName());
    }
    if (project.getAnalysisOption() != null) {
      this.setAnalysisOption(project.getAnalysisOption());
    }
    if (project.getUazDataStoreURI() != null) {
      this.setUazDataStoreURI(project.getUazDataStoreURI());
    }
    if (project.getStudyArea() != null) {
      this.setStudyArea(project.getStudyArea());
    }
  }

  /**
   * Aand.
   * 
   * @return the string
   */
  public String aand() {
    return srs;
  }

  /**
   * Sets the srs.
   * 
   * @param SRS
   *          the new srs
   */
  public void setSrs(final String SRS) {
    this.srs = SRS;
  }

  /**
   * Gets the bbox.
   * 
   * @return the bbox
   */
  public String getBbox() {
    return bbox;
  }

  /**
   * Sets the bbox.
   * 
   * @param bbox
   *          the new bbox
   */
  public void setBbox(final String bbox) {
    this.bbox = bbox;
  }

  /**
   * Gets the demand config.
   * 
   * @return the demand config
   */
  public DemandConfig getDemandConfig() {
    return demandConfig;
  }

  /**
   * Sets the demand config.
   * 
   * @param demandConfig
   *          the new demand config
   */
  public void setDemandConfig(final DemandConfig demandConfig) {
    this.demandConfig = demandConfig;
  }

  /**
   * Gets the ready.
   * 
   * @return the ready
   */
  public Boolean getReady() {
    return ready;
  }

  /**
   * Sets the ready.
   * 
   * @param ready
   *          the new ready
   */
  public void setReady(final Boolean ready) {
    this.ready = ready;
  }

  /**
   * Gets the include trends.
   * 
   * @return the include trends
   */
  @JsonIgnore
  public Boolean getIncludeTrends() {
    return demandConfig.getIncludeTrends();
  }

  /**
   * Sets the include trends.
   * 
   * @param includeTrends
   *          the new include trends
   */
  @JsonIgnore
  public void setIncludeTrends(final Boolean includeTrends) {
    demandConfig.setIncludeTrends(includeTrends);
  }

  /**
   * Checks if is being used.
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @return true, if is being used
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public boolean isBeingUsed(final SuitabilityScenario suitabilityScenario)
      throws WifInvalidInputException {
    final Set<AllocationScenario> allocationScenarios = getAllocationScenarios();
    for (final AllocationScenario allocationScenario : allocationScenarios) {
      if (allocationScenario.getSuitabilityScenario().getId()
          .equals(suitabilityScenario.getId())) {

        throw new WifInvalidInputException(
            "this is scenario is associated with allocationScenario! = "
                + allocationScenario.getLabel() + " cannot be deleted");
      }
    }
    return false;
  }

  /**
   * Gets the own geo datastore name.
   * 
   * @return the own geo datastore name
   */
  public String getOwnGeoDatastoreName() {
    return ownGeoDatastoreName;
  }

  /**
   * Sets the own geo datastore name.
   * 
   * @param ownGeoDatastoreName
   *          the new own geo datastore name
   */
  public void setOwnGeoDatastoreName(final String ownGeoDatastoreName) {
    this.ownGeoDatastoreName = ownGeoDatastoreName;
  }

  /**
   * Gets the setup completed.
   * 
   * @return the setup completed
   */
  public Boolean getSetupCompleted() {
    return setupCompleted;
  }

  /**
   * Sets the setup completed.
   * 
   * @param setupCompleted
   *          the new setup completed
   */
  public void setSetupCompleted(final Boolean setupCompleted) {
    this.setupCompleted = setupCompleted;
  }

  /**
   * Gets the geometry column name.
   * 
   * @return the geometry column name
   */
  public String getGeometryColumnName() {
    return geometryColumnName;
  }

  /**
   * Sets the geometry column name.
   * 
   * @param geometryColumnName
   *          the new geometry column name
   */
  public void setGeometryColumnName(final String geometryColumnName) {
    this.geometryColumnName = geometryColumnName;
  }

  /**
   * Gets the demand config id.
   * 
   * @return the demand config id
   */
  public String getDemandConfigId() {
    return demandConfigId;
  }

  /**
   * Sets the demand config id.
   * 
   * @param demandConfigId
   *          the new demand config id
   */
  public void setDemandConfigId(final String demandConfigId) {
    this.demandConfigId = demandConfigId;
  }

  /**
   * Gets the demand scenarios map.
   * 
   * @return the demand scenarios map
   */
  @JsonProperty(value = "demandScenarios")
  public Map<String, String> getDemandScenariosMap() {
    return demandScenariosMap;
  }

  @JsonProperty(value = "demandOutcomes")
  public Map<String, String> getDemandOutcomesMap() {
    return demandOutcomesMap;
  }

  /**
   * Sets the demand scenarios map.
   * 
   * @param demandScenariosMap
   *          the demand scenarios map
   */
  @JsonProperty(value = "demandScenarios")
  public void setDemandScenariosMap(final Map<String, String> demandScenariosMap) {
    this.demandScenariosMap = demandScenariosMap;
  }

  @JsonProperty(value = "demandOutcomes")
  public void setDemandOutcomesMap(final Map<String, String> demandOutcomesMap) {
    this.demandOutcomesMap = demandOutcomesMap;
  }

  /**
   * Gets the allocation scenarios.
   * 
   * @return the allocation scenarios
   */
  public Set<AllocationScenario> getAllocationScenarios() {
    return allocationScenarios;
  }

  /**
   * Sets the allocation scenarios.
   * 
   * @param allocationScenarios
   *          the new allocation scenarios
   */
  public void setAllocationScenarios(
      final Set<AllocationScenario> allocationScenarios) {
    this.allocationScenarios = allocationScenarios;
  }

  /**
   * Gets the allocation scenarios map.
   * 
   * @return the allocation scenarios map
   */
  @JsonProperty(value = "allocationScenarios")
  public Map<String, String> getAllocationScenariosMap() {
    return allocationScenariosMap;
  }

  /**
   * Sets the allocation scenarios map.
   * 
   * @param allocationScenariosMap
   *          the allocation scenarios map
   */
  @JsonProperty(value = "allocationScenarios")
  public void setAllocationScenariosMap(
      final Map<String, String> allocationScenariosMap) {
    this.allocationScenariosMap = allocationScenariosMap;
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
   * Gets the allocation config.
   * 
   * @return the allocation config
   */
  public AllocationConfig getAllocationConfig() {
    return allocationConfig;
  }

  /**
   * Sets the allocation config.
   * 
   * @param allocationConfig
   *          the new allocation config
   */
  public void setAllocationConfig(final AllocationConfig allocationConfig) {
    this.allocationConfig = allocationConfig;

  }

  /**
   * Gets the srs.
   * 
   * @return the srs
   */
  public String getSrs() {
    return srs;
  }

  /**
   * Gets the area label.
   * 
   * @return the area label
   */
  public String getAreaLabel() {
    return areaLabel;
  }

  /**
   * Sets the area label.
   * 
   * @param areaLabel
   *          the new area label
   */
  public void setAreaLabel(final String areaLabel) {
    this.areaLabel = areaLabel;
  }

  /**
   * Gets the upload uaz datatore uri.
   * 
   * @return the upload uaz datatore uri
   */
  public String getUploadUAZDatatoreUri() {
    return uploadUAZDatatoreUri;
  }

  /**
   * Sets the upload uaz datatore uri.
   * 
   * @param uploadUAZDatatoreUri
   *          the new upload uaz datatore uri
   */
  public void setUploadUAZDatatoreUri(final String uploadUAZDatatoreUri) {
    this.uploadUAZDatatoreUri = uploadUAZDatatoreUri;
  }

  /**
   * Gets the local shp file.
   * 
   * @return the localShpFile
   */
  public String getLocalShpFile() {
    return localShpFile;
  }

  /**
   * Sets the local shp file.
   * 
   * @param localShpFile
   *          the localShpFile to set
   */
  public void setLocalShpFile(final String localShpFile) {
    this.localShpFile = localShpFile;
  }

  /**
   * @return the schema
   */
  public SimpleFeatureType getSchema() {
    return schema;
  }

  /**
   * @param schema
   *          the schema to set
   */
  public void setSchema(final SimpleFeatureType schema) {
    this.schema = schema;
  }

  /**
   * Gets the demand scenario by label.
   * 
   * @param newScenarioLabel
   *          the new scenario label
   * @return the demand scenario by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandOutcome getManualDemandScenarioByLabel(
      final String newScenarioLabel) throws WifInvalidInputException {
    for (final DemandOutcome scn : this.getdemandOutcomes()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  /**
   * Gets the allocation configs
   * 
   * @return the allocation configs
   */
  public AllocationConfigs getAllocationConfigs() {
    return allocationConfigs;
  }

  /**
   * sets the allocation configs
   * 
   */
  public void setAllocationConfigs(final AllocationConfigs allocationConfigs) {
    this.allocationConfigs = allocationConfigs;
  }

  /**
   * Gets the allocation configs id.
   * 
   * @return the allocation configs id
   */
  public String getAllocationConfigsId() {
    return allocationConfigsId;
  }

  /**
   * Sets the allocation configs id.
   * 
   */

  public void setAllocationConfigsId(final String allocationConfigsId) {
    this.allocationConfigsId = allocationConfigsId;
  }

  public Set<AllocationControlScenario> getAllocationControlScenarios() {
    return allocationControlScenarios;
  }

  public void setAllocationControlScenarios(
      final Set<AllocationControlScenario> allocationControlScenarios) {
    this.allocationControlScenarios = allocationControlScenarios;
  }

  @JsonProperty(value = "allocationControlScenarios")
  public Map<String, String> getAllocationControlScenariosMap() {
    return allocationControlScenariosMap;
  }

  @JsonProperty(value = "allocationControlScenarios")
  public void setAllocationControlScenariosMap(
      final Map<String, String> allocationControlScenariosMap) {
    this.allocationControlScenariosMap = allocationControlScenariosMap;
  }

  public AllocationControlScenario getAllocationControlScenarioByLabel(
      final String newScenarioLabel) throws WifInvalidInputException {
    for (final AllocationControlScenario scn : this
        .getAllocationControlScenarios()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  public DemandScenarioNew getDemandScenarioNewByLabel(
      final String newScenarioLabel) throws WifInvalidInputException {
    for (final DemandScenarioNew scn : this.getDemandScenariosNew()) {
      if (scn.getLabel().equals(newScenarioLabel)) {
        return scn;
      }
    }
    throw new WifInvalidInputException(newScenarioLabel + " name not found");
  }

  public DemandConfigNew getDemandConfigNew() {
    return demandConfigNew;
  }

  public void setDemandConfigNew(final DemandConfigNew demandConfigNew) {
    this.demandConfigNew = demandConfigNew;
  }

  public String getDemandConfigNewId() {
    return demandConfigNewId;
  }

  public void setDemandConfigNewId(final String demandConfigNewId) {
    this.demandConfigNewId = demandConfigNewId;
  }

}
