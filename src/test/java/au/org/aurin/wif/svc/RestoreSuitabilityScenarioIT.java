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
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class RestoreSuitabilityScenarioIT extends
    AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The project id. */
  private String scenarioId;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The wif suitabilityScenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(RestoreSuitabilityScenarioIT.class);

  @Autowired
  private WifFileUtils fileUtils;

  /** The mapper. */
  private ObjectMapper mapper;

  @Test(enabled = true, groups = { "setup", "interface" })
  public void restoreSuitabilityScenarioTest() throws Exception {

    LOGGER.debug("restoreSuitabilityScenarioTest");

    mapper = new ObjectMapper();
    File input = fileUtils.getJsonFile("JSONs/RestoreSuburbanization.json");
    SuitabilityScenario suitabilityScenario = mapper.readValue(input,
        SuitabilityScenario.class);

    WifProject restoreProject = projectService
        .getProject(WifKeys.TEST_PROJECT_ID);
    SuitabilityScenario restoredSuitabilityScenario = suitabilityScenarioService
        .restoreSuitabilityScenario(suitabilityScenario, restoreProject);
    Assert.assertEquals(restoredSuitabilityScenario.getLabel(),
        "Suburbanization");
    LOGGER.debug(restoredSuitabilityScenario.toString());
    scenarioId = restoredSuitabilityScenario.getId();
    Assert.assertNotNull(restoreProject.getId());
    Assert.assertNotNull(restoredSuitabilityScenario.getSuitabilityRules()
        .size());

  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "restoreSuitabilityScenarioTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteSuitabilityScenarioTest() throws Exception {
    LOGGER.debug("deleteSuitabilityScenarioTest, project id: " + scenarioId);
    suitabilityScenarioService.deleteSuitabilityScenario(scenarioId,
        WifKeys.TEST_PROJECT_ID);
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(scenarioId);

  }
}
