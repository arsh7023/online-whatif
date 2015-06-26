/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class IncompleteAllocationConfigsException.
 */
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "AllocationConfigs does not have required information, check that all the UAZ column names are valid or the configuration is complete!")
public class IncompleteAllocationConfigsException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete Allocation config exception.
   */
  public IncompleteAllocationConfigsException() {
  }

  /**
   * Instantiates a new incomplete Allocation config exception.
   * 
   * @param message
   *          the message
   */
  public IncompleteAllocationConfigsException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete Allocation config exception.
   * 
   * @param cause
   *          the cause
   */
  public IncompleteAllocationConfigsException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new incomplete Allocation config exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public IncompleteAllocationConfigsException(String message, Throwable cause) {
    super(message, cause);
  }

}
