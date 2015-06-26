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
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.FactorDao;
import au.org.aurin.wif.repo.suitability.FactorTypeDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;

/**
 * The Class CouchSuitabilityRuleDaoTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchSuitabilityRuleDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The new project id. */
  private String newProjectId;

  /** The factor id. */
  private String factorId;

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The factor dao. */
  @Autowired
  private FactorDao factorDao;

  /** The factor type dao. */
  @Autowired
  private FactorTypeDao factorTypeDao;

  /** The suitability lu label. */
  private final String suitabilityLULabel = "suitabilityLUTest"
      + System.currentTimeMillis();

  /** The suitability lu id. */
  private String suitabilityLUId;

  /** The new id. */
  private String newId;

  /** The new factor type id. */
  private String newFactorTypeId;

  /** The new scenario id. */
  private String newScenarioId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchSuitabilityRuleDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "setup", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### SuitabilityRuleDaoTest ");
    WifProject project = new WifProject();
    project.setName("test");
    project.setRoleOwner("aurin");
    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);

    SuitabilityScenario suitabilityScenario = new SuitabilityScenario();
    suitabilityScenario.setLabel("Test");
    LOGGER.debug("docType: " + suitabilityScenario.getDocType());
    LOGGER.debug("project uuid: " + newProjectId);
    suitabilityScenario.setProjectId(newProjectId);
    SuitabilityScenario createdSuitabilityScenario = suitabilityScenarioDao
        .persistSuitabilityScenario(suitabilityScenario);
    LOGGER.debug("createdSuitabilityScenario uuid: "
        + createdSuitabilityScenario.getId());
    newScenarioId = createdSuitabilityScenario.getId();
    Assert.assertNotNull(newScenarioId);

    // SuitabilityLU suitabilityLU = new SuitabilityLU();
    // suitabilityLU.setLabel(suitabilityLULabel);
    // LOGGER.debug("docType: " + suitabilityLU.getDocType());
    // LOGGER.debug("project uuid: " + newProjectId);
    // suitabilityLU.setProjectId(newProjectId);
    // SuitabilityLU createdSuitabilityLU = suitabilityLUDao
    // .persistSuitabilityLU(suitabilityLU);
    // LOGGER.debug("createdSuitabilityLU uuid: "
    // + createdSuitabilityLU.getId());
    // suitabilityLUId = createdSuitabilityLU.getId();
    // Assert.assertNotNull(suitabilityLUId);
    // Factor factor = new Factor();
    // factor.setLabel("Factor test");
    // LOGGER.debug("project uuid: "+ newProjectId);
    // factor.setProjectId(newProjectId);
    // Factor createdFactor = factorDao.persistFactor(factor);
    // Assert.assertNotNull(createdFactor.getId());
    // LOGGER.debug("createdFactor uuid: "+ createdFactor.getId());
    // factorId = createdFactor.getId();
    // FactorType factorType = new FactorType();
    // factorType.setLabel("typeTest");
    // factorType.setFactorId(factorId);
    // factorType.setNaturalOrder(1);
    // FactorType createdFactorType =
    // factorTypeDao.persistFactorType(factorType);
    // newFactorTypeId = createdFactorType.getId();
    // LOGGER.debug("createdFactorType uuid: "+
    // createdFactorType.getId());

  }

  /**
   * Creates the suitability rule test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" })
  public void createSuitabilityRuleTest() throws Exception {
    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(newScenarioId);

    SuitabilityRule suitabilityRule = new SuitabilityRule();
    suitabilityRule.setScenarioId(newScenarioId);
    LOGGER.debug("docType: " + suitabilityRule.getDocType());
    LOGGER.debug("Suitability Scenario uuid: " + newScenarioId);
    SuitabilityRule createdSuitabilityRule = suitabilityRuleDao
        .persistSuitabilityRule(suitabilityRule);
    LOGGER.debug("createdSuitabilityRule uuid: "
        + createdSuitabilityRule.getId());
    newId = createdSuitabilityRule.getId();
    Assert.assertNotNull(newId);
  }

  /**
   * Find suitability rule by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityRuleTest" })
  public void findSuitabilityRuleByIdTest() throws Exception {

    SuitabilityRule suitabilityRule = suitabilityRuleDao
        .findSuitabilityRuleById(newId);
    Assert.assertNotNull(suitabilityRule);
  }

  /**
   * Update suitability rule test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "createSuitabilityRuleTest" })
  public void updateSuitabilityRuleTest() throws Exception {

    SuitabilityRule suitabilityRule = suitabilityRuleDao
        .findSuitabilityRuleById(newId);
    // suitabilityRule.setLabel("test");
    suitabilityRuleDao.updateSuitabilityRule(suitabilityRule);
    SuitabilityRule suitabilityRule2 = suitabilityRuleDao
        .findSuitabilityRuleById(newId);
    // Assert.assertEquals(suitabilityRule2.getLabel(), "test");
  }

  /**
   * Gets the all suitability rules by project test.
   * 
   * @return the all suitability rules by project test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couch" }, dependsOnMethods = { "updateSuitabilityRuleTest" })
  public void getAllSuitabilityRulesByScenarioTest() throws Exception {
    LOGGER.debug("getAllSuitabilityRulesByScenarioTest scenario uuid: "
        + newScenarioId);
    List<SuitabilityRule> allSuitabilityRules = suitabilityRuleDao
        .getSuitabilityRules(newScenarioId);
    Assert.assertNotNull(allSuitabilityRules);
    LOGGER.debug("SuitabilityRule size: " + allSuitabilityRules.size());
    Assert.assertEquals(allSuitabilityRules.size(), 1);
  }

  /**
   * Delete suitability rule test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couch" }, dependsOnMethods = { "getAllSuitabilityRulesByScenarioTest" })
  public void deleteSuitabilityRuleTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    SuitabilityRule suitabilityRule = suitabilityRuleDao
        .findSuitabilityRuleById(newId);
    suitabilityRuleDao.deleteSuitabilityRule(suitabilityRule);
    SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
        .findSuitabilityScenarioById(newScenarioId);
    suitabilityScenarioDao.deleteSuitabilityScenario(suitabilityScenario);
    SuitabilityRule suitabilityRule2 = suitabilityRuleDao
        .findSuitabilityRuleById(newId);
    Assert.assertNull(suitabilityRule2);

  }
}
