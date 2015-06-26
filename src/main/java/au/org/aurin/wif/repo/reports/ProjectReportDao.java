package au.org.aurin.wif.repo.reports;

import java.util.List;

import au.org.aurin.wif.model.reports.ProjectReport;

/**
 * The Interface ProjectReportDao.
 */
public interface ProjectReportDao {

  /**
   * Persist project report.
   * 
   * @param projectReport
   *          the project report
   * @return the project report
   */
  ProjectReport persistProjectReport(ProjectReport projectReport);

  /**
   * Find project report by id.
   * 
   * @param id
   *          the id
   * @return the project report
   */
  ProjectReport findProjectReportById(String id);

  /**
   * Delete project report.
   * 
   * @param projectReport
   *          the project report
   */
  void deleteProjectReport(ProjectReport projectReport);

  /**
   * Gets the project reports.
   * 
   * @param projectId
   *          the project id
   * @return the project reports
   */
  List<ProjectReport> getProjectReports(String projectId);
}
