/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>ResidentialPastTrendInfo.java</b> : Past trends that hold information
 * necessary to produce demographic trends automatically.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class ResidentialPastTrendInfo extends PastTrendInfo {

  /** The total population. */
  private Long totalPopulation;

  /** The housing units. */
  private Long housingUnits;

  /** The households. */
  private Long households;

  /** The g q population. */
  private Long gQPopulation;

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
  public void setTotalPopulation(Long totalPopulation) {
    this.totalPopulation = totalPopulation;
  }

  /**
   * Gets the housing units.
   * 
   * @return the housingUnits
   */
  public Long getHousingUnits() {
    return housingUnits;
  }

  /**
   * Sets the housing units.
   * 
   * @param housingUnits
   *          the housingUnits to set
   */
  public void setHousingUnits(Long housingUnits) {
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
  public void setgQPopulation(Long gQPopulation) {
    this.gQPopulation = gQPopulation;
  }

  /**
   * Gets the average household size.
   * 
   * @return the avgHHSize
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @JsonIgnore
  public Double getAverageHouseholdSize() throws WifInvalidInputException {
    if (this.getHouseholds() != null) {
      if (this.getHouseholds() != 0) {
        return ((double) this.getTotalPopulation() - ((double) this
            .getgQPopulation())) / this.getHouseholds();
      }
    } else {
      throw new WifInvalidInputException("Households number is invalid");
    }
    return 0.0;
  }

  /**
   * Gets the households.
   * 
   * @return the households
   */
  public Long getHouseholds() {
    return households;
  }

  /**
   * Sets the households.
   * 
   * @param households
   *          the households to set
   */
  public void setHouseholds(Long households) {
    this.households = households;
  }

}
