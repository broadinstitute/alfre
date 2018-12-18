package alfre.v0.cli;

public class Main {

  public static void main(final String[] args) {
    final Options options = MainCommand.parseOptions(args);
    new MainRunner().run(options);
  }
}

class MainRunner implements Runner {

  @Override
  public void runUsage(final UsageOptions usageOptions) {
    System.out.print(usageOptions.getUsage());
  }

  @Override
  public void runInvalid(final InvalidOptions invalidOptions) {
    System.err.print(invalidOptions.getUsage());
    System.exit(1);
  }

  @Override
  public void runLs(final LsOptions lsOptions) {
    System.out.println(lsOptions);
  }

  @Override
  public void runCat(final CatOptions catOptions) {
    System.out.println(catOptions);
  }

  @Override
  public void runCp(final CpOptions cpOptions) {
    System.out.println(cpOptions);
  }

  @Override
  public void runRm(final RmOptions rmOptions) {
    System.out.println(rmOptions);
  }

  private static void handleException(final Options options, final Exception exception) {
    if (options.getVerbosity() != Verbosity.QUIET) {
      System.err.println("Error: " + exception);
      if (options.getVerbosity() == Verbosity.VERBOSE) {
        exception.printStackTrace();
      }
    }
  }
}
