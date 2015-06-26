/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.demand;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class DemandScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandOutcomeServiceIT extends AbstractTestNGSpringContextTests {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandOutcomeServiceIT.class);

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The demand scenario service. */
  @Resource
  private DemandOutcomeService manualdemandScenarioService;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand scenario id. */
  private String manualdemandScenarioId;

  /**
   * Creates the demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "service", "manualdemandScenario" })
  public void createManualDemandScenarioTest() throws Exception {
    LOGGER.debug("createDemandScenarioTest");
    final WifProject project = projectService
        .getProject(WifKeys.TEST_PROJECT_ID);
    // AllocationLU residentialLowLU = project
    // .getExistingLandUseByLabel("Low Density Res.");
    // AllocationLU regionalRetailLU = project
    // .getExistingLandUseByLabel("Regional Retail");
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(project.getId());
    // EmploymentSector sectorRetailTrade =
    // demandConfig.getSectorByLabel("Retail Trade");

    // Create projections years
    final Projection projection0 = new Projection();
    final Projection projection1 = new Projection();
    final Projection projection2 = new Projection();

    projection0.setLabel("2005");
    projection0.setYear(2005);
    projection0.setWifProject(project);

    projection1.setLabel("2010");
    projection1.setYear(2010);
    projection1.setWifProject(project);

    projection2.setLabel("2015");
    projection2.setYear(2015);
    projection2.setWifProject(project);

    final Set<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.add(projection0);
    projections.add(projection1);
    projections.add(projection2);
    // DemographicTrend highGrowthDemographicTrend = manualdemandConfig
    // .getTrendByLabel("High Growth Trend");

    demandConfig.setProjections(projections);

    final DemandOutcome testmanualDemandScenario = new DemandOutcome();
    testmanualDemandScenario.setLabel("High Growth New");

    final DemandOutcome createmanualDemandScenario = manualdemandScenarioService
        .createDemandOutcomeNew(testmanualDemandScenario,
            WifKeys.TEST_PROJECT_ID);
    manualdemandScenarioId = createmanualDemandScenario.getId();

    final AreaRequirement area1 = new AreaRequirement();
    area1.setAllocationLUId("9e4f0e162729c1946280571819005074");
    area1.setAllocationLULabel("Local Retail");
    area1.setDemandScenarioId(manualdemandScenarioId);
    area1.setRequiredArea(100.0);
    area1.setProjection(projection0);
    area1.setProjectionLabel(projection0.getLabel());
    area1.setRequiredArea(1.0);
    area1.setUnchangedArea(1.0);
    final AreaRequirement area2 = new AreaRequirement();
    area2.setAllocationLUId("9e4f0e162729c1946280571819000714");
    area2.setAllocationLULabel("Water");
    area2.setDemandScenarioId(manualdemandScenarioId);
    area2.setRequiredArea(200.0);
    area2.setProjection(projection1);
    area2.setProjectionLabel(projection1.getLabel());
    area2.setRequiredArea(2.0);
    area2.setUnchangedArea(2.0);
    final Set<AreaRequirement> ManualAreaRequirements = new HashSet<AreaRequirement>();
    ManualAreaRequirements.add(area1);
    ManualAreaRequirements.add(area2);

    final DemandOutcome maunaldemandScenario = manualdemandScenarioService
        .getDemandOutcome(manualdemandScenarioId);
    maunaldemandScenario.setAreaRequirements(ManualAreaRequirements);

    manualdemandScenarioService.updateDemandOutcome(maunaldemandScenario,
        WifKeys.TEST_PROJECT_ID);

    LOGGER.debug("createDemandScenarioTest finished");
  }

  /**
   * Gets the demand scenario test.
   * 
   * @return the demand scenario test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "service", "manualdemandScenario" }, dependsOnMethods = { "createManualDemandScenarioTest" })
  public void getManualDemandScenarioTest() throws Exception {
    LOGGER.debug("getDemandScenarioTest: ");
    final DemandOutcome maunaldemandScenario = manualdemandScenarioService
        .getDemandOutcome(manualdemandScenarioId);
    Assert.assertNotNull(maunaldemandScenario);
    Assert.assertNotNull(maunaldemandScenario.getProjectId());
    Assert.assertEquals(maunaldemandScenario.getProjectId(),
        WifKeys.TEST_PROJECT_ID);
    final WifProject project = projectService
        .getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertTrue(project.getDemandOutcomesMap().containsKey(
        manualdemandScenarioId));
  }

  /**
   * Update demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getManualDemandScenarioTest" }, groups = {
      "demand", "service", "manualdemandScenario" })
  public void updateManualDemandScenarioTest() throws Exception {
    final DemandOutcome manualdemandScenario = manualdemandScenarioService
        .getDemandOutcome(manualdemandScenarioId);
    final String manualdemandScenarioLabel = manualdemandScenario.getLabel();
    LOGGER
        .debug("update manualdemandScenario test, manualdemandScenario label: "
            + manualdemandScenario.getLabel());
    manualdemandScenario.setLabel("new" + manualdemandScenarioLabel);
    manualdemandScenarioService.updateDemandOutcome(manualdemandScenario,
        WifKeys.TEST_PROJECT_ID);
    final DemandOutcome manualdemandScenario2 = manualdemandScenarioService
        .getDemandOutcome(manualdemandScenarioId);
    Assert.assertEquals(manualdemandScenario2.getLabel(), "new"
        + manualdemandScenarioLabel);
    manualdemandScenario.setLabel(manualdemandScenarioLabel);
    manualdemandScenarioService.updateDemandOutcome(manualdemandScenario,
        WifKeys.TEST_PROJECT_ID);
  }

  /**
   * Delete demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "updateManualDemandScenarioTest" }, groups = {
      "demand", "service", "demandScenario" })
  public void deleteManualDemandScenarioTest() throws Exception {
    manualdemandScenarioService.deleteDemandOutcome(manualdemandScenarioId,
        WifKeys.TEST_PROJECT_ID);
    final WifProject project = projectService
        .getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertFalse(project.getDemandOutcomesMap().containsKey(
        manualdemandScenarioId));
  }
}
