/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.impl.datacreator;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationDemandSetupData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.EmploymentEntry;
import au.org.aurin.wif.model.demand.EmploymentPastTrendInfo;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentData;
import au.org.aurin.wif.model.demand.info.ResidentialCurrentData;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.svc.datacreator.DBDemandSetupDataCreatorService;
import au.org.aurin.wif.svc.datacreator.DBSuitabilityDataCreatorService;

/**
 * The Class DBDemandSetupDataCreatorServiceImpl.
 */
@Component("DBDemandSetupDataCreator")
public class DBDemandSetupDataCreatorServiceImpl implements
    DBDemandSetupDataCreatorService {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The creator. */
  @Autowired
  private DBSuitabilityDataCreatorService creator;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DBDemandSetupDataCreatorServiceImpl.class);

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.DBDemandSetupDataCreatorService#createDemandSetupModule
   * (java.lang.String, java.lang.String)
   */
  public WifProject createDemandSetupModule(final String projectId,
      final String suitabilityScenarioId, final String demandConfigId,
      final String manualdemandConfigId) throws Exception {
    WifProject project = creator.createSuitabilityModule(projectId,
        suitabilityScenarioId);
    DemandConfig demandConfig = null;

    if (demandConfig == null) {
      project = DemonstrationDemandSetupData.createDemandSetupModule(project);
      demandConfig = project.getDemandConfig();
      project.setDemandConfigId(demandConfigId);

      demandConfig.setProjectId(projectId);
      demandConfig.setId(demandConfigId);
      // Map sectors
      final Set<EmploymentSector> sectors = demandConfig.getSectors();
      LOGGER.trace("Loading {} sectors...", sectors.size());
      for (final EmploymentSector sector : sectors) {
        LOGGER.trace("sector label: {}", sector.getLabel());
        final Set<AllocationLU> associatedLUs = sector.getAssociatedLUs();
        for (final AllocationLU allocationLU : sector.getAssociatedLUs()) {
          LOGGER.trace("... associated ALU label: {}", allocationLU.getLabel());
        }
        sector.setAssociatedALUsMap(mapper.mapAllocationLUs(associatedLUs));
      }
      // Map current residential datas
      for (final ResidentialCurrentData data : demandConfig
          .getCurrentDemographic().getResidentialCurrentData()) {
        data.setResidentialLUId(data.getResidentialLU().getId());
      }
      // Map current employment data
      for (final EmploymentCurrentData data : demandConfig
          .getCurrentDemographic().getEmploymentCurrentDatas()) {
        data.setSectorLabel(data.getSector().getLabel());
      }
      // Map past employment datas
      for (final EmploymentPastTrendInfo data : demandConfig
          .getEmploymentPastTrendInfos()) {
        for (final EmploymentEntry entry : data.getEmploymentEntries()) {
          entry.setSectorLabel(entry.getSector().getLabel());
        }
      }
      final DemandConfig createdDemandConfig = demandConfigDao
          .persistDemandConfig(demandConfig);

      // Updating allocationLU associations
      final Set<AllocationLU> allocationLandUses = project
          .getAllocationLandUses();
      for (final AllocationLU allocationLU : allocationLandUses) {
        LOGGER.trace("... ALU label: {}", allocationLU.getLabel());
        final Set<EmploymentSector> employmentSectors = allocationLU
            .getEmploymentSectors();
        for (final EmploymentSector employmentSector : employmentSectors) {
          LOGGER.trace("Associated sector label: {}",
              employmentSector.getLabel());
          allocationLU.addSectorLabel(employmentSector.getLabel());
        }
        allocationLUDao.updateAllocationLU(allocationLU);
        LOGGER.trace("Updated allocationLU: {}, {} ", allocationLU.getId(),
            allocationLU.getRevision());
      }

      project.setDemandConfigId(demandConfigId);

      //

      wifProjectDao.updateProject(project);

    } else {
      project.setDemandConfig(demandConfig);

    }

    LOGGER.trace("Demonstration demandConfig with ID {} is loaded ",
        demandConfigId);
    return project;
  }
}
