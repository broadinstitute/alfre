package alfre.v0.cli;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CatOptions extends Options {

  private final String[] paths;

  /** Create a new CatOptions. */
  public CatOptions(
      final Verbosity verbosity, final boolean doModifications, final String... paths) {
    super(verbosity, doModifications);
    Objects.requireNonNull(paths, "paths is null");
    this.paths = paths;
  }

  public String[] getPaths() {
    return Arrays.copyOf(paths, paths.length);
  }

  @Override
  public String toString() {
    return String.format(
        "CatOptions(verbosity=%s, doModifications=%s, paths=%s)",
        getVerbosity(), isDoModifications(), Arrays.toString(getPaths()));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof CatOptions)) {
      return false;
    }
    final CatOptions catOptions = (CatOptions) other;
    return isDoModifications() == catOptions.isDoModifications()
        && getVerbosity() == catOptions.getVerbosity()
        && Arrays.equals(getPaths(), catOptions.getPaths());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getVerbosity(), isDoModifications());
    result = 31 * result + Arrays.hashCode(getPaths());
    return result;
  }
}
