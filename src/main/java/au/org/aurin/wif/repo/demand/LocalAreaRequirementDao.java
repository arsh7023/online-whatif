package au.org.aurin.wif.repo.demand;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;

/**
 * The Interface LocalAreaRequirementDao.
 */
public interface LocalAreaRequirementDao {

  /**
   * Adds the local area requirement.
   * 
   * @param localAreaRequirement
   *          the local area requirement
   */
  void addLocalAreaRequirement(LocalAreaRequirement localAreaRequirement);

  /**
   * Update local area requirement.
   * 
   * @param localAreaRequirement
   *          the local area requirement
   */
  void updateLocalAreaRequirement(LocalAreaRequirement localAreaRequirement);

  /**
   * Gets the local area requirements.
   * 
   * @param demandScenarioId
   *          the demand scenario id
   * @return the local area requirements
   */
  List<LocalAreaRequirement> getLocalAreaRequirements(String demandScenarioId);

  /**
   * Find local area requirement by id.
   * 
   * @param id
   *          the id
   * @return the local area requirement
   */
  LocalAreaRequirement findLocalAreaRequirementById(String id);

  /**
   * Delete local area requirement.
   * 
   * @param localAreaRequirement
   *          the local area requirement
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteLocalAreaRequirement(LocalAreaRequirement localAreaRequirement)
      throws WifInvalidInputException;

  /**
   * Persist local area requirement.
   * 
   * @param localAreaRequirement
   *          the local area requirement
   * @return the local area requirement
   */
  LocalAreaRequirement persistLocalAreaRequirement(
      LocalAreaRequirement localAreaRequirement);
}
