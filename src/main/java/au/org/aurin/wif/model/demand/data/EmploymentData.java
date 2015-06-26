package au.org.aurin.wif.model.demand.data;

import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class EmploymentData.
 */
public class EmploymentData extends ProjectedData {

  /** The employment info. */
	@JsonIgnore
	private EmploymentDemandInfo employmentInfo;
	
	/** The employees. */
	private Integer employees;
	
	/**
	 * Instantiates a new employment data.
	 *
	 * @param copy the copy
	 */
	public EmploymentData(EmploymentData copy) {
	    super(copy);
	    this.setEmployees(copy.getEmployees());
	    this.employmentInfo = copy.getEmploymentInfo();
	  }

	  /**
  	 * Instantiates a new employment data.
  	 */
  	public EmploymentData() {
	  }
	  
	/**
	 * Sets the employment info.
	 *
	 * @param edInfo the new employment info
	 */
	public void setEmploymentInfo(EmploymentDemandInfo edInfo) {
		this.employmentInfo = edInfo;
	}

	/**
	 * Gets the employment info.
	 *
	 * @return the employmentInfo
	 */
	public EmploymentDemandInfo getEmploymentInfo() {
		return employmentInfo;
	}

  /**
   * Gets the employees.
   *
   * @return the employees
   */
  public Integer getEmployees() {
    return employees;
  }

  /**
   * Sets the employees.
   *
   * @param employees the employees to set
   */
  public void setEmployees(Integer employees) {
    this.employees = employees;
  }

}
