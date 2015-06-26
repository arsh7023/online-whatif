package au.org.aurin.wif.io;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.simple.SimpleFeatureSource;

/**
 * GeospatialDatasourceImpl uses geotools as an interface to geo and
 * non-geospatial databases.
 * 
 * @author Gerson Galang
 */
public class GeospatialDataSourceImpl extends AbstractGeospatialDataSource {
  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(GeospatialDataSourceImpl.class);

  public GeospatialDataSourceImpl(final Map<String, Object> dataStoreParams)
      throws IOException {
    super(dataStoreParams);
  }

  public Dataset getDataset(final String datasetName)
      throws DatasetAccessException {
    if (datasetsCache.containsKey(datasetName)) {
      return datasetsCache.get(datasetName);
    }
    try {
      final SimpleFeatureSource source = dataStore
          .getFeatureSource(datasetName);

      // let's see if a call to getSchema would return a SimpleFeatureType.
      // if this throws an exception, that means the datasetName provided
      // is invalid
      source.getSchema();

      final GeospatialDatasetImpl dataset = new GeospatialDatasetImpl(this,
          source);
      // let's cache this dataset
      datasetsCache.put(datasetName, dataset);
      return dataset;
    } catch (final IOException e) {
      LOGGER.error("The requested Dataset " + datasetName + " does not exist.",
          e);
      throw new DatasetAccessException(
          "The requested Dataset " + datasetName + " does not exist.",
          "The remote dataset has stopped responding. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final NullPointerException e) {
      throw new DatasetAccessException(
          "The requested Dataset " + datasetName + " does not exist.",
          "The remote dataset has stopped responding. Please, try later or notify AURIN if the problem persists",
          e);
    }
  }

}
