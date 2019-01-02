package alfre.v0.spi;

import alfre.v0.spi.function.SupplierWithExceptions;

public class SingleTry implements CloudRetry {

  @Override
  public <T, E extends Exception> T runWithRetries(final SupplierWithExceptions<T, E> supplier)
      throws CloudRetryException {
    try {
      return supplier.get();
    } catch (final Exception exception) {
      throw new CloudRetryException(exception);
    }
  }
}
