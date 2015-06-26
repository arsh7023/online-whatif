package au.org.aurin.wif.model.reports.allocation;

import java.util.Set;

import au.org.aurin.wif.model.reports.ScenarioReport;

public class AllocationSimpleAnalysisReport extends ScenarioReport {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -27923554363458781L;

  private Set<AllocationSimpleItemReport> allocationSimpleItemReport;

  public Set<AllocationSimpleItemReport> getAllocationSimpleItemReport() {
    return allocationSimpleItemReport;
  }

  public void setAllocationSimpleItemReport(
      Set<AllocationSimpleItemReport> allocationSimpleItemReport) {
    this.allocationSimpleItemReport = allocationSimpleItemReport;
  }

}
