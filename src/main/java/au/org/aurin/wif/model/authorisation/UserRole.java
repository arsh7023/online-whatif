/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.authorisation;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Maps authorisation for CRUD rights to projects. 
 *
 * @author marcosnr
 */
public class UserRole {

	/** The id. @uml.property name="id" */
	private Integer id;

	@JsonIgnore
	private Set <AuthorisationRight> authorisationRights;

	/** The roleName. @uml.property name="roleName" */
	private String roleName;

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the authorisationRights
	 */
	public Set <AuthorisationRight> getAuthorisationRights() {
		return authorisationRights;
	}

	/**
	 * @param authorisationRights the authorisationRights to set
	 */
	public void setAuthorisationRights(Set <AuthorisationRight> authorisationRights) {
		this.authorisationRights = authorisationRights;
	}

}
