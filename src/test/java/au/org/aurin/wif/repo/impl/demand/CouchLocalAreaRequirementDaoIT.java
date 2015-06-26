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

import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.demand.LocalAreaRequirementDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class CouchLocalAreaRequirementDaoIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchLocalAreaRequirementDaoIT extends
    AbstractTestNGSpringContextTests {

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The new scenario id. */
  private String newScenarioId;

  /** The local area requirement dao. */
  @Autowired
  private LocalAreaRequirementDao localAreaRequirementDao;

  /** The new id. */
  private String newId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchLocalAreaRequirementDaoIT.class);

  /**
   * Load test project.
   * 
   * @throws Exception
   *           the exception
   */
  @BeforeClass(groups = { "demand", "database", "couch" })
  public void loadTestProject() throws Exception {
    LOGGER.debug("########### CouchLocalAreaRequirementDaoIT ");
    DemandScenario demandScenario = new DemandScenario();
    demandScenario.setLabel("CouchLocalAreaRequirementDaoIT");
    DemandScenario createdDemandScenario = demandScenarioDao
        .persistDemandScenario(demandScenario);
    newScenarioId = createdDemandScenario.getId();
    Assert.assertNotNull(newScenarioId);
    LOGGER.debug("newScenarioId", newScenarioId);
  }

  /**
   * Creates the local area requirement test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" })
  public void createLocalAreaRequirementTest() throws Exception {

    LocalAreaRequirement localAreaRequirement = new LocalAreaRequirement();
    LocalJurisdiction lj = new LocalJurisdiction();
    lj.setLabel("createLocalAreaRequirementTest");
    localAreaRequirement.setLocalJurisdiction(lj);
    localAreaRequirement.setNewArea(10.0);
    LOGGER.debug("docType: " + localAreaRequirement.getDocType());
    LOGGER.debug("project uuid: " + newScenarioId);
    localAreaRequirement.setDemandScenarioId(newScenarioId);
    LocalAreaRequirement createdLocalAreaRequirement = localAreaRequirementDao
        .persistLocalAreaRequirement(localAreaRequirement);
    LOGGER.debug("createdLocalAreaRequirement uuid: {}, for scenario {}",
        createdLocalAreaRequirement.getId(),
        createdLocalAreaRequirement.getDemandScenarioId());
    newId = createdLocalAreaRequirement.getId();
    Assert.assertNotNull(newId);
    Assert.assertNotNull(createdLocalAreaRequirement.getDemandScenarioId());
  }

  /**
   * Find local area requirement by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createLocalAreaRequirementTest" })
  public void findLocalAreaRequirementByIdTest() throws Exception {

    LocalAreaRequirement localAreaRequirement = localAreaRequirementDao
        .findLocalAreaRequirementById(newId);
    Assert.assertNotNull(localAreaRequirement);
    Assert.assertNotNull(localAreaRequirement.getLocalJurisdictionLabel());
  }

  /**
   * Gets the local area requirements.
   * 
   * @return the local area requirements
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createLocalAreaRequirementTest" })
  public void getLocalAreaRequirements() throws Exception {

    List<LocalAreaRequirement> localAreaRequirements = localAreaRequirementDao
        .getLocalAreaRequirements(newScenarioId);
    Assert.assertNotNull(localAreaRequirements);
    Assert.assertEquals(localAreaRequirements.size(), 1);
  }

  /**
   * Gets the local area requirements demonstration.
   * 
   * @return the local area requirements demonstration
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createLocalAreaRequirementTest" })
  public void getLocalAreaRequirementsDemonstration() throws Exception {

    List<LocalAreaRequirement> localAreaRequirements = localAreaRequirementDao
        .getLocalAreaRequirements(WifKeys.TEST_DEMAND_SCENARIO_ID);
    Assert.assertNotNull(localAreaRequirements);
    Assert.assertEquals(localAreaRequirements.size(), 2);
  }

  /**
   * Update local area requirement test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "demand", "database", "couch" }, dependsOnMethods = { "createLocalAreaRequirementTest" })
  public void updateLocalAreaRequirementTest() throws Exception {

    LocalAreaRequirement localAreaRequirement = localAreaRequirementDao
        .findLocalAreaRequirementById(newId);
    localAreaRequirement.setNewArea(20.0);
    localAreaRequirementDao.updateLocalAreaRequirement(localAreaRequirement);
    LocalAreaRequirement localAreaRequirement2 = localAreaRequirementDao
        .findLocalAreaRequirementById(newId);
    Assert.assertEquals(localAreaRequirement2.getNewArea(), 20.0);
  }

  /**
   * Delete local area requirement test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "demand", "database",
      "couch" }, dependsOnMethods = { "updateLocalAreaRequirementTest" })
  public void deleteLocalAreaRequirementTest() throws Exception {

    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(newScenarioId);
    demandScenarioDao.deleteDemandScenario(demandScenario);
    LocalAreaRequirement localAreaRequirement = localAreaRequirementDao
        .findLocalAreaRequirementById(newId);
    localAreaRequirementDao.deleteLocalAreaRequirement(localAreaRequirement);
    LocalAreaRequirement localAreaRequirement2 = localAreaRequirementDao
        .findLocalAreaRequirementById(newId);
    Assert.assertNull(localAreaRequirement2);
  }
}
