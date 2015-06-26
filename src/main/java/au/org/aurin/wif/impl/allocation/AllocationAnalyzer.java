package au.org.aurin.wif.impl.allocation;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.IncompleteDemandScenarioException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.io.DataSourceFactory;
import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.io.PostgisDataStoreConfig;
import au.org.aurin.wif.io.jdbcDataStoreConfig;
import au.org.aurin.wif.io.parsers.AllocationCouchParser;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationControlScenario;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.allocation.GrowthPatternALU;
import au.org.aurin.wif.model.allocation.InfrastructureALU;
import au.org.aurin.wif.model.allocation.InfrastructureUses;
import au.org.aurin.wif.model.allocation.PlannedALU;
import au.org.aurin.wif.model.allocation.control.ALURule;
import au.org.aurin.wif.model.demand.AreaRequirement;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.demand.DemandOutcome;
import au.org.aurin.wif.model.demand.DemandScenario;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.allocation.AllocationControlScenarioDao;
import au.org.aurin.wif.repo.allocation.AllocationScenarioDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.repo.demand.DemandScenarioDao;
import au.org.aurin.wif.svc.AllocationLUService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.demand.DemandScenarioService;

import com.vividsolutions.jts.io.ParseException;

/**
 * The Class AllocationAnalyzer.
 */
@Component
public class AllocationAnalyzer {

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The geodata filterer. */
  @Autowired
  private GeodataFilterer geodataFilterer;

  /** The parser. */
  @Autowired
  private AllocationCouchParser allocationParser;

  /** The parser. */
  @Autowired
  private AllocationLUService allocationLUService;

  @Autowired
  @Qualifier(value = "wifDataStoreConfig")
  private PostgisDataStoreConfig postgisDataStoreConfig;

  @Autowired
  @Qualifier(value = "myjdbcDataStoreConfig")
  private jdbcDataStoreConfig myjdbcDataStoreConfig;

  @Autowired
  private DataSourceFactory dataSourceFactory;

  /** The Allocation config dao. */
  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  /** The Demand config dao. */
  @Autowired
  private DemandConfigDao demandConfigDao;

  /** The allocation scenario dao. */
  @Autowired
  private AllocationControlScenarioDao AllocationControlScenarioDao;

  /** The wif allocationScenario dao. */
  @Autowired
  private AllocationScenarioDao allocationScenarioDao;

  /** The wif demandScenario dao. */
  @Autowired
  private DemandScenarioDao demandScenarioDao;

  @Autowired
  private DemandScenarioService demandScenarioService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(AllocationAnalyzer.class);

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
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /**
   * Do allocation analysis.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @return the boolean
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws TransformException
   *           the transform exception
   * @throws ParseException
   *           the parse exception
   * @throws CQLException
   *           the cQL exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws IncompleteDemandScenarioException
   * @throws ParsingException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  /**
   * @param allocationScenario
   * @return
   * @throws WifInvalidConfigException
   * @throws WifInvalidInputException
   * @throws MismatchedDimensionException
   * @throws TransformException
   * @throws ParseException
   * @throws CQLException
   * @throws IOException
   * @throws NoSuchAuthorityCodeException
   * @throws FactoryException
   * @throws ParsingException
   * @throws IncompleteDemandScenarioException
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  public String doAllocationAnalysis(final AllocationScenario allocationScenario)
      throws WifInvalidConfigException, WifInvalidInputException,
      MismatchedDimensionException, TransformException, ParseException,
      CQLException, IOException, NoSuchAuthorityCodeException,
      FactoryException, ParsingException, IncompleteDemandScenarioException,
      ClassNotFoundException, SQLException {
    // TODO unite all these logging information in one single method
    LOGGER.info("processing allocation analysis for ={}",
        allocationScenario.getLabel());
    final WifProject project = allocationScenario.getWifProject();
    // WifProject project = projectService.getProjectConfiguration(projectId);

    final String uazDBTable = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    LOGGER.debug("uaz spatial table: {}", uazDBTable);
    final String areaLabel = project.getAreaLabel();
    LOGGER.info("area UAZ label: {}", areaLabel);

    final String existingLULabel = project.getExistingLUAttributeName();
    LOGGER.debug("area UAZ label: {}", existingLULabel);
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());

    if (allocationScenario.isManual()) {

      // projections.addAll(allocationScenario.getManualdemandScenario()
      // .getManualDemandConfig().getProjections());
      // projections.addAll(project.getManualDemandConfig().getProjections());

      final DemandConfig demandConfig = demandConfigDao
          .findDemandConfigById(project.getDemandConfigId());

      // projections.addAll(project.getDemandConfig().getProjections());
      projections.addAll(demandConfig.getProjections());
    } else {
      projections.addAll(project.getProjections());
    }
    final SuitabilityScenario suitabilityScn = allocationScenario
        .getSuitabilityScenario();
    LOGGER.info("Suitability Scenario name: {}", suitabilityScn.getLabel());
    DemandScenario demandScn;
    DemandOutcome manualdemandScn;
    Set<AreaRequirement> outcome = new HashSet<AreaRequirement>();
    if (allocationScenario.isManual()) {
      final String scenarioID = allocationScenario.getManualdemandScenarioId();

      final List<DemandScenario> listDemand = demandScenarioService
          .getDemandScenarios(project.getId());
      Boolean lsw = false;
      for (final DemandScenario dsn : listDemand) {
        if (dsn.getId().equals(scenarioID)) {
          lsw = true;
        }
      }
      if (lsw == false) {
        manualdemandScn = allocationScenario.getManualdemandScenario();
        LOGGER.info("Manual Demand Scenario label: {}",
            manualdemandScn.getLabel());
        outcome = allocationScenario.getManualdemandScenario()
            .getAreaRequirements();
      } else {
        final List<AreaRequirement> outAreas = demandScenarioService
            .getOutcome(scenarioID);
        for (final AreaRequirement areaRequirement : outAreas) {
          areaRequirement.setProjectionLabel(areaRequirement.getProjection()
              .getLabel());
          outcome.add(areaRequirement);
        }
      }

    } else {
      demandScn = allocationScenario.getDemandScenario();
      LOGGER.info("Demand Scenario label: {}", demandScn.getLabel());
    }

    // setting up taking into account current projection year is not a
    // projection by itself,per se,
    final Projection current = projections.first();
    LOGGER.info("current year projection: {}", current.getLabel());
    final NavigableSet<Projection> projectedSet = projections.tailSet(
        projections.first(), false);

    //
    final Map<String, Integer> maplu = allocationScenario.getLandUseOrderMap();
    // int i = maplu.size();

    final Set<AllocationLU> setAlu = new HashSet<AllocationLU>();
    final Set<AllocationLU> setAluSorted = new HashSet<AllocationLU>();
    final Iterator iterator = maplu.entrySet().iterator();
    while (iterator.hasNext()) {
      final Map.Entry mapEntry = (Map.Entry) iterator.next();

      final AllocationLU lu = allocationLUService.getAllocationLU(mapEntry
          .getKey().toString(), allocationScenario.getProjectId());
      lu.setPriority((Integer) mapEntry.getValue());
      setAlu.add(lu);
    }

    allocationScenario.setLandUseOrder(setAlu);
    final Set<AllocationLU> landUseOrder = allocationScenario.getLandUseOrder();
    LOGGER
        .info(
            "About to perform allocation analysis of {} land uses for the following projections: ",
            landUseOrder.size());

    // for (AllocationLU allocationLU : landUseOrder) {
    for (final AllocationLU allocationLU : setAlu) {
      LOGGER.debug("Priority of {} for {} ", allocationLU.getPriority(),
          allocationLU.getLabel());
      LOGGER.debug("id {} Number of area requirements {} ",
          allocationLU.getId(), allocationLU.getAreaRequirements().size());
    }
    for (final Projection projection : projectedSet) {
      LOGGER.debug("-- projection year: {}", projection.getLabel());
    }
    LOGGER.debug("obtaining feature story from: {}", uazDBTable);
    // AllocationConfigs allocationConfig = allocationScenario
    // .getAllocationConfig();

    final String AllocationConfigsId = project.getAllocationConfigsId();

    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    // String[] columnList = allocationConfig.getAllocationColumnsMap().values()
    // .toArray(new String[0]);

    final ArrayList<String> columnList1 = new ArrayList<String>();
    for (final Projection proj : projections) {
      columnList1.add(proj.getLabel());
    }

    final String[] columnList = new String[columnList1.size()];
    for (int ind = 0; ind < columnList1.size(); ind++)

    {
      columnList[ind] = "ALU_" + columnList1.get(ind);
    }

    Arrays.sort(columnList);

    if (project.getId().equals(WifKeys.TEST_PROJECT_ID)) {
      geodataFinder.updateUAZcolumnsALUDemonstration(uazDBTable, columnList);
    } else {
      geodataFinder.updateUAZcolumnsALU(project.getExistingLUAttributeName(),
          uazDBTable, columnList);
    }

    // SimpleFeatureStore featureStore = geodataFinder
    // .getFeatureStorefromDB(uazDBTable);
    // 0. Reset analysis layers
    // resetAnalysisLayer(allocationScenario, featureStore);

    if (allocationScenario.isManual()) {
      for (final AllocationLU futureLU : landUseOrder) {
        Boolean lsw = false;
        final Collection<SuitabilityLU> suitabilityLUs = project
            .getSuitabilityLUs();
        LOGGER.trace("Loading {} Suitablity Land Uses...",
            suitabilityLUs.size());
        for (final SuitabilityLU suitabilityLU : suitabilityLUs) {
          LOGGER.trace("Suitability LU label: {}", suitabilityLU.getLabel());
          LOGGER.trace("... SLU score UAZ value: {}",
              suitabilityLU.getFeatureFieldName());
          final Set<AllocationLU> associatedALUs = suitabilityLU
              .getAssociatedALUs();
          for (final AllocationLU allocationLU : associatedALUs) {
            if (allocationLU.getLabel().equals(futureLU.getLabel())) {
              if (lsw == false) { // just assign first
                futureLU.setAssociatedLU(suitabilityLU);
                lsw = true;
              }
            }
          }
        }
      }
    }

    // find out area size for first year before starting allocation for further
    // controlling.
    final Map<String, Double> mapLanduseSize = new HashMap<String, Double>();
    final Map<String, Double> mapLanduseExtra = new HashMap<String, Double>();
    for (final AllocationLU futureLU : landUseOrder) {
      final Double dfirstYear = geodataFilterer.getSumAreaFirstYear(
          allocationScenario, futureLU);
      mapLanduseSize.put(futureLU.getLabel(), dfirstYear);
      LOGGER.info("The Area size for " + futureLU.getLabel()
          + " in first year is: " + dfirstYear.toString());

      mapLanduseExtra.put(futureLU.getLabel(), 0.0);
    }

    // 1. Allocate within each projection and for each land-use
    for (final Projection projection : projectedSet) {
      LOGGER.info("performing allocation analysis for projection year: {}",
          projection.getLabel());

      for (int i = 0; i < setAlu.size(); i++) {
        for (final AllocationLU futureLU : landUseOrder) {
          if (futureLU.getPriority() == i + 1) {
            LOGGER.info(" allocating land use: {}, priority = {}",
                futureLU.getLabel(), futureLU.getPriority());
            String scoreLabel = "";
            if (futureLU.getAssociatedLU() != null) {
              LOGGER.debug("is associated to : {}", futureLU.getAssociatedLU()
                  .getLabel());
              scoreLabel = futureLU.getAssociatedLU().getFeatureFieldName();

              // ///////////////////////////////////////////////
              // control scenario
              final ArrayList<String> GrowthPatternFields = new ArrayList<String>();
              String PlannedSQL = "";
              String InfrastructureSQL = "";

              if (!allocationScenario.getControlScenarioId().equals("None")) {
                final AllocationControlScenario controlScenario = AllocationControlScenarioDao
                    .findAllocationControlScenarioById(allocationScenario
                        .getControlScenarioId());

                // Growth Pattern : Added to Order By section

                // String GrowthPatternSQL = "";
                if (controlScenario.getGrowthPatternControl() == true) {

                  final Set<GrowthPatternALU> gFields = allocationConfig
                      .getGrowthPatternALUs();

                  for (final String item : controlScenario
                      .getGrowthPatternControlLabels()) {
                    for (final GrowthPatternALU growthPattern : gFields) {
                      if (growthPattern.getLabel().equals(item)) {
                        GrowthPatternFields.add(growthPattern.getFieldName());
                      }
                    }

                  }

                }

                // planned land use; gaining query filter
                final String plannedALUsFieldName = allocationConfig
                    .getPlannedALUsFieldName();

                if (controlScenario.getPlannedlandUseControl() == true) {
                  Boolean lswPlanned = false;
                  PlannedSQL = "(";
                  final Set<PlannedALU> pFields = allocationConfig
                      .getPlannedALUs();
                  int cnt = 0;
                  for (final PlannedALU plannedALU : pFields) {
                    final String plabel = plannedALU.getLabel();
                    final Map<String, String> spLU = plannedALU
                        .getAssociatedALUsMap();
                    final Iterator spMap = spLU.entrySet().iterator();
                    while (spMap.hasNext()) {
                      final Map.Entry mapEntry = (Map.Entry) spMap.next();
                      // LOGGER.info("The planned land use key is: "
                      // + mapEntry.getKey() + ",value is :"
                      // + mapEntry.getValue());
                      if (mapEntry.getValue().toString()
                          .equals(futureLU.getLabel())) {
                        lswPlanned = true;
                        if (cnt == 0) {
                          PlannedSQL = PlannedSQL + "\"" + plannedALUsFieldName
                              + "\"" + "='" + plabel + "'";
                        } else {
                          PlannedSQL = PlannedSQL + " OR " + "\""
                              + plannedALUsFieldName + "\"" + "='" + plabel
                              + "'";
                        }
                        cnt = cnt + 1;
                      }
                    }
                  }
                  PlannedSQL = PlannedSQL + ")";
                  if (lswPlanned == false) {
                    PlannedSQL = "";
                  }
                }

                LOGGER.info("The PlannedSQL is: " + PlannedSQL);

                // Infrastructure; gaining query filter

                String InfrastructureSQLMaster = "";

                if (controlScenario.getInfrastructureControl() == true) {

                  final Set<InfrastructureUses> infUses = controlScenario
                      .getInfrastructureUses();

                  int cntInfraItem = 0;
                  for (final String item : controlScenario
                      .getInfrastructureControlLabels()) {

                    Boolean lswInf = false;
                    for (final InfrastructureUses infUse : infUses) {
                      if (infUse.getLanduseName().equals(futureLU.getLabel())) {

                        int cntInfra = 0;
                        Map<String, String> map = new HashMap<String, String>();
                        map = infUse.getInfrastructureMap();
                        final Iterator itMap = map.entrySet().iterator();
                        while (itMap.hasNext()) {

                          final Map.Entry mapEntry = (Map.Entry) itMap.next();

                          // new
                          if (item.equals(mapEntry.getKey())) {

                            if (!mapEntry.getValue().equals("N/A")) {
                              lswInf = true;
                              // InfrastructureSQL = InfrastructureSQL + "(";
                              // if (cntInfra == 0) {
                              // if (!InfrastructureSQL.equals("")) {
                              // InfrastructureSQL = InfrastructureSQL
                              // + " AND (";
                              // } else {
                              // InfrastructureSQL = InfrastructureSQL + "(";
                              // }
                              // } else {
                              if (!InfrastructureSQL.equals("")) {
                                InfrastructureSQL = InfrastructureSQL
                                    + " AND (";
                              } else {
                                InfrastructureSQL = InfrastructureSQL + "(";
                              }
                              // }

                              for (final InfrastructureALU infField : allocationConfig
                                  .getInfrastructureALUs()) {
                                if (infField.getLabel().equals(
                                    mapEntry.getKey())) {
                                  if (mapEntry.getValue().equals("Required")) {
                                    InfrastructureSQL = InfrastructureSQL
                                        + "\"" + infField.getFieldName() + "\""
                                        + "<=" + projection.getLabel();
                                  } else if (mapEntry.getValue().equals(
                                      "Excluded")) {
                                    InfrastructureSQL = InfrastructureSQL + "("
                                        + "\"" + infField.getFieldName() + "\""
                                        + ">" + projection.getLabel() + ")";
                                  }
                                  InfrastructureSQL = InfrastructureSQL + ")";
                                }
                              }
                            }
                          }

                          // LOGGER.info("The Infrastructure key is: "
                          // + mapEntry.getKey() + ",value is :"
                          // + mapEntry.getValue());

                          cntInfra = cntInfra + 1;

                        }
                      }
                    }
                    // InfrastructureSQL = InfrastructureSQL + ")";
                    if (lswInf == false) {
                      if (InfrastructureSQL.length() < 2) {
                        InfrastructureSQL = "";
                      }
                    }

                    if (!InfrastructureSQL.equals("")) {

                      if (cntInfraItem == 0) {
                        InfrastructureSQLMaster = InfrastructureSQLMaster
                            + InfrastructureSQL;
                      } else {

                        if (!InfrastructureSQLMaster.equals("")) {
                          InfrastructureSQLMaster = InfrastructureSQLMaster
                              + " AND (" + InfrastructureSQL + ")";
                        } else {
                          InfrastructureSQLMaster = InfrastructureSQLMaster
                              + InfrastructureSQL;
                        }
                      }

                    }

                    cntInfraItem = cntInfraItem + 1;
                  }// end for new (string item)

                }// end if

                // InfrastructureSQL = InfrastructureSQLMaster;
                LOGGER.info(
                    "For the projection year {}, the InfrastructureSQL is: {}",
                    projection.getLabel(), InfrastructureSQL);
              }

              // ///////

              // //////// //new claudia. finding outcome we needed
              AreaRequirement areaRequirement = null;
              AreaRequirement manualareaRequirement = null;
              if (allocationScenario.isManual()) {
                // for (final AreaRequirement ar : allocationScenario
                // .getManualdemandScenario().getAreaRequirements()) {
                for (final AreaRequirement ar : outcome) {
                  if (ar.getProjectionLabel().equals(projection.getLabel())) {
                    if (ar.getAllocationLULabel().equals(futureLU.getLabel())) {
                      manualareaRequirement = ar;
                    }
                  }
                }
              } else {
                areaRequirement = futureLU.getAreaRequirement(projection,
                    allocationScenario.getDemandScenario());
              }
              final Double newLandUseArea = 0.0;
              if (allocationScenario.isManual()) {
                if (manualareaRequirement != null) {
                  // LOGGER.info(
                  // "@@@@@ For {} allocating  area requirement of: {}",
                  // futureLU.getLabel(),
                  // manualareaRequirement.getRequiredArea());
                }
              } else {
                // LOGGER.info("@@@@@ For {} allocating  area requirement of: {}",
                // futureLU.getLabel(), areaRequirement.getRequiredArea());
              }
              Double remainingArea = 0.0;
              Double offsetremainingArea = 0.0;
              if (allocationScenario.isManual()) {
                if (manualareaRequirement != null) {
                  remainingArea = manualareaRequirement.getRequiredArea();
                }
              } else {
                remainingArea = areaRequirement.getRequiredArea();
              }

              if (futureLU.getLabel().equals("Medium Density - Urbanised")) {
                int yyyy = 0;
                yyyy = 99999;
              }

              // if land extra allocated before.
              offsetremainingArea = remainingArea
                  - mapLanduseExtra.get(futureLU.getLabel());
              if (mapLanduseExtra.get(futureLU.getLabel()) > 0) {
                LOGGER.info("We have  "
                    + mapLanduseExtra.get(futureLU.getLabel()).toString()
                    + " as Extra allocated before for land use "
                    + futureLU.getLabel() + " in projection year:"
                    + projection.getLabel() + " So original demand is "
                    + remainingArea.toString() + " but actual demand is :"
                    + offsetremainingArea.toString());
              } else {
                LOGGER.info("We have  "
                    + mapLanduseExtra.get(futureLU.getLabel()).toString()
                    + " as Extra allocated before for land use "
                    + futureLU.getLabel() + " in projection year:"
                    + projection.getLabel() + " So original demand is "
                    + remainingArea.toString() + " and actual demand is :"
                    + offsetremainingArea.toString());
              }

              // //new we know what is the demand size of the landuse. sending
              // this value to the getAllocationRule to take into account in the
              // SQL rule.
              if (offsetremainingArea > 0) {
                String sql = "";
                // sql =
                // "select  score_sdsd, \"URBVALUE\", ABS(\"URBVALUE\" - 10)  from wifdemo.wif_2cbcb5d481fe66bcea28dccb8c011260 order by score_sdsd desc,  ABS(\"URBVALUE\" - 10) asc ";
                sql = geodataFilterer.getAllocationRuleNew(futureLU,
                    allocationScenario, scoreLabel, existingLULabel,
                    PlannedSQL, InfrastructureSQL, GrowthPatternFields,
                    offsetremainingArea, projection);

                geodataFinder.updateALlocationColumnNew(sql);
                LOGGER
                    .info(
                        "allocation sucessfully done for : {} in Projection year {} ",
                        futureLU.getLabel(), projection.getLabel());
                // mapLanduseExtra.put(futureLU.getLabel(), 0.0);
                LOGGER.info("putting 0.0 as Extra for land use "
                    + futureLU.getLabel()
                    + " after allocation, in projection year:"
                    + projection.getLabel());
              } else {
                // comment. it can be done in controls section
                // final Double offValue = mapLanduseExtra
                // .get(futureLU.getLabel()) - remainingArea;
                // mapLanduseExtra.put(futureLU.getLabel(),
                // mapLanduseExtra.get(futureLU.getLabel()) - remainingArea);

                // LOGGER
                // .info("putting "
                // + offValue.toString()
                // + " as Extra for land use "
                // + futureLU.getLabel()
                // +
                // " since we had extra and allcocation didnt happen in projection year:"
                // + projection.getLabel());
              }

              // Class.forName("org.postgresql.Driver");
              // Connection connection = null;

              // final String connStr = "jdbc:postgresql://"
              // + myjdbcDataStoreConfig.getHost() + ":"
              // + myjdbcDataStoreConfig.getPort() + "/"
              // + myjdbcDataStoreConfig.getDatabaseName();
              // // connection = DriverManager.getConnection(
              // // "jdbc:postgresql://localhost:5432/whatifdb", "whatif", "");

              // connection = DriverManager.getConnection(connStr,
              // myjdbcDataStoreConfig.getUser(),
              // myjdbcDataStoreConfig.getPassword());

              // PreparedStatement pst = null;
              // ResultSet rs = null;

              // pst = connection.prepareStatement(sql);
              // rs = pst.executeQuery();

              // if (allocateLandUseNew(projection, futureLU,
              // allocationScenario,
              // remainingArea, scoreLabel, rs,
              // myjdbcDataStoreConfig.getSchema(), uazDBTable) == true) {

              // } else {
              // LOGGER.info(
              // "no more area available for allocation!! {} unallocated!",
              // futureLU.getLabel());
              // }

              // connection.close();

              // ///////////

              // //////////////// ////end claudia

              // /old code
              // final ALURule rule =
              // geodataFilterer.getAllocationRule(futureLU,
              // allocationScenario, scoreLabel, existingLULabel, PlannedSQL,
              // InfrastructureSQL, GrowthPatternFields, remainingArea);
              // LOGGER
              // .debug("About to retrieve the features from the store with the given rule query.... ");
              //
              // final Transaction transaction = new
              // DefaultTransaction("update");
              // try {// TODO perhaps there is a better way to make sure that
              // // transactions always close!
              // // resetUAZForAllocation
              //
              // // instead of using autowired bean; declare a new one, since
              // // auto wired bean it is not updated.
              // GeospatialDataSource wifDataSource;
              // wifDataSource = dataSourceFactory
              // .createGeospatialDataSource(postgisDataStoreConfig
              // .getDataStoreParams());
              //
              // DataStore wifDataStore = null;
              // SimpleFeatureStore featureStore = null;
              // wifDataSource = dataSourceFactory
              // .createGeospatialDataSource(postgisDataStoreConfig
              // .getDataStoreParams());
              // wifDataStore = wifDataSource.getDataStore();
              // featureStore = (SimpleFeatureStore) wifDataStore
              // .getFeatureSource(uazDBTable);
              //
              // final SimpleFeatureCollection sortedUazCollection =
              // featureStore
              // .getFeatures(rule.getRuleQuery());
              // // if (sortedUazCollection.equals(null)) {
              // if (sortedUazCollection == null) {
              // LOGGER
              // .warn("no features retrieved for this filter, not allocating this land use!");
              // continue;
              // }
              // if (sortedUazCollection.size() == 0) {
              // LOGGER
              // .warn("no features retrieved for this filter, not allocating this land use!");
              // continue;
              // }
              //
              // // 2. Perform the proper allocation for this land use
              // allocateLandUse(projection, futureLU, allocationScenario,
              // sortedUazCollection, featureStore, rule, transaction,
              // scoreLabel, outcome);
              //
              // } catch (final Exception e) {
              // LOGGER
              // .error(
              // "doAllocationAnalysis rolling back to the last known modification, commit transaction failed: {}",
              // e.getMessage());
              // transaction.rollback();
              // throw new WifInvalidInputException(
              // "doAllocationAnalysis failed", e);
              // } finally {
              // transaction.close();
              // LOGGER
              // .trace(
              // "Closing any open transaction, Finished analyzing for Allocation Scenario label: {}",
              // allocationScenario.getLabel());
              // }

            }// end if
          }// end if (futureLU.getPriority() == i + 1) {
        }
      }

      // ////////////////////////////////////////////////////
      // ////////internal control loop
      // /before next projection, controls allocated enough land

      Boolean lswRepeat = false;

      // new
      final Map<String, Boolean> mapLanduseControl = new HashMap<String, Boolean>();
      for (final AllocationLU futureLU : landUseOrder) {
        mapLanduseControl.put(futureLU.getLabel(), false);
      }

      for (int k = 1; k <= 3; k++) { // three times
        LOGGER.info("k in first control loop is " + k + " ,lswRepeat is: "
            + lswRepeat);

        if (k == 1 || lswRepeat == true) {
          lswRepeat = false;
          for (int i = 0; i < setAlu.size(); i++) {
            for (final AllocationLU futureLU : landUseOrder) {
              if (futureLU.getPriority() == i + 1) {
                String scoreLabel = "";
                if (futureLU.getAssociatedLU() != null) {
                  scoreLabel = futureLU.getAssociatedLU().getFeatureFieldName();

                  // ///////////////////////////////////////////////
                  // control scenario
                  LOGGER.info("mapLanduseControl in first control loop for "
                      + futureLU.getLabel() + " in  k =" + k + " is: "
                      + mapLanduseControl.get(futureLU.getLabel()));
                  if (mapLanduseControl.get(futureLU.getLabel()) == false) {

                    final ArrayList<String> GrowthPatternFields = new ArrayList<String>();
                    String PlannedSQL = "";
                    String InfrastructureSQL = "";

                    if (!allocationScenario.getControlScenarioId().equals(
                        "None")) {
                      final AllocationControlScenario controlScenario = AllocationControlScenarioDao
                          .findAllocationControlScenarioById(allocationScenario
                              .getControlScenarioId());

                      // Growth Pattern : Added to Order By section

                      // String GrowthPatternSQL = "";
                      if (controlScenario.getGrowthPatternControl() == true) {

                        final Set<GrowthPatternALU> gFields = allocationConfig
                            .getGrowthPatternALUs();

                        for (final String item : controlScenario
                            .getGrowthPatternControlLabels()) {
                          for (final GrowthPatternALU growthPattern : gFields) {
                            if (growthPattern.getLabel().equals(item)) {
                              GrowthPatternFields.add(growthPattern
                                  .getFieldName());
                            }
                          }

                        }

                      }

                      // planned land use; gaining query filter
                      final String plannedALUsFieldName = allocationConfig
                          .getPlannedALUsFieldName();

                      if (controlScenario.getPlannedlandUseControl() == true) {
                        Boolean lswPlanned = false;
                        PlannedSQL = "(";
                        final Set<PlannedALU> pFields = allocationConfig
                            .getPlannedALUs();
                        int cnt = 0;
                        for (final PlannedALU plannedALU : pFields) {
                          final String plabel = plannedALU.getLabel();
                          final Map<String, String> spLU = plannedALU
                              .getAssociatedALUsMap();
                          final Iterator spMap = spLU.entrySet().iterator();
                          while (spMap.hasNext()) {
                            final Map.Entry mapEntry = (Map.Entry) spMap.next();
                            // LOGGER.info("The planned land use key is: "
                            // + mapEntry.getKey() + ",value is :"
                            // + mapEntry.getValue());
                            if (mapEntry.getValue().toString()
                                .equals(futureLU.getLabel())) {
                              lswPlanned = true;
                              if (cnt == 0) {
                                PlannedSQL = PlannedSQL + "\""
                                    + plannedALUsFieldName + "\"" + "='"
                                    + plabel + "'";
                              } else {
                                PlannedSQL = PlannedSQL + " OR " + "\""
                                    + plannedALUsFieldName + "\"" + "='"
                                    + plabel + "'";
                              }
                              cnt = cnt + 1;
                            }
                          }
                        }
                        PlannedSQL = PlannedSQL + ")";
                        if (lswPlanned == false) {
                          PlannedSQL = "";
                        }
                      }

                      LOGGER.info("The PlannedSQL is: " + PlannedSQL);

                      // Infrastructure; gaining query filter

                      String InfrastructureSQLMaster = "";

                      if (controlScenario.getInfrastructureControl() == true) {

                        final Set<InfrastructureUses> infUses = controlScenario
                            .getInfrastructureUses();

                        int cntInfraItem = 0;
                        for (final String item : controlScenario
                            .getInfrastructureControlLabels()) {

                          Boolean lswInf = false;
                          for (final InfrastructureUses infUse : infUses) {
                            if (infUse.getLanduseName().equals(
                                futureLU.getLabel())) {

                              int cntInfra = 0;
                              Map<String, String> map = new HashMap<String, String>();
                              map = infUse.getInfrastructureMap();
                              final Iterator itMap = map.entrySet().iterator();
                              while (itMap.hasNext()) {

                                final Map.Entry mapEntry = (Map.Entry) itMap
                                    .next();

                                // new
                                if (item.equals(mapEntry.getKey())) {

                                  if (!mapEntry.getValue().equals("N/A")) {
                                    lswInf = true;
                                    if (!InfrastructureSQL.equals("")) {
                                      InfrastructureSQL = InfrastructureSQL
                                          + " AND (";
                                    } else {
                                      InfrastructureSQL = InfrastructureSQL
                                          + "(";
                                    }
                                    // }

                                    for (final InfrastructureALU infField : allocationConfig
                                        .getInfrastructureALUs()) {
                                      if (infField.getLabel().equals(
                                          mapEntry.getKey())) {
                                        if (mapEntry.getValue().equals(
                                            "Required")) {
                                          InfrastructureSQL = InfrastructureSQL
                                              + "\"" + infField.getFieldName()
                                              + "\"" + "<="
                                              + projection.getLabel();
                                        } else if (mapEntry.getValue().equals(
                                            "Excluded")) {
                                          InfrastructureSQL = InfrastructureSQL
                                              + "(" + "\""
                                              + infField.getFieldName() + "\""
                                              + ">" + projection.getLabel()
                                              + ")";
                                        }
                                        InfrastructureSQL = InfrastructureSQL
                                            + ")";
                                      }
                                    }
                                  }
                                }

                                cntInfra = cntInfra + 1;

                              }
                            }
                          }
                          // InfrastructureSQL = InfrastructureSQL + ")";
                          if (lswInf == false) {
                            if (InfrastructureSQL.length() < 2) {
                              InfrastructureSQL = "";
                            }
                          }

                          if (!InfrastructureSQL.equals("")) {

                            if (cntInfraItem == 0) {
                              InfrastructureSQLMaster = InfrastructureSQLMaster
                                  + InfrastructureSQL;
                            } else {

                              if (!InfrastructureSQLMaster.equals("")) {
                                InfrastructureSQLMaster = InfrastructureSQLMaster
                                    + " AND (" + InfrastructureSQL + ")";
                              } else {
                                InfrastructureSQLMaster = InfrastructureSQLMaster
                                    + InfrastructureSQL;
                              }
                            }

                          }

                          cntInfraItem = cntInfraItem + 1;
                        }// end for new (string item)

                      }// end if

                      // InfrastructureSQL = InfrastructureSQLMaster;
                      LOGGER
                          .info(
                              "For the projection year {}, the InfrastructureSQL is: {}",
                              projection.getLabel(), InfrastructureSQL);
                    }

                    AreaRequirement areaRequirement = null;
                    AreaRequirement manualareaRequirement = null;
                    if (allocationScenario.isManual()) {
                      // for (final AreaRequirement ar : allocationScenario
                      // .getManualdemandScenario().getAreaRequirements()) {
                      for (final AreaRequirement ar : outcome) {
                        if (ar.getProjectionLabel().equals(
                            projection.getLabel())) {
                          if (ar.getAllocationLULabel().equals(
                              futureLU.getLabel())) {
                            manualareaRequirement = ar;
                          }
                        }
                      }
                    } else {
                      areaRequirement = futureLU.getAreaRequirement(projection,
                          allocationScenario.getDemandScenario());
                    }
                    final Double newLandUseArea = 0.0;
                    if (allocationScenario.isManual()) {
                      if (manualareaRequirement != null) {
                        LOGGER.info(
                            "@@@@@ For {} allocating  area requirement of: {}",
                            futureLU.getLabel(),
                            manualareaRequirement.getRequiredArea());
                      }
                    } else {
                      LOGGER.info(
                          "@@@@@ For {} allocating  area requirement of: {}",
                          futureLU.getLabel(),
                          areaRequirement.getRequiredArea());
                    }
                    Double remainingArea = 0.0;
                    if (allocationScenario.isManual()) {
                      if (manualareaRequirement != null) {
                        remainingArea = manualareaRequirement.getRequiredArea();
                      }
                    } else {
                      remainingArea = areaRequirement.getRequiredArea();
                    }

                    // Double dfirstYear = geodataFilterer.getSumAreaFirstYear(
                    // allocationScenario, futureLU);
                    Double dfirstYear = 0.0;
                    for (final String landUseKey : mapLanduseSize.keySet()) {
                      if (futureLU.getLabel().equals(landUseKey)) {
                        dfirstYear = mapLanduseSize.get(landUseKey);
                      }

                    }
                    final Double dprojectionYear = geodataFilterer
                        .getSumAreaProjectionYear(allocationScenario,
                            projection, futureLU);

                    Double demandHappend = 0.0;
                    Double demandExpected = 0.0;
                    Double extranew = 0.0;
                    Double extraused = 0.0;

                    demandExpected = dfirstYear + remainingArea;
                    demandHappend = dfirstYear + dprojectionYear;

                    demandExpected = remainingArea;
                    demandHappend = dprojectionYear;

                    // demandHappend = dfirstYear + dprojectionYear +
                    // mapLanduseExtra.get(futureLU.getLabel());

                    if (futureLU.getLabel().equals("Residential")) {
                      int yyyy = 0;
                      yyyy = 1;
                    }
                    LOGGER.info(futureLU.getLabel());

                    if (mapLanduseExtra.get(futureLU.getLabel()) > 0) {

                      if (mapLanduseExtra.get(futureLU.getLabel()) >= remainingArea) {
                        extranew = mapLanduseExtra.get(futureLU.getLabel())
                            - remainingArea;
                        extraused = remainingArea;
                      } else {
                        extranew = 0.0;
                        extraused = mapLanduseExtra.get(futureLU.getLabel());

                      }
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);

                    }
                    demandHappend = demandHappend + extraused;
                    if (k == 1) {

                      if (Math.abs(demandHappend - demandExpected) < 1) {
                        mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      } else if (demandHappend < demandExpected) {
                      } else {
                        mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      }

                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);

                      LOGGER.info("toltal extra used for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " is :"
                          + extraused.toString());

                      LOGGER.info("toltal area needed for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " before alloction is :"
                          + demandExpected.toString());

                      LOGGER.info("toltal area needed for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " after alloction is :"
                          + demandHappend.toString());
                    }

                    if (Math.abs(demandHappend - demandExpected) < 1) {
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      mapLanduseControl.put(futureLU.getLabel(), true);

                    } else if (demandHappend < demandExpected) {
                      lswRepeat = true;
                      final Double newremainingArea = demandExpected
                          - demandHappend;
                      LOGGER.info("Allocating remaining area needed for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel()
                          + " in first control loop for k = " + k
                          + " ,for remaining :" + newremainingArea.toString());
                      final String sql = geodataFilterer.getAllocationRuleNew(
                          futureLU, allocationScenario, scoreLabel,
                          existingLULabel, PlannedSQL, InfrastructureSQL,
                          GrowthPatternFields, newremainingArea, projection);

                      geodataFinder.updateALlocationColumnNew(sql);
                      // LOGGER
                      // .info(
                      // "allocation sucessfully done for : {} in Projection year {} ",
                      // futureLU.getLabel(), projection.getLabel());
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      //
                      // LOGGER.info("putting zero " + " as Extra for land use "
                      // + futureLU.getLabel()
                      // + "  in control phase in projection year:"
                      // + projection.getLabel());
                    } else if (demandHappend > demandExpected) {

                      mapLanduseControl.put(futureLU.getLabel(), true);
                      // if land extra allocated before.
                      final Double offValue = demandHappend - demandExpected;
                      extranew = extranew + offValue;
                      mapLanduseExtra.put(futureLU.getLabel(), extranew);

                      LOGGER
                          .info("putting "
                              + offValue.toString()
                              + " as Extra for land use "
                              + futureLU.getLabel()
                              + " since we had extra and in control phase in projection year:"
                              + projection.getLabel());

                    }

                  }// endif(mapLanduseControl.get(futureLU.getLabel()) ==false)

                }// end if
              }// end if (futureLU.getPriority() == i + 1) {
            }
          }//
        }// end if k ==1
      }// for k= 1 to 3 end for internal control loop
      if (lswRepeat == true) {
        LOGGER
            .info(" Allocation not satisfied after three times attempt for projection year:"
                + projection.getLabel()
                + " ,before starting allocation for next projection year.We should check again after completing all projection years.");
      } else {
        LOGGER.info(" Allocation staisfed for projection year:"
            + projection.getLabel()
            + " before starting allocation for next projection year.");

      }
      // //////////////////////////
    }// for projection loop

    // ////////////////////////Second loop

    String notSatisfield = "";
    String returnStr = "";

    for (final AllocationLU futureLU : landUseOrder) {

      mapLanduseExtra.put(futureLU.getLabel(), 0.0);
    }

    for (final Projection projection : projectedSet) {

      final ArrayList<String> notSatisfieldLUArr = new ArrayList<String>();

      LOGGER
          .info(
              "performing remaining allocation analysis for Second time to make sure allocation satisfied for projection year: {}",
              projection.getLabel());

      // new
      final Map<String, Boolean> mapLanduseControl = new HashMap<String, Boolean>();
      for (final AllocationLU futureLU : landUseOrder) {
        mapLanduseControl.put(futureLU.getLabel(), false);
      }

      Boolean lswRepeat = false;
      for (int k = 1; k <= 3; k++) {

        LOGGER.info("k in second control loop is " + k + " ,lswRepeat is: "
            + lswRepeat + ", projection is:" + projection.getLabel());

        if (k == 1 || lswRepeat == true) {
          lswRepeat = false;
          for (int i = 0; i < setAlu.size(); i++) {
            for (final AllocationLU futureLU : landUseOrder) {
              if (futureLU.getPriority() == i + 1) {
                String scoreLabel = "";

                LOGGER.info("mapLanduseControl in second control loop for "
                    + futureLU.getLabel() + " in  k =" + k + " is: "
                    + mapLanduseControl.get(futureLU.getLabel()));
                if (mapLanduseControl.get(futureLU.getLabel()) == false) {

                  Boolean lswstl = true;
                  for (final String stl : notSatisfieldLUArr) {
                    if (stl.equals(futureLU.getLabel())) {
                      lswstl = false;
                    }
                  }
                  if (lswstl == true) {
                    notSatisfieldLUArr.add(futureLU.getLabel());
                  }

                  if (futureLU.getAssociatedLU() != null) {
                    scoreLabel = futureLU.getAssociatedLU()
                        .getFeatureFieldName();

                    // ///////////////////////////////////////////////
                    // control scenario

                    final ArrayList<String> GrowthPatternFields = new ArrayList<String>();
                    String PlannedSQL = "";
                    String InfrastructureSQL = "";

                    if (!allocationScenario.getControlScenarioId().equals(
                        "None")) {
                      final AllocationControlScenario controlScenario = AllocationControlScenarioDao
                          .findAllocationControlScenarioById(allocationScenario
                              .getControlScenarioId());

                      // Growth Pattern : Added to Order By section

                      // String GrowthPatternSQL = "";
                      if (controlScenario.getGrowthPatternControl() == true) {

                        final Set<GrowthPatternALU> gFields = allocationConfig
                            .getGrowthPatternALUs();

                        for (final String item : controlScenario
                            .getGrowthPatternControlLabels()) {
                          for (final GrowthPatternALU growthPattern : gFields) {
                            if (growthPattern.getLabel().equals(item)) {
                              GrowthPatternFields.add(growthPattern
                                  .getFieldName());
                            }
                          }

                        }

                      }

                      // planned land use; gaining query filter
                      final String plannedALUsFieldName = allocationConfig
                          .getPlannedALUsFieldName();

                      if (controlScenario.getPlannedlandUseControl() == true) {
                        Boolean lswPlanned = false;
                        PlannedSQL = "(";
                        final Set<PlannedALU> pFields = allocationConfig
                            .getPlannedALUs();
                        int cnt = 0;
                        for (final PlannedALU plannedALU : pFields) {
                          final String plabel = plannedALU.getLabel();
                          final Map<String, String> spLU = plannedALU
                              .getAssociatedALUsMap();
                          final Iterator spMap = spLU.entrySet().iterator();
                          while (spMap.hasNext()) {
                            final Map.Entry mapEntry = (Map.Entry) spMap.next();
                            if (mapEntry.getValue().toString()
                                .equals(futureLU.getLabel())) {
                              lswPlanned = true;
                              if (cnt == 0) {
                                PlannedSQL = PlannedSQL + "\""
                                    + plannedALUsFieldName + "\"" + "='"
                                    + plabel + "'";
                              } else {
                                PlannedSQL = PlannedSQL + " OR " + "\""
                                    + plannedALUsFieldName + "\"" + "='"
                                    + plabel + "'";
                              }
                              cnt = cnt + 1;
                            }
                          }
                        }
                        PlannedSQL = PlannedSQL + ")";
                        if (lswPlanned == false) {
                          PlannedSQL = "";
                        }
                      }

                      // Infrastructure; gaining query filter

                      String InfrastructureSQLMaster = "";

                      if (controlScenario.getInfrastructureControl() == true) {

                        final Set<InfrastructureUses> infUses = controlScenario
                            .getInfrastructureUses();

                        int cntInfraItem = 0;
                        for (final String item : controlScenario
                            .getInfrastructureControlLabels()) {

                          Boolean lswInf = false;
                          for (final InfrastructureUses infUse : infUses) {
                            if (infUse.getLanduseName().equals(
                                futureLU.getLabel())) {

                              int cntInfra = 0;
                              Map<String, String> map = new HashMap<String, String>();
                              map = infUse.getInfrastructureMap();
                              final Iterator itMap = map.entrySet().iterator();
                              while (itMap.hasNext()) {

                                final Map.Entry mapEntry = (Map.Entry) itMap
                                    .next();

                                // new
                                if (item.equals(mapEntry.getKey())) {

                                  if (!mapEntry.getValue().equals("N/A")) {
                                    lswInf = true;
                                    if (!InfrastructureSQL.equals("")) {
                                      InfrastructureSQL = InfrastructureSQL
                                          + " AND (";
                                    } else {
                                      InfrastructureSQL = InfrastructureSQL
                                          + "(";
                                    }
                                    // }

                                    for (final InfrastructureALU infField : allocationConfig
                                        .getInfrastructureALUs()) {
                                      if (infField.getLabel().equals(
                                          mapEntry.getKey())) {
                                        if (mapEntry.getValue().equals(
                                            "Required")) {
                                          InfrastructureSQL = InfrastructureSQL
                                              + "\"" + infField.getFieldName()
                                              + "\"" + "<="
                                              + projection.getLabel();
                                        } else if (mapEntry.getValue().equals(
                                            "Excluded")) {
                                          InfrastructureSQL = InfrastructureSQL
                                              + "(" + "\""
                                              + infField.getFieldName() + "\""
                                              + ">" + projection.getLabel()
                                              + ")";
                                        }
                                        InfrastructureSQL = InfrastructureSQL
                                            + ")";
                                      }
                                    }
                                  }
                                }

                                cntInfra = cntInfra + 1;

                              }
                            }
                          }
                          // InfrastructureSQL = InfrastructureSQL + ")";
                          if (lswInf == false) {
                            if (InfrastructureSQL.length() < 2) {
                              InfrastructureSQL = "";
                            }
                          }

                          if (!InfrastructureSQL.equals("")) {

                            if (cntInfraItem == 0) {
                              InfrastructureSQLMaster = InfrastructureSQLMaster
                                  + InfrastructureSQL;
                            } else {

                              if (!InfrastructureSQLMaster.equals("")) {
                                InfrastructureSQLMaster = InfrastructureSQLMaster
                                    + " AND (" + InfrastructureSQL + ")";
                              } else {
                                InfrastructureSQLMaster = InfrastructureSQLMaster
                                    + InfrastructureSQL;
                              }
                            }

                          }

                          cntInfraItem = cntInfraItem + 1;
                        }// end for new (string item)

                      }// end if

                    }

                    AreaRequirement areaRequirement = null;
                    AreaRequirement manualareaRequirement = null;
                    if (allocationScenario.isManual()) {
                      // for (final AreaRequirement ar : allocationScenario
                      // .getManualdemandScenario().getAreaRequirements()) {
                      for (final AreaRequirement ar : outcome) {
                        if (ar.getProjectionLabel().equals(
                            projection.getLabel())) {
                          if (ar.getAllocationLULabel().equals(
                              futureLU.getLabel())) {
                            manualareaRequirement = ar;
                          }
                        }
                      }
                    } else {
                      areaRequirement = futureLU.getAreaRequirement(projection,
                          allocationScenario.getDemandScenario());
                    }
                    final Double newLandUseArea = 0.0;
                    if (allocationScenario.isManual()) {
                      if (manualareaRequirement != null) {

                      }
                    } else {

                    }
                    Double remainingArea = 0.0;
                    if (allocationScenario.isManual()) {
                      if (manualareaRequirement != null) {
                        remainingArea = manualareaRequirement.getRequiredArea();
                      }
                    } else {
                      remainingArea = areaRequirement.getRequiredArea();
                    }

                    // Double dfirstYear = geodataFilterer.getSumAreaFirstYear(
                    // allocationScenario, futureLU);
                    Double dfirstYear = 0.0;
                    for (final String landUseKey : mapLanduseSize.keySet()) {
                      if (futureLU.getLabel().equals(landUseKey)) {
                        dfirstYear = mapLanduseSize.get(landUseKey);
                      }

                    }
                    final Double dprojectionYear = geodataFilterer
                        .getSumAreaProjectionYear(allocationScenario,
                            projection, futureLU);

                    Double demandHappend = 0.0;
                    Double demandExpected = 0.0;
                    Double extranew = 0.0;
                    Double extraused = 0.0;

                    demandExpected = dfirstYear + remainingArea;
                    demandHappend = dfirstYear + dprojectionYear;

                    demandExpected = remainingArea;
                    demandHappend = dprojectionYear;

                    // demandHappend = dfirstYear + dprojectionYear +
                    // mapLanduseExtra.get(futureLU.getLabel());

                    if (mapLanduseExtra.get(futureLU.getLabel()) > 0) {

                      if (mapLanduseExtra.get(futureLU.getLabel()) >= remainingArea) {
                        extranew = mapLanduseExtra.get(futureLU.getLabel())
                            - remainingArea;
                        extraused = remainingArea;
                      } else {
                        extranew = 0.0;
                        extraused = mapLanduseExtra.get(futureLU.getLabel());

                      }
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);

                    }
                    demandHappend = demandHappend + extraused;
                    if (k == 1) {
                      if (Math.abs(demandHappend - demandExpected) < 1) {
                        mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      } else if (demandHappend < demandExpected) {
                      } else {
                        mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      }

                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);

                      LOGGER.info("toltal extra used for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " is :"
                          + extraused.toString());

                      LOGGER.info("toltal area needed for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " before alloction is :"
                          + demandExpected.toString());

                      LOGGER.info("toltal area needed for "
                          + futureLU.getLabel() + " in year "
                          + projection.getLabel() + " after alloction is :"
                          + demandHappend.toString());
                    }

                    if (futureLU.getLabel()
                        .equals("Medium Density - Urbanised")) {
                      int yyyy = 0;
                      yyyy = 1;
                    }
                    if (Math.abs(demandHappend - demandExpected) < 1) {
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);
                      mapLanduseControl.put(futureLU.getLabel(), true);
                      notSatisfieldLUArr.remove(futureLU.getLabel());

                    } else if (demandHappend < demandExpected) {
                      lswRepeat = true;
                      final Double newremainingArea = demandExpected
                          - demandHappend;
                      LOGGER.info("Allocating remaining area needed for "
                          + futureLU.getLabel()
                          + " in second control loop for k = " + k
                          + " , in year " + projection.getLabel()
                          + " ,for remaining :" + newremainingArea.toString());
                      final String sql = geodataFilterer.getAllocationRuleNew(
                          futureLU, allocationScenario, scoreLabel,
                          existingLULabel, PlannedSQL, InfrastructureSQL,
                          GrowthPatternFields, newremainingArea, projection);

                      geodataFinder.updateALlocationColumnNew(sql);
                      // LOGGER
                      // .info(
                      // "allocation sucessfully done for : {} in Projection year {} ",
                      // futureLU.getLabel(), projection.getLabel());
                      // mapLanduseExtra.put(futureLU.getLabel(), extranew);

                      // LOGGER.info("putting zero " + " as Extra for land use "
                      // + futureLU.getLabel()
                      // + "  in control phase in projection year:"
                      // + projection.getLabel());

                    } else if (demandHappend > demandExpected) {
                      mapLanduseControl.put(futureLU.getLabel(), true);
                      // if land extra allocated before.
                      final Double offValue = demandHappend - demandExpected;
                      extranew = extranew + offValue;
                      mapLanduseExtra.put(futureLU.getLabel(), extranew);

                      LOGGER
                          .info("putting "
                              + offValue.toString()
                              + " as Extra for land use "
                              + futureLU.getLabel()
                              + " since we had extra and in control phase in projection year:"
                              + projection.getLabel());

                      notSatisfieldLUArr.remove(futureLU.getLabel());

                    }

                  }// end if
                }// end if
              }// end if (futureLU.getPriority() == i + 1) {
            }// end for second loop
          }
        }// end if k ==1
      }// for k= 1 to 3 // end second loop
      if (lswRepeat == true) {
        LOGGER
            .info(" Allocation not satisfied in second loop, after three times attempt for projection year:"
                + projection.getLabel()
                + " ,before starting allocation for next projection year.We should check again after completing all projection years.");

        notSatisfield = notSatisfield + " in projection year:"
            + projection.getLabel() + " for " + notSatisfieldLUArr.toString()
            + " , ";
      } else {
        LOGGER.info(" Allocation staisfed in second loop, for projection year:"
            + projection.getLabel()
            + " before starting allocation for next projection year.");

      }

    }// for projection loop

    if (notSatisfield.length() > 0) {
      returnStr = "Allocation not satisfied " + notSatisfield;
    } else {
    }
    LOGGER.info(returnStr);

    allocationScenario.setReady(true);
    allocationScenarioDao.updateAllocationScenario(allocationScenario);

    // return true;
    return returnStr;

  }

  /**
   * Allocate land use.
   * 
   * @param projection
   *          the projection
   * @param allocationLU
   *          the future lu
   * @param allocationScenario
   *          the allocation scenario
   * @param sortedUazCollection
   * @param featureStore
   * @param transaction
   * @param rule
   * @param scoreLabel
   * @throws WifInvalidConfigException
   * @throws CQLException
   * @throws WifInvalidInputException
   */
  private boolean allocateLandUse(final Projection projection,
      final AllocationLU allocationLU,
      final AllocationScenario allocationScenario,
      final SimpleFeatureCollection sortedUazCollection,
      final SimpleFeatureStore featureStore, final ALURule rule,
      final Transaction transaction, final String scoreLabel,
      final Set<AreaRequirement> outcome) throws CQLException,
      WifInvalidConfigException, WifInvalidInputException, IOException {
    // 1. querying with ordering UAZs
    int allocatedFeatures = 0;

    final WifProject wifProject = allocationScenario.getWifProject();
    final String AllocationConfigsId = wifProject.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final SimpleFeatureIterator its = sortedUazCollection.features();
    final String suitabilityScoreLabel = scoreLabel;
    LOGGER.debug(">>>>> Suitability UAZ score label: {} for: {}",
        suitabilityScoreLabel, allocationLU.getLabel());
    AreaRequirement areaRequirement = null;
    AreaRequirement manualareaRequirement = null;
    // TODO the next operations are very expensive, enabled only when testing in
    // local host
    final int size = sortedUazCollection.size();
    LOGGER.debug("analyzing {} features that comply with this rule", size);
    final HashMap<String, Boolean> allocatedFeaturesIds = new HashMap<String, Boolean>(
        size / WifKeys.ALLOCATION_PERFORMANCE_INDEX);
    // TODO enabled the following in production mode
    // HashMap<String, Boolean> allocatedFeaturesIds = new HashMap<String,
    // Boolean>(
    // (WifKeys.ALLOCATION_PERFORMANCE_INDEX*10));

    // if is manual read form user entered values.
    if (allocationScenario.isManual()) {
      // for (final AreaRequirement ar : allocationScenario
      // .getManualdemandScenario().getAreaRequirements()) {
      for (final AreaRequirement ar : outcome) {

        // if (ar.getProjection().getLabel().equals(projection.getLabel())) {
        if (ar.getProjectionLabel().equals(projection.getLabel())) {
          if (ar.getAllocationLULabel().equals(allocationLU.getLabel())) {
            manualareaRequirement = ar;
          }
        }
      }
    } else {
      areaRequirement = allocationLU.getAreaRequirement(projection,
          allocationScenario.getDemandScenario());
    }
    Double newLandUseArea = 0.0;
    if (allocationScenario.isManual()) {
      if (manualareaRequirement != null) {
        LOGGER.trace("@@@@@ For {} allocating  area requirement of: {}",
            allocationLU.getLabel(), manualareaRequirement.getRequiredArea());
      }
    } else {
      LOGGER.info("@@@@@ For {} allocating  area requirement of: {}",
          allocationLU.getLabel(), areaRequirement.getRequiredArea());
    }
    Double remainingArea = 0.0;
    if (allocationScenario.isManual()) {
      if (manualareaRequirement != null) {
        remainingArea = manualareaRequirement.getRequiredArea();
      }
    } else {
      remainingArea = areaRequirement.getRequiredArea();
    }
    try {

      // 2. for each ORDERED UAZ
      while (its.hasNext() && remainingArea > 0) {
        // 3. while there is a still area to allocate
        final SimpleFeature uazFeature = its.next();
        if (allocationScenario.getWifProject().getId()
        // FIXME
            .equals(WifKeys.TEST_PROJECT_ID)) {
          if (alreadyAllocatedDemonstration(uazFeature, allocationScenario
              .getAllocationConfig().getAllocationColumnsMap().values())) {
            continue;
          }

        } else {

          final String[] columnList = allocationConfig
              .getAllocationColumnsMap().values().toArray(new String[0]);
          // reseting values.
          Arrays.sort(columnList);
          if (alreadyAllocated(uazFeature, columnList)) {
            continue;
          }
        }
        final String id = uazFeature.getID();
        // LOGGER.debug("Allocating feature id: {}", id);

        allocatedFeaturesIds.put(id, true);
        final String areaLabel = allocationScenario.getWifProject()
            .getAreaLabel();
        final Double featureArea = (Double) uazFeature.getAttribute(areaLabel);
        // LOGGER.debug("feature suitability score= {}",
        // uazFeature.getAttribute(scoreLabel));
        allocatedFeatures++;
        // LOGGER.debug(
        // "feature area = {},number of allocated features so far: {}",
        // featureArea, allocatedFeatures);
        remainingArea -= featureArea;
        newLandUseArea += featureArea;

        if (remainingArea <= 0) {
          LOGGER.trace(
              "all area that was required has been allocated for = {}",
              allocationLU.getLabel());

          its.close();// close feature iterators, very
          // important
          break;
        }
        /*
         * LOGGER.debug("remaining area to allocate= {}", remainingArea);
         * LOGGER.debug("newLandUse area allocated so far = {}",
         * newLandUseArea);
         */

      }
    } finally {
      // 4.finish UAZ, no more features available or remaining
      // area
      // has been allocated
      its.close();// close feature iterators, very important
    }
    // update new allocation for the suitability land use
    final String aluColumn = allocationConfig.getAllocationColumnsMap().get(
        projection.getLabel());

    LOGGER
        .trace("%%%% updating this land use features column {}, with value {}",
            aluColumn,
            WifKeys.FUTURELU_PREFIX + allocationLU.getFeatureFieldName());
    final DataAccess<SimpleFeatureType, SimpleFeature> wifDataStore = featureStore
        .getDataStore();
    FeatureWriter<SimpleFeatureType, SimpleFeature> writer;
    final String uazDBTable = allocationScenario.getWifProject()
        .getSuitabilityConfig().getUnifiedAreaZone();
    final Transaction transactionnew = new DefaultTransaction("update");
    writer = ((DataStore) wifDataStore).getFeatureWriter(uazDBTable, rule
        .getRuleQuery().getFilter(), transactionnew);
    int featureCounter = 1;
    try {
      while (writer.hasNext()) {
        final SimpleFeature uazFeature = writer.next();
        if (allocatedFeaturesIds.containsKey(uazFeature.getID())) {
          // LOGGER.debug(
          // "In feature {}, updating allocation column with value = {}",
          // uazFeature.getID(),
          // WifKeys.FUTURELU_PREFIX + allocationLU.getFeatureFieldName());
          // uazFeature.setAttribute(aluColumn,
          // new Double(allocationLU.getAllocationFeatureFieldName()));
          uazFeature.setAttribute(aluColumn, new String(WifKeys.FUTURELU_PREFIX
              + allocationLU.getFeatureFieldName()));

          writer.write();
          featureCounter++;
        }
      }

      LOGGER
          .trace(
              "About to Update {} features with the new information for allocation... ",
              featureCounter);
      transactionnew.commit();
    } finally {
      LOGGER
          .trace("modified {} features in : {} !", featureCounter, uazDBTable);
      writer.close(); // IMPORTANT
      transactionnew.close();

    }

    if (remainingArea > 0) {
      LOGGER.warn("no more area available for allocation!! {} unallocated!",
          remainingArea);
      // break;
    }
    return false;
  }

  /**
   * Reset analysis layer.
   * 
   * @param allocationScenario
   *          the allocation scenario
   * @param featureStore
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private boolean resetAnalysisLayer(
      final AllocationScenario allocationScenario,
      final SimpleFeatureStore featureStore) throws WifInvalidInputException,
      IOException {
    final String uazDBTable = allocationScenario.getWifProject()
        .getSuitabilityConfig().getUnifiedAreaZone();
    final boolean status = true;
    final AllocationConfigs allocationConfig = allocationScenario
        .getAllocationConfig();
    // getting ready to update the data store
    LOGGER.debug("obtaining spatial information from: {}", uazDBTable);

    final Transaction transaction = new DefaultTransaction("update");
    try {
      final String[] allocationLabels = allocationConfig
          .getAllocationColumnsMap().values().toArray(new String[0]);
      for (final String value : allocationLabels) {
        LOGGER.debug("AllocationFFname to use: {}", value);
      }

      // Double[] resetValues = allocationConfig.getResetColumns();
      final String[] resetValues = allocationConfig.getResetColumnsStr();
      LOGGER.debug("Reset value is: {}", resetValues[0]);
      final Filter resetFilter = geodataFilterer.getResetFilter();

      // erasing the canvas of the allocation process in
      // database,it would be better if it was done before
      featureStore.modifyFeatures(allocationLabels, resetValues, resetFilter);
      LOGGER.info("About to reset the information for allocation... ");
      transaction.commit();
    } catch (final Exception e) {
      LOGGER
          .error(
              "resetAnalysisLayer rolling back to the last known modification, commit transaction failed: {}",
              e.getMessage());
      transaction.rollback();
      throw new WifInvalidInputException("Reset analysis layer failed", e);
    } finally {
      transaction.close();
      LOGGER
          .info(
              "Closing reset transaction, Finished resetting for Allocation Scenario label: {}",
              allocationScenario.getLabel());
    }
    return status;
  }

  /**
   * Already allocated.
   * 
   * @param uazFeature
   *          the uaz feature
   * @param columns
   *          the columns
   * @return true, if successful
   */
  private boolean alreadyAllocated(final SimpleFeature uazFeature,
      final String[] columns) {
    Arrays.sort(columns);
    int i = 1;
    for (final String aluLabel : columns) {
      if (i != 1) { // discard current year.
        if (uazFeature.getAttribute(aluLabel) == null) {
          return false;
        } else {
          // if (!((Double) uazFeature.getAttribute(aluLabel))
          // .equals(WifKeys.NOT_SUITABLE_SCORE)) {
          // LOGGER
          // .trace(
          // "feature WITH ID= {}, has already being allocated in projection {}",
          // uazFeature.getID(), aluLabel);
          // return true;
          if (uazFeature.getAttribute(aluLabel).toString().length() > 0) {
            LOGGER
                .trace(
                    "feature WITH ID= {}, has already being allocated in projection {}",
                    uazFeature.getID(), aluLabel);
            return true;
          }
        }
      }
      i = i + 1;
    }
    return false;
  }

  private boolean alreadyAllocatedDemonstration(final SimpleFeature uazFeature,
      final Collection<String> columns) {
    for (final String aluLabel : columns) {
      // FIXME
      if (!aluLabel.equals(WifKeys.DEMO_ALLOCATION_0)) {
        if (uazFeature.getAttribute(aluLabel) == null) {
          return false;
        } else {
          if (!((Double) uazFeature.getAttribute(aluLabel))
              .equals(WifKeys.NOT_SUITABLE_SCORE)) {
            LOGGER
                .trace(
                    "feature WITH ID= {}, has already being allocated in projection {}",
                    uazFeature.getID(), aluLabel);
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean allocateLandUseNew(final Projection projection,
      final AllocationLU allocationLU,
      final AllocationScenario allocationScenario, Double remainingArea,
      final String scoreLabel, final ResultSet its, final String tschema,
      final String tname) throws CQLException, WifInvalidConfigException,
      WifInvalidInputException, IOException, SQLException {

    final WifProject wifProject = allocationScenario.getWifProject();
    final String AllocationConfigsId = wifProject.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final String suitabilityScoreLabel = scoreLabel;
    LOGGER.debug(">>>>> Suitability UAZ score label: {} for: {}",
        suitabilityScoreLabel, allocationLU.getLabel());
    final int size = 0;
    Double newLandUseArea = 0.0;
    int allocatedFeatures = 0;

    final String pkName = geodataFinder.getPrimaryKeyName(tname);
    LOGGER.debug("analyzing {} features that comply with resultset", size);

    final ArrayList<String> arrList = new ArrayList<String>();

    try {

      while (its.next() && remainingArea > 0) {

        if (allocationScenario.getWifProject().getId()
        // FIXME
            .equals(WifKeys.TEST_PROJECT_ID)) {

        } else {

          final String id = String.valueOf(its.getInt(pkName));
          arrList.add(id);

          final String areaLabel = allocationScenario.getWifProject()
              .getAreaLabel();
          final Double featureArea = its.getDouble(areaLabel);

          allocatedFeatures++;
          remainingArea -= featureArea;
          newLandUseArea += featureArea;

          if (remainingArea <= 0) {
            LOGGER.info(
                "all area that was required has been allocated for = {}",
                allocationLU.getLabel());

            its.close();// close feature iterators, very
            // important
            break;
          }
        }

      }
    } finally {

      its.close();
    }
    // update new allocation for the suitability land use
    final String aluColumn = allocationConfig.getAllocationColumnsMap().get(
        projection.getLabel());

    int k = 0;
    String strValues = "";
    for (final String str : arrList) {
      if (k == 0) {
        strValues = strValues + str;
      } else {
        strValues = strValues + "," + str;
      }
      k = k + 1;
    }

    final String SQL = "Update " + tschema + "." + tname + " Set \""
        + aluColumn + "\" = '" + WifKeys.FUTURELU_PREFIX
        + allocationLU.getFeatureFieldName() + "' Where " + pkName + " in ("
        + strValues + ")";

    geodataFinder.updateALlocationColumnNew(SQL);

    LOGGER
        .info(
            "%%%% updated this land use features column:value {}, for number of records: {}",
            aluColumn + ":" + WifKeys.FUTURELU_PREFIX
                + allocationLU.getFeatureFieldName(), allocatedFeatures);

    if (remainingArea > 0) {
      LOGGER.warn("no more area available for allocation!! {} unallocated!",
          remainingArea);
      // break;
    }
    return true;
  }
}
