/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.executors;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.executors.svc.AsyncAllocationService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * Allocation asynchronous test.
 * 
 * @author marcosnr
 * 
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AsyncAllocationDBOutcomeIT extends
    AbstractTestNGSpringContextTests {

  @Resource
  private AsyncAllocationService asyncAllocationService;

  private Future<String> outcome;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncAllocationDBOutcomeIT.class);

  @Test(enabled = false, groups = { "allocation", "integration" })
  public void getOutcomeTest() throws Exception {
    LOGGER.debug("getAllocationScenarioTest: ");

    outcome = asyncAllocationService
        .doAllocationAnalysisAsync(WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    LOGGER.debug("Finished");
  }

  @Test(enabled = false, alwaysRun = true, dependsOnMethods = { "getOutcomeTest" })
  public void getFutureTest() throws Exception {
    LOGGER.debug("getOutcomeTest: ");
    while (!outcome.isDone()) {
      LOGGER.debug("Waiting for allocation to complete...");
      Thread.sleep(2000);
    }
    LOGGER.debug("Terminated with: " + outcome.get());
    Assert.assertTrue(false, outcome.get());
  }
}
