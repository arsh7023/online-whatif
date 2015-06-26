/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class InvalidFFNameException.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = " INVALID feature field name/column,  it is null or  it has really been used ")
public class InvalidFFNameException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new invalid ff name exception.
   */
  public InvalidFFNameException() {
  }

  /**
   * Instantiates a new invalid ff name exception.
   * 
   * @param message
   *          the message
   */
  public InvalidFFNameException(String message) {
    super(message);
  }

  /**
   * Instantiates a new invalid ff name exception.
   * 
   * @param cause
   *          the cause
   */
  public InvalidFFNameException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new invalid ff name exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public InvalidFFNameException(String message, Throwable cause) {
    super(message, cause);
  }

}
