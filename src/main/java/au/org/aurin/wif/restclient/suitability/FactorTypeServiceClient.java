package au.org.aurin.wif.restclient.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.FactorType;

/**
 * The Interface FactorTypeServiceClient.
 */
public interface FactorTypeServiceClient {
  
  /**
   * Gets the factor types for project and factor.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factorId the factor id
   * @return the factor types for project and factor
   * @throws WifInvalidInputException the wif invalid input exception
   */
  List<FactorType> getFactorTypesForProjectAndFactor(String roleId, 
      String projectId, String factorId)
      throws WifInvalidInputException;

  /**
   * Gets the factor type.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factorId the factor id
   * @param id the id
   * @return the factor type
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  FactorType getFactorType(String roleId, String projectId, String factorId,
      String id) throws WifInvalidInputException,
      WifInvalidConfigException;

  /**
   * Creates the factor type.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factorId the factor id
   * @param factorType the factor type
   * @return the string
   */
  String createFactorType(String roleId, String projectId, String factorId,
      FactorType factorType);

  /**
   * Update factor type.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factorId the factor id
   * @param id the id
   * @param factorType the factor type
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateFactorType(String roleId, String projectId, String factorId,
      String id, FactorType factorType)
      throws WifInvalidInputException, WifInvalidConfigException;;

  /**
   * Delete factor type.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param factorId the factor id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   */
  void deleteFactorType(String roleId, String projectId, String factorId,
      String id) throws WifInvalidInputException;

}
