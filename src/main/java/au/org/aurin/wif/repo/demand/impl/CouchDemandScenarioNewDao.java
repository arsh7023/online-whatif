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
import au.org.aurin.wif.model.demand.DemandScenarioNew;
import au.org.aurin.wif.repo.demand.DemandScenarioNewDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchDemandScenarioNewDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("DemandScenarioNewDao")
public class CouchDemandScenarioNewDao implements DemandScenarioNewDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandScenarioNewDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private DemandScenarioNewRepository repository;

  /**
   * Inits the DemandScenarioNewDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new DemandScenarioNewRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#addDemandScenarioNew(
   * au.org .aurin.wif .model.DemandScenarioNew)
   */
  public void addDemandScenarioNew(DemandScenarioNew DemandScenarioNew) {
    repository.add(DemandScenarioNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#persistDemandScenarioNew
   * (au .org.aurin .wif.model.DemandScenarioNew)
   */
  public DemandScenarioNew persistDemandScenarioNew(
      DemandScenarioNew DemandScenarioNew) {

    repository.add(DemandScenarioNew);

    return DemandScenarioNew;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#updateDemandScenarioNew
   * (au. org.aurin .wif.model.DemandScenarioNew)
   */
  public void updateDemandScenarioNew(DemandScenarioNew DemandScenarioNew) {
    repository.update(DemandScenarioNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#findDemandScenarioNewById
   * (java .lang .String)
   */
  public DemandScenarioNew findDemandScenarioNewById(String id) {
    try {
      DemandScenarioNew DemandScenarioNew = repository.get(id);
      return DemandScenarioNew;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#deleteDemandScenarioNew
   * (au. org.aurin .wif.model.DemandScenarioNew)
   */
  public void deleteDemandScenarioNew(DemandScenarioNew DemandScenarioNew) {
    repository.remove(DemandScenarioNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandScenarioNewDao#getDemandScenarioNews
   * (java. lang.String)
   */
  public List<DemandScenarioNew> getDemandScenarioNews(String projectId) {
    return repository.getDemandScenarioNews(projectId);
  }

  /**
   * The Class DemandScenarioNewRepository. This helperclass is provided by
   * Ektorp for CRUD operations.
   */
  public class DemandScenarioNewRepository extends
      CouchDbRepositorySupport<DemandScenarioNew> {

    /**
     * Instantiates a new DemandScenarioNew repository.
     * 
     * @param db
     *          the db
     */
    public DemandScenarioNewRepository(CouchDbConnector db) {
      super(DemandScenarioNew.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the demand scenarios.
     * 
     * @param projectId
     *          the project id
     * @return the demand scenarios
     */
    @View(name = "getDemandScenarioNews", map = "function(doc) { if(doc.projectId && doc.docType=='DemandScenarioNew') "
        + "{emit(doc.projectId, doc)} }")
    public List<DemandScenarioNew> getDemandScenarioNews(String projectId) {
      return queryView("getDemandScenarioNews", projectId);
    }
  }
}
