package au.org.aurin.wif.repo.allocation;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.allocation.AllocationConfigs;

/**
 * The Interface AllocationConfigsDao.
 */
public interface AllocationConfigsDao {

  /**
   * Adds the AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   */
  void addAllocationConfigs(AllocationConfigs AllocationConfigs);

  /**
   * Persist AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   * @return the AllocationConfigs
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  AllocationConfigs persistAllocationConfigs(AllocationConfigs AllocationConfigs)
      throws WifInvalidConfigException;

  /**
   * Update AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateAllocationConfigs(AllocationConfigs AllocationConfigs)
      throws WifInvalidConfigException;

  /**
   * Find AllocationConfigs by id.
   * 
   * @param id
   *          the id
   * @return the wif AllocationConfigs
   */
  AllocationConfigs findAllocationConfigsById(String id);

  /**
   * Delete AllocationConfigs.
   * 
   * @param AllocationConfigs
   *          the AllocationConfigs
   */
  void deleteAllocationConfigs(AllocationConfigs AllocationConfigs);

}
