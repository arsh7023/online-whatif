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
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "ManualDemandScenario does not have required information")
public class IncompleteDemandOutcomeException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete demand scenario exception.
   */
  public IncompleteDemandOutcomeException() {
  }

  /**
   * Instantiates a new incomplete demand scenario exception.
   * 
   * @param message
   *          the message
   */
  public IncompleteDemandOutcomeException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete demand scenario exception.
   * 
   * @param cause
   *          the cause
   */
  public IncompleteDemandOutcomeException(Throwable cause) {
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
  public IncompleteDemandOutcomeException(String message, Throwable cause) {
    super(message, cause);
  }
}
