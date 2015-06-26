package au.org.aurin.wif.repo.demand;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandScenarioNew;

/**
 * The Interface DemandScenarioNewDao.
 */
public interface DemandScenarioNewDao {

  /**
   * Adds the demand scenario.
   * 
   * @param DemandScenarioNew
   *          the demand scenario
   */
  void addDemandScenarioNew(DemandScenarioNew DemandScenarioNew);

  /**
   * Update demand scenario.
   * 
   * @param DemandScenarioNew
   *          the demand scenario
   */
  void updateDemandScenarioNew(DemandScenarioNew DemandScenarioNew);

  /**
   * Gets the demand scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the demand scenarios
   */
  List<DemandScenarioNew> getDemandScenarioNews(String projectId);

  /**
   * Find demand scenario by id.
   * 
   * @param id
   *          the id
   * @return the demand scenario
   */
  DemandScenarioNew findDemandScenarioNewById(String id);

  /**
   * Delete demand scenario.
   * 
   * @param DemandScenarioNew
   *          the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteDemandScenarioNew(DemandScenarioNew DemandScenarioNew)
      throws WifInvalidInputException;

  /**
   * Persist demand scenario.
   * 
   * @param DemandScenarioNew
   *          the demand scenario
   * @return the demand scenario
   */
  DemandScenarioNew persistDemandScenarioNew(DemandScenarioNew DemandScenarioNew);
}
