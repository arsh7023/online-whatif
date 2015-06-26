package au.org.aurin.wif.repo.reports.allocation;

import java.util.List;

import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;

/**
 * The Interface AllocationAnalysisReportDao.
 */
public interface AllocationAnalysisReportDao {

  /**
   * Persist allocation analysis report.
   * 
   * @param allocationAnalysisReport
   *          the allocation analysis report
   * @return the allocation analysis report
   */
  AllocationAnalysisReport persistAllocationAnalysisReport(
      AllocationAnalysisReport allocationAnalysisReport);

  /**
   * Find allocation analysis report by id.
   * 
   * @param id
   *          the id
   * @return the allocation analysis report
   */
  AllocationAnalysisReport findAllocationAnalysisReportById(String id);

  /**
   * Delete allocation analysis report.
   * 
   * @param allocationAnalysisReport
   *          the allocation analysis report
   */
  void deleteAllocationAnalysisReport(
      AllocationAnalysisReport allocationAnalysisReport);

  /**
   * Gets the allocation analysis reports.
   * 
   * @param projectId
   *          the project id
   * @return the allocation analysis reports
   */
  List<AllocationAnalysisReport> getAllocationAnalysisReports(String projectId);
}
