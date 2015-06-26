/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>ProjectSetupFailedException.java</b> : throws exceptions if the project setup couldn't be performed
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, 
reason = "projects asynchronous setup failed, possibly because of configuration issues")
public class ProjectSetupFailedException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public ProjectSetupFailedException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   */
  public ProjectSetupFailedException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param cause the cause
   */
  public ProjectSetupFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public ProjectSetupFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
