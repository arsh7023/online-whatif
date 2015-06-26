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
import au.org.aurin.wif.model.demand.DemandConfigNew;
import au.org.aurin.wif.repo.demand.DemandConfigNewDao;
import au.org.aurin.wif.svc.WifKeys;

//
/**
 * The Class CouchDemandConfigNewDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("DemandConfigNewDao")
public class CouchDemandConfigNewDao implements DemandConfigNewDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandConfigNewDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private DemandConfigNewRepository repository;

  /**
   * Inits the DemandConfigNewDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new DemandConfigNewRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#addDemandConfigNew(au.org
   * .aurin .wif .model.DemandConfigNew)
   */
  public void addDemandConfigNew(DemandConfigNew DemandConfigNew) {
    repository.add(DemandConfigNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#persistDemandConfigNew(
   * au.org .aurin .wif.model.DemandConfigNew)
   */
  public DemandConfigNew persistDemandConfigNew(DemandConfigNew DemandConfigNew) {

    repository.add(DemandConfigNew);

    return DemandConfigNew;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#updateDemandConfigNew(au
   * .org. aurin .wif.model.DemandConfigNew)
   */
  public void updateDemandConfigNew(DemandConfigNew DemandConfigNew) {
    repository.update(DemandConfigNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#findDemandConfigNewById
   * (java.lang .String)
   */
  public DemandConfigNew findDemandConfigNewById(String id) {
    try {
      DemandConfigNew DemandConfigNew = repository.get(id);
      return DemandConfigNew;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#deleteDemandConfigNew(au
   * .org. aurin .wif.model.DemandConfigNew)
   */
  public void deleteDemandConfigNew(DemandConfigNew DemandConfigNew) {
    repository.remove(DemandConfigNew);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.DemandConfigNewDao#getDemandConfigNews(java
   * .lang .String)
   */
  /**
   * Gets the demand configs.
   * 
   * @param projectId
   *          the project id
   * @return the demand configs
   */
  public List<DemandConfigNew> getDemandConfigNews(String projectId) {
    return repository.getDemandConfigNews(projectId);
  }

  /**
   * The Class DemandConfigNewRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class DemandConfigNewRepository extends
      CouchDbRepositorySupport<DemandConfigNew> {

    /**
     * Instantiates a new DemandConfigNew repository.
     * 
     * @param db
     *          the db
     */
    public DemandConfigNewRepository(CouchDbConnector db) {
      super(DemandConfigNew.class, db);
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
    @View(name = "getDemandConfigNews", map = "function(doc) { if(doc.projectId && doc.docType=='DemandConfigNew') {emit(doc.projectId, doc)} }")
    public List<DemandConfigNew> getDemandConfigNews(String projectId) {
      return queryView("getDemandConfigNews", projectId);
    }
  }
}
