/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.suitability;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * 
 * @author marcosnr
 * 
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class SuitabilityGeoJSONOutcomeIT extends
    AbstractTestNGSpringContextTests {

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(SuitabilityGeoJSONOutcomeIT.class);

  @Autowired
  private SuitabilityScenarioService suitabilityScenarioService;

  @Test(enabled = true, groups = { "suitability", "integration" })
  public void getOutcomeTest() throws Exception {
    String areaAnalyzed = WifKeys.POLYGON_TEST;
    String crsArea = WifKeys.CRS_TEST;
    SimpleFeatureCollection suitabilityAnalysis = suitabilityScenarioService
        .getOutcome(WifKeys.TEST_SUITABILITY_SCENARIO_ID, areaAnalyzed, crsArea);
    SimpleFeatureIterator it = suitabilityAnalysis.features();
    LOGGER.debug("results feature size: " + suitabilityAnalysis.size());
    Assert.assertEquals(suitabilityAnalysis.size(), (8));
    Double score = 0.0;
    try {
      while (it.hasNext()) {
        SimpleFeature uazFeature = it.next();
        score += (Double) uazFeature.getAttribute("SCORE_1");
      }

    } finally {
      it.close();
    }
    LOGGER.debug("Score = " + score);
    Assert.assertNotEquals(score, new Double(0));
    // FIXME This was before, please check
    // Assert.assertEquals(score, new Double(90000));
  }
}
