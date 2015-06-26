package au.org.aurin.wif.io;

import java.util.List;

/**
 * 
 * @author Gerson Galang
 */
public interface Dataset {

  String getName();

  List<DatasetAttribute> getAttributes();

  DataCollection getData(DatasetQueryParams params)
      throws InvalidDataQueryParamsException, DatasetAccessException;

  /**
   * Return the reference to the datasource holding this dataset.
   * 
   * @return the datasource that holds this dataset
   */
  au.org.aurin.wif.io.DataSource getDataSource();
}
