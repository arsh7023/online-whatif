/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.model.WifProject;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ReusePastProjectUAZIT extends AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The async project service. */
  @Resource
  private AsyncProjectService asyncProjectService;

  /** The project label. */
  private final String projectLabel = "rreuseProject"
      + System.currentTimeMillis();

  /** The project id. */
  private String projectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ReusePastProjectUAZIT.class);

  /**
   * Creates the project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void reuseProjectTest() throws Exception {
    WifProject project = new WifProject();
    project.setName(projectLabel);
    project.setOriginalUnits("metric");
    project.setOwnGeoDatastoreName("uaz_demonstration");
    LOGGER.debug("reuseProjectTest: " + project.getLabel());
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
   * Find project by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "reuseProjectTest" }, groups = {
      "setup", "service" })
  public void findProjectByIdTest() throws Exception {

    WifProject project = projectService.getProject(projectId);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getSuitabilityConfig());
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "reuseProjectTest", "findProjectByIdTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
