/**
 * 
 */
package au.org.aurin.wif.model;


/**
 * @author Marcos Nino-Ruiz
 * 
 */
public class ProjectCouchDoc extends CouchDoc {
  /**
   * 
   */
  private static final long serialVersionUID = -5694707862028236733L;
  private String projectId;
  
  /** The label. */
  private String label;

  /**
   * @return the projectId
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * @param projectId
   *          the projectId to set
   */
  public void setProjectId(String projectUUID) {
    this.projectId = projectUUID;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

}
