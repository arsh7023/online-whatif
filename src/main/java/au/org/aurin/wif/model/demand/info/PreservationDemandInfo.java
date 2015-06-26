/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.data.PreservedData;
import au.org.aurin.wif.model.demand.data.ProjectedData;

// TODO: Auto-generated Javadoc
/**TODO it doesn't change from its superclass,  maybe the future,  but is easier for the logic for the other command influence one or nice number
 * <b>PreservationDemandInfo.java</b> : Configuration parameters required to set up the residential demand.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class PreservationDemandInfo extends ProjectedDemandInfo  {
   
   /**
    * Instantiates a new preservation demand info.
    */
public PreservationDemandInfo() {
	super();
	// 
}

  /**
   * Gets the projected data by projection.
   *
   * @param projection the projection
   * @return the projected data by projection
   * @throws WifInvalidInputException the wif invalid input exception
   */
  public PreservedData getProjectedDataByProjection(Projection projection) throws WifInvalidInputException {
    ProjectedData projectedData = super.getProjectedData(projection);
    return (PreservedData) projectedData;
  }

  /**
   * Instantiates a new preservation demand info.
   *
   * @param copy the copy
   */
  public PreservationDemandInfo(PreservationDemandInfo copy) {
    super(copy);
  }
  
 
}
