/*
 *
 */
package au.org.aurin.wif.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>AbstractLandUse.java</b> : Land uses that will dictate which factors and
 * the corresponding weights are applied. for instance: Residential, Industry,
 * etc. It will not be instantiated as an entity, just to aggregate common
 * inheritance
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label" })
public class AbstractLandUse extends ProjectCouchDoc implements Model2JsonMapped {

  /**
   * 
   */
  private static final long serialVersionUID = 3037808887881179425L;

  /**
	 * 
	  /** The wif project. @uml.property name="wifProject_fk" */
  @JsonIgnore
  private WifProject wifProject;

  /**
   * The name.
   * 
   * @uml.property name="name"
   */
  private String featureFieldName;

  /** Helper attribute to deal with Jackson mapping. */
  @JsonIgnore
  private Map<Integer, String> projectMap;

  /**
   * Gets theprojectMap for JSON.
   * 
   * @return the theprojectMap
   */
  public Map<Integer, String> getProjectMap() {
    return this.projectMap;
  }

  /**
   * Sets the parentProject for JSON.
   * 
   * @return the parentProject idLabelMap
   */
  public void setProjectMap(Map<Integer, String> idLabelMap) {
    this.projectMap = idLabelMap;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   * @uml.property name="name"
   */
  public String getFeatureFieldName() {
    return featureFieldName;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name to set
   * @uml.property name="name"
   */
  public void setFeatureFieldName(String name) {
    this.featureFieldName = name;
  }

  /**
   * Gets the wif project.
   * 
   * @return the wifProject
   */
  public WifProject getWifProject() {
    return this.wifProject;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the wifProject to set
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }
}
