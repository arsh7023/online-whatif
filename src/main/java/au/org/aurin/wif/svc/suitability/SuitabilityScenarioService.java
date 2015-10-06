package au.org.aurin.wif.svc.suitability;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.io.ParseException;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.suitability.WMSOutcome;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * The Interface SuitabilityScenarioService.
 */
public interface SuitabilityScenarioService {

  /**
   * Creates the suitability scenario.
   *
   * @param suitabilityScenario
   *          the suitability scenario
   * @param projectId
   *          the project id
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  SuitabilityScenario createSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, String projectId)
          throws WifInvalidInputException, WifInvalidConfigException,
          ParsingException;

  /**
   * Gets the suitability scenario.
   *
   * @param id
   *          the id
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  SuitabilityScenario getSuitabilityScenario(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Gets the suitability scenario no mapping.
   *
   * @param id
   *          the id
   * @return the suitability scenario no mapping
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  SuitabilityScenario getSuitabilityScenarioNoMapping(String id)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Gets the suitability scenario.
   *
   * @param id
   *          the id
   * @param projectId
   *          the project id
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  SuitabilityScenario getSuitabilityScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException;

  /**
   * Update suitability scenario.
   *
   * @param suitabilityScenario
   *          the suitability scenario
   * @param projectId
   *          the project id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  void updateSuitabilityScenario(SuitabilityScenario suitabilityScenario,
      String projectId) throws WifInvalidInputException,
  WifInvalidConfigException;

  /**
   * Delete suitability scenario.
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
  void deleteSuitabilityScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException;

  /**
   * Gets the suitability scenarios.
   *
   * @param projectId
   *          the project id
   * @return the suitability scenarios
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  List<SuitabilityScenario> getSuitabilityScenarios(String projectId)
      throws WifInvalidInputException;

  /**
   * Gets the wMS outcome.
   *
   * @param id
   *          the id
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the wMS outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SuitabilityAnalysisFailedException
   *           the wif analysis failed exception
   * @throws CQLException
   *           the cQL exception
   * @throws ParsingException
   *           the parsing exception
   */
  Boolean getWMSOutcome(String id, String areaAnalyzed, String crsArea)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      SuitabilityAnalysisFailedException, CQLException, ParsingException;

  /**
   * Gets the outcome.
   *
   * @param id
   *          the id
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the outcome
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws CQLException
   *           the cQL exception
   * @throws FactoryException
   *           the factory exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws ParsingException
   *           the parsing exception
   * @throws DatabaseFailedException
   */
  SimpleFeatureCollection getOutcome(String id, String areaAnalyzed,
      String crsArea) throws WifInvalidInputException,
  WifInvalidConfigException, MismatchedDimensionException,
  NoSuchAuthorityCodeException, CQLException, FactoryException,
  TransformException, ParseException, ParsingException,
  DatabaseFailedException;

  /**
   * Gets the wms.
   *
   * @param id
   *          the id
   * @return the wms
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws ParsingException
   *           the parsing exception
   */
  WMSOutcome getWMS(String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException;

  /**
   * Restore suitability scenario from a configuration in json format
   *
   * @param suitabilityScenario
   *          the suitability scenario json
   * @param restoreProject
   * @return the suitability scenario
   * @throws WifInvalidInputException
   */
  SuitabilityScenario restoreSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, WifProject restoreProject)
          throws WifInvalidInputException;

  /**
   * duplicate suitability scenario
   *
   * @param projectID
   * @param scenarioID
   * @return String
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   */
  public String duplicateSuitabiliyScenario(final String projectID, final String scenarioID, final String name)
      throws WifInvalidInputException, WifInvalidConfigException, ParsingException;

  /**
   * duplicate uploadXlsFactors
   *
   * @param projectID
   * @param scenarioID
   * @param inputStream
   * @return String
   * @throws WifInvalidInputException
   * @throws WifInvalidConfigException
   * @throws ParsingException
   * @throws IOException
   */
  public String uploadXlsFactors(final String projectID, final String scenarioID, final InputStream inputStream)
      throws WifInvalidInputException, WifInvalidConfigException, ParsingException, IOException;

}
