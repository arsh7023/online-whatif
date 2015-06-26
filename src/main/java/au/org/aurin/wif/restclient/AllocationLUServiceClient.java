package au.org.aurin.wif.restclient;

import java.util.List;

import org.springframework.validation.BindException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Interface AllocationLUServiceClient.
 */
public interface AllocationLUServiceClient {

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  void setUrl(String url);

  /**
   * Gets the allocation l us for project.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @return the allocation l us for project
   * @throws WifInvalidInputException the wif invalid input exception
   */
  List<AllocationLU> getAllocationLUsForProject(String roleId, String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the allocation lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @return the allocation lu
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  AllocationLU getAllocationLU(String roleId, String projectId, String id)
      throws WifInvalidInputException, WifInvalidConfigException;


  /**
   * Creates the allocation lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param allocationLU the allocation lu
   * @return the string
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   */
  String createAllocationLU(String roleId, String projectId, AllocationLU allocationLU)
      throws WifInvalidInputException, BindException;

  /**
   * Update allocation lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @param allocationLU the allocation lu
   * @throws WifInvalidInputException the wif invalid input exception
   * @throws BindException the bind exception
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateAllocationLU(String roleId, String projectId, String id, AllocationLU allocationLU)
      throws WifInvalidInputException, BindException, WifInvalidConfigException;

  /**
   * Delete allocation lu.
   *
   * @param roleId the role id
   * @param projectId the project id
   * @param id the id
   * @throws WifInvalidInputException the wif invalid input exception
   */
  void deleteAllocationLU(String roleId, String projectId, String id)
      throws WifInvalidInputException;

}
