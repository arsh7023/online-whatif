package au.org.aurin.wif.model.suitability;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.aurin.wif.exception.validate.IncompleteSuitabilityLUConfigException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <b>SuitabilityConfig.java</b> : Where the information for configuring a what
 * If? project is kept.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonInclude(Include.NON_NULL)
public class SuitabilityConfig {

  /** The id. @uml.property name="id" */
  private Integer id;

  /** The wif project. */
  @JsonIgnore
  private WifProject wifProject;

  /** The unified area zone. @uml.property name="unifiedAreaZone" */
  private String unifiedAreaZone;

  /** The not developable score. */
  private Double notDevelopableScore;

  /** should be in wifProject and LUaCatergries/labels!. */
  private Double notConvertableScore;

  /** should be in wifProject and LUaCatergries/labels!. */
  private Double notSuitableScore;

  private Double undefinedScore;
  /**
   * The suitabilityColumns, so that we give back to the interface only the info
   * needed from the UAZ in Suitability Analysis.
   */
  private Set<String> suitabilityColumns;

  /** The suitability categories. */
  private Set<String> suitabilityCategories;

  /** The score columns. */
  private Set<String> scoreColumns;

  /**
   * Setup project, creating the attribute values to store the score of
   * suitability analysis in the UAZ. Also optional receives an argument with a
   * list of columns' names that should be included in the geoJSON response.
   * 
   * @param optionalColumnsList
   *          the new setup
   * @param project
   *          the project
   * @throws IncompleteSuitabilityLUConfigException
   *           the incomplete suitability lu config exception
   */
  public void setup(List<String> optionalColumnsList, WifProject project)
      throws IncompleteSuitabilityLUConfigException {
    // TODO create better exceptions for extreme case, the setup can only be
    // executed after the end of the basic workflow, which suitability land
    // uses already created
    Set<SuitabilityLU> suitabilityLUs = project.getSuitabilityLUs();

    if (suitabilityLUs.size() == 0) {
      String msg = "SuitabilityLUs are not properly configured !";
      throw new IncompleteSuitabilityLUConfigException(msg);
    }

    scoreColumns = new HashSet<String>();
    suitabilityColumns = new HashSet<String>();
    suitabilityCategories = new HashSet<String>(
        WifKeys.DEFAULT_SUITABILITY_CATEGORIES);
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {

      if (suitabilityLU.getAssociatedALUs() == null) {
        String msg = "SuitabilityLU " + suitabilityLU.getLabel()
            + " doesn't have associated LUs!";
        throw new IncompleteSuitabilityLUConfigException(msg);
      }

      if (suitabilityLU.getAssociatedALUs().size() == 0) {
        String msg = "SuitabilityLU " + suitabilityLU.getLabel()
            + " doesn't have associated LUs!";
        throw new IncompleteSuitabilityLUConfigException(msg);
      }
      if (suitabilityLU.getFeatureFieldName() == null) {
        String msg = "SuitabilityLU " + suitabilityLU.getLabel()
            + " doesn't have  a label/featurefname set!";
        throw new IncompleteSuitabilityLUConfigException(msg);
      }
      if (suitabilityLU.getFeatureFieldName().isEmpty()) {
        String msg = "SuitabilityLU " + suitabilityLU.getLabel()
            + " doesn't have a label/featurefname set!";
        throw new IncompleteSuitabilityLUConfigException(msg);
      }
      scoreColumns.add(suitabilityLU.getFeatureFieldName());
    }
    suitabilityColumns.addAll(scoreColumns);
    suitabilityColumns.add(project.getExistingLUAttributeName());
    if (optionalColumnsList != null) {
      for (String column : optionalColumnsList) {
        suitabilityColumns.add(column.replaceAll(" ", "_").replaceAll("-", "_")
            .toLowerCase());
      }
    }
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
   * Sets the wif project.
   * 
   * @param wifProject
   *          the new wif project
   */
  public void setWifProject(WifProject wifProject) {
    this.wifProject = wifProject;
  }

  /**
   * Gets the unified area zone.
   * 
   * @return the unified area zone
   */
  public String getUnifiedAreaZone() {
    return unifiedAreaZone;
  }

  /**
   * Sets the unified area zone.
   * 
   * @param unifiedAreaZone
   *          the new unified area zone
   */
  public void setUnifiedAreaZone(String unifiedAreaZone) {
    this.unifiedAreaZone = unifiedAreaZone;
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   *          the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the suitability columns.
   * 
   * @return the suitabilityColumns
   */
  public Set<String> getSuitabilityColumns() {
    return suitabilityColumns;
  }

  /**
   * Sets the suitability columns.
   * 
   * @param suitabilityColumns
   *          the suitabilityColumns to set
   */
  public void setSuitabilityColumns(Set<String> suitabilityColumns) {
    this.suitabilityColumns = suitabilityColumns;
  }

  /**
   * Gets the score columns.
   * 
   * @return the scoreColumns
   */
  public Set<String> getScoreColumns() {
    return scoreColumns;
  }

  /**
   * Sets the score columns.
   * 
   * @param scoreColumns
   *          the scoreColumns to set
   */
  public void setScoreColumns(Set<String> scoreColumns) {
    this.scoreColumns = scoreColumns;
  }

  /**
   * Gets the not developable score.
   * 
   * @return the notDevelopableScore
   */
  public Double getNotDevelopableScore() {
    return notDevelopableScore;
  }

  /**
   * Sets the not developable score.
   * 
   * @param notDevelopableScore
   *          the notDevelopableScore to set
   */
  public void setNotDevelopableScore(Double notDevelopableScore) {
    this.notDevelopableScore = notDevelopableScore;
  }

  /**
   * Gets the not convertable score.
   * 
   * @return the notConvertableScore
   */
  public Double getNotConvertableScore() {
    return notConvertableScore;
  }

  /**
   * Sets the not convertable score.
   * 
   * @param notConvertableScore
   *          the notConvertableScore to set
   */
  public void setNotConvertableScore(Double notConvertableScore) {
    this.notConvertableScore = notConvertableScore;
  }

  /**
   * Gets the not suitable score.
   * 
   * @return the notSuitableScore
   */
  public Double getNotSuitableScore() {
    return notSuitableScore;
  }

  /**
   * Sets the not suitable score.
   * 
   * @param notSuitableScore
   *          the notSuitableScore to set
   */
  public void setNotSuitableScore(Double notSuitableScore) {
    this.notSuitableScore = notSuitableScore;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SuitabilityConfig ["
        + (id != null ? "id=" + id + ", " : "")
        + (unifiedAreaZone != null ? "unifiedAreaZone=" + unifiedAreaZone
            + ", " : "")
        + (notDevelopableScore != null ? "notDevelopableScore="
            + notDevelopableScore + ", " : "")
        + (notConvertableScore != null ? "notConvertableScore="
            + notConvertableScore + ", " : "")
        + (notSuitableScore != null ? "notSuitableScore=" + notSuitableScore
            + ", " : "")
        + (suitabilityColumns != null ? "suitabilityColumns="
            + suitabilityColumns + ", " : "")
        + (scoreColumns != null ? "scoreColumns=" + scoreColumns : "") + "]";
  }

  /**
   * Gets the suitability categories.
   * 
   * @return the suitabilityCategories
   */
  public Set<String> getSuitabilityCategories() {
    return suitabilityCategories;
  }

  /**
   * Sets the suitability categories.
   * 
   * @param suitabilityCategories
   *          the suitabilityCategories to set
   */
  public void setSuitabilityCategories(Set<String> suitabilityCategories) {
    this.suitabilityCategories = suitabilityCategories;
  }

  /**
   * @return the undefinedScore
   */
  public Double getUndefinedScore() {
    return undefinedScore;
  }

  /**
   * @param undefinedScore
   *          the undefinedScore to set
   */
  public void setUndefinedScore(Double undefinedScore) {
    this.undefinedScore = undefinedScore;
  }

}
