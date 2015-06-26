/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.demand;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.demand.DemandProjector;

/**
 * Projects population and area demand in the future according to demographics
 * values.
 * 
 * @author marcosnr
 * 
 */
public class DemandProjectorTest {
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandProjectorTest.class);

  @Test(groups = { "demand" })
  public void projectHouseholdsDemandTest() {

    final Long totalPop = 2500L;
    final Long groupQuartersPop = 250L;
    final Double averageHousehold = 2.5;
    final Double vacancyRate = 0.1;

    final Long projectedHouses = DemandProjector.projectHouseholdsDemand(
        totalPop, groupQuartersPop, averageHousehold);
    Assert.assertEquals(projectedHouses, new Long(900));

    final Long projectedHouseunits = DemandProjector.adjustVacancy(
        projectedHouses, vacancyRate);
    Assert.assertEquals(projectedHouseunits, new Long(1000));

  }

  @Test(groups = { "demand" })
  public void projectedDensityResidentialDemandTest() {

    final Long totalPop = 2500L;
    final Long groupQuartersPop = 250L;
    final Double averageHousehold = 2.5;
    final Double lowResidentialDensity = 2.5;
    final Double vacancyRate = 0.1;
    final Double infillRate = 0.05;
    final Long currentHousingUnits = 800L;

    final Double lowResidentialBreakdown = 0.75;
    LOGGER.debug("testing density for  low residential demand");
    final Double projectedDensityResidentialDemand = DemandProjector
        .projectDensityResidentialDemand(totalPop, groupQuartersPop,
            averageHousehold, lowResidentialDensity, vacancyRate, infillRate,
            currentHousingUnits, lowResidentialBreakdown);

    Assert.assertEquals(projectedDensityResidentialDemand, new Double(57.0));

  }

  @Test(groups = { "demand" })
  public void projectedResidentialDemandTest() {

    // projected Global data in 2010
    final Long projecteTotalPop = 23149L;
    final Long groupQuartersPop = 146L;
    final Double averageHousehold = 2.41;

    // Low density residential information for 2010
    final Double lowResidentialDensity = 1.0;
    final Double vacancyRate = 0.0963;
    final Double infillRate = 0.0;
    final Long currentHousingUnits = 8129L;
    final Double lowResidentialBreakdown = 0.60;
    LOGGER.debug("Testing residential demand");
    final Double projectedDensityResidentialDemand = DemandProjector
        .projectDensityResidentialDemand(projecteTotalPop, groupQuartersPop,
            averageHousehold, lowResidentialDensity, vacancyRate, infillRate,
            currentHousingUnits, lowResidentialBreakdown);
    final DecimalFormat twoDForm = new DecimalFormat("#.##");

    Assert.assertEquals(
        Double.valueOf(twoDForm.format(projectedDensityResidentialDemand)),
        new Double(1459.8));

  }

  @Test(groups = { "demand" })
  public void projectedGroupQuatersDemandTest() {

    final Long currentPopulationGQ = 200L;
    final Double projectedDensity = 20.0;
    final Double vacancyRate = 0.1;
    final Double infillRate = 0.05;
    final Long projectPopulationGQ = 250L;
    LOGGER.debug("testing for group quarters demand");
    final Double projectedDensityDemand = DemandProjector.projectDensityDemand(
        currentPopulationGQ, projectPopulationGQ, vacancyRate, infillRate,
        projectedDensity);

    // Assert.assertEquals(projectedDensityDemand, new
    // Double(2.6599999999999997));

  }

  @Test(groups = { "demand" })
  public void projectedEmploymentDemandTest() {

    final Long employmentBySector = 1000L;
    final Double projectedDensity = 20.0;
    final Double infillRate = 0.05;
    final Long projectedEmploymentBySector = 1500L;
    final Integer numberOfLandUsesPerSector = 1;
    LOGGER.debug("testing for employment demand");
    final Double projectedDensityDemand = DemandProjector
        .projectEmploymentDensityDemand(employmentBySector,
            projectedEmploymentBySector, infillRate, projectedDensity,
            numberOfLandUsesPerSector, 1.0, 1.0);

    // Assert.assertEquals(projectedDensityDemand, new Double(23.75));

  }

  @Test(groups = { "demand" })
  public void projectedEmploymentOneToManyDemandTest() {

    final Long currentSectorEmployment = 5771L;
    final Double projectedDensity = 9.85;
    final Double infillRate = 0.0;
    final Long projectedEmploymentBySector = 6558L;
    final Integer numberOfLandUsesPerSector = 2;
    System.out
        .println("testing for employment demand one to many: retail trade");
    final Double projectedDensityDemand = DemandProjector
        .projectEmploymentDensityDemand(currentSectorEmployment,
            projectedEmploymentBySector, infillRate, projectedDensity,
            numberOfLandUsesPerSector, 1.0, 1.0);

    // Assert.assertEquals(projectedDensityDemand, new
    // Double(39.9492385786802));
  }

  @Test(groups = { "demand" })
  public void projectedEmploymentMany2manyDemandTest() {

    final Long currentSectorEmployment = 10L;
    final Double projectedDensity = 0.02;
    final Double infillRate = 0.0;
    final Long projectedEmploymentBySector = 20L;
    final Integer numberOfLandUsesPerSector = 1;
    System.out
        .println("testing for employment demand many to many: management");
    Double projectedDensityDemand = DemandProjector
        .projectEmploymentDensityDemand(currentSectorEmployment,
            projectedEmploymentBySector, infillRate, projectedDensity,
            numberOfLandUsesPerSector, 1.0, 1.0);
    projectedDensityDemand = projectedDensityDemand / numberOfLandUsesPerSector;
    // Assert.assertEquals(projectedDensityDemand, new Double(500.0));
  }

}
