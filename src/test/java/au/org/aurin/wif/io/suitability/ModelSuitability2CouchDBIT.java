/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.suitability;

import java.util.Collection;

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
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.datacreator.DBSuitabilityDataCreatorService;

/**
 * The Class Model2CouchDBTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ModelSuitability2CouchDBIT extends
    AbstractTestNGSpringContextTests {

  /** The creator. */
  @Autowired
  private DBSuitabilityDataCreatorService creator;

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

  private String suitabilityScenarioId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ModelSuitability2CouchDBIT.class);

  /**
   * Persist project suitability module test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void persistProjectSuitabilityModuleTest() throws Exception {
    projectId = "ProjectTestId" + System.currentTimeMillis();
    suitabilityScenarioId = "ScenarioTestId" + System.currentTimeMillis();
    WifProject project = creator.createSuitabilityModule(projectId,
        suitabilityScenarioId);
    Assert.assertNotNull(project.getId());
    Collection<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (AllocationLU allocationLU : allocationLandUses) {
      Assert.assertNotNull(allocationLU.getId());
    }
    Collection<Factor> factors = project.getFactors();
    for (Factor factor : factors) {
      for (FactorType aFactorType : factor.getFactorTypes()) {
        Assert.assertNotNull(aFactorType.getLabel());
      }
      Assert.assertNotNull(factor.getId());
    }

    Collection<SuitabilityLU> suitabilityLUs = project.getSuitabilityLUs();
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      Assert.assertNotNull(suitabilityLU.getId());
    }
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "persistProjectSuitabilityModuleTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
