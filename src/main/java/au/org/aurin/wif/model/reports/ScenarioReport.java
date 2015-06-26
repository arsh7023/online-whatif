/**
 * 
 */
package au.org.aurin.wif.model.reports;

import org.springframework.stereotype.Component;

/**
 * The Class ScenarioReport.
 * 
 * @author Marcos Nino-Ruiz
 */
@Component
public class ScenarioReport extends Report {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5500190908679921075L;

  /** The scenario label. */
  private String scenarioLabel;

  public ScenarioReport() {

  }

  /**
   * Gets the scenario label.
   * 
   * @return the scenarioLabel
   */
  public String getScenarioLabel() {
    return scenarioLabel;
  }

  /**
   * Sets the scenario label.
   * 
   * @param scenarioLabel
   *          the scenarioLabel to set
   */
  public void setScenarioLabel(String scenarioLabel) {
    this.scenarioLabel = scenarioLabel;
  }

}
