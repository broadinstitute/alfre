package alfre.v0.spi.uri;

import alfre.v0.spi.CloudFileSystemProvider;
import alfre.v0.spi.CloudPathException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public abstract class UriFileSystemProvider extends CloudFileSystemProvider<UriHost> {

  @Override
  public UriHost getHost(final String uriAsString, final Map<String, ?> env) {
    try {
      return new UriHost(new URI(uriAsString));
    } catch (final URISyntaxException uriSyntaxException) {
      throw new CloudPathException(uriSyntaxException);
    }
  }
}
