package alfre.v0.cli;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "ls", description = "List paths")
@SuppressWarnings({"unused", "WeakerAccess"})
public class LsCommand extends Command<LsOptions> {

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
