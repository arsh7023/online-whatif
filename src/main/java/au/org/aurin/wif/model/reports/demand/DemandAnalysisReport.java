/**
 * 
 */
package au.org.aurin.wif.model.reports.demand;

import java.util.ArrayList;
import java.util.List;

import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.reports.ScenarioReport;

/**
 * The Class SuitabilityAnalysisReport. In summary, to perform a demand scenario
 * analysis you must have the following setup (see What if demand assumptions
 * report) : - What If suitability setup complete. - Current demographic data
 * (2005), global to the project. - Need to have a demographic trend with
 * demographic data (population, current density etc) for the projection years,
 * in this case there is information only for 2010 and 2015. This information is
 * global to the project. - Projected data specific to each scenario (like
 * future breakdown of all units and future density). You can differentiate this
 * information from the global information, because in the What If desktop
 * program, it is grayed out and you cannot modify it when you are in the demand
 * scenario specific screen! - This analysis will create an area requirement for
 * each land use in each future projection year.
 * 
 * @author Marcos Nino-Ruiz
 */
public class DemandAnalysisReport extends ScenarioReport {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2793636824302358781L;
  private List<AreaRequirement> outcome;

  /**
   * 
   */
  public DemandAnalysisReport() {
    super();
    outcome = new ArrayList<AreaRequirement>();
  }

  /**
   * @return the outcome
   */
  public List<AreaRequirement> getOutcome() {
    return outcome;
  }

  /**
   * @param outcome
   *          the outcome to set
   */
  public void setOutcome(List<AreaRequirement> outcome) {
    this.outcome = outcome;
  }
}
