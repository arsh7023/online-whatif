/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**FOR couuch database
 * A projected year in the future, for instance, in demand you can Select a set
 * of projections for the total population, the group quarters population, and
 * average household size for each future year (prefilled with previous
 * projections made).
 *
 * @author marcosnr
 */
public class Project {

	@JsonIgnore
    private String id;

	@JsonIgnore
	private String revision;

	/** The year. */
	private Integer year;
	
	/** The label. @uml.property name="label" */
	private String label;

	/** The allocation label. */
	private String allocationLabel;

	public String getId() {
		return id;
	}

	 @JsonProperty("_id")
	public void setId(String id) {
		this.id = id;
	}

    @JsonProperty("_rev")
	public String getRevision() {
		return revision;
	}

    @JsonProperty("_rev")
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAllocationLabel() {
		return allocationLabel;
	}

	public void setAllocationLabel(String allocationLabel) {
		this.allocationLabel = allocationLabel;
	}

}
