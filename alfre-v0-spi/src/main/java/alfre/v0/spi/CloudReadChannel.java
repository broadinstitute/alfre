package alfre.v0.spi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.BasicFileAttributes;

@SuppressWarnings({"WeakerAccess", "Duplicates"})
public class CloudReadChannel<CloudHostT extends CloudHost> implements SeekableByteChannel {

  private final CloudPath<CloudHostT> cloudPath;

  private long internalPosition;
  private ReadableByteChannel channel;

  /** Creates a new CloudReadChannel. */
  public CloudReadChannel(final CloudPath<CloudHostT> cloudPath) throws IOException {
    this.cloudPath = cloudPath;
    channel = resetReadablePosition(0);
  }

  @Override
  public int read(final ByteBuffer dst) throws IOException {
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
                    channel = fileProvider.read(cloudPath, internalPosition);
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
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      return retry.runWithRetries(() -> fileProvider.read(cloudPath, newPosition));
    } catch (final CloudRetryException cloudRetryException) {
      throw cloudRetryException.getCauseIoException();
    }
  }

  @Override
  public long size() throws IOException {
    try {
      final CloudFileSystem<CloudHostT> fileSystem = cloudPath.getFileSystem();
      final CloudFileProvider<CloudHostT> fileProvider = fileSystem.getFileProvider();
      final CloudRetry retry = fileSystem.getRetry();
      return retry
          .runWithRetries(() -> fileProvider.fileAttributes(cloudPath))
          .map(BasicFileAttributes::size)
          .orElseThrow(FileNotFoundException::new);
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
