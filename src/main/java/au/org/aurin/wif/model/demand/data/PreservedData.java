package au.org.aurin.wif.model.demand.data;


/**
 * The Class PreservedData.
 */
public class PreservedData extends ProjectedData {

  /** The reserved area. */
  private Double reservedArea;

  /**
   * Instantiates a new preserved data.
   *
   * @param copy the copy
   */
  public PreservedData(PreservedData copy) {
    super(copy);
    this.reservedArea = copy.getReservedArea();
  }

  /**
   * Instantiates a new preserved data.
   */
  public PreservedData() {
  }

  /**
   * Sets the reserved area.
   * 
   * @param reservedArea
   *          the employees to set
   */
  public void setReservedArea(Double reservedArea) {
    this.reservedArea = reservedArea;
  }

  /**
   * Gets the reserved area.
   * 
   * @return the employees
   */
  public Double getReservedArea() {
    return reservedArea;
  }

}
