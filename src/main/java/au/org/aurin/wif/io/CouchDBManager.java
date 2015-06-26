/**
 *
 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.io;

import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.config.CouchDBConfig;
import au.org.aurin.wif.svc.WifKeys;

/**
 * //TODO remember to catch the following exception Caused by:
 * org.ektorp.DbAccessException: 401:Unauthorized URI: /whatif/ Response Body: {
 * "error" : "unauthorized", "reason" : "You are not a server admin." }
 * 
 * @author marcosnr
 */
@Component
public class CouchDBManager {
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchDBManager.class);
  private String repoURL;
  private HttpClient httpClient;
  private String wifDB;

  private CouchDbConnector db;
  @Autowired
  private CouchDBConfig couchDBConfig;
  private boolean loginRequired;

  /**
   * Inits the.
   */

  @PostConstruct
  public void init() {

    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    // TODO do it more elegant,enable the rewriting of reviews, only for
    // development
    System.setProperty("org.ektorp.support.AutoUpdateViewOnChange", "true");
    repoURL = couchDBConfig.getRepoURL();
    wifDB = couchDBConfig.getWifDB();
    loginRequired = couchDBConfig.isLoginRequired();
    LOGGER.info("Using CouchDB URL {}, with What If database: {} ", repoURL,
        wifDB);
    try {
      if (loginRequired) {

        LOGGER.info("using the login name {} ", couchDBConfig.getLogin());
        httpClient = new StdHttpClient.Builder().url(repoURL)
            .username(couchDBConfig.getLogin())
            .password(couchDBConfig.getPassword()).build();
      } else {
        LOGGER.info("authentication not required, not using credentials");
        httpClient = new StdHttpClient.Builder().url(repoURL).build();
      }
    } catch (MalformedURLException e) {
      LOGGER.error(
          "MalformedURLException: Using CouchDB URL {} , with What If database: {} "
              + repoURL, wifDB);
    }

    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    // TODO There should be one for each repository
    db = new StdCouchDbConnector(wifDB, dbInstance);

  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("succesfully cleared! ");
  }

  /**
   * @return the db
   */
  public CouchDbConnector getDb() {
    return db;
  }

  /**
   * @param db
   *          the db to set
   */
  public void setDb(CouchDbConnector db) {
    this.db = db;
  }
}
