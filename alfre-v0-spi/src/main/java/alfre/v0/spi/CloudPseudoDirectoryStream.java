package alfre.v0.spi;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class CloudPseudoDirectoryStream<CloudHostT extends CloudHost>
    implements DirectoryStream<Path> {

  private final CloudPath<CloudHostT> prefix;
  private final DirectoryStream.Filter<? super Path> filter;

  /** Creates a new CloudPseudoDirectoryStream. */
  public CloudPseudoDirectoryStream(
      final CloudPath<CloudHostT> prefix, final Filter<? super Path> filter) {
    this.prefix = prefix;
    this.filter = filter;
  }

  @Override
  public Iterator<Path> iterator() {
    return pathStream(null).filter(this::filterPath).iterator();
  }

  @Override
  public void close() {
    /* Nothing to close. */
  }

  private Stream<Path> pathStream(final String marker) {
    final CloudFileList nextList = listNext(marker);
    final Stream<Path> nextStream = nextList.getElements().map(this::toPath);
    final String nextMarker = nextList.getMarker();
    if (nextMarker == null) {
      return nextStream;
    } else {
      return Stream.<Supplier<Stream<Path>>>of(() -> nextStream, () -> pathStream(nextMarker))
          .flatMap(Supplier::get);
    }
  }

  private Path toPath(final CloudFileListElement element) {
    final String key = element.getKey();
    final String slashed = key.startsWith("/") ? key : "/" + key;
    return prefix.getFileSystem().getPath(slashed).withAttributes(element.getAttributes());
  }

  private CloudFileList listNext(final String marker) {
    try {
      final CloudFileSystem<CloudHostT> fileSystem = prefix.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      return retry.runWithRetries(() -> fileProvider.list(prefix, marker));
    } catch (final CloudRetryException cloudRetryException) {
      throw new DirectoryIteratorException(cloudRetryException.getCauseIoException());
    }
  }

  private boolean filterPath(final Path path) {
    if (Objects.equals(path, prefix)) {
      return false;
    }
    try {
      return filter.accept(path);
    } catch (final IOException ioException) {
      throw new DirectoryIteratorException(ioException);
    }
  }
}
