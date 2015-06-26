/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.io.File;

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
import au.org.aurin.wif.model.reports.ProjectReport;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class RestoreProjectIT extends AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The project id. */
  private String projectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(RestoreProjectIT.class);

  @Autowired
  private WifFileUtils fileUtils;

  /** The mapper. */
  private ObjectMapper mapper;

  @Test(enabled = true, groups = { "setup", "interface" })
  public void restoreProjectTest() throws Exception {

    LOGGER.debug("restoreProjectTest");

    mapper = new ObjectMapper();
    File input = fileUtils.getJsonFile("JSONs/RestoreDemonstrationReport.json");
    ProjectReport projectReport = mapper.readValue(input, ProjectReport.class);

    WifProject restoreProject = projectService
        .restoreProjectConfiguration(projectReport);
    Assert.assertEquals(restoreProject.getName(), "Demonstration");
    LOGGER.debug(restoreProject.toString());
    projectId = restoreProject.getId();
    Assert.assertNotNull(restoreProject.getId());
    Assert.assertNotNull(restoreProject.getSuitabilityConfig()
        .getScoreColumns().size());
    Assert.assertNotNull(restoreProject.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
    Assert.assertNotNull(restoreProject.getFactors().iterator().next()
        .getFactorTypes().iterator().next().getFactorId());
    Assert.assertNotNull(restoreProject.getAllocationLandUses().iterator()
        .next().getId());
    Assert.assertTrue(restoreProject.getSuitabilityScenariosMap()
        .containsValue("Suburbanization"));
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "restoreProjectTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId, false);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
