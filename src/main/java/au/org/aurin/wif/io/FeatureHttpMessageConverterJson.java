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

public class FeatureHttpMessageConverterJson extends
    AbstractHttpMessageConverter<Object> {

  /** The logger. */
  private static final Log LOGGER = LogFactory
      .getLog(FeatureHttpMessageConverter.class);

  public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  private FeatureJSON featureJSON = new FeatureJSON();

  public FeatureHttpMessageConverterJson() {
    super(new MediaType("application", "json", DEFAULT_CHARSET));
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
    featureJSON.setEncodeNullValues(true);
    featureJSON.setEncodeFeatureCollectionBounds(false);
    featureJSON.setEncodeFeatureCollectionCRS(false);
    featureJSON.writeFeatureCollection((FeatureCollection) o,
        new OutputStreamWriter(outputMessage.getBody()));
  }

}
