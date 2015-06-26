/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import javax.annotation.Resource;

import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.simple.SimpleFeature;
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
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.AllocationScenario;
import au.org.aurin.wif.model.allocation.control.ALURule;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.allocation.AllocationScenarioService;

/**
 * The Class GeodataFiltererIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class GeodataFiltererIT extends AbstractTestNGSpringContextTests {

  /** The geodata finder. */
  @Autowired
  private GeodataFinder geodataFinder;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The geodata filterer. */
  @Autowired
  private GeodataFilterer geodataFilterer;

  /** The project. */
  private WifProject project;

  /** The allocation scenario service. */
  @Resource
  private AllocationScenarioService allocationScenarioService;

  /** The feature store. */
  private SimpleFeatureStore featureStore;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(GeodataFiltererIT.class);

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
    final String uazDBTable = project.getSuitabilityConfig()
        .getUnifiedAreaZone();
    featureStore = geodataFinder.getFeatureStorefromDB(uazDBTable);
  }

  /**
   * Gets the allocation rule.
   * 
   * @return the allocation rule
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "geo", "database" })
  public void getAllocationRule() throws Exception {

    LOGGER.debug("getAllocationRule: {}");
    final AllocationScenario allocationScenario = allocationScenarioService
        .getAllocationScenario(WifKeys.TEST_ALLOCATION_SCENARIO_ID);
    final AllocationLU residentiallu = project
        .getExistingLandUseByLabel("Conservation");
    final String existingLULabel = project.getExistingLUAttributeName();
    final String scoreLabel = residentiallu.getAssociatedLU()
        .getFeatureFieldName();

    final ALURule rule = geodataFilterer.getAllocationRule(residentiallu,
        allocationScenario, scoreLabel, existingLULabel, "", "", null, 0.0);
    final Query ruleQuery = rule.getRuleQuery();
    // ruleQuery.
    final SimpleFeatureCollection sortedUazCollection = featureStore
        .getFeatures(rule.getRuleQuery());
    LOGGER.debug("sortedUazCollection size: {}", sortedUazCollection.size());
    // Assert.assertEquals(sortedUazCollection.size(), 7834);
    final SimpleFeatureIterator its = sortedUazCollection.features();
    // 2. for each ORDERED UAZ
    int remaining = 0;
    final String areaLabel = allocationScenario.getWifProject().getAreaLabel();
    while (its.hasNext() && remaining < 10) {
      // 3. while there is a still area to allocate
      final SimpleFeature uazFeature = its.next();

      final String id = uazFeature.getID();
      LOGGER.debug("Allocating feature id: {}", id);
      final Double featureArea = (Double) uazFeature.getAttribute(areaLabel);
      LOGGER.debug("feature suitability score= {}, feature area = {},",
          uazFeature.getAttribute(scoreLabel), featureArea);
      remaining++;
    }
  }
}
