/**
 *

 * marcosnr
 * 30/03/2012
 */
package au.org.aurin.wif.io.demonstrationdata;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfig;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class DemonstrationAllocationNoControlsData.
 */
public class DemonstrationAllocationNoControlsData {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemonstrationAllocationNoControlsData.class);

  /**
   * Creates the allocation module for no control scenarios.
   * 
   * @param project
   *          the project
   * @return the wif project
   */
  public static WifProject createAllocationModule(WifProject project) {

    try {

      AllocationLU residentiallu = project
          .getExistingLandUseByLabel("Low Density Res.");
      residentiallu.setAllocationFeatureFieldName("201.0");

      AllocationLU residentiallum = project
          .getExistingLandUseByLabel("Med Density Res.");
      residentiallum.setAllocationFeatureFieldName("202.0");

      AllocationLU residentialmixedUseLU = project
          .getExistingLandUseByLabel("Mixed Use");
      residentialmixedUseLU.setAllocationFeatureFieldName("203.0");

      AllocationLU groupQuartersLU = project
          .getExistingLandUseByLabel("Nursing Home");
      groupQuartersLU.setAllocationFeatureFieldName("204.0");

      AllocationLU conservationLU = project
          .getExistingLandUseByLabel("Conservation");
      conservationLU.setAllocationFeatureFieldName("283.0");

      AllocationLU localParkLU = project
          .getExistingLandUseByLabel("Parks & Rec.");
      localParkLU.setAllocationFeatureFieldName("241.0");

      AllocationLU empRRetailLU = project
          .getExistingLandUseByLabel("Regional Retail");
      empRRetailLU.setAllocationFeatureFieldName("213.0");

      AllocationLU empLRetailLU = project
          .getExistingLandUseByLabel("Local Retail");
      empLRetailLU.setAllocationFeatureFieldName("211.0");

      AllocationLU empLUOfficeLU = project.getExistingLandUseByLabel("Office");
      empLUOfficeLU.setAllocationFeatureFieldName("212.0");

      AllocationLU empLUPubLU = project
          .getExistingLandUseByLabel("Public/Semi-pub.");
      empLUPubLU.setAllocationFeatureFieldName("251.0");

      AllocationLU industrialLU = project
          .getExistingLandUseByLabel("Industrial");
      industrialLU.setAllocationFeatureFieldName("221.0");

      AllocationLU undevelopedLU = project
          .getExistingLandUseByLabel("Undeveloped");

      AllocationLU agricultureLU = project
          .getExistingLandUseByLabel("Agriculture");

      AllocationConfig allocationConfig = new AllocationConfig();

      // control information
      Map<String, Integer> landUseOrder = new HashMap<String, Integer>();

      landUseOrder.put(conservationLU.getId(), 1);
      landUseOrder.put(industrialLU.getId(), 2);
      landUseOrder.put(empRRetailLU.getId(), 3);
      landUseOrder.put(residentialmixedUseLU.getId(), 4);
      landUseOrder.put(empLUOfficeLU.getId(), 5);
      landUseOrder.put(empLRetailLU.getId(), 6);
      landUseOrder.put(empLUPubLU.getId(), 7);
      landUseOrder.put(groupQuartersLU.getId(), 8);
      landUseOrder.put(residentiallum.getId(), 9);
      landUseOrder.put(residentiallu.getId(), 10);
      landUseOrder.put(localParkLU.getId(), 99);

      conservationLU.setPriority(1);
      industrialLU.setPriority(2);
      empRRetailLU.setPriority(3);
      residentialmixedUseLU.setPriority(4);
      empLUOfficeLU.setPriority(5);
      empLRetailLU.setPriority(6);
      empLUPubLU.setPriority(7);
      groupQuartersLU.setPriority(8);
      residentiallum.setPriority(9);
      residentiallu.setPriority(10);
      localParkLU.setPriority(99);

      DemandScenario demandScenario = project
          .getDemandScenarioByLabel("High Growth");

      // Setting a specific allocation label for demonstration
      // base year since and we ignore it during analysis.
      // fix me later
      allocationConfig.getAllocationColumnsMap().put("2005",
          WifKeys.DEMO_ALLOCATION_0);
      allocationConfig.getAllocationColumnsMap().put("2010", "ALU_1");
      allocationConfig.getAllocationColumnsMap().put("2015", "ALU_2");
      // allocationConfig.getAllocationColumnsMap().put("2020", "ALU_3");
      // Setting undeveloped columns
      allocationConfig.getUndevelopedLUsColumns().add(
          undevelopedLU.getFeatureFieldName());
      allocationConfig.getUndevelopedLUsColumns().add(
          agricultureLU.getFeatureFieldName());
      // allocation information
      AllocationScenario allocationScenarioNoControls = new AllocationScenario();
      allocationScenarioNoControls
          .setFeatureFieldName("Suburbanization-High Growth-No Controls");
      allocationScenarioNoControls.setLandUseOrderMap(landUseOrder);
      allocationScenarioNoControls
          .setLabel("Suburbanization-High Growth-No Controls");
      allocationScenarioNoControls.setDemandScenario(demandScenario);
      allocationScenarioNoControls.setSuitabilityScenario(project
          .getSuitabilityScenarioByLabel("Suburbanization"));
      allocationScenarioNoControls.setSpatialPatternLabel("GROWTH_1");
      project.setAllocationConfig(allocationConfig);

      project.getAllocationScenarios().add(allocationScenarioNoControls);
    } catch (WifInvalidInputException e) {
      LOGGER.error("Problem occurred in createAllocationModule");
    }
    return project;
  }
}
