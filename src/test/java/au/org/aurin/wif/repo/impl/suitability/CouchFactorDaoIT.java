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
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.FactorDao;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchFactorDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  private String newProjectId;

  /** The factor dao. */
  @Autowired
  private FactorDao factorDao;

  /** The allocation lu label. */
  private final String factorLabel = "factorTest" + System.currentTimeMillis();

  /** The new id. */
  private String newId;
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchFactorDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("#########CouchFactorDaoTest ");
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
  public void createFactorTest() throws Exception {

    Factor factor = new Factor();
    factor.setLabel(factorLabel);
    LOGGER.debug("project uuid: " + newProjectId);
    factor.setProjectId(newProjectId);
    Factor createdFactor = factorDao.persistFactor(factor);
    LOGGER.debug("createdFactor uuid: " + createdFactor.getId());
    newId = createdFactor.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find allocation lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createFactorTest" })
  public void findFactorByIdTest() throws Exception {

    Factor factor = factorDao.findFactorById(newId);
    Assert.assertNotNull(factor);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createFactorTest" })
  public void updateFactorTest() throws Exception {

    Factor factor = factorDao.findFactorById(newId);
    factor.setLabel("test");
    factorDao.updateFactor(factor);
    Factor factor2 = factorDao.findFactorById(newId);
    Assert.assertEquals(factor2.getLabel(), "test");
  }

  /**
   * Gets the all allocation l us by project test.
   * 
   * @return the all allocation l us by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateFactorTest" })
  public void getAllFactorsByProjectTest() throws Exception {

    List<Factor> allFactors = factorDao.getFactors(newProjectId);
    Assert.assertNotNull(allFactors);
    LOGGER.debug("Factor size: " + allFactors.size());
    Assert.assertEquals(allFactors.size(), 1);
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllFactorsByProjectTest" })
  public void deleteFactorTest() throws Exception {

    Factor factor = factorDao.findFactorById(newId);
    factorDao.deleteFactor(factor);
    Factor factor2 = factorDao.findFactorById(newId);
    Assert.assertNull(factor2);

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
  }
}
