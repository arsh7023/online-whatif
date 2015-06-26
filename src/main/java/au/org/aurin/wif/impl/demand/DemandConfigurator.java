/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.config.ProjectAnalyzer;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.TrendComparator;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentEntry;
import au.org.aurin.wif.model.demand.EmploymentGrowthRate;
import au.org.aurin.wif.model.demand.EmploymentPastTrendInfo;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.ResidentialPastTrendInfo;
import au.org.aurin.wif.model.demand.data.PreservedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemandConfigurator.
 */
@Component
public class DemandConfigurator {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 21342673455379L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandConfigurator.class);

  /** The required area analyzer. */
  @Autowired
  private RequiredAreaAnalyzer requiredAreaAnalyzer;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The project analyzer. */
  @Autowired
  private ProjectAnalyzer projectAnalyzer;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service successfully cleared! ");
  }

  /**
   * Creates the automated trends. PRE: User deefined UAZ columns names,
   * projections//TODO and employment sectors information.
   * 
   * @param demandConfig
   *          the demand config
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public DemandConfig createAutomatedTrends(final DemandConfig demandConfig)
      throws WifInvalidInputException, WifInvalidConfigException,
      IncompleteDemandConfigException {
    LOGGER.info("createAutomatedTrends for project {}",
        demandConfig.getProjectId());
    DemographicTrend dt = new DemographicTrend();
    dt.setLabel(WifKeys.DEFAULT_AUTOMATIC_TREND_NAME);
    LOGGER.info("creating trend {}", dt.getLabel());
    dt.setDemographicData(new HashSet<DemographicData>());
    try {

      dt = this.createResidentialTrendFromPastData(demandConfig, dt);
      // TODO not yet supported
      dt = this.createEmploymentTrendFromPastData(demandConfig, dt);
      demandConfig.getDemographicTrends().add(dt);
      return demandConfig;
    } catch (final IncompleteDemandConfigException e) {
      LOGGER.error("createAutomatedTrends failed!");
      throw e;
    } catch (final WifInvalidInputException e) {
      final String msg = "createAutomatedTrends failed!";
      LOGGER.error(msg);
      throw new IncompleteDemandConfigException(msg, e);

    }

  }

  /**
   * Fill basic demand info.
   * 
   * @param demandConfig
   *          the demand config
   * @param uazTbl
   *          the uaz tbl
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public DemandConfig fillBasicDemandInfo(final DemandConfig demandConfig,
      final String uazTbl) throws WifInvalidInputException,
      WifInvalidConfigException, IncompleteDemandConfigException {
    LOGGER.info(
        "fillBasicDemandInfo, beginning Demand configuration for project : {}",
        demandConfig.getProjectId());
    // Updating current demographic from setup information
    final CurrentDemographic currentDemographic = demandConfig
        .getCurrentDemographic();
    final Integer baseYear = demandConfig.getBaseYear();
    currentDemographic.setLabel(baseYear.toString());
    currentDemographic.setYear(baseYear);
    LOGGER.info("Base year: {}", baseYear);

    final Set<Projection> projections = demandConfig.getProjections();
    // Filling up automated information
    for (final Projection projection : projections) {
      if (projection.getLabel() == null) {
        projection.setLabel(projection.getYear().toString());
      }
    }

    if (demandConfig.getCurrentDemographic().getTotalPopulation() == null) {
      LOGGER
          .debug(
              "Configuring demand information from union geospatial information: {}",
              uazTbl);

      final String popFfName = demandConfig
          .getTotalPopulationFeatureFieldName();
      final String clippedEdAreaFfName = demandConfig
          .getClippedEnumerationDistrictAreaFeatureFieldName();
      final String edAreaFfName = demandConfig
          .getEnumerationDistrictAreaFeatureFieldName();
      final String housingUnitsFfName = demandConfig
          .getNumberOfHousingUnitsFeatureFieldName();
      final String householdsFfName = demandConfig
          .getNumberOfHouseholdsFeatureFieldName();

      final long totalPopulation = Math.round(geodataFinder
          .getNormalisedSumOfDistinctEntriesUAZAttribute(uazTbl, popFfName,
              clippedEdAreaFfName, edAreaFfName));
      final long totalHousingUnits = Math.round(geodataFinder
          .getNormalisedSumOfDistinctEntriesUAZAttribute(uazTbl,
              housingUnitsFfName, clippedEdAreaFfName, edAreaFfName));
      final long totalHouseholds = Math.round(geodataFinder
          .getNormalisedSumOfDistinctEntriesUAZAttribute(uazTbl,
              householdsFfName, clippedEdAreaFfName, edAreaFfName));

      demandConfig.getCurrentDemographic().setTotalPopulation(totalPopulation);
      demandConfig.getCurrentDemographic().setHousingUnits(totalHousingUnits);
      demandConfig.getCurrentDemographic().setHouseholds(
          Double.valueOf(totalHouseholds));
      final long vacantHousingUnits = totalHousingUnits - totalHouseholds;
      LOGGER.info("vacantHousingUnits: {}", vacantHousingUnits);
      demandConfig.getCurrentDemographic().setVacantLand(
          (double) vacantHousingUnits);
    }
    LOGGER.info("totalPopulation: {}", demandConfig.getCurrentDemographic()
        .getTotalPopulation());
    LOGGER.info("totalHousingUnits: {}", demandConfig.getCurrentDemographic()
        .getHousingUnits());
    LOGGER.info("totalHouseholds: {}", demandConfig.getCurrentDemographic()
        .getHouseholds());

    return demandConfig;

  }

  /**
   * Creates the past trends.
   * 
   * @param wifProject
   *          the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public void createPastTrends(final WifProject wifProject)
      throws WifInvalidInputException, WifInvalidConfigException,
      IncompleteDemandConfigException {
    LOGGER.info("Including trends? : {}", wifProject.getIncludeTrends());
    final DemandConfig demandConfig = wifProject.getDemandConfig();
    try {
      if (demandConfig.getCurrentDemographic().getResidentialCurrentData() != null
          && demandConfig.getCurrentDemographic().getEmploymentCurrentDatas() != null) {
        // Calculating automated trends
        if (wifProject.getIncludeTrends()) {
          DemographicTrend dt = new DemographicTrend();
          dt.setLabel(WifKeys.DEFAULT_AUTOMATIC_TREND_NAME);
          LOGGER.info("Creating trend : {}", dt.getLabel());

          dt.setDemographicData(new HashSet<DemographicData>());

          dt = createResidentialTrendFromPastData(demandConfig, dt);

          dt = createEmploymentTrendFromPastData(demandConfig, dt);
          dt.setWifProject(wifProject);
          if (wifProject.getDemographicTrends() == null) {
            final Set<DemographicTrend> dtrends = new HashSet<DemographicTrend>();

            dtrends.add(dt);
          }
          wifProject.getDemographicTrends().add(dt);
        }
      } else {
        final String msg = "No current demopraphic data exists:";
        LOGGER.error(msg);
        throw new IncompleteDemandConfigException(msg);
      }
    } catch (final IncompleteDemandConfigException e) {
      throw e;
    }
  }

  /**
   * Creates the residential trend from past data.
   * 
   * @param demandConfig
   *          the demand config
   * @param dt
   *          the dt
   * @return the demographic trend
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public DemographicTrend createResidentialTrendFromPastData(
      DemandConfig demandConfig, DemographicTrend dt)
      throws WifInvalidInputException, WifInvalidConfigException,
      IncompleteDemandConfigException {

    final Set<ResidentialPastTrendInfo> pastTrends = demandConfig
        .getResidentialPastTrendInfos();
    if (pastTrends == null) {
      throw new IncompleteDemandConfigException(
          "There are no past trends for this project!");
    }
    if (pastTrends.size() == 0) {
      throw new IncompleteDemandConfigException(
          "There are no past trends for this project!");
    }
    LOGGER.info(
        "Creating residential projected trend: {} from {} past trends : ",
        dt.getLabel(), pastTrends.size());
    for (final ResidentialPastTrendInfo pastTrend : pastTrends) {
      LOGGER.info("Past trend : {}", pastTrend.getLabel());
    }

    final TreeSet<ResidentialPastTrendInfo> pastTrendsSorted = new TreeSet<ResidentialPastTrendInfo>(
        new TrendComparator());
    pastTrendsSorted.addAll(pastTrends);
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    demandConfig = analyseResidentialGrowthTrend(demandConfig,
        pastTrendsSorted, projections);
    dt = DemandProjector.projectResidentialDemographicData(dt, projections,
        demandConfig);
    return dt;
  }

  /**
   * Creates the employment trend from past data.
   * 
   * @param demandConfig
   *          the demand config
   * @param dt
   *          the dt
   * @return the demographic trend
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  public DemographicTrend createEmploymentTrendFromPastData(
      DemandConfig demandConfig, DemographicTrend dt)
      throws WifInvalidInputException, WifInvalidConfigException,
      IncompleteDemandConfigException {
    final Set<EmploymentPastTrendInfo> pastTrends = demandConfig
        .getEmploymentPastTrendInfos();

    if (pastTrends == null) {
      throw new WifInvalidConfigException(
          "There are no past employment trends for this project!");
    }
    if (pastTrends.size() == 0) {
      throw new WifInvalidConfigException(
          "There are no past employment trends for this project!");
    }
    LOGGER.info(
        "Creating employment projected trend: {} from {} past trends : ",
        dt.getLabel(), pastTrends.size());
    for (final EmploymentPastTrendInfo pastTrend : pastTrends) {
      LOGGER.info("Past trend : {}", pastTrend.getLabel());
    }
    final TreeSet<EmploymentPastTrendInfo> trends = new TreeSet<EmploymentPastTrendInfo>(
        new TrendComparator());
    trends.addAll(pastTrends);
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    demandConfig.setEmploymentGrowthRates(new HashSet<EmploymentGrowthRate>());
    demandConfig = analyseEmploymentGrowthTrend(demandConfig, trends,
        projections, demandConfig.getSectors());
    dt = DemandProjector.projectEmploymentDemographicData(dt, projections,
        demandConfig, demandConfig.getSectors());
    // dt.setWifProject(demandConfig);
    return dt;
  }

  /**
   * Analyse employment growth trend.
   * 
   * @param demandConfig
   *          the demand config
   * @param trends
   *          the trends
   * @param projections
   *          the projections
   * @param sectors
   *          the sectors
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  private DemandConfig analyseEmploymentGrowthTrend(
      final DemandConfig demandConfig,
      final TreeSet<EmploymentPastTrendInfo> trends,
      final TreeSet<Projection> projections, final Set<EmploymentSector> sectors)
      throws WifInvalidInputException, IncompleteDemandConfigException {
    final EmploymentPastTrendInfo baseTrend = trends.first();
    final EmploymentPastTrendInfo nextTrend = trends.higher(trends.first());
    final Integer observationPeriod = Math.abs(baseTrend.getYear()
        - trends.higher(trends.first()).getYear());
    LOGGER.info("Observation period: {}", observationPeriod);
    // Projection period (2010 - 2005 = 5)
    final Integer projectionPeriod = Math.abs(projections.higher(
        projections.first()).getYear()
        - projections.first().getYear());
    LOGGER.info("Projection period: {}", projectionPeriod);

    for (final EmploymentSector sector : sectors) {
      EmploymentEntry baseEmploymentEntry;
      try {
        baseEmploymentEntry = baseTrend.getEmploymentEntryBySector(sector);

        final EmploymentEntry nextEmploymentEntry = nextTrend
            .getEmploymentEntryBySector(sector);

        // Get projected total population growth rate
        final Double growthRate = DemandProjector.projectGrowthRate(
            projectionPeriod, observationPeriod,
            baseEmploymentEntry.getEmployees(),
            nextEmploymentEntry.getEmployees());
        LOGGER.info("Projected growth rate for the sector: {}, is: {} ",
            sector.getLabel(), growthRate);
        final EmploymentGrowthRate employmentGrowthRate = new EmploymentGrowthRate();
        employmentGrowthRate.setDemandConfig(demandConfig);
        employmentGrowthRate.setSector(sector);
        employmentGrowthRate.setSectorLabel(sector.getLabel());
        employmentGrowthRate.setGrowthRate(growthRate);
        demandConfig.getEmploymentGrowthRates().add(employmentGrowthRate);

      } catch (final IncompleteDemandConfigException e) {
        LOGGER.error("analyseEmploymentGrowthTrend failed: {}", e.getMessage());
        throw e;

      }
    }

    return demandConfig;
  }

  /**
   * Analyse residential growth trend.
   * 
   * @param demandConfig
   *          the demand config
   * @param pastTrends
   *          the trends
   * @param projections
   *          the projections
   * @return the demand config
   */
  private DemandConfig analyseResidentialGrowthTrend(
      final DemandConfig demandConfig,
      final TreeSet<ResidentialPastTrendInfo> pastTrends,
      final TreeSet<Projection> projections) {
    // Observation period
    LOGGER.info("analyseResidentialGrowthTrend:number of past trends {}",
        pastTrends.size());
    final ResidentialPastTrendInfo baseTrend = pastTrends.first();
    final ResidentialPastTrendInfo nextTrend = pastTrends.higher(pastTrends
        .first());
    final Integer observationPeriod = Math.abs(baseTrend.getYear()
        - pastTrends.higher(pastTrends.first()).getYear());
    LOGGER.info("Observation period: {}", observationPeriod);
    // Projection period (2010 - 2005 = 5)
    final Integer projectionPeriod = Math.abs(projections.higher(
        projections.first()).getYear()
        - projections.first().getYear());
    LOGGER.info("Projection period: {}", projectionPeriod);

    // Get projected total population growth rate
    final Double growthRatePopulation = DemandProjector.projectGrowthRate(
        projectionPeriod, observationPeriod, baseTrend.getTotalPopulation(),
        nextTrend.getTotalPopulation());
    LOGGER.info("Projected growth rate for the total population: {}",
        growthRatePopulation);
    demandConfig.setPopulationGrowthRate(growthRatePopulation);

    // Get projected GQ population growth rate
    final Double growthRateGQ = DemandProjector.projectGrowthRate(
        projectionPeriod, observationPeriod, baseTrend.getgQPopulation(),
        nextTrend.getgQPopulation());
    LOGGER.info("Projected growth rate for GQ population: {}", growthRateGQ);
    demandConfig.setGqGrowthRate(growthRateGQ);

    // Get projected households growth rate
    final Double growthRateHouseholds = DemandProjector.projectGrowthRate(
        projectionPeriod, observationPeriod, baseTrend.getHouseholds(),
        nextTrend.getHouseholds());
    LOGGER.info("Projected growth rate for households: {}",
        growthRateHouseholds);
    demandConfig.setHouseholdsGrowthRate(growthRateHouseholds);
    return demandConfig;
  }

  /**
   * Creates the automated scenario.
   * 
   * @param demandConfig
   *          the demand config
   * @param project
   *          the project
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandScenario createAutomatedScenario(
      final DemandConfig demandConfig, final WifProject project)
      throws WifInvalidInputException {
    final DemandScenario automatedDemandScenario = new DemandScenario();
    LOGGER.info("createAutomatedScenario for : {}", project.getLabel());
    final DemographicTrend demographicTrend = demandConfig
        .getTrendByLabel(WifKeys.DEFAULT_AUTOMATIC_TREND_NAME);

    automatedDemandScenario
        .setFeatureFieldName(WifKeys.DEFAULT_DEMAND_SCENARIO_NAME);
    automatedDemandScenario.setLabel(WifKeys.DEFAULT_DEMAND_SCENARIO_NAME);
    automatedDemandScenario.setWifProject(project);
    automatedDemandScenario.setProjectId(project.getId());
    automatedDemandScenario.setDemographicTrend(demographicTrend);
    automatedDemandScenario
        .setDemographicTrendLabel(WifKeys.DEFAULT_AUTOMATIC_TREND_NAME);

    LOGGER.info("Using trend: {}", demographicTrend.getLabel());
    // FIXME ccreation of demand information is necessary only for employment
    // calculations?
    // The information to create Current residential demographic data and
    // employment
    // demographic data, is specified in the interface.
    // To calculate the density, you have to refer to the documentation
    // Vacant HU = Number of Housing Units – Number of Households
    // Vacancy Rate = Vacant HU / Total HU
    // Density = Area / Number of Housing Units
    // Breakdown/Density = Number of HU / Total number of HU (this total is for
    // all residential, ie low res, med res, mixed use)
    // Average Household Size = Population / Number of Households

    final Double residentialDensity = 1.0;
    final Double residentialInfillRate = 0.0;
    final Double residentialBreakdownBType = 0.60;
    final Double reservedArea = 500.0;

    final Double vacancyRate = demandConfig.getCurrentDemographic()
        .getVacancyRate();
    final Set<Projection> projections = demandConfig.getProjections();

    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.isNewPreservation()) {
        // conservation information demand
        final PreservationDemandInfo preservedDInfo = new PreservationDemandInfo();
        preservedDInfo.setDemandScenario(automatedDemandScenario);
        automatedDemandScenario.getDemandInfos().add(preservedDInfo);
        preservedDInfo.setAllocationLUId(allocationLU.getId());

        for (final Projection projection : projections) {
          final PreservedData preservedData = new PreservedData();
          preservedData.setReservedArea(reservedArea);
          preservedData.setProjection(projection);
          preservedData.setProjectionLabel(projection.getLabel());
          preservedDInfo.addProjectedData(preservedData);
        }
      } else if (allocationLU.isResidentialLU()) {
        final ResidentialDemandInfo rdinfo = new ResidentialDemandInfo();
        rdinfo.setInfillRate(residentialInfillRate);
        //
        rdinfo.setFutureBreakdownByHType(residentialBreakdownBType);
        rdinfo.setCurrentDensity(residentialDensity);
        rdinfo.setFutureDensity(residentialDensity);
        //
        rdinfo.setFutureVacancyRate(vacancyRate);
        rdinfo.setResidentialLU(allocationLU);
        rdinfo.setResidentialLUId(allocationLU.getId());
        rdinfo.setDemandScenario(automatedDemandScenario);
        automatedDemandScenario.getDemandInfos().add(rdinfo);

        // Associate residential land use demand information
        final Set<DemandInfo> rsDemandInfos = new HashSet<DemandInfo>();
        allocationLU.setDemandInfos(rsDemandInfos);
        allocationLU.addDemandInfo(rdinfo);
      }
    }
    return automatedDemandScenario;
  }
}
