package alfre.v0.spi;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Optional;

@SuppressWarnings("unused")
public interface CloudFileAttributes extends BasicFileAttributes {
  FileTime FILE_TIME_ZERO = FileTime.fromMillis(0);
  long FILE_SIZE_ZERO = 0L;

  String fileHash();

  @Override
  default Object fileKey() {
    return null;
  }

  /** Converts an Instant to a FileTime. */
  static FileTime toFileTime(final Instant instant) {
    return Optional.ofNullable(instant)
        .map(FileTime::from)
        .orElse(CloudFileAttributes.FILE_TIME_ZERO);
  }
}
