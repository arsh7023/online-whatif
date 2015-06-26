/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.suitability;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.executors.svc.AsyncSuitabilityService;
import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * Integration test for the whole What If implementation
 * 
 * @author marcosnr
 * 
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AsyncSuitabilityDBOutcomeTest extends
    AbstractTestNGSpringContextTests {

  @Autowired
  private SuitabilityScenarioService suitabilityScenarioService;

  @Resource
  private AsyncSuitabilityService asyncSuitabilityService;

  @Autowired
  private GeodataFinder geodataFinder;
  @Autowired
  private GeodataFilterer geodataFilterer;

  private Future<Boolean> outcome;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncSuitabilityDBOutcomeTest.class);

  @BeforeClass(enabled = false, groups = { "suitability", "integration" })
  public void loadWifProject() throws WifInvalidConfigException {
  }

  @Test(enabled = false, groups = { "suitability", "integration" })
  public void getWMSOutcomeTest() throws Exception {
    LOGGER.debug("getWMSOutcomeTest");
    String areaAnalyzed = WifKeys.POLYGON_TEST;
    // String areaAnalyzed = null;
    String crsArea = WifKeys.CRS_TEST;

    outcome = asyncSuitabilityService.doSuitabilityAnalysisWMSAsync(
        WifKeys.TEST_SUITABILITY_SCENARIO_ID, areaAnalyzed, crsArea);
    LOGGER.debug("Finished");
  }

  @Test(enabled = false, alwaysRun = true, dependsOnMethods = { "getWMSOutcomeTest" })
  public void getOutcomeTest() throws Exception {
    LOGGER.debug("getOutcomeTest: ");
    while (!outcome.isDone()) {
      LOGGER.debug("Waiting for WMS to complete...");
      Thread.sleep(2000);
    }
    LOGGER.debug("Terminated with: " + outcome.get());
    Assert.assertTrue(outcome.get());
  }
}
