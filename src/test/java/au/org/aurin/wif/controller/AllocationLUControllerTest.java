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
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.svc.AllocationLUService;

/**
 * The Class AllocationLUControllerTest.
 */
public class AllocationLUControllerTest {

  /** The allocation lu controller. */
  private AllocationLUController allocationLUController;

  /**
   * Setup.
   */
  @BeforeClass (enabled = false)
  public void setup() {
    allocationLUController = new AllocationLUController();
  }

  /**
   * Creates the allocation lu.
   *
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void createAllocationLU() throws Exception {
    AllocationLUService allocationLUService = mock(AllocationLUService.class);
    allocationLUController.setAllocationLUService(allocationLUService);

    AllocationLU expectedAllocationLU = new AllocationLU();
    expectedAllocationLU.setId(9 + "");

    AllocationLU newAllocationLU = new AllocationLU();
    when(
        allocationLUService.createAllocationLU(newAllocationLU,
            "theBestAllocationLU")).thenReturn(expectedAllocationLU);

    HttpServletResponse response = mock(HttpServletResponse.class);

    AllocationLU returnedAllocationLU = allocationLUController
        .createAllocationLU("aurin", "theBestAllocationLU",
        newAllocationLU, response);
    Assert.assertEquals(returnedAllocationLU.getId().toString(), "9");
  }

  /**
   * Delete allocation lu.
   *
   * @throws Exception the exception
   */
  @Test(enabled = false, expectedExceptions = { WifInvalidInputException.class })
  public void deleteAllocationLU() throws Exception {
    AllocationLUService allocationLUService = mock(AllocationLUService.class);
    when(allocationLUService.getAllocationLU("8")).thenThrow(
        new WifInvalidInputException());
    allocationLUController.setAllocationLUService(allocationLUService);

    allocationLUController.deleteAllocationLU("aurin", "projectId", "8");
    verify(allocationLUService).deleteAllocationLU("8", "projectId");
    allocationLUController.getAllocationLU("aurin", "theBestAllocationLU", "8");
  }

  /**
   * Gets the allocation lu.
   *
   * @return the allocation lu
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void getAllocationLU() throws Exception {
    AllocationLU expectedAllocationLU = new AllocationLU();
    expectedAllocationLU.setAllocationFeatureFieldName("theBestAllocationLU");

    AllocationLUService allocationLUService = mock(AllocationLUService.class);
    when(allocationLUService.getAllocationLU("8")).thenReturn(
        expectedAllocationLU);

    allocationLUController.setAllocationLUService(allocationLUService);

    AllocationLU allocationLU = allocationLUController.getAllocationLU("aurin", 
        "projectId", "8");
    Assert.assertEquals(allocationLU.getAllocationFeatureFieldName(),
        "theBestAllocationLU");
  }

  /**
   * Gets the allocation l us for project.
   *
   * @return the allocation l us for project
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void getAllocationLUsForProject() throws Exception {

    List<AllocationLU> expectedAllocationLUs = asList(new AllocationLU(), new AllocationLU());

    AllocationLUService allocationLUService = mock(AllocationLUService.class);
    when(allocationLUService.getAllocationLUs("theBestAllocationLU"))
        .thenReturn(expectedAllocationLUs);

    allocationLUController.setAllocationLUService(allocationLUService);

    List<AllocationLU> returnedAllocationLUs = allocationLUController
        .getAllocationLUsForProject("aurin", "theBestAllocationLU");
    Assert.assertEquals(returnedAllocationLUs.size(), 2);
  }

  /**
   * Update allocation lu.
   *
   * @throws Exception the exception
   */
  @Test (enabled = false)
  public void updateAllocationLU() throws Exception {
    AllocationLUService allocationLUService = mock(AllocationLUService.class);
    allocationLUController.setAllocationLUService(allocationLUService);

    AllocationLU updateAllocationLU = new AllocationLU();
    allocationLUController.updateAllocationLU("aurin", "projectId", "8",
        updateAllocationLU);
    verify(allocationLUService).updateAllocationLU(updateAllocationLU,
        "projectId");

  }
}
