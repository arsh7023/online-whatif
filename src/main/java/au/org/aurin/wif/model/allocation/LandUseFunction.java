package au.org.aurin.wif.model.allocation;

/**
 * Enumeration describing the land use function. This is based
 * on the LBCS standard, function dimension. What-if only
 * uses the coarsest level of classification.
 */
public enum LandUseFunction {
  
  /** Residence or accommodation */
  LBCS_1XXX (1), 
  /** General sales or services */
  LBCS_2XXX (2),
  /** Manufacturing and wholesale trade */
  LBCS_3XXX (3),
  /** Transportation, communication, information, and utilities */
  LBCS_4XXX (4),
  /** Arts, entertainment, and recreation */
  LBCS_5XXX (5),
  /** Education, public administration, health care, and other institutions */
  LBCS_6XXX (6),
  /** Construction-related businesses */
  LBCS_7XXX (7),
  /** Mining and extraction establishments */
  LBCS_8XXX (8),
  /** Agriculture, forestry, fishing, and hunting */
  LBCS_9XXX (9),

  /** Non-developable or unknown use
   * 
   * This partly overlaps LBCS 99XX, but What-if treats it 
   * as a separate concept.
   */
  NOT_DEVELOPABLE_OR_UNDEFINED (0); // XXX This partly overlaps LBCS_99XX.
  private final int value;

  LandUseFunction(int value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public int getValue() {
    return value;
  }
}