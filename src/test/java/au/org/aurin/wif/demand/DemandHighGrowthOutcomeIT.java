/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.demand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;

/**
 * The Class DemandHighGrowthOutcomeIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandHighGrowthOutcomeIT extends AbstractTestNGSpringContextTests {

  /** The demand scenario service. */
  @Autowired
  private DemandScenarioService demandScenarioService;

  /** The area requirement dao. */
  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /** The outcome. */
  private List<AreaRequirement> outcome;

  /** The demand scenario id. */
  private String demandScenarioId;

  /** The demand scenario. */
  private DemandScenario demandScenario;

  /** The reqs label. */
  private Set<String> reqsLabel = new HashSet<String>();

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandHighGrowthOutcomeIT.class);

  /**
   * Do demand analysis test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "integration" })
  public void doDemandAnalysisTest() throws Exception {
    LOGGER.debug("DemandHighGrowthIT");

    outcome = demandScenarioService.getOutcome(WifKeys.TEST_DEMAND_SCENARIO_ID);
    Assert.assertNotNull(outcome);
    LOGGER.debug("outcome size " + outcome.size());
    Assert.assertEquals(outcome.size(), 16);
  }

  /**
   * Save outcome test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "integration" }, dependsOnMethods = { "doDemandAnalysisTest" })
  public void saveOutcomeTest() throws Exception {
    LOGGER.debug("saveOutcomeTest");
    for (AreaRequirement areaRequirement : outcome) {
      areaRequirement.setDemandScenarioId(areaRequirement.getDemandScenario()
          .getId());
      areaRequirement.setProjectionLabel(areaRequirement.getProjection()
          .getLabel());
      areaRequirement.setAllocationLUId(areaRequirement.getAllocationLU()
          .getId());
      if (areaRequirement.getId() == null) {
        areaRequirement.setDemandScenarioId(demandScenarioId);
        AreaRequirement requirement = areaRequirementDao
            .persistAreaRequirement(areaRequirement);
        LOGGER.debug("Created area requirement {} for {} ",
            requirement.getId(), areaRequirement.getAllocationLU().getLabel());
        Assert.assertNotNull(requirement.getId());
        reqsLabel.add(requirement.getId());
      } else {
        areaRequirementDao.updateAreaRequirement(areaRequirement);
      }
    }
  }

  /**
   * Delete outcome test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "saveOutcomeTest" }, groups = {
      "demand", "service" })
  public void deleteOutcomeTest() throws Exception {

    for (String ID : reqsLabel) {
      AreaRequirement areaRequirement = areaRequirementDao
          .findAreaRequirementById(ID);
      areaRequirementDao.deleteAreaRequirement(areaRequirement);
    }
  }
}
