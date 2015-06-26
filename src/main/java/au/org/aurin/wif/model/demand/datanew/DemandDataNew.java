package au.org.aurin.wif.model.demand.datanew;

public class DemandDataNew {

  private String ItemID;
  private String ItemLabel;
  private String ItemProjectionName;
  private String ItemYear;
  private Double ItemValue;

  public String getItemID() {
    return ItemID;
  }

  public void setItemID(String itemID) {
    ItemID = itemID;
  }

  public String getItemLabel() {
    return ItemLabel;
  }

  public void setItemLabel(String itemLabel) {
    ItemLabel = itemLabel;
  }

  public String getItemProjectionName() {
    return ItemProjectionName;
  }

  public void setItemProjectionName(String itemProjectionName) {
    ItemProjectionName = itemProjectionName;
  }

  public String getItemYear() {
    return ItemYear;
  }

  public void setItemYear(String itemYear) {
    ItemYear = itemYear;
  }

  public Double getItemValue() {
    return ItemValue;
  }

  public void setItemValue(Double itemValue) {
    ItemValue = itemValue;
  }

  public DemandDataNew() {
    super();
  }

}
