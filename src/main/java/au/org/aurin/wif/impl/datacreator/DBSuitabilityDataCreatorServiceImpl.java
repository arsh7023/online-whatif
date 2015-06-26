/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.impl.datacreator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationSetupData;
import au.org.aurin.wif.io.demonstrationdata.DemonstrationSuitabilityData;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.impl.CouchAllocationLUDao;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityLUDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorDao;
import au.org.aurin.wif.repo.suitability.impl.CouchFactorTypeDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.datacreator.DBSuitabilityDataCreatorService;

/**
 * The Class Model2CouchDBTest.
 */
@Component("DBSuitabilityDataCreator")
public class DBSuitabilityDataCreatorServiceImpl implements
    DBSuitabilityDataCreatorService {

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

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The new project id. */
  private String newProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(DBSuitabilityDataCreatorServiceImpl.class);

  /**
   * Persist project suitability module test.
   * 
   * @param projectId
   *          the project id
   * @param suitabilityScenarioId
   *          the suitability scenario id
   * @return the wif project
   * @throws Exception
   *           the exception
   */
  public WifProject createSuitabilityModule(String projectId,
      String suitabilityScenarioId) throws Exception {
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
        suitabilityLU.setProjectId(newProjectId);
        mapper.mapSuitabilityLU(suitabilityLU);
        SuitabilityLU createdSuitabilityLU = suitabilityLUDao
            .persistSuitabilityLU(suitabilityLU);
      }
      // ######################
      // CREATE IN SUITABILITY
      // ######################

      project = DemonstrationSuitabilityData.createSuitabilityModule(project);

      LOGGER.debug("projectUuidForSuitability = " + project.getId());
      Collection<SuitabilityScenario> suitabilityScenarios = project
          .getSuitabilityScenarios();
      LOGGER.info("*****   Loading {} suitablity scenarios...",
          suitabilityScenarios.size());

      for (SuitabilityScenario suitabilityScenario : suitabilityScenarios) {
        if (suitabilityScenarioId != null) {
          LOGGER.info("Creating scenario with id: {}", suitabilityScenarioId);
          suitabilityScenario.setId(suitabilityScenarioId);
        }
        suitabilityScenario.setProjectId(newProjectId);

        SuitabilityScenario createdSuitabilityScenario = suitabilityScenarioDao
            .persistSuitabilityScenario(suitabilityScenario);
        Map<String, String> idLabelMap = mapper
            .getIdLabelMap(suitabilityScenario);
        project.getSuitabilityScenariosMap().putAll(idLabelMap);
        Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
            .getSuitabilityRules();
        LOGGER.debug("{} has {} suitability rules associated",
            suitabilityScenario.getLabel(), suitabilityRules.size());
        for (SuitabilityRule rule : suitabilityRules) {
          rule.setScenarioId(createdSuitabilityScenario.getId());
          LOGGER.info(
              "++++++ suitability LU label: {} has {} convertibles ALU's", rule
                  .getSuitabilityLU().getLabel(), rule.getConvertibleLUs()
                  .size());

          Collection<AllocationLU> conversions = rule.getConvertibleLUs();
          for (AllocationLU aLU : conversions) {
            LOGGER.debug(
                " ALU label {}, with attribute name {} is convertible",
                aLU.getLabel(), aLU.getFeatureFieldName());
          }
          mapper.mapSuitabilityRule(rule);

          SuitabilityRule createdSuitabilityRule = suitabilityRuleDao
              .persistSuitabilityRule(rule);

        }
        suitabilityScenarioDao
            .updateSuitabilityScenario(createdSuitabilityScenario);
      }
      wifProjectDao.updateProject(project);
    }
    LOGGER.debug("Demonstration project  with ID {} is loaded ",
        WifKeys.TEST_PROJECT_ID);
    return project;
  }

  /**
   * Creates the simple suitability scenario.
   * 
   * @param project
   *          the project
   * @param suitabilityScenarioId
   *          the suitability scenario id
   * @return the suitability scenario
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  public static SuitabilityScenario createSimpleSuitabilityScenario(
      WifProject project, String suitabilityScenarioId)
      throws WifInvalidInputException {
    SuitabilityScenario suitabilityScenario = new SuitabilityScenario();
    suitabilityScenario.setLabel("Suburbanization");
    suitabilityScenario.setId(suitabilityScenarioId);
    suitabilityScenario.setReady(true);
    suitabilityScenario.setWifProject(project);
    suitabilityScenario.setSuitabilityRules(new HashSet<SuitabilityRule>());
    project.getSuitabilityScenarios().add(suitabilityScenario);

    AllocationLU agricultureLU = project
        .getExistingLandUseByLabel("Agriculture");
    AllocationLU undevelopedLU = project
        .getExistingLandUseByLabel("Undeveloped");
    SuitabilityLU suitabilityLU = project.getSuitabilityLUByName("Residential");

    Factor slopes = project.getFactorByLabel("slopes");
    FactorType slopeft1 = slopes.getFactorTypeByLabel("<6%");

    // Residential Suitability Rule
    SuitabilityRule residentialSuitabilityRule = new SuitabilityRule();
    residentialSuitabilityRule.setSuitabilityScenario(suitabilityScenario);
    residentialSuitabilityRule.setSuitabilityLU(suitabilityLU);
    residentialSuitabilityRule
        .setFactorImportances(new HashSet<FactorImportance>());

    FactorImportance residentialImportanceSlope = new FactorImportance();
    HashMap<String, String> importanceMap = new HashMap<String, String>();
    importanceMap.put(slopes.getId(), slopes.getLabel());
    residentialImportanceSlope.setFactorMap(importanceMap);
    residentialImportanceSlope.setFactor(slopes);
    residentialImportanceSlope.setImportance(100.0);
    residentialImportanceSlope
        .setFactorTypeRatings(new HashSet<FactorTypeRating>());
    residentialImportanceSlope.setSuitabilityRule(residentialSuitabilityRule);

    FactorTypeRating residentialSlopeFtr1 = new FactorTypeRating();
    residentialSlopeFtr1.setFactorType(slopeft1);
    residentialSlopeFtr1.setScore(100.0);
    HashMap<String, String> residentialSlopeFtr1Map = new HashMap<String, String>();
    residentialSlopeFtr1Map.put(slopeft1.getId(), slopeft1.getLabel());

    residentialSlopeFtr1.setFactorImportance(residentialImportanceSlope);
    Set<AllocationLU> convertibleLUsResidential = new HashSet<AllocationLU>();
    convertibleLUsResidential.add(agricultureLU);
    convertibleLUsResidential.add(undevelopedLU);
    residentialSuitabilityRule.setConvertibleLUs(convertibleLUsResidential);
    suitabilityScenario.getSuitabilityRules().add(residentialSuitabilityRule);
    residentialSuitabilityRule.setScenarioId(suitabilityScenario.getId());

    HashMap<String, String> cluMaps = new HashMap<String, String>();
    Collection<AllocationLU> conversions = residentialSuitabilityRule
        .getConvertibleLUs();
    for (AllocationLU aLU : conversions) {
      LOGGER.debug(" ALU label {}, with attribute name {} is convertible",
          aLU.getLabel(), aLU.getFeatureFieldName());
      cluMaps.put(aLU.getId(), aLU.getLabel());
    }
    residentialSuitabilityRule.setConvertibleLUsMap(cluMaps);
    HashMap<String, String> sluMap = new HashMap<String, String>();
    sluMap.put(suitabilityLU.getId(), suitabilityLU.getLabel());
    residentialSuitabilityRule.setSuitabilityLUMap(sluMap);

    return suitabilityScenario;
  }
}
