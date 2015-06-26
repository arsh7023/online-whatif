/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.impl.datacreator;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.impl.report.allocation.AllocationReporter;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationAllocationNoControlsData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.reports.allocation.AllocationAnalysisReportDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.datacreator.DBDataCreatorService;
import au.org.aurin.wif.svc.datacreator.DBDemandAnalysisDataCreatorService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class DBAllocationDataCreatorServiceImpl.
 */
@Component("DBDataCreator")
public class DBDataCreatorServiceImpl implements DBDataCreatorService {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The allocationSetupCreator. */
  @Autowired
  private DBDemandAnalysisDataCreatorService demandAnalysisCreator;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DBDataCreatorServiceImpl.class);

  /** The allocation reporter. */
  @Autowired
  private AllocationReporter allocationReporter;

  /** The allocation analysis report dao. */
  @Autowired
  private AllocationAnalysisReportDao allocationAnalysisReportDao;

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DBAllocationDataCreatorService#
   * createAllocationModule (java.lang.String, java.lang.String)
   */
  public WifProject createDemonstrationModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String demandScenarioId, String allocationScenarioId,
      String allocationScenarioReportId, String manualdemandConfigId)
      throws Exception {

    WifProject project = demandAnalysisCreator.createDemandAnalysisModule(
        projectId, suitabilityScenarioId, demandConfigId, demandScenarioId,
        manualdemandConfigId);

    if (allocationScenarioId != null) {
      LOGGER.trace("Finding a scenario with id: {}", allocationScenarioId);
      AllocationScenario allocationScenario = allocationScenarioDao
          .findAllocationScenarioById(allocationScenarioId);

      if (allocationScenario == null) {
        DemandScenario demandScenario = demandScenarioService
            .getDemandScenario(demandScenarioId);
        SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId);
        project.getSuitabilityScenarios().add(suitabilityScenario);
        project.getDemandScenarios().add(demandScenario);
        project = DemonstrationAllocationNoControlsData
            .createAllocationModule(project);

        allocationScenario = project
            .getAllocationScenarioByLabel("Suburbanization-High Growth-No Controls");
        LOGGER.trace("Creating a scenario: {}", allocationScenario.getLabel());
        allocationScenario.setProjectId(projectId);
        allocationScenario.setId(allocationScenarioId);
        allocationScenario.setSuitabilityScenarioId(suitabilityScenarioId);
        allocationScenario.setDemandScenarioId(demandScenarioId);
        projectService.updateProject(project);
        allocationScenarioDao.persistAllocationScenario(allocationScenario);
        // Updating allocationLU and allocationLabels
        Set<AllocationLU> allocationLandUses = project.getAllocationLandUses();
        for (AllocationLU allocationLU : allocationLandUses) {
          LOGGER.trace("... ALU label: {}, allocationFFName {}",
              allocationLU.getLabel(),
              allocationLU.getAllocationFeatureFieldName());
          allocationLUDao.updateAllocationLU(allocationLU);
        }

        // creating an allocation report if it doesn't exist
        if (allocationScenarioReportId != null) {
          LOGGER.trace("Finding an allocation scenarioReport with id: {}",
              allocationScenarioId);
          AllocationAnalysisReport allocationAnalysisReport = allocationAnalysisReportDao
              .findAllocationAnalysisReportById(allocationScenarioReportId);
          if (allocationAnalysisReport == null) {
            allocationScenario.setWifProject(project);
            allocationScenario.setDemandScenario(demandScenario);

            allocationAnalysisReport = allocationReporter
                .getAllocationAnalysisReport(allocationScenario);
            allocationAnalysisReport.setId(allocationScenarioReportId);
            AllocationAnalysisReport createdAllocationAnalysisReport = allocationAnalysisReportDao
                .persistAllocationAnalysisReport(allocationAnalysisReport);
            LOGGER.info("createdAllocationAnalysisReport uuid: "
                + createdAllocationAnalysisReport.getId());
          }
        }
        // Updating project
        Map<String, String> idLabelMap = mapper
            .getIdLabelMap(allocationScenario);
        project.getAllocationScenariosMap().putAll(idLabelMap);
        wifProjectDao.updateProject(project);
      }
      LOGGER.trace("Successfully loaded allocation scenario: {}",
          allocationScenario.getLabel());

    }

    LOGGER.info("Successfully loaded allocation analysis module ");

    return project;
  }
}
