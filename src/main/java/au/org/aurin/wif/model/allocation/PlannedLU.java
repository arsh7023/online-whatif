/*
 *
 */
package au.org.aurin.wif.model.allocation;

import java.util.Set;

import au.org.aurin.wif.model.AbstractLandUse;
import au.org.aurin.wif.model.allocation.control.LandUseControl;

/**
 * <b>PlannedLU.java</b> :These land uses are used in any comprehensive plans,
 * zoning ordinances, or “vision plans” that may be used to control the
 * allocation of future land use demand.
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class PlannedLU extends AbstractLandUse {

  /** The future type. @uml.property futureType="futureType" */
  private String futureType;

	/** The land use control. */
	private LandUseControl landUseControl;
	
	/** The existing l us. */
private Set<AllocationLU> existingLUs;

	/**
 * Gets the existing l us.
 *
 * @return the plannedLUs
 */
public Set<AllocationLU> getExistingLUs() {
	return existingLUs;
}

/**
 * Sets the existing l us.
 *
 * @param existingLUs the new existing l us
 */
public void setExistingLUs(Set<AllocationLU> existingLUs) {
	this.existingLUs = existingLUs;
}
  
/**
 * Adds the existing lu.
 *
 * @param elu the elu
 */
public void addExistingLU (AllocationLU elu) {
	this.existingLUs.add(elu);
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
 * @param landUseControl the landUseControl to set
 */
public void setLandUseControl(LandUseControl landUseControl) {
	this.landUseControl = landUseControl;
}

/**
 * Gets the future type.
 *
 * @return the future type
 */
public String getFutureType() {
	return futureType;
}

/**
 * Sets the future type.
 *
 * @param futureType the new future type
 */
public void setFutureType(String futureType) {
	this.futureType = futureType;
}

}
