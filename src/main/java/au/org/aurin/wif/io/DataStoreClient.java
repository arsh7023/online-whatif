package au.org.aurin.wif.io;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.web.client.RestTemplate;

/**
 * The Data Store Client interface.
 * 
 * @author gerson
 */
public interface DataStoreClient {

  void setRestTemplate(RestTemplate restTemplate);

  /**
   * Sets the URL to be used for the DataStore service.
   * 
   * @param url
   */
  void setUrl(String url);

  /**
   * Calls the create storage location operation of the Data Store service and
   * return the URL of the created resource where data can be stored.
   * 
   * @return the URL of the created storage resource
   */
  @Deprecated
  String createStorageLocation();

  /**
   * Calls the create storage location operation of the Data Store service and
   * return the URL of the created resource where data can be stored.
   * 
   * @param userId
   *          - the user ID as set in the X-AURIN-USERID HEADER
   * @return the URL of the created storage resource
   */
  String createStorageLocation(String userId);

  /**
   * Store the provided geoJSON object into the given URL.
   * 
   * @param url
   *          the URL of the storage resource
   * @param geoJsonStr
   *          the string representation of the geoJSON object
   */
  @Deprecated
  void storeGeoJsonData(String url, String geoJsonStr);

  /**
   * Store the provided geoJSON object into the given URL.
   * 
   * @param userId
   *          - the user ID as set in the X-AURIN-USERID HEADER
   * @param url
   *          the URL of the storage resource
   * @param geoJsonStr
   *          the string representation of the geoJSON object
   */
  void storeGeoJsonData(String userId, String url, String geoJsonStr);

  /**
   * Get the JSON data stored on the given URL.
   * 
   * @param url
   *          the URL of the storage resource
   * @return the JSON object
   */
  @Deprecated
  String getDataInJSON(String dataURI);

  /**
   * Get the JSON data stored on the given URL.
   * 
   * @param userId
   *          - the user ID as set in the X-AURIN-USERID HEADER
   * @param url
   *          the URL of the storage resource
   * @return the JSON object
   */
  String getDataInJSON(String userId, String dataURI);

  /**
   * Get the data stored on the given URL.
   * 
   * @param userId
   *          - the user ID as set in the X-AURIN-USERID HEADER
   * @param url
   *          the URL of the storage resource
   * @return the String object
   */
  String getData(String userId, String dataURI);

  /**
   * Similar to getDataInGeoJSON the difference being this method returns
   * FeatureCollection instead of String.
   * 
   * @param userId
   * @param dataURI
   * @return
   */
  FeatureCollection<SimpleFeatureType, SimpleFeature> getDataFeatureCollectionInGeoJSON(
      String userId, String dataURI);

  /**
   * Get the GeoJSON data stored on the given URL.
   * 
   * @param url
   *          the URL of the storage resource
   * @return the GeoJSON object
   */
  @Deprecated
  String getDataInGeoJSON(String dataURI);

  /**
   * Get the GeoJSON data stored on the given URL.
   * 
   * @param userId
   *          - the user ID as set in the X-AURIN-USERID HEADER
   * @param url
   *          the URL of the storage resource
   * @return the GeoJSON object
   */
  String getDataInGeoJSON(String userId, String dataURI);

}
