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
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.reports.ProjectReportDao;

/**
 * The Class ProjectReportDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchProjectReportDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The project report dao. */
  @Autowired
  private ProjectReportDao projectReportDao;

  /** The project report label. */
  private final String projectReportLabel = "projectReportTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchProjectReportDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### ProjectReportDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the project report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createProjectReportTest() throws Exception {

    ProjectReport projectReport = new ProjectReport();
    projectReport.setLabel(projectReportLabel);
    projectReport.setReportType("ProjectReport");
    projectReport.setProject(wifProjectDao.findProjectById(newProjectId));
    LOGGER.debug("docType: " + projectReport.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    projectReport.setProjectId(newProjectId);
    ProjectReport createdProjectReport = projectReportDao
        .persistProjectReport(projectReport);
    LOGGER.debug("createdProjectReport uuid: " + createdProjectReport.getId());
    newId = createdProjectReport.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find project report by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createProjectReportTest" })
  public void findProjectReportByIdTest() throws Exception {

    ProjectReport projectReport = projectReportDao.findProjectReportById(newId);
    Assert.assertNotNull(projectReport);
    Assert.assertEquals(projectReport.getDocType(), "ProjectReport");
  }

  /**
   * Gets the all project reports by project test.
   * 
   * @return the all project reports by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createProjectReportTest" })
  public void getAllProjectReportsByProjectTest() throws Exception {
    System.out.println("getAllProjectReportsByProjectTest project uuid: "
        + newProjectId);
    List<ProjectReport> allProjectReports = projectReportDao
        .getProjectReports(newProjectId);
    Assert.assertNotNull(allProjectReports);
    LOGGER.debug("ProjectReport size: " + allProjectReports.size());
    Assert.assertEquals(allProjectReports.size(), 1);
  }

  /**
   * Delete project report test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllProjectReportsByProjectTest" })
  public void deleteProjectReportTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    ProjectReport projectReport = projectReportDao.findProjectReportById(newId);
    projectReportDao.deleteProjectReport(projectReport);
    ProjectReport projectReport2 = projectReportDao
        .findProjectReportById(newId);
    Assert.assertNull(projectReport2);

  }
}
