package au.org.aurin.wif.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.MathExpressionImpl;
import org.geotools.filter.SortByImpl;
import org.geotools.filter.function.math.FilterFunction_abs;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import au.org.aurin.wif.config.WifConfig;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.impl.allocation.comparators.YearComparator;
import au.org.aurin.wif.model.Projection;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationConfigs;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.allocation.control.ALURule;
import au.org.aurin.wif.model.demand.DemandConfig;
import au.org.aurin.wif.model.suitability.SuitabilityConfig;
import au.org.aurin.wif.repo.allocation.AllocationConfigsDao;
import au.org.aurin.wif.repo.demand.DemandConfigDao;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class GeodataFilterer.
 */

@Component
public class GeodataFilterer {

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(GeodataFilterer.class);

  /** The wif config. */
  @Resource
  private WifConfig wifConfig;

  /** The Allocation config dao. */
  @Autowired
  private AllocationConfigsDao AllocationConfigsDao;

  @Autowired
  @Qualifier(value = "myjdbcDataStoreConfig")
  private jdbcDataStoreConfig myjdbcDataStoreConfig;

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  @Autowired
  private DemandConfigDao demandConfigDao;

  /**
   * Gets the filter according to the CQL parseable text entered.
   *
   * @param queryTxt
   *          the query txt
   * @return the filter
   * @throws CQLException
   *           the cQL exception
   */
  public Filter getFilter(final String queryTxt) throws CQLException {

    final Filter filter = CQL.toFilter(queryTxt);

    LOGGER.trace("using filter: " + filter);
    return filter;
  }

  /**
   * Gets the filter from parameters.
   *
   * @param wifParameters
   *          the wif parameters
   * @return the filter from parameters
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws TransformException
   *           the transform exception
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws ParseException
   *           the parse exception
   * @throws CQLException
   *           the cQL exception
   */
  public Filter getFilterFromParameters(final Map<String, Object> wifParameters)
      throws MismatchedDimensionException, TransformException,
      NoSuchAuthorityCodeException, FactoryException, ParseException,
      CQLException {
    LOGGER.debug("getFilterFromParameters...");

    String queryTxt = "";

    final String polyTxt = getIntersectionPolygon(wifParameters);

    if (polyTxt == null) {
      LOGGER
      .info("no polygon query filter received, defaulting to including all the unified area zone!");
      queryTxt = "include";
    } else if (polyTxt.equals("")) {
      LOGGER
      .info("no polygon query filter received, defaulting to including all the unified area zone!");
      queryTxt = "include";
    } else {
      queryTxt = polyTxt;
    }
    LOGGER.debug("query filter: {}", queryTxt);
    return getFilter(queryTxt);
  }

  /**
   * Gets the reset filter tthat will erase the canvas for the allocation
   * analysis.
   *
   * @return the reset filter
   * @throws CQLException
   *           the cQL exception
   */
  public Filter getResetFilter() throws CQLException {
    if (wifConfig.isTestAllocationArea()) {
      return getFilter(WifKeys.TEST_QUERY);
    } else {
      return Filter.INCLUDE;
    }
  }

  /**
   * Gets the intersection polygon.
   *
   * @param wifParameters
   *          the wif parameters
   * @return the intersection polygon
   * @throws NoSuchAuthorityCodeException
   *           the no such authority code exception
   * @throws FactoryException
   *           the factory exception
   * @throws ParseException
   *           the parse exception
   * @throws MismatchedDimensionException
   *           the mismatched dimension exception
   * @throws TransformException
   *           the transform exception
   */
  private String getIntersectionPolygon(final Map<String, Object> wifParameters)
      throws NoSuchAuthorityCodeException, FactoryException, ParseException,
      MismatchedDimensionException, TransformException {
    final String polyTxt = (String) wifParameters.get(WifKeys.POLYGON);
    String queryTxt = "";

    if (polyTxt != null) {
      // default values do better with constants
      final String CRS_ORG = (String) wifParameters.get(WifKeys.CRS_ORG);
      final String CRS_DEST = (String) wifParameters.get(WifKeys.CRS_DEST);
      String geometryColumnName = (String) wifParameters
          .get(WifKeys.GEOMETRY_COLUMN_KEY);
      LOGGER.debug("CRS_ORG ={} ", CRS_ORG);
      if (wifParameters.get(WifKeys.CRS_ORG) == null) {
        wifParameters.put(WifKeys.CRS_ORG, WifKeys.CRS_INTERFACE);
        LOGGER.warn("it is null, defaulting to CRS_ORG ={} ",
            wifParameters.get(WifKeys.CRS_ORG));
      }
      LOGGER.debug("CRS_DEST ={} ", CRS_DEST);
      if (wifParameters.get(WifKeys.CRS_DEST) == null) {
        wifParameters.put(WifKeys.CRS_DEST, WifKeys.CRS_DEFAULT);
        LOGGER.warn("it is null, defaulting to CRS_DEST ={} ",
            wifParameters.get(WifKeys.CRS_DEST));
      }
      if (geometryColumnName == null) {
        wifParameters.put(WifKeys.GEOMETRY_COLUMN_KEY,
            WifKeys.DEFAULT_COLUMN_NAME);
        geometryColumnName = (String) wifParameters
            .get(WifKeys.GEOMETRY_COLUMN_KEY);
        LOGGER.warn("Geometry column name not specified, defaulting to ={} ",
            wifParameters.get(WifKeys.GEOMETRY_COLUMN_KEY));
      }

      final GeometryFactory geometryFactory = JTSFactoryFinder
          .getGeometryFactory(null);
      final WKTReader reader = new WKTReader(geometryFactory);
      LOGGER.debug("requested interception ORG polygon wkt: {}", polyTxt);
      Polygon polygon = (Polygon) reader.read(polyTxt);

      final CoordinateReferenceSystem orgCRS = CRS
          .decode((String) wifParameters.get(WifKeys.CRS_ORG));

      final CoordinateReferenceSystem targetCRS = CRS
          .decode((String) wifParameters.get(WifKeys.CRS_DEST));
      if (!orgCRS.equals(targetCRS)) {
        LOGGER
        .info("the CRS of the polygon requested is different from the uaz, attempting to  transform...");
        final MathTransform transform = CRS.findMathTransform(orgCRS,
            targetCRS, true);
        final Polygon traPoly = (Polygon) JTS.transform(polygon, transform);

        LOGGER.debug("transformed polygon wkt: ", traPoly.toText());
        polygon = traPoly;
      }
      LOGGER.debug("geometry column name: {}", geometryColumnName);

      queryTxt = "INTERSECTS(" + geometryColumnName + ", " + polygon.toText()
      + ")";
    }
    return queryTxt;
  }

  /**
   * Gets the allocation rule without any controls guidelines.
   *
   * @param futureLU
   *          the future lu
   * @param allocationScenario
   *          the allocation scenario
   * @param scoreLabel
   *          the score label
   * @param existingLULabel
   *          the existing lu label
   * @return the allocation rule
   * @throws CQLException
   *           the cQL exception
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   */
  public ALURule getAllocationRule(final AllocationLU futureLU,
      final AllocationScenario allocationScenario, final String scoreLabel,
      final String existingLULabel, final String PlannedSQL,
      final String InfrastructureSQL,
      final ArrayList<String> GrowthPatternFields, final Double remainingArea)
          throws CQLException, WifInvalidConfigException {
    LOGGER.debug("getAllocationRule for: {}", futureLU.getLabel());
    final String spatialPatternLabel = allocationScenario
        .getSpatialPatternLabel();
    LOGGER.debug("spatialPatternLabel for: {}, scorelabel = {}",
        spatialPatternLabel, scoreLabel);
    final ALURule rule = new ALURule();
    Query query = null;

    final WifProject project = allocationScenario.getWifProject();
    final String AllocationConfigsId = project.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final String luFilterTxt = "";
    final StringBuilder b = new StringBuilder(luFilterTxt);
    final Set<String> undevelopedLUsColumns = allocationConfig
        .getUndevelopedLUsColumns();

    if (undevelopedLUsColumns.size() > 0) {
      final Iterator<String> iterator = undevelopedLUsColumns.iterator();
      do {
        b.append("(" + existingLULabel + " = " + iterator.next() + ") ");
        b.append("OR");
      } while (iterator.hasNext());

      final int i = b.lastIndexOf("OR");
      b.replace(i, i + 2, "AND");
    }
    String queryTxt = b.toString();
    if (futureLU.isLocal() != null) {
      if (futureLU.isLocal()) {
        LOGGER.info("Analysing local use LU {}", futureLU.getLabel());
      }
    }
    LOGGER
    .trace("including all the unified area zones That satisfies the filter!");
    queryTxt = luFilterTxt + scoreLabel + " > 0";
    if (PlannedSQL.length() > 0) {
      queryTxt = queryTxt + " AND (" + PlannedSQL + ")";
    }
    if (InfrastructureSQL.length() > 0) {
      queryTxt = queryTxt + " AND (" + InfrastructureSQL + ")";
    }
    // queryTxt = "(ELU = 81) OR (ELU = 82) AND SCORE_6 > 0";
    LOGGER.info("query filter: {}", queryTxt);

    // queryTxt =
    // "score_s1 > 0 AND (Meshblock='Industrial') AND (URBVALUE=XXXX)";

    Filter filter = null;
    try {
      filter = getFilter(queryTxt);
    } catch (final Exception e) {
      LOGGER
      .error(
          "doAllocationAnalysis rolling back to the last known modification, commit transaction failed: {}",
          e.getMessage());
    }

    query = getSortedQuery(scoreLabel, spatialPatternLabel, allocationScenario,
        filter, GrowthPatternFields, remainingArea);

    if (query == null) {
      throw new WifInvalidConfigException(
          "there's no query information from : " + futureLU.getLabel());
    }
    rule.setRuleQuery(query);
    return rule;
  }

  /**
   * Gets the sorted query. The minimum saltine for allocation with no controls
   * requires: A growth spatial pattern label,, suitability score, and an area
   *
   * @param scoreLabel
   *          the score label
   * @param spatialPatternLabel
   *          the spatial pattern label
   * @param allocationScenario
   *          the allocation scenario
   * @param filter
   *          the filter
   * @return the sorted query
   */
  private Query getSortedQuery(final String scoreLabel,
      final String spatialPatternLabel,
      final AllocationScenario allocationScenario, final Filter filter,
      final ArrayList<String> GrowthPatternFields, final Double remainingArea) {
    final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
    final WifProject wifProject = allocationScenario.getWifProject();

    final String AllocationConfigsId = wifProject.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final SuitabilityConfig suitabilityConfig = wifProject
        .getSuitabilityConfig();
    final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();
    final String areaLabel = wifProject.getAreaLabel();

    final ArrayList<String> values = new ArrayList<String>();
    values.addAll(allocationConfig.getAllocationColumnsMap().values());
    values.add(wifProject.getGeometryColumnName());
    values.add(wifProject.getExistingLUAttributeName());
    values.add(areaLabel);
    values.add(scoreLabel);

    // /claudia test
    String areaLableNew = "ABS(\"" + areaLabel + "\" - " + remainingArea + ")";
    areaLableNew = areaLabel + " - " + remainingArea;
    LOGGER.info("areaLableNew is : " + areaLableNew);
    // //

    values.addAll(GrowthPatternFields);
    // FIXME
    if (wifProject.getId().equals(WifKeys.TEST_PROJECT_ID)) {
      values.remove(WifKeys.DEMO_ALLOCATION_0);
    }
    final String[] properties = values.toArray(new String[values.size()]);
    for (final String column : properties) {
      LOGGER.trace("obtaining the following {} column: ", column);
    }
    final Query query = new Query(uazDBTable, filter, properties);

    final SortBy sortByScore = ff.sort(scoreLabel, SortOrder.DESCENDING);

    // /////////new claudia

    // final FilterFunction_abs fb;
    // final Add f;
    // Expression tt;
    // tt.evaluate(object)
    // tt = (Expression) fb.evaluate(f);

    final PropertyName pn = null;
    pn.evaluate("ABS(\"" + areaLabel + "\" - " + remainingArea + ")");

    final SortByImpl sortByAreaNew2 = new SortByImpl(pn, SortOrder.ASCENDING);

    final MathExpressionImpl m;

    final SortBy sortByAreaNew3 = new SortBy() {
      @Override
      public PropertyName getPropertyName() {
        return YourPropertyNameImpl(areaLabel);
      }

      private PropertyName YourPropertyNameImpl(final String string) {
        // TODO Auto-generated method stub
        final PropertyName pn = null;
        pn.evaluate("ABS(\"" + string + "\" - " + remainingArea + ")");
        return pn;
      }

      @Override
      public SortOrder getSortOrder() {
        return SortOrder.ASCENDING;
      }
    };
    // ////////

    // just considering two : concentric or radial.
    SortBy sortByConcentric = null;
    SortBy sortByRadial = null;
    if (GrowthPatternFields.size() > 0) {
      int i = 1;
      for (final String gpField : GrowthPatternFields) {
        if (i == 1) {
          sortByConcentric = ff.sort(gpField, SortOrder.ASCENDING);
        }
        if (i == 2) {
          sortByRadial = ff.sort(gpField, SortOrder.ASCENDING);
        }
        i = i + 1;
      }
    }

    Boolean lsw = false;
    if (spatialPatternLabel == null) {
      lsw = false;
    } else {
      if (spatialPatternLabel.equals("None")) {
        lsw = false;
      } else {
        lsw = true;
      }
    }
    if (lsw == true) {
      final SortBy sortBySpatialPattern = ff.sort(spatialPatternLabel,
          SortOrder.ASCENDING);
      final SortBy sortByArea = ff.sort(areaLabel, SortOrder.DESCENDING);

      // new claudia
      final SortBy sortByAreaNew = ff.sort(areaLableNew, SortOrder.ASCENDING);

      if (sortByConcentric != null && sortByRadial != null) {
        query.setSortBy(new SortBy[] { sortByScore, sortBySpatialPattern,
            sortByArea, sortByConcentric, sortByRadial });
      } else if (sortByConcentric == null && sortByRadial != null) {
        query.setSortBy(new SortBy[] { sortByScore, sortBySpatialPattern,
            sortByArea, sortByRadial });
      } else if (sortByConcentric != null && sortByRadial == null) {
        query.setSortBy(new SortBy[] { sortByScore, sortBySpatialPattern,
            sortByArea, sortByConcentric });
      } else {
        query.setSortBy(new SortBy[] { sortByScore, sortBySpatialPattern,
            sortByArea });
      }
    }
    if (lsw == false) {
      final SortBy sortByArea = ff.sort(areaLabel, SortOrder.DESCENDING);

      if (sortByConcentric != null && sortByRadial != null) {
        query.setSortBy(new SortBy[] { sortByScore, sortByArea,
            sortByConcentric, sortByRadial });
      } else if (sortByConcentric == null && sortByRadial != null) {
        query.setSortBy(new SortBy[] { sortByScore, sortByArea, sortByRadial });
      } else if (sortByConcentric != null && sortByRadial == null) {
        query.setSortBy(new SortBy[] { sortByScore, sortByArea,
            sortByConcentric });
      } else {
        // query.setSortBy(new SortBy[] { sortByScore, sortByArea });

        // /new claudia

        // new claudia
        final SortBy sortByAreaNew = ff.sort(areaLableNew, SortOrder.ASCENDING);
        final FilterFunction_abs fb;
        final Add f;
        final PropertyName name = sortByAreaNew.getPropertyName();

        ff.property(name.getPropertyName());
        // Expression expr = ff.function( fb, ff.property(
        // name.getPropertyName()), ff.literal( remainingArea ) );
        // name.evaluate(expr);

        // f.getExpression1().accept(fb, remainingArea);
        // name.evaluate(object)
        // PropertyName extended = extender.extendProperty(name, FF,
        // NAMESPACES);
        final SortBy nn = new SortByImpl(name, SortOrder.DESCENDING);

        // test clasudia if fails uncomment above
        // query.setSortBy(new SortBy[] { sortByScore, sortByAreaNew2 });

      }
    }

    LOGGER
    .debug(
        "sorting by priority of first by suitability score column: {}, then by growth pattern: {}, and only then by area size",
        scoreLabel, spatialPatternLabel);
    return query;
  }

  // //////////////////////////////////////////////////////////////
  public String getAllocationRuleNew(final AllocationLU futureLU,
      final AllocationScenario allocationScenario, final String scoreLabel,
      final String existingLULabel, final String PlannedSQL,
      final String InfrastructureSQL,
      final ArrayList<String> GrowthPatternFields, final Double remainingArea,
      final Projection projection) throws CQLException,
  WifInvalidConfigException {

    String SQL = " Where ";
    LOGGER.debug("getAllocationRule for: {}", futureLU.getLabel());
    final String spatialPatternLabel = allocationScenario
        .getSpatialPatternLabel();
    LOGGER.debug("spatialPatternLabel for: {}, scorelabel = {}",
        spatialPatternLabel, scoreLabel);
    final ALURule rule = new ALURule();
    final Query query = null;

    final WifProject project = allocationScenario.getWifProject();
    final String AllocationConfigsId = project.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final String luFilterTxt = "";
    final StringBuilder b = new StringBuilder(luFilterTxt);
    final Set<String> undevelopedLUsColumns = allocationConfig
        .getUndevelopedLUsColumns();

    if (undevelopedLUsColumns.size() > 0) {
      final Iterator<String> iterator = undevelopedLUsColumns.iterator();
      do {
        b.append("(\"" + existingLULabel + "\"" + " ='" + iterator.next()
        + "') ");
        b.append("OR");
      } while (iterator.hasNext());

      final int i = b.lastIndexOf("OR");
      b.replace(i, i + 2, "AND");
    }
    String queryTxt = b.toString();
    if (futureLU.isLocal() != null) {
      if (futureLU.isLocal()) {
        LOGGER.info("Analysing local use LU {}", futureLU.getLabel());
      }
    }
    LOGGER
    .trace("including all the unified area zones That satisfies the filter!");
    queryTxt = luFilterTxt + scoreLabel + " > 0";
    if (PlannedSQL.length() > 0) {
      queryTxt = queryTxt + " AND (" + PlannedSQL + ")";
    }
    if (InfrastructureSQL.length() > 0) {
      queryTxt = queryTxt + " AND (" + InfrastructureSQL + ")";
    }
    // queryTxt = "(ELU = 81) OR (ELU = 82) AND SCORE_6 > 0";

    // check not allocated before.
    final String[] columnList = allocationConfig.getAllocationColumnsMap()
        .values().toArray(new String[0]);
    Arrays.sort(columnList);
    int inx = 1;
    for (final String aluLabel : columnList) {
      if (inx != 1) {
        queryTxt = queryTxt + " AND (\"" + aluLabel + "\" = '')";

      }
      inx = inx + 1;
    }

    SQL = SQL + queryTxt;
    LOGGER.info("where query is: {}", SQL);

    SQL = getSortedQueryNew(SQL, scoreLabel, spatialPatternLabel,
        allocationScenario, GrowthPatternFields, remainingArea, projection,
        futureLU);

    LOGGER.info("Final query is: {}", SQL);

    return SQL;
  }

  private String getSortedQueryNew(final String whereStr,
      final String scoreLabel, final String spatialPatternLabel,
      final AllocationScenario allocationScenario,
      final ArrayList<String> GrowthPatternFields, final Double remainingArea,
      final Projection projection, final AllocationLU futureLU) {

    String SQL = "SELECT ";

    final WifProject wifProject = allocationScenario.getWifProject();

    final String AllocationConfigsId = wifProject.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);
    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final String aluColumnProjection = allocationConfig
        .getAllocationColumnsMap().get(projection.getLabel());

    final SuitabilityConfig suitabilityConfig = wifProject
        .getSuitabilityConfig();
    final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();
    final String areaLabel = wifProject.getAreaLabel();

    final ArrayList<String> values = new ArrayList<String>();
    values.addAll(allocationConfig.getAllocationColumnsMap().values());
    values.add(wifProject.getGeometryColumnName());
    values.add(wifProject.getExistingLUAttributeName());
    values.add(areaLabel);
    // //values.add(scoreLabel);

    final String pkName = geodataFinder.getPrimaryKeyName(uazDBTable);
    values.add(pkName);
    // /claudia test
    final String areaLableNew = " ABS(\"" + areaLabel + "\" - " + remainingArea
        + ")";
    // areaLableNew = areaLabel + " - " + remainingArea;
    LOGGER.info("areaLableNew is : " + areaLableNew);
    // //

    values.addAll(GrowthPatternFields);
    // FIXME
    if (wifProject.getId().equals(WifKeys.TEST_PROJECT_ID)) {
      values.remove(WifKeys.DEMO_ALLOCATION_0);
    }
    final String[] properties = values.toArray(new String[values.size()]);

    SQL = SQL + scoreLabel;
    for (final String column : properties) {

      SQL = SQL + ",\"" + column + "\"";

    }

    // SQL = SQL + "," + areaLableNew;

    // select score_sdsd, \"URBVALUE\", ABS(\"URBVALUE\" - 10) from
    // wifdemo.wif_2cbcb5d481fe66bcea28dccb8c011260 order by score_sdsd desc,
    // ABS(\"URBVALUE\" - 10) asc

    String ORDERSQL = " order by ";
    // String ORDERSQL = " order by " + scoreLabel + " desc ";

    SQL = SQL + " FROM " + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable
        + " " + whereStr + " order by " + scoreLabel + " desc ";

    Boolean lswGrowth = false;
    if (GrowthPatternFields.size() > 0) {
      lswGrowth = true;
      int i = 1;
      for (final String gpField : GrowthPatternFields) {
        if (i == 1) {
          SQL = SQL + "," + "\"" + gpField + "\"" + " asc ";
          // ORDERSQL = ORDERSQL + "," + "\"" + gpField + "\"" + " asc ";
          ORDERSQL = ORDERSQL + "" + "\"" + gpField + "\"" + " asc ";

        }
        if (i == 2) {
          SQL = SQL + "," + "\"" + gpField + "\"" + " asc ";
          ORDERSQL = ORDERSQL + "," + "\"" + gpField + "\"" + " asc ";
        }
        i = i + 1;
      }
    }
    // for Claudia since she says that growth pattern has more important than
    // suitability score (if user wants growth pattern control)
    if (lswGrowth == true) {
      ORDERSQL = ORDERSQL + "," + scoreLabel + " desc ";
    } else {
      ORDERSQL = ORDERSQL + scoreLabel + " desc ";
    }

    Boolean lsw = false;
    if (spatialPatternLabel == null) {
      lsw = false;
    } else {
      if (spatialPatternLabel.equals("None")) {
        lsw = false;
      } else {
        lsw = true;
      }
    }
    if (lsw == true) {

      SQL = SQL + "," + "\"" + spatialPatternLabel + "\"" + " asc";
      ORDERSQL = ORDERSQL + "," + "\"" + spatialPatternLabel + "\"" + " asc";

    }
    SQL = SQL + "," + areaLableNew + " asc";
    ORDERSQL = ORDERSQL + "," + areaLableNew + " asc";

    LOGGER.info("ORDERSQL is {}", ORDERSQL);

    SQL = " SELECT ROW_NUMBER() OVER (" + ORDERSQL + ") as pk," + pkName + ","
        + scoreLabel + ",\"" + areaLabel + "\"," + areaLableNew + " FROM "
        + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable + " " + whereStr
        + ORDERSQL;

    String SQLFIRST = "";
    SQLFIRST = SQLFIRST + "Update " + myjdbcDataStoreConfig.getSchema() + "."
        + uazDBTable + " Set \"" + aluColumnProjection + "\" = '"
        + WifKeys.FUTURELU_PREFIX + futureLU.getFeatureFieldName() + "' where "
        + pkName + " in (";

    SQLFIRST = SQLFIRST + " select b." + pkName
        + " from (select a.* , sum(a.\"" + areaLabel
        + "\")  OVER (order by a.pk ) as cum from (";
    SQLFIRST = SQLFIRST + SQL;
    SQLFIRST = SQLFIRST
        + ") as a ) as b where b.pk <= (select case when (count(*)>0) then min(c) when  (count(*)=0) then 2000000000 else 0 end as pk from (select b.pk as c from (select a.*, sum(a.\""
        + areaLabel + "\")  OVER (order by a.pk ) as cum from (";
    SQLFIRST = SQLFIRST + SQL;
    SQLFIRST = SQLFIRST + ") as a ) as b where b.cum >=" + remainingArea
        + " limit 1 ) as d )) ";

    LOGGER.info("SQL is {}", SQLFIRST);
    return SQLFIRST;
  }

  public Double getSumAreaFirstYear(
      final AllocationScenario allocationScenario, final AllocationLU futureLU) {

    String SQL = "";

    final WifProject wifProject = allocationScenario.getWifProject();

    final String AllocationConfigsId = wifProject.getAllocationConfigsId();
    LOGGER.info("getting the AllocationConfig with ID={}", AllocationConfigsId);

    final SuitabilityConfig suitabilityConfig = wifProject
        .getSuitabilityConfig();
    final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();
    final String areaLabel = wifProject.getAreaLabel();

    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());

    // find first projection year
    if (allocationScenario.isManual()) {

      final DemandConfig demandConfig = demandConfigDao
          .findDemandConfigById(wifProject.getDemandConfigId());
      projections.addAll(demandConfig.getProjections());
    } else {
      projections.addAll(wifProject.getProjections());
    }
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

    final String currentYear = columnList[0];

    SQL = "select Sum(\"" + areaLabel + "\") from "
        + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable + " where \""
        + currentYear + "\" ='" + WifKeys.FUTURELU_PREFIX
        + futureLU.getFeatureFieldName() + "'";

    final Double d = geodataFinder.getSumofALU(SQL);

    return d;
  }

  public Double getSumAreaProjectionYear(
      final AllocationScenario allocationScenario, final Projection projection,
      final AllocationLU futureLU) {

    String SQL = "";

    final WifProject wifProject = allocationScenario.getWifProject();

    final String AllocationConfigsId = wifProject.getAllocationConfigsId();

    final AllocationConfigs allocationConfig = AllocationConfigsDao
        .findAllocationConfigsById(AllocationConfigsId);

    final String aluColumnProjection = allocationConfig
        .getAllocationColumnsMap().get(projection.getLabel());

    final SuitabilityConfig suitabilityConfig = wifProject
        .getSuitabilityConfig();
    final String uazDBTable = suitabilityConfig.getUnifiedAreaZone();
    final String areaLabel = wifProject.getAreaLabel();

    SQL = "select Sum(\"" + areaLabel + "\") from "
        + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable + " where \""
        + aluColumnProjection + "\" ='" + WifKeys.FUTURELU_PREFIX
        + futureLU.getFeatureFieldName() + "'";

    LOGGER.info("getSumAreaProjectionYear D1 SQL={}", SQL);
    final Double d1 = geodataFinder.getSumofALU(SQL);

    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////
    /////new for finding the area which converted to the other landuses.
    final TreeSet<Projection> projections = new TreeSet<Projection>(
        new YearComparator());
    if (allocationScenario.isManual()) {
      final DemandConfig demandConfig = demandConfigDao
          .findDemandConfigById(wifProject.getDemandConfigId());
      projections.addAll(demandConfig.getProjections());
    } else {
      projections.addAll(wifProject.getProjections());
    }
    final ArrayList<String> columnList1 = new ArrayList<String>();
    final ArrayList<String> columnList2 = new ArrayList<String>();
    for (final Projection proj : projections) {
      //if (proj.getYear()<= projection.getYear())
      if (proj.getYear().equals(projection.getYear()))
      {
        columnList1.add(proj.getLabel());
      }
      columnList2.add(proj.getLabel());
    }

    final String[] columnList = new String[columnList1.size()];
    final String[] columnListAll = new String[columnList2.size()];
    for (int ind = 0; ind < columnList1.size(); ind++)
    {
      columnList[ind] = "ALU_" + columnList1.get(ind);

    }
    for (int ind = 0; ind < columnList2.size(); ind++)
    {
      columnListAll[ind] = "ALU_" + columnList2.get(ind);

    }

    Arrays.sort(columnList);
    Arrays.sort(columnListAll);

    final String firstYear = columnListAll[0];
    String wherest=" Where \"" + firstYear + "\" ='" + WifKeys.FUTURELU_PREFIX
        + futureLU.getFeatureFieldName() + "' AND (";
    int inx=0;

    for (final String st : columnList)
    {
      //      if (inx != 0)
      //      {
      //        if (inx ==1)
      //        {
      wherest = wherest + "(\"" + st + "\" <>'" + WifKeys.FUTURELU_PREFIX
          + futureLU.getFeatureFieldName() + "' AND \"" + st + "\" <>'')";
      //        }
      //        else
      //        {
      //          wherest = wherest + " OR (\"" + st + "\" <>'" + WifKeys.FUTURELU_PREFIX
      //              + futureLU.getFeatureFieldName() + "' AND \"" + st + "\" <>'')";
      //        }
      //      }
      inx = inx+1;
    }

    wherest= wherest + ")";

    SQL = "select Sum(\"" + areaLabel + "\") from "
        + myjdbcDataStoreConfig.getSchema() + "." + uazDBTable + wherest;

    LOGGER.info("getSumAreaProjectionYear D2 SQL={}", SQL);
    final Double d2 = geodataFinder.getSumofALU(SQL);

    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////



    //return d1;
    return d1 - d2;
  }

}
