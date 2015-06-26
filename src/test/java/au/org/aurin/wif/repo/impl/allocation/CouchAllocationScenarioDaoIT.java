/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.allocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;

/**
 * The Class CouchAllocationScenarioDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchAllocationScenarioDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The allocation scenario label. */
  private final String allocationScenarioLabel = "allocationScenarioTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationScenarioDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "allocation", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### AllocationScenarioDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "allocation", "database", "couch" })
  public void createAllocationScenarioTest() throws Exception {

    AllocationScenario allocationScenario = new AllocationScenario();
    allocationScenario.setLabel(allocationScenarioLabel);
    LOGGER.debug("docType: " + allocationScenario.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    allocationScenario.setProjectId(newProjectId);
    AllocationScenario createdAllocationScenario = allocationScenarioDao
        .persistAllocationScenario(allocationScenario);
    LOGGER.debug("createdAllocationScenario uuid: "
        + createdAllocationScenario.getId());
    newId = createdAllocationScenario.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation scenario by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "allocation", "database", "couch" }, dependsOnMethods = { "createAllocationScenarioTest" })
  public void findAllocationScenarioByIdTest() throws Exception {

    AllocationScenario allocationScenario = allocationScenarioDao
        .findAllocationScenarioById(newId);
    Assert.assertNotNull(allocationScenario);
  }

  /**
   * Update allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "allocation", "database", "couch" }, dependsOnMethods = { "createAllocationScenarioTest" })
  public void updateAllocationScenarioTest() throws Exception {

    AllocationScenario allocationScenario = allocationScenarioDao
        .findAllocationScenarioById(newId);
    allocationScenario.setLabel("test");
    allocationScenarioDao.updateAllocationScenario(allocationScenario);
    AllocationScenario allocationScenario2 = allocationScenarioDao
        .findAllocationScenarioById(newId);
    Assert.assertEquals(allocationScenario2.getLabel(), "test");
  }

  /**
   * Delete allocation scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "allocation", "database",
      "couch" }, dependsOnMethods = { "updateAllocationScenarioTest" })
  public void deleteAllocationScenarioTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    AllocationScenario allocationScenario = allocationScenarioDao
        .findAllocationScenarioById(newId);
    allocationScenarioDao.deleteAllocationScenario(allocationScenario);
    AllocationScenario allocationScenario2 = allocationScenarioDao
        .findAllocationScenarioById(newId);
    Assert.assertNull(allocationScenario2);
  }
}
