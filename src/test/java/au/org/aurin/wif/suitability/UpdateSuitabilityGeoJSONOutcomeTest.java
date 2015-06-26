/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.suitability;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.io.GeodataFinder;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.FactorImportance;
import au.org.aurin.wif.model.suitability.FactorTypeRating;
import au.org.aurin.wif.model.suitability.SuitabilityRule;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.repo.suitability.SuitabilityScenarioDao;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration test for the whole What If implementation
 * 
 * @author marcosnr
 * 
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class UpdateSuitabilityGeoJSONOutcomeTest extends
		AbstractTestNGSpringContextTests {

	@Autowired
	private GeodataFinder geodataFinder;

	@Autowired
	private WifProjectDao wifProjectDao;

	/** The allocation lu dao. */
	@Autowired
	private SuitabilityScenarioDao suitabilityScenarioDao;

	@Autowired
	private SuitabilityScenarioService suitabilityScenarioService;

	@Test(enabled = false, groups = { "suitability", "integration" })
	public void getOutcomeTest() throws Exception {

		// Making changes
		ObjectMapper mapper = new ObjectMapper();
		File input = geodataFinder
				.getJsonFile("JSONs/CBUpdateSuitabilityScenarioOutcome.json");

		SuitabilityScenario suitabilityScenario = mapper.readValue(input,
				SuitabilityScenario.class);
		System.out
				.println("update suitabilityScenario from Jackson test, suitabilityScenario label: "
						+ suitabilityScenario.getLabel());
		suitabilityScenario.setId(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
		// Updating changes
		suitabilityScenarioService.updateSuitabilityScenario(
				suitabilityScenario, WifKeys.TEST_PROJECT_ID.toString());
		// Checking values where updated
		SuitabilityScenario suitabilityScenario2 = suitabilityScenarioService
				.getSuitabilityScenario(suitabilityScenario.getId().toString(),
						WifKeys.TEST_PROJECT_ID.toString());

		Set<SuitabilityRule> suitabilityRules2 = suitabilityScenario2
				.getSuitabilityRules();
		boolean found = false;
		boolean notFound = true;
//		Assert.assertTrue(found);
//		Assert.assertTrue(notFound);

		String areaAnalyzed = WifKeys.POLYGON_TEST;
		String crsArea = WifKeys.CRS_TEST;
		SimpleFeatureCollection suitabilityAnalysis = suitabilityScenarioService
				.getOutcome(WifKeys.TEST_SUITABILITY_SCENARIO_ID, areaAnalyzed,
						crsArea);
		SimpleFeatureIterator it = suitabilityAnalysis.features();
		System.out
				.println("results feature size " + suitabilityAnalysis.size());
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
		Assert.assertEquals(score, new Double(90000));

	}

	@Test(enabled = false, groups = { "suitability", "integration" }, dependsOnMethods = { "getOutcomeTest" })
	public void rollBackTest() throws Exception {

		// Making changes
		ObjectMapper mapper = new ObjectMapper();

		// Putting the DB to its previous state
		File input2 = geodataFinder.getJsonFile("JSONs/Suburbanization.json");

		SuitabilityScenario savedSuitabilityScenario = mapper.readValue(input2,
				SuitabilityScenario.class);
		System.out
				.println("update savedSuitabilityScenario from Jackson test, suitabilityScenario label: "
						+ savedSuitabilityScenario.getLabel());
		savedSuitabilityScenario.setId(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
		suitabilityScenarioService.updateSuitabilityScenario(
				savedSuitabilityScenario, WifKeys.TEST_PROJECT_ID.toString());
	}
}
