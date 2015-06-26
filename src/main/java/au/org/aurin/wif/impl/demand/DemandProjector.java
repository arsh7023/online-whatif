/*
 *
 */
package au.org.aurin.wif.impl.demand;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;

/**
 * <b>DemandProjector.java</b> : includes logic for discovering land demand
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class DemandProjector {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandProjector.class);

  /**
   * Project households demand. See Appendix E of What If manual for detailed
   * functionality
   * 
   * @param totalPop
   *          the total pop
   * @param groupQuartersPop
   *          the group quarters pop
   * @param averageHousehold
   *          the average household
   * @return the long
   */
  public static Long projectHouseholdsDemand(final Long totalPop,
      final Long groupQuartersPop, final Double averageHousehold) {
    Long projectedHouseHoldsPopulation = 0l;
    if (groupQuartersPop != null) {
      projectedHouseHoldsPopulation = totalPop - groupQuartersPop;
    } else {
      projectedHouseHoldsPopulation = totalPop;
    }
    LOGGER.debug("projected HouseHolds Population: {}",
        projectedHouseHoldsPopulation);
    return Math.round(projectedHouseHoldsPopulation / averageHousehold);
  }

  /**
   * Adjust vacancy. See Appendix E of What If manual for detailed functionality
   * 
   * @param projected
   *          the projected
   * @param vacancyRate
   *          the vacancy rate
   * @return the long
   */
  public static Long adjustVacancy(final Long projected,
      final Double vacancyRate) {

    return Math.round(projected / (1 - vacancyRate));
  }

  /**
   * Adjust vacancy. See Appendix E of What If manual for detailed functionality
   * 
   * @param projected
   *          the projected
   * @param vacancyRate
   *          the vacancy rate
   * @return the double
   */
  private static Double adjustDemandVacancy(final Double projected,
      final Double vacancyRate) {
    return projected / (1 - vacancyRate);
  }

  /**
   * Adjust population vacancy. See Appendix E of What If manual for detailed
   * functionality
   * 
   * @param projected
   *          the projected
   * @param vacancyRate
   *          the vacancy rate
   * @return the double
   */
  private static Double adjustPopulationVacancy(final Double projected,
      final Double vacancyRate) {
    return projected * (1 - vacancyRate);
  }

  /**
   * Adjust infill. See Appendix E of What If manual for detailed functionality
   * 
   * @param projectedDensityDemand
   *          the demand
   * @param infillRate
   *          the infill rate
   * @return the double
   */
  public static Double adjustDemandInfill(final Double projectedDensityDemand,
      final Double infillRate) {

    return projectedDensityDemand * (1 - infillRate);
  }

  /**
   * Adjust infill. See Appendix E of What If manual for detailed functionality
   * 
   * @param newHUnits
   *          the demand
   * @param infillRate
   *          the infill rate
   * @return the double
   */
  public static Double adjustPopulationInfill(final Double newHUnits,
      final Double infillRate) {

    return newHUnits / (1 - infillRate);
  }

  /**
   * Project housing units. See Appendix E of What If manual for detailed
   * functionality
   * 
   * @param totalPop
   *          the total pop
   * @param groupQuartersPop
   *          the group quarters pop
   * @param averageHousehold
   *          the average household
   * @param vacancyRate
   *          the vacancy rate
   * @return the long
   */
  public static Long projectHousingUnits(final Long totalPop,
      final Long groupQuartersPop, final Double averageHousehold,
      final Double vacancyRate) {

    final Long projectedHouses = projectHouseholdsDemand(totalPop,
        groupQuartersPop, averageHousehold);
    LOGGER.debug("projected HouseHolds: {}", projectedHouses);

    final Long projectedHouseunits = adjustVacancy(projectedHouses, vacancyRate);
    LOGGER.debug("ProjectedUnits,adjusted with Vacancy: {}",
        projectedHouseunits);

    return projectedHouseunits;
  }

  /**
   * Project density residential demand. See Appendix E of What If manual for
   * detailed functionality
   * 
   * @param totalPop
   *          the total pop
   * @param groupQuartersPop
   *          the group quarters pop
   * @param averageHousehold
   *          the average household
   * @param residentialDensity
   *          the residential density
   * @param vacancyRate
   *          the vacancy rate
   * @param infillRate
   *          the infill rate
   * @param currentHousingUnits
   *          the current housing units
   * @param residentialBreakdown
   *          the residential breakdown
   * @return the double
   */
  public static Double projectDensityResidentialDemand(final Long totalPop,
      final Long groupQuartersPop, final Double averageHousehold,
      final Double residentialDensity, final Double vacancyRate,
      final Double infillRate, final Long currentHousingUnits,
      final Double residentialBreakdown) {

    LOGGER.debug("<<<<<<< assuming the following demographic values...");
    LOGGER.debug("projected Total Population: {}", totalPop);
    LOGGER.debug("projected Group Quarters Population: {}", groupQuartersPop);
    LOGGER.debug("projected Average Household Size: {}", averageHousehold);
    LOGGER.debug("projected Residential Density: {}", residentialDensity);
    LOGGER.debug("projected Vacancy Rate: {}", vacancyRate);
    LOGGER.debug("projected infillRate: {}", infillRate);
    LOGGER.debug(" current Housing Units: {}", currentHousingUnits);
    LOGGER.debug("projected breakdown percentage by housing type: {}",
        residentialBreakdown);

    LOGGER.debug(">>>>>> calculating the following variables...");
    final Long projectedHouseunits = projectHousingUnits(totalPop,
        groupQuartersPop, averageHousehold, vacancyRate);

    final Long newHousingUnits = projectedHouseunits - currentHousingUnits;
    LOGGER.debug("new House Units: {}", newHousingUnits);

    final Double projectedResidentialUnitsByType = newHousingUnits
        * residentialBreakdown;
    LOGGER.debug("breakdown,  new House Units by type: {}",
        projectedResidentialUnitsByType);

    final Double projectedResidentialUnits = adjustDemandInfill(
        projectedResidentialUnitsByType, infillRate);
    LOGGER.debug("area with infilled Adjusted Density Demand: {}",
        projectedResidentialUnits);

    final Double densityDemand = projectedResidentialUnits / residentialDensity;
    LOGGER.debug("area density Demand :  {}", densityDemand);

    return densityDemand;
  }

  /**
   * Project density demand of land in area. See Appendix E of What If manual
   * for detailed functionality
   * 
   * @param currentPopulation
   *          the current populination
   * @param projectPopulation
   *          the project population
   * @param vacancyRate
   *          the vacancy rate
   * @param infillRate
   *          the infill rate
   * @param projectedDensity
   *          the projected density
   * @return the new area for demand
   */
  public static Double projectDensityDemand(final Long currentPopulation,
      final Long projectPopulation, final Double vacancyRate,
      final Double infillRate, final Double projectedDensity) {

    final Long newPopulation = projectPopulation - currentPopulation;
    LOGGER.debug("New Population of {}", newPopulation);
    final Long projectedUnits = adjustVacancy(newPopulation, vacancyRate);
    LOGGER.debug("adjusted with Vacancy Projected Units are: {}",
        projectedUnits);
    final Double projectedDensityDemand = projectedUnits / projectedDensity;
    LOGGER
        .debug("area of projected Density Demand: {}", projectedDensityDemand);

    final Double infilledAdjustedDensityDemand = adjustDemandInfill(
        projectedDensityDemand, infillRate);

    LOGGER.debug("area with infilled Adjusted Density Demand: {}",
        infilledAdjustedDensityDemand);

    return infilledAdjustedDensityDemand;
  }

  /**
   * Project employment density demand.
   * 
   * @param currentEmploymentBySector
   *          the employment by sector
   * @param projectedEmploymentBySector
   *          the projected employment by sector
   * @param infillRate
   *          the infill rate
   * @param employmentDensity
   *          the projected density
   * @param numberOfLandUsesPerSector
   *          the number of land uses per sector
   * @return the double
   */
  public static Double projectEmploymentDensityDemand(
      final Long currentEmploymentBySector,
      final Long projectedEmploymentBySector, final Double infillRate,
      final Double employmentDensity, final Integer numberOfLandUsesPerSector,
      final Double dPercentage, final Double futureDensity) {
    LOGGER
        .debug("<<<<<<< assuming the following employment-sector-projection-land use values...");
    LOGGER.debug("current employment: {}", currentEmploymentBySector);
    LOGGER.debug("projected employment: {}", projectedEmploymentBySector);
    LOGGER.debug("number Of LandUses associated with this Sector: {}",
        numberOfLandUsesPerSector);
    LOGGER.debug("projected Employment Density: {}", employmentDensity);

    LOGGER.debug("projected infillRate: {}", infillRate);
    //
    final Long newPopulation = projectedEmploymentBySector
        - currentEmploymentBySector;
    LOGGER.debug(" New employees : {}", newPopulation);

    final Double projectedDensityDemand = newPopulation / employmentDensity;
    LOGGER
        .debug("area of projected Density Demand: {}", projectedDensityDemand);

    final Double infilledAdjustedDensityDemand = adjustDemandInfill(
        projectedDensityDemand, infillRate);

    LOGGER.debug("area with infilled Adjusted Density Demand: {}",
        infilledAdjustedDensityDemand);

    final Double projectedDensityDemandBySector = infilledAdjustedDensityDemand
        / numberOfLandUsesPerSector;
    LOGGER.debug("adjusted for {} number of LandUses per sectors: {}",
        numberOfLandUsesPerSector, projectedDensityDemandBySector);

    // return projectedDensityDemandBySector;

    // /new claudia
    final double x1 = newPopulation * dPercentage;
    final double x2 = x1 * (1 - infillRate);
    final double x3 = x2 / futureDensity;
    return x3;
    // //

  }

  /**
   * Project employment density demand by sector. See Appendix E of What If
   * manual for detailed functionality
   * 
   * @param currentEmploymentBySector
   *          the current employment by sector
   * @param projectedEmploymentBySector
   *          the projected employment by sector
   * @param infillRate
   *          the infill rate
   * @param employmentDensity
   *          the employment density
   * @param landUseArea
   *          the land use area
   * @return the double
   */
  public static Double projectEmploymentDensityDemandBySector(
      final Long currentEmploymentBySector,
      final Long projectedEmploymentBySector, final Double infillRate,
      Double employmentDensity, final Double landUseArea,
      final Double dPercentage, final Double futureDensityLU) {
    LOGGER
        .debug("<<<<<<< many to many analysis, assuming the following employment-sector-projection-land use values...");
    LOGGER.debug("current employment: {}", currentEmploymentBySector);
    LOGGER.debug("projected employment: {}", projectedEmploymentBySector);
    LOGGER.debug("LandUseArea associated with this Sector: {}", landUseArea);
    LOGGER.debug("projected Employment Density: {}", employmentDensity);

    LOGGER.debug("projected infillRate: {}", infillRate);
    //
    final Long newPopulation = projectedEmploymentBySector
        - currentEmploymentBySector;
    LOGGER.debug(" New employees (growth) : {}", newPopulation);
    employmentDensity = currentEmploymentBySector / landUseArea;
    LOGGER.debug("associated land use employment density: {}",
        employmentDensity);
    final Double projectedDensityDemand = newPopulation / employmentDensity;
    LOGGER.debug("area of projected Density Demand for this LU/sector: {}",
        projectedDensityDemand);

    final Double infilledAdjustedDensityDemand = adjustDemandInfill(
        projectedDensityDemand, infillRate);

    LOGGER.debug("area with infilled Adjusted Density Demand: {}",
        infilledAdjustedDensityDemand);
    // return infilledAdjustedDensityDemand;

    // new claudia
    final double x1 = newPopulation * dPercentage;
    final double x2 = x1 * (1 - infillRate);
    final double x3 = x2 / futureDensityLU;
    return x3;

  }

  /**
   * Project residential population. See Appendix E of What If manual for
   * detailed functionality
   * 
   * @param unchangedLandUseArea
   *          the unchanged land use area
   * @param newLandUseArea
   *          the new land use area
   * @param averageHousehold
   *          the average household
   * @param currentResidentialDensity
   *          the current residential density
   * @param projectedResidentialDensity
   *          the projected residential density
   * @param vacancyRate
   *          the vacancy rate
   * @param infillRate
   *          the infill rate
   * @param averageHouseHoldSize
   *          the average house hold size
   * @return the double
   */
  public static Double projectResidentialPopulation(
      final Double unchangedLandUseArea, final Double newLandUseArea,
      final Double averageHousehold, final Double currentResidentialDensity,
      final Double projectedResidentialDensity, final Double vacancyRate,
      final Double infillRate, final Double averageHouseHoldSize) {

    final Double currentHUnits = unchangedLandUseArea
        * currentResidentialDensity;
    LOGGER.debug(" currentHUnits : {}", currentHUnits);

    final Double projectedHUnits = newLandUseArea * projectedResidentialDensity;
    LOGGER.debug(" projectedHUnits : {}", projectedHUnits);

    final Double newHUnits = currentHUnits + projectedHUnits;
    LOGGER.debug(" newHUnits : {}", newHUnits);

    final Double infilledNnewHUnits = adjustPopulationInfill(newHUnits,
        infillRate);
    LOGGER.debug(" infilledNnewHUnits : {}", infilledNnewHUnits);

    final Double newHouseHolds = adjustPopulationVacancy(infilledNnewHUnits,
        vacancyRate);
    LOGGER.debug(" newHouseHolds : {}", newHouseHolds);

    final Double newPopulation = newHouseHolds * averageHouseHoldSize;
    LOGGER.debug(" newPopulation : {}", newPopulation);

    return newPopulation;
  }

  /**
   * Project population group quarters. See Appendix E of What If manual for
   * detailed functionality
   * 
   * @param unchangedLandUseArea
   *          the unchanged land use area
   * @param newLandUseArea
   *          the new land use area
   * @param currentResidentialDensity
   *          the current residential density
   * @param projectedResidentialDensity
   *          the projected residential density
   * @return the double
   */
  public static Double projectPopulationGroupQuarters(
      final Double unchangedLandUseArea, final Double newLandUseArea,
      final Double currentResidentialDensity,
      final Double projectedResidentialDensity) {

    final Double currentHUnits = unchangedLandUseArea
        * currentResidentialDensity;
    LOGGER.debug(" current : {}", currentHUnits);

    final Double projectedHUnits = newLandUseArea * projectedResidentialDensity;
    LOGGER.debug(" projected : {}", projectedHUnits);

    final Double newGroupQuaters = currentHUnits + projectedHUnits;
    LOGGER.debug(" newGroupQuaters : {}", newGroupQuaters);

    return newGroupQuaters;
  }

  /**
   * Do residential demand area. See Appendix E of What If manual for detailed
   * functionality
   * 
   * @param demandInfo
   *          the demand info
   * @param currentDemographic
   *          the current demographic
   * @param nextDemographic
   *          the next demographic
   * @return the double
   */
  public static Double doResidentialDemandArea(
      final ResidentialDemandInfo demandInfo,
      final ResidentialDemographicData currentDemographic,
      final ResidentialDemographicData nextDemographic) {

    LOGGER.debug(" calculating residential demand area  ");
    final Double demand = DemandProjector.projectDensityResidentialDemand(
        nextDemographic.getTotalPopulation(),
        nextDemographic.getgQPopulation(),
        nextDemographic.getAverageHouseholdSize(),
        demandInfo.getFutureDensity(), demandInfo.getFutureVacancyRate(),
        demandInfo.getInfillRate(), currentDemographic.getHousingUnits(),
        demandInfo.getFutureBreakdownByHType());
    LOGGER
        .debug(
            "projected demand is {}; updating future  housing units for the next cycle... ",
            demand);
    nextDemographic.setHousingUnits(projectHousingUnits(
        nextDemographic.getTotalPopulation(),
        nextDemographic.getgQPopulation(),
        nextDemographic.getAverageHouseholdSize(),
        demandInfo.getFutureVacancyRate()));

    return demand;

  }

  /**
   * Him him him Project employment demographic data.
   * 
   * @param dt
   *          the dt
   * @param projections
   *          the projections
   * @param demandConfig
   *          the demand config
   * @param sectors
   *          the sectors
   * @return the demographic trend
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public static DemographicTrend projectEmploymentDemographicData(
      final DemographicTrend dt, final TreeSet<Projection> projections,
      final DemandConfig demandConfig, final Set<EmploymentSector> sectors)
      throws WifInvalidInputException {

    // // new claudia
    final Projection current = projections.first();
    LOGGER.info("current year projection: {}", current.getLabel());
    final NavigableSet<Projection> projectedSet = projections.tailSet(
        projections.first(), false);
    // end new claudia

    for (final EmploymentSector employmentSector : sectors) {
      LOGGER.debug("******* Current employmentSector: {}",
          employmentSector.getLabel());

      // Sector growth rates
      final double growthRateSector = demandConfig
          .getEmploymentGrowthRate(employmentSector);
      // Base case
      final CurrentDemographic currentDemographic = demandConfig
          .getCurrentDemographic();
      LOGGER.debug("Current year: {}", currentDemographic.getYear());
      Long baseEmployees = currentDemographic.getEmployees(employmentSector);

      // new claudia
      final EmploymentDemographicData projectedDemographicDataFirst = new EmploymentDemographicData();
      projectedDemographicDataFirst.setSector(employmentSector);
      projectedDemographicDataFirst.setEmployees(baseEmployees.intValue());
      projectedDemographicDataFirst.setSector(employmentSector);
      projectedDemographicDataFirst.setSectorLabel(employmentSector.getLabel());
      projectedDemographicDataFirst.setProjection(current);
      projectedDemographicDataFirst.setProjectionLabel(current.getLabel());
      projectedDemographicDataFirst.setDemographicTrend(dt);
      dt.getDemographicData().add(projectedDemographicDataFirst);
      // end new claudia

      // General case
      for (final Projection projection : projectedSet) {
        // for (final Projection projection : projections) { // for claudia
        final EmploymentDemographicData projectedDemographicData = new EmploymentDemographicData();
        LOGGER.info("----> projection year: {}", projection.getLabel());
        LOGGER.debug("Base employees: {}", baseEmployees);
        final Long projectedEmployees = DemandProjector.projectPopulation(
            growthRateSector, baseEmployees);
        LOGGER.debug("Projected total employees: {}", projectedEmployees);
        projectedDemographicData.setEmployees(projectedEmployees.intValue());
        baseEmployees = projectedDemographicData.getEmployees().longValue();
        projectedDemographicData.setSector(employmentSector);
        projectedDemographicData.setSectorLabel(employmentSector.getLabel());
        projectedDemographicData.setProjection(projection);
        projectedDemographicData.setProjectionLabel(projection.getLabel());
        projectedDemographicData.setDemographicTrend(dt);
        dt.getDemographicData().add(projectedDemographicData);
      }
    }
    return dt;
  }

  /**
   * Project demographic data. Based on projection and past trends, FIXME
   * replace with real algorithm.
   * 
   * @param dt
   *          the dt
   * @param projections
   *          the projection
   * @param demandConfig
   *          the demand config
   * @return the demographic data
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public static DemographicTrend projectResidentialDemographicData(
      final DemographicTrend dt, final TreeSet<Projection> projections,
      final DemandConfig demandConfig) throws WifInvalidInputException {
    // Residential growth rates
    final Double growthRatePopulation = demandConfig.getPopulationGrowthRate();
    final Double growthRateGQ = demandConfig.getGqGrowthRate();
    final Double growthRateHouseholds = demandConfig.getHouseholdsGrowthRate();

    // TODO Base case, why do we need the currentDemographicData?
    final CurrentDemographic currentDemographic = demandConfig
        .getCurrentDemographic();

    Long basePopulation = currentDemographic.getTotalPopulation();
    double baseHouseholds = currentDemographic.getHouseholds();
    double baseAvgHHSize = currentDemographic.getAverageHouseholdSize();
    double baseHousingUnits = currentDemographic.getHousingUnits();

    LOGGER.debug("Current year: {}", currentDemographic.getYear());

    // new claudia
    final Projection current = projections.first();
    LOGGER.info("current year projection: {}", current.getLabel());
    final NavigableSet<Projection> projectedSet = projections.tailSet(
        projections.first(), false);
    final ResidentialDemographicData projectedDemographicDataFirst = new ResidentialDemographicData();
    projectedDemographicDataFirst.setTotalPopulation(basePopulation);
    projectedDemographicDataFirst.setHouseholds(baseHouseholds);
    projectedDemographicDataFirst.setHousingUnits((long) baseHousingUnits);
    projectedDemographicDataFirst.setAverageHouseholdSize(baseAvgHHSize);
    projectedDemographicDataFirst.setProjection(current);
    projectedDemographicDataFirst.setProjectionLabel(current.getLabel());
    projectedDemographicDataFirst.setDemographicTrend(dt);
    dt.getDemographicData().add(projectedDemographicDataFirst);
    // end new claudia

    // General case
    for (final Projection projection : projectedSet) {
      // for (final Projection projection : projections) {// for claudia
      final ResidentialDemographicData projectedDemographicData = new ResidentialDemographicData();
      LOGGER.info("----> projection year: {}", projection.getLabel());

      LOGGER.debug("Base population: {}", basePopulation);
      LOGGER.debug("Base households: {}", baseHouseholds);

      LOGGER.debug("Base average household size: {}", baseAvgHHSize);
      LOGGER.debug("Base housing units: {}", baseHousingUnits);

      final Long projectedTotalPopulation = DemandProjector.projectPopulation(
          growthRatePopulation, basePopulation);
      LOGGER.debug("Projected total population: {}", projectedTotalPopulation);

      final Long projectedHouseholds = Math.round((1 + growthRateHouseholds)
          * baseHouseholds);
      LOGGER.debug("Projected households: {}", projectedHouseholds);
      // FIXME FInd out if I apply this projection to housing units
      final Long projectedHousingUnits = Math.round((1 + growthRateHouseholds)
          * baseHousingUnits);
      LOGGER.debug("Projected HousingUnits: {}", projectedHousingUnits);

      projectedDemographicData.setTotalPopulation(projectedTotalPopulation);

      projectedDemographicData.setHouseholds(Double
          .valueOf(projectedHouseholds));
      LOGGER.debug("Projected average household size: {}",
          projectedDemographicData.getAverageHouseholdSize());
      projectedDemographicData.setHousingUnits(projectedHousingUnits);

      // Setting the stage for the next iteration
      basePopulation = projectedDemographicData.getTotalPopulation();
      baseHouseholds = projectedDemographicData.getHouseholds();

      baseHousingUnits = projectedDemographicData.getHousingUnits();
      baseAvgHHSize = projectedDemographicData.getAverageHouseholdSize();
      projectedDemographicData.setProjection(projection);
      projectedDemographicData.setProjectionLabel(projection.getLabel());

      projectedDemographicData.setDemographicTrend(dt);
      dt.getDemographicData().add(projectedDemographicData);
    }
    return dt;
  }

  /**
   * Project growth rate.
   * 
   * @param projectionPeriod
   *          the projection period
   * @param observationPeriod
   *          the observation period
   * @param basePopulation
   *          the base population
   * @param nextPopulation
   *          the next population
   * @return the double
   */
  public static Double projectGrowthRate(final double projectionPeriod,
      final double observationPeriod, final double basePopulation,
      final double nextPopulation) {
    LOGGER.trace("Projection period: {}", projectionPeriod);
    final Double observedGrowthRate = (nextPopulation - basePopulation)
        / basePopulation;
    LOGGER.trace("observedGrowthRate: {}", observedGrowthRate);
    // Rm = (1 + Rn)^(m/n) – 1
    final double periodRate = projectionPeriod / observationPeriod;
    LOGGER.trace("period rate: {}", periodRate);
    final double baseGrowth = 1 + observedGrowthRate;
    LOGGER.trace("baseGrowth: {}", baseGrowth);
    final Double rm = Math.pow(1 + observedGrowthRate, periodRate) - 1;
    // Double rm = Math.pow((1 + observedGrowthRate), periodRate) - 1;
    LOGGER.debug("Projected growth rate: {}", rm);
    return rm;
  }

  /**
   * Project population.
   * 
   * @param rmPopulation
   *          the rm population
   * @param currentPopulation
   *          the current population
   * @return the int
   */
  public static Long projectPopulation(final Double rmPopulation,
      final Long currentPopulation) {
    return Math.round((1 + rmPopulation) * currentPopulation);
  }

}
