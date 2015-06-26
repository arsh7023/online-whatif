package au.org.aurin.wif.io;

import org.geotools.data.simple.SimpleFeatureCollection;

/**
 * The DataCollection is an abstraction of the collection of data that
 * Dataset.getData() returns.
 * <p>
 * At the moment it wraps geotools' FeatureCollection so that
 * NonGeospatialDatasetImpl can make full use of the features provided by
 * FeatureCollection.
 * </p>
 * 
 * @author Gerson Galang
 */
public class DataCollection {
  private final SimpleFeatureCollection collection;

  public DataCollection(final SimpleFeatureCollection collection) {
    this.collection = collection;
  }

  public SimpleFeatureCollection getFeatureCollection() {
    return collection;
  }
}
