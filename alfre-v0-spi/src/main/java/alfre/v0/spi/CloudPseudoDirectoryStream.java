package alfre.v0.spi;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class CloudPseudoDirectoryStream implements DirectoryStream<Path> {

  private final CloudFileProvider fileProvider;
  private final CloudRetry retry;
  private final CloudPath prefix;
  private final DirectoryStream.Filter<? super Path> filter;

  /** Creates a new CloudPseudoDirectoryStream. */
  public CloudPseudoDirectoryStream(
      final CloudFileProvider fileProvider,
      final CloudRetry retry,
      final CloudPath prefix,
      final Filter<? super Path> filter) {
    this.fileProvider = fileProvider;
    this.retry = retry;
    this.prefix = prefix;
    this.filter = filter;
  }

  @Override
  public Iterator<Path> iterator() {
    return pathStream(null).filter(this::filterPath).iterator();
  }

  @Override
  public void close() {}

  private Stream<Path> pathStream(final String marker) {
    final CloudFileList nextList = listNext(marker);
    final Stream<Path> nextStream =
        StreamSupport.stream(nextList.getPaths().spliterator(), false).map(this::toPath);
    final String nextMarker = nextList.getMarker();
    if (nextMarker == null) {
      return nextStream;
    } else {
      return Stream.<Supplier<Stream<Path>>>of(() -> nextStream, () -> pathStream(nextMarker))
          .flatMap(Supplier::get);
    }
  }

  private Path toPath(final String key) {
    return prefix.getFileSystem().getPath("/" + key);
  }

  private CloudFileList listNext(final String marker) {
    try {
      return retry.runWithRetries(
          () -> fileProvider.listObjects(prefix.getCloudHost(), prefix.getCloudPath(), marker));
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
