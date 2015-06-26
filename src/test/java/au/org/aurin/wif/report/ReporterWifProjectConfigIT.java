/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.report;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.ParsingException;
import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.impl.report.ConsoleReporter;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.WifKeys;
import au.org.aurin.wif.svc.suitability.SuitabilityScenarioService;

/**
 * The Class ReporterWifProjectConfigTest.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ReporterWifProjectConfigIT extends
    AbstractTestNGSpringContextTests {

  /** The reporter. */
  @Autowired
  private ConsoleReporter reporter;

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The suitability scenario service. */
  @Autowired
  private SuitabilityScenarioService suitabilityScenarioService;

  /**
   * Prints the wif project Quick test to make sure that the mappings are right
   * 
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @Test(enabled = true, groups = { "setup", "integration" })
  public void printWifProject() throws WifInvalidConfigException,
      WifInvalidInputException {
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    reporter.reportWifProjectConfig(project);
  }

  /**
   * Prints the suitability Quick test to make sure that the mappings are right
   * 
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   * @throws ParsingException
   *           the parsing exception
   */
  @Test(enabled = true, groups = { "setup", "integration" })
  public void printSuitability() throws WifInvalidConfigException,
      WifInvalidInputException, ParsingException {
    SuitabilityScenario suitabilityScenario = suitabilityScenarioService
        .getSuitabilityScenario(WifKeys.TEST_SUITABILITY_SCENARIO_ID);
    reporter.reportSuitabilityScenario(suitabilityScenario);
  }
}
