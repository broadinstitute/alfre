package alfre.v0.spi;

import alfre.v0.spi.function.SupplierWithExceptions;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@SuppressWarnings("WeakerAccess")
public final class CloudFileAttributeView<CloudHostT extends CloudHost>
    implements BasicFileAttributeView {

  public static final String VIEW_NAME = "cloud";
  private final CloudPath<CloudHostT> cloudPath;
  private final SupplierWithExceptions<Boolean, ? extends Exception> isDirectory;

  /** Creates a new CloudFileAttributeView. */
  public CloudFileAttributeView(
      final CloudPath<CloudHostT> cloudPath,
      final SupplierWithExceptions<Boolean, ? extends Exception> isDirectory) {
    this.cloudPath = cloudPath;
    this.isDirectory = isDirectory;
  }

  @Override
  public String name() {
    return VIEW_NAME;
  }

  @Override
  public BasicFileAttributes readAttributes() throws IOException {
    final CloudFileAttributes attributes = cloudPath.getAttributes();
    if (attributes != null) {
      return attributes;
    }

    try {
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      return retry.runWithRetries(
          () -> {
            if (isDirectory.get()) {
              return CloudPseudoDirectoryAttributes.INSTANCE;
            } else {
              return fileProvider.fileAttributes(cloudPath).orElseThrow(FileNotFoundException::new);
            }
          });
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public void setTimes(
      final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime) {
    throw new UnsupportedOperationException();
  }
}
