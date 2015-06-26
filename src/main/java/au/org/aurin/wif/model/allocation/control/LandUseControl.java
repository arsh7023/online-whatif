/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation.control;

import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.PlannedLU;

/**
 * Land Use Controls [Data hardcoded in interface?] Can be used to select the
 * 2030 Land Use plan from the drop down list. If this option is selected, the
 * projected land use demands will only be allocated to locations for which they
 * are planned in the 2030 the land use plan
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class LandUseControl {

	/** The id. @uml.property name="id" */
	private Integer id;
	
	/** The planned l us. */
	private Set<PlannedLU> plannedLUs;
	
	/** The label. */
	private String label;

	/** The attribute name. */
	private String attributeName;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the planned l us.
	 *
	 * @return the planned l us
	 */
	public Set<PlannedLU> getPlannedLUs() {
		return plannedLUs;
	}

	/**
	 * Sets the planned l us.
	 *
	 * @param plannedLUs the new planned l us
	 */
	public void setPlannedLUs(Set<PlannedLU> plannedLUs) {
		this.plannedLUs = plannedLUs;
	}

	/**
	 * Adds the planned lu.
	 *
	 * @param plannedLUs the planned l us
	 */
	public void addPlannedLU(PlannedLU plannedLUs) {
		this.plannedLUs.add(plannedLUs);
	}

	/**
	 * Gets the attribute name.
	 *
	 * @return the attribute name
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * Sets the attribute name.
	 *
	 * @param attributeName the new attribute name
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
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
	 * Gets the pL uby alu.
	 *
	 * @param futureLU the future lu
	 * @return the pL uby alu
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	public PlannedLU getPLUbyALU(AllocationLU futureLU) throws WifInvalidInputException {

		for (PlannedLU plu : this.plannedLUs) {
			Set<AllocationLU> existingLUs = plu.getExistingLUs();
			for (AllocationLU allocationLU : existingLUs) {
				if (allocationLU == futureLU) {
					return plu;
				}
			}
		}
		throw new WifInvalidInputException(
				"there's no  planned  land use associated in: " + this.getLabel()
						+ " for: " + futureLU.getLabel());
	}

}
