/**
 * 
 */
package au.org.aurin.wif.model.reports;

import java.util.HashSet;
import java.util.Set;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

/**
 * ProjectReport will print the information to save a project for configuration
 * or show it to any reporting tools
 * 
 * @author Marcos Nino-Ruiz
 */
public class ProjectReport extends Report {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -550019052589921075L;
  private String serviceVersion;
  private WifProject project;
  private DemandConfig demandConfig;

  private Set<SuitabilityScenario> suitabilityScenarios;
  private Set<DemandOutcome> demandOutcome;

  private AllocationConfigs allocationconfig;
  private Set<AllocationControlScenario> allocationControlScenarios;

  private Set<AllocationScenario> allocationScenarios;
  private Set<DemandScenario> demandScenarios;

  /**
   * @return the project
   */
  public WifProject getProject() {
    return project;
  }

  /**
   * @param project
   *          the project to set
   */
  public void setProject(final WifProject project) {
    this.project = project;
  }

  /**
   * @return the suitabilityScenarios
   */
  public Set<SuitabilityScenario> getSuitabilityScenarios() {
    return suitabilityScenarios;
  }

  /**
   * @param suitabilityScenarios
   *          the suitabilityScenarios to set
   */
  public void setSuitabilityScenarios(
      final Set<SuitabilityScenario> suitabilityScenarios) {
    this.suitabilityScenarios = suitabilityScenarios;
  }

  /**
   * @return the serviceVersion
   */
  public String getServiceVersion() {
    return serviceVersion;
  }

  /**
   * @param serviceVersion
   *          the serviceVersion to set
   */
  public void setServiceVersion(final String serviceVersion) {
    this.serviceVersion = serviceVersion;
  }

  /**
   * 
   */
  public ProjectReport() {
    super();
    suitabilityScenarios = new HashSet<SuitabilityScenario>();
    demandOutcome = new HashSet<DemandOutcome>();
    allocationControlScenarios = new HashSet<AllocationControlScenario>();
    allocationScenarios = new HashSet<AllocationScenario>();
    demandScenarios = new HashSet<DemandScenario>();
  }

  public Set<DemandOutcome> getDemandOutcomes() {
    return demandOutcome;
  }

  public void setDemandOutcomes(final Set<DemandOutcome> demandOutcomes) {
    this.demandOutcome = demandOutcomes;
  }

  public AllocationConfigs getAllocationconfig() {
    return allocationconfig;
  }

  public void setAllocationconfig(final AllocationConfigs allocationconfig) {
    this.allocationconfig = allocationconfig;
  }

  public Set<AllocationControlScenario> getAllocationControlScenarios() {
    return allocationControlScenarios;
  }

  public void setAllocationControlScenarios(
      final Set<AllocationControlScenario> allocationControlScenarios) {
    this.allocationControlScenarios = allocationControlScenarios;
  }

  public Set<AllocationScenario> getAllocationScenarios() {
    return allocationScenarios;
  }

  public void setAllocationScenarios(
      final Set<AllocationScenario> allocationScenarios) {
    this.allocationScenarios = allocationScenarios;
  }

  public DemandConfig getDemandConfig() {
    return demandConfig;
  }

  public void setDemandConfig(final DemandConfig demandConfig) {
    this.demandConfig = demandConfig;
  }

  public Set<DemandScenario> getDemandScenarios() {
    return demandScenarios;
  }

  public void setDemandScenarios(final Set<DemandScenario> demandScenarios) {
    this.demandScenarios = demandScenarios;
  }

}
