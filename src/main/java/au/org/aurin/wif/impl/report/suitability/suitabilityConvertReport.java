package au.org.aurin.wif.impl.report.suitability;

public class suitabilityConvertReport {

  private String SuName;
  private String LuName;
  private String isConvert;
  private String scenarioLabel;
  private String projectName;

  public String getSuName() {
    return SuName;
  }

  public void setSuName(final String suName) {
    SuName = suName;
  }

  public String getLuName() {
    return LuName;
  }

  public void setLuName(final String luName) {
    LuName = luName;
  }

  public String getIsConvert() {
    return isConvert;
  }

  public void setIsConvert(final String isConvert) {
    this.isConvert = isConvert;
  }

  public String getScenarioLabel() {
    return scenarioLabel;
  }

  public void setScenarioLabel(final String scenarioLabel) {
    this.scenarioLabel = scenarioLabel;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(final String projectName) {
    this.projectName = projectName;
  }

}
