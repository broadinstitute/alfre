package alfre.v0.spi;

import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class CloudFileList {
  private final Stream<CloudFileListElement> elements;
  private final String marker;

  /** Creates a new file list. */
  public CloudFileList(final Stream<CloudFileListElement> elements, final String marker) {
    Objects.requireNonNull(elements, "elements is null");
    this.elements = elements;
    this.marker = marker;
  }

  public Stream<CloudFileListElement> getElements() {
    return elements;
  }

  public String getMarker() {
    return marker;
  }

  public static CloudFileList empty() {
    return new CloudFileList(Stream.empty(), null);
  }

  public static CloudFileList of(final CloudFileListElement element) {
    return new CloudFileList(Stream.of(element), null);
  }

  public static CloudFileList of(final CloudFileListElement... elements) {
    return new CloudFileList(Stream.of(elements), null);
  }
}
