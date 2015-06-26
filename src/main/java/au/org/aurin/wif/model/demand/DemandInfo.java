/*
 *
 */
package au.org.aurin.wif.model.demand;

import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandScenario;

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
public class DemandInfo {

  /** The demand scenario. @uml.property name="the demandScenario" */
  @JsonIgnore
  private DemandScenario demandScenario;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The allocation lu. */
  @JsonIgnore
  private AllocationLU allocationLU;

  /**
   * Instantiates a new demand info.
   */
  public DemandInfo() {
    super();
  }

  /**
   * Instantiates a new demand info.
   * 
   * @param copy
   *          the copy
   */
  public DemandInfo(DemandInfo copy) {

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
