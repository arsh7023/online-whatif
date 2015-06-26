package au.org.aurin.wif.model.demand.info;

public class DensityDemandInfo {

  /** The current density. */
  private Double currentDensity;

  /** The future density. */
  private Double futureDensity;

  /** The infill rate. */
  private Double infillRate;

  /** The sector label. */
  private String landuseID;

  public String getLanduseName() {
    return landuseName;
  }

  public void setLanduseName(final String landuseName) {
    this.landuseName = landuseName;
  }

  private String landuseName;

  public Double getCurrentDensity() {
    return currentDensity;
  }

  public void setCurrentDensity(final Double currentDensity) {
    this.currentDensity = currentDensity;
  }

  public Double getFutureDensity() {
    return futureDensity;
  }

  public void setFutureDensity(final Double futureDensity) {
    this.futureDensity = futureDensity;
  }

  public Double getInfillRate() {
    return infillRate;
  }

  public void setInfillRate(final Double infillRate) {
    this.infillRate = infillRate;
  }

  public String getLanduseID() {
    return landuseID;
  }

  public void setLanduseID(final String landuseID) {
    this.landuseID = landuseID;
  }

}
