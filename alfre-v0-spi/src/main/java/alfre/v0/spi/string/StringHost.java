package alfre.v0.spi.string;

import alfre.v0.spi.CloudHost;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class StringHost implements CloudHost {

  private final String scheme;
  private final String host;

  public StringHost(final String scheme, final String host) {
    this.scheme = scheme;
    this.host = host;
  }

  public String getScheme() {
    return scheme;
  }

  public String getHost() {
    return host;
  }

  @Override
  public String getUriAsString(final String cloudPath) {
    return scheme + "://" + getRelativeHostPath(cloudPath);
  }

  @Override
  public String getRelativeHostPath(final String cloudPath) {
    return host + "/" + cloudPath;
  }

  @Override
  public int compareTo(final CloudHost other) {
    if (!(other instanceof StringHost)) {
      return -1;
    }
    final StringHost that = (StringHost) other;
    final int result = getScheme().compareTo(that.getScheme());
    return result != 0 ? result : getHost().compareTo(that.getHost());
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof StringHost)) {
      return false;
    }
    final StringHost that = (StringHost) other;
    return Objects.equals(getScheme(), that.getScheme())
        && Objects.equals(getHost(), that.getHost());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getScheme(), getHost());
  }
}
