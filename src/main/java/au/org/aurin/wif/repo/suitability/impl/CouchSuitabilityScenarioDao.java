/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.suitability.impl;

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
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchSuitabilityScenarioDao. Implements persistence based on
 * CouchDB database specified in the CouchDBManager.
 */
@Component("SuitabilityScenarioDao")
public class CouchSuitabilityScenarioDao implements SuitabilityScenarioDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityScenarioDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private SuitabilityScenarioRepository repository;

  /**
   * Inits the SuitabilityScenarioDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new SuitabilityScenarioRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#addSuitabilityScenario
   * (au.org.aurin.wif .model.SuitabilityScenario)
   */
  public void addSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    repository.add(suitabilityScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#persistSuitabilityScenario
   * (au.org.aurin .wif.model.SuitabilityScenario)
   */
  public SuitabilityScenario persistSuitabilityScenario(
      SuitabilityScenario suitabilityScenario) {

    repository.add(suitabilityScenario);

    return suitabilityScenario;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#updateSuitabilityScenario
   * (au.org.aurin .wif.model.SuitabilityScenario)
   */
  public void updateSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    repository.update(suitabilityScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#findSuitabilityScenarioById
   * (java.lang .String)
   */
  public SuitabilityScenario findSuitabilityScenarioById(String id) {
    try {
      SuitabilityScenario suitabilityScenario = repository.get(id);
      return suitabilityScenario;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#deleteSuitabilityScenario
   * (au.org.aurin .wif.model.SuitabilityScenario)
   */
  public void deleteSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    repository.remove(suitabilityScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityScenarioDao#getSuitabilityScenarios
   * (java.lang.String)
   */
  public List<SuitabilityScenario> getSuitabilityScenarios(String projectId) {
    return repository.getSuitabilityScenarios(projectId);
  }

  /**
   * The Class SuitabilityScenarioRepository. This helperclass is provided by
   * Ektorp for CRUD operations.
   */
  public class SuitabilityScenarioRepository extends
      CouchDbRepositorySupport<SuitabilityScenario> {

    /**
     * Instantiates a new suitabilityScenario repository.
     * 
     * @param db
     *          the db
     */
    public SuitabilityScenarioRepository(CouchDbConnector db) {
      super(SuitabilityScenario.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the suitability scenarios.
     *
     * @param projectId the project id
     * @return the suitability scenarios
     */
    @View(name = "getSuitabilityScenarios", map = "function(doc) { if(doc.projectId && doc.docType=='SuitabilityScenario') "
        + "{emit(doc.projectId, doc)} }")
    public List<SuitabilityScenario> getSuitabilityScenarios(String projectId) {
      return queryView("getSuitabilityScenarios", projectId);
    }
  }

  /* (non-Javadoc)
   * @see au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao#updateSuitabilityScenarioStatus(au.org.aurin.wif.model.suitability.SuitabilityScenario, java.lang.Boolean)
   */
  public void updateSuitabilityScenarioStatus(
      SuitabilityScenario suitabilityScenario, Boolean state) {
    suitabilityScenario.setReady(state);
    updateSuitabilityScenario(suitabilityScenario);
  }
}
