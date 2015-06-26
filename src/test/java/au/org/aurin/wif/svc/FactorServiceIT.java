/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.util.HashSet;
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
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.suitability.FactorService;

/**
 * The Class FactorServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class FactorServiceIT extends AbstractTestNGSpringContextTests {

  /** The factor service. */
  @Resource
  private FactorService factorService;

  /** The factor label. */
  private final String factorLabel = "factorTest" + System.currentTimeMillis();

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
      .getLogger(FactorServiceIT.class);

  /**
   * Creates the factor test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void createFactorTest() throws Exception {
    project = new WifProject();
    project.setName("test");
    project = wifProjectDao.persistProject(project);
    Assert.assertNotNull(project.getId());
    newProjectId = project.getId().toString();
    Factor factor = new Factor();
    factor.setLabel(factorLabel);
    factor.setProjectId(newProjectId);
    LOGGER.debug("createFactorTest: " + factor.getLabel());
    FactorType factorType1 = new FactorType();
    factorType1.setLabel("type1");
    factorType1.setFactor(factor);
    FactorType factorType = new FactorType();
    factorType.setLabel("type0");
    factorType.setFactor(factor);
    factor.setFactorTypes(new HashSet<FactorType>());
    factor.addFactorType(factorType1);
    factor.addFactorType(factorType);
    factor = factorService.createFactor(factor, newProjectId);
    testID = (factor.getId());
  }

  /**
   * Gets the factor test.
   * 
   * @return the factor test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createFactorTest" }, groups = {
      "setup", "service" })
  public void getFactorTest() throws Exception {
    LOGGER.debug("getFactorTest ");
    Factor factor = factorService.getFactor(testID, newProjectId);
    Assert.assertNotNull(factor);
  }

  /**
   * Creates the bad factor test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createFactorTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadFactorTest() throws Exception {
    LOGGER.debug("createBadFactorTest ");

    Factor factor = new Factor();
    factor.setProjectId(newProjectId);
    factor = factorService.createFactor(factor, newProjectId);
  }

  /**
   * Creates the bad factor type test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "createFactorTest" }, groups = {
      "setup", "service" }, expectedExceptions = InvalidLabelException.class)
  public void createBadFactorTypeTest() throws Exception {
    LOGGER.debug("createBadFactorTest ");

    Factor factor = factorService.getFactor(testID, newProjectId);

    FactorType factorType1 = new FactorType();

    factorType1.setLabel("type1");
    factorType1.setFactor(factor);
    factor = factorService.createFactor(factor, newProjectId);
  }

  /**
   * Update factor type test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "getFactorTest" }, groups = {
      "setup", "service" })
  public void updateFactorTypeTest() throws Exception {
    LOGGER.debug("updateFactorTest ");

    Factor factor = factorService.getFactor(testID, newProjectId);
    Assert.assertEquals(factor.getFactorTypes().size(), 2);
    factor.getFactorTypes().remove(factor.getFactorTypeByLabel("type0"));
    factorService.updateFactor(factor, newProjectId);
    Factor factor2 = factorService.getFactor(testID, newProjectId);
    Assert.assertEquals(factor2.getFactorTypes().size(), 1);
  }

  /**
   * Update factor test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateFactorTypeTest" }, groups = {
      "setup", "service" })
  public void updateFactorTest() throws Exception {
    LOGGER.debug("updateFactorTest ");

    Factor factor = factorService.getFactor(testID, newProjectId);
    factor.setRevision("Bad revision");
    factor.setLabel("new" + factorLabel);
    factorService.updateFactor(factor, newProjectId);
    Factor factor2 = factorService.getFactor(testID, newProjectId);
    Assert.assertEquals(factor2.getLabel(), "new" + factorLabel);
    FactorType factorType1 = new FactorType();
    factorType1.setLabel("type1new");
    factorType1.setFactor(factor2);
    factor2.getFactorTypes().add(factorType1);
    factor2.setRevision("Bad revision");
    factorService.updateFactor(factor2, newProjectId);
    Assert.assertEquals(factor2.getFactorTypes().size(), 2);
  }

  /**
   * Gets the factors by project test.
   * 
   * @return the factors by project test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, dependsOnMethods = { "updateFactorTest" }, groups = {
      "setup", "service" })
  public void getFactorsByProjectTest() throws Exception {
    LOGGER.debug("getFactorsByProjectTest ");

    List<Factor> allFactors = factorService.getFactors(WifKeys.TEST_PROJECT_ID
        .toString());
    Assert.assertNotNull(allFactors);
    Assert.assertNotEquals(allFactors.size(), 3);
  }

  /**
   * Delete factor test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = {
      "getFactorsByProjectTest", "updateFactorTest" },

  expectedExceptions = InvalidEntityIdException.class, groups = { "setup",
      "service" })
  public void deleteFactorTest() throws Exception {
    LOGGER.debug("delete factor test, factor id: " + testID);
    factorService.deleteFactor(testID, newProjectId);
    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    Factor factor = factorService.getFactor(testID, newProjectId);
    Assert.assertNull(factor);
  }
}
