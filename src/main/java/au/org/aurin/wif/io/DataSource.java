package au.org.aurin.wif.io;

/**
 * The DataSource is an abstraction of a RDBMS, WFS and other geospatial types
 * of DataSources.
 * 
 * @author Gerson Galang
 */
public interface DataSource {
  /**
   * Returns a reference to the requested dataset.
   * 
   * @param datasetName
   *          can be the RDBMS table name or WFS typename
   * @return a reference to the dataset
   * @throws DatasetAccessException
   *           if dataset is not found in this DataSource
   */
  Dataset getDataset(String datasetName) throws DatasetAccessException;

  /**
   * Returns the array of dataset names provided by this DataSource.
   * 
   * @return an array of datasets names provided by this DataSource
   */
  String[] getDatasetNames();
}
