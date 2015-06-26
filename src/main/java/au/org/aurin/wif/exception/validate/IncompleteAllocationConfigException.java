/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class IncompleteAllocationScenarioException.
 */
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "AllocationConfigSetup does not have required information")
public class IncompleteAllocationConfigException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete allocation scenario exception.
   */
  public IncompleteAllocationConfigException() {
  }

  /**
   * Instantiates a new incomplete allocation scenario exception.
   * 
   * @param message
   *          the message
   */
  public IncompleteAllocationConfigException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete allocation scenario exception.
   * 
   * @param cause
   *          the cause
   */
  public IncompleteAllocationConfigException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new incomplete allocation scenario exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public IncompleteAllocationConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
