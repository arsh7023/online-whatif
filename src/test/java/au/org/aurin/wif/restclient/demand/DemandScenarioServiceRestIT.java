package au.org.aurin.wif.restclient.demand;

import java.util.HashSet;
import java.util.List;
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

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.SslUtil;
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
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemandScenarioServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class DemandScenarioServiceRestIT extends
    AbstractTestNGSpringContextTests {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioServiceRestIT.class);

  /** The demand scenario service client. */
  @Autowired
  private DemandScenarioServiceClient demandScenarioServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The demand scenario id. */
  private String demandScenarioId;

  /** The role id. */
  private final String roleId = "aurin";

  /** The demand scenario label. */
  private String demandScenarioLabel = "demandScenarioTest474533%##$%%18";

  /** The wif project id. */
  String wifProjectId;

  /** The demand config service client. */
  @Autowired
  private DemandConfigServiceClient demandConfigServiceClient;

  /**
   * Setup.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @BeforeClass(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" })
  public void setup() throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.debug("setup: ");
    SslUtil.trustSelfSignedSSL();
    LOGGER.debug("setup: ");
    wifProjectId = WifKeys.TEST_PROJECT_ID;
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertEquals(project.getLabel(), "Demonstration");
    Assert.assertNotNull(project.getCreationDate());
    Assert.assertNotNull(project.getId());
  }

  /**
   * Creates the demand scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" })
  public void createDemandScenario() throws Exception {
    LOGGER.debug("createDemandScenario: ");
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    // Creating dummy scenario
    final AllocationLU residentialLowLU = project
        .getExistingLandUseByLabel("Low Density Res.");
    final AllocationLU regionalRetailLU = project
        .getExistingLandUseByLabel("Regional Retail");

    final DemandConfig demandConfig = demandConfigServiceClient
        .getDemandConfig(roleId, WifKeys.TEST_PROJECT_ID);

    Assert.assertNotNull(demandConfig.getId());
    demandScenarioLabel = "DemandScenarioRestId" + System.currentTimeMillis();
    final EmploymentSector sectorRetailTrade = demandConfig
        .getSectorByLabel("Retail Trade");

    final Projection projection0 = demandConfig.getProjectionByLabel("2005");
    final Projection projection1 = demandConfig.getProjectionByLabel("2010");
    final Projection projection2 = demandConfig.getProjectionByLabel("2015");
    final DemographicTrend highGrowthDemographicTrend = demandConfig
        .getTrendByLabel("High Growth Trend");

    final DemandScenario testDemandScenario = new DemandScenario();
    testDemandScenario.setFeatureFieldName("High Growth");
    testDemandScenario.setLabel("High Growth");
    testDemandScenario.setDemographicTrendLabel("High Growth Trend");
    testDemandScenario.setProjectId(project.getId());
    testDemandScenario.setDemographicTrendLabel(highGrowthDemographicTrend
        .getLabel());

    // low residential information for demand
    final ResidentialDemandInfo rdinfo = new ResidentialDemandInfo();
    rdinfo.setInfillRate(0.0);
    rdinfo.setFutureBreakdownByHType(0.60);
    rdinfo.setCurrentDensity(0.94);
    rdinfo.setFutureDensity(1.0);
    rdinfo.setFutureVacancyRate(0.0963);
    rdinfo.setResidentialLUId(residentialLowLU.getId());

    final EmploymentDemandInfo edinfoARetailTrade = new EmploymentDemandInfo();

    edinfoARetailTrade.setSectorLabel(sectorRetailTrade.getLabel());
    edinfoARetailTrade.setCurrentDensity(9.85);
    edinfoARetailTrade.setFutureDensity(9.85);
    edinfoARetailTrade.setInfillRate(0.0);
    final Set<ProjectedData> empProysA = new HashSet<ProjectedData>();
    edinfoARetailTrade.setProjectedDatas(empProysA);
    edinfoARetailTrade.setDemandScenario(testDemandScenario);

    final Set<DemandInfo> empDemandInfosRR = new HashSet<DemandInfo>();
    regionalRetailLU.setDemandInfos(empDemandInfosRR);
    regionalRetailLU.addDemandInfo(edinfoARetailTrade);

    final EmploymentData emProjRR = new EmploymentData();
    emProjRR.setEmploymentInfo(edinfoARetailTrade);
    emProjRR.setEmployees(5771);
    emProjRR.setProjectionLabel(projection0.getLabel());
    final EmploymentData emProjRR1 = new EmploymentData();
    emProjRR1.setEmploymentInfo(edinfoARetailTrade);
    emProjRR1.setEmployees(6558);
    emProjRR1.setProjectionLabel(projection1.getLabel());
    final EmploymentData emProjRR2 = new EmploymentData();
    emProjRR2.setEmploymentInfo(edinfoARetailTrade);
    emProjRR2.setEmployees(7182);
    emProjRR2.setProjectionLabel(projection2.getLabel());

    edinfoARetailTrade.addProjectedData(emProjRR);
    edinfoARetailTrade.addProjectedData(emProjRR1);
    edinfoARetailTrade.addProjectedData(emProjRR2);

    testDemandScenario.getDemandInfos().add(edinfoARetailTrade);
    testDemandScenario.getDemandInfos().add(rdinfo);
    testDemandScenario.setLabel(demandScenarioLabel);

    demandScenarioId = demandScenarioServiceClient.createDemandScenario(roleId,
        wifProjectId, testDemandScenario);
    Assert.assertNotNull(demandScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getDemandScenariosMap());
    Assert.assertTrue(project2.getDemandScenariosMap().containsKey(
        demandScenarioId));
  }

  /**
   * Gets the demand scenario.
   * 
   * @return the demand scenario
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" }, dependsOnMethods = { "createDemandScenario" })
  public void getDemandScenario() throws Exception {
    final DemandScenario demandScenario = demandScenarioServiceClient
        .getDemandScenario(roleId, wifProjectId, demandScenarioId);
    Assert.assertEquals(demandScenario.getLabel(), demandScenarioLabel);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertTrue(project2.getDemandScenariosMap().containsKey(
        demandScenarioId));
  }

  /**
   * Gets the demand scenarios for project.
   * 
   * @return the demand scenarios for project
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" }, dependsOnMethods = "getDemandScenario")
  public void getDemandScenariosForProject() throws Exception {
    final List<DemandScenario> list = demandScenarioServiceClient
        .getDemandScenariosForProject(roleId, wifProjectId);
    LOGGER.debug("DemandScenarios = " + list.size());
    Assert.assertNotEquals(list.size(), 1);
  }

  /**
   * Update demand scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" }, dependsOnMethods = { "getDemandScenariosForProject" })
  public void updateDemandScenario() throws Exception {
    final DemandScenario demandScenario = demandScenarioServiceClient
        .getDemandScenario(roleId, wifProjectId, demandScenarioId);
    demandScenario.setLabel("demandScenario REST test");
    demandScenarioServiceClient.updateDemandScenario(roleId, wifProjectId,
        demandScenarioId, demandScenario);

    final DemandScenario tmp = demandScenarioServiceClient.getDemandScenario(
        roleId, wifProjectId, demandScenarioId);
    Assert.assertEquals(tmp.getLabel(), "demandScenario REST test");
  }

  /**
   * Gets the outcome.
   * 
   * @return the outcome
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" }, dependsOnMethods = "updateDemandScenario")
  public void getOutcome() throws Exception {

    final List<AreaRequirement> outcome = demandScenarioServiceClient
        .getDemandScenarioOutcome(roleId, wifProjectId, demandScenarioId);
    Assert.assertNotNull(outcome);
    LOGGER.debug("outcome size" + outcome.size());
    Assert.assertEquals(outcome.size(), 6);
  }

  /**
   * Gets the demand scenario report.
   * 
   * @return the demand scenario report
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand",
      "demandScenario" }, dependsOnMethods = { "createDemandScenario" })
  public void getDemandScenarioReport() throws Exception {
    final DemandAnalysisReport demandAnalysisReport = demandScenarioServiceClient
        .getDemandScenarioReport(roleId, WifKeys.TEST_PROJECT_ID,
            WifKeys.TEST_DEMAND_SCENARIO_ID);
    Assert.assertEquals(demandAnalysisReport.getReportType(), "DemandScenario");
  }

  /**
   * Delete demand scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, alwaysRun = true, groups = { "restclienttest",
      "demand", "demandScenario" }, dependsOnMethods = {
      "updateDemandScenario", "getOutcome" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteDemandScenario() throws Exception {
    demandScenarioServiceClient.deleteDemandScenario(roleId, wifProjectId,
        demandScenarioId);
    final WifProject project2 = projectServiceClient.getProjectConfiguration(
        roleId, wifProjectId);
    Assert.assertNotNull(project2.getDemandScenariosMap());
    Assert.assertFalse(project2.getDemandScenariosMap().containsKey(
        demandScenarioId));
    Assert.assertNull(demandScenarioServiceClient.getDemandScenario(roleId,
        wifProjectId, demandScenarioId));
  }
}
