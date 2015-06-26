package au.org.aurin.wif.repo.reports.suitability;

import java.util.List;

import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;

/**
 * The Interface SuitabilityAnalysisReportDao.
 */
public interface SuitabilityAnalysisReportDao {

  /**
   * Persist suitability analysis report.
   * 
   * @param suitabilityAnalysisReport
   *          the suitability analysis report
   * @return the suitability analysis report
   */
  SuitabilityAnalysisReport persistSuitabilityAnalysisReport(
      SuitabilityAnalysisReport suitabilityAnalysisReport);

  /**
   * Find suitability analysis report by id.
   * 
   * @param id
   *          the id
   * @return the suitability analysis report
   */
  SuitabilityAnalysisReport findSuitabilityAnalysisReportById(String id);

  /**
   * Delete suitability analysis report.
   * 
   * @param suitabilityAnalysisReport
   *          the suitability analysis report
   */
  void deleteSuitabilityAnalysisReport(
      SuitabilityAnalysisReport suitabilityAnalysisReport);

  /**
   * Gets the suitability analysis reports.
   * 
   * @param projectId
   *          the project id
   * @return the suitability analysis reports
   */
  List<SuitabilityAnalysisReport> getSuitabilityAnalysisReports(String projectId);
}
