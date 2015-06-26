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
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchDemandScenarioDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("DemandScenarioDao")
public class CouchDemandScenarioDao implements DemandScenarioDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandScenarioDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private DemandScenarioRepository repository;

  /**
   * Inits the DemandScenarioDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new DemandScenarioRepository(manager.getDb());

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
  public void addDemandScenario(DemandScenario demandScenario) {
    repository.add(demandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#persistDemandScenario(au
   * .org.aurin .wif.model.DemandScenario)
   */
  public DemandScenario persistDemandScenario(DemandScenario demandScenario) {

    repository.add(demandScenario);

    return demandScenario;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#updateDemandScenario(au.
   * org.aurin .wif.model.DemandScenario)
   */
  public void updateDemandScenario(DemandScenario demandScenario) {
    repository.update(demandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#findDemandScenarioById(java
   * .lang .String)
   */
  public DemandScenario findDemandScenarioById(String id) {
    try {
      DemandScenario demandScenario = repository.get(id);
      return demandScenario;
    } catch (org.ektorp.DocumentNotFoundException e) {
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
  public void deleteDemandScenario(DemandScenario demandScenario) {
    repository.remove(demandScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioDao#getDemandScenarios(java.
   * lang.String)
   */
  public List<DemandScenario> getDemandScenarios(String projectId) {
    return repository.getDemandScenarios(projectId);
  }

  /**
   * The Class DemandScenarioRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class DemandScenarioRepository extends
      CouchDbRepositorySupport<DemandScenario> {

    /**
     * Instantiates a new demandScenario repository.
     * 
     * @param db
     *          the db
     */
    public DemandScenarioRepository(CouchDbConnector db) {
      super(DemandScenario.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the demand scenarios.
     *
     * @param projectId the project id
     * @return the demand scenarios
     */
    @View(name = "getDemandScenarios", map = "function(doc) { if(doc.projectId && doc.docType=='DemandScenario') "
        + "{emit(doc.projectId, doc)} }")
    public List<DemandScenario> getDemandScenarios(String projectId) {
      return queryView("getDemandScenarios", projectId);
    }
  }
}
