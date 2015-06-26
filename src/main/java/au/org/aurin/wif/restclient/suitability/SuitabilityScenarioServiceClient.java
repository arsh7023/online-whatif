package au.org.aurin.wif.restclient.suitability;

import java.util.HashMap;
import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.reports.suitability.SuitabilityAnalysisReport;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * The Interface SuitabilityScenarioServiceClient.
 */
public interface SuitabilityScenarioServiceClient {

  /**
   * Sets the url.
   * 
   * @param url
   *          the new url
   */
  void setUrl(String url);

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
  List<SuitabilityScenario> getSuitabilityScenariosForProject(String roleId,
      String projectId) throws WifInvalidInputException;

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
   */
  SuitabilityScenario getSuitabilityScenario(String roleId, String projectId,
      String id) throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the suitability scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param suitabilityScenario
   *          the suitability scenario
   * @return the string
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   */
  String createSuitabilityScenario(String roleId, String projectId,
      SuitabilityScenario suitabilityScenario) throws WifInvalidInputException,
      BindException;

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
  void updateSuitabilityScenario(String roleId, String projectId, String id,
      SuitabilityScenario suitabilityScenario) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

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
   */
  void deleteSuitabilityScenario(String roleId, String projectId, String id)
      throws WifInvalidInputException;

  /**
   * Gets the wMS outcome.
   * 
   * @param roleId
   *          the role id
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the wMS outcome
   */
  WMSOutcome getWMSOutcome(String roleId, String id, String projectId,
      String areaAnalyzed, String crsArea);

  /**
   * Gets the wMS outcome async.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the wMS outcome async
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  void getWMSOutcomeAsync(String roleId, String projectId, String id,
      String areaAnalyzed, String crsArea) throws WifInvalidInputException;

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
   * Gets the suitability scenario report.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param id
   *          the id
   * @return the suitability scenario report
   */
  SuitabilityAnalysisReport getSuitabilityScenarioReport(String roleId,
      String projectId, String id);
}
