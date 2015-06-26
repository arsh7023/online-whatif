package au.org.aurin.wif.controller;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class DemandScenarioController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class DemandScenarioController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioController.class);

  /** The demand scenario service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /**
   * Sets the demand scenario service.
   * 
   * @param demandScenarioService
   *          the new demand scenario service
   */
  public void setDemandScenarioService(
      final DemandScenarioService demandScenarioService) {
    this.demandScenarioService = demandScenarioService;
  }

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Creates the demand scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param demandScenario
   *          the demand scenario
   * @param response
   *          the response
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/demandScenarios", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  DemandScenario createDemandScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final DemandScenario demandScenario,
      final HttpServletResponse response) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException {
    LOGGER.info("*******>> createDemandScenario request for project  id ={}",
        projectId);
    final String msg = "createDemandScenario failed: {}";
    try {
      return demandScenarioService.createDemandScenario(demandScenario,
          projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteDemandScenarioException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteDemandScenarioException(msg, e);
    }
  }

  /**
   * Gets the demand scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandScenario getDemandScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> getDemandScenario request for scenario id={}", id);
    final String msg = "getDemandScenario failed: {}";
    try {
      return demandScenarioService.getDemandScenario(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Update demand scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param demandScenario
   *          the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/demandScenarios/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateDemandScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final DemandScenario demandScenario)
      throws WifInvalidInputException, BindException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> updateDemandScenario request for project  id ={}",
        projectId);
    final String msg = "updateDemandScenario failed: {}";
    try {
      demandScenario.setId(id);
      demandScenarioService.updateDemandScenario(demandScenario, projectId);
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
   * Delete demand scenario.
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
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/demandScenarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDemandScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER
        .info("*******>> deleteDemandScenario request for scenario id={}", id);
    final String msg = "deleteDemandScenario failed: {}";
    try {
      demandScenarioService.deleteDemandScenario(id, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the demand scenarios for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the demand scenarios for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<DemandScenario> getDemandScenariosForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER.info(
        "*******>> getDemandScenariosForProject request for project id ={}",
        projectId);
    final String msg = "getDemandScenariosForProject failed: {}";
    try {
      return demandScenarioService.getDemandScenarios(projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    }
  }

  /**
   * Gets the demand scenario outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the demand scenario outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}/outcome", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AreaRequirement> getDemandScenarioOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException {
    LOGGER.info(
        "*******>> getDemandScenarioOutcome request for scenario id={}", id);
    final String msg = "getDemandScenarioOutcome failed: {}";
    try {
      return demandScenarioService.getOutcome(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteDemandScenarioException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteDemandScenarioException(msg, e);
    }
  }

  /**
   * Gets the demand scenario report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the demand scenario report
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandScenarioException
   *           the incomplete demand scenario exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demandScenarios/{id}/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandAnalysisReport getDemandScenarioReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandScenarioException {
    LOGGER.info("*******>> getDemandScenarioReport request for scenario id={}",
        id);
    final String msg = "getDemandScenarioReport failed: {}";
    try {
      final DemandScenario demandScenario = demandScenarioService
          .getDemandScenario(id);
      return reportService.getDemandAnalysisReport(demandScenario);
    } catch (final IncompleteDemandScenarioException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteDemandScenarioException(msg, e);
    }
  }
}
