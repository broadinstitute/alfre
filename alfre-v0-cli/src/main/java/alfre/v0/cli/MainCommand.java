package alfre.v0.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.RunLast;

@CommandLine.Command(
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
public class MainCommand extends Command<UsageOptions> {

  /** Parse args into command line options. */
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
    } catch (final UnsupportedEncodingException unsupportedEncodingException) {
      throw new CommandLineException(unsupportedEncodingException);
    }
  }

  @Override
  public UsageOptions call() {
    final String usageMessage = new CommandLine(new MainCommand()).getUsageMessage();
    return new UsageOptions(getVerbosity(), isDoModifications(), usageMessage);
  }
}
