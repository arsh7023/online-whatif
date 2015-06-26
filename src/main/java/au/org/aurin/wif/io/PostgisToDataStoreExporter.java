package au.org.aurin.wif.io;

import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.restclient.AurinServiceClient;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DataStoreToPostgisExporter.
 */
@Component
public class PostgisToDataStoreExporter {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(PostgisToDataStoreExporter.class);

  /** The data store client. */
  @Autowired
  private DataStoreClient dataStoreClient;

  /** The file to postgis exporter. */
  @Autowired
  private FileToPostgisExporter fileToPostgisExporter;

  /** The wif config. */
  @Resource
  private WifConfig wifConfig;

  /** The postgis data store config. */
  @Autowired
  @Qualifier("wifDataStoreConfig")
  private PostgisDataStoreConfig postgisDataStoreConfig;
  /** The project service. */
  @Resource
  private AurinServiceClient aurinServiceClient;

  /**
   * Inits the sservice.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    LOGGER.info("using the following server: {} for database/schema: {}",
        postgisDataStoreConfig.getDataStoreParams().get(HOST.key),
        postgisDataStoreConfig.getDataStoreParams().get(DATABASE.key) + "/"
            + postgisDataStoreConfig.getDataStoreParams().get(SCHEMA.key));
  }

  /**
   * Export uaz.
   * 
   * @param project
   *          the project
   * @param userId
   *          the user id
   * @return the string
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws MiddlewarePersistentException
   */
  public String exportUAZ(final WifProject project, final String userId)
      throws IOException, DataStoreCreationException,
      MiddlewarePersistentException {
    LOGGER.info("Entering exportUAZ/share in my aurin");
    LOGGER.info("using the following server: {} for database/schema: {}",
        postgisDataStoreConfig.getDataStoreParams().get(HOST.key),
        postgisDataStoreConfig.getDataStoreParams().get(DATABASE.key) + "/"
            + postgisDataStoreConfig.getDataStoreParams().get(SCHEMA.key));
    final String tableName = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    final DataStore dataStore = DataStoreFinder
        .getDataStore(postgisDataStoreConfig.getDataStoreParams());
    final SimpleFeatureSource featureSource = dataStore
        .getFeatureSource(tableName);
    final SimpleFeatureCollection features = featureSource.getFeatures();

    final String result = shareUAZSnapshot(features, userId, project);
    LOGGER
        .info("exporting finished, information sent to AURIN/middleware persistence!");
    return result;
  }

  /**
   * Upload features.
   * 
   * @param features
   *          the features
   * @param userId
   *          the user id
   * @param project
   * @return the string
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws MiddlewarePersistentException
   */
  private String shareUAZSnapshot(final SimpleFeatureCollection features,
      final String userId, final WifProject project) throws IOException,
      DataStoreCreationException, MiddlewarePersistentException {

    // TODO the following chunk of code is from Mohammed, we should have these
    // in a shared library
    final int decimals = WifKeys.GEOJSON_PRECISION;
    final GeometryJSON gjson = new GeometryJSON(decimals);
    final FeatureJSON featureJSON = new FeatureJSON(gjson);
    featureJSON.setEncodeFeatureCollectionBounds(true);
    featureJSON.setEncodeFeatureCollectionCRS(true);
    featureJSON.setEncodeNullValues(true);

    final String uuid = UUID.randomUUID().toString();
    // Get the temporary directory and print it.
    final String tempDir = System.getProperty("java.io.tmpdir");

    final File tmpFile = new File(tempDir + "/" + uuid + ".json");
    if (!tmpFile.exists()) {
      tmpFile.createNewFile();

      LOGGER.info(tmpFile.getAbsolutePath() + " has been created");
    }

    final OutputStream output = new BufferedOutputStream(new FileOutputStream(
        tmpFile));
    // TODO end the chunk code from Mohammed
    // FIXME the following operation tthrow a
    // org.geotools.data.postgis.PostGISDialect getOptimizedBounds
    // WARNING: Failed to use ST_Estimated_Extent, falling back on envelope
    // aggregation
    // org.postgresql.util.PSQLException: ERROR: LWGEOM_estimated_extent:
    // couldn't locate table within current schema
    // Nonetheless, this a known issue of geotools when working with psotgis of
    // earlier versions of 1.5
    featureJSON.writeFeatureCollection(features, output);// output

    LOGGER.info("done writing feature collection..." + tmpFile.length() / 1024
        / 1024 + " MB.");
    final Map<String, Object> item = aurinServiceClient.shareAurinProject(
        tmpFile, userId, project);
    LOGGER.debug("response " + item.get("result"));
    return (String) item.get("result");
  }

  /**
   * Persist in aurin creating an item for middleware with the following format:
   * { "id": "uuid4(), what-if projectId that would be used in items", "userId":
   * "ivow", "projectId": "project-meta", "timeStamp": "<system-generated>",
   * "data": { "type": "meta", "name": "Project Name", "description":
   * "Project description", } }
   * 
   * @param project
   *          the project
   * @param userId
   *          the user id
   * @param datastoreUri
   *          the datastore uri
   * @throws MiddlewarePersistentException
   */
  public void persistInAurin(final WifProject project, final String userId)
      throws MiddlewarePersistentException {
    final Map<String, Object> item = new HashMap<String, Object>();
    item.put("userId", userId);
    item.put("projectId", "project-meta-whatif");
    final Map<String, Object> metadata = new HashMap<String, Object>();
    metadata.put("type", "meta");
    metadata.put("name", project.getName());
    // TODO Have a common date format for AURIN
    item.put("timeStamp",
        new SimpleDateFormat().format(project.getCreationDate()));
    metadata.put("description", project.getStudyArea());
    item.put("data", metadata);
    aurinServiceClient.sendPersistenceItem(item, userId);
  }
}
