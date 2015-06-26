package au.org.aurin.wif.svc.allocation;

import java.net.MalformedURLException;
import java.util.List;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationConfigException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationControlScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;

/**
 * What if? projects future land use, population and employment patterns by
 */
public interface AllocationControlScenarioService {

  /**
   * Creates the allocation scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation scenario
   * @param projectId
   *          the project id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteAllocationControlScenarioException
   *           the incomplete allocation scenario exception
   * @throws FactoryException
   * @throws GeoServerConfigException
   * @throws DataStoreUnavailableException
   * @throws NoSuchAuthorityCodeException
   * @throws MalformedURLException
   * @throws IllegalArgumentException
   * @throws IncompleteAllocationConfigException
   */
  AllocationControlScenario createAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteAllocationControlScenarioException,
      IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException;

  /**
   * Gets the allocation scenario.
   * 
   * @param id
   *          the id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  AllocationControlScenario getAllocationControlScenario(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the allocation scenario.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  AllocationControlScenario getAllocationControlScenario(String id,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  /**
   * Update allocation scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation scenario
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void updateAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Delete allocation scenario.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void deleteAllocationControlScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the allocation scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the allocation scenarios
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<AllocationControlScenario> getAllocationControlScenarios(String projectId)
      throws WifInvalidInputException;

}
