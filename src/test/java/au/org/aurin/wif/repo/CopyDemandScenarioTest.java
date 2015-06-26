/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.demand.DemandAnalyzer;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;

/**
 * 
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CopyDemandScenarioTest extends AbstractTestNGSpringContextTests {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CopyDemandScenarioTest.class);

  /** The demand analyzer. */
  @Autowired
  private DemandAnalyzer demandAnalyzer;

  @Autowired
  private WifProjectDao wifProjectDao;

  @Autowired
  private DemandScenarioDao demandScenarioDao;

  @Autowired
  private DemandScenarioService demandScenarioService;

  @Test(enabled = false, groups = { "demand" })
  public void CopyDemandScenarioTest() throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.debug("CopyDemandScenarioTest");
    WifProject wifProject = wifProjectDao
        .findProjectById(WifKeys.TEST_PROJECT_ID);
    LOGGER.debug(wifProject.getLabel());
    DemandScenario dsHighGrowth = wifProject
        .getDemandScenarioByLabel("High Growth");
    DemandScenario dsMediumGrowth = new DemandScenario(dsHighGrowth);
    dsMediumGrowth.setFeatureFieldName("Medium Growth");
    dsMediumGrowth.setLabel("Medium Growth");
    LOGGER.debug("Medium Growth Id " + dsMediumGrowth.getId());
    Assert.assertEquals(dsMediumGrowth.getDemandInfos().size(), dsHighGrowth
        .getDemandInfos().size());
    dsMediumGrowth.setWifProject(wifProject);
    wifProject.getDemandScenarios().add(dsMediumGrowth);
    DemandScenario demandScenario = demandScenarioDao
        .persistDemandScenario(dsMediumGrowth);
    LOGGER.debug("Medium Growth Id " + demandScenario.getId());
    Assert.assertNotNull(demandScenario.getId());
    // FIXME Having an unexpected border effect of deleting ALL scenarios.
    // HIBERNATE SUCKS BALLS.
    // demandScenarioDao.deleteDemandScenario(demandScenario);
  }
}
