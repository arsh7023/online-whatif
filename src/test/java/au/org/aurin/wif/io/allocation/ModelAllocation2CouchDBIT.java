/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.allocation;

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
import au.org.aurin.wif.svc.datacreator.DBDataCreatorService;

/**
 * The Class ModelAllocationAnalysis2CouchDBIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ModelAllocation2CouchDBIT extends AbstractTestNGSpringContextTests {

  /** The allocation analysis creator. */
  @Autowired
  private DBDataCreatorService dataCreator;

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

  /** The allocation config id. */
  private String allocationConfigId;

  /** The suitability scenario id. */
  private String suitabilityScenarioId;

  private String demandScenarioId;
  private String allocationScenarioId;

  private String allocationReportId;

  private String manualdemandScenarioId;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ModelAllocation2CouchDBIT.class);

  /**
   * Persist allocation analysis module test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void persistAllocationAnalysisModuleTest() throws Exception {
    projectId = "ModelAllocationAnalysis2CouchDBIT"
        + System.currentTimeMillis();
    allocationConfigId = "ModelAllocationAnalysis2CouchDBITAllocationConfigTestId"
        + System.currentTimeMillis();
    suitabilityScenarioId = "ModelAllocationAnalysis2CouchDBITSuitabilityScenarioTestId"
        + System.currentTimeMillis();
    demandScenarioId = "ModelAllocationAnalysis2CouchDBITDemandScenarioTestId"
        + System.currentTimeMillis();
    allocationScenarioId = "ModelAllocationAnalysis2CouchDBITAllocationScenarioTestId"
        + System.currentTimeMillis();
    allocationReportId = "ModelAllocationAnalysis2CouchDBITAllocationReportId"
        + System.currentTimeMillis();
    WifProject project = dataCreator.createDemonstrationModule(projectId,
        suitabilityScenarioId, allocationConfigId, demandScenarioId,
        allocationScenarioId, allocationReportId, manualdemandScenarioId);
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "persistAllocationAnalysisModuleTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
