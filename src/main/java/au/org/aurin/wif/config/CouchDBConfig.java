package au.org.aurin.wif.config;

/**
 * The Class CouchDBConfig.
 */
public class CouchDBConfig {

  /** The repo url. */
  private String repoURL;

  /** The wif db. */
  private String wifDB;
  
  /** The login. */
  private String login;
  
  /** The password. */
  private String password;
 
 /** The login required. */
 private  boolean loginRequired;
  
  /**
   * Gets the repo url.
   *
   * @return the repoURL
   */
  public String getRepoURL() {
    return repoURL;
  }

  /**
   * Sets the repo url.
   *
   * @param repoURL the repoURL to set
   */
  public void setRepoURL(String repoURL) {
    this.repoURL = repoURL;
  }

  /**
   * Gets the wif db.
   *
   * @return the wifDB
   */
  public String getWifDB() {
    return wifDB;
  }

  /**
   * Sets the wif db.
   *
   * @param wifDB the wifDB to set
   */
  public void setWifDB(String wifDB) {
    this.wifDB = wifDB;
  }

  /**
   * Gets the login.
   *
   * @return the login
   */
  public String getLogin() {
    return login;
  }

  /**
   * Sets the login.
   *
   * @param login the login to set
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * Gets the password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password.
   *
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Checks if is login required.
   *
   * @return the loginRequired
   */
  public boolean isLoginRequired() {
    return loginRequired;
  }

  /**
   * Sets the login required.
   *
   * @param loginRequired the loginRequired to set
   */
  public void setLoginRequired(boolean loginRequired) {
    this.loginRequired = loginRequired;
  }


}
