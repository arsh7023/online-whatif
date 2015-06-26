package au.org.aurin.wif.restclient.suitability;

import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.Factor;

/**
 * The Interface FactorServiceClient.
 */
public interface FactorServiceClient {

  /**
   * Gets the factors for project.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @return the factors for project
   * @throws WifInvalidInputException the wif invalid input exception
   */
  List<Factor> getFactorsForProject(String roleId, String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the factor.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @return the factor
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  Factor getFactor(String roleId, String projectId, String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Creates the factor.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factor the factor
   * @return the string
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  String createFactor(String roleId, String projectId, Factor factor)
      throws WifInvalidInputException, BindException, WifInvalidConfigException;

  /**
   * Update factor.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @param factor the factor
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateFactor(String roleId, String projectId, String id, Factor factor)
      throws WifInvalidInputException, BindException, WifInvalidConfigException;

  /**
   * Delete factor.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   */
  void deleteFactor(String roleId, String projectId, String id)
      throws WifInvalidInputException;

}
