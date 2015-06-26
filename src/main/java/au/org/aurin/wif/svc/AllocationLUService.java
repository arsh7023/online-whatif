package au.org.aurin.wif.svc;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Interface AllocationLUService. It handles the CRUD operations on
 * AllocationLU, plus other important access operations
 */
public interface AllocationLUService {

  /**
   * Adds the allocationLU.
   * 
   * @param allocationLU
   *          the allocationLU
   * @param projectId
   *          the project id
   * @return the allocationLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   * @throws InvalidFFNameException
   */
  AllocationLU createAllocationLU(AllocationLU allocationLU, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      InvalidLabelException, InvalidFFNameException;

  /**
   * Gets the allocationLU.
   * 
   * @param id
   *          the id
   * @return the allocationLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  AllocationLU getAllocationLU(String id) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Gets the allocationLU.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the allocationLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  AllocationLU getAllocationLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Update allocationLU.
   * 
   * @param allocationLU
   *          the allocationLU
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateAllocationLU(AllocationLU allocationLU, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete allocationLU.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteAllocationLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Gets the allocationLUs.
   * 
   * @param projectId
   *          the project id
   * @return the allocationLUs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<AllocationLU> getAllocationLUs(String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the allocationLUs which have suitability land use.
   * 
   * @param projectId
   *          the project id
   * @return the allocationLUs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   */
  List<AllocationLU> getAllocationLUsSuitabilityAssociated(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;
}
