/**
 *
 * marcosnr
 * 08/03/2012
 */
package au.org.aurin.wif.model.demand;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.AbstractScenario;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.DensityDemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <b>DemandScenario.java</b> : Configuration parameters required to run a
 * successful demand Scenario. A DemandScenario is composed of 1 demographic
 * trend, information there is global for a project, which holds the projection
 * trend for demographic data and demand information tailored for each
 * particular type of land use. This information is particular to each scenario.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@JsonPropertyOrder({ "id", "label", "docType" })
public class DemandScenario extends AbstractScenario {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2167463430566658863L;

  /**
   * The demand infos. @uml.property name="demandInfos" @uml.associationEnd
   * multiplicity="(0 -1)"
   * inverse="demandScenario:au.org.aurin.wif.model.demand.DemandInfo"
   */
  private Set<DemandInfo> demandInfos;

  /** The local datas. */
  private Set<LocalData> localDatas;

  @JsonIgnore
  private DemandConfig demandConfig;

  /** The demographic trend. */
  @JsonIgnore
  private DemographicTrend demographicTrend;

  /** The demographic trend label. */
  private String demographicTrendLabel;

  private Set<DensityDemandInfo> densityDemandInfo;

  public Set<DensityDemandInfo> getDensityDemandInfo() {
    return densityDemandInfo;
  }

  public void setDensityDemandInfo(
      final Set<DensityDemandInfo> densityDemandInfo) {
    this.densityDemandInfo = densityDemandInfo;
  }

  /**
   * To duplicate the scenario, the demand information and sector information is
   * duplicated so that the user doesn't have to key in all the information
   * again.
   * 
   * The trend information is global.
   * 
   * @param copy
   *          the copy
   */
  public DemandScenario(final DemandScenario copy) {
    super(copy);

    this.demographicTrend = copy.getDemographicTrend();
    final Set<DemandInfo> newDemandInfos = new HashSet<DemandInfo>();
    final Set<DemandInfo> copyDemandInfos = copy.getDemandInfos();
    for (final DemandInfo demandInfoCopy : copyDemandInfos) {
      DemandInfo demandInfoNew = null;
      if (demandInfoCopy instanceof ResidentialDemandInfo) {
        demandInfoNew = new ResidentialDemandInfo(
            (ResidentialDemandInfo) demandInfoCopy);
      } else if (demandInfoCopy instanceof EmploymentDemandInfo) {
        demandInfoNew = new EmploymentDemandInfo(
            (EmploymentDemandInfo) demandInfoCopy);
      } else if (demandInfoCopy instanceof PreservationDemandInfo) {
        demandInfoNew = new PreservationDemandInfo(
            (PreservationDemandInfo) demandInfoCopy);
      }
      newDemandInfos.add(demandInfoNew);
      demandInfoNew.setDemandScenario(this);
    }
    this.demandInfos = newDemandInfos;
  }

  /**
   * Instantiates a new demand scenario.
   */
  public DemandScenario() {
    super();
    this.demandInfos = new HashSet<DemandInfo>();
    this.localDatas = new HashSet<LocalData>();
    this.densityDemandInfo = new HashSet<DensityDemandInfo>();
  }

  /**
   * Gets the demand infos.
   * 
   * @return the demandInfos
   */
  public Set<DemandInfo> getDemandInfos() {
    return this.demandInfos;
  }

  /**
   * Sets the demand infos.
   * 
   * @param demandInfos
   *          the demandInfos to set
   */
  public void setDemandInfos(final Set<DemandInfo> demandInfos) {
    this.demandInfos = demandInfos;
  }

  /**
   * Adds the demand info.
   * 
   * @param demandInfos
   *          the demand infos
   */
  public void addDemandInfo(final DemandInfo demandInfos) {
    this.demandInfos.add(demandInfos);
  }

  /**
   * Gets the eD info by lu.
   * 
   * @param allocationLU
   *          the allocation lu
   * @return the eD info by lu
   */
  public Set<EmploymentDemandInfo> getEmploymentDemandInfoByLU(
      final AllocationLU allocationLU) {
    final Set<EmploymentDemandInfo> employmentDemandInfosByLU = new HashSet<EmploymentDemandInfo>();
    //
    // for (final DemandInfo edinfo : this.getDemandInfos()) {
    // if (edinfo instanceof EmploymentDemandInfo
    // && allocationLU.isAssociated((EmploymentDemandInfo) edinfo)) {
    // employmentDemandInfosByLU.add((EmploymentDemandInfo) edinfo);
    // }
    // }

    // FIXME alu.getLabel() maybe change by user
    for (final DemandInfo edinfo : this.getDemandInfos()) {
      if (edinfo instanceof EmploymentDemandInfo) {
        for (final AllocationLU alu : ((EmploymentDemandInfo) edinfo)
            .getSector().getAssociatedLUs()) {
          if (alu.getLabel().equals(allocationLU.getLabel())) {
            employmentDemandInfosByLU.add((EmploymentDemandInfo) edinfo);
          }
        }

      }
    }
    return employmentDemandInfosByLU;
  }

  /**
   * Gets the demographic trend.
   * 
   * @return the demographicTrend
   */
  public DemographicTrend getDemographicTrend() {
    return demographicTrend;
  }

  /**
   * Sets the demographic trend.
   * 
   * @param demographicTrend
   *          the demographicTrend to set
   */
  public void setDemographicTrend(final DemographicTrend demographicTrend) {
    this.demographicTrend = demographicTrend;
  }

  /**
   * Gets the demographic trend label.
   * 
   * @return the demographicTrendLabel
   */
  public String getDemographicTrendLabel() {
    return demographicTrendLabel;
  }

  /**
   * Sets the demographic trend label.
   * 
   * @param trendId
   *          the new demographic trend label
   */
  public void setDemographicTrendLabel(final String trendId) {
    this.demographicTrendLabel = trendId;
  }

  /**
   * Gets the demand info by sector.
   * 
   * @param sector
   *          the sector
   * @return the demand info by sector
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public EmploymentDemandInfo getDemandInfoBySector(
      final EmploymentSector sector) throws WifInvalidInputException {

    for (final DemandInfo edinfo : this.getDemandInfos()) {
      if (edinfo instanceof EmploymentDemandInfo
          && ((EmploymentDemandInfo) edinfo).getSector().equals(sector)) {
        return (EmploymentDemandInfo) edinfo;
      }
    }
    throw new WifInvalidInputException("Sector " + sector.getLabel()
        + "not configured for this scenario!");
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
   * Sets the local datas.
   * 
   * @param localDatas
   *          the localDatas to set
   */
  public void setLocalDatas(final Set<LocalData> localDatas) {
    this.localDatas = localDatas;
  }

  /**
   * @return the demandConfig
   */
  public DemandConfig getDemandConfig() {
    return demandConfig;
  }

  /**
   * @param demandConfig
   *          the demandConfig to set
   */
  public void setDemandConfig(final DemandConfig demandConfig) {
    this.demandConfig = demandConfig;
  }
}
