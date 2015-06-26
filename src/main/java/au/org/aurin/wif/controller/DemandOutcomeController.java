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
import au.org.aurin.wif.exception.validate.IncompleteDemandOutcomeException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class ManualDemandScenarioController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class DemandOutcomeController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandOutcomeController.class);

  /** The demand scenario service. */
  @Resource
  private DemandOutcomeService manualDemandScenarioService;

  /**
   * Sets the demand scenario service.
   * 
   * @param DemandOutcomeService
   *          the new demand scenario service
   */
  public void setManualDemandScenarioService(
      final DemandOutcomeService manualDemandScenarioService) {
    this.manualDemandScenarioService = manualDemandScenarioService;
  }

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Creates the demand Outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param ManualDemandScenario
   *          the demand scenario
   * @param response
   *          the response
   * @return the demand Outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandOutcomeException
   *           the incomplete demand scenario exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/manualDemandScenarios", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  DemandOutcome createDemandOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final DemandOutcome ManualDemandScenario,
      final HttpServletResponse response) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandOutcomeException {
    LOGGER.info("*******>> createDemandOutcome request for project  id ={}",
        projectId);
    final String msg = "createDemandOutcome failed: {}";
    try {
      return manualDemandScenarioService.createDemandOutcomeNew(
          ManualDemandScenario, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteDemandOutcomeException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteDemandOutcomeException(msg, e);
    }
  }

  /**
   * Gets the demand Outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the demand Outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/manualDemandScenarios/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandOutcome getDemandOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> getDemandOutcome request for scenario id={}", id);
    final String msg = "getDemandOutcome failed: {}";
    try {
      // return manualDemandScenarioService.getDemandOutcome(id);
      return manualDemandScenarioService.getDemandOutcome(id, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Update demand outcome.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param ManualDemandScenario
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
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/manualDemandScenarios/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateDemandOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final DemandOutcome ManualDemandScenario)
      throws WifInvalidInputException, BindException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> updateDemandOutcome request for project  id ={}",
        projectId);
    final String msg = "updateDemandOutcome failed: {}";
    try {
      ManualDemandScenario.setId(id);
      manualDemandScenarioService.updateDemandOutcome(ManualDemandScenario,
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
    }
  }

  /**
   * Delete demand Outcome.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/manualDemandScenarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDemandOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> deleteDemandOutcome request for scenario id={}", id);
    final String msg = "deleteDemandOutcome failed: {}";
    try {
      manualDemandScenarioService.deleteDemandOutcome(id, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Gets the demand Outcomes for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the demand scenarios for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/manualDemandScenarios", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<DemandOutcome> getDemandOutcomeForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER.info(
        "*******>> getDemandOutcomeForProject request for project id ={}",
        projectId);
    final String msg = "getDemandOutcomeForProject failed: {}";
    try {
      return manualDemandScenarioService.getDemandOutcomes(projectId);
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
   * @throws IncompleteDemandOutcomeException
   *           the incomplete demand scenario exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/manualDemandScenarios/{id}/outcome", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandOutcome getDemandOutcomesOutcome(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandOutcomeException {
    LOGGER.info(
        "*******>> getManualDemandScenarioOutcome request for scenario id={}",
        id);
    final String msg = "getManualDemandScenarioOutcome failed: {}";
    try {
      return manualDemandScenarioService.getDemandOutcome(id, projectId);
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

}
