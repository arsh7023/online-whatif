package au.org.aurin.wif.restclient.demand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemandConfigServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class DemandConfigServiceRestIT extends AbstractTestNGSpringContextTests {

  /** The demand config service client. */
  @Autowired
  private DemandConfigServiceClient demandConfigServiceClient;

  /** The role id. */
  private final String roleId = WifKeys.TEST_ROLE_ID;

  /** The wif project id. */
  String wifProjectId = WifKeys.TEST_PROJECT_ID;

  /**
   * Gets the demand config.
   * 
   * @return the demand config
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest" })
  public void getDemandConfig() throws Exception {
    SslUtil.trustSelfSignedSSL();
    final DemandConfig demandConfig = demandConfigServiceClient
        .getDemandConfig(roleId, wifProjectId);
    Assert.assertEquals(demandConfig.getId(), WifKeys.TEST_DEMAND_CONFIG_ID);
  }

  /**
   * Update demand config. Not supported right now, very hard logic to update a
   * complex demand config.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "restclienttest", "rolerest" }, dependsOnMethods = { "getDemandConfig" })
  public void updateDemandConfig() throws Exception {
    final DemandConfig demandConfig = demandConfigServiceClient
        .getDemandConfig(roleId, wifProjectId);
    demandConfig.setLabel("demandConfig REST test");
    demandConfigServiceClient.updateDemandConfig(roleId, wifProjectId,
        demandConfig);

    final DemandConfig tmp = demandConfigServiceClient.getDemandConfig(roleId,
        wifProjectId);
    Assert.assertEquals(tmp.getLabel(), "demandConfig REST test");
  }
}
