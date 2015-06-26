package au.org.aurin.wif.io;

import java.io.IOException;
import java.util.Map;

/**
 * This class wraps the functionality that the GeospatialDatasourceImpl
 * provides. We're using GeospatialDatasourceImpl to provide an easy access to
 * non-geospatial databases like postgresql and mysql. It is assumed that we're
 * only going to access DBs supported by geotools. We might need to write a
 * plugin for the unsupported DB later on, if there is a need for it.
 * 
 * @author Gerson Galang
 */
public class NonGeospatialDataSourceImpl implements DataSource {

  private final GeospatialDataSource geoDataSource;

  public NonGeospatialDataSourceImpl(final Map<String, Object> dataStoreParams)
      throws IOException {
    geoDataSource = new GeospatialDataSourceImpl(dataStoreParams);
  }

  public Dataset getDataset(final String datasetName)
      throws DatasetAccessException {
    return geoDataSource.getDataset(datasetName);
  }

  public String[] getDatasetNames() {
    return geoDataSource.getDatasetNames();
  }

}
