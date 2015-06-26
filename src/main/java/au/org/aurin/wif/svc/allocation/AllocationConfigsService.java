package au.org.aurin.wif.svc.allocation;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationConfigsException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationConfigs;

/**
 * The Interface AllocationConfigsService. It handles the CRUD operations on
 * AllocationConfigs.
 */
public interface AllocationConfigsService {

  /**
   * Adds the AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   * @param projectId
   *          the project id
   * @return the AllocationConfigs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteAllocationConfigsException
   *           the incomplete demand config exception
   */
  AllocationConfigs createAllocationConfigs(
      AllocationConfigs AllocationConfigs, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteAllocationConfigsException;

  /**
   * Gets the AllocationConfigs.
   * 
   * @param projectId
   *          the project id
   * @return the AllocationConfigs
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  AllocationConfigs getAllocationConfigs(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateAllocationConfigs(AllocationConfigs AllocationConfigs,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Delete AllocationConfigs.
   * 
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteAllocationConfigs(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  Boolean CreateStyle(final AllocationConfigs AllocationConfigs,
      final String projectId, final Boolean lsw);

}
