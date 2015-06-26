/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class AllocationScenario.
 */
@JsonPropertyOrder({ "id", "label", "docType", "suitabilityScenarioId",
    "demandScenarioId" })
public class AllocationScenario extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1778238396632217821L;

  /** The feature field name. */
  private String featureFieldName;

  /** The control scenario. */
  @JsonIgnore
  private AllocationControlScenario controlScenario;

  /** The control scenario id. */
  private String controlScenarioId;

  /** The demand scenario. */
  @JsonIgnore
  private DemandScenario demandScenario;

  @JsonIgnore
  private DemandOutcome manualdemandScenario;

  /** The demand scenario id. */
  private String demandScenarioId;

  /** The manual demand scenario id. */
  private String manualdemandScenarioId;

  /** The suitability scenario. */
  @JsonIgnore
  private SuitabilityScenario suitabilityScenario;

  /** The suitability scenario id. */
  private String suitabilityScenarioId;

  /** The land use order map. */
  private Map<String, Integer> landUseOrderMap;

  /** The land use order. */
  @JsonIgnore
  private Set<AllocationLU> landUseOrder;

  /**
   * The spatial pattern label. This component specifies the growth pattern to
   * be followed by the allocation process
   */
  private String spatialPatternLabel;

  /** The ready. */
  private boolean ready;

  private boolean isManual = false;

  public boolean isManual() {
    return isManual;
  }

  public void setManual(boolean isManual) {
    this.isManual = isManual;
  }

  /**
   * Sets the control scenario.
   * 
   * @param controlScenario
   *          the new control scenario
   */
  public void setControlScenario(AllocationControlScenario controlScenario) {
    this.controlScenario = controlScenario;
  }

  /**
   * Gets the control scenario.
   * 
   * @return the control scenario
   */
  public AllocationControlScenario getControlScenario() {
    return controlScenario;
  }

  /**
   * Sets the demand scenario.
   * 
   * @param demandScenario
   *          the new demand scenario
   */
  public void setDemandScenario(DemandScenario demandScenario) {
    this.demandScenario = demandScenario;
  }

  /**
   * Gets the demand scenario.
   * 
   * @return the demand scenario
   */
  public DemandScenario getDemandScenario() {
    return demandScenario;
  }

  /**
   * Sets the suitability scenario.
   * 
   * @param suitabilityScenario
   *          the new suitability scenario
   */
  public void setSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    this.suitabilityScenario = suitabilityScenario;
  }

  /**
   * Gets the suitability scenario.
   * 
   * @return the suitability scenario
   */
  public SuitabilityScenario getSuitabilityScenario() {
    return suitabilityScenario;
  }

  /**
   * Gets the allocation config.
   * 
   * @return the allocation config
   */
  @JsonIgnore
  public AllocationConfigs getAllocationConfig() {
    return getWifProject().getAllocationConfigs();
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.model.AbstractScenario#getFeatureFieldName()
   */
  @Override
  public String getFeatureFieldName() {
    return featureFieldName;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.model.AbstractScenario#setFeatureFieldName(java.lang.String
   * )
   */
  @Override
  public void setFeatureFieldName(String featureFieldName) {
    this.featureFieldName = featureFieldName;
  }

  /**
   * Gets the control scenario id.
   * 
   * @return the control scenario id
   */
  public String getControlScenarioId() {
    return controlScenarioId;
  }

  /**
   * Sets the control scenario id.
   * 
   * @param controlScenarioLabel
   *          the new control scenario id
   */
  public void setControlScenarioId(String controlScenarioLabel) {
    this.controlScenarioId = controlScenarioLabel;
  }

  /**
   * Gets the demand scenario id.
   * 
   * @return the demand scenario id
   */
  public String getDemandScenarioId() {
    return demandScenarioId;
  }

  /**
   * Sets the demand scenario id.
   * 
   * @param demandScenarioLabel
   *          the new demand scenario id
   */
  public void setDemandScenarioId(String demandScenarioLabel) {
    this.demandScenarioId = demandScenarioLabel;
  }

  /**
   * Gets the suitability scenario id.
   * 
   * @return the suitability scenario id
   */
  public String getSuitabilityScenarioId() {
    return suitabilityScenarioId;
  }

  /**
   * Sets the suitability scenario id.
   * 
   * @param suitabilityScenarioLabel
   *          the new suitability scenario id
   */
  public void setSuitabilityScenarioId(String suitabilityScenarioLabel) {
    this.suitabilityScenarioId = suitabilityScenarioLabel;
  }

  /**
   * Checks if is ready.
   * 
   * @return true, if is ready
   */
  public boolean isReady() {
    return ready;
  }

  /**
   * Sets the ready.
   * 
   * @param ready
   *          the new ready
   */
  public void setReady(boolean ready) {
    this.ready = ready;
  }

  /**
   * Gets the land use order map.
   * 
   * @return the land use order map
   */
  public Map<String, Integer> getLandUseOrderMap() {
    return landUseOrderMap;
  }

  /**
   * Sets the land use order map.
   * 
   * @param landUseOrder
   *          the land use order
   */
  public void setLandUseOrderMap(Map<String, Integer> landUseOrder) {
    this.landUseOrderMap = landUseOrder;
  }

  /**
   * Gets the land use order.
   * 
   * @return the land use order
   */
  public Set<AllocationLU> getLandUseOrder() {
    return landUseOrder;
  }

  /**
   * Sets the land use order.
   * 
   * @param landUseOrder
   *          the new land use order
   */
  public void setLandUseOrder(Set<AllocationLU> landUseOrder) {
    this.landUseOrder = landUseOrder;
  }

  /**
   * Instantiates a new allocation scenario.
   */
  public AllocationScenario() {
    super();
    landUseOrderMap = new HashMap<String, Integer>();
    landUseOrder = new HashSet<AllocationLU>();
  }

  /**
   * @return the spatialPatternLabel
   */
  public String getSpatialPatternLabel() {
    return spatialPatternLabel;
  }

  /**
   * @param spatialPatternLabel
   *          the spatialPatternLabel to set
   */

  public String getManualdemandScenarioId() {
    return manualdemandScenarioId;
  }

  public void setManualdemandScenarioId(String manualdemandScenarioId) {
    this.manualdemandScenarioId = manualdemandScenarioId;
  }

  public void setSpatialPatternLabel(String spatialPatternLabel) {
    this.spatialPatternLabel = spatialPatternLabel;
  }

  public DemandOutcome getManualdemandScenario() {
    return manualdemandScenario;
  }

  public void setManualdemandScenario(DemandOutcome manualdemandScenario) {
    this.manualdemandScenario = manualdemandScenario;
  }
}
