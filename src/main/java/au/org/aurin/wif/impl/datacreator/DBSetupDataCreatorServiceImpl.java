/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.impl.datacreator;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationSetupData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorTypeDao;
import au.org.aurin.wif.svc.datacreator.DBSetupDataCreatorService;

/**
 * The Class Model2CouchDBTest.
 */
@Component("DBSetupDataCreator")
public class DBSetupDataCreatorServiceImpl implements DBSetupDataCreatorService {

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The allocation lu dao. */
  @Autowired
  private CouchAllocationLUDao allocationLUDao;

  /** The suitability lu dao. */
  @Autowired
  private SuitabilityLUDao suitabilityLUDao;

  /** The factor dao. */
  @Autowired
  private CouchFactorDao factorDao;

  /** The factor type dao. */
  @Autowired
  private CouchFactorTypeDao factorTypeDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The new project id. */
  private String newProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DBSetupDataCreatorServiceImpl.class);

  /**
   * Persist project setup test.
   *
   * @param projectId the project id
   * @return the wif project
   * @throws Exception the exception
   */
  public WifProject createSetupModule(String projectId) throws Exception {
    WifProject project = null;
    if (projectId != null) {
      LOGGER.info("Finding project with id: {}", projectId);
      project = wifProjectDao.findProjectById(projectId);
    }
    if (project == null) {
      LOGGER.info("Project not found. Creating project with id: {}", projectId);
      project = DemonstrationSetupData.createProject();
      if (projectId != null) {
        project.setId(projectId);
      }
      project = wifProjectDao.persistProject(project);
      project = DemonstrationSetupData.createSetupModule(project);

      newProjectId = project.getId();
      LOGGER.debug("projectUuidForSetup = " + project.getId());

      // create allocation land uses
      Collection<AllocationLU> allocationLandUses = project
          .getAllocationLandUses();
      LOGGER.info("Loading {} allocation land uses (ALU)...",
          allocationLandUses.size());
      for (AllocationLU allocationLU : allocationLandUses) {
        LOGGER.info("ALU label: {}", allocationLU.getLabel());
        LOGGER
            .info("--- UAZ value: {}, is not developable?: {}",
                allocationLU.getFeatureFieldName(),
                allocationLU.isNotDevelopable());
        allocationLU.setProjectId(newProjectId);
        AllocationLU createdAllocationLU = allocationLUDao
            .persistAllocationLU(allocationLU);

        LOGGER.debug("createdAllocationLU: " + createdAllocationLU.getId());
      }

      // create factors
      Collection<Factor> factors = project.getFactors();
      LOGGER.info("Loading {} suitablity factors...", factors.size());
      for (Factor aSuitabilityFactor : factors) {
        LOGGER.info("- Suitability Factor label: {}",
            aSuitabilityFactor.getLabel());
        LOGGER.info("- Factor UAZ column name: {}",
            aSuitabilityFactor.getFeatureFieldName());
        aSuitabilityFactor.setProjectId(newProjectId);
        Factor factor = factorDao.persistFactor(aSuitabilityFactor);
        LOGGER.debug("factorUuid: " + factor.getId());

        // factor types
        for (FactorType aFactorType : aSuitabilityFactor.getFactorTypes()) {
          LOGGER.debug("::  aFactorType label: {}", aFactorType.getLabel());
          LOGGER.debug(":: aFactorType UAZ value: {}", aFactorType.getValue());
          aFactorType.setFactorId(factor.getId());
          FactorType createdFactorType = factorTypeDao
              .persistFactorType(aFactorType);
        }
        factorDao.updateFactor(factor);

      }
      // create suitability land uses
      Collection<SuitabilityLU> suitabilityLUs = (Collection<SuitabilityLU>) project
          .getSuitabilityLUs();
      LOGGER.info("Loading {} Suitablity Land Uses...", suitabilityLUs.size());
      for (SuitabilityLU suitabilityLU : suitabilityLUs) {
        LOGGER.info("Suitability LU label: {}", suitabilityLU.getLabel());
        LOGGER.info("... SLU score UAZ value: {}",
            suitabilityLU.getFeatureFieldName());
        LOGGER.info("... it has {} associated Land Uses...", suitabilityLU
            .getAssociatedALUs().size());
        for (AllocationLU allocationLU : suitabilityLU.getAssociatedALUs()) {
          LOGGER.info("... associated ALU label: {}", allocationLU.getLabel());
        }
        suitabilityLU.setProjectId(newProjectId);
        mapper.mapSuitabilityLU(suitabilityLU);
        SuitabilityLU createdSuitabilityLU = suitabilityLUDao
            .persistSuitabilityLU(suitabilityLU);
      }

      wifProjectDao.updateProject(project);
    }
    LOGGER.debug("Demonstration project  with ID {} is loaded ", projectId);
    return project;
  }
}
