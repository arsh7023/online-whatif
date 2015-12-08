package au.org.aurin.wif.model.allocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.ProjectCouchDoc;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class AllocationConfigs.
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "docType" })
public class AllocationConfigs extends ProjectCouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4573398998727018611L;

  private Map<String, String> allocationColumnsMap;
  private Set<String> undevelopedLUsColumns;

  private String plannedALUsFieldName;
  private Set<PlannedALU> plannedALUs;

  private Set<InfrastructureALU> infrastructureALUs;
  private Set<GrowthPatternALU> growthPatternALUs;

  private Set<ColorALU> colorALUs;

  public Set<ColorALU> getColorALUs() {
    return colorALUs;
  }

  public void setColorALUs(
      final Set<ColorALU> colorALUs) {
    this.colorALUs = colorALUs;
  }

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
  public void setWifProject(final WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Instantiates a new allocation config.
   */
  public AllocationConfigs() {
    super();
    allocationColumnsMap = new HashMap<String, String>();
    undevelopedLUsColumns = new HashSet<String>();
    plannedALUs = new HashSet<PlannedALU>();
    infrastructureALUs = new HashSet<InfrastructureALU>();
    growthPatternALUs = new HashSet<GrowthPatternALU>();
    plannedALUsFieldName = new String();
    colorALUs = new HashSet<ColorALU>();

  }

  /**
   * Gets the reset columns.
   *
   * @return the reset columns
   */
  @JsonIgnore
  public Double[] getResetColumns() {
    final ArrayList<Double> columns = new ArrayList<Double>(
        getAllocationColumnsMap().size());
    for (final String value : getAllocationColumnsMap().values()) {
      columns.add(WifKeys.NOT_SUITABLE_SCORE);

    }
    return columns.toArray(new Double[0]);
  }

  @JsonIgnore
  public String[] getResetColumnsStr() {
    final ArrayList<String> columns = new ArrayList<String>(
        getAllocationColumnsMap().size());
    for (final String value : getAllocationColumnsMap().values()) {
      columns.add(" ");

    }
    return columns.toArray(new String[0]);
  }

  @JsonIgnore
  public String getLocalUseScoreLabel(final Set<AllocationLU> landUses)
      throws WifInvalidConfigException {
    for (final AllocationLU allocationLU : landUses) {
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
  public void setAllocationColumnsMap(
      final Map<String, String> allocationColumnsMap) {
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
  public void setUndevelopedLUsColumns(final Set<String> undevelopedLUsColumns) {
    this.undevelopedLUsColumns = undevelopedLUsColumns;
  }

  // new fields
  public Set<PlannedALU> getPlannedALUs() {
    return plannedALUs;
  }

  public void setPlannedALUs(final Set<PlannedALU> plannedALUs) {
    this.plannedALUs = plannedALUs;
  }

  public Set<InfrastructureALU> getInfrastructureALUs() {
    return infrastructureALUs;
  }

  public void setInfrastructureALUs(
      final Set<InfrastructureALU> infrastructureALUs) {
    this.infrastructureALUs = infrastructureALUs;
  }

  public Set<GrowthPatternALU> getGrowthPatternALUs() {
    return growthPatternALUs;
  }

  public void setGrowthPatternALUs(final Set<GrowthPatternALU> growthPatternALUs) {
    this.growthPatternALUs = growthPatternALUs;
  }

  public String getPlannedALUsFieldName() {
    return plannedALUsFieldName;
  }

  public void setPlannedALUsFieldName(final String plannedALUsFieldName) {
    this.plannedALUsFieldName = plannedALUsFieldName;
  }

}
