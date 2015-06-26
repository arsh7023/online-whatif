/**
 * 
Most of the click * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

/**
 * It holds the residential demographic information of the demand scenario that
 * doesn't change for each land use, it is global for a given projection year.
 * 
 * @author marcosnr
 */
public class ResidentialDemographicData extends DemographicData {

  /** The total population. */
  private Long totalPopulation;

  /** The housing units. */
  private Long housingUnits;

  /** The g q population. */
  private Long gQPopulation;

  /** The average household size. */
  private Double averageHouseholdSize;

  /** The households. */
  private Double households;

  /** The vacant land. */
  private Double vacantLand;

  /**
   * Gets the total population.
   * 
   * @return the totalPopulation
   */
  public Long getTotalPopulation() {
    return totalPopulation;
  }

  /**
   * Sets the total population.
   * 
   * @param totalPopulation
   *          the totalPopulation to set
   */
  public void setTotalPopulation(final Long totalPopulation) {
    this.totalPopulation = totalPopulation;
  }

  /**
   * Gets the housing units.
   * 
   * @return the housingUnits
   */
  public Long getHousingUnits() {
    if (housingUnits == null) {
      // FIXME FInd out with expert
      return 0L;
    }
    return housingUnits;
  }

  /**
   * Sets the housing units.
   * 
   * @param housingUnits
   *          the housingUnits to set
   */
  public void setHousingUnits(final Long housingUnits) {

    this.housingUnits = housingUnits;
  }

  /**
   * Gets the g q population.
   * 
   * @return the gQPopulation
   */
  public Long getgQPopulation() {
    return gQPopulation;
  }

  /**
   * Sets the g q population.
   * 
   * @param gQPopulation
   *          the gQPopulation to set
   */
  public void setgQPopulation(final Long gQPopulation) {
    this.gQPopulation = gQPopulation;
  }

  /**
   * Gets the average household size.
   * 
   * @return the averageHouseholdSize
   */
  public Double getAverageHouseholdSize() {
    if ((this.getHouseholds() != null) && (this.getgQPopulation() != null)) {
      if (this.getHouseholds() != 0) {
        return ((double) this.getTotalPopulation() - ((double) this
            .getgQPopulation())) / this.getHouseholds();
      }
    } else if (this.getHouseholds() != null) {
      if (this.getHouseholds() != 0) {
        return ((double) this.getTotalPopulation()) / this.getHouseholds();
      }
    }
    return averageHouseholdSize;
  }

  /**
   * Sets the average household size.
   * 
   * @param averageHouseholdSize
   *          the averageHouseholdSize to set
   */
  public void setAverageHouseholdSize(final Double averageHouseholdSize) {
    this.averageHouseholdSize = averageHouseholdSize;
  }

  /**
   * Gets the households.
   * 
   * @return the households
   */
  public Double getHouseholds() {
    return households;
  }

  /**
   * Sets the households.
   * 
   * @param households
   *          the households to set
   */
  public void setHouseholds(final Double households) {
    this.households = households;
  }

  /**
   * Gets the vacant land.
   * 
   * @return the vacantLand
   */
  public Double getVacantLand() {
    return vacantLand;
  }

  /**
   * Sets the vacant land.
   * 
   * @param vacantLand
   *          the vacantLand to set
   */
  public void setVacantLand(final Double vacantLand) {
    this.vacantLand = vacantLand;
  }
}
