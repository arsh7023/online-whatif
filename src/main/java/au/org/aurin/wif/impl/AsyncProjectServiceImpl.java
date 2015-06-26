package au.org.aurin.wif.impl;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.exception.io.ShapeFile2PostGISCreationException;
import au.org.aurin.wif.exception.io.WifIOException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.DataStoreToPostgisExporter;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.PostgisToDataStoreExporter;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.AsyncProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AsyncProjectServiceImpl.
 */
@Service
@Qualifier("asyncProjectService")
public class AsyncProjectServiceImpl implements AsyncProjectService {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 213432423433L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AsyncProjectServiceImpl.class);

  /** The geoserver publisher. */
  @Autowired
  private GeoServerRESTPublisher geoserverPublisher;

  /** The postgis to data store exporter. */
  @Autowired
  private PostgisToDataStoreExporter postgisToDataStoreExporter;
  /** The geoserver config. */
  @Autowired
  private GeoServerConfig geoserverConfig;

  /** The wif config. */
  @Resource
  private WifConfig wifConfig;

  /** The validator crs. */
  @Resource
  private ValidatorCRS validatorCRS;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The exporter. */
  @Autowired
  private DataStoreToPostgisExporter exporter;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The validator crs. */
  @Resource
  private ValidatorUAZ validatorUAZ;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AsyncProjectService#setupProjectAsync(au.org.aurin
   * .wif.model.WifProject, java.lang.String)
   */
  @Async
  public Future<String> setupProjectAsync(final WifProject project,
      final String username) throws WifInvalidInputException,
      DataStoreUnavailableException, DataStoreCreationException {
    return new AsyncResult<String>(setupProject(project, username));
  }

  /**
   * Setup project.
   * 
   * @param project
   *          the project
   * @param username
   *          the username
   * @return the string
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws DataStoreCreationException
   *           the data store creation exception
   */
  public String setupProject(WifProject project, final String username)
      throws WifInvalidInputException, DataStoreUnavailableException,
      DataStoreCreationException {

    String tableName = "";
    CoordinateReferenceSystem crs = null;
    String geometryColumnName = WifKeys.DEFAULT_COLUMN_NAME;
    final String bbox = "";
    final String msg = "Creating postGIS spatial store with table name"
        + tableName + " failed";
    tableName = "wif_"
        + project.getId().toString().replaceAll(" ", "_").replaceAll("-", "_")
            .toLowerCase();
    LOGGER
        .info("Creating postGIS spatial store with table name {} ", tableName);
    if (project.getOwnGeoDatastoreName() != null) {
      project = this.reuseDBTable(tableName, project);
    } else {
      SimpleFeatureType featureType = null;
      try {
        project = this.createDatastore(project, username, tableName);
      } catch (final Exception e) {
        LOGGER.error(msg);
        throw new DataStoreCreationException(msg, e);
      }
      featureType = project.getSchema();
      crs = featureType.getCoordinateReferenceSystem();
      crs = validatorCRS.validateSimple(crs, msg);

      geometryColumnName = featureType.getGeometryDescriptor().getLocalName();
      LOGGER.info("Geometry column name= {} ", geometryColumnName);
      LOGGER.info("bounding box is= {} ", project.getBbox());
    }
    LOGGER.info("Creating suitabilityConfig with uaz name {} ", tableName);
    final SuitabilityConfig suitabilityConfig = this
        .createSuitabilityConfig(tableName);
    suitabilityConfig.setWifProject(project);
    project.setSuitabilityConfig(suitabilityConfig);
    project.setGeometryColumnName(geometryColumnName);
    project.setSrs(CRS.toSRS(crs));
    project.setReady(true);
    project.setLocalShpFile(null);

    /*
     * LOGGER .info("New Validate UAZ Started"); try {
     * validatorUAZ.validateSimple(project, msg); } catch (IOException e) {
     * LOGGER.error(msg); throw new DataStoreCreationException(msg, e); }
     */

    wifProjectDao.updateProject(project);

    // ali
    // creating metadata in aurin
    // TODO enable in the next iteration oof what if
    // try {
    // postgisToDataStoreExporter.persistInAurin(project, roleId);
    // } catch (MiddlewarePersistentException e) {
    // LOGGER.warn("sharing with aurin is not enabled!");
    // }
    // ali- also call postgisToDataStoreExporter.exportUAZ

    LOGGER.info(
        "setup process for project {},with id= {} has completed successfully!",
        project.getName(), project.getId());
    return project.getId().toString();
  }

  /**
   * Reuse db table.
   * 
   * @param tableName
   *          the table name
   * @param project
   *          the project
   * @return the wif project
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private WifProject reuseDBTable(final String tableName,
      final WifProject project) throws DataStoreCreationException {
    LOGGER
        .info(
            "Creating feature collection from table already present in the database! tablename is={}",
            tableName);

    FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
    featureCollection = exporter.getFeatures(project.getOwnGeoDatastoreName());

    final ReferencedEnvelope bbox = featureCollection.getBounds();
    final String bboxJson = new StringBuffer().append("[")
        .append(bbox.getMinX()).append(",").append(bbox.getMinY()).append(",")
        .append(bbox.getMaxX()).append(",").append(bbox.getMaxY()).append("]")
        .toString();
    project.setBbox(bboxJson);
    project.setSchema(featureCollection.getSchema());
    try {
      exporter.storeFeatureCollectionToPostgis(featureCollection, tableName);
    } catch (final IOException e) {
      final String msg = "creating postGIS spatial features from table name "
          + tableName + " failed";
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    }

    return project;

  }

  /**
   * Creates the datastore.
   * 
   * @param project
   *          the project
   * @param username
   *          the username
   * @param tableName
   *          the table name
   * @return the simple feature type
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws ShapeFile2PostGISCreationException
   *           the shape file2 post gis creation exception
   */
  public WifProject createDatastore(WifProject project, final String username,
      final String tableName) throws WifInvalidInputException,
      DataStoreUnavailableException, DataStoreCreationException,
      ShapeFile2PostGISCreationException {
    // handle the datastore to postgis export process here...

    try {
      LOGGER.info("exporting to postGIS, with tableName={}", tableName);
      final long startTime = System.nanoTime();
      project = exporter.exportToPostgis(username, project, tableName);

      final long endTime = System.nanoTime();

      final long duration = endTime - startTime;
      LOGGER.info(">>>>>>>>>>>>>>*************** Export to Postgis took in ms "
          + duration / 1000000);
      return project;
    } catch (final IOException e) {
      LOGGER.error("Error occurred while exporting to Postgis", e);
      throw new WifInvalidInputException(
          "Error occurred while exporting to Postgis", e);
    }
  }

  /**
   * Creates the suitability config.
   * 
   * @param tableName
   *          the table name
   * @return the suitability config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   */
  public SuitabilityConfig createSuitabilityConfig(final String tableName)
      throws WifInvalidInputException, DataStoreUnavailableException {
    final SuitabilityConfig suitabilityConfig = new SuitabilityConfig();
    suitabilityConfig.setUnifiedAreaZone(tableName);
    LOGGER.debug("using UAZ table={}", suitabilityConfig.getUnifiedAreaZone());
    suitabilityConfig.setScoreColumns(new HashSet<String>());
    suitabilityConfig.setSuitabilityColumns(new HashSet<String>());
    return suitabilityConfig;

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.AsyncProjectService#uploadUAZAsync(au.org.aurin.wif
   * .model.WifProject, java.lang.String)
   */
  @Async
  public Future<String> uploadUAZAsync(final WifProject project,
      final String roleId) throws WifIOException, IOException,
      DataStoreCreationException, MiddlewarePersistentException {

    return new AsyncResult<String>(uploadUAZ(project, roleId));
  }

  /**
   * Upload uaz of the project into AURIN middleware persistence services.
   * 
   * @param project
   *          the project
   * @param roleId
   *          the role id
   * @return the string
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws MiddlewarePersistentException
   *           the middleware persistent exception
   */
  private String uploadUAZ(final WifProject project, final String roleId)
      throws IOException, DataStoreCreationException,
      MiddlewarePersistentException {

    final String dataStoreUri = postgisToDataStoreExporter.exportUAZ(project,
        roleId);

    return dataStoreUri;
  }
}
