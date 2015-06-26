package au.org.aurin.wif.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class WifConfig.
 */
@Component
public class WifConfig {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 491349213490L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(WifConfig.class);

  /** The server wmsurl. */
  private String serverWMSURL;
  private String middlewareService;

  /** The suitability styles. */
  private List<String> suitabilityStyles;

  /** The rest client target url. */
  private String restClientTargetURL;

  /** The test allocation. */
  private boolean testAllocationArea = false;

  /** For changing log level. */
  private boolean productionLevel = false;

  /**
   * The stand alone indicates whether what if will be dependent on datastore
   * and middleware.
   */
  private boolean standAlone = true;

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
    suitabilityStyles = new ArrayList<String>();
    suitabilityStyles.add("suitabilityStyle");
  }

  /**
   * Checks if is test allocation.
   * 
   * @return true, if is test allocation
   */
  public boolean isTestAllocationArea() {
    return testAllocationArea;
  }

  /**
   * Sets the test allocation.
   * 
   * @param testAllocation
   *          the new test allocation
   */
  public void setTestAllocationArea(boolean testAllocation) {
    this.testAllocationArea = testAllocation;
  }

  /**
   * Gets the server wmsurl.
   * 
   * @return the server wmsurl
   */
  public String getServerWMSURL() {
    return serverWMSURL;
  }

  /**
   * Sets the server wmsurl.
   * 
   * @param serverWMSURL
   *          the new server wmsurl
   */
  public void setServerWMSURL(String serverWMSURL) {
    this.serverWMSURL = serverWMSURL;
  }

  /**
   * Gets the rest client target url.
   * 
   * @return the rest client target url
   */
  public String getRestClientTargetURL() {
    return restClientTargetURL;
  }

  /**
   * Sets the rest client target url.
   * 
   * @param restClientTargetURL
   *          the new rest client target url
   */
  public void setRestClientTargetURL(String restClientTargetURL) {
    this.restClientTargetURL = restClientTargetURL;
  }

  /**
   * Gets the suitability styles.
   * 
   * @return the suitability styles
   */
  public List<String> getSuitabilityStyles() {
    return suitabilityStyles;
  }

  /**
   * Sets the suitability styles.
   * 
   * @param availableStyles
   *          the new suitability styles
   */
  public void setSuitabilityStyles(List<String> availableStyles) {
    this.suitabilityStyles = availableStyles;
  }

  /**
   * Checks if is stand alone.
   * 
   * @return true, if is stand alone
   */
  public boolean isStandAlone() {
    return standAlone;
  }

  /**
   * Sets the stand alone.
   * 
   * @param standAlone
   *          the new stand alone
   */
  public void setStandAlone(boolean standAlone) {
    this.standAlone = standAlone;
  }

  /**
   * @return the middlewareService
   */
  public String getMiddlewareService() {
    return middlewareService;
  }

  /**
   * @param middlewareService
   *          the middlewareService to set
   */
  public void setMiddlewareService(String middlewareService) {
    this.middlewareService = middlewareService;
  }

  public boolean isProductionLevel() {
    return productionLevel;
  }

  public void setProductionLevel(boolean productionLevel) {
    this.productionLevel = productionLevel;
  }

}
