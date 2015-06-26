/*
 *
 */
package au.org.aurin.wif.model.suitability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.AbstractLandUse;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.info.DemandInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class SuitabilityLU.
 */
@JsonPropertyOrder({ "id", "docType", "associatedLUs" })
public class SuitabilityLU extends AbstractLandUse {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6201852000706301488L;

  /** The associated al us. */
  @JsonIgnore
  private Set<AllocationLU> associatedALUs;

  /** The associated al us map. */
  @JsonIgnore
  private Map<String, String> associatedALUsMap;
  
  /**
   * Sets the associated al us.
   *
   * @param associatedALUs the new associated al us
   */
  public void setAssociatedALUs(Set<AllocationLU> associatedALUs) {
    this.associatedALUs = associatedALUs;
  }

  /**
   * Gets the associated al us.
   *
   * @return the associated al us
   */
  public Set<AllocationLU> getAssociatedALUs() {
    return associatedALUs;
  }

  

//  /**
//   * Gets the area requirement.
//   * 
//   * @param projection
//   *          the projection
//   * @param demandScn
//   *          the demand scn
//   * @return the area requirement
//   * @throws WifInvalidConfigException
//   *           the wif invalid config exception
//   */
//  public AreaRequirement getAreaRequirement(Projection projection,
//      DemandScenario demandScn) throws WifInvalidConfigException {
//    AreaRequirement areq = new AreaRequirement();
//    Double requiredArea = 0.0;
//    for (AllocationLU allocationLU : this.associatedALUs) {
//
//      requiredArea += allocationLU.getAreaRequirement(projection, demandScn)
//          .getRequiredArea();
//    }
//    areq.setProjection(projection);
//    areq.setDemandScenario(demandScn);
//    areq.setRequiredArea(requiredArea);
//
//    return areq;
//  }

  /**
 * Gets the demand infos.
 *
 * @param demandScn the demand scn
 * @return the demand infos
 * @throws WifInvalidConfigException the wif invalid config exception
 * @throws WifInvalidInputException the wif invalid input exception
 */
  public Set<DemandInfo> getDemandInfos(DemandScenario demandScn)
      throws WifInvalidConfigException, WifInvalidInputException {
    Set<DemandInfo> demandInfos = new HashSet<DemandInfo>();
    for (AllocationLU allocationLU : this.associatedALUs) {
      demandInfos.add(allocationLU.getDemandInfoByScenario(demandScn));
    }
    return demandInfos;
  }

  /**
   * Checks if is associated alu.
   *
   * @param id the id
   * @return true, if is associated alu
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  public boolean isAssociatedALU(String id) throws WifInvalidConfigException {
    Set<AllocationLU> alUs = this.getAssociatedALUs();
    for (AllocationLU allocationLU : alUs) {
      if (allocationLU.getId().equals(id)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the associated alu.
   *
   * @param id the id
   * @return the associated alu
   * @throws WifInvalidConfigException the wif invalid config exception
   */
  public AllocationLU getAssociatedALU(String id)
      throws WifInvalidConfigException {
    Set<AllocationLU> alUs = this.getAssociatedALUs();
    for (AllocationLU allocationLU : alUs) {
      if (allocationLU.getId().equals(id)) {
        return allocationLU;
      }

    }
    throw new WifInvalidConfigException(
        "allocationLU not found in associatedLUs in suitabilityLU: "
            + this.getLabel() + " for allocationLUId" + id);
  }

  /**
   * Gets the associated al us map.
   *
   * @return the associated al us map
   */
  @JsonProperty(value = "associatedALUs")
  public Map<String, String> getAssociatedALUsMap() {
    return associatedALUsMap;
  }

  /**
   * Sets the associated al us map.
   *
   * @param associatedALUsMap the associated al us map
   */
  @JsonProperty(value = "associatedALUs")
  public void setAssociatedALUsMap(Map<String, String> associatedALUsMap) {
    this.associatedALUsMap = associatedALUsMap;
  }

  /**
   * Instantiates a new suitability lu.
   */
  public SuitabilityLU() {
    super();
    this.associatedALUs =  new HashSet<AllocationLU>();
    associatedALUsMap = new HashMap<String, String>();
    
  }
}
