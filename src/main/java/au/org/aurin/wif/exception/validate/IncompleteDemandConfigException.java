/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class IncompleteDemandConfigConfigException.
 */
@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "DemandConfig does not have required information, check that all the UAZ column names are valid or the configuration is complete!")
public class IncompleteDemandConfigException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new incomplete suitability lu config exception.
   */
  public IncompleteDemandConfigException() {
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   * 
   * @param message
   *          the message
   */
  public IncompleteDemandConfigException(String message) {
    super(message);
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   * 
   * @param cause
   *          the cause
   */
  public IncompleteDemandConfigException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new incomplete suitability lu config exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public IncompleteDemandConfigException(String message, Throwable cause) {
    super(message, cause);
  }

}
