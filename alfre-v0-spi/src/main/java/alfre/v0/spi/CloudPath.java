package alfre.v0.spi;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CloudPath implements Path {

  /**
   * Returns the path if it's a cloud path, otherwise throws a {@code ProviderMismatchException}.
   */
  public static CloudPath checkPath(final Path path) {
    if (path instanceof CloudPath) {
      return (CloudPath) path;
    } else {
      throw new ProviderMismatchException("Not a CloudPath: " + path);
    }
  }

  private final UnixPath unixPath;
  private final CloudFileSystem fileSystem;

  CloudPath(final CloudFileSystem fileSystem, final UnixPath unixPath) {
    this.fileSystem = fileSystem;
    this.unixPath = unixPath;
  }

  public String getCloudHost() {
    return fileSystem.getHost();
  }

  public String getCloudPath() {
    return unixPath.toAbsolutePath().toString().replaceFirst("^/", "");
  }

  /**
   * Returns a path compatible with [[java.nio.file.Paths#get(java.net.URI)]].
   *
   * <p>``` val path1: CloudNioPath = ... val uri = new URI(path1.uriAsString) val path2: Path =
   * Paths.get(uri) require(path1 == path2) ```
   *
   * @see CloudPath#toString()
   * @see java.nio.file.Paths#get(java.net.URI)
   */
  public String getUriAsString() {
    return fileSystem.provider().getScheme() + "://" + getCloudHost() + "/" + getCloudPath();
  }

  /** Returns just the path as a string without the host. */
  public String getPathOnlyAsString() {
    return unixPath.toString();
  }

  /**
   * If this path is relative, returns just the normalized path, otherwise if this path is absolute,
   * return the host + the absolute path.
   */
  public String getRelativeDependentPath() {
    if (unixPath.isAbsolute()) {
      return getCloudHost() + "/" + unixPath.toString().replaceFirst("^/", "");
    } else {
      return unixPath.normalize().toString();
    }
  }

  public boolean seemsLikeDirectory() {
    return unixPath.seemsLikeADirectory();
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public int compareTo(final Path other) {
    if (!(other instanceof CloudPath)) {
      return -1;
    }
    final CloudPath that = (CloudPath) other;
    final int res = getCloudHost().compareTo(that.getCloudHost());
    return res != 0 ? res : unixPath.compareTo(that.unixPath);
  }

  @Override
  public boolean isAbsolute() {
    return unixPath.isAbsolute();
  }

  @Override
  public CloudPath getRoot() {
    return newPathOrNull(unixPath.getRoot());
  }

  @Override
  public CloudPath getFileName() {
    return newPathOrNull(unixPath.getFileName());
  }

  @Override
  public CloudPath getParent() {
    return newPathOrNull(unixPath.getParent());
  }

  @Override
  public int getNameCount() {
    return unixPath.getNameCount();
  }

  @Override
  public CloudPath getName(final int index) {
    return newPathOrNull(unixPath.getName(index));
  }

  @Override
  public CloudPath subpath(final int beginIndex, final int endIndex) {
    return newPathOrNull(unixPath.subpath(beginIndex, endIndex));
  }

  private boolean isSameCloudHost(final Path other) {
    if (other instanceof CloudPath) {
      final CloudPath that = (CloudPath) other;
      return Objects.equals(getCloudHost(), that.getCloudHost());
    }
    return false;
  }

  @Override
  public boolean startsWith(final Path other) {
    if (isSameCloudHost(other)) {
      final CloudPath that = (CloudPath) other;
      return unixPath.startsWith(that.unixPath);
    }
    return false;
  }

  @Override
  public boolean startsWith(final String other) {
    return unixPath.startsWith(UnixPath.getPath(other));
  }

  @Override
  public boolean endsWith(final Path other) {
    if (isSameCloudHost(other)) {
      final CloudPath that = (CloudPath) other;
      return unixPath.endsWith(that.unixPath);
    }
    return false;
  }

  @Override
  public boolean endsWith(final String other) {
    return unixPath.endsWith(UnixPath.getPath(other));
  }

  @Override
  public CloudPath normalize() {
    return newPathOrNull(unixPath.normalize());
  }

  @Override
  public CloudPath resolve(final Path other) {
    final CloudPath that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.resolve(that.unixPath));
  }

  @Override
  public CloudPath resolve(final String other) {
    return newPathOrNull(unixPath.resolve(UnixPath.getPath(other)));
  }

  @Override
  public CloudPath resolveSibling(final Path other) {
    final CloudPath that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.resolveSibling(that.unixPath));
  }

  @Override
  public CloudPath resolveSibling(final String other) {
    return newPathOrNull(UnixPath.getPath(other));
  }

  @Override
  public CloudPath relativize(final Path other) {
    final CloudPath that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.relativize(that.unixPath));
  }

  @Override
  public URI toUri() {
    try {
      return new URI(
          fileSystem.provider().getScheme(), fileSystem.getHost(), "/" + getCloudPath(), null);
    } catch (final URISyntaxException uriSyntaxException) {
      throw new CloudPathException(uriSyntaxException);
    }
  }

  @Override
  public CloudPath toAbsolutePath() {
    return newPathOrNull(unixPath.toAbsolutePath());
  }

  @Override
  public CloudPath toRealPath(final LinkOption... options) {
    return toAbsolutePath();
  }

  @Override
  public File toFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(
      final WatchService watcher,
      final WatchEvent.Kind<?>[] events,
      final WatchEvent.Modifier... modifiers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchKey register(final WatchService watcher, final WatchEvent.Kind<?>... events) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterator<Path> iterator() {
    if (unixPath.isEmpty() || unixPath.isRoot()) {
      return Collections.emptyIterator();
    } else {
      return unixPath
          .getParts()
          .stream()
          .map(UnixPath::getPath)
          .map(this::newPathOrNull)
          .map(cloudPath -> (Path) cloudPath)
          .iterator();
    }
  }

  protected CloudPath newPathOrNull(final UnixPath unixPath) {
    if (unixPath == null) {
      return null;
    } else if (Objects.equals(this.unixPath, unixPath)) {
      return this;
    } else {
      return new CloudPath(fileSystem, unixPath);
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CloudPath)) {
      return false;
    }

    final CloudPath paths = (CloudPath) o;

    if (!Objects.equals(unixPath, paths.unixPath)) {
      return false;
    }
    return Objects.equals(fileSystem, paths.fileSystem);
  }

  @Override
  public int hashCode() {
    int result;
    result = (unixPath != null ? unixPath.hashCode() : 0);
    result = 31 * result + (fileSystem != null ? fileSystem.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return String.format("CloudPath{unixPath=%s, fileSystem=%s}", unixPath, fileSystem);
  }
}
