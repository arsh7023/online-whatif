/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.impl;

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
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.repo.AllocationLUDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchAllocationLUDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("AllocationLUDao")
public class CouchAllocationLUDao implements AllocationLUDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationLUDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AllocationLURepository repository;

  /**
   * Inits the AllocationLUDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AllocationLURepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.AllocationLUDao#addAllocationLU(au.org.aurin
   * .wif .model.AllocationLU)
   */
  public void addAllocationLU(AllocationLU allocationLU) {
    repository.add(allocationLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AllocationLUDao#persistAllocationLU(au.org
   * .aurin .wif.model.AllocationLU)
   */
  public AllocationLU persistAllocationLU(AllocationLU allocationLU) {

    repository.add(allocationLU);

    return allocationLU;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AllocationLUDao#updateAllocationLU(au.org.
   * aurin .wif.model.AllocationLU)
   */
  public void updateAllocationLU(AllocationLU allocationLU) {
    repository.update(allocationLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AllocationLUDao#findAllocationLUById(java.lang
   * .String)
   */
  public AllocationLU findAllocationLUById(String id) {
    try {
      AllocationLU allocationLU = repository.get(id);
      return allocationLU;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AllocationLUDao#deleteAllocationLU(au.org.
   * aurin .wif.model.AllocationLU)
   */
  public void deleteAllocationLU(AllocationLU allocationLU) {
    repository.remove(allocationLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AllocationLUDao#getAllocationLUs(java.lang
   * .String)
   */
  public List<AllocationLU> getAllocationLUs(String projectId) {
    return repository.getAllocationLUs(projectId);
  }

  /**
   * The Class AllocationLURepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class AllocationLURepository extends
      CouchDbRepositorySupport<AllocationLU> {

    /**
     * Instantiates a new allocationLU repository.
     * 
     * @param db
     *          the db
     */
    public AllocationLURepository(CouchDbConnector db) {
      super(AllocationLU.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the allocation l us.
     *
     * @param projectId the project id
     * @return the allocation l us
     */
    @View(name = "getAllocationLUs", map = "function(doc) { if(doc.projectId && doc.docType=='AllocationLU') {emit(doc.projectId, doc)} }")
    public List<AllocationLU> getAllocationLUs(String projectId) {
      return queryView("getAllocationLUs", projectId);
    }
  }
}
