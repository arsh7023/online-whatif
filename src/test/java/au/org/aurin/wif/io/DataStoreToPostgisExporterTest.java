package au.org.aurin.wif.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.org.aurin.wif.exception.io.DataStoreUnavailableException;

@ContextConfiguration(locations = { "/test-integration-context.xml" })
public class DataStoreToPostgisExporterTest extends
    AbstractTestNGSpringContextTests {

  private DataStoreToPostgisExporter exporter;

  @Autowired
  private DataStoreClient dataStoreClient;

  @BeforeClass
  public void setup() {
    exporter = new DataStoreToPostgisExporter();
    exporter.setDataStoreClient(dataStoreClient);
  }

  @Test(enabled = false, groups = { "setup", "integration" })
  public void convertGeoJSONToFeatureCollection()
      throws DataStoreUnavailableException {
    final String uri = "https://dev-api.aurin.org.au/datastore/files/593db61e14f16b99f26a14a2f246e4f1148095552d9fcee73645002c0c70af9b";
    Assert.assertEquals(exporter
        .convertGeoJSONToFeatureCollection("aurin", uri).size(), 18762);
  }
}
