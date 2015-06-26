package au.org.aurin.wif.repo.demand;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandScenario;

/**
 * The Interface DemandScenarioDao.
 */
public interface DemandScenarioDao {

	/**
	 * Adds the demand scenario.
	 *
	 * @param demandScenario the demand scenario
	 */
	void addDemandScenario(DemandScenario demandScenario);

	/**
	 * Update demand scenario.
	 *
	 * @param demandScenario the demand scenario
	 */
	void updateDemandScenario(DemandScenario demandScenario);

	/**
	 * Gets the demand scenarios.
	 *
	 * @param projectId the project id
	 * @return the demand scenarios
	 */
	List<DemandScenario> getDemandScenarios(String projectId);

	/**
	 * Find demand scenario by id.
	 *
	 * @param id the id
	 * @return the demand scenario
	 */
	DemandScenario findDemandScenarioById(String id);

	/**
	 * Delete demand scenario.
	 *
	 * @param demandScenario the demand scenario
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	void deleteDemandScenario(DemandScenario demandScenario) throws WifInvalidInputException;

  /**
   * Persist demand scenario.
   *
   * @param demandScenario the demand scenario
   * @return the demand scenario
   */
  DemandScenario persistDemandScenario(DemandScenario demandScenario);
}
