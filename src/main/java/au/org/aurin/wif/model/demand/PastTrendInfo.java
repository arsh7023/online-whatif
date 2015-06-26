/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <b>PastTrendInfo.java</b> : Past trends that hold information necessary to
 * produce demographic trends automatically.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonInclude(Include.NON_NULL)
public class PastTrendInfo {

  /** The demand config. */
  @JsonIgnore
  private DemandConfig demandConfig;

  /** The label. @uml.property name="label" */
  private String label;

  /** The year. */
  private Integer year;

  /**
   * Gets the demand config.
   *
   * @return the demandConfig
   */
  public DemandConfig getDemandConfig() {
    return demandConfig;
  }

  /**
   * Sets the demand config.
   *
   * @param demandConfig the demandConfig to set
   */
  public void setDemandConfig(DemandConfig demandConfig) {
    this.demandConfig = demandConfig;
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
   * @param label the label to set
   */
  public void setLabel(String label) {
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
   * @param year the year to set
   */
  public void setYear(Integer year) {
    this.year = year;
  }

}
