package alfre.v0.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class CloudWriteChannel<CloudHostT extends CloudHost> implements SeekableByteChannel {

  private final CloudPath<CloudHostT> cloudPath;

  private long internalPosition;
  private WritableByteChannel channel;

  /** Creates a new CloudWriteChannel. */
  public CloudWriteChannel(final CloudPath<CloudHostT> cloudPath) throws IOException {
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
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      final boolean[] resetConnection = {false};
      final int count =
          retry.runWithRetries(
              () -> {
                try {
                  if (resetConnection[0]) {
                    channel = fileProvider.write(cloudPath, internalPosition);
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
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      return retry.runWithRetries(() -> fileProvider.write(cloudPath, newPosition));
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
