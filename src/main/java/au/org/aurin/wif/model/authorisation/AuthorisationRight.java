/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.authorisation;

import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Maps authorisation for CRUD rights to projects. 
 *
 * @author marcosnr
 */
public class AuthorisationRight {

	/** The id. @uml.property name="id" */
	private Integer id;

	@JsonIgnore
	private WifProject wifProject;

	  @JsonIgnore
	  private UserRole userRole;
	 
	/** The rights. @uml.property name="rights" */
	private String rights;

	/**
	 * @return the rights
	 */
	public String getRights() {
		return rights;
	}

	/**
	 * @param rights the rights to set
	 */
	public void setRights(String rights) {
		this.rights = rights;
	}

	/**
	 * @return the wifProject
	 */
	public WifProject getWifProject() {
		return wifProject;
	}

	/**
	 * @param wifProject the wifProject to set
	 */
	public void setWifProject(WifProject wifProject) {
		this.wifProject = wifProject;
	}

	/**
	 * @return the userRole
	 */
	public UserRole getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

}
