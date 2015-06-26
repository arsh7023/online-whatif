/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.demand;

import java.util.HashSet;

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
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class CreateTrendIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CreateTrendIT extends AbstractTestNGSpringContextTests {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CreateTrendIT.class);

  /** The demand analyzer. */
  @Autowired
  private DemandConfigurator demandConfigurator;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  @Resource
  private DemandConfigService demandConfigService;

  private DemandConfig demandConfig;

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
    Assert.assertNotNull(demandConfig.getId());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getResidentialCurrentData().iterator().next().getResidentialLU());
    Assert.assertNotNull(demandConfig.getCurrentDemographic()
        .getEmploymentCurrentDatas().iterator().next().getSector());
    Assert.assertNotNull(demandConfig.getSectors().iterator().next()
        .getAssociatedLUs().size());
    Assert.assertNotNull(demandConfig.getEmploymentPastTrendInfos().iterator()
        .next().getEmploymentEntries().iterator().next().getSector());
  }

  /**
   * Creates the residential test.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @Test(enabled = true, groups = { "demand" })
  public void createResidentialTest() throws Exception,
      WifInvalidConfigException {
    LOGGER.debug("Creating residential trend Test");
    DemographicTrend dt = new DemographicTrend();
    dt.setLabel("Growth Trend");
    dt.setDemographicData(new HashSet<DemographicData>());
    dt = demandConfigurator
        .createResidentialTrendFromPastData(demandConfig, dt);

    Double populationGrowthRate = demandConfig.getPopulationGrowthRate();
    Assert.assertEquals(populationGrowthRate, 0.08775606478849363);
  }

  /**
   * Creates the employment test.
   * 
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   * 
   *           the wif invalid config exception
   */
  @Test(enabled = true, groups = { "demand" })
  public void createEmploymentTest() throws Exception,
      WifInvalidConfigException {
    LOGGER.debug("Creating employment trend Test");
    DemographicTrend dt = new DemographicTrend();
    dt.setLabel("Growth Trend");
    dt.setDemographicData(new HashSet<DemographicData>());
    dt = demandConfigurator.createEmploymentTrendFromPastData(demandConfig, dt);

    EmploymentSector sectorAgricultureHunting = demandConfig
        .getSectorByLabel("Ag/Forest/Fish/Hunt");
    Double employeeGrowthRate = demandConfig
        .getEmploymentGrowthRate(sectorAgricultureHunting);
    Assert.assertEquals(employeeGrowthRate, -0.05882352941176472);
  }
}
