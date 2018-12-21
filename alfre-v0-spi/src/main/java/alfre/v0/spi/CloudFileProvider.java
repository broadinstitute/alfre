package alfre.v0.spi;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface CloudFileProvider {

  boolean existsPath(String cloudHost, String cloudPath) throws Exception;

  default boolean existsPaths(final String cloudHost, final String cloudPath) throws Exception {
    return existsPath(cloudHost, cloudPath);
  }

  /** Returns a list of objects matching the supplied prefix. */
  default CloudFileList listObjects(
      final String cloudHost, final String cloudPathPrefix, final String marker) throws Exception {
    final boolean exists = existsPath(cloudHost, cloudPathPrefix);
    final List<String> paths =
        exists ? Collections.singletonList(cloudPathPrefix) : Collections.emptyList();
    return new CloudFileList(paths, null);
  }

  default void copy(
      final String sourceCloudHost,
      final String sourceCloudPath,
      final String targetCloudHost,
      final String targetCloudPath)
      throws Exception {
    throw new UnsupportedOperationException(getClass() + " copying not supported");
  }

  boolean deleteIfExists(String cloudHost, String cloudPath) throws Exception;

  ReadableByteChannel read(String cloudHost, String cloudPath, long offset) throws Exception;

  WritableByteChannel write(String cloudHost, String cloudPath, long offset) throws Exception;

  Optional<CloudFileAttributes> fileAttributes(String cloudHost, String cloudPath) throws Exception;
}
