package au.org.aurin.wif.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <b>AbstractScenario.java</b> : Configure what if scenarios, basic
 * functionality. It will not be instantiated as an entity, just to aggregate
 * common inheritance
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class AbstractScenario extends ProjectCouchDoc implements
    Model2JsonMapped {

  /**
   * 
   */
  private static final long serialVersionUID = 5255624039447975648L;

  /** The wif project. @uml.property name="the wif project" */
  @JsonIgnore
  private WifProject wifProject;

  /**
   * The name.
   * 
   * @uml.property name="name"
   */
  private String featureFieldName;

  /**
   * Gets the wif project.
   * 
   * @return the wifProject
   */
  public WifProject getWifProject() {
    return this.wifProject;
  }

  public AbstractScenario(AbstractScenario copy) {
    super.setLabel(copy.getLabel());
    this.featureFieldName = copy.getFeatureFieldName();
    this.wifProject = copy.getWifProject();
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the wifProject to set
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Instantiates a new abstract scenario.
   */
  public AbstractScenario() {
    super();
  }

  /**
   * Gets the name.
   * 
   * @return the name
   * @uml.property name="name"
   */
  public String getFeatureFieldName() {
    return featureFieldName;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name to set
   * @uml.property name="name"
   */
  public void setFeatureFieldName(String name) {
    this.featureFieldName = name;
  }

  public void updateNewValues(AbstractScenario copy) {
    if (copy.getLabel() != null) {
      this.setLabel(copy.getLabel());
    }
    if (copy.getFeatureFieldName() != null) {
      this.setFeatureFieldName(copy.getFeatureFieldName());
    }
  }

}
