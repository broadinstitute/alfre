package alfre.v0.spi;

@SuppressWarnings("WeakerAccess")
public class CloudPathException extends RuntimeException {

  public CloudPathException(final Exception cause) {
    super(cause);
  }

  public Exception getCauseException() {
    return (Exception) getCause();
  }
}
