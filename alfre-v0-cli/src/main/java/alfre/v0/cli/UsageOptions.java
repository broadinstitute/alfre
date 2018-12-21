package alfre.v0.cli;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class UsageOptions extends Options {

  private final String usage;

  public UsageOptions(final String usage) {
    this(Verbosity.DEFAULT, true, usage);
  }

  /** Create a new UsageOptions. */
  public UsageOptions(
      final Verbosity verbosity, final boolean doModifications, final String usage) {
    super(verbosity, doModifications);
    Objects.requireNonNull(usage, "usage is null");
    this.usage = usage;
  }

  public String getUsage() {
    return usage;
  }

  @Override
  public String toString() {
    return String.format(
        "UsageOptions(verbosity=%s, doModifications=%s, usage='%s')",
        getVerbosity(), isDoModifications(), getUsage());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UsageOptions)) {
      return false;
    }
    final UsageOptions usageOptions = (UsageOptions) o;
    return isDoModifications() == usageOptions.isDoModifications()
        && getVerbosity() == usageOptions.getVerbosity();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVerbosity(), isDoModifications());
  }
}
