/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>WifInvalidInputException.java</b> : throws exceptions if the input doesn't
 * meet the WIF criteria to perform a successful Analysis
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = " INVALID label,  it is null or  it has really been used ")
public class InvalidLabelException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public InvalidLabelException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   * 
   * @param message
   *          the message
   */
  public InvalidLabelException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   * 
   * @param cause
   *          the cause
   */
  public InvalidLabelException(Throwable cause) {
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
  public InvalidLabelException(String message, Throwable cause) {
    super(message, cause);
  }

}
