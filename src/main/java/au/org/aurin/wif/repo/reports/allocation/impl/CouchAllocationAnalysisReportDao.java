/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.reports.allocation.impl;

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
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.repo.reports.allocation.AllocationAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchAllocationAnalysisReportDao. Implements persistence based on
 * CouchDB database specified in the CouchDBManager.
 */
@Component("AllocationAnalysisReportDao")
public class CouchAllocationAnalysisReportDao implements
    AllocationAnalysisReportDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationAnalysisReportDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private AllocationAnalysisReportRepository repository;

  /**
   * Inits the AllocationAnalysisReportDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new AllocationAnalysisReportRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.demand.AllocationAnalysisReportDao#
   * persistAllocationAnalysisReport(au.org .aurin
   * .wif.model.AllocationAnalysisReport)
   */
  public AllocationAnalysisReport persistAllocationAnalysisReport(
      AllocationAnalysisReport allocationAnalysisReport) {

    repository.add(allocationAnalysisReport);

    return allocationAnalysisReport;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.AllocationAnalysisReportDao#
   * findAllocationAnalysisReportById(java.lang .String)
   */
  public AllocationAnalysisReport findAllocationAnalysisReportById(String id) {
    try {
      AllocationAnalysisReport allocationAnalysisReport = repository.get(id);
      return allocationAnalysisReport;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.AllocationAnalysisReportDao#
   * deleteAllocationAnalysisReport(au.org. aurin
   * .wif.model.AllocationAnalysisReport)
   */
  public void deleteAllocationAnalysisReport(
      AllocationAnalysisReport allocationAnalysisReport) {
    repository.remove(allocationAnalysisReport);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.AllocationAnalysisReportDao#
   * getAllocationAnalysisReports(java.lang .String)
   */
  public List<AllocationAnalysisReport> getAllocationAnalysisReports(
      String projectId) {
    return repository.getAllocationAnalysisReports(projectId);
  }

  /**
   * The Class AllocationAnalysisReportRepository. This helperclass is provided
   * by Ektorp for CRUD operations.
   */
  public class AllocationAnalysisReportRepository extends
      CouchDbRepositorySupport<AllocationAnalysisReport> {

    /**
     * Instantiates a new allocationAnalysisReport repository.
     * 
     * @param db
     *          the db
     */
    public AllocationAnalysisReportRepository(CouchDbConnector db) {
      super(AllocationAnalysisReport.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the allocation analysis reports.
     * 
     * @param projectId
     *          the project id
     * @return the allocation analysis reports
     */
    @View(name = "getAllocationAnalysisReports", map = "function(doc) { if(doc.projectId && doc.docType=='AllocationAnalysisReport') {emit(doc.projectId, doc)} }")
    public List<AllocationAnalysisReport> getAllocationAnalysisReports(
        String projectId) {
      return queryView("getAllocationAnalysisReports", projectId);
    }
  }
}
