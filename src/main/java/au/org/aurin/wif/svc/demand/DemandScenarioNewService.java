package au.org.aurin.wif.svc.demand;

import java.util.List;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioNewException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandScenarioNew;

/**
 * The Interface DemandScenarioNewService.
 */
public interface DemandScenarioNewService {

  /**
   * Creates the demand scenario new.
   * 
   * @param DemandScenarioNew
   *          the demand scenario new
   * @param projectId
   *          the project id
   * @return the demand scenario new
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioNewException
   *           the incomplete demand scenario new exception
   */
  DemandScenarioNew createDemandScenarioNew(
      DemandScenarioNew DemandScenarioNew, String projectId)
          throws WifInvalidInputException, WifInvalidConfigException,
          ParsingException, IncompleteDemandScenarioNewException;

  /**
   * Gets the demand scenario new.
   * 
   * @param id
   *          the id
   * @return the demand scenario new
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandScenarioNew getDemandScenarioNew(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the demand scenario new.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the demand scenario new
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandScenarioNew getDemandScenarioNew(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update demand scenario new.
   * 
   * @param DemandScenarioNew
   *          the demand scenario new
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void updateDemandScenarioNew(DemandScenarioNew DemandScenarioNew,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  /**
   * Delete demand scenario new.
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
  void deleteDemandScenarioNew(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the demand scenario news.
   * 
   * @param projectId
   *          the project id
   * @return the demand scenario news
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<DemandScenarioNew> getDemandScenarioNews(String projectId)
      throws WifInvalidInputException;


}
