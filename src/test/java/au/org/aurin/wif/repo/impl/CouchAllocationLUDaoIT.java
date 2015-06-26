/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl;

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
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.repo.AllocationLUDao;
import au.org.aurin.wif.repo.WifProjectDao;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchAllocationLUDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The allocationLU dao. */
  @Autowired
  private AllocationLUDao allocationLUDao;

  /** The allocation lu label. */
  private final String allocationLULabel = "allocationLUTest"
      + System.currentTimeMillis();

  /** The new id. */
  private String newId;
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAllocationLUDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### AllocationLUDaoTest ");
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
  public void createAllocationLUTest() throws Exception {

    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel(allocationLULabel);

    allocationLU.setNewPreservation(false);
    allocationLU.setLandUseFunction(LandUseFunction.LBCS_1XXX);
    allocationLU.setFeatureFieldName("1.0");
    allocationLU.setTotalArea(5520.86);
    LOGGER.debug("docType: " + allocationLU.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    allocationLU.setProjectId(newProjectId);
    AllocationLU createdAllocationLU = allocationLUDao
        .persistAllocationLU(allocationLU);
    LOGGER.debug("createdAllocationLU uuid: " + createdAllocationLU.getId());
    newId = createdAllocationLU.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createAllocationLUTest" })
  public void findAllocationLUByIdTest() throws Exception {

    AllocationLU allocationLU = allocationLUDao.findAllocationLUById(newId);
    Assert.assertNotNull(allocationLU);
    Assert.assertEquals(allocationLU.getTotalArea(), 5520.86);
    Assert.assertEquals(allocationLU.getLandUseFunction(),
        LandUseFunction.LBCS_1XXX);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createAllocationLUTest" })
  public void updateAllocationLUTest() throws Exception {

    AllocationLU allocationLU = allocationLUDao.findAllocationLUById(newId);
    allocationLU.setLabel("test");
    allocationLUDao.updateAllocationLU(allocationLU);
    AllocationLU allocationLU2 = allocationLUDao.findAllocationLUById(newId);
    Assert.assertEquals(allocationLU2.getLabel(), "test");
  }

  /**
   * Gets the all allocation l us by project test.
   * 
   * @return the all allocation l us by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateAllocationLUTest" })
  public void getAllAllocationLUsByProjectTest() throws Exception {
    LOGGER.debug("getAllAllocationLUsByProjectTest project uuid: "
        + newProjectId);
    List<AllocationLU> allAllocationLUs = allocationLUDao
        .getAllocationLUs(newProjectId);
    Assert.assertNotNull(allAllocationLUs);
    LOGGER.debug("AllocationLU size: " + allAllocationLUs.size());
    Assert.assertEquals(allAllocationLUs.size(), 1);
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllAllocationLUsByProjectTest" })
  public void deleteAllocationLUTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    AllocationLU allocationLU = allocationLUDao.findAllocationLUById(newId);
    allocationLUDao.deleteAllocationLU(allocationLU);
    AllocationLU allocationLU2 = allocationLUDao.findAllocationLUById(newId);
    Assert.assertNull(allocationLU2);

  }
}
