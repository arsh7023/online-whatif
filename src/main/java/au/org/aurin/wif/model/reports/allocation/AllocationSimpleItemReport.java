package au.org.aurin.wif.model.reports.allocation;

/*Simple Allocation Report Items*/
public class AllocationSimpleItemReport {

  private String landuseName;
  private int year;
  private double sumofArea;

  public String getLanduseName() {
    return landuseName;
  }

  public void setLanduseName(String landuseName) {
    this.landuseName = landuseName;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public double getSumofArea() {
    return sumofArea;
  }

  public void setSumofArea(double sumofArea) {
    this.sumofArea = sumofArea;
  }

}
