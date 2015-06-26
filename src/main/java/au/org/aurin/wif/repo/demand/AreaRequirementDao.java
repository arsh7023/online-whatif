package au.org.aurin.wif.repo.demand;

import java.util.List;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.AreaRequirement;

/**
 * The Interface AreaRequirementDao.
 */
public interface AreaRequirementDao {

  /**
   * Adds the area requirement.
   * 
   * @param areaRequirement
   *          the area requirement
   */
  void addAreaRequirement(AreaRequirement areaRequirement);

  /**
   * Update area requirement.
   * 
   * @param areaRequirement
   *          the area requirement
   */
  void updateAreaRequirement(AreaRequirement areaRequirement);

  /**
   * Gets the area requirements.
   * 
   * @param demandScenarioId
   *          the demand scenario id
   * @return the area requirements
   */
  List<AreaRequirement> getAreaRequirements(String demandScenarioId);

  /**
   * Find area requirement by id.
   * 
   * @param id
   *          the id
   * @return the area requirement
   */
  AreaRequirement findAreaRequirementById(String id);

  /**
   * Delete area requirement.
   * 
   * @param areaRequirement
   *          the area requirement
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteAreaRequirement(AreaRequirement areaRequirement)
      throws WifInvalidInputException;

  /**
   * Persist area requirement.
   * 
   * @param areaRequirement
   *          the area requirement
   * @return the area requirement
   */
  AreaRequirement persistAreaRequirement(AreaRequirement areaRequirement);
}
