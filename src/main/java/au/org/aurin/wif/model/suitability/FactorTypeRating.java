package au.org.aurin.wif.model.suitability;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>FactorTypeRating.java</b> : Each factor (for instance "Slope") has
 * different ratings or weights according to its value. (for instance value
 * "<6%" of slope has a score of 50)
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "factorType" })
@JsonInclude(Include.NON_NULL)
public class FactorTypeRating {

  /** The factor importance. */
  @JsonIgnore
  private FactorImportance factorImportance;

  /** The factor type. */
  @JsonIgnore
  private FactorType factorType;

  /** The factor type map. */
  @JsonIgnore
  private Map<String, String> factorTypeMap;
  /**
   * The score.
   * 
   * @uml.property name="score"
   */
  private Double score;

  /**
   * Gets the score.
   * 
   * @return the score
   * @uml.property name="score"
   */
  public Double getScore() {
    return score;
  }

  /**
   * Sets the score.
   * 
   * @param rating
   *          the new score
   * @uml.property name="score"
   */
  public void setScore(Double rating) {
    this.score = rating;
  }

  /**
   * Gets the factor importance.
   * 
   * @return the suitabilityFactor
   */
  public FactorImportance getFactorImportance() {
    return this.factorImportance;
  }

  /**
   * Sets the factor importance.
   * 
   * @param factorImportance
   *          the new factor importance
   */
  public void setFactorImportance(FactorImportance factorImportance) {
    this.factorImportance = factorImportance;
  }

  /**
   * Sets the factor type.
   * 
   * @param factorType
   *          the new factor type
   */
  public void setFactorType(FactorType factorType) {
    this.factorType = factorType;
  }

  /**
   * Gets the factor type.
   * 
   * @return the factor type
   */
  public FactorType getFactorType() {
    return factorType;
  }

  /**
   * Gets the factorType idLabelMap for JSON.
   * 
   * @return the factorType idLabelMap
   */
  @JsonProperty(value = "factorType")
  public Map<String, String> getFactorTypeMap() {
    return factorTypeMap;
  }

  /**
   * Sets the factorType idLabelMap for JSON.
   * 
   * @param map
   *          the map
   * @return the factorType idLabelMap
   */
  @JsonProperty(value = "factorType")
  public void setFactorTypeMap(Map<String, String> map) {
    this.factorTypeMap = map;
  }

  /**
   * 
   */
  public FactorTypeRating() {
    super();
    factorTypeMap = new HashMap<String, String>();

  }
}
