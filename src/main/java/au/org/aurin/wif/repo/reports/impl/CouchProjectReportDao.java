/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo.reports.impl;

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
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.repo.reports.ProjectReportDao;
import au.org.aurin.wif.svc.WifKeys;

// 
/**
 * The Class CouchProjectReportDao. Implements persistence based on CouchDB
 * database specified in the CouchDBManager.
 */
@Component("ProjectReportDao")
public class CouchProjectReportDao implements ProjectReportDao {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchProjectReportDao.class);

  /** The manager. */
  @Autowired
  private CouchDBManager manager;

  /** The repository. */
  private ProjectReportRepository repository;

  /**
   * Inits the ProjectReportDao.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    repository = new ProjectReportRepository(manager.getDb());

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
   * @see au.org.aurin.wif.repo.demand.ProjectReportDao#
   * persistProjectReport(au.org .aurin .wif.model.ProjectReport)
   */
  public ProjectReport persistProjectReport(ProjectReport projectReport) {

    repository.add(projectReport);

    return projectReport;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.ProjectReportDao#
   * findProjectReportById(java.lang .String)
   */
  public ProjectReport findProjectReportById(String id) {
    try {
      ProjectReport projectReport = repository.get(id);
      return projectReport;
    } catch (org.ektorp.DocumentNotFoundException e) {
      LOGGER.warn(WifKeys.STANDARD_ID_NOT_FOUND_MSG, id, manager.getDb()
          .getDbInfo().getDbName());
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.ProjectReportDao#
   * deleteProjectReport(au.org. aurin .wif.model.ProjectReport)
   */
  public void deleteProjectReport(ProjectReport projectReport) {
    repository.remove(projectReport);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.repo.demand.ProjectReportDao#
   * getProjectReports(java.lang .String)
   */
  public List<ProjectReport> getProjectReports(String projectId) {
    return repository.getProjectReports(projectId);
  }

  /**
   * The Class ProjectReportRepository. This helperclass is provided by Ektorp
   * for CRUD operations.
   */
  public class ProjectReportRepository extends
      CouchDbRepositorySupport<ProjectReport> {

    /**
     * Instantiates a new projectReport repository.
     * 
     * @param db
     *          the db
     */
    public ProjectReportRepository(CouchDbConnector db) {
      super(ProjectReport.class, db);
      initStandardDesignDocument();
    }

    // TODO Careful if class is renamed, the map might be broken.
    /**
     * Gets the suitability analysis reports.
     * 
     * @param projectId
     *          the project id
     * @return the suitability analysis reports
     */
    @View(name = "getProjectReports", map = "function(doc) { if(doc.projectId && doc.docType=='ProjectReport') {emit(doc.projectId, doc)} }")
    public List<ProjectReport> getProjectReports(String projectId) {
      return queryView("getProjectReports", projectId);
    }
  }
}
