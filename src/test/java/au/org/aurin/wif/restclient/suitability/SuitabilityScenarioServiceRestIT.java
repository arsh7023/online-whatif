package au.org.aurin.wif.restclient.suitability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.datacreator.DBSuitabilityDataCreatorServiceImpl;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class SuitabilityScenarioServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class SuitabilityScenarioServiceRestIT extends
    AbstractTestNGSpringContextTests {

  /** The suitability scenario service client. */
  @Autowired
  private SuitabilityScenarioServiceClient suitabilityScenarioServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The suitability scenario id. */
  private String suitabilityScenarioId;

  /** The role id. */
  private final String roleId = "aurin";

  /** The suitability scenario label. */
  private final String suitabilityScenarioLabel = "suitabilityScenarioTest474533%##$%%18";

  /** The wif project id. */
  String wifProjectId;

  /** The suitability lu id. */
  private String suitabilityLUId;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityScenarioServiceRestIT.class);

  /**
   * Setup.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @BeforeClass(enabled = true, groups = { "restclienttest", "suitability" })
  public void setup() throws WifInvalidInputException {
    SslUtil.trustSelfSignedSSL();
    wifProjectId = WifKeys.TEST_PROJECT_ID;
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    LOGGER.debug("fullConfiguration " + project.toString());
    final AllocationLU allocationLU = project
        .getExistingLandUseByLabel("Industrial");
    final SuitabilityLU suitabilityLU = project
        .getSuitabilityLUByName("Residential");
    suitabilityLUId = suitabilityLU.getId();
    allocationLUId = allocationLU.getId();
    Assert.assertEquals(project.getLabel(), "Demonstration");
    Assert.assertNotNull(project.getCreationDate());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
  }

  /**
   * Creates the suitability scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" })
  public void createSuitabilityScenario() throws Exception {
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    // Creating dummy scenario

    suitabilityScenarioId = "restsuitabilityTestId"
        + System.currentTimeMillis();
    final SuitabilityScenario suitabilityScenario = DBSuitabilityDataCreatorServiceImpl
        .createSimpleSuitabilityScenario(project, suitabilityScenarioId);

    suitabilityScenario.setLabel(suitabilityScenarioLabel);

    suitabilityScenarioId = suitabilityScenarioServiceClient
        .createSuitabilityScenario(roleId, wifProjectId, suitabilityScenario);
    Assert.assertNotNull(suitabilityScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getSuitabilityScenariosMap());
    Assert.assertTrue(project2.getSuitabilityScenariosMap().containsKey(
        suitabilityScenarioId));
  }

  /**
   * Gets the suitability scenario.
   * 
   * @return the suitability scenario
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "createSuitabilityScenario" })
  public void getSuitabilityScenario() throws Exception {
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, wifProjectId, suitabilityScenarioId);
    Assert.assertEquals(suitabilityScenario.getLabel(),
        suitabilityScenarioLabel);
  }

  /**
   * Gets the suitability scenarios for project.
   * 
   * @return the suitability scenarios for project
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = "getSuitabilityScenario")
  public void getSuitabilityScenariosForProject() throws Exception {
    final List<SuitabilityScenario> list = suitabilityScenarioServiceClient
        .getSuitabilityScenariosForProject(roleId, wifProjectId);
    LOGGER.debug("Scenarios = " + list.size());
    Assert.assertNotEquals(list.size(), 1);
  }

  /**
   * Update suitability scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "getSuitabilityScenariosForProject" })
  public void updateSuitabilityScenario() throws Exception {
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, wifProjectId, suitabilityScenarioId);
    suitabilityScenario.setLabel("suitabilityScenario REST test");
    suitabilityScenarioServiceClient.updateSuitabilityScenario(roleId,
        wifProjectId, suitabilityScenarioId, suitabilityScenario);

    final SuitabilityScenario tmp = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, wifProjectId, suitabilityScenarioId);
    Assert.assertEquals(tmp.getLabel(), "suitabilityScenario REST test");
  }

  /**
   * Update complex scenario.
   * 
   * @throws Exception
   *           the exception
   */

  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = "updateSuitabilityScenario")
  public void updateComplexScenario() throws Exception {
    // Get the scenario
    LOGGER.debug("****************** updateComplexScenario");
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, wifProjectId, suitabilityScenarioId);
    Assert.assertEquals(suitabilityScenario.getLabel(),
        "suitabilityScenario REST test");

    // Making changes
    suitabilityScenario.setLabel("Update Suburbanization");
    final Set<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    Assert.assertEquals(suitabilityRules.size(), 1);
    final SuitabilityRule rule = suitabilityRules.iterator().next();
    Assert.assertEquals(rule.getConvertibleLUsMap().size(), 2);
    rule.getConvertibleLUsMap().put(allocationLUId, "Residential");
    final Set<FactorImportance> factorImportances = rule.getFactorImportances();
    for (final FactorImportance factorImportance : factorImportances) {
      final Set<FactorTypeRating> factorTypeRatings = factorImportance
          .getFactorTypeRatings();
      for (final FactorTypeRating factorTypeRating : factorTypeRatings) {
        factorTypeRating.setScore(11.1);
      }
      factorImportance.setImportance(33.0);
    }
    // TODO Make this test a little bit more specific!
    // boolean found = false;
    // boolean notFound = true;

    // Updating scenario
    suitabilityScenarioServiceClient.updateSuitabilityScenario(roleId,
        WifKeys.TEST_PROJECT_ID.toString(),
        WifKeys.TEST_SUITABILITY_SCENARIO_ID, suitabilityScenario);

    // Checking changes
    final SuitabilityScenario changedSuitabilityScenario = suitabilityScenarioServiceClient
        .getSuitabilityScenario(roleId, wifProjectId, suitabilityScenarioId);
    Assert.assertEquals(changedSuitabilityScenario.getLabel(),
        "Update Suburbanization");
    final Set<SuitabilityRule> changedSuitabilityRules = changedSuitabilityScenario
        .getSuitabilityRules();
    Assert.assertEquals(changedSuitabilityRules.size(), 1);

    Assert.assertEquals(changedSuitabilityRules.size(), 1);
    final SuitabilityRule changedRule = changedSuitabilityRules.iterator()
        .next();
    Assert.assertEquals(changedRule.getConvertibleLUsMap().size(), 3);
    final Set<FactorImportance> changedFactorImportances = changedRule
        .getFactorImportances();
    for (final FactorImportance factorImportance : changedFactorImportances) {
      Assert.assertEquals(factorImportance.getImportance(), 33.0);
      final Set<FactorTypeRating> factorTypeRatings = factorImportance
          .getFactorTypeRatings();
      for (final FactorTypeRating factorTypeRating : factorTypeRatings) {
        Assert.assertEquals(factorTypeRating.getScore(), 11.1);
      }
    }

    // LOGGER.debug("found rating: " + found);
    // Assert.assertTrue(found);
    // LOGGER.debug("LU not found: " + notFound);
    // Assert.assertTrue(notFound);

  }

  /**
   * Gets the wMS outcome async.
   * 
   * @return the wMS outcome async
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = "updateComplexScenario")
  public void getWMSOutcomeAsync() throws Exception {

    // Getting the WMS Outcome Async
    final String areaAnalyzed = WifKeys.POLYGON_TEST;
    final String crsArea = WifKeys.CRS_TEST;
    suitabilityScenarioServiceClient.getWMSOutcomeAsync(roleId, wifProjectId,
        suitabilityScenarioId, areaAnalyzed, crsArea);
    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(5000);
      resp = suitabilityScenarioServiceClient.getStatus(roleId, wifProjectId,
          suitabilityScenarioId);
      LOGGER.debug("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Analysis finished...");
  }

  /**
   * Gets the wMS test.
   * 
   * @return the wMS test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getWMSOutcomeAsync" }, groups = {
      "restclienttest", "suitability" })
  public void getWMSTest() throws Exception {
    LOGGER.debug("getWMSTest: {}");
    final WMSOutcome wms = suitabilityScenarioServiceClient.getWMS(roleId,
        WifKeys.TEST_PROJECT_ID, WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    LOGGER.debug("wms.getServerURL() {}", wms.getServerURL());

    LOGGER.debug("wms.getStoreName() {}", wms.getStoreName());
    final Map<String, Integer> scoreColumns = wms.getScoreColumns();
    LOGGER.debug("scoreColumns size: {}", scoreColumns.size());

    for (final String key : scoreColumns.keySet()) {
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
   * Gets the suitability scenario report.
   * 
   * @return the suitability scenario report
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "createSuitabilityScenario" })
  public void getSuitabilityScenarioReport() throws Exception {
    final SuitabilityAnalysisReport suitabilityAnalysisReport = suitabilityScenarioServiceClient
        .getSuitabilityScenarioReport(roleId, WifKeys.TEST_PROJECT_ID,
            WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    Assert.assertEquals(suitabilityAnalysisReport.getReportType(),
        "SuitabilityScenario");
  }

  /**
   * Delete suitability scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "restclienttest",
      "suitability" }, dependsOnMethods = { "updateSuitabilityScenario",
      "updateComplexScenario", "getWMSOutcomeAsync", "getWMSTest",
      "getSuitabilityScenarioReport" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteSuitabilityScenario() throws Exception {
    suitabilityScenarioServiceClient.deleteSuitabilityScenario(roleId,
        wifProjectId, suitabilityScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getSuitabilityScenariosMap());
    Assert.assertFalse(project2.getSuitabilityScenariosMap().containsKey(
        suitabilityScenarioId));
    Assert.assertNull(suitabilityScenarioServiceClient.getSuitabilityScenario(
        roleId, wifProjectId, suitabilityScenarioId));
  }

}
