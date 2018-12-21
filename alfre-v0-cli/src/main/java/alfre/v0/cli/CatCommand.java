package alfre.v0.cli;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "cat", description = "Concatenate and print files")
@SuppressWarnings({"unused", "WeakerAccess"})
public class CatCommand extends Command<CatOptions> {

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
