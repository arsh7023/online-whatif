package au.org.aurin.wif.model.allocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class AllocationConfig.
 */

public class AllocationConfig {

  private Map<String, String> allocationColumnsMap;
  private Set<String> undevelopedLUsColumns;

  @JsonIgnore
  private WifProject wifProject;

  /**
   * Gets the wif project.
   * 
   * @return the wif project
   */
  public WifProject getWifProject() {
    return wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Instantiates a new allocation config.
   */
  public AllocationConfig() {
    super();
    allocationColumnsMap = new HashMap<String, String>();
    undevelopedLUsColumns = new HashSet<String>();
  }

  /**
   * Gets the reset columns.
   * 
   * @return the reset columns
   */
  @JsonIgnore
  public Double[] getResetColumns() {
    ArrayList<Double> columns = new ArrayList<Double>(getAllocationColumnsMap()
        .size());
    for (String value : getAllocationColumnsMap().values()) {
      columns.add(WifKeys.NOT_SUITABLE_SCORE);

    }
    return columns.toArray(new Double[0]);
  }

  @JsonIgnore
  public String[] getResetColumnsStr() {
    ArrayList<String> columns = new ArrayList<String>(getAllocationColumnsMap()
        .size());
    for (String value : getAllocationColumnsMap().values()) {
      columns.add(" ");

    }
    return columns.toArray(new String[0]);
  }

  @JsonIgnore
  public String getLocalUseScoreLabel(Set<AllocationLU> landUses)
      throws WifInvalidConfigException {
    for (AllocationLU allocationLU : landUses) {
      if (allocationLU.getLandUseFunction().equals(LandUseFunction.LBCS_9XXX)) {
        return allocationLU.getAssociatedLU().getFeatureFieldName();
      }
    }
    throw new WifInvalidConfigException(
        "There is no conservation suitability land use defined for local use");
  }

  /**
   * @return the allocationColumnsMap
   */
  public Map<String, String> getAllocationColumnsMap() {
    return allocationColumnsMap;
  }

  /**
   * @param allocationColumnsMap
   *          the allocationColumnsMap to set
   */
  public void setAllocationColumnsMap(Map<String, String> allocationColumnsMap) {
    this.allocationColumnsMap = allocationColumnsMap;
  }

  /**
   * @return the undevelopedLUsColumns
   */
  public Set<String> getUndevelopedLUsColumns() {
    return undevelopedLUsColumns;
  }

  /**
   * @param undevelopedLUsColumns
   *          the undevelopedLUsColumns to set
   */
  public void setUndevelopedLUsColumns(Set<String> undevelopedLUsColumns) {
    this.undevelopedLUsColumns = undevelopedLUsColumns;
  }

}
