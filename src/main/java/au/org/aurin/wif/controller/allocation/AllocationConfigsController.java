package au.org.aurin.wif.controller.allocation;

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
import au.org.aurin.wif.exception.validate.IncompleteAllocationConfigsException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.svc.allocation.AllocationConfigsService;

/**
 * The Class AllocationConfigsController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class AllocationConfigsController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationConfigsController.class);

  /** The Allocation config service. */
  @Resource
  private AllocationConfigsService AllocationConfigsService;

  /**
   * Sets the Allocation config service.
   * 
   * @param AllocationConfigsService
   *          the new Allocation config service
   */
  public void setAllocationConfigsService(
      final AllocationConfigsService AllocationConfigsService) {
    this.AllocationConfigsService = AllocationConfigsService;
  }

  /**
   * Creates the Allocation config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param AllocationConfigs
   *          the Allocation config
   * @param response
   *          the response
   * @return the Allocation config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   * @throws IncompleteAllocationConfigsException
   *           the incomplete Allocation config exception
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/AllocationConfigs/setup", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  AllocationConfigs createAllocationConfigs(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final AllocationConfigs AllocationConfigs,
      final HttpServletResponse response)
      throws IncompleteAllocationConfigsException, WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info(
        "*******>> createAllocationConfigs request for project  id ={}",
        projectId);
    final String msg = "createAllocationConfigs failed: {}";
    try {

      final AllocationConfigs allocationConfigs = AllocationConfigsService
          .createAllocationConfigs(AllocationConfigs, projectId);

      AllocationConfigsService.CreateStyle(allocationConfigs, projectId, true);
      return allocationConfigs;

      // return AllocationConfigsService.createAllocationConfigs(
      // AllocationConfigs, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteAllocationConfigsException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteAllocationConfigsException(msg, e);
    }
  }

  /**
   * Gets the Allocation config.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the Allocation config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/AllocationConfigs/setup", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationConfigs getAllocationConfigs(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info("*******>> getAllocationConfigs request for project  id ={}",
        projectId);
    final String msg = "getAllocationConfigs failed: {}";
    try {
      return AllocationConfigsService.getAllocationConfigs(projectId);
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
   * Update Allocation config. Because of a complex logic constraints, updating
   * values are not allowed, deleting the old one and creating a new
   * AllocationConfigs
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param AllocationConfigs
   *          the Allocation config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   * @throws IncompleteAllocationConfigsException
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/AllocationConfigs/setup", consumes = "application/json")
  @ResponseStatus(HttpStatus.OK)
  // was NO_CONTENT
  public @ResponseBody
  AllocationConfigs updateAllocationConfigs(
      // instead of public void
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final AllocationConfigs AllocationConfigs)
      throws WifInvalidInputException, BindException,
      WifInvalidConfigException, ParsingException,
      IncompleteAllocationConfigsException {
    LOGGER
        .info(
            "*******>> updateAllocationConfigs/deleting the old one and creating a new AllocationConfigs request for project  id ={}",
            projectId);
    final String msg = "updateAllocationConfigs failed: {}";
    try {
      AllocationConfigsService.deleteAllocationConfigs(projectId);
      AllocationConfigs.setRevision(null);

      final AllocationConfigs allocationConfigs = AllocationConfigsService
          .createAllocationConfigs(AllocationConfigs, projectId);

      AllocationConfigsService.CreateStyle(allocationConfigs, projectId, false);
      return allocationConfigs;
      // return AllocationConfigsService.createAllocationConfigs(
      // AllocationConfigs, projectId);

    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    } catch (final ParsingException e) {
      LOGGER.error(msg, e.getMessage());
      throw new ParsingException(msg, e);
    } catch (final IncompleteAllocationConfigsException e) {
      LOGGER.error(msg, e.getMessage());
      throw new IncompleteAllocationConfigsException(msg, e);
    }
  }

  /**
   * Delete Allocation config.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/AllocationConfigs/setup")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllocationConfigs(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info(
        "*******>> deleteAllocationConfigs request for project  id ={}",
        projectId);
    final String msg = "deleteAllocationConfigs failed: {}";
    try {
      AllocationConfigsService.deleteAllocationConfigs(projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidInputException(msg, e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error(msg, e.getMessage());
      throw new WifInvalidConfigException(msg, e);
    }
  }

  /**
   * ali Gets the Allocation config report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the Allocation config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/AllocationConfigs/setup/report", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationConfigs getAllocationConfigsReport(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    LOGGER.info(
        "*******>> getAllocationConfigsReport request for project  id ={}",
        projectId);
    final String msg = "getAllocationConfigsReport failed: {}";
    try {
      return AllocationConfigsService.getAllocationConfigs(projectId);
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
