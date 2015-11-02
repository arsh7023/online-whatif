package au.org.aurin.wif.impl.report.allocation;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.AllocationAnalyzer;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleAnalysisReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleItemReport;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

@Service
@Qualifier("allocationReporter")
public class AllocationReporter {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1363346734533L;

  /** The allocation analyzer. */
  @Autowired
  private AllocationAnalyzer allocationAnalyzer;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationReporter.class);

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand config service. */

  /** The Allocation config dao. */
  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.report.ReportService#getAllocationAnalysisReport(
   * au.org.aurin.wif.model.allocation.AllocationScenario)
   */
  public AllocationAnalysisReport getAllocationAnalysisReport(
      final AllocationScenario allocationScenario)
          throws WifInvalidInputException, WifInvalidConfigException,
          ParsingException {
    LOGGER.info("getAllocationAnalysisReport for: {}",
        allocationScenario.getLabel());
    final AllocationAnalysisReport allocationAnalysisReport = new AllocationAnalysisReport();

    final WifProject project = allocationScenario.getWifProject();
    final AllocationConfig allocationConfig = project.getAllocationConfig();
    final String projectId = allocationScenario.getProjectId();
    allocationAnalysisReport.setReportType(allocationScenario.getDocType());
    allocationAnalysisReport.setLabel(project.getName());
    allocationAnalysisReport.setScenarioLabel(allocationScenario.getDocType());
    allocationAnalysisReport.setProjectId(allocationScenario.getProjectId());

    // Getting land use information
    LOGGER.info("Getting land use information: {}");
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(projectId);
    LOGGER.info("Associated demandConfigId: {}", demandConfig.getId());
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    final Projection current = projections.first();
    LOGGER.info("current year projection: {}", current.getLabel());
    // setting up taking into account current projection year is not a
    // projection by itself,per se,
    final NavigableSet<Projection> projectedSet = projections.tailSet(
        projections.first(), false);
    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.getLabel() == null) {
        LOGGER.warn("Not performing analysis for null label for LU: {}",
            allocationLU.getId());
        continue;
      }
      for (final Projection projection : projectedSet) {
        LOGGER.info("getAreaByLU for: {}, id {}", allocationLU.getLabel(),
            allocationLU.getId());
        final String allocationFFName = allocationConfig
            .getAllocationColumnsMap().get(projection.getLabel());

        final Double areaByLU = geodataFinder.getAreaByLU(project
            .getSuitabilityConfig().getUnifiedAreaZone(), project
            .getAreaLabel(), project.getExistingLUAttributeName(), allocationLU
            .getFeatureFieldName(), allocationFFName, allocationLU
            .getAllocationFeatureFieldName());
        final AreaRequirement ar = new AreaRequirement();
        ar.setAllocationLU(allocationLU);
        ar.setRequiredArea(areaByLU);
        ar.setProjection(projection);
        allocationAnalysisReport.getLandUseInformation().add(ar);
      }
    }
    // Getting population and employment information
    LOGGER.info("Getting population and employment information: {}");
    final DemographicTrend demographicTrend = demandConfig
        .getTrendByLabel(allocationScenario.getDemandScenario()
            .getDemographicTrendLabel());
    final Set<DemographicData> demographicData = demographicTrend
        .getDemographicData();
    for (final DemographicData data : demographicData) {
      if (data instanceof ResidentialDemographicData) {
        allocationAnalysisReport.getPopulationInformation().add(
            (ResidentialDemographicData) data);
      } else if (data instanceof EmploymentDemographicData) {
        allocationAnalysisReport.getEmploymentInformation().add(
            (EmploymentDemographicData) data);
      }
    }
    LOGGER.info("Finished allocationAnalysisReport for: {}",
        allocationAnalysisReport.getProjectId());
    return allocationAnalysisReport;
  }

  public AllocationSimpleAnalysisReport getAllocationSimpleAnalysisReport(
      final AllocationScenario allocationScenario)
          throws WifInvalidInputException, WifInvalidConfigException,
          ParsingException {
    LOGGER.info("getAllocationSimpleAnalysisReport for: {}",
        allocationScenario.getLabel());
    final AllocationSimpleAnalysisReport allocationSimpleAnalysisReport = new AllocationSimpleAnalysisReport();

    // final WifProject project = allocationScenario.getWifProject();

    final String projectId = allocationScenario.getProjectId();
    final WifProject project = projectService.getProject(projectId);

    final String AllocationConfigsId = project.getAllocationConfigsId();

    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    allocationSimpleAnalysisReport.setReportType(allocationScenario
        .getDocType());
    allocationSimpleAnalysisReport.setLabel(project.getName());
    allocationSimpleAnalysisReport.setScenarioLabel(allocationScenario
        .getDocType());
    allocationSimpleAnalysisReport.setProjectId(allocationScenario
        .getProjectId());

    final Set<AllocationSimpleItemReport> setallocationSimpleItemReport = new HashSet<AllocationSimpleItemReport>();

    // Getting land use information
    // LOGGER.info("Getting land use information: {}");
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(projectId);
    // LOGGER.info("Associated manualdemandConfigId: {}",
    // manualdemandConfig.getId());
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(demandConfig.getProjections());
    final Projection current = projections.first();
    LOGGER.debug("current year projection: {}", current.getLabel());
    // setting up taking into account current projection year is not a
    // projection by itself,per se,
    // NavigableSet<Projection> projectedSet = projections.tailSet(
    // projections.first(), false);
    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.getLabel() == null) {
        LOGGER.warn("Not performing analysis for null label for LU: {}",
            allocationLU.getId());
        continue;
      }
      for (final Projection projection : projections) {
        LOGGER.debug("getAreaByLU for: {}, id {}", allocationLU.getLabel(),
            allocationLU.getId());
        final String allocationFFName = allocationConfig
            .getAllocationColumnsMap().get(projection.getLabel());

        //        final Double areaByLU = geodataFinder.getAreaByLUNew(project
        //            .getSuitabilityConfig().getUnifiedAreaZone(), project
        //            .getAreaLabel(), allocationFFName, WifKeys.FUTURELU_PREFIX
        //            + allocationLU.getFeatureFieldName());

        final Double areaByLU = geodataFinder.getAreaByLUNew2(project
            .getSuitabilityConfig().getUnifiedAreaZone(), project
            .getAreaLabel(), allocationFFName, WifKeys.FUTURELU_PREFIX
            + allocationLU.getFeatureFieldName(), projections, projection);

        final AllocationSimpleItemReport allocationSimpleItemReport = new AllocationSimpleItemReport();
        allocationSimpleItemReport.setLanduseName(allocationLU.getLabel());
        if (areaByLU == null) {
          allocationSimpleItemReport.setSumofArea(0.0);
        } else {
          allocationSimpleItemReport.setSumofArea(areaByLU);
        }

        allocationSimpleItemReport.setYear(projection.getYear());

        setallocationSimpleItemReport.add(allocationSimpleItemReport);

      }
    }
    allocationSimpleAnalysisReport
    .setAllocationSimpleItemReport(setallocationSimpleItemReport);
    LOGGER.info("Finished allocationAnalysisReport for: {}",
        allocationSimpleAnalysisReport.getProjectId());
    return allocationSimpleAnalysisReport;
  }
}
