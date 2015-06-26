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
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.FactorDao;
import au.org.aurin.wif.repo.suitability.FactorTypeDao;

/**
 * The Class CouchFactorTypeDaoTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchFactorTypeDaoIT extends AbstractTestNGSpringContextTests {

  /** The new project id. */
  private String newProjectId;

  @Autowired
  private WifProjectDao wifProjectDao;

  /** The factor dao. */
  @Autowired
  private FactorDao factorDao;

  /** The factor type dao. */
  @Autowired
  private FactorTypeDao factorTypeDao;

  /** The factor type label. */
  private final String factorTypeLabel = "factorType"
      + System.currentTimeMillis();

  /** The new id. */
  private String factorId;
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchFactorTypeDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("CouchFactorTypeDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
    Factor factor = new Factor();
    factor.setLabel("Factor test");
    LOGGER.debug("project uuid: " + newProjectId);
    factor.setProjectId(newProjectId);
    Factor createdFactor = factorDao.persistFactor(factor);
    Assert.assertNotNull(createdFactor.getId());
    LOGGER.debug("createdFactor uuid: " + createdFactor.getId());
    factorId = createdFactor.getId();

  }

  /**
   * Creates the factor type test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createFactorTypeTest() throws Exception {

    FactorType factorType = new FactorType();
    factorType.setLabel(factorTypeLabel);
    factorType.setFactorId(factorId);
    factorType.setNaturalOrder(1);
    FactorType createdFactorType = factorTypeDao.persistFactorType(factorType);
    newId = createdFactorType.getId();
    LOGGER.debug("createdFactorType uuid: " + createdFactorType.getId());
    Assert.assertNotNull(newId);
  }

  /**
   * Find factor type by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createFactorTypeTest" })
  public void findFactorTypeByIdTest() throws Exception {

    FactorType factorType = factorTypeDao.findFactorTypeById(newId);
    Assert.assertNotNull(factorType);
  }

  /**
   * Update factor type test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createFactorTypeTest" })
  public void updateFactorTypeTest() throws Exception {

    FactorType factorType = factorTypeDao.findFactorTypeById(newId);
    factorType.setLabel("test");
    factorTypeDao.updateFactorType(factorType);
    FactorType factorType2 = factorTypeDao.findFactorTypeById(newId);
    Assert.assertEquals(factorType2.getLabel(), "test");
  }

  /**
   * Gets the all factor types by factor test.
   * 
   * @return the all factor types by factor test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateFactorTypeTest" })
  public void getAllFactorTypesByFactorTest() throws Exception {

    List<FactorType> allFactorTypes = factorTypeDao.getFactorTypes(factorId);
    Assert.assertNotNull(allFactorTypes);
    LOGGER.debug("FactorType size: " + allFactorTypes.size());
    Assert.assertEquals(allFactorTypes.size(), 1);
  }

  /**
   * Delete factor type test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllFactorTypesByFactorTest" })
  public void deleteFactorTypeTest() throws Exception {
    FactorType factorType = factorTypeDao.findFactorTypeById(newId);
    factorTypeDao.deleteFactorType(factorType);
    FactorType factorType2 = factorTypeDao.findFactorTypeById(newId);
    Assert.assertNull(factorType2);

    Factor factor = factorDao.findFactorById(factorId);
    factorDao.deleteFactor(factor);

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);

  }
}
