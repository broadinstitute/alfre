package alfre.v0.spi;

import java.nio.file.attribute.FileTime;

@SuppressWarnings("WeakerAccess")
public final class CloudPseudoDirectoryAttributes implements CloudFileAttributes {

  public static final CloudPseudoDirectoryAttributes INSTANCE =
      new CloudPseudoDirectoryAttributes();

  private CloudPseudoDirectoryAttributes() {}

  @Override
  public String fileHash() {
    return null;
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
}
