package alfre.v0.spi;

import java.nio.file.attribute.FileTime;

public class CloudRegularFileAttributes implements CloudFileAttributes {

  private final FileTime lastModifiedTime;
  private final String fileHash;
  private final long size;

  /** Creates a new CloudRegularFileAttributes. */
  public CloudRegularFileAttributes(
      final FileTime lastModifiedTime, final String fileHash, final long size) {
    this.lastModifiedTime = lastModifiedTime;
    this.fileHash = fileHash;
    this.size = size;
  }

  @Override
  public String fileHash() {
    return fileHash;
  }

  @Override
  public FileTime lastModifiedTime() {
    return lastModifiedTime;
  }

  @Override
  public long size() {
    return size;
  }

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
