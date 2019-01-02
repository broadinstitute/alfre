package alfre.v0.util;

import alfre.v0.spi.function.ConsumerWithExceptions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.WritableByteChannel;
import java.util.function.Consumer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;

public class ChannelUtil {

  /**
   * Creates an deferred buffer of memory to consume data before passing it into the consumer.
   *
   * <p>Useful only for uploaders that must know the full size ahead of time.
   *
   * @param threadName the name of the background thread.
   * @param threshold the number of bytes at which to trigger an event.
   * @param initialBufferSize the initial size of the in memory buffer.
   * @param prefix the optional prefix for the temporary file.
   * @param suffix the optional suffix for the temporary file.
   * @param outputFileOrDirectory if the prefix is null, the temporary directory, otherwise the
   *     temporary file.
   * @param byteConsumer consumes the buffered data as a byte[].
   * @param fileConsumer consumes the buffered data as a File.
   * @return a writable byte channel for the buffer.
   */
  public static WritableByteChannel deferredFileWriter(
      final String threadName,
      final int threshold,
      final int initialBufferSize,
      final String prefix,
      final String suffix,
      final File outputFileOrDirectory,
      final Consumer<byte[]> byteConsumer,
      final Consumer<File> fileConsumer) {
    final Object bufferLock = new Object();
    final DeferredFileOutputStream deferredFileOutputStream;
    if (prefix == null) {
      deferredFileOutputStream =
          new DeferredFileOutputStream(threshold, initialBufferSize, outputFileOrDirectory);
    } else {
      deferredFileOutputStream =
          new DeferredFileOutputStream(
              threshold, initialBufferSize, prefix, suffix, outputFileOrDirectory);
    }
    final ExceptionQueue exceptions = new ExceptionQueue();
    final boolean[] done = {false};

    final Runnable runnable =
        () -> {
          synchronized (bufferLock) {
            try {
              while (!done[0]) {
                bufferLock.wait();
              }
              if (deferredFileOutputStream.isInMemory()) {
                byteConsumer.accept(deferredFileOutputStream.getData());
              } else {
                fileConsumer.accept(deferredFileOutputStream.getFile());
              }
            } catch (final Exception exception) {
              exceptions.add(exception);
            } finally {
              FileUtils.deleteQuietly(deferredFileOutputStream.getFile());
            }
          }
        };

    final WritableByteChannel wrapper = Channels.newChannel(deferredFileOutputStream);

    final Thread thread = new Thread(runnable, threadName);
    thread.start();

    return new WritableByteChannel() {
      @Override
      public int write(final ByteBuffer src) throws IOException {
        return wrapper.write(src);
      }

      @Override
      public boolean isOpen() {
        return wrapper.isOpen();
      }

      @Override
      public void close() throws IOException {
        exceptions.runQuietly(wrapper::close);
        synchronized (bufferLock) {
          done[0] = true;
          bufferLock.notifyAll();
        }
        exceptions.runQuietly(thread::join);
        exceptions.throwFirstAsIoException();
      }
    };
  }

  /**
   * Creates a WritableByteChannel from an InputStream updated on a background thread.
   *
   * @param threadName the name of the background thread.
   * @param consumer consumes the input stream.
   * @return a writable byte channel for the buffer.
   */
  public static WritableByteChannel pipedStreamWriter(
      final String threadName, final ConsumerWithExceptions<InputStream, ?> consumer)
      throws IOException {
    final Pipe pipe = Pipe.open();
    final ExceptionQueue exceptions = new ExceptionQueue();

    final SourceChannel source = pipe.source();
    final SinkChannel sink = pipe.sink();
    final Runnable runnable =
        () -> exceptions.runQuietly(() -> consumer.accept(Channels.newInputStream(source)));

    final Thread thread = new Thread(runnable, threadName);
    thread.setDaemon(true);
    thread.start();

    return new WritableByteChannel() {

      @Override
      public int write(final ByteBuffer src) throws IOException {
        return sink.write(src);
      }

      @Override
      public boolean isOpen() {
        return sink.isOpen();
      }

      @Override
      public void close() throws IOException {
        exceptions.runQuietly(sink::close);
        exceptions.runQuietly(thread::join);
        exceptions.runQuietly(source::close);
        exceptions.throwFirstAsIoException();
      }
    };
  }
}
