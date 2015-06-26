/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc.suitability;

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
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.AllocationLUService;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class SuitabilityLUServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityLUServiceIT extends AbstractTestNGSpringContextTests {

  /** The suitability lu service. */
  @Resource
  private SuitabilityLUService suitabilityLUService;

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The suitability lu label. */
  private final String suitabilityLULabel = "suitabilityLUTest"
      + System.currentTimeMillis();

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The project. */
  private WifProject project;

  /** The test id. */
  private String testID;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityLUServiceIT.class);

  /**
   * Creates the suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createSuitabilityLUTest() throws Exception {
    project = new WifProject();
    project.setName("SuitabilityLUServiceIT");
    project = wifProjectDao.persistProject(project);
    Assert.assertNotNull(project.getId());
    newProjectId = project.getId().toString();
    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel(suitabilityLULabel);
    suitabilityLU.setProjectId(newProjectId);
    LOGGER.debug("createSuitabilityLUTest: " + suitabilityLU.getLabel());
    suitabilityLU = suitabilityLUService.createSuitabilityLU(suitabilityLU,
        newProjectId);
    testID = suitabilityLU.getId();
    Assert.assertNotNull(testID);
    LOGGER.debug("finished" + testID);
  }

  /**
   * Creates the bad suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityLUTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadSuitabilityLUTest() throws Exception {

    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setProjectId(newProjectId);

    suitabilityLU = suitabilityLUService.createSuitabilityLU(suitabilityLU,
        newProjectId);
  }

  /**
   * Creates the bad suitability l u2 test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityLUTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadSuitabilityLU2Test() throws Exception {

    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel(suitabilityLULabel);
    suitabilityLU.setProjectId(newProjectId);

    suitabilityLU = suitabilityLUService.createSuitabilityLU(suitabilityLU,
        newProjectId);
  }

  /**
   * Gets the suitability lu test.
   * 
   * @return the suitability lu test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityLUTest" }, groups = {
      "setup", "service" })
  public void getSuitabilityLUTest() throws Exception {

    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID);
    Assert.assertNotNull(suitabilityLU);
  }

  /**
   * Adds the suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityLUTest" }, groups = {
      "setup", "service" })
  public void addSuitabilityLUTest() throws Exception {
    WifProject project0 = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    int size = project0.getSuitabilityLUs().size();
    LOGGER.debug("SuitabilityLU size {}: "
        + project0.getSuitabilityLUs().size());

    SuitabilityLU suitabilityLU = new SuitabilityLU();
    suitabilityLU.setLabel(suitabilityLULabel);
    suitabilityLU.setProjectId(WifKeys.TEST_PROJECT_ID);
    LOGGER.debug("addSuitabilityLUTest: " + suitabilityLU.getLabel());
    suitabilityLU = suitabilityLUService.createSuitabilityLU(suitabilityLU,
        WifKeys.TEST_PROJECT_ID);
    SuitabilityLU savedSuitabilityLU = suitabilityLUService
        .getSuitabilityLU(suitabilityLU.getId());
    Assert.assertNotNull(savedSuitabilityLU);
    WifProject project2 = projectService.getProject(savedSuitabilityLU
        .getProjectId());
    LOGGER.debug("new SuitabilityLU size {}: "
        + project2.getSuitabilityLUs().size());
    Assert.assertEquals(project2.getSuitabilityLUs().size(), size + 1);
    suitabilityLUService.deleteSuitabilityLU(savedSuitabilityLU.getId(),
        WifKeys.TEST_PROJECT_ID);
    WifProject project3 = projectService.getProject(savedSuitabilityLU
        .getProjectId());
    LOGGER.debug("SuitabilityLU size {}: "
        + project3.getSuitabilityLUs().size());
    Assert.assertEquals(project3.getSuitabilityLUs().size(), size);
  }

  /**
   * Update suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getSuitabilityLUTest" }, groups = {
      "setup", "service" })
  public void updateSuitabilityLUTest() throws Exception {
    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID);
    LOGGER.debug("update suitabilityLU test, suitabilityLU label: "
        + suitabilityLU.getLabel());
    suitabilityLU.setLabel("new" + suitabilityLULabel);
    suitabilityLUService.updateSuitabilityLU(suitabilityLU, newProjectId);
    SuitabilityLU suitabilityLU2 = suitabilityLUService
        .getSuitabilityLU(testID);
    Assert.assertEquals(suitabilityLU2.getLabel(), "new" + suitabilityLULabel);
  }

  /**
   * Gets the suitability l us by project test.
   * 
   * @return the suitability l us by project test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateSuitabilityLUTest" }, groups = {
      "setup", "service" })
  public void getSuitabilityLUsByProjectTest() throws Exception {

    List<SuitabilityLU> allSuitabilityLUs = suitabilityLUService
        .getSuitabilityLUs(newProjectId);
    Assert.assertNotNull(allSuitabilityLUs);
    LOGGER.debug("size " + allSuitabilityLUs.size());
    Assert.assertNotEquals(allSuitabilityLUs.size(), 0);
  }

  /**
   * Gets the suitability lu with wrong project id test.
   * 
   * @return the suitability lu with wrong project id test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getSuitabilityLUsByProjectTest" }, expectedExceptions = WifInvalidInputException.class, groups = {
      "setup", "service" })
  public void getSuitabilityLUWithWrongProjectIdTest() throws Exception {
    LOGGER.debug("getSuitabilityLUWithProjectIdTest, suitabilityLU id: "
        + testID);
    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID,
        "0");
    Assert.assertNull(suitabilityLU);
  }

  /**
   * Delete suitability lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "getSuitabilityLUWithWrongProjectIdTest", "createSuitabilityLUTest",
      "updateSuitabilityLUTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteSuitabilityLUTest() throws Exception {
    System.out
        .println("delete suitabilityLU test, suitabilityLU id: " + testID);
    suitabilityLUService.deleteSuitabilityLU(testID, newProjectId);
    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID);
    Assert.assertNull(suitabilityLU);
  }
}
