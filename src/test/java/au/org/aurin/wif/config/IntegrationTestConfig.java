package au.org.aurin.wif.config;

public class IntegrationTestConfig {
  private String unionDemoDatastore;
  private String uazDemoDatastore;

  /**
   * @return the unionDemoDatastore
   */
  public String getUnionDemoDatastore() {
    return unionDemoDatastore;
  }

  /**
   * @param unionDemoDatastore
   *          the unionDemoDatastore to set
   */
  public void setUnionDemoDatastore(String unionDemoDatastore) {
    this.unionDemoDatastore = unionDemoDatastore;
  }

  /**
   * @return the uazDemoDatastore
   */
  public String getUazDemoDatastore() {
    return uazDemoDatastore;
  }

  /**
   * @param uazDemoDatastore
   *          the uazDemoDatastore to set
   */
  public void setUazDemoDatastore(String uazDemoDatastore) {
    this.uazDemoDatastore = uazDemoDatastore;
  }

}
