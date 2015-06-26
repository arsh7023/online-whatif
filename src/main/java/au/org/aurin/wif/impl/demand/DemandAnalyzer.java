/*
 * 
 */
package au.org.aurin.wif.impl.demand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.config.ProjectConfigurator;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioNewService;

/**
 * <b>DemandAnalyzer.java</b> : Implementation of @see WifAnalysisService
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Component
public class DemandAnalyzer {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 21342673455379L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandAnalyzer.class);

  /** The project configurator. */
  @Autowired
  private ProjectConfigurator projectConfigurator;

  /** The required area analyzer. */
  @Autowired
  private RequiredAreaAnalyzer requiredAreaAnalyzer;

  /** The demand scenario new service. */
  @Resource
  private DemandScenarioNewService DemandScenarioNewService;

  /**
   * to handle initialization.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * to handle destroy.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /**
   * Do demand analysis REST version.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the list
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public List<AreaRequirement> doDemandAnalysis(
      final DemandScenario demandScenario) throws WifInvalidInputException,
      WifInvalidConfigException {

    final List<AreaRequirement> outcome = new ArrayList<AreaRequirement>();
    LOGGER
        .info("processing demand analysis for ={}", demandScenario.getLabel());

    final WifProject wifProject = demandScenario.getWifProject();
    final String uazDBTable = wifProject.getSuitabilityConfig()
        .getUnifiedAreaZone();
    LOGGER.debug("uaz spatial table: {}", uazDBTable);

    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    projections.addAll(wifProject.getProjections());

    final Collection<AllocationLU> existingLandUses = wifProject
        .getAllocationLandUses();

    LOGGER.info("Demand Scenario label: {}", demandScenario.getLabel());
    final DemographicTrend demographicTrend = demandScenario
        .getDemographicTrend();
    LOGGER.info("Using the following trend: {}", demographicTrend.getLabel());
    final Set<LocalJurisdiction> localJurisdictions = wifProject
        .getDemandConfig().getLocalJurisdictions();
    // setting up taking into account current projection year is not a
    // projection by itself,per se,
    final Projection current = projections.first();
    LOGGER.info("current year projection: {}", current.getLabel());
    final NavigableSet<Projection> projectedSet = projections.tailSet(
        projections.first(), false);
    LOGGER
        .info(
            "About to perform demand analysis of {} land uses for the following projections: ",
            existingLandUses.size());
    for (final Projection projection : projectedSet) {
      LOGGER.info("----> projection year: {}", projection.getLabel());
    }

    for (final AllocationLU allocationLU : existingLandUses) {

      if (allocationLU.hasDemandInfoInScenario(demandScenario)
          || allocationLU.isLocal()) {
        outcome.addAll(requiredAreaAnalyzer.analyseAllocationLUDemand(
            allocationLU, demandScenario, projections, projectedSet,
            localJurisdictions));
      } else {
        LOGGER.warn(
            "there is not demand information for {} in the {} scenario",
            allocationLU.getLabel(), demandScenario.getFeatureFieldName());
      }
    }
    LOGGER.info("Finished analyzing for Demand Scenario label: {}",
        demandScenario.getLabel());
    for (final AreaRequirement area : outcome) {
      area.setProjectionLabel(area.getProjection().getLabel());
    }
    return outcome;
  }
}
