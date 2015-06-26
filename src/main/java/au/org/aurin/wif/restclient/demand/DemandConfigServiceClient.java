package au.org.aurin.wif.restclient.demand;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.DemandConfig;

/**
 * The Interface DemandConfigServiceClient.
 */
public interface DemandConfigServiceClient {

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  void setUrl(String url);

  /**
   * Gets the demand config.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @return the demand config
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  DemandConfig getDemandConfig(String roleId, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;


  /**
   * Creates the demand config.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param demandConfig the demand config
   * @return the string
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   */
  String createDemandConfig(String roleId, String projectId, DemandConfig demandConfig)
      throws WifInvalidInputException, BindException;

  /**
   * Update demand config.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param demandConfig the demand config
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateDemandConfig(String roleId, String projectId, DemandConfig demandConfig)
      throws WifInvalidInputException, BindException, WifInvalidConfigException;

  /**
   * Delete demand config.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @throws WifInvalidInputException the wif invalid input exception
   */
  void deleteDemandConfig(String roleId, String projectId)
      throws WifInvalidInputException;

}
