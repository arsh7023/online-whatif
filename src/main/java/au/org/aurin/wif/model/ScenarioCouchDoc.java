/**
 * 
 */
package au.org.aurin.wif.model;


/**
 * @author Marcos Nino-Ruiz
 * 
 */
public class ScenarioCouchDoc extends CouchDoc {
  /**
   * 
   */
  private static final long serialVersionUID = -8513278895771743495L;
  /**
   * 
   */
  private String scenarioId;

  /**
   * @return the scenarioId
   */
  public String getScenarioId() {
    return scenarioId;
  }

  /**
   * @param scenarioId
   *          the scenarioId to set
   */
  public void setScenarioId(String projectUUID) {
    this.scenarioId = projectUUID;
  }

}
