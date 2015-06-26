package au.org.aurin.wif.restclient.suitability;

import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.SuitabilityLU;

/**
 * The Interface SuitabilityLUServiceClient.
 */
public interface SuitabilityLUServiceClient {

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  void setUrl(String url);

  /**
   * Gets the suitability l us for project.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @return the suitability l us for project
   * @throws WifInvalidInputException the wif invalid input exception
   */
  List<SuitabilityLU> getSuitabilityLUsForProject(String roleId,
      String projectId) throws WifInvalidInputException;

  /**
   * Gets the suitability lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @return the suitability lu
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  SuitabilityLU getSuitabilityLU(String roleId, String projectId, String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the suitability lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param suitabilityLU the suitability lu
   * @return the string
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  String createSuitabilityLU(String roleId, String projectId,
      SuitabilityLU suitabilityLU) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

  /**
   * Update suitability lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @param suitabilityLU the suitability lu
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateSuitabilityLU(String roleId, String projectId, String id,
      SuitabilityLU suitabilityLU) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

  /**
   * Delete suitability lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   */
  void deleteSuitabilityLU(String roleId, String projectId, String id)
      throws WifInvalidInputException;

  /**
   * Adds the associated lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param suitabilityLUId the suitability lu id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void addAssociatedLU(String roleId, String projectId, String suitabilityLUId,
      String id) throws WifInvalidInputException, BindException,
      WifInvalidConfigException;

  /**
   * Delete associated lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param suitabilityLUId the suitability lu id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void deleteAssociatedLU(String roleId, String projectId,
      String suitabilityLUId, String id) throws WifInvalidInputException,
      BindException, WifInvalidConfigException;

  /**
   * Gets the associated l us.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param suitabilityLUId the suitability lu id
   * @return the associated l us
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  List<String> getAssociatedLUs(String roleId, String projectId,
      String suitabilityLUId) throws WifInvalidInputException, BindException,
      WifInvalidConfigException;
}
