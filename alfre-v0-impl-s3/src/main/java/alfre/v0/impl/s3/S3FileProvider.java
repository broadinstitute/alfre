package alfre.v0.impl.s3;

import alfre.v0.spi.CloudFileAttributes;
import alfre.v0.spi.CloudFileList;
import alfre.v0.spi.CloudFileListElement;
import alfre.v0.spi.CloudRegularFileAttributes;
import alfre.v0.spi.string.StringFileProvider;
import alfre.v0.util.ChannelUtil;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@SuppressWarnings("WeakerAccess")
public class S3FileProvider extends StringFileProvider {

  private final S3Client client;

  public S3FileProvider(final S3Client client) {
    this.client = client;
  }

  @Override
  public boolean exists(final String bucket, final String key) {
    final HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
    try {
      client.headObject(request);
      return true;
    } catch (final NoSuchKeyException noSuchKeyException) {
      return false;
    } catch (final S3Exception s3Exception) {
      if (s3Exception.statusCode() == 404) {
        return false;
      } else {
        throw s3Exception;
      }
    }
  }

  @Override
  public boolean existsPrefix(final String bucket, final String keyPrefix) {
    final ListObjectsV2Request request =
        ListObjectsV2Request.builder().bucket(bucket).prefix(keyPrefix).maxKeys(1).build();
    final ListObjectsV2Iterable response = client.listObjectsV2Paginator(request);
    return response.contents().stream().iterator().hasNext();
  }

  @Override
  public CloudFileList list(final String bucket, final String keyPrefix, final String marker) {
    final ListObjectsV2Request request =
        ListObjectsV2Request.builder().bucket(bucket).prefix(keyPrefix).build();
    final ListObjectsV2Iterable response = client.listObjectsV2Paginator(request);
    final Stream<CloudFileListElement> elements =
        response.contents().stream().map(S3FileProvider::toCloudFileListElement);
    return new CloudFileList(elements, null);
  }

  @Override
  public ReadableByteChannel read(
      final String bucket,
      final String key,
      final long offset,
      final Collection<? extends OpenOption> options) {
    final GetObjectRequest request;
    if (offset <= 0) {
      request = GetObjectRequest.builder().bucket(bucket).key(key).build();
    } else {
      final String range = String.format("bytes=%d-", offset);
      request = GetObjectRequest.builder().bucket(bucket).key(key).range(range).build();
    }
    final ResponseInputStream<GetObjectResponse> response = client.getObject(request);
    return Channels.newChannel(response);
  }

  @Override
  public WritableByteChannel write(
      final String bucket,
      final String key,
      final long offset,
      final Collection<? extends OpenOption> options) {
    if (0 < offset) {
      throw new UnsupportedOperationException("Cannot resume uploads.");
    }
    return ChannelUtil.deferredFileWriter(
        String.format("writer s3://%s/%s", bucket, key),
        1024,
        1024,
        null,
        null,
        null,
        bytes -> {
          final PutObjectRequest request =
              PutObjectRequest.builder().bucket(bucket).key(key).build();
          final RequestBody body = RequestBody.fromBytes(bytes);
          client.putObject(request, body);
        },
        file -> {
          final PutObjectRequest request =
              PutObjectRequest.builder().bucket(bucket).key(key).build();
          final RequestBody body = RequestBody.fromFile(file);
          client.putObject(request, body);
        });
  }

  @Override
  public void copy(
      final String sourceBucket,
      final String sourceKey,
      final String targetBucket,
      final String targetKey,
      final Collection<? extends CopyOption> options) {
    /*
    In the original SDK these used to be passed as separate values.
    https://github.com/aws/aws-sdk-java/blob/1.11.354/aws-java-sdk-s3/src/main/java/com/amazonaws/services/s3/model/CopyObjectRequest.java#L50-L57

    For some reason we're supposed to concatenate them as of v2.2.0.
    Hoping the encodings are not an issue.
    https://github.com/aws/aws-sdk-java-v2/blob/2.2.0/services/s3/src/it/java/software/amazon/awssdk/services/s3/CopyObjectIntegrationTest.java#L90
     */
    final String sourceBucketSlashKey = sourceBucket + "/" + sourceKey;
    final CopyObjectRequest request =
        CopyObjectRequest.builder()
            .copySource(sourceBucketSlashKey)
            .bucket(targetBucket)
            .key(targetKey)
            .build();
    client.copyObject(request);
  }

  @Override
  public boolean deleteIfExists(final String bucket, final String key) {
    final boolean exists = exists(bucket, key);
    if (exists) {
      final DeleteObjectRequest request =
          DeleteObjectRequest.builder().bucket(bucket).key(key).build();
      client.deleteObject(request);
    }
    return exists;
  }

  @Override
  public Optional<CloudRegularFileAttributes> fileAttributes(
      final String bucket, final String key) {
    final HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
    try {
      final HeadObjectResponse response = client.headObject(request);
      final long size =
          Optional.ofNullable(response.contentLength()).orElse(CloudFileAttributes.FILE_SIZE_ZERO);
      final String fileHash = response.eTag();
      final FileTime lastModifiedTime = CloudFileAttributes.toFileTime(response.lastModified());
      final CloudRegularFileAttributes attributes =
          new CloudRegularFileAttributes(lastModifiedTime, fileHash, size);
      return Optional.of(attributes);
    } catch (final NoSuchKeyException noSuchKeyException) {
      return Optional.empty();
    }
  }

  private static CloudFileListElement toCloudFileListElement(final S3Object s3Object) {
    final String key = s3Object.key();
    final long size =
        Optional.ofNullable(s3Object.size()).orElse(CloudFileAttributes.FILE_SIZE_ZERO);
    final String fileHash = s3Object.eTag();
    final FileTime lastModifiedTime = CloudFileAttributes.toFileTime(s3Object.lastModified());
    final CloudRegularFileAttributes attributes =
        new CloudRegularFileAttributes(lastModifiedTime, fileHash, size);
    return new CloudFileListElement(key, attributes);
  }
}
