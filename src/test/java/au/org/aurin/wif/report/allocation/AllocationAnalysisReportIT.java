/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.report.allocation;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.repo.reports.allocation.AllocationAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class AllocationAnalysisReportIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AllocationAnalysisReportIT extends
    AbstractTestNGSpringContextTests {

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The allocation analysis report dao. */
  @Autowired
  private AllocationAnalysisReportDao allocationAnalysisReportDao;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationAnalysisReportIT.class);

  /**
   * Creates the allocation analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "report" })
  public void createAllocationAnalysisReportTest() throws Exception {
    LOGGER.debug("createAllocationAnalysisReportTest: {}");
    AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    AllocationAnalysisReport allocationAnalysisReport = reportService
        .getAllocationAnalysisReport(allocationScenario);
    newId = allocationAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Delete allocation analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "report" }, dependsOnMethods = { "createAllocationAnalysisReportTest" })
  public void deleteAllocationAnalysisReportTest() throws Exception {

    AllocationAnalysisReport allocationAnalysisReport = allocationAnalysisReportDao
        .findAllocationAnalysisReportById(newId);
    allocationAnalysisReportDao
        .deleteAllocationAnalysisReport(allocationAnalysisReport);
    AllocationAnalysisReport allocationAnalysisReport2 = allocationAnalysisReportDao
        .findAllocationAnalysisReportById(newId);
    Assert.assertNull(allocationAnalysisReport2);

  }
}
