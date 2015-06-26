/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.reports;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.suitability.CategoryItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisItem;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.reports.suitability.SuitabilityAnalysisReportDao;

/**
 * The Class CouchSuitabilityAnalysisReportDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchSuitabilityAnalysisReportDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The suitability analysis report dao. */
  @Autowired
  private SuitabilityAnalysisReportDao suitabilityAnalysisReportDao;

  /** The suitability analysis report label. */
  private final String suitabilityAnalysisReportLabel = "suitabilityAnalysisReportTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityAnalysisReportDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### SuitabilityAnalysisReportDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the suitability analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createSuitabilityAnalysisReportTest() throws Exception {

    SuitabilityAnalysisReport suitabilityAnalysisReport = new SuitabilityAnalysisReport();
    suitabilityAnalysisReport.setLabel(suitabilityAnalysisReportLabel);

    suitabilityAnalysisReport.setReportType("SuitabilityAnalysis");
    suitabilityAnalysisReport.setLabel("Suburbanization");
    suitabilityAnalysisReport.setScenarioLabel("Suburbanization");
    SuitabilityAnalysisItem item = new SuitabilityAnalysisItem();
    item.setSuitabilityLULabel("Residential");
    Set<CategoryItem> suitabilityCategories = new HashSet<CategoryItem>();
    CategoryItem categoryItem = new CategoryItem();
    categoryItem.setArea(940.4);
    categoryItem.setCategory("Low");
    categoryItem.setScoreRange("1.0 to 3000");
    suitabilityCategories.add(categoryItem);
    item.setSuitabilityCategories(suitabilityCategories);
    item.setTotalArea(5520.86);

    LOGGER.debug("docType: " + suitabilityAnalysisReport.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    suitabilityAnalysisReport.setProjectId(newProjectId);
    SuitabilityAnalysisReport createdSuitabilityAnalysisReport = suitabilityAnalysisReportDao
        .persistSuitabilityAnalysisReport(suitabilityAnalysisReport);
    LOGGER.debug("createdSuitabilityAnalysisReport uuid: "
        + createdSuitabilityAnalysisReport.getId());
    newId = createdSuitabilityAnalysisReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find suitability analysis report by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityAnalysisReportTest" })
  public void findSuitabilityAnalysisReportByIdTest() throws Exception {

    SuitabilityAnalysisReport suitabilityAnalysisReport = suitabilityAnalysisReportDao
        .findSuitabilityAnalysisReportById(newId);
    Assert.assertNotNull(suitabilityAnalysisReport);
    Assert.assertEquals(suitabilityAnalysisReport.getScenarioLabel(),
        "Suburbanization");
  }

  /**
   * Gets the all suitability analysis reports by project test.
   * 
   * @return the all suitability analysis reports by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityAnalysisReportTest" })
  public void getAllSuitabilityAnalysisReportsByProjectTest() throws Exception {
    System.out
        .println("getAllSuitabilityAnalysisReportsByProjectTest project uuid: "
            + newProjectId);
    List<SuitabilityAnalysisReport> allSuitabilityAnalysisReports = suitabilityAnalysisReportDao
        .getSuitabilityAnalysisReports(newProjectId);
    Assert.assertNotNull(allSuitabilityAnalysisReports);
    LOGGER.debug("SuitabilityAnalysisReport size: "
        + allSuitabilityAnalysisReports.size());
    Assert.assertEquals(allSuitabilityAnalysisReports.size(), 1);
  }

  /**
   * Delete suitability analysis report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllSuitabilityAnalysisReportsByProjectTest" })
  public void deleteSuitabilityAnalysisReportTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    SuitabilityAnalysisReport suitabilityAnalysisReport = suitabilityAnalysisReportDao
        .findSuitabilityAnalysisReportById(newId);
    suitabilityAnalysisReportDao
        .deleteSuitabilityAnalysisReport(suitabilityAnalysisReport);
    SuitabilityAnalysisReport suitabilityAnalysisReport2 = suitabilityAnalysisReportDao
        .findSuitabilityAnalysisReportById(newId);
    Assert.assertNull(suitabilityAnalysisReport2);

  }
}
