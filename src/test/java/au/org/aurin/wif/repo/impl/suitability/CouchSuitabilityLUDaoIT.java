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
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;

/**
 * The Class CouchSuitabilityLUDaoTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchSuitabilityLUDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The suitability lu label. */
  private final String suitabilityLULabel = "suitabilityLUTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityLUDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### SuitabilityLUDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Creates the suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createSuitabilityLUTest() throws Exception {

    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel(suitabilityLULabel);
    LOGGER.debug("docType: " + suitabilityLU.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    suitabilityLU.setProjectId(newProjectId);
    SuitabilityLU createdSuitabilityLU = suitabilityLUDao
        .persistSuitabilityLU(suitabilityLU);
    LOGGER.debug("createdSuitabilityLU uuid: " + createdSuitabilityLU.getId());
    newId = createdSuitabilityLU.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find suitability lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityLUTest" })
  public void findSuitabilityLUByIdTest() throws Exception {

    SuitabilityLU suitabilityLU = suitabilityLUDao.findSuitabilityLUById(newId);
    Assert.assertNotNull(suitabilityLU);
  }

  /**
   * Update suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityLUTest" })
  public void updateSuitabilityLUTest() throws Exception {

    SuitabilityLU suitabilityLU = suitabilityLUDao.findSuitabilityLUById(newId);
    suitabilityLU.setLabel("test");
    suitabilityLUDao.updateSuitabilityLU(suitabilityLU);
    SuitabilityLU suitabilityLU2 = suitabilityLUDao
        .findSuitabilityLUById(newId);
    Assert.assertEquals(suitabilityLU2.getLabel(), "test");
  }

  /**
   * Gets the all suitability l us by project test.
   * 
   * @return the all suitability l us by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateSuitabilityLUTest" })
  public void getAllSuitabilityLUsByProjectTest() throws Exception {
    LOGGER.debug("getAllSuitabilityLUsByProjectTest project uuid: "
        + newProjectId);
    List<SuitabilityLU> allSuitabilityLUs = suitabilityLUDao
        .getSuitabilityLUs(newProjectId);
    Assert.assertNotNull(allSuitabilityLUs);
    LOGGER.debug("SuitabilityLU size: " + allSuitabilityLUs.size());
    Assert.assertEquals(allSuitabilityLUs.size(), 1);
  }

  /**
   * Delete suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllSuitabilityLUsByProjectTest" })
  public void deleteSuitabilityLUTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    SuitabilityLU suitabilityLU = suitabilityLUDao.findSuitabilityLUById(newId);
    suitabilityLUDao.deleteSuitabilityLU(suitabilityLU);
    SuitabilityLU suitabilityLU2 = suitabilityLUDao
        .findSuitabilityLUById(newId);
    Assert.assertNull(suitabilityLU2);

  }
}
