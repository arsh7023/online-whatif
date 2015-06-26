package au.org.aurin.wif.model.allocation.control;

import java.util.Set;

/**
 * The Class .
 */
public class InfrastructureControlRule extends ALURule {

	/** The type. */
	private String type;

	/** The plans. */
	private Set<InfrastructurePlan> plans;

	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the plans.
	 *
	 * @param plans the plans to set
	 */
	public void setPlans(Set<InfrastructurePlan> plans) {
		this.plans = plans;
	}

	/**
	 * Gets the plans.
	 *
	 * @return the plans
	 */
	public Set<InfrastructurePlan> getPlans() {
		return plans;
	}
}
