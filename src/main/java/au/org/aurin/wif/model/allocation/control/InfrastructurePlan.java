/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation.control;

import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Class InfrastructurePlan.
 *
 * @author marcosnr
 */
public class InfrastructurePlan {
	 
 	/** The control scenario. @uml.property name="the controlScenario" */
	  private ControlScenario controlScenario;
	  
	/** The infrastructure control rule. */
	private InfrastructureControlRule infrastructureControlRule;

	/** The requirements. */
	private Set<InfrastructureRequirement> requirements;

	/** The provisions. */
	private Set<InfrastructureProvision> provisions;

	/** The attribute name. */
	private String attributeName;

	/** The label. */
	private String label;
	
	/**
	 * Sets the control rule.
	 *
	 * @param infrastructureControlRule the infrastructureControlRule to set
	 */
	public void setControlRule(
			InfrastructureControlRule infrastructureControlRule) {
		this.infrastructureControlRule = infrastructureControlRule;
	}

	/**
	 * Gets the control rule.
	 *
	 * @return the infrastructureControlRule
	 */
	public InfrastructureControlRule getControlRule() {
		return infrastructureControlRule;
	}

	/** The id. */
	private Integer id;
	
	/** The projection. @uml.property attributeName="projection" @uml.associationEnd multiplicity="(1 1)" inverse="infrastructurePlan:au.org.aurin.wif.model.Proyection" */

	// come fix me this relationship is wrong
	private Projection projection = new Projection();
	
	/**
	 * Getter of the property <tt>projection</tt>.
	 *
	 * @return Returns the projection.
	 * @uml.property attributeName="projection"
	 */
	public Projection getProyection() {
		return projection;
	}

	/**
	 * Setter of the property <tt>projection</tt>.
	 *
	 * @param projection The projection to set.
	 * @uml.property attributeName="projection"
	 */
	public void setProyection(Projection projection) {
		this.projection = projection;
	}

	/**
	 * Sets the attribute name.
	 *
	 * @param name the new attribute name
	 */
	public void setAttributeName(String name) {
		this.attributeName = name;
	}

	/**
	 * Gets the attribute name.
	 *
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * Sets the requirements.
	 *
	 * @param requirements the requirements to set
	 */
	public void setRequirements(
			Set<InfrastructureRequirement> requirements) {
		this.requirements = requirements;
	}

	/**
	 * Gets the requirements.
	 *
	 * @return the requirements
	 */
	public Set<InfrastructureRequirement> getRequirements() {
		return requirements;
	}

	/**
	 * Adds the infrastructure requirement.
	 *
	 * @param requirement the requirement
	 */
	public void addInfrastructureRequirement(
			InfrastructureRequirement requirement) {
		this.requirements.add(requirement);
	}

	/**
	 * Sets the provisions.
	 *
	 * @param provisions the provisions to set
	 */
	public void setProvisions(Set<InfrastructureProvision> provisions) {
		this.provisions = provisions;
	}

	/**
	 * Adds the infrastructure provision.
	 *
	 * @param provision the provision
	 */
	public void addInfrastructureProvision(InfrastructureProvision provision) {
		this.provisions.add(provision);
	}

	/**
	 * Gets the provisions.
	 *
	 * @return the provisions
	 */
	public Set<InfrastructureProvision> getProvisions() {
		return provisions;
	}

	/**
	 * Gets the requirement by lu.
	 *
	 * @param futureLU the future lu
	 * @return the requirement by lu
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	public RequirementType getRequirementByLU(AllocationLU futureLU)
			throws WifInvalidInputException {

		for (InfrastructureRequirement req : this.getRequirements()) {
			if (futureLU == req.getAllocationLU()) {
				return req.getRequirement();

			}
		}

		throw new WifInvalidInputException(
				"there's no Infrastructure Requirement for Land Use "
						+ futureLU.getLabel()+ "in infrastructure plan:  "+this.getLabel());

	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the provision by projection.
	 *
	 * @param projection the projection
	 * @return the provision by projection
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	public InfrastructureProvision getProvisionByProjection(
			Projection projection) throws WifInvalidInputException {

		for (InfrastructureProvision provision : this.getProvisions()) {
//		FIXME	if (projection == provision.getProjection()) {
				if (projection.getLabel().equalsIgnoreCase(provision.getProjection().getLabel())) {

					return provision;

			}
		}

		throw new WifInvalidInputException(
				"there's no Infrastructure Provision in "+ this.getLabel() + " for projection: " 
						+ projection.getLabel());
	}

}
