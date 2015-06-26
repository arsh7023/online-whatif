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

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.suitability.Factor;
import au.org.aurin.wif.svc.suitability.FactorService;

/**
 * The Class FactorsControllerTest.
 */
public class FactorsControllerTest {

  /** The factors controller. */
  private FactorsController factorsController;

  /**
   * Setup.
   */
  @BeforeClass (enabled = false)
  public void setup() {
    factorsController = new FactorsController();
  }

  /**
   * Creates the factor.
   *
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void createFactor() throws Exception {
    FactorService factorService = mock(FactorService.class);
    factorsController.setFactorService(factorService);

    Factor expectedFactor = new Factor();
    expectedFactor.setId(9 + "");

    Factor newFactor = new Factor();
    when(factorService.createFactor(newFactor, "theBestFactor")).thenReturn(
        expectedFactor);

    HttpServletResponse response = mock(HttpServletResponse.class);

    Factor returnedFactor = factorsController.createFactor("aurin", "theBestFactor",
        newFactor, response);
    Assert.assertEquals(returnedFactor.getId().toString(), "9");
  }

  /**
   * Delete factor.
   *
   * @throws Exception the exception
   */
  @Test(enabled = false, expectedExceptions = { WifInvalidInputException.class })
  public void deleteFactor() throws Exception {
    FactorService factorService = mock(FactorService.class);
    when(factorService.getFactor("8", "projectId"))
        .thenThrow(
        new WifInvalidInputException());
    factorsController.setFactorService(factorService);

    factorsController.deleteFactor("aurin", "projectId", "8");
    verify(factorService).deleteFactor("8", "projectId");
    factorsController.getFactor("aurin", "projectId", "8");
  }

  /**
   * Gets the factor.
   *
   * @return the factor
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void getFactor() throws Exception {
    Factor expectedFactor = new Factor();
    expectedFactor.setLabel("theBestFactor");

    FactorService factorService = mock(FactorService.class);
    when(factorService.getFactor("8", "projectId")).thenReturn(expectedFactor);

    factorsController.setFactorService(factorService);

    Factor allocationLU = factorsController.getFactor("aurin", 
        "projectId", "8");
    Assert.assertEquals(allocationLU.getLabel(), "theBestFactor");
  }

  /**
   * Gets the factors for project.
   *
   * @return the factors for project
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void getFactorsForProject() throws Exception {
    List<Factor> expectedFactors = asList(new Factor(), new Factor());

    FactorService factorService = mock(FactorService.class);
    when(factorService.getFactors("theBestFactor")).thenReturn(expectedFactors);

    factorsController.setFactorService(factorService);

    List<Factor> returnedFactors = factorsController
        .getFactorsForProject("aurin", "theBestFactor");
    Assert.assertEquals(returnedFactors.size(), 2);
  }

  /**
   * Update factor.
   *
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void updateFactor() throws Exception {
    FactorService factorService = mock(FactorService.class);
    factorsController.setFactorService(factorService);

    Factor updateFactor = new Factor();
    factorsController.updateFactor("aurin", "projectId", "8", updateFactor);
    verify(factorService).updateFactor(updateFactor, "projectId");
  }
}
