/*
 *
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandOutcome;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * <b>DemandInfo.java</b> : Holds the configuration parameters for Demand
 * Information. It is subclass to hold a specific information for a given land
 * use type.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ManualDemandInfo {

  /** The demand scenario. @uml.property name="the demandScenario" */
  @JsonIgnore
  private DemandOutcome ManualdemandScenario;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The allocation lu. */
  @JsonIgnore
  private AllocationLU allocationLU;

  /**
   * Instantiates a new demand info.
   */
  public ManualDemandInfo() {
    super();
  }

  /**
   * Instantiates a new demand info.
   * 
   * @param copy
   *          the copy
   */
  public ManualDemandInfo(ManualDemandInfo copy) {

  }

  /**
   * Sets the demand scenario.
   * 
   * @param demandScenario
   *          the new demand scenario
   */
  public void setManualDemandScenario(DemandOutcome ManualdemandScenario) {
    this.ManualdemandScenario = ManualdemandScenario;
  }

  /**
   * Gets the demand scenario.
   * 
   * @return the demand scenario
   */
  public DemandOutcome getManualDemandScenario() {
    return ManualdemandScenario;
  }

  /**
   * Gets the allocation lu id.
   * 
   * @return the allocationLUId
   */
  public String getAllocationLUId() {
    return allocationLUId;
  }

  /**
   * Sets the allocation lu id.
   * 
   * @param allocationLUId
   *          the allocationLUId to set
   */
  public void setAllocationLUId(String allocationLUId) {
    this.allocationLUId = allocationLUId;
  }

  /**
   * Gets the allocation lu.
   * 
   * @return the allocationLU
   */
  public AllocationLU getAllocationLU() {
    return allocationLU;
  }

  /**
   * Sets the allocation lu.
   * 
   * @param allocationLU
   *          the allocationLU to set
   */
  public void setAllocationLU(AllocationLU allocationLU) {
    this.allocationLU = allocationLU;
  }

}
