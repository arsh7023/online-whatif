package au.org.aurin.wif.io;

import java.util.Map;

public interface DataStoreConfig {
  /**
   * Returns the map of datastore parameters to be used in instantiating the
   * connection to the DataStore.
   * 
   * @return Map of DataStore parameters
   */
  Map<String, Object> getDataStoreParams();
}
