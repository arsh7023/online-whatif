package au.org.aurin.wif.impl.report;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.reports.suitability.SuitabilityAnalysisReportDao;

/**
 * The Class Reporter.
 */
@Component
public class ConsoleReporter {

  /** The suitability analysis report dao. */
  @Autowired
  private SuitabilityAnalysisReportDao suitabilityAnalysisReportDao;
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleReporter.class);

  /**
   * Report wif project config.
   * 
   * @param project
   *          the project
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public void reportWifProjectConfig(WifProject project)
      throws WifInvalidConfigException {

    if (project == null) {
      throw new WifInvalidConfigException("project hasn't been loaded");
    }
    LOGGER.info("showing configuration for project ID: {}", project.getId());

    LOGGER.info("Project  name: {}", project.getLabel());

    LOGGER.info("unified area zone (UAZ) Datastore  name: {}", project
        .getSuitabilityConfig().getUnifiedAreaZone());

    Collection<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    LOGGER.info("Loading {} allocation land uses (ALU)...",
        allocationLandUses.size());
    for (AllocationLU aCurrLandUse : allocationLandUses) {
      LOGGER.info("ALU label: {}", aCurrLandUse.getLabel());
      LOGGER.info("--- UAZ value: {}, is not developable?: {}",
          aCurrLandUse.getFeatureFieldName(), aCurrLandUse.isNotDevelopable());
    }

    Collection<SuitabilityLU> suitabilityLUs = project.getSuitabilityLUs();
    LOGGER.info("Loading {} Suitablity Land Uses...", suitabilityLUs.size());
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      LOGGER.info("Suitability LU label: {}", suitabilityLU.getLabel());
      LOGGER.info("... SLU score UAZ value: {}",
          suitabilityLU.getFeatureFieldName());
      LOGGER.info("... it has {} associated Land Uses...", suitabilityLU
          .getAssociatedALUs().size());
      for (AllocationLU aLU : suitabilityLU.getAssociatedALUs()) {
        LOGGER.info("... associated ALU label: {}", aLU.getLabel());
      }
    }

    Collection<Factor> factors = project.getFactors();
    LOGGER.info("Loading {} suitablity factors...", factors.size());
    for (Factor aSuitabilityFactor : factors) {
      LOGGER.info("- Suitability Factor label: {}",
          aSuitabilityFactor.getLabel());
      LOGGER.info("- Factor UAZ column name: {}",
          aSuitabilityFactor.getFeatureFieldName());

      for (FactorType aFactorType : aSuitabilityFactor.getFactorTypes()) {
        LOGGER.debug("::  Rating label: {}", aFactorType.getLabel());
        LOGGER.debug(":: Rating UAZ value: {}", aFactorType.getValue());
      }
    }
  }

  /**
   * Report allocation scenarios.
   * 
   * @param allocationScn
   *          the allocation scn
   */
  public void reportAllocationScenarios(AllocationScenario allocationScn) {
    LOGGER.info(">>>>> Allocation Scenario name: {}",
        allocationScn.getFeatureFieldName());
    LOGGER.info("Allocation Scenario UAZ attribute name: {}",
        allocationScn.getLabel());
  }

  /**
   * Report suitability scenario.
   * 
   * @param suitabilityScenario
   *          the suitability scenario
   */
  public void reportSuitabilityScenario(SuitabilityScenario suitabilityScenario) {
    LOGGER.info(">>>>> Suitability Scenario name: {}",
        suitabilityScenario.getLabel());
    LOGGER.info("Suitability Scenario UAZ attribute name: {}",
        suitabilityScenario.getFeatureFieldName());

    Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());
    for (SuitabilityRule rules : suitabilityRules) {
      LOGGER
          .info("++++++ suitability LU label: {} has {} convertibles ALU's",
              rules.getSuitabilityLU().getLabel(), rules.getConvertibleLUs()
                  .size());

      Collection<AllocationLU> conversions = rules.getConvertibleLUs();
      for (AllocationLU aLU : conversions) {
        LOGGER.debug(" ALU label {}, with attribute name {} is convertible",
            aLU.getLabel(), aLU.getFeatureFieldName());
      }

      Collection<FactorImportance> factorsImp = rules.getFactorImportances();
      LOGGER.info("Loading {} factor importances configured for SLU: {}...",
          factorsImp.size(), rules.getSuitabilityLU().getLabel());
      LOGGER.info("SLU feature field name is: {}...", rules.getSuitabilityLU()
          .getFeatureFieldName());
      for (FactorImportance factorImportance : factorsImp) {
        Factor aFactor = factorImportance.getFactor();
        String factorLabel = aFactor.getLabel();
        Double factorWeight = factorImportance.getImportance();
        LOGGER.debug(" Factor label: {}", factorLabel);
        LOGGER.debug("- Factor importance: {}", factorWeight);

        for (FactorTypeRating aFactorRating : factorImportance
            .getFactorTypeRatings()) {
          Double ratingScore = aFactorRating.getScore();
          String ratingLabel = aFactorRating.getFactorType().getLabel();
          LOGGER.debug("--> for factor type label: {}", ratingLabel);
          LOGGER.debug("--> the rating is: {}", ratingScore);
        }
      }
    }
  }

  /**
   * Report demand setup.
   * 
   * @param project
   *          the project
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public void reportDemandSetup(WifProject project)
      throws WifInvalidConfigException {
    // TODO this reporting should be in a separate module, because it belongs to
    // demand
    Set<Projection> proyections = project.getProjections();
    for (Projection projection : proyections) {
      LOGGER.info("Loading {} projection ", projection.getLabel());
    }
    Set<EmploymentSector> sectors = project.getSectors();
    for (EmploymentSector employmentSector : sectors) {
      LOGGER.info("Loading {} sector ", employmentSector.getLabel());
    }

    Collection<SuitabilityScenario> suitabilityScenarios = project
        .getSuitabilityScenarios();
    LOGGER.info("*****   Loading {} suitablity scenarios...",
        suitabilityScenarios.size());
    for (SuitabilityScenario suitabilityScn : suitabilityScenarios) {
      reportSuitabilityScenario(suitabilityScn);
    }
  }

  /**
   * Report demand scenario.
   * 
   * @param demandScenario
   *          the demand scenario
   */
  public void reportDemandScenario(DemandScenario demandScenario) {
    LOGGER.info("Loading {} demandScenario ", demandScenario.getLabel());
    Set<DemographicData> demographicData = demandScenario.getDemographicTrend()
        .getDemographicData();
    for (DemographicData demograph : demographicData) {
      LOGGER.info("for demographic projection year: {}", demograph
          .getProjection().getYear());
      // LOGGER.info("demographic population: {}",
      // demograph.getTotalPopulation());
    }
    // Set<DemandScenario> demandScenarios = project.getDemandScenarios();
    // LOGGER.info("*****   Loading {} demand scenarios...",
    // demandScenarios.size());
    // for (DemandScenario demandScenario : demandScenarios) {
    // reportDemandScenario(demandScenario);
    // }
  }
}
