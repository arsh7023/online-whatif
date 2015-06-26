package au.org.aurin.wif.restclient.demand;

import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.reports.demand.DemandAnalysisReport;

/**
 * The Interface DemandScenarioServiceClient.
 */
public interface DemandScenarioServiceClient {

  /**
   * Sets the url.
   * 
   * @param url
   *          the new url
   */
  void setUrl(String url);

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
  List<DemandScenario> getDemandScenariosForProject(String roleId,
      String projectId) throws WifInvalidInputException;

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
   */
  DemandScenario getDemandScenario(String roleId, String projectId, String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the demand scenario.
   * 
   * @param roleId
   *          the role id
   * @param projectId
   *          the project id
   * @param demandScenario
   *          the demand scenario
   * @return the string
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws BindException
   *           the bind exception
   */
  String createDemandScenario(String roleId, String projectId,
      DemandScenario demandScenario) throws WifInvalidInputException,
      BindException;

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
   */
  void updateDemandScenario(String roleId, String projectId, String id,
      DemandScenario demandScenario) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

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
   */
  void deleteDemandScenario(String roleId, String projectId, String id)
      throws WifInvalidInputException;

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
   */
  List<AreaRequirement> getDemandScenarioOutcome(String roleId,
      String projectId, String id) throws WifInvalidInputException,
      WifInvalidConfigException;

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
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   */
  DemandAnalysisReport getDemandScenarioReport(String roleId, String projectId,
      String id) throws WifInvalidInputException, WifInvalidConfigException;

}
