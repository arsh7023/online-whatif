/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.io;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>PostGISFailedException.java</b> : throws exceptions if postGIS couldn't be
 * accessed.
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "PostGIS access failed, possibly because of configuration issues")
public class DatabaseFailedException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public DatabaseFailedException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   * 
   * @param message
   *          the message
   */
  public DatabaseFailedException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   * 
   * @param cause
   *          the cause
   */
  public DatabaseFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid input exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public DatabaseFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
