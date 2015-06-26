/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.validation.BindException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.IncompleteSuitabilityLUConfigException;
import au.org.aurin.wif.exception.validate.UAZAlreadyCreatedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ProjectServiceIT extends AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The async project service. */
  @Resource
  private AsyncProjectService asyncProjectService;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /** The project label. */
  private final String projectLabel = "ProjectServiceIT"
      + System.currentTimeMillis();

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  /** The project id. */
  private String projectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectServiceIT.class);

  /**
   * Creates the project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createProjectTest() throws Exception {
    WifProject project = new WifProject();
    project.setName(projectLabel);
    project.setOriginalUnits("metric");
    LOGGER.debug("createProjectTest: " + project.getLabel());

    String uri = integrationTestConfig.getUnionDemoDatastore();

    project.setUazDataStoreURI(uri);
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
  @Test(enabled = true, groups = { "setup", "service" }, dependsOnMethods = { "createProjectTest" }, expectedExceptions = InvalidEntityIdException.class)
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
  @Test(enabled = true, dependsOnMethods = { "createProjectTest" }, groups = {
      "setup", "service" })
  public void findProjectByIdTest() throws Exception {

    WifProject project = projectService.getProject(projectId);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getSuitabilityConfig());
    Assert.assertNotNull(project.getBbox());
  }

  /**
   * Update project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createProjectTest" }, groups = {
      "setup", "service" })
  public void updateProjectTest() throws Exception {
    WifProject project = projectService.getProject(projectId);
    project.setName("new" + projectLabel);
    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setProjectId(projectId);
    allocationLU.setLabel("Conservation");
    allocationLU.setFeatureFieldName("name");

    projectService.updateProject(project);
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        projectId);
    WifProject project2 = projectService.getProject(projectId);
    Assert.assertEquals(project2.getLabel(), "new" + projectLabel);
    Assert.assertNotNull(project.getSuitabilityConfig());
    Assert.assertNotNull(project.getSuitabilityConfig().getScoreColumns());
    AllocationLU conservationLU = project2
        .getExistingLandUseByLabel("Conservation");
    Assert.assertNotNull(conservationLU);
  }

  /**
   * Gets the all projects by role test.
   * 
   * @return the all projects by role test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateProjectTest" }, groups = {
      "setup", "service" })
  public void getAllProjectsByRoleTest() throws Exception {

    List<WifProject> allProjects = projectService.getAllProjects("aurin");
    Assert.assertNotNull(allProjects);
    Assert.assertNotEquals(allProjects.size(), 0);
  }

  /**
   * Update uaz test.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws UAZAlreadyCreatedException
   *           the uAZ already created exception
   * @throws IncompleteSuitabilityLUConfigException
   *           the incomplete suitability lu config exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws FactoryException
   *           the factory exception
   * @throws GeoServerConfigException
   *           the geo server config exception
   * @throws DataStoreCreationException
   */
  @Test(enabled = true, dependsOnMethods = { "updateProjectTest" }, groups = {
      "setup", "service" }, expectedExceptions = IncompleteSuitabilityLUConfigException.class)
  public void updateUAZTest() throws WifInvalidInputException, BindException,
      WifInvalidConfigException, UAZAlreadyCreatedException,
      IncompleteSuitabilityLUConfigException, NoSuchAuthorityCodeException,
      DataStoreUnavailableException, FactoryException,
      GeoServerConfigException, DataStoreCreationException {
    List<String> factorsList = new ArrayList<String>();
    factorsList.add("testColumn");
    projectService.convertUnionToUAZ(projectId, factorsList, "aurin");
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "createProjectTest", "updateProjectTest", "getAllProjectsByRoleTest",
      "updateUAZTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
