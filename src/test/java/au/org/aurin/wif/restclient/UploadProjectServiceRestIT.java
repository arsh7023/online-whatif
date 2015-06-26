/*
 * 
 */
package au.org.aurin.wif.restclient;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import au.org.aurin.wif.svc.WifKeys;

/**
 * The Class ProjectServiceRestIT.
 */
@ContextConfiguration(locations = { "/test-integration-client-context.xml" })
public class UploadProjectServiceRestIT extends
    AbstractTestNGSpringContextTests {

  /** The project service client. */
  @Autowired
  private ProjectServiceClient projectServiceClient;

  /** The role id. */
  private String roleId = WifKeys.TEST_ROLE_ID;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(UploadProjectServiceRestIT.class);

  /**
   * Upload uaz test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "restclienttest", "rolerest", "projectrest" })
  public void uploadUAZTest() throws Exception {

    LOGGER.debug("uploadUAZTest: ");

    projectServiceClient.uploadUAZ(roleId, WifKeys.TEST_PROJECT_ID);

    HashMap<String, String> resp = new HashMap<String, String>();
    do {
      LOGGER.debug("Waiting for upload to complete...");
      Thread.sleep(3000);
      resp = projectServiceClient.getUploadStatus(roleId,
          WifKeys.TEST_PROJECT_ID);
      LOGGER.debug("Status is " + resp.get(WifKeys.STATUS_KEY));
    } while (!resp.get(WifKeys.STATUS_KEY)
        .equals(WifKeys.PROCESS_STATE_SUCCESS));
    LOGGER.debug("Upload complete...");
  }
}
