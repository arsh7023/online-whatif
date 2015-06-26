package au.org.aurin.wif.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.ResidentialDemographicData;
import au.org.aurin.wif.model.demand.data.EmploymentData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.ProjectedDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * <b>ProjectConfigurator.java</b> : in charge of configuration of the complex
 * what if projects
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Component
public class ProjectConfigurator implements InitializingBean {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 213426734533L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectConfigurator.class);

  /** The active project. */
  private WifProject activeProject;

  /**
   * Modify project from parameters.
   * 
   * @param wifProject
   *          the wif project
   * @param wifParameters
   *          the wif parameters
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public void modifySuitabilityFromParameters(WifProject wifProject,
      Map<String, Object> wifParameters) throws WifInvalidInputException {
    //

    String newScn = (String) wifParameters.get(WifKeys.SUITABILITY_SCENARIO);
    String newLU = (String) wifParameters.get(WifKeys.SUITABILITYLU_NAME);
    String newFactor = (String) wifParameters.get(WifKeys.FACTOR);
    String newImportance = (String) wifParameters.get(WifKeys.IMPORTANCE);
    String newFactorType = (String) wifParameters.get(WifKeys.FACTOR_TYPE);
    String newRating = (String) wifParameters.get(WifKeys.RATING);

    LOGGER.debug("project name: {}", wifProject.getLabel());

    LOGGER.debug("modifyProjectFromParameters for scenario: {}", newScn);
    LOGGER.debug("landuse: {}", newLU);
    LOGGER.debug("factor: {}", newFactor);
    LOGGER.debug("importance: {}", newImportance);
    LOGGER.debug("factortype: {}", newFactorType);
    LOGGER.debug("rating: {}", newRating);
    // }

    // wifProject.modifyFactorRating(newScn, newLU, newFactor,
    // newImportance, newFactorType, newRating);
    modifyFactorRating(newScn, newLU, newFactor, newImportance, newFactorType,
        newRating);

  }

  /*
   * FIXME dirty implementation of JSON while we implement real change of the
   * configuration.
   */
  /**
   * Modify factor rating.
   * 
   * @param newScenarioName
   *          the new scenario
   * @param newLUName
   *          the new lu name
   * @param newFactorName
   *          the new factor
   * @param newImportance
   *          the new importance
   * @param newFactorTypeName
   *          the new factor type name
   * @param newRating
   *          the new rating
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public void modifyFactorRating(String newScenarioName, String newLUName,
      String newFactorName, String newImportance, String newFactorTypeName,
      String newRating) throws WifInvalidInputException {
    SuitabilityScenario suitabilityScn = getActiveProject()
        .getSuitabilityScenarioByLabel(newScenarioName);

    SuitabilityRule rule = suitabilityScn
        .getLandUseConversionBySLUName(newLUName);
    Collection<FactorImportance> factorImportances = rule
        .getFactorImportances();

    // Set<SuitabilityRules> suitabilityRules =
    // suitabilityScn.getSuitabilityRules();
    // for (SuitabilityRules suitabilityRule : suitabilityRules) {
    // if
    // (suitabilityRule.getSuitabilityLU().getName().equalsIgnoreCase(newLUName))
    // {
    // Set<FactorImportance> factorImportances =
    // suitabilityRule.getFactorImportances();
    LOGGER.trace("factorImportances {}", factorImportances.size());
    for (FactorImportance factorImportance : factorImportances) {
      LOGGER.trace("Importance {}: {}",
          factorImportance.getFactor().getLabel(),
          factorImportance.getImportance());
      // LOGGER.trace("id: {}", factorImportance.getId());
      if (factorImportance.getFactor().getLabel()
          .equalsIgnoreCase(newFactorName)) {
        factorImportance.setImportance(Double.valueOf(newImportance));
        LOGGER.trace("new: {}", factorImportance.getImportance());

        Set<FactorTypeRating> factorTypeRatings = factorImportance
            .getFactorTypeRatings();
        for (FactorTypeRating factorTypeRating : factorTypeRatings) {

          if (factorTypeRating.getFactorType().getLabel()
              .equalsIgnoreCase(newFactorTypeName)) {
            factorTypeRating.setScore(Double.valueOf(newRating));
          }
        }
      }
    }
  }

  /**
   * Update project.
   * 
   * @param wifProject
   *          the wif project
   */
  public void updateProject(WifProject wifProject) {

  }

  /**
   * Gets the suitability scn.
   * 
   * @param wifParameters
   *          the wif parameters
   * @return the suitability scn
   */
  public String getSuitabilityScn(Map<String, Object> wifParameters) {
    String name = (String) wifParameters.get(WifKeys.SUITABILITY_SCENARIO);
    // if (name != null) {
    // name = "Suburbanization";
    // }
    return name;

  }

  /**
   * Gets the allocation scn.
   * 
   * @param wifParameters
   *          the wif parameters
   * @return the allocation scn
   */
  public String getAllocationScn(Map<String, Object> wifParameters) {
    String name = (String) wifParameters.get(WifKeys.ALLOCATION_SCENARIO);
    return name;

  }

  /**
   * Gets the demand scn.
   * 
   * @param wifParameters
   *          the wif parameters
   * @return the demand scn
   */
  public String getDemandScn(Map<String, Object> wifParameters) {
    String name = (String) wifParameters.get(WifKeys.DEMAND_SCENARIO);

    return name;

  }

  /**
   * Modify demand from parameters.
   * 
   * @param wifProject
   *          the wif project
   * @param wifParameters
   *          the wif parameters
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public void modifyDemandFromParameters(WifProject wifProject,
      Map<String, Object> wifParameters) throws WifInvalidInputException,
      WifInvalidConfigException {
    String newProjectionYear = (String) wifParameters
        .get(WifKeys.PROJECTION_YEAR);

    String newtotalPopulation = (String) wifParameters
        .get(WifKeys.TOTAL_POPULATION);
    String newGQPopulation = (String) wifParameters.get(WifKeys.GQ_POPULATION);
    String newavgHHSize = (String) wifParameters.get(WifKeys.AVG_HH_SIZE);

    String newexistingLU = (String) wifParameters.get(WifKeys.EXISTING_LU);

    String newcurrentDensity = (String) wifParameters
        .get(WifKeys.CURRENT_DENSITY);
    String newfutureDensity = (String) wifParameters
        .get(WifKeys.FUTURE_DENSITY);
    String newcurrentBreakdown = (String) wifParameters
        .get(WifKeys.CURRENT_BREAKDOWN);
    String newfutureBreakdown = (String) wifParameters
        .get(WifKeys.FUTURE_BREAKDOWN);
    String newcurrentVacancy = (String) wifParameters
        .get(WifKeys.CURRENT_VACANCY);
    String newfutureVacancy = (String) wifParameters
        .get(WifKeys.FUTURE_VACANCY);
    String newinfillRate = (String) wifParameters.get(WifKeys.INFILL_RATE);

    String newProjectedSize = (String) wifParameters
        .get(WifKeys.PROJECTED_SIZE);
    String newSectorName = (String) wifParameters.get(WifKeys.SECTOR_NAME);
    String demandScnName = (String) wifParameters.get(WifKeys.DEMAND_SCENARIO);

    TreeSet<Projection> projections = new TreeSet<Projection>(
        (new YearComparator()));
    projections.addAll((Set<Projection>) wifProject.getProjections());
    DemandScenario demandScn = wifProject
        .getDemandScenarioByLabel(demandScnName);

    Collection<AllocationLU> existingLandUses = (Collection<AllocationLU>) wifProject
        .getAllocationLandUses();

    Set<EmploymentSector> sectors = wifProject.getSectors();

    Projection selprojection = new Projection();

    for (Projection projection : projections) {

      if (projection.getLabel().equalsIgnoreCase(newProjectionYear)) {

        selprojection = projection;
        DemographicData demographic = demandScn.getDemographicTrend()
            .getResidentialDemographicData(projection);

        ((ResidentialDemographicData) demographic).setTotalPopulation(Long
            .valueOf(newtotalPopulation));
        ((ResidentialDemographicData) demographic).setgQPopulation(Long
            .valueOf(newGQPopulation));
      }

    }

    for (AllocationLU allocationLU : existingLandUses) {
      if (allocationLU.getLabel().equalsIgnoreCase(newexistingLU)) {

        if (allocationLU.hasDemandInfoInScenario(demandScn)) {
          DemandInfo demandInfo = allocationLU
              .getDemandInfoByScenario(demandScn);

          if (demandInfo instanceof ResidentialDemandInfo) {
            ResidentialDemandInfo rinfo = (ResidentialDemandInfo) demandInfo;
            rinfo.setFutureBreakdownByHType(Double.valueOf(newfutureBreakdown));
            rinfo.setCurrentDensity(Double.valueOf(newcurrentDensity));
            rinfo.setFutureDensity(Double.valueOf(newfutureDensity));
            rinfo.setFutureVacancyRate(Double.valueOf(newfutureVacancy));
            rinfo.setInfillRate(Double.valueOf(newinfillRate));
          }

          else if (demandInfo instanceof ProjectedDemandInfo) {

            ProjectedData projectedData = ((ProjectedDemandInfo) demandInfo)
                .getProjectedData(selprojection);

            if (demandInfo instanceof EmploymentDemandInfo) {

              Set<EmploymentDemandInfo> employmentDemandInfos = demandScn
                  .getEmploymentDemandInfoByLU(allocationLU);

              for (EmploymentDemandInfo edi : employmentDemandInfos) {
                LOGGER.trace("edi sector name: {}", edi.getSector().getLabel());
                if (edi.getSector().getLabel().equalsIgnoreCase(newSectorName)) {

                  EmploymentDemandInfo einfo = (EmploymentDemandInfo) edi;
                  einfo.setFutureDensity(Double.valueOf(newfutureDensity));
                  einfo.setInfillRate(Double.valueOf(newinfillRate));

                  EmploymentData projectedEmployment = (EmploymentData) projectedData;
                  projectedEmployment.setEmployees(Integer
                      .valueOf(newProjectedSize));
                  LOGGER.trace("project employment: {}",
                      projectedEmployment.getEmployees());

                }

              }
            }

          }
        }

      }
    }

  }

  /**
   * Sets the active project.
   * 
   * @param activeProject
   *          the activeProject to set
   */
  public void setActiveProject(WifProject activeProject) {
    this.activeProject = activeProject;
  }

  /**
   * Gets the active project.
   * 
   * @return the activeProject
   */
  public WifProject getActiveProject() {
    return activeProject;
  }

  /* (non-Javadoc)
   * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
   */
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(wifProjectDao);

  }

}
