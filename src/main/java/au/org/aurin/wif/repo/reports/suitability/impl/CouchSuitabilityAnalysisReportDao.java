/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.reports.suitability.impl;

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
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.repo.reports.suitability.SuitabilityAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchSuitabilityAnalysisReportDao. Implements persistence based on
 * CouchDB database specified in the CouchDBManager.
 */
@Component("SuitabilityAnalysisReportDao")
public class CouchSuitabilityAnalysisReportDao implements
    SuitabilityAnalysisReportDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityAnalysisReportDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private SuitabilityAnalysisReportRepository repository;

  /**
   * Inits the SuitabilityAnalysisReportDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new SuitabilityAnalysisReportRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.demand.SuitabilityAnalysisReportDao#
   * persistSuitabilityAnalysisReport(au.org .aurin
   * .wif.model.SuitabilityAnalysisReport)
   */
  public SuitabilityAnalysisReport persistSuitabilityAnalysisReport(
      SuitabilityAnalysisReport suitabilityAnalysisReport) {

    repository.add(suitabilityAnalysisReport);

    return suitabilityAnalysisReport;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.SuitabilityAnalysisReportDao#
   * findSuitabilityAnalysisReportById(java.lang .String)
   */
  public SuitabilityAnalysisReport findSuitabilityAnalysisReportById(String id) {
    try {
      SuitabilityAnalysisReport suitabilityAnalysisReport = repository.get(id);
      return suitabilityAnalysisReport;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.SuitabilityAnalysisReportDao#
   * deleteSuitabilityAnalysisReport(au.org. aurin
   * .wif.model.SuitabilityAnalysisReport)
   */
  public void deleteSuitabilityAnalysisReport(
      SuitabilityAnalysisReport suitabilityAnalysisReport) {
    repository.remove(suitabilityAnalysisReport);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.SuitabilityAnalysisReportDao#
   * getSuitabilityAnalysisReports(java.lang .String)
   */
  public List<SuitabilityAnalysisReport> getSuitabilityAnalysisReports(
      String projectId) {
    return repository.getSuitabilityAnalysisReports(projectId);
  }

  /**
   * The Class SuitabilityAnalysisReportRepository. This helperclass is provided
   * by Ektorp for CRUD operations.
   */
  public class SuitabilityAnalysisReportRepository extends
      CouchDbRepositorySupport<SuitabilityAnalysisReport> {

    /**
     * Instantiates a new suitabilityAnalysisReport repository.
     * 
     * @param db
     *          the db
     */
    public SuitabilityAnalysisReportRepository(CouchDbConnector db) {
      super(SuitabilityAnalysisReport.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the suitability analysis reports.
     *
     * @param projectId the project id
     * @return the suitability analysis reports
     */
    @View(name = "getSuitabilityAnalysisReports", map = "function(doc) { if(doc.projectId && doc.docType=='SuitabilityAnalysisReport') {emit(doc.projectId, doc)} }")
    public List<SuitabilityAnalysisReport> getSuitabilityAnalysisReports(
        String projectId) {
      return queryView("getSuitabilityAnalysisReports", projectId);
    }
  }
}
