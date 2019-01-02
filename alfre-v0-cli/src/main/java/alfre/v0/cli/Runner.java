package alfre.v0.cli;

import java.util.Objects;

public interface Runner {

  /** Runs with the provided cli options. */
  default boolean run(final Options options) {
    Objects.requireNonNull(options, "options is null");
    if (options instanceof UsageOptions) {
      return runUsage((UsageOptions) options);
    } else if (options instanceof InvalidOptions) {
      return runInvalid((InvalidOptions) options);
    } else if (options instanceof LsOptions) {
      return runLs((LsOptions) options);
    } else if (options instanceof CatOptions) {
      return runCat((CatOptions) options);
    } else if (options instanceof CpOptions) {
      return runCp((CpOptions) options);
    } else if (options instanceof RmOptions) {
      return runRm((RmOptions) options);
    } else {
      throw new IllegalArgumentException("unknown options: " + options);
    }
  }

  boolean runUsage(UsageOptions usageOptions);

  boolean runInvalid(InvalidOptions invalidOptions);

  boolean runLs(LsOptions lsOptions);

  boolean runCat(CatOptions catOptions);

  boolean runCp(CpOptions cpOptions);

  boolean runRm(RmOptions rmOptions);
}
