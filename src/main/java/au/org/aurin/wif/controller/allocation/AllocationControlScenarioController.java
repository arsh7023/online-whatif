package au.org.aurin.wif.controller.allocation;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
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
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.validate.IncompleteAllocationControlScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.executors.svc.AsyncAllocationService;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.svc.allocation.AllocationControlScenarioService;
import au.org.aurin.wif.svc.report.ReportService;

/**
 * The Class AllocationControlScenarioController.
 */
@Controller
@RequestMapping(OWIURLs.PROJECT_SVC_URI)
public class AllocationControlScenarioController {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationControlScenarioController.class);

  /** The allocation scenario service. */
  @Resource
  private AllocationControlScenarioService AllocationControlScenarioService;

  /** The async allocation service. */
  @Resource
  private AsyncAllocationService asyncAllocationService;

  /** The scenarios pool. */
  private final HashMap<String, Future<Boolean>> scenariosPool = new HashMap<String, Future<Boolean>>();

  /** The report service. */
  @Autowired
  private ReportService reportService;

  /**
   * Sets the allocation control scenario service.
   * 
   * @param AllocationControlScenarioService
   *          the new allocation scenario service
   */
  public void setAllocationControlScenarioService(
      final AllocationControlScenarioService AllocationControlScenarioService) {
    this.AllocationControlScenarioService = AllocationControlScenarioService;
  }

  /**
   * Gets the allocation control scenarios for project.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @return the allocation scenarios for project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/AllocationControlScenarios", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  List<AllocationControlScenario> getAllocationControlScenariosForProject(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId)
      throws WifInvalidInputException {
    LOGGER
        .info(
            "*******>> getAllocationControlScenariosForProject request for project  id ={}",
            projectId);

    return AllocationControlScenarioService
        .getAllocationControlScenarios(projectId);
  }

  /**
   * Gets the allocation control scenario.
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
  @RequestMapping(method = RequestMethod.GET, value = "/{projectId}/AllocationControlScenarios/{id}", produces = "application/json")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  AllocationControlScenario getAllocationControlScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {
    LOGGER.info(
        "*******>> getAllocationControlScenario request for project  id ={}",
        projectId);

    try {
      return AllocationControlScenarioService.getAllocationControlScenario(id);
    } catch (final WifInvalidInputException e) {
      LOGGER.error("getAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException("getAllocationControlScenario failed",
          e);
    } catch (final WifInvalidConfigException e) {
      LOGGER.error("getAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException(
          "getAllocationControlScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER.error("getAllocationControlScenario failed: {}", e.getMessage());
      throw new ParsingException("getAllocationControlScenario failed", e);
    }
  }

  /**
   * Creates the allocation control scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param AllocationControlScenario
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
   * @throws IncompleteAllocationControlScenarioException
   *           the incomplete allocation scenario exception
   * @throws FactoryException
   * @throws GeoServerConfigException
   * @throws DataStoreUnavailableException
   * @throws NoSuchAuthorityCodeException
   * @throws MalformedURLException
   * @throws IllegalArgumentException
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{projectId}/AllocationControlScenarios", consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  AllocationControlScenario createAllocationControlScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @RequestBody final AllocationControlScenario AllocationControlScenario,
      final HttpServletResponse response) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException,
      IncompleteAllocationControlScenarioException, IllegalArgumentException,
      MalformedURLException, NoSuchAuthorityCodeException,
      DataStoreUnavailableException, GeoServerConfigException, FactoryException {
    LOGGER
        .info(
            "*******>> createAllocationControlScenario request for project  id ={}",
            projectId);
    try {

      return AllocationControlScenarioService.createAllocationControlScenario(
          AllocationControlScenario, projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER
          .error("createAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException(
          "createAllocationControlScenario failed", e);
    } catch (final WifInvalidConfigException e) {
      LOGGER
          .error("createAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException(
          "createAllocationControlScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER
          .error("createAllocationControlScenario failed: {}", e.getMessage());
      throw new ParsingException("createAllocationControlScenario failed", e);
    } catch (final IncompleteAllocationControlScenarioException e) {
      LOGGER
          .error("createAllocationControlScenario failed: {}", e.getMessage());
      throw new IncompleteAllocationControlScenarioException(
          "createAllocationControlScenario failed", e);
    }
  }

  /**
   * Update allocation control scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param AllocationControlScenario
   *          the allocation scenario
   * @throws Exception
   *           the exception
   */
  @RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/AllocationControlScenarios/{id}", consumes = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateAllocationControlScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id,
      @RequestBody final AllocationControlScenario AllocationControlScenario)
      throws Exception {
    LOGGER
        .info(
            "*******>> updateAllocationControlScenario request for project  id ={}",
            projectId);
    try {
      AllocationControlScenario.setId(id);
      AllocationControlScenarioService.updateAllocationControlScenario(
          AllocationControlScenario, projectId);
    } catch (final Exception e) {
      LOGGER
          .error("updateAllocationControlScenario failed: {}", e.getMessage());
      throw new Exception("updateAllocationControlScenario failed", e);
    }
  }

  /**
   * Delete allocation control scenario.
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
  @RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/AllocationControlScenarios/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllocationControlScenario(
      @RequestHeader(HEADER_USER_ID_KEY) final String roleId,
      @PathVariable("projectId") final String projectId,
      @PathVariable("id") final String id) throws Exception {
    LOGGER
        .info(
            "*******>> deleteAllocationControlScenario request for project  id ={}",
            projectId);
    try {
      AllocationControlScenarioService.deleteAllocationControlScenario(id,
          projectId);
    } catch (final WifInvalidInputException e) {
      LOGGER
          .error("deleteAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidInputException(
          "deleteAllocationControlScenario failed", e);
    } catch (final WifInvalidConfigException e) {
      LOGGER
          .error("deleteAllocationControlScenario failed: {}", e.getMessage());
      throw new WifInvalidConfigException(
          "deleteAllocationControlScenario failed", e);
    } catch (final ParsingException e) {
      LOGGER
          .error("deleteAllocationControlScenario failed: {}", e.getMessage());
      throw new ParsingException("deleteAllocationControlScenario failed", e);
    }
  }

}
