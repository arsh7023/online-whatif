/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.report.suitability;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.reports.suitability.SuitabilityAnalysisReportDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class SuitabilityAnalysisReportIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityAnalysisReportIT extends
    AbstractTestNGSpringContextTests {

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The suitability analysis report dao. */
  @Autowired
  private SuitabilityAnalysisReportDao suitabilityAnalysisReportDao;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityAnalysisReportIT.class);

  /**
   * Creates the suitability analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "report" })
  public void createSuitabilityAnalysisReportTest() throws Exception {
    LOGGER.debug("createSuitabilityAnalysisReportTest: {}");
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    SuitabilityAnalysisReport suitabilityAnalysisReport = reportService
        .getSuitabilityAnalysisReport(suitabilityScenario);
    newId = suitabilityAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Delete suitability analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "report" }, dependsOnMethods = { "createSuitabilityAnalysisReportTest" })
  public void deleteSuitabilityAnalysisReportTest() throws Exception {

    SuitabilityAnalysisReport suitabilityAnalysisReport = suitabilityAnalysisReportDao
        .findSuitabilityAnalysisReportById(newId);
    suitabilityAnalysisReportDao
        .deleteSuitabilityAnalysisReport(suitabilityAnalysisReport);
    SuitabilityAnalysisReport suitabilityAnalysisReport2 = suitabilityAnalysisReportDao
        .findSuitabilityAnalysisReportById(newId);
    Assert.assertNull(suitabilityAnalysisReport2);

  }
}
