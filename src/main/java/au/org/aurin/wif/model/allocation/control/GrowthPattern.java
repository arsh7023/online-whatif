/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation.control;


/**
 * Defining Growth Patterns [Data hardcoded in interface?] Can be used to select
 * options which will determine the general spatial pattern for future growth.
 * For instance, the Concentric growth option assumes that, other things being
 * equal, the region will grow in a series of concentric rings from Central
 * City, located to the southeast of Edge City. The Radial growth option assumes
 * that, other things being equal, the region will grow in a radial pattern
 * outward from the region’s major roads.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class GrowthPattern {

  /** The id. @uml.property name="id" */
  private Integer id;
  
  /** The control scenario. @uml.property name="the controlScenario" */
  private ControlScenario controlScenario;
  
  /** The label. @uml.property name="label" */
  private String label;

  /** The priority. @uml.property name="priority" */
  private Integer priority;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public Integer getId() {
    return this.id;
  }

  /**
   * Sets the id.
   *
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the control scenario.
   *
   * @return the controlScenario
   */
  public ControlScenario getControlScenario() {
    return this.controlScenario;
  }

  /**
   * Sets the control scenario.
   *
   * @param controlScenario the controlScenario to set
   */
  public void setControlScenario(ControlScenario controlScenario) {
    this.controlScenario = controlScenario;
  }

  /**
   * Getter of the property <tt>label</tt>.
   *
   * @return Returns the label.
   * @uml.property name="label"
   */
  public String getLabel() {
    return label;
  }

  /**
   * Setter of the property <tt>label</tt>.
   *
   * @param label The label to set.
   * @uml.property name="label"
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Getter of the property <tt>priority</tt>.
   *
   * @return Returns the priority.
   * @uml.property name="priority"
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Setter of the property <tt>priority</tt>.
   *
   * @param priority The priority to set.
   * @uml.property name="priority"
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

}
