package alfre.v0.cli;

import java.util.Objects;

public interface Runner {

  default void run(Options options) {
    Objects.requireNonNull(options, "options is null");
    if (options instanceof UsageOptions) {
      runUsage((UsageOptions) options);
    } else if (options instanceof InvalidOptions) {
      runInvalid((InvalidOptions) options);
    } else if (options instanceof LsOptions) {
      runLs((LsOptions) options);
    } else if (options instanceof CatOptions) {
      runCat((CatOptions) options);
    } else if (options instanceof CpOptions) {
      runCp((CpOptions) options);
    } else if (options instanceof RmOptions) {
      runRm((RmOptions) options);
    } else {
      throw new IllegalArgumentException("unknown options: " + options);
    }
  }

  void runUsage(UsageOptions usageOptions);

  void runInvalid(InvalidOptions invalidOptions);

  void runLs(LsOptions lsOptions);

  void runCat(CatOptions catOptions);

  void runCp(CpOptions cpOptions);

  void runRm(RmOptions rmOptions);
}
