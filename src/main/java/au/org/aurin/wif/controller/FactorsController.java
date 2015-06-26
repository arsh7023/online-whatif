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
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.svc.suitability.FactorService;

/**
 * The Class FactorsController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class FactorsController {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(FactorsController.class);

  /** the service. */
  @Resource
  private FactorService factorService;

  /**
   * Sets the factor service.
   * 
   * @param factorService
   *          the new factor service
   */
  public void setFactorService(final FactorService factorService) {
    this.factorService = factorService;
  }

  /**
   * Gets the factors for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the factors for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/factors", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<Factor> getFactorsForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER.info("*******>> getFactorsForProject request for project  id ={}",
        projectId);
    return factorService.getFactors(projectId);
  }

  /**
   * Gets the factor.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/factors/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  Factor getFactor(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER.info("*******>> getFactor request for project  id ={}", projectId);
    return factorService.getFactor(id, projectId);
  }

  /**
   * Creates the factor.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param factor
   *          the factor
   * @param response
   *          the response
   * @return the factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/factors", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  Factor createFactor(@RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final Factor factor, final HttpServletResponse response)
      throws WifInvalidInputException, BindException,
      WifInvalidConfigException, InvalidLabelException {
    LOGGER
        .info("*******>> createFactor request for project  id ={}", projectId);
    return factorService.createFactor(factor, projectId);
  }

  /**
   * Update factor.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param factor
   *          the factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/factors/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateFactor(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id, @RequestBody final Factor factor)
      throws WifInvalidInputException, BindException, WifInvalidConfigException {
    LOGGER
        .info("*******>> updateFactor request for project  id ={}", projectId);
    factor.setId(id);
    factorService.updateFactor(factor, projectId);
  }

  /**
   * Delete factor.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/factors/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFactor(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException {
    LOGGER
        .info("*******>> deleteFactor request for project  id ={}", projectId);

    factorService.deleteFactor(id, projectId);
  }

}
