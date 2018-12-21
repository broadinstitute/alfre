package alfre.v0.spi;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CloudFileList {
  private final Iterable<String> paths;
  private final String marker;

  public CloudFileList(final Iterable<String> paths, final String marker) {
    this.paths = paths;
    this.marker = marker;
  }

  public Iterable<String> getPaths() {
    return paths;
  }

  public String getMarker() {
    return marker;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CloudFileList)) {
      return false;
    }
    final CloudFileList that = (CloudFileList) o;
    return Objects.equals(paths, that.paths) && Objects.equals(marker, that.marker);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paths, marker);
  }

  @Override
  public String toString() {
    return String.format("CloudFileList{paths=%s, marker='%s'}", paths, marker);
  }
}
