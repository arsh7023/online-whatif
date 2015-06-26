package au.org.aurin.wif.controller;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.util.List;

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

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidFFNameException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.svc.AllocationLUService;

/**
 * The Class AllocationLUController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class AllocationLUController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationLUController.class);

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  /**
   * Sets the allocation lu service.
   * 
   * @param allocationLUService
   *          the new allocation lu service
   */
  public void setAllocationLUService(
      final AllocationLUService allocationLUService) {
    this.allocationLUService = allocationLUService;
  }

  /**
   * Creates the allocation lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param allocationLU
   *          the allocation lu
   * @param response
   *          the response
   * @return the allocation lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   * @throws InvalidFFNameException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/allocationLUs", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  AllocationLU createAllocationLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final AllocationLU allocationLU,
      final HttpServletResponse response) throws WifInvalidInputException,
      BindException, WifInvalidConfigException, InvalidLabelException,
      InvalidFFNameException {
    LOGGER.info("*******>> createAllocationLU request for project  id ={}",
        projectId);

    return allocationLUService.createAllocationLU(allocationLU, projectId);
  }

  /**
   * Gets the allocation lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the allocation lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationLUs/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationLU getAllocationLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("*******>> getAllocationLU request for project  id ={}",
        projectId);

    return allocationLUService.getAllocationLU(id);
  }

  /**
   * Update allocation lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param allocationLU
   *          the allocation lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/allocationLUs/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateAllocationLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final AllocationLU allocationLU)
      throws WifInvalidInputException, BindException, WifInvalidConfigException {
    LOGGER.info("*******>> updateAllocationLU request for project  id ={}",
        projectId);

    allocationLU.setId(id);
    allocationLUService.updateAllocationLU(allocationLU, projectId);
  }

  /**
   * Delete allocation lu.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/allocationLUs/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllocationLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("*******>> deleteAllocationLU request for project  id ={}",
        projectId);

    allocationLUService.deleteAllocationLU(id, projectId);
  }

  /**
   * Gets the allocation l us for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the allocation l us for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationLUs", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AllocationLU> getAllocationLUsForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER.info(
        "*******>> getAllocationLUsForProject request for project  id ={}",
        projectId);

    return allocationLUService.getAllocationLUs(projectId);
  }

  /**
   * Gets the allocation lus Suitability Associated for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the allocation l us for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/allocationLUsSuitabilityAssociated", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AllocationLU> getAllocationLUsSuitabilityAssociated(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info(
        "*******>> getAllocationLUsForProject request for project  id ={}",
        projectId);

    return allocationLUService.getAllocationLUsSuitabilityAssociated(projectId);
  }

}
