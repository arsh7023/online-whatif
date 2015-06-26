package au.org.aurin.wif.io;

import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;

/**
 * 
 * @author Gerson Galang
 */
public class DatasetQueryParams {

  /** List of property names to be returned. */
  private String[] propertyNamesToReturn;

  /** The getFeature query filter. */
  private Filter filter;

  private String datasetIdentifier;

  private String srsName;

  public String getSrsName() {
    return srsName;
  }

  public void setSrsName(final String srsName) {
    this.srsName = srsName;
  }

  public String getDatasetIdentifier() {
    return datasetIdentifier;
  }

  public void setDatasetIdentifier(final String datasetIdentifier) {
    this.datasetIdentifier = datasetIdentifier;
  }

  public String[] getPropertyNamesToReturn() {
    return propertyNamesToReturn;
  }

  public void setPropertyNamesToReturn(final String[] propertyNamesToReturn) {
    this.propertyNamesToReturn = propertyNamesToReturn;
  }

  public Filter getFilter() {
    return filter;
  }

  public void setCQLFilterString(final String cqlFilterString)
      throws CQLException {
    filter = ECQL.toFilter(cqlFilterString);
  }

  public String getCQLFilterString() {
    if (filter != null) {
      return ECQL.toCQL(filter);
    }
    return null;
  }

  public void setFilter(final Filter filter) {
    this.filter = filter;
  }

}
