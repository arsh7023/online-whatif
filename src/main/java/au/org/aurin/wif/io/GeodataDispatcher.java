package au.org.aurin.wif.io;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// TODO: Auto-generated Javadoc
/**
 * TODO Should be an AURIN service! <b>GeodataDispatcher.java</b> : Handle
 * creation of the geodata output file
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */

@Component
public class GeodataDispatcher {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(GeodataDispatcher.class);

  /**
   * To access the data store configuration properties.
   */
  @Autowired
  @Qualifier(value = "wifDataStoreConfig")
  private PostgisDataStoreConfig wifDataStoreConfig;

  /**
   * To access the data store.
   */
  @Autowired
  private DataSourceFactory dataSourceFactory;

  /**
   * Creates the lua shp file.
   * 
   * @param luaCollection
   *          the lua collection
   * @param lsaShpURI
   *          the lsa shp uri
   * @return true, if successful
   */
  public boolean createLUAShpFile(final SimpleFeatureCollection luaCollection,
      final URL lsaShpURI) {

    LOGGER.info("url2: {} ", lsaShpURI.getFile());

    final ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
    final Map<String, Serializable> params = new HashMap<String, Serializable>();

    try {
      params.put("url", lsaShpURI);
      params.put("create spatial index", Boolean.TRUE);

      final ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory
          .createNewDataStore(params);
      newDataStore.createSchema(luaCollection.getSchema());
      final Transaction transaction = new DefaultTransaction("create");

      final String typeName = newDataStore.getTypeNames()[0];
      final SimpleFeatureSource featureSource = newDataStore
          .getFeatureSource(typeName);

      if (featureSource instanceof SimpleFeatureStore) {
        final SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

        featureStore.setTransaction(transaction);
        try {
          featureStore.addFeatures(luaCollection);
          transaction.commit();

        } catch (final Exception problem) {
          LOGGER.error("createLUAShpFile problem {}", problem.getMessage());
          transaction.rollback();

        } finally {
          transaction.close();
        }
        LOGGER.info("{} created successfully", typeName);
        return true;
      } else {
        LOGGER.error("{} does not support read/write access", typeName);
      }
    } catch (final MalformedURLException e) {
      LOGGER.error("createLUAShpFile is MalformedURLException", e.getMessage());
    } catch (final IOException e) {
      LOGGER.error("createLUAShpFile trhows IOException", e.getMessage());
    }
    return false;
  }

  /**
   * Create an output DB table.
   * 
   * @param luaCollection
   *          the lua collection
   */
  public void createLUADBTable(final SimpleFeatureCollection luaCollection) {

    try {

      final GeospatialDataSource wifDataSource = dataSourceFactory
          .createGeospatialDataSource(wifDataStoreConfig.getDataStoreParams());
      final DataStore wifDataStore = wifDataSource.getDataStore();
      wifDataStore.createSchema(luaCollection.getSchema());
      final Transaction transaction = new DefaultTransaction("create");

      final String typeName = wifDataStore.getTypeNames()[0];
      LOGGER.info("table to be created: {} ", typeName);
      final SimpleFeatureSource luaFeatureSource = wifDataStore
          .getFeatureSource(typeName);

      if (luaFeatureSource instanceof SimpleFeatureStore) {
        final SimpleFeatureStore luaFeatureStore = (SimpleFeatureStore) luaFeatureSource;

        luaFeatureStore.setTransaction(transaction);
        try {
          luaFeatureStore.addFeatures(luaCollection);
          transaction.commit();

        } catch (final Exception problem) {
          LOGGER.error("{} throws IOException", problem.getMessage());
          transaction.rollback();

        } finally {
          transaction.close();
        }
        LOGGER.info("{} created successfully", typeName);
      } else {
        LOGGER.error("{} does not support read/write access", typeName);
      }
    } catch (final MalformedURLException e) {
      LOGGER.error("createLUADBTable {}", e.getMessage());
    } catch (final IOException e) {
      LOGGER.error("createLUADBTable {}", e.getMessage());
    }

  }

  /**
   * Update luadb table.
   * 
   * @param luaCollection
   *          the lua collection
   * @param lsaDBTable
   *          the lsa db table
   */
  public void updateLUADBTable(final SimpleFeatureCollection luaCollection,
      final String lsaDBTable) {
    try {
      final GeospatialDataSource wifDataSource = dataSourceFactory
          .createGeospatialDataSource(wifDataStoreConfig.getDataStoreParams());
      final DataStore wifDataStore = wifDataSource.getDataStore();
      wifDataStore.createSchema(luaCollection.getSchema());

      // lsaDBTable = wifDataStore.getTypeNames()[0];
      LOGGER.info("typeName to be updated: {} ", lsaDBTable);

      final SimpleFeatureSource luaFeatureSource = wifDataStore
          .getFeatureSource(lsaDBTable);

      if (luaFeatureSource instanceof SimpleFeatureStore) {
        final SimpleFeatureStore luaFeatureStore = (SimpleFeatureStore) luaFeatureSource;

        final Transaction transaction = new DefaultTransaction("updateLSA");
        luaFeatureStore.setTransaction(transaction);
        // Filter filter = CQL.toFilter("fred");

        try {
          luaFeatureStore.addFeatures(luaCollection);
          // luaFeatureStore.
          transaction.commit();

        } catch (final Exception problem) {
          LOGGER.error("{} throws IOException", problem.getMessage());
          transaction.rollback();

        } finally {
          transaction.close();
        }
        LOGGER.info("{} created successfully", lsaDBTable);
      } else {
        LOGGER.error("{} does not support read/write access", lsaDBTable);
      }
    } catch (final MalformedURLException e) {
      LOGGER.error("createLUADBTable {}", e.getMessage());
    } catch (final IOException e) {
      LOGGER.error("createLUADBTable {}", e.getMessage());
    }
  }

}
