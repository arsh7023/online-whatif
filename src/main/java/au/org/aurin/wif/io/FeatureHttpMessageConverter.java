package au.org.aurin.wif.io;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Implementation of
 * {@link org.springframework.http.converter.HttpMessageConverter
 * HttpMessageConverter} that can read and write GeoJSON using <a
 * href="http://geotools.org/">Geotools'</a> {@link FeatureJSON}.
 * 
 * <p>
 * By default, this converter supports {@code application/geo+json}. This can be
 * overridden by setting the {@link #setSupportedMediaTypes(List)
 * supportedMediaTypes} property.
 * 
 * @author Gerson Galang
 */
public class FeatureHttpMessageConverter extends
    AbstractHttpMessageConverter<Object> {
  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(FeatureHttpMessageConverter.class);
  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private FeatureJSON featureJSON = new FeatureJSON();

  public FeatureHttpMessageConverter() {
    super(new MediaType("application", "geo+json", DEFAULT_CHARSET));
  }

  public void setFeatureJSON(final FeatureJSON featureJSON) {
    this.featureJSON = featureJSON;
  }

  @Override
  protected Object readInternal(final Class<? extends Object> arg0,
      final HttpInputMessage inputMessage) throws IOException,
      HttpMessageNotReadableException {
    LOGGER.debug("Entering FeatureHttpMessageConverter.readInternal().");
    final MediaType contentType = inputMessage.getHeaders().getContentType();
    LOGGER.debug("content type of the message received: " + contentType);
    return featureJSON.readFeatureCollection(inputMessage.getBody());
  }

  @Override
  protected boolean supports(final Class<?> arg0) {
    LOGGER.debug("json.FeatureHttpMessageConverter supports(" + arg0.getName()
        + ")?");
    if (FeatureCollection.class.isAssignableFrom(arg0)) {
      return true;
    }
    return false;
  }

  @Override
  protected void writeInternal(final Object o,
      final HttpOutputMessage outputMessage) throws IOException,
      HttpMessageNotWritableException {
    LOGGER.debug("Entering FeatureHttpMessageConverter.writeInternal().");
    LOGGER.debug("Content type of the outputMessage "
        + outputMessage.getHeaders().getContentType());
    featureJSON.setEncodeFeatureCollectionBounds(true);
    featureJSON.setEncodeNullValues(true);
    // TODO we'll need to enable this once geotools allow us to patch
    // GML2ParsingUtils class and set SRID on each of the features created
    // for the call to encodeCRS not to throw an exception, we'll need to check
    // for the existence of CRS info in the feature collection before we set
    // this flag...
    if (((FeatureCollection) o).getBounds().getCoordinateReferenceSystem() != null) {
      featureJSON.setEncodeFeatureCollectionCRS(true);
    }
    featureJSON.writeFeatureCollection((FeatureCollection) o,
        new OutputStreamWriter(outputMessage.getBody()));
  }
}
