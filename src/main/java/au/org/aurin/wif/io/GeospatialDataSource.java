package au.org.aurin.wif.io;

import org.geotools.data.DataStore;

/**
 * 
 * @author Gerson Galang
 */
public interface GeospatialDataSource extends DataSource {
  /**
   * Gets the geotools DataStore object referenced by this DataSource.
   * 
   * @return the geotools DataStore object referenced by this DataSource
   */
  DataStore getDataStore();
}
