/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.allocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.InfrastructureUses;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationControlScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AllocationControlScenarioServiceIT extends
    AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The allocation scenario service. */
  @Resource
  private AllocationControlScenarioService AllocationControlScenarioService;

  /** The allocation scenario id. */
  private String AllocationControlScenarioId;

  private AllocationConfig allocationConfig;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationControlScenarioServiceIT.class);

  /**
   * Creates the allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "allocation", "service" })
  public void createAllocationControlScenarioTest() throws Exception {
    AllocationControlScenario AllocationControlScenario = new AllocationControlScenario();
    AllocationControlScenario.setFeatureFieldName("No Controls");
    AllocationControlScenario.setLabel("Controls10");
    AllocationControlScenario.setProjectId(WifKeys.TEST_PROJECT_ID);

    AllocationControlScenario.setInfrastructureControl(true);
    AllocationControlScenario.setPlannedlandUseControl(false);
    AllocationControlScenario.setGrowthPatternControl(false);

    Set<String> infrastructureControlLabels = new HashSet<String>();
    // infrastructureControlLabels.add("RoadControl");
    // infrastructureControlLabels.add("WaterControl");

    Set<String> growthPatternControlLabels = new HashSet<String>();
    // growthPatternControlLabels.add("Growth Activity");
    // growthPatternControlLabels.add("Growth Coast");

    AllocationControlScenario
        .setInfrastructureControlLabels(infrastructureControlLabels);

    AllocationControlScenario
        .setGrowthPatternControlLabels(growthPatternControlLabels);

    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Set<InfrastructureUses> infrastructureUses;
    infrastructureUses = new HashSet<InfrastructureUses>();

    InfrastructureUses s = new InfrastructureUses();
    s.setLanduseName("Education");
    Map<String, String> landUseOrderMap = new HashMap<String, String>();
    landUseOrderMap.put("water", "N/A");
    // s.setInfrastructureMap(landUseOrderMap);

    infrastructureUses.add(s);

    AllocationControlScenario.setInfrastructureUses(infrastructureUses);

    AllocationControlScenario createAllocationControlScenario = AllocationControlScenarioService
        .createAllocationControlScenario(AllocationControlScenario,
            WifKeys.TEST_PROJECT_ID);
    AllocationControlScenarioId = createAllocationControlScenario.getId();
  }

  /**
   * Gets the allocation scenario test.
   * 
   * @return the allocation scenario test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "allocation", "service" }, dependsOnMethods = { "createAllocationControlScenarioTest" })
  public void getAllocationControlScenarioTest() throws Exception {
    LOGGER.debug("getAllocationControlScenarioTest: ");
    AllocationControlScenario AllocationControlScenario = AllocationControlScenarioService
        .getAllocationControlScenario(AllocationControlScenarioId);
    Assert.assertNotNull(AllocationControlScenario);
    Assert.assertNotNull(AllocationControlScenario.getProjectId());

  }

  /**
   * Gets the allocation columns.
   * 
   * @return the allocation columns
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "allocation", "service" }, dependsOnMethods = { "createAllocationControlScenarioTest" })
  public void getAllocationColumns() throws Exception {
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(project.getAllocationConfig());
    Assert.assertNotNull(project.getAllocationConfig()
        .getAllocationColumnsMap());
    Assert.assertTrue(project.getAllocationControlScenariosMap().containsKey(
        AllocationControlScenarioId));
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
  @Test(enabled = false, dependsOnMethods = { "getAllocationControlScenarioTest" }, groups = {
      "allocation", "service" })
  public void updateAllocationControlScenarioTest() throws Exception {
    AllocationControlScenario AllocationControlScenario = AllocationControlScenarioService
        .getAllocationControlScenario(AllocationControlScenarioId);
    String AllocationControlScenarioLabel = AllocationControlScenario
        .getLabel();
    System.out
        .println("update AllocationControlScenario test, AllocationControlScenario label: "
            + AllocationControlScenario.getLabel());
    AllocationControlScenario.setLabel("new" + AllocationControlScenarioLabel);
    AllocationControlScenarioService.updateAllocationControlScenario(
        AllocationControlScenario, WifKeys.TEST_PROJECT_ID);
    AllocationControlScenario AllocationControlScenario2 = AllocationControlScenarioService
        .getAllocationControlScenario(AllocationControlScenarioId);
    Assert.assertEquals(AllocationControlScenario2.getLabel(), "new"
        + AllocationControlScenarioLabel);
    AllocationControlScenario.setLabel(AllocationControlScenarioLabel);
    AllocationControlScenarioService.updateAllocationControlScenario(
        AllocationControlScenario, WifKeys.TEST_PROJECT_ID);
  }

  /**
   * Delete allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, alwaysRun = true, dependsOnMethods = { "updateAllocationControlScenarioTest" }, groups = {
      "allocation", "service" })
  public void deleteAllocationControlScenarioTest() throws Exception {
    AllocationControlScenarioService.deleteAllocationControlScenario(
        AllocationControlScenarioId, WifKeys.TEST_PROJECT_ID);
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertFalse(project.getAllocationControlScenariosMap().containsKey(
        AllocationControlScenarioId));
  }
}
