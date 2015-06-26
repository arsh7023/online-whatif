package au.org.aurin.wif.model.allocation.control;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The control scenarios allow you to save the allocation order, land use
 * controls, infrastructure controls, and growth pattern assumption that are
 * defined on the Control scenario assumptions. This allows a set of control
 * assumptions to be used along with a suitability scenario and demand scenario
 * to define an allocation scenario. It comprises the definition of:
 * 
 * - Allocation Priorities - Infrastructure Controls - Land Use Controls -
 * Growth Patterns
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class ControlScenario extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7009435498389903603L;

  /**
   * The growth patterns. @uml.property name="growthPatterns"
   * 
   * @uml.associationEnd multiplicity="(0 -1)" inverse=
   *                     "controlScenario:au.org.aurin.wif.model.allocation.GrowthPattern"
   */
  @JsonIgnore
  private Set<GrowthPattern> growthPatterns;

  /** The plans. */
  @JsonIgnore
  private Set<InfrastructurePlan> plans;

  /** The land use control. */
  @JsonIgnore
  private LandUseControl landUseControl;

  /** The land use order to specify user-defined land use plan for allocation. */
  @JsonIgnore
  private Map<String, Integer> plannedLUOrder;

  /**
   * Gets the growth patterns.
   * 
   * @return the growthPatterns
   */
  public Collection<GrowthPattern> getGrowthPatterns() {
    return this.growthPatterns;
  }

  /**
   * Sets the growth patterns.
   * 
   * @param growthPatterns
   *          the growthPatterns to set
   */
  public void setGrowthPatterns(Set<GrowthPattern> growthPatterns) {
    this.growthPatterns = growthPatterns;
  }

  /**
   * FIXME STILL NOT WORKING Sort the land use order according to the priorities
   * established in land use priorities.
   * 
   * @return the plans
   */
  // public void sortLUOrder() {
  // landUseOrder.clear();
  // Collection<Integer> ordercollection = landUsePriorities.values();
  // ArrayList<Integer> LUlist = new ArrayList<Integer>(ordercollection);
  // Collections.sort(LUlist);
  // for (Integer newOrder : LUlist) {
  // for (AllocationLU allocationLU : landUsePriorities.keySet()) {
  // allocationLU.setPriority(newOrder);
  // landUseOrder.add(allocationLU);
  // landUsePriorities.put(allocationLU, newOrder);
  // }
  // }
  // for (AllocationLU allocationLU : this.getLandUseOrder()) {
  // LOGGER.debug("The {} order is: {}", allocationLU.getLabel(),
  // allocationLU.getPriority());
  // }
  // }

  /**
   * Gets the plans.
   * 
   * @return the plans
   */
  public Set<InfrastructurePlan> getPlans() {
    return plans;
  }

  /**
   * Sets the plans.
   * 
   * @param plans
   *          the plans to set
   */
  public void setPlans(Set<InfrastructurePlan> plans) {
    this.plans = plans;
  }

  /**
   * Gets the land use control.
   * 
   * @return the landUseControl
   */
  public LandUseControl getLandUseControl() {
    return landUseControl;
  }

  /**
   * Sets the land use control.
   * 
   * @param landUseControl
   *          the landUseControl to set
   */
  public void setLandUseControl(LandUseControl landUseControl) {
    this.landUseControl = landUseControl;
  }

  /**
   * Gets the planned lu order.
   * 
   * @return the landUseOrder
   */
  public Map<String, Integer> getPlannedLUOrder() {
    return plannedLUOrder;
  }

  /**
   * Sets the planned lu order.
   * 
   * @param landUseOrder
   *          the landUseOrder to set
   */
  public void setPlannedLUOrder(Map<String, Integer> landUseOrder) {
    this.plannedLUOrder = landUseOrder;
  }

}
