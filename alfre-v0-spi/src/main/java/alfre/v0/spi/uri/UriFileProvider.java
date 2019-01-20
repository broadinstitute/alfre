package alfre.v0.spi.uri;

import alfre.v0.spi.CloudFileList;
import alfre.v0.spi.CloudFileListElement;
import alfre.v0.spi.CloudFileProvider;
import alfre.v0.spi.CloudPath;
import alfre.v0.spi.CloudRegularFileAttributes;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.OpenOption;
import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public abstract class UriFileProvider implements CloudFileProvider<UriHost> {

  public abstract boolean exists(final URI uri) throws Exception;

  @Override
  public final boolean exists(final CloudPath<UriHost> cloudPath) throws Exception {
    return exists(cloudPath.toUri());
  }

  public boolean existsPrefix(final URI uriPrefix) throws Exception {
    return exists(uriPrefix);
  }

  @Override
  public final boolean existsPrefix(final CloudPath<UriHost> cloudPathPrefix) throws Exception {
    return existsPrefix(cloudPathPrefix.toUri());
  }

  /** Returns a list of paths matching the supplied prefix. */
  @SuppressWarnings("unused")
  public CloudFileList list(final URI uriPrefix, final String marker) throws Exception {
    final boolean exists = exists(uriPrefix);
    return exists
        ? CloudFileList.of(new CloudFileListElement(uriPrefix.getPath()))
        : CloudFileList.empty();
  }

  @Override
  public final CloudFileList list(final CloudPath<UriHost> cloudPathPrefix, final String marker)
      throws Exception {
    return list(cloudPathPrefix.toUri(), marker);
  }

  public abstract void copy(
      final URI sourceUri, final URI targetUri, final Collection<? extends CopyOption> options)
      throws Exception;

  @Override
  public final void copy(
      final CloudPath<UriHost> sourceCloudPath,
      final CloudPath<UriHost> targetCloudPath,
      final Collection<? extends CopyOption> options)
      throws Exception {
    copy(sourceCloudPath.toUri(), targetCloudPath.toUri(), options);
  }

  public abstract ReadableByteChannel read(
      final URI uri, final long offset, final Collection<? extends OpenOption> options)
      throws Exception;

  @Override
  public final ReadableByteChannel read(
      final CloudPath<UriHost> cloudPath,
      final long offset,
      final Collection<? extends OpenOption> options)
      throws Exception {
    return read(cloudPath.toUri(), offset, options);
  }

  public abstract WritableByteChannel write(
      final URI uri, final long offset, final Collection<? extends OpenOption> options)
      throws Exception;

  @Override
  public final WritableByteChannel write(
      final CloudPath<UriHost> cloudPath,
      final long offset,
      final Collection<? extends OpenOption> options)
      throws Exception {
    return write(cloudPath.toUri(), offset, options);
  }

  public abstract boolean deleteIfExists(final URI uri) throws Exception;

  @Override
  public final boolean deleteIfExists(final CloudPath<UriHost> cloudPath) throws Exception {
    return deleteIfExists(cloudPath.toUri());
  }

  public abstract Optional<CloudRegularFileAttributes> fileAttributes(final URI uri)
      throws Exception;

  @Override
  public final Optional<CloudRegularFileAttributes> fileAttributes(
      final CloudPath<UriHost> cloudPath) throws Exception {
    return fileAttributes(cloudPath.toUri());
  }
}
