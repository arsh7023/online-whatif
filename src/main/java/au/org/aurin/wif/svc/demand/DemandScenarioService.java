package au.org.aurin.wif.svc.demand;

import java.util.List;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;

/**
 * The Interface DemandScenarioService.
 */
public interface DemandScenarioService {

  /**
   * Creates the demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   * @param projectId
   *          the project id
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  DemandScenario createDemandScenario(DemandScenario demandScenario,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException;

  /**
   * Gets the demand scenario.
   * 
   * @param id
   *          the id
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandScenario getDemandScenario(String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException;

  /**
   * Gets the demand scenario.
   * 
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  DemandScenario getDemandScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  void updateDemandScenario(DemandScenario demandScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Delete demand scenario.
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
  void deleteDemandScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the demand scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the demand scenarios
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<DemandScenario> getDemandScenarios(String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the outcome.
   * 
   * @param id
   *          the id
   * @return the outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  List<AreaRequirement> getOutcome(String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException;

  /**
   * Gets the outcome.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  List<AreaRequirement> getOutcome(DemandScenario demandScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandScenarioException;
}
