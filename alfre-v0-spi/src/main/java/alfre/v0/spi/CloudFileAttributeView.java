package alfre.v0.spi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CloudFileAttributeView implements BasicFileAttributeView {

  public static final String VIEW_NAME = "cloud";
  private final CloudFileProvider fileProvider;
  private final CloudRetry retry;
  private final CloudPath cloudPath;
  private final CloudSupplier<Boolean> isDirectory;

  /** Creates a new CloudFileAttributeView. */
  public CloudFileAttributeView(
      final CloudFileProvider fileProvider,
      final CloudRetry retry,
      final CloudPath cloudPath,
      final CloudSupplier<Boolean> isDirectory) {
    this.fileProvider = fileProvider;
    this.retry = retry;
    this.cloudPath = cloudPath;
    this.isDirectory = isDirectory;
  }

  @Override
  public String name() {
    return VIEW_NAME;
  }

  @Override
  public BasicFileAttributes readAttributes() throws IOException {
    try {
      return retry.runWithRetries(
          () -> {
            if (isDirectory.get()) {
              return new CloudPseudoDirectoryAttributes(cloudPath);
            } else {
              return fileProvider
                  .fileAttributes(cloudPath.getCloudHost(), cloudPath.getCloudPath())
                  .orElseThrow(() -> new FileNotFoundException(cloudPath.getUriAsString()));
            }
          });
    } catch (final CloudRetryException e) {
      throw e.getCauseIoException();
    }
  }

  @Override
  public void setTimes(
      final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) {
    throw new UnsupportedOperationException();
  }
}
