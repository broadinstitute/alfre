package alfre.v0.spi;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class CloudRetryException extends Exception {

  public CloudRetryException(final Exception cause) {
    super(cause);
    Objects.requireNonNull(cause, "cause was null");
  }

  public Exception getCauseException() {
    return (Exception) getCause();
  }

  /** Returns the cause as an IOException, wrapping it if necessary. */
  public IOException getCauseIoException() {
    final Throwable throwable = getCause();
    if (throwable instanceof IOException) {
      throw new DirectoryIteratorException((IOException) throwable);
    } else {
      throw new DirectoryIteratorException(new IOException(throwable));
    }
  }
}
