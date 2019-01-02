package alfre.v0.cli;

public class Main {

  /** Runs the Alfre CLI. */
  @SuppressWarnings("PMD.DoNotCallSystemExit")
  public static void main(final String[] args) {
    final Options options = MainCommand.parseOptions(args);
    try {
      if (new MainRunner().run(options)) {
        System.exit(1);
      }
    } catch (final MainShutdownException mainShutdownException) {
      System.exit(1);
    }
  }
}
