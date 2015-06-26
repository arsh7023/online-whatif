/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.demand;

import java.text.DecimalFormat;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.demand.DemandAnalyzer;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandAnalysisData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandSetupData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandTrendData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationSetupData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationSuitabilityData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;

/**
 * Demand High Growth Test for Low Density Res *-1 Employment Regional Retail
 * *-* Employment Office
 *
 * In summary, to perform a demand scenario analysis you must have the following
 * setup (see What if demand assumptions report) : - What If suitability setup
 * complete. - Current demographic data (2005), global to the project. - Need to
 * have a demographic trend with demographic data (population, current density
 * etc) for the projection years, in this case there is information only for
 * 2010 and 2015. This information is global to the project. - Projected data
 * specific to each scenario (like future breakdown of all units and future
 * density). You can differentiate this information from the global information,
 * because in the What If desktop program, it is grayed out and you cannot
 * modify it when you are in the demand scenario specific screen! - This
 * analysis will create an area requirement for each land use in each future
 * projection year.
 *
 *
 * @author marcosnr
 *
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DemandHighGrowthIT extends AbstractTestNGSpringContextTests {

  @Autowired
  private DemandAnalyzer demandAnalyzer;
  /** The areaRequirement dao. */

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandHighGrowthIT.class);

  private WifProject project;

  @Test(enabled = true, groups = { "demand", "integration" })
  public void doDemandAnalysisTest() throws Exception {
    LOGGER.debug("DemandHighGrowthIT");
    project = DemonstrationSetupData.createProject();
    project = DemonstrationSetupData.createSetupModule(project);
    project = DemonstrationSuitabilityData.createSuitabilityModule(project);
    project = DemonstrationDemandSetupData.createDemandSetupModule(project);
    project = DemonstrationDemandTrendData.createDemandTrendModule(project);
    project = DemonstrationDemandAnalysisData
        .createDemandAnalysisModule(project);

    boolean result = false;
    boolean result2 = false;
    boolean result3 = false;
    boolean result4 = false;

    DemandScenario demandScenario = project
        .getDemandScenarioByLabel("High Growth");
    demandAnalyzer.doDemandAnalysis(demandScenario);
    AllocationLU resLU = project.getExistingLandUseByLabel("Low Density Res.");
    Set<AreaRequirement> areaRequirements = resLU.getAreaRequirements();
    for (AreaRequirement areaRequirement : areaRequirements) {
      DecimalFormat twoDForm = new DecimalFormat("#");
      LOGGER.debug("required area " + areaRequirement.getRequiredArea());
      if (areaRequirement.getProjection().getLabel().equals("2010")) {
        System.out
            .println("testing required area for low residential demand in 2010 = "
                + areaRequirement.getRequiredArea());
        Double area = Double.valueOf(twoDForm.format(areaRequirement
            .getRequiredArea()));
        if ((area <= new Double(1460.0)) && (area >= new Double(1459.0))) {
          LOGGER.debug("valid range");
          result = true;
        }
      }
    }
    LOGGER.debug("testing for *-1 employment...");
    AllocationLU empLU = project.getExistingLandUseByLabel("Regional Retail");
    Set<AreaRequirement> areaRequirementsEmp = empLU.getAreaRequirements();
    for (AreaRequirement empAR : areaRequirementsEmp) {
      DecimalFormat twoDForm = new DecimalFormat("#");
      LOGGER.debug("required area " + empAR.getRequiredArea());
      if (empAR.getProjection().getLabel().equals("2010")) {
        System.out
            .println("testing required area for industrial demand in 2010 = "
                + empAR.getRequiredArea());
        Double area = Double.valueOf(twoDForm.format(empAR.getRequiredArea()));
        LOGGER.debug("formatted area " + area);
        if ((area <= new Double(40.0)) && (area >= new Double(39.0))) {
          LOGGER.debug("valid range");
          result2 = true;
        }
      }
    }
    LOGGER.debug("testing for *-* employment...");
    AllocationLU empLU2 = project.getExistingLandUseByLabel("Office");
    Set<AreaRequirement> areaRequirementsEmp2 = empLU2.getAreaRequirements();
    for (AreaRequirement empAR2 : areaRequirementsEmp2) {
      DecimalFormat twoDForm = new DecimalFormat("#");
      LOGGER.debug("required area " + empAR2.getRequiredArea());
      if (empAR2.getProjection().getLabel().equals("2010")) {
        System.out
            .println("testing required area for industrial demand in 2010 = "
                + empAR2.getRequiredArea());
        Double area = Double.valueOf(twoDForm.format(empAR2.getRequiredArea()));
        LOGGER.debug("formatted area " + area);
        if ((area <= new Double(51.0)) && (area >= new Double(50.0))) {
          LOGGER.debug("valid range");
          result3 = true;
        }
      }
    }
    LOGGER.debug("testing for local requirement...");
    AllocationLU localLU = project.getExistingLandUseByLabel("Parks & Rec.");

    Set<AreaRequirement> areaRequirementsLocal = localLU.getAreaRequirements();
    for (AreaRequirement localAR : areaRequirementsLocal) {
      DecimalFormat twoDForm = new DecimalFormat("#");
      LOGGER.debug("required area " + localAR.getRequiredArea());
      if (localAR.getProjection().getLabel().equals("2010")) {
        LOGGER.debug("testing required area for local demand in 2010 = "
            + localAR.getRequiredArea());
        Double area = Double
            .valueOf(twoDForm.format(localAR.getRequiredArea()));
        LOGGER.debug("formatted area " + area);
        if ((area <= new Double(65.0)) && (area >= new Double(64.0))) {
          LOGGER.debug("valid range");
          result4 = true;
        }
      }
    }
    LOGGER.debug("Result for residential requirement... {}", result);
    LOGGER.debug("Result for 1-* requirement... {}", result2);
    LOGGER.debug("Result for *-* requirement... {}", result3);
    LOGGER.debug("Result for local requirement... {}", result4);

    Assert.assertTrue(result);
    Assert.assertTrue(result2);
    Assert.assertTrue(result3);
    Assert.assertTrue(result4);
  }
}
