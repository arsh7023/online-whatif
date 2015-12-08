package au.org.aurin.wif.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.IncompleteSuitabilityLUConfigException;
import au.org.aurin.wif.exception.validate.UAZAlreadyCreatedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.DataStoreToPostgisExporter;
import au.org.aurin.wif.io.FileToPostgisExporter;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.PostgisToDataStoreExporter;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.model.allocation.PlannedALU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.CurrentDemographic;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
//import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialCurrentData;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.demand.LocalAreaRequirementDao;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorTypeDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationConfigsService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityLUService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

/**
 * The Class ProjectServiceImpl.
 */
@Service
@Qualifier("projectService")
public class ProjectServiceImpl implements ProjectService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 213426734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectServiceImpl.class);
  /** The file to postgis exporter. */
  @Resource
  private FileToPostgisExporter fileToPostgisExporter;

  /** The postgis to data store exporter. */
  @Autowired
  private PostgisToDataStoreExporter postgisToDataStoreExporter;
  /** The geoserver config. */
  @Autowired
  private GeoServerConfig geoserverConfig;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  @Resource
  private SuitabilityLUService suitabilityLUService;

  @Autowired
  private AllocationConfigsDao allocationConfigsDao;

  /** The wif config. */
  @Autowired
  private WifConfig wifConfig;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  @Resource
  private ValidatorCRS validatorCRS;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The manual demand scenario dao. */
  @Autowired
  private DemandOutcomeDao manualdemandScenarioDao;

  /** The allocation control scenario dao. */
  @Autowired
  private AllocationControlScenarioDao allocationControlScenarioDao;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The area requirement dao. */
  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /** The local area requirement dao. */
  @Autowired
  private LocalAreaRequirementDao localAreaRequirementDao;

  /** The factor dao. */
  @Autowired
  private CouchFactorDao factorDao;

  /** The factor type dao. */
  @Autowired
  private CouchFactorTypeDao factorTypeDao;

  /** The geoserver publisher. */
  @Autowired
  private GeoServerRESTPublisher geoserverPublisher;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /** The parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The exporter. */
  @Autowired
  private DataStoreToPostgisExporter exporter;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The projects pool. */
  private HashMap<Integer, Future<WifProject>> projectsPool;

  @Resource
  private AllocationConfigsService AllocationConfigsService;

  @Resource
  private DemandScenarioService demandScenarioService;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {
    projectsPool = new HashMap<Integer, Future<WifProject>>();
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    projectsPool = null;
    LOGGER.trace("Project Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#getProjectConfiguration(java.lang.String
   * )
   */
  @Override
  public WifProject getProjectConfiguration(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {
    WifProject project = wifProjectDao.findProjectById(id);
    //LOGGER.debug("...Building project full configuration: {}",
    //project.getLabel());
    final Collection<SuitabilityLU> suitabilityLUs = project
        .getSuitabilityLUs();
    final Set<SuitabilityLU> suitabilityLUsConfigured = new HashSet<SuitabilityLU>();
    //LOGGER.debug("Loading {} Suitablity Land Uses...", suitabilityLUs.size());
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      //LOGGER.debug("Suitability LU label: {}", suitabilityLU.getLabel());
      suitabilityLU = suitabilityLUDao.findSuitabilityLUById(suitabilityLU
          .getId());
      //      LOGGER.debug("AssociatedLUsMap size: "
      //          + suitabilityLU.getAssociatedALUsMap().size());
      suitabilityLU = suitabilityParser.parse(suitabilityLU);
      suitabilityLUsConfigured.add(suitabilityLU);
    }
    final Collection<Factor> factors = project.getFactors();
    final Set<Factor> factorsConfigured = new HashSet<Factor>();
    // LOGGER.info("Loading {} suitablity factors...", factors.size());
    for (final Factor aSuitabilityFactor : factors) {
      // LOGGER.info("- Suitability Factor label: {}",
      // aSuitabilityFactor.getLabel());
      final Factor factor = factorDao
          .findFactorById(aSuitabilityFactor.getId());

      final List<FactorType> factorTypes = factorTypeDao.getFactorTypes(factor
          .getId());
      factor.setFactorTypes(new HashSet<FactorType>());
      factor.getFactorTypes().addAll(factorTypes);
      factorsConfigured.add(factor);
    }
    project.setSuitabilityLUs(suitabilityLUsConfigured);
    project.setFactors(factorsConfigured);
    project = mapper.map(project);
    project.setRevision(wifProjectDao.findProjectById(id).getRevision());
    wifProjectDao.updateProject(project);
    project = projectParser.parse(project);

    return project;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.ProjectService#getProjectZipUAZ(java.lang.String)
   */
  @Override
  public File getProjectZipUAZ(final String id)
      throws WifInvalidInputException, WifInvalidConfigException, IOException,
      DatabaseFailedException {
    final WifProject project = getProjectNoMapping(id);
    final File resultFile = fileToPostgisExporter.getZipShp(project
        .getSuitabilityConfig().getUnifiedAreaZone());
    return resultFile;
  } /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.OWIProjectService#getProject(java.lang.Integer)
   */

  @Override
  public WifProject getProject(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.debug("getting the wif project with ID={}", id);
    try {
      WifProject project = wifProjectDao.findProjectById(id);
      if (project == null) {
        LOGGER.error("illegal argument, the project with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the project with the ID " + id
            + " supplied was not found ");
      }
      if (project.getSuitabilityConfig() != null) {
        if (project.getSuitabilityConfig().getUnifiedAreaZone() != null) {
          LOGGER.trace("using UAZ table={}", project.getSuitabilityConfig()
              .getUnifiedAreaZone());
        } else {
          LOGGER.warn("Wif project with ID={}, doesn't have defined UAZ", id);
        }
      }
      project = projectParser.parse(project);
      return project;
    } catch (final IllegalArgumentException e) {

      LOGGER
      .error("illegal argument, the ID supplied doesn't identify a valid project ");
      throw new WifInvalidInputException(
          "illegal argument, the ID supplied doesn't identify a valid project ",
          e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#getProjectNoMapping(java.lang.Integer )
   */
  @Override
  public WifProject getProjectNoMapping(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.trace("getting the wif project,no mapping, with ID={}", id);
    final String msg = "illegal argument, the project with the ID " + id
        + " supplied was not found";
    try {
      final WifProject project = wifProjectDao.findProjectById(id);
      if (project == null) {
        LOGGER.error(msg);
        throw new InvalidEntityIdException(msg);
      }
      return project;

    } catch (final IllegalArgumentException e) {
      LOGGER.error(msg);
      throw new WifInvalidInputException(msg, e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.ProjectService#getProject(java.lang.String,
   * java.lang.String)
   */
  @Override
  public WifProject getProject(final String role, final String id)
      throws WifInvalidInputException, WifInvalidConfigException {
    final WifProject project = getProject(id);
    // TODO support multi user
    // if (project.getRoleOwner().equalsIgnoreCase(role)) {
    // if (project.isAuthorised(role))
    return project;
    // }
    // else {
    // LOGGER.error("illegal argument, the role supplied doesn't belong to this project ");
    // throw new WifInvalidInputException (
    // "illegal argument, the role supplied doesn't belong to this project ");
    // }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.OWIProjectService#getAllProjects(java.lang.String)
   */
  @Override
  public List<WifProject> getAllProjects(final String role)
      throws WifInvalidInputException {
    LOGGER.info("getting all projects for role {} ", role);

    return wifProjectDao.getAllProjects(role);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#createProject(au.org.aurin.wif.model
   * .WifProject, java.lang.String)
   */
  @Override
  public WifProject createProject(final WifProject project,
      final String username) throws WifInvalidInputException,
  DataStoreUnavailableException, WifInvalidConfigException {
    if (project == null) { // TODO do a proper validation
      LOGGER.error("createProject failed: project is null or invalid");
      throw new WifInvalidInputException(
          "createProject failed: project is null or invalid");
    }
    LOGGER.info("persisting the wif project={}", project.getLabel());
    project.setReady(false);
    project.setRoleOwner(username);
    final WifProject savedProject = wifProjectDao.persistProject(project);
    LOGGER.info("returning the wif project with id={}", savedProject.getId());
    return savedProject;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.ProjectService#purgeProject(java.lang.String)
   */
  @Override
  public void purgeProject(final String id) throws InvalidEntityIdException {
    LOGGER.info("purging the wif project with ID={}", id);

    final WifProject project = wifProjectDao.findProjectById(id);
    if (project == null) {
      LOGGER.error("illegal argument, the project with the ID " + id
          + " supplied was not found ");
      throw new InvalidEntityIdException(
          "illegal argument, the project with the ID " + id
          + " supplied was not found ");
    }

    if (project.getLocalShpFile() != null) {
      final File shpFile = new File(project.getLocalShpFile());
      // Deleting the temporal file created
      if (shpFile != null) {
        if (FileUtils.deleteQuietly(shpFile)) {
          LOGGER.info("{} has been deleted successfully.",
              project.getLocalShpFile());
        } else {
          LOGGER
          .warn(
              "{} delete operation failed. in the future the disc can  possibly run out of space",
              shpFile.getAbsolutePath());
        }
      }
    }
    wifProjectDao.deleteProject(project);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.OWIProjectService#deleteProject(java.lang.String)
   */
  // TODO Create an exception for each delete to see which one fails, the layer
  // removal, or the UAz or the project itself.also create deletes outside this
  // method,this one is too big
  @Override
  public void deleteProject(final String id, final Boolean deleteUAZ)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the wif project with ID={}, deleteUAZ? = {}", id,
        deleteUAZ);
    try {
      final WifProject project = wifProjectDao.findProjectById(id);
      if (project == null) {
        LOGGER.error("illegal argument, the project with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the project with the ID " + id
            + " supplied was not found ");
      }
      final String tableName = project.getSuitabilityConfig()
          .getUnifiedAreaZone();

      // delete allocation land uses
      final Collection<AllocationLU> allocationLandUses = project
          .getAllocationLandUses();
      LOGGER.info("Deleting {} allocation land uses (ALU)...",
          allocationLandUses.size());
      for (AllocationLU allocationLU : allocationLandUses) {
        LOGGER.info("ALU id{}, label: {}", allocationLU.getId(),
            allocationLU.getLabel());
        allocationLU = allocationLUDao.findAllocationLUById(allocationLU
            .getId());
        allocationLUDao.deleteAllocationLU(allocationLU);
      }

      // delete factors
      final Collection<Factor> factors = project.getFactors();
      LOGGER.info("Deleting {} suitablity factors...", factors.size());
      for (Factor aSuitabilityFactor : factors) {
        LOGGER.info("- Suitability Factor label: {}",
            aSuitabilityFactor.getLabel());
        aSuitabilityFactor = factorDao.findFactorById(aSuitabilityFactor
            .getId());
        factorDao.deleteFactor(aSuitabilityFactor);

        // factor types
        for (final FactorType aFactorType : aSuitabilityFactor.getFactorTypes()) {
          LOGGER.debug("::  aFactorType label: {}", aFactorType.getLabel());
          factorTypeDao.deleteFactorType(aFactorType);
        }
      }
      // delete suitability land uses
      final Collection<SuitabilityLU> suitabilityLUs = project
          .getSuitabilityLUs();
      LOGGER.info("Deleting {} Suitablity Land Uses...", suitabilityLUs.size());
      for (final SuitabilityLU suitabilityLU : suitabilityLUs) {
        LOGGER.info("Suitability LU label: {}", suitabilityLU.getLabel());
        suitabilityLUDao.deleteSuitabilityLU(suitabilityLU);
      }
      // deleting suitability scenarios
      final Map<String, String> suitabilityScenariosMap = project
          .getSuitabilityScenariosMap();
      LOGGER.info("***** Deleting {} suitability scenarios...",
          suitabilityScenariosMap.keySet().size());

      for (final String scnId : suitabilityScenariosMap.keySet()) {
        final SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
            .findSuitabilityScenarioById(scnId);
        LOGGER.info("Deleting scenario with id: {}",
            suitabilityScenario.getId());
        final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
            .getSuitabilityRules();
        LOGGER.debug("{} has {} suitability rules associated",
            suitabilityScenario.getLabel(), suitabilityRules.size());
        for (final SuitabilityRule rule : suitabilityRules) {
          suitabilityRuleDao.deleteSuitabilityRule(rule);
        }
        suitabilityScenarioDao.deleteSuitabilityScenario(suitabilityScenario);
      }
      // deleting demand config
      final String demandConfigId = project.getDemandConfigId();
      if (demandConfigId != null) {
        final DemandConfig demandConfig = demandConfigDao
            .findDemandConfigById(demandConfigId);
        if (demandConfig != null) {
          demandConfigDao.deleteDemandConfig(demandConfig);
        }
      }
      // deleting demand scenarios
      final Map<String, String> demandScenariosMap = project
          .getDemandScenariosMap();
      LOGGER.info("***** Deleting {} demand scenarios...", demandScenariosMap
          .keySet().size());

      for (final String scnId : demandScenariosMap.keySet()) {
        final DemandScenario demandScenario = demandScenarioDao
            .findDemandScenarioById(scnId);
        LOGGER.info("Deleting scenario with id: {}", demandScenario.getId());
        // Deleting associated area requirements
        final List<AreaRequirement> requirements = areaRequirementDao
            .getAreaRequirements(demandScenario.getId());
        for (final AreaRequirement areaRequirement : requirements) {
          areaRequirementDao.deleteAreaRequirement(areaRequirement);
        }
        final List<LocalAreaRequirement> localRequirements = localAreaRequirementDao
            .getLocalAreaRequirements(demandScenario.getId());
        for (final LocalAreaRequirement areaRequirement : localRequirements) {
          areaRequirementDao.deleteAreaRequirement(areaRequirement);
        }
        demandScenarioDao.deleteDemandScenario(demandScenario);
      }
      // deleting allocation scenarios
      final Map<String, String> allocationScenariosMap = project
          .getAllocationScenariosMap();
      LOGGER.info("***** Deleting {} allocation scenarios...",
          allocationScenariosMap.keySet().size());

      for (final String scnId : allocationScenariosMap.keySet()) {
        final AllocationScenario allocationScenario = allocationScenarioDao
            .findAllocationScenarioById(scnId);
        LOGGER
        .info("Deleting scenario with id: {}", allocationScenario.getId());
        allocationScenarioDao.deleteAllocationScenario(allocationScenario);
      }
      LOGGER.info("Deleting project {} ...", project.getLabel());

      wifProjectDao.deleteProject(project);
      if (project.getSetupCompleted() && deleteUAZ) {
        LOGGER.info("Deleting external dependencies for: {} ...", tableName);
        // removing external dependencies
        geoserverPublisher.removeLayer(geoserverConfig.getWorkspace(),
            tableName);
        geodataFinder.deleteWifUAZInDB(tableName);
      }
    } catch (final IllegalArgumentException e) {
      final String msg = "illegal argument, some ID in the configuration is not valid for {} ";
      LOGGER.error(msg, id);
      throw new WifInvalidConfigException(msg + id, e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.OWIProjectService#updateProject(au.org.aurin.wif
   * .model.WifProject)
   */
  @Override
  public void updateProject(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("updating project ={}, with id: {}", project.getLabel(),
        project.getId());
    try {
      project.setRevision(wifProjectDao.findProjectById(project.getId())
          .getRevision());
      project = mapper.map(project);
      // if (project.getAllocationConfig() != null) {
      if (project.getAllocationConfig().getAllocationColumnsMap().size() > 0) {
        LOGGER
        .debug("allocation config is set for: {} ...", project.getLabel());
      }
      wifProjectDao.updateProject(project);
    } catch (final IllegalArgumentException e) {

      LOGGER
      .error("illegal argument, the ID supplied doesn't identify a valid project ");
      throw new WifInvalidInputException(
          "illegal argument, the ID supplied doesn't identify a valid project ",
          e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#setupAllocationConfig(au.org.aurin.
   * wif.model.WifProject)
   */
  @Override
  public boolean setupAllocationConfig(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.trace("setupAllocationConfig for: {}", project.getLabel());
    final AllocationConfig allocationConfig = new AllocationConfig();
    project = getProject(project.getId());

    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(project.getId());

    for (final Projection projection : demandConfig.getProjections()) {
      final String ffName = "ALU_" + projection.getYear();
      LOGGER.trace("Future projection feature field name: {}", ffName);
      allocationConfig.getAllocationColumnsMap().put(projection.getLabel(),
          ffName);
    }

    LOGGER.trace("persisting allocation config  for project : {}",
        project.getLabel());
    // Updating allocationLU with allocationLabels
    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.getAssociatedLU() != null) {
        allocationLU.setAllocationFeatureFieldName(WifKeys.FUTURELU_PREFIX
            + allocationLU.getFeatureFieldName());
        LOGGER.trace("... ALU label: {}, allocationFFName {}",
            allocationLU.getLabel(),
            allocationLU.getAllocationFeatureFieldName());
        allocationLUDao.updateAllocationLU(allocationLU);
      }
      // Automatically adding undeveloped land uses
      if (allocationLU.getLandUseFunction().equals(LandUseFunction.LBCS_9XXX)) {
        allocationConfig.getUndevelopedLUsColumns().add(
            allocationLU.getFeatureFieldName());
      }
    }
    final ArrayList<String> columnList = new ArrayList<String>(allocationConfig
        .getAllocationColumnsMap().values());
    final String tableName = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    Boolean lsw = true;
    if (geodataFinder.expandUAZcolumns(tableName, columnList)) {
      /*
       * if (geodataFinder.createSpatialIndex(tableName,
       * project.getGeometryColumnName())) {
       * project.setAllocationConfig(allocationConfig);
       * wifProjectDao.updateProject(project); //return true; } else {
       * LOGGER.error
       * ("could not create the spatial index of database of the project ");
       * lsw=false; }
       */

    } else {
      LOGGER.error("could not update the spatial database of the project ");
      lsw = false;

    }
    return lsw;

  }

  @Override
  public WifProject setupManualAllocationConfig(WifProject project)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IllegalArgumentException, MalformedURLException,
      NoSuchAuthorityCodeException, DataStoreUnavailableException,
      GeoServerConfigException, FactoryException {
    LOGGER.trace("setupMnaualAllocationConfig for: {}", project.getLabel());
    final AllocationConfig allocationConfig = new AllocationConfig();
    project = getProject(project.getId());

    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(project.getId());

    for (final Projection projection : demandConfig.getProjections()) {
      final String ffName = "ALU_" + projection.getYear();
      LOGGER.trace("Future projection feature field name: {}", ffName);
      allocationConfig.getAllocationColumnsMap().put(projection.getLabel(),
          ffName);
    }

    LOGGER.trace("persisting allocation config  for project : {}",
        project.getLabel());

    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {
      if (allocationLU.getAssociatedLU() != null) {
        allocationLU.setAllocationFeatureFieldName(WifKeys.FUTURELU_PREFIX
            + allocationLU.getFeatureFieldName());
        LOGGER.trace("... ALU label: {}, allocationFFName {}",
            allocationLU.getLabel(),
            allocationLU.getAllocationFeatureFieldName());
        allocationLUDao.updateAllocationLU(allocationLU);
      }
      // Automatically adding undeveloped land uses
      if (allocationLU.getLandUseFunction().equals(LandUseFunction.LBCS_9XXX)) {
        allocationConfig.getUndevelopedLUsColumns().add(
            allocationLU.getFeatureFieldName());
      }
    }
    final ArrayList<String> columnList = new ArrayList<String>(allocationConfig
        .getAllocationColumnsMap().values());
    final String tableName = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    Boolean lsw = true;
    if (geodataFinder.expandUAZcolumnsALU(tableName, columnList)) {
      // if (geodataFinder.createSpatialIndex(tableName,
      // project.getGeometryColumnName())) {

      // for reloading geoserver layers
      geoserverPublisher.reload();

      allocationConfig.setWifProject(project);
      project.setAllocationConfig(allocationConfig);
      updateProject(project);
      wifProjectDao.updateProject(project); // return true; } else {
      // LOGGER
      // .error("could not create the spatial index of database of the project ");
      // lsw = false;
      // }

    } else {
      LOGGER.error("could not update the spatial database of the project ");
      lsw = false;

    }
    return project;

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#convertUnionToUAZ(java.lang.String,
   * java.util.List)
   */
  @Override
  public Boolean convertUnionToUAZ(final String id,
      List<String> optionalColumns, final String roleId)
          throws WifInvalidInputException, UAZAlreadyCreatedException,
          WifInvalidConfigException, IncompleteSuitabilityLUConfigException,
          NoSuchAuthorityCodeException, DataStoreUnavailableException,
          FactoryException, GeoServerConfigException, DataStoreCreationException {
    String msg = "Expanding union to UAZ failed";
    LOGGER.info("converting the  project Union table to UAZ with projectID={}",
        id);
    WifProject project = getProjectConfiguration(id);
    if (project.getSetupCompleted()) {
      LOGGER.error("cannot execute the request, the project with the ID " + id
          + " project has already created an uaz");
      throw new UAZAlreadyCreatedException(
          "cannot execute the request, the project with the ID " + id
          + " project has already created an uaz");
    }
    // TODO better not supported yet
    // LOGGER.debug("Using {} optional columns ", optionalColumns.size());
    // for (String column : optionalColumns) {
    // LOGGER.debug("Using optional column ={}", column);
    // }
    optionalColumns = null;
    LOGGER
    .debug("setting up suitability configuration,having the specified columns to the union table");
    final SuitabilityConfig suitabilityConfig = project.getSuitabilityConfig();
    if (suitabilityConfig == null) {
      msg = msg + " SuitabilityConfig is not valid!";
      LOGGER.error(msg);
      throw new WifInvalidConfigException(msg);
    }
    LOGGER.debug("Using the following suitabilityConfig {} ",
        suitabilityConfig.toString());
    suitabilityConfig.setup(optionalColumns, project);
    final int size = suitabilityConfig.getScoreColumns().size();
    if (size == 0) {
      msg = msg
          + " There are no score columns configured, analysis will not be shown! Check that there are suitability landuses configured";
      LOGGER.error(msg);
      throw new IncompleteSuitabilityLUConfigException(msg);
    }
    LOGGER.debug("Setup created {} scoreColumns", size);
    final String tableName = suitabilityConfig.getUnifiedAreaZone();
    final ArrayList<String> columnList = new ArrayList<String>(
        suitabilityConfig.getScoreColumns());
    // FIXME check that the UAZ doesn't have these columns:
    // columnList.addAll(optionalColumns)
    if (project.getAreaLabel() == null) {
      msg = msg + " area column for reporting hasn't been set!";
      LOGGER.error(msg);
      throw new WifInvalidConfigException(msg);
    }
    try {
      LOGGER.debug("Expanding UAZ with {} columns", columnList.size());

      if (geodataFinder.expandUAZcolumns(tableName, columnList)) {
        LOGGER.info("requesting to create a geoserver layer for SRS: {}",
            project.getSrs());
        final CoordinateReferenceSystem crs = CRS.decode(project.getSrs());

        project = this.createWMSLayer(project, roleId, project
            .getSuitabilityConfig().getUnifiedAreaZone(), crs);
        // creating metadata in aurin
        // TODO enable in the next iteration oof what if
        // try {
        // postgisToDataStoreExporter.persistInAurin(project, roleId);
        // } catch (MiddlewarePersistentException e) {
        // LOGGER.warn("sharing with aurin is not enabled!");
        // }
        // ali- also call postgisToDataStoreExporter.exportUAZ

        project.setSetupCompleted(true);
        project.setSuitabilityConfig(suitabilityConfig);
        wifProjectDao.updateProject(project);
        return true;
      } else {
        return false;
      }
    } catch (final NoSuchAuthorityCodeException e) {
      msg = msg
          + " could not update the spatial database of the project, geoserver doesn't recognise the SRS";
      LOGGER.error(msg);
      throw new GeoServerConfigException(msg, e);
    }
  }

  /**
   * Creates the wms layer.
   *
   * @param project
   *          the project
   * @param username
   *          the username
   * @param tableName
   *          the table name
   * @param crs
   *          the crs
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws GeoServerConfigException
   */
  public WifProject createWMSLayer(final WifProject project,
      final String username, final String tableName,
      final CoordinateReferenceSystem crs) throws WifInvalidInputException,
  DataStoreUnavailableException, GeoServerConfigException {
    final GSFeatureTypeEncoder ftEnc = new GSFeatureTypeEncoder();
    LOGGER.info("creating new Geoserver layer ={}", tableName);
    ftEnc.setName(tableName);
    ftEnc.setTitle(tableName);
    final String srs = CRS.toSRS(crs);

    LOGGER.info("Project SRS ={}", srs);
    if (srs == null) {
      LOGGER.error("createProject failed: srs is null or invalid");
      throw new WifInvalidInputException(
          "createProject failed: srs is null or invalid");
    }
    project.setSrs(srs);
    ftEnc.setSRS(srs);

    final GSLayerEncoder layerEnc = new GSLayerEncoder();
    LOGGER.info("publishing the project layer in geoserver restUrl ={}",
        geoserverConfig.getRestUrl());
    LOGGER.info("publishing the project layer in workspace ={}, store ={} ",
        geoserverConfig.getWorkspace(), geoserverConfig.getStoreName());

    final long startTime = System.nanoTime();
    final boolean publishResult = geoserverPublisher.publishDBLayer(
        geoserverConfig.getWorkspace(), geoserverConfig.getStoreName(), ftEnc,
        layerEnc);
    final long endTime = System.nanoTime();
    final long duration = endTime - startTime;
    LOGGER
    .info(">>>>>>>>>>>>>>*************** GeoServer publishing took in ms "
        + duration / 1000000);
    if (!publishResult) {
      LOGGER
      .error("createProject failed: geoserver layer could not be created, wmsOutcome will not work!");
      throw new GeoServerConfigException(
          "createProject failed: geoserver layer could not be created, wmsOutcome will not work!");
    }
    return project;
    // TODO implement the publishing of SLDs into geoserver
    // foreach factor type
    // 1. read the SLD template file, generate a new file based on its
    // contents
    // and modify the property name according to the factor type
    // 2. use geoserverPublisher.publishStyle to publish the text file
    // 3. remove the newly created SLD file from localhost

    // geoserverPublisher.publishStyle(sldBody, "test");

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#restoreProjectConfiguration(au.org.
   * aurin.wif.model.WifProject)
   */
  @Override
  public WifProject restoreProjectConfiguration(
      final ProjectReport projectReport) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException {
    final WifProject oldProject = projectReport.getProject();
    LOGGER.debug("...Serializing project full configuration: {}",
        oldProject.getLabel());
    WifProject restoreProject = new WifProject();
    restoreProject.setName(oldProject.getName());
    restoreProject.setSrs(oldProject.getSrs());
    restoreProject.setAnalysisOption(oldProject.getAnalysisOption());
    restoreProject.setOriginalUnits(oldProject.getOriginalUnits());
    restoreProject.setExistingLUAttributeName(oldProject
        .getExistingLUAttributeName());
    restoreProject.setStudyArea(oldProject.getStudyArea());
    restoreProject.setAreaLabel(oldProject.getAreaLabel());
    restoreProject.setBbox(oldProject.getBbox());
    restoreProject.setGeometryColumnName(oldProject.getGeometryColumnName());
    restoreProject.setRoleOwner(oldProject.getRoleOwner());
    restoreProject.setSuitabilityConfig(oldProject.getSuitabilityConfig());
    restoreProject.setSetupCompleted(oldProject.getSetupCompleted());
    restoreProject.setReady(oldProject.getReady());
    restoreProject = wifProjectDao.persistProject(restoreProject);

    final String newProjectId = restoreProject.getId();
    LOGGER.debug("Project id for restoring = " + restoreProject.getId());

    // restore allocation land uses
    final Collection<AllocationLU> allocationLandUses = oldProject
        .getAllocationLandUses();
    LOGGER.info("Restoring {} allocation land uses (ALU)...",
        allocationLandUses.size());
    for (final AllocationLU oldAllocationLU : allocationLandUses) {
      LOGGER.info("ALU label: {}", oldAllocationLU.getLabel());
      LOGGER.info("--- UAZ value: {}, is not developable?: {}",
          oldAllocationLU.getFeatureFieldName(),
          oldAllocationLU.isNotDevelopable());
      final AllocationLU restoreLU = new AllocationLU();
      restoreLU.setProjectId(newProjectId);
      restoreLU.setLabel(oldAllocationLU.getLabel());
      restoreLU.setFeatureFieldName(oldAllocationLU.getFeatureFieldName());
      restoreLU.setLandUseFunction(oldAllocationLU.getLandUseFunction());
      restoreLU.setGroupQuarters(oldAllocationLU.isGroupQuarters());
      restoreLU.setNewLU(oldAllocationLU.getNewLU());
      restoreLU.setLocal(oldAllocationLU.isLocal());
      restoreLU.setNewPreservation(oldAllocationLU.isNewPreservation());
      restoreLU.setNotDevelopable(oldAllocationLU.isNotDevelopable());
      restoreLU.setNotDefined(oldAllocationLU.getNotDefined());

      final AllocationLU createdAllocationLU = allocationLUDao
          .persistAllocationLU(restoreLU);
      restoreProject.getAllocationLandUses().add(createdAllocationLU);

      LOGGER.debug("createdAllocationLU: " + createdAllocationLU.getId());
    }
    // restore suitability land uses
    final Collection<SuitabilityLU> suitabilityLUs = oldProject
        .getSuitabilityLUs();
    LOGGER.info("Restoring {} Suitablity Land Uses...", suitabilityLUs.size());
    for (final SuitabilityLU oldSsuitabilityLU : suitabilityLUs) {
      LOGGER.info("Suitability LU label: {}", oldSsuitabilityLU.getLabel());
      LOGGER.info("... SLU score UAZ value: {}",
          oldSsuitabilityLU.getFeatureFieldName());
      LOGGER.info("... it has {} associated Land Uses...", oldSsuitabilityLU
          .getAssociatedALUs().size());

      final SuitabilityLU restoreSLU = new SuitabilityLU();
      restoreSLU.setProjectId(newProjectId);
      restoreSLU.setLabel(oldSsuitabilityLU.getLabel());
      restoreSLU.setFeatureFieldName(oldSsuitabilityLU.getFeatureFieldName());
      final Set<Entry<String, String>> associatedALUs = oldSsuitabilityLU
          .getAssociatedALUsMap().entrySet();
      for (final Entry<String, String> entryLU : associatedALUs) {
        final AllocationLU associatedLU = restoreProject
            .getExistingLandUseByLabel(entryLU.getValue());
        LOGGER
        .debug(
            "++++++ suitability LU label: {} has {} associated ALU's,  restoring them…",
            restoreSLU.getLabel(), associatedALUs.size());
        restoreSLU.getAssociatedALUsMap().put(associatedLU.getId(),
            associatedLU.getLabel());
      }
      final SuitabilityLU createdSuitabilityLU = suitabilityLUDao
          .persistSuitabilityLU(restoreSLU);
      restoreProject.getSuitabilityLUs().add(createdSuitabilityLU);
      LOGGER.debug("createdSuitabilityLU: " + createdSuitabilityLU.getId());
    }
    // restore factors
    final Collection<Factor> factors = oldProject.getFactors();
    LOGGER.info("Restoring {}  factors...", factors.size());
    for (final Factor oldFactor : factors) {
      LOGGER.debug("-  Factor label: {}", oldFactor.getLabel());
      final Factor restoreFactor = new Factor();
      restoreFactor.setProjectId(newProjectId);
      restoreFactor.setLabel(oldFactor.getLabel());
      restoreFactor.setFeatureFieldName(oldFactor.getFeatureFieldName());
      final Factor createdFactor = factorDao.persistFactor(restoreFactor);
      LOGGER.debug("restored factor ID: " + createdFactor.getId());

      // restore factor types
      for (final FactorType oldFactorType : oldFactor.getFactorTypes()) {
        final FactorType restoredFactorType = new FactorType();
        LOGGER.debug("::  aFactorType label: {}:: aFactorType UAZ value: {}",
            oldFactorType.getLabel(), oldFactorType.getValue());
        restoredFactorType.setFactorId(createdFactor.getId());
        restoredFactorType.setLabel(oldFactorType.getLabel());
        restoredFactorType.setValue(oldFactorType.getValue());
        restoredFactorType.setNaturalOrder(oldFactorType.getNaturalOrder());
        final FactorType createdFactorType = factorTypeDao
            .persistFactorType(restoredFactorType);
        createdFactor.getFactorTypes().add(createdFactorType);
        LOGGER.debug("restored factorType ID: " + createdFactorType.getId());
      }
      factorDao.updateFactor(createdFactor);
      restoreProject.getFactors().add(createdFactor);
    }

    wifProjectDao.updateProject(restoreProject);

    final Set<SuitabilityScenario> suitabilityScenarios = projectReport
        .getSuitabilityScenarios();
    LOGGER.debug("Restoring {} suitabilityScenarios",
        suitabilityScenarios.size());

    for (final SuitabilityScenario suitabilityScenario : suitabilityScenarios) {

      final SuitabilityScenario restoredSuitabilityScenario = suitabilityScenarioService
          .restoreSuitabilityScenario(suitabilityScenario, restoreProject);
      restoreProject.getSuitabilityScenariosMap().put(
          restoredSuitabilityScenario.getId(),
          restoredSuitabilityScenario.getLabel());
    }

    // //// new restore for DemandOutcome
    final Set<DemandOutcome> setdemandOutcomes = projectReport
        .getDemandOutcomes();

    LOGGER
    .debug("Restoring {} manualdemandScenarios", setdemandOutcomes.size());

    for (final DemandOutcome oldDemandOutcome : setdemandOutcomes) {

      DemandOutcome restoreDemandOutcome = new DemandOutcome();
      restoreDemandOutcome.setLabel(oldDemandOutcome.getLabel());
      restoreDemandOutcome.setProjectId(restoreProject.getId());
      restoreDemandOutcome = manualdemandScenarioDao
          .persistDemandOutcome(restoreDemandOutcome);

      final Set<AreaRequirement> old1AreaRequirements = oldDemandOutcome
          .getAreaRequirements();

      final Set<AreaRequirement> new1AreaRequirements = new HashSet<AreaRequirement>();

      for (final AreaRequirement old1AreaRequirement : old1AreaRequirements) {
        final AreaRequirement new1AreaRequirement = new AreaRequirement();
        final String oldallocationLULabel = old1AreaRequirement
            .getAllocationLULabel();
        new1AreaRequirement.setAllocationLULabel(oldallocationLULabel);
        new1AreaRequirement.setRequiredArea(old1AreaRequirement
            .getRequiredArea());
        new1AreaRequirement.setProjection(old1AreaRequirement.getProjection());
        new1AreaRequirement.setProjectionLabel(old1AreaRequirement
            .getProjectionLabel());

        final Set<AllocationLU> allocationLUs = restoreProject
            .getAllocationLandUses();

        for (final AllocationLU allocationLU : allocationLUs) {
          if (allocationLU.getLabel().equals(oldallocationLULabel)) {
            new1AreaRequirement.setAllocationLUId(allocationLU.getId());
          }
        }
        new1AreaRequirements.add(new1AreaRequirement);
      }
      restoreDemandOutcome.setAreaRequirements(new1AreaRequirements);
      manualdemandScenarioDao.updateDemandOutcome(restoreDemandOutcome);
      restoreProject.getDemandOutcomesMap().put(restoreDemandOutcome.getId(),
          restoreDemandOutcome.getLabel());
    }

    // /// new restore for Allocation configs
    final AllocationConfigs allocationConfigs = new AllocationConfigs();
    allocationConfigs.setProjectId(newProjectId);
    LOGGER.debug("persisting the allocationConfig for project ={}",
        restoreProject.getLabel());

    final AllocationConfigs allocationConfigold = projectReport
        .getAllocationconfig();
    if (allocationConfigold != null) {
      allocationConfigs.setLabel(allocationConfigold.getLabel());

      allocationConfigs.setAllocationColumnsMap(allocationConfigold
          .getAllocationColumnsMap());
      allocationConfigs.setUndevelopedLUsColumns(allocationConfigold
          .getUndevelopedLUsColumns());
      allocationConfigs.setPlannedALUsFieldName(allocationConfigold
          .getPlannedALUsFieldName());

      final Set<PlannedALU> oldPlannedALUs = allocationConfigold
          .getPlannedALUs();
      final Set<PlannedALU> newPlannedALUs = new HashSet<PlannedALU>();

      for (final PlannedALU oldPlannedALU : oldPlannedALUs) {
        final PlannedALU newPlannedALU = new PlannedALU();
        newPlannedALU.setLabel(oldPlannedALU.getLabel());

        final Set<Entry<String, String>> associatedALUs = oldPlannedALU
            .getAssociatedALUsMap().entrySet();
        final Map<String, String> map = new HashMap<String, String>();

        for (final Entry<String, String> entryLU : associatedALUs) {
          final AllocationLU associatedLU = restoreProject
              .getExistingLandUseByLabel(entryLU.getValue());

          map.put(associatedLU.getId(), associatedLU.getLabel());
        }
        newPlannedALU.setAssociatedALUsMap(map);
        newPlannedALUs.add(newPlannedALU);

      }
      allocationConfigs.setPlannedALUs(newPlannedALUs);
      allocationConfigs.setInfrastructureALUs(allocationConfigold
          .getInfrastructureALUs());
      allocationConfigs.setGrowthPatternALUs(allocationConfigold
          .getGrowthPatternALUs());

      allocationConfigs.setColorALUs(allocationConfigold.getColorALUs());



      final AllocationConfigs savedAllocationConfigs = allocationConfigsDao
          .persistAllocationConfigs(allocationConfigs);
      LOGGER.debug("returning the allocationConfigs with id={}",
          savedAllocationConfigs.getId());
      restoreProject.setAllocationConfigsId(savedAllocationConfigs.getId());
    }

    // //// new restore for Allocation Control scenario
    final Set<AllocationControlScenario> allocationControlScenarios = projectReport
        .getAllocationControlScenarios();
    LOGGER.debug("Restoring {} allocationControlScenarios",
        allocationControlScenarios.size());

    for (final AllocationControlScenario oldallocationControlScenario : allocationControlScenarios) {

      AllocationControlScenario restoreallocationControlScenario = new AllocationControlScenario();
      restoreallocationControlScenario.setLabel(oldallocationControlScenario
          .getLabel());
      restoreallocationControlScenario.setProjectId(restoreProject.getId());
      restoreallocationControlScenario = allocationControlScenarioDao
          .persistAllocationControlScenario(restoreallocationControlScenario);

      restoreallocationControlScenario
      .setInfrastructureUses(oldallocationControlScenario
          .getInfrastructureUses());
      restoreallocationControlScenario
      .setGrowthPatternControl(oldallocationControlScenario
          .getGrowthPatternControl());
      restoreallocationControlScenario
      .setGrowthPatternControlLabels(oldallocationControlScenario
          .getGrowthPatternControlLabels());

      restoreallocationControlScenario
      .setPlannedlandUseControl(oldallocationControlScenario
          .getPlannedlandUseControl());

      restoreallocationControlScenario
      .setInfrastructureControl(oldallocationControlScenario
          .getInfrastructureControl());

      restoreallocationControlScenario
      .setInfrastructureControlLabels(oldallocationControlScenario
          .getInfrastructureControlLabels());

      allocationControlScenarioDao
      .updateAllocationControlScenario(restoreallocationControlScenario);
      restoreProject.getAllocationControlScenariosMap().put(
          restoreallocationControlScenario.getId(),
          restoreallocationControlScenario.getLabel());
    }

    // //// new restore for Demand scenario
    final Set<DemandScenario> demandcenarios = projectReport
        .getDemandScenarios();
    LOGGER.debug("Restoring {} demandcenarios", demandcenarios.size());

    final Set<AllocationLU> allocationLUs = restoreProject
        .getAllocationLandUses();

    for (final DemandScenario olddemandScenario : demandcenarios) {

      DemandScenario restoredemandScenario = new DemandScenario();
      restoredemandScenario.setLabel(olddemandScenario.getLabel());
      restoredemandScenario.setProjectId(restoreProject.getId());

      restoredemandScenario
      .setDemandConfig(olddemandScenario.getDemandConfig());

      final Set<DemandInfo> oldDemandInfos = olddemandScenario.getDemandInfos();
      final Set<DemandInfo> newDemandInfos = new HashSet<DemandInfo>();

      final Set<DensityDemandInfo> oldDensityDemandInfo = olddemandScenario
          .getDensityDemandInfo();

      for (final DemandInfo demandinfo : oldDemandInfos) {
        DemandInfo demandinfoNew = new DemandInfo();
        if (demandinfo instanceof ResidentialDemandInfo) {

          final ResidentialDemandInfo rdemandInfo = new ResidentialDemandInfo();
          final String oldID = ((ResidentialDemandInfo) demandinfo)
              .getResidentialLUId();

          for (final AllocationLU oldAllocationLU : allocationLandUses) {
            if (oldAllocationLU.getId().equals(oldID)) {
              for (final AllocationLU newALU : allocationLUs) {
                if (newALU.getLabel().equals(oldAllocationLU.getLabel())) {
                  // ((ResidentialDemandInfo)demandinfoNew).setResidentialLUId(newALU.getId());
                  rdemandInfo.setResidentialLUId(newALU.getId());
                  rdemandInfo.setAllocationLUId(newALU.getId());
                }
              }
            }
          }
          rdemandInfo.setCurrentDensity(((ResidentialDemandInfo) demandinfo)
              .getCurrentDensity());
          rdemandInfo
          .setFutureBreakdownByHType(((ResidentialDemandInfo) demandinfo)
              .getFutureBreakdownByHType());
          rdemandInfo.setFutureDensity(((ResidentialDemandInfo) demandinfo)
              .getFutureDensity());
          rdemandInfo.setInfillRate(((ResidentialDemandInfo) demandinfo)
              .getInfillRate());
          rdemandInfo.setFutureVacancyRate(((ResidentialDemandInfo) demandinfo)
              .getFutureVacancyRate());

          newDemandInfos.add(rdemandInfo);

        } else if (demandinfo instanceof EmploymentDemandInfo) {
          demandinfoNew = demandinfo;
          newDemandInfos.add(demandinfoNew);
        }

      }

      restoredemandScenario.setDemandInfos(newDemandInfos);
      restoredemandScenario.setDemographicTrend(olddemandScenario
          .getDemographicTrend());

      restoredemandScenario.setDemographicTrendLabel(olddemandScenario
          .getDemographicTrendLabel());

      restoredemandScenario.setFeatureFieldName(olddemandScenario
          .getFeatureFieldName());

      restoredemandScenario.setLocalDatas(olddemandScenario.getLocalDatas());

      restoredemandScenario.setDensityDemandInfo(oldDensityDemandInfo);

      restoredemandScenario = demandScenarioDao
          .persistDemandScenario(restoredemandScenario);

      // demandScenarioDao.updateDemandScenario(restoredemandScenario);
      restoreProject.getDemandScenariosMap().put(restoredemandScenario.getId(),
          restoredemandScenario.getLabel());
    }

    // restore for demand config
    final DemandConfig demandConfig = new DemandConfig();
    demandConfig.setProjectId(newProjectId);
    LOGGER.debug("persisting the demandConfig for project ={}",
        restoreProject.getLabel());

    final DemandConfig demandConfigold = projectReport.getDemandConfig();

    if (demandConfigold != null) {

      demandConfig.setLabel(demandConfigold.getLabel());
      demandConfig.setBaseYear(demandConfigold.getBaseYear());
      demandConfig
      .setClippedEnumerationDistrictAreaFeatureFieldName(demandConfigold
          .getClippedEnumerationDistrictAreaFeatureFieldName());

      final CurrentDemographic newCurrentDemographic = new CurrentDemographic();
      final CurrentDemographic oldCurrentDemographic = demandConfigold
          .getCurrentDemographic();
      newCurrentDemographic.setLabel(oldCurrentDemographic.getLabel());
      newCurrentDemographic.setYear(oldCurrentDemographic.getYear());
      newCurrentDemographic.setTotalPopulation(oldCurrentDemographic
          .getTotalPopulation());
      newCurrentDemographic.setgQPopulation(oldCurrentDemographic
          .getgQPopulation());
      newCurrentDemographic
      .setHouseholds(oldCurrentDemographic.getHouseholds());
      newCurrentDemographic.setHousingUnits(oldCurrentDemographic
          .getHousingUnits());
      newCurrentDemographic.setVacancyRate(oldCurrentDemographic
          .getVacancyRate());
      newCurrentDemographic
      .setVacantLand(oldCurrentDemographic.getVacantLand());

      final Set<ResidentialCurrentData> setNewRes = new HashSet<ResidentialCurrentData>();
      for (final ResidentialCurrentData oldRescur : oldCurrentDemographic
          .getResidentialCurrentData()) {
        final ResidentialCurrentData newRes = new ResidentialCurrentData();
        newRes.setNumberOfHousingUnits(oldRescur.getNumberOfHousingUnits());
        newRes.setBreakdownDensity(oldRescur.getBreakdownDensity());
        newRes.setDensity(oldRescur.getDensity());
        newRes.setVacancyRate(oldRescur.getVacancyRate());
        for (final AllocationLU oldAllocationLU : allocationLandUses) {
          if (oldAllocationLU.getId().equals(oldRescur.getResidentialLUId())) {
            for (final AllocationLU newALU : allocationLUs) {
              if (newALU.getLabel().equals(oldAllocationLU.getLabel())) {
                newRes.setResidentialLUId(newALU.getId());
              }
            }
          }
        }
        setNewRes.add(newRes);
      }
      newCurrentDemographic.setEmploymentCurrentDatas(oldCurrentDemographic
          .getEmploymentCurrentDatas());
      newCurrentDemographic.setResidentialCurrentData(setNewRes);
      demandConfig.setCurrentDemographic(newCurrentDemographic);

      demandConfig.setDemographicTrends(demandConfigold.getDemographicTrends());
      demandConfig.setEmploymentGrowthRates(demandConfigold
          .getEmploymentGrowthRates());
      demandConfig.setEmploymentPastTrendInfos(demandConfigold
          .getEmploymentPastTrendInfos());
      demandConfig.setEnumerationDistrictAreaFeatureFieldName(demandConfigold
          .getEnumerationDistrictAreaFeatureFieldName());
      demandConfig.setEnumerationDistrictFeatureFieldName(demandConfigold
          .getEnumerationDistrictFeatureFieldName());
      demandConfig.setGqGrowthRate(demandConfigold.getGqGrowthRate());
      demandConfig.setGroupQuartersPopulationFeatureFieldName(demandConfigold
          .getGroupQuartersPopulationFeatureFieldName());
      demandConfig.setHouseholdsGrowthRate(demandConfigold
          .getHouseholdsGrowthRate());
      demandConfig.setIncludeTrends(demandConfigold.getIncludeTrends());
      demandConfig.setLocalDemandAreasFeatureFieldName(demandConfigold
          .getLocalDemandAreasFeatureFieldName());
      demandConfig.setLocalJurisdictions(demandConfigold
          .getLocalJurisdictions());
      demandConfig.setNumberOfHouseholdsFeatureFieldName(demandConfigold
          .getNumberOfHouseholdsFeatureFieldName());
      demandConfig.setNumberOfHousingUnitsFeatureFieldName(demandConfigold
          .getNumberOfHousingUnitsFeatureFieldName());
      demandConfig.setParsed(demandConfigold.isParsed());
      demandConfig.setPopulationGrowthRate(demandConfigold
          .getPopulationGrowthRate());
      demandConfig.setProjections(demandConfigold.getProjections());

      demandConfig.setResidentialPastTrendInfos(demandConfigold
          .getResidentialPastTrendInfos());

      final Set<EmploymentSector> oldEmploymentSectors = demandConfigold
          .getSectors();
      final Set<EmploymentSector> newEmploymentSectors = new HashSet<EmploymentSector>();

      for (final EmploymentSector mOldsec : oldEmploymentSectors) {

        final EmploymentSector newEmploymentSector = new EmploymentSector();
        newEmploymentSector.setCode(mOldsec.getCode());
        newEmploymentSector.setLabel(mOldsec.getLabel());
        newEmploymentSector.setFeatureFieldName(mOldsec.getFeatureFieldName());

        final Map<String, String> msecMap = mOldsec.getAssociatedALUsMap();
        final Map<String, String> map = new HashMap<String, String>();
        for (final String scnId : msecMap.keySet()) {
          for (final AllocationLU oldAllocationLU : allocationLandUses) {
            if (oldAllocationLU.getId().equals(scnId)) {

              for (final AllocationLU newALU : allocationLUs) {
                if (newALU.getLabel().equals(oldAllocationLU.getLabel())) {

                  map.put(newALU.getId(), newALU.getLabel());

                }
              }
            }
          }
        }
        newEmploymentSector.setAssociatedALUsMap(map);
        // newEmploymentSectors.add(newEmploymentSector);

        final Map<String, Double> msecMapPercenatge = mOldsec
            .getAssociatedALUsPercentage();
        final Map<String, Double> mapPercenatge = new HashMap<String, Double>();
        for (final String scnId : msecMapPercenatge.keySet()) {
          for (final AllocationLU oldAllocationLU : allocationLandUses) {
            if (oldAllocationLU.getId().equals(scnId)) {

              for (final AllocationLU newALU : allocationLUs) {
                if (newALU.getLabel().equals(oldAllocationLU.getLabel())) {

                  mapPercenatge.put(newALU.getId(),
                      msecMapPercenatge.get(scnId));

                }
              }
            }
          }
        }
        newEmploymentSector.setAssociatedALUsPercentage(mapPercenatge);
        newEmploymentSectors.add(newEmploymentSector);

      }

      demandConfig.setSectors(newEmploymentSectors);

      demandConfig.setTotalPopulationFeatureFieldName(demandConfigold
          .getTotalPopulationFeatureFieldName());
      demandConfig.setVacantLandFeatureFieldName(demandConfigold
          .getVacantLandFeatureFieldName());

      demandConfig.setDensityDemandInfo(demandConfigold.getDensityDemandInfo());

      final DemandConfig savedDemandConfig = demandConfigDao
          .persistDemandConfig(demandConfig);
      LOGGER.debug("returning the DemandConfig with id={}",
          savedDemandConfig.getId());
      restoreProject.setDemandConfigId(savedDemandConfig.getId());
    }

    // //// new restore for Allocation scenario
    final Set<AllocationScenario> allocationScenarios = projectReport
        .getAllocationScenarios();
    LOGGER
    .debug("Restoring {} allocationScenarios", allocationScenarios.size());

    for (final AllocationScenario oldallocationScenario : allocationScenarios) {

      AllocationScenario restoreallocationScenario = new AllocationScenario();
      restoreallocationScenario.setLabel(oldallocationScenario.getLabel());
      restoreallocationScenario.setFeatureFieldName(oldallocationScenario
          .getFeatureFieldName());

      final Set<SuitabilityScenario> oldSuitabilityScenarios = projectReport
          .getSuitabilityScenarios();

      final Map<String, String> msecSuitability = restoreProject
          .getSuitabilityScenariosMap();

      for (final SuitabilityScenario oldsuitabilityScenario : oldSuitabilityScenarios) {
        if (oldsuitabilityScenario.getId().equals(
            oldallocationScenario.getSuitabilityScenarioId())) {

          for (final String scnId : msecSuitability.keySet()) {
            if (msecSuitability.get(scnId).equals(
                oldsuitabilityScenario.getLabel())) {
              restoreallocationScenario.setSuitabilityScenarioId(scnId);
            }
          }
        }
      }

      final Set<DemandOutcome> oldDemandOutcomes = projectReport
          .getDemandOutcomes();

      final Map<String, String> msecOutcomes = restoreProject
          .getDemandOutcomesMap();

      Boolean lsw = false;
      for (final DemandOutcome oldDemandoutcome : oldDemandOutcomes) {
        if (oldDemandoutcome.getId().equals(
            oldallocationScenario.getManualdemandScenarioId())) {

          for (final String scnId : msecOutcomes.keySet()) {
            if (msecOutcomes.get(scnId).equals(oldDemandoutcome.getLabel())) {
              restoreallocationScenario.setManualdemandScenarioId(scnId);
              lsw = true;
            }
          }
        }
      }

      if (lsw == false) {
        final Set<DemandScenario> oldDemandScenarios = projectReport
            .getDemandScenarios();

        final Map<String, String> msecScenarios = restoreProject
            .getDemandScenariosMap();

        for (final DemandScenario oldDemandScenario : oldDemandScenarios) {
          if (oldDemandScenario.getId().equals(
              oldallocationScenario.getManualdemandScenarioId())) {
            for (final String scnId : msecScenarios.keySet()) {
              if (msecScenarios.get(scnId).equals(oldDemandScenario.getLabel())) {
                restoreallocationScenario.setManualdemandScenarioId(scnId);
                lsw = true;
              }
            }
          }
        }
      }

      final Set<AllocationControlScenario> oldControlScenarios = projectReport
          .getAllocationControlScenarios();

      final Map<String, String> msecControl = restoreProject
          .getAllocationControlScenariosMap();

      lsw = false;
      for (final AllocationControlScenario oldControlScenario : oldControlScenarios) {
        if (oldControlScenario.getId().equals(
            oldallocationScenario.getControlScenarioId())) {

          for (final String scnId : msecControl.keySet()) {
            if (msecControl.get(scnId).equals(oldControlScenario.getLabel())) {
              restoreallocationScenario.setControlScenarioId(scnId);
              lsw = true;
            }
          }
        }
      }
      if (lsw == false) {
        restoreallocationScenario.setControlScenarioId("None");
      }

      restoreallocationScenario.setDemandScenarioId(oldallocationScenario
          .getDemandScenarioId());

      final Map<String, Integer> msecMap = oldallocationScenario
          .getLandUseOrderMap();
      final Map<String, Integer> map = new HashMap<String, Integer>();
      for (final String scnId : msecMap.keySet()) {
        for (final AllocationLU oldAllocationLU : allocationLandUses) {
          if (oldAllocationLU.getId().equals(scnId)) {

            for (final AllocationLU newALU : allocationLUs) {
              if (newALU.getLabel().equals(oldAllocationLU.getLabel())) {
                map.put(newALU.getId(), msecMap.get(scnId));
              }
            }
          }
        }
      }
      restoreallocationScenario.setLandUseOrderMap(map);

      restoreallocationScenario.setProjectId(restoreProject.getId());
      // restoreallocationScenario.setReady(false);
      restoreallocationScenario.setReady(oldallocationScenario.isReady());
      restoreallocationScenario.setManual(oldallocationScenario.isManual());

      restoreallocationScenario = allocationScenarioDao
          .persistAllocationScenario(restoreallocationScenario);

      // allocationScenarioDao.updateAllocationScenario(restoreallocationScenario);
      restoreProject.getAllocationScenariosMap().put(
          restoreallocationScenario.getId(),
          restoreallocationScenario.getLabel());
    }

    // ////////////
    wifProjectDao.updateProject(restoreProject);

    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////
    demandConfigService.updateDemandConfig(demandConfig, restoreProject.getId());

    final List<DemandScenario> lst = demandScenarioService.getDemandScenarios(restoreProject.getId());
    for (final DemandScenario demandScenario: lst)
    {
      demandScenarioService.updateDemandScenario(demandScenario, restoreProject.getId());
    }

    final List<SuitabilityScenario> lstSuit= suitabilityScenarioService.getSuitabilityScenarios(restoreProject.getId());
    for (final SuitabilityScenario suitScen: lstSuit)
    {
      suitScen.setReady(true);
      suitabilityScenarioService.updateSuitabilityScenario(suitScen,
          restoreProject.getId());
    }

    ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////


    LOGGER.debug("Project with ID {}, and name {} is restored ", newProjectId,
        restoreProject.getLabel());
    return restoreProject;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.ProjectService#deleteProject(java.lang.String)
   */
  @Override
  public void deleteProject(final String id) throws WifInvalidInputException,
  WifInvalidConfigException {
    deleteProject(id, true);
  }

  @Override
  public Boolean PublishWMSLayer( final String tableName,
      final CoordinateReferenceSystem crs, final String projectID)
  {
    Boolean lsw = false;
    try
    {

      if (checkMSLayerExists(tableName).equals("")) {
      } else {
        deleteWMSLayer(tableName);
        lsw = true;
      }

      ////////////////////////



      final GSFeatureTypeEncoder ftEnc = new GSFeatureTypeEncoder();
      LOGGER.info("creating new Geoserver layer ={}", tableName);
      ftEnc.setName(tableName);
      ftEnc.setTitle(tableName);
      final String srs = CRS.toSRS(crs);

      LOGGER.info("Project SRS ={}", srs);
      if (srs == null) {
        LOGGER.info("PublishWMSLayer failed: srs is null or invalid");
      }
      ftEnc.setSRS(srs);

      final GSLayerEncoder layerEnc = new GSLayerEncoder();
      LOGGER.info("publishing the project layer in geoserver restUrl ={}",
          geoserverConfig.getRestUrl());
      LOGGER.info("publishing the project layer in workspace ={}, store ={} ",
          geoserverConfig.getWorkspace(), geoserverConfig.getStoreName());

      final long startTime = System.nanoTime();
      final boolean publishResult = geoserverPublisher.publishDBLayer(
          geoserverConfig.getWorkspace(), geoserverConfig.getStoreName(), ftEnc,
          layerEnc);
      final long endTime = System.nanoTime();
      final long duration = endTime - startTime;
      LOGGER.info(">>>>>>>>>>>>>>*************** GeoServer publishing took in ms "+ duration / 1000000);
      //geoserverPublisher.reload();
      if (!publishResult) {
        LOGGER.info("PublishWMSLayer failed: geoserver layer could not be created, wmsOutcome will not work!");
      }
      else
      {

        //publish style //
        final AllocationConfigs allocationConfigs = AllocationConfigsService.getAllocationConfigs(projectID);
        AllocationConfigsService.CreateStyleDemo(allocationConfigs, projectID, true);



      }

    }
    catch(final Exception e)
    {
      LOGGER.info("PublishWMSLayer failed for table: {}  ", tableName);
    }
    return lsw;
  }

  public void deleteWMSLayer(final String tableName)
      throws DataStoreUnavailableException, GeoServerConfigException {

    geoserverPublisher.removeLayer(geoserverConfig.getWorkspace(), tableName);
    geoserverPublisher.reload();
    LOGGER.info("deleting the project layer in geoserver ={}", tableName);

  }

  public String checkMSLayerExists(final String tableName) {

    String outs = "";
    try {

      final String sUrl = geoserverConfig.getRestUrl() + "rest/workspaces/"
          + geoserverConfig.getWorkspace() + "/datastores/"
          + geoserverConfig.getStoreName() + "/featuretypes/" + tableName;
      // final String str =
      // "http://localhost:7000/geoserver/rest/workspaces/envision/datastores/Envision/featuretypes/vic_maroondah_mce02_9ea2659d94165120867ab5fa0e00a754";

      LOGGER.info("checkMSLayerExists layer ={}", sUrl);

      final String aut = geoserverConfig.getUserName() + ":"
          + geoserverConfig.getPassword();
      final String plainCreds = aut; // "admin:geoserver";
      final byte[] plainCredsBytes = plainCreds.getBytes();

      final byte[] base64CredsBytes = org.apache.commons.codec.binary.Base64
          .encodeBase64(plainCredsBytes);
      final String base64Creds = new String(base64CredsBytes);

      final HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Basic " + base64Creds);

      /*
       * final HttpHeaders headers = new HttpHeaders(); headers.add("username",
       * "admin"); headers.add("password", "geoserver");
       */
      final HttpEntity<String> entity = new HttpEntity<String>("parameters",
          headers);

      final RestTemplate restTemplate = new RestTemplate();
      final ResponseEntity<String> st = restTemplate.exchange(sUrl,
          HttpMethod.GET, entity, String.class);
      outs = st.getBody();
    } catch (final Exception e) {
      LOGGER.info("checkMSLayerExists Error: " + e.toString());
    }
    return outs;

  }

}
