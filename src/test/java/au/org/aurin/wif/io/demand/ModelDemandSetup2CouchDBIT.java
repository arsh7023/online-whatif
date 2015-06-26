/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.demand;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.datacreator.DBDemandSetupDataCreatorService;

/**
 * The Class Model2CouchDBTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ModelDemandSetup2CouchDBIT extends
    AbstractTestNGSpringContextTests {

  @Autowired
  private DBDemandSetupDataCreatorService demandSetupCreator;

  @Resource
  private ProjectService projectService;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The project id. */
  private String projectId;

  private String demandConfigId;

  private String suitabilityScenarioId;

  private String manualdemandScenarioId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ModelDemandSetup2CouchDBIT.class);

  /**
   * Persist project suitability module test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "database", "couchdb" })
  public void persistDemandSetupModuleTest() throws Exception {
    projectId = "ModelDemandSetup2CouchDBIT" + System.currentTimeMillis();
    demandConfigId = "ModelDemandSetup2CouchDBITDemandConfigTestId"
        + System.currentTimeMillis();
    suitabilityScenarioId = "ScenarioTestId" + System.currentTimeMillis();

    WifProject project = demandSetupCreator.createDemandSetupModule(projectId,
        suitabilityScenarioId, demandConfigId, manualdemandScenarioId);
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "persistDemandSetupModuleTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "demand", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
