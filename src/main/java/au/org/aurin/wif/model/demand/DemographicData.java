package au.org.aurin.wif.model.demand;

import au.org.aurin.wif.model.Projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The Class DemographicData.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, 
property = "@class")
public class DemographicData {

  /**
   * Instantiates a new demographic data.
   */
  public DemographicData() {
    super();
  }

  /** The projection. */
  @JsonIgnore
  private Projection projection;

  /** The projection label. */
  private String projectionLabel;

  /** The demographic trend. */
  @JsonIgnore
  private DemographicTrend demographicTrend;

  /**
   * Gets the projection.
   *
   * @return the projection
   */
  public Projection getProjection() {
    return projection;
  }

  /**
   * Sets the projection.
   *
   * @param projection the projection to set
   */
  public void setProjection(Projection projection) {
    this.projection = projection;
  }

  /**
   * Gets the demographic trend.
   *
   * @return the demographicTrend
   */
  public DemographicTrend getDemographicTrend() {
    return demographicTrend;
  }

  /**
   * Sets the demographic trend.
   *
   * @param demographicTrend the demographicTrend to set
   */
  public void setDemographicTrend(DemographicTrend demographicTrend) {
    this.demographicTrend = demographicTrend;
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
