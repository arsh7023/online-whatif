/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The Class Projection.
 */
public class Projection {
  
  /** The wif project. */
	@JsonIgnore
	private WifProject wifProject;

	/** The year. */
	private Integer year;
	
	/** The label. */
	private String label;

	/**
	 * Gets the wif project.
	 *
	 * @return the wif project
	 */
	public WifProject getWifProject() {
		return this.wifProject;
	}

	/**
	 * Sets the wif project.
	 *
	 * @param wifProject the new wif project
	 */
	public void setWifProject(WifProject wifProject) {
		this.wifProject = wifProject;
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
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

}
