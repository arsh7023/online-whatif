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
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchAllocationControlScenarioDao. Implements persistence based on
 * CouchDB database specified in the CouchDBManager.
 */
@Component("AllocationControlScenarioDao")
public class CouchAllocationControlScenarioDao implements
    AllocationControlScenarioDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationControlScenarioDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AllocationControlScenarioRepository repository;

  /**
   * Inits the AllocationControlScenarioDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AllocationControlScenarioRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * addAllocationControlScenario (au.org .aurin.wif
   * .model.AllocationControlScenario)
   */
  public void addAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario) {
    repository.add(AllocationControlScenario);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * persistAllocationControlScenario(au .org.aurin
   * .wif.model.AllocationControlScenario)
   */
  public AllocationControlScenario persistAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario) {

    repository.add(AllocationControlScenario);

    return AllocationControlScenario;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * updateAllocationControlScenario (au. org.aurin
   * .wif.model.AllocationControlScenario)
   */
  public void updateAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario) {
    repository.update(AllocationControlScenario);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * findAllocationControlScenarioById(java .lang .String)
   */
  public AllocationControlScenario findAllocationControlScenarioById(String id) {
    try {
      AllocationControlScenario AllocationControlScenario = repository.get(id);
      return AllocationControlScenario;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * deleteAllocationControlScenario (au. org.aurin
   * .wif.model.AllocationControlScenario)
   */
  public void deleteAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario) {
    repository.remove(AllocationControlScenario);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao#
   * getAllocationControlScenarios (java. lang.String)
   */
  public List<AllocationControlScenario> getAllocationControlScenarios(
      String projectId) {
    return repository.getAllocationControlScenarios(projectId);
  }

  /**
   * The Class AllocationControlScenarioRepository. This helperclass is provided
   * by Ektorp for CRUD operations.
   */
  public class AllocationControlScenarioRepository extends
      CouchDbRepositorySupport<AllocationControlScenario> {

    /**
     * Instantiates a new AllocationControlScenario repository.
     * 
     * @param db
     *          the db
     */
    public AllocationControlScenarioRepository(CouchDbConnector db) {
      super(AllocationControlScenario.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the allocation scenarios.
     * 
     * @param projectId
     *          the project id
     * @return the allocation scenarios
     */
    @View(name = "getAllocationControlScenarios", map = "function(doc) { if(doc.projectId && doc.docType=='AllocationControlScenario') "
        + "{emit(doc.projectId, doc)} }")
    public List<AllocationControlScenario> getAllocationControlScenarios(
        String projectId) {
      return queryView("getAllocationControlScenarios", projectId);
    }
  }
}
