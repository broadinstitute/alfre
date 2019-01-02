package alfre.v0.impl.s3;

import alfre.v0.spi.string.StringFileSystemProvider;
import java.util.Map;
import software.amazon.awssdk.services.s3.S3Client;

public class S3FileSystemProvider extends StringFileSystemProvider {

  @Override
  public String getScheme() {
    return "s3";
  }

  @Override
  protected S3FileProvider newFileProvider(final Map<String, ?> env) {
    final S3Client s3Client = S3Client.create();
    return new S3FileProvider(s3Client);
  }
}
