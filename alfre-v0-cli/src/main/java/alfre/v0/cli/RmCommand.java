package alfre.v0.cli;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "rm", description = "Remove files")
@SuppressWarnings({"unused", "WeakerAccess"})
public class RmCommand extends Command<RmOptions> {

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
