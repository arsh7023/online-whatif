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
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchSuitabilityRuleDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("SuitabilityRuleDao")
public class CouchSuitabilityRuleDao implements SuitabilityRuleDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityRuleDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private SuitabilityRuleRepository repository;

  /**
   * Inits the SuitabilityRuleDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new SuitabilityRuleRepository(manager.getDb());

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
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#addSuitabilityRule(au.org
   * .aurin .wif .model.SuitabilityRule)
   */
  public void addSuitabilityRule(SuitabilityRule suitabilityRule) {
    repository.add(suitabilityRule);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#persistSuitabilityRule(
   * au.org .aurin .wif.model.SuitabilityRule)
   */
  public SuitabilityRule persistSuitabilityRule(SuitabilityRule suitabilityRule) {

    repository.add(suitabilityRule);

    return suitabilityRule;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#updateSuitabilityRule(au
   * .org .aurin .wif.model.SuitabilityRule)
   */
  public void updateSuitabilityRule(SuitabilityRule suitabilityRule) {
    repository.update(suitabilityRule);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#findSuitabilityRuleById
   * (java .lang .String)
   */
  public SuitabilityRule findSuitabilityRuleById(String id) {
    try {
      SuitabilityRule suitabilityRule = repository.get(id);
      return suitabilityRule;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#deleteSuitabilityRule(au
   * .org .aurin .wif.model.SuitabilityRule)
   */
  public void deleteSuitabilityRule(SuitabilityRule suitabilityRule) {
    repository.remove(suitabilityRule);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.repo.demand.SuitabilityRuleDao#getSuitabilityRules(java
   * .lang .String)
   */
  public List<SuitabilityRule> getSuitabilityRules(String projectId) {
    return repository.getSuitabilityRules(projectId);
  }

  /**
   * The Class SuitabilityRuleRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class SuitabilityRuleRepository extends
      CouchDbRepositorySupport<SuitabilityRule> {

    /**
     * Instantiates a new suitabilityRule repository.
     * 
     * @param db
     *          the db
     */
    public SuitabilityRuleRepository(CouchDbConnector db) {
      super(SuitabilityRule.class, db);
      initStandardDesignDocument();

    }

    /**
     * Gets the suitability rules.
     *
     * @param scenarioId the scenario id
     * @return the suitability rules
     */
    @View(name = "getSuitabilityRules", map = "function(doc) { if(doc.scenarioId && doc.docType=='SuitabilityRule') "
        + "{emit(doc.scenarioId, doc)} }")
    public List<SuitabilityRule> getSuitabilityRules(String scenarioId) {
      return queryView("getSuitabilityRules", scenarioId);
    }
  }
}
