package au.org.aurin.wif.model.reports.suitability;

import java.util.Set;

/**
 * The Class SuitabilityAnalysisItem.
 * 
 */
public class SuitabilityAnalysisItem {

  /** The suitability lu label. */
  private String suitabilityLULabel;
  
  /** The total area. */
  private Double totalArea;
  
  /** The suitability categories. */
  private Set<CategoryItem> suitabilityCategories;

  /**
   * Gets the suitability lu label.
   *
   * @return the suitabilityLULabel
   */
  public String getSuitabilityLULabel() {
    return suitabilityLULabel;
  }

  /**
   * Sets the suitability lu label.
   *
   * @param suitabilityLULabel the suitabilityLULabel to set
   */
  public void setSuitabilityLULabel(String suitabilityLULabel) {
    this.suitabilityLULabel = suitabilityLULabel;
  }

  /**
   * Gets the total area.
   *
   * @return the totalArea
   */
  public Double getTotalArea() {
    return totalArea;
  }

  /**
   * Sets the total area.
   *
   * @param totalArea the totalArea to set
   */
  public void setTotalArea(Double totalArea) {
    this.totalArea = totalArea;
  }

  /**
   * Gets the suitability categories.
   *
   * @return the suitabilityCategories
   */
  public Set<CategoryItem> getSuitabilityCategories() {
    return suitabilityCategories;
  }

  /**
   * Sets the suitability categories.
   *
   * @param suitabilityCategories the suitabilityCategories to set
   */
  public void setSuitabilityCategories(Set<CategoryItem> suitabilityCategories) {
    this.suitabilityCategories = suitabilityCategories;
  }
}
