package au.org.aurin.wif.model.suitability;

import au.org.aurin.wif.model.CouchDoc;
import au.org.aurin.wif.model.Model2JsonMapped;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>FactorType.java</b> : Each factor (for instance "Slope") has different
 * ratings or weights according to its value. (for instance value "<6%" of slope
 * has a score of 50)
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label", "value", "docType" })
public class FactorType extends CouchDoc implements Model2JsonMapped  {

	/** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2081699448713446096L;

  /** The factor. @uml.property name="factor" */
	@JsonIgnore
	private Factor factor;

  /** The factor id. */
  private String factorId;

	/**
	 * The value.
	 * 
	 * @uml.property name="value"
	 */
	private String value;

	/** The label. */
	private String label;

	/** The naturalOrder. */
	private Integer naturalOrder;

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 * @uml.property name="value"
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @uml.property name="value"
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the suitability factor.
	 * 
	 * @return the factor
	 */
	public Factor getFactor() {
		return this.factor;
	}

	/**
	 * Sets the suitability factor.
	 * 
	 * @param factor
	 *            the factor to set
	 */
	public void setFactor(Factor factor) {
		this.factor = factor;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * Gets the natural order.
	 *
	 * @return the naturalOrder
	 */
	public Integer getNaturalOrder() {
		return naturalOrder;
	}

	/**
	 * Sets the natural order.
	 *
	 * @param naturalOrder the naturalOrder to set
	 */
	public void setNaturalOrder(Integer naturalOrder) {
		this.naturalOrder = naturalOrder;
	}

  /**
   * Gets the factor id.
   *
   * @return the factorId
   */
  public String getFactorId() {
    return factorId;
  }

  /**
   * Sets the factor id.
   *
   * @param factorUUID the new factor id
   */
  public void setFactorId(String factorUUID) {
    this.factorId = factorUUID;
  }
}
