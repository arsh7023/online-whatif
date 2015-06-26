package au.org.aurin.wif.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.wfs.WFSDataStore;

/**
 * 
 * @author Gerson Galang
 */
public abstract class AbstractGeospatialDataSource implements
    GeospatialDataSource {

  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(AbstractGeospatialDataSource.class);

  protected Map<String, GeospatialDataset> datasetsCache;

  protected DataStore dataStore;

  protected Wfs_1_0_0_Workaround wfsWorkaround;

  public AbstractGeospatialDataSource(final Map<String, Object> dataStoreParams)
      throws IOException {
    dataStore = DataStoreFinder.getDataStore(dataStoreParams);
    if (dataStore instanceof WFSDataStore) {
      try {
        // when this doesn't throw an exception, it means we're using
        // WFS version > 1.0.0
        ((WFSDataStore) dataStore).getServiceVersion();
        LOGGER.debug("Using getFeature 1.1.0");
        // we'll default to using FeatureSource's getFeatures() implementation

      } catch (final UnsupportedOperationException e) {
        // this exception is usually thrown when we're trying to talk to WFS
        // 1.0.0
        // at the moment, geotools WFS Plugin only supports WFS 1.1.0.
        // for cases that the geotools WFS Plugin cannot handle our query,
        // we'll have to revert back to our old way of running getFeature
        LOGGER.debug("Using getFeature 1.0.0", e);
        wfsWorkaround = new Wfs_1_0_0_Workaround(dataStoreParams);
      }
    }

    datasetsCache = new HashMap<String, GeospatialDataset>();
  }

  /**
   * {@inheritDoc}
   */
  public DataStore getDataStore() {
    return dataStore;
  }

  public String[] getDatasetNames() {
    try {
      return dataStore.getTypeNames();
    } catch (final IOException e) {
      LOGGER.error(
          "IOException occurred while accessing the DataSource type names.", e);
      return null;
    }
  }

}
