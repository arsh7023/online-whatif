package au.org.aurin.wif.model.reports.suitability;

/**
 * The Enum SuitabilityCategory.
 */
public enum SuitabilityCategory {

  /** The low rank. */
  LOW(1),

  /** The medium low. */
  MEDIUM_LOW(2),

  /** The medium. */
  MEDIUM(3),

  /** The medium high. */
  MEDIUM_HIGH(4),

  /** The high rank. */
  HIGH(5);

  /** The rank. */
  private final int rank;

  /**
   * Instantiates a new suitability category.
   * 
   * @param rank
   *          the rank
   */
  SuitabilityCategory(int rank) {
    this.rank = rank;
  }

  /**
   * Gets the rank.
   * 
   * @return the rank
   */
  public int getRank() {
    return rank;
  }
}
