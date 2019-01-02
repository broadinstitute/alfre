package alfre.v0.cli;

import java.util.Arrays;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "rm", description = "Remove files")
@SuppressWarnings("WeakerAccess")
public class RmCommand extends Command<RmOptions> {

  @SuppressWarnings("unused")
  @Parameters(paramLabel = "<path>", description = "Cloud or local path(s)")
  private String[] paths;

  public String[] getPaths() {
    return paths == null ? new String[0] : Arrays.copyOf(paths, paths.length);
  }

  @Override
  public RmOptions call() {
    return new RmOptions(getVerbosity(), isDoModifications(), getPaths());
  }
}
