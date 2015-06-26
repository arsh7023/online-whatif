/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.jdbcDataStoreConfig;
import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentData;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class DemandConfigServiceImpl.
 */
@Service
@Qualifier("demandConfigService")
public class DemandConfigServiceImpl implements DemandConfigService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandConfigServiceImpl.class);

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The demand configurator. */
  @Autowired
  private DemandConfigurator demandConfigurator;

  /** The demand validator. */
  @Autowired
  private DemandValidator demandValidator;
  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The parser. */
  @Autowired
  private DemandSetupCouchParser demandSetupParser;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The project service. */
  @Resource
  private ProjectService projectService;
  /** The demand scenario service. */
  @Autowired
  private DemandScenarioService demandScenarioService;
  @Resource
  private DemandOutcomeService manualDemandScenarioService;

  @Autowired
  @Qualifier(value = "myjdbcDataStoreConfig")
  private jdbcDataStoreConfig myjdbcDataStoreConfig;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

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
    LOGGER.trace("DemandConfig Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.DemandConfigService#createDemandConfig
   * (au.org.aurin.wif.model.demand.DemandConfig, java.lang.String)
   */
  public DemandConfig createDemandConfig(DemandConfig demandConfig,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, IncompleteDemandConfigException {
    if (demandConfig == null) {
      LOGGER
          .error("createDemandConfig failed: demandConfig is null or invalid");
      throw new WifInvalidInputException(
          "createDemandConfig failed: demandConfig is null or invalid");
    }

    LOGGER.debug("createDemandConfig for project  id ={}", projectId);
    WifProject project = projectService.getProject(projectId);
    project = projectParser.parse(project);
    demandConfig.setProjectId(projectId);
    demandValidator.validate(demandConfig);

    if (demandConfig.getIncludeTrends() == true) { // ///// new if
      demandConfig = demandConfigurator.fillBasicDemandInfo(demandConfig,
          project.getSuitabilityConfig().getUnifiedAreaZone());
    }

    // commented to save demand configs if error in createAutomatedTrends
    // happens.
    // demandConfig = demandConfigurator.createAutomatedTrends(demandConfig);

    LOGGER.debug("persisting the demandConfig for project ={}",
        project.getLabel());
    final DemandConfig savedDemandConfig = demandConfigDao
        .persistDemandConfig(demandConfig);
    LOGGER.debug("returning the demandConfig with id={}",
        savedDemandConfig.getId());
    project.setDemandConfig(savedDemandConfig);
    project.setDemandConfigId(savedDemandConfig.getId());

    wifProjectDao.updateProject(project);

    if (demandConfig.getIncludeTrends() == true) { // ////// new if

      // new instaed of commented above
      if (updateDemandConfig(demandConfig, projectId)) {
        // so that we dont have conflict with revisions.
        // commented new
        // project = wifProjectDao.findProjectById(projectId);
        // project = createAutomaticInformation(savedDemandConfig, project);

        // TODO Find out if it should be done through the service, but it is
        // much
        // more efficient to
        // wifProjectDao.updateProject(project);
      } else {
        LOGGER.debug("AutomaticeTrend Failed for project ={}",
            project.getLabel());
      }
    }
    return savedDemandConfig;
  }

  /**
   * Creates the automatic generated information from demand configuration setup
   * 
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  private WifProject createAutomaticInformation(
      final DemandConfig demandConfig, final WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      IncompleteDemandConfigException {

    final DemandScenario automatedScenario = demandConfigurator
        .createAutomatedScenario(demandConfig, project);
    automatedScenario.setDemandConfig(demandConfig);
    final DemandScenario savedScenario = demandScenarioDao
        .persistDemandScenario(automatedScenario);
    LOGGER.debug("returning the automatedScenario with id={}",
        savedScenario.getId());
    project.getDemandScenarios().add(automatedScenario);
    project.getDemandScenariosMap().put(savedScenario.getId(),
        savedScenario.getLabel());
    List<AreaRequirement> outcome;
    try {
      outcome = demandScenarioService.getOutcome(savedScenario.getId());
    } catch (final ParsingException e) {
      throw new IncompleteDemandConfigException(e);
    } catch (final IncompleteDemandScenarioException e) {
      throw new IncompleteDemandConfigException(e);
    }

    final DemandOutcome savedManualDemandScenario = manualDemandScenarioService
        .createDemandOutcome(outcome, project.getId());
    LOGGER.debug("returning the ManualDemandScenario with id={}",
        savedManualDemandScenario.getId());
    project.getDemandOutcomesMap().put(savedManualDemandScenario.getId(),
        savedManualDemandScenario.getLabel());

    return project;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DemandConfigService#getDemandConfig(java.lang.String)
   */
  public DemandConfig getDemandConfig(final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    String demandConfigId = null;
    LOGGER.debug("getting the demandConfig for project with ID={}", projectId);
    String msg = "illegal argument, the ID " + demandConfigId
        + " supplied doesn't identify a valid demandConfig ";
    try {
      WifProject project = projectService.getProjectNoMapping(projectId);
      project = projectParser.parse(project);
      DemandConfig demandConfig = null;
      demandConfigId = project.getDemandConfigId();
      LOGGER.info("getting the demandConfig with ID={}", demandConfigId);
      if (demandConfigId != null) {
        demandConfig = demandConfigDao.findDemandConfigById(demandConfigId);
        if (demandConfig == null) {
          msg = "illegal argument, the demandConfig with the ID "
              + demandConfigId + " supplied was not found ";
          LOGGER.error(msg);
          throw new InvalidEntityIdException(msg);
        } else {
          demandConfig = demandSetupParser.parse(demandConfig, project);
        }
        return demandConfig;
      } else {
        // LOGGER.error(msg);
        // throw new InvalidEntityIdException(msg);
        return null;
      }
    } catch (final IllegalArgumentException e) {

      LOGGER.error(msg);
      throw new WifInvalidInputException(msg);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DemandConfigService#updateDemandConfig(au.org.aurin
   * .wif.model.allocation.DemandConfig, java.lang.String)
   */
  public boolean updateDemandConfig(final DemandConfig demandConfig,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("updating demandConfig: {}, with id: {}",
        demandConfig.getLabel(), demandConfig.getId());
    Boolean lsw = true;
    try {
      final WifProject project = projectService.getProject(projectId);
      if (demandConfig.getProjectId().equals(projectId)) {

        // demandConfig = demandSetupParser.parse(demandConfig, project);
        // demandConfig.setRevision(demandConfigDao.findDemandConfigById(
        // demandConfig.getId()).getRevision());
        // final DemandConfig dg = demandConfigDao.findDemandConfigById(project
        // .getDemandConfigId());
        // demandConfig.setRevision(demandConfigDao.findDemandConfigById(
        // project.getDemandConfigId()).getRevision());

        // demandConfig.setRevision(null);
        // demandConfig.setId(project.getDemandConfigId());

        // new2 - before automatic trends
        demandConfigDao.updateDemandConfig(demandConfig);

        if (demandConfig.getIncludeTrends() == true) { // ////// new if

          // new for updating automatic demographic trends
          final Set<DemographicTrend> newDemographicTrends = new HashSet<DemographicTrend>();
          final Set<DemographicTrend> DemographicTrend = demandConfig
              .getDemographicTrends();
          for (final DemographicTrend demographicTrend : DemographicTrend) {
            if (demographicTrend.getLabel().equals(
                WifKeys.DEFAULT_AUTOMATIC_TREND_NAME)) {

            } else {
              newDemographicTrends.add(demographicTrend);
            }
          }
          DemographicTrend dt = new DemographicTrend();
          dt.setLabel(WifKeys.DEFAULT_AUTOMATIC_TREND_NAME);
          LOGGER.info("creating trend {}", dt.getLabel());
          dt.setDemographicData(new HashSet<DemographicData>());
          try {
            dt = demandConfigurator.createResidentialTrendFromPastData(
                demandConfig, dt);
            dt = demandConfigurator.createEmploymentTrendFromPastData(
                demandConfig, dt);
          } catch (final IncompleteDemandConfigException e) {
            LOGGER.error("Error in updating automatic trends");
          }
          newDemographicTrends.add(dt);
          demandConfig.setDemographicTrends(newDemographicTrends);
          // //

          demandConfigDao.updateDemandConfig(demandConfig);

          // //////////////////
          // ////newnewnewnwnwe claudia updating percenatge; In emp sector for
          // each landuse: sum Area(landuse)/Sum(area) all landuses for that emp
          // sector
          final Map<String, Double> mapLanduseSize = new HashMap<String, Double>();

          final SuitabilityConfig suitabilityConfig = project
              .getSuitabilityConfig();
          final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();
          final String areaLabel = project.getAreaLabel();
          final String luFields = project.getExistingLUAttributeName();
          for (final AllocationLU futureLU : project.getAllocationLandUses()) {

            final String SQL = "select Sum(\"" + areaLabel + "\") from "
                + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable
                + " where \"" + luFields + "\" ='"
                + futureLU.getFeatureFieldName() + "'";

            final Double d = geodataFinder.getSumofALU(SQL);
            mapLanduseSize.put(futureLU.getId(), d);
            LOGGER.info("The Area size for " + futureLU.getLabel() + "  is: "
                + d.toString());
          }

          final Set<EmploymentSector> allEmploymentSectorsNew = new HashSet<EmploymentSector>();
          for (final EmploymentSector empOld : demandConfig.getSectors()) {
            final EmploymentSector empNew = new EmploymentSector();

            empNew.setLabel(empOld.getLabel());
            empNew.setCode(empOld.getCode());
            empNew.setFeatureFieldName(empOld.getFeatureFieldName());
            empNew.setAssociatedLUs(empOld.getAssociatedLUs());
            empNew.setAssociatedALUsMap(empOld.getAssociatedALUsMap());

            Double sumArea = 0.0;
            // first loop calculate sum:
            for (final String landUseKey : empOld.getAssociatedALUsMap()
                .keySet()) {
              for (final String maplandUseKey : mapLanduseSize.keySet()) {
                if (landUseKey.equals(maplandUseKey)) {
                  sumArea = sumArea + mapLanduseSize.get(landUseKey);
                }

              }
            }

            // second loop calculate avg
            final Map<String, Double> associatedALUsPercentageNew = new HashMap<String, Double>();
            for (final String landUseKey1 : empOld.getAssociatedALUsMap()
                .keySet()) {

              for (final String maplandUseKey : mapLanduseSize.keySet()) {
                if (landUseKey1.equals(maplandUseKey)) {
                  final double tx = mapLanduseSize.get(landUseKey1) / sumArea;
                  Double empPercentage = 0.0;
                  // empPercentage = tx * 100;
                  empPercentage = tx;
                  associatedALUsPercentageNew.put(landUseKey1, empPercentage);
                }
              }
            }
            empNew.setAssociatedALUsPercentage(associatedALUsPercentageNew);
            allEmploymentSectorsNew.add(empNew);
          }// end for EmploymentSector empOld

          demandConfig.setSectors(allEmploymentSectorsNew);

          // //for density
          final Set<DensityDemandInfo> setDinf = new HashSet<DensityDemandInfo>();
          for (final AllocationLU futureLU : project.getAllocationLandUses()) {

            final String id = futureLU.getId();
            Double sumValue = 0.0;
            Double sumAreaLu = 0.0;
            Double finalValue = 0.0;
            for (final EmploymentSector empNew : allEmploymentSectorsNew) {

              Double curEmpNumber = 0.0;
              for (final EmploymentCurrentData empCur : demandConfig
                  .getCurrentDemographic().getEmploymentCurrentDatas()) {
                if (empCur.getSectorLabel().equals(empNew.getLabel())) {
                  curEmpNumber = empCur.getEmployees().doubleValue();
                }
              }

              Double percenatgeValue1 = 0.0;

              for (final String landUseKey : empNew
                  .getAssociatedALUsPercentage().keySet()) {

                if (landUseKey.equals(id)) {
                  percenatgeValue1 = empNew.getAssociatedALUsPercentage().get(
                      landUseKey);
                  sumValue = sumValue + percenatgeValue1 * curEmpNumber;
                }
              }

            }// loop emp sector

            for (final String maplandUseKey : mapLanduseSize.keySet()) {
              if (id.equals(maplandUseKey)) {
                sumAreaLu = mapLanduseSize.get(id);
              }
            }
            if (sumAreaLu != 0) {
              finalValue = sumValue / sumAreaLu;
            }

            final DensityDemandInfo dinf = new DensityDemandInfo();
            dinf.setCurrentDensity(finalValue);
            dinf.setFutureDensity(finalValue);
            dinf.setInfillRate(0.0);
            dinf.setLanduseID(futureLU.getId());
            dinf.setLanduseName(futureLU.getLabel());
            futureLU.getEmploymentSectors();
            setDinf.add(dinf);

          }// loop future land use
          demandConfig.setDensityDemandInfo(setDinf);

          demandConfigDao.updateDemandConfig(demandConfig);
          // end newnewnewn claudia
          // /////////////////////////////

        }// ////// end new if
      } else {
        lsw = false;
        final String msg = "illegal argument, the ID " + projectId
            + " supplied is not associated with this demandConfig ";
        LOGGER.error(msg);
        throw new WifInvalidInputException(msg);

      }
    } catch (final IllegalArgumentException e) {
      lsw = false;
      LOGGER.error("illegal argument, the demandConfig supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the demandConfig supplied is invalid ");

    }

    return lsw;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DemandConfigService#deleteDemandConfig(java.lang.String
   * , java.lang.String)
   */
  public void deleteDemandConfig(final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the demandConfig from project with ID={}", projectId);
    try {
      final DemandConfig demandConfig = getDemandConfig(projectId);
      final WifProject project = wifProjectDao.findProjectById(projectId);
      project.setDemandConfigId(null);
      project.setDemandConfig(null);
      wifProjectDao.updateProject(project);
      demandConfigDao.deleteDemandConfig(demandConfig);
    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + projectId
          + " supplied doesn't identify a valid demandConfig ");
      throw new InvalidEntityIdException("illegal argument, the ID "
          + projectId + " supplied doesn't identify a valid demandConfig ");
    }
  }

  /*
   * Manual public ManualDemandConfig createManualDemandConfig(
   * ManualDemandConfig demandConfig, String projectId) throws
   * WifInvalidInputException, WifInvalidConfigException, ParsingException,
   * IncompleteDemandConfigException { if (demandConfig == null) { LOGGER
   * .error("createDemandConfig failed: demandConfig is null or invalid"); throw
   * new WifInvalidInputException(
   * "createDemandConfig failed: demandConfig is null or invalid"); }
   * LOGGER.debug("createDemandConfig for project  id ={}", projectId);
   * WifProject project = projectService.getProject(projectId); project =
   * projectParser.parse(project); demandConfig.setProjectId(projectId); // line
   * below commented by Ali // demandValidator.validate(demandConfig); // line
   * below commented by Ali // demandConfig =
   * demandConfigurator.fillBasicDemandInfo(demandConfig, //
   * project.getSuitabilityConfig().getUnifiedAreaZone()); // line below
   * commented by Ali // demandConfig =
   * demandConfigurator.createAutomatedTrends(demandConfig); // TODO this is
   * commented until interface of demand a scenario is finished // project =
   * createAutomaticInformation(demandConfig, project);
   * LOGGER.debug("persisting the demandConfig for project ={}",
   * project.getLabel()); ManualDemandConfig savedDemandConfig = demandConfigDao
   * .persistManualDemandConfig(demandConfig);
   * LOGGER.debug("returning the demandConfig with id={}",
   * savedDemandConfig.getId());
   * project.setDemandConfigId(savedDemandConfig.getId()); // TODO Find out if
   * it should be done through the service, but it is much // more efficient to
   * wifProjectDao.updateProject(project); return savedDemandConfig; } public
   * void deleteManualDemandConfig(String projectId) throws
   * WifInvalidInputException, WifInvalidConfigException {
   * LOGGER.info("deleting the demandConfig from project with ID={}",
   * projectId); try { ManualDemandConfig demandConfig =
   * getManualDemandConfig(projectId); WifProject project =
   * wifProjectDao.findProjectById(projectId); project.setDemandConfigId(null);
   * project.setDemandConfig(null); wifProjectDao.updateProject(project);
   * demandConfigDao.deleteManualDemandConfig(demandConfig); } catch
   * (IllegalArgumentException e) { LOGGER.error("illegal argument, the ID " +
   * projectId + " supplied doesn't identify a valid demandConfig "); throw new
   * InvalidEntityIdException("illegal argument, the ID " + projectId +
   * " supplied doesn't identify a valid demandConfig "); } } public
   * ManualDemandConfig getManualDemandConfig(String projectId) throws
   * WifInvalidInputException, WifInvalidConfigException { String demandConfigId
   * = null; LOGGER.debug("getting the demandConfig for project with ID={}",
   * projectId); String msg = "illegal argument, the ID " + demandConfigId +
   * " supplied doesn't identify a valid demandConfig "; try { WifProject
   * project = projectService.getProjectNoMapping(projectId); project =
   * projectParser.parse(project); ManualDemandConfig demandConfig = null;
   * demandConfigId = project.getManualDemandConfigId();
   * LOGGER.info("getting the demandConfig with ID={}", demandConfigId); if
   * (demandConfigId != null) { demandConfig = demandConfigDao
   * .findManualDemandConfigById(demandConfigId); if (demandConfig == null) {
   * msg = "illegal argument, the demandConfig with the ID " + demandConfigId +
   * " supplied was not found "; LOGGER.error(msg); throw new
   * InvalidEntityIdException(msg); } else { demandConfig =
   * demandSetupParser.parse(demandConfig, project); } return demandConfig; }
   * else { LOGGER.error(msg); throw new InvalidEntityIdException(msg); } }
   * catch (IllegalArgumentException e) { LOGGER.error(msg); throw new
   * WifInvalidInputException(msg); } }
   */

}
