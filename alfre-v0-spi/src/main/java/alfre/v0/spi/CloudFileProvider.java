package alfre.v0.spi;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Optional;

public interface CloudFileProvider<CloudHostT extends CloudHost> {

  boolean exists(CloudPath<CloudHostT> cloudPath) throws Exception;

  default boolean existsPrefix(final CloudPath<CloudHostT> cloudPathPrefix) throws Exception {
    return exists(cloudPathPrefix);
  }

  /** Returns a list of paths matching the supplied prefix. */
  @SuppressWarnings("unused")
  default CloudFileList list(final CloudPath<CloudHostT> cloudPathPrefix, final String marker)
      throws Exception {
    final boolean exists = exists(cloudPathPrefix);
    return exists
        ? CloudFileList.of(new CloudFileListElement(cloudPathPrefix.getAbsolutedPathAsString()))
        : CloudFileList.empty();
  }

  default void copy(
      final CloudPath<CloudHostT> sourceCloudPath, final CloudPath<CloudHostT> targetCloudPath)
      throws Exception {
    throw new UnsupportedOperationException(getClass() + " copying not supported");
  }

  ReadableByteChannel read(CloudPath<CloudHostT> cloudPath, long offset) throws Exception;

  WritableByteChannel write(CloudPath<CloudHostT> cloudPath, long offset) throws Exception;

  boolean deleteIfExists(CloudPath<CloudHostT> cloudPath) throws Exception;

  Optional<CloudRegularFileAttributes> fileAttributes(CloudPath<CloudHostT> cloudPath)
      throws Exception;
}
