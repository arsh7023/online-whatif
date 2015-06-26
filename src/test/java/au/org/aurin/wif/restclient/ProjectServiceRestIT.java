/*
 * 
 */
package au.org.aurin.wif.restclient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class ProjectServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class ProjectServiceRestIT extends AbstractTestNGSpringContextTests {

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /** The project id. */
  private String projectId;

  /** The role id. */
  private final String roleId = WifKeys.TEST_ROLE_ID;

  /** The bad project id. */
  private String badProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectServiceRestIT.class);

  /**
   * Gets the version.
   * 
   * @return the version
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" })
  public void getVersion() {
    final Map<String, String> version = projectServiceClient.getVersion();
    final Set<Entry<String, String>> entrySet = version.entrySet();
    for (final Entry<String, String> entry : entrySet) {
      LOGGER.debug(entry.getKey() + ": {}", entry.getValue());
    }

    Assert.assertNotNull(version);
  }

  /**
   * Creates the project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" })
  public void createProject() throws Exception {
    SslUtil.trustSelfSignedSSL();
    final WifProject wifProject = new WifProject();
    wifProject.setName("ProjectServiceRestIT");
    wifProject.setOriginalUnits("m.k.s.");
    wifProject.setAnalysisOption("suitability");
    wifProject.setUazDataStoreURI(integrationTestConfig.getUazDemoDatastore());
    // wifProject
    // .setUazDataStoreURI(integrationTestConfig.getUnionDemoDatastore());
    projectId = projectServiceClient.createProject(roleId, wifProject);
    Assert.assertNotNull(projectId);
    LOGGER.debug("project Id " + projectId);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(30000);
      resp = projectServiceClient.getStatus(roleId, projectId);
      LOGGER.debug("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Setup finished...");
  }

  /**
   * Creates the bad project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "setup", "service", "projectrest" })
  public void createBadProjectTest() throws Exception {
    final WifProject project = new WifProject();
    project.setName("badProjectProjectServiceRESTIT");
    project.setOriginalUnits("metric");

    LOGGER.debug("createbut  badProjectTest: " + project.getLabel());

    final String uri = "badUrl";

    project.setUazDataStoreURI(uri);
    badProjectId = projectServiceClient.createProject(roleId, project);
    Assert.assertNotNull(badProjectId);

    LOGGER.debug("project uuid: " + badProjectId);
    Assert.assertNotNull(badProjectId);
    LOGGER.debug("project Id " + badProjectId);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(100);
      resp = projectServiceClient.getStatus(roleId, badProjectId);
      LOGGER.debug("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY).equals(WifKeys.PROCESS_STATE_FAILED));
    LOGGER.debug("Setup failed...");
  }

  /**
   * Gets the uAZ attributes.
   * 
   * @return the uAZ attributes
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getUAZAttributes() {
    final List<String> uazAttributes = projectServiceClient.getUAZAttributes(
        roleId, projectId);
    for (final String string : uazAttributes) {
      LOGGER.debug(string);
    }
    Assert.assertNotEquals(uazAttributes.size(), 58);
  }

  /**
   * Gets the distinct entries for uaz attribute.
   * 
   * @return the distinct entries for uaz attribute
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getDistinctEntriesForUAZAttribute() {
    final List<String> uazAttributes = projectServiceClient
        .getDistinctEntriesForUAZAttribute(roleId, projectId, "ELU");
    Assert.assertEquals(uazAttributes.size(), 7);
  }

  /**
   * Gets the project.
   * 
   * @return the project
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getProject() {
    final WifProject project = projectServiceClient.getProject(roleId,
        projectId);

    Assert.assertEquals(project.getLabel(), "ProjectServiceRestIT");
    Assert.assertNotNull(project.getCreationDate());
  }

  /**
   * Gets the project revision.
   * 
   * @return the project revision
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getProjectRevision() {
    final String revision = projectServiceClient.getProjectRevision(roleId,
        projectId);
    Assert.assertNotNull(revision);
  }

  /**
   * Gets the project configuration.
   * 
   * @return the project configuration
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getProjectConfiguration() {
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, WifKeys.TEST_PROJECT_ID);
    LOGGER.debug("fullConfiguration " + project.toString());
    Assert.assertEquals(project.getLabel(), "Demonstration");
    Assert.assertNotNull(project.getCreationDate());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
  }

  /**
   * Update project.
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "getProject" })
  public void updateProject() {
    final WifProject project = projectServiceClient.getProject(roleId,
        projectId);
    project.setName("projectService REST test");
    projectServiceClient.updateProject(roleId, projectId, project);

    final WifProject tmp = projectServiceClient.getProject(roleId, projectId);
    Assert.assertEquals(tmp.getLabel(), "projectService REST test");
    Assert
        .assertNotEquals(project.getModifiedDate(), project.getCreationDate());
  }

  /**
   * Delete project.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @Test(alwaysRun = true, groups = { "restclienttest", "rolerest",
      "projectrest" }, dependsOnMethods = { "getProject", "getUAZAttributes",
      "getDistinctEntriesForUAZAttribute", "updateProject", "createProject" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteProject() throws WifInvalidInputException {
    projectServiceClient.deleteProject(roleId, projectId);
    Assert.assertNull(projectServiceClient.getProject(roleId, projectId));
  }

  /**
   * Gets the project report.
   * 
   * @return the project report
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createProject" })
  public void getProjectReport() {
    final ProjectReport report = projectServiceClient.getProjectReport(roleId,
        WifKeys.TEST_PROJECT_ID);
    final WifProject project = report.getProject();
    Assert.assertEquals(project.getLabel(), "Demonstration");
    Assert.assertNotNull(project.getCreationDate());
  }

}
