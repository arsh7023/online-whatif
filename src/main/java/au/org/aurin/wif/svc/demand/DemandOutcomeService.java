package au.org.aurin.wif.svc.demand;

import java.util.List;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandOutcomeException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandOutcome;

/**
 * The Interface ManualDemandScenarioService.
 */
public interface DemandOutcomeService {

  // FIXME quick implementation to create a manual demand outcome from a demand
  // scenario
  // All references to manual demand the scenarios should change to minor demand
  // outcomes!
  DemandOutcome createDemandOutcome(List<AreaRequirement> outcome,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Creates the demand scenario.
   * 
   * @param DemandOutcome
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
   * @throws IncompleteDemandOutcomeException
   *           the incomplete demand scenario exception
   */
  DemandOutcome createDemandOutcomeNew(DemandOutcome demandOutcome,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandOutcomeException;

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
  DemandOutcome getDemandOutcome(String id) throws WifInvalidInputException,
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
  DemandOutcome getDemandOutcome(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update demand scenario.
   * 
   * @param demandOutcome
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
  void updateDemandOutcome(DemandOutcome demandOutcome, String projectId)
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
  void deleteDemandOutcome(String id, String projectId)
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
  List<DemandOutcome> getDemandOutcomes(String projectId)
      throws WifInvalidInputException;

}
