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
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDemandConfigDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The demandConfig dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The allocation lu label. */
  private final String demandConfigLabel = "demandConfigTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDemandConfigDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### DemandConfigDaoTest ");
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
  @Test(groups = { "setup", "database", "couch" })
  public void createDemandConfigTest() throws Exception {

    DemandConfig demandConfig = new DemandConfig();
    demandConfig.setLabel(demandConfigLabel);
    LOGGER.debug("docType: " + demandConfig.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    demandConfig.setProjectId(newProjectId);
    DemandConfig createdDemandConfig = demandConfigDao
        .persistDemandConfig(demandConfig);
    LOGGER.debug("createdDemandConfig uuid: " + createdDemandConfig.getId());
    newId = createdDemandConfig.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createDemandConfigTest" })
  public void findDemandConfigByIdTest() throws Exception {

    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(newId);
    Assert.assertNotNull(demandConfig);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createDemandConfigTest" })
  public void updateDemandConfigTest() throws Exception {

    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(newId);
    demandConfig.setLabel("test");
    demandConfigDao.updateDemandConfig(demandConfig);
    DemandConfig demandConfig2 = demandConfigDao.findDemandConfigById(newId);
    Assert.assertEquals(demandConfig2.getLabel(), "test");
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "updateDemandConfigTest" })
  public void deleteDemandConfigTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(newId);
    demandConfigDao.deleteDemandConfig(demandConfig);
    DemandConfig demandConfig2 = demandConfigDao.findDemandConfigById(newId);
    Assert.assertNull(demandConfig2);

  }
}
