package alfre.v0.spi;

import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class CloudFileSystem extends FileSystem {

  private static final String SEPARATOR = "/";
  private final CloudFileSystemProvider provider;
  private final String host;

  public CloudFileSystem(final CloudFileSystemProvider provider, final String host) {
    this.provider = provider;
    this.host = host;
  }

  public String getHost() {
    return host;
  }

  @Override
  public CloudFileSystemProvider provider() {
    return provider;
  }

  @Override
  public void close() {
    // do nothing currently.
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public boolean isReadOnly() {
    return false;
  }

  @Override
  public String getSeparator() {
    return SEPARATOR;
  }

  @Override
  public Iterable<Path> getRootDirectories() {
    return Collections.singleton(getPath(UnixPath.ROOT));
  }

  @Override
  public Iterable<FileStore> getFileStores() {
    return Collections.emptySet();
  }

  @Override
  public Set<String> supportedFileAttributeViews() {
    final List<String> list = Arrays.asList("basic", CloudFileAttributeView.VIEW_NAME);
    final HashSet<String> set = new HashSet<>(list);
    return Collections.unmodifiableSet(set);
  }

  @Override
  public CloudPath getPath(final String first, final String... more) {
    return new CloudPath(this, UnixPath.getPath(first, more));
  }

  @Override
  public PathMatcher getPathMatcher(final String syntaxAndPattern) {
    return FileSystems.getDefault().getPathMatcher(syntaxAndPattern);
  }

  @Override
  public UserPrincipalLookupService getUserPrincipalLookupService() {
    throw new UnsupportedOperationException();
  }

  @Override
  public WatchService newWatchService() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CloudFileSystem)) {
      return false;
    }
    final CloudFileSystem that = (CloudFileSystem) o;
    return Objects.equals(provider(), that.provider()) && Objects.equals(getHost(), that.getHost());
  }

  @Override
  public int hashCode() {
    return Objects.hash(provider(), getHost());
  }
}
