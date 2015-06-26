package au.org.aurin.wif.repo.allocation;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationScenario;

/**
 * The Interface AllocationScenarioDao.
 */
public interface AllocationScenarioDao {

  /**
   * Adds the allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   */
  void addAllocationScenario(AllocationScenario allocationScenario);

  /**
   * Update allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   */
  void updateAllocationScenario(AllocationScenario allocationScenario);

  /**
   * Gets the allocation scenarios.
   * 
   * @param projectId
   *          the project id
   * @return the allocation scenarios
   */
  List<AllocationScenario> getAllocationScenarios(String projectId);

  /**
   * Find allocation scenario by id.
   * 
   * @param id
   *          the id
   * @return the allocation scenario
   */
  AllocationScenario findAllocationScenarioById(String id);

  /**
   * Delete allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteAllocationScenario(AllocationScenario allocationScenario)
      throws WifInvalidInputException;

  /**
   * Persist allocation scenario.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @return the allocation scenario
   */
  AllocationScenario persistAllocationScenario(
      AllocationScenario allocationScenario);
}
