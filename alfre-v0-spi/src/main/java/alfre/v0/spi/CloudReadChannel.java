package alfre.v0.spi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.BasicFileAttributes;

@SuppressWarnings("unused")
public class CloudReadChannel implements SeekableByteChannel {

  private final CloudFileProvider fileProvider;
  private final CloudRetry retry;
  private final CloudPath cloudPath;

  private long internalPosition;
  private ReadableByteChannel channel;

  /** Creates a new CloudReadChannel. */
  public CloudReadChannel(
      final CloudFileProvider fileProvider, final CloudRetry retry, final CloudPath cloudPath)
      throws IOException {
    this.fileProvider = fileProvider;
    this.retry = retry;
    this.cloudPath = cloudPath;
    channel = resetReadablePosition(0);
  }

  @Override
  public int read(final ByteBuffer dst) throws IOException {
    try {
      final boolean[] resetConnection = {false};
      final int count =
          retry.runWithRetries(
              () -> {
                try {
                  if (resetConnection[0]) {
                    channel =
                        fileProvider.read(
                            cloudPath.getCloudHost(), cloudPath.getCloudPath(), internalPosition);
                  }
                  return channel.read(dst);
                } catch (final Exception exception) {
                  resetConnection[0] = true;
                  throw exception;
                }
              });

      if (count > 0) {
        internalPosition += count;
      }

      return count;
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public int write(final ByteBuffer src) {
    throw new NonWritableChannelException();
  }

  @Override
  public long position() {
    return internalPosition;
  }

  @Override
  public SeekableByteChannel position(final long newPosition) throws IOException {
    if (channel.isOpen()) {
      throw new ClosedChannelException();
    }

    if (internalPosition != newPosition) {
      channel.close();
      internalPosition = newPosition;
      channel = resetReadablePosition(newPosition);
    }

    return this;
  }

  private ReadableByteChannel resetReadablePosition(final long newPosition) throws IOException {
    try {
      return retry.runWithRetries(
          () -> fileProvider.read(cloudPath.getCloudHost(), cloudPath.getCloudPath(), newPosition));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public long size() throws IOException {
    try {
      return retry
          .runWithRetries(
              () -> fileProvider.fileAttributes(cloudPath.getCloudHost(), cloudPath.getCloudPath()))
          .map(BasicFileAttributes::size)
          .orElseThrow(() -> new FileNotFoundException(cloudPath.getUriAsString()));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public SeekableByteChannel truncate(final long size) {
    throw new NonWritableChannelException();
  }

  @Override
  public boolean isOpen() {
    return channel.isOpen();
  }

  @Override
  public void close() throws IOException {
    channel.close();
  }
}
