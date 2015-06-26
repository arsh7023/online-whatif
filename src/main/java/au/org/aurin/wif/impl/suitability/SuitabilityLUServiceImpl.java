package au.org.aurin.wif.impl.suitability;

import java.util.ArrayList;
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
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.AllocationLUDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.svc.AllocationLUService;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityLUService;

/**
 * The Class SuitabilityLUServiceImpl.
 */
@Service
@Qualifier("suitabilityLUService")
public class SuitabilityLUServiceImpl implements SuitabilityLUService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityLUServiceImpl.class);

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The allocation lu dao. */
  @Autowired
  private AllocationLUDao allocationLUDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

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
    LOGGER.trace("SuitabilityLU Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityLUService#createSuitabilityLU
   * (au.org.aurin.wif.model.suitability.SuitabilityLU, java.lang.String)
   */
  public SuitabilityLU createSuitabilityLU(final SuitabilityLU suitabilityLU,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, InvalidLabelException {
    validate(suitabilityLU, projectId);
    final WifProject project = projectService.getProject(projectId);
    suitabilityLU.setProjectId(projectId);
    suitabilityLU.setFeatureFieldName(WifKeys.SCORE_SUFFIX
        + suitabilityLU.getLabel().replaceAll(" ", "_").replaceAll("-", "_")
            .toLowerCase());
    LOGGER.debug("persisting the suitabilityLU with id={}, label {}",
        suitabilityLU.getId(), suitabilityLU.getLabel());
    final SuitabilityLU savedSuitabilityLU = suitabilityLUDao
        .persistSuitabilityLU(suitabilityLU);
    LOGGER.debug("returning the suitabilityLU with id={}",
        savedSuitabilityLU.getId());
    project.getSuitabilityLUs().add(savedSuitabilityLU);
    projectService.updateProject(project);
    return savedSuitabilityLU;

  }

  /**
   * Validate.
   * 
   * @param suitabilityLU
   *          the suitability lu
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws InvalidLabelException
   *           the invalid label exception
   */
  private void validate(final SuitabilityLU suitabilityLU,
      final String projectId) throws WifInvalidInputException,
      InvalidLabelException {
    String message = "suitabilityLU null";
    if (suitabilityLU == null) {
      LOGGER.error(message);
      throw new WifInvalidInputException(message);
    } else if (suitabilityLU.getLabel() == null) {
      message = "label is null";
      LOGGER.error(message);
      throw new InvalidLabelException(message);
    } else {

      message = "suitabilityLU label has already been used in this project configuration";
      final List<SuitabilityLU> suitabilityLUs = suitabilityLUDao
          .getSuitabilityLUs(projectId);
      for (final SuitabilityLU suitabilityLU2 : suitabilityLUs) {
        if (suitabilityLU.getLabel().equals(suitabilityLU2.getLabel())) {
          LOGGER.error(message);
          throw new InvalidLabelException(message);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityLUService#getSuitabilityLU(
   * java.lang.String)
   */
  public SuitabilityLU getSuitabilityLU(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.debug("getting the suitabilityLU with ID={}", id);
    try {
      final SuitabilityLU suitabilityLU = suitabilityLUDao
          .findSuitabilityLUById(id);
      if (suitabilityLU == null) {
        LOGGER.error("illegal argument, the suitabilityLU with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityLU with the ID " + id
                + " supplied was not found ");
      }
      return suitabilityParser.parse(suitabilityLU);

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
    }
  }

  /**
   * Gets the suitability lu no mapping.
   * 
   * @param id
   *          the id
   * @return the suitability lu no mapping
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public SuitabilityLU getSuitabilityLUNoMapping(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.debug("getting the suitabilityLU with ID={}", id);
    try {
      final SuitabilityLU suitabilityLU = suitabilityLUDao
          .findSuitabilityLUById(id);
      if (suitabilityLU == null) {
        LOGGER.error("illegal argument, the suitabilityLU with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityLU with the ID " + id
                + " supplied was not found ");
      }
      return suitabilityLU;

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityLUService#getSuitabilityLU(java.lang.String
   * )
   */
  public SuitabilityLU getSuitabilityLU(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    final SuitabilityLU suitabilityLU = getSuitabilityLU(id);
    if (suitabilityLU.getProjectId().equals(projectId)) {
      return suitabilityLU;
    } else {
      LOGGER
          .error("illegal argument, the suitabilityLU supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the suitabilityLU supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityLUService#getSuitabilityLUNoMapping
   * (java.lang.String, java.lang.String)
   */
  public SuitabilityLU getSuitabilityLUNoMapping(final String suitabilityLUId,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    final SuitabilityLU suitabilityLU = getSuitabilityLU(suitabilityLUId);
    if (suitabilityLU.getProjectId().equals(projectId)) {
      return this.getSuitabilityLUNoMapping(suitabilityLUId);
    } else {
      LOGGER
          .error("illegal argument, the suitabilityLU supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the suitabilityLU supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityLUService#updateSuitabilityLU(au.org.aurin
   * .wif.model.allocation.SuitabilityLU, java.lang.String)
   */
  public void updateSuitabilityLU(SuitabilityLU suitabilityLU,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("updating suitabilityLU: {}, with id: {}",
        suitabilityLU.getLabel(), suitabilityLU.getId());
    try {
      final WifProject project = projectService.getProject(projectId);
      suitabilityLU.setWifProject(project);
      suitabilityLU = suitabilityParser.parse(suitabilityLU);
      suitabilityLU.setRevision(suitabilityLUDao.findSuitabilityLUById(
          suitabilityLU.getId()).getRevision());
      suitabilityLUDao.updateSuitabilityLU(suitabilityLU);
      final SuitabilityLU oldSLU = project.getSuitabilityLUById(suitabilityLU
          .getId());
      project.getSuitabilityLUs().remove(oldSLU);
      project.getSuitabilityLUs().add(suitabilityLU);
      projectService.updateProject(project);

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the suitabilityLU supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the suitabilityLU supplied is invalid ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityLUService#deleteSuitabilityLU(java.lang
   * .String, java.lang.String)
   */
  public void deleteSuitabilityLU(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the suitabilityLU with ID={}", id);
    try {
      final SuitabilityLU suitabilityLU = suitabilityLUDao
          .findSuitabilityLUById(id);
      if (suitabilityLU == null) {
        LOGGER.error("illegal argument, the suitabilityLU with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityLU with the ID " + id
                + " supplied was not found ");
      }
      if (suitabilityLU.getProjectId().equals(projectId)) {
        suitabilityLUDao.deleteSuitabilityLU(suitabilityLU);
        final WifProject project = projectService.getProject(projectId);

        final SuitabilityLU oldSLU = project.getSuitabilityLUById(suitabilityLU
            .getId());
        project.getSuitabilityLUs().remove(oldSLU);
        projectService.updateProject(project);
        // maybe it's faster
        // wifProjectDao.updateProject(project);

      } else {
        LOGGER
            .error("illegal argument, the suitabilityLU supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the suitabilityLU supplied doesn't belong to project: "
                + projectId);
      }

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
      throw new InvalidEntityIdException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityLU ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityLUService#getSuitabilityLUs(java.lang.String
   * )
   */
  public List<SuitabilityLU> getSuitabilityLUs(final String projectID)
      throws WifInvalidInputException {
    LOGGER.info("getting all suitabilityLUs for projectID: {} ", projectID);
    final List<SuitabilityLU> resSuitabilityLUs = new ArrayList<SuitabilityLU>();
    final List<SuitabilityLU> suitabilityLUs = suitabilityLUDao
        .getSuitabilityLUs(projectID);
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      suitabilityLU = suitabilityParser.parse(suitabilityLU);
      resSuitabilityLUs.add(suitabilityLU);
    }
    return resSuitabilityLUs;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityLUService#addAssociatedLU(java
   * .lang.String, java.lang.String, java.lang.String)
   */
  public void addAssociatedLU(final String id, final String suitabilityLUId,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.debug("addAssociatedLU in SLU {}", suitabilityLUId);
    SuitabilityLU suitabilityLU = getSuitabilityLU(suitabilityLUId, projectId);
    final AllocationLU allocationLU = allocationLUService.getAllocationLU(id,
        projectId);
    LOGGER.debug("allocationLUId to  associate {}", id);
    if (suitabilityLU.getAssociatedALUs() == null) {
      suitabilityLU.setAssociatedALUs(new HashSet<AllocationLU>());

    }
    if (!suitabilityLU.isAssociatedALU(allocationLU.getId())) {
      suitabilityLU.getAssociatedALUs().add(allocationLU);
      allocationLU.setAssociatedLU(suitabilityLU);
      suitabilityLU = mapper.mapSuitabilityLU(suitabilityLU);
      suitabilityLUDao.updateSuitabilityLU(suitabilityLU);
      // Updating the project configuration
      final WifProject project = projectService.getProject(projectId);
      final SuitabilityLU oldSLU = project.getSuitabilityLUById(suitabilityLU
          .getId());
      project.getSuitabilityLUs().remove(oldSLU);
      project.getSuitabilityLUs().add(suitabilityLU);
      projectService.updateProject(project);
    } else {
      LOGGER.error("illegal argument, the allocationLU " + id
          + " supplied already belongs to suitability land use: "
          + suitabilityLUId);
      throw new WifInvalidInputException("illegal argument, the allocationLU "
          + id + " supplied already belongs to suitability land use: "
          + suitabilityLUId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityLUService#deleteAssociatedLU
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  public void deleteAssociatedLU(final String id, final String suitabilityLUId,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.debug("deleteAssociatedLU in SLU {}", suitabilityLUId);

    SuitabilityLU suitabilityLU = getSuitabilityLU(suitabilityLUId, projectId);
    AllocationLU allocationLU = allocationLUService.getAllocationLU(id,
        projectId);
    LOGGER.debug("allocationLUId to disassociate {}", id);
    if (suitabilityLU.isAssociatedALU(allocationLU.getId())) {
      LOGGER.debug("allocationLUId is associated, removing");
      allocationLU = suitabilityLU.getAssociatedALU(allocationLU.getId());
      suitabilityLU.getAssociatedALUs().remove(allocationLU);
      allocationLU.setAssociatedLU(null);
      suitabilityLU = mapper.mapSuitabilityLU(suitabilityLU);
      suitabilityLUDao.updateSuitabilityLU(suitabilityLU);
      // Updating the project configuration
      final WifProject project = projectService.getProject(projectId);
      final SuitabilityLU oldSLU = project.getSuitabilityLUById(suitabilityLU
          .getId());
      project.getSuitabilityLUs().remove(oldSLU);
      project.getSuitabilityLUs().add(suitabilityLU);
      projectService.updateProject(project);
    } else {
      LOGGER.error("illegal argument, the allocationLU " + id
          + " supplied doesn't belong to suitability land use: "
          + suitabilityLUId);
      throw new WifInvalidInputException("illegal argument, the allocationLU "
          + id + " supplied doesn't belong to suitability land use: "
          + suitabilityLUId);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityLUService#getAssociatedLUs
   * (java.lang.String, java.lang.String)
   */
  public List<String> getAssociatedLUs(final String suitabilityLUId,
      final String projectId) throws WifInvalidInputException,
      WifInvalidConfigException {

    final List<String> associatedLUs = new ArrayList<String>();
    final SuitabilityLU suitabilityLU = getSuitabilityLU(suitabilityLUId,
        projectId);
    final Set<AllocationLU> associatedALUs = suitabilityLU.getAssociatedALUs();
    for (final AllocationLU allocationLU : associatedALUs) {
      associatedLUs.add(allocationLU.getId().toString());
    }
    return associatedLUs;
  }

}
