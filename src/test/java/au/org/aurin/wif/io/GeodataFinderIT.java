/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import java.util.List;

import javax.annotation.Resource;

import org.geotools.data.simple.SimpleFeatureStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class GeodataFinderIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class GeodataFinderIT extends AbstractTestNGSpringContextTests {

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The feature store. */
  private SimpleFeatureStore featureStore;

  /** The project. */
  private WifProject project;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(GeodataFinderIT.class);

  /**
   * Load wif project.
   *
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws Exception
   *           the exception
   */
  @BeforeClass(enabled = true, groups = { "setup", "geo", "database" })
  public void loadWifProject() throws WifInvalidConfigException, Exception {
    project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getSuitabilityConfig());
  }

  /**
   * Gets the sorted query test.
   *
   * @return the sorted query test
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getSortedQueryTest() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    featureStore = geodataFinder.getFeatureStorefromDB(uazDBTable);
    Assert.assertEquals(featureStore.getCount(null), 18762);
  }

  /**
   * Gets the uAZ attributes.
   *
   * @return the uAZ attributes
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getUAZAttributes() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    List<String> attrs = geodataFinder.getUAZAttributes(uazDBTable);
    Assert.assertNotEquals(attrs.size(), 58);
    Assert.assertTrue(attrs.contains("FACTOR_6"));
  }

  /**
   * Gets the distinct entries for uaz attribute.
   *
   * @return the distinct entries for uaz attribute
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getDistinctEntriesForUAZAttribute() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    List<String> possibleValues = geodataFinder
        .getDistinctEntriesForUAZAttribute(uazDBTable, "FACTOR_6");
    Assert.assertEquals(possibleValues.size(), 5);
    Assert.assertTrue(possibleValues.contains("-99.0"));
  }

  /**
   * Gets the sum of distinct entries for uaz attribute.
   *
   * @return the sum of distinct entries for uaz attribute
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getSumOfDistinctEntriesForUAZAttribute() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    Assert.assertEquals(geodataFinder.getSumOfDistinctEntriesForUAZAttribute(
        uazDBTable, "FACTOR_6"), -89.0);
  }

  /**
   * Gets the normalised sum of distinct entries uaz attribute.
   *
   * @return the normalised sum of distinct entries uaz attribute
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getNormalisedSumOfDistinctEntriesUAZAttribute() throws Exception {
    double total = geodataFinder.getNormalisedSumOfDistinctEntriesUAZAttribute(
        "demonstration_union", "TOTHU_CY", "CLIP_ACRES", "BG_ACRES");
    boolean result = false;
    if (total >= 8129.0) {
      result = true;
    }

    Assert.assertTrue(result);
  }

  /**
   * Gets the score ranges.
   * TODO
   * This test should be done aftre suitability scenario creation
   * @return the score ranges
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "setup", "geo", "database" })
  public void getScoreRanges() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    double total = geodataFinder.getAreaByScoreRanges(uazDBTable, "SCORE_1",
        "UAZ_AREA", 6000, 9000);
    boolean result = false;
    if (total >= 940.0) {
      result = true;
    }

    Assert.assertTrue(result);
  }

  /**
   * Gets the undefined.
   *
   * @return the undefined
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getUndefined() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    double total = geodataFinder.getAreaByScoreRanges(uazDBTable, "SCORE_1",
        "UAZ_AREA", -98, -98);
    LOGGER.debug("total undefined area {}", total);
    boolean result = false;
    if (total >= 0) {
      result = true;
    }

    Assert.assertTrue(result);
  }

  /**
   * Gets the uAZ attributes.
   *
   * @return the uAZ attributes
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void updatesUAZArea() throws Exception {
    String uazDBTable = project.getSuitabilityConfig().getUnifiedAreaZone();
    geodataFinder.updateUAZArea(uazDBTable);

    Assert.assertNotEquals(geodataFinder
        .getSumOfDistinctEntriesForUAZAttribute(uazDBTable,
            WifKeys.DEFAULT_AREA_COLUMN_NAME), 0);
  }
}
