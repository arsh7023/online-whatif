package au.org.aurin.wif.model.allocation.control;

import au.org.aurin.wif.model.Projection;

/**
 * The Class InfrastructureProvision.
 */
public class InfrastructureProvision {

	/** The id. @uml.property name="id" */
	private Integer id;
	
	/** The projection. */
	private Projection projection;
	
	/** The infrastructure plan. */
	private InfrastructurePlan infrastructurePlan;
	
	/** The attribute name. */
	private String attributeName;
	
	/**
	 * Sets the attribute name.
	 *
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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
	 * Sets the projection.
	 *
	 * @param projection the projection to set
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
	
}
