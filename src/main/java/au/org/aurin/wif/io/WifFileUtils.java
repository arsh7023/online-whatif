package au.org.aurin.wif.io;

import java.io.File;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.org.aurin.wif.svc.WifKeys;

/**
 * Provide filesystem utility functions.
 */
@Component
public class WifFileUtils {

  private static final String OS = System.getProperty("os.name").toLowerCase();

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(WifFileUtils.class);

  /**
   * Inits the.
   */
  @PostConstruct
  public void init() {
    LOGGER.trace("Initializing version: " + WifKeys.WIF_KEY_VERSION);
  }

  /**
   * Cleanup.
   */
  @PreDestroy
  public void cleanup() {
    LOGGER.trace("Service successfully cleared! ");
  }

  /**
   * Gets the json file as a resource from classpath.
   * 
   * @param path
   *          the path
   * @return the json file
   */
  public File getJsonFile(String path) {
    URL urlOut = this.getClass().getClassLoader().getResource(path);
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
