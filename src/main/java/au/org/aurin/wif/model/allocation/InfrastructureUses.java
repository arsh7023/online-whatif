/*
 *
 */
package au.org.aurin.wif.model.allocation;

import java.util.Map;

/**
 * <b>PlanneAdLU.java</b> :These land uses are used in allocation each existing
 * land use can be in one or more Planned land use.
 * 
 */
public class InfrastructureUses {

  /** The land use name */
  private String landuseName;

  /** Infrastructure uses map */
  private Map<String, String> infrastructureMap;

  public String getLanduseName() {
    return landuseName;
  }

  public void setLanduseName(String landuseName) {
    this.landuseName = landuseName;
  }

  public Map<String, String> getInfrastructureMap() {
    return infrastructureMap;
  }

  public void setInfrastructureMap(Map<String, String> infrastructureMap) {
    this.infrastructureMap = infrastructureMap;
  }

}
