package alfre.v0.cli;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public abstract class Options {

  private final Verbosity verbosity;
  private final boolean doModifications;

  /** Create a new Options. */
  @SuppressWarnings("WeakerAccess")
  public Options(final Verbosity verbosity, final boolean doModifications) {
    Objects.requireNonNull(verbosity, "verbosity is null");
    this.verbosity = verbosity;
    this.doModifications = doModifications;
  }

  public Verbosity getVerbosity() {
    return verbosity;
  }

  public boolean isDoModifications() {
    return doModifications;
  }
}
