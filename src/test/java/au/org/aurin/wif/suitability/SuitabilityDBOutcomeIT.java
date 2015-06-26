/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.suitability;

import java.util.HashMap;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.GeodataFilterer;
import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * Integration test for the whole What If implementation
 *
 * @author marcosnr
 *
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityDBOutcomeIT extends AbstractTestNGSpringContextTests {

  @Autowired
  private SuitabilityScenarioService suitabilityScenarioService;

  @Autowired
  private GeodataFinder geodataFinder;
  @Autowired
  private GeodataFilterer geodataFilterer;
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityDBOutcomeIT.class);

  @Test(enabled = true, groups = { "suitability", "integration" })
  public void getWMSOutcomeTest() throws Exception {
    LOGGER.debug("getWMSOutcomeTest");
    // TODO Sometimes analysis of the whole area studies necessary,bbetter create a different test altogether  and disable it?
//    String areaAnalyzed = WifKeys.POLYGON_TEST;
     String areaAnalyzed = null;
    String crsArea = WifKeys.CRS_TEST;

    Boolean wmsOutcome = suitabilityScenarioService.getWMSOutcome(
        WifKeys.TEST_SUITABILITY_SCENARIO_ID, areaAnalyzed, crsArea);
    Assert.assertEquals(wmsOutcome, Boolean.TRUE);

    Map<String, Object> wifParameters = new HashMap<String, Object>();
    wifParameters.put(WifKeys.POLYGON, WifKeys.POLYGON_TEST);
    wifParameters.put(WifKeys.CRS_ORG, WifKeys.CRS_TEST);
    wifParameters.put(WifKeys.CRS_DEST, WifKeys.CRS_TEST);
    Filter filter = geodataFilterer.getFilterFromParameters(wifParameters);
    SimpleFeatureCollection uazCollection = geodataFinder
        .getFeatureCollectionfromDB("uaz_demonstration", filter);
    SimpleFeatureIterator it = uazCollection.features();
    Double score = 0.0;
    try {
      while (it.hasNext()) {
        SimpleFeature uazFeature = it.next();
        if (uazFeature.getID().equals("uaz_demonstration.730")) {

          score = (Double) uazFeature.getAttribute("SCORE_1");
        }
      }
    } finally {
      it.close();
    }
    // TODO this is the value when the polygon test is enforced
    Assert.assertEquals(score, new Double(10000.0));
    // TODO this is the value when the wwhole areatest is enforced
    // Assert.assertEquals((Double) score, new Double(8125.0));
  }
}
