/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.allocation.control;


/**
 * The Class InfrastructureType.
 *
 * @author marcosnr
 */
public class InfrastructureType {

  /** The id. @uml.property reference="id" */
  private Integer id;
  
  /** TODO Cardinality is not assured, just for now. @uml.property reference="infrastructurePlan" @uml.associationEnd inverse= "infrastructureType:au.org.aurin.wif.model.allocation.InfrastructurePlan" */
  private InfrastructurePlan infrastructurePlan;

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
   * Getter of the property <tt>infrastructurePlan</tt>.
   *
   * @return Returns the infrastructurePlan.
   * @uml.property reference="infrastructurePlan"
   */
  public InfrastructurePlan getInfrastructurePlan() {
    return infrastructurePlan;
  }

  /**
   * Setter of the property <tt>infrastructurePlan</tt>.
   *
   * @param infrastructurePlan The infrastructurePlan to set.
   * @uml.property reference="infrastructurePlan"
   */
  public void setInfrastructurePlan(InfrastructurePlan infrastructurePlan) {
    this.infrastructurePlan = infrastructurePlan;
  }

}
