package au.org.aurin.wif.impl.suitability;

import java.util.List;
import java.util.Map;

/**
 * <b>WifAnalysis.java</b> : Helper class to send to the interface the proper
 * information to retrieve a WMS Outcome from a chosen GeoServer.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class WMSOutcome {

  /** The server url. */
  private String serverURL;

  /** The store name. */
  private String storeName;

  /** The workspace name. */
  private String workspaceName;

  /** The available styles. */
  private List<String> availableStyles;

  /** The score columnspress and its corresponding ranges. */
  private Map<String, Integer> scoreColumns;

  /** The layer name. */
  private String layerName;

  /** The layer parameters. */
  private List<String> layerParameters;
  private Map<String, List<String>> allocationLabels;

  /**
   * Gets the server url.
   * 
   * @return the server url
   */
  public String getServerURL() {
    return serverURL;
  }

  /**
   * Sets the server url.
   * 
   * @param serverURL
   *          the new server url
   */
  public void setServerURL(String serverURL) {
    this.serverURL = serverURL;
  }

  /**
   * Gets the layer name.
   * 
   * @return the layer name
   */
  public String getLayerName() {
    return layerName;
  }

  /**
   * Sets the layer name.
   * 
   * @param layerName
   *          the new layer name
   */
  public void setLayerName(String layerName) {
    this.layerName = layerName;
  }

  /**
   * Gets the layer parameters.
   * 
   * @return the layer parameters
   */
  public List<String> getLayerParameters() {
    return layerParameters;
  }

  /**
   * Sets the layer parameters.
   * 
   * @param layerParameters
   *          the new layer parameters
   */
  public void setLayerParameters(List<String> layerParameters) {
    this.layerParameters = layerParameters;
  }

  /**
   * Gets the store name.
   * 
   * @return the storeName
   */
  public String getStoreName() {
    return storeName;
  }

  /**
   * Sets the store name.
   * 
   * @param storeName
   *          the storeName to set
   */
  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  /**
   * Gets the workspace name.
   * 
   * @return the workspaceName
   */
  public String getWorkspaceName() {
    return workspaceName;
  }

  /**
   * Sets the workspace name.
   * 
   * @param workspaceName
   *          the workspaceName to set
   */
  public void setWorkspaceName(String workspaceName) {
    this.workspaceName = workspaceName;
  }

  /**
   * Gets the available styles.
   * 
   * @return the availableStyles
   */
  public List<String> getAvailableStyles() {
    return availableStyles;
  }

  /**
   * Sets the available styles.
   * 
   * @param availableStyles
   *          the availableStyles to set
   */
  public void setAvailableStyles(List<String> availableStyles) {
    this.availableStyles = availableStyles;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "WMSOutcome ["
        + (serverURL != null ? "serverURL=" + serverURL + ", " : "")
        + (storeName != null ? "storeName=" + storeName + ", " : "")
        + (workspaceName != null ? "workspaceName=" + workspaceName + ", " : "")
        + (availableStyles != null ? "availableStyles=" + availableStyles
            + ", " : "")
        + (scoreColumns != null ? "scoreColumns=" + scoreColumns + ", " : "")
        + (layerName != null ? "layerName=" + layerName + ", " : "")
        + (layerParameters != null ? "layerParameters=" + layerParameters : "")
        + "]";
  }

  /**
   * @return the scoreColumns
   */
  public Map<String, Integer> getScoreColumns() {
    return scoreColumns;
  }

  /**
   * @param scoreColumns
   *          the scoreColumns to set
   */
  public void setScoreColumns(Map<String, Integer> scoreColumns) {
    this.scoreColumns = scoreColumns;
  }

  /**
   * @return the allocationLabels
   */
  public Map<String, List<String>> getAllocationLabels() {
    return allocationLabels;
  }

  /**
   * @param allocationLabels
   *          the allocationLabels to set
   */
  public void setAllocationLabels(Map<String, List<String>> allocationLabels) {
    this.allocationLabels = allocationLabels;
  }

}
