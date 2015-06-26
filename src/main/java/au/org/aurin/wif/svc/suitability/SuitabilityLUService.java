package au.org.aurin.wif.svc.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.SuitabilityLU;

/**
 * The Interface SuitabilityLUService. It handles the CRUD operations on
 * SuitabilityLU, plus other important access operations
 */
public interface SuitabilityLUService {
  /**
   * Adds the suitabilityLU.
   * 
   * @param suitabilityLU
   *          the suitabilityLU
   * @param projectId
   *          the project id
   * @return the suitabilityLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   */
  SuitabilityLU createSuitabilityLU(SuitabilityLU suitabilityLU,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, InvalidLabelException;

  /**
   * Gets the suitabilityLU.
   * 
   * @param id
   *          the id
   * @return the suitabilityLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  SuitabilityLU getSuitabilityLU(String id) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Gets the suitabilityLU.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the suitabilityLU
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  SuitabilityLU getSuitabilityLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Adds the associated lu.
   * 
   * @param id
   *          the allocationLU id
   * @param suitabilityLUId
   *          the suitability lu id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void addAssociatedLU(String id, String suitabilityLUId, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete associated lu.
   * 
   * @param id
   *          the allocationLU id
   * @param suitabilityLUId
   *          the suitability lu id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteAssociatedLU(String id, String suitabilityLUId, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Get associated LU IDs.
   * 
   * @param suitabilityLUId
   *          the suitability lu id
   * @param projectId
   *          the project id
   * @return list of associated LU IDs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  List<String> getAssociatedLUs(String suitabilityLUId, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  //
  /**
   * Update suitabilityLU.
   * 
   * @param suitabilityLU
   *          the suitabilityLU
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateSuitabilityLU(SuitabilityLU suitabilityLU, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete suitabilityLU.
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
  void deleteSuitabilityLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Gets the suitabilityLUs.
   * 
   * @param projectId
   *          the project id
   * @return the suitabilityLUs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<SuitabilityLU> getSuitabilityLUs(String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the suitability lu no mapping.
   * 
   * @param suitabilityLUId
   *          the suitability lu id
   * @param projectId
   *          the project id
   * @return the suitability lu no mapping
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  SuitabilityLU getSuitabilityLUNoMapping(String suitabilityLUId,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException;

}
