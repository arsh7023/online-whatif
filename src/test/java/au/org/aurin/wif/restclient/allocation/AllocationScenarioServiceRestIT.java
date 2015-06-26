package au.org.aurin.wif.restclient.allocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationScenarioServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class AllocationScenarioServiceRestIT extends
    AbstractTestNGSpringContextTests {

  /** The allocation scenario service client. */
  @Autowired
  private AllocationScenarioServiceClient allocationScenarioServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The allocation scenario id. */
  private String allocationScenarioId;

  /** The role id. */
  private final String roleId = "aurin";

  /** The allocation scenario label. */
  private final String allocationScenarioLabel = "allocationScenarioTest474533%##$%%18";

  /** The wif project id. */
  String wifProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationScenarioServiceRestIT.class);

  /**
   * Creates the allocation scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "allocation" })
  public void createAllocationScenario() throws Exception {
    SslUtil.trustSelfSignedSSL();
    wifProjectId = WifKeys.TEST_PROJECT_ID;
    // Creating dummy scenario
    LOGGER.debug("createAllocationScenario");
    allocationScenarioId = "restallocationTestId" + System.currentTimeMillis();
    final AllocationScenario allocationScenario = new AllocationScenario();

    allocationScenario.setFeatureFieldName("No Controls");
    allocationScenario.setLabel(allocationScenarioLabel);
    allocationScenario.setProjectId(WifKeys.TEST_PROJECT_ID);
    allocationScenario
        .setSuitabilityScenarioId(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    allocationScenario.setDemandScenarioId(WifKeys.TEST_DEMAND_SCENARIO_ID);

    final WifProject projectOld = projectServiceClient.getProject(roleId,
        WifKeys.TEST_PROJECT_ID);
    final Map<String, Integer> landUseOrderMap = new HashMap<String, Integer>();
    final AllocationLU allocationlu = projectOld
        .getExistingLandUseByLabel("Local Retail");
    allocationlu.setAllocationFeatureFieldName("283.0");
    landUseOrderMap.put(allocationlu.getId(), 1);
    allocationScenario.setLandUseOrderMap(landUseOrderMap);

    allocationScenarioId = allocationScenarioServiceClient
        .createAllocationScenario(roleId, wifProjectId, allocationScenario);
    Assert.assertNotNull(allocationScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getAllocationScenariosMap());
    Assert.assertTrue(project2.getAllocationScenariosMap().containsKey(
        allocationScenarioId));
  }

  /**
   * Gets the allocation scenario.
   * 
   * @return the allocation scenario
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "allocation" }, dependsOnMethods = { "createAllocationScenario" })
  public void getAllocationScenario() throws Exception {
    final AllocationScenario allocationScenario = allocationScenarioServiceClient
        .getAllocationScenario(roleId, wifProjectId, allocationScenarioId);
    Assert.assertEquals(allocationScenario.getLabel(), allocationScenarioLabel);
    Assert.assertNotEquals(allocationScenario.getLandUseOrderMap().size(), 0);
  }

  /**
   * Gets the allocation config test.
   * 
   * @return the allocation config test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, dependsOnMethods = { "getAllocationScenario" }, groups = {
      "setup", "service" })
  public void getAllocationConfigTest() throws Exception {

    final WifProject project = projectServiceClient.getProject(roleId,
        WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(project.getAllocationConfig()
        .getAllocationColumnsMap());
    Assert.assertNotEquals(project.getAllocationConfig()
        .getUndevelopedLUsColumns().size(), 0);
  }

  /**
   * Gets the allocation scenarios for project.
   * 
   * @return the allocation scenarios for project
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "allocation" }, dependsOnMethods = "getAllocationScenario")
  public void getAllocationScenariosForProject() throws Exception {
    final List<AllocationScenario> list = allocationScenarioServiceClient
        .getAllocationScenariosForProject(roleId, wifProjectId);
    LOGGER.debug("Scenarios = " + list.size());
    Assert.assertNotEquals(list.size(), 1);
  }

  /**
   * Update allocation scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "allocation" }, dependsOnMethods = { "getAllocationScenariosForProject" })
  public void updateAllocationScenario() throws Exception {
    final AllocationScenario allocationScenario = allocationScenarioServiceClient
        .getAllocationScenario(roleId, wifProjectId, allocationScenarioId);
    allocationScenario.setLabel("Allocation REST test");
    allocationScenarioServiceClient.updateAllocationScenario(roleId,
        wifProjectId, allocationScenarioId, allocationScenario);

    final AllocationScenario tmp = allocationScenarioServiceClient
        .getAllocationScenario(roleId, wifProjectId, allocationScenarioId);
    Assert.assertEquals(tmp.getLabel(), "Allocation REST test");
  }

  /**
   * Gets the outcome async.
   * 
   * @return the outcome async
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "allocation" }, dependsOnMethods = "updateAllocationScenario")
  public void getOutcomeAsync() throws Exception {
    LOGGER.debug("Getting the Outcome Async = ");
    // Getting the Outcome Async
    allocationScenarioServiceClient.getOutcomeAsync(roleId,
        WifKeys.TEST_PROJECT_ID, WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(8000);
      resp = allocationScenarioServiceClient.getStatus(roleId,
          WifKeys.TEST_PROJECT_ID, WifKeys.TEST_ALLOCATION_SCENARIO_ID);
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
  @Test(enabled = false, dependsOnMethods = { "getOutcomeAsync" }, groups = {
      "restclienttest", "suitability" })
  public void getWMSTest() throws Exception {
    LOGGER.debug("getWMSTest: {}");
    final WMSOutcome wms = allocationScenarioServiceClient.getWMS(roleId,
        WifKeys.TEST_PROJECT_ID, WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    LOGGER.debug("wms.getServerURL() {}", wms.getServerURL());

    LOGGER.debug("wms.getStoreName() {}", wms.getStoreName());
    final Map<String, List<String>> scoreColumns = wms.getAllocationLabels();
    LOGGER.debug("using the following {} columns for the outcome layers", wms
        .getAllocationLabels().size());
    for (final Entry<String, List<String>> aluLabel : wms.getAllocationLabels()
        .entrySet()) {
      LOGGER.debug("suitabilityLU ={}", aluLabel.getKey());
      final List<String> values = aluLabel.getValue();
      for (final String label : values) {
        LOGGER.debug("Future field name ={}", label);
      }
    }
    LOGGER.debug("getWMSTest: finished");
    Assert.assertEquals(scoreColumns.size(), 6);
    Assert.assertNotNull(wms.getServerURL());
    Assert.assertTrue(scoreColumns.containsKey("Residential"));
    final List<String> list = scoreColumns.get("Residential");
    Assert.assertTrue(list.contains("203.0"));
  }

  /**
   * Delete allocation scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, alwaysRun = true, groups = { "restclienttest",
      "allocation" }, dependsOnMethods = { "updateAllocationScenario" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteAllocationScenario() throws Exception {
    allocationScenarioServiceClient.deleteAllocationScenario(roleId,
        wifProjectId, allocationScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getAllocationScenariosMap());
    Assert.assertFalse(project2.getAllocationScenariosMap().containsKey(
        allocationScenarioId));
    Assert.assertNull(allocationScenarioServiceClient.getAllocationScenario(
        roleId, wifProjectId, allocationScenarioId));
  }

}
