package alfre.v0.cli;

@SuppressWarnings("WeakerAccess")
public class CommandLineException extends RuntimeException {

  public CommandLineException(final Exception cause) {
    super(cause);
  }

  public Exception getCauseException() {
    return (Exception) getCause();
  }
}
