/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class IncompleteSuitabilityLUConfigException.
 */
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, 
reason = "SuitabilityLUs do not have associated LUs configured")
public class IncompleteSuitabilityLUConfigException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete suitability lu config exception.
   */
  public IncompleteSuitabilityLUConfigException() {
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   *
   * @param message the message
   */
  public IncompleteSuitabilityLUConfigException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   *
   * @param cause the cause
   */
  public IncompleteSuitabilityLUConfigException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public IncompleteSuitabilityLUConfigException(String message, Throwable cause) {
    super(message, cause);
  }

}
