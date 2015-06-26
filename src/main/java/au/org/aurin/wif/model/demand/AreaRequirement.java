package au.org.aurin.wif.model.demand;

import au.org.aurin.wif.model.CouchDoc;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.allocation.AllocationLU;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class AreaRequirement.
 */
@JsonPropertyOrder({ "id", "docType" })
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class AreaRequirement extends CouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5192003471132979481L;

  /** The allocation lu. */
  @JsonIgnore
  private AllocationLU allocationLU;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The allocation lu label. */
  private String allocationLULabel;

  /** The required area. */
  private Double requiredArea;

  /** The unchanged area. */
  private Double unchangedArea;

  /** The new area. */
  private Double newArea;

  /** The projection. */
  @JsonIgnore
  private Projection projection;

  /** The projection label. */
  private String projectionLabel;

  /** The demand scenario. */
  @JsonIgnore
  private DemandScenario demandScenario;

  /** The demand scenario id. */
  private String demandScenarioId;

  /**
   * Sets the required area.
   * 
   * @param requiredArea
   *          the new required area
   */
  public void setRequiredArea(Double requiredArea) {
    this.requiredArea = requiredArea;
  }

  /**
   * Gets the required area.
   * 
   * @return the required area
   */
  public Double getRequiredArea() {
    return requiredArea;
  }

  /**
   * Sets the projection.
   * 
   * @param projection
   *          the new projection
   */
  public void setProjection(Projection projection) {
    this.projection = projection;
  }

  /**
   * Gets the projection.
   * 
   * @return the projection
   */
  public Projection getProjection() {
    return projection;
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
   * Gets the unchanged area.
   * 
   * @return the unchanged area
   */
  public Double getUnchangedArea() {
    return unchangedArea;
  }

  /**
   * Sets the unchanged area.
   * 
   * @param unchangedArea
   *          the new unchanged area
   */
  public void setUnchangedArea(Double unchangedArea) {
    this.unchangedArea = unchangedArea;
  }

  /**
   * Gets the new area.
   * 
   * @return the new area
   */
  public Double getNewArea() {
    return newArea;
  }

  /**
   * Sets the new area.
   * 
   * @param newArea
   *          the new new area
   */
  public void setNewArea(Double newArea) {
    this.newArea = newArea;
  }

  /**
   * Gets the allocation lu.
   * 
   * @return the allocation lu
   */
  public AllocationLU getAllocationLU() {
    return allocationLU;
  }

  /**
   * Sets the allocation lu.
   * 
   * @param areaRequirementLU
   *          the new allocation lu
   */
  public void setAllocationLU(AllocationLU areaRequirementLU) {
    this.allocationLU = areaRequirementLU;
    this.allocationLUId = areaRequirementLU.getId();
    this.allocationLULabel = areaRequirementLU.getLabel();
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
   * @param demandScenarioId
   *          the new demand scenario id
   */
  public void setDemandScenarioId(String demandScenarioId) {
    this.demandScenarioId = demandScenarioId;
  }

  /**
   * Gets the projection label.
   * 
   * @return the projection label
   */
  public String getProjectionLabel() {
    return projectionLabel;
  }

  /**
   * Sets the projection label.
   * 
   * @param projectionLabel
   *          the new projection label
   */
  public void setProjectionLabel(String projectionLabel) {
    this.projectionLabel = projectionLabel;
  }

  /**
   * Gets the allocation lu id.
   * 
   * @return the allocation lu id
   */
  public String getAllocationLUId() {
    return allocationLUId;
  }

  /**
   * Sets the allocation lu id.
   * 
   * @param areaRequirementLUId
   *          the new allocation lu id
   */
  public void setAllocationLUId(String areaRequirementLUId) {
    this.allocationLUId = areaRequirementLUId;
  }

  /**
   * Gets the allocation lu label.
   * 
   * @return the allocation lu label
   */
  public String getAllocationLULabel() {
    return allocationLULabel;
  }

  /**
   * Sets the allocation lu label.
   * 
   * @param areaRequirementLULabel
   *          the new allocation lu label
   */
  public void setAllocationLULabel(String areaRequirementLULabel) {
    this.allocationLULabel = areaRequirementLULabel;
  }

}
