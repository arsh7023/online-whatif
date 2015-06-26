package au.org.aurin.wif.model.demand.datanew;

import java.util.Set;

public class DemandEmpNew {

  private String SectorName;
  private Set<DemandDataNew> SectorData;

  public String getSectorName() {
    return SectorName;
  }

  public void setSectorName(String sectorName) {
    SectorName = sectorName;
  }

  public Set<DemandDataNew> getSectorData() {
    return SectorData;
  }

  public void setSectorData(Set<DemandDataNew> sectorData) {
    SectorData = sectorData;
  }

}
