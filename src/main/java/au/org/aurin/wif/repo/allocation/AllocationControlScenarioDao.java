package au.org.aurin.wif.repo.allocation;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;

/**
 * The Interface AllocationControlScenarioDao.
 */
public interface AllocationControlScenarioDao {

  /**
   * Adds the allocation control scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation control scenario
   */
  void addAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario);

  /**
   * Update allocation control scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation control scenario
   */
  void updateAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario);

  /**
   * Gets the allocation control scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the allocation control scenarios
   */
  List<AllocationControlScenario> getAllocationControlScenarios(String projectId);

  /**
   * Find allocation control scenario by id.
   * 
   * @param id
   *          the id
   * @return the allocation control scenario
   */
  AllocationControlScenario findAllocationControlScenarioById(String id);

  /**
   * Delete allocation control scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation control scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario)
      throws WifInvalidInputException;

  /**
   * Persist allocation control scenario.
   * 
   * @param AllocationControlScenario
   *          the allocation control scenario
   * @return the allocation control scenario
   */
  AllocationControlScenario persistAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario);
}
