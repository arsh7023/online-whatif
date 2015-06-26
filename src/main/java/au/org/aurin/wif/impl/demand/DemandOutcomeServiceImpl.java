/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.List;

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
import au.org.aurin.wif.exception.validate.IncompleteDemandOutcomeException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * <b>ManualDemandScenarioServiceImpl.java</b> : Implementation of @see
 * ManualDemandScenarioService
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Service
@Qualifier("DemandOutcomeService")
public class DemandOutcomeServiceImpl implements DemandOutcomeService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandOutcomeServiceImpl.class);

  /** The wif ManualDemandScenario dao. */
  @Autowired
  private DemandOutcomeDao demandOutcomeDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  // /** The parser. */
  // @Autowired
  // private DemandSetupCouchParser demandSetupParser;

  /** The parser. */

  /** the ProjectService. */
  @Resource
  private ProjectService projectService;

  /** The suitability analyzer. */
  // @Autowired
  // private DemandAnalyzer demandAnalyzer;

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
    LOGGER.trace("ManualDemandScenario Service succesfully cleared! ");
  }

  public DemandOutcome createDemandOutcome(final List<AreaRequirement> outcome,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    final DemandOutcome newDemandOutcome = new DemandOutcome();
    newDemandOutcome.setLabel("automated-manualdemand");
    LOGGER.debug("creating a new ManualDemandScenario={}",
        newDemandOutcome.getLabel());
    final WifProject project = projectService.getProject(projectId);
    newDemandOutcome.setProjectId(project.getId());
    for (final AreaRequirement areaRequirement : outcome) {
      // final ManualAreaRequirement manualAreaRequirement = new
      // ManualAreaRequirement();
      // manualAreaRequirement.setAllocationLU(areaRequirement.getAllocationLU());
      // manualAreaRequirement.setRequiredArea(areaRequirement.getRequiredArea());
      // manualAreaRequirement.setProjection(areaRequirement.getProjection());

      areaRequirement.setProjectionLabel(areaRequirement.getProjection()
          .getLabel());

      newDemandOutcome.addAreaRequirenment(areaRequirement);
      LOGGER.info(
          "recreating manually required area for land use  {} in projection "
              + areaRequirement.getProjection().getLabel() + " is : {}",
          areaRequirement.getAllocationLU().getLabel(),
          areaRequirement.getRequiredArea());
    }

    final DemandOutcome savedDemandOutcome = demandOutcomeDao
        .persistDemandOutcome(newDemandOutcome);
    LOGGER.debug("returning the ManualDemandScenario with id={}",
        savedDemandOutcome.getId());
    return savedDemandOutcome;

  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.ManualDemandScenarioService#
   * createManualDemandScenario
   * (au.org.aurin.wif.model.suitability.ManualDemandScenario, java.lang.String)
   */
  public DemandOutcome createDemandOutcomeNew(
      final DemandOutcome demandOutcome, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandOutcomeException {
    if (demandOutcome == null) {
      LOGGER
          .error("createManualDemandScenario failed: ManualDemandScenario is null or invalid");
      throw new WifInvalidInputException(
          "createManualDemandScenario failed: ManualDemandScenario is null or invalid");
    }
    final WifProject project = projectService.getProject(projectId);
    // final ManualDemandConfig manualdemandConfig = manualdemandConfigService
    // .getManualDemandConfig(projectId);
    final DemandConfig demandConfig = demandConfigService
        .getDemandConfig(projectId);
    project.setDemandConfig(demandConfig);
    // project.setManualDemandConfig(manualdemandConfig);
    try {
      // ManualDemandScenario = ManualDemandScenarioParser.parse(
      // ManualDemandScenario, manualdemandConfig, project);
    } catch (final Exception e) {
      LOGGER.error("Parsing new scenario failed", e);
      throw new IncompleteDemandOutcomeException("Parsing new scenario failed",
          e);
    }
    LOGGER.debug("persisting the DemandOutcome={}", demandOutcome.getLabel());
    demandOutcome.setProjectId(projectId);
    final DemandOutcome savedDemandOutcome = demandOutcomeDao
        .persistDemandOutcome(demandOutcome);
    LOGGER.debug("returning the ManualDemandScenario with id={}",
        savedDemandOutcome.getId());
    project.getDemandOutcomesMap().put(savedDemandOutcome.getId(),
        savedDemandOutcome.getLabel());
    wifProjectDao.updateProject(project);

    return savedDemandOutcome;
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
  public DemandOutcome getDemandOutcome(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the ManualDemandScenario with ID={}", id);
    try {
      final DemandOutcome demandOutcome = demandOutcomeDao
          .findDemandOutcomeById(id);
      if (demandOutcome == null) {
        LOGGER.error("illegal argument, the ManualDemandScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the ManualDemandScenario with the ID " + id
                + " supplied was not found ");
      }
      // final String projectId = demandOutcome.getProjectId();
      // final WifProject project = projectService.getProject(projectId);
      // final ManualDemandConfig manualdemandConfig = manualdemandConfigService
      // .getManualDemandConfig(projectId);
      // project.setManualDemandConfig(manualdemandConfig);
      // ManualDemandScenario = ManualDemandScenarioParser.parse(
      // ManualDemandScenario, manualdemandConfig, project);
      // ManualDemandScenario.setManualDemandConfig(manualdemandConfig);
      // ManualDemandScenario = ManualDemandScenarioParser
      // .parseAreaRequirements(ManualDemandScenario);

      // demandOutcome.setWifProject(project);
      return demandOutcome;

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid ManualDemandScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid ManualDemandScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ManualDemandScenarioService#getManualDemandScenario(
   * java.lang.String)
   */
  public DemandOutcome getDemandOutcome(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    final DemandOutcome demandOutcome = getDemandOutcome(id);
    if (demandOutcome.getProjectId().equals(projectId)) {
      return demandOutcome;
    } else {
      LOGGER
          .error("illegal argument, the ManualDemandScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the ManualDemandScenario supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ManualDemandScenarioService#updateManualDemandScenario
   * (au.org.aurin.wif.model.allocation.ManualDemandScenario, java.lang.String)
   */
  public void updateDemandOutcome(final DemandOutcome demandOutcome,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("updating ManualDemandScenario: {}, with id: {}",
        demandOutcome.getLabel(), demandOutcome.getId());
    try {
      if (demandOutcome.getProjectId().equals(projectId)) {
        demandOutcome.setRevision(demandOutcomeDao.findDemandOutcomeById(
            demandOutcome.getId()).getRevision());
        demandOutcomeDao.updateDemandOutcome(demandOutcome);
      } else {
        LOGGER
            .error("illegal argument, the ManualDemandScenario supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the ManualDemandScenario supplied doesn't belong to project: "
                + projectId);
      }
    } catch (final IllegalArgumentException e) {

      LOGGER
          .error("illegal argument, the ManualDemandScenario supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the ManualDemandScenario supplied is invalid ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ManualDemandScenarioService#deleteManualDemandScenario
   * (java.lang.String, java.lang.String)
   */
  public void deleteDemandOutcome(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("deleting the ManualDemandScenario with ID={}", id);
    final DemandOutcome demandOutcome = demandOutcomeDao
        .findDemandOutcomeById(id);
    if (demandOutcome.getProjectId().equals(projectId)) {
      // Deleting associated area requirements
      final List<AreaRequirement> requirements = areaRequirementDao
          .getAreaRequirements(demandOutcome.getId());
      for (final AreaRequirement areaRequirement : requirements) {
        areaRequirementDao.deleteAreaRequirement(areaRequirement);
      }
      demandOutcomeDao.deleteDemandOutcome(demandOutcome);
      final WifProject project = projectService.getProject(projectId);
      project.getDemandOutcomesMap().remove(id);
      wifProjectDao.updateProject(project);
    } else {
      LOGGER
          .error("illegal argument, the ManualDemandScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the ManualDemandScenario supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ManualDemandScenarioService#getManualDemandScenarios
   * (java.lang.String)
   */
  public List<DemandOutcome> getDemandOutcomes(final String projectID)
      throws WifInvalidInputException {
    LOGGER.info("getting all ManualDemandScenarios for projectID: {} ",
        projectID);

    return demandOutcomeDao.getDemandOutcomes(projectID);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.ManualDemandScenarioService#getOutcome(
   * java.lang.String, java.lang.String, java.lang.String)
   */
  public List<AreaRequirement> getOutcome(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandOutcomeException {
    LOGGER.info("getOutcome for demand scenario ID: {} ", id);
    final DemandOutcome demandOutcome = getDemandOutcome(id);
    return getOutcome(demandOutcome);

  }

  public List<AreaRequirement> getOutcome(
      final DemandOutcome manualDemandScenario)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandOutcomeException {
    // TODO Auto-generated method stub
    return null;
  }

  // public List<AreaRequirement> getOutcome(
  // ManualDemandScenario ManualDemandScenario)
  // throws WifInvalidInputException, WifInvalidConfigException,
  // ParsingException, IncompleteManualDemandScenarioException {
  // LOGGER.debug("getOutcome for demand scenario: {} ",
  // ManualDemandScenario.getLabel());
  //
  // // Double association not supported by couchDB, that make the complex
  // demand
  // // algorithm much easier
  // /*
  // * Set<LocalData> localDatas = ManualDemandScenario.getLocalDatas(); for
  // * (LocalData localData : localDatas) { LocalJurisdiction localJurisdiction
  // * = ManualDemandScenario .getWifProject().getDemandConfig()
  // * .getLocalJurisdictionByLabel(localData.getLocalJurisdictionLabel());
  // * localJurisdiction.addLocalData(localData); }
  // */
  //
  // try {
  // return demandAnalyzer.doDemandAnalysis(ManualDemandScenario);
  // } catch (Exception e) {
  // LOGGER.error("getOutcome failed", e);
  // throw new IncompleteManualDemandScenarioException("getOutcome failed", e);
  // }
  // }
}
