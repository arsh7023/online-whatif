package au.org.aurin.wif.impl.suitability;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.org.aurin.wif.config.GeoServerConfig;
import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.io.DatabaseFailedException;
import au.org.aurin.wif.exception.validate.SuitabilityAnalysisFailedException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.lsa.SuitabilityAnalyzer;
import au.org.aurin.wif.io.CouchMapper;
import au.org.aurin.wif.io.parsers.SuitabilityCouchParser;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorType;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityRuleDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.AllocationLUService;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.vividsolutions.jts.io.ParseException;

/**
 * The Class SuitabilityScenarioServiceImpl.
 */
@Service
@Qualifier("suitabilityScenarioService")
public class SuitabilityScenarioServiceImpl implements
    SuitabilityScenarioService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 79823546576734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityScenarioServiceImpl.class);

  /** The suitability scenario dao. */
  @Autowired
  private SuitabilityScenarioDao suitabilityScenarioDao;

  /** The geoserver config. */
  @Autowired
  private GeoServerConfig geoserverConfig;

  /** The wif config. */
  @Autowired
  private WifConfig wifConfig;

  /** The mapper. */
  @Autowired
  private CouchMapper mapper;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The allocation lu service. */
  @Resource
  private AllocationLUService allocationLUService;

  /** The suitability analyzer. */
  @Autowired
  private SuitabilityAnalyzer suitabilityAnalyzer;

  /** The suitability rule dao. */
  @Autowired
  private SuitabilityRuleDao suitabilityRuleDao;

  /** The parser. */
  @Autowired
  private SuitabilityCouchParser suitabilityParser;

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace("SuitabilityScenario Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#
   * createSuitabilityScenario
   * (au.org.aurin.wif.model.suitability.SuitabilityScenario, java.lang.String)
   */
  public SuitabilityScenario createSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    if (suitabilityScenario == null) {
      LOGGER
          .error("createSuitabilityScenario failed: suitabilityScenario is null or invalid");
      throw new WifInvalidInputException(
          "createSuitabilityScenario failed: suitabilityScenario is null or invalid");
    }
    WifProject project = projectService.getProject(projectId);
    suitabilityScenario = suitabilityParser.parseSuitabilityScenario(
        suitabilityScenario, project);
    suitabilityScenario.setProjectId(projectId);
    project.getSuitabilityConfig().setNotConvertableScore(
        WifKeys.NOT_CONVERTABLE_SCORE);
    project.getSuitabilityConfig().setNotSuitableScore(
        WifKeys.NOT_SUITABLE_SCORE);
    project.getSuitabilityConfig().setNotDevelopableScore(
        WifKeys.NOT_DEVELOPABLE_SCORE);
    project.getSuitabilityConfig().setUndefinedScore(WifKeys.UNDEFINED_SCORE);
    LOGGER.debug("persisting the suitabilityScenario={}",
        suitabilityScenario.getLabel());
    suitabilityScenario.setReady(true);

    SuitabilityScenario savedSuitabilityScenario = suitabilityScenarioDao
        .persistSuitabilityScenario(suitabilityScenario);

    LOGGER.debug("returning the suitabilityScenario with id={}",
        savedSuitabilityScenario.getId());

    Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());
    for (SuitabilityRule rule : suitabilityRules) {
      rule.setScenarioId(savedSuitabilityScenario.getId());
      suitabilityRuleDao.persistSuitabilityRule(rule);
    }

    suitabilityScenarioDao.updateSuitabilityScenario(savedSuitabilityScenario);
    project.getSuitabilityScenariosMap().put(savedSuitabilityScenario.getId(),
        savedSuitabilityScenario.getLabel());
    // TODO It would be more efficient if we use the dao here
    // projectService.updateProject(project);
    project.getSuitabilityScenariosMap().put(savedSuitabilityScenario.getId(),
        savedSuitabilityScenario.getLabel());
    wifProjectDao.updateProject(project);
    return savedSuitabilityScenario;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#
   * getSuitabilityScenario(java.lang.String)
   */
  public SuitabilityScenario getSuitabilityScenario(String id)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {

    LOGGER.debug("getting the suitabilityScenario with ID={}", id);
    try {
      SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(id);
      if (suitabilityScenario == null) {
        LOGGER.error("illegal argument, the suitabilityScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityScenario with the ID " + id
                + " supplied was not found ");
      }

      WifProject project = projectService
          .getProjectConfiguration(suitabilityScenario.getProjectId());

      suitabilityScenario = suitabilityParser.parseSuitabilityScenario(
          suitabilityScenario, project);

      suitabilityScenario.setWifProject(project);
      return suitabilityScenario;

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#
   * getSuitabilityScenarioNoMapping(java.lang.String)
   */
  public SuitabilityScenario getSuitabilityScenarioNoMapping(String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.debug("getting the suitabilityScenario with ID={}", id);
    try {
      SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(id);
      if (suitabilityScenario == null) {
        LOGGER.error("illegal argument, the suitabilityScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityScenario with the ID " + id
                + " supplied was not found ");
      }

      return suitabilityScenario;

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ");
      throw new WifInvalidInputException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityScenarioService#getSuitabilityScenario(
   * java.lang.String)
   */
  public SuitabilityScenario getSuitabilityScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    if (suitabilityScenario.getProjectId().equals(projectId)) {
      return suitabilityScenario;
    } else {
      LOGGER
          .error("illegal argument, the suitabilityScenario supplied doesn't belong to project: "
              + projectId);
      throw new WifInvalidInputException(
          "illegal argument, the suitabilityScenario supplied doesn't belong to this  project: "
              + projectId);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityScenarioService#updateSuitabilityScenario
   * (au.org.aurin.wif.model.allocation.SuitabilityScenario, java.lang.String)
   */
  public synchronized void updateSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("updating suitabilityScenario: {}, with id: {}",
        suitabilityScenario.getLabel(), suitabilityScenario.getId());
    try {
      Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
          .getSuitabilityRules();
      LOGGER.debug("{} has {} suitability rules associated",
          suitabilityScenario.getLabel(), suitabilityRules.size());
      for (SuitabilityRule rule : suitabilityRules) {
        rule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
            rule.getId()).getRevision());
        LOGGER.debug("updating the rule with Rev={}", rule.getRevision());
        suitabilityRuleDao.updateSuitabilityRule(rule);
      }
      suitabilityScenario.setRevision(suitabilityScenarioDao
          .findSuitabilityScenarioById(suitabilityScenario.getId())
          .getRevision());
      LOGGER.debug("updating the suitabilityScenario with Rev={}",
          suitabilityScenario.getRevision());
      suitabilityScenarioDao.updateSuitabilityScenario(suitabilityScenario);
    } catch (IllegalArgumentException e) {

      LOGGER
          .error("illegal argument, the suitabilityScenario supplied is invalid ");
      throw new WifInvalidInputException(
          "illegal argument, the suitabilityScenario supplied is invalid ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityScenarioService#deleteSuitabilityScenario
   * (java.lang.String, java.lang.String)
   */
  public void deleteSuitabilityScenario(String id, String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the suitabilityScenario with ID={}", id);
    try {
      SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(id);
      if (suitabilityScenario == null) {
        LOGGER.error("illegal argument, the suitabilityScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityScenario with the ID " + id
                + " supplied was not found ");
      }
      if (suitabilityScenario.getProjectId().equals(projectId)) {
        Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
            .getSuitabilityRules();
        LOGGER.debug("{} has {} suitability rules associated",
            suitabilityScenario.getLabel(), suitabilityRules.size());
        for (SuitabilityRule rule : suitabilityRules) {
          suitabilityRuleDao.deleteSuitabilityRule(rule);
        }
        suitabilityScenarioDao.deleteSuitabilityScenario(suitabilityScenario);
        WifProject project = projectService.getProject(projectId);
        project.getSuitabilityScenariosMap().remove(id);
        wifProjectDao.updateProject(project);
      } else {
        LOGGER
            .error("illegal argument, the suitabilityScenario supplied doesn't belong to project: "
                + projectId);
        throw new WifInvalidInputException(
            "illegal argument, the suitabilityScenario supplied doesn't belong to project: "
                + projectId);
      }

    } catch (IllegalArgumentException e) {

      LOGGER.error("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ");
      throw new InvalidEntityIdException("illegal argument, the ID " + id
          + " supplied doesn't identify a valid suitabilityScenario ", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.SuitabilityScenarioService#getSuitabilityScenarios
   * (java.lang.String)
   */
  public List<SuitabilityScenario> getSuitabilityScenarios(String projectID)
      throws WifInvalidInputException {
    LOGGER.info("getting all suitabilityScenarios for projectID: {} ",
        projectID);

    return suitabilityScenarioDao.getSuitabilityScenarios(projectID);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#getWMSOutcome
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  public Boolean getWMSOutcome(String id, String areaAnalyzed, String crsArea)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      CQLException, SuitabilityAnalysisFailedException, ParsingException {
    SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    return suitabilityAnalyzer.doSuitabilityAnalysisWMS(suitabilityScenario,
        areaAnalyzed, crsArea);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#getOutcome(
   * java.lang.String, java.lang.String, java.lang.String)
   */
  public SimpleFeatureCollection getOutcome(String id, String areaAnalyzed,
      String crsArea) throws WifInvalidInputException,
      WifInvalidConfigException, MismatchedDimensionException,
      NoSuchAuthorityCodeException, CQLException, FactoryException,
      TransformException, ParseException, ParsingException,
      DatabaseFailedException {
    SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    return suitabilityAnalyzer.doSuitabilityAnalysis(suitabilityScenario,
        areaAnalyzed, crsArea);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#getWMS(au.org.aurin.wif.model.WifProject
   * )
   */
  public WMSOutcome getWMS(String id) throws WifInvalidInputException,
      WifInvalidConfigException, ParsingException {

    SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    LOGGER
        .info(
            "creating the information for WMS outcome of wif suitabilityScenario={}",
            suitabilityScenario.getLabel());
    WMSOutcome outcome = new WMSOutcome();
    outcome.setStoreName(geoserverConfig.getStoreName());
    outcome.setWorkspaceName(geoserverConfig.getWorkspace());
    LOGGER.debug("using the following Geoserver store: {} workspace name:  {}",
        outcome.getStoreName(), outcome.getWorkspaceName());
    WifProject project = wifProjectDao.findProjectById(suitabilityScenario
        .getProjectId());
    outcome.setScoreColumns(suitabilityAnalyzer
        .generateScoreRanges(suitabilityScenario));
    outcome.setAvailableStyles(wifConfig.getSuitabilityStyles());
    LOGGER.debug(
        "using the following {} suitability columns for the outcome layers",
        outcome.getScoreColumns().size());
    for (String column : outcome.getScoreColumns().keySet()) {
      LOGGER.debug("={}", column);
    }
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    LOGGER.info(
        "creating a suitability outcome for WMS Layer ={}, geo WMS  URL ={}",
        uazDBTable, wifConfig.getServerWMSURL());
    outcome.setLayerName(uazDBTable);
    String serverWMSURL = wifConfig.getServerWMSURL();
    outcome.setServerURL(serverWMSURL);
    return outcome;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#
   * restoreSuitabilityScenario
   * (au.org.aurin.wif.model.suitability.SuitabilityScenario)
   */
  public SuitabilityScenario restoreSuitabilityScenario(
      SuitabilityScenario oldSuitabilityScenario, WifProject restoreProject)
      throws WifInvalidInputException {
    LOGGER.info("Restoring {} suitability scenario...",
        oldSuitabilityScenario.getLabel());
    SuitabilityScenario restoreSuitabilityScenario = new SuitabilityScenario();
    restoreSuitabilityScenario.setLabel(oldSuitabilityScenario.getLabel());
    restoreSuitabilityScenario.setFeatureFieldName(oldSuitabilityScenario
        .getFeatureFieldName());
    restoreSuitabilityScenario.setProjectId(restoreProject.getId());
    restoreSuitabilityScenario = suitabilityScenarioDao
        .persistSuitabilityScenario(restoreSuitabilityScenario);
    Set<SuitabilityRule> suitabilityRules = oldSuitabilityScenario
        .getSuitabilityRules();
    for (SuitabilityRule oldRule : suitabilityRules) {
      SuitabilityRule newRule = new SuitabilityRule();
      String suitabilityLULabel = oldRule.getSuitabilityLUMap().values()
          .iterator().next();
      LOGGER.debug("Restoring {} suitabilityLU...", suitabilityLULabel);
      SuitabilityLU suitabilityLU = restoreProject
          .getSuitabilityLUByName(suitabilityLULabel);
      newRule.getSuitabilityLUMap().put(suitabilityLU.getId(),
          suitabilityLU.getLabel());
      Collection<String> convertibleLUsLabels = oldRule.getConvertibleLUsMap()
          .values();
      for (String luLabel : convertibleLUsLabels) {
        LOGGER.debug("Restoring {} convertibleLU...", luLabel);
        AllocationLU allocationLU = restoreProject
            .getExistingLandUseByLabel(luLabel);
        newRule.getConvertibleLUsMap().put(allocationLU.getId(),
            allocationLU.getLabel());
      }
      Set<FactorImportance> factorImportances = oldRule.getFactorImportances();
      for (FactorImportance oldImportance : factorImportances) {
        FactorImportance importance = new FactorImportance();
        importance.setImportance(oldImportance.getImportance());
        String factorLabel = oldImportance.getFactorMap().values().iterator()
            .next();
        LOGGER.debug("Restoring factorImportance for {}...", factorLabel);
        Factor factor = restoreProject.getFactorByLabel(factorLabel);
        importance.getFactorMap().put(factor.getId(), factor.getLabel());

        Set<FactorTypeRating> factorTypeRatings = oldImportance
            .getFactorTypeRatings();
        for (FactorTypeRating oldRating : factorTypeRatings) {
          FactorTypeRating rating = new FactorTypeRating();
          String ftLabel = oldRating.getFactorTypeMap().values().iterator()
              .next();
          rating.setScore(oldRating.getScore());
          LOGGER.debug(
              "Restoring factor type importance for {} with score {}...",
              ftLabel, rating.getScore());
          FactorType factorType = factor.getFactorTypeByLabel(ftLabel);
          rating.getFactorTypeMap().put(factorType.getId(),
              factorType.getLabel());
          importance.getFactorTypeRatings().add(rating);
        }
        newRule.getFactorImportances().add(importance);
      }
      SuitabilityRule savedRule = suitabilityRuleDao
          .persistSuitabilityRule(newRule);
      restoreSuitabilityScenario.getSuitabilityRules().add(savedRule);
    }
    suitabilityScenarioDao
        .updateSuitabilityScenario(restoreSuitabilityScenario);
    return restoreSuitabilityScenario;
  }
}
