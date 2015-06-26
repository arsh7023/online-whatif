/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.demand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class CouchDB2DemandSetupIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDB2DemandSetupIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The demand setup parser. */
  @Autowired
  private DemandSetupCouchParser demandSetupParser;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDB2DemandSetupIT.class);

  /**
   * Parses the demand config test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "database", "couchdb" })
  public void parseDemandConfigTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(WifKeys.TEST_PROJECT_ID);
    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(project
        .getDemandConfigId());

    project = projectParser.parse(project);
    demandConfig = demandSetupParser.parse(demandConfig, project);
    Assert.assertNotNull(demandConfig.getId());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getResidentialCurrentData().iterator().next().getResidentialLU());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getEmploymentCurrentDatas().iterator().next().getSector());
    Assert.assertNotNull(demandConfig.getSectors().iterator().next()
        .getAssociatedLUs().size());
    Assert.assertNotNull(demandConfig.getResidentialPastTrendInfos().iterator()
        .next().getTotalPopulation());
    Assert.assertNotNull(demandConfig.getEmploymentPastTrendInfos().iterator()
        .next().getEmploymentEntries().iterator().next().getSector());
    Assert.assertNotNull(project.getExistingLandUseByLabel("Industrial")
        .getEmploymentSectors().size());
  }
}
