package alfre.v0.util;

import alfre.v0.spi.function.RunnableWithExceptions;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("WeakerAccess")
public class ExceptionQueue {

  private final Queue<Exception> exceptions = new LinkedList<>();

  /** Runs possibly enqueuing any thrown exception. */
  public void runQuietly(final RunnableWithExceptions<?> runnable) {
    try {
      runnable.run();
    } catch (final Exception exception) {
      add(exception);
    }
  }

  /** Adds an exception. */
  public void add(final Exception exception) {
    synchronized (exceptions) {
      exceptions.add(exception);
    }
  }

  /** Returns the first Exception. */
  public Exception getFirst() {
    synchronized (exceptions) {
      return exceptions.peek();
    }
  }

  /** Throws the first exception as an IOException. */
  public void throwFirstAsIoException() throws IOException {
    final Exception exception = getFirst();
    if (exception != null) {
      if (exception instanceof IOException) {
        throw (IOException) exception;
      } else {
        throw new IOException(exception);
      }
    }
  }
}
