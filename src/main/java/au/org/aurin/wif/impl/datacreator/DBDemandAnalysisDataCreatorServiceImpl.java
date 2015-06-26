/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.impl.datacreator;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.impl.demand.DemandConfigurator;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandAnalysisData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandTrendData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ProjectedDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.demand.LocalAreaRequirementDao;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.datacreator.DBDemandAnalysisDataCreatorService;
import au.org.aurin.wif.svc.datacreator.DBDemandSetupDataCreatorService;

/**
 * The Class DBDemandAnalysisDataCreatorServiceImpl.
 */
@Component("DBDemandAnalysisDataCreator")
public class DBDemandAnalysisDataCreatorServiceImpl implements
    DBDemandAnalysisDataCreatorService {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The demandSetupCreator. */
  @Autowired
  private DBDemandSetupDataCreatorService demandSetupCreator;

  /** The demand analyzer. */
  @Autowired
  private DemandConfigurator demandConfigurator;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /** The localAreaRequirement dao. */
  @Autowired
  private LocalAreaRequirementDao localAreaRequirementDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DBDemandAnalysisDataCreatorServiceImpl.class);

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DBDemandAnalysisDataCreatorService#
   * createDemandAnalysisModule (java.lang.String, java.lang.String)
   */
  public WifProject createDemandAnalysisModule(String projectId,
      String suitabilityScenarioId, String demandConfigId,
      String demandScenarioId, String manualdemandConfigId) throws Exception {
    WifProject project = demandSetupCreator.createDemandSetupModule(projectId,
        suitabilityScenarioId, demandConfigId, manualdemandConfigId);
    DemandConfig demandConfig = project.getDemandConfig();

    if (demandConfig.getDemographicTrends().size() == 0) {
      project = DemonstrationDemandTrendData.createDemandTrendModule(project);

      demandConfig = project.getDemandConfig();

      Set<DemographicTrend> trends = demandConfig.getDemographicTrends();
      LOGGER.info(" creating trends for demandConfig id: {}", demandConfigId);

      demandConfig.setDemographicTrends(project.getDemandConfig()
          .getDemographicTrends());

      trends = demandConfig.getDemographicTrends();
      for (DemographicTrend demographicTrend : trends) {
        LOGGER.info("Creating trends: {}", demographicTrend.getLabel());

        Set<DemographicData> demographicData = demographicTrend
            .getDemographicData();
        for (DemographicData data : demographicData) {
          data.setProjectionLabel(data.getProjection().getLabel());
          LOGGER.info("Creating data for projection: {}", data.getProjection()
              .getLabel());
          if (data instanceof EmploymentDemographicData) {
            ((EmploymentDemographicData) data)
                .setSectorLabel(((EmploymentDemographicData) data).getSector()
                    .getLabel());
          }
        }
        demandConfig = demandConfigurator.createAutomatedTrends(demandConfig);
        demandConfigDao.updateDemandConfig(demandConfig);

        demandConfigDao.updateDemandConfig(demandConfig);
      }
    }
    LOGGER.debug("Trends for demandConfig with ID {} is loaded ",
        demandConfigId);

    if (demandScenarioId != null) {
      LOGGER.info("Finding a scenario with id: {}", demandScenarioId);
      DemandScenario demandScenario = demandScenarioDao
          .findDemandScenarioById(demandScenarioId);

      if (demandScenario == null) {
        project = DemonstrationDemandAnalysisData
            .createDemandAnalysisModule(project);
        demandConfig = project.getDemandConfig();

        demandScenario = project.getDemandScenarioByLabel("High Growth");
        LOGGER.info("Creating a scenario: {}", demandScenario.getLabel());
        demandScenario.setProjectId(projectId);
        demandScenario.setId(demandScenarioId);
        DemographicTrend demographicTrend = demandConfig
            .getTrendByLabel(demandScenario.getDemographicTrend().getLabel());
        demandScenario.setDemographicTrendLabel(demographicTrend.getLabel());
        // Mapping demand infos
        Set<DemandInfo> demandInfos = demandScenario.getDemandInfos();
        for (DemandInfo demandInfo : demandInfos) {
          if (demandInfo instanceof ResidentialDemandInfo) {

            ResidentialDemandInfo resDInfo = ((ResidentialDemandInfo) demandInfo);
            LOGGER.debug("Mapping ResidentialDemandInfo for {} ", resDInfo
                .getResidentialLU().getLabel());
            resDInfo.setResidentialLUId(resDInfo.getResidentialLU().getId());
          } else if (demandInfo instanceof ProjectedDemandInfo) {
            ProjectedDemandInfo proDInfo = ((ProjectedDemandInfo) demandInfo);
            Set<ProjectedData> datas = proDInfo.getProjectedDatas();
            for (ProjectedData data : datas) {
              data.setProjectionLabel(data.getProjection().getLabel());
            }
            if (demandInfo instanceof EmploymentDemandInfo) {
              EmploymentDemandInfo eDInfo = ((EmploymentDemandInfo) demandInfo);
              LOGGER
                  .debug(
                      "Mapping EmploymentDemandInfo for  {},number of projected data  {}",
                      eDInfo.getSector().getLabel(), eDInfo.getProjectedDatas()
                          .size());
              eDInfo.setSectorLabel(eDInfo.getSector().getLabel());
            } else { // PreservationDemandInfohim and
              LOGGER.debug("Found PreservationDemandInfo");
              PreservationDemandInfo pdfInfo = (PreservationDemandInfo) demandInfo;
              pdfInfo.setAllocationLUId(pdfInfo.getAllocationLU().getId());
            }
          }
        }
        Set<LocalData> localDatas = demandScenario.getLocalDatas();
        for (LocalData localData : localDatas) {
          localData.setLocalJurisdictionLabel(localData.getLocalJurisdiction()
              .getLabel());
          localData.setProjectionLabel(localData.getProjection().getLabel());
        }

        DemandScenario savedDemandScenario = demandScenarioDao
            .persistDemandScenario(demandScenario);

        LOGGER.info("Finding a automatic demand scenario with id: {}",
            WifKeys.TEST_AUTOMATIC_SCENARIO_ID);
        DemandScenario automatedDemandScenario = demandScenarioDao
            .findDemandScenarioById(WifKeys.TEST_AUTOMATIC_SCENARIO_ID);

        if (automatedDemandScenario == null) {
          // Creating automated scenario
          automatedDemandScenario = demandConfigurator.createAutomatedScenario(
              demandConfig, project);
          automatedDemandScenario.setId(WifKeys.TEST_AUTOMATIC_SCENARIO_ID);
          DemandScenario savedAutomatedDemandScenario = demandScenarioDao
              .persistDemandScenario(automatedDemandScenario);
          LOGGER.info("Successfully created automated demand scenario: {}",
              savedAutomatedDemandScenario.getId());
        }
        // Creating area requirements
        Set<AllocationLU> allocationLandUses = project.getAllocationLandUses();
        for (AllocationLU allocationLU : allocationLandUses) {
          Set<AreaRequirement> areaRequirements = allocationLU
              .getAreaRequirements();
          for (AreaRequirement areaRequirement : areaRequirements) {
            areaRequirement.setDemandScenarioId(savedDemandScenario.getId());
            areaRequirement.setProjectionLabel(areaRequirement.getProjection()
                .getLabel());
            areaRequirement.setAllocationLUId(areaRequirement.getAllocationLU()
                .getId());
            if (allocationLU.isLocal()) {
              LocalAreaRequirement localAreaRequirement = (LocalAreaRequirement) areaRequirement;
              LocalJurisdiction jurisdiction = demandConfig
                  .getLocalJurisdictionByLabel(localAreaRequirement
                      .getLocalJurisdictionLabel());
              localAreaRequirement.setLocalJurisdiction(jurisdiction);
              LocalAreaRequirement requirement = localAreaRequirementDao
                  .persistLocalAreaRequirement(localAreaRequirement);
              LOGGER.debug("Created Local Area Requirement {} for {} ",
                  requirement.getId(), allocationLU.getLabel());
            } else {
              AreaRequirement requirement = areaRequirementDao
                  .persistAreaRequirement(areaRequirement);
              LOGGER.debug("Created area requirement {} for {} ",
                  requirement.getId(), allocationLU.getLabel());
            }
          }
        }
        Map<String, String> idLabelMap = mapper.getIdLabelMap(demandScenario);
        project.getDemandScenariosMap().putAll(idLabelMap);
        wifProjectDao.updateProject(project);
      }
      LOGGER.info("Successfully loaded demand scenario: {}",
          demandScenario.getLabel());
    }

    LOGGER.info("Successfully loaded demand analysis module: ");
    return project;
  }
}
