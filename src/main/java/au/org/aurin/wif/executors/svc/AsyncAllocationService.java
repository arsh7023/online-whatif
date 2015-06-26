package au.org.aurin.wif.executors.svc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.geotools.filter.text.cql2.CQLException;
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

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.AllocationAnalyzer;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;
import au.org.aurin.wif.svc.demand.DemandOutcomeService;
import au.org.aurin.wif.svc.demand.DemandScenarioService;
import au.org.aurin.wif.svc.suitability.DemandConfigService;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.vividsolutions.jts.io.ParseException;

/**
 * Asynchronous service to perform lengthy allocation analysis.
 */
@Service
@Qualifier("asyncAllocationService")
public class AsyncAllocationService {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncAllocationService.class);

  /** The allocation analyzer. */
  @Autowired
  private AllocationAnalyzer allocationAnalyzer;

  /** The wif allocationScenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The demand config service. */
  @Resource
  private DemandConfigService demandConfigService;

  /** The demand config service. */
  @Resource
  private DemandScenarioService demandScenarioService;

  /** The manual demand config service. */
  @Resource
  private DemandOutcomeService manualdemandScenarioService;

  /** The demand config service. */
  @Resource
  private SuitabilityScenarioService suitabilityScenarioService;

  /**
   * Do allocation analysis async.
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
   * @throws CQLException
   * @throws IncompleteDemandScenarioException
   * @throws SQLException
   * @throws ClassNotFoundException
   */
  @Async
  // public Future<Boolean> doAllocationAnalysisAsync(final String id)
  public Future<String> doAllocationAnalysisAsync(final String id)
      throws WifInvalidConfigException, WifInvalidInputException,
      NoSuchAuthorityCodeException, FactoryException,
      MismatchedDimensionException, TransformException, ParseException,
      IOException, SuitabilityAnalysisFailedException, ParsingException,
      CQLException, IncompleteDemandScenarioException, ClassNotFoundException,
      SQLException {

    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(id);

    LOGGER.debug(
        "doAllocationAnalysisAsync processing allocation analysis  for ={}",
        allocationScenario.getLabel());

    allocationScenario.setReady(false);
    allocationScenarioDao.updateAllocationScenario(allocationScenario);

    final String suitabilityScenarioId = allocationScenario
        .getSuitabilityScenarioId();
    final SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(suitabilityScenarioId);

    allocationScenario.setSuitabilityScenario(suitabilityScenario);

    if (allocationScenario.isManual()) {
      final String manualdemandScenarioId = allocationScenario
          .getManualdemandScenarioId();

      final String projectId = suitabilityScenario.getWifProject().getId();
      final List<DemandScenario> listDemand = demandScenarioService
          .getDemandScenarios(projectId);
      Boolean lsw = false;
      for (final DemandScenario dsn : listDemand) {
        if (dsn.getId().equals(manualdemandScenarioId)) {
          lsw = true;
        }
      }
      if (lsw == false) {
        final DemandOutcome manualdemandScenario = manualdemandScenarioService
            .getDemandOutcome(manualdemandScenarioId, suitabilityScenario
                .getWifProject().getId());
        allocationScenario.setManualdemandScenario(manualdemandScenario);
      }

      // allocationScenario.setWifProject(manualdemandScenario.getWifProject());
      allocationScenario.setWifProject(suitabilityScenario.getWifProject());
    } else {
      final String demandScenarioId = allocationScenario.getDemandScenarioId();
      final DemandScenario demandScenario = demandScenarioService
          .getDemandScenario(demandScenarioId);
      allocationScenario.setDemandScenario(demandScenario);
      allocationScenario.setWifProject(demandScenario.getWifProject());
    }

    // return new AsyncResult<Boolean>(
    // allocationAnalyzer.doAllocationAnalysis(allocationScenario));

    return new AsyncResult<String>(
        allocationAnalyzer.doAllocationAnalysis(allocationScenario));
  }
}
