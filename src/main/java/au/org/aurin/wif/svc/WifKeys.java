package au.org.aurin.wif.svc;

import java.util.Arrays;
import java.util.Collection;

/**
 * The Class WifKeys.
 */
public class WifKeys {

  /** The Constant WIF_KEY_VERSION. */
  public static final String WIF_KEY_VERSION = "1.0";

  /** The Constant UAZ. */
  public static final String UAZ = "uazTable";

  /** The Constant QUERY. */
  public static final String QUERY = "queryTxt";

  /** The Constant PROJECT_ID_KEY. */
  public static final String PROJECT_ID_KEY = "projectId";

  /** The Constant CRS_ORG. */
  public static final String CRS_ORG = "CRS_ORG";

  /** The Constant CRS_DEST. */
  public static final String CRS_DEST = "CRS_DEST";

  /** The Constant POLYGON. */
  public static final String POLYGON = "POLYGON";

  /** The Constant ALLOCATION_SCENARIO. */
  public static final String ALLOCATION_SCENARIO = "allocationScn";

  /** The Constant HOUSEHOLD_SIZE. */
  public static final String HOUSEHOLD_SIZE = "HOUSEHOLD_SIZE";

  /** The Constant FILTER. */
  public static final String FILTER = "FILTER";

  /** The Constant TEST_FACTOR_ID. */
  public static final String TEST_FACTOR_ID = "100";

  /** The Constant ALLOCATION_PERFORMANCE_INDEX. */
  public static final int ALLOCATION_PERFORMANCE_INDEX = 15;

  /** The Constant TEST_SUITABILITY_SCENARIO_ID. */
  public static final String TEST_SUITABILITY_SCENARIO_ID = "SuburbanizationTestID";

  /** The test project id. */
  public static String TEST_PROJECT_ID = "DemonstrationTestID";

  /** The Constant POLYGON_TEST. */
  public static final String POLYGON_TEST = "POLYGON((1799535 788274,1799535 788000,1799000 788000,1799000 788274,1799535 788274))";// 8
  /** The Constant CRS_TEST_HB. */
  public static final String CRS_TEST_HB = "EPSG:28356";

  /** The Constant CRS_TEST. */
  public static final String CRS_TEST = "EPSG:102723";

  /** The Constant CRS_INTERFACE. */
  public static final String CRS_INTERFACE = "EPSG:900913";

  /** The Constant CRS_DEFAULT. */
  public static final String CRS_DEFAULT = "EPSG:4326"; // was EPSG:4283

  /** The Constant CRS_TEST. */
  public static final String CRS_WANNEROO = "EPSG:28350";

  /** The Constant POLYGON_ALLOCATION_TEST. */
  public static final Object POLYGON_ALLOCATION_TEST =
      // null;//it will only work if the whole area is selected, but makes the
      // automated testing very slow
      "POLYGON((1799535 788274,1799535 788000,1799000 788000,1799000 788274,1799535 788274))";// 8
  // features

  /** The Constant TIME_LAPSE. */
  public static final long TIME_LAPSE = 1000000;

  /** The Constant SCORE_SUFFIX. */
  public static final String SCORE_SUFFIX = "score_";

  /** The Constant STATUS_KEY. */
  public static final String STATUS_KEY = "status";

  /** The Constant SETUP_PROCESS_KEY. */
  public static final String SETUP_PROCESS_KEY = "process";

  /** The Constant PROCESS_STATE_SETUP. */
  public static final String PROCESS_STATE_SETUP = "setup";

  /** The Constant PROCESS_STATE_SUCCESS. */
  public static final String PROCESS_STATE_SUCCESS = "success";

  /** The Constant PROCESS_STATE_NA. */
  public static final String PROCESS_STATE_NA = "not available";

  /** The Constant PROCESS_STATE_RUNNING. */
  public static final String PROCESS_STATE_RUNNING = "running";

  /** The Constant PROCESS_STATE_FAILED. */
  public static final String PROCESS_STATE_FAILED = "failed";

  /** The Constant SUITABILITY_PROCESS_STATE_SETUP. */
  public static final String SUITABILITY_PROCESS_STATE_SETUP = "wms suitability analysis";

  /** The Constant ALLOCATION_PROCESS_STATE_SETUP. */
  public static final String ALLOCATION_PROCESS_STATE_SETUP = "allocation analysis";

  /** The Constant GEOMETRY_COLUMN_KEY. */
  public static final String GEOMETRY_COLUMN_KEY = "geometry_column_name";

  /** The Constant DEFAULT_COLUMN_NAME. */
  public static final String DEFAULT_COLUMN_NAME = "the_geom";

  /** The Constant STANDARD_ID_NOT_FOUND_MSG. */
  public static final String STANDARD_ID_NOT_FOUND_MSG = "Document with uuid = {} not found in database: {}! Returning a null object. ";

  /** The Constant STANDARD_ALREADY_EXISTS_MSG. */
  public static final String STANDARD_ALREADY_EXISTS_MSG = "Document with uuid = {} already in database: {}! Returning a null object. ";

  /** The Constant TEST_ROLE_ID. */
  public static final String TEST_ROLE_ID = "aurin";

  /** The Constant TEST_DEMAND_CONFIG_ID. */
  public static final String TEST_DEMAND_CONFIG_ID = "demonstrationDemandConfigTestId";

  /** The Constant TEST_MANUAL_DEMAND_CONFIG_ID. */
  public static final String TEST_MANUAL_DEMAND_CONFIG_ID = "demonstrationManualDemandConfigTestId";

  /** The Constant DEFAULT_SUITABILITY_CATEGORIES. */
  public static final Collection<? extends String> DEFAULT_SUITABILITY_CATEGORIES = Arrays
      .asList("Low", "Medium Low", "Medium", "Medium High", "High");

  /** The Constant TEST_DEMAND_SCENARIO_ID. */
  public static final String TEST_DEMAND_SCENARIO_ID = "demandScenarioTestId";

  /** The Constant TEST_MANUAL_DEMAND_SCENARIO_ID. */
  public static final String TEST_MANUAL_DEMAND_SCENARIO_ID = "manualdemandScenarioTestId";

  /** The Constant TEST_ALLOCATION_SCENARIO_ID. */
  public static final String TEST_ALLOCATION_SCENARIO_ID = "allocationScenarioTestId";

  /** The Constant TEST_ALLOCATION_REPORT_ID. */
  public static final String TEST_ALLOCATION_REPORT_ID = "allocationScenarioReportTestId";

  /** The Constant DEFAULT_AUTOMATIC_TREND_NAME. */
  public static final String DEFAULT_AUTOMATIC_TREND_NAME = "Growth Trend";

  /** The Constant DEFAULT_DEMAND_SCENARIO_NAME. */
  public static final String DEFAULT_DEMAND_SCENARIO_NAME = "Growth Trend Scenario";

  /** The Constant TEST_QUERY. */
  public static final String TEST_QUERY = "INTERSECTS(the_geom, POLYGON ((1799535 788274, 1799535 788000, 1799000 788000, 1799000 788274, 1799535 788274)))";

  /** The Constant LOCAL_SCORE_LABEL. */
  public static final String LOCAL_SCORE_LABEL = "506";

  /** The Constant FUTURELU_PREFIX. */
  public static final String FUTURELU_PREFIX = "F";

  /** The Constant DEFAULT_AREA_COLUMN_NAME. */
  public static final String DEFAULT_AREA_COLUMN_NAME = "UAZ_AREA";

  /** The Constant TEST_AUTOMATIC_SCENARIO_ID. */
  public static final String TEST_AUTOMATIC_SCENARIO_ID = "DemonstrationAutomatedScenarioTestId";

  /** The Constant SSL_PORT. */
  public static final int SSL_PORT = 443;

  /** The Constant X_AURIN_HEADER. */
  public static final String X_AURIN_HEADER = "X-AURIN-USER-ID";

  /** The Constant ACCEPT_HEADER. */
  public static final String ACCEPT_HEADER = "Accept";

  /** The Constant LOCATION_HEADER. */
  public static final String LOCATION_HEADER = "Location";

  /** The Constant CONTENT_TYPE_HEADER. */
  public static final String CONTENT_TYPE_HEADER = "Content-Type";

  /** The middleware upload svc. */
  public static String MIDDLEWARE_UPLOAD_SVC = "/myaurin/api/datasets/";

  /** The middleware persistence svc. */
  public static String MIDDLEWARE_PERSISTENCE_SVC = "/persistence/items/";
  /** The not convertable score. */
  public static Double NOT_CONVERTABLE_SCORE = -1.0;

  /** The not developable score. */
  public static Double NOT_DEVELOPABLE_SCORE = -100.0;

  /** The not suitable score. */
  public static Double NOT_SUITABLE_SCORE = 0.0;

  /** The undefined score. */
  public static Double UNDEFINED_SCORE = -999.0;

  /** The enable cache. */
  public static boolean ENABLE_CACHE = false;

  /** The suitability scenario. */
  public static String SUITABILITY_SCENARIO = "suitabilityScn";

  /** The demand scenario. */
  public static String DEMAND_SCENARIO = "demandScn";

  /** The control scenario. */
  public static String CONTROL_SCENARIO = "controlScn";

  /** The lu. */
  public static String LU = "lu";

  /** The FACTO r3. */
  public static String FACTOR3 = "factor3";

  /** The RATIN g3. */
  public static String RATING3 = "rating3";

  /** The factor. */
  public static String FACTOR = "factor";

  /** The rating. */
  public static String RATING = "rating";

  /** The FACTO r2. */
  public static String FACTOR2 = "factor2";

  /** The RATIN g2. */
  public static String RATING2 = "rating2";

  /** The suitabilitylu name. */
  public static String SUITABILITYLU_NAME = "suitabilityLUName";

  /** The importance. */
  public static String IMPORTANCE = "importance";

  /** The factor type. */
  public static String FACTOR_TYPE = "factorType";

  /** The convert from. */
  public static String CONVERT_FROM = "convertFrom";

  /** The total population. */
  public static String TOTAL_POPULATION = "totalPopulation";

  /** The gq population. */
  public static String GQ_POPULATION = "gqPopulation";

  /** The avg hh size. */
  public static String AVG_HH_SIZE = "avgHHSize";

  /** The current density. */
  public static String CURRENT_DENSITY = "currentDensity";

  /** The future density. */
  public static String FUTURE_DENSITY = "futureDensity";

  /** The current breakdown. */
  public static String CURRENT_BREAKDOWN = "currentBreakdown";

  /** The future breakdown. */
  public static String FUTURE_BREAKDOWN = "futureBreakdown";

  /** The current vacancy. */
  public static String CURRENT_VACANCY = "currentVacancy";

  /** The future vacancy. */
  public static String FUTURE_VACANCY = "futureVacancy";

  /** The infill rate. */
  public static String INFILL_RATE = "infillRate";

  /** The existing lu. */
  public static String EXISTING_LU = "existingLU";

  /** The projection year. */
  public static String PROJECTION_YEAR = "projectionYear";

  /** The projected size. */
  public static String PROJECTED_SIZE = "projectedSize";

  /** The future lu. */
  public static String FUTURE_LU = "futureLU";

  /** The priority. */
  public static String PRIORITY = "priority";

  /** The sector name. */
  public static String SECTOR_NAME = "sectorName";

  public static int GEOJSON_PRECISION = 15;

  public static String DEMO_ALLOCATION_0 = "ALU_0";

  /** The New test project name. */
  public static String TEST_PROJECT_NAME = "Perth Demonstration";

  /** The shib name to see all projects. */
  public static String SHIB_ROLE_NAME = "alireza.shamakhy@unimelb.edu.au";// "Alireza+Shamakhy"

  /** The demo project name. */
  public static String DEMO_PROJECT_NAME = "wanneroo";

  public static String DEMO_PROJECT_NAME_ADMIN = "WannerooAdmin";

}
