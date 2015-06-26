/**
 * 
 */
package au.org.aurin.wif.model;

/**
 * The Interface Model2JsonMapped.
 *
 * @author Marcos Nino-Ruiz
 * Allowing the proper serialization of Json objects in Jackson
 */
public interface Model2JsonMapped {
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId();
	
	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel();
	
}
