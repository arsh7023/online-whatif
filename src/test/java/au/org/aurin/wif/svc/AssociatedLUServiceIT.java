/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.suitability.SuitabilityLUService;

/**
 * service test for What If configurations suitabilityLU.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class AssociatedLUServiceIT extends AbstractTestNGSpringContextTests {

  /** the service. */
  @Resource
  private SuitabilityLUService suitabilityLUService;

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The project. */
  private WifProject project;

  /** The suitabilityLU label. */
  private final String suitabilityLULabel = "suitabilityLUTest"
      + System.currentTimeMillis();

  /** The test id. */
  private String testID;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  private String ALUId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AssociatedLUServiceIT.class);

  /**
   * Adds the suitabilityLU test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createSuitabilityLUTest() throws Exception {
    project = new WifProject();
    project.setName("AssociatedLUServiceIT");
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
   * Adds the associated lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createSuitabilityLUTest" }, groups = {
      "setup", "service" })
  public void addAssociatedLUTest() throws Exception {
    LOGGER.debug("addAssociatedLUTest, suitabilityLU id: " + testID);
    AllocationLU allocationLU = new AllocationLU();
    allocationLU.setLabel("ASUTest1");
    allocationLU.setFeatureFieldName("name");

    allocationLU.setProjectId(newProjectId);
    allocationLU = allocationLUService.createAllocationLU(allocationLU,
        newProjectId);
    ALUId = allocationLU.getId();
    LOGGER.debug("added ALUId: " + ALUId);
    suitabilityLUService.addAssociatedLU(allocationLU.getId(), testID,
        newProjectId);
    SuitabilityLU suitabilityLU2 = suitabilityLUService.getSuitabilityLU(
        testID, newProjectId);
    Assert.assertNotNull(suitabilityLU2.getAssociatedALUs());
    Assert.assertEquals(suitabilityLU2.getAssociatedALUs().iterator().next()
        .getLabel(), "ASUTest1");
  }

  /**
   * Delete associated lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "addAssociatedLUTest" }, groups = {
      "setup", "service" })
  public void deleteAssociatedLUTest() throws Exception {
    LOGGER.debug("deleteAssociatedLUTest, suitabilityLU id: " + testID);
    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID,
        newProjectId);

    AllocationLU allocationLU = suitabilityLU.getAssociatedALUs().iterator()
        .next();
    ALUId = allocationLU.getId();
    LOGGER.debug("to delete ALUId: " + ALUId);
    suitabilityLUService.deleteAssociatedLU(allocationLU.getId().toString(),
        testID, newProjectId);
    SuitabilityLU suitabilityLU2 = suitabilityLUService
        .getSuitabilityLU(testID);
    Assert.assertEquals(suitabilityLU2.isAssociatedALU(allocationLU.getId()),
        false);
  }

  /**
   * Delete suitabilityLU test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "deleteAssociatedLUTest", "createSuitabilityLUTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteSuitabilityLUTest() throws Exception {
    System.out
        .println("delete suitabilityLU test, suitabilityLU id: " + testID);
    suitabilityLUService.deleteSuitabilityLU(testID, newProjectId);
    allocationLUService.deleteAllocationLU(ALUId.toString(), newProjectId);
    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);

    SuitabilityLU suitabilityLU = suitabilityLUService.getSuitabilityLU(testID);
    Assert.assertNull(suitabilityLU);

  }

}
