/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl.demand;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * jpa access test for What If configurations factors.
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchAreaRequirementDaoIT extends AbstractTestNGSpringContextTests {
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  private String newScenarioId;

  /** The areaRequirement dao. */
  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchAreaRequirementDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "demand", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### CouchAreaRequirementDaoIT ");
    DemandScenario demandScenario = new DemandScenario();
    demandScenario.setLabel("CouchAreaRequirementDaoIT");
    DemandScenario createdDemandScenario = demandScenarioDao
        .persistDemandScenario(demandScenario);
    newScenarioId = createdDemandScenario.getId();
    Assert.assertNotNull(newScenarioId);
    LOGGER.debug("newScenarioId", newScenarioId);
  }

  /**
   * Creates the allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" })
  public void createAreaRequirementTest() throws Exception {

    AreaRequirement areaRequirement = new AreaRequirement();
    areaRequirement.setNewArea(10.0);
    LOGGER.debug("docType: " + areaRequirement.getDocType());
    LOGGER.debug("project uuid: " + newScenarioId);
    areaRequirement.setDemandScenarioId(newScenarioId);
    AreaRequirement createdAreaRequirement = areaRequirementDao
        .persistAreaRequirement(areaRequirement);
    LOGGER.debug("createdAreaRequirement uuid: {}, for scenario {}",
        createdAreaRequirement.getId(),
        createdAreaRequirement.getDemandScenarioId());
    newId = createdAreaRequirement.getId();
    Assert.assertNotNull(newId);
    Assert.assertNotNull(createdAreaRequirement.getDemandScenarioId());
  }

  /**
   * Find allocation lu by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createAreaRequirementTest" })
  public void findAreaRequirementByIdTest() throws Exception {

    AreaRequirement areaRequirement = areaRequirementDao
        .findAreaRequirementById(newId);
    Assert.assertNotNull(areaRequirement);
  }

  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createAreaRequirementTest" })
  public void getAreaRequirements() throws Exception {

    List<AreaRequirement> areaRequirements = areaRequirementDao
        .getAreaRequirements(newScenarioId);
    Assert.assertNotNull(areaRequirements);
    Assert.assertEquals(areaRequirements.size(), 1);
  }

  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createAreaRequirementTest" })
  public void getAreaRequirementsDemonstration() throws Exception {

    List<AreaRequirement> areaRequirements = areaRequirementDao
        .getAreaRequirements(WifKeys.TEST_DEMAND_SCENARIO_ID);
    Assert.assertNotNull(areaRequirements);
    Assert.assertEquals(areaRequirements.size(), 20);
  }

  /**
   * Update allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createAreaRequirementTest" })
  public void updateAreaRequirementTest() throws Exception {

    AreaRequirement areaRequirement = areaRequirementDao
        .findAreaRequirementById(newId);
    areaRequirement.setNewArea(20.0);
    areaRequirementDao.updateAreaRequirement(areaRequirement);
    AreaRequirement areaRequirement2 = areaRequirementDao
        .findAreaRequirementById(newId);
    Assert.assertEquals(areaRequirement2.getNewArea(), 20.0);
  }

  /**
   * Delete allocation lu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "demand", "database",
      "couch" }, dependsOnMethods = { "updateAreaRequirementTest" })
  public void deleteAreaRequirementTest() throws Exception {

    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(newScenarioId);
    demandScenarioDao.deleteDemandScenario(demandScenario);
    AreaRequirement areaRequirement = areaRequirementDao
        .findAreaRequirementById(newId);
    areaRequirementDao.deleteAreaRequirement(areaRequirement);
    AreaRequirement areaRequirement2 = areaRequirementDao
        .findAreaRequirementById(newId);
    Assert.assertNull(areaRequirement2);
  }
}
