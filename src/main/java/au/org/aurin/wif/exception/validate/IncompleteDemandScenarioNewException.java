/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class IncompleteDemandScenarioException.
 */
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "DemandScenarioNew does not have required information")
public class IncompleteDemandScenarioNewException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete demand scenario exception.
   */
  public IncompleteDemandScenarioNewException() {
  }

  /**
   * Instantiates a new incomplete demand scenario exception.
   * 
   * @param message
   *          the message
   */
  public IncompleteDemandScenarioNewException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete demand scenario exception.
   * 
   * @param cause
   *          the cause
   */
  public IncompleteDemandScenarioNewException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new incomplete demand scenario exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public IncompleteDemandScenarioNewException(String message, Throwable cause) {
    super(message, cause);
  }
}
