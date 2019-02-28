package alfre.v0.util;

import alfre.v0.spi.CloudPath;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CloudPaths {

  /**
   * Parses a path in a way reciprocal with {@link #toAbsoluteString toAbsoluteString}.
   *
   * @see alfre.v0.util.CloudPaths#toAbsoluteString(java.nio.file.Path)
   * @see alfre.v0.spi.CloudPath#getUriAsString()
   */
  public static Path get(final String filePath) {
    try {
      // TODO: softer parsing using Guava UrlEscapers. May also be better to list the providers
      // ourselves if possible.
      return Paths.get(new URI(filePath));
    } catch (final URISyntaxException uriSyntaxException) {
      return Paths.get(filePath);
    } catch (final IllegalArgumentException illegalArgumentException) {
      if ("Missing scheme".equals(illegalArgumentException.getMessage())) {
        return Paths.get(filePath);
      } else {
        throw illegalArgumentException;
      }
    }
  }

  /**
   * Return a path in a way reciprocal with {@link alfre.v0.util.CloudPaths#get get}.
   *
   * @see alfre.v0.util.CloudPaths#get(java.lang.String)
   * @see alfre.v0.util.CloudPaths#toRelativeString(java.nio.file.Path)
   * @see alfre.v0.spi.CloudPath#getUriAsString()
   */
  public static String toAbsoluteString(final Path path) {
    Objects.requireNonNull(path, "path is null");
    if (path instanceof CloudPath<?>) {
      final CloudPath<?> cloudPath = (CloudPath<?>) path;
      return cloudPath.getUriAsString();
    } else {
      return path.toAbsolutePath().toString();
    }
  }

  /**
   * When the path is relative returns a relative path in a way reciprocal with {@link
   * java.nio.file.Path#resolve resolve}.
   *
   * <p>If the path is absolute then it is returned as relative but including the host/bucket.
   *
   * @see alfre.v0.util.CloudPaths#toAbsoluteString(java.nio.file.Path)
   * @see java.nio.file.Path#resolve(java.nio.file.Path)
   * @see alfre.v0.spi.CloudPath#getUriAsString()
   */
  public static String toRelativeString(final Path path) {
    Objects.requireNonNull(path, "path is null");
    if (path instanceof CloudPath<?>) {
      final CloudPath<?> cloudPath = (CloudPath<?>) path;
      return cloudPath.getRelativeDependentPath();
    } else if (path.isAbsolute()) {
      return path.getRoot().relativize(path).normalize().toString();
    } else {
      return path.normalize().toString();
    }
  }
}
