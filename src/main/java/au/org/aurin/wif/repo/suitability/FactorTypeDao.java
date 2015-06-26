package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.suitability.FactorType;




/**
 * The Interface FactorTypeDao.
 */
public interface FactorTypeDao {

  /**
   * Adds the factorType.
   * 
   * @param factorType
   *          the factorType
   */
  void addFactorType(FactorType factorType);
  
  /**
   * Persist factorType.
   *
   * @param factorType the factorType
   * @return the factorType
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  FactorType persistFactorType(FactorType factorType) throws WifInvalidConfigException;

  /**
   * Update factorType.
   *
   * @param factorType the factorType
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateFactorType(FactorType factorType) throws WifInvalidConfigException;
  
  /**
   * Find factorType by id.
   *
   * @param id the id
   * @return the wif factorType
   */
  FactorType findFactorTypeById(String id);
  
	/**
	 * Delete factorType.
	 *
	 * @param factorType the factorType
	 */
	void deleteFactorType(FactorType factorType);	  
	  /**
  	 * Gets the all factorTypes belonging to a factor.
  	 *
  	 * @param factorId the factor id
  	 * @return the all factorTypes
  	 */
	  List<FactorType> getFactorTypes(String factorId);
}

