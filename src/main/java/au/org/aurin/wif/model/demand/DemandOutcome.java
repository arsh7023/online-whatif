/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.AbstractScenario;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>ManualDemandScenario.java</b> :
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class DemandOutcome extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2167463430566658863L;

  /**
   * The demand infos. @uml.property name="demandInfos" @uml.associationEnd
   * multiplicity="(0 -1)"s
   * inverse="demandScenario:au.org.aurin.wif.model.demand.DemandInfo"
   */
  private Set<AreaRequirement> areaRequirements;

  /** The demographic trend. */

  public DemandOutcome() {
    super();
    this.areaRequirements = new HashSet<AreaRequirement>();
  }

  /**
   * Gets the demand infos.
   * 
   * @return the demandInfos
   */
  public Set<AreaRequirement> getAreaRequirements() {
    return this.areaRequirements;
  }

  /**
   * Sets the demand infos.
   * 
   * @param demandInfos
   *          the demandInfos to set
   */
  public void setAreaRequirements(final Set<AreaRequirement> AreaRequirement) {
    this.areaRequirements = AreaRequirement;
  }

  /**
   * Adds the demand info.
   * 
   * @param demandInfos
   *          the demand infos
   */
  public void addAreaRequirenment(final AreaRequirement AreaRequirement) {
    this.areaRequirements.add(AreaRequirement);
  }

}
