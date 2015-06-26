package au.org.aurin.wif.exception.io;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Data Provider job request is not valid")
public class WifIOException extends Exception {

  public WifIOException(String msg) {
    super(msg);
  }

  public WifIOException(Throwable cause) {
    super(cause);
  }

  public WifIOException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
