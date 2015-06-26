package au.org.aurin.wif.impl.allocation;

import java.net.MalformedURLException;
import java.util.List;

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
import au.org.aurin.wif.exception.validate.IncompleteAllocationControlScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.AllocationCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationControlScenarioService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class AllocationControlScenarioServiceImpl.
 */
@Service
@Qualifier("allocationControlScenarioService")
public class AllocationControlScenarioServiceImpl implements
    AllocationControlScenarioService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationControlScenarioServiceImpl.class);

  /** The allocation scenario dao. */
  @Autowired
  private AllocationControlScenarioDao AllocationControlScenarioDao;

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
    LOGGER.trace("AllocationControlScenario Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.AllocationControlScenarioService#
   * createAllocationControlScenario
   * (au.org.aurin.wif.model.suitability.AllocationControlScenario,
   * java.lang.String)
   */
  public AllocationControlScenario createAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteAllocationControlScenarioException,
      IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException {
    if (AllocationControlScenario == null) {
      LOGGER
          .error("createAllocationControlScenario failed: AllocationControlScenario is null or invalid");
      throw new WifInvalidInputException(
          "createAllocationControlScenario failed: AllocationControlScenario is null or invalid");
    }

    WifProject project = projectService.getProjectConfiguration(projectId);

    LOGGER.debug("persisting the AllocationControlScenario={}",
        AllocationControlScenario.getLabel());
    AllocationControlScenario.setProjectId(projectId);
    AllocationControlScenario savedAllocationControlScenario = AllocationControlScenarioDao
        .persistAllocationControlScenario(AllocationControlScenario);
    LOGGER.debug("returning the AllocationControlScenario with id={}",
        savedAllocationControlScenario.getId());
    project.getAllocationControlScenariosMap().put(
        savedAllocationControlScenario.getId(),
        savedAllocationControlScenario.getLabel());
    // project = AllocationControlScenario.getWifProject();
    projectService.updateProject(project);
    return savedAllocationControlScenario;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.allocation.AllocationControlScenarioService#
   * getAllocationControlScenario (java.lang.String)
   */
  public AllocationControlScenario getAllocationControlScenario(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the AllocationControlScenario with ID={}", id);
    try {
      AllocationControlScenario AllocationControlScenario = AllocationControlScenarioDao
          .findAllocationControlScenarioById(id);
      if (AllocationControlScenario == null) {
        LOGGER
            .error("illegal argument, the AllocationControlScenario with the ID "
                + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the AllocationControlScenario with the ID " + id
                + " supplied was not found ");
      }
      String projectId = AllocationControlScenario.getProjectId();
      WifProject project = projectService.getProject(projectId);

      AllocationControlScenario.setWifProject(project);
      return AllocationControlScenario;

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid AllocationControlScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid AllocationControlScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationControlScenarioService#
   * getAllocationControlScenario( java.lang.String)
   */
  public AllocationControlScenario getAllocationControlScenario(String id,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    AllocationControlScenario AllocationControlScenario = getAllocationControlScenario(id);
    if (AllocationControlScenario.getProjectId().equals(projectId)) {
      return AllocationControlScenario;
    } else {
      LOGGER
          .error("illegal argument, the AllocationControlScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the AllocationControlScenario supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationControlScenarioService#
   * updateAllocationControlScenario
   * (au.org.aurin.wif.model.allocation.AllocationControlScenario,
   * java.lang.String)
   */
  public void updateAllocationControlScenario(
      AllocationControlScenario AllocationControlScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER
        .info("updating AllocationControlScenario: {}, with id: {}",
            AllocationControlScenario.getLabel(),
            AllocationControlScenario.getId());
    try {
      if (AllocationControlScenario.getProjectId().equals(projectId)) {
        AllocationControlScenario.setRevision(AllocationControlScenarioDao
            .findAllocationControlScenarioById(
                AllocationControlScenario.getId()).getRevision());
        AllocationControlScenarioDao
            .updateAllocationControlScenario(AllocationControlScenario);
      } else {
        LOGGER
            .error("illegal argument, the AllocationControlScenario supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the AllocationControlScenario supplied doesn't belong to project: "
                + projectId);
      }
    } catch (IllegalArgumentException e) {

      LOGGER
          .error("illegal argument, the AllocationControlScenario supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the AllocationControlScenario supplied is invalid ",
          e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationControlScenarioService#
   * deleteAllocationControlScenario (java.lang.String, java.lang.String)
   */
  public void deleteAllocationControlScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("deleting the AllocationControlScenario with ID={}", id);
    AllocationControlScenario AllocationControlScenario = AllocationControlScenarioDao
        .findAllocationControlScenarioById(id);
    if (AllocationControlScenario.getProjectId().equals(projectId)) {
      AllocationControlScenarioDao
          .deleteAllocationControlScenario(AllocationControlScenario);
      WifProject project = projectService.getProject(projectId);
      project.getAllocationControlScenariosMap().remove(id);
      wifProjectDao.updateProject(project);
    } else {
      LOGGER
          .error("illegal argument, the AllocationControlScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the AllocationControlScenario supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.AllocationControlScenarioService#
   * getAllocationControlScenarios (java.lang.String)
   */
  public List<AllocationControlScenario> getAllocationControlScenarios(
      String projectID) throws WifInvalidInputException {
    LOGGER.info("getting all AllocationControlScenarios for projectID: {} ",
        projectID);

    return AllocationControlScenarioDao
        .getAllocationControlScenarios(projectID);
  }

}
