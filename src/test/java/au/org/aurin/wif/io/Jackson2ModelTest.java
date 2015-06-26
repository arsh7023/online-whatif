/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.WifInvalidConfigException;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.allocation.AllocationLU;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.model.suitability.SuitabilityScenario;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosnr
 */
@ContextConfiguration(locations = { "/test-simple-context.xml" })
public class Jackson2ModelTest extends AbstractTestNGSpringContextTests {

  /** The mapper. */
  private ObjectMapper mapper;

  @Autowired
  private WifFileUtils fileUtils;

  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(Jackson2ModelTest.class);

  /**
   * Load wif project.
   * 
   * @throws WifInvalidConfigException
   *           the wif invalid config exception
   * @throws WifInvalidInputException
   *           the wif invalid input exception
   */
  @BeforeClass(enabled = true, groups = { "setup", "interface" })
  public void loadWifProject() throws WifInvalidConfigException,
      WifInvalidInputException {
    mapper = new ObjectMapper();

  }

  /**
   * Parses the alu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "interface" })
  public void parseALUTest() throws Exception {

    File input = fileUtils.getJsonFile("JSONs/CBAllocationLU2ModelTest.json");
    AllocationLU allocationLU = mapper.readValue(input, AllocationLU.class);
    LOGGER.debug(allocationLU.toString());
    Assert.assertEquals(allocationLU.getLabel(), "Low Density Res.");
  }

  /**
   * Parses the project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "interface" })
  public void parseProjectTest() throws Exception {
    WifProject project = new WifProject();

    File input = fileUtils.getJsonFile("JSONs/CBDemonstrationNew.json");
    project = mapper.readValue(input, WifProject.class);
    Assert.assertEquals(project.getName(), "Demonstration");
    LOGGER.debug(project.toString());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
  }

  @Test(enabled = true, groups = { "setup", "interface" })
  public void parseCouchDBProjectTest() throws Exception {
    WifProject project = new WifProject();

    File input = fileUtils.getJsonFile("JSONs/CBDemonstration.json");
    project = mapper.readValue(input, WifProject.class);
    Assert.assertEquals(project.getName(), "Demonstration");
    LOGGER.debug(project.toString());
    Assert.assertNotNull(project.getId());
    Assert.assertNotNull(project.getSuitabilityLUs().iterator().next()
        .getAssociatedALUsMap().size());
  }

  /**
   * Parses the slu test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "interface" })
  public void parseSLUTest() throws Exception {
    File input = fileUtils.getJsonFile("JSONs/CBSuitabilityLU.json");

    SuitabilityLU slu = mapper.readValue(input, SuitabilityLU.class);
    LOGGER.debug(slu.toString());
    Assert.assertEquals(slu.getLabel(), "Residential");
  }

  @Test(groups = { "setup", "interface" })
  public void parseNewSuitabilityScenarioTest() throws Exception {
    File input = fileUtils.getJsonFile("JSONs/CBSuburbanizationNew.json");
    SuitabilityScenario suitabilityScenario = mapper.readValue(input,
        SuitabilityScenario.class);
    Assert.assertEquals(suitabilityScenario.getLabel(), "SuburbanizationNew");
    LOGGER.debug(suitabilityScenario.toString());
    Assert.assertNotNull(suitabilityScenario.getSuitabilityRules().iterator()
        .next().getConvertibleLUsMap().size());
  }
}
