package au.org.aurin.wif.io;

import java.io.IOException;
import java.util.Map;

/**
 * A factory which produces geospatial datasource objects.
 * 
 * @author Gerson Galang
 */
public class DataSourceFactory {
  public DataSourceFactory() {
  }

  public GeospatialDataSource createGeospatialDataSource(
      final Map<String, Object> dataSourceParams) throws IOException {
    final GeospatialDataSource dataSource = new GeospatialDataSourceImpl(
        dataSourceParams);
    return dataSource;
  }

  public DataSource createNonGeospatialDataSource(
      final Map<String, Object> dataSourceParams) throws IOException {
    final DataSource dataSource = new NonGeospatialDataSourceImpl(
        dataSourceParams);
    return dataSource;
  }
}
