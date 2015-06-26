package au.org.aurin.wif.restclient.suitability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.restclient.AllocationLUServiceClient;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class BasicWorkflowIntegrationTest.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class SetupSuitabilityRestIT extends AbstractTestNGSpringContextTests {

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

  /** The factor service client. */
  @Autowired
  private FactorServiceClient factorServiceClient;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The suitability lu id. */
  private String suitabilityLUId;

  private String factorId;

  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SetupSuitabilityRestIT.class);

  /**
   * Test create project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "workflow" })
  public void setupSuitabilityRestITTest() throws Exception {
    LOGGER.debug("SetupSuitabilityRestIT");

    final WifProject wifProject = new WifProject();
    wifProject.setName("SetupSuitabilityRestIT");
    wifProject.setOriginalUnits("m.k.s.");
    wifProject.setAnalysisOption("suitability");
    LOGGER.debug("Using union datastore "
        + integrationTestConfig.getUnionDemoDatastore());

    wifProject
        .setUazDataStoreURI(integrationTestConfig.getUnionDemoDatastore());
    SslUtil.trustSelfSignedSSL();
    projectId = projectServiceClient.createProject(roleId, wifProject);
    Assert.assertNotNull(projectId);
    LOGGER.debug("project Id " + projectId);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for setup to complete...");
      Thread.sleep(5000);
      resp = projectServiceClient.getStatus(roleId, projectId);
      LOGGER.debug("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Setup finished...");
  }

  /**
   * Test query for union attributes.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "workflow" }, dependsOnMethods = "setupSuitabilityRestITTest")
  public void testQueryForUnionAttributes() throws Exception {
    LOGGER.debug("testQueryForUnionAttributes project Id " + projectId);

    final List<String> unionAttributes = projectServiceClient.getUAZAttributes(
        roleId, projectId);
    for (final String attribute : unionAttributes) {
      LOGGER.debug(attribute);
    }
    Assert.assertNotEquals(unionAttributes.size(), 48);
    Assert.assertTrue(unionAttributes.contains("LAND_USE"));
  }

  /**
   * Test attribute values.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "workflow" }, dependsOnMethods = "testQueryForUnionAttributes")
  public void testAttributeValues() throws Exception {
    LOGGER.debug("testAttributeValues project Id " + projectId);

    final List<String> entries = Arrays.asList("Undeveloped", "Agriculture",
        "Low Density Res.");
    final List<String> distinctEntriesForUAZAttribute = projectServiceClient
        .getDistinctEntriesForUAZAttribute(roleId, projectId, "LAND_USE");
    for (final String attribute : distinctEntriesForUAZAttribute) {
      LOGGER.debug(attribute);
    }
    Assert.assertTrue(distinctEntriesForUAZAttribute.containsAll(entries));
  }

  @Test(enabled = true, groups = { "restclienttest", "workflow" }, dependsOnMethods = "testAttributeValues")
  public void testAddAllocationLU() throws Exception {
    LOGGER.debug("testAllocationLU project Id " + projectId);

    final AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel("Agriculture");
    allocationLU.setFeatureFieldName("41.0");
    allocationLU.setNotDevelopable(false);

    allocationLUId = allocationLUServiceClient.createAllocationLU(roleId,
        projectId, allocationLU);
    Assert.assertNotNull(allocationLUId);
    LOGGER.debug("allocationLUId Id " + allocationLUId);

    Assert.assertEquals(
        allocationLUServiceClient.getAllocationLUsForProject(roleId, projectId)
            .size(), 1);
  }

  /**
   * Test suitability lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "workflow" }, dependsOnMethods = "testAddAllocationLU")
  public void testAddSuitabilityLU() throws Exception {
    LOGGER.debug("testSuitabilityLU project Id " + projectId);

    final SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel("new suitability industrial");

    suitabilityLUId = suitabilityLUServiceClient.createSuitabilityLU(roleId,
        projectId, suitabilityLU);
    Assert.assertNotNull(suitabilityLUId);

    final WifProject project = projectServiceClient.getProject(roleId,
        projectId);
    Assert.assertEquals(project.getSuitabilityLUs().size(), 1);

    suitabilityLUServiceClient.addAssociatedLU(roleId, projectId,
        suitabilityLUId, allocationLUId);
    Assert.assertEquals(
        suitabilityLUServiceClient.getAssociatedLUs(roleId, projectId,
            suitabilityLUId).size(), 1);
  }

  /**
   * Test create suitability factors.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "workflow" }, dependsOnMethods = "testAddSuitabilityLU")
  public void testCreateSuitabilityFactors() throws Exception {
    LOGGER.debug("testCreateSuitabilityFactors project Id " + projectId);

    final List<String> entries = Arrays.asList("<6%", "6% - <12%");
    final List<String> attributes = projectServiceClient
        .getDistinctEntriesForUAZAttribute(roleId, projectId, "SLOPES");
    for (final String attribute : attributes) {
      LOGGER.debug(attribute);

    }
    Assert.assertTrue(attributes.containsAll(entries));

    final Factor factor = new Factor();
    factor.setLabel("SLOPES");
    final FactorType factorType = new FactorType();
    factorType.setLabel("<6%");
    factorType.setValue("<6%");
    factor.addFactorType(factorType);

    factorId = factorServiceClient.createFactor(roleId, projectId, factor);
    Assert.assertNotNull(factorId);
    final Factor factor2 = factorServiceClient.getFactor(roleId, projectId,
        factorId);
    Assert.assertEquals(factor2.getFactorTypes().size(), 1);
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
  // FIXME This test fails when running in maven due to race conditions
  @Test(enabled = false, groups = { "restclienttest", "rolerest" }, dependsOnMethods = { "testCreateSuitabilityFactors" })
  public void updateUAZTest() throws WifInvalidInputException, BindException,
      WifInvalidConfigException {
    final List<String> factorsList = new ArrayList<String>();
    factorsList.add("testColumn");
    LOGGER.debug("updateUAZTest: {}");

    final AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel("New Agriculture");
    allocationLU.setFeatureFieldName("41.0");
    allocationLU.setNotDevelopable(false);

    final String allocationLUId2 = allocationLUServiceClient
        .createAllocationLU(roleId, projectId, allocationLU);

    Assert.assertNotNull(allocationLUId2);
    LOGGER.debug("allocationLUId Id " + allocationLUId2);

    final SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel("new suitability industrial");
    final String suitabilityLUId2 = suitabilityLUServiceClient
        .createSuitabilityLU(roleId, projectId, suitabilityLU);

    suitabilityLUServiceClient.addAssociatedLU(roleId, projectId,
        suitabilityLUId2, allocationLUId2);

    projectServiceClient.finalizeUAZ(roleId, projectId, factorsList);
  }

  /**
   * Cannot update uaz test.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  // FIXME This test fails when running in maven due to race conditions
  @Test(enabled = false, groups = { "restclienttest", "rolerest" }, dependsOnMethods = { "getWMSTest" }, expectedExceptions = org.springframework.web.client.HttpClientErrorException.class)
  public void cannotUpdateUAZTest() throws WifInvalidInputException,
      BindException, WifInvalidConfigException {
    final List<String> factorsList = new ArrayList<String>();
    factorsList.add("testColumn");

    projectServiceClient.finalizeUAZ(roleId, projectId, factorsList);
  }

  /**
   * Cleanup.
   * 
   * @throws WifInvalidInputException
   */
  @AfterClass(enabled = true)
  public void cleanup() throws WifInvalidInputException {
    projectServiceClient.deleteProject(roleId, projectId);
  }
}
