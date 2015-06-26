package au.org.aurin.wif.model.suitability;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>FactorImportance.java</b> : Each factor (for instance "Slope") has
 * different ratings or weights according to its value. (for instance value
 * "<6%" of slope has a importance of 50)
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "factor" })
@JsonInclude(Include.NON_NULL)
public class FactorImportance {

  /** The suitability rules. */
  @JsonIgnore
  private SuitabilityRule suitabilityRule;

  /** The factor. */
  @JsonIgnore
  private Factor factor;

  /** The factor map. */
  @JsonIgnore
  private Map<String, String> factorMap;
  /**
   * The importance.
   * 
   * @uml.property name="importance"
   */
  private Double importance;

  /** The factor type ratings. */
  @JsonIgnore
  private Set<FactorTypeRating> factorTypeRatings;

  /**
   * Gets the importance.
   * 
   * @return the importance
   * @uml.property name="importance"
   */
  public Double getImportance() {
    return importance;
  }

  /**
   * Sets the importance.
   * 
   * @param rating
   *          the new importance
   * @uml.property name="importance"
   */
  public void setImportance(Double rating) {
    this.importance = rating;
  }

  /**
   * Sets the factor type ratings.
   * 
   * @param factorTypeRatings
   *          the new factor type ratings
   */
  @JsonProperty(value = "factorTypeRatings")
  public void setFactorTypeRatings(Set<FactorTypeRating> factorTypeRatings) {
    this.factorTypeRatings = factorTypeRatings;
  }

  /**
   * Gets the factor type ratings.
   * 
   * @return the factor type ratings
   */
  public Set<FactorTypeRating> getFactorTypeRatings() {
    return factorTypeRatings;
  }

  /**
   * Gets the factor type ratings.
   * 
   * @return the factor type ratings
   */
  @JsonProperty(value = "factorTypeRatings")
  public Set<FactorTypeRating> getFactorTypeRatingsSorted() {

    return factorTypeRatings;
  }

  /**
   * Sets the factor.
   * 
   * @param factor
   *          the new factor
   */
  public void setFactor(Factor factor) {
    this.factor = factor;
  }

  /**
   * Gets the factor.
   * 
   * @return the factor
   */
  public Factor getFactor() {
    return factor;
  }

  /**
   * Gets the Factor idLabelMap for JSON.
   * 
   * @return the Factor idLabelMap
   */
  @JsonProperty(value = "factor")
  public Map<String, String> getFactorMap() {
    return this.factorMap;
  }

  /**
   * Sets the factor idLabelMap for JSON.
   * 
   * @param map
   *          the id label map
   * @return the factor idLabelMap
   */
  @JsonProperty(value = "factor")
  public void setFactorMap(Map<String, String> map) {
    this.factorMap = map;
  }

  /**
   * Sets the suitability rules.
   * 
   * @param suitabilityRules
   *          the new suitability rules
   */
  public void setSuitabilityRule(SuitabilityRule suitabilityRules) {
    this.suitabilityRule = suitabilityRules;
  }

  /**
   * Gets the suitability rules.
   * 
   * @return the suitability rules
   */
  public SuitabilityRule getSuitabilityRule() {
    return suitabilityRule;
  }

  /**
   * Modify factor rating.
   * 
   * @param newFactorName
   *          the new factor name
   * @param newImportance
   *          the new importance
   * @param newFactorTypeName
   *          the new factor type name
   * @param newRating
   *          the new rating
   */
  public void modifyFactorRating(String newFactorName, String newImportance,
      String newFactorTypeName, String newRating) {
    if (this.getFactor().getLabel().equalsIgnoreCase(newFactorName)) {
      this.setImportance(Double.valueOf(newImportance));
      Set<FactorTypeRating> factorTypeRatings = this.getFactorTypeRatings();
      for (FactorTypeRating factorTypeRating : factorTypeRatings) {

        if (factorTypeRating.getFactorType().getLabel()
            .equalsIgnoreCase(newFactorTypeName)) {
          factorTypeRating.setScore(Double.valueOf(newRating));
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "FactorImportance ["
        + (factorMap != null ? "factorMap=" + factorMap + ", " : "")
        + (importance != null ? "importance=" + importance + ", " : "")
        + (factorTypeRatings != null ? "factorTypeRatings=" + factorTypeRatings
            : "") + "]";
  }

  /**
   * 
   */
  public FactorImportance() {
    super();
    factorTypeRatings = new HashSet<FactorTypeRating>();

    factorMap = new HashMap<String, String>();
  }

}
