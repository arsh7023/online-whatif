package au.org.aurin.wif.repo.demand;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandOutcome;

/**
 * The Interface DemandScenarioDao.
 */
public interface DemandOutcomeDao {

  /**
   * Adds the demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   */
  void addDemandOutcome(DemandOutcome manualdemandScenario);

  /**
   * Update demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   */
  void updateDemandOutcome(DemandOutcome manualdemandScenario);

  /**
   * Gets the demand scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the demand scenarios
   */
  List<DemandOutcome> getDemandOutcomes(String projectId);

  /**
   * Find demand scenario by id.
   * 
   * @param id
   *          the id
   * @return the demand scenario
   */
  DemandOutcome findDemandOutcomeById(String id);

  /**
   * Delete demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteDemandOutcome(DemandOutcome manualdemandScenario)
      throws WifInvalidInputException;

  /**
   * Persist demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the demand scenario
   */
  DemandOutcome persistDemandOutcome(
      DemandOutcome manualdemandScenario);
}
