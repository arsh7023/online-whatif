/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.HashSet;
import java.util.List;
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
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.DemandScenarioCouchParser;
import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * <b>DemandScenarioServiceImpl.java</b> : Implementation of @see
 * DemandScenarioService
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Service
@Qualifier("demandScenarioService")
public class DemandScenarioServiceImpl implements DemandScenarioService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioServiceImpl.class);

  /** The wif demandScenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The parser. */
  @Autowired
  private DemandSetupCouchParser demandSetupParser;

  /** The parser. */
  @Autowired
  private DemandScenarioCouchParser demandScenarioParser;

  /** the ProjectService. */
  @Resource
  private ProjectService projectService;

  /** The suitability analyzer. */
  @Autowired
  private DemandAnalyzer demandAnalyzer;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * to handle destroy.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("DemandScenario Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.DemandScenarioService#
   * createDemandScenario (au.org.aurin.wif.model.suitability.DemandScenario,
   * java.lang.String)
   */
  public DemandScenario createDemandScenario(DemandScenario demandScenario,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException {
    if (demandScenario == null) {
      LOGGER
          .error("createDemandScenario failed: demandScenario is null or invalid");
      throw new WifInvalidInputException(
          "createDemandScenario failed: demandScenario is null or invalid");
    }
    final WifProject project = projectService.getProject(projectId);
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(projectId);
    project.setDemandConfig(demandConfig);
    try {
      demandScenario = demandScenarioParser.parse(demandScenario, demandConfig,
          project);
    } catch (final Exception e) {
      LOGGER.error("Parsing new scenario failed", e);
      throw new IncompleteDemandScenarioException(
          "Parsing new scenario failed", e);
    }
    LOGGER.debug("persisting the demandScenario={}", demandScenario.getLabel());
    demandScenario.setProjectId(projectId);
    final DemandScenario savedDemandScenario = demandScenarioDao
        .persistDemandScenario(demandScenario);
    LOGGER.debug("returning the demandScenario with id={}",
        savedDemandScenario.getId());
    project.getDemandScenariosMap().put(savedDemandScenario.getId(),
        savedDemandScenario.getLabel());
    wifProjectDao.updateProject(project);
    return savedDemandScenario;
  }

  /**
   * Gets the suitability scenario.
   * 
   * @param id
   *          the id
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  public DemandScenario getDemandScenario(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the demandScenario with ID={}", id);
    try {
      DemandScenario demandScenario = demandScenarioDao
          .findDemandScenarioById(id);
      if (demandScenario == null) {
        LOGGER.error("illegal argument, the demandScenario with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the demandScenario with the ID " + id
                + " supplied was not found ");
      }
      final String projectId = demandScenario.getProjectId();
      final WifProject project = projectService.getProject(projectId);
      final DemandConfig demandConfig = demandConfigService
          .getDemandConfig(projectId);
      project.setDemandConfig(demandConfig);
      demandScenario = demandScenarioParser.parse(demandScenario, demandConfig,
          project);
      demandScenario.setDemandConfig(demandConfig);
      demandScenario = demandScenarioParser
          .parseAreaRequirements(demandScenario);

      demandScenario.setWifProject(project);
      return demandScenario;

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid demandScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid demandScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioService#getDemandScenario(
   * java.lang.String)
   */
  public DemandScenario getDemandScenario(final String id,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    final DemandScenario demandScenario = getDemandScenario(id);
    if (demandScenario.getProjectId().equals(projectId)) {
      return demandScenario;
    } else {
      LOGGER
          .error("illegal argument, the demandScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the demandScenario supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioService#updateDemandScenario
   * (au.org.aurin.wif.model.allocation.DemandScenario, java.lang.String)
   */
  public void updateDemandScenario(final DemandScenario demandScenario,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("updating demandScenario: {}, with id: {}",
        demandScenario.getLabel(), demandScenario.getId());
    try {
      if (demandScenario.getProjectId().equals(projectId)) {
        demandScenario.setRevision(demandScenarioDao.findDemandScenarioById(
            demandScenario.getId()).getRevision());

        // /new claudia
        final DemandConfig demandConfig = demandConfigService
            .getDemandConfig(projectId);

        Set<DensityDemandInfo> setScen = new HashSet<DensityDemandInfo>();
        final Set<DensityDemandInfo> setNew = new HashSet<DensityDemandInfo>();
        Set<DensityDemandInfo> setDconfig = new HashSet<DensityDemandInfo>();
        setDconfig = demandConfig.getDensityDemandInfo();
        setScen = demandScenario.getDensityDemandInfo();

        for (final DensityDemandInfo dsConf : setDconfig) {
          final DensityDemandInfo nadd = new DensityDemandInfo();
          nadd.setCurrentDensity(dsConf.getCurrentDensity());
          nadd.setInfillRate(dsConf.getInfillRate());
          nadd.setFutureDensity(dsConf.getFutureDensity());
          nadd.setLanduseID(dsConf.getLanduseID());
          nadd.setLanduseName(dsConf.getLanduseName());

          for (final DensityDemandInfo dsScen : setScen) {
            if (dsScen.getLanduseID().equals(dsConf.getLanduseID())) {
              nadd.setInfillRate(dsScen.getInfillRate());
              nadd.setFutureDensity(dsScen.getFutureDensity());
            }
          }
          setNew.add(nadd);
        }

        demandScenario.setDensityDemandInfo(setNew);

        // end claudia

        demandScenarioDao.updateDemandScenario(demandScenario);
      } else {
        LOGGER
            .error("illegal argument, the demandScenario supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the demandScenario supplied doesn't belong to project: "
                + projectId);
      }
    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the demandScenario supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the demandScenario supplied is invalid ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioService#deleteDemandScenario
   * (java.lang.String, java.lang.String)
   */
  public void deleteDemandScenario(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("deleting the demandScenario with ID={}", id);
    final DemandScenario demandScenario = demandScenarioDao
        .findDemandScenarioById(id);
    if (demandScenario.getProjectId().equals(projectId)) {
      // Deleting associated area requirements
      final List<AreaRequirement> requirements = areaRequirementDao
          .getAreaRequirements(demandScenario.getId());
      for (final AreaRequirement areaRequirement : requirements) {
        areaRequirementDao.deleteAreaRequirement(areaRequirement);
      }
      demandScenarioDao.deleteDemandScenario(demandScenario);
      final WifProject project = projectService.getProject(projectId);
      project.getDemandScenariosMap().remove(id);
      wifProjectDao.updateProject(project);
    } else {
      LOGGER
          .error("illegal argument, the demandScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the demandScenario supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioService#getDemandScenarios
   * (java.lang.String)
   */
  public List<DemandScenario> getDemandScenarios(final String projectID)
      throws WifInvalidInputException {
    // LOGGER.info("getting all demandScenarios for projectID: {} ", projectID);

    return demandScenarioDao.getDemandScenarios(projectID);
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.DemandScenarioService#getOutcome(
   * java.lang.String, java.lang.String, java.lang.String)
   */
  public List<AreaRequirement> getOutcome(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandScenarioException {
    LOGGER.info("getOutcome for demand scenario ID: {} ", id);
    final DemandScenario demandScenario = getDemandScenario(id);
    return getOutcome(demandScenario);

  }

  public List<AreaRequirement> getOutcome(final DemandScenario demandScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandScenarioException {
    LOGGER.debug("getOutcome for demand scenario: {} ",
        demandScenario.getLabel());

    // Double association not supported by couchDB, that make the complex demand
    // algorithm much easier
    final Set<LocalData> localDatas = demandScenario.getLocalDatas();

    for (final LocalData localData : localDatas) {
      final LocalJurisdiction localJurisdiction = demandScenario
          .getWifProject().getDemandConfig()
          .getLocalJurisdictionByLabel(localData.getLocalJurisdictionLabel());
      localJurisdiction.addLocalData(localData);
    }

    try {
      return demandAnalyzer.doDemandAnalysis(demandScenario);
    } catch (final Exception e) {
      LOGGER.error("getOutcome failed", e);
      throw new IncompleteDemandScenarioException("getOutcome failed", e);
    }
  }
}
