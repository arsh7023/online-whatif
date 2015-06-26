package au.org.aurin.wif.executors.svc;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.lsa.SuitabilityAnalyzer;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.vividsolutions.jts.io.ParseException;

/**
 * Asynchronous service to perform lengthy suitability analysis.
 */
@Service
@Qualifier("asyncSuitabilityService")
public class AsyncSuitabilityService {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncSuitabilityService.class);

  /** The suitability analyzer. */
  @Autowired
  private SuitabilityAnalyzer suitabilityAnalyzer;

  /** The wif suitabilityScenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The suitability scenario service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /**
   * Do suitability analysis wms async.
   * 
   * @param id
   *          the id
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the future
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SuitabilityAnalysisFailedException
   *           the wif analysis failed exception
   * @throws ParsingException
   *           the parsing exception
   */
  @Async
  public Future<Boolean> doSuitabilityAnalysisWMSAsync(String id,
      String areaAnalyzed, String crsArea) throws WifInvalidConfigException,
      WifInvalidInputException, NoSuchAuthorityCodeException, FactoryException,
      MismatchedDimensionException, TransformException, ParseException,
      IOException, SuitabilityAnalysisFailedException, ParsingException {

    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(id);
    suitabilityScenarioDao.updateSuitabilityScenarioStatus(suitabilityScenario,
        Boolean.FALSE);
    if (suitabilityScenario == null) {
      LOGGER.error("illegal argument, the suitabilityScenario with the ID "
          + id + " supplied was not found ");
      throw new InvalidEntityIdException(
          "illegal argument, the suitabilityScenario with the ID " + id
              + " supplied was not found ");
    }
    LOGGER
        .debug(
            "doSuitabilityAnalysisWMSAsync processing suitability analysis  for ={}",
            suitabilityScenario.getLabel());
    return new AsyncResult<Boolean>(
        suitabilityAnalyzer.doSuitabilityAnalysisWMS(suitabilityScenario,
            areaAnalyzed, crsArea));
  }
}
