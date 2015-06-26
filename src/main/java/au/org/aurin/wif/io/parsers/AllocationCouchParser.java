package au.org.aurin.wif.io.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.SLUOrderComparator;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;

/**
 * The Class CouchParser.
 */
@Component
public class AllocationCouchParser {
  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationCouchParser.class);

  /** The area requirement dao. */
  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /**
   * Parses the allocation l us with requirements.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the list
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public List<AllocationLU> parseAllocationLUsWithRequirements(
      DemandScenario demandScenario) throws WifInvalidInputException {
    LOGGER.trace("Parsing areaRequirements landUses, with demandscenario {}",
        demandScenario.getId());
    List<AllocationLU> allocationLUs = new ArrayList<AllocationLU>();
    // Now we parse area requirements
    List<AreaRequirement> areaRequirements = areaRequirementDao
        .getAreaRequirements(demandScenario.getId());
    for (AreaRequirement areaRequirement : areaRequirements) {
      areaRequirement.setDemandScenario(demandScenario);
      LOGGER.trace("Parsing areaRequirement {}, belonging to a scenarioId {}",
          areaRequirement.getId(), demandScenario.getId());
      LOGGER.trace("AllocationLUId {} for projection {}",
          areaRequirement.getAllocationLUId(),
          areaRequirement.getProjectionLabel());
      areaRequirement.setProjection(demandScenario.getWifProject()
          .getProjectionByLabel(areaRequirement.getProjectionLabel()));
      AllocationLU allocationLU = demandScenario.getWifProject()
          .getExistingLandUseById(areaRequirement.getAllocationLUId());
      allocationLU.getAreaRequirements().add(areaRequirement);
      LOGGER.trace("Adding area requirement to {}", allocationLU.getLabel());
      areaRequirement.setAllocationLU(allocationLU);
      allocationLUs.add(allocationLU);
    }
    return allocationLUs;
  }

  /**
   * Parses the.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @param project
   *          the project
   * @return the allocation scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public AllocationScenario parse(AllocationScenario allocationScenario,
      WifProject project) throws WifInvalidInputException {

    Map<String, Integer> landUseOrderMap = allocationScenario
        .getLandUseOrderMap();
    LOGGER.debug(
        "Parsing {} allocation landUseOrders for allocation scenario ",
        allocationScenario.getLandUseOrderMap().keySet().size());
    Set<AllocationLU> lUsOrder = new TreeSet<AllocationLU>(
        (new SLUOrderComparator()));
    Set<Entry<String, Integer>> keySet = landUseOrderMap.entrySet();
    for (Entry<String, Integer> ID : keySet) {
      AllocationLU allocationLU = project.getExistingLandUseById(ID.getKey());
      allocationLU.setPriority(landUseOrderMap.get(ID.getKey()));
      lUsOrder.add(allocationLU);
      LOGGER.trace("Priority of {} for {} ", allocationLU.getPriority(),
          allocationLU.getLabel());
      LOGGER.trace("id {} Number of area requirements {} ",
          allocationLU.getId(), allocationLU.getAreaRequirements().size());
    }
    allocationScenario.setLandUseOrder(lUsOrder);
    allocationScenario.setWifProject(project);
    return allocationScenario;
  }
}
