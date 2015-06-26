package au.org.aurin.wif.svc.suitability;

import java.util.List;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.FactorImportance;


/**
 * The Interface FactorImportanceService CRUD.It handles the CRUD  operations on  FactorImportance entity,  
 *  plus other important  access operations
 */
public interface FactorImportanceService {

	/**
	 * Adds the factorImportance.
	 *
	 * @param factorImportance the factorImportance
	 * @return the wif factorImportance
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	FactorImportance createFactorImportance(FactorImportance factorImportance)  	throws WifInvalidInputException;

	/**
	 * Gets the  factorImportance.
	 *
	 * @param id the id
	 * @return the factorImportance
	 * @throws WifInvalidInputException the wif invalid input exception
	 * @throws WifInvalidConfigException the wif invalid config exception
	 */
	FactorImportance getFactorImportance(String id) throws WifInvalidInputException, WifInvalidConfigException;
	
	/**
	 * Update factorImportance.
	 *
	 * @param factorImportance the factorImportance
	 * @throws WifInvalidInputException the wif invalid input exception
	 * @throws WifInvalidConfigException the wif invalid config exception
	 */
	void updateFactorImportance(FactorImportance factorImportance) throws WifInvalidInputException, WifInvalidConfigException;	
	/**
	 * Delete factorImportance.
	 *
	 * @param id the id
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	void deleteFactorImportance(String id) 	throws WifInvalidInputException;;


	/**
	 * Gets the factorImportances.
	 *
	 * @param scenarioId the scenario id
	 * @return the factorImportances
	 * @throws WifInvalidInputException the wif invalid input exception
	 */
	List<FactorImportance> getFactorImportances(String scenarioId) throws WifInvalidInputException;

}
