/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import au.org.aurin.wif.exception.validate.WifInvalidInputException;

/**
 * <b>InvalidProjectIdException.java</b> : throws exceptions if the input id doesn't exist in the model
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidEntityIdException extends WifInvalidInputException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 24265676271144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public InvalidEntityIdException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   */
  public InvalidEntityIdException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param cause the cause
   */
  public InvalidEntityIdException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public InvalidEntityIdException(String message, Throwable cause) {
    super(message, cause);
  }

}
