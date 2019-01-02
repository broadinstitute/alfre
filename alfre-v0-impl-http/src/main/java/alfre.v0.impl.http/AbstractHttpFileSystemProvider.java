package alfre.v0.impl.http;

import alfre.v0.spi.uri.UriFileSystemProvider;
import java.util.Map;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public abstract class AbstractHttpFileSystemProvider extends UriFileSystemProvider {

  @Override
  protected HttpFileProvider newFileProvider(final Map<String, ?> env) {
    final HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
    return new HttpFileProvider(httpClient);
  }
}
