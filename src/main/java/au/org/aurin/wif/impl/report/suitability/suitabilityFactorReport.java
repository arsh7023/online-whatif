package au.org.aurin.wif.impl.report.suitability;

public class suitabilityFactorReport {

  private String LUName;
  private String factorName;
  private Double fatorValue;
  private String isHead;
  private String scenarioLabel;
  private String projectName;

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

  public String getLUName() {
    return LUName;
  }

  public void setLUName(final String lUName) {
    LUName = lUName;
  }

  public String getFactorName() {
    return factorName;
  }

  public void setFactorName(final String factorName) {
    this.factorName = factorName;
  }

  public Double getFatorValue() {
    return fatorValue;
  }

  public void setFatorValue(final Double fatorValue) {
    this.fatorValue = fatorValue;
  }

  public String getIsHead() {
    return isHead;
  }

  public void setIsHead(final String isHead) {
    this.isHead = isHead;
  }

}
