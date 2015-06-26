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
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class AllocationDBOutcomeTest.
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AllocationDBOutcomeIT extends AbstractTestNGSpringContextTests {

  /** The allocation analyzer. */
  @Autowired
  private AllocationAnalyzer allocationAnalyzer;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  @Autowired
  private GeodataFinder geodataFinder;
  @Autowired
  private GeodataFilterer geodataFilterer;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationDBOutcomeIT.class);

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

    AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(WifKeys.TEST_ALLOCATION_SCENARIO_ID);

    LOGGER.debug(
        "doAllocationAnalysisAsync processing allocation analysis  for ={}",
        allocationScenario.getLabel());
    String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);
    allocationScenario.setSuitabilityScenario(suitabilityScenario);

    String demandScenarioId = allocationScenario.getDemandScenarioId();
    DemandScenario demandScenario = demandScenarioService
        .getDemandScenario(demandScenarioId);

    allocationScenario.setDemandScenario(demandScenario);
    allocationScenario.setWifProject(demandScenario.getWifProject());
    allocationAnalyzer.doAllocationAnalysis(allocationScenario);

    // TODO Because of the test not running on the whole study area (would take
    // many minutes to finish test), results vary
    // Checking analysis
    // Map<String, Object> wifParameters = new HashMap<String, Object>();
    // wifParameters.put(WifKeys.POLYGON, WifKeys.POLYGON_TEST);
    // wifParameters.put(WifKeys.CRS_ORG, WifKeys.CRS_TEST);
    // wifParameters.put(WifKeys.CRS_DEST, WifKeys.CRS_TEST);
    // Filter filter = geodataFilterer.getFilterFromParameters(wifParameters);
    // SimpleFeatureCollection uazCollection = geodataFinder
    // .getFeatureCollectionfromDB("uaz_demonstration", filter);
    // SimpleFeatureIterator it = uazCollection.features();
    // Double score = 0.0;
    // try {
    // while (it.hasNext()) {
    // SimpleFeature uazFeature = it.next();
    // if (uazFeature.getID().equals("uaz_demonstration.659")) {
    //
    // score = (Double) uazFeature.getAttribute("ALU_3");
    // }
    // }
    // } finally {
    // it.close();
    // }
    // LOGGER.debug("Allocation {}", score);
    // Assert.assertEquals(score, new Double(213.0));
  }
}
