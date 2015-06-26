/**
 * 
 */
package au.org.aurin.wif.model.reports;

import java.util.Date;

import org.springframework.stereotype.Component;

import au.org.aurin.wif.model.CouchDoc;

/**
 * The Class Report.
 * 
 * @author Marcos Nino-Ruiz
 */
@Component
public class Report extends CouchDoc {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 339893913465583236L;

  /** The project id. */
  private String projectId;

  /** The label. */
  private String label;

  /** The report type. */
  private String reportType;

  /** The creation date. */
  private Date creationDate;

  /** The printed date. */
  private Date printedDate;

  /**
   * Gets the project id.
   * 
   * @return the projectId
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Sets the project id.
   * 
   * @param projectUUID
   *          the new project id
   */
  public void setProjectId(String projectUUID) {
    this.projectId = projectUUID;
  }

  /**
   * Gets the label.
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the printed date.
   * 
   * @return the printedDate
   */
  public Date getPrintedDate() {
    return printedDate;
  }

  /**
   * Sets the printed date.
   * 
   * @param printedDate
   *          the printedDate to set
   */
  public void setPrintedDate(Date printedDate) {
    this.printedDate = printedDate;
  }

  /**
   * Gets the creation date.
   * 
   * @return the creationDate
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the creation date.
   * 
   * @param creationDate
   *          the creationDate to set
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets the report type.
   * 
   * @return the reportType
   */
  public String getReportType() {
    return reportType;
  }

  /**
   * Sets the report type.
   * 
   * @param reportType
   *          the reportType to set
   */
  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

}
