package alfre.v0.spi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
public class CloudPath<CloudHostT extends CloudHost> implements Path {

  private final UnixPath unixPath;
  private final CloudFileSystem<CloudHostT> fileSystem;
  private final CloudFileAttributes attributes;

  /** Creates a new CloudPath. */
  protected CloudPath(final CloudFileSystem<CloudHostT> fileSystem, final UnixPath unixPath) {
    this(fileSystem, unixPath, null);
  }

  /** Creates a new CloudPath. */
  protected CloudPath(
      final CloudFileSystem<CloudHostT> fileSystem,
      final UnixPath unixPath,
      final CloudFileAttributes attributes) {
    Objects.requireNonNull(fileSystem, "fileSystem is null");
    Objects.requireNonNull(unixPath, "unixPath is null");
    this.fileSystem = fileSystem;
    this.unixPath = unixPath;
    this.attributes = attributes;
  }

  protected CloudPath<CloudHostT> withAttributes(final CloudFileAttributes attributes) {
    if (attributes == null) {
      return this;
    } else {
      return new CloudPath<>(fileSystem, unixPath, attributes);
    }
  }

  public CloudFileAttributes getAttributes() {
    return attributes;
  }

  public CloudHostT getCloudHost() {
    return fileSystem.getHost();
  }

  public String getAbsolutedPathAsString() {
    return unixPath.toAbsolutePath().removeBeginningSeparator().toString();
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
    return getCloudHost().getUriAsString(getAbsolutedPathAsString());
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
      return getCloudHost().getRelativeHostPath(unixPath.removeBeginningSeparator().toString());
    } else {
      return unixPath.normalize().toString();
    }
  }

  public boolean seemsLikeDirectory() {
    return unixPath.seemsLikeDirectory();
  }

  public CloudPath<CloudHostT> addTrailingSeparator() {
    return new CloudPath<>(
        fileSystem, unixPath.addTrailingSeparator(), CloudPseudoDirectoryAttributes.INSTANCE);
  }

  @Override
  public CloudFileSystem<CloudHostT> getFileSystem() {
    return fileSystem;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(final Path other) {
    if (!(other instanceof CloudPath)) {
      return -1;
    }
    final CloudPath<CloudHostT> that = (CloudPath<CloudHostT>) other;
    final int result = getCloudHost().compareTo(that.getCloudHost());
    return result != 0 ? result : unixPath.compareTo(that.unixPath);
  }

  @Override
  public boolean isAbsolute() {
    return unixPath.isAbsolute();
  }

  @Override
  public CloudPath<CloudHostT> getRoot() {
    return newPathOrNull(unixPath.getRoot());
  }

  @Override
  public CloudPath<CloudHostT> getFileName() {
    return newPathOrNull(unixPath.getFileName());
  }

  @Override
  public CloudPath<CloudHostT> getParent() {
    return newPathOrNull(unixPath.getParent());
  }

  @Override
  public int getNameCount() {
    return unixPath.getNameCount();
  }

  @Override
  public CloudPath<CloudHostT> getName(final int index) {
    return newPathOrNull(unixPath.getName(index));
  }

  @Override
  public CloudPath<CloudHostT> subpath(final int beginIndex, final int endIndex) {
    return newPathOrNull(unixPath.subpath(beginIndex, endIndex));
  }

  @SuppressWarnings("unchecked")
  private boolean isSameCloudHost(final Path other) {
    if (other instanceof CloudPath) {
      final CloudPath<CloudHostT> that = (CloudPath<CloudHostT>) other;
      return Objects.equals(getCloudHost(), that.getCloudHost());
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean startsWith(final Path other) {
    if (isSameCloudHost(other)) {
      final CloudPath<CloudHostT> that = (CloudPath<CloudHostT>) other;
      return unixPath.startsWith(that.unixPath);
    }
    return false;
  }

  @Override
  public boolean startsWith(final String other) {
    return unixPath.startsWith(UnixPath.getPath(other));
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean endsWith(final Path other) {
    if (isSameCloudHost(other)) {
      final CloudPath<CloudHostT> that = (CloudPath<CloudHostT>) other;
      return unixPath.endsWith(that.unixPath);
    }
    return false;
  }

  @Override
  public boolean endsWith(final String other) {
    return unixPath.endsWith(UnixPath.getPath(other));
  }

  @Override
  public CloudPath<CloudHostT> normalize() {
    return newPathOrNull(unixPath.normalize());
  }

  @Override
  public CloudPath<CloudHostT> resolve(final Path other) {
    final CloudPath<CloudHostT> that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.resolve(that.unixPath));
  }

  @Override
  public CloudPath<CloudHostT> resolve(final String other) {
    return newPathOrNull(unixPath.resolve(UnixPath.getPath(other)));
  }

  @Override
  public CloudPath<CloudHostT> resolveSibling(final Path other) {
    final CloudPath<CloudHostT> that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.resolveSibling(that.unixPath));
  }

  @Override
  public CloudPath<CloudHostT> resolveSibling(final String other) {
    return newPathOrNull(UnixPath.getPath(other));
  }

  @Override
  public CloudPath<CloudHostT> relativize(final Path other) {
    final CloudPath<CloudHostT> that = CloudPath.checkPath(other);
    return newPathOrNull(unixPath.relativize(that.unixPath));
  }

  @Override
  public URI toUri() {
    try {
      return new URI(getUriAsString());
    } catch (final URISyntaxException uriSyntaxException) {
      throw new CloudPathException(uriSyntaxException);
    }
  }

  @Override
  public CloudPath<CloudHostT> toAbsolutePath() {
    return newPathOrNull(unixPath.toAbsolutePath());
  }

  @Override
  public Path toRealPath(final LinkOption... options) throws IOException {
    return this.fileSystem.provider().toRealPath(this, options);
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

  protected CloudPath<CloudHostT> newPathOrNull(final UnixPath unixPath) {
    if (unixPath == null) {
      return null;
    } else if (Objects.equals(this.unixPath, unixPath)) {
      return this;
    } else {
      return new CloudPath<>(fileSystem, unixPath);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof CloudPath)) {
      return false;
    }

    final CloudPath<CloudHostT> paths = (CloudPath<CloudHostT>) other;

    if (!Objects.equals(unixPath, paths.unixPath)) {
      return false;
    }
    return Objects.equals(fileSystem, paths.fileSystem);
  }

  @Override
  public int hashCode() {
    int result;
    result = unixPath != null ? unixPath.hashCode() : 0;
    result = 31 * result + (fileSystem != null ? fileSystem.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return getPathOnlyAsString();
  }

  /**
   * Returns the path if it's a cloud path, otherwise throws a {@code ProviderMismatchException}.
   */
  @SuppressWarnings("unchecked")
  public static <CloudHostT extends CloudHost> CloudPath<CloudHostT> checkPath(final Path path) {
    Objects.requireNonNull(path, "path is null");
    if (path instanceof CloudPath) {
      return (CloudPath<CloudHostT>) path;
    } else {
      throw new ProviderMismatchException("Not a CloudPath: " + path.getClass());
    }
  }
}
