package alfre.v0.impl.http;

import static alfre.v0.spi.function.LambdaExceptionUtil.rethrowFunction;

import alfre.v0.spi.CloudFileAttributes;
import alfre.v0.spi.CloudFileList;
import alfre.v0.spi.CloudFileListElement;
import alfre.v0.spi.CloudRegularFileAttributes;
import alfre.v0.spi.uri.UriFileProvider;
import alfre.v0.util.ChannelUtil;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.protocol.HttpDateGenerator;

@SuppressWarnings("WeakerAccess")
public class HttpFileProvider extends UriFileProvider {

  private final HttpClient client;

  public HttpFileProvider(final HttpClient client) {
    this.client = client;
  }

  @Override
  public boolean exists(final URI uri) throws Exception {
    final HttpHead request = new HttpHead(uri);
    final HttpResponse response = client.execute(request);
    return checkFound(response);
  }

  @Override
  public CloudFileList list(final URI uriPrefix, final String marker) throws Exception {
    final HttpHead request = new HttpHead(uriPrefix);
    final HttpResponse response = client.execute(request);
    return getCloudRegularFileAttributes(response)
        .map(attributes -> new CloudFileListElement(uriPrefix.getPath(), attributes))
        .map(CloudFileList::of)
        .orElseGet(CloudFileList::empty);
  }

  @Override
  public void copy(final URI sourceUri, final URI targetUri) {
    throw new UnsupportedOperationException("copying not supported");
  }

  @Override
  public ReadableByteChannel read(final URI uri, final long offset) throws Exception {
    final HttpGet request = new HttpGet(uri);
    if (0 < offset) {
      request.setHeader(HttpHeaders.RANGE, String.format("bytes=%s-", offset));
    }
    final HttpResponse response = client.execute(request);
    checkSuccess(response);
    if (0 < offset && response.getFirstHeader(HttpHeaders.CONTENT_RANGE) == null) {
      response.getEntity().getContent().close();
      throw new IOException("Connection interrupted");
    }
    return Channels.newChannel(response.getEntity().getContent());
  }

  @Override
  public WritableByteChannel write(final URI uri, final long offset) throws Exception {
    if (0 < offset) {
      throw new UnsupportedOperationException("Cannot resume uploads.");
    }
    return ChannelUtil.pipedStreamWriter(
        String.format("writer %s://%s/%s", uri.getScheme(), uri.getHost(), uri.getPath()),
        inputStream -> {
          final HttpPut request = new HttpPut(uri);
          request.setEntity(EntityBuilder.create().setStream(inputStream).gzipCompress().build());
          final HttpResponse response = client.execute(request);
          checkSuccess(response);
        });
  }

  @Override
  public boolean deleteIfExists(final URI uri) throws Exception {
    final HttpDelete request = new HttpDelete(uri);
    final HttpResponse response = client.execute(request);
    return checkFound(response);
  }

  @Override
  public Optional<CloudRegularFileAttributes> fileAttributes(final URI uri) throws Exception {
    final HttpHead request = new HttpHead(uri);
    final HttpResponse response = client.execute(request);
    return getCloudRegularFileAttributes(response);
  }

  private static Optional<String> header(final HttpResponse response, final String name) {
    return Optional.ofNullable(response.getFirstHeader(name)).map(Header::getValue);
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private static <T> Stream<T> toStream(final Optional<T> optional) {
    return optional.map(Stream::of).orElseGet(Stream::empty);
  }

  private static boolean checkFound(final HttpResponse response) throws IOException {
    final int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode == HttpStatus.SC_NOT_FOUND) {
      return false;
    } else if (statusCode < 200 || 300 <= statusCode) {
      throw new IOException("Unexpected response " + response.getStatusLine());
    } else {
      return true;
    }
  }

  private static void checkSuccess(final HttpResponse response) throws IOException {
    final int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode < 200 || 300 <= statusCode) {
      throw new IOException("Unexpected response " + response.getStatusLine());
    }
  }

  private static Optional<CloudRegularFileAttributes> getCloudRegularFileAttributes(
      final HttpResponse response) throws Exception {
    if (!checkFound(response)) {
      return Optional.empty();
    }

    final long size =
        header(response, HttpHeaders.CONTENT_LENGTH)
            .map(Long::valueOf)
            .orElse(CloudFileAttributes.FILE_SIZE_ZERO);

    final String fileHash =
        Stream.of(HttpHeaders.CONTENT_MD5, HttpHeaders.ETAG)
            .map(headerName -> header(response, headerName))
            .flatMap(HttpFileProvider::toStream)
            .findFirst()
            .orElse(null);

    final FileTime lastModifiedTime =
        header(response, HttpHeaders.LAST_MODIFIED)
            .map(
                rethrowFunction(
                    headerValue -> {
                      final SimpleDateFormat format =
                          new SimpleDateFormat(HttpDateGenerator.PATTERN_RFC1123, Locale.US);
                      format.setTimeZone(HttpDateGenerator.GMT);
                      return FileTime.from(format.parse(headerValue).toInstant());
                    }))
            .orElse(CloudFileAttributes.FILE_TIME_ZERO);

    final CloudRegularFileAttributes attributes =
        new CloudRegularFileAttributes(lastModifiedTime, fileHash, size);

    return Optional.of(attributes);
  }
}
