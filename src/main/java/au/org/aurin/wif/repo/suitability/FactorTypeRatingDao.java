package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.suitability.FactorTypeRating;




/**
 * The Interface FactorTypeRatingDao.
 */
public interface FactorTypeRatingDao {

  /**
   * Adds the factorTypeRating.
   * 
   * @param factorTypeRating
   *          the factorTypeRating
   */
  void addFactorTypeRating(FactorTypeRating factorTypeRating);
  
  /**
   * Persist factorTypeRating.
   *
   * @param factorTypeRating the factorTypeRating
   * @return the factorTypeRating
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  FactorTypeRating persistFactorTypeRating(FactorTypeRating factorTypeRating) throws WifInvalidConfigException;

  /**
   * Update factorTypeRating.
   *
   * @param factorTypeRating the factorTypeRating
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  void updateFactorTypeRating(FactorTypeRating factorTypeRating) throws WifInvalidConfigException;

  
  /**
   * Find factorTypeRating by id.
   *
   * @param id the id
   * @return the wif factorTypeRating
   */
  FactorTypeRating findFactorTypeRatingById(Integer id);
  
  /**
   * Find factorTypeRating by id.
   *
   * @param id the id
   * @return the wif factorTypeRating
   */
  FactorTypeRating findFactorTypeRatingById(String id);
  
	/**
	 * Delete factorTypeRating.
	 *
	 * @param factorTypeRating the factorTypeRating
	 */
	void deleteFactorTypeRating(FactorTypeRating factorTypeRating);	  
	  
  	/**
  	 * Gets the all factorTypeRatings belonging to a factor.
  	 *
  	 * @param factorImportanceId the factor importance id
  	 * @return the all factorTypeRatings
  	 */
	  List<FactorTypeRating> getFactorTypeRatings(String factorImportanceId);
}

