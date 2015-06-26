package au.org.aurin.wif.restclient;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.HttpClientErrorException;
import org.testng.Assert;
import org.testng.annotations.Test;

import au.org.aurin.wif.config.IntegrationTestConfig;
import au.org.aurin.wif.exception.validate.WifInvalidInputException;
import au.org.aurin.wif.io.SslUtil;
import au.org.aurin.wif.model.WifProject;
import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class ProjectServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class StandAloneProjectRestIT extends AbstractTestNGSpringContextTests {
  private static final String OS = System.getProperty("os.name").toLowerCase();
  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The integration test config. */
  @Resource
  private IntegrationTestConfig integrationTestConfig;

  /** The project id. */
  private String projectId;

  /** The role id. */
  private final String roleId = WifKeys.TEST_ROLE_ID;

  private String filePath;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(StandAloneProjectRestIT.class);

  /**
   * Upload zip.
   * 
   * @param input
   *          the input
   * 
   */

  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" })
  public void uploadZip() {
    // TODO the validation of CRS is not working get very well for the very
    // strange Ohio demo
    // File input = getJsonFile("zips/test.zip");
    final File input = getJsonFile("zips/Perth_small_test.zip");
    final String response = projectServiceClient.uploadZip(input);
    // Have to do these to comply with UI requirements for response format
    filePath = response.substring(14, response.indexOf(",") - 1);
    LOGGER.debug("filePath " + filePath);

    Assert.assertNotNull(filePath);
  }

  /**
   * Creates the project.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = { "uploadZip" })
  public void createProject() throws Exception {
    SslUtil.trustSelfSignedSSL();
    final WifProject wifProject = new WifProject();
    wifProject.setName("Stand aloneProjectServiceRestIT");
    wifProject.setOriginalUnits("m.k.s.");
    wifProject.setAnalysisOption("suitability");
    wifProject.setLocalShpFile(filePath);
    projectId = projectServiceClient.createProject(roleId, wifProject);
    Assert.assertNotNull(projectId);
    LOGGER.debug("project Id " + projectId);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.trace("Waiting for setup to complete...");
      Thread.sleep(3000);
      resp = projectServiceClient.getStatus(roleId, projectId);
      LOGGER.trace("Status is " + resp.get(WifKeys.STATUS_KEY));
      if (resp.get(WifKeys.STATUS_KEY).equals(WifKeys.PROCESS_STATE_FAILED)) {
        Assert.fail();
        break;
      }
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Setup finished...");

  }

  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" }, dependsOnMethods = {
      "uploadZip", "createProject" })
  public void downloadZip() {

    final boolean result = projectServiceClient.downloadZip(roleId, projectId);
    LOGGER.debug("result: " + result);

    Assert.assertTrue(result);
  }

  @Test(alwaysRun = true, groups = { "restclienttest", "rolerest",
      "projectrest" }, dependsOnMethods = { "downloadZip", "uploadZip",
      "createProject" }, expectedExceptions = HttpClientErrorException.class)
  public void deleteProject() throws WifInvalidInputException {
    projectServiceClient.deleteProject(roleId, projectId);
    Assert.assertNull(projectServiceClient.getProject(roleId, projectId));
  }

  /**
   * Gets the json file as a resource from classpath.
   * 
   * @param path
   *          the path
   * @return the json file
   */
  public File getJsonFile(final String path) {
    final URL urlOut = this.getClass().getClassLoader().getResource(path);
    String uri = urlOut.getFile();
    LOGGER.debug("url: " + uri);
    LOGGER.debug("os: " + OS);
    // TODO Find another way that is compatible with Windows, spaces in path
    // FIXME Guido: added Linux/Unix. I think this is actually always needed
    // because of using URL over URI
    if (OS.contains("win") || OS.contains("nux") || OS.contains("nix")) {
      uri = uri.replaceAll("%20", " ");
      LOGGER.info("url to path: " + uri);
    }
    return new File(uri);
  }
}
