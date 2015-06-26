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
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioNewException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenarioNew;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.DemandScenarioNewDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioNewService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * <b>DemandScenarioNewServiceImpl.java</b> : Implementation of @see
 * DemandScenarioNewService
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Service
@Qualifier("DemandScenarioNewService")
public class DemandScenarioNewServiceImpl implements DemandScenarioNewService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioNewServiceImpl.class);

  /** The wif DemandScenarioNew dao. */
  @Autowired
  private DemandScenarioNewDao DemandScenarioNewDao;

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

  /** The suitability analyzer. */
  @Autowired
  private DemandAnalyzer demandAnalyzer;

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
    LOGGER.trace("DemandScenarioNew Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.DemandScenarioNewService#
   * createDemandScenarioNew
   * (au.org.aurin.wif.model.suitability.DemandScenarioNew, java.lang.String)
   */
  public DemandScenarioNew createDemandScenarioNew(
      final DemandScenarioNew DemandScenarioNew, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException, IncompleteDemandScenarioNewException {
    if (DemandScenarioNew == null) {
      LOGGER
          .error("createDemandScenarioNew failed: DemandScenarioNew is null or invalid");
      throw new WifInvalidInputException(
          "createDemandScenarioNew failed: DemandScenarioNew is null or invalid");
    }
    final WifProject project = projectService.getProject(projectId);
    // ManualDemandConfig manualdemandConfig = manualdemandConfigService
    // .getManualDemandConfig(projectId);
    // project.setManualDemandConfig(manualdemandConfig);
    try {
      // DemandScenarioNew = DemandScenarioNewParser.parse(
      // DemandScenarioNew, manualdemandConfig, project);
    } catch (final Exception e) {
      LOGGER.error("Parsing new scenario failed", e);
      throw new IncompleteDemandScenarioNewException(
          "Parsing new scenario failed", e);
    }
    LOGGER.debug("persisting the DemandScenarioNew={}",
        DemandScenarioNew.getLabel());
    DemandScenarioNew.setProjectId(projectId);
    final DemandScenarioNew savedDemandScenarioNew = DemandScenarioNewDao
        .persistDemandScenarioNew(DemandScenarioNew);
    LOGGER.debug("returning the DemandScenarioNew with id={}",
        savedDemandScenarioNew.getId());
    project.getDemandScenariosNewMap().put(savedDemandScenarioNew.getId(),
        savedDemandScenarioNew.getLabel());
    wifProjectDao.updateProject(project);
    return savedDemandScenarioNew;
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
  public DemandScenarioNew getDemandScenarioNew(final String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the DemandScenarioNew with ID={}", id);
    try {
      final DemandScenarioNew DemandScenarioNew = DemandScenarioNewDao
          .findDemandScenarioNewById(id);
      if (DemandScenarioNew == null) {
        LOGGER.error("illegal argument, the DemandScenarioNew with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the DemandScenarioNew with the ID " + id
                + " supplied was not found ");
      }
      final String projectId = DemandScenarioNew.getProjectId();
      final WifProject project = projectService.getProject(projectId);
      final DemandConfig demandConfig = demandConfigService
          .getDemandConfig(projectId);
      project.setDemandConfig(demandConfig);
      // DemandScenarioNew = DemandScenarioNewParser.parse(
      // DemandScenarioNew, manualdemandConfig, project);
      // DemandScenarioNew.setManualDemandConfig(manualdemandConfig);
      // DemandScenarioNew = DemandScenarioNewParser
      // .parseAreaRequirements(DemandScenarioNew);

      DemandScenarioNew.setWifProject(project);
      return DemandScenarioNew;

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid DemandScenarioNew ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid DemandScenarioNew ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioNewService#getDemandScenarioNew(
   * java.lang.String)
   */
  public DemandScenarioNew getDemandScenarioNew(final String id,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    final DemandScenarioNew DemandScenarioNew = getDemandScenarioNew(id);
    if (DemandScenarioNew.getProjectId().equals(projectId)) {
      return DemandScenarioNew;
    } else {
      LOGGER
          .error("illegal argument, the DemandScenarioNew supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the DemandScenarioNew supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioNewService#updateDemandScenarioNew
   * (au.org.aurin.wif.model.allocation.DemandScenarioNew, java.lang.String)
   */
  public void updateDemandScenarioNew(
      final DemandScenarioNew DemandScenarioNew, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("updating DemandScenarioNew: {}, with id: {}",
        DemandScenarioNew.getLabel(), DemandScenarioNew.getId());
    try {
      if (DemandScenarioNew.getProjectId().equals(projectId)) {
        DemandScenarioNew
            .setRevision(DemandScenarioNewDao.findDemandScenarioNewById(
                DemandScenarioNew.getId()).getRevision());
        DemandScenarioNewDao.updateDemandScenarioNew(DemandScenarioNew);
      } else {
        LOGGER
            .error("illegal argument, the DemandScenarioNew supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the DemandScenarioNew supplied doesn't belong to project: "
                + projectId);
      }
    } catch (final IllegalArgumentException e) {

      LOGGER
          .error("illegal argument, the DemandScenarioNew supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the DemandScenarioNew supplied is invalid ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioNewService#deleteDemandScenarioNew
   * (java.lang.String, java.lang.String)
   */
  public void deleteDemandScenarioNew(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("deleting the DemandScenarioNew with ID={}", id);
    final DemandScenarioNew DemandScenarioNew = DemandScenarioNewDao
        .findDemandScenarioNewById(id);
    if (DemandScenarioNew.getProjectId().equals(projectId)) {
      // Deleting associated area requirements
      final List<AreaRequirement> requirements = areaRequirementDao
          .getAreaRequirements(DemandScenarioNew.getId());
      for (final AreaRequirement areaRequirement : requirements) {
        areaRequirementDao.deleteAreaRequirement(areaRequirement);
      }
      DemandScenarioNewDao.deleteDemandScenarioNew(DemandScenarioNew);
      final WifProject project = projectService.getProject(projectId);
      project.getDemandScenariosNewMap().remove(id);
      wifProjectDao.updateProject(project);
    } else {
      LOGGER
          .error("illegal argument, the DemandScenarioNew supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the DemandScenarioNew supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.DemandScenarioNewService#getDemandScenarioNews
   * (java.lang.String)
   */
  public List<DemandScenarioNew> getDemandScenarioNews(final String projectID)
      throws WifInvalidInputException {
    LOGGER.info("getting all DemandScenarioNews for projectID: {} ", projectID);

    return DemandScenarioNewDao.getDemandScenarioNews(projectID);
  }

}
