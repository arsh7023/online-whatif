/*
 *
 */
package au.org.aurin.wif.model.allocation;

import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>PlanneAdLU.java</b> :These land uses are used in allocation each existing
 * land use can be in one or more Planned land use.
 * 
 */
public class ColorALU {

  /** The label of the field */
  private String label;

  private String associatedColors;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

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
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(final WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * gets the label.
   */

  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   */

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getAssociatedColors() {
    return associatedColors;
  }

  public void setAssociatedColors(final String associatedColors) {
    this.associatedColors = associatedColors;
  }

}
