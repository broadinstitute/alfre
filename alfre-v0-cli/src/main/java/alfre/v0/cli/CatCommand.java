package alfre.v0.cli;

import java.util.Arrays;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "cat", description = "Concatenate and print files")
@SuppressWarnings("WeakerAccess")
public class CatCommand extends Command<CatOptions> {

  @SuppressWarnings("unused")
  @Parameters(paramLabel = "<path>", description = "Cloud or local path(s)")
  private String[] paths;

  public String[] getPaths() {
    return paths == null ? new String[0] : Arrays.copyOf(paths, paths.length);
  }

  @Override
  public CatOptions call() {
    return new CatOptions(getVerbosity(), isDoModifications(), getPaths());
  }
}
