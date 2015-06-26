/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.repo.WifProjectDao;

/**
 * The Class AllocationLUServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AllocationLUServiceIT extends AbstractTestNGSpringContextTests {

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The project. */
  private WifProject project;

  /** The allocation lu label. */
  private final String allocationLULabel = "allocationLUTest"
      + System.currentTimeMillis();

  /** The test id. */
  private String testID;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationLUServiceIT.class);

  /**
   * Creates the allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createAllocationLUTest() throws Exception {
    LOGGER.debug("createAllocationLUTest");
    project = new WifProject();
    project.setName("test");
    project = wifProjectDao.persistProject(project);
    Assert.assertNotNull(project.getId());
    newProjectId = project.getId().toString();
    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setProjectId(newProjectId);
    allocationLU.setLabel(allocationLULabel);
    allocationLU.setFeatureFieldName("name");
    LOGGER.debug("createAllocationLUTest: " + allocationLU.getLabel());
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        newProjectId);
    testID = (allocationLU.getId());
  }

  /**
   * Gets the allocation lu test.
   * 
   * @return the allocation lu test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createAllocationLUTest" }, groups = {
      "setup", "service" })
  public void getAllocationLUTest() throws Exception {

    AllocationLU allocationLU = allocationLUService.getAllocationLU(testID);
    Assert.assertNotNull(allocationLU);
    Assert.assertNotNull(allocationLU.getProjectId());
  }

  /**
   * Creates the bad allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createAllocationLUTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadAllocationLUTest() throws Exception {

    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setProjectId(newProjectId);
    allocationLU.setFeatureFieldName("name");
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        newProjectId);
  }

  /**
   * Creates the bad allocation l u2 test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createAllocationLUTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadAllocationLU2Test() throws Exception {

    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel(allocationLULabel);
    allocationLU.setProjectId(newProjectId);
    allocationLU.setFeatureFieldName("name");
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        newProjectId);
  }

  @Test(enabled = true, dependsOnMethods = { "createAllocationLUTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidFFNameException.class)
  public void createBadAllocationLU3Test() throws Exception {

    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel("createBadAllocationLU3Test");
    allocationLU.setProjectId(newProjectId);
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        newProjectId);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getAllocationLUTest" }, groups = {
      "setup", "service" })
  public void updateAllocationLUTest() throws Exception {
    AllocationLU allocationLU = allocationLUService.getAllocationLU(testID);
    LOGGER.debug("update allocationLU test, allocationLU label: "
        + allocationLU.getLabel());
    allocationLU.setLabel("new" + allocationLULabel);
    allocationLUService.updateAllocationLU(allocationLU, newProjectId);
    AllocationLU allocationLU2 = allocationLUService.getAllocationLU(testID);
    Assert.assertEquals(allocationLU2.getLabel(), "new" + allocationLULabel);
  }

  /**
   * Gets the allocation l us by project test.
   * 
   * @return the allocation l us by project test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateAllocationLUTest" }, groups = {
      "setup", "service" })
  public void getAllocationLUsByProjectTest() throws Exception {

    List<AllocationLU> allAllocationLUs = allocationLUService
        .getAllocationLUs(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(allAllocationLUs);
    LOGGER.debug("size " + allAllocationLUs.size());
    Assert.assertNotEquals(allAllocationLUs.size(), 14);
  }

  /**
   * Gets the allocation lu withwrong project id test.
   * 
   * @return the allocation lu withwrong project id test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getAllocationLUsByProjectTest" }, expectedExceptions = WifInvalidInputException.class, groups = {
      "setup", "service" })
  public void getAllocationLUWithwrongProjectIdTest() throws Exception {
    LOGGER
        .debug("getAllocationLUWithProjectIdTest, allocationLU id: " + testID);
    AllocationLU allocationLU = allocationLUService
        .getAllocationLU(testID, "0");
    Assert.assertNull(allocationLU);
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "getAllocationLUWithwrongProjectIdTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteAllocationLUTest() throws Exception {
    LOGGER.debug("delete allocationLU test, allocationLU id: " + testID);
    allocationLUService.deleteAllocationLU(testID, newProjectId);
    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    AllocationLU allocationLU = allocationLUService.getAllocationLU(testID);
    Assert.assertNull(allocationLU);
  }

}
