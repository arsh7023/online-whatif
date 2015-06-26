package au.org.aurin.wif.model.reports;

import java.util.ArrayList;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component
public class BirtReport {

  private String scenarioName;
  private String projectName;

  private ArrayList<String[]> data;
  private Properties props;

  public Properties getProps() {
    return props;
  }

  public void setProps(Properties props) {
    this.props = props;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  public void setScenarioName(String scenarioName) {
    this.scenarioName = scenarioName;
  }

  public ArrayList<String[]> getData() {
    return data;
  }

  public void setData(ArrayList<String[]> data) {
    this.data = data;
  }

  public int size() {
    return this.data.size();
  }

}
