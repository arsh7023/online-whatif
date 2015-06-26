/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.model.suitability.SuitabilityLU;
import au.org.aurin.wif.repo.impl.CouchWifProjectDao;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ProjectConfigurationIT extends AbstractTestNGSpringContextTests {

  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The wif project dao. */
  @Autowired
  private CouchWifProjectDao wifProjectDao;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ProjectConfigurationIT.class);

  @Test(enabled = true, groups = { "setup", "service" })
  public void getProjectConfigurationTest() throws Exception {

    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getSuitabilityLUs());
    SuitabilityLU residential = project.getSuitabilityLUByName("Residential");
    Map<String, String> associatedALUs = residential.getAssociatedALUsMap();
    LOGGER.debug("AssociatedLUs size: " + associatedALUs.size());
    project.getSuitabilityLUByName("Residential").setAssociatedALUsMap(
        new HashMap<String, String>());
    wifProjectDao.updateProject(project);
    WifProject project2 = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    SuitabilityLU residential2 = project2.getSuitabilityLUByName("Residential");
    Map<String, String> associatedALUs2 = residential2.getAssociatedALUsMap();
    LOGGER.debug("AssociatedLUs2 size: " + associatedALUs2.size());
    Assert.assertEquals(associatedALUs2.size(), 0);
    WifProject project3 = projectService
        .getProjectConfiguration(WifKeys.TEST_PROJECT_ID);
    SuitabilityLU residential3 = project3.getSuitabilityLUByName("Residential");
    Map<String, String> associatedALUs3 = residential3.getAssociatedALUsMap();
    LOGGER.debug("AssociatedLUs3 size: " + associatedALUs3.size());
    Assert.assertEquals(associatedALUs3.size(), 4);
  }
}
