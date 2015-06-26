package au.org.aurin.wif.controller.allocation;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.geotools.filter.text.cql2.CQLException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import au.org.aurin.wif.controller.OWIURLs;
import au.org.aurin.wif.exception.config.GeoServerConfigException;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.AllocationAnalysisFailedException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationScenarioException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.executors.svc.AsyncAllocationService;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.report.ReportService;

import com.vividsolutions.jts.io.ParseException;

/**
 * The Class AllocationScenarioController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class AllocationScenarioController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationScenarioController.class);

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The async allocation service. */
  @Resource
  private AsyncAllocationService asyncAllocationService;

  /** The scenarios pool. */
  // private final HashMap<String, Future<Boolean>> scenariosPool = new
  // HashMap<String, Future<Boolean>>();
  private final HashMap<String, Future<String>> scenariosPool = new HashMap<String, Future<String>>();

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Sets the allocation scenario service.
   * 
   * @param allocationScenarioService
   *          the new allocation scenario service
   */
  public void setAllocationScenarioService(
      final AllocationScenarioService allocationScenarioService) {
    this.allocationScenarioService = allocationScenarioService;
  }

  // TODO wrap the service calls in try-catch blocks so that we can
  // log what sort of exceptions were thrown while accessing the service.

  /**
   * Gets the allocation scenarios for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the allocation scenarios for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AllocationScenario> getAllocationScenariosForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER
        .info(
            "*******>> getAllocationScenariosForProject request for project  id ={}",
            projectId);

    return allocationScenarioService.getAllocationScenarios(projectId);
  }

  /**
   * Gets the allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationScenario getAllocationScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> getAllocationScenario request for project  id ={}",
        projectId);

    try {
      return allocationScenarioService.getAllocationScenario(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException("getAllocationScenario failed", e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException("getAllocationScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER.error("getAllocationScenario failed: {}", e.getMessage());
      throw new ParsingException("getAllocationScenario failed", e);
    }
  }

  /**
   * Creates the allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param allocationScenario
   *          the allocation scenario
   * @param response
   *          the response
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteAllocationScenarioException
   *           the incomplete allocation scenario exception
   * @throws FactoryException
   * @throws GeoServerConfigException
   * @throws DataStoreUnavailableException
   * @throws NoSuchAuthorityCodeException
   * @throws MalformedURLException
   * @throws IllegalArgumentException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/allocationScenarios", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  AllocationScenario createAllocationScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final AllocationScenario allocationScenario,
      final HttpServletResponse response) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteAllocationScenarioException, IllegalArgumentException,
      MalformedURLException, NoSuchAuthorityCodeException,
      DataStoreUnavailableException, GeoServerConfigException, FactoryException {
    LOGGER.info(
        "*******>> createAllocationScenario request for project  id ={}",
        projectId);
    try {

      return allocationScenarioService.createAllocationScenario(
          allocationScenario, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("createAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException("createAllocationScenario failed", e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("createAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException("createAllocationScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER.error("createAllocationScenario failed: {}", e.getMessage());
      throw new ParsingException("createAllocationScenario failed", e);
    } catch (final IncompleteAllocationScenarioException e) {
      LOGGER.error("createAllocationScenario failed: {}", e.getMessage());
      throw new IncompleteAllocationScenarioException(
          "createAllocationScenario failed", e);
    }
  }

  /**
   * Update allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param allocationScenario
   *          the allocation scenario
   * @throws Exception
   *           the exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/allocationScenarios/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateAllocationScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final AllocationScenario allocationScenario)
      throws Exception {
    LOGGER.info(
        "*******>> updateAllocationScenario request for project  id ={}",
        projectId);
    try {
      allocationScenario.setId(id);
      allocationScenarioService.updateAllocationScenario(allocationScenario,
          projectId);
    } catch (final Exception e) {
      LOGGER.error("updateAllocationScenario failed: {}", e.getMessage());
      throw new Exception("updateAllocationScenario failed", e);
    }
  }

  /**
   * Delete allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @throws Exception
   *           the exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/allocationScenarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllocationScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws Exception {
    LOGGER.info(
        "*******>> deleteAllocationScenario request for project  id ={}",
        projectId);
    try {
      allocationScenarioService.deleteAllocationScenario(id, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("deleteAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException("deleteAllocationScenario failed", e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("deleteAllocationScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException("deleteAllocationScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER.error("deleteAllocationScenario failed: {}", e.getMessage());
      throw new ParsingException("deleteAllocationScenario failed", e);
    }
  }

  /**
   * Gets the outcome async.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the outcome async
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SuitabilityAnalysisFailedException
   *           the wif analysis failed exception
   * @throws CQLException
   *           the cQL exception
   * @throws InterruptedException
   *           the interrupted exception
   * @throws ExecutionException
   *           the execution exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/allocationScenarios/{id}/asyncOutcome", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public void getOutcomeAsync(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, MismatchedDimensionException,
      NoSuchAuthorityCodeException, FactoryException, TransformException,
      ParseException, IOException, SuitabilityAnalysisFailedException,
      CQLException, InterruptedException, ExecutionException, ParsingException,
      IncompleteDemandScenarioException, ClassNotFoundException, SQLException {
    LOGGER
        .info(
            "*******>> getOutcomeAsync request for allocation scenario  id ={}",
            id);

    try {

      // final Future<Boolean> outcome = asyncAllocationService
      // .doAllocationAnalysisAsync(id);

      final Future<String> outcome = asyncAllocationService
          .doAllocationAnalysisAsync(id);

      scenariosPool.put(id, outcome);

    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidConfigException(e.getMessage(), e);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidInputException(e.getMessage(), e);
    } catch (final MismatchedDimensionException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new MismatchedDimensionException(e.getMessage(), e);
    }
  }

  /**
   * Gets the status.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the status
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws ExecutionException
   * @throws InterruptedException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/status", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  HashMap<String, String> getStatus(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException, InterruptedException,
      ExecutionException {
    // LOGGER.info(
    // "*******>> getScenarioStatus request for allocation scenario id ={}",
    // id);
    final HashMap<String, String> answer = new HashMap<String, String>(2);
    answer.put(WifKeys.SETUP_PROCESS_KEY,
        WifKeys.ALLOCATION_PROCESS_STATE_SETUP);
    String message = WifKeys.PROCESS_STATE_NA;
    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    if (allocationScenario.isReady()) {
      message = WifKeys.PROCESS_STATE_SUCCESS;

      // ////// new ali
      final Future<String> result = scenariosPool.get(id);
      message = result.get();
      if (message.equals("")) {
        message = WifKeys.PROCESS_STATE_SUCCESS;
      }
      // ////////////

    } else {
      try {
        // final Future<Boolean> result = scenariosPool.get(id);
        final Future<String> result = scenariosPool.get(id);
        if (result == null) {
          LOGGER.error("id not found in scenariosPool for {}", id);
          throw new WifInvalidInputException("id not found in scenariosPool");
        }
        // if (result.isDone()) {
        // message = WifKeys.PROCESS_STATE_SUCCESS;
        // scenariosPool.remove(id);
        // // TODO Probably this is not necessary...only time will tell
        // // allocationScenario.setReady(true);
        // // allocationScenarioService.updateAllocationScenario(
        // // allocationScenario, projectId);
        // } else {
        // message = WifKeys.PROCESS_STATE_RUNNING;
        // }
        if (result.isDone()) {
          try {
            // final Boolean msg = result.get();
            final String msg = result.get();
            LOGGER.info("process ended with result: {}", msg);

          } catch (final ExecutionException e) {
            message = WifKeys.PROCESS_STATE_FAILED;
            final String errorMessage = "Allocation analysis asynchronous process failed";
            answer.put(WifKeys.STATUS_KEY, message);
            LOGGER.info("Status is = {}", answer.get(WifKeys.STATUS_KEY));
            LOGGER.error(errorMessage, e);
            scenariosPool.remove(id);
            throw new AllocationAnalysisFailedException(errorMessage, e);
          }
          message = WifKeys.PROCESS_STATE_SUCCESS;

          // ////// new ali
          message = result.get();
          if (message.equals("")) {
            message = WifKeys.PROCESS_STATE_SUCCESS;
          }
          // ///////////

          scenariosPool.remove(id);
        } else {
          message = WifKeys.PROCESS_STATE_RUNNING;
        }
      } catch (final Exception e) {
        if (e instanceof InterruptedException) {
          LOGGER.error("get status failed for {}", id);
          throw new InvalidEntityIdException("get status failed "
              + e.toString(), e);
        }
      }
    }
    answer.put(WifKeys.STATUS_KEY, message);
    // LOGGER.info("Status is ={}", answer.get(WifKeys.STATUS_KEY));
    return answer;
  }

  /**
   * Gets the wms.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the wms
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/wmsinfo", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  WMSOutcome getWMS(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER
        .info(
            "*******>> getWMS allocation request for allocationScenario id ={}",
            id);
    try {
      return allocationScenarioService.getWMS(id);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidConfigException(e.getMessage(), e);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidInputException(e.getMessage(), e);
    } catch (final ParsingException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new ParsingException(e.getMessage(), e);
    }
  }

  /**
   * Gets the allocation scenario report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the allocation scenario report
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationScenarios/{id}/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationAnalysisReport getAllocationScenarioReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info(
        "*****>> getAllocationScenarioReport request for scenario id ={}", id);

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);
    return reportService.getAllocationAnalysisReport(allocationScenario);

  }

}
