package alfre.v0.spi.uri;

import alfre.v0.spi.CloudHost;
import alfre.v0.spi.CloudPathException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class UriHost implements CloudHost {

  private final URI originalUri;

  /** Creates a new UriHost. */
  public UriHost(final URI originalUri) {
    Objects.requireNonNull(originalUri, "originalUri is null");
    Objects.requireNonNull(originalUri.getHost(), "originalUri.getHost() is null");
    this.originalUri = originalUri;
  }

  public URI getOriginalUri() {
    return originalUri;
  }

  @Override
  public String getUriAsString(final String cloudPath) {
    try {
      return new URI(
              originalUri.getScheme(),
              originalUri.getAuthority(),
              "/" + cloudPath,
              originalUri.getQuery(),
              originalUri.getFragment())
          .toString();
    } catch (final URISyntaxException uriSyntaxException) {
      throw new CloudPathException(uriSyntaxException);
    }
  }

  @Override
  public String getRelativeHostPath(final String cloudPath) {
    return originalUri.getHost() + "/" + cloudPath;
  }

  @Override
  public int compareTo(final CloudHost other) {
    if (!(other instanceof UriHost)) {
      return -1;
    }
    final UriHost that = (UriHost) other;
    return getOriginalUri().compareTo(that.getOriginalUri());
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof UriHost)) {
      return false;
    }
    final UriHost uriHost = (UriHost) other;
    return Objects.equals(getOriginalUri(), uriHost.getOriginalUri());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getOriginalUri());
  }
}
