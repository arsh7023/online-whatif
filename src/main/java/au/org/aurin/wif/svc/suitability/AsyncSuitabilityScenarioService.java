package au.org.aurin.wif.svc.suitability;

import java.io.IOException;

import org.geotools.filter.text.cql2.CQLException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;

import com.vividsolutions.jts.io.ParseException;

/**
 * The Interface AsyncSuitabilityScenarioService. It handles the Async operations on
 * SuitabilityScenario, plus other important access operations
 */
public interface AsyncSuitabilityScenarioService {
	
	/**
	 * Gets the WMS Outcome Async of the Suitability Analysis. This will perform
	 * the analysis asynchronously and notify the interface of the outcome
	 * later.
	 *
	 * @param id the id
	 * @param areaAnalyzed the area analyzed
	 * @param crsArea the crs area
	 * @return the WMS
	 * @throws WifInvalidInputException the wif invalid input exception
	 * @throws WifInvalidConfigException the wif invalid config exception
	 * @throws MismatchedDimensionException the mismatched dimension exception
	 * @throws NoSuchAuthorityCodeException the no such authority code exception
	 * @throws FactoryException the factory exception
	 * @throws TransformException the transform exception
	 * @throws ParseException the parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws SuitabilityAnalysisFailedException the wif analysis failed exception
	 * @throws CQLException the cQL exception
	 */
	void getWMSOutcomeAysnc(String id, String areaAnalyzed, String crsArea)
			throws WifInvalidInputException, WifInvalidConfigException,
			MismatchedDimensionException, NoSuchAuthorityCodeException,
			FactoryException, TransformException, ParseException, IOException,
			SuitabilityAnalysisFailedException, CQLException;
}
