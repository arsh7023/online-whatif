/*
 *
 */
package au.org.aurin.wif.model.suitability;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>SuitabilityScenario.java</b> : Configuration parameters required to set up
 * a Suitability Scenario.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class SuitabilityScenario extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8915313127623113019L;
  /** The suitability rules. */
  private Set<SuitabilityRule> suitabilityRules;

  /** The ready. */
  private Boolean ready = false;

  /**
   * Gets the suitability rules.
   * 
   * @return the suitabilityRules
   */
  public Set<SuitabilityRule> getSuitabilityRules() {
    return suitabilityRules;
  }

  /**
   * Sets the suitability rules.
   * 
   * @param suitabilityRules
   *          the suitabilityRules to set
   */
  public void setSuitabilityRules(Set<SuitabilityRule> suitabilityRules) {
    this.suitabilityRules = suitabilityRules;
  }

  /**
   * Gets the land use conversion by slu name.
   * 
   * @param newLUName
   *          the new lu name
   * @return the land use conversion by slu name
   */
  public SuitabilityRule getLandUseConversionBySLUName(String newLUName) {
    for (SuitabilityRule lu : this.getSuitabilityRules()) {
      if (lu.getSuitabilityLU().getLabel().equals(newLUName)) {
        return lu;
      }
    }
    return null;
  }

  /**
   * Update new values because the cache will take precedence over the JSON
   * object.
   * 
   * @param copy
   *          the copy
   */
  public void updateNewValues(SuitabilityScenario copy) {

    super.updateNewValues(copy);

    Set<SuitabilityRule> copyrules = copy.getSuitabilityRules();
    if (copyrules != null) {
      for (SuitabilityRule copyRule : copyrules) {
        for (SuitabilityRule rule : this.getSuitabilityRules()) {
          if (rule.getId().equals(copyRule.getId())) {
            rule.updateNewValues(copyRule);
          }
        }
      }
    }
  }

  /**
   * Modify factor rating.
   * 
   * @param newLUName
   *          the new lu name
   * @param newFactorName
   *          the new factor name
   * @param newImportance
   *          the new importance
   * @param newFactorTypeName
   *          the new factor type name
   * @param newRating
   *          the new rating
   */
  public void modifyFactorRating(String newLUName, String newFactorName,
      String newImportance, String newFactorTypeName, String newRating) {
    Set<SuitabilityRule> suitabilityRules = this.getSuitabilityRules();
    for (SuitabilityRule suitabilityRule : suitabilityRules) {
      if (suitabilityRule.getSuitabilityLU().getLabel()
          .equalsIgnoreCase(newLUName)) {
        Set<FactorImportance> factorImportances = suitabilityRule
            .getFactorImportances();
        for (FactorImportance factorImportance : factorImportances) {
          factorImportance.modifyFactorRating(newFactorName, newImportance,
              newFactorTypeName, newRating);

        }
      }
    }
  }

  /**
   * Gets the ready.
   * 
   * @return the ready
   */
  public Boolean getReady() {
    return ready;
  }

  /**
   * Sets the ready.
   * 
   * @param ready
   *          the ready to set
   */
  public void setReady(Boolean ready) {
    this.ready = ready;
  }

  // TODO perhaps it is better to remove this from the mother class
  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.model.AbstractScenario#getFeatureFieldName()
   */
  @Override
  public String getFeatureFieldName() {
    return this.getLabel();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SuitabilityScenario ["
        + (suitabilityRules != null ? "suitabilityRules=" + suitabilityRules
            + ", " : "")
        + (ready != null ? "ready=" + ready + ", " : "")
        + (getProjectId() != null ? "getProjectId()=" + getProjectId() + ", "
            : "")
        + (getDocType() != null ? "getDocType()=" + getDocType() + ", " : "")
        + (getId() != null ? "getId()=" + getId() : "") + "]";
  }

  /**
   * 
   */
  public SuitabilityScenario() {
    super();
    suitabilityRules = new HashSet<SuitabilityRule>();

  }

}
