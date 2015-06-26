package au.org.aurin.wif.impl.allocation;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.AllocationCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class AllocationScenarioServiceImpl.
 */
@Service
@Qualifier("allocationScenarioService")
public class AllocationScenarioServiceImpl implements AllocationScenarioService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationScenarioServiceImpl.class);

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The parser. */
  @Autowired
  private AllocationCouchParser allocationParser;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The allocation analyzer. */
  @Autowired
  private AllocationAnalyzer allocationAnalyzer;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The demand scenario service. */
  @Resource
  private DemandOutcomeService manualdemandScenarioService;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The geoserver config. */
  @Autowired
  private GeoServerConfig geoserverConfig;

  /** The wif config. */
  @Autowired
  private WifConfig wifConfig;

  /** The Allocation config dao. */
  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

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
    LOGGER.trace("AllocationScenario Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.AllocationScenarioService#
   * createAllocationScenario
   * (au.org.aurin.wif.model.suitability.AllocationScenario, java.lang.String)
   */
  public AllocationScenario createAllocationScenario(
      AllocationScenario allocationScenario, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteAllocationScenarioException,
      IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException {
    if (allocationScenario == null) {
      LOGGER
          .error("createAllocationScenario failed: allocationScenario is null or invalid");
      throw new WifInvalidInputException(
          "createAllocationScenario failed: allocationScenario is null or invalid");
    }
    // because for creating first scenario in UI
    // if (allocationScenario.getLandUseOrderMap().size() == 0) {
    // throw new IncompleteAllocationScenarioException(
    // "there's no allocation land use order defined for scenario with project id "
    // + projectId);
    // }

    // WifProject project = projectService.getProject(projectId);

    final WifProject project = projectService
        .getProjectConfiguration(projectId);

    // allocationScenario.getSuitabilityScenarioId();

    final String AllocationConfigsId = project.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);
    // it checks in the allocation configs
    // if (allocationConfig.getAllocationColumnsMap().size() == 0) {
    // LOGGER.debug("AllocationConfig not setup, creating it");
    // if (allocationScenario.isManual()) {
    // // if (!projectService.setupManualAllocationConfig(project)) {
    // // since we need to have allocation config information
    // project = projectService.setupManualAllocationConfig(project);
    // // LOGGER.error("Creation of new scenario failed");
    // // throw new WifInvalidConfigException(
    // //
    // "Creation of new scenario failed, could not expand UAZ with allocation labels");
    // // }
    // } else {
    // if (!projectService.setupAllocationConfig(project)) {
    // LOGGER.error("Creation of new scenario failed");
    // throw new WifInvalidConfigException(
    // "Creation of new scenario failed, could not expand UAZ with allocation labels");
    // }
    // }
    //
    // }

    final String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    // if (allocationScenario.getSuitabilityScenarioId() != "") {
    if (!allocationScenario.getSuitabilityScenarioId().equals("")) {
      final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
          .getSuitabilityScenario(suitabilityScenarioId);
      allocationScenario.setSuitabilityScenario(suitabilityScenario);
    }

    if (allocationScenario.isManual()) {
      final String manualdemandScenarioId = allocationScenario
          .getManualdemandScenarioId();
      // if (allocationScenario.getManualdemandScenarioId() != "") {
      if (!allocationScenario.getManualdemandScenarioId().equals("")) {
        final DemandOutcome manualdemandScenario = manualdemandScenarioService
            .getDemandOutcome(manualdemandScenarioId);
        allocationScenario.setManualdemandScenario(manualdemandScenario);
      }
    } else {
      final String demandScenarioId = allocationScenario.getDemandScenarioId();

      final DemandScenario demandScenario = demandScenarioService
          .getDemandScenario(demandScenarioId);
      allocationScenario.setDemandScenario(demandScenario);
    }

    try {
      if (allocationScenario.getLandUseOrder().size() > 0) {
        allocationScenario = allocationParser
            .parse(allocationScenario, project);
      }

    } catch (final Exception e) {
      LOGGER.error("Parsing new scenario failed", e);
      throw new IncompleteAllocationScenarioException(
          "Parsing new scenario failed", e);
    }
    LOGGER.debug("persisting the allocationScenario={}",
        allocationScenario.getLabel());
    allocationScenario.setProjectId(projectId);
    final AllocationScenario savedAllocationScenario = allocationScenarioDao
        .persistAllocationScenario(allocationScenario);
    LOGGER.debug("returning the allocationScenario with id={}",
        savedAllocationScenario.getId());
    project.getAllocationScenariosMap().put(savedAllocationScenario.getId(),
        savedAllocationScenario.getLabel());
    // project = allocationScenario.getWifProject();
    projectService.updateProject(project);
    return savedAllocationScenario;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.allocation.AllocationScenarioService#getAllocationScenario
   * (java.lang.String)
   */
  public AllocationScenario getAllocationScenario(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the allocationScenario with ID={}", id);
    try {
      AllocationScenario allocationScenario = allocationScenarioDao
          .findAllocationScenarioById(id);
      if (allocationScenario == null) {
        LOGGER.error("illegal argument, the allocationScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the allocationScenario with the ID " + id
                + " supplied was not found ");
      }
      final String projectId = allocationScenario.getProjectId();
      WifProject project = projectService.getProject(projectId);
      // if (allocationScenario.getSuitabilityScenarioId() != "") {
      if (!allocationScenario.getSuitabilityScenarioId().equals("")) {
        final String suitabilityScenarioId = allocationScenario
            .getSuitabilityScenarioId();
        final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
            .getSuitabilityScenario(suitabilityScenarioId);
        allocationScenario.setSuitabilityScenario(suitabilityScenario);
      }

      if (allocationScenario.isManual()) {
        final String manualdemandScenarioId = allocationScenario
            .getManualdemandScenarioId();
        // if (allocationScenario.getManualdemandScenarioId() != "") {
        if (!allocationScenario.getManualdemandScenarioId().equals("")) {

          final List<DemandScenario> listDemand = demandScenarioService
              .getDemandScenarios(project.getId());
          Boolean lsw = false;
          for (final DemandScenario dsn : listDemand) {
            if (dsn.getId().equals(
                allocationScenario.getManualdemandScenarioId())) {
              lsw = true;
            }
          }
          if (lsw == false) {
            final DemandOutcome manualdemandScenario = manualdemandScenarioService
                .getDemandOutcome(manualdemandScenarioId);
            allocationScenario.setManualdemandScenario(manualdemandScenario);
            project = manualdemandScenario.getWifProject();
          }

        }
      } else {
        final String demandScenarioId = allocationScenario
            .getDemandScenarioId();
        final DemandScenario demandScenario = demandScenarioService
            .getDemandScenario(demandScenarioId);
        allocationScenario.setDemandScenario(demandScenario);
        project = demandScenario.getWifProject();
      }

      if (allocationScenario.getLandUseOrder().size() > 0) {
        allocationScenario = allocationParser
            .parse(allocationScenario, project);
      }
      allocationScenario.setWifProject(project);
      return allocationScenario;

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationScenarioService#getAllocationScenario(
   * java.lang.String)
   */
  public AllocationScenario getAllocationScenario(final String id,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    final AllocationScenario allocationScenario = getAllocationScenario(id);
    if (allocationScenario.getProjectId().equals(projectId)) {
      return allocationScenario;
    } else {
      LOGGER
          .error("illegal argument, the allocationScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the allocationScenario supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationScenarioService#updateAllocationScenario
   * (au.org.aurin.wif.model.allocation.AllocationScenario, java.lang.String)
   */
  public void updateAllocationScenario(
      final AllocationScenario allocationScenario, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("updating allocationScenario: {}, with id: {}",
        allocationScenario.getLabel(), allocationScenario.getId());
    try {
      if (allocationScenario.getProjectId().equals(projectId)) {
        allocationScenario.setRevision(allocationScenarioDao
            .findAllocationScenarioById(allocationScenario.getId())
            .getRevision());
        allocationScenarioDao.updateAllocationScenario(allocationScenario);
      } else {
        LOGGER
            .error("illegal argument, the allocationScenario supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the allocationScenario supplied doesn't belong to project: "
                + projectId);
      }
    } catch (final IllegalArgumentException e) {

      LOGGER
          .error("illegal argument, the allocationScenario supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the allocationScenario supplied is invalid ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationScenarioService#deleteAllocationScenario
   * (java.lang.String, java.lang.String)
   */
  public void deleteAllocationScenario(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("deleting the allocationScenario with ID={}", id);
    final AllocationScenario allocationScenario = allocationScenarioDao
        .findAllocationScenarioById(id);
    if (allocationScenario.getProjectId().equals(projectId)) {
      allocationScenarioDao.deleteAllocationScenario(allocationScenario);
      final WifProject project = projectService.getProject(projectId);
      project.getAllocationScenariosMap().remove(id);
      wifProjectDao.updateProject(project);
    } else {
      LOGGER
          .error("illegal argument, the allocationScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the allocationScenario supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationScenarioService#getAllocationScenarios
   * (java.lang.String)
   */
  public List<AllocationScenario> getAllocationScenarios(final String projectID)
      throws WifInvalidInputException {
    LOGGER
        .info("getting all allocationScenarios for projectID: {} ", projectID);

    return allocationScenarioDao.getAllocationScenarios(projectID);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.allocation.AllocationScenarioService#getWMS(java.lang
   * .String)
   */
  public WMSOutcome getWMS(final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    final AllocationScenario allocationScenario = getAllocationScenario(id);
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(allocationScenario.getSuitabilityScenarioId());

    LOGGER
        .info(
            "creating the information for WMS outcome of wif allocationScenario={}",
            allocationScenario.getLabel());
    final WMSOutcome outcome = new WMSOutcome();
    outcome.setStoreName(geoserverConfig.getStoreName());
    outcome.setWorkspaceName(geoserverConfig.getWorkspace());
    final WifProject project = wifProjectDao
        .findProjectById(suitabilityScenario.getProjectId());
    outcome.setAllocationLabels(generateAllocationLabels(suitabilityScenario));
    outcome.setAvailableStyles(wifConfig.getSuitabilityStyles());
    LOGGER.debug("using the following {} columns for the outcome layers",
        outcome.getAllocationLabels().size());
    for (final Entry<String, List<String>> aluLabel : outcome
        .getAllocationLabels().entrySet()) {
      LOGGER.debug("suitabilityLU ={}", aluLabel.getKey());
      final List<String> values = aluLabel.getValue();
      for (final String label : values) {
        LOGGER.debug("Future field name ={}", label);
      }
    }
    final String uazDBTable = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    LOGGER.info("creating an allocation outcome for WMS Layer ={}, server ={}",
        uazDBTable, wifConfig.getServerWMSURL());
    outcome.setLayerName(uazDBTable);
    final String serverWMSURL = wifConfig.getServerWMSURL();
    outcome.setServerURL(serverWMSURL);
    return outcome;
  }

  /**
   * Generate allocation labels. They must include the allocation labels for
   * each associated LU belonging to a particular suitabilityLU
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @return the map
   */
  private Map<String, List<String>> generateAllocationLabels(
      final SuitabilityScenario suitabilityScenario) {
    final Map<String, List<String>> allocationLabels = new HashMap<String, List<String>>();
    final Set<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    for (final SuitabilityRule suitabilityRule : suitabilityRules) {
      final List<String> labels = new ArrayList<String>();
      final SuitabilityLU suitabilityLU = suitabilityRule.getSuitabilityLU();
      final Set<AllocationLU> associatedALUs = suitabilityLU
          .getAssociatedALUs();
      for (final AllocationLU allocationLU : associatedALUs) {
        labels.add(allocationLU.getAllocationFeatureFieldName());
      }
      allocationLabels.put(suitabilityLU.getLabel(), labels);
    }
    return allocationLabels;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.allocation.AllocationScenarioService#getWMS(java.lang
   * .String)
   */
}
