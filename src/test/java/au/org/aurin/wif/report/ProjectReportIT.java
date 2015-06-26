/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.report;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.repo.reports.ProjectReportDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class ProjectReportIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ProjectReportIT extends AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The project report dao. */
  @Autowired
  private ProjectReportDao projectReportDao;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectReportIT.class);

  /**
   * Creates the project report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "report" })
  public void createProjectReportTest() throws Exception {
    LOGGER.debug("createProjectReportTest: {}");
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    ProjectReport projectReport = reportService.getProjectReport(project);
    newId = projectReport.getId();

    Assert.assertNotNull(newId);
    Assert.assertNotNull(projectReport.getProject());
    Assert.assertNotEquals(projectReport.getSuitabilityScenarios().size(), 0);
    LOGGER.debug("createProjectReport ID: {}", newId);
  }

  /**
   * Delete project report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, alwaysRun = true, groups = { "report" }, dependsOnMethods = { "createProjectReportTest" })
  public void deleteProjectReportTest() throws Exception {

    ProjectReport projectReport = projectReportDao.findProjectReportById(newId);
    projectReportDao.deleteProjectReport(projectReport);
    ProjectReport projectReport2 = projectReportDao
        .findProjectReportById(newId);
    Assert.assertNull(projectReport2);

  }
}
