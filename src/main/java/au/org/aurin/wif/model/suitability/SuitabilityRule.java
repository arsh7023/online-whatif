/*
 *
 */
package au.org.aurin.wif.model.suitability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.org.aurin.wif.model.ScenarioCouchDoc;
import au.org.aurin.wif.model.allocation.AllocationLU;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class SuitabilityRule.
 */
@JsonPropertyOrder({ "id", "docType", "suitabilityLU", "convertibleLUs" })
// @JsonInclude(Include.NON_NULL)
public class SuitabilityRule extends ScenarioCouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7737874431897648747L;

  /** The suitability scenario. */
  @JsonIgnore
  private SuitabilityScenario suitabilityScenario;

  /** The suitability lu. */
  @JsonIgnore
  private SuitabilityLU suitabilityLU;

  /** The convertible l us. */
  @JsonIgnore
  private Set<AllocationLU> convertibleLUs;

  /** The factor importances. */
  private Set<FactorImportance> factorImportances;

  /** The suitability lu map. */
  @JsonIgnore
  private Map<String, String> suitabilityLUMap;

  /** The convertible l us map. */
  @JsonIgnore
  private Map<String, String> convertibleLUsMap;

  /**
   * Update new values.
   * 
   * @param copyRule
   *          the copy rule
   */
  public void updateNewValues(SuitabilityRule copyRule) {
    Set<FactorImportance> importances = this.getFactorImportances();
    for (FactorImportance factorImportance : importances) {
      for (FactorImportance copyImportance : copyRule.getFactorImportances()) {

        // if (copyImportance.getId().equals(factorImportance.getId())) {
        // // factorImportance.updateNewValues(copyImportance);
        // }
      }
    }

    this.setConvertibleLUs(copyRule.getConvertibleLUs());
  }

  /**
   * Gets the suitability scenario.
   * 
   * @return the suitability scenario
   */
  public SuitabilityScenario getSuitabilityScenario() {
    return suitabilityScenario;
  }

  /**
   * Sets the suitability scenario.
   * 
   * @param suitabilityScenario
   *          the new suitability scenario
   */
  public void setSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    this.suitabilityScenario = suitabilityScenario;
  }

  /**
   * Gets the suitability lu.
   * 
   * @return the suitability lu
   */
  public SuitabilityLU getSuitabilityLU() {
    return suitabilityLU;
  }

  /**
   * Sets the suitability lu.
   * 
   * @param suitabilityLU
   *          the new suitability lu
   */
  public void setSuitabilityLU(SuitabilityLU suitabilityLU) {
    this.suitabilityLU = suitabilityLU;
  }

  /**
   * Gets the convertible l us.
   * 
   * @return the convertible l us
   */
  public Set<AllocationLU> getConvertibleLUs() {
    return convertibleLUs;
  }

  /**
   * Gets the convertible l us map.
   * 
   * @return the convertible l us map
   */
  @JsonProperty(value = "convertibleLUs")
  public Map<String, String> getConvertibleLUsMap() {

    return this.convertibleLUsMap;

  }

  /**
   * Sets the convertible l us map.
   * 
   * @param idLabelMap
   *          the id label map
   */
  @JsonProperty(value = "convertibleLUs")
  public void setConvertibleLUsMap(Map<String, String> idLabelMap) {
    this.convertibleLUsMap = idLabelMap;
  }

  /**
   * Sets the convertible l us.
   * 
   * @param convertibleLUs
   *          the new convertible l us
   */
  public void setConvertibleLUs(Set<AllocationLU> convertibleLUs) {
    this.convertibleLUs = convertibleLUs;
  }

  /**
   * Sets the factor importances.
   * 
   * @param factorImportances
   *          the new factor importances
   */
  public void setFactorImportances(Set<FactorImportance> factorImportances) {
    this.factorImportances = factorImportances;
  }

  /**
   * Gets the factor importances.
   * 
   * @return the factor importances
   */
  public Set<FactorImportance> getFactorImportances() {
    return factorImportances;
  }

  /**
   * Gets the suitability lu map.
   * 
   * @return the suitability lu map
   */
  @JsonProperty(value = "suitabilityLU")
  public Map<String, String> getSuitabilityLUMap() {
    return suitabilityLUMap;
  }

  // @JsonProperty(value = "label")
  // public String getLabel() {
  // return suitabilityLUMap.values().toString();
  // }

  /**
   * Sets the suitability lu map.
   * 
   * @param suitabilityLUMap
   *          the suitability lu map
   */
  @JsonProperty(value = "suitabilityLU")
  public void setSuitabilityLUMap(Map<String, String> suitabilityLUMap) {
    this.suitabilityLUMap = suitabilityLUMap;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SuitabilityRule ["
        + (suitabilityScenario != null ? "suitabilityScenario="
            + suitabilityScenario + ", " : "")
        + (suitabilityLU != null ? "suitabilityLU=" + suitabilityLU + ", " : "")
        + (convertibleLUs != null ? "convertibleLUs=" + convertibleLUs + ", "
            : "")
        + (factorImportances != null ? "factorImportances=" + factorImportances
            + ", " : "")
        + (suitabilityLUMap != null ? "suitabilityLUMap=" + suitabilityLUMap
            + ", " : "")
        + (convertibleLUsMap != null ? "convertibleLUsMap=" + convertibleLUsMap
            + ", " : "")
        + (docType != null ? "docType=" + docType + ", " : "")
        + (getScenarioId() != null ? "getScenarioId()=" + getScenarioId()
            + ", " : "") + (getId() != null ? "getId()=" + getId() + ", " : "")
        + (getRevision() != null ? "getRevision()=" + getRevision() : "") + "]";
  }

  /**
   * 
   */
  public SuitabilityRule() {
    super();
    convertibleLUsMap = new HashMap<String, String>();
    suitabilityLUMap = new HashMap<String, String>();
    convertibleLUs = new HashSet<AllocationLU>();
    factorImportances = new HashSet<FactorImportance>();
  }

}
