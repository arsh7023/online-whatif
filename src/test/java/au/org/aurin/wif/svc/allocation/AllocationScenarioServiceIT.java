/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.allocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AllocationScenarioServiceIT extends
    AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The allocation scenario id. */
  private String allocationScenarioId;

  private AllocationConfig allocationConfig;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationScenarioServiceIT.class);

  /**
   * Creates the allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "allocation", "service" })
  public void createAllocationScenarioTest() throws Exception {
    AllocationScenario allocationScenario = new AllocationScenario();
    allocationScenario.setFeatureFieldName("No Controls");
    allocationScenario.setLabel("No Controls");
    allocationScenario.setProjectId(WifKeys.TEST_PROJECT_ID);
    allocationScenario
        .setSuitabilityScenarioId(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    allocationScenario.setDemandScenarioId(WifKeys.TEST_DEMAND_SCENARIO_ID);
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Map<String, Integer> landUseOrderMap = new HashMap<String, Integer>();
    allocationScenario.setLandUseOrderMap(landUseOrderMap);
    AllocationLU allocationlu = project
        .getExistingLandUseByLabel("Local Retail");
    allocationlu.setAllocationFeatureFieldName("283.0");
    landUseOrderMap.put(allocationlu.getId(), 1);

    AllocationScenario createAllocationScenario = allocationScenarioService
        .createAllocationScenario(allocationScenario, WifKeys.TEST_PROJECT_ID);
    allocationScenarioId = createAllocationScenario.getId();
  }

  /**
   * Gets the allocation scenario test.
   * 
   * @return the allocation scenario test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "allocation", "service" }, dependsOnMethods = { "createAllocationScenarioTest" })
  public void getAllocationScenarioTest() throws Exception {
    LOGGER.debug("getAllocationScenarioTest: ");
    AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(allocationScenarioId);
    Assert.assertNotNull(allocationScenario);
    Assert.assertNotNull(allocationScenario.getProjectId());
    Assert.assertEquals(allocationScenario.getProjectId(),
        WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(allocationScenario.getAllocationConfig());

  }

  /**
   * Gets the allocation columns.
   * 
   * @return the allocation columns
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "allocation", "service" }, dependsOnMethods = { "createAllocationScenarioTest" })
  public void getAllocationColumns() throws Exception {
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(project.getAllocationConfig());
    Assert.assertNotNull(project.getAllocationConfig()
        .getAllocationColumnsMap());
    Assert.assertTrue(project.getAllocationScenariosMap().containsKey(
        allocationScenarioId));
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    Collection<String> values = project.getAllocationConfig()
        .getAllocationColumnsMap().values();
    List<String> attrs = geodataFinder.getUAZAttributes(uazDBTable);
    Assert.assertTrue(attrs.contains(values.iterator().next()));
  }

  /**
   * Update allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getAllocationScenarioTest" }, groups = {
      "allocation", "service" })
  public void updateAllocationScenarioTest() throws Exception {
    AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(allocationScenarioId);
    String allocationScenarioLabel = allocationScenario.getLabel();
    System.out
        .println("update allocationScenario test, allocationScenario label: "
            + allocationScenario.getLabel());
    allocationScenario.setLabel("new" + allocationScenarioLabel);
    allocationScenarioService.updateAllocationScenario(allocationScenario,
        WifKeys.TEST_PROJECT_ID);
    AllocationScenario allocationScenario2 = allocationScenarioService
        .getAllocationScenario(allocationScenarioId);
    Assert.assertEquals(allocationScenario2.getLabel(), "new"
        + allocationScenarioLabel);
    allocationScenario.setLabel(allocationScenarioLabel);
    allocationScenarioService.updateAllocationScenario(allocationScenario,
        WifKeys.TEST_PROJECT_ID);
  }

  /**
   * Gets the wMS test.
   * 
   * @return the wMS test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getAllocationScenarioTest" }, groups = {
      "allocation", "service" })
  public void getWMSTest() throws Exception {
    LOGGER.debug("getWMSTest: {}");
    WMSOutcome wms = allocationScenarioService
        .getWMS(WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    LOGGER.debug("wms.getServerURL() {}", wms.getServerURL());

    LOGGER.debug("wms.getStoreName() {}", wms.getStoreName());
    Map<String, List<String>> scoreColumns = wms.getAllocationLabels();
    LOGGER.debug("using the following {} columns for the outcome layers", wms
        .getAllocationLabels().size());
    for (Entry<String, List<String>> aluLabel : wms.getAllocationLabels()
        .entrySet()) {
      LOGGER.debug("suitabilityLU ={}", aluLabel.getKey());
      List<String> values = aluLabel.getValue();
      for (String label : values) {
        LOGGER.debug("Future field name ={}", label);
      }
    }
    LOGGER.debug("getWMSTest: finished");
    Assert.assertEquals(scoreColumns.size(), 6);
    Assert.assertNotNull(wms.getServerURL());
    Assert.assertTrue(scoreColumns.containsKey("Residential"));
    List<String> list = scoreColumns.get("Residential");
    Assert.assertTrue(list.contains("203.0"));
  }

  /**
   * Delete allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "updateAllocationScenarioTest" }, groups = {
      "allocation", "service" })
  public void deleteAllocationScenarioTest() throws Exception {
    allocationScenarioService.deleteAllocationScenario(allocationScenarioId,
        WifKeys.TEST_PROJECT_ID);
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertFalse(project.getAllocationScenariosMap().containsKey(
        allocationScenarioId));
  }
}
