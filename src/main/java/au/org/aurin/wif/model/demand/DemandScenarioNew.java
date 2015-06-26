/**
 *
 * ashamakhy
 * 18/12/2013
 */
package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;
import au.org.aurin.wif.model.demand.datanew.DemandDataNew;
import au.org.aurin.wif.model.demand.datanew.DemandEmpNew;

//@JsonPropertyOrder({ "id", "label", "docType" })
public class DemandScenarioNew extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2167463430566658863L;

  // @JsonIgnore
  // private Map<String, String> demandDataItemsMap;

  private Set<DemandDataNew> demandDataItems;

  // @JsonProperty(value = "demandDataItems")
  // public Map<String, String> getDemandDataItemsMap() {
  // return demandDataItemsMap;
  // }
  //
  // @JsonProperty(value = "demandDataItems")
  // public void setDemandDataItemsMap(Map<String, String> demandDataItemsMap) {
  // this.demandDataItemsMap = demandDataItemsMap;
  // }

  private Set<DemandEmpNew> demandEmpItems;

  private Set<AreaRequirement> AreaRequirements;

  public Set<DemandDataNew> getDemandDataItems() {
    return demandDataItems;
  }

  public void setDemandDataItems(final Set<DemandDataNew> demandDataItems) {
    this.demandDataItems = demandDataItems;
  }

  public Set<DemandEmpNew> getDemandEmpItems() {
    return demandEmpItems;
  }

  public void setDemandEmpItems(final Set<DemandEmpNew> demandEmpItems) {
    this.demandEmpItems = demandEmpItems;
  }

  public DemandScenarioNew() {
    super();
    this.demandDataItems = new HashSet<DemandDataNew>();
    // demandDataItemsMap = new HashMap<String, String>();
    this.demandEmpItems = new HashSet<DemandEmpNew>();
    this.AreaRequirements = new HashSet<AreaRequirement>();
  }

  public Set<AreaRequirement> getManualAreaRequirements() {
    return AreaRequirements;
  }

  public void setManualAreaRequirements(
      final Set<AreaRequirement> manualAreaRequirements) {
    this.AreaRequirements = manualAreaRequirements;
  }
}
