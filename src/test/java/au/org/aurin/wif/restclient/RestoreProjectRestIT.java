package au.org.aurin.wif.restclient;

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
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.restclient.suitability.SuitabilityScenarioServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class RestoreProjectRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class RestoreProjectRestIT extends AbstractTestNGSpringContextTests {

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

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(RestoreProjectRestIT.class);

  /** The suitability scenario service client. */
  @Autowired
  private SuitabilityScenarioServiceClient suitabilityScenarioServiceClient;

  /**
   * Restore project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest" })
  public void restoreProject() throws Exception {
    LOGGER.debug("restoreProject");
    SslUtil.trustSelfSignedSSL();

    final WifProject projectTest = projectServiceClient.getProject(roleId,
        WifKeys.TEST_PROJECT_ID);
    final ProjectReport projectReport = new ProjectReport();
    projectReport.setLabel(projectTest.getName());
    projectReport.setReportType(projectTest.getDocType());
    projectReport.setServiceVersion(WifKeys.WIF_KEY_VERSION);

    final SuitabilityScenario suitabilityScenario = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, WifKeys.TEST_PROJECT_ID,
            WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    projectReport.getSuitabilityScenarios().add(suitabilityScenario);

    projectTest.getSuitabilityScenariosMap().clear();
    projectTest.getDemandScenariosMap().clear();
    projectTest.getAllocationScenariosMap().clear();
    projectReport.setProject(projectTest);
    projectReport.setProjectId(projectTest.getId());
    projectId = projectServiceClient.restoreProject(roleId, projectReport);
    final WifProject project = projectServiceClient.getProject(roleId,
        projectId);
    Assert.assertEquals(project.getName(), "Demonstration");
    LOGGER.debug(project.toString());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityConfig().getScoreColumns()
        .size());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
    Assert.assertNotNull(project.getFactors().iterator().next()
        .getFactorTypes().iterator().next().getFactorId());
    Assert.assertNotNull(project.getAllocationLandUses().iterator().next()
        .getId());
  }

  /**
   * Delete project.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @Test(alwaysRun = true, groups = { "restclienttest", "rolerest" }, dependsOnMethods = { "restoreProject" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteProject() throws WifInvalidInputException {
    projectServiceClient.deleteProject(roleId, projectId, false);
    Assert.assertNull(projectServiceClient.getProject(roleId, projectId));
  }
}
