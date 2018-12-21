package alfre.v0.spi;

import java.nio.file.attribute.FileTime;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public final class CloudPseudoDirectoryAttributes implements CloudFileAttributes {
  private final CloudPath path;

  public CloudPseudoDirectoryAttributes(final CloudPath path) {
    this.path = path;
  }

  @Override
  public Optional<String> fileHash() {
    return Optional.empty();
  }

  @Override
  public FileTime lastModifiedTime() {
    return FILE_TIME_ZERO;
  }

  @Override
  public FileTime lastAccessTime() {
    return FILE_TIME_ZERO;
  }

  @Override
  public FileTime creationTime() {
    return FILE_TIME_ZERO;
  }

  @Override
  public boolean isRegularFile() {
    return false;
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public boolean isSymbolicLink() {
    return false;
  }

  @Override
  public boolean isOther() {
    return false;
  }

  @Override
  public long size() {
    return 0;
  }

  @Override
  public Object fileKey() {
    return path;
  }
}
