/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.io.File;
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

import au.org.aurin.wif.io.WifFileUtils;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.restclient.AurinServiceClient;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class Upload2MiddlewareIT extends AbstractTestNGSpringContextTests {

  /** The file utils. */
  @Autowired
  private WifFileUtils fileUtils;

  /** The project service. */
  @Resource
  private AurinServiceClient aurinServiceClient;
  /** The project service. */
  @Resource
  private ProjectService projectService;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(Upload2MiddlewareIT.class);

  /**
   * Creates the project test. Not supported in this iteration
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = false, groups = { "integration", "service" })
  public void uploadJsonTest() throws Exception {
    String userId = "whatif";
    WifProject project = projectService.getProject(WifKeys.TEST_PROJECT_ID);
    File input = fileUtils.getJsonFile("JSONs/collections/Ohiotest.json");
    Map<String, Object> datastoreUri = aurinServiceClient.shareAurinProject(
        input, userId, project);
    LOGGER.debug("response " + datastoreUri.get("result"));

    Assert.assertNotNull(datastoreUri);
  }

  /**
   * Creates the projectmetadata test. Not supported in this iteration
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "integration", "service" })
  public void uploadJsonItemTest() throws Exception {
    String userId = "whatifTest";
    Map<String, Object> item = new HashMap<String, Object>();
    item.put("userId", userId);
    item.put("projectId", "project-meta-whatif");
    Map<String, Object> metadata = new HashMap<String, Object>();
    metadata.put("type", "meta");

    item.put("timeStamp", "2013-08-19T05:13:45.128+0000");
    metadata.put("name", "project name");
    metadata.put("type", "meta");
    metadata.put("description", "project description");
    item.put("data", metadata);

    Assert.assertTrue(aurinServiceClient.sendPersistenceItem(item, userId));
  }

}
