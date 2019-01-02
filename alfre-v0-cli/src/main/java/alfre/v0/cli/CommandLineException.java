package alfre.v0.cli;

@SuppressWarnings("WeakerAccess")
public class CommandLineException extends RuntimeException {

  private static final long serialVersionUID = -7517740891137114035L;

  public CommandLineException(final Exception cause) {
    super(cause);
  }
}
