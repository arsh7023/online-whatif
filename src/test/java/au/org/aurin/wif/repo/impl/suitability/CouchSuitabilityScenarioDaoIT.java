/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.suitability;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;

/**
 * The Class CouchSuitabilityScenarioDaoTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchSuitabilityScenarioDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The suitability scenario label. */
  private final String suitabilityScenarioLabel = "suitabilityScenarioTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityScenarioDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### SuitabilityScenarioDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createSuitabilityScenarioTest() throws Exception {

    SuitabilityScenario suitabilityScenario = new SuitabilityScenario();
    suitabilityScenario.setLabel(suitabilityScenarioLabel);
    LOGGER.debug("docType: " + suitabilityScenario.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    suitabilityScenario.setProjectId(newProjectId);
    SuitabilityScenario createdSuitabilityScenario = suitabilityScenarioDao
        .persistSuitabilityScenario(suitabilityScenario);
    LOGGER.debug("createdSuitabilityScenario uuid: "
        + createdSuitabilityScenario.getId());
    newId = createdSuitabilityScenario.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find suitability scenario by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityScenarioTest" })
  public void findSuitabilityScenarioByIdTest() throws Exception {

    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(newId);
    Assert.assertNotNull(suitabilityScenario);
  }

  /**
   * Update suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityScenarioTest" })
  public void updateSuitabilityScenarioTest() throws Exception {

    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(newId);
    suitabilityScenario.setLabel("test");
    suitabilityScenarioDao.updateSuitabilityScenario(suitabilityScenario);
    SuitabilityScenario suitabilityScenario2 = suitabilityScenarioDao
        .findSuitabilityScenarioById(newId);
    Assert.assertEquals(suitabilityScenario2.getLabel(), "test");
  }

  /**
   * Gets the all suitability scenarios by project test.
   * 
   * @return the all suitability scenarios by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateSuitabilityScenarioTest" })
  public void getAllSuitabilityScenariosByProjectTest() throws Exception {
    LOGGER.debug("getAllSuitabilityScenariosByProjectTest project uuid: "
        + newProjectId);
    List<SuitabilityScenario> allSuitabilityScenarios = suitabilityScenarioDao
        .getSuitabilityScenarios(newProjectId);
    Assert.assertNotNull(allSuitabilityScenarios);
    LOGGER.debug("SuitabilityScenario size: " + allSuitabilityScenarios.size());
    Assert.assertEquals(allSuitabilityScenarios.size(), 1);
  }

  /**
   * Delete suitability scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllSuitabilityScenariosByProjectTest" })
  public void deleteSuitabilityScenarioTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(newId);
    suitabilityScenarioDao.deleteSuitabilityScenario(suitabilityScenario);
    SuitabilityScenario suitabilityScenario2 = suitabilityScenarioDao
        .findSuitabilityScenarioById(newId);
    Assert.assertNull(suitabilityScenario2);

  }
}
