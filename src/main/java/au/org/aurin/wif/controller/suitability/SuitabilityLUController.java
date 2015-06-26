package au.org.aurin.wif.controller.suitability;

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

import au.org.aurin.wif.controller.OWIURLs;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.svc.suitability.SuitabilityLUService;

/**
 * The Class SuitabilityLUController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class SuitabilityLUController {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityLUController.class);

  /** the service. */
  @Resource
  private SuitabilityLUService suitabilityLUService;

  /**
   * Sets the suitability lu service.
   * 
   * @param suitabilityLUService
   *          the new suitability lu service
   */
  public void setSuitabilityLUService(
      final SuitabilityLUService suitabilityLUService) {
    this.suitabilityLUService = suitabilityLUService;
  }

  // TODO Gerson to wrap the service calls in try-catch blocks so that we can
  // log what sort of exceptions were thrown while accessing the service.

  /**
   * Gets the suitability l us for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the suitability l us for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityLUs", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<SuitabilityLU> getSuitabilityLUsForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER.info(
        "*******>> getSuitabilityLUsForProject request for project  id ={}",
        projectId);

    return suitabilityLUService.getSuitabilityLUs(projectId);
  }

  /**
   * Gets the suitability lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the suitability lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityLUs/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  SuitabilityLU getSuitabilityLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("*******>> getSuitabilityLU request for project  id ={}",
        projectId);

    return suitabilityLUService.getSuitabilityLU(id);
  }

  /**
   * Creates the suitability lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityLU
   *          the suitability lu
   * @param response
   *          the response
   * @return the suitability lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/suitabilityLUs", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  SuitabilityLU createSuitabilityLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final SuitabilityLU suitabilityLU,
      final HttpServletResponse response) throws WifInvalidInputException,
      BindException, WifInvalidConfigException, InvalidLabelException {
    LOGGER.info("*******>> createSuitabilityLU request for project  id ={}",
        projectId);

    return suitabilityLUService.createSuitabilityLU(suitabilityLU, projectId);
  }

  /**
   * Update suitability lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param suitabilityLU
   *          the suitability lu
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/suitabilityLUs/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateSuitabilityLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final SuitabilityLU suitabilityLU)
      throws WifInvalidInputException, BindException, WifInvalidConfigException {
    LOGGER.info("*******>> updateSuitabilityLU request for project  id ={}",
        projectId);

    suitabilityLU.setId(id);
    suitabilityLUService.updateSuitabilityLU(suitabilityLU, projectId);
  }

  /**
   * Delete suitability lu.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/suitabilityLUs/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSuitabilityLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("*******>> deleteSuitabilityLU request for project  id ={}",
        projectId);

    suitabilityLUService.deleteSuitabilityLU(id, projectId);
  }

  /**
   * Adds the associated lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityLUId
   *          the suitability lu id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/suitabilityLUs/{suitabilityLUId}/associatedLUs/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addAssociatedLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("suitabilityLUId") final String suitabilityLUId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      BindException, WifInvalidConfigException {
    LOGGER.info("*******>> addAssociatedLU request for project  id ={}",
        projectId);

    suitabilityLUService.addAssociatedLU(id, suitabilityLUId, projectId);
  }

  /**
   * Delete associated lu.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityLUId
   *          the suitability lu id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/suitabilityLUs/{suitabilityLUId}/associatedLUs/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAssociatedLU(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("suitabilityLUId") final String suitabilityLUId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      BindException, WifInvalidConfigException {
    LOGGER.info("*******>> deleteAssociatedLU request for project  id ={}",
        projectId);

    suitabilityLUService.deleteAssociatedLU(id, suitabilityLUId, projectId);
  }

  /**
   * Gets the associated lus.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityLUId
   *          the suitability lu id
   * @return the associated l us
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/suitabilityLUs/{suitabilityLUId}/associatedLUs")
  public @ResponseBody
  List<String> getAssociatedLUs(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("suitabilityLUId") final String suitabilityLUId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("*******>> getAssociatedLUs request for project  id ={}",
        projectId);

    return suitabilityLUService.getAssociatedLUs(suitabilityLUId, projectId);
  }

}
