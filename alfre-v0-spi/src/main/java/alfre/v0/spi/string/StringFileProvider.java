package alfre.v0.spi.string;

import alfre.v0.spi.CloudFileList;
import alfre.v0.spi.CloudFileListElement;
import alfre.v0.spi.CloudFileProvider;
import alfre.v0.spi.CloudPath;
import alfre.v0.spi.CloudRegularFileAttributes;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.OpenOption;
import java.util.Collection;
import java.util.Optional;

public abstract class StringFileProvider implements CloudFileProvider<StringHost> {

  public abstract boolean exists(String cloudHost, String cloudPath) throws Exception;

  @Override
  public final boolean exists(final CloudPath<StringHost> cloudPath) throws Exception {
    return exists(cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString());
  }

  public abstract boolean existsPrefix(String cloudHost, String cloudPathPrefix) throws Exception;

  @Override
  public final boolean existsPrefix(final CloudPath<StringHost> cloudPath) throws Exception {
    return existsPrefix(cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString());
  }

  /** Returns a list of path matching the supplied prefix. */
  public CloudFileList list(
      final String cloudHost, final String cloudPathPrefix, final String marker) throws Exception {
    final boolean exists = exists(cloudHost, cloudPathPrefix);
    return exists
        ? CloudFileList.of(new CloudFileListElement(cloudPathPrefix))
        : CloudFileList.empty();
  }

  @Override
  public final CloudFileList list(final CloudPath<StringHost> cloudPathPrefix, final String marker)
      throws Exception {
    return list(
        cloudPathPrefix.getCloudHost().getHost(),
        cloudPathPrefix.getAbsolutedPathAsString(),
        marker);
  }

  public abstract ReadableByteChannel read(
      String cloudHost,
      String cloudPath,
      long offset,
      final Collection<? extends OpenOption> options)
      throws Exception;

  @Override
  public final ReadableByteChannel read(
      final CloudPath<StringHost> cloudPath,
      final long offset,
      final Collection<? extends OpenOption> options)
      throws Exception {
    return read(
        cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString(), offset, options);
  }

  public abstract WritableByteChannel write(
      String cloudHost,
      String cloudPath,
      long offset,
      final Collection<? extends OpenOption> options)
      throws Exception;

  @Override
  public final WritableByteChannel write(
      final CloudPath<StringHost> cloudPath,
      final long offset,
      final Collection<? extends OpenOption> options)
      throws Exception {
    return write(
        cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString(), offset, options);
  }

  public abstract void copy(
      final String sourceCloudHost,
      final String sourceCloudPath,
      final String targetCloudHost,
      final String targetCloudPath,
      final Collection<? extends CopyOption> options)
      throws Exception;

  @Override
  public final void copy(
      final CloudPath<StringHost> sourceCloudPath,
      final CloudPath<StringHost> targetCloudPath,
      final Collection<? extends CopyOption> options)
      throws Exception {
    copy(
        sourceCloudPath.getCloudHost().getHost(),
        sourceCloudPath.getAbsolutedPathAsString(),
        targetCloudPath.getCloudHost().getHost(),
        targetCloudPath.getAbsolutedPathAsString(),
        options);
  }

  public abstract boolean deleteIfExists(String cloudHost, String cloudPath) throws Exception;

  @Override
  public final boolean deleteIfExists(final CloudPath<StringHost> cloudPath) throws Exception {
    return deleteIfExists(cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString());
  }

  public abstract Optional<CloudRegularFileAttributes> fileAttributes(
      String cloudHost, String cloudPath) throws Exception;

  @Override
  public final Optional<CloudRegularFileAttributes> fileAttributes(
      final CloudPath<StringHost> cloudPath) throws Exception {
    return fileAttributes(cloudPath.getCloudHost().getHost(), cloudPath.getAbsolutedPathAsString());
  }
}
