/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class ParsingException.
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Mapping incorrect, check JSON structure")
public class ParsingException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new parsing exception.
   */
  public ParsingException() {
  }

  /**
   * Instantiates a new parsing exception.
   *
   * @param message the message
   */
  public ParsingException(String message) {
    super(message);
  }

  /**
   * Instantiates a new parsing exception.
   *
   * @param cause the cause
   */
  public ParsingException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new parsing exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public ParsingException(String message, Throwable cause) {
    super(message, cause);
  }

}
