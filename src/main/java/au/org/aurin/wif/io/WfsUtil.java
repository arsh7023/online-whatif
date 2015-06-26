package au.org.aurin.wif.io;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;

public class WfsUtil {

  public static boolean isWfsVersion1_0_0(final WFSDataStore wfsDataStore) {
    try {
      // when this doesn't throw an exception, it implicitly means
      // we're using WFS version > 1.0.0
      wfsDataStore.getServiceVersion();
      return false;
    } catch (final UnsupportedOperationException e) {
      // this exception is usually thrown when we're trying to talk to WFS 1.0.0
      // at the moment, geotools WFS Plugin only supports WFS 1.1.0.
      // for cases that the geotools WFS Plugin cannot handle our query,
      // we'll have to revert back to our old way of running getFeature
      return true;
    }
  }

  public static boolean isWfsDataStore(final DataStore dataStore) {
    if (dataStore instanceof WFSDataStore) {
      return true;
    }
    return false;
  }
}
