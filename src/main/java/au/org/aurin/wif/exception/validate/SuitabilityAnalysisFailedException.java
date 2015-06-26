/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>WifAnalysisFailedException.java</b> : throws exceptions if the analysis couldn't be performed
 * 
 * @author <a href="mailto:marcosnr@unimelb.edu.au"> Marcos Nino-Ruiz
 *         marcosnr@unimelb.edu.au</a> - 2012
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "analysis failed, possibly because of configuration issues")
public class SuitabilityAnalysisFailedException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1116820426271144835L;

  /**
   * Instantiates a new wif invalid input exception.
   */
  public SuitabilityAnalysisFailedException() {
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   */
  public SuitabilityAnalysisFailedException(String message) {
    super(message);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param cause the cause
   */
  public SuitabilityAnalysisFailedException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new wif invalid input exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public SuitabilityAnalysisFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
