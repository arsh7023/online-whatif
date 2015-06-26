package au.org.aurin.wif.config;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.allocation.LandUseFunction;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.WifKeys;

/**
 * <b>ProjectAnalyzer.java</b> :
 * 
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@Component
public class ProjectAnalyzer {

  /** The Constant serialVersionUID. */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 286786908953L;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectAnalyzer.class);

  /**
   * to handle initialization.
   */

  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * to handle destroy.
   */

  @PreDestroy
  public void cleanup() {
    LOGGER.trace(" Service succesfully cleared! ");
  }

  /**
   * Gets the existing land use by label.
   *
   * @param project the project
   * @param function the function
   * @return the existing land use by label
   * @throws WifInvalidInputException the wif invalid input exception
   */
  public Set<AllocationLU> getAllocationLUsByFunction(WifProject project,
      LandUseFunction function) throws WifInvalidInputException {
    Set<AllocationLU> allocationLUs = new HashSet<AllocationLU>();

    for (AllocationLU alu : project.getAllocationLandUses()) {
      if (alu.getLandUseFunction().equals(function)) {
        allocationLUs.add(alu);
      }
    }
    return allocationLUs;
  }

  /**
   * Modify factor rating.
   *
   * @param project the project
   * @param newScenarioName the new scenario
   * @param newLUName the new lu name
   * @param newFactorName the new factor
   * @param newImportance the new importance
   * @param newFactorTypeName the new factor type name
   * @param newRating the new rating
   * @throws WifInvalidInputException the wif invalid input exception
   */
  public void modifyFactorRating(WifProject project, String newScenarioName,
      String newLUName, String newFactorName, String newImportance,
      String newFactorTypeName, String newRating)
      throws WifInvalidInputException {
    SuitabilityScenario suitabilityScn = project
        .getSuitabilityScenarioByLabel(newScenarioName);
    suitabilityScn.modifyFactorRating(newLUName, newFactorName, newImportance,
        newFactorTypeName, newRating);

  }
}
