package au.org.aurin.wif.svc.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.InvalidLabelException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;

/**
 * The Interface FactorService CRUD.It handles the CRUD operations on Factor,
 * plus other important access operations
 */
public interface FactorService {

  /**
   * Adds the factor.
   *
   * @param factor
   *          the factor
   * @param projectId
   *          the project id
   * @return the wif factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws InvalidLabelException
   */
  Factor createFactor(Factor factor, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      InvalidLabelException;

  /**
   * Gets the factor.
   *
   * @param id
   *          the id
   * @return the factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  Factor getFactor(String id) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Gets the factor.
   *
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the factor
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  Factor getFactor(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Update factor.
   *
   * @param factor
   *          the factor
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateFactor(Factor factor, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete factor.
   *
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteFactor(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Gets the factors for a given project.
   *
   * @param projectId
   *          the project id
   * @return the factors
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<Factor> getFactors(String projectId) throws WifInvalidInputException;


  /**
   * Gets the factorTypes for a given factor.
   *
   * @param factorId
   *          the factor id
   * @return the factorTypes
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<FactorType> getFactorTypes(String factorId) throws WifInvalidInputException;


  /**
   * Gets the factorType.
   *
   * @param projectId
   *          the project id
   * @param factorId
   *          the factor id
   * @param id
   *          the id
   * @return the factorType
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  FactorType getFactorType(String projectId, String factorId, String id )
      throws WifInvalidInputException, WifInvalidConfigException;


  /**
   * Delete factorType.
   *
   *
   * @param projectId
   *          the project id
   * @param factorid
   *          the factorid
   * @param id
   *          the id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteFactorType(String projectId, String factorId, String id)
      throws WifInvalidInputException, WifInvalidConfigException;


  /**
   * Gets the factorTypeByLabel.
   *
   * @param projectId
   *          the project id
   * @param factorId
   *          the factor id
   * @param label
   *          the label
   * @return the factorType
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  List<FactorType> getFactorTypeByLable(String projectId, String factorId, String lable )
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Delete factorTypeExtra.
   *
   *
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void deleteFactorTypesExtra(String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;


}
