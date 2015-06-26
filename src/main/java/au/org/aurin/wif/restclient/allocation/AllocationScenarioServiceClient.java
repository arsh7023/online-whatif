package au.org.aurin.wif.restclient.allocation;

import java.util.HashMap;
import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.reports.allocation.AllocationAnalysisReport;

/**
 * The Interface AllocationScenarioServiceClient.
 */
public interface AllocationScenarioServiceClient {

  /**
   * Sets the url.
   * 
   * @param url
   *          the new url
   */
  void setUrl(String url);

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
  List<AllocationScenario> getAllocationScenariosForProject(String roleId,
      String projectId) throws WifInvalidInputException;

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
   */
  AllocationScenario getAllocationScenario(String roleId, String projectId,
      String id) throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param allocationScenario
   *          the allocation scenario
   * @return the string
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   */
  String createAllocationScenario(String roleId, String projectId,
      AllocationScenario allocationScenario) throws WifInvalidInputException,
      BindException;

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
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateAllocationScenario(String roleId, String projectId, String id,
      AllocationScenario allocationScenario) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

  /**
   * Delete allocation scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void deleteAllocationScenario(String roleId, String projectId, String id)
      throws WifInvalidInputException;

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
   */
  void getOutcomeAsync(String roleId, String projectId, String id)
      throws WifInvalidInputException;

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
   */
  HashMap<String, String> getStatus(String roleId, String projectId, String id);

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
   */
  WMSOutcome getWMS(String roleId, String projectId, String id);

  /**
   * Gets the allocation scenario report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param allocationScenarioId
   *          the allocation scenario id
   * @return the allocation scenario report
   */
  AllocationAnalysisReport getAllocationScenarioReport(String roleId,
      String projectId, String allocationScenarioId);
}
