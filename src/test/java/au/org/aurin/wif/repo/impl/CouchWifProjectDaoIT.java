/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.repo.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.repo.WifProjectDao;

/**
 * The Class CouchWifProjectDaoIT.
 */

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class CouchWifProjectDaoIT extends AbstractTestNGSpringContextTests {

  /** The wif project dao. */
  @Autowired
  private WifProjectDao wifProjectDao;

  /** The project label. */
  private final String projectLabel = "projectTest"
      + System.currentTimeMillis();

  /** The new project id. */
  private String newProjectId;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(CouchWifProjectDaoIT.class);

  /**
   * Persist project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couchdb" })
  public void persistProjectTest() throws Exception {

    WifProject project = new WifProject();
    project.setName(projectLabel);
    project.setRoleOwner("aurin");
    project.setOriginalUnits("m.k.s.");

    WifProject createProject = wifProjectDao.persistProject(project);
    newProjectId = createProject.getId();
    LOGGER.debug("project uuid: " + createProject.getId());
    Assert.assertNotNull(newProjectId);
  }

  /**
   * Find new project by id test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couchdb" }, dependsOnMethods = { "persistProjectTest" })
  public void findNewProjectByIdTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getCreationDate());
  }

  /**
   * Update project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couchdb" }, dependsOnMethods = { "persistProjectTest" })
  public void updateProjectTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    project.setOriginalUnits("metric");
    wifProjectDao.updateProject(project);
    WifProject project2 = wifProjectDao.findProjectById(newProjectId);
    Assert.assertEquals(project2.getOriginalUnits(), "metric");
    Assert.assertNotNull(project2.getModifiedDate());
    Assert.assertNotEquals(project2.getCreationDate(),
        project2.getModifiedDate());
  }

  /**
   * Gets the all projects by role test.
   * 
   * @return the all projects by role test
   * @throws Exception
   *           the exception
   */
  @Test(groups = { "setup", "database", "couchdb" }, dependsOnMethods = { "updateProjectTest" })
  public void getAllProjectsByRoleTest() throws Exception {

    List<WifProject> allProjects = wifProjectDao.getAllProjects("aurin");
    Assert.assertNotNull(allProjects);
    LOGGER.debug("Size = " + allProjects.size());
    Assert.assertNotEquals(allProjects.size(), 0);
  }

  /**
   * Delete project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, alwaysRun = true, groups = { "setup", "database",
      "couchdb" }, dependsOnMethods = { "getAllProjectsByRoleTest" })
  public void deleteProjectTest() throws Exception {

    WifProject project = wifProjectDao.findProjectById(newProjectId);
    wifProjectDao.deleteProject(project);
    WifProject project2 = wifProjectDao.findProjectById(newProjectId);
    Assert.assertNull(project2);
  }
}
