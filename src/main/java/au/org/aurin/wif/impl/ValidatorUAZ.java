package au.org.aurin.wif.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AsyncProjectServiceImpl.
 */

@Service
@Qualifier("validatorUAZ")
public class ValidatorUAZ {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 213432423433L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ValidatorUAZ.class);

  @Autowired
  private GeodataFinder geodataFinder;

  @Autowired
  private GeodataFilterer geodataFilterer;

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

  /**
   * Validate simple, it just for making some update into table using gettools.
   *
   * @param msg
   *          the msg
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws IOException
   */
  public void validateSimple(WifProject project, String msg)
      throws DataStoreCreationException, IOException {

    msg = msg
        + "Assigning temp value to one record in uazDBTable in Postgis Failed!";

    WifProject wifProject = project;

    String uazDBTable = wifProject.getSuitabilityConfig().getUnifiedAreaZone();
    LOGGER.debug("uazDBTable: {}", uazDBTable);
    String geometryColumnName = wifProject.getGeometryColumnName();

    LOGGER.debug("geometryColumnName: {}", geometryColumnName);

    DataStore wifDataStore = null;
    Transaction transaction = new DefaultTransaction("update");
    FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
    int featureCounter = 1;
    try {

      LOGGER.debug("Attempting to open the datastore...");
      wifDataStore = geodataFinder.openPostgisDataStore();

      SimpleFeatureSource featureSourceUD = wifDataStore
          .getFeatureSource(uazDBTable);

      SimpleFeature fin = featureSourceUD.getFeatures().features().next();
      SimpleFeatureType featureTypesource = fin.getFeatureType();
      List<AttributeDescriptor> mlist = featureTypesource
          .getAttributeDescriptors();
      Iterator<AttributeDescriptor> iteratorsource = mlist.iterator();

      String fieldname = "";
      while (iteratorsource.hasNext()) {

        AttributeDescriptor ad = iteratorsource.next();

        if (!ad.getType().getClass().getSimpleName().equals("GeometryTypeImpl")) {
          fieldname = ad.getName().toString();
          break;
        }

      }

      writer = wifDataStore.getFeatureWriter(uazDBTable, transaction);
      LOGGER
          .debug("Feature writer successfully obtained! Proceeding to performing analysis...");
      //for reading values for warming geotools.
      if (writer.hasNext()) {

        SimpleFeature uazFeature = writer.next();

        Object tempvalue = uazFeature.getAttribute(fieldname);
        LOGGER.info("--> tempvalue is: {}", tempvalue.toString());

        //if (featureCounter == 1) {
          uazFeature.setAttribute(fieldname, 1);
          uazFeature.setAttribute(fieldname, tempvalue);
          writer.write();
          transaction.commit();
          LOGGER.debug("one record changed and reset to original value.");

        //}
       // featureCounter++;
      }// end if



    } catch (Exception e) {
      transaction.rollback();
      LOGGER.error(msg);
      throw new DataStoreCreationException(msg, e);
    } finally {

      try {
        if (writer != null) {
          writer.close();
        }
        transaction.close();
        /*
          if ( wifDataStore != null) {
            LOGGER.info(" Attempting to dispose of data store ");
            wifDataStore.dispose();
          }
       */

      } catch (IOException e) {
        LOGGER.error(msg);
        throw new DataStoreCreationException(msg, e);
      }
    }

  }

}
