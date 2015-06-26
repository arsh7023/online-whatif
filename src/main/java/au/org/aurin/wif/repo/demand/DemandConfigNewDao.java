package au.org.aurin.wif.repo.demand;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.demand.DemandConfigNew;

/**
 * The Interface DemandConfigNewDao.
 */
public interface DemandConfigNewDao {

  /**
   * Adds the DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   */
  void addDemandConfigNew(DemandConfigNew DemandConfigNew);

  /**
   * Persist DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   * @return the DemandConfigNew
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  DemandConfigNew persistDemandConfigNew(DemandConfigNew DemandConfigNew)
      throws WifInvalidConfigException;

  /**
   * Update DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateDemandConfigNew(DemandConfigNew DemandConfigNew)
      throws WifInvalidConfigException;

  /**
   * Find DemandConfigNew by id.
   * 
   * @param id
   *          the id
   * @return the wif DemandConfigNew
   */
  DemandConfigNew findDemandConfigNewById(String id);

  /**
   * Delete DemandConfigNew.
   * 
   * @param DemandConfigNew
   *          the DemandConfigNew
   */
  void deleteDemandConfigNew(DemandConfigNew DemandConfigNew);

}
