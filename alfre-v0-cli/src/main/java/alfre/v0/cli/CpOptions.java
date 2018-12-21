package alfre.v0.cli;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CpOptions extends Options {

  private final String sourcePath;
  private final String targetPath;

  /** Create a new CpOptions. */
  public CpOptions(
      final Verbosity verbosity,
      final boolean doModifications,
      final String sourcePath,
      final String targetPath) {
    super(verbosity, doModifications);
    Objects.requireNonNull(sourcePath, "sourcePath is null");
    Objects.requireNonNull(targetPath, "targetPath is null");
    this.sourcePath = sourcePath;
    this.targetPath = targetPath;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public String getTargetPath() {
    return targetPath;
  }

  @Override
  public String toString() {
    return String.format(
        "CpOptions(verbosity=%s, doModifications=%s, sourcePath='%s', targetPath='%s')",
        getVerbosity(), isDoModifications(), getSourcePath(), getTargetPath());
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CpOptions)) {
      return false;
    }
    final CpOptions cpOptions = (CpOptions) o;
    return isDoModifications() == cpOptions.isDoModifications()
        && getVerbosity() == cpOptions.getVerbosity()
        && Objects.equals(getSourcePath(), cpOptions.getSourcePath())
        && Objects.equals(getTargetPath(), cpOptions.getTargetPath());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVerbosity(), isDoModifications(), getSourcePath(), getTargetPath());
  }
}
