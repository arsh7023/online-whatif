package au.org.aurin.wif.restclient.suitability;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.restclient.AllocationLUServiceClient;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class SuitabilityLURestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class SuitabilityLURestIT extends AbstractTestNGSpringContextTests {

  /** The suitability lu service client. */
  @Autowired
  private SuitabilityLUServiceClient suitabilityLUServiceClient;

  /** The allocation lu service client. */
  @Autowired
  private AllocationLUServiceClient allocationLUServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /** The suitability lu id. */
  private String suitabilityLUId;

  /** The role id. */
  private String roleId = "aurin";

  /** The suitability lu label. */
  private final String suitabilityLULabel = "suitabilityLUTest"
      + System.currentTimeMillis();

  /** The wif project id. */

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityLURestIT.class);
  String wifProjectId = WifKeys.TEST_PROJECT_ID;

  private int listSize;

  /**
   * Test create suitability lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" })
  public void testCreateSuitabilityLU() throws Exception {
    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel(suitabilityLULabel);

    suitabilityLUId = suitabilityLUServiceClient.createSuitabilityLU(roleId,
        wifProjectId, suitabilityLU);
    Assert.assertNotNull(suitabilityLUId);
  }

  /**
   * Test get suitability lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "testCreateSuitabilityLU" })
  public void testGetSuitabilityLU() throws Exception {
    SuitabilityLU suitabilityLU = suitabilityLUServiceClient.getSuitabilityLU(
        roleId, wifProjectId, suitabilityLUId);
    Assert.assertEquals(suitabilityLU.getLabel(), suitabilityLULabel);
  }

  /**
   * Test get suitability l us for project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = "testGetSuitabilityLU")
  public void testGetSuitabilityLUsForProject() throws Exception {
    List<SuitabilityLU> list = suitabilityLUServiceClient
        .getSuitabilityLUsForProject(roleId, wifProjectId);
    listSize = list.size();
    LOGGER.debug("Size = " + listSize);
    Assert.assertNotEquals(listSize, 6);
  }

  /**
   * Test update suitability lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "testGetSuitabilityLUsForProject" })
  public void testUpdateSuitabilityLU() throws Exception {
    SuitabilityLU suitabilityLU = suitabilityLUServiceClient.getSuitabilityLU(
        roleId, wifProjectId, suitabilityLUId);
    suitabilityLU.setLabel("suitabilityLU REST test");
    suitabilityLUServiceClient.updateSuitabilityLU(roleId, wifProjectId,
        suitabilityLUId, suitabilityLU);

    SuitabilityLU tmp = suitabilityLUServiceClient.getSuitabilityLU(roleId,
        wifProjectId, suitabilityLUId);
    Assert.assertEquals(tmp.getLabel(), "suitabilityLU REST test");
  }

  /**
   * Test delete suitability lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "suitability" }, dependsOnMethods = { "testUpdateSuitabilityLU" }, expectedExceptions = HttpClientErrorException.class)
  public void testDeleteSuitabilityLU() throws Exception {
    suitabilityLUServiceClient.deleteSuitabilityLU(roleId, wifProjectId,
        suitabilityLUId);
    List<SuitabilityLU> list = suitabilityLUServiceClient
        .getSuitabilityLUsForProject(roleId, wifProjectId);
    int listSize2 = list.size();
    LOGGER.debug("New size = " + listSize2);
    Assert.assertNotEquals(listSize2, listSize);
    Assert.assertNull(suitabilityLUServiceClient.getSuitabilityLU(roleId,
        wifProjectId, suitabilityLUId));
  }
}
