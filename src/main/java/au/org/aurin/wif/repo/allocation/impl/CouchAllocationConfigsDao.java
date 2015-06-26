/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.allocation.impl;

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
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.svc.WifKeys;

//
/**
 * The Class CouchAllocationConfigsDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("AllocationConfigsDao")
public class CouchAllocationConfigsDao implements AllocationConfigsDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationConfigsDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AllocationConfigsRepository repository;

  /**
   * Inits the AllocationConfigDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AllocationConfigsRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#addAllocationConfig
   * (au.org.aurin .wif .model.AllocationConfig)
   */
  public void addAllocationConfigs(AllocationConfigs AllocationConfig) {
    repository.add(AllocationConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#persistAllocationConfig
   * (au.org .aurin .wif.model.AllocationConfig)
   */
  public AllocationConfigs persistAllocationConfigs(
      AllocationConfigs AllocationConfig) {

    repository.add(AllocationConfig);

    return AllocationConfig;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#updateAllocationConfig
   * (au.org. aurin .wif.model.AllocationConfig)
   */
  public void updateAllocationConfigs(AllocationConfigs AllocationConfig) {
    repository.update(AllocationConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#findAllocationConfigById
   * (java.lang .String)
   */
  public AllocationConfigs findAllocationConfigsById(String id) {
    try {
      AllocationConfigs AllocationConfigs = repository.get(id);
      return AllocationConfigs;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#deleteAllocationConfig
   * (au.org. aurin .wif.model.AllocationConfig)
   */
  public void deleteAllocationConfigs(AllocationConfigs AllocationConfig) {
    repository.remove(AllocationConfig);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.Allocation.AllocationConfigDao#getAllocationConfigs
   * (java.lang .String)
   */
  /**
   * Gets the Allocation configs.
   * 
   * @param projectId
   *          the project id
   * @return the Allocation configs
   */
  public List<AllocationConfigs> getAllocationConfigs(String projectId) {
    return repository.getAllocationConfigs(projectId);
  }

  /**
   * The Class AllocationConfigRepository. This helperclass is provided by
   * Ektorp for CRUD operations.
   */
  public class AllocationConfigsRepository extends
      CouchDbRepositorySupport<AllocationConfigs> {

    /**
     * Instantiates a new AllocationConfig repository.
     * 
     * @param db
     *          the db
     */
    public AllocationConfigsRepository(CouchDbConnector db) {
      super(AllocationConfigs.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the Allocation configs.
     * 
     * @param projectId
     *          the project id
     * @return the Allocation configs
     */
    @View(name = "getAllocationConfigs", map = "function(doc) { if(doc.projectId && doc.docType=='AllocationConfigs') {emit(doc.projectId, doc)} }")
    public List<AllocationConfigs> getAllocationConfigs(String projectId) {
      return queryView("getAllocationConfigs", projectId);
    }

  }
}
