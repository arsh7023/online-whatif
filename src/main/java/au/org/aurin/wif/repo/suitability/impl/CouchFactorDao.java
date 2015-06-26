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

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.io.CouchDBManager;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.repo.suitability.FactorDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchFactorDao. Implements persistence based on CouchDB database
 * specified in the CouchDBManager.
 */
@Component("FactorDao")
public class CouchFactorDao implements FactorDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchFactorDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private FactorRepository repository;

  /**
   * Inits the FactorDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new FactorRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.demand.FactorDao#addFactor(au.org.aurin.wif
   * .model.Factor)
   */
  public void addFactor(Factor factor) {
    repository.add(factor);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.FactorDao#persistFactor(au.org.aurin
   * .wif.model.Factor)
   */
  public Factor persistFactor(Factor factor) throws WifInvalidConfigException {

    repository.add(factor);

    return factor;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.FactorDao#updateFactor(au.org.aurin
   * .wif.model.Factor)
   */
  public void updateFactor(Factor factor) throws WifInvalidConfigException {
    repository.update(factor);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.FactorDao#findFactorById(java.lang
   * .String)
   */
  public Factor findFactorById(String id) {
    try {
      Factor factor = repository.get(id);
      return factor;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.FactorDao#deleteFactor(au.org.aurin
   * .wif.model.Factor)
   */
  public void deleteFactor(Factor factor) {
    repository.remove(factor);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.FactorDao#getFactors(java.lang.String)
   */
  public List<Factor> getFactors(String projectId) {
    // return repository.findByProjectUUID(projectId);
    return repository.getFactors(projectId);
  }

  /**
   * The Class FactorRepository. This helperclass is provided by Ektorp for CRUD
   * operations.
   */
  public class FactorRepository extends CouchDbRepositorySupport<Factor> {

    /**
     * Instantiates a new factor repository.
     * 
     * @param db
     *          the db
     */
    public FactorRepository(CouchDbConnector db) {
      super(Factor.class, db);
      initStandardDesignDocument();

    }

    // @GenerateView
    // public List<Factor> findByProjectUUID(String projectUUID) {
    // return queryView("by_projectUUID", projectUUID);
    // }
    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the factors.
     *
     * @param projectId the project id
     * @return the factors
     */
    @View(name = "getFactors", map = "function(doc) { if(doc.projectId && doc.docType=='Factor') "
        + "{emit(doc.projectId, doc)} }")
    public List<Factor> getFactors(String projectId) {
      return queryView("getFactors", projectId);
    }
  }
}
