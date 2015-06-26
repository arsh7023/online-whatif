package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.model.suitability.SuitabilityLU;

/**
 * The Interface SuitabilityLU.
 */
public interface SuitabilityLUDao {

  /**
   * Adds the suitabilityLU.
   * 
   * @param suitabilityLU
   *          the suitabilityLU
   */
  void addSuitabilityLU(SuitabilityLU suitabilityLU);
  
  /**
   * Persist allocation lu.
   *
   * @param suitabilityLU the allocation lu
   * @return the allocation lu
   */
  SuitabilityLU persistSuitabilityLU(SuitabilityLU suitabilityLU);

  /**
   * Update suitabilityLU.
   *
   * @param suitabilityLU the suitabilityLU
   */
  void updateSuitabilityLU(SuitabilityLU suitabilityLU);
  
  /**
   * Find suitabilityLU by id.
   *
   * @param id the id
   * @return the wif suitabilityLU
   */
  SuitabilityLU findSuitabilityLUById(String id);
  
	/**
	 * Delete suitabilityLU.
	 *
	 * @param suitabilityLU the suitabilityLU
	 */
	void deleteSuitabilityLU(SuitabilityLU suitabilityLU);
	  
	  /**
  	 * Gets the all suitabilityLUs belonging to a project.
  	 *
  	 * @param projectId the project id
  	 * @return the all suitabilityLUs
  	 */
	  List<SuitabilityLU> getSuitabilityLUs(String projectId);
}

