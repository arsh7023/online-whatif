package au.org.aurin.wif.controller;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.geotools.filter.text.cql2.CQLException;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.vividsolutions.jts.io.ParseException;

import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.exception.io.WifIOException;
import au.org.aurin.wif.exception.validate.IncompleteSuitabilityLUConfigException;
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.ProjectNotReadyException;
import au.org.aurin.wif.exception.validate.ProjectSetupFailedException;
import au.org.aurin.wif.exception.validate.UAZAlreadyCreatedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.parsers.ProjectCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.ColorALU;
import au.org.aurin.wif.model.reports.ProjectReport;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.svc.AllocationLUService;
import au.org.aurin.wif.svc.AsyncProjectService;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.FactorService;

/**
 * The Class ProjectController.
 */

@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class ProjectController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectController.class);

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The async project service. */
  @Resource
  private AsyncProjectService asyncProjectService;

  /** The geodata finder. */
  @Resource
  private GeodataFinder geodataFinder;

  /** The report service. */
  @Autowired
  private ReportService reportService;

  @Autowired
  private ProjectCouchParser projectParser;

  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  /** The projects pool. */
  private final HashMap<String, Future<String>> projectsPool = new HashMap<String, Future<String>>();

  /** The uploads pool. */
  private final HashMap<String, Future<String>> uploadsPool = new HashMap<String, Future<String>>();

  @Resource
  private AllocationLUService allocationLUService;

  /** the service. */
  @Resource
  private FactorService factorService;

  /**
   * Gets the project revision.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the project revision
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/revision", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String getProjectRevision(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException {
    final String msg = "getProjectRevision failed: {}";
    try {
      return projectService.getProjectNoMapping(id).getRevision();
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the zipfile of the project.
   *
   * @param roleId
   * @param id
   * @param response
   * @return
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   * @throws DatabaseFailedException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/zipUAZ", produces = "application/zip")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public FileSystemResource getZipUAZ(
      // public byte[] getZipUAZ(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id, final HttpServletResponse response)
          throws WifInvalidConfigException, WifInvalidInputException,
          DatabaseFailedException {

    LOGGER.info("*******>> getProjectZipUAZ request for project  id ={}", id);
    final String msg = "getProject failed: {}";

    try {
      final File zipFile = projectService.getProjectZipUAZ(id);
      final FileSystemResource result = new FileSystemResource(zipFile);
      response.setContentType("application/zip");

      final byte[] bytem = org.springframework.util.FileCopyUtils
          .copyToByteArray(zipFile);
      response.setHeader("Content-Disposition", "attachment; filename=\""
          + zipFile.getName() + "\"");
      response.setContentLength(bytem.length);

      LOGGER.info("Returning zip file");
      return result;
      // return bytem;

    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final IOException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the project.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the project
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  WifProject getProject(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidConfigException,
  WifInvalidInputException {

    LOGGER.info("*******>> getProject request for project  id ={}", id);
    final String msg = "getProject failed: {}";

    try {
      return projectService.getProject(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the all projects.
   *
   * @param roleId
   *          the role id
   * @return the all projects
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws CQLException
   *           the cQL exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/")
  @ResponseStatus(HttpStatus.OK)
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public @ResponseBody
  List<WifProject> getAllProjects(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      final HttpServletResponse response) throws WifInvalidConfigException,
  WifInvalidInputException, NoSuchAuthorityCodeException, FactoryException,
  TransformException, ParseException, CQLException {

    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
    response.setHeader("Access-Control-Allow-Headers", HEADER_USER_ID_KEY);
    response
    .setHeader("Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");

    LOGGER.info("*******>> getAllProjectsNoMapping");

    try {
      return projectService.getAllProjects(roleId);
    } catch (final Exception e) {
      LOGGER.error("find a project failed {}");
      throw new WifInvalidInputException("find a project failed {}");
    }
  }

  /**
   * Gets the project configuration.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the project configuration
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/configuration", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  WifProject getProjectConfiguration(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException {

    LOGGER
    .info(
        "*******>> getProjectFullConfiguration request for project  id ={}",
        id);
    final String msg = "getProjectFullConfiguration failed: {}";

    try {
      return projectService.getProjectConfiguration(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the status.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the status
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ProjectSetupFailedException
   *           the project setup failed exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/status", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  HashMap<String, String> getStatus(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ProjectSetupFailedException {
    LOGGER.trace("*******>> getProjectStatus request for project  id ={}", id);

    if (id == null) {
      LOGGER.error("get status failed, ID is undefined", id);
      throw new InvalidEntityIdException("get status failed ");
    }

    if (id.equalsIgnoreCase("undefined")) {
      LOGGER.error("get status failed, ID is undefined", id);
      throw new InvalidEntityIdException("get status failed ");
    }
    final HashMap<String, String> answer = new HashMap<String, String>(2);
    answer.put(WifKeys.SETUP_PROCESS_KEY, WifKeys.PROCESS_STATE_SETUP);
    String statusMessage = WifKeys.PROCESS_STATE_NA;
    final WifProject project = projectService.getProjectNoMapping(id);
    if (project.getReady()) {
      statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
    } else {
      final String msge = "get status failed: {}";

      try {
        final Future<String> result = projectsPool.get(id);
        if (result == null) {
          LOGGER.error("id not found in futuresPool for {}", id);
          throw new WifInvalidInputException("id not found in futuresPool");
        }
        if (result.isDone()) {
          try {
            final String msg = result.get();
            LOGGER.info("process ended with result: {}", msg);
          } catch (final ExecutionException e) {
            statusMessage = WifKeys.PROCESS_STATE_FAILED;
            final String errorMessage = "project a synchronous setup failed";
            answer.put(WifKeys.STATUS_KEY, statusMessage);
            LOGGER.info("Status is = {}", answer.get(WifKeys.STATUS_KEY));
            LOGGER.error(errorMessage, e);
            projectsPool.remove(id);
            projectService.purgeProject(project.getId());
            throw new ProjectSetupFailedException(errorMessage, e);
          }
          statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
          projectsPool.remove(id);
          // TODO Probably this is not necessary...only time will tell
          // project.setReady(true);
          // projectService.updateProject(project);
        } else {
          statusMessage = WifKeys.PROCESS_STATE_RUNNING;
        }
      } catch (final Exception e) {
        if (e instanceof InterruptedException) {
          LOGGER.error("Interrupted asynchronous creation of project " + msge,
              e.getMessage());
          throw new ProjectSetupFailedException(msge, e);
        } else {
          LOGGER.error(msge, e.getMessage());
          throw new ProjectSetupFailedException(msge, e);
        }
      }
    }
    answer.put(WifKeys.STATUS_KEY, statusMessage);
    LOGGER.debug("Status is ={}", answer.get(WifKeys.STATUS_KEY));
    return answer;
  }

  /**
   * Update project.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param project
   *          the project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id, @RequestBody final WifProject project)
          throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("*******>> updateProject request for project  id ={}", id);
    // TODO we'll need to wire the project id on the wifProject object so
    // that the DAO will be able to handle it. Doing it this way will also
    // prevent
    // users/callers to request project 'A' to be updated but providing a
    // different
    // project ID on the actual content body

    final String msg = "setupAllocationConfig failed: {}";
    project.setId(id);
    try {
      projectService.updateProject(project);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Delete project.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException {
    LOGGER.info("*******>> deleteProject request for project  id ={}", id);
    final String msg = "deleteProject failed: {}";
    try {
      projectService.deleteProject(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);

    }
  }

  /**
   * Creates the project.
   *
   * @param roleId
   *          the role id
   * @param project
   *          the project
   * @param response
   *          the response
   * @return the wif project
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws InvalidFFNameException
   * @throws InvalidLabelException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  WifProject createProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @RequestBody WifProject project, final HttpServletResponse response)
          throws WifInvalidConfigException, WifInvalidInputException,
          DataStoreUnavailableException, DataStoreCreationException, InvalidLabelException, InvalidFFNameException {
    LOGGER.info("*******>> createProject request for project  label ={}",
        project.getLabel());
    final String msg = "createProject failed: {}";

    try {
      project = projectService.createProject(project, roleId);
      LOGGER
      .info(
          "*******>> project created with ID ={} requesting asynchronous task to finish setup ",
          project.getId());
      final Future<String> future = asyncProjectService.setupProjectAsync(
          project, roleId);

      projectsPool.put(project.getId(), future);
      response.setHeader("Location",
          OWIURLs.PROJECT_SVC_URI + "/" + project.getId());
      return project;
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final DataStoreUnavailableException e) {
      LOGGER.error(msg, e.getMessage());
      throw new DataStoreUnavailableException(msg, e);
    } catch (final DataStoreCreationException e) {
      LOGGER.error(msg, e.getMessage());
      throw new DataStoreCreationException(msg, e);
    }
  }

  /**
   * Gets the uAZ attributes.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the uAZ attributes
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributes", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getUAZAttributes(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException {

    LOGGER.info("*******>> getUAZAttributes for Project request id ={}", id);

    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();
      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
        return geodataFinder.getUAZAttributes(uazTbl);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getUAZAttributes project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getUAZAttributes project failed for " + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("getUAZAttributes failed for {}", id);
        throw new InvalidEntityIdException("getUAZAttributes failed "
            + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER.error(
            "NumberFormatException getUAZAttributes a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getUAZAttributes project failed "
                + e.toString());
      } else {
        LOGGER.error("getUAZAttributes a project failed for {}", id);
        throw new WifInvalidInputException("find a project failed " + id);
      }
    }
  }

  /**
   * Gets the distinct entries for uaz attribute.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param attr
   *          the attr
   * @return the distinct entries for uaz attribute
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributes/{attr}/values", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getDistinctEntriesForUAZAttribute(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id,
      @PathVariable("attr") final String attr) throws WifInvalidInputException {

    LOGGER
    .info(
        "*******>> getDistinctEntriesForUAZAttribute on Project id ={} and attr ={}",
        id, attr);
    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();
      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
        return geodataFinder.getDistinctEntriesForUAZAttribute(uazTbl, attr);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getDistinctEntriesForUAZAttribute project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getDistinctEntriesForUAZAttribute project failed for "
                + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("getDistinctEntriesForUAZAttribute failed for {}", id);
        throw new InvalidEntityIdException(
            "getDistinctEntriesForUAZAttribute project failed " + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException getDistinctEntriesForUAZAttribute a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getDistinctEntriesForUAZAttribute project failed "
                + e.toString());
      } else {
        LOGGER.error(
            "getDistinctEntriesForUAZAttribute a project failed for {}", id);
        throw new WifInvalidInputException(
            "getDistinctEntriesForUAZAttribute a project failed " + id);
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributesfixed", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getDistinctEntriesForUAZAttributefixed(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException {

    LOGGER.info(
        "*******>> getDistinctEntriesForUAZAttributefixed on Project id ={} ",
        id);
    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final String attr = project.getExistingLUAttributeName();
      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();
      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
        return geodataFinder.getDistinctEntriesForUAZAttribute(uazTbl, attr);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getDistinctEntriesForUAZAttributefixed project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getDistinctEntriesForUAZAttributefixed project failed for "
                + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER
        .error("getDistinctEntriesForUAZAttributefixed failed for {}", id);
        throw new InvalidEntityIdException(
            "getDistinctEntriesForUAZAttributefixed project failed "
                + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException getDistinctEntriesForUAZAttributefixed a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getDistinctEntriesForUAZAttributefixed project failed "
                + e.toString());
      } else {
        LOGGER.error(
            "getDistinctEntriesForUAZAttributefixed a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "getDistinctEntriesForUAZAttributefixed a project failed " + id);
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributes/{attr}/colorsold", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getDistinctColorsForUAZAttributeold(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id,
      @PathVariable("attr") final String attr) throws WifInvalidInputException {

    LOGGER
    .info(
        "*******>> getDistinctColorsForUAZAttribute on Project id ={} and attr ={}",
        id, attr);
    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();
      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
        return geodataFinder.getDistinctColorsForUAZAttribute(uazTbl, attr);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getDistinctColorsForUAZAttribute project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getDistinctColorsForUAZAttribute project failed for "
                + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("getDistinctColorsForUAZAttribute failed for {}", id);
        throw new InvalidEntityIdException(
            "getDistinctColorsForUAZAttribute project failed " + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException getDistinctColorsForUAZAttribute a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getDistinctColorsForUAZAttribute project failed "
                + e.toString());
      } else {
        // LOGGER.error(
        // "getDistinctColorsForUAZAttribute a project failed for {}", id);
        // throw new WifInvalidInputException(
        // "getDistinctColorsForUAZAttribute a project failed " + id);
        return null;
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributes/colorsforaluconfig", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getDistinctColorsForAluConfig(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException {

    LOGGER.info("*******>> colorsforaluconfig on Project id ={} ", id);
    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final String attr = project.getExistingLUAttributeName();

      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();
      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();
        return geodataFinder.getDistinctColorsForALUConfig(uazTbl, attr);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getDistinctColorsForAluConfig project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getDistinctColorsForAluConfig project failed for "
                + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("getDistinctColorsForUAZAttribute failed for {}", id);
        throw new InvalidEntityIdException(
            "getDistinctColorsForUAZAttribute project failed " + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException getDistinctColorsForAluConfig a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getDistinctColorsForAluConfig project failed "
                + e.toString());
      } else {
        // LOGGER.error(
        // "getDistinctColorsForUAZAttribute a project failed for {}", id);
        // throw new WifInvalidInputException(
        // "getDistinctColorsForUAZAttribute a project failed " + id);
        return null;
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{id}/unionAttributes/{attr}/colors", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getDistinctColorsForUAZAttribute(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id,
      @PathVariable("attr") final String attr) throws WifInvalidInputException {

    LOGGER
    .info(
        "*******>> getDistinctColorsForUAZAttribute on Project id ={} and attr ={}",
        id, attr);
    try {

      final List<String> lst = new ArrayList<String>();

      WifProject project = projectService.getProjectNoMapping(id);
      project = projectParser.parse(project);
      AllocationConfigs AllocationConfigs = null;
      final String AllocationConfigsId = project.getAllocationConfigsId();

      if (AllocationConfigsId != null) {

        LOGGER.info("getting the AllocationConfig with ID={}",
            AllocationConfigsId);
        AllocationConfigs = AllocationConfigsDao
            .findAllocationConfigsById(AllocationConfigsId);
        if (AllocationConfigs.getColorALUs().size() != 0) {
          for (final ColorALU color : AllocationConfigs.getColorALUs()) {

            lst.add('F' + color.getLabel() + '@' + color.getAssociatedColors());

          }
        }
      }

      return lst;

    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("getDistinctColorsForUAZAttribute failed for {}", id);
        throw new InvalidEntityIdException(
            "getDistinctColorsForUAZAttribute project failed " + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException getDistinctColorsForUAZAttribute a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException getDistinctColorsForUAZAttribute project failed "
                + e.toString());
      } else {
        // LOGGER.error(
        // "getDistinctColorsForUAZAttribute a project failed for {}", id);
        // throw new WifInvalidInputException(
        // "getDistinctColorsForUAZAttribute a project failed " + id);
        return null;
      }
    }
  }

  /**
   * Update uaz.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param optionalColumns
   *          the optional columns
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws UAZAlreadyCreatedException
   *           the uAZ already created exception
   * @throws IncompleteSuitabilityLUConfigException
   *           the incomplete suitability lu config exception
   * @throws FactoryException
   *           the factory exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws GeoServerConfigException
   * @throws DataStoreCreationException
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{id}/UAZ", consumes = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public void updateUAZ(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id,
      @RequestBody final List<String> optionalColumns)
          throws WifInvalidInputException, BindException,
          UAZAlreadyCreatedException, IncompleteSuitabilityLUConfigException,
          FactoryException, DataStoreUnavailableException,
          WifInvalidConfigException, GeoServerConfigException,
          DataStoreCreationException {
    final String msg = "updateUAZ failed: {}";

    LOGGER.info("*******>> updateUAZ request for project id ={}", id);
    try {
      projectService.convertUnionToUAZ(id, optionalColumns, roleId);
      LOGGER
      .info(" finalized setup process for project id ={} <<*******>>", id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final UAZAlreadyCreatedException e) {
      LOGGER.error(msg, e.getMessage());
      throw new UAZAlreadyCreatedException(msg, e);
    } catch (final IncompleteSuitabilityLUConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteSuitabilityLUConfigException(msg, e);
    } catch (final DataStoreUnavailableException e) {
      LOGGER.error(msg, e.getMessage());
      throw new DataStoreUnavailableException(msg, e);
    } catch (final FactoryException e) {
      LOGGER.error(msg, e.getMessage());
      throw new FactoryException(msg, e);
    } catch (final GeoServerConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new GeoServerConfigException(msg, e);
    }
  }

  /**
   * Restore project.
   *
   * @param roleId
   *          the role id
   * @param projectReport
   *          the project report
   * @param response
   *          the response
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/restore", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  WifProject restoreProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @RequestBody final ProjectReport projectReport,
      final HttpServletResponse response) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> restoreProject request for projectReport label ={}",
        projectReport.getLabel());
    final String msg = "restoreProject failed: {}";

    try {
      final WifProject wifProject = projectService
          .restoreProjectConfiguration(projectReport);
      LOGGER
      .info("*******>> project restored with ID ={} ", wifProject.getId());
      response.setHeader("Location",
          OWIURLs.PROJECT_SVC_URI + "/" + wifProject.getId());
      return wifProject;
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Delete project configuration.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/keepUAZ")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProjectConfiguration(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException {
    LOGGER
    .info(
        "*******>> deleteProjectconfiguration only request for project  id ={}",
        id);
    final String msg = "deleteProjectconfiguration failed: {}";
    try {
      projectService.deleteProject(id, false);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);

    }
  }

  /**
   * Gets the project report.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the project report
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  ProjectReport getProjectReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidConfigException,
  WifInvalidInputException, ParsingException {

    LOGGER.info("*******>> getProjectReport request for project  id ={}", id);
    final String msg = "getProjectReport failed: {}";

    try {

      final WifProject project = projectService.getProjectConfiguration(id);
      /////for deleting extra factor types.
      projectService.updateProject(project);
      factorService.deleteFactorTypesExtra(id);
      /////





      return reportService.getProjectReport(project);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    }
  }

  /**
   * Upload uaz async.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifIOException
   *           the wif io exception
   * @throws ProjectNotReadyException
   *           the project not ready exception
   * @throws DataStoreCreationException
   * @throws IOException
   * @throws MiddlewarePersistentException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{id}/upload")
  @ResponseStatus(HttpStatus.OK)
  public void uploadUAZAsync(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  DataStoreUnavailableException, WifInvalidConfigException, WifIOException,
  ProjectNotReadyException, IOException, DataStoreCreationException,
  MiddlewarePersistentException {
    LOGGER.info("*******>> uploadUAZAsync request for project  id ={}", id);

    final WifProject project = projectService.getProject(id);
    if (project.getReady()) {
      final Future<String> future = asyncProjectService.uploadUAZAsync(project,
          roleId);
      uploadsPool.put(project.getId(), future);
    } else {
      final String msg = "Project is not ready for uploading UAZ";
      LOGGER.error(msg);
      throw new ProjectNotReadyException(msg);
    }
  }

  /**
   * Gets the upload status.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the upload status
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ProjectSetupFailedException
   *           the project setup failed exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/upload/status", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  HashMap<String, String> getUploadStatus(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ProjectSetupFailedException {
    LOGGER.info("*******>> getUploadStatus request for project  id ={}", id);

    if (id == null) {
      LOGGER.error("getUploadStatus failed, ID is undefined", id);
      throw new InvalidEntityIdException("getUploadStatus failed ");
    }

    if (id.equalsIgnoreCase("undefined")) {
      LOGGER.error("get status failed, ID is undefined", id);
      throw new InvalidEntityIdException("getUploadStatus failed ");
    }
    final HashMap<String, String> answer = new HashMap<String, String>(2);
    answer.put(WifKeys.SETUP_PROCESS_KEY, WifKeys.PROCESS_STATE_SETUP);
    String statusMessage = WifKeys.PROCESS_STATE_NA;
    final WifProject project = projectService.getProjectNoMapping(id);
    if (project.getReady()) {
      statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
    } else {
      final String msge = "getUploadStatus failed: {}";

      try {
        final Future<String> result = uploadsPool.get(id);
        if (result == null) {
          LOGGER.error("id not found in futuresPool for {}", id);
          throw new WifInvalidInputException("id not found in futuresPool");
        }
        if (result.isDone()) {
          try {
            final String msg = result.get();

            LOGGER.info("process ended with result: {}", msg);
          } catch (final ExecutionException e) {
            statusMessage = WifKeys.PROCESS_STATE_FAILED;
            final String errorMessage = "project uploadUAZ synchronous failed";
            answer.put(WifKeys.STATUS_KEY, statusMessage);
            LOGGER.info("Status is = {}", answer.get(WifKeys.STATUS_KEY));
            LOGGER.error(errorMessage, e);
            uploadsPool.remove(id);
            throw new ProjectSetupFailedException(errorMessage, e);
          }
          statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
          uploadsPool.remove(id);

        } else {
          statusMessage = WifKeys.PROCESS_STATE_RUNNING;
        }
      } catch (final Exception e) {
        if (e instanceof InterruptedException) {
          LOGGER.error("Interrupted asynchronous upload of UAZ " + msge,
              e.getMessage());
          throw new ProjectSetupFailedException(msge, e);
        } else {
          LOGGER.error(msge, e.getMessage());
          throw new ProjectSetupFailedException(msge, e);
        }
      }
    }
    answer.put(WifKeys.STATUS_KEY, statusMessage);
    LOGGER.info("Status is ={}", answer.get(WifKeys.STATUS_KEY));
    return answer;
  }

  /**
   * Gets the uAZ uri.
   *
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @return the uAZ uri
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifIOException
   *           the wif io exception
   * @throws ProjectNotReadyException
   *           the project not ready exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}/upload")
  @ResponseStatus(HttpStatus.OK)
  public String getUAZUri(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
  DataStoreUnavailableException, WifInvalidConfigException, WifIOException,
  ProjectNotReadyException {
    LOGGER.info("*******>> getUAZUri request for project  id ={}", id);

    final WifProject project = projectService.getProjectNoMapping(id);
    return project.getUploadUAZDatatoreUri();
  }

  /**
   * Gets the all project names.
   *
   * @param roleId
   *          the role id
   * @return
   * @return the all projects names
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws CQLException
   *           the cQL exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/projectNames")
  @ResponseStatus(HttpStatus.OK)
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public @ResponseBody
  List<String> getAllProjectNames(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      final HttpServletResponse response) throws WifInvalidConfigException,
  WifInvalidInputException, NoSuchAuthorityCodeException, FactoryException,
  TransformException, ParseException, CQLException {

    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
    response.setHeader("Access-Control-Allow-Headers", HEADER_USER_ID_KEY);
    response
    .setHeader("Access-Control-Allow-Headers",
        "Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");

    LOGGER.info("*******>> getAllProject names");
    final List<String> lst = new ArrayList<String>();
    try {
      final List<WifProject> prjList = projectService.getAllProjects(roleId);
      for (final WifProject wif : prjList) {
        lst.add(wif.getLabel());
      }
      return lst;
    } catch (final Exception e) {
      LOGGER.error("getAllProject names {}");
      throw new WifInvalidInputException("find a project failed {}");
    }
  }


  @RequestMapping(method = RequestMethod.GET, value = "/{id}/MakeLUsforUnionAttributes/{attr}/makeLU", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AllocationLU>  MakeLUsforUnionAttributes(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("id") final String id,
      @PathVariable("attr") final String attr) throws WifInvalidInputException {

    LOGGER
    .info(
        "*******>> MakeLUsforUnionAttributes on Project id ={} and attr ={}",
        id, attr);
    try {
      final WifProject project = projectService.getProjectNoMapping(id);
      final SuitabilityConfig suitabilityConfig = project
          .getSuitabilityConfig();

      ////////////////


      if (suitabilityConfig != null) {
        final String uazTbl = suitabilityConfig.getUnifiedAreaZone();

        final List<String> lst= geodataFinder.getDistinctEntriesForUAZAttribute(uazTbl, attr);
        for (final String str: lst)
        {
          final AllocationLU allocationLU = new AllocationLU();
          allocationLU.setProjectId(project.getId());
          allocationLU.setLabel(str);
          allocationLU.setFeatureFieldName(str);
          allocationLUService.createAllocationLU(allocationLU,
              project.getId());
        }


        return allocationLUService.getAllocationLUs(id);
      } else {
        LOGGER
        .error(
            "No analysisConfig set, getDistinctEntriesForUAZAttribute project failed for {}",
            id);
        throw new WifInvalidInputException(
            "No analysisConfig set, getDistinctEntriesForUAZAttribute project failed for "
                + id);
      }
    } catch (final Exception e) {
      if (e instanceof InvalidEntityIdException) {
        LOGGER.error("MakeLUsforUnionAttributes failed for {}", id);
        throw new InvalidEntityIdException(
            "MakeLUsforUnionAttributes project failed " + e.toString());
      } else if (e instanceof NumberFormatException) {
        LOGGER
        .error(
            "NumberFormatException MakeLUsforUnionAttributes a project failed for {}",
            id);
        throw new WifInvalidInputException(
            "NumberFormatException MakeLUsforUnionAttributes project failed "
                + e.toString());
      } else {
        LOGGER.error(
            "MakeLUsforUnionAttributes a project failed for {}", id);
        throw new WifInvalidInputException(
            "MakeLUsforUnionAttributes a project failed " + id);
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/{user_id}/copydemo", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String CopyDemoProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId
      ,@PathVariable("user_id") final String user_id) throws WifInvalidConfigException,
  WifInvalidInputException, ParsingException, NoSuchAuthorityCodeException, FactoryException {


    final String msg = "getProjectReport failed: {}";

    String out ="";
    String id = "";
    String dbName="";
    CoordinateReferenceSystem crs = null;

    try {

      Boolean lsw=false;
      final List<WifProject> wifProjectsRole = projectService.getAllProjects(roleId);
      for (final WifProject prjRole:wifProjectsRole)
      {
        if (prjRole.getRoleOwner().toLowerCase().equals(roleId))
        {
          final String prjName = "Demo_for_" + roleId;
          if (prjRole.getLabel().toLowerCase().equals(prjName.toLowerCase()))
          {
            lsw = true;
          }
        }
      }



      if (lsw == false)
      {
        final List<WifProject> wifProjects = projectService.getAllProjects(WifKeys.SHIB_ROLE_NAME);
        for (final WifProject prj:wifProjects)
        {
          if (prj.getRoleOwner().toLowerCase().equals(WifKeys.SHIB_ROLE_NAME.toLowerCase()))
          {
            if (prj.getLabel().toLowerCase().equals(WifKeys.DEMO_PROJECT_NAME_ADMIN.toLowerCase()))
            {
              id = prj.getId();
              dbName = prj.getSuitabilityConfig().getUnifiedAreaZone();
              crs = CRS.decode(prj.getSrs());

            }
          }
        }

        if (!id.equals("")) {
          LOGGER.info("*******>> copydemo request for project  id ={}", id);
        }

        if (!dbName.equals(""))
        {

          //copydb
          geodataFinder.CopyDemoTable("wanneroofor" + user_id, dbName);


          final WifProject project = projectService.getProjectConfiguration(id);
          /////for deleting extra factor types.
          projectService.updateProject(project);
          factorService.deleteFactorTypesExtra(id);
          ////

          final ProjectReport projectReport= reportService.getProjectReport(project);

          final WifProject wifProject = projectService.restoreProjectConfiguration(projectReport);
          LOGGER.info("*******>> project restored with ID ={} ", wifProject.getId());

          final WifProject newProject = projectService.getProject(wifProject.getId());
          newProject.setName("Demo_for_" + roleId);
          newProject.setRoleOwner(roleId);
          newProject.getSuitabilityConfig().setUnifiedAreaZone("wanneroofor" + user_id);
          projectService.updateProject(newProject);

          //pubish Geoserver layer
          crs = CRS.decode(newProject.getSrs());
          projectService.PublishWMSLayer( newProject
              .getSuitabilityConfig().getUnifiedAreaZone(), crs, newProject.getId());


          out ="copied";
        }
      }



    } catch (final WifInvalidInputException e) {
      out ="failed";
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      out ="failed";
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      out ="failed";
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    }
    return out;
  }

}
