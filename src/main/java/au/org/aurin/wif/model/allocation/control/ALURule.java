/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation.control;

import org.geotools.data.Query;

/**
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class ALURule {

	/** The id. @uml.property name="id" */
	private Integer id;

	/** The control scenario. @uml.property name="the controlScenario" */
	private ControlScenario controlScenario;

	/**
	 * The rule query. this property cannot be persistent, probably we should
	 * be factored outside the model in the future
	 */
	private Query ruleQuery;

	/** The label. */
	private String label;// TODO it should be an enumeration type

	/**
	 * Gets the control scenario.
	 * 
	 * @return the controlScenario
	 */
	public ControlScenario getControlScenario() {
		return this.controlScenario;
	}

	/**
	 * Sets the control scenario.
	 * 
	 * @param controlScenario
	 *            the controlScenario to set
	 */
	public void setControlScenario(ControlScenario controlScenario) {
		this.controlScenario = controlScenario;
	}

	/**
	 * Sets the rule query.
	 * 
	 * @param ruleQuery
	 *            the ruleQuery to set
	 */
	public void setRuleQuery(Query ruleQuery) {
		this.ruleQuery = ruleQuery;
	}

	/**
	 * Gets the rule query.
	 * 
	 * @return the ruleQuery
	 */
	public Query getRuleQuery() {
		return ruleQuery;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

}
