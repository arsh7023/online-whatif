/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class ProjectNotReadyException.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "ProjectNotReady for uploadUAZ")
public class ProjectNotReadyException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new project not ready exception.
   */
  public ProjectNotReadyException() {
  }

  /**
   * Instantiates a new project not ready exception.
   * 
   * @param message
   *          the message
   */
  public ProjectNotReadyException(String message) {
    super(message);
  }

  /**
   * Instantiates a new project not ready exception.
   * 
   * @param cause
   *          the cause
   */
  public ProjectNotReadyException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new project not ready exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public ProjectNotReadyException(String message, Throwable cause) {
    super(message, cause);
  }

}
