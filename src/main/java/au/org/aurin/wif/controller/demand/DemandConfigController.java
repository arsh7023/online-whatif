package au.org.aurin.wif.controller.demand;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.svc.suitability.DemandConfigService;

/**
 * The Class DemandConfigController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class DemandConfigController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandConfigController.class);

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /**
   * Sets the demand config service.
   * 
   * @param demandConfigService
   *          the new demand config service
   */
  public void setDemandConfigService(
      final DemandConfigService demandConfigService) {
    this.demandConfigService = demandConfigService;
  }

  /**
   * Creates the demand config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param demandConfig
   *          the demand config
   * @param response
   *          the response
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteDemandConfigException
   *           the incomplete demand config exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/demand/setup", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  DemandConfig createDemandConfig(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final DemandConfig demandConfig,
      final HttpServletResponse response)
      throws IncompleteDemandConfigException, WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info("*******>> createDemandConfig request for project  id ={}",
        projectId);
    final String msg = "createDemandConfig failed: {}";
    try {
      return demandConfigService.createDemandConfig(demandConfig, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteDemandConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteDemandConfigException(msg, e);
    }
  }

  /**
   * Gets the demand config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demand/setup", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandConfig getDemandConfig(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("*******>> getDemandConfig request for project  id ={}",
        projectId);
    final String msg = "getDemandConfig failed: {}";
    try {
      return demandConfigService.getDemandConfig(projectId);
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
   * Update demand config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param demandConfig
   *          the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   * @throws IncompleteDemandConfigException
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/demand/setup", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  // was NO_CONTENT
  public @ResponseBody
  String updateDemandConfig(
      // instead of public void
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final DemandConfig demandConfig)
      throws WifInvalidInputException, BindException,
      WifInvalidConfigException, ParsingException,
      IncompleteDemandConfigException {
    LOGGER
        .info(
            "*******>> updateDemandConfig/deleting the old one and creating a new demandConfig request for project  id ={}",
            projectId);
    final String msg = "updateDemandConfig failed: {}";
    try {

      demandConfigService.updateDemandConfig(demandConfig, projectId);

      // //very new return revision
      return "{\"_rev\" :\"" + demandConfig.getRevision() + "\"}";

    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * Delete demand config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/demand/setup")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDemandConfig(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("*******>> deleteDemandConfig request for project  id ={}",
        projectId);
    final String msg = "deleteDemandConfig failed: {}";
    try {
      demandConfigService.deleteDemandConfig(projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * ali Gets the demand config report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/demand/setup/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  DemandConfig getDemandConfigReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("*******>> getDemandConfigReport request for project  id ={}",
        projectId);
    final String msg = "getDemandConfigReport failed: {}";
    try {
      return demandConfigService.getDemandConfig(projectId);
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
