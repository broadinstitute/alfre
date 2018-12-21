package alfre.v0.spi;

import java.nio.file.attribute.FileTime;

public abstract class CloudRegularFileAttributes implements CloudFileAttributes {

  @Override
  public FileTime lastAccessTime() {
    return lastModifiedTime();
  }

  @Override
  public FileTime creationTime() {
    return lastModifiedTime();
  }

  @Override
  public boolean isRegularFile() {
    return true;
  }

  @Override
  public boolean isDirectory() {
    return false;
  }

  @Override
  public boolean isSymbolicLink() {
    return false;
  }

  @Override
  public boolean isOther() {
    return false;
  }
}
