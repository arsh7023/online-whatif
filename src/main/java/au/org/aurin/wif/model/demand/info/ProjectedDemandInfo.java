/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand.info;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.data.ProjectedData;

// 
/**
 * <b>ProjectedDemandInfo.java</b> : Configuration parameters required to set up
 * the projected demand like preservation.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
public class ProjectedDemandInfo extends DemandInfo {

  /** The projected datas. */
  private Set<ProjectedData> projectedDatas;

  /**
   * Instantiates a new projected demand info.
   */
  public ProjectedDemandInfo() {
    super();
    this.projectedDatas = new HashSet<ProjectedData>();
  }

  /**
   * Instantiates a new projected demand info.
   *
   * @param copy the copy
   */

  public ProjectedDemandInfo(ProjectedDemandInfo copy) {
    super(copy);
    Set<ProjectedData> projectedDataNew = new HashSet<ProjectedData>();
    Set<ProjectedData> projectedDatas = copy.getProjectedDatas();
    for (ProjectedData projectedData : projectedDatas) {
      ProjectedData copyPd = new ProjectedData(projectedData);
      projectedDataNew.add(copyPd);
    }

    this.projectedDatas = projectedDataNew;
  }

  /**
   * Sets the data projections.
   * 
   * @param projectedDatas
   *          the projectedDatas to set
   */
  public void setProjectedDatas(Set<ProjectedData> projectedDatas) {
    this.projectedDatas = projectedDatas;
  }

  /**
   * Gets the data projections.
   * 
   * @return the projectedDatas
   */
  public Set<ProjectedData> getProjectedDatas() {
    return projectedDatas;
  }

  /**
   * Adds the data projection.
   * 
   * @param projectedDatas
   *          the projected datas
   */
  public void addProjectedData(ProjectedData projectedDatas) {
    this.projectedDatas.add(projectedDatas);
  }

  /**
   * Gets the data projection.
   * 
   * @param projection
   *          the projection
   * @return the data projection
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public ProjectedData getProjectedData(Projection projection)
      throws WifInvalidInputException {

    for (ProjectedData projectedData : this.getProjectedDatas()) {
      if (projectedData.getProjection() == projection)
        return projectedData;
    }
    throw new WifInvalidInputException(
        "there's no ProjectedData from  projection: " + projection.getLabel()
            + " in demand scenario: " + this.getDemandScenario().getLabel());
  }

}
