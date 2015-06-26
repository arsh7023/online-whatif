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
import au.org.aurin.wif.svc.datacreator.DBDemandAnalysisDataCreatorService;

/**
 * The Class ModelDemandAnalysis2CouchDBIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ModelDemandAnalysis2CouchDBIT extends
    AbstractTestNGSpringContextTests {

  /** The demand analysis creator. */
  @Autowired
  private DBDemandAnalysisDataCreatorService demandAnalysisCreator;

  /** The project service. */
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

  /** The demand config id. */
  private String demandConfigId;

  /** The suitability scenario id. */
  private String suitabilityScenarioId;

  private String demandScenarioId;

  private String manualdemandScenarioId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ModelDemandAnalysis2CouchDBIT.class);

  /**
   * Persist demand analysis module test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void persistDemandAnalysisModuleTest() throws Exception {
    projectId = "ModelDemandAnalysis2CouchDBIT" + System.currentTimeMillis();
    demandConfigId = "ModelDemandAnalysis2CouchDBITDemandConfigTestId"
        + System.currentTimeMillis();
    suitabilityScenarioId = "ModelDemandAnalysis2CouchDBITSuitabilityScenarioTestId"
        + System.currentTimeMillis();
    demandScenarioId = "ModelDemandAnalysis2CouchDBITDemandScenarioTestId"
        + System.currentTimeMillis();
    WifProject project = demandAnalysisCreator.createDemandAnalysisModule(
        projectId, suitabilityScenarioId, demandConfigId, demandScenarioId,
        manualdemandScenarioId);
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "persistDemandAnalysisModuleTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
