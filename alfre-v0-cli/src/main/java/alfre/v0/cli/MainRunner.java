package alfre.v0.cli;

import alfre.v0.spi.CloudFileAttributes;
import alfre.v0.util.CloudFiles;
import alfre.v0.util.CloudPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class MainRunner implements Runner {

  @Override
  public boolean runUsage(final UsageOptions usageOptions) {
    System.out.print(usageOptions.getUsage());
    return false;
  }

  @Override
  public boolean runInvalid(final InvalidOptions invalidOptions) {
    System.err.print(invalidOptions.getUsage());
    throw new MainShutdownException();
  }

  @Override
  public boolean runLs(final LsOptions lsOptions) {
    return Arrays.stream(lsOptions.getPaths()).anyMatch(filePath -> runLsFile(lsOptions, filePath));
  }

  @Override
  public boolean runCat(final CatOptions catOptions) {
    return Arrays.stream(catOptions.getPaths())
        .anyMatch(filePath -> runCatFile(catOptions, filePath));
  }

  @Override
  public boolean runCp(final CpOptions cpOptions) {
    try {
      final Path sourcePath = CloudPaths.get(cpOptions.getSourcePath());
      final Path targetPath = CloudPaths.get(cpOptions.getTargetPath());
      return CloudFiles.relativeFiles(sourcePath, targetPath)
          .anyMatch(pair2 -> runCpPath(cpOptions, pair2.getOne(), pair2.getTwo()));
    } catch (final Exception exception) {
      handleException(cpOptions, exception);
      return true;
    }
  }

  @Override
  public boolean runRm(final RmOptions rmOptions) {
    return Arrays.stream(rmOptions.getPaths()).anyMatch(filePath -> runRmFile(rmOptions, filePath));
  }

  private static boolean runLsFile(final LsOptions lsOptions, final String filePath) {
    try {
      final Path path = CloudPaths.get(filePath);
      return CloudFiles.listRegularFiles(path)
          .anyMatch(listedPath -> runLsPath(lsOptions, listedPath));
    } catch (final Exception exception) {
      handleException(lsOptions, exception);
      return true;
    }
  }

  private static boolean runLsPath(final LsOptions lsOptions, final Path path) {
    try {
      if (lsOptions.isListAttributes()) {
        final CloudFileAttributes attributes =
            Files.readAttributes(path, CloudFileAttributes.class);
        final String[] columns = {
          String.valueOf(attributes.size()),
          String.valueOf(attributes.lastModifiedTime()),
          CloudPaths.showAbsolute(path),
          attributes.fileHash() == null ? "" : attributes.fileHash(),
        };
        System.out.println(String.join("\t", columns));
      } else {
        System.out.println(CloudPaths.showAbsolute(path));
      }
      return false;
    } catch (final Exception exception) {
      handleException(lsOptions, exception);
      return true;
    }
  }

  private static boolean runCatFile(final CatOptions catOptions, final String filePath) {
    try {
      final Path path = CloudPaths.get(filePath);
      Files.copy(path, System.out);
      return false;
    } catch (final Exception exception) {
      handleException(catOptions, exception);
      return true;
    }
  }

  private static boolean runCpPath(
      final CpOptions cpOptions, final Path sourcePath, final Path targetPath) {
    try {
      if (!cpOptions.isDoModifications() || cpOptions.getVerbosity() == Verbosity.VERBOSE) {
        System.out.println(
            String.format(
                "%s -> %s",
                CloudPaths.showAbsolute(sourcePath), CloudPaths.showAbsolute(targetPath)));
      }
      if (cpOptions.isDoModifications()) {
        final Path parent = targetPath.getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }
      return false;
    } catch (final Exception exception) {
      handleException(cpOptions, exception);
      return true;
    }
  }

  private static boolean runRmFile(final RmOptions rmOptions, final String filePath) {
    try {
      final Path path = CloudPaths.get(filePath);
      return CloudFiles.listRegularFiles(path)
          .anyMatch(listedPath -> runRmPath(rmOptions, listedPath));
    } catch (final Exception exception) {
      handleException(rmOptions, exception);
      return true;
    }
  }

  private static boolean runRmPath(final RmOptions rmOptions, final Path path) {
    try {
      if (!rmOptions.isDoModifications() || rmOptions.getVerbosity() == Verbosity.VERBOSE) {
        System.out.println(CloudPaths.showAbsolute(path));
      }
      if (rmOptions.isDoModifications()) {
        Files.deleteIfExists(path);
      }
      return false;
    } catch (final Exception exception) {
      handleException(rmOptions, exception);
      return true;
    }
  }

  private static void handleException(final Options options, final Exception exception) {
    if (options.getVerbosity() != Verbosity.QUIET) {
      System.err.println("Error: " + exception);
      if (options.getVerbosity() == Verbosity.VERBOSE) {
        exception.printStackTrace();
      }
    }
  }
}
