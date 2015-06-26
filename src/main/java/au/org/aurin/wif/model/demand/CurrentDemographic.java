/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentData;
import au.org.aurin.wif.model.demand.info.ResidentialCurrentData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <b>CurrentDemographic.java</b> : Current demographic
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonInclude(Include.NON_NULL)
public class CurrentDemographic {

  /** The label. @uml.property name="label" */
  private String label;

  /** The year. */
  private Integer year;

  /** The ResidentialCurrentData. */
  private Set<ResidentialCurrentData> residentialCurrentData;

  /** The EmploymentCurrentData. */
  private Set<EmploymentCurrentData> employmentCurrentDatas;

  /** The total population. TOTPOP_CY */
  private Long totalPopulation;

  /** The housing units. TOTHU_CY */
  private Long housingUnits;

  /** The g q population. GQPOP_CY */
  private Long gQPopulation;

  /** The households. TOTHH_CY */
  private Double households;

  /** The vacant land. VACANT_CY */
  private Double vacantLand;

  /** The vacancy Rate */
  private Double vacancyRate;

  public void setVacancyRate(final Double vacancyRate) {
    this.vacancyRate = vacancyRate;
  }

  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  /**
   * Gets the year.
   * 
   * @return the year
   */
  public Integer getYear() {
    return year;
  }

  /**
   * Sets the year.
   * 
   * @param year
   *          the year to set
   */
  public void setYear(final Integer year) {
    this.year = year;
  }

  /**
   * Gets the residential current data.
   * 
   * @return the residentialCurrentData
   */
  public Set<ResidentialCurrentData> getResidentialCurrentData() {
    return residentialCurrentData;
  }

  /**
   * Sets the residential current data.
   * 
   * @param currentData
   *          the new residential current data
   */
  public void setResidentialCurrentData(
      final Set<ResidentialCurrentData> currentData) {
    this.residentialCurrentData = currentData;
  }

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

  /**
   * Gets the vacancy rate.
   * 
   * @return the vacancyRate
   */
  public Double getVacancyRate() {
    if (vacancyRate == null) {
      return getVacantLand() / getHousingUnits();
    }
    return vacancyRate;
  }

  /*
   * @JsonIgnore public Double getVacancyRate() { return getVacantLand() /
   * getHousingUnits(); }
   */

  /**
   * Gets the average household size. //TODO ccheck science behind changing the
   * average household size without group quarters population //FIXME duplicated
   * code with ResidentialDemographicData
   * 
   * @return the avgHHSize
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @JsonIgnore
  public Double getAverageHouseholdSize() throws WifInvalidInputException {
    if ((this.getHouseholds() != null) && (this.getgQPopulation() != null)) {
      if (this.getHouseholds() != 0) {
        return ((double) this.getTotalPopulation() - ((double) this
            .getgQPopulation())) / this.getHouseholds();
      }
    } else if (this.getHouseholds() != null) {
      if (this.getHouseholds() != 0) {
        return ((double) this.getTotalPopulation()) / this.getHouseholds();
      }
    } else {
      throw new WifInvalidInputException("Households number is invalid");
    }
    return 0.0;
  }

  /**
   * Gets the employment current datas.
   * 
   * @return the employmentCurrentDatas
   */
  public Set<EmploymentCurrentData> getEmploymentCurrentDatas() {
    return employmentCurrentDatas;
  }

  /**
   * Sets the employment current datas.
   * 
   * @param employmentCurrentData
   *          the new employment current datas
   */
  public void setEmploymentCurrentDatas(
      final Set<EmploymentCurrentData> employmentCurrentData) {
    this.employmentCurrentDatas = employmentCurrentData;
  }

  /**
   * Gets the employees.
   * 
   * @param employmentSector
   *          the employment sector
   * @return the employees
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public Long getEmployees(final EmploymentSector employmentSector)
      throws WifInvalidInputException {
    final Set<EmploymentCurrentData> datas = getEmploymentCurrentDatas();
    for (final EmploymentCurrentData employmentCurrentData : datas) {
      if (employmentCurrentData.getSectorLabel().equals(
          employmentSector.getLabel())) {
        return employmentCurrentData.getEmployees();
      }
    }
    throw new WifInvalidInputException(employmentSector.getLabel()
        + " sector not assigned");
  }

}
