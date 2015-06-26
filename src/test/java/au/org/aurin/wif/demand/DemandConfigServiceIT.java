/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.demand;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class DemandConfigServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandConfigServiceIT extends AbstractTestNGSpringContextTests {

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandConfigServiceIT.class);

  /** The demand config label. */
  private final String demandConfigLabel = "demandConfigTest"
      + System.currentTimeMillis();

  // TODO Very hard to do a complex test, the project needs a valid union-UAZ
  // process, now only performed at the REST interface level
  // @Test(enabled = false, groups = { "demand", "service" })
  // public void createDemandConfigTest() throws Exception {
  // }

  /**
   * Gets the demand config test.
   * 
   * @return the demand config test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "service" })
  public void getDemandConfigTest() throws Exception {

    DemandConfig demandConfig = demandConfigService
        .getDemandConfig(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(demandConfig);
    Assert.assertNotNull(demandConfig.getProjectId());
  }

  /**
   * Update demand config test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getDemandConfigTest" }, groups = {
      "demand", "service" })
  public void updateDemandConfigTest() throws Exception {
    DemandConfig demandConfig = demandConfigService
        .getDemandConfig(WifKeys.TEST_PROJECT_ID);
    LOGGER.debug("update demandConfig test, demandConfig label: "
        + demandConfig.getLabel());
    demandConfig.setLabel("new" + demandConfigLabel);
    demandConfigService.updateDemandConfig(demandConfig,
        WifKeys.TEST_PROJECT_ID);
    DemandConfig demandConfig2 = demandConfigService
        .getDemandConfig(WifKeys.TEST_PROJECT_ID);
    Assert.assertEquals(demandConfig2.getLabel(), "new" + demandConfigLabel);
  }
}
