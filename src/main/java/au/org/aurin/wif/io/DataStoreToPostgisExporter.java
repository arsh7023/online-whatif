package au.org.aurin.wif.io;

import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.SCHEMA;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.DataStoreUnavailableException;
import au.org.aurin.wif.exception.io.ShapeFile2PostGISCreationException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DataStoreToPostgisExporter.
 */
@Component
public class DataStoreToPostgisExporter implements InitializingBean {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DataStoreToPostgisExporter.class);

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

  // private GeospatialDataSource wifDataSource;

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    LOGGER.info("using the following server: {} for database/schema: {}",
        postgisDataStoreConfig.getDataStoreParams().get(HOST.key),
        postgisDataStoreConfig.getDataStoreParams().get(DATABASE.key) + "/"
            + postgisDataStoreConfig.getDataStoreParams().get(SCHEMA.key));
  }

  // TODO Why did I comment this??
  // @PreDestroy
  // public void cleanup() {
  // if (wifDataSource != null) {
  // if (wifDataSource.getDataStore() != null) {
  // LOGGER.info(" Attempting to dispose of data store ");
  // wifDataSource.getDataStore().dispose();
  // }
  // }
  // LOGGER.info(" Service succesfully cleared! ");
  // }

  /**
   * Sets the data store client.
   * 
   * @param dataStoreClient
   *          the new data store client
   */
  public void setDataStoreClient(final DataStoreClient dataStoreClient) {
    this.dataStoreClient = dataStoreClient;
  }

  /**
   * Convert geo json to feature collection.
   * 
   * @param owner
   *          the owner
   * @param geoJsonUri
   *          the geo json uri
   * @return the feature collection
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   */
  public FeatureCollection<SimpleFeatureType, SimpleFeature> convertGeoJSONToFeatureCollection(
      final String owner, final String geoJsonUri)
      throws DataStoreUnavailableException {
    LOGGER
        .debug("Entering DataStoreToPostgisExporter.convertGeoJSONToFeatureCollection()");
    LOGGER.info("exporting to postGIS, from datastore URI={}", geoJsonUri);
    SslUtil.trustSelfSignedSSL();
    FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
    final long startTime = System.nanoTime();

    try {
      featureCollection = dataStoreClient.getDataFeatureCollectionInGeoJSON(
          owner, geoJsonUri);
    } catch (final Exception e) {
      final String msg = "DataStore for doc-id: " + geoJsonUri
          + " isn't available: " + e.getMessage();
      LOGGER.error(msg);
      throw new DataStoreUnavailableException(msg, e);
    }
    final long endTime = System.nanoTime();

    if (featureCollection == null) {
      final String msg = "featureCollection is null: ";
      LOGGER.error(msg);
      throw new DataStoreUnavailableException(msg);
    }
    logTime(startTime, endTime,
        "Get GeoJSON object from DataStore to FeatureCollection took ");
    return featureCollection;
  }

  /**
   * Store feature collection to postgis.
   * 
   * @param featureCollection
   *          the feature collection
   * @param tableName
   *          the table name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void storeFeatureCollectionToPostgis(
      final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection,
      final String tableName) throws IOException {
    LOGGER
        .info("Entering DataStoreToPostgisExporter.storeFeatureCollectionToPostgis()");
    LOGGER.info("using the following server: {} for database/schema: {}",
        postgisDataStoreConfig.getDataStoreParams().get(HOST.key),
        postgisDataStoreConfig.getDataStoreParams().get(DATABASE.key) + "/"
            + postgisDataStoreConfig.getDataStoreParams().get(SCHEMA.key));

    long startTime = System.nanoTime();
    final DataStore dataStore = DataStoreFinder
        .getDataStore(postgisDataStoreConfig.getDataStoreParams());
    long endTime = System.nanoTime();
    logTime(startTime, endTime, "Accessing datastore ");
    final SimpleFeatureType featureType = featureCollection.getSchema();

    // here's one way for us to overwrite the defaut typeName geotools gives
    // unnamed featureTypes
    final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.init(featureType);
    builder.setName(tableName);

    final SimpleFeatureType newFeatureType = builder.buildFeatureType();
    dataStore.createSchema(newFeatureType);

    final Transaction transaction = new DefaultTransaction("create");

    final String typeName = newFeatureType.getTypeName();
    final SimpleFeatureSource featureSource = dataStore
        .getFeatureSource(typeName);

    if (featureSource instanceof SimpleFeatureStore) {
      final SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

      featureStore.setTransaction(transaction);
      try {
        LOGGER.debug(">>>>>>>>>>>>>>*************** Beginning addFeatures");
        featureStore.addFeatures(featureCollection);
        LOGGER.debug(">>>>>>>>>>>>>>*************** Beginning commit");
        startTime = System.nanoTime();
        transaction.commit();
        endTime = System.nanoTime();
        logTime(startTime, endTime,
            "Transaction to all of features to DB took ");

      } catch (final Exception problem) {
        // problem.printStackTrace();
        transaction.rollback();
        LOGGER
            .error("Problem occurred while storing the feature collection. We'll need to rollback...");
      } finally {
        transaction.close();
        if (featureSource != null) {
          if (featureSource.getDataStore() != null) {
            LOGGER.info(" Attempting to dispose of postGIS data store ");
            featureSource.getDataStore().dispose();
          }
        }
      }
    } else {
      LOGGER.error(typeName + " table does not support read/write access");
      throw new IOException(typeName
          + " table does not support read/write access");
    }
    if (featureSource != null) {
      if (featureSource.getDataStore() != null) {
        LOGGER.info(" Attempting to dispose of postGIS data store ");
        featureSource.getDataStore().dispose();
      }
    }
  }

  /**
   * Export user provided geospatial information to postGIS datastore. If
   * standalone is enabled, we'll obtain the information from a previously
   * unzipped shape file, otherwise it will get a geojson from AURIN datastore.
   * 
   * @param owner
   *          the owner
   * @param project
   *          the project
   * @param tableName
   *          the table name
   * @return the simple feature type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws DataStoreUnavailableException
   *           the data store unavailable exception
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws ShapeFile2PostGISCreationException
   *           the shape file2 post gis creation exception
   */
  public WifProject exportToPostgis(final String owner,
      final WifProject project, final String tableName) throws IOException,
      DataStoreUnavailableException, DataStoreCreationException,
      ShapeFile2PostGISCreationException {
    LOGGER.debug("Creating feature collection from owner={}", owner);
    try {

      FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = null;
      File shpFile = null;
      if (project.getLocalShpFile() == null) {
        if (project.getUazDataStoreURI() == null) {
          LOGGER.error("DataStoreURI needed to create the project");
          throw new DataStoreCreationException(
              "DataStoreURI needed to create the project");
        }
        featureCollection = convertGeoJSONToFeatureCollection(owner,
            project.getUazDataStoreURI());
      } else {
        if (project.getLocalShpFile() != null
            && project.getUazDataStoreURI() == null) {
          LOGGER.debug("...with local shape file ={}",
              project.getLocalShpFile());
          shpFile = new File(project.getLocalShpFile());

          featureCollection = fileToPostgisExporter.zipFileToPostGIS(shpFile);
        } else {
          throw new DataStoreCreationException("LocalShpFile not set");
        }
      }

      final ReferencedEnvelope bbox = featureCollection.getBounds();
      final String bboxJson = new StringBuffer().append("[")
          .append(bbox.getMinX()).append(",").append(bbox.getMinY())
          .append(",").append(bbox.getMaxX()).append(",")
          .append(bbox.getMaxY()).append("]").toString();
      project.setBbox(bboxJson);
      project.setSchema(featureCollection.getSchema());
      storeFeatureCollectionToPostgis(featureCollection, tableName);
      // Deleting the temporal file created
      if (project.getLocalShpFile() != null) {
        LOGGER.debug("deleting temporal local shape file ={}",
            project.getLocalShpFile());
        if (shpFile != null) {
          if (FileUtils.deleteQuietly(shpFile)) {
            LOGGER.info("{} has been deleted successfully.",
                shpFile.getAbsolutePath());

            // ali
            final String fileNameWithOutExt = org.apache.commons.io.FilenameUtils
                .removeExtension(shpFile.getName());

            final String dPath = shpFile.getParentFile().getAbsolutePath();
            final File f = new File(dPath + "/" + fileNameWithOutExt + ".dbf");
            if (f.exists() && !f.isDirectory()) {
              LOGGER.info("file deleted :" + f.getAbsolutePath());
              f.delete();
            }
            final File f1 = new File(dPath + "/" + fileNameWithOutExt + ".shx");

            if (f1.exists() && !f1.isDirectory()) {
              LOGGER.info("file deleted :" + f1.getAbsolutePath());
              f1.delete();
            }

            final File f2 = new File(dPath + "/" + fileNameWithOutExt + ".fix");
            if (f2.exists() && !f2.isDirectory()) {
              LOGGER.info("file deleted :" + f2.getAbsolutePath());
              f2.delete();
            }

            final File f3 = new File(dPath + "/" + fileNameWithOutExt + ".prj");
            if (f3.exists() && !f3.isDirectory()) {
              LOGGER.info("file deleted :" + f3.getAbsolutePath());
              f3.delete();
            }

            final File f4 = new File(dPath + "/" + fileNameWithOutExt + ".qix");
            if (f4.exists() && !f4.isDirectory()) {
              LOGGER.info("file deleted :" + f4.getAbsolutePath());
              f4.delete();
            }

          } else {
            LOGGER
                .warn(
                    "{} delete operation failed. in the future the disc can  possibly run out of space",
                    shpFile.getAbsolutePath());
          }
        }
      }
      return project;

    } catch (final Exception e) {
      final String msg = "Creating postGIS spatial store with table name "
          + tableName + " failed";
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(dataStoreClient);
  }

  /**
   * Log time.
   * 
   * @param startTime
   *          the start time
   * @param endTime
   *          the end time
   * @param msg
   *          the msg
   */
  public void logTime(final long startTime, final long endTime, final String msg) {
    final long duration = endTime - startTime;

    LOGGER.info(">>>>>>>>>>>>>>*************** " + msg + " in ms " + duration
        / WifKeys.TIME_LAPSE);
  }

  /**
   * Gets the features from a given table in postGIS.
   * 
   * @param tableName
   *          the table name
   * @return the features
   * @throws DataStoreCreationException
   *           the data store creation exception
   */
  public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures(
      final String tableName) throws DataStoreCreationException {
    LOGGER.info("Entering DataStoreToPostgisExporter.getFeatures()");
    LOGGER.info("using the following server: {} for database/schema: {}",
        postgisDataStoreConfig.getDataStoreParams().get(HOST.key),
        postgisDataStoreConfig.getDataStoreParams().get(DATABASE.key) + "/"
            + postgisDataStoreConfig.getDataStoreParams().get(SCHEMA.key));

    try {
      final DataStore dataStore = DataStoreFinder
          .getDataStore(postgisDataStoreConfig.getDataStoreParams());
      return dataStore.getFeatureSource(tableName).getFeatures();
    } catch (final IOException e) {
      final String msg = "obtaining postGIS spatial features from table name "
          + tableName + " failed";
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    }

  }

}
