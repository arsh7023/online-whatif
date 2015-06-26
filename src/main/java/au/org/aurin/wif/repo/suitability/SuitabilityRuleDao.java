package au.org.aurin.wif.repo.suitability;

import java.util.List;

import au.org.aurin.wif.model.suitability.SuitabilityRule;

/**
 * The Interface SuitabilityRuleDao.
 */
public interface SuitabilityRuleDao {

  /**
   * Adds the suitability rule.
   *
   * @param suitabilityRule the suitability rule
   */
  void addSuitabilityRule(SuitabilityRule suitabilityRule);
  
  /**
   * Persist suitability rule.
   *
   * @param suitabilityRule the suitability rule
   * @return the suitability rule
   */
  SuitabilityRule persistSuitabilityRule(SuitabilityRule suitabilityRule);

  /**
   * Update suitability rule.
   *
   * @param suitabilityRule the suitability rule
   */
  void updateSuitabilityRule(SuitabilityRule suitabilityRule);
  
  /**
   * Find suitability rule by id.
   *
   * @param id the id
   * @return the suitability rule
   */
  SuitabilityRule findSuitabilityRuleById(String id);
  
	/**
	 * Delete suitability rule.
	 *
	 * @param suitabilityRule the suitability rule
	 */
	void deleteSuitabilityRule(SuitabilityRule suitabilityRule);
	  
	  /**
  	 * Gets the suitability rules.
  	 *
  	 * @param scenarioId the scenario id
  	 * @return the suitability rules
  	 */
	  List<SuitabilityRule> getSuitabilityRules(String scenarioId);
}

