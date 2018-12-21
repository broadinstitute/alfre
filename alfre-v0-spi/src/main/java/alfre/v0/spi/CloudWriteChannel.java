package alfre.v0.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

@SuppressWarnings("unused")
public class CloudWriteChannel implements SeekableByteChannel {

  private final CloudFileProvider fileProvider;
  private final CloudRetry retry;
  private final CloudPath cloudPath;

  private long internalPosition;
  private WritableByteChannel channel;

  /** Creates a new CloudWriteChannel. */
  public CloudWriteChannel(
      final CloudFileProvider fileProvider, final CloudRetry retry, final CloudPath cloudPath)
      throws IOException {
    this.fileProvider = fileProvider;
    this.retry = retry;
    this.cloudPath = cloudPath;
    channel = resetWritablePosition(0);
  }

  @Override
  public int read(final ByteBuffer dst) {
    throw new NonReadableChannelException();
  }

  @Override
  public int write(final ByteBuffer src) throws IOException {
    try {
      final boolean[] resetConnection = {false};
      final int count =
          retry.runWithRetries(
              () -> {
                try {
                  if (resetConnection[0]) {
                    channel =
                        fileProvider.write(
                            cloudPath.getCloudHost(), cloudPath.getCloudPath(), internalPosition);
                  }
                  return channel.write(src);
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
      channel = resetWritablePosition(newPosition);
    }

    return this;
  }

  private WritableByteChannel resetWritablePosition(final long newPosition) throws IOException {
    try {
      return retry.runWithRetries(
          () ->
              fileProvider.write(cloudPath.getCloudHost(), cloudPath.getCloudPath(), newPosition));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public long size() {
    return internalPosition;
  }

  @Override
  public SeekableByteChannel truncate(final long size) {
    throw new UnsupportedOperationException();
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
