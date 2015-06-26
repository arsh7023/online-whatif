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
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.repo.demand.LocalAreaRequirementDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchLocalAreaRequirementDao.
 */
@Component("LocalAreaRequirementDao")
public class CouchLocalAreaRequirementDao implements LocalAreaRequirementDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchLocalAreaRequirementDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private LocalAreaRequirementRepository repository;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new LocalAreaRequirementRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#addLocalAreaRequirement
   * (au.org .aurin.wif .model.LocalAreaRequirement)
   */
  public void addLocalAreaRequirement(LocalAreaRequirement localAreaRequirement) {
    repository.add(localAreaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#
   * persistLocalAreaRequirement(au .org.aurin .wif.model.LocalAreaRequirement)
   */
  public LocalAreaRequirement persistLocalAreaRequirement(
      LocalAreaRequirement localAreaRequirement) {

    repository.add(localAreaRequirement);

    return localAreaRequirement;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#updateLocalAreaRequirement
   * (au. org.aurin .wif.model.LocalAreaRequirement)
   */
  public void updateLocalAreaRequirement(
      LocalAreaRequirement localAreaRequirement) {
    repository.update(localAreaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#
   * findLocalAreaRequirementById (java .lang .String)
   */
  public LocalAreaRequirement findLocalAreaRequirementById(String id) {
    try {
      LocalAreaRequirement localAreaRequirement = repository.get(id);
      return localAreaRequirement;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#deleteLocalAreaRequirement
   * (au. org.aurin .wif.model.LocalAreaRequirement)
   */
  public void deleteLocalAreaRequirement(
      LocalAreaRequirement localAreaRequirement) {
    repository.remove(localAreaRequirement);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.LocalAreaRequirementDao#getLocalAreaRequirements
   * (java. lang.String)
   */
  public List<LocalAreaRequirement> getLocalAreaRequirements(
      String demandScenarioId) {
    return repository.getLocalAreaRequirements(demandScenarioId);
  }

  /**
   * The Class LocalAreaRequirementRepository.
   */
  public class LocalAreaRequirementRepository extends
      CouchDbRepositorySupport<LocalAreaRequirement> {

    /**
     * Instantiates a new local area requirement repository.
     * 
     * @param db
     *          the db
     */
    public LocalAreaRequirementRepository(CouchDbConnector db) {
      super(LocalAreaRequirement.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the local area requirements.
     * 
     * @param demandScenarioId
     *          the demand scenario id
     * @return the local area requirements
     */
    @View(name = "getLocalAreaRequirements", map = "function(doc) { if(doc.demandScenarioId && doc.docType=='LocalAreaRequirement') "
        + "{emit(doc.demandScenarioId, doc)} }")
    public List<LocalAreaRequirement> getLocalAreaRequirements(
        String demandScenarioId) {
      return queryView("getLocalAreaRequirements", demandScenarioId);
    }
  }
}
