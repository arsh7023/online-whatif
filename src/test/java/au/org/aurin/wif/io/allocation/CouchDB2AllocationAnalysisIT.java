/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.parsers.AllocationCouchParser;
import au.org.aurin.wif.io.parsers.DemandScenarioCouchParser;
import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class CouchDB2AllocationAnalysisIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDB2AllocationAnalysisIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The suitability parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /** The demand setup parser. */
  @Autowired
  private DemandSetupCouchParser demandSetupParser;

  /** The demand scenario parser. */
  @Autowired
  private DemandScenarioCouchParser demandScenarioParser;

  /** The allocation parser. */
  @Autowired
  private AllocationCouchParser allocationParser;

  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDB2AllocationAnalysisIT.class);

  /**
   * Parses the allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "database", "couchdb" })
  public void parseAllocationScenarioTest() throws Exception {

    AllocationScenario allocationScenario = allocationScenarioDao
        .findAllocationScenarioById(WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    WifProject project = wifProjectDao.findProjectById(allocationScenario
        .getProjectId());
    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(allocationScenario
            .getSuitabilityScenarioId());
    Assert.assertNotNull(suitabilityScenario);
    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(project
        .getDemandConfigId());
    Assert.assertNotNull(demandConfig);

    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(allocationScenario.getDemandScenarioId());
    Assert.assertNotNull(demandScenario);

    // Parsing components
    project = projectParser.parse(project);
    suitabilityScenario = suitabilityParser.parseSuitabilityScenario(
        suitabilityScenario, project);
    demandConfig = demandSetupParser.parse(demandConfig, project);
    demandScenario = demandScenarioParser.parse(demandScenario, demandConfig,
        project);
    demandScenario = demandScenarioParser.parseAreaRequirements(demandScenario);
    allocationScenario.setDemandScenario(demandScenario);
    allocationScenario = allocationParser.parse(allocationScenario, project);
    // Checking mapping

    String AllocationConfigsId = project.getAllocationConfigsId();

    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    Assert.assertNotNull(allocationScenario.getLandUseOrderMap());
    Assert.assertNotNull(allocationConfig.getAllocationColumnsMap());
    // Checking allocationLU associations
    AllocationLU resLU = project.getExistingLandUseByLabel("Low Density Res.");
    Assert.assertNotNull(resLU.getAllocationFeatureFieldName());
    Assert.assertNotEquals(resLU.getAreaRequirements().size(), 0);
  }
}
