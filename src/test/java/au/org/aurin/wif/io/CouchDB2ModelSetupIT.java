/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorTypeDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class Model2CouchDBTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDB2ModelSetupIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The factor dao. */
  @Autowired
  private CouchFactorDao factorDao;

  /** The factor type dao. */
  @Autowired
  private CouchFactorTypeDao factorTypeDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDB2ModelSetupIT.class);

  @Autowired
  private WifFileUtils fileUtils;

  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void parseProjectSetupTest() throws Exception {
    // INFO-CouchParser:139 - fSLU score UAZ value: null
    WifProject project = wifProjectDao.findProjectById(WifKeys.TEST_PROJECT_ID);
    project = projectParser.parse(project);
    Assert.assertNotNull(project.getId());
    Set<SuitabilityLU> suitabilityLUs = project.getSuitabilityLUs();
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      LOGGER.debug("SuitabilityLU {} has {} associatedLUs",
          suitabilityLU.getLabel(), suitabilityLU.getAssociatedALUs().size());
      Assert.assertNotNull(suitabilityLU.getAssociatedALUs().size());
    }
  }
}
