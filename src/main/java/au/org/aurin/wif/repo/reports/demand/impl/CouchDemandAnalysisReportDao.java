/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.reports.demand.impl;

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
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.repo.reports.demand.DemandAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchDemandAnalysisReportDao. Implements persistence based on
 * CouchDB database specified in the CouchDBManager.
 */
@Component("DemandAnalysisReportDao")
public class CouchDemandAnalysisReportDao implements DemandAnalysisReportDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandAnalysisReportDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private DemandAnalysisReportRepository repository;

  /**
   * Inits the DemandAnalysisReportDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new DemandAnalysisReportRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.demand.DemandAnalysisReportDao#
   * persistDemandAnalysisReport(au.org .aurin .wif.model.DemandAnalysisReport)
   */
  public DemandAnalysisReport persistDemandAnalysisReport(
      DemandAnalysisReport demandAnalysisReport) {

    repository.add(demandAnalysisReport);

    return demandAnalysisReport;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.DemandAnalysisReportDao#
   * findDemandAnalysisReportById(java.lang .String)
   */
  public DemandAnalysisReport findDemandAnalysisReportById(String id) {
    try {
      DemandAnalysisReport demandAnalysisReport = repository.get(id);
      return demandAnalysisReport;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.DemandAnalysisReportDao#
   * deleteDemandAnalysisReport(au.org. aurin .wif.model.DemandAnalysisReport)
   */
  public void deleteDemandAnalysisReport(
      DemandAnalysisReport demandAnalysisReport) {
    repository.remove(demandAnalysisReport);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.DemandAnalysisReportDao#
   * getDemandAnalysisReports(java.lang .String)
   */
  public List<DemandAnalysisReport> getDemandAnalysisReports(String projectId) {
    return repository.getDemandAnalysisReports(projectId);
  }

  /**
   * The Class DemandAnalysisReportRepository. This helperclass is provided by
   * Ektorp for CRUD operations.
   */
  public class DemandAnalysisReportRepository extends
      CouchDbRepositorySupport<DemandAnalysisReport> {

    /**
     * Instantiates a new demandAnalysisReport repository.
     * 
     * @param db
     *          the db
     */
    public DemandAnalysisReportRepository(CouchDbConnector db) {
      super(DemandAnalysisReport.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the demand analysis reports.
     * 
     * @param projectId
     *          the project id
     * @return the demand analysis reports
     */
    @View(name = "getDemandAnalysisReports", map = "function(doc) { if(doc.projectId && doc.docType=='DemandAnalysisReport') {emit(doc.projectId, doc)} }")
    public List<DemandAnalysisReport> getDemandAnalysisReports(String projectId) {
      return queryView("getDemandAnalysisReports", projectId);
    }
  }
}
