/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.io.WifFileUtils;
import au.org.aurin.wif.model.WifProject;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class StandAloneProjectIT extends AbstractTestNGSpringContextTests {

  /** The file utils. */
  @Autowired
  private WifFileUtils fileUtils;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The async project service. */
  @Resource
  private AsyncProjectService asyncProjectService;

  /** The project label. */
  private final String projectLabel = "StandAloneProject"
      + System.currentTimeMillis();

  /** The project id. */
  private String projectId;

  /** The local filename. */
  private String localFilename;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(StandAloneProjectIT.class);

  /**
   * Creates the project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createStandAloneProjectTest() throws Exception {
    WifProject project = new WifProject();
    File input = fileUtils.getJsonFile("shapefiles/Perth_small_test.shp");
    localFilename = input.getAbsolutePath();
    project.setName(projectLabel);
    project.setOriginalUnits("metric");
    project.setLocalShpFile(localFilename);
    LOGGER.debug("createStandAloneProjectTest: " + project.getLabel());
    LOGGER.debug("Absolute path  of shape file: " + localFilename);
    project = projectService.createProject(project, "aurin");
    projectId = project.getId();

    LOGGER.debug("project uuid: " + project.getId());

    Future<String> future = asyncProjectService.setupProjectAsync(project,
        "aurin");
    while (!future.isDone()) {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(3000);
    }

    String result = future.get();
    Assert.assertNotNull(projectId);

    LOGGER.debug("finished: {} with status: {}", project.getLabel(), result);
  }

  /**
   * tests the bad project exception deleting.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "setup", "service" }, expectedExceptions = InvalidEntityIdException.class)
  public void createBADProjectTest() throws Exception {
    WifProject project = new WifProject();
    project.setName("   bad project");
    project.setOriginalUnits("metric");
    LOGGER.debug("createbadProjectTest: " + project.getLabel());

    String uri = "bad Datastore URI";

    project.setUazDataStoreURI(uri);
    project = projectService.createProject(project, "aurin");

    LOGGER.debug("bad project uuid: " + project.getId());

    Future<String> result = asyncProjectService.setupProjectAsync(project,
        "aurin");
    while (!result.isDone()) {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(3000);
    }
    try {
      String msg = result.get();
    } catch (ExecutionException e) {

      projectService.purgeProject(project.getId());

    }
    WifProject project2 = projectService.getProject(project.getId());

  }

  /**
   * Find project by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createStandAloneProjectTest" }, groups = {
      "setup", "service" })
  public void findProjectByIdTest() throws Exception {

    WifProject project = projectService.getProject(projectId);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getSuitabilityConfig());
  }

  @Test(enabled = true, dependsOnMethods = { "createStandAloneProjectTest" }, groups = {
      "setup", "service" })
  public void getZipTest() throws Exception {

    File projectzip = projectService.getProjectZipUAZ(projectId);
    Assert.assertTrue(projectzip.canRead());
  }

  /**
   * Upload uaz test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "setup", "service" })
  public void uploadUAZTest() throws Exception {
    WifProject project = projectService.getProject(projectId);

    Future<String> future = asyncProjectService
        .uploadUAZAsync(project, "aurin");
    while (!future.isDone()) {
      LOGGER.debug("Waiting for upload to complete...");
      Thread.sleep(3000);
    }
    Assert.assertNotNull(future.get());
    LOGGER.debug("finished upload of {}, with Datastore URI:   {}",
        project.getLabel(), future.get());
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "createStandAloneProjectTest", "findProjectByIdTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
