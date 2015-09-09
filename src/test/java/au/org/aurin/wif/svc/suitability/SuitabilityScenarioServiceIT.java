/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.suitability;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.impl.datacreator.DBSuitabilityDataCreatorServiceImpl;
import au.org.aurin.wif.impl.report.ConsoleReporter;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.WifFileUtils;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.datacreator.DBSetupDataCreatorService;

/**
 * The Class SuitabilityScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityScenarioServiceIT extends
    AbstractTestNGSpringContextTests {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityScenarioServiceIT.class);

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The project id. */
  private String projectId;

  /** The project. */
  private WifProject project;

  /** The suitability scenario label. */
  private final String suitabilityScenarioLabel = "suitabilityScenarioTest"
      + System.currentTimeMillis();;

  /** The test id. */
  private String testID;

  /** The reporter. */
  @Autowired
  private ConsoleReporter reporter;

  /** The utils. */
  @Autowired
  private WifFileUtils utils;

  /** The creator. */
  @Autowired
  private DBSetupDataCreatorService creator;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The suitability scenario id. */
  private String suitabilityScenarioId;

  /**
   * Creates the suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service", "suitability" })
  public void createSuitabilityScenarioTest() throws Exception {

    projectId = "suitabilityProjectTestId" + System.currentTimeMillis();
    suitabilityScenarioId = "suitabilityScenarioTestId"
        + System.currentTimeMillis();
    WifProject project = creator.createSetupModule(projectId);
    Assert.assertNotNull(project.getId());
    Assert.assertEquals(project.getLabel(), "Demonstration");
    Assert.assertNotNull(project.getCreationDate());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUs().size());

    // Creating dummy scenario
    SuitabilityScenario suitabilityScenario = DBSuitabilityDataCreatorServiceImpl
        .createSimpleSuitabilityScenario(project, suitabilityScenarioId);

    suitabilityScenario.setLabel(suitabilityScenarioLabel);

    suitabilityScenario = suitabilityScenarioService.createSuitabilityScenario(
        suitabilityScenario, projectId);
    testID = suitabilityScenario.getId();
    Assert.assertNotNull(testID);
  }

  /**
   * Gets the suitability scenario test.
   * 
   * @return the suitability scenario test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityScenarioTest" }, groups = {
      "setup", "service", "suitability" })
  public void getSuitabilityScenarioTest() throws Exception {

    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);
    Assert.assertNotNull(suitabilityScenario.getId());
    Assert.assertTrue(suitabilityScenario.getReady());
  }

  /**
   * Update suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getSuitabilityScenarioTest" }, groups = {
      "setup", "service", "suitability" })
  public void updateSuitabilityScenarioTest() throws Exception {
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);
    System.out
        .println("update suitabilityScenario test, suitabilityScenario label: "
            + suitabilityScenario.getLabel());
    String oldLabel = suitabilityScenario.getLabel();
    suitabilityScenario.setLabel("new" + suitabilityScenarioLabel);
    suitabilityScenarioService.updateSuitabilityScenario(suitabilityScenario,
        projectId);
    SuitabilityScenario suitabilityScenario2 = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);
    Assert.assertEquals(suitabilityScenario2.getLabel(), "new"
        + suitabilityScenarioLabel);
    // Reverting change
    suitabilityScenario.setLabel(oldLabel);
    suitabilityScenarioService.updateSuitabilityScenario(suitabilityScenario,
        projectId);
  }

  /**
   * Gets the suitability scenario with project id test.
   * 
   * @return the suitability scenario with project id test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateSuitabilityScenarioTest" }, groups = {
      "setup", "service", "suitability" })
  public void getSuitabilityScenarioWithProjectIdTest() throws Exception {
    LOGGER
        .debug("getSuitabilityScenarioWithProjectIdTest, suitabilityScenario id: "
            + testID);
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId, projectId);
    Assert.assertNotNull(suitabilityScenario);
  }

  /**
   * Gets the wMS test.
   * 
   * @return the wMS test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getSuitabilityScenarioWithProjectIdTest" }, groups = {
      "setup", "service", "suitability" })
  public void getWMSTest() throws Exception {
    LOGGER.debug("getWMSTest: {}");
    WMSOutcome wms = suitabilityScenarioService
        .getWMS(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    LOGGER.debug("wms.getServerURL() {}", wms.getServerURL());

    LOGGER.debug("wms.getStoreName() {}", wms.getStoreName());
    Map<String, Integer> scoreColumns = wms.getScoreColumns();
    LOGGER.debug("scoreColumns size: {}", scoreColumns.size());

    for (String key : scoreColumns.keySet()) {
      LOGGER.debug("column: {}, range {}", key, scoreColumns.get(key));

    }
    LOGGER.debug("getWMSTest: finished");
    Assert.assertEquals(scoreColumns.size(), 6);
    Assert.assertNotNull(wms.getServerURL());
    Assert.assertTrue(scoreColumns.containsKey("score_residential"));
    Assert.assertEquals(scoreColumns.get("score_residential"), new Integer(
        15000));
  }

  /**
   * Delete suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "getSuitabilityScenarioWithProjectIdTest" }, groups = {
      "setup", "service", "suitability" }, expectedExceptions = {
      InvalidEntityIdException.class, BadSqlGrammarException.class })
  public void deleteSuitabilityScenarioTest() throws Exception {
    System.out
        .println("delete suitabilityScenario test, suitabilityScenario id: "
            + testID);
    projectService.deleteProject(projectId);
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(testID, projectId);
  }
  
  @Test(enabled = true)
	  public void duplicateSuitabilityScenarioTest() throws Exception {

	  
	    String projectid= "DemonstrationTestID";
	   
	    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
	        .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
	    suitabilityScenario.setLabel(suitabilityScenario.getLabel()+"new");
	    
	     project = projectService
	            .getProjectConfiguration(suitabilityScenario.getProjectId());
	    
	    
	    final SuitabilityScenario restoredSuitabilityScenario = suitabilityScenarioService
	            .restoreSuitabilityScenario(suitabilityScenario, project);
	    project.getSuitabilityScenariosMap().put(
	            restoredSuitabilityScenario.getId(),
	            restoredSuitabilityScenario.getLabel());
	    
	    
	    wifProjectDao.updateProject(project);
	    
	    Assert.assertNotNull(suitabilityScenario.getId());
	    Assert.assertTrue(suitabilityScenario.getReady());
	  }
  
  
}
