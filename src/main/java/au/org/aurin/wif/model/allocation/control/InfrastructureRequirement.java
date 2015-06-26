package au.org.aurin.wif.model.allocation.control;

import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Class AreaRequirement.
 */
public class InfrastructureRequirement {

	/** The id. @uml.property name="id" */
	private Integer id;
	
	/** The allocation lu. */
  private AllocationLU allocationLU;
 
  /** The requirement type. */
  private RequirementType  requirementType;

	/** The infrastructure plan. */
	private InfrastructurePlan infrastructurePlan;

	/**
	 * Sets the allocation lu.
	 *
	 * @param allocationLU the allocationLU to set
	 */
	public void setAllocationLU(AllocationLU allocationLU) {
		this.allocationLU = allocationLU;
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
	 * Sets the requirement.
	 *
	 * @param requirementType the requirementType to set
	 */
	public void setRequirement(RequirementType requirementType) {
		this.requirementType = requirementType;
	}

	/**
	 * Gets the requirement.
	 *
	 * @return the requirementType
	 */
	public RequirementType getRequirement() {
		return requirementType;
	}
}
