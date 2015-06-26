package au.org.aurin.wif.model.reports.suitability;


/**
 * The Class CategoryItem.
 */
public class CategoryItem {
  
  /** The category. */
  private String category;
  
  /** The score range. */
  private String scoreRange;
  
  /** The area. */
  private Double area;

  /**
   * Gets the category.
   *
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the category.
   *
   * @param category the new category
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets the area.
   *
   * @return the area
   */
  public Double getArea() {
    return area;
  }

  /**
   * Sets the area.
   *
   * @param area the new area
   */
  public void setArea(Double area) {
    this.area = area;
  }

  /**
   * Gets the score range.
   *
   * @return the score range
   */
  public String getScoreRange() {
    return scoreRange;
  }

  /**
   * Sets the score range.
   *
   * @param scoreRange the new score range
   */
  public void setScoreRange(String scoreRange) {
    this.scoreRange = scoreRange;
  }
}
