package alfre.v0.spi.string;

import alfre.v0.spi.CloudFileSystemProvider;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class StringFileSystemProvider extends CloudFileSystemProvider<StringHost> {
  private static final String PATTERN_STRING = "";
  private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

  @Override
  public StringHost getHost(final String uriAsString, final Map<String, ?> env) {
    if (!uriAsString.startsWith(getScheme() + "://")) {
      throw new IllegalArgumentException("Scheme does not start with " + getScheme());
    }

    final String host;
    final int hostIndex = getScheme().length() + 3;
    final int slashIndex = uriAsString.indexOf('/', hostIndex);
    if (slashIndex < 0) {
      host = uriAsString.substring(hostIndex);
    } else {
      host = uriAsString.substring(hostIndex, slashIndex);
    }

    if (host.isEmpty()) {
      throw new IllegalArgumentException("Host is empty");
    }

    return new StringHost(getScheme(), host);
  }
}
