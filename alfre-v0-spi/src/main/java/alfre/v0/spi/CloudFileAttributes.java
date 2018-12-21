package alfre.v0.spi;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

public interface CloudFileAttributes extends BasicFileAttributes {
  public static final FileTime FILE_TIME_ZERO = FileTime.fromMillis(0);
  public static final long FILE_SIZE_ZERO = 0L;

  public Optional<String> fileHash();
}
