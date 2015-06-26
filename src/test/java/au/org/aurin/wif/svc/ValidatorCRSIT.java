/**
 *
 * marcosnr
 * 28/02/2012
 */
package au.org.aurin.wif.svc;

import java.io.File;

import javax.annotation.Resource;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import au.org.aurin.wif.impl.ValidatorCRS;
import au.org.aurin.wif.io.FileToPostgisExporter;
import au.org.aurin.wif.io.WifFileUtils;

/**
 * The Class ProjectServiceIT.
 */
@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class ValidatorCRSIT extends AbstractTestNGSpringContextTests {

  /** The file utils. */
  @Autowired
  private WifFileUtils fileUtils;

  @Autowired
  private FileToPostgisExporter fileToPostgisExporter;

  /** The Constant LOGGER. */
  private static final Logger LOGGER = LoggerFactory
      .getLogger(ValidatorCRSIT.class);
  @Resource
  private ValidatorCRS validatorCRS;

  /**
   * Creates the project test.
   * 
   * @throws Exception
   *           the exception
   */
  @Test(enabled = true, groups = { "setup", "service" })
  public void validateTest() throws Exception {
    // String path = "shapefiles/Perth_small_test.shp";
    // File input = fileUtils.getJsonFile(path);
    // String path =
    // "C:\\Users\\Marcos Nino-Ruiz\\Documents\\WhatIF\\nw\\Perth_Union_40000\\Perth_Union_40000.shp";
    String path = "C:\\Users\\Marcos Nino-Ruiz\\Documents\\WhatIF\\nw\\Perth_Union_200000\\Perth_Union_200000.shp";
    File input = new File(path);

    SimpleFeatureCollection featureCollection = fileToPostgisExporter
        .zipFileToPostGIS(input);
    CoordinateReferenceSystem crs = featureCollection.getSchema()
        .getCoordinateReferenceSystem();
    // crs = validatorCRS.validate(crs, "");
    crs = validatorCRS.validateSimple(crs, "");

  }
}
