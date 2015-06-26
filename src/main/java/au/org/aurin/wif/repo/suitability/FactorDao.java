package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.suitability.Factor;




/**
 * The Interface FactorDao.
 */
public interface FactorDao {

  /**
   * Adds the factor.
   * 
   * @param factor
   *          the factor
   */
  void addFactor(Factor factor);
  
  /**
   * Persist factor.
   *
   * @param factor the factor
   * @return the factor
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  Factor persistFactor(Factor factor) throws WifInvalidConfigException;

  /**
   * Update factor.
   *
   * @param factor the factor
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateFactor(Factor factor) throws WifInvalidConfigException;
  
  /**
   * Find factor by id.
   *
   * @param id the id
   * @return the wif factor
   */
  Factor findFactorById(String id);
  
	/**
	 * Delete factor.
	 *
	 * @param factor the factor
	 */
	void deleteFactor(Factor factor);	  
	  /**
  	 * Gets the all factors belonging to a project.
  	 *
  	 * @param projectId the project id
  	 * @return the all factors
  	 */
	  List<Factor> getFactors(String projectId);
}

