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
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchDemandScenarioDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("MaunalDemandScenarioDao")
public class CouchManualDemandScenarioDao implements DemandOutcomeDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchManualDemandScenarioDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private ManualDemandScenarioRepository repository;

  /**
   * Inits the DemandScenarioDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new ManualDemandScenarioRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#addDemandScenario(au.org
   * .aurin.wif .model.DemandScenario)
   */
  public void addDemandOutcome(final DemandOutcome manualdemandScenario) {
    repository.add(manualdemandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#persistDemandScenario(au
   * .org.aurin .wif.model.DemandScenario)
   */
  public DemandOutcome persistDemandOutcome(
      final DemandOutcome manualdemandScenario) {

    repository.add(manualdemandScenario);

    return manualdemandScenario;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#updateDemandScenario(au.
   * org.aurin .wif.model.DemandScenario)
   */
  public void updateDemandOutcome(
      final DemandOutcome manualdemandScenario) {
    repository.update(manualdemandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#findDemandScenarioById(java
   * .lang .String)
   */
  public DemandOutcome findDemandOutcomeById(final String id) {
    try {
      final DemandOutcome manualdemandScenario = repository.get(id);
      return manualdemandScenario;
    } catch (final org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#deleteDemandScenario(au.
   * org.aurin .wif.model.DemandScenario)
   */
  public void deleteDemandOutcome(
      final DemandOutcome manualdemandScenario) {
    repository.remove(manualdemandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#getDemandScenarios(java.
   * lang.String)
   */
  public List<DemandOutcome> getDemandOutcomes(final String projectId) {
    return repository.getManualDemandScenarios(projectId);
  }

  /**
   * The Class DemandScenarioRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class ManualDemandScenarioRepository extends
      CouchDbRepositorySupport<DemandOutcome> {

    /**
     * Instantiates a new demandScenario repository.
     * 
     * @param db
     *          the db
     */
    public ManualDemandScenarioRepository(final CouchDbConnector db) {
      super(DemandOutcome.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the demand scenarios.
     * 
     * @param projectId
     *          the project id
     * @return the demand scenarios
     */
    @View(name = "getDemandOutcomes", map = "function(doc) { if(doc.projectId && doc.docType=='DemandOutcome') "
        + "{emit(doc.projectId, doc)} }")
    public List<DemandOutcome> getManualDemandScenarios(final String projectId) {
      return queryView("getDemandOutcomes", projectId);
    }
  }

}
