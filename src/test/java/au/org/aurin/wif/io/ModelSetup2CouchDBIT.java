/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.config.InvalidEntityIdException;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.repo.WifProjectDao;
import au.org.aurin.wif.svc.ProjectService;
import au.org.aurin.wif.svc.datacreator.DBSetupDataCreatorService;

/**
 * The Class ModelSetup2CouchDBIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ModelSetup2CouchDBIT extends AbstractTestNGSpringContextTests {
  /**
   * logger.
   */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ModelSetup2CouchDBIT.class);

  /** The setupCreator. */
  @Autowired
  private DBSetupDataCreatorService setupCreator;

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  @Resource
  private ProjectService projectService;

  /** The project id. */
  private String projectId;

  /**
   * Persist project setup test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "database", "couchdb" })
  public void persistProjectSetupTest() throws Exception {
    // projectId = "ProjectTestId";
    projectId = "ProjectTestId" + System.currentTimeMillis();
    setupCreator.createSetupModule(projectId);
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, dependsOnMethods = { "persistProjectSetupTest" }, expectedExceptions = InvalidEntityIdException.class, groups = {
      "setup", "service" })
  public void deleteProjectTest() throws Exception {
    LOGGER.debug("delete project test, project id: " + projectId);
    projectService.deleteProject(projectId);
    WifProject project = projectService.getProject(projectId);
    Assert.assertNull(project);
  }
}
