/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.reports;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.reports.allocation.AllocationAnalysisReportDao;

/**
 * The Class CouchAllocationAnalysisReportDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchAllocationAnalysisReportDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The allocation analysis report dao. */
  @Autowired
  private AllocationAnalysisReportDao allocationAnalysisReportDao;

  /** The allocation analysis report label. */
  private final String allocationAnalysisReportLabel = "allocationAnalysisReportTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationAnalysisReportDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### AllocationAnalysisReportDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the allocation analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createAllocationAnalysisReportTest() throws Exception {

    AllocationAnalysisReport allocationAnalysisReport = new AllocationAnalysisReport();
    allocationAnalysisReport.setLabel(allocationAnalysisReportLabel);

    allocationAnalysisReport.setReportType("AllocationAnalysis");

    allocationAnalysisReport
        .setScenarioLabel("Suburbanization-High-Growth-No-Controls");

    LOGGER.debug("docType: " + allocationAnalysisReport.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    allocationAnalysisReport.setProjectId(newProjectId);
    AllocationAnalysisReport createdAllocationAnalysisReport = allocationAnalysisReportDao
        .persistAllocationAnalysisReport(allocationAnalysisReport);
    LOGGER.debug("createdAllocationAnalysisReport uuid: "
        + createdAllocationAnalysisReport.getId());
    newId = createdAllocationAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation analysis report by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createAllocationAnalysisReportTest" })
  public void findAllocationAnalysisReportByIdTest() throws Exception {

    AllocationAnalysisReport allocationAnalysisReport = allocationAnalysisReportDao
        .findAllocationAnalysisReportById(newId);
    Assert.assertNotNull(allocationAnalysisReport);
    Assert.assertEquals(allocationAnalysisReport.getScenarioLabel(),
        "Suburbanization-High-Growth-No-Controls");
  }

  /**
   * Gets the all allocation analysis reports by project test.
   * 
   * @return the all allocation analysis reports by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createAllocationAnalysisReportTest" })
  public void getAllAllocationAnalysisReportsByProjectTest() throws Exception {
    System.out
        .println("getAllAllocationAnalysisReportsByProjectTest project uuid: "
            + newProjectId);
    List<AllocationAnalysisReport> allAllocationAnalysisReports = allocationAnalysisReportDao
        .getAllocationAnalysisReports(newProjectId);
    Assert.assertNotNull(allAllocationAnalysisReports);
    LOGGER.debug("AllocationAnalysisReport size: "
        + allAllocationAnalysisReports.size());
    Assert.assertEquals(allAllocationAnalysisReports.size(), 1);
  }

  /**
   * Delete allocation analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllAllocationAnalysisReportsByProjectTest" })
  public void deleteAllocationAnalysisReportTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    AllocationAnalysisReport allocationAnalysisReport = allocationAnalysisReportDao
        .findAllocationAnalysisReportById(newId);
    allocationAnalysisReportDao
        .deleteAllocationAnalysisReport(allocationAnalysisReport);
    AllocationAnalysisReport allocationAnalysisReport2 = allocationAnalysisReportDao
        .findAllocationAnalysisReportById(newId);
    Assert.assertNull(allocationAnalysisReport2);

  }
}
