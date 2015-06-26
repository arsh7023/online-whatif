/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>WifInvalidConfigException.java</b> : throws exceptions if the internal
 * configuration is invalid for successful Analysis
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = " could not update the spatial database of the project, geoserver doesn't recognise the SRS")
public class GeoServerConfigException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new wif invalid config exception.
   */
  public GeoServerConfigException() {
  }

  /**
   * Instantiates a new wif invalid config exception.
   * 
   * @param message
   *          the message
   */
  public GeoServerConfigException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid config exception.
   * 
   * @param cause
   *          the cause
   */
  public GeoServerConfigException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid config exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public GeoServerConfigException(String message, Throwable cause) {
    super(message, cause);
  }

}
