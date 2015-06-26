/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.demand;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.demand.DemandConfigurator;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class CreateTrendIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CreateAutomatedDemandScenarioIT extends
    AbstractTestNGSpringContextTests {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CreateAutomatedDemandScenarioIT.class);

  /** The demand scenario service. */
  @Autowired
  private DemandScenarioService demandScenarioService;

  /** The demand analyzer. */
  @Autowired
  private DemandConfigurator demandConfigurator;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The demand scenario id. */
  private String demandScenarioId;

  @Resource
  private DemandConfigService demandConfigService;

  private DemandConfig demandConfig;

  @Resource
  private ProjectService projectService;

  private WifProject project;

  /**
   * Load wif project.
   * 
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   * @throws WifInvalidInputException
   */
  @BeforeClass(enabled = true)
  public void loadWifProject() throws WifInvalidConfigException,
      WifInvalidInputException, ParsingException {

    demandConfig = demandConfigService.getDemandConfig(WifKeys.TEST_PROJECT_ID);
    project = projectService.getProject(WifKeys.TEST_PROJECT_ID);

    Assert.assertNotNull(demandConfig.getId());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getResidentialCurrentData().iterator().next().getResidentialLU());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getEmploymentCurrentDatas().iterator().next().getSector());
    Assert.assertNotNull(demandConfig.getSectors().iterator().next()
        .getAssociatedLUs().size());
    Assert.assertNotNull(demandConfig.getEmploymentPastTrendInfos().iterator()
        .next().getEmploymentEntries().iterator().next().getSector());
    Assert.assertNotNull(demandConfig.getDemographicTrends().iterator().next()
        .getDemographicData().size());
    Set<DemographicTrend> trends = demandConfig.getDemographicTrends();
    for (DemographicTrend demographicTrend : trends) {
      LOGGER.info("Checking trends: {}", demographicTrend.getLabel());
      Set<DemographicData> demographicData = demographicTrend
          .getDemographicData();
      for (DemographicData data : demographicData) {
        LOGGER.info("Checking data for projection: {}", data.getProjection()
            .getLabel());
        Assert.assertNotNull(data.getProjection());
        data.setProjection(demandConfig.getProjectionByLabel(data
            .getProjectionLabel()));
        if (data instanceof EmploymentDemographicData) {
          EmploymentDemographicData empData = ((EmploymentDemographicData) data);
          Assert.assertNotNull(empData.getSector());
        }
      }
    }
  }

  /**
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @Test(enabled = true, groups = { "demand" })
  public void createAutomatedScenarioTest() throws Exception,
      WifInvalidConfigException {
    LOGGER.debug("Creating automated demand scenario Test");

    DemandScenario demandScenario = demandConfigurator.createAutomatedScenario(
        demandConfig, project);
    Assert.assertNotNull(demandScenario);
    Assert.assertNotNull(demandScenario.getDemandInfos());

    DemandScenario savedScenario = demandScenarioDao
        .persistDemandScenario(demandScenario);
    demandScenarioId = savedScenario.getId();
    LOGGER.debug("returning the automatedScenario with id={}",
        savedScenario.getId());
    LOGGER.debug("beginning demand analysis...");

    List<AreaRequirement> outcome = demandScenarioService
        .getOutcome(demandScenarioId);
    Assert.assertNotNull(outcome);
    LOGGER.debug("outcome size " + outcome.size());
    Assert.assertEquals(outcome.size(), 20);
  }

}
