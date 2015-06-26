package au.org.aurin.wif.io.parsers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;

/**
 * The Class CouchParser.
 */
@Component
public class ProjectCouchParser {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectCouchParser.class);

  /**
   * Parses the.
   * 
   * @param project
   *          the project
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public WifProject parse(WifProject project) throws WifInvalidInputException {

    // LOGGER.debug("Parsing project: {}", project.getName());
    LOGGER.trace("Parsing project: {}", project.getName());
    // allocation land uses
    Collection<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    LOGGER.trace("Loading {} allocation land uses (ALU)...",
        allocationLandUses.size());
    for (AllocationLU allocationLU : allocationLandUses) {

      LOGGER.trace("ALU label: {}", allocationLU.getLabel());
      LOGGER.trace("--- UAZ value: {}, is not developable?: {}",
          allocationLU.getFeatureFieldName(), allocationLU.isNotDevelopable());
    }
    // suitability land uses
    Collection<SuitabilityLU> suitabilityLUs = project.getSuitabilityLUs();
    LOGGER.trace("Loading {} Suitablity Land Uses...", suitabilityLUs.size());
    for (SuitabilityLU suitabilityLU : suitabilityLUs) {
      LOGGER.trace("Suitability LU label: {}", suitabilityLU.getLabel());
      LOGGER.trace("... SLU score UAZ value: {}",
          suitabilityLU.getFeatureFieldName());
      // AssociatedLus
      suitabilityLU.setAssociatedALUs(parseAllocationLUs(suitabilityLU
          .getAssociatedALUsMap()));
      Set<AllocationLU> associatedALUs = suitabilityLU.getAssociatedALUs();
      for (AllocationLU allocationLU : associatedALUs) {
        AllocationLU lu = project.getExistingLandUseByLabel(allocationLU
            .getLabel());
        lu.setAssociatedLU(suitabilityLU);
        // TODO They should be t he same object
        allocationLU.setAssociatedLU(suitabilityLU);
        LOGGER
            .trace(
                "&&&&&&&&&&&&&&& AllocationLU {} is associated with Suitability LU: {}",
                lu.getLabel(), lu.getAssociatedLU().getLabel());
      }
    }
    // factors
    Collection<Factor> factors = project.getFactors();
    LOGGER.trace("Loading {} suitablity factors...", factors.size());
    for (Factor factor : factors) {
      LOGGER.trace("factorUuid: " + factor.getId());
      // factor types
      for (FactorType aFactorType : factor.getFactorTypes()) {
        LOGGER.trace("::  aFactorType label: {}", aFactorType.getLabel());
        LOGGER.trace(":: aFactorType UAZ value: {}", aFactorType.getValue());
        aFactorType.setFactorId(factor.getId());
      }
    }
    return project;
  }

  /**
   * Parses the allocation l us.
   * 
   * @param associatedALUsMap
   *          the associated al us map
   * @return the sets the
   */
  Set<AllocationLU> parseAllocationLUs(Map<String, String> associatedALUsMap) {

    Set<AllocationLU> associatedALUs = new HashSet<AllocationLU>();
    Collection<String> keys = associatedALUsMap.keySet();
    LOGGER.trace("Parsing {} alus ", keys.size());

    for (String id : keys) {
      LOGGER.trace("Looking for alu id {} ", id);
      AllocationLU allocationLU = allocationLUDao.findAllocationLUById(id);
      associatedALUs.add(allocationLU);
      LOGGER.trace("Parsed alu {} ", allocationLU.getLabel());

    }
    return associatedALUs;
  }
}
