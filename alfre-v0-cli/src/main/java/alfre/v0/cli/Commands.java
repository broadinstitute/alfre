package alfre.v0.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.RunLast;

@SuppressWarnings({"FieldCanBeLocal", "unused", "WeakerAccess"})
abstract class AbstractCommand<T extends Options> implements Callable<T> {

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

@Command(
    name = "alfre-cli",
    description = "Alfre Command Line Interface",
    subcommands = {
      HelpCommand.class,
      CatCommand.class,
      LsCommand.class,
      RmCommand.class,
      CpCommand.class
    })
@SuppressWarnings({"unused", "WeakerAccess"})
class MainCommand extends AbstractCommand<UsageOptions> {

  public static Options parseOptions(final String[] args) {
    final CommandLine commandLine = new CommandLine(new MainCommand());
    final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    final ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    try {
      final List<Object> result;
      try (final PrintStream outStream =
              new PrintStream(outBytes, true, StandardCharsets.UTF_8.name());
          final PrintStream errStream =
              new PrintStream(errBytes, true, StandardCharsets.UTF_8.name())) {
        result =
            commandLine.parseWithHandlers(
                new RunLast().useOut(outStream).useErr(errStream),
                CommandLine.defaultExceptionHandler().useOut(outStream).useErr(errStream),
                args);
      }
      if (result == null || result.size() == 0) {
        if (errBytes.size() > 0) {
          return new InvalidOptions(
              new String(errBytes.toByteArray(), StandardCharsets.UTF_8.name()));
        } else {
          return new UsageOptions(
              new String(outBytes.toByteArray(), StandardCharsets.UTF_8.name()));
        }
      } else {
        return (Options) result.get(result.size() - 1);
      }
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    }
  }

  @Override
  public UsageOptions call() {
    final String usageMessage = new CommandLine(new MainCommand()).getUsageMessage();
    return new UsageOptions(getVerbosity(), isDoModifications(), usageMessage);
  }
}

@Command(name = "ls", description = "List paths")
@SuppressWarnings({"unused", "WeakerAccess"})
class LsCommand extends AbstractCommand<LsOptions> {

  @Option(names = "-l", description = "List in long format")
  private boolean listAttributes;

  @Parameters(paramLabel = "<path>", description = "Cloud or local path(s)")
  private String[] paths;

  public boolean isListAttributes() {
    return listAttributes;
  }

  public String[] getPaths() {
    return paths == null ? new String[0] : paths;
  }

  @Override
  public LsOptions call() {
    return new LsOptions(getVerbosity(), isDoModifications(), isListAttributes(), getPaths());
  }
}

@Command(name = "cat", description = "Concatenate and print files")
@SuppressWarnings({"unused", "WeakerAccess"})
class CatCommand extends AbstractCommand<CatOptions> {

  @Parameters(paramLabel = "<path>", description = "Cloud or local path(s)")
  private String[] paths;

  public String[] getPaths() {
    return paths == null ? new String[0] : paths;
  }

  @Override
  public CatOptions call() {
    return new CatOptions(getVerbosity(), isDoModifications(), getPaths());
  }
}

@Command(name = "cp", description = "Copy files")
@SuppressWarnings({"unused", "WeakerAccess"})
class CpCommand extends AbstractCommand<CpOptions> {

  @Parameters(paramLabel = "<source>", description = "Source cloud or local path", index = "0")
  private String sourcePath;

  @Parameters(paramLabel = "<target>", description = "Target cloud or local path", index = "1")
  private String targetPath;

  public String getSourcePath() {
    return sourcePath;
  }

  public String getTargetPath() {
    return targetPath;
  }

  @Override
  public CpOptions call() {
    return new CpOptions(getVerbosity(), isDoModifications(), getSourcePath(), getTargetPath());
  }
}

@Command(name = "rm", description = "Remove files")
@SuppressWarnings({"unused", "WeakerAccess"})
class RmCommand extends AbstractCommand<RmOptions> {

  @Parameters(paramLabel = "<path>", description = "Cloud or local path(s)")
  private String[] paths;

  public String[] getPaths() {
    return paths == null ? new String[0] : paths;
  }

  @Override
  public RmOptions call() {
    return new RmOptions(getVerbosity(), isDoModifications(), getPaths());
  }
}
