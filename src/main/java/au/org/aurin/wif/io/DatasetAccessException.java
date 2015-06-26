package au.org.aurin.wif.io;

public class DatasetAccessException extends Exception {

  private String userFriendlyMessage;

  public DatasetAccessException() {
    super();
  }

  public DatasetAccessException(final String message) {
    super(message);
  }

  public DatasetAccessException(final String message,
      final String userFriendlyMessage) {
    super(message);
    this.userFriendlyMessage = userFriendlyMessage;
  }

  public DatasetAccessException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DatasetAccessException(final String message,
      final String userFriendlyMessage, final Throwable cause) {
    super(message, cause);
    this.userFriendlyMessage = userFriendlyMessage;
  }

  public String getUserFriendlyMessage() {
    return userFriendlyMessage;
  }

}
