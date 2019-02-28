package alfre.v0.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class CloudFiles {

  /** Lists all files under a path. */
  public static Stream<Path> listRegularFiles(final Path path) throws IOException {
    Objects.requireNonNull(path, "path is null");
    return Files.walk(path, Integer.MAX_VALUE).filter(Files::isRegularFile);
  }

  /** Lists all files under a path. */
  public static Stream<PathPair> relativeFiles(final Path sourcePath, final Path targetPath)
      throws IOException {
    return relativeFiles(sourcePath, targetPath, CloudPaths::toRelativeString);
  }

  /** Returns all regular files under sourcePath mapped relatively to targetPath. */
  public static Stream<PathPair> relativeFiles(
      final Path sourcePath, final Path targetPath, final Function<Path, String> pathToString)
      throws IOException {
    return listRegularFiles(sourcePath)
        .map(path -> relativeSourceTarget(sourcePath, targetPath, path, pathToString));
  }

  private static PathPair relativeSourceTarget(
      final Path sourcePath,
      final Path targetPath,
      final Path path,
      final Function<Path, String> pathToString) {
    final Path sourceRelative = sourcePath.relativize(path);
    final String relativeString = pathToString.apply(sourceRelative);
    final Path targetResolve = targetPath.resolve(relativeString);
    return new PathPair(path, targetResolve);
  }
}
