package au.org.aurin.wif.io;

import static au.org.aurin.wif.io.DataStoreClientConstants.HEADER_GENERIC_USER_VALUE;
import static au.org.aurin.wif.io.DataStoreServiceConstants.CREATE_PATH;
import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;
import static au.org.aurin.wif.io.RestUtil.removeTrailingSlash;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * The Data Store Client implementation.
 * 
 * @author Gerson Galang
 */
public class DataStoreClientImpl implements DataStoreClient, InitializingBean {

  private String url;

  private RestTemplate restTemplate;

  public void setUrl(final String url) {
    this.url = url;
  }

  @Autowired
  public void setRestTemplate(final RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * {@inheritDoc}
   */
  public String createStorageLocation() {
    return createStorageLocation(HEADER_GENERIC_USER_VALUE);
  }

  public String createStorageLocation(final String userId) {
    // this call is likely to fail unless the client trying to initialise
    // a storage doesn't have permissions to do so
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add(HEADER_USER_ID_KEY, userId);
    final HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
    final ResponseEntity<String> response = restTemplate.exchange(
        removeTrailingSlash(url) + "/" + CREATE_PATH, HttpMethod.POST,
        requestEntity, String.class);

    if (response.getStatusCode() != HttpStatus.CREATED) {
      throw new ServiceException(
          "HTTP Response Status Code: "
              + response.getStatusCode()
              + " was thrown while accessing the DataStoreService.createStorageLocation(userId).");
    }
    return response.getHeaders().getLocation().toString();
  }

  /**
   * {@inheritDoc}
   */
  public void storeGeoJsonData(final String url, final String geoJsonStr) {
    storeGeoJsonData(HEADER_GENERIC_USER_VALUE, url, geoJsonStr);
  }

  public void storeGeoJsonData(final String userId, final String url,
      final String geoJsonStr) {
    // this one's going to be a put request as we're just updating the
    // object stored in the given URL
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add(HEADER_USER_ID_KEY, userId);
    final HttpEntity<Object> requestEntity = new HttpEntity<Object>(geoJsonStr,
        headers);
    final ResponseEntity<String> response = restTemplate.exchange(
        removeTrailingSlash(url), HttpMethod.PUT, requestEntity, String.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new ServiceException(
          "HTTP Response Status Code: "
              + response.getStatusCode()
              + " was thrown while accessing the DataStoreService.getGeoJsonData().");
    }
    // TODO do we need to return the response body here? "Upload successful"
    // is returned when the process is successful
    // response.getBody();
  }

  /**
   * {@inheritDoc}
   */
  public String getDataInJSON(final String dataURI) {
    return getDataInJSON(HEADER_GENERIC_USER_VALUE, dataURI);
  }

  public String getDataInJSON(final String userId, final String dataURI) {
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add("Accept", "application/json");
    headers.add(HEADER_USER_ID_KEY, userId);
    return getData(dataURI, headers);
  }

  public FeatureCollection<SimpleFeatureType, SimpleFeature> getDataFeatureCollectionInGeoJSON(
      final String userId, final String dataURI) {
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add("Accept", "application/geo+json");
    headers.add(HEADER_USER_ID_KEY, userId);
    return getDataFeatureCollection(dataURI, headers);
  }

  /**
   * {@inheritDoc}
   */
  public String getDataInGeoJSON(final String dataURI) {
    return getDataInGeoJSON(HEADER_GENERIC_USER_VALUE, dataURI);
  }

  public String getDataInGeoJSON(final String userId, final String dataURI) {
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add("Accept", "application/geo+json");
    headers.add(HEADER_USER_ID_KEY, userId);
    return getData(dataURI, headers);
  }

  private FeatureCollection<SimpleFeatureType, SimpleFeature> getDataFeatureCollection(
      final String dataURI, final MultiValueMap<String, String> headers) {
    final HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
    final ResponseEntity<FeatureCollection> response = restTemplate.exchange(
        removeTrailingSlash(dataURI), HttpMethod.GET, requestEntity,
        FeatureCollection.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new ServiceException("HTTP Response Status Code: "
          + response.getStatusCode()
          + " was thrown while accessing the DataProviderService.getData().");
    }
    return response.getBody();
  }

  private String getData(final String dataURI,
      final MultiValueMap<String, String> headers) {
    final HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);
    final ResponseEntity<String> response = restTemplate.exchange(
        removeTrailingSlash(dataURI), HttpMethod.GET, requestEntity,
        String.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new ServiceException("HTTP Response Status Code: "
          + response.getStatusCode()
          + " was thrown while accessing the DataProviderService.getData().");
    }
    return response.getBody();
  }

  public void afterPropertiesSet() throws Exception {
    Assert.notNull(restTemplate);
  }

  public String getData(final String userId, final String dataURI) {
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
    headers.add(HEADER_USER_ID_KEY, userId);
    return getData(dataURI, headers);
  }

}
