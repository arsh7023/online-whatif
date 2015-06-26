package au.org.aurin.wif.io;

public class InvalidDataQueryParamsException extends Exception {

  public InvalidDataQueryParamsException() {
    super();
  }

  public InvalidDataQueryParamsException(final String message) {
    super(message);
  }

  public InvalidDataQueryParamsException(final String message,
      final Throwable cause) {
    super(message, cause);
  }

}
