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
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchSuitabilityLUDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("SuitabilityLUDao")
public class CouchSuitabilityLUDao implements SuitabilityLUDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityLUDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private SuitabilityLURepository repository;

  /**
   * Inits the SuitabilityLUDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new SuitabilityLURepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#addSuitabilityLU(au.org.aurin
   * .wif .model.SuitabilityLU)
   */
  public void addSuitabilityLU(SuitabilityLU suitabilityLU) {
    repository.add(suitabilityLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#persistSuitabilityLU(au.org
   * .aurin .wif.model.SuitabilityLU)
   */
  public SuitabilityLU persistSuitabilityLU(SuitabilityLU suitabilityLU) {

    repository.add(suitabilityLU);

    return suitabilityLU;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#updateSuitabilityLU(au.org
   * .aurin .wif.model.SuitabilityLU)
   */
  public void updateSuitabilityLU(SuitabilityLU suitabilityLU) {
    repository.update(suitabilityLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#findSuitabilityLUById(java
   * .lang .String)
   */
  public SuitabilityLU findSuitabilityLUById(String id) {
    try {
      SuitabilityLU suitabilityLU = repository.get(id);
      return suitabilityLU;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#deleteSuitabilityLU(au.org
   * .aurin .wif.model.SuitabilityLU)
   */
  public void deleteSuitabilityLU(SuitabilityLU suitabilityLU) {
    repository.remove(suitabilityLU);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityLUDao#getSuitabilityLUs(java.lang
   * .String)
   */
  public List<SuitabilityLU> getSuitabilityLUs(String projectId) {
    return repository.getSuitabilityLUs(projectId);
  }

  /**
   * The Class SuitabilityLURepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class SuitabilityLURepository extends
      CouchDbRepositorySupport<SuitabilityLU> {

    /**
     * Instantiates a new suitabilityLU repository.
     * 
     * @param db
     *          the db
     */
    public SuitabilityLURepository(CouchDbConnector db) {
      super(SuitabilityLU.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the suitability l us.
     *
     * @param projectId the project id
     * @return the suitability l us
     */
    @View(name = "getSuitabilityLUs", map = "function(doc) { if(doc.projectId && doc.docType=='SuitabilityLU') "
        + "{emit(doc.projectId, doc)} }")
    public List<SuitabilityLU> getSuitabilityLUs(String projectId) {
      return queryView("getSuitabilityLUs", projectId);
    }
  }
}
