package au.org.aurin.wif.impl;

import java.util.ArrayList;
import java.util.Iterator;
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
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
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

/**
 * The Class AllocationLUServiceImpl.
 */
@Service
@Qualifier("allocationLUService")
public class AllocationLUServiceImpl implements AllocationLUService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationLUServiceImpl.class);

  /** The allocation lu dao. */
  @Autowired
  private AllocationLUDao allocationLUDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

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
    LOGGER.trace("AllocationLU Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#createAllocationLU(au.org.aurin
   * .wif.model.allocation.AllocationLU, java.lang.String)
   */
  public AllocationLU createAllocationLU(AllocationLU allocationLU,
      String projectId) throws WifInvalidInputException,
      WifInvalidConfigException, InvalidLabelException, InvalidFFNameException {

    validate(allocationLU, projectId);
    WifProject project = projectService.getProject(projectId);
    allocationLU.setProjectId(projectId);
    LOGGER.debug("persisting the allocationLU={}", allocationLU.getLabel());
    AllocationLU savedAllocationLU = allocationLUDao
        .persistAllocationLU(allocationLU);
    LOGGER.debug("returning the allocationLU with id={}",
        savedAllocationLU.getId());
    project.getAllocationLandUses().add(savedAllocationLU);
    projectService.updateProject(project);
    return savedAllocationLU;

  }

  /**
   * Validate business logic
   * 
   * @param allocationLU
   *          the allocation lu
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws InvalidLabelException
   *           the invalid label exception
   * @throws InvalidFFNameException
   */
  private void validate(AllocationLU allocationLU, String projectId)
      throws WifInvalidInputException, InvalidLabelException,
      InvalidFFNameException {
    String message = "createAllocationLU failed: allocationLU is null";
    if (allocationLU == null) {
      LOGGER.error(message);
      throw new WifInvalidInputException(message);
    } else if (allocationLU.getLabel() == null) {
      message = "createAllocationLU failed: allocationLU label is null";
      LOGGER.error(message);
      throw new InvalidLabelException(message);
    } else if (allocationLU.getFeatureFieldName() == null) {
      message = "createAllocationLU failed: allocationLU feature field name is null";
      LOGGER.error(message);
      throw new InvalidFFNameException(message);
    } else {
      message = "createAllocationLU failed: allocationLU label has already been used in this project configuration";
      List<AllocationLU> allocationLUs = allocationLUDao
          .getAllocationLUs(projectId);
      for (AllocationLU allocationLU2 : allocationLUs) {
        if (allocationLU.getLabel().equals(allocationLU2.getLabel())) {
          LOGGER.error(message);
          throw new InvalidLabelException(message);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#getAllocationLU(java.lang.String)
   */
  public AllocationLU getAllocationLU(String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.debug("getting the allocationLU with ID={}", id);
    try {
      AllocationLU allocationLU = allocationLUDao.findAllocationLUById(id);
      if (allocationLU == null) {
        LOGGER.error("illegal argument, the allocationLU with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the albased onlocationLU with the ID " + id
                + " supplied was not found ");
      }

      return allocationLU;

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationLU ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationLU ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#getAllocationLU(java.lang.String)
   */
  public AllocationLU getAllocationLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    AllocationLU allocationLU = getAllocationLU(id);
    if (allocationLU.getProjectId().equals(projectId)) {
      return allocationLU;
    } else {
      LOGGER
          .error("illegal argument, the allocationLU supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the allocationLU supplied doesn't belong to project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#updateAllocationLU(au.org.aurin
   * .wif.model.allocation.AllocationLU, java.lang.String)
   */
  public void updateAllocationLU(AllocationLU allocationLU, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("updating allocationLU: {}, with id: {}",
        allocationLU.getLabel(), allocationLU.getId());
    try {
      WifProject project = projectService.getProject(projectId);
      allocationLU.setWifProject(project);
      allocationLU.setRevision(allocationLUDao.findAllocationLUById(
          allocationLU.getId()).getRevision());
      allocationLUDao.updateAllocationLU(allocationLU);
      AllocationLU oldLU = project.getExistingLandUseById(allocationLU.getId());
      project.getAllocationLandUses().remove(oldLU);
      project.getAllocationLandUses().add(allocationLU);
      projectService.updateProject(project);
    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the allocationLU supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the allocationLU supplied is invalid ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#deleteAllocationLU(java.lang.String
   * , java.lang.String)
   */
  public void deleteAllocationLU(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the allocationLU with ID={}", id);
    try {
      AllocationLU allocationLU = allocationLUDao.findAllocationLUById(id);
      if (allocationLU == null) {
        LOGGER.error("illegal argument, the allocationLU with the ID " + id
            + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the allocationLU with the ID " + id
                + " supplied was not found ");
      }
      if (allocationLU.getProjectId().equals(projectId)) {
        allocationLUDao.deleteAllocationLU(allocationLU);
        WifProject project = projectService.getProject(projectId);
        AllocationLU oldLU = project.getExistingLandUseById(allocationLU
            .getId());
        project.getAllocationLandUses().remove(oldLU);
        projectService.updateProject(project);

      } else {
        LOGGER
            .error("illegal argument, the allocationLU supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the allocationLU supplied doesn't belong to project: "
                + projectId);
      }

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationLU ");
      throw new InvalidEntityIdException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid allocationLU ");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AllocationLUService#getAllocationLUs(java.lang.String)
   */
  public List<AllocationLU> getAllocationLUs(String projectID)
      throws WifInvalidInputException {
    LOGGER.info("getting all allocationLUs for projectID: {} ", projectID);

    return allocationLUDao.getAllocationLUs(projectID);
  }

  public List<AllocationLU> getAllocationLUsSuitabilityAssociated(
      String projectID) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("getting all allocationLUs for projectID: {} ", projectID);

    List<AllocationLU> listAlu = allocationLUDao.getAllocationLUs(projectID);

    List<AllocationLU> listAlunew = new ArrayList<AllocationLU>();
    WifProject project = projectService.getProject(projectID);
    Iterator<AllocationLU> iterator = listAlu.iterator();
    while (iterator.hasNext()) {
      AllocationLU futureLU = iterator.next();
      Boolean lsw = false;

      List<SuitabilityLU> resSuitabilityLUs = new ArrayList<SuitabilityLU>();
      List<SuitabilityLU> suitabilityLUs = suitabilityLUDao
          .getSuitabilityLUs(projectID);
      for (SuitabilityLU suitabilityLU : suitabilityLUs) {
        suitabilityLU = suitabilityParser.parse(suitabilityLU);
        resSuitabilityLUs.add(suitabilityLU);
      }

      LOGGER.trace("Loading {} Suitablity Land Uses...", suitabilityLUs.size());
      for (SuitabilityLU suitabilityLU : resSuitabilityLUs) {
        LOGGER.trace("Suitability LU label: {}", suitabilityLU.getLabel());
        LOGGER.trace("... SLU score UAZ value: {}",
            suitabilityLU.getFeatureFieldName());
        Set<AllocationLU> associatedALUs = suitabilityLU.getAssociatedALUs();
        for (AllocationLU allocationLU : associatedALUs) {
          if (allocationLU.getLabel().equals(futureLU.getLabel())) {
            if (lsw == false) { // just assign first
              futureLU.setAssociatedLU(suitabilityLU);
              listAlunew.add(futureLU);
              lsw = true;
            }
          }
        }
      }
    }
    return listAlunew;
    // return allocationLUDao.getAllocationLUs(projectID);
  }

}
