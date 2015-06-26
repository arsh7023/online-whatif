/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.report.demand;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.repo.reports.demand.DemandAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class DemandAnalysisReportIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandAnalysisReportIT extends AbstractTestNGSpringContextTests {

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The demand analysis report dao. */
  @Autowired
  private DemandAnalysisReportDao demandAnalysisReportDao;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandAnalysisReportIT.class);

  /**
   * Creates the demand analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "report" })
  public void createDemandAnalysisReportTest() throws Exception {
    LOGGER.debug("createDemandAnalysisReportTest: {}");
    DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(WifKeys.TEST_DEMAND_SCENARIO_ID);
    DemandAnalysisReport demandAnalysisReport = reportService
        .getDemandAnalysisReport(demandScenario);
    newId = demandAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Delete demand analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "report" }, dependsOnMethods = { "createDemandAnalysisReportTest" })
  public void deleteDemandAnalysisReportTest() throws Exception {

    DemandAnalysisReport demandAnalysisReport = demandAnalysisReportDao
        .findDemandAnalysisReportById(newId);
    demandAnalysisReportDao.deleteDemandAnalysisReport(demandAnalysisReport);
    DemandAnalysisReport demandAnalysisReport2 = demandAnalysisReportDao
        .findDemandAnalysisReportById(newId);
    Assert.assertNull(demandAnalysisReport2);

  }
}
