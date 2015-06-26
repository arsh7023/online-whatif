/*
 * 
 */
package au.org.aurin.wif.restclient;

import static au.org.aurin.wif.io.RestAPIConstants.HEADER_USER_ID_KEY;
import static au.org.aurin.wif.io.RestUtil.removeTrailingSlash;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ProjectServiceClientImpl.
 */
@Service
@Qualifier("aurinServiceClient")
public class AurinServiceClientImpl implements AurinServiceClient {

  /** The url. */
  private String url;

  /** The rest template. */
  @Autowired
  private RestTemplate restTemplate;

  /** The wif config. */
  @Autowired
  private WifConfig wifConfig;

  /** The upload url. */
  private String uploadUrl;

  /** The persistence url. */
  private String persistenceUrl;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AurinServiceClientImpl.class);

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    LOGGER.info("Using the following middleware service: "
        + wifConfig.getMiddlewareService());
    setUrl(wifConfig.getMiddlewareService());
    setUploadUrl(url + WifKeys.MIDDLEWARE_UPLOAD_SVC);
    setPersistenceUrl(url + WifKeys.MIDDLEWARE_PERSISTENCE_SVC);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.restclient.ProjectServiceClient#setUrl(java.lang.String)
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.restclient.AurinServiceClient#sendPersistenceItem(java
   * .io.File, java.lang.String, au.org.aurin.wif.model.WifProject)
   */
  public Map<String, Object> shareAurinProject(final File input,
      final String userId, final WifProject project)
      throws DataStoreCreationException, JsonParseException,
      JsonMappingException, IOException, MiddlewarePersistentException {
    LOGGER.debug("url: {}, userId:{}", uploadUrl, userId);
    final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
    final HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.add("Content-type", MediaType.MULTIPART_FORM_DATA_VALUE);

    final FileSystemResource r = new FileSystemResource(input);
    final HttpEntity<FileSystemResource> sample_file = new HttpEntity<FileSystemResource>(
        r, fileHeaders);
    parts.add("data-path", sample_file);
    parts.add("parse", "true");
    parts.add("projectId", project.getId());
    // TODO Have a common date format for AURIN

    parts.add("projectDesc", project.getName() + " as of "
        + new SimpleDateFormat().format(project.getModifiedDate()));
    parts.add("name", project.getName());
    final HttpHeaders reqheaders = new HttpHeaders();
    reqheaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    reqheaders.add(HEADER_USER_ID_KEY, userId);
    final HttpEntity<MultiValueMap<String, Object>> ereq = new HttpEntity<MultiValueMap<String, Object>>(
        parts, reqheaders);

    final ResponseEntity<String> response = restTemplate.exchange(uploadUrl
        + userId, HttpMethod.POST, ereq, String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new DataStoreCreationException("HTTP Response Status Code: "
          + response.getStatusCode()
          + " was thrown while running a request against " + url + userId
          + " for file: " + input.getName());
    }

    final String body = response.getBody();
    LOGGER.debug("response: {} ", body);

    // Manually parsing because this service returns txt/html type
    final ObjectMapper mapper = new ObjectMapper();
    final Map<String, Object> jsonResponse = mapper.readValue(body, Map.class);
    // Replaced projectID with internal whatif ID
    jsonResponse.put("projectId", project.getId());
    sendPersistenceItem(jsonResponse, userId);

    return jsonResponse;

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.restclient.AurinServiceClient#uploadFile(java.io.File,
   * java.lang.String)
   */
  public String uploadFile(final File input, final String userId)
      throws DataStoreCreationException {
    LOGGER.debug("url: {}, userId:{}", uploadUrl, userId);
    final MultiValueMap<String, org.springframework.http.HttpEntity<FileSystemResource>> parts = new LinkedMultiValueMap<String, org.springframework.http.HttpEntity<FileSystemResource>>();
    final HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.add("Content-type", MediaType.MULTIPART_FORM_DATA_VALUE);

    final FileSystemResource r = new FileSystemResource(input);
    final org.springframework.http.HttpEntity<FileSystemResource> sample_file = new org.springframework.http.HttpEntity<FileSystemResource>(
        r, fileHeaders);
    parts.add("data-path", sample_file);

    final HttpHeaders reqheaders = new HttpHeaders();
    reqheaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    reqheaders.add(HEADER_USER_ID_KEY, userId);
    final org.springframework.http.HttpEntity<MultiValueMap<String, org.springframework.http.HttpEntity<FileSystemResource>>> ereq = new org.springframework.http.HttpEntity<MultiValueMap<String, org.springframework.http.HttpEntity<FileSystemResource>>>(
        parts, reqheaders);

    final ResponseEntity<String> response = restTemplate.exchange(uploadUrl
        + userId, HttpMethod.POST, ereq, String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new DataStoreCreationException("HTTP Response Status Code: "
          + response.getStatusCode()
          + " was thrown while running a request against " + url + userId
          + " for file: " + input.getName());
    }
    return response.getBody();

  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.restclient.AurinServiceClient#sendPersistenceItem(java
   * .util.Map, java.lang.String)
   */
  public boolean sendPersistenceItem(final Map<String, Object> item,
      final String userId) throws MiddlewarePersistentException {
    final HttpHeaders headers = new HttpHeaders();

    headers.setContentType(MediaType.APPLICATION_JSON);
    LOGGER.debug("Using link: " + removeTrailingSlash(persistenceUrl));
    headers.add(HEADER_USER_ID_KEY, userId);
    final HttpEntity<Map> requestEntity = new org.springframework.http.HttpEntity<Map>(
        item, headers);
    final ResponseEntity<Object> response = restTemplate.postForEntity(
        persistenceUrl, requestEntity, Object.class);

    if (response.getStatusCode() != HttpStatus.OK) {
      throw new MiddlewarePersistentException("HTTP Response Status Code: "
          + response.getStatusCode() + " was thrown while accessing "
          + persistenceUrl);
    }
    return true;
  }

  /**
   * Gets the upload url.
   * 
   * @return the uploadUrl
   */
  public String getUploadUrl() {
    return uploadUrl;
  }

  /**
   * Sets the upload url.
   * 
   * @param uploadUrl
   *          the uploadUrl to set
   */
  public void setUploadUrl(final String uploadUrl) {
    this.uploadUrl = uploadUrl;
  }

  /**
   * Gets the persistence url.
   * 
   * @return the persistenceUrl
   */
  public String getPersistenceUrl() {
    return persistenceUrl;
  }

  /**
   * Sets the persistence url.
   * 
   * @param persistenceUrl
   *          the persistenceUrl to set
   */
  public void setPersistenceUrl(final String persistenceUrl) {
    this.persistenceUrl = persistenceUrl;
  }

}
