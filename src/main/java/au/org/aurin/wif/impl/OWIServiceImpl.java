package au.org.aurin.wif.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.geotools.factory.GeoTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import au.org.aurin.wif.io.DataStoreClient;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.OWIService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.datacreator.DBDataCreatorService;

/**
 * The Class OWIServiceImpl.
 */
@Service
@Qualifier("oWIService")
public class OWIServiceImpl implements OWIService {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1363346734533L;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(OWIServiceImpl.class);

  /** The data creator. */
  @Autowired
  private DBDataCreatorService dataCreator;

  /** The versions map. */
  private final HashMap<String, String> versionsMap = new HashMap<String, String>();

  /** The data store client. */
  @Inject
  private DataStoreClient dataStoreClient;

  /**
   * Inits the demonstration data.
   */
  @PostConstruct
  public void init() {
    LOGGER.info("Initializing version: {} ", WifKeys.WIF_KEY_VERSION);
    LOGGER.info("Spring version: {} ", SpringVersion.getVersion());
    LOGGER.info("GeoTools version: {} ", GeoTools.getVersion());
    // LOGGER.info("Datastore client version: {} ",
    // dataStoreClient.getVersion());
    LOGGER.info("loading demonstration data...");

    try {

      final WifProject project = dataCreator.createDemonstrationModule(
          WifKeys.TEST_PROJECT_ID, WifKeys.TEST_SUITABILITY_SCENARIO_ID,
          WifKeys.TEST_DEMAND_CONFIG_ID, WifKeys.TEST_DEMAND_SCENARIO_ID,
          WifKeys.TEST_ALLOCATION_SCENARIO_ID,
          WifKeys.TEST_ALLOCATION_REPORT_ID,
          WifKeys.TEST_MANUAL_DEMAND_CONFIG_ID);
      Assert.notNull(project, "Default project must not be null");
      LOGGER.info(" successfully loaded all demonstration data!!");
    } catch (final Exception e) {
      LOGGER.error(" demonstration data could not be loaded!");
    }
  }

  /**
   * Cleanup.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /*
   * (non-Javadoc)
   * @see au.org.aurin.wif.svc.OWIService#getOWIVersion()
   */
  public Map<String, String> getOWIVersion() {
    LOGGER.info("Using version: " + WifKeys.WIF_KEY_VERSION);
    LOGGER.info("Spring version: {} ", SpringVersion.getVersion());
    LOGGER.info("GeoTools version: {} ", GeoTools.getVersion());
    // LOGGER.info("Datastore client version: {} ",
    // dataStoreClient.getVersion());
    versionsMap.put("What-If API", WifKeys.WIF_KEY_VERSION);
    versionsMap.put("Spring", SpringVersion.getVersion());
    versionsMap.put("Geotools", GeoTools.getVersion().toString());
    versionsMap.put("DatastoreClient", "N/A");
    return versionsMap;
  }
}
