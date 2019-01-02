package alfre.v0.cli;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class LsOptions extends Options {

  private final boolean listAttributes;
  private final String[] paths;

  /** Create a new LsOptions. */
  public LsOptions(
      final Verbosity verbosity,
      final boolean doModifications,
      final boolean listAttributes,
      final String... paths) {
    super(verbosity, doModifications);
    Objects.requireNonNull(paths, "paths is null");
    this.listAttributes = listAttributes;
    this.paths = paths;
  }

  public boolean isListAttributes() {
    return listAttributes;
  }

  public String[] getPaths() {
    return Arrays.copyOf(paths, paths.length);
  }

  @Override
  public String toString() {
    return String.format(
        "LsOptions(verbosity=%s, doModifications=%s, listAttributes=%s, paths=%s)",
        getVerbosity(), isDoModifications(), isListAttributes(), Arrays.toString(getPaths()));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof LsOptions)) {
      return false;
    }
    final LsOptions lsOptions = (LsOptions) other;
    return isDoModifications() == lsOptions.isDoModifications()
        && getVerbosity() == lsOptions.getVerbosity()
        && isListAttributes() == lsOptions.isListAttributes()
        && Arrays.equals(getPaths(), lsOptions.getPaths());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getVerbosity(), isDoModifications(), isListAttributes());
    result = 31 * result + Arrays.hashCode(getPaths());
    return result;
  }
}
