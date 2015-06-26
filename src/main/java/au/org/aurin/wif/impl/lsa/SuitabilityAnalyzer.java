package au.org.aurin.wif.impl.lsa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.config.ProjectConfigurator;
import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.lsa.scoring.LSAScoring;
import au.org.aurin.wif.impl.report.ConsoleReporter;
import au.org.aurin.wif.io.DataSourceFactory;
import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.GeospatialDataSource;
import au.org.aurin.wif.io.PostgisDataStoreConfig;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.WifKeys;

import com.vividsolutions.jts.io.ParseException;

/**
 * <b>SuitabilityAnalyzer.java</b> : Implementation of @see WifAnalysisService
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Component
public class SuitabilityAnalyzer {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 213426734553L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityAnalyzer.class);

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The reporter. */
  @Autowired
  private ConsoleReporter reporter;

  /** The geoserver config. */
  @Autowired
  private GeoServerConfig geoserverConfig;

  /** The geodata filterer. */
  @Autowired
  private GeodataFilterer geodataFilterer;

  /** The wif config. */
  @Autowired
  private WifConfig wifConfig;

  /** The project configurator. */
  @Autowired
  private ProjectConfigurator projectConfigurator;

  /** The wif suitabilityScenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  @Autowired
  @Qualifier(value = "wifDataStoreConfig")
  private PostgisDataStoreConfig postgisDataStoreConfig;

  @Autowired
  private DataSourceFactory dataSourceFactory;

  /**
   * to handle initialization.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * to handle destroy.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /**
   * Performs the suitability analysis based on the suitability scenario, and
   * store them in a geospatial database accessible to geoserver. The
   * suitability analysis show the spatial distribution of the various factors
   * (slopes, prime agricultural soils, etc.) that can be used to analyse the
   * suitability of different locations to accommodate future land use demands
   * in the Suitability component. the results of the land allocation analysis
   * is stored directly in the provided unified areas zone
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the wMS outcome
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
   */
  public Boolean doSuitabilityAnalysisWMS(
      final SuitabilityScenario suitabilityScenario, final String areaAnalyzed,
      final String crsArea) throws WifInvalidConfigException,
      WifInvalidInputException, NoSuchAuthorityCodeException, FactoryException,
      MismatchedDimensionException, TransformException, ParseException,
      IOException, SuitabilityAnalysisFailedException {
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("doSuitabilityAnalysisWMS  suitability analysis of == {}",
          suitabilityScenario.getLabel());
    }

    final WifProject wifProject = suitabilityScenario.getWifProject();
    final String uazDBTable = wifProject.getSuitabilityConfig()
        .getUnifiedAreaZone();
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("uazDBTable: {}", uazDBTable);
    }
    final String crsProject = wifProject.getSrs();
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("crsProject: {}", crsProject);
    }
    final String geometryColumnName = wifProject.getGeometryColumnName();
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("geometryColumnName: {}", geometryColumnName);
    }

    final Collection<AllocationLU> existingLandUses = wifProject
        .getAllocationLandUses();
    final Collection<SuitabilityLU> suitabilityLUs = wifProject
        .getSuitabilityLUs();

    DataStore wifDataStore = null;
    final Transaction transaction = new DefaultTransaction("update");
    FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
    int featureCounter = 1;
    try {
      // TODO maybe from parameters as well in a later stage
      if (wifConfig.isProductionLevel()) {
        if (wifConfig.isProductionLevel()) {
          LOGGER.debug("Attempting to open the datastore...");
          // commented by ali
          // wifDataStore = geodataFinder.openPostgisDataStore();
        }
      }

      final Map<String, Object> wifParameters = new HashMap<String, Object>();
      wifParameters.put(WifKeys.POLYGON, areaAnalyzed);
      wifParameters.put(WifKeys.CRS_ORG, crsArea);
      wifParameters.put(WifKeys.CRS_DEST, crsProject);
      wifParameters.put(WifKeys.GEOMETRY_COLUMN_KEY, geometryColumnName);
      final Filter filter = geodataFilterer
          .getFilterFromParameters(wifParameters);

      // commented by ali
      // SimpleFeatureSource featureSourceUD = wifDataStore
      // .getFeatureSource(uazDBTable);

      // ali - instead of using autowired bean; I declare a new one, since
      // autowired bean it is not updated.
      GeospatialDataSource wifDataSource;
      wifDataSource = dataSourceFactory
          .createGeospatialDataSource(postgisDataStoreConfig
              .getDataStoreParams());

      wifDataStore = wifDataSource.getDataStore();
      final SimpleFeatureSource featureSourceUD = wifDataStore
          .getFeatureSource(uazDBTable);
      writer = wifDataStore.getFeatureWriter(uazDBTable, filter, transaction);
      // end ali

      LOGGER.info("calculating the number of rows for analysis...");
      final Query query = new Query(geometryColumnName, filter);
      final int count = featureSourceUD.getCount(query);
      LOGGER.info(
          "Attempting to get the feature writer to modify {}  features...",
          count);
      // commented by ali
      // writer = wifDataStore.getFeatureWriter(uazDBTable, filter,
      // transaction);
      LOGGER
          .debug("Feature writer successfully obtained! Proceeding to performing analysis...");
      for (final SuitabilityLU suitabilityLU : suitabilityLUs) {
        if (wifConfig.isProductionLevel()) {
          LOGGER.debug("Using suitabilityLU.getFeatureFieldName(): {}",
              suitabilityLU.getFeatureFieldName());
        }
        if (wifConfig.isProductionLevel()) {
          LOGGER.debug("--> Analysing suitabilityLU {} ,scorecolumnlabel: {}",
              suitabilityLU.getLabel(), suitabilityLU.getFeatureFieldName());
        }
      }

      while (writer.hasNext()) {
        final SimpleFeature uazFeature = writer.next();
        // trying to solve caching/synchronicity issue
        if (featureCounter == 0) {
          Thread.sleep(3000);
        }
        // for lengthy analysis to know that the analysis is still going
        if (featureCounter % 3000 == 0) {
          LOGGER.info("--> Analysed {} features so far...", featureCounter);
        }
        LOGGER.trace("Analysing feature = {} ", featureCounter);
        // LOGGER.trace("Analysing Suitability of uaz id: {}",
        // uazFeature.getID());
        // test ali
        // System.out.println(uazFeature.getAttribute("score_lblnew2"));
        final Object uazLandUseValueObj = uazFeature.getAttribute(wifProject
            .getExistingLUAttributeName());
        // TODO Commented because geotools problem with logging levels weird
        // NullPointerException in a caching/synchronicity issue for
        // uazFeature.setAttribute
        // LOGGER.trace("--> Existing UAZ LandUse value= {}",
        // uazLandUseValueObj);
        // LOGGER.trace("--> Analysing {} suitabilities ",
        // suitabilityLUs.size());
        for (final SuitabilityLU suitabilityLU : suitabilityLUs) {
          if (suitabilityScenario.getLandUseConversionBySLUName(suitabilityLU
              .getLabel()) == null) {
            // LOGGER.trace("No suitability rules defined for suitabilityLU: {}",
            // suitabilityLU.getLabel());
          } else {
            final Double scoreSuitability = LSAScoring.scoreSuitability(
                uazFeature, uazLandUseValueObj, existingLandUses,
                suitabilityScenario, suitabilityLU, wifProject,
                wifConfig.isProductionLevel());
            try {
              LOGGER.trace(suitabilityLU.getFeatureFieldName());
              LOGGER.trace(scoreSuitability.toString());

              uazFeature.setAttribute(suitabilityLU.getFeatureFieldName(),
                  scoreSuitability);
            } catch (final Exception e) {
              if (featureCounter < 10) {
                LOGGER
                    .warn("caching/synchronicity issue, trying to recover...");
                Thread.sleep(3000);
                continue;
              } else {
                throw e;
              }
            }
          }
        }
        // LOGGER.trace("--> Finished analysis of {} writing feature ",
        // uazFeature.getID());
        writer.write();
        featureCounter++;

      }

      LOGGER
          .info(
              "attempting to modify the last features of {} features in : {}... it might take a while",
              featureCounter, uazDBTable);
      final Long timeelapsed = System.currentTimeMillis() / 1000 % 60;

      transaction.commit();
      LOGGER.info("Transaction  took {} seconds to perform, in : {}",
          timeelapsed, uazDBTable);
    } catch (final IOException e) {
      LOGGER
          .error("IOException in updating acccess failed: {}", e.getMessage());
      LOGGER.error("WMSOutcome could not be created");
      throw new SuitabilityAnalysisFailedException(
          "WMSOutcome could not be created ", e);

    } catch (final WifInvalidConfigException e) {
      LOGGER.error("WifInvalidConfigException configuration failed: {}",
          e.getMessage());
      LOGGER.error("WMSOutcome could not be created");
      throw e;

    } catch (final CQLException e) {
      LOGGER.error("CQLException filter failed: {}", e.getMessage());
      LOGGER.error("WMSOutcome could not be created");
      throw new SuitabilityAnalysisFailedException(
          "WMSOutcome could not be created ", e);
    } catch (final Exception e) {
      LOGGER.error("Exception: {}", e.getMessage());
      LOGGER.error("WMSOutcome could not be created");
      throw new SuitabilityAnalysisFailedException(
          "WMSOutcome could not be created ", e);
    } finally {
      LOGGER.info("modified {} features,closing opened geofeatures in : {}",
          featureCounter, uazDBTable);
      if (writer != null) {
        writer.close(); // IMPORTANT
      }
      transaction.close();
      // ali
      // wifDataStore.dispose();
      // ali
    }
    LOGGER.info("analysis process  finished successfully for {}", uazDBTable);
    suitabilityScenarioDao.updateSuitabilityScenarioStatus(suitabilityScenario,
        Boolean.TRUE);
    return Boolean.TRUE;
  }

  /**
   * Performs the suitability analysis based on the SuitabilityScenario. The
   * suitability analysis show the spatial distribution of the various factors
   * (slopes, prime agricultural soils, etc.) that can be used to analyse the
   * suitability of different locations to accommodate future land use demands
   * in the Suitability component
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @param areaAnalyzed
   *          the area analyzed
   * @param crsArea
   *          the crs area
   * @return the simple feature collection with the modified analysis
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
   * @throws CQLException
   *           the cQL exception
   * @throws DatabaseFailedException
   */

  public SimpleFeatureCollection doSuitabilityAnalysis(
      final SuitabilityScenario suitabilityScenario, final String areaAnalyzed,
      final String crsArea) throws WifInvalidConfigException,
      WifInvalidInputException, NoSuchAuthorityCodeException, FactoryException,
      MismatchedDimensionException, TransformException, ParseException,
      CQLException, DatabaseFailedException {

    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("processing suitability analysis  for ={}",
          suitabilityScenario.getLabel());
    }

    final WifProject wifProject = suitabilityScenario.getWifProject();
    final String uazDBTable = wifProject.getSuitabilityConfig()
        .getUnifiedAreaZone();
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("uazDBTable: {}", uazDBTable);
    }
    final String crsProject = wifProject.getSrs();

    final List<String> suitabilityColumns = new ArrayList<String>(wifProject
        .getSuitabilityConfig().getSuitabilityColumns());

    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("crsProject: {}", crsProject);
    }
    final Collection<AllocationLU> existingLandUses = wifProject
        .getAllocationLandUses();
    final Collection<SuitabilityLU> suitabilityLUs = wifProject
        .getSuitabilityLUs();
    LOGGER.info("Suitability Scenario Score UAZ column name: {}",
        suitabilityScenario.getLabel());
    // SimpleFeatureCollection lsaCollection =
    // FeatureCollections.newCollection();
    final DefaultFeatureCollection lsaCollection = new DefaultFeatureCollection();

    final Map<String, Object> wifParameters = new HashMap<String, Object>();
    wifParameters.put(WifKeys.POLYGON, areaAnalyzed);
    wifParameters.put(WifKeys.CRS_ORG, crsArea);
    wifParameters.put(WifKeys.CRS_DEST, crsProject);
    final Filter filter = geodataFilterer
        .getFilterFromParameters(wifParameters);

    final SimpleFeatureCollection uazCollection = geodataFinder
        .getFeatureCollectionfromDB(uazDBTable, filter, suitabilityColumns);
    final SimpleFeatureIterator it = uazCollection.features();

    try {

      final SimpleFeatureType uazFeatureType = uazCollection.getSchema();
      final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
      sftb.init(uazFeatureType);
      sftb.setName(suitabilityScenario.getLabel());
      final SimpleFeatureType lsaFeatureType = sftb.buildFeatureType();
      while (it.hasNext()) {
        final SimpleFeature uazFeature = it.next();

        LOGGER.info("Analysing Suitability of id: {}", uazFeature.getID());
        final Object uazLandUseValueObj = uazFeature.getAttribute(wifProject
            .getExistingLUAttributeName());
        if (wifConfig.isProductionLevel()) {
          LOGGER
              .debug("--> Existing UAZ LandUse value= {}", uazLandUseValueObj);
          // TODO Maybe later will be necessary for performance
          // SimpleFeatureBuilder sfb = new
          // SimpleFeatureBuilder(lsaFeatureType);
          // sfb.addAll(uazFeature.getAttributes());
          // SimpleFeature lsaFeature = sfb.buildFeature(null);
        }

        for (final SuitabilityLU suitabilityLU : suitabilityLUs) {
          final Double scoreSuitability = LSAScoring.scoreSuitability(
              uazFeature, uazLandUseValueObj, existingLandUses,
              suitabilityScenario, suitabilityLU, wifProject,
              wifConfig.isProductionLevel());
          uazFeature.setAttribute(suitabilityLU.getFeatureFieldName(),
              scoreSuitability);
        }
        lsaCollection.add(uazFeature);
      }
      if (wifConfig.isProductionLevel()) {
        LOGGER.debug("total number of modified features: {}",
            lsaCollection.size());
      }

    } catch (final WifInvalidConfigException e) {
      LOGGER.error("doSuitabilityAnalysis configuration failed: {}",
          e.getMessage());
      throw e;
    }

    finally {
      it.close(); // IMPORTANT
    }
    return lsaCollection;
  }

  /**
   * Generate score ranges.
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   * @return the map
   */
  public Map<String, Integer> generateScoreRanges(
      final SuitabilityScenario suitabilityScenario) {
    if (wifConfig.isProductionLevel()) {
      LOGGER.debug("generateScoreRanges for: {}",
          suitabilityScenario.getLabel());
    }
    final Map<String, Integer> scoreColumns = new HashMap<String, Integer>();
    final Set<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    for (final SuitabilityRule suitabilityRule : suitabilityRules) {

      final SuitabilityLU suitabilityLU = suitabilityRule.getSuitabilityLU();
      final Set<FactorImportance> factorImportances = suitabilityRule
          .getFactorImportances();
      Double totalRange = 0.0;
      for (final FactorImportance factorImportance : factorImportances) {
        if (factorImportance.getImportance() != 0.0) {
          if (wifConfig.isProductionLevel()) {
            LOGGER.debug("range  for {} is {}", factorImportance.getFactor()
                .getLabel(), factorImportance.getImportance() * 100);
          }

          totalRange += factorImportance.getImportance() * 100;
        }
      }

      if (wifConfig.isProductionLevel()) {
        LOGGER.debug("totalRange for {} is {}", suitabilityLU.getLabel(),
            totalRange);
      }
      scoreColumns.put(suitabilityLU.getFeatureFieldName(),
          totalRange.intValue());
    }
    return scoreColumns;
  }
}
