package alfre.v0.spi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class CloudPath implements Path {
  private String host;
  private UnixPath unixPath;
  private FileSystem fileSystem;

  CloudPath(String host, UnixPath unixPath, FileSystem fileSystem) {
    this.host = host;
    this.unixPath = unixPath;
    this.fileSystem = fileSystem;
  }

  @Override
  public FileSystem getFileSystem() {
    return fileSystem;
  }

  @Override
  public int compareTo(Path other) {
    return 0;
  }

  @Override
  public boolean isAbsolute() {
    return false;
  }

  @Override
  public Path getRoot() {
    CloudPath rootCloudPath;
    rootCloudPath = new CloudPath(this.host, this.unixPath.getRoot(), this.fileSystem);
    return rootCloudPath;
  }

  @Override
  public Path getFileName() {
    return null;
  }

  @Override
  public Path getParent() {
    return null;
  }

  @Override
  public int getNameCount() {
    return 0;
  }

  @Override
  public Path getName(int index) {
    return null;
  }

  @Override
  public Path subpath(int beginIndex, int endIndex) {
    return null;
  }

  @Override
  public boolean startsWith(Path other) {
    return false;
  }

  @Override
  public boolean startsWith(String other) {
    return false;
  }

  @Override
  public boolean endsWith(Path other) {
    return false;
  }

  @Override
  public boolean endsWith(String other) {
    return false;
  }

  @Override
  public Path normalize() {
    return null;
  }

  @Override
  public Path resolve(Path other) {
    CloudPath otherCloudPath = (CloudPath) other;
    return new CloudPath(
        this.host, this.unixPath.resolve(otherCloudPath.unixPath), this.fileSystem);
  }

  @Override
  public Path resolve(String other) {
    return null;
  }

  @Override
  public Path resolveSibling(Path other) {
    return null;
  }

  @Override
  public Path resolveSibling(String other) {
    return null;
  }

  @Override
  public Path relativize(Path other) {
    return null;
  }

  @Override
  public URI toUri() {
    return null;
  }

  @Override
  public Path toAbsolutePath() {
    return null;
  }

  @Override
  public Path toRealPath(LinkOption... options) throws IOException {
    return null;
  }

  @Override
  public File toFile() {
    return null;
  }

  @Override
  public WatchKey register(
      WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers)
      throws IOException {
    return null;
  }

  @Override
  public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
    return null;
  }

  @Override
  public Iterator<Path> iterator() {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CloudPath)) return false;

    CloudPath paths = (CloudPath) o;

    if (host != null ? !host.equals(paths.host) : paths.host != null) return false;
    if (unixPath != null ? !unixPath.equals(paths.unixPath) : paths.unixPath != null) return false;
    return fileSystem != null ? fileSystem.equals(paths.fileSystem) : paths.fileSystem == null;
  }

  @Override
  public int hashCode() {
    int result = host != null ? host.hashCode() : 0;
    result = 31 * result + (unixPath != null ? unixPath.hashCode() : 0);
    result = 31 * result + (fileSystem != null ? fileSystem.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CloudPath{"
        + "host='"
        + host
        + '\''
        + ", unixPath="
        + unixPath
        + ", fileSystem="
        + fileSystem
        + '}';
  }
}
