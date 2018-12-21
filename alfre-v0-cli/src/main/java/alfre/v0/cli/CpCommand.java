package alfre.v0.cli;

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "cp", description = "Copy files")
@SuppressWarnings({"unused", "WeakerAccess"})
public class CpCommand extends Command<CpOptions> {

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
