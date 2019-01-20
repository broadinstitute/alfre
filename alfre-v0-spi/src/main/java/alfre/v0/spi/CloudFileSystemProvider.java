package alfre.v0.spi;

import alfre.v0.spi.function.SupplierWithExceptions;
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
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public abstract class CloudFileSystemProvider<CloudHostT extends CloudHost>
    extends FileSystemProvider {

  protected abstract CloudFileProvider<CloudHostT> newFileProvider(final Map<String, ?> env);

  @SuppressWarnings("unused")
  protected CloudRetry newRetry(final Map<String, ?> env) {
    return new SingleTry();
  }

  /** Returns the host of the provided URI. */
  public abstract CloudHostT getHost(final String uriAsString, final Map<String, ?> env);

  /** Creates a new CloudFileSystem for the URI. */
  public CloudFileSystem<CloudHostT> newFileSystem(
      final String uriAsString, final Map<String, ?> env) {
    final CloudRetry retry = newRetry(env);
    return newFileSystem(uriAsString, retry, env);
  }

  /** Creates a new CloudFileSystem for the URI. */
  public CloudFileSystem<CloudHostT> newFileSystem(
      final String uriAsString, final CloudRetry retry, final Map<String, ?> env) {
    final CloudHostT host = getHost(uriAsString, env);
    final CloudFileProvider<CloudHostT> cloudFileProvider = newFileProvider(env);
    return new CloudFileSystem<>(this, cloudFileProvider, host, retry);
  }

  @Override
  public CloudFileSystem<CloudHostT> newFileSystem(final URI uri, final Map<String, ?> env) {
    return newFileSystem(uri.toString(), env);
  }

  @Override
  public CloudFileSystem<CloudHostT> getFileSystem(final URI uri) {
    return newFileSystem(uri, Collections.emptyMap());
  }

  @Override
  public CloudPath<CloudHostT> getPath(final URI uri) {
    return getFileSystem(uri).getPath(uri.getPath());
  }

  @Override
  public SeekableByteChannel newByteChannel(
      final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs)
      throws IOException {
    final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(path);

    for (final OpenOption option : options) {
      if (option instanceof StandardOpenOption) {
        final StandardOpenOption standardOption = (StandardOpenOption) option;
        switch (standardOption) {
          case READ:
          case WRITE:
          case TRUNCATE_EXISTING:
          case CREATE:
          case CREATE_NEW:
          case SPARSE:
            /* ok */
            break;
          default:
            throw new UnsupportedOperationException(option.toString());
        }
      }
    }

    if (options.contains(StandardOpenOption.READ) && options.contains(StandardOpenOption.WRITE)) {
      throw new UnsupportedOperationException("Cannot open a READ+WRITE channel");
    } else if (options.contains(StandardOpenOption.WRITE)) {
      return new CloudWriteChannel<>(cloudPath, options);
    } else {
      return new CloudReadChannel<>(cloudPath, options);
    }
  }

  @Override
  public DirectoryStream<Path> newDirectoryStream(
      final Path dir, final DirectoryStream.Filter<? super Path> filter) {
    final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(dir);
    return new CloudPseudoDirectoryStream<>(cloudPath, filter);
  }

  @SuppressWarnings("RedundantThrows")
  @Override
  public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
    /* ignored, but may be overridden */
  }

  @Override
  public void delete(final Path path) throws IOException {
    if (deleteIfExists(path)) {
      throw new NoSuchFileException(null);
    }
  }

  @Override
  public boolean deleteIfExists(final Path path) throws IOException {
    try {
      final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(path);
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();

      if (checkDirectoryExists(cloudPath)) {
        final Boolean hasObjects = retry.runWithRetries(() -> fileProvider.existsPrefix(cloudPath));
        if (hasObjects) {
          throw new UnsupportedOperationException("Can not delete a non-empty directory");
        } else {
          return true;
        }
      } else {
        return retry.runWithRetries(() -> fileProvider.deleteIfExists(cloudPath));
      }
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public void copy(final Path source, final Path target, final CopyOption... options)
      throws IOException {
    try {
      final CloudPath<CloudHostT> sourceCloudPath = CloudPath.checkPath(source);
      final CloudPath<CloudHostT> targetCloudPath = CloudPath.checkPath(target);
      final CloudFileSystem<CloudHostT> fileSystem = targetCloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();

      final Collection<CopyOption> optionsCollection = Arrays.asList(options);
      if (!Objects.equals(sourceCloudPath, targetCloudPath)) {
        retry.runWithRetries(
            () -> {
              fileProvider.copy(sourceCloudPath, targetCloudPath, optionsCollection);
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
    try {
      if (modes.length != 0) {
        throw new UnsupportedOperationException(
            "Checking access modes is not supported, only file existence.");
      }

      final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(path);
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();

      final boolean exists =
          checkDirectoryExists(cloudPath)
              || retry.runWithRetries(() -> fileProvider.exists(cloudPath));
      if (!exists) {
        throw new NoSuchFileException(null);
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

    final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(path);
    final SupplierWithExceptions<Boolean, IOException> isDirectory =
        () -> checkDirectoryExists(cloudPath);

    return (V) new CloudFileAttributeView<>(cloudPath, isDirectory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <A extends BasicFileAttributes> A readAttributes(
      final Path path, final Class<A> type, final LinkOption... options) throws IOException {
    if (type != CloudFileAttributes.class && type != BasicFileAttributes.class) {
      throw new UnsupportedOperationException(type.getSimpleName());
    }

    final CloudPath<CloudHostT> cloudPath = CloudPath.checkPath(path);

    final CloudFileAttributes attributes = cloudPath.getAttributes();
    if (attributes != null) {
      return (A) attributes;
    }

    final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
    final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
    final CloudRetry retry = fileSystem.getRetry();

    if (checkDirectoryExists(cloudPath)) {
      return (A) CloudPseudoDirectoryAttributes.INSTANCE;
    } else {
      try {
        return (A)
            retry
                .runWithRetries(() -> fileProvider.fileAttributes(cloudPath))
                .orElseThrow(() -> new NoSuchFileException(null));
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

  private boolean checkDirectoryExists(final CloudPath<CloudHostT> cloudPath) throws IOException {
    try {
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      // Anything that "seems" like a directory exists.
      // Otherwise see if the path with a "/" contains files on the cloud.
      return cloudPath.seemsLikeDirectory()
          || retry.runWithRetries(
              () -> fileProvider.existsPrefix(cloudPath.addTrailingSeparator()));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }
}
