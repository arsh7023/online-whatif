package au.org.aurin.wif.model.allocation.control;

// TODO: Auto-generated Javadoc
/**
 * The Enum RequirementType.
 */
public enum RequirementType {

	/** The required. */
	REQUIRED ("REQUIRED"), /** The must be near. */
 MUST_BE_NEAR("MUST_BE_NEAR"), /** The n a. */
 N_A("N/A"), /** The excluded. */
 EXCLUDED("EXCLUDED");
	
	/** The attribute name. */
	private final String attributeName;

	/**
	 * Instantiates a new requirement type.
	 *
	 * @param attributeName the attribute name
	 */
	private RequirementType(String attributeName) {
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
	
}
