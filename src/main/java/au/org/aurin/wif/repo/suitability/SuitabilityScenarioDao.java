package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * The Interface SuitabilityScenario.
 */
public interface SuitabilityScenarioDao {

	/**
	 * Adds the suitabilityScenario.
	 * 
	 * @param suitabilityScenario
	 *            the suitabilityScenario
	 */
	void addSuitabilityScenario(SuitabilityScenario suitabilityScenario);

	/**
	 * Update suitabilityScenario.
	 *
	 * @param suitabilityScenario the suitabilityScenario
	 */
	void updateSuitabilityScenario(SuitabilityScenario suitabilityScenario);

	/**
	 * Gets the all suitabilityScenarios.
	 *
	 * @param projectId the project id
	 * @return the all suitabilityScenarios
	 */
	List<SuitabilityScenario> getSuitabilityScenarios(String projectId);
	
	/**
	 * Find suitabilityScenario by id.
	 * 
	 * @param id
	 *            the id
	 * @return the wif suitabilityScenario
	 */
	SuitabilityScenario findSuitabilityScenarioById(String id);

	/**
	 * Delete suitabilityScenario.
	 *
	 * @param suitabilityScenario the suitabilityScenario
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	void deleteSuitabilityScenario(SuitabilityScenario suitabilityScenario) throws WifInvalidInputException;

  /**
   * Persist suitability scenario.
   *
   * @param suitabilityScenario the suitability scenario
   * @return the suitability scenario
   */
  SuitabilityScenario persistSuitabilityScenario(
      SuitabilityScenario suitabilityScenario);

  /**
   * Update suitability scenario status.
   *
   * @param suitabilityScenario the suitability scenario
   * @param true1 the true1
   */
  void updateSuitabilityScenarioStatus(SuitabilityScenario suitabilityScenario,
      Boolean true1);
}
