package au.org.aurin.wif.model.reports.allocation;

/*Simple Allocation Report Items*/
public class AllocationSimpleItemReport {

  private String landuseName;
  private int year;
  private double sumofArea;
  private double sumofPreviousArea;

  public String getLanduseName() {
    return landuseName;
  }

  public void setLanduseName(final String landuseName) {
    this.landuseName = landuseName;
  }

  public int getYear() {
    return year;
  }

  public void setYear(final int year) {
    this.year = year;
  }

  public double getSumofArea() {
    return sumofArea;
  }

  public void setSumofArea(final double sumofArea) {
    this.sumofArea = sumofArea;
  }

  public double getSumofPreviousArea() {
    return sumofPreviousArea;
  }

  public void setSumofPreviousArea(final double sumofPreviousArea) {
    this.sumofPreviousArea = sumofPreviousArea;
  }

}
