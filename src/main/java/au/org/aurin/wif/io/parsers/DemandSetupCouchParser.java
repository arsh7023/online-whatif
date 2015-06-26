package au.org.aurin.wif.io.parsers;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemographicData;
import au.org.aurin.wif.model.demand.DemographicTrend;
import au.org.aurin.wif.model.demand.EmploymentDemographicData;
import au.org.aurin.wif.model.demand.EmploymentEntry;
import au.org.aurin.wif.model.demand.EmploymentPastTrendInfo;
import au.org.aurin.wif.model.demand.EmploymentSector;
import au.org.aurin.wif.model.demand.info.EmploymentCurrentData;
import au.org.aurin.wif.model.demand.info.ResidentialCurrentData;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;

/**
 * The Class DemandSetupCouchParser.
 */
@Component
public class DemandSetupCouchParser {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DemandSetupCouchParser.class);

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The project parser. */
  @Autowired
  private ProjectCouchParser projectParser;

  /**
   * Parses the.
   * 
   * @param demandConfig
   *          the demand config
   * @param project
   *          the project
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandConfig parse(DemandConfig demandConfig, WifProject project)
      throws WifInvalidInputException {

    LOGGER.debug("Parsing {} demandConfig ", demandConfig.getId());
    demandConfig = parseSectors(demandConfig);
    project.setDemandConfig(demandConfig);
    // Parse residential datas
    if (demandConfig.getCurrentDemographic() != null) {

      final Set<ResidentialCurrentData> residentialDatas = demandConfig
          .getCurrentDemographic().getResidentialCurrentData();
      // ali delete later if condition
      if (residentialDatas != null) // by ali
      {
        for (final ResidentialCurrentData data : residentialDatas) {
          final AllocationLU allocationLU = allocationLUDao.findAllocationLUById(data
              .getResidentialLUId());
          data.setResidentialLU(allocationLU);
        }
      }// end if
      // Parse employment datas
      final Set<EmploymentCurrentData> employmentDatas = demandConfig
          .getCurrentDemographic().getEmploymentCurrentDatas();
      if (employmentDatas != null) {
        for (final EmploymentCurrentData data : employmentDatas) {
          data.setSector(project.getSectorByLabel(data.getSectorLabel()));
        }
      }
      demandConfig = parsePastEmployment(demandConfig, project);
    }
    // Parse trends
    if (demandConfig.getDemographicTrends() != null) {
      final Set<DemographicTrend> trends = demandConfig.getDemographicTrends();
      for (final DemographicTrend demographicTrend : trends) {
        LOGGER.trace("Parsing trends: {}", demographicTrend.getLabel());

        final Set<DemographicData> demographicData = demographicTrend
            .getDemographicData();
        for (final DemographicData data : demographicData) {
          LOGGER.trace("Parsing data for projection: {}",
              data.getProjectionLabel());
          data.setProjection(demandConfig.getProjectionByLabel(data
              .getProjectionLabel()));
          if (data instanceof EmploymentDemographicData) {
            final EmploymentDemographicData empData = ((EmploymentDemographicData) data);
            empData.setSector(demandConfig.getSectorByLabel(empData
                .getSectorLabel()));
          }
        }
      }
    }
    project.setDemandConfig(demandConfig);
    if (demandConfig.getSectors() != null) {
      project = parseALUSectors(demandConfig, project);
    }
    demandConfig.setWifProject(project);
    return demandConfig;
  }

  /**
   * Parses the alu sectors.
   * 
   * @param demandConfig
   *          the demand config
   * @param project
   *          the project
   * @return the wif project
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public WifProject parseALUSectors(final DemandConfig demandConfig,
      final WifProject project) throws WifInvalidInputException {
    if ((project.getSectors() != null) && (project.getSectors().size() > 0)) {

      final Set<AllocationLU> allocationLandUses = project.getAllocationLandUses();
      for (final AllocationLU allocationLU : allocationLandUses) {
        if (!allocationLU.isResidentialLU()) {
          final Set<String> sectorsLabel = allocationLU.getSectorsLabel();
          if (sectorsLabel != null) {
            LOGGER.trace("For ALU: {}, parsing the following {} sectors",
                allocationLU.getLabel(), sectorsLabel.size());

            for (final String label : sectorsLabel) {
              final EmploymentSector sector = project.getSectorByLabel(label);
              LOGGER.trace("Adding sector: {}", sector.getLabel());
              allocationLU.addEmploymentSector(sector);
            }
          } else {
            LOGGER.trace("For ALU: {}, there are not defined sectors",
                allocationLU.getLabel());
          }
        }
      }
    }
    return project;
  }

  /**
   * Parses the past employment.
   * 
   * @param demandConfig
   *          the demand config
   * @param project
   *          the project
   * @return the demand config
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public DemandConfig parsePastEmployment(final DemandConfig demandConfig,
      final WifProject project) throws WifInvalidInputException {
    // Parse past employment datas
    if (demandConfig.getEmploymentPastTrendInfos() != null) {
      for (final EmploymentPastTrendInfo data : demandConfig
          .getEmploymentPastTrendInfos()) {
        for (final EmploymentEntry entry : data.getEmploymentEntries()) {
          entry.setSector((project.getSectorByLabel(entry.getSectorLabel())));
        }
      }
    }
    return demandConfig;
  }

  /**
   * Parses the sectors.
   * 
   * @param demandConfig
   *          the demand config
   * @return the demand config
   */
  public DemandConfig parseSectors(final DemandConfig demandConfig) {
    final Set<EmploymentSector> sectors = demandConfig.getSectors();
    LOGGER.trace("Parsing {} sectors...", sectors.size());
    for (final EmploymentSector sector : sectors) {
      LOGGER.trace("sector label: {}", sector.getLabel());
      LOGGER.trace("parsing the following sector: " + sector.getLabel());
      if (sector.getAssociatedALUsMap() != null) {
        LOGGER.trace("++++++ sector label: {} has {} associated ALU's...",
            sector.getLabel(), sector.getAssociatedALUsMap().size());
        sector.setAssociatedLUs(projectParser.parseAllocationLUs(sector
            .getAssociatedALUsMap()));
      }
    }
    return demandConfig;
  }

}
