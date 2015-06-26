/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigNewException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.DemandSetupCouchParser;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.demand.DemandConfigNew;
import au.org.aurin.wif.repo.demand.DemandConfigNewDao;
import au.org.aurin.wif.repo.demand.DemandOutcomeDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.DemandConfigNewService;

/**
 * The Class DemandConfigServiceImpl.
 */
@Service
@Qualifier("DemandConfigNewService")
public class DemandConfigNewServiceImpl implements DemandConfigNewService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandConfigNewServiceImpl.class);

  /** The demand config dao. */
  @Autowired
  private DemandConfigNewDao DemandConfigNewDao;

  /** The demand scenario dao. */
  @Autowired
  private DemandOutcomeDao manualdemandScenarioDao;

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
  public DemandConfigNew createDemandConfigNew(DemandConfigNew DemandConfigNew,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, IncompleteDemandConfigNewException {
    if (DemandConfigNew == null) {
      LOGGER
          .error("createDemandConfig failed: demandConfig is null or invalid");
      throw new WifInvalidInputException(
          "createDemandConfig failed: demandConfig is null or invalid");
    }

    LOGGER.debug("createDemandConfig for project  id ={}", projectId);
    WifProject project = projectService.getProject(projectId);
    project = projectParser.parse(project);
    DemandConfigNew.setProjectId(projectId);

    LOGGER.debug("persisting the demandConfig for project ={}",
        project.getLabel());
    DemandConfigNew manualsavedDemandConfig = DemandConfigNewDao
        .persistDemandConfigNew(DemandConfigNew);
    LOGGER.debug("returning the demandConfig with id={}",
        manualsavedDemandConfig.getId());
    project.setDemandConfigNewId(manualsavedDemandConfig.getId());
    // TODO Find out if it should be done through the service, but it is much
    // more efficient to
    wifProjectDao.updateProject(project);
    return manualsavedDemandConfig;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DemandConfigService#getDemandConfig(java.lang.String)
   */
  public DemandConfigNew getDemandConfigNew(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    String DemandConfigNewId = null;
    LOGGER.debug("getting the demandConfig for project with ID={}", projectId);
    String msg = "illegal argument, the ID " + DemandConfigNewId
        + " supplied doesn't identify a valid demandConfig ";
    try {
      WifProject project = projectService.getProjectNoMapping(projectId);
      project = projectParser.parse(project);
      DemandConfigNew DemandConfigNew = null;
      DemandConfigNewId = project.getDemandConfigNewId();
      LOGGER.info("getting the demandConfig with ID={}", DemandConfigNewId);
      if (DemandConfigNewId != null) {
        DemandConfigNew = DemandConfigNewDao
            .findDemandConfigNewById(DemandConfigNewId);
        if (DemandConfigNew == null) {
          msg = "illegal argument, the DemandConfigNew with the ID "
              + DemandConfigNewId + " supplied was not found ";
          LOGGER.error(msg);
          throw new InvalidEntityIdException(msg);
        } else {
          // DemandConfigNew = demandSetupParser.parse(DemandConfigNew,
          // project);
          project.setDemandConfigNew(DemandConfigNew);
          // project = parseALUSectors(demandConfig, project);
          DemandConfigNew.setWifProject(project);
        }
        return DemandConfigNew;
      } else {
        // LOGGER.error(msg);
        // throw new InvalidEntityIdException(msg);
        return null;
      }
    } catch (IllegalArgumentException e) {

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
  public void updateDemandConfigNew(DemandConfigNew DemandConfigNew,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("updating demandConfig: {}, with id: {}",
        DemandConfigNew.getLabel(), DemandConfigNew.getId());
    try {
      WifProject project = projectService.getProject(projectId);
      if (DemandConfigNew.getProjectId().equals(projectId)) {

        // demandConfig = demandSetupParser.parse(demandConfig, project);
        DemandConfigNew.setRevision(DemandConfigNewDao.findDemandConfigNewById(
            DemandConfigNew.getId()).getRevision());
        DemandConfigNewDao.updateDemandConfigNew(DemandConfigNew);
      } else {
        String msg = "illegal argument, the ID " + projectId
            + " supplied is not associated with this demandConfig ";
        LOGGER.error(msg);
        throw new WifInvalidInputException(msg);
      }
    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the demandConfig supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the demandConfig supplied is invalid ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DemandConfigService#deleteDemandConfig(java.lang.String
   * , java.lang.String)
   */
  public void deleteDemandConfigNew(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the demandConfig from project with ID={}", projectId);
    try {
      DemandConfigNew DemandConfigNew = getDemandConfigNew(projectId);
      WifProject project = wifProjectDao.findProjectById(projectId);
      project.setDemandConfigNewId(null);
      project.setDemandConfigNew(null);
      wifProjectDao.updateProject(project);
      DemandConfigNewDao.deleteDemandConfigNew(DemandConfigNew);
    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + projectId
          + " supplied doesn't identify a valid demandConfigNew ");
      throw new InvalidEntityIdException("illegal argument, the ID "
          + projectId + " supplied doesn't identify a valid demandConfigNew ");
    }
  }
}
