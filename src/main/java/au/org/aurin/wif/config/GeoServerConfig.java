/*
 * 
 */
package au.org.aurin.wif.config;

/**
 * The Class GeoServerConfig.
 */
public class GeoServerConfig {

  /** The workspace. */
  private String workspace;

  /** The store name. */
  private String storeName;

  /** The rest url. */
  private String restUrl;

  private String userName;

  private String password;

  public String getUserName() {
    return userName;
  }

  public void setUserName(final String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * The production mode specifies if the naming conversion should be
   * bulletproof. Later to be directed from a properties file
   */
  private boolean productionMode = false;

  /**
   * Gets the workspace.
   * 
   * @return the workspace
   */
  public String getWorkspace() {
    return workspace;
  }

  /**
   * Sets the workspace.
   * 
   * @param workspace
   *          the new workspace
   */
  public void setWorkspace(final String workspace) {
    this.workspace = workspace;
  }

  /**
   * Gets the store name.
   * 
   * @return the store name
   */
  public String getStoreName() {
    return storeName;
  }

  /**
   * Sets the store name.
   * 
   * @param storeName
   *          the new store name
   */
  public void setStoreName(final String storeName) {
    this.storeName = storeName;
  }

  /**
   * Gets the rest url.
   * 
   * @return the rest url
   */
  public String getRestUrl() {
    return restUrl;
  }

  /**
   * Sets the rest url.
   * 
   * @param restUrl
   *          the new rest url
   */
  public void setRestUrl(final String restUrl) {
    this.restUrl = restUrl;
  }

  /**
   * Checks if is production mode.
   * 
   * @return true, if is production mode
   */
  public boolean isProductionMode() {
    // TODO Auto-generated method stub
    return productionMode;
  }

  /**
   * @param productionMode
   *          the productionMode to set
   */
  public void setProductionMode(final boolean productionMode) {
    this.productionMode = productionMode;
  }

}
