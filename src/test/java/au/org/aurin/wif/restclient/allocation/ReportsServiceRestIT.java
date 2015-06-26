package au.org.aurin.wif.restclient.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.restclient.ProjectServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationScenarioServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class ReportsServiceRestIT extends AbstractTestNGSpringContextTests {

  /** The allocation scenario service client. */
  @Autowired
  private AllocationScenarioServiceClient allocationScenarioServiceClient;

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The allocation scenario id. */
  private String allocationScenarioId;

  /** The role id. */
  private String roleId = "aurin";

  /** The wif project id. */
  String wifProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ReportsServiceRestIT.class);

  /**
   * Gets the allocation scenario report.
   * 
   * @return the allocation scenario report
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "allocation" })
  public void getAllocationScenarioReport() throws Exception {
    LOGGER.debug("getAllocationScenarioReport");
    AllocationAnalysisReport allocationScenarioReport = allocationScenarioServiceClient
        .getAllocationScenarioReport(roleId, WifKeys.TEST_PROJECT_ID,
            WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    Assert.assertEquals(allocationScenarioReport.getScenarioLabel(),
        "AllocationScenario");
  }

  /**
   * Delete allocation scenario.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, alwaysRun = true, groups = { "restclienttest",
      "allocation" }, dependsOnMethods = { "updateAllocationScenario",
      "getAllocationScenarioReport" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteAllocationScenario() throws Exception {
    allocationScenarioServiceClient.deleteAllocationScenario(roleId,
        wifProjectId, allocationScenarioId);
    WifProject project2 = projectServiceClient.getProjectConfiguration(roleId,
        wifProjectId);
    Assert.assertNotNull(project2.getAllocationScenariosMap());
    Assert.assertFalse(project2.getAllocationScenariosMap().containsKey(
        allocationScenarioId));
    Assert.assertNull(allocationScenarioServiceClient.getAllocationScenario(
        roleId, wifProjectId, allocationScenarioId));
  }

}
