package au.org.aurin.wif.io.parsers;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.LocalAreaRequirement;
import au.org.aurin.wif.model.demand.LocalJurisdiction;
import au.org.aurin.wif.model.demand.data.LocalData;
import au.org.aurin.wif.model.demand.data.ProjectedData;
import au.org.aurin.wif.model.demand.info.DemandInfo;
import au.org.aurin.wif.model.demand.info.EmploymentDemandInfo;
import au.org.aurin.wif.model.demand.info.PreservationDemandInfo;
import au.org.aurin.wif.model.demand.info.ProjectedDemandInfo;
import au.org.aurin.wif.model.demand.info.ResidentialDemandInfo;
import au.org.aurin.wif.repo.demand.AreaRequirementDao;
import au.org.aurin.wif.repo.demand.LocalAreaRequirementDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;

/**
 * The Class CouchParser.
 */
@Component
public class DemandScenarioCouchParser {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandScenarioCouchParser.class);

  /** The area requirement dao. */
  @Autowired
  private AreaRequirementDao areaRequirementDao;

  /** The local area requirement dao. */
  @Autowired
  private LocalAreaRequirementDao localAreaRequirementDao;

  /**
   * Parses the.
   * 
   * @param demandScenario
   *          the demand scenario
   * @param demandConfig
   *          the demand config
   * @param project
   *          the project
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandScenario parse(final DemandScenario demandScenario,
      final DemandConfig demandConfig, final WifProject project)
      throws WifInvalidInputException {
    LOGGER.debug("Parsing demandConfigId {}, with demandscenarioLabel {}",
        demandConfig.getId(), demandScenario.getLabel());
    demandScenario.setFeatureFieldName(demandScenario.getLabel());
    final Set<DemandInfo> demandInfos = demandScenario.getDemandInfos();
    for (final DemandInfo demandInfo : demandInfos) {
      demandInfo.setDemandScenario(demandScenario);
      if (demandInfo instanceof ResidentialDemandInfo) {
        final ResidentialDemandInfo resDInfo = (ResidentialDemandInfo) demandInfo;
        LOGGER.trace("Parsing ResidentialDemandInfo for {} ",
            resDInfo.getResidentialLUId());
        final AllocationLU allocationLU = project
            .getExistingLandUseById(resDInfo.getResidentialLUId());
        resDInfo.setResidentialLU(allocationLU);

        LOGGER.trace("assigning demand information to allocationLU  {}",
            allocationLU.getLabel());
        allocationLU.addDemandInfo(resDInfo);

      } else if (demandInfo instanceof ProjectedDemandInfo) {
        final ProjectedDemandInfo proDInfo = (ProjectedDemandInfo) demandInfo;
        final Set<ProjectedData> datas = proDInfo.getProjectedDatas();
        for (final ProjectedData data : datas) {
          data.setProjection(demandConfig.getProjectionByLabel(data
              .getProjectionLabel()));
        }
        if (demandInfo instanceof EmploymentDemandInfo) {

          final EmploymentDemandInfo eDInfo = (EmploymentDemandInfo) demandInfo;
          LOGGER.trace("Parsing EmploymentDemandInfo for {}",
              eDInfo.getSectorLabel());
          eDInfo.setSector(demandConfig.getSectorByLabel(eDInfo
              .getSectorLabel()));

        } else { // PreservationDemandInfo
          LOGGER.trace("Found PreservationDemandInfo");
          final PreservationDemandInfo pdfInfo = (PreservationDemandInfo) demandInfo;
          final AllocationLU allocationLU = project
              .getExistingLandUseById(pdfInfo.getAllocationLUId());
          pdfInfo.setAllocationLU(allocationLU);

          LOGGER.trace("assigning demand information to allocationLU  {}",
              allocationLU.getLabel());
          allocationLU.addDemandInfo(pdfInfo);
        }
      }
    }
    final String trendLabel = demandScenario.getDemographicTrendLabel();
    // since in the first time creation, we don't know the name
    if (trendLabel != "") {
      demandScenario.setDemographicTrend(demandConfig
          .getTrendByLabel(trendLabel));
    }
    // Now we parse the employment demand info to the allocation that uses
    final Set<AllocationLU> allocationLandUses = project
        .getAllocationLandUses();
    for (final AllocationLU allocationLU : allocationLandUses) {

      if (allocationLU.isEmploymentLU()) {
        LOGGER.trace("Parsing EmploymentLU {}", allocationLU.getLabel());
        final Set<EmploymentDemandInfo> demandInfoByLU = demandScenario
            .getEmploymentDemandInfoByLU(allocationLU);
        for (final EmploymentDemandInfo employmentDemandInfo : demandInfoByLU) {
          LOGGER.trace("Associating EmploymentDemandInfo to {} for {}",
              allocationLU.getLabel(), employmentDemandInfo.getSector()
                  .getLabel());
          Boolean lsw = false;
          for (final DemandInfo ein : allocationLU.getDemandInfos()) {
            if (ein.equals(employmentDemandInfo)) {
              lsw = true;
            }
          }
          if (lsw == false) {
            allocationLU.addDemandInfo(employmentDemandInfo);
          }
          lsw = false;
          final Set<EmploymentSector> empsS = allocationLU
              .getEmploymentSectors();
          for (final EmploymentSector emps : empsS) {
            if (emps.equals(demandConfig.getSectorByLabel(employmentDemandInfo
                .getSectorLabel()))) {
              lsw = true;
            }
          }
          if (lsw == false) {
            allocationLU.addEmploymentSector(demandConfig
                .getSectorByLabel(employmentDemandInfo.getSectorLabel()));
          }

        }
      }
    }
    // Now we parse localDatas
    final Set<LocalData> localDatas = demandScenario.getLocalDatas();
    for (final LocalData localData : localDatas) {
      LOGGER.trace("Parsing LocalJurisdiction {}",
          localData.getLocalJurisdictionLabel());
      localData.setLocalJurisdiction(demandConfig
          .getLocalJurisdictionByLabel(localData.getLocalJurisdictionLabel()));
      localData.setProjection(demandConfig.getProjectionByLabel(localData
          .getProjectionLabel()));
    }
    demandScenario.setWifProject(project);
    demandScenario.setDemandConfig(demandConfig);
    return demandScenario;
  }

  /**
   * Parses the area requirements.
   * 
   * @param demandScenario
   *          the demand scenario
   * @return the demand scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandScenario parseAreaRequirements(
      final DemandScenario demandScenario) throws WifInvalidInputException {
    LOGGER.trace("Parsing areaRequirements, with demandscenario {}",
        demandScenario.getId());
    // Now we parse area requirements
    final List<AreaRequirement> areaRequirements = areaRequirementDao
        .getAreaRequirements(demandScenario.getId());
    for (final AreaRequirement areaRequirement : areaRequirements) {
      areaRequirement.setDemandScenario(demandScenario);
      LOGGER.trace("Parsing areaRequirement {}, belonging to a scenarioId {}",
          areaRequirement.getId(), demandScenario.getId());
      LOGGER.trace("AllocationLUId {} for projection {}",
          areaRequirement.getAllocationLUId(),
          areaRequirement.getProjectionLabel());
      areaRequirement.setProjection(demandScenario.getWifProject()
          .getProjectionByLabel(areaRequirement.getProjectionLabel()));
      final AllocationLU allocationLU = demandScenario.getWifProject()
          .getExistingLandUseById(areaRequirement.getAllocationLUId());
      allocationLU.getAreaRequirements().add(areaRequirement);
      LOGGER.trace("Adding area requirement to {}", allocationLU.getLabel());
      areaRequirement.setAllocationLU(allocationLU);
    }
    final List<LocalAreaRequirement> localAreaRequirements = localAreaRequirementDao
        .getLocalAreaRequirements(demandScenario.getId());

    for (final LocalAreaRequirement areaRequirement : localAreaRequirements) {
      areaRequirement.setDemandScenario(demandScenario);
      LOGGER.trace(
          "Parsing LocalAreaRequirement {}, belonging to a scenarioId {}",
          areaRequirement.getId(), demandScenario.getId());
      LOGGER.trace("AllocationLUId {} for projection {}",
          areaRequirement.getAllocationLUId(),
          areaRequirement.getProjectionLabel());
      areaRequirement.setProjection(demandScenario.getWifProject()
          .getProjectionByLabel(areaRequirement.getProjectionLabel()));
      final AllocationLU allocationLU = demandScenario.getWifProject()
          .getExistingLandUseById(areaRequirement.getAllocationLUId());
      allocationLU.getAreaRequirements().add(areaRequirement);
      final LocalJurisdiction localJurisdiction = demandScenario
          .getDemandConfig().getLocalJurisdictionByLabel(
              areaRequirement.getLocalJurisdictionLabel());
      areaRequirement.setLocalJurisdiction(localJurisdiction);
      LOGGER.trace(
          "Adding LocalAreaRequirement to {}, belong to jurisdiction {}",
          allocationLU.getLabel(), localJurisdiction.getLabel());
      areaRequirement.setAllocationLU(allocationLU);
    }
    return demandScenario;
  }

}
