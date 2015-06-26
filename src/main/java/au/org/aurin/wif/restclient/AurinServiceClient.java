package au.org.aurin.wif.restclient;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import au.org.aurin.wif.exception.io.DataStoreCreationException;
import au.org.aurin.wif.exception.io.MiddlewarePersistentException;
import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The Interface ProjectServiceClient.
 */
public interface AurinServiceClient {

  /**
   * Sets the url.
   * 
   * @param url
   *          the new url
   */
  void setUrl(String url);

  /**
   * Upload zip shape file as a GEOJSON to middleware.
   * 
   * @param input
   *          the input
   * @param userId
   *          the user id
   * @return the string
   * @throws DataStoreCreationException
   *           the data store creation exception
   */
  String uploadFile(File input, String userId)
      throws DataStoreCreationException;

  /**
   * Send persistence item to middleware AURIN with metadata information for a
   * What If project. Persist in aurin creating an item for middleware with the
   * following format: { "userId": "ivow", "projectId": "project-meta-whatif",
   * "data": { "type": "meta", "value": { "name": "Demonstration",
   * "creationDate": "2013-08-19T05:13:45.128+0000", "modifiedDate":
   * "2013-08-19T05:13:45.170+0000", "uazDataStoreURI":
   * "https://dev-api.aurin.org.au/datastore-new/datasets/e7526fdc1311f364d201c2f9ef433a5b"
   * } } }
   * 
   * @param item
   *          the item
   * @param userId
   *          the user id
   * @return true, if successful
   * @throws MiddlewarePersistentException
   *           the middleware persistent exception
   */
  boolean sendPersistenceItem(Map<String, Object> item, String userId)
      throws MiddlewarePersistentException;

  /**
   * Send persistence item to middleware AURIN with metadata information for a
   * What If project. Persist in aurin creating an item for middleware with the
   * following format: Multipart object can contain 'projectId', 'name',
   * 'projectDesc' and 'parse'. Values for all of these flags are strings.
   * "projectId" is not compulsory. You need to set the "parse"="true".
   * "name"="datasetName" "projDesc" = "datasetDescription", "data_path" = the
   * geoJSON file to be uploaded
   * 
   * @param input
   *          the input
   * @param userId
   *          the user id
   * @param project
   *          the project
   * @return the string
   * @throws DataStoreCreationException
   *           the data store creation exception
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   * @throws MiddlewarePersistentException
   */
  public Map<String, Object> shareAurinProject(File input, String userId,
      WifProject project) throws DataStoreCreationException,
      JsonParseException, JsonMappingException, IOException,
      MiddlewarePersistentException;
}
