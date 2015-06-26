package au.org.aurin.wif.restclient.demand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.validation.BindException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandSetupData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.restclient.AllocationLUServiceClient;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.restclient.suitability.SuitabilityLUServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemandSetupRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class DemandSetupRestIT extends AbstractTestNGSpringContextTests {

  /** The demand config service client. */
  @Autowired
  private DemandConfigServiceClient demandConfigServiceClient;

  /** The demand config id. */
  private String demandConfigId;

  /** The role id. */
  private final String roleId = "aurin";

  /** The project id. */
  private String projectId;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The allocation lu service client. */
  @Autowired
  private AllocationLUServiceClient allocationLUServiceClient;

  /** The suitability lu service client. */
  @Autowired
  private SuitabilityLUServiceClient suitabilityLUServiceClient;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  @Autowired
  private DemandScenarioServiceClient demandScenarioServiceClient;

  /** The residential low lu id. */
  private String residentialLowLUId;

  /** The local retail lu id. */
  private String localRetailLUId;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandSetupRestIT.class);

  /**
   * Demand setup rest it project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "workflow" })
  public void demandSetupRestITProject() throws Exception {
    LOGGER.debug("demandSetupRestITProject");

    final WifProject wifProject = new WifProject();
    wifProject.setName("demandSetupRestITProject");
    wifProject.setOriginalUnits("m.k.s.");
    wifProject.setAnalysisOption("demand");
    wifProject.setAreaLabel("UAZ_AREA");
    wifProject
        .setUazDataStoreURI(integrationTestConfig.getUnionDemoDatastore());
    SslUtil.trustSelfSignedSSL();
    LOGGER.debug("Using union datastore "
        + integrationTestConfig.getUnionDemoDatastore());

    projectId = projectServiceClient.createProject(roleId, wifProject);
    Assert.assertNotNull(projectId);
    LOGGER.debug("project Id " + projectId);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.trace("Waiting for setup to complete...");
      Thread.sleep(5000);
      resp = projectServiceClient.getStatus(roleId, projectId);
      LOGGER.trace("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Setup finished...");
  }

  /**
   * Update uaz test.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand" }, dependsOnMethods = { "demandSetupRestITProject" })
  public void updateUAZTest() throws WifInvalidInputException, BindException,
      WifInvalidConfigException {
    LOGGER.debug("updateUAZTest");
    final List<String> factorsList = new ArrayList<String>();
    factorsList.add("testColumn");

    final AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel("New Agriculture");
    allocationLU.setFeatureFieldName("41.0");
    allocationLU.setNotDevelopable(false);

    final String allocationLUId2 = allocationLUServiceClient
        .createAllocationLU(roleId, projectId, allocationLU);

    Assert.assertNotNull(allocationLUId2);
    LOGGER.debug("allocationLUId Id " + allocationLUId2);

    final SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel("new suitability office");
    final String suitabilityLUId2 = suitabilityLUServiceClient
        .createSuitabilityLU(roleId, projectId, suitabilityLU);

    suitabilityLUServiceClient.addAssociatedLU(roleId, projectId,
        suitabilityLUId2, allocationLUId2);

    projectServiceClient.finalizeUAZ(roleId, projectId, factorsList);
  }

  /**
   * Creates the demand config incomplete test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand" }, expectedExceptions = org.springframework.web.client.HttpClientErrorException.class, dependsOnMethods = "updateUAZTest")
  public void createDemandConfigIncompleteTest() throws Exception {
    LOGGER.debug("createDemandConfigIncompleteTest");
    // Adding ALUs
    AllocationLU residentialLowLU = new AllocationLU();
    residentialLowLU.setLabel("Low Density Res.");
    residentialLowLU.setNewPreservation(false);
    residentialLowLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    residentialLowLU.setFeatureFieldName("1.0");
    residentialLowLU.setTotalArea(5520.86);

    AllocationLU localRetailLU = new AllocationLU();
    localRetailLU.setLabel("Local Retail");
    localRetailLU.setNewPreservation(false);
    localRetailLU.setLandUseFunction(LandUseFunction.LBCS_2XXX);
    localRetailLU.setFeatureFieldName("11.0");
    localRetailLU.setTotalArea(255.03);

    residentialLowLUId = allocationLUServiceClient.createAllocationLU(roleId,
        projectId, residentialLowLU);
    Assert.assertNotNull(residentialLowLUId);
    localRetailLUId = allocationLUServiceClient.createAllocationLU(roleId,
        projectId, localRetailLU);
    Assert.assertNotNull(localRetailLUId);
    residentialLowLU = allocationLUServiceClient.getAllocationLU(roleId,
        projectId, residentialLowLUId);
    localRetailLU = allocationLUServiceClient.getAllocationLU(roleId,
        projectId, localRetailLUId);
    final DemandConfig demandConfig = DemonstrationDemandSetupData
        .createUserDefineDemandConfig(localRetailLU);

    demandConfigId = demandConfigServiceClient.createDemandConfig(roleId,
        projectId, demandConfig);
  }

  /**
   * Gets the demand config.
   * 
   * @return the demand config
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "demand" }, dependsOnMethods = { "createDemandConfigIncompleteTest" })
  public void getDemandConfig() throws Exception {
    LOGGER.debug("getDemandConfig");
    final DemandConfig demandConfig = demandConfigServiceClient
        .getDemandConfig(roleId, WifKeys.TEST_PROJECT_ID);

    Assert.assertNotNull(demandConfig.getId());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getResidentialCurrentData().iterator().next().getResidentialLUId());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getEmploymentCurrentDatas().iterator().next().getSectorLabel());
    Assert.assertNotNull(demandConfig.getSectors().iterator().next()
        .getAssociatedALUsMap().size());
    Assert.assertNotNull(demandConfig.getEmploymentPastTrendInfos().iterator()
        .next().getEmploymentEntries().iterator().next().getSectorLabel());

  }

  @Test(enabled = false, groups = { "restclienttest", "demand" }, dependsOnMethods = { "createDemandConfigIncompleteTest" })
  public void getProjectAfterDemandSetup() throws Exception {
    LOGGER.debug("getProjectAfterDemandSetup");
    final WifProject project = projectServiceClient.getProjectConfiguration(
        roleId, WifKeys.TEST_PROJECT_ID);

    Assert.assertNotNull(project.getId());
    final Map<String, String> scenariosMap = project.getDemandScenariosMap();

    Assert.assertNotNull(scenariosMap);
    // TODO When a new demandConfig creation is possible, it should be done with
    // WifKeys.DEFAULT_DEMAND_SCENARIO_NAME
    Assert.assertTrue(scenariosMap.containsValue("High Growth"));
    final Set<Entry<String, String>> entrySet = scenariosMap.entrySet();
    for (final Entry<String, String> entry : entrySet) {
      if (entry.getValue().equals("High Growth")) {
        final String key = entry.getKey();

        final DemandScenario demandScenario = demandScenarioServiceClient
            .getDemandScenario(roleId, WifKeys.TEST_PROJECT_ID, key);
        Assert.assertNotNull(demandScenario);
        Assert.assertNotNull(demandScenario.getDemandInfos());
        break;
      }
    }
  }

  /**
   * Cleanup.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @AfterClass(enabled = true)
  public void cleanup() throws WifInvalidInputException {
    LOGGER.debug("cleanup");
    projectServiceClient.deleteProject(roleId, projectId);
  }
}
