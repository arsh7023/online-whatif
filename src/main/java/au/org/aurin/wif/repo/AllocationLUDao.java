package au.org.aurin.wif.repo;

import java.util.List;

import au.org.aurin.wif.model.allocation.AllocationLU;

/**
 * The Interface AllocationLU.
 */
public interface AllocationLUDao {

  /**
   * Adds the allocationLU.
   * 
   * @param allocationLU
   *          the allocationLU
   */
  void addAllocationLU(AllocationLU allocationLU);
  
  /**
   * Persist allocation lu.
   *
   * @param allocationLU the allocation lu
   * @return the allocation lu
   */
  AllocationLU persistAllocationLU(AllocationLU allocationLU);

  /**
   * Update allocationLU.
   *
   * @param allocationLU the allocationLU
   */
  void updateAllocationLU(AllocationLU allocationLU);
  
  /**
   * Find allocationLU by id.
   *
   * @param id the id
   * @return the wif allocationLU
   */
  AllocationLU findAllocationLUById(String id);
  
	/**
	 * Delete allocationLU.
	 *
	 * @param allocationLU the allocationLU
	 */
	void deleteAllocationLU(AllocationLU allocationLU);
	  
	  /**
  	 * Gets the all allocationLUs belonging to a project.
  	 *
  	 * @param projectId the project id
  	 * @return the all allocationLUs
  	 */
	  List<AllocationLU> getAllocationLUs(String projectId);
}

