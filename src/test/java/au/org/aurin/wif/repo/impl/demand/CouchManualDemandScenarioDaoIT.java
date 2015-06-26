/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.demand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchManualDemandScenarioDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The demandScenario dao. */
  @Autowired
  private DemandOutcomeDao manualdemandScenarioDao;

  /** The allocation lu label. */
  private final String demandScenarioLabel = "demandScenarioTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchManualDemandScenarioDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "demand", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### DemandScenarioDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" })
  public void createManualDemandScenarioTest() throws Exception {

    DemandOutcome demandScenario = new DemandOutcome();
    demandScenario.setLabel(demandScenarioLabel);
    LOGGER.debug("docType: " + demandScenario.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    demandScenario.setProjectId(newProjectId);
    DemandOutcome createdDemandScenario = manualdemandScenarioDao
        .persistDemandOutcome(demandScenario);
    LOGGER
        .debug("createdDemandScenario uuid: " + createdDemandScenario.getId());
    newId = createdDemandScenario.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createManualDemandScenarioTest" })
  public void findManualDemandScenarioByIdTest() throws Exception {

    // ManualDemandScenario demandScenario = manualdemandScenarioDao
    // .findManualDemandScenarioById(newId);
    // Assert.assertNotNull(demandScenario);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createManualDemandScenarioTest" })
  public void updateManualDemandScenarioTest() throws Exception {

    // ManualDemandScenario demandScenario = manualdemandScenarioDao
    // .findManualDemandScenarioById(newId);
    // demandScenario.setLabel("test");
    // manualdemandScenarioDao.updateManualDemandScenario(demandScenario);
    // ManualDemandScenario demandScenario2 = manualdemandScenarioDao
    // .findManualDemandScenarioById(newId);
    // Assert.assertEquals(demandScenario2.getLabel(), "test");
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "demand", "database",
      "couch" }, dependsOnMethods = { "updateManualDemandScenarioTest" })
  public void deleteDemandScenarioTest() throws Exception {

    // WifProject project = wifProjectDao.findProjectById(newProjectId);
    // wifProjectDao.deleteProject(project);
    // ManualDemandScenario demandScenario = manualdemandScenarioDao
    // .findManualDemandScenarioById(newId);
    // manualdemandScenarioDao.deleteManualDemandScenario(demandScenario);
    // ManualDemandScenario demandScenario2 = manualdemandScenarioDao
    // .findManualDemandScenarioById(newId);
    // Assert.assertNull(demandScenario2);

  }
}
