/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.executors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.allocation.AllocationAnalyzer;
import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class AllocationDBOutcomeTest.
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ManualAllocationDBOutcomeIT extends
    AbstractTestNGSpringContextTests {

  /** The allocation analyzer. */
  @Autowired
  private AllocationAnalyzer allocationAnalyzer;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The demand scenario service. */
  @Resource
  private DemandOutcomeService manualdemandScenarioService;

  @Autowired
  private GeodataFinder geodataFinder;
  @Autowired
  private GeodataFilterer geodataFilterer;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  @Resource
  private ProjectService projectService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ManualAllocationDBOutcomeIT.class);

  /**
   * Gets the outcome test. Make sure the following: gid=17768 --would be one to
   * the lower right, area 40.05, GROWTH_1 = 1 and should be 283 Conservation
   * 
   * @return the outcome test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "allocation", "integration" })
  public void getOutcomeTest() throws Exception {
    LOGGER.debug("getAllocationScenarioIT: ");

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(WifKeys.TEST_ALLOCATION_SCENARIO_ID);

    LOGGER.debug(
        "doAllocationAnalysisAsync processing allocation analysis  for ={}",
        allocationScenario.getLabel());
    final String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);
    allocationScenario.setSuitabilityScenario(suitabilityScenario);

    final String demandScenarioId = allocationScenario
        .getManualdemandScenarioId();
    final DemandOutcome demandScenario = manualdemandScenarioService
        .getDemandOutcome(demandScenarioId);

    allocationScenario.setManualdemandScenario(demandScenario);
    allocationScenario.setWifProject(demandScenario.getWifProject());
    allocationAnalyzer.doAllocationAnalysis(allocationScenario);

  }
}
