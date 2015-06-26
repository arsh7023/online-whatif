/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io.demand;

import java.text.DecimalFormat;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.parsers.DemandScenarioCouchParser;
import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.ProjectedDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class CouchDB2DemandAnalysisIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchDB2DemandAnalysisIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The demand setup parser. */
  @Autowired
  private DemandSetupCouchParser demandSetupParser;

  /** The demand scenario parser. */
  @Autowired
  private DemandScenarioCouchParser demandScenarioParser;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDB2DemandAnalysisIT.class);

  /**
   * Parses the demand config test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "database", "couchdb" })
  public void parseDemandConfigTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(WifKeys.TEST_PROJECT_ID);
    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(project
        .getDemandConfigId());

    project = projectParser.parse(project);
    demandConfig = demandSetupParser.parse(demandConfig, project);
    Assert.assertNotNull(demandConfig.getId());
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
   * Parses the demand scenario test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "demand", "database", "couchdb" })
  public void parseDemandScenarioTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(WifKeys.TEST_PROJECT_ID);
    DemandConfig demandConfig = demandConfigDao.findDemandConfigById(project
        .getDemandConfigId());
    DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(WifKeys.TEST_DEMAND_SCENARIO_ID);
    project = projectParser.parse(project);
    demandConfig = demandSetupParser.parse(demandConfig, project);
    demandScenario = demandScenarioParser.parse(demandScenario, demandConfig,
        project);
    demandScenario = demandScenarioParser.parseAreaRequirements(demandScenario);
    Assert.assertNotNull(demandConfig.getId());
    Assert.assertNotNull(demandScenario);
    Assert.assertNotNull(demandScenario.getDemandInfos());
    Set<DemandInfo> demandInfos = demandScenario.getDemandInfos();
    for (DemandInfo demandInfo : demandInfos) {
      if (demandInfo instanceof ResidentialDemandInfo) {
        ResidentialDemandInfo resDInfo = ((ResidentialDemandInfo) demandInfo);
        Assert.assertNotNull(resDInfo.getResidentialLU());

      } else if (demandInfo instanceof ProjectedDemandInfo) {
        ProjectedDemandInfo proDInfo = ((ProjectedDemandInfo) demandInfo);
        Set<ProjectedData> datas = proDInfo.getProjectedDatas();
        for (ProjectedData data : datas) {
          Assert.assertNotNull(data.getProjection());
        }
        if (demandInfo instanceof EmploymentDemandInfo) {
          EmploymentDemandInfo eDInfo = ((EmploymentDemandInfo) demandInfo);
          Assert.assertNotNull(eDInfo.getSector());

        } else { // PreservationDemandInfo
          Assert.assertNotNull(demandInfo);

        }
      }
    }
    Set<LocalData> localDatas = demandScenario.getLocalDatas();
    for (LocalData localData : localDatas) {
      Assert.assertNotNull(localData.getLocalJurisdiction());
    }
    // Checking allocation demand info relationship
    Set<AllocationLU> allocationLandUses = project.getAllocationLandUses();
    for (AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.isEmploymentLU()) {
        Set<DemandInfo> infos = allocationLU.getDemandInfos();
        for (DemandInfo demandInfo : infos) {
          Assert.assertNotNull((demandInfo));
          LOGGER.trace("Found EmploymentDemandInfo for {}",
              ((EmploymentDemandInfo) demandInfo).getSector().getLabel());
        }
        if (allocationLU.getLabel().equals("Low Density Res.")) {
          Assert.assertNotEquals(allocationLU.getDemandInfos().size(), 0);
        }
      }
    }
    // Checking demographic trends
    DemographicTrend demographicTrend = demandScenario.getDemographicTrend();
    Assert.assertNotNull(demographicTrend);
    // Checking area requirements
    boolean result = false;
    AllocationLU resLU = project.getExistingLandUseByLabel("Low Density Res.");
    Set<AreaRequirement> areaRequirements = resLU.getAreaRequirements();
    for (AreaRequirement areaRequirement : areaRequirements) {
      DecimalFormat twoDForm = new DecimalFormat("#");
      LOGGER.debug("required area " + areaRequirement.getRequiredArea());
      if (areaRequirement.getProjection().getLabel().equals("2010")) {
        LOGGER.trace(
            "testing required area for low residential demand in 2010 = {} ",
            areaRequirement.getRequiredArea());
        Double area = Double.valueOf(twoDForm.format(areaRequirement
            .getRequiredArea()));
        if ((area <= new Double(1460.0)) && (area >= new Double(1459.0))) {
          LOGGER.trace("valid range");
          result = true;
        }
      }
    }
    Assert.assertTrue(result);
  }
}
