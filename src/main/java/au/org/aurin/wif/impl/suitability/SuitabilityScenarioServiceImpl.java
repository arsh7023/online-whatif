package au.org.aurin.wif.impl.suitability;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import com.vividsolutions.jts.io.ParseException;

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
  @Override
  public SuitabilityScenario createSuitabilityScenario(
      SuitabilityScenario suitabilityScenario, final String projectId)
          throws WifInvalidInputException, WifInvalidConfigException,
          ParsingException {
    if (suitabilityScenario == null) {
      LOGGER
      .error("createSuitabilityScenario failed: suitabilityScenario is null or invalid");
      throw new WifInvalidInputException(
          "createSuitabilityScenario failed: suitabilityScenario is null or invalid");
    }
    final WifProject project = projectService.getProject(projectId);
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

    final SuitabilityScenario savedSuitabilityScenario = suitabilityScenarioDao
        .persistSuitabilityScenario(suitabilityScenario);

    LOGGER.debug("returning the suitabilityScenario with id={}",
        savedSuitabilityScenario.getId());

    final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
        .getSuitabilityRules();
    LOGGER.debug("{} has {} suitability rules associated",
        suitabilityScenario.getLabel(), suitabilityRules.size());
    for (final SuitabilityRule rule : suitabilityRules) {
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
  @Override
  public SuitabilityScenario getSuitabilityScenario(final String id)
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

      final WifProject project = projectService
          .getProjectConfiguration(suitabilityScenario.getProjectId());

      suitabilityScenario = suitabilityParser.parseSuitabilityScenario(
          suitabilityScenario, project);

      suitabilityScenario.setWifProject(project);
      return suitabilityScenario;

    } catch (final IllegalArgumentException e) {

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
  @Override
  public SuitabilityScenario getSuitabilityScenarioNoMapping(final String id)
      throws WifInvalidInputException, WifInvalidConfigException {

    LOGGER.debug("getting the suitabilityScenario with ID={}", id);
    try {
      final SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(id);
      if (suitabilityScenario == null) {
        LOGGER.error("illegal argument, the suitabilityScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityScenario with the ID " + id
            + " supplied was not found ");
      }

      return suitabilityScenario;

    } catch (final IllegalArgumentException e) {

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
  @Override
  public SuitabilityScenario getSuitabilityScenario(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException,
      ParsingException {
    final SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
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
  @Override
  public synchronized void updateSuitabilityScenario(
      final SuitabilityScenario suitabilityScenario, final String projectId)
          throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("updating suitabilityScenario: {}, with id: {}",
        suitabilityScenario.getLabel(), suitabilityScenario.getId());
    try {
      final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
          .getSuitabilityRules();
      LOGGER.debug("{} has {} suitability rules associated",
          suitabilityScenario.getLabel(), suitabilityRules.size());
      for (final SuitabilityRule rule : suitabilityRules) {
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
    } catch (final IllegalArgumentException e) {

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
  @Override
  public void deleteSuitabilityScenario(final String id, final String projectId)
      throws WifInvalidInputException, WifInvalidConfigException {
    LOGGER.info("deleting the suitabilityScenario with ID={}", id);
    try {
      final SuitabilityScenario suitabilityScenario = suitabilityScenarioDao
          .findSuitabilityScenarioById(id);
      if (suitabilityScenario == null) {
        LOGGER.error("illegal argument, the suitabilityScenario with the ID "
            + id + " supplied was not found ");
        throw new InvalidEntityIdException(
            "illegal argument, the suitabilityScenario with the ID " + id
            + " supplied was not found ");
      }
      if (suitabilityScenario.getProjectId().equals(projectId)) {
        final Collection<SuitabilityRule> suitabilityRules = suitabilityScenario
            .getSuitabilityRules();
        LOGGER.debug("{} has {} suitability rules associated",
            suitabilityScenario.getLabel(), suitabilityRules.size());
        for (final SuitabilityRule rule : suitabilityRules) {
          suitabilityRuleDao.deleteSuitabilityRule(rule);
        }
        suitabilityScenarioDao.deleteSuitabilityScenario(suitabilityScenario);
        final WifProject project = projectService.getProject(projectId);
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

    } catch (final IllegalArgumentException e) {

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
  @Override
  public List<SuitabilityScenario> getSuitabilityScenarios(final String projectID)
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
  @Override
  public Boolean getWMSOutcome(final String id, final String areaAnalyzed, final String crsArea)
      throws WifInvalidInputException, WifInvalidConfigException,
      MismatchedDimensionException, NoSuchAuthorityCodeException,
      FactoryException, TransformException, ParseException, IOException,
      CQLException, SuitabilityAnalysisFailedException, ParsingException {
    final SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    return suitabilityAnalyzer.doSuitabilityAnalysisWMS(suitabilityScenario,
        areaAnalyzed, crsArea);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#getOutcome(
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public SimpleFeatureCollection getOutcome(final String id, final String areaAnalyzed,
      final String crsArea) throws WifInvalidInputException,
  WifInvalidConfigException, MismatchedDimensionException,
  NoSuchAuthorityCodeException, CQLException, FactoryException,
  TransformException, ParseException, ParsingException,
  DatabaseFailedException {
    final SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    return suitabilityAnalyzer.doSuitabilityAnalysis(suitabilityScenario,
        areaAnalyzed, crsArea);
  }

  /*
   * (non-Javadoc)
   * @see
   * au.org.aurin.wif.svc.ProjectService#getWMS(au.org.aurin.wif.model.WifProject
   * )
   */
  @Override
  public WMSOutcome getWMS(final String id) throws WifInvalidInputException,
  WifInvalidConfigException, ParsingException {

    final SuitabilityScenario suitabilityScenario = getSuitabilityScenario(id);
    LOGGER
    .info(
        "creating the information for WMS outcome of wif suitabilityScenario={}",
        suitabilityScenario.getLabel());
    final WMSOutcome outcome = new WMSOutcome();
    outcome.setStoreName(geoserverConfig.getStoreName());
    outcome.setWorkspaceName(geoserverConfig.getWorkspace());
    LOGGER.debug("using the following Geoserver store: {} workspace name:  {}",
        outcome.getStoreName(), outcome.getWorkspaceName());
    final WifProject project = wifProjectDao.findProjectById(suitabilityScenario
        .getProjectId());
    outcome.setScoreColumns(suitabilityAnalyzer
        .generateScoreRanges(suitabilityScenario));
    outcome.setAvailableStyles(wifConfig.getSuitabilityStyles());
    LOGGER.debug(
        "using the following {} suitability columns for the outcome layers",
        outcome.getScoreColumns().size());
    for (final String column : outcome.getScoreColumns().keySet()) {
      LOGGER.debug("={}", column);
    }
    final String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    LOGGER.info(
        "creating a suitability outcome for WMS Layer ={}, geo WMS  URL ={}",
        uazDBTable, wifConfig.getServerWMSURL());
    outcome.setLayerName(uazDBTable);
    final String serverWMSURL = wifConfig.getServerWMSURL();
    outcome.setServerURL(serverWMSURL);
    return outcome;
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.suitability.SuitabilityScenarioService#
   * restoreSuitabilityScenario
   * (au.org.aurin.wif.model.suitability.SuitabilityScenario)
   */
  @Override
  public SuitabilityScenario restoreSuitabilityScenario(
      final SuitabilityScenario oldSuitabilityScenario, final WifProject restoreProject)
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
    final Set<SuitabilityRule> suitabilityRules = oldSuitabilityScenario
        .getSuitabilityRules();
    for (final SuitabilityRule oldRule : suitabilityRules) {
      final SuitabilityRule newRule = new SuitabilityRule();
      final String suitabilityLULabel = oldRule.getSuitabilityLUMap().values()
          .iterator().next();
      LOGGER.debug("Restoring {} suitabilityLU...", suitabilityLULabel);
      final SuitabilityLU suitabilityLU = restoreProject
          .getSuitabilityLUByName(suitabilityLULabel);
      newRule.getSuitabilityLUMap().put(suitabilityLU.getId(),
          suitabilityLU.getLabel());
      final Collection<String> convertibleLUsLabels = oldRule.getConvertibleLUsMap()
          .values();
      for (final String luLabel : convertibleLUsLabels) {
        LOGGER.debug("Restoring {} convertibleLU...", luLabel);
        final AllocationLU allocationLU = restoreProject
            .getExistingLandUseByLabel(luLabel);
        newRule.getConvertibleLUsMap().put(allocationLU.getId(),
            allocationLU.getLabel());
      }
      final Set<FactorImportance> factorImportances = oldRule.getFactorImportances();
      for (final FactorImportance oldImportance : factorImportances) {
        final FactorImportance importance = new FactorImportance();
        importance.setImportance(oldImportance.getImportance());
        final String factorLabel = oldImportance.getFactorMap().values().iterator()
            .next();
        LOGGER.debug("Restoring factorImportance for {}...", factorLabel);
        final Factor factor = restoreProject.getFactorByLabel(factorLabel);
        importance.getFactorMap().put(factor.getId(), factor.getLabel());

        final Set<FactorTypeRating> factorTypeRatings = oldImportance
            .getFactorTypeRatings();
        for (final FactorTypeRating oldRating : factorTypeRatings) {
          final FactorTypeRating rating = new FactorTypeRating();
          final String ftLabel = oldRating.getFactorTypeMap().values().iterator()
              .next();
          rating.setScore(oldRating.getScore());
          LOGGER.debug(
              "Restoring factor type importance for {} with score {}...",
              ftLabel, rating.getScore());
          final FactorType factorType = factor.getFactorTypeByLabel(ftLabel);
          rating.getFactorTypeMap().put(factorType.getId(),
              factorType.getLabel());
          importance.getFactorTypeRatings().add(rating);
        }
        newRule.getFactorImportances().add(importance);
      }
      final SuitabilityRule savedRule = suitabilityRuleDao
          .persistSuitabilityRule(newRule);
      restoreSuitabilityScenario.getSuitabilityRules().add(savedRule);
    }
    suitabilityScenarioDao
    .updateSuitabilityScenario(restoreSuitabilityScenario);
    return restoreSuitabilityScenario;
  }


  @Override
  public String duplicateSuitabiliyScenario(final String projectID, final String scenarioID, final String name)
      throws WifInvalidInputException, WifInvalidConfigException, ParsingException
  {
    LOGGER.info("duplicateSuitabiliyScenario");
    String out="";
    try
    {

      final SuitabilityScenario suitabilityScenario = getSuitabilityScenario(scenarioID);
      suitabilityScenario.setLabel(name);

      final WifProject project = projectService
          .getProjectConfiguration(suitabilityScenario.getProjectId());


      final SuitabilityScenario restoredSuitabilityScenario = restoreSuitabilityScenario(suitabilityScenario, project);
      project.getSuitabilityScenariosMap().put(
          restoredSuitabilityScenario.getId(),
          restoredSuitabilityScenario.getLabel());


      wifProjectDao.updateProject(project);
      out= "Success";

    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the suitabilityScenario supplied is invalid ");
      out ="Error:"+ e.getMessage();
    }

    return out;

  }

  @Override
  public String uploadXlsFactors(final String projectID, final String scenarioID, final InputStream inputStream)
      throws WifInvalidInputException, WifInvalidConfigException, ParsingException, IOException {
    LOGGER.info("uploadXlsFactors");
    String out="";
    try
    {

      final HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
      final HSSFSheet sheet = workbook.getSheetAt(0);
      HSSFRow row6;
      HSSFRow row;
      HSSFCell cell;
      HSSFCell factorName;


      LOGGER.info("hii");

      final Map<String, Double> map = new HashMap<String, Double>();


      int rows; // No of rows
      rows = sheet.getPhysicalNumberOfRows();


      final SuitabilityScenario rawSuitabilityScenario =
          getSuitabilityScenario(scenarioID);
      if (!rawSuitabilityScenario.getLabel().equals(sheet.getRow(2).getCell(0).getStringCellValue()))
      {
        out = "{\"result\" : \"" +"The scenario name is not match!" +"\"}";
        return out;
      }
      final WifProject rawProject = projectService
          .getProjectConfiguration(rawSuitabilityScenario.getProjectId());
      if (!rawProject.getLabel().equals(sheet.getRow(4).getCell(0).getStringCellValue()))
      {
        out = "{\"result\" : \"" +"The project name is not match!" +"\"}";
        return out;
      }

      int cols = 0; // No of columns
      int tmp = 0;
      // This trick ensures that we get the data properly even if it doesn't start from first few rows
      for(int i = 6;  i < rows; i++) {
        row = sheet.getRow(i);
        if(row != null) {
          tmp = sheet.getRow(i).getPhysicalNumberOfCells();
          if(tmp > cols) {
            cols = tmp;
          }
        }
      }


      for(int c = 1; c < cols; c++) {


        row6 = sheet.getRow(6);
        cell = row6.getCell(c);

        final String headerLU = cell.getStringCellValue();
        LOGGER.info(headerLU);



        LOGGER.info("********");

        String lbl="";
        String lbluse="";
        for(int r = 8; r < rows; r++) {

          Boolean lswHeader = false;

          row = sheet.getRow(r);
          if(row != null)
          {

            cell = row.getCell((short)c);
            factorName = row.getCell(0);
            lbluse= factorName.getStringCellValue();
            if (!factorName.getStringCellValue().equals(lbl))
            {
              if (lbl.equals(""))
              {
                lbl =  factorName.getStringCellValue();
                lbluse= "Header:" + lbl;
                lswHeader= true;
              }
              else
              {
                if (factorName.getStringCellValue().length() <= lbl.length())
                {
                  lbl =  factorName.getStringCellValue();
                  lbluse= "Header:" + lbl;
                  lswHeader= true;
                }
                else
                {
                  if (factorName.getStringCellValue().substring(0, lbl.length()).equals(lbl))
                  {
                    int k = lbl.length();
                    final String str = factorName.getStringCellValue();
                    if (str.substring(k, k+1).equals("_"))
                    {
                      k = factorName.getStringCellValue().length();

                      String lname=str.substring(lbl.length()+1,k);
                      lname = lname.replaceAll(",", "");
                      lname = lname.replaceAll("Greater ", ">");
                      lname = lname.replaceAll("Lower ", "<");

                      //lbluse= "Header:" + lbl + ":Child:" +str.substring(lbl.length()+1,k);
                      lbluse= "Header:" + lbl + ":Child:" +lname;
                      lswHeader= false;

                      Double newvalue=0.0;
                      if (cell.getCellType() == 1)
                      {

                        newvalue= Double.parseDouble(cell.getStringCellValue());
                      }
                      else if (cell.getCellType() == 0)
                      {

                        newvalue= cell.getNumericCellValue();
                      }


                    }
                    else
                    {
                      lbl =  factorName.getStringCellValue();
                      lbluse= "Header:" + lbl;
                      lswHeader= true;
                    }
                  }
                  else
                  {
                    lbl =  factorName.getStringCellValue();
                    lbluse= "Header:" + lbl;
                    lswHeader= true;
                  }
                }
              }
            }//end for

            if (lswHeader== true)
            {

            }
            else
            {

            }


            if(cell != null) {
              // Your code here
              //LOGGER.info(lbluse + " : "+cell.getCellType());
              if (cell.getCellType() == 1)
              {

                LOGGER.info(headerLU+":"+lbluse + ":"+cell.getStringCellValue());
                map.put(headerLU+":"+lbluse, Double.valueOf(cell.getStringCellValue()));
              }
              else
              {
                LOGGER.info(headerLU+":"+lbluse + ":"+cell.getNumericCellValue());
                map.put(headerLU+":"+lbluse, cell.getNumericCellValue());
              }
            }

          }
        }
      }//end for c


      for (final Map.Entry<String, Double> entry : map.entrySet()) {
        LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
        //LOGGER.info("Key : " + entry.getKey().split(":").length);
      }

      //////////////////////////////////////////////////////////////////////////////

      final SuitabilityScenario oldSuitabilityScenario =
          getSuitabilityScenario(scenarioID);
      //          final SuitabilityScenario oldSuitabilityScenario = suitabilityScenarioService
      //              .getSuitabilityScenario("d2ee05b32886f857ef1dd7272211b38d");


      final WifProject project = projectService
          .getProjectConfiguration(oldSuitabilityScenario.getProjectId());

      final Set<SuitabilityRule> setRules =  new HashSet<SuitabilityRule>();
      final Set<SuitabilityRule> suitabilityRules = oldSuitabilityScenario
          .getSuitabilityRules();
      for (final SuitabilityRule oldRule : suitabilityRules) {

        final SuitabilityRule newRule = new SuitabilityRule();

        newRule.setId(oldRule.getId());
        final String suitabilityLULabel = oldRule.getSuitabilityLUMap().values()
            .iterator().next();
        // LOGGER.debug("Restoring {} suitabilityLU...", suitabilityLULabel);
        final SuitabilityLU suitabilityLU = project
            .getSuitabilityLUByName(suitabilityLULabel);
        newRule.getSuitabilityLUMap().put(suitabilityLU.getId(),
            suitabilityLU.getLabel());
        final Collection<String> convertibleLUsLabels = oldRule.getConvertibleLUsMap()
            .values();
        for (final String luLabel : convertibleLUsLabels) {
          //LOGGER.debug("Restoring {} convertibleLU...", luLabel);
          final AllocationLU allocationLU = project
              .getExistingLandUseByLabel(luLabel);
          newRule.getConvertibleLUsMap().put(allocationLU.getId(),
              allocationLU.getLabel());
        }
        final Set<FactorImportance> factorImportances = oldRule.getFactorImportances();
        for (final FactorImportance oldImportance : factorImportances) {
          final Double newvalue=oldImportance.getImportance();

          final FactorImportance importance = new FactorImportance();

          final String factorLabel = oldImportance.getFactorMap().values().iterator()
              .next();
          //LOGGER.debug("Restoring factorImportance for {}...", factorLabel);
          final Factor factor = project.getFactorByLabel(factorLabel);
          importance.getFactorMap().put(factor.getId(), factor.getLabel());

          for (final Map.Entry<String, Double> entry : map.entrySet()) {
            //
            //System.out.println("Key : " + entry.getKey().split(":").length);
            if (entry.getKey().split(":").length==3)
            {
              final String[] str = entry.getKey().split(":");
              if (str[0].equals(suitabilityLULabel))
              {
                if (str[2].equals(factorLabel))
                {
                  //importance.setImportance(2.0);
                  importance.setImportance(entry.getValue());
                  //LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
                }
              }
            }
          }

          final Set<FactorTypeRating> factorTypeRatings = oldImportance
              .getFactorTypeRatings();
          for (final FactorTypeRating oldRating : factorTypeRatings) {
            final FactorTypeRating rating = new FactorTypeRating();
            final String ftLabel = oldRating.getFactorTypeMap().values().iterator()
                .next();

            for (final Map.Entry<String, Double> entry : map.entrySet()) {
              if (entry.getKey().split(":").length==5)
              {
                final String[] str = entry.getKey().split(":");
                if (str[0].equals(suitabilityLULabel))
                {
                  if (str[2].equals(factorLabel))
                  {

                    if (str[4].equals(ftLabel))
                    {
                      //rating.setScore(2.0);
                      rating.setScore(entry.getValue());
                      //LOGGER.info("Key : " + entry.getKey() + " Value : " + entry.getValue());
                      if (suitabilityLULabel.equals("Retail"))
                      {
                        if (factorLabel.equals("streams"))
                        {
                          int cc=0;
                          cc=2;
                          if (ftLabel.equals("Outside buffers"))
                          {

                          }
                        }
                      }

                    }

                  }
                }
              }
            }

            final FactorType factorType = factor.getFactorTypeByLabel(ftLabel);
            rating.getFactorTypeMap().put(factorType.getId(),
                factorType.getLabel());
            importance.getFactorTypeRatings().add(rating);
          }
          newRule.getFactorImportances().add(importance);


          newRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
              oldRule.getId()).getRevision());
          LOGGER.info("updating the newrule with Rev={}", newRule.getRevision());
          suitabilityRuleDao.updateSuitabilityRule(newRule);

          newRule.setRevision(suitabilityRuleDao.findSuitabilityRuleById(
              oldRule.getId()).getRevision());
          setRules.add(newRule);

        }

      }

      oldSuitabilityScenario.setSuitabilityRules(setRules);
      oldSuitabilityScenario.setRevision(suitabilityScenarioDao
          .findSuitabilityScenarioById(oldSuitabilityScenario.getId())
          .getRevision());
      LOGGER.info("updating the suitabilityScenario with Rev={}",
          oldSuitabilityScenario.getRevision());
      updateSuitabilityScenario(oldSuitabilityScenario,
          project.getId());

      //////////////////////////////////////////////////////////////////////////////

      inputStream.close();

      out = "{\"result\" : \"" +"success" +"\"}";



    } catch (final IllegalArgumentException e) {

      LOGGER.error("illegal argument, the suitabilityScenario supplied is invalid ");
      out ="Error: " + e.getMessage();
      out = "{\"result\" : \"" + out +"\"}";
    }

    return out;
  }

}
