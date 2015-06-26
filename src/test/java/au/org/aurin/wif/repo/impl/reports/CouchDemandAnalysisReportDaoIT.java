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
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.reports.demand.DemandAnalysisReportDao;

/**
 * The Class CouchDemandAnalysisReportDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDemandAnalysisReportDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The demand analysis report dao. */
  @Autowired
  private DemandAnalysisReportDao demandAnalysisReportDao;

  /** The demand analysis report label. */
  private final String demandAnalysisReportLabel = "demandAnalysisReportTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandAnalysisReportDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### DemandAnalysisReportDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the demand analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createDemandAnalysisReportTest() throws Exception {

    DemandAnalysisReport demandAnalysisReport = new DemandAnalysisReport();
    demandAnalysisReport.setLabel(demandAnalysisReportLabel);

    demandAnalysisReport.setReportType("DemandAnalysis");
    demandAnalysisReport.setLabel("High Growth");
    demandAnalysisReport.setScenarioLabel("High Growth");
    AreaRequirement areaRequirement = new AreaRequirement();
    areaRequirement.setNewArea(10.0);
    demandAnalysisReport.getOutcome().add(areaRequirement);
    LOGGER.debug("docType: " + demandAnalysisReport.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    demandAnalysisReport.setProjectId(newProjectId);
    DemandAnalysisReport createdDemandAnalysisReport = demandAnalysisReportDao
        .persistDemandAnalysisReport(demandAnalysisReport);
    LOGGER.debug("createdDemandAnalysisReport uuid: "
        + createdDemandAnalysisReport.getId());
    newId = createdDemandAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find demand analysis report by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createDemandAnalysisReportTest" })
  public void findDemandAnalysisReportByIdTest() throws Exception {

    DemandAnalysisReport demandAnalysisReport = demandAnalysisReportDao
        .findDemandAnalysisReportById(newId);
    Assert.assertNotNull(demandAnalysisReport);
    Assert.assertEquals(demandAnalysisReport.getScenarioLabel(), "High Growth");
  }

  /**
   * Gets the all demand analysis reports by project test.
   * 
   * @return the all demand analysis reports by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createDemandAnalysisReportTest" })
  public void getAllDemandAnalysisReportsByProjectTest() throws Exception {
    System.out
        .println("getAllDemandAnalysisReportsByProjectTest project uuid: "
            + newProjectId);
    List<DemandAnalysisReport> allDemandAnalysisReports = demandAnalysisReportDao
        .getDemandAnalysisReports(newProjectId);
    Assert.assertNotNull(allDemandAnalysisReports);
    LOGGER.debug("DemandAnalysisReport size: "
        + allDemandAnalysisReports.size());
    Assert.assertEquals(allDemandAnalysisReports.size(), 1);
  }

  /**
   * Delete demand analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllDemandAnalysisReportsByProjectTest" })
  public void deleteDemandAnalysisReportTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    DemandAnalysisReport demandAnalysisReport = demandAnalysisReportDao
        .findDemandAnalysisReportById(newId);
    demandAnalysisReportDao.deleteDemandAnalysisReport(demandAnalysisReport);
    DemandAnalysisReport demandAnalysisReport2 = demandAnalysisReportDao
        .findDemandAnalysisReportById(newId);
    Assert.assertNull(demandAnalysisReport2);

  }
}
