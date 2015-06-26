package au.org.aurin.wif.svc.suitability;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandConfig;

/**
 * The Interface DemandConfigService. It handles the CRUD operations on
 * DemandConfig.
 */
public interface DemandConfigService {

  /**
   * Adds the demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   * @param projectId
   *          the project id
   * @return the demandConfig
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  DemandConfig createDemandConfig(DemandConfig demandConfig, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandConfigException;

  /**
   * Gets the demandConfig.
   * 
   * @param projectId
   *          the project id
   * @return the demandConfig
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandConfig getDemandConfig(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   * @param projectId
   *          the project id
   * @return
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  boolean updateDemandConfig(DemandConfig demandConfig, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete demandConfig.
   * 
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteDemandConfig(String projectId) throws WifInvalidInputException,
      WifInvalidConfigException;

}
