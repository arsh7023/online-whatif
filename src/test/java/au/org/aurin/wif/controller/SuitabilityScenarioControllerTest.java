package au.org.aurin.wif.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.controller.suitability.SuitabilityScenarioController;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class SuitabilityScenarioControllerTest.
 */
public class SuitabilityScenarioControllerTest {

	/** The allocation lu controller. */
	private SuitabilityScenarioController suitabilityScenarioController;

	/**
	 * Setup.
	 */
	@BeforeClass (enabled = false)
	public void setup() {
		suitabilityScenarioController = new SuitabilityScenarioController();
	}

	/**
	 * Creates the allocation lu.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test (enabled = false)
	public void createSuitabilityScenario() throws Exception {
		SuitabilityScenarioService suitabilityScenarioService = mock(SuitabilityScenarioService.class);
		suitabilityScenarioController
				.setSuitabilityScenarioService(suitabilityScenarioService);

		SuitabilityScenario expectedSuitabilityScenario = new SuitabilityScenario();
		expectedSuitabilityScenario.setId(9 + "");

		SuitabilityScenario newSuitabilityScenario = new SuitabilityScenario();
		when(
				suitabilityScenarioService.createSuitabilityScenario(
						newSuitabilityScenario, "theBestSuitabilityScenario"))
				.thenReturn(expectedSuitabilityScenario);

		HttpServletResponse response = mock(HttpServletResponse.class);

		SuitabilityScenario returnedSuitabilityScenario = suitabilityScenarioController
				.createSuitabilityScenario("aurin",
						"theBestSuitabilityScenario", newSuitabilityScenario,
						response);
		Assert.assertEquals(returnedSuitabilityScenario.getId().toString(), "9");
	}

	/**
	 * Delete allocation lu.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test(enabled = false, expectedExceptions = { WifInvalidInputException.class })
	public void deleteSuitabilityScenario() throws Exception {
		SuitabilityScenarioService suitabilityScenarioService = mock(SuitabilityScenarioService.class);
		when(suitabilityScenarioService.getSuitabilityScenario("8")).thenThrow(
				new WifInvalidInputException());
		suitabilityScenarioController
				.setSuitabilityScenarioService(suitabilityScenarioService);

		suitabilityScenarioController.deleteSuitabilityScenario("aurin",
				"projectId", "8");
		verify(suitabilityScenarioService).deleteSuitabilityScenario("8",
				"projectId");
		suitabilityScenarioController.getSuitabilityScenario("aurin",
				"theBestSuitabilityScenario", "8");
	}

	/**
	 * Gets the allocation lu.
	 * 
	 * @return the allocation lu
	 * @throws Exception
	 *             the exception
	 */
	@Test (enabled = false)
	public void getSuitabilityScenario() throws Exception {
		SuitabilityScenario expectedSuitabilityScenario = new SuitabilityScenario();
		expectedSuitabilityScenario.setLabel("theBestSuitabilityScenario");

		SuitabilityScenarioService suitabilityScenarioService = mock(SuitabilityScenarioService.class);
		when(suitabilityScenarioService.getSuitabilityScenario("8"))
				.thenReturn(expectedSuitabilityScenario);

		suitabilityScenarioController
				.setSuitabilityScenarioService(suitabilityScenarioService);

		SuitabilityScenario suitabilityScenario = suitabilityScenarioController
				.getSuitabilityScenario("aurin", "projectId", "8");
		Assert.assertEquals(suitabilityScenario.getLabel(),
				"theBestSuitabilityScenario");
	}

	/**
	 * Gets the allocation l us for project.
	 * 
	 * @return the allocation l us for project
	 * @throws Exception
	 *             the exception
	 */
	@Test (enabled = false)
	public void getSuitabilityScenariosForProject() throws Exception {

		List<SuitabilityScenario> expectedSuitabilityScenarios = asList(
				new SuitabilityScenario(), new SuitabilityScenario());

		SuitabilityScenarioService suitabilityScenarioService = mock(SuitabilityScenarioService.class);
		when(
				suitabilityScenarioService
						.getSuitabilityScenarios("theBestSuitabilityScenario"))
				.thenReturn(expectedSuitabilityScenarios);

		suitabilityScenarioController
				.setSuitabilityScenarioService(suitabilityScenarioService);

		List<SuitabilityScenario> returnedSuitabilityScenarios = suitabilityScenarioController
				.getSuitabilityScenariosForProject("aurin", "theBestSuitabilityScenario");
		Assert.assertEquals(returnedSuitabilityScenarios.size(), 2);
	}

	/**
	 * Update allocation lu.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test (enabled = false)
	public void updateSuitabilityScenario() throws Exception {
		SuitabilityScenarioService suitabilityScenarioService = mock(SuitabilityScenarioService.class);
		suitabilityScenarioController
				.setSuitabilityScenarioService(suitabilityScenarioService);

		SuitabilityScenario updateSuitabilityScenario = new SuitabilityScenario();
		suitabilityScenarioController.updateSuitabilityScenario("aurin", "projectId",
				"8", updateSuitabilityScenario);
		verify(suitabilityScenarioService).updateSuitabilityScenario(
				updateSuitabilityScenario, "projectId");

	}
}
