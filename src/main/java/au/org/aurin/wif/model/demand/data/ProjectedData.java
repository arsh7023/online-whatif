package au.org.aurin.wif.model.demand.data;

import au.org.aurin.wif.model.Projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class ProjectedData.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, 
property = "@class")
public class ProjectedData {

  /**
   * Instantiates a new projected data.
   */
  public ProjectedData() {
    super();
  }

  /** The projection. */
  @JsonIgnore
  private Projection projection;
  
  /** The projection label. */
  private String projectionLabel;

  /**
   * Instantiates a new projected data.
   *
   * @param copy the copy
   */
  public ProjectedData(ProjectedData copy) {
    this.projection = copy.getProjection();
  }

  /**
   * Sets the projection.
   * 
   * @param projection
   *          the projection to set
   */
  public void setProjection(Projection projection) {
    this.projection = projection;
  }

  /**
   * Gets the projection.
   * 
   * @return the projection
   */
  public Projection getProjection() {
    return projection;
  }

  /**
   * Gets the projection label.
   *
   * @return the projectionLabel
   */
  public String getProjectionLabel() {
    return projectionLabel;
  }

  /**
   * Sets the projection label.
   *
   * @param projectionLabel the projectionLabel to set
   */
  public void setProjectionLabel(String projectionLabel) {
    this.projectionLabel = projectionLabel;
  }

}
