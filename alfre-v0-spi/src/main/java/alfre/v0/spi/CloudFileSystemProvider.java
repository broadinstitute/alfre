package alfre.v0.spi;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO: Finish implementations
public abstract class CloudFileSystemProvider extends FileSystemProvider {

  protected abstract CloudFileProvider fileProvider();

  protected abstract CloudRetry retry();

  @Override
  public CloudFileSystem newFileSystem(final URI uri, final Map<String, ?> env) {
    return null;
  }

  @Override
  public CloudFileSystem getFileSystem(final URI uri) {
    return newFileSystem(uri, Collections.emptyMap());
  }

  @Override
  public CloudPath getPath(final URI uri) {
    return null;
  }

  @Override
  public SeekableByteChannel newByteChannel(
      final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
      throws IOException {
    return null;
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(
      final Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
    return null;
  }

  @Override
  public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {}

  @Override
  public void delete(final Path path) throws IOException {}

  @Override
  public boolean deleteIfExists(final Path path) throws IOException {
    return super.deleteIfExists(path);
  }

  @Override
  public void copy(final Path source, final Path target, final CopyOption... options)
      throws IOException {
    try {
      final CloudPath sourceCloudPath = CloudPath.checkPath(source);
      final CloudPath targetCloudPath = CloudPath.checkPath(target);

      if (!Objects.equals(sourceCloudPath, targetCloudPath)) {
        retry()
            .<Void>runWithRetries(
                () -> {
                  fileProvider()
                      .copy(
                          sourceCloudPath.getCloudHost(),
                          sourceCloudPath.getCloudPath(),
                          targetCloudPath.getCloudHost(),
                          targetCloudPath.getCloudPath());
                  return null;
                });
      }
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public void move(final Path source, final Path target, final CopyOption... options)
      throws IOException {
    for (final CopyOption option : options) {
      if (option == StandardCopyOption.ATOMIC_MOVE) {
        throw new AtomicMoveNotSupportedException(null, null, "Atomic move unsupported");
      }
    }
    copy(source, target, options);
    delete(source);
  }

  @Override
  public boolean isSameFile(final Path path, final Path path2) {
    return Objects.equals(CloudPath.checkPath(path), CloudPath.checkPath(path2));
  }

  @Override
  public boolean isHidden(final Path path) {
    CloudPath.checkPath(path);
    return false;
  }

  @Override
  public FileStore getFileStore(final Path path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
    if (modes.length != 0) {
      throw new UnsupportedOperationException(
          "Checking access modes is not supported, only file existence.");
    }

    final CloudPath cloudPath = CloudPath.checkPath(path);

    try {
      final boolean exists =
          checkDirectoryExists(cloudPath)
              || retry()
                  .runWithRetries(
                      () ->
                          fileProvider()
                              .existsPath(cloudPath.getCloudHost(), cloudPath.getCloudPath()));
      if (!exists) {
        throw new NoSuchFileException(cloudPath.getUriAsString());
      }
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V extends FileAttributeView> V getFileAttributeView(
      final Path path, final Class<V> type, final LinkOption... options) {
    if (type != CloudFileAttributeView.class && type != BasicFileAttributeView.class) {
      throw new UnsupportedOperationException(type.getSimpleName());
    }

    final CloudPath cloudPath = CloudPath.checkPath(path);
    final CloudSupplier<Boolean> isDirectory = () -> checkDirectoryExists(cloudPath);

    return (V) new CloudFileAttributeView(fileProvider(), retry(), cloudPath, isDirectory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends BasicFileAttributes> A readAttributes(
      final Path path, final Class<A> type, final LinkOption... options) throws IOException {
    if (type != CloudFileAttributes.class && type != BasicFileAttributes.class) {
      throw new UnsupportedOperationException(type.getSimpleName());
    }

    final CloudPath cloudPath = CloudPath.checkPath(path);

    if (checkDirectoryExists(cloudPath)) {
      return (A) new CloudPseudoDirectoryAttributes(cloudPath);
    } else {
      try {
        return (A)
            retry()
                .runWithRetries(
                    () ->
                        fileProvider()
                            .fileAttributes(cloudPath.getCloudHost(), cloudPath.getCloudPath()))
                .orElseThrow(() -> new NoSuchFileException(cloudPath.getUriAsString()));
      } catch (final CloudRetryException cloudRetryException) {
        throw cloudRetryException.getCauseIoException();
      }
    }
  }

  @Override
  public Map<String, Object> readAttributes(
      final Path path, final String attributes, final LinkOption... options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setAttribute(
      final Path path, final String attribute, final Object value, final LinkOption... options) {
    throw new UnsupportedOperationException();
  }

  private boolean checkDirectoryExists(final CloudPath cloudPath) throws IOException {
    try {
      // Anything that "seems" like a directory exists. Otherwise see if the path with a "/"
      // contains files on the cloud.
      return cloudPath.seemsLikeDirectory()
          || retry()
              .runWithRetries(
                  () ->
                      fileProvider()
                          .existsPaths(cloudPath.getCloudHost(), cloudPath.getCloudPath() + "/"));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }
}
