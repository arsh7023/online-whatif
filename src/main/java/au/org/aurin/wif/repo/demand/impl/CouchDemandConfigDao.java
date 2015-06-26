/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.demand.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.io.CouchDBManager;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.svc.WifKeys;

//
/**
 * The Class CouchDemandConfigDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("DemandConfigDao")
public class CouchDemandConfigDao implements DemandConfigDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandConfigDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private DemandConfigRepository repository;

  /**
   * Inits the DemandConfigDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new DemandConfigRepository(manager.getDb());

  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("successfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#addDemandConfig(au.org.aurin
   * .wif .model.DemandConfig)
   */
  public void addDemandConfig(DemandConfig demandConfig) {
    repository.add(demandConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#persistDemandConfig(au.org
   * .aurin .wif.model.DemandConfig)
   */
  public DemandConfig persistDemandConfig(DemandConfig demandConfig) {

    repository.add(demandConfig);

    return demandConfig;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#updateDemandConfig(au.org.
   * aurin .wif.model.DemandConfig)
   */
  public void updateDemandConfig(DemandConfig demandConfig) {
    repository.update(demandConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#findDemandConfigById(java.lang
   * .String)
   */
  public DemandConfig findDemandConfigById(String id) {
    try {
      DemandConfig demandConfig = repository.get(id);
      return demandConfig;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#deleteDemandConfig(au.org.
   * aurin .wif.model.DemandConfig)
   */
  public void deleteDemandConfig(DemandConfig demandConfig) {
    repository.remove(demandConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigDao#getDemandConfigs(java.lang
   * .String)
   */
  /**
   * Gets the demand configs.
   * 
   * @param projectId
   *          the project id
   * @return the demand configs
   */
  public List<DemandConfig> getDemandConfigs(String projectId) {
    return repository.getDemandConfigs(projectId);
  }

  /**
   * The Class DemandConfigRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class DemandConfigRepository extends
      CouchDbRepositorySupport<DemandConfig> {

    /**
     * Instantiates a new demandConfig repository.
     * 
     * @param db
     *          the db
     */
    public DemandConfigRepository(CouchDbConnector db) {
      super(DemandConfig.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the demand configs.
     * 
     * @param projectId
     *          the project id
     * @return the demand configs
     */
    @View(name = "getDemandConfigs", map = "function(doc) { if(doc.projectId && doc.docType=='DemandConfig') {emit(doc.projectId, doc)} }")
    public List<DemandConfig> getDemandConfigs(String projectId) {
      return queryView("getDemandConfigs", projectId);
    }
  }
}
