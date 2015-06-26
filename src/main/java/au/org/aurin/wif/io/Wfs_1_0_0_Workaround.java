package au.org.aurin.wif.io;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.opengis.wfs.FeatureCollectionType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.wfs.WFSConfiguration;
import org.geotools.xml.Parser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Just a workaround until the Geotools guys decide to support gt-wfs module and
 * fix the issue with authenticated connections to WFS 1.0.0 services.
 * 
 * Here are the two tickets that will need to be fixed before we can get rid of
 * this workaround http://jira.codehaus.org/browse/GEOS-3967
 * http://jira.codehaus.org/browse/GEOS-2507
 * 
 */
public class Wfs_1_0_0_Workaround {
  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(Wfs_1_0_0_Workaround.class);
  private static final String QUERY_GET_FEATURE = "service=wfs&version=1.0.0&request=GetFeature";
  private static final String QUERY_DESCRIBE_FEATURE_TYPE = "service=wfs&version=1.0.0&request=DescribeFeatureType";
  private final String getCapabilitiesURL;
  private final String username;
  private final String password;
  private final String endpoint;

  public Wfs_1_0_0_Workaround(final Map<String, Object> dataStoreParams) {
    username = (String) dataStoreParams.get("WFSDataStoreFactory:USERNAME");
    password = (String) dataStoreParams.get("WFSDataStoreFactory:PASSWORD");
    getCapabilitiesURL = (String) dataStoreParams
        .get("WFSDataStoreFactory:GET_CAPABILITIES_URL");
    endpoint = getCapabilitiesURL.substring(0, getCapabilitiesURL.indexOf("?"));
  }

  public SimpleFeatureCollection getFeatures_1_0_0(final String typeName,
      final String[] attributesToReturn, final String cqlFilterString,
      final String srsName) throws DatasetAccessException {
    InputStream in = null;
    try {
      String result = runQuery(QUERY_DESCRIBE_FEATURE_TYPE + "&typename="
          + typeName);
      File tmp = File.createTempFile("describeFeatureType", ".xml");
      tmp.deleteOnExit();
      in = new ByteArrayInputStream(result.getBytes());
      copy(in, tmp);
      // let's get the targetNamespace for this dataset
      // this is a very slow way of finding the targetNamespace...
      // String datasetNS = result.replaceAll(".*targetNamespace=\"", "")
      // .replaceAll("\"\\W.*", "");
      final String find = "targetNamespace=\"";
      final int startIndex = result.indexOf(find);
      final String cutoff = result.substring(startIndex + find.length());
      final String datasetNS = cutoff.substring(0, cutoff.indexOf("\""));
      final String getFeaturesQuery = QUERY_GET_FEATURE
          + "&typename="
          + typeName
          + (attributesToReturn != null && attributesToReturn.length > 0 ? "&propertyName="
              + convertListToStringURL(attributesToReturn)
              : "")
          // + (cqlFilterInXML != null ? "&Filter="
          // + URLEncoder.encode(cqlFilterInXML, "UTF-8") : "");
          + (cqlFilterString != null ? "&cql_filter="
              + URLEncoder.encode(cqlFilterString, "UTF-8") : "")
          + (srsName != null ? "&srsName=" + srsName : "&srsName=EPSG:4283");
      result = runQuery(getFeaturesQuery);
      // cql filter in XML example
      // Note that in the BBOX example, there is a white space between the
      // two coordinates and there should only be a comma separating the
      // x,y coordinates (No whitespaces)!!!
      /*
       * "<Filter><Within><PropertyName>" + geomAttribute +
       * "</PropertyName><Box srsName=\"EPSG:4283\"><coordinates>" +
       * bbox.getMinX() + "," + bbox.getMinY() + " " + bbox.getMaxX() + "," +
       * bbox.getMaxY() + "</coordinates></Box></Within></Filter>"
       */
      in = new ByteArrayInputStream(result.getBytes());
      final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      final DocumentBuilder db = dbf.newDocumentBuilder();
      final Document doc = db.parse(in);
      // http://cite.opengeospatial.org/gmlsf
      // http://localhost:8080/geoserver/wfs?service=WFS&amp;version=1.1.0&amp;request=DescribeFeatureType&amp;typeName=sf:PrimitiveGeoFeature
      String schemaLocation = doc.getDocumentElement().getAttributeNS(
          "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
      final String absolutePath = DataUtilities.fileToURL(tmp).toExternalForm();
      // we'll replace the actual location of the schema in the getFeature
      // document to an actual xsd document that exists temporarily on the
      // server
      // TODO try and make this handle dynamic schemas
      schemaLocation = schemaLocation.replaceAll(datasetNS + " .*", datasetNS
          + " " + absolutePath);
      doc.getDocumentElement().setAttributeNS(
          "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation",
          schemaLocation);
      tmp = File.createTempFile("getFeature", ".xml");
      tmp.deleteOnExit();
      final Transformer tx = TransformerFactory.newInstance().newTransformer();
      tx.transform(new DOMSource(doc), new StreamResult(tmp));
      in = new FileInputStream(tmp);
      final WFSConfiguration configuration = new org.geotools.wfs.v1_0.WFSConfiguration();
      final Parser parser = new Parser(configuration);
      final Object parsingResult = parser.parse(in);
      in.close();
      if (parsingResult instanceof FeatureCollectionType) {
        final FeatureCollectionType fc = (FeatureCollectionType) parsingResult;
        // LOGGER.debug("FeatureCollectionType size: " + fc.toString()
        // + " " + fc.getNumberOfFeatures());
        final EList featureCollections = fc.getFeature();
        assert featureCollections.size() <= 1 : "feature collection size should only be zero or one";
        if (featureCollections.size() > 0) {
          final SimpleFeatureCollection featureCollection = (SimpleFeatureCollection) featureCollections
              .get(0);
          return featureCollection;
        } else {
          LOGGER
              .warn("returned featureCollection was empty ... returning a new empty featureCollection");
          return new DefaultFeatureCollection(null, null);
        }
      }
      throw new DatasetAccessException(
          "The returned response from WFS GetFeature query is not of type FeatureCollection",
          "An invalid response has been returned from the remote data provider. Please, try later or notify AURIN if the problem persists");
    } catch (final IOException e) {
      LOGGER
          .error(
              "An exception has occurred while trying to access the WFS datasource",
              e);
      throw new DatasetAccessException(
          "An exception has occurred while trying to access the WFS datasource",
          "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final ParserConfigurationException e) {
      LOGGER
          .error(
              "An exception has occurred while trying to access the WFS datasource",
              e);
      throw new DatasetAccessException(
          "An exception has occurred while trying to access the WFS datasource",
          "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final SAXException e) {
      LOGGER
          .error(
              "An exception has occurred while trying to access the WFS datasource",
              e);
      throw new DatasetAccessException(
          "An exception has occurred while trying to access the WFS datasource",
          "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final TransformerConfigurationException e) {
      LOGGER
          .error(
              "An exception has occurred while trying to access the WFS datasource",
              e);
      throw new DatasetAccessException(
          "An exception has occurred while trying to access the WFS datasource",
          "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } catch (final TransformerException e) {
      LOGGER
          .error(
              "An exception has occurred while trying to access the WFS datasource",
              e);
      throw new DatasetAccessException(
          "An exception has occurred while trying to access the WFS datasource",
          "We are unable to connect to the remote dataset. Please, try later or notify AURIN if the problem persists",
          e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (final IOException e) {
          LOGGER
              .error("something very bad while closing inputstream happened.");
        }
      }
    }
  }

  private String runQuery(final String query) throws DatasetAccessException {
    LOGGER.info("Running WFS Query: " + query);
    String result = "";
    if (username != null && password != null) {
      result = HttpRequestPoster.sendGetRequest(username, password, endpoint,
          query);
    } else {
      result = HttpRequestPoster.sendGetRequest(endpoint, query);
    }
    // TODO: we might need to preparse the result here if the server returns
    // an exception after the query is run.
    return result;
  }

  private void copy(final InputStream in, final File to) throws IOException {
    final Writer writer = new BufferedWriter(new FileWriter(to));
    final InputStreamReader reader = new InputStreamReader(in);
    int b = -1;
    while ((b = reader.read()) != -1) {
      writer.write(b);
    }
    writer.flush();
    writer.close();
  }

  private String convertListToStringURL(final String[] list) {
    final StringBuffer sBuf = new StringBuffer();
    for (int i = 0; i < list.length - 1; i++) {
      sBuf.append(list[i] + ",");
    }
    sBuf.append(list[list.length - 1]);
    return sBuf.toString();
  }
}
