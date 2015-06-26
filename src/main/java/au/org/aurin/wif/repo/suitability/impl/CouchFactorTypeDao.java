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
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.repo.suitability.FactorTypeDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchFactorTypeDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("FactorTypeDao")
public class CouchFactorTypeDao implements FactorTypeDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchFactorTypeDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private FactorTypeRepository repository;

  /**
   * Inits the FactorTypeDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new FactorTypeRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.FactorTypeDao#addFactorType(au.org.aurin.wif
   * .model.FactorType)
   */
  public void addFactorType(FactorType factorType) {
    repository.add(factorType);

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.FactorTypeDao#persistFactorType(au.org.aurin
   * .wif.model.FactorType)
   */
  public FactorType persistFactorType(FactorType factorType)
      throws WifInvalidConfigException {
    repository.add(factorType);
    return factorType;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.FactorTypeDao#updateFactorType(au.org.aurin
   * .wif.model.FactorType)
   */
  public void updateFactorType(FactorType factorType)
      throws WifInvalidConfigException {
    repository.update(factorType);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.FactorTypeDao#findFactorTypeById(java.lang
   * .String)
   */
  public FactorType findFactorTypeById(String id) {
    try {
      FactorType factorType = repository.get(id);
      return factorType;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.FactorTypeDao#deleteFactorType(au.org.aurin
   * .wif.model.FactorType)
   */
  public void deleteFactorType(FactorType factorType) {
    repository.remove(factorType);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.FactorTypeDao#getFactorTypes(java.lang.String)
   */
  public List<FactorType> getFactorTypes(String factorId) {
    return repository.getFactorTypes(factorId);
  }

  /**
   * The Class FactorTypeRepository. This helperclass is provided by Ektorp for
   * CRUD operations.
   */
  public class FactorTypeRepository extends
      CouchDbRepositorySupport<FactorType> {

    /**
     * Instantiates a new factorType repository.
     * 
     * @param db
     *          the db
     */
    public FactorTypeRepository(CouchDbConnector db) {
      super(FactorType.class, db);
      initStandardDesignDocument();
    }

    /**
     * Gets the factor types.
     *
     * @param factorId the factor id
     * @return the factor types
     */
    @View(name = "getFactorTypes", map = "function(doc) { if(doc.factorId && doc.docType=='FactorType') "
        + "{emit(doc.factorId, doc)} }")
    public List<FactorType> getFactorTypes(String factorId) {
      return queryView("getFactorTypes", factorId);
    }
  }
}
