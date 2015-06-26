/*
 *
 */
package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.info.LocalDemandInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class LocalJurisdiction.
 *
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 * marcosnr@unimelb.edu.au</a> - 2012
 */

public class LocalJurisdiction {

  /** The local demand info. */
  @JsonIgnore
  private LocalDemandInfo localDemandInfo;

  /** The local datas. */
  @JsonIgnore
  private Set<LocalData> localDatas;

  /** The label. @uml.property name="label" */
  private String label;
  /**
   * Instantiates a new local jurisdiction.
   */
  public LocalJurisdiction() {
    super();
    this.localDatas = new HashSet<LocalData>();
  }

  /**
   * Getter of the property <tt>label</tt>.
   * 
   * @return Returns the label.
   * @uml.property name="label"
   */
  public String getLabel() {
    return label;
  }

  /**
   * Setter of the property <tt>label</tt>.
   * 
   * @param label
   *          The label to set.
   * @uml.property name="label"
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Sets the local datas.
   * 
   * @param localDatas
   *          the localDatas to set
   */
  public void setLocalDatas(Set<LocalData> localDatas) {
    this.localDatas = localDatas;
  }

  /**
   * Gets the local datas.
   * 
   * @return the localDatas
   */
  public Set<LocalData> getLocalDatas() {
    return localDatas;
  }

  /**
   * Adds the local data.
   * 
   * @param localC0
   *          the local c0
   */
  public void addLocalData(LocalData localC0) {
    this.localDatas.add(localC0);
  }

  /**
   * Gets the local data.
   * 
   * @param projection
   *          the projection
   * @return the local data
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public LocalData getLocalData(Projection projection)
      throws WifInvalidInputException {

    for (LocalData localData : this.localDatas) {
      if (localData.getProjection().getYear().equals(projection.getYear())) {
        return localData;

      }
    }

    throw new WifInvalidInputException("there's no local data for : "
        + this.getLabel() + " in projection: " + projection.getLabel());
  }

  /**
   * Gets the local demand info.
   *
   * @return the localDemandInfo
   */
  public LocalDemandInfo getLocalDemandInfo() {
    return localDemandInfo;
  }

  /**
   * Sets the local demand info.
   *
   * @param localDemandInfo the localDemandInfo to set
   */
  public void setLocalDemandInfo(LocalDemandInfo localDemandInfo) {
    this.localDemandInfo = localDemandInfo;
  }

}
