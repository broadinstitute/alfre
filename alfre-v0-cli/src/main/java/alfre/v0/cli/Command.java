package alfre.v0.cli;

import java.util.concurrent.Callable;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
public abstract class Command<T extends Options> implements Callable<T> {

  @ParentCommand private MainCommand mainCommand;

  @Option(names = "-n", description = "Dry run, do not perform any modifications")
  private boolean skipModifications = false;

  @Option(names = "-v", description = "Produce verbose output")
  private boolean verbose = false;

  @Option(names = "-q", description = "Produce quieter output")
  private boolean quiet = false;

  protected boolean isSkipModifications() {
    return skipModifications || (mainCommand != null && mainCommand.isSkipModifications());
  }

  protected boolean isVerbose() {
    return verbose || (mainCommand != null && mainCommand.isVerbose());
  }

  protected boolean isQuiet() {
    return quiet || (mainCommand != null && mainCommand.isQuiet());
  }

  protected Verbosity getVerbosity() {
    if (isVerbose()) {
      return Verbosity.VERBOSE;
    } else if (isQuiet()) {
      return Verbosity.QUIET;
    } else {
      return Verbosity.DEFAULT;
    }
  }

  protected boolean isDoModifications() {
    return !isSkipModifications();
  }
}
