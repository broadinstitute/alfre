package alfre.v0.cli;

import java.util.Arrays;
import java.util.Objects;

enum Verbosity {
  DEFAULT,
  QUIET,
  VERBOSE
}

@SuppressWarnings("WeakerAccess")
abstract class Options {

  private final Verbosity verbosity;
  private final boolean doModifications;

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

@SuppressWarnings("WeakerAccess")
class UsageOptions extends Options {

  private final String usage;

  public UsageOptions(final String usage) {
    this(Verbosity.DEFAULT, true, usage);
  }

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

@SuppressWarnings("WeakerAccess")
class InvalidOptions extends Options {

  private final String usage;

  public InvalidOptions(String usage) {
    super(Verbosity.DEFAULT, true);
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

@SuppressWarnings("WeakerAccess")
class LsOptions extends Options {

  private final boolean listAttributes;
  private final String[] paths;

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
    return paths;
  }

  @Override
  public String toString() {
    return String.format(
        "LsOptions(verbosity=%s, doModifications=%s, listAttributes=%s, paths=%s)",
        getVerbosity(), isDoModifications(), isListAttributes(), Arrays.toString(getPaths()));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LsOptions)) {
      return false;
    }
    final LsOptions lsOptions = (LsOptions) o;
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

@SuppressWarnings("WeakerAccess")
class CatOptions extends Options {

  private final String[] paths;

  public CatOptions(
      final Verbosity verbosity, final boolean doModifications, final String... paths) {
    super(verbosity, doModifications);
    Objects.requireNonNull(paths, "paths is null");
    this.paths = paths;
  }

  public String[] getPaths() {
    return paths;
  }

  @Override
  public String toString() {
    return String.format(
        "CatOptions(verbosity=%s, doModifications=%s, paths=%s)",
        getVerbosity(), isDoModifications(), Arrays.toString(getPaths()));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CatOptions)) {
      return false;
    }
    final CatOptions catOptions = (CatOptions) o;
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

@SuppressWarnings("WeakerAccess")
class CpOptions extends Options {

  private final String sourcePath;
  private final String targetPath;

  public CpOptions(
      Verbosity verbosity, boolean doModifications, String sourcePath, String targetPath) {
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
    CpOptions cpOptions = (CpOptions) o;
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

@SuppressWarnings("WeakerAccess")
class RmOptions extends Options {

  private final String[] paths;

  public RmOptions(
      final Verbosity verbosity, final boolean doModifications, final String... paths) {
    super(verbosity, doModifications);
    Objects.requireNonNull(paths, "paths is null");
    this.paths = paths;
  }

  public String[] getPaths() {
    return paths;
  }

  @Override
  public String toString() {
    return String.format(
        "RmOptions(verbosity=%s, doModifications=%s, paths=%s)",
        getVerbosity(), isDoModifications(), Arrays.toString(getPaths()));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RmOptions)) {
      return false;
    }
    final RmOptions rmOptions = (RmOptions) o;
    return isDoModifications() == rmOptions.isDoModifications()
        && getVerbosity() == rmOptions.getVerbosity()
        && Arrays.equals(getPaths(), rmOptions.getPaths());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getVerbosity(), isDoModifications());
    result = 31 * result + Arrays.hashCode(getPaths());
    return result;
  }
}
