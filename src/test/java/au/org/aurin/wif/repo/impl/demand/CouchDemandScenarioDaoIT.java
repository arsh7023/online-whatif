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
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDemandScenarioDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The demandScenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The allocation lu label. */
  private final String demandScenarioLabel = "demandScenarioTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandScenarioDaoIT.class);

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
  public void createDemandScenarioTest() throws Exception {

    DemandScenario demandScenario = new DemandScenario();
    demandScenario.setLabel(demandScenarioLabel);
    LOGGER.debug("docType: " + demandScenario.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    demandScenario.setProjectId(newProjectId);
    DemandScenario createdDemandScenario = demandScenarioDao
        .persistDemandScenario(demandScenario);
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
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createDemandScenarioTest" })
  public void findDemandScenarioByIdTest() throws Exception {

    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(newId);
    Assert.assertNotNull(demandScenario);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createDemandScenarioTest" })
  public void updateDemandScenarioTest() throws Exception {

    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(newId);
    demandScenario.setLabel("test");
    demandScenarioDao.updateDemandScenario(demandScenario);
    DemandScenario demandScenario2 = demandScenarioDao
        .findDemandScenarioById(newId);
    Assert.assertEquals(demandScenario2.getLabel(), "test");
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "demand", "database",
      "couch" }, dependsOnMethods = { "updateDemandScenarioTest" })
  public void deleteDemandScenarioTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(newId);
    demandScenarioDao.deleteDemandScenario(demandScenario);
    DemandScenario demandScenario2 = demandScenarioDao
        .findDemandScenarioById(newId);
    Assert.assertNull(demandScenario2);

  }
}
