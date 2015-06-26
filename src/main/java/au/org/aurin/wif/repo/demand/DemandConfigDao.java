package au.org.aurin.wif.repo.demand;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.demand.DemandConfig;

/**
 * The Interface DemandConfigDao.
 */
public interface DemandConfigDao {

  /**
   * Adds the demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   */
  void addDemandConfig(DemandConfig demandConfig);

  /**
   * Persist demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   * @return the demandConfig
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  DemandConfig persistDemandConfig(DemandConfig demandConfig)
      throws WifInvalidConfigException;

  /**
   * Update demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateDemandConfig(DemandConfig demandConfig)
      throws WifInvalidConfigException;

  /**
   * Find DemandConfig by id.
   * 
   * @param id
   *          the id
   * @return the wif DemandConfig
   */
  DemandConfig findDemandConfigById(String id);

  /**
   * Delete demandConfig.
   * 
   * @param demandConfig
   *          the demandConfig
   */
  void deleteDemandConfig(DemandConfig demandConfig);

}
