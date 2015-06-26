package au.org.aurin.wif.restclient;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationLUServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class AllocationLUServiceRestIT extends AbstractTestNGSpringContextTests {

  /** The allocation lu service client. */
  @Autowired
  private AllocationLUServiceClient allocationLUServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The allocation lu id. */
  private String allocationLUId;

  /** The role id. */
  private final String roleId = WifKeys.TEST_ROLE_ID;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /** The allocation lu label. */
  private final String allocationLULabel = "allocationLUTest"
      + System.currentTimeMillis();

  /** The wif project id. */
  String wifProjectId = WifKeys.TEST_PROJECT_ID;

  /**
   * Creates the allocation lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest", "projectrest" })
  public void createAllocationLU() throws Exception {
    SslUtil.trustSelfSignedSSL();
    final AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel(allocationLULabel);
    allocationLU.setFeatureFieldName("name");

    allocationLUId = allocationLUServiceClient.createAllocationLU(roleId,
        wifProjectId, allocationLU);
    Assert.assertNotNull(allocationLUId);
  }

  /**
   * Gets the allocation lu.
   * 
   * @return the allocation lu
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "createAllocationLU" })
  public void getAllocationLU() throws Exception {
    final AllocationLU allocationLU = allocationLUServiceClient
        .getAllocationLU(roleId, wifProjectId, allocationLUId);
    Assert.assertEquals(allocationLU.getLabel(), allocationLULabel);
  }

  /**
   * Update allocation lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "getAllocationLU" })
  public void updateAllocationLU() throws Exception {
    final AllocationLU allocationLU = allocationLUServiceClient
        .getAllocationLU(roleId, wifProjectId, allocationLUId);
    allocationLU.setLabel("allocationLU REST test");
    allocationLUServiceClient.updateAllocationLU(roleId, wifProjectId,
        allocationLUId, allocationLU);

    final AllocationLU tmp = allocationLUServiceClient.getAllocationLU(roleId,
        wifProjectId, allocationLUId);
    Assert.assertEquals(tmp.getLabel(), "allocationLU REST test");
  }

  /**
   * Gets the allocation l us for project.
   * 
   * @return the allocation l us for project
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = "updateAllocationLU")
  public void getAllocationLUsForProject() throws Exception {
    final List<AllocationLU> list = allocationLUServiceClient
        .getAllocationLUsForProject(roleId, wifProjectId);
    Assert.assertNotEquals(list.size(), 15);
  }

  /**
   * Delete allocation lu.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "getAllocationLUsForProject" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteAllocationLU() throws Exception {
    allocationLUServiceClient.deleteAllocationLU(roleId, wifProjectId,
        allocationLUId);
    Assert.assertNull(allocationLUServiceClient.getAllocationLU(roleId,
        wifProjectId, allocationLUId));
  }

}
