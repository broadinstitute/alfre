package alfre.v0.spi;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CloudFileListElement {

  private final String key;
  private final CloudFileAttributes attributes;

  public CloudFileListElement(final String key) {
    this(key, null);
  }

  /** Creates a new CloudFileListElement. */
  public CloudFileListElement(final String key, final CloudFileAttributes attributes) {
    Objects.requireNonNull(key, "key is null");
    this.key = key;
    this.attributes = attributes;
  }

  public String getKey() {
    return key;
  }

  public CloudFileAttributes getAttributes() {
    return attributes;
  }
}
