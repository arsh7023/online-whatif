package au.org.aurin.wif.impl.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.report.allocation.AllocationReporter;
import au.org.aurin.wif.impl.report.suitability.SuitabilityReporter;
import au.org.aurin.wif.impl.report.suitability.suitabilityConvertReport;
import au.org.aurin.wif.impl.report.suitability.suitabilityFactorReport;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.model.reports.allocation.AllocationSimpleAnalysisReport;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.reports.ProjectReportDao;
import au.org.aurin.wif.repo.reports.allocation.AllocationAnalysisReportDao;
import au.org.aurin.wif.repo.reports.demand.DemandAnalysisReportDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class ReportServiceImpl.
 */
@Service
@Qualifier("reportService")
public class ReportServiceImpl implements ReportService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1363346734533L;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The demand outcome dao. */
  @Autowired
  private DemandOutcomeDao demandOutcomeDao;

  /** The manual demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The Allocation Control scenario dao. */
  @Autowired
  private AllocationControlScenarioDao allocationControlScenarioDao;

  /** The Allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The project report dao. */
  @Autowired
  private ProjectReportDao projectReportDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ReportServiceImpl.class);

  /** The suitability reporter. */
  @Autowired
  private SuitabilityReporter suitabilityReporter;

  /** The allocation reporter. */
  @Autowired
  private AllocationReporter allocationReporter;

  /** The demand scenario service. */
  @Autowired
  private DemandScenarioService demandScenarioService;

  /** The demand analysis report dao. */
  @Autowired
  private DemandAnalysisReportDao demandAnalysisReportDao;
  /** The allocation analysis report dao. */
  @Autowired
  private AllocationAnalysisReportDao allocationAnalysisReportDao;

  @Autowired
  private SuitabilityAnalysisReport suitabilityAnalysisReport;

  @Autowired
  private AllocationConfigsDao allocationConfigsDao;

  @Autowired
  private DemandConfigService demandConfigDao;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service successfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.report.ReportService#getSuitabilityAnalysisReport(
   * au.org.aurin.wif.model.suitability.SuitabilityScenario)
   */
  public SuitabilityAnalysisReport getSuitabilityAnalysisReport(
      final SuitabilityScenario suitabilityScenario)
      throws WifInvalidInputException, WifInvalidConfigException {

    return suitabilityReporter
        .getSuitabilityAnalysisReport(suitabilityScenario);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.report.ReportService#getProjectReport(au.org.aurin
   * .wif.model.WifProject)
   */
  public ProjectReport getProjectReport(final WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.debug("creating project report for : {}", project.getId());
    final ProjectReport projectReport = new ProjectReport();
    projectReport.setLabel(project.getName());
    projectReport.setReportType(project.getDocType());
    projectReport.setServiceVersion(WifKeys.WIF_KEY_VERSION);

    final Set<String> entrySet = project.getSuitabilityScenariosMap().keySet();
    LOGGER
        .debug("adding to report : {} suitability scenarios", entrySet.size());
    for (final String entry : entrySet) {
      final SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(entry);
      projectReport.getSuitabilityScenarios().add(suitabilityScenario);
    }

    // new for manual demand config
    DemandConfig demandConfig = null;
    String demandConfigId = null;
    demandConfigId = project.getDemandConfigId();
    LOGGER.info("getting the demandConfig with ID={}", demandConfigId);
    if (demandConfigId != null) {
      demandConfig = demandConfigDao.getDemandConfig(project.getId());
      project.setDemandConfig(demandConfig);
      // project = parseALUSectors(demandConfig, project);
      demandConfig.setWifProject(project);
    }
    projectReport.setDemandConfig(demandConfig);

    // new for manual demand scenario
    final Set<String> entrySetManualdemand = project.getDemandOutcomesMap()
        .keySet();
    LOGGER.debug("adding to report : {} manual demand scenarios",
        entrySetManualdemand.size());
    for (final String entryManual : entrySetManualdemand) {
      final DemandOutcome manualdemandScenario = demandOutcomeDao
          .findDemandOutcomeById(entryManual);
      projectReport.getDemandOutcomes().add(manualdemandScenario);
    }

    // new for allocation config
    AllocationConfigs allocationConfig = null;
    String allocationConfigId = null;
    allocationConfigId = project.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", allocationConfigId);
    if (allocationConfigId != null) {
      allocationConfig = allocationConfigsDao
          .findAllocationConfigsById(allocationConfigId);
      project.setAllocationConfigs(allocationConfig);

      allocationConfig.setWifProject(project);
    }
    projectReport.setAllocationconfig(allocationConfig);

    // new for allocation control scenario
    final Set<String> entrySetallocationControl = project
        .getAllocationControlScenariosMap().keySet();
    LOGGER.debug("adding to report : {} allocation control scenarios",
        entrySetallocationControl.size());
    for (final String entryControl : entrySetallocationControl) {
      final AllocationControlScenario allocationControlScenario = allocationControlScenarioDao
          .findAllocationControlScenarioById(entryControl);
      projectReport.getAllocationControlScenarios().add(
          allocationControlScenario);
    }

    // new for allocation scenario
    final Set<String> entrySetallocation = project.getAllocationScenariosMap()
        .keySet();
    LOGGER.debug("adding to report : {} allocation  scenarios",
        entrySetallocation.size());
    for (final String entryalloc : entrySetallocation) {
      final AllocationScenario allocationScenario = allocationScenarioDao
          .findAllocationScenarioById(entryalloc);
      projectReport.getAllocationScenarios().add(allocationScenario);
    }

    // end new

    // new for demand scenario
    final Set<String> entrySetdemand = project.getDemandScenariosMap().keySet();
    LOGGER.debug("adding to report : {}  demand scenarios",
        entrySetdemand.size());
    for (final String entryManual : entrySetdemand) {
      final DemandScenario demandScenario = demandScenarioDao
          .findDemandScenarioById(entryManual);
      projectReport.getDemandScenarios().add(demandScenario);
    }

    project.getSuitabilityScenariosMap().clear();
    project.getDemandScenariosMap().clear();
    project.getDemandOutcomesMap().clear();
    project.getAllocationScenariosMap().clear();
    projectReport.setProject(project);
    projectReport.setProjectId(project.getId());
    final ProjectReport createdProjectReport = projectReportDao
        .persistProjectReport(projectReport);

    LOGGER.debug("createdProjectReport uuid: " + createdProjectReport.getId());

    return createdProjectReport;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.report.ReportService#getDemandAnalysisReport(au.org
   * .aurin.wif.model.demand.DemandScenario)
   */
  public DemandAnalysisReport getDemandAnalysisReport(
      final DemandScenario demandScenario) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException {
    final DemandAnalysisReport demandAnalysisReport = new DemandAnalysisReport();
    demandAnalysisReport.setLabel(demandScenario.getLabel());
    demandAnalysisReport.setProjectId(demandScenario.getProjectId());
    demandAnalysisReport.setReportType(demandScenario.getDocType());
    demandAnalysisReport.setLabel(demandScenario.getLabel());
    demandAnalysisReport.setScenarioLabel(demandScenario.getLabel());
    demandAnalysisReport.setOutcome(demandScenarioService
        .getOutcome(demandScenario));
    final DemandAnalysisReport persistDemandAnalysisReport = demandAnalysisReportDao
        .persistDemandAnalysisReport(demandAnalysisReport);
    LOGGER.debug("persistDemandAnalysisReport: id {} "
        + persistDemandAnalysisReport.getId());

    return persistDemandAnalysisReport;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.report.ReportService#getAllocationAnalysisReport(au
   * .org.aurin.wif.model.allocation.AllocationScenario)
   */
  public AllocationAnalysisReport getAllocationAnalysisReport(
      final AllocationScenario allocationScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    final AllocationAnalysisReport allocationAnalysisReport = allocationReporter
        .getAllocationAnalysisReport(allocationScenario);
    final AllocationAnalysisReport createdAllocationAnalysisReport = allocationAnalysisReportDao
        .persistAllocationAnalysisReport(allocationAnalysisReport);
    LOGGER.info("createdAllocationAnalysisReport uuid: "
        + createdAllocationAnalysisReport.getId());
    return createdAllocationAnalysisReport;
  }

  public AllocationSimpleAnalysisReport getAllocationSimpleAnalysisReport(
      final AllocationScenario allocationScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    final AllocationSimpleAnalysisReport allocationSimpleAnalysisReport = allocationReporter
        .getAllocationSimpleAnalysisReport(allocationScenario);

    LOGGER.info("createdAllocationAnalysisSimpleReport ");
    return allocationSimpleAnalysisReport;
  }

  public List<suitabilityFactorReport> getSuitabilityCSVAnalysisReport(
      final SuitabilityScenario suitabilityScenario) {
    return suitabilityReporter
        .getSuitabilityCSVAnalysisReport(suitabilityScenario);
  }

  public List<suitabilityConvertReport> getSuitabilityConvertAnalysisReport(
      final SuitabilityScenario suitabilityScenario) {
    return suitabilityReporter
        .getSuitabilityConvertAnalysisReport(suitabilityScenario);
  }

  public List<String> getSuitabilityLUsScores(final String projectID)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("getting all suitabilityLUs for projectID: {} ", projectID);
    final List<String> out = new ArrayList<String>();

    final List<SuitabilityLU> resSuitabilityLUs = new ArrayList<SuitabilityLU>();
    final List<SuitabilityLU> suitabilityLUs = suitabilityLUDao
        .getSuitabilityLUs(projectID);
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      suitabilityLU = suitabilityParser.parse(suitabilityLU);
      resSuitabilityLUs.add(suitabilityLU);

    }
    final WifProject project = projectService.getProject(projectID);
    final SuitabilityConfig suitabilityConfig = project.getSuitabilityConfig();
    for (final SuitabilityLU slu : resSuitabilityLUs) {
      final Double minv = geodataFinder.getScoreRanges("min",
          suitabilityConfig.getUnifiedAreaZone(), slu.getFeatureFieldName());
      final Double maxv = geodataFinder.getScoreRanges("max",
          suitabilityConfig.getUnifiedAreaZone(), slu.getFeatureFieldName());
      final String st = slu.getId() + "," + slu.getFeatureFieldName() + ","
          + Double.toString(minv) + "," + Double.toString(maxv);
      out.add(st);
    }

    return out;
  }
  
 
}
