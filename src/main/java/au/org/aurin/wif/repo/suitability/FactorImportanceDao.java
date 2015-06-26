package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.model.suitability.FactorImportance;




/**
 * The Interface FactorImportanceDao.
 */
public interface FactorImportanceDao {

  /**
   * Adds the factorImportance.
   * 
   * @param factorImportance
   *          the factorImportance
   */
  void addFactorImportance(FactorImportance factorImportance);
  
  /**
   * Persist factorImportance.
   *
   * @param factorImportance the factorImportance
   * @return the factorImportance
   */
  FactorImportance persistFactorImportance(FactorImportance factorImportance);

  /**
   * Update factorImportance.
   *
   * @param factorImportance the factorImportance
   */
  void updateFactorImportance(FactorImportance factorImportance);

  
  /**
   * Find factorImportance by id.
   *
   * @param id the id
   * @return the wif factorImportance
   */
  FactorImportance findFactorImportanceById(Integer id);
  
  /**
   * Find factorImportance by id.
   *
   * @param id the id
   * @return the wif factorImportance
   */
  FactorImportance findFactorImportanceById(String id);
  
	/**
	 * Delete factorImportance.
	 *
	 * @param factorImportance the factorImportance
	 */
	void deleteFactorImportance(FactorImportance factorImportance);	  
	  /**
  	 * Gets the all factorImportances belonging to a project.
  	 *
  	 * @param projectId the project id
  	 * @return the all factorImportances
  	 */
	  List<FactorImportance> getFactorImportances(String projectId);
}

