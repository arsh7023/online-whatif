package au.org.aurin.wif.repo.reports.demand;

import java.util.List;

import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;

/**
 * The Interface DemandAnalysisReportDao.
 */
public interface DemandAnalysisReportDao {

  /**
   * Persist demand analysis report.
   * 
   * @param demandAnalysisReport
   *          the demand analysis report
   * @return the demand analysis report
   */
  DemandAnalysisReport persistDemandAnalysisReport(
      DemandAnalysisReport demandAnalysisReport);

  /**
   * Find demand analysis report by id.
   * 
   * @param id
   *          the id
   * @return the demand analysis report
   */
  DemandAnalysisReport findDemandAnalysisReportById(String id);

  /**
   * Delete demand analysis report.
   * 
   * @param demandAnalysisReport
   *          the demand analysis report
   */
  void deleteDemandAnalysisReport(DemandAnalysisReport demandAnalysisReport);

  /**
   * Gets the demand analysis reports.
   * 
   * @param projectId
   *          the project id
   * @return the demand analysis reports
   */
  List<DemandAnalysisReport> getDemandAnalysisReports(String projectId);
}
