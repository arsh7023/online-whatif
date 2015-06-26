/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class AllocationScenario.
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "label", "docType" })
public class AllocationControlScenario extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1778238396632217821L;

  /** The Planned Land Use. */
  private Boolean PlannedlandUseControl;

  /** The Infrastructure. */
  private Boolean InfrastructureControl;

  /** The GrowthPattern. */
  private Boolean GrowthPatternControl;

  /** Infrastructure Uses */
  private Set<InfrastructureUses> infrastructureUses;

  private Set<String> InfrastructureControlLabels;

  private Set<String> GrowthPatternControlLabels;

  // @JsonIgnore
  // private Map<String, String> infrastructureUsesMap;
  //
  // @JsonProperty(value = "infrastructureUses")
  // public Map<String, String> getInfrastructureUsesMap() {
  // return infrastructureUsesMap;
  // }
  //
  // @JsonProperty(value = "infrastructureUses")
  // public void setInfrastructureUsesMap(Map<String, String>
  // infrastructureUsesMap) {
  // this.infrastructureUsesMap = infrastructureUsesMap;
  // }

  public AllocationControlScenario() {
    super();
    infrastructureUses = new HashSet<InfrastructureUses>();
    // infrastructureUsesMap = new HashMap<String, String>();
  }

  public void addinfrastructureUses(InfrastructureUses InfrastructureUses) {
    this.infrastructureUses.add(InfrastructureUses);
  }

  public Boolean getPlannedlandUseControl() {
    return PlannedlandUseControl;
  }

  public void setPlannedlandUseControl(Boolean plannedlandUseControl) {
    PlannedlandUseControl = plannedlandUseControl;
  }

  public Boolean getInfrastructureControl() {
    return InfrastructureControl;
  }

  public void setInfrastructureControl(Boolean infrastructureControl) {
    InfrastructureControl = infrastructureControl;
  }

  public Boolean getGrowthPatternControl() {
    return GrowthPatternControl;
  }

  public void setGrowthPatternControl(Boolean growthPatternControl) {
    GrowthPatternControl = growthPatternControl;
  }

  public Set<InfrastructureUses> getInfrastructureUses() {
    return infrastructureUses;
  }

  public void setInfrastructureUses(Set<InfrastructureUses> infrastructureUses) {
    this.infrastructureUses = infrastructureUses;
  }

  public Set<String> getInfrastructureControlLabels() {
    return InfrastructureControlLabels;
  }

  public void setInfrastructureControlLabels(
      Set<String> infrastructureControlLabels) {
    InfrastructureControlLabels = infrastructureControlLabels;
  }

  public Set<String> getGrowthPatternControlLabels() {
    return GrowthPatternControlLabels;
  }

  public void setGrowthPatternControlLabels(
      Set<String> growthPatternControlLabels) {
    GrowthPatternControlLabels = growthPatternControlLabels;
  }

}
