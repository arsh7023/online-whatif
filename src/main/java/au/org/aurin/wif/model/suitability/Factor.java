/*
 *
 */
package au.org.aurin.wif.model.suitability;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Model2JsonMapped;
import au.org.aurin.wif.model.ProjectCouchDoc;
import au.org.aurin.wif.model.WifProject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * The Class Factor.
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class Factor extends ProjectCouchDoc implements Model2JsonMapped {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9169319362703688454L;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

  /** The feature field name. */
  private String featureFieldName;

  /** The factor types. */
  // @JsonIgnore
  private Set<FactorType> factorTypes;

  /**
   * Gets the feature field name.
   * 
   * @return the feature field name
   */
  public String getFeatureFieldName() {
    return this.featureFieldName;
  }

  /**
   * Sets the feature field name.
   * 
   * @param name
   *          the new feature field name
   */
  public void setFeatureFieldName(String name) {
    this.featureFieldName = name;
  }

  /**
   * Sets the factor types.
   * 
   * @param factorTypes
   *          the new factor types
   */
  public void setFactorTypes(Set<FactorType> factorTypes) {
    this.factorTypes = factorTypes;
  }

  /**
   * Gets the factor types.
   * 
   * @return the factor types
   */
  public Set<FactorType> getFactorTypes() {
    return factorTypes;
  }

  /**
   * Sets the wif project.
   * 
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the wif project.
   * 
   * @return the wif project
   */
  public WifProject getWifProject() {
    return wifProject;
  }

  /**
   * Gets the factor type by id.
   * 
   * @param factortypeid
   *          the factortypeid
   * @return the factor type by id
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public FactorType getFactorTypeById(String factortypeid)
      throws WifInvalidInputException {
    for (FactorType ft : this.getFactorTypes()) {
      if (ft.getId().equals(factortypeid)) {
        return ft;
      }
    }
    throw new WifInvalidInputException(factortypeid + " id not found");
  }

  /**
   * Gets the factor type by label.
   * 
   * @param label
   *          the label
   * @return the factor type by label
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public FactorType getFactorTypeByLabel(String label)
      throws WifInvalidInputException {
    for (FactorType ft : this.getFactorTypes()) {
      if (ft.getLabel().equals(label)) {
        return ft;
      }
    }
    throw new WifInvalidInputException(label + " label not found");
  }

  /**
   * Adds the factor type.
   * 
   * @param factorType
   *          the factor type
   */
  public void addFactorType(FactorType factorType) {
    if (this.factorTypes == null) {
      this.factorTypes = new HashSet<FactorType>();
    }
    this.factorTypes.add(factorType);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Factor [id=" + super.getId() + ", featureFieldName="
        + featureFieldName + ", label=" + getLabel() + ", factorTypes="
        + factorTypes + "]";
  }

  public Factor() {
    super();
    this.factorTypes = new HashSet<FactorType>();
  }

}
