/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.demand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.data.EmploymentData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class DemandScenarioServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandScenarioServiceIT extends AbstractTestNGSpringContextTests {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioServiceIT.class);

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand scenario id. */
  private String demandScenarioId;

  /** The outcome. */
  private List<AreaRequirement> outcome;

  /**
   * Creates the demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "service", "demandScenario" })
  public void createDemandScenarioTest() throws Exception {
    LOGGER.debug("createDemandScenarioTest");
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    AllocationLU residentialLowLU = project
        .getExistingLandUseByLabel("Low Density Res.");
    AllocationLU regionalRetailLU = project
        .getExistingLandUseByLabel("Regional Retail");
    DemandConfig demandConfig = demandConfigService.getDemandConfig(project
        .getId());
    EmploymentSector sectorRetailTrade = demandConfig
        .getSectorByLabel("Retail Trade");

    Projection projection0 = demandConfig.getProjectionByLabel("2005");
    Projection projection1 = demandConfig.getProjectionByLabel("2010");
    Projection projection2 = demandConfig.getProjectionByLabel("2015");
    DemographicTrend highGrowthDemographicTrend = demandConfig
        .getTrendByLabel("High Growth Trend");

    DemandScenario testDemandScenario = new DemandScenario();
    testDemandScenario.setFeatureFieldName("High Growth");
    testDemandScenario.setLabel("High Growth");
    testDemandScenario.setDemographicTrendLabel("High Growth");
    testDemandScenario.setProjectId(project.getId());
    testDemandScenario.setDemographicTrendLabel(highGrowthDemographicTrend
        .getLabel());

    // low residential information for demand
    ResidentialDemandInfo rdinfo = new ResidentialDemandInfo();
    rdinfo.setInfillRate(0.0);
    rdinfo.setFutureBreakdownByHType(0.60);
    rdinfo.setCurrentDensity(0.94);
    rdinfo.setFutureDensity(1.0);
    rdinfo.setFutureVacancyRate(0.0963);
    rdinfo.setResidentialLUId(residentialLowLU.getId());

    EmploymentDemandInfo edinfoARetailTrade = new EmploymentDemandInfo();

    edinfoARetailTrade.setSectorLabel(sectorRetailTrade.getLabel());
    edinfoARetailTrade.setCurrentDensity(9.85);
    edinfoARetailTrade.setFutureDensity(9.85);
    edinfoARetailTrade.setInfillRate(0.0);
    Set<ProjectedData> empProysA = new HashSet<ProjectedData>();
    edinfoARetailTrade.setProjectedDatas(empProysA);
    edinfoARetailTrade.setDemandScenario(testDemandScenario);

    Set<DemandInfo> empDemandInfosRR = new HashSet<DemandInfo>();
    regionalRetailLU.setDemandInfos(empDemandInfosRR);
    regionalRetailLU.addDemandInfo(edinfoARetailTrade);

    EmploymentData emProjRR = new EmploymentData();
    emProjRR.setEmploymentInfo(edinfoARetailTrade);
    emProjRR.setEmployees(5771);
    emProjRR.setProjectionLabel(projection0.getLabel());
    EmploymentData emProjRR1 = new EmploymentData();
    emProjRR1.setEmploymentInfo(edinfoARetailTrade);
    emProjRR1.setEmployees(6558);
    emProjRR1.setProjectionLabel(projection1.getLabel());
    EmploymentData emProjRR2 = new EmploymentData();
    emProjRR2.setEmploymentInfo(edinfoARetailTrade);
    emProjRR2.setEmployees(7182);
    emProjRR2.setProjectionLabel(projection2.getLabel());

    edinfoARetailTrade.addProjectedData(emProjRR);
    edinfoARetailTrade.addProjectedData(emProjRR1);
    edinfoARetailTrade.addProjectedData(emProjRR2);

    testDemandScenario.getDemandInfos().add(edinfoARetailTrade);
    testDemandScenario.getDemandInfos().add(rdinfo);
    DemandScenario createDemandScenario = demandScenarioService
        .createDemandScenario(testDemandScenario, WifKeys.TEST_PROJECT_ID);
    demandScenarioId = createDemandScenario.getId();
  }

  /**
   * Gets the demand scenario test.
   * 
   * @return the demand scenario test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "service", "demandScenario" }, dependsOnMethods = { "createDemandScenarioTest" })
  public void getDemandScenarioTest() throws Exception {
    LOGGER.debug("getDemandScenarioTest: ");
    DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(demandScenarioId);
    Assert.assertNotNull(demandScenario);
    Assert.assertNotNull(demandScenario.getProjectId());
    Assert.assertEquals(demandScenario.getProjectId(), WifKeys.TEST_PROJECT_ID);
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertTrue(project.getDemandScenariosMap().containsKey(
        demandScenarioId));
  }

  /**
   * Gets the outcome test.
   * 
   * @return the outcome test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getDemandScenarioTest" }, groups = {
      "demand", "service", "demandScenario" })
  public void getOutcomeTest() throws Exception {
    outcome = demandScenarioService.getOutcome(demandScenarioId);
    Assert.assertNotNull(outcome);
    LOGGER.debug("outcome size" + outcome.size());
    Assert.assertEquals(outcome.size(), 6);
  }

  /**
   * Update demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getDemandScenarioTest" }, groups = {
      "demand", "service", "demandScenario" })
  public void updateDemandScenarioTest() throws Exception {
    DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(demandScenarioId);
    String demandScenarioLabel = demandScenario.getLabel();
    LOGGER.debug("update demandScenario test, demandScenario label: "
        + demandScenario.getLabel());
    demandScenario.setLabel("new" + demandScenarioLabel);
    demandScenarioService.updateDemandScenario(demandScenario,
        WifKeys.TEST_PROJECT_ID);
    DemandScenario demandScenario2 = demandScenarioService
        .getDemandScenario(demandScenarioId);
    Assert
        .assertEquals(demandScenario2.getLabel(), "new" + demandScenarioLabel);
    demandScenario.setLabel(demandScenarioLabel);
    demandScenarioService.updateDemandScenario(demandScenario,
        WifKeys.TEST_PROJECT_ID);
  }

  /**
   * Delete demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "updateDemandScenarioTest" }, groups = {
      "demand", "service", "demandScenario" })
  public void deleteDemandScenarioTest() throws Exception {
    demandScenarioService.deleteDemandScenario(demandScenarioId,
        WifKeys.TEST_PROJECT_ID);
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertFalse(project.getDemandScenariosMap().containsKey(
        demandScenarioId));
  }
}
