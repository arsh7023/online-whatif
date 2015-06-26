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
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchAreaRequirementDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("AreaRequirementDao")
public class CouchAreaRequirementDao implements AreaRequirementDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAreaRequirementDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AreaRequirementRepository repository;

  /**
   * Inits the AreaRequirementDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AreaRequirementRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#addAreaRequirement(au.org
   * .aurin.wif .model.AreaRequirement)
   */
  public void addAreaRequirement(AreaRequirement areaRequirement) {
    repository.add(areaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#persistAreaRequirement(au
   * .org.aurin .wif.model.AreaRequirement)
   */
  public AreaRequirement persistAreaRequirement(AreaRequirement areaRequirement) {

    repository.add(areaRequirement);

    return areaRequirement;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#updateAreaRequirement(au.
   * org.aurin .wif.model.AreaRequirement)
   */
  public void updateAreaRequirement(AreaRequirement areaRequirement) {
    repository.update(areaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#findAreaRequirementById
   * (java .lang .String)
   */
  public AreaRequirement findAreaRequirementById(String id) {
    try {
      AreaRequirement areaRequirement = repository.get(id);
      return areaRequirement;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#deleteAreaRequirement(au.
   * org.aurin .wif.model.AreaRequirement)
   */
  public void deleteAreaRequirement(AreaRequirement areaRequirement) {
    repository.remove(areaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.AreaRequirementDao#getAreaRequirements(java.
   * lang.String)
   */
  public List<AreaRequirement> getAreaRequirements(String demandScenarioId) {
    return repository.getAreaRequirements(demandScenarioId);
  }

  /**
   * The Class AreaRequirementRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class AreaRequirementRepository extends
      CouchDbRepositorySupport<AreaRequirement> {

    /**
     * Instantiates a new areaRequirement repository.
     * 
     * @param db
     *          the db
     */
    public AreaRequirementRepository(CouchDbConnector db) {
      super(AreaRequirement.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the area requirements.
     *
     * @param demandScenarioId the demand scenario id
     * @return the area requirements
     */
    @View(name = "getAreaRequirements", map = "function(doc) { if(doc.demandScenarioId && doc.docType=='AreaRequirement') "
        + "{emit(doc.demandScenarioId, doc)} }")
    public List<AreaRequirement> getAreaRequirements(String demandScenarioId) {
      return queryView("getAreaRequirements", demandScenarioId);
    }
  }
}
