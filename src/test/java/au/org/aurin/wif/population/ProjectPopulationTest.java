/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.population;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.population.PopulationProjector;

/**
 * jpa access test for What If configurations projects
 * 
 * @author marcosnr
 * 
 */
public class ProjectPopulationTest {
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectPopulationTest.class);

  @Test(enabled = true, groups = { "analysis", "demand" })
  public void projectPopulationTest() {

    Double averageHousehold = 2.5;
    Double currentResidentialDensity = 2.0;
    Double projectedResidentialDensity = 2.5;
    Double vacancyRate = 0.1;
    Double infillRate = 0.05;
    Double unchangedLandUseArea = 300.0;
    Double newLandUseArea = 140.0;
    Double averageHouseHoldSize = 2.4;
    LOGGER.debug("Testing residential project population");
    Double projectResidentialPopulation = PopulationProjector
        .projectResidentialPopulation(unchangedLandUseArea, newLandUseArea,
            averageHousehold, currentResidentialDensity,
            projectedResidentialDensity, vacancyRate, infillRate,
            averageHouseHoldSize);

    Assert.assertEquals(projectResidentialPopulation, new Double(2160.0));

  }

  @Test(enabled = true, groups = { "analysis", "demand" })
  public void projectPopulationQuartersTest() {

    Double currentResidentialDensity = 25.0;
    Double projectedResidentialDensity = 20.0;
    Double unchangedLandUseArea = 10.0;
    Double newLandUseArea = 5.0;

    LOGGER.debug("Testing residential project group quarters population");
    Double projectResidentialPopulation = PopulationProjector
        .projectPopulationGroupQuarters(unchangedLandUseArea, newLandUseArea,
            currentResidentialDensity, projectedResidentialDensity);

    Assert.assertEquals(projectResidentialPopulation, new Double(350.0));

  }

}
