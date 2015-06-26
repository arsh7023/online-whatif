/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.suitability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.WifFileUtils;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class CouchDB2ModelSuitabilityIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDB2ModelSuitabilityIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The suitability parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDB2ModelSuitabilityIT.class);

  /** The file utils. */
  @Autowired
  private WifFileUtils fileUtils;

  /**
   * Parses the suitability test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void parseSuitabilityTest() throws Exception {

    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    WifProject project = wifProjectDao.findProjectById(suitabilityScenario
        .getProjectId());
    project = projectParser.parse(project);
    suitabilityScenario = suitabilityParser.parseSuitabilityScenario(
        suitabilityScenario, project);
    Assert.assertNotNull(suitabilityScenario.getId());
    Assert.assertNotNull(suitabilityScenario.getSuitabilityRules().iterator()
        .next().getConvertibleLUs().size());
  }
}
