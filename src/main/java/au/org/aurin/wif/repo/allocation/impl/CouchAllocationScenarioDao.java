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
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchAllocationScenarioDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("AllocationScenarioDao")
public class CouchAllocationScenarioDao implements AllocationScenarioDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationScenarioDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AllocationScenarioRepository repository;

  /**
   * Inits the AllocationScenarioDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AllocationScenarioRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.allocation.AllocationScenarioDao#addAllocationScenario
   * (au.org .aurin.wif .model.AllocationScenario)
   */
  public void addAllocationScenario(AllocationScenario allocationScenario) {
    repository.add(allocationScenario);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationScenarioDao#
   * persistAllocationScenario(au .org.aurin .wif.model.AllocationScenario)
   */
  public AllocationScenario persistAllocationScenario(
      AllocationScenario allocationScenario) {

    repository.add(allocationScenario);

    return allocationScenario;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.allocation.AllocationScenarioDao#updateAllocationScenario
   * (au. org.aurin .wif.model.AllocationScenario)
   */
  public void updateAllocationScenario(AllocationScenario allocationScenario) {
    repository.update(allocationScenario);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationScenarioDao#
   * findAllocationScenarioById(java .lang .String)
   */
  public AllocationScenario findAllocationScenarioById(String id) {
    try {
      AllocationScenario allocationScenario = repository.get(id);
      return allocationScenario;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.allocation.AllocationScenarioDao#deleteAllocationScenario
   * (au. org.aurin .wif.model.AllocationScenario)
   */
  public void deleteAllocationScenario(AllocationScenario allocationScenario) {
    repository.remove(allocationScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.allocation.AllocationScenarioDao#getAllocationScenarios
   * (java. lang.String)
   */
  public List<AllocationScenario> getAllocationScenarios(String projectId) {
    return repository.getAllocationScenarios(projectId);
  }

  /**
   * The Class AllocationScenarioRepository. This helperclass is provided by
   * Ektorp for CRUD operations.
   */
  public class AllocationScenarioRepository extends
      CouchDbRepositorySupport<AllocationScenario> {

    /**
     * Instantiates a new allocationScenario repository.
     * 
     * @param db
     *          the db
     */
    public AllocationScenarioRepository(CouchDbConnector db) {
      super(AllocationScenario.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the allocation scenarios.
     *
     * @param projectId the project id
     * @return the allocation scenarios
     */
    @View(name = "getAllocationScenarios", map = "function(doc) { if(doc.projectId && doc.docType=='AllocationScenario') "
        + "{emit(doc.projectId, doc)} }")
    public List<AllocationScenario> getAllocationScenarios(String projectId) {
      return queryView("getAllocationScenarios", projectId);
    }
  }
}
