/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.io;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * The Class DataStoreCreationException.
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, 
reason = "Spatial DataStore creation failed!")
public class DataStoreCreationException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public DataStoreCreationException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   */
  public DataStoreCreationException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param cause the cause
   */
  public DataStoreCreationException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public DataStoreCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
