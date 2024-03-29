/**
 *
 * marcosnr
 * 19/03/2012
 */
package au.org.aurin.wif.exception.io;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The Class ShapeFile2PostGISCreationException if the PostGIS to shape file
 * operation failed
 */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "PostGIS to shape file operation failed")
public class ShapeFile2PostGISCreationException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1124576769564144835L;

  /**
   * Instantiates a new shape file2 post gis creation exception.
   */
  public ShapeFile2PostGISCreationException() {
  }

  /**
   * Instantiates a new shape file2 post gis creation exception.
   * 
   * @param message
   *          the message
   */
  public ShapeFile2PostGISCreationException(String message) {
    super(message);
  }

  /**
   * Instantiates a new shape file2 post gis creation exception.
   * 
   * @param cause
   *          the cause
   */
  public ShapeFile2PostGISCreationException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new shape file2 post gis creation exception.
   * 
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public ShapeFile2PostGISCreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
