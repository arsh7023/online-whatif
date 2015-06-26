package au.org.aurin.wif.controller.suitability;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.geotools.feature.FeatureCollection;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import au.org.aurin.wif.controller.OWIURLs;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.executors.svc.AsyncSuitabilityService;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.report.ReportService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.vividsolutions.jts.io.ParseException;

/**
 * The Class SuitabilityScenarioController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class SuitabilityScenarioController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityScenarioController.class);

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /** The async suitability service. */
  @Resource
  private AsyncSuitabilityService asyncSuitabilityService;

  /** The scenarios pool. */
  private final HashMap<String, Future<Boolean>> scenariosPool = new HashMap<String, Future<Boolean>>();

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Sets the suitability scenario service.
   * 
   * @param suitabilityScenarioService
   *          the new suitability scenario service
   */
  public void setSuitabilityScenarioService(
      final SuitabilityScenarioService suitabilityScenarioService) {
    this.suitabilityScenarioService = suitabilityScenarioService;
  }

  /**
   * Gets the suitability scenarios for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the suitability scenarios for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<SuitabilityScenario> getSuitabilityScenariosForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER
        .info(
            "*******>> getSuitabilityScenariosForProject request for project  id ={}",
            projectId);

    return suitabilityScenarioService.getSuitabilityScenarios(projectId);
  }

  /**
   * Gets the suitability scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  SuitabilityScenario getSuitabilityScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> getSuitabilityScenario request for project  id ={}",
        projectId);

    return suitabilityScenarioService.getSuitabilityScenario(id);
  }

  /**
   * Creates the suitability scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityScenario
   *          the suitability scenario
   * @param response
   *          the response
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/suitabilityScenarios", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  SuitabilityScenario createSuitabilityScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final SuitabilityScenario suitabilityScenario,
      final HttpServletResponse response) throws WifInvalidInputException,
      BindException, WifInvalidConfigException, ParsingException {
    LOGGER.info(
        "*******>> createSuitabilityScenario request for project  id ={}",
        projectId);

    return suitabilityScenarioService.createSuitabilityScenario(
        suitabilityScenario, projectId);
  }

  /**
   * Update suitability scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param suitabilityScenario
   *          the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/suitabilityScenarios/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateSuitabilityScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final SuitabilityScenario suitabilityScenario)
      throws WifInvalidInputException, BindException, WifInvalidConfigException {
    LOGGER.info(
        "*******>> updateSuitabilityScenario request for project  id ={}",
        projectId);

    suitabilityScenarioService.updateSuitabilityScenario(suitabilityScenario,
        projectId);
  }

  /**
   * Delete suitability scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/suitabilityScenarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSuitabilityScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info(
        "*******>> deleteSuitabilityScenario request for project  id ={}",
        projectId);

    suitabilityScenarioService.deleteSuitabilityScenario(id, projectId);
  }

  /**
   * Gets the wMS outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param getWmsOutcomeParams
   *          the get wms outcome params
   * @return the wMS outcome
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
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/suitabilityScenarios/{id}/wms", produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void getWMSOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final Map<String, String> getWmsOutcomeParams)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      SuitabilityAnalysisFailedException, CQLException, ParsingException {
    LOGGER.info("*******>> getWMSOutcome request for project  id ={}",
        projectId);

    try {
      final String areaAnalyzed = getWmsOutcomeParams.get("areaAnalyzed");
      final String crsArea = getWmsOutcomeParams.get("crsArea");
      suitabilityScenarioService.getWMSOutcome(id, areaAnalyzed, crsArea);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidConfigException(e.getMessage(), e);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidInputException(e.getMessage(), e);
    } catch (final MismatchedDimensionException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new MismatchedDimensionException(e.getMessage(), e);
    } catch (final CQLException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new CQLException(e.getMessage());
    }
  }

  /**
   * Gets the wMS outcome async.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param getWmsOutcomeParams
   *          the get wms outcome params
   * @return the wMS outcome async
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
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/suitabilityScenarios/{id}/async/wms", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public void getWMSOutcomeAsync(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final Map<String, String> getWmsOutcomeParams)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      SuitabilityAnalysisFailedException, CQLException, InterruptedException,
      ExecutionException, ParsingException {
    LOGGER.info("*******>> getWMSOutcomeAsync request for project  id ={}",
        projectId);

    try {
      final String areaAnalyzed = getWmsOutcomeParams.get("areaAnalyzed");
      final String crsArea = getWmsOutcomeParams.get("crsArea");

      LOGGER.info("areaAnalyzed ={}", areaAnalyzed);
      LOGGER.info("crsArea ={}", crsArea);

      final Future<Boolean> outcome = asyncSuitabilityService
          .doSuitabilityAnalysisWMSAsync(id, areaAnalyzed, crsArea);
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
   * Gets the outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param getOutcomeParams
   *          the get outcome params
   * @return the outcome
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
   * @throws ParsingException
   *           the parsing exception
   * @throws DatabaseFailedException
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/suitabilityScenarios/{id}/outcome", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  FeatureCollection<FeatureType, Feature> getOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final Map<String, String> getOutcomeParams)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      SuitabilityAnalysisFailedException, CQLException, ParsingException,
      DatabaseFailedException {
    LOGGER.info("*******>> getOutcome request for project  id ={}", projectId);

    try {
      final String areaAnalyzed = getOutcomeParams.get("areaAnalyzed");
      final String crsArea = getOutcomeParams.get("crsArea");
      return (FeatureCollection) suitabilityScenarioService.getOutcome(id,
          areaAnalyzed, crsArea);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidConfigException(e.getMessage(), e);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new WifInvalidInputException(e.getMessage(), e);
    } catch (final MismatchedDimensionException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new MismatchedDimensionException(e.getMessage(), e);
    } catch (final CQLException e) {
      LOGGER.error("getOutcome failed: {}", e.getMessage());
      throw new CQLException(e.getMessage());
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
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/status", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  HashMap<String, String> getStatus(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, SuitabilityAnalysisFailedException {
    LOGGER.debug("*******>> getScenarioStatus request for scenario id ={}", id);
    final HashMap<String, String> answer = new HashMap<String, String>(2);
    answer.put(WifKeys.SETUP_PROCESS_KEY,
        WifKeys.SUITABILITY_PROCESS_STATE_SETUP);
    String statusMessage = WifKeys.PROCESS_STATE_NA;
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenarioNoMapping(id);
    if (suitabilityScenario.getReady()) {
      statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
    } else {
      try {
        final Future<Boolean> result = scenariosPool.get(id);
        if (result == null) {
          LOGGER.error("id not found in scenariosPool for {}", id);
          throw new WifInvalidInputException("id not found in scenariosPool");
        }
        if (result.isDone()) {
          try {
            final Boolean msg = result.get();
            LOGGER.info("process ended with result: {}", msg);
          } catch (final ExecutionException e) {
            statusMessage = WifKeys.PROCESS_STATE_FAILED;
            final String errorMessage = "suitability analysis asynchronous process failed";
            answer.put(WifKeys.STATUS_KEY, statusMessage);
            LOGGER.info("Status is = {}", answer.get(WifKeys.STATUS_KEY));
            LOGGER.error(errorMessage, e);
            scenariosPool.remove(id);
            throw new SuitabilityAnalysisFailedException(errorMessage, e);
          }
          statusMessage = WifKeys.PROCESS_STATE_SUCCESS;
          scenariosPool.remove(id);
        } else {
          statusMessage = WifKeys.PROCESS_STATE_RUNNING;
        }
      } catch (final Exception e) {
        if (e instanceof InterruptedException) {
          LOGGER.error("get status failed for {}", id);
          throw new InvalidEntityIdException("get status failed ", e);
        }
      }
    }
    answer.put(WifKeys.STATUS_KEY, statusMessage);
    LOGGER.debug("Status is ={}", answer.get(WifKeys.STATUS_KEY));
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/wmsinfo", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  WMSOutcome getWMS(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> getWMS request for suitabilityScenario  id ={}", id);
    try {
      return suitabilityScenarioService.getWMS(id);
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
   * Gets the suitability scenario report based on the latest analysis
   * configuration.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the suitability scenario report
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityScenarios/{id}/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  SuitabilityAnalysisReport getSuitabilityScenarioReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info(
        "*******>> getSuitabilityScenarioReport request for scenario id ={}",
        id);
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    return reportService.getSuitabilityAnalysisReport(suitabilityScenario);

  }

  /**
   * Gets the suitabilityLUsScores
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return String
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityLUsScores", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<String> getSuitabilityLUsScoresForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER
        .info(
            "*******>> getSuitabilityLUsScoresForProject request for project  id ={}",
            projectId);

    return reportService.getSuitabilityLUsScores(projectId);
  }

}
