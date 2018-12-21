package alfre.v0.cli;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class InvalidOptions extends Options {

  private final String usage;

  /** Create a new InvalidOptions. */
  public InvalidOptions(final String usage) {
    super(Verbosity.DEFAULT, true);
    Objects.requireNonNull(usage, "usage is null");
    this.usage = usage;
  }

  public String getUsage() {
    return usage;
  }

  @Override
  public String toString() {
    return String.format(
        "InvalidOptions(verbosity=%s, doModifications=%s, usage='%s')",
        getVerbosity(), isDoModifications(), getUsage());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof InvalidOptions)) {
      return false;
    }
    final InvalidOptions invalidOptions = (InvalidOptions) o;
    return isDoModifications() == invalidOptions.isDoModifications()
        && getVerbosity() == invalidOptions.getVerbosity();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVerbosity(), isDoModifications());
  }
}
