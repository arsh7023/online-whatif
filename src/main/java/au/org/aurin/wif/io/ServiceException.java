package au.org.aurin.wif.io;

/**
 * The ServiceException gets thrown when an exception gets thrown while the
 * client accesses the service.
 * 
 * @author Gerson Galang
 */
public class ServiceException extends RuntimeException {
  private static final long serialVersionUID = -7130334633244671398L;

  public ServiceException() {
    super();
  }

  public ServiceException(final String message) {
    super(message);
  }

  public ServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
