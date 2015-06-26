/**
 *

 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.io;

import java.io.File;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The Class GeodataFinderIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class FileToPostgisExporterIT extends AbstractTestNGSpringContextTests {

  /** The file to postgis exporter. */
  @Autowired
  private FileToPostgisExporter fileToPostgisExporter;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(FileToPostgisExporterIT.class);

  @Test(enabled = false, groups = { "setup", "service" })
  public void readshapefile() throws Exception {
    String path = "C:\\Users\\MARCOS~1\\AppData\\Local\\Temp\\1377149686251-0\\uazdemomin.shp";
    File shpFile = new File(path);

    String response = path;
    String filePath = response.substring(13, response.indexOf(","));
    LOGGER.debug("filePath " + filePath);

    SimpleFeatureCollection featureCollection = fileToPostgisExporter
        .zipFileToPostGIS(shpFile);
    Assert.assertNotNull(featureCollection);
  }
}
